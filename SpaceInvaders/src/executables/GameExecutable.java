package executables;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import components.HardCollision;
import components.Input;
import game_objects.GameObject;
import managers.EventManager;
import managers.GameObjectManager;
import managers.ScriptManager;
import managers.TimeManager;
import processing.core.PApplet;
import space_invaders_components.BulletShoot;
import space_invaders_components.PlayerInput;
import space_invaders_components.PlayerMovement;

public class GameExecutable extends PApplet {

    private static int               width         = 500;
    private static int               height        = 600;
    private static int               enemyRows     = 2;
    private static int               enemiesPerRow = 4;

    private static GameObjectManager objectManager = new GameObjectManager();
    private static int               playerObjectId;

    private static long              ticRate       = 16;
    private static TimeManager       timeManager   = new TimeManager( ticRate );

    private static EventManager      eventManager  = new EventManager();

    public static void main ( final String[] args ) {

        // Top Wall
        final GameObject topWall = new GameObject();
        topWall.setWidth( width );
        topWall.setHeight( height / 20 );
        topWall.setYpos( topWall.getHeight() );
        topWall.addComponent( new HardCollision() );

        objectManager.add( topWall );
        eventManager.register( topWall );

        // Initialize player
        final GameObject player = new GameObject();
        player.setWidth( width / 10 );
        player.setHeight( width / 15 );
        player.setXpos( ( width / 2 ) - ( player.getWidth() / 2 ) );
        player.setYpos( height - player.getHeight() - ( height / 20 ) );
        player.addComponent( new PlayerMovement() );
        player.addComponent( new PlayerInput() );
        player.addComponent( new BulletShoot() );

        playerObjectId = player.getId();
        objectManager.add( player );
        eventManager.register( player );

        // Initialize all enemies
        ScriptManager.loadScript( "scripts/initialize_enemies.js" );

        for ( int i = 0; i < enemyRows; i++ ) {
            for ( int j = 0; j < enemiesPerRow; j++ ) {
                final GameObject enemy = new GameObject();

                enemy.setWidth( ( width / enemiesPerRow ) * .4 );
                enemy.setHeight( ( width / enemiesPerRow ) * .25 );
                enemy.setXpos( ( width / enemiesPerRow ) * j );
                enemy.setYpos( ( height / 3 ) - ( enemy.getHeight() * i * 2 ) );

                objectManager.add( enemy );
                eventManager.register( enemy );
            }

        }

        // Separate thread to render objects
        PApplet.main( "executables.GameExecutable" );

        // Main game loop
        gameLoop();

    }

    private static void gameLoop () {

        long baseTic = timeManager.getRelativeTime();

        while ( true ) {

            final long currentTic = timeManager.getRelativeTime();

            if ( currentTic != baseTic ) {
                eventManager.raiseAndHandle( EventManager.E_TYPE_MOVEMENT );

                eventManager.handleEvents();

                baseTic = timeManager.getRelativeTime();
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
                    eventManager.raise( EventManager.E_TYPE_COLLISION,
                            new Object[] { go1, go2, objectManager, eventManager }, EventManager.PRIORITY_HIGH,
                            timeManager.getRelativeTime() );

                }
            }
        }
    }

    @Override
    public void settings () {
        size( width, height );
    }

    @Override
    public void keyReleased () {

        eventManager.raise( EventManager.E_TYPE_INPUT,
                new Object[] { Input.KEY_RELEASED, key, objectManager, eventManager }, EventManager.PRIORITY_LOW,
                timeManager.getRelativeTime() );

    }

    @Override
    public void keyPressed () {
        if ( key == ' ' ) {
            eventManager.raise( EventManager.E_TYPE_SPAWN, new Object[] { objectManager, eventManager },
                    EventManager.PRIORITY_HIGH, timeManager.getRelativeTime() );
        }
        eventManager.raise( EventManager.E_TYPE_INPUT, new Object[] { Input.KEY_PRESSED, key, objectManager },
                EventManager.PRIORITY_LOW, timeManager.getRelativeTime() );
    }

    @Override
    public void draw () {
        background( 120, 120, 120 );

        for ( final GameObject enemy : objectManager.getGameObjects() ) {
            rect( (float) enemy.getXpos(), (float) enemy.getYpos(), (float) enemy.getWidth(),
                    (float) enemy.getHeight() );
        }
    }

}
