package executables;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import components.CyclicalMovement;
import components.DeathCollision;
import components.HardCollision;
import components.Input;
import components.NormalMovement;
import components.PlaybackControls;
import components.PlayerControls;
import components.Render;
import components.SoftCollision;
import components.Spawn;
import game_objects.GameObject;
import game_objects.RenderObject;
import managers.EventManager;
import managers.GameObjectManager;
import managers.StreamManager;
import managers.TimeManager;

public class Server implements Serializable, Runnable {

    /**
     *
     */
    private static final long                   serialVersionUID  = 1L;
    private static final int                    gameWidth         = 600;
    private static final int                    gameHeight        = 400;
    private static final double                 gravity           = .6;

    private static final double                 playerDimension   = gameHeight / 10;

    private static long                         ticRate           = 16;
    private static final TimeManager            timeManager       = new TimeManager( ticRate );
    private static final EventManager           eventManager      = new EventManager();
    private static final GameObjectManager      objectManager     = new GameObjectManager();
    private static final StreamManager          streamManager     = new StreamManager();
    private static final ArrayList<Rectangle2D> spawnPoints       = new ArrayList<Rectangle2D>();

    private static ServerSocket                 ss;

    private static GameObject                   recordIndicator   = new GameObject();
    private static GameObject                   playbackIndicator = new GameObject();

    private static Queue<Long>                  recordedTics      = new LinkedList<Long>();
    private static Queue<Long>                  usedTics          = new LinkedList<Long>();

    public static final int                     NOT_RECORDING     = 0;
    public static final int                     RECORDING         = 1;
    public static final int                     PLAYBACK          = 2;

    private static volatile int                 recordState       = NOT_RECORDING;

    public static void main ( final String[] args ) throws IOException {

        ss = new ServerSocket( 6001 );

        setup();

        final Server gl = new Server();
        ( new Thread( gl ) ).start(); // accepting clients

        mainGameLoop();
    }

    private static void mainGameLoop () {

        long baseTic = timeManager.getRelativeTime();

        while ( true ) {

            final long currentTic = timeManager.getRelativeTime();

            if ( currentTic != baseTic ) {

                eventManager.raiseAndHandle( EventManager.E_TYPE_SPAWN,
                        new Object[] { gravity, objectManager.getGameObjects() } );

                eventManager.raiseAndHandle( EventManager.E_TYPE_MOVEMENT );

                if ( recordState == NOT_RECORDING || recordState == RECORDING ) {

                    detectCollision();

                    eventManager.handleEvents();

                    if ( recordState == RECORDING ) {
                        recordedTics.add( currentTic );
                    }

                }
                else if ( recordState == PLAYBACK ) {

                    if ( recordedTics.isEmpty() ) {
                        usedTics.forEach( l -> recordedTics.add( l ) );
                        usedTics = new LinkedList<Long>();
                        objectManager.restoreToFirstState();
                    }
                    final long tic = recordedTics.poll();
                    eventManager.handleRecordedEvents( tic );
                    usedTics.add( tic );

                }

                updateClients();

                baseTic = timeManager.getRelativeTime();

            }

        }

    }

    @Override
    public void run () { // for accepting new clients
        while ( true ) {
            try {
                final Socket s = ss.accept();
                final int playerId = createNewPlayer();

                final ObjectOutputStream newOS = new ObjectOutputStream( s.getOutputStream() );
                newOS.writeObject( new Object[] { gameWidth, gameHeight, playerId } );

                streamManager.addOutputStream( playerId, newOS );
                streamManager.addInputStream( playerId, new ObjectInputStream( s.getInputStream() ) );

                final Thread clientThread = new Thread( new Runnable() {
                    @Override
                    public void run () {
                        handleClientInput( playerId );
                    }
                } );
                clientThread.start();

            }
            catch ( final IOException e ) {
                System.out.println( "Unable to accept client" );
            }
        }
    }

    public static void handleClientInput ( final int id ) {
        try {

            while ( true ) {

                final Object[] input = (Object[]) streamManager.readObject( id );
                final char key = Character.toLowerCase( (char) input[0] );
                final int keyState = (int) input[1];

                if ( keyState == Input.KEY_PRESSED ) {
                    recordFSM( key );
                }

                if ( recordState == NOT_RECORDING || recordState == RECORDING ) {
                    eventManager.raise( EventManager.E_TYPE_INPUT, new Object[] { key, keyState, id },
                            EventManager.PRIORITY_LOW, timeManager.getRelativeTime() + 1 );

                }
                else if ( recordState == PLAYBACK ) {
                    eventManager.raiseAndHandle( EventManager.E_TYPE_ALTER_PLAYBACK,
                            new Object[] { key, keyState, id, timeManager, recordState } );
                }

            }
        }
        catch ( final IOException | ClassNotFoundException e ) {
            playerDisconnect( id );
        }

    }

    public static void playerDisconnect ( final int id ) {
        eventManager.deregister( objectManager.get( id ) );

        objectManager.remove( id );

        streamManager.removeOutputStream( id );
        streamManager.removeInputStream( id );
    }

    public static int createNewPlayer () {
        final GameObject player = new GameObject();
        player.setWidth( playerDimension );
        player.setHeight( playerDimension );
        player.setXpos( -100 );
        player.setYpos( -100 );
        player.setColor( 255, 0, 255 );
        player.setJumpAmount( -15f );
        player.addComponent( new Render() );
        player.addComponent( new PlayerControls() );
        player.addComponent( new NormalMovement() );
        player.addComponent( new SoftCollision() );
        player.addComponent( new PlaybackControls() );
        player.addComponent( new Spawn( spawnPoints ) );

        eventManager.register( player );

        objectManager.add( player );

        return player.getId();

    }

    public static void updateClients () {

        final ArrayList<RenderObject> sendObjects = objectManager.getSendableObjects();

        for ( final int id : streamManager.getOutputIds() ) {
            try {
                streamManager.writeObject( id, sendObjects );
            }
            catch ( final IOException e ) {
                playerDisconnect( id );
            }
        }
    }

    public static void detectCollision () {

        final ArrayList<Integer> ids = objectManager.getIds();

        for ( final int id1 : ids ) {
            for ( final int id2 : ids ) {
                if ( id1 == id2 ) {
                    continue;
                }

                final GameObject go1 = objectManager.get( id1 );
                final GameObject go2 = objectManager.get( id2 );

                if ( go1 == null || go2 == null ) {
                    continue;
                }

                final Rectangle2D go1Rect = new Rectangle.Double( go1.getXpos(), go1.getYpos(), go1.getWidth(),
                        go1.getHeight() );
                final Rectangle2D go2Rect = new Rectangle.Double( go2.getXpos(), go2.getYpos(), go2.getWidth(),
                        go2.getHeight() );

                if ( go1Rect.intersects( go2Rect ) ) {
                    eventManager.raise( EventManager.E_TYPE_COLLISION, new Object[] { go1, go2 },
                            EventManager.PRIORITY_HIGH, timeManager.getRelativeTime() );

                }
            }
        }
    }

    public static void recordFSM ( final char input ) {
        switch ( recordState ) {
            case NOT_RECORDING:
                if ( input == 'r' ) {
                    recordState = RECORDING;

                    objectManager.saveFirstState();
                    objectManager.add( recordIndicator );

                    eventManager.raiseAndHandle( EventManager.E_TYPE_RECORD );
                }

                break;

            case RECORDING:
                if ( input == 'r' ) {
                    recordState = PLAYBACK;

                    objectManager.remove( recordIndicator.getId() );
                    objectManager.saveLastState();
                    objectManager.restoreToFirstState();
                    objectManager.add( playbackIndicator );

                    eventManager.raiseAndHandle( EventManager.E_TYPE_PLAYBACK );

                }
                break;
            case PLAYBACK:
                if ( input == 't' ) {
                    recordState = NOT_RECORDING;

                    objectManager.remove( playbackIndicator.getId() );
                    objectManager.restoreToLastState();

                    eventManager.raiseAndHandle( EventManager.E_TYPE_STOP_RECORDING );

                    timeManager.resetTic();

                    recordedTics = new LinkedList<Long>();
                    usedTics = new LinkedList<Long>();

                }

            default:
                break;
        }
    }

    public static void setup () {

        final double floorHeight = gameHeight / 25;
        final double floorWidth = gameWidth * .6f;
        final double killZoneWidth = gameWidth * .4f;

        final GameObject floor = new GameObject();
        floor.setYpos( gameHeight - floorHeight );
        floor.setWidth( floorWidth );
        floor.setHeight( floorHeight );
        floor.addComponent( new Render() );
        floor.addComponent( new HardCollision() );

        eventManager.register( floor );
        objectManager.add( floor );

        final GameObject killZone = new GameObject();
        killZone.setYpos( gameHeight * 1.2f );
        killZone.setXpos( gameWidth - killZoneWidth );
        killZone.setWidth( killZoneWidth );
        killZone.setHeight( floorHeight );
        killZone.addComponent( new DeathCollision() );

        eventManager.register( killZone );
        objectManager.add( killZone );

        final double invisibleWallWidth = 10;

        final GameObject leftInvisWall = new GameObject();
        leftInvisWall.setXpos( -invisibleWallWidth );
        leftInvisWall.setWidth( invisibleWallWidth );
        leftInvisWall.setHeight( gameHeight - floorHeight );
        leftInvisWall.addComponent( new HardCollision() );

        eventManager.register( leftInvisWall );
        objectManager.add( leftInvisWall );

        final GameObject rightInvisWall = new GameObject();
        rightInvisWall.setXpos( gameWidth );
        rightInvisWall.setWidth( invisibleWallWidth );
        rightInvisWall.setHeight( gameHeight );
        rightInvisWall.addComponent( new HardCollision() );

        eventManager.register( rightInvisWall );
        objectManager.add( rightInvisWall );

        final GameObject platform = new GameObject();
        platform.setXpos( gameWidth / 5 );
        platform.setYpos( gameHeight - ( gameHeight / 4 ) );
        platform.setWidth( gameWidth / 4 );
        platform.setHeight( gameHeight / 15 );
        platform.setColor( 255, 255, 100 );
        platform.addComponent( new Render() );
        platform.addComponent( new HardCollision() );

        eventManager.register( platform );
        objectManager.add( platform );
        final GameObject verMovingPlatform = new GameObject();
        verMovingPlatform.setXpos( gameWidth - ( gameWidth / 5 ) );
        verMovingPlatform.setYpos( gameHeight - ( gameHeight / 6 ) );
        verMovingPlatform.setYvel( -2 );
        verMovingPlatform.setWidth( gameWidth / 6 );
        verMovingPlatform.setHeight( gameHeight / 15 );
        verMovingPlatform.setColor( 100, 200, 44 );

        verMovingPlatform.addComponent( new Render() );
        double x = verMovingPlatform.getXpos();
        double y = verMovingPlatform.getYpos();
        verMovingPlatform.addComponent( new CyclicalMovement( x, x, gameHeight / 5, y ) );
        verMovingPlatform.addComponent( new HardCollision() );

        eventManager.register( verMovingPlatform );
        objectManager.add( verMovingPlatform );

        final GameObject horMovingPlatform = new GameObject();
        horMovingPlatform.setWidth( gameWidth / 7 );
        horMovingPlatform.setHeight( ( gameHeight / 20 ) );
        horMovingPlatform.setXvel( -2 );
        horMovingPlatform.setXpos( gameWidth / 3 );
        horMovingPlatform.setYpos( gameHeight / 2 );
        horMovingPlatform.setColor( 10, 200, 200 );

        x = horMovingPlatform.getXpos();
        y = horMovingPlatform.getYpos();

        horMovingPlatform.addComponent( new Render() );
        horMovingPlatform.addComponent( new CyclicalMovement( x, gameWidth * ( 3 / 5f ), y, y ) );

        eventManager.register( horMovingPlatform );
        objectManager.add( horMovingPlatform );

        // Set up record record object
        recordIndicator.setXpos( 20 );
        recordIndicator.setYpos( 20 );
        recordIndicator.setWidth( 20 );
        recordIndicator.setHeight( 20 );
        recordIndicator.setColor( 255, 0, 0 );
        recordIndicator.addComponent( new Render() );

        playbackIndicator.setXpos( 20 );
        playbackIndicator.setYpos( 20 );
        playbackIndicator.setWidth( 20 );
        playbackIndicator.setHeight( 20 );
        playbackIndicator.setColor( 20, 20, 20 );
        playbackIndicator.addComponent( new Render() );

        // set up spawn points
        spawnPoints.add( new Rectangle2D.Double( floor.getWidth() / 4, floor.getYpos() - playerDimension,
                playerDimension, playerDimension ) );
        spawnPoints.add( new Rectangle2D.Double( floor.getWidth() - ( floor.getWidth() / 4 ),
                floor.getYpos() - playerDimension, playerDimension, playerDimension ) );
        spawnPoints.add( new Rectangle2D.Double( platform.getXpos() + ( platform.getWidth() / 2 ),
                platform.getYpos() - playerDimension, playerDimension, playerDimension ) );
    }

}
