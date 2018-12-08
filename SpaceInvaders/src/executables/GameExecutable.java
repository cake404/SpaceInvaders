package executables;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import default_components.HardCollision;
import default_components.Input;
import default_components.Render;
import default_components.SoftCollision;
import game_objects.GameObject;
import managers.EventManager;
import managers.GameObjectManager;
import managers.ScriptManager;
import managers.TimeManager;
import processing.core.PApplet;
import space_invaders_components.AlienMovement;
import space_invaders_components.BulletShoot;
import space_invaders_components.DeathCollision;
import space_invaders_components.EndGameCollision;
import space_invaders_components.PlayerInput;
import space_invaders_components.PlayerMovement;

public class GameExecutable extends PApplet {

    private static int                     width         = 500;
    private static int                     height        = 600;
    private static int                     enemyRows     = 2;
    private static int                     enemiesPerRow = 5;
    private static double                  alienSpeed    = 1;

    private static final GameObjectManager aliens        = new GameObjectManager();
    private static final GameObjectManager bullets       = new GameObjectManager();
    private static final GameObjectManager walls         = new GameObjectManager();
    private static final GameObject        player        = new GameObject();
    private static final GameObject        floor         = new GameObject();

    private static final long              ticRate       = 16;
    private static final TimeManager       timeManager   = new TimeManager( ticRate );
    private static final ScriptManager     scriptManager = new ScriptManager();
    private static final EventManager      eventManager  = new EventManager();

    private static volatile boolean        winGame       = false;
    private static volatile boolean        loseGame      = false;

    public static void main ( final String[] args ) {

        // Load color changing script
        scriptManager.loadScript( "scripts/change_color.js" );
        scriptManager.bindArgument( "player", player );

        // Top Wall
        final GameObject topWall = new GameObject();
        topWall.setWidth( width );
        topWall.setHeight( height / 20 );
        topWall.setYpos( -topWall.getHeight() );
        topWall.addComponent( new HardCollision() );
        topWall.addComponent( new Render() );

        walls.add( topWall );
        eventManager.register( topWall );

        // Left Wall
        final GameObject leftWall = new GameObject();
        leftWall.setWidth( width / 20 );
        leftWall.setHeight( height );
        leftWall.setXpos( -leftWall.getWidth() );
        leftWall.addComponent( new HardCollision() );

        walls.add( leftWall );
        eventManager.register( leftWall );

        // Right wall
        final GameObject rightWall = new GameObject();
        rightWall.setWidth( width / 20 );
        rightWall.setHeight( height );
        rightWall.setXpos( width );
        rightWall.addComponent( new HardCollision() );

        walls.add( rightWall );
        eventManager.register( rightWall );

        floor.setWidth( width );
        floor.setHeight( height / 20 );
        floor.setYpos( height );
        floor.addComponent( new EndGameCollision() );
        floor.addComponent( new Render() );

        eventManager.register( floor );

        // Initialize player
        player.setWidth( width / 10 );
        player.setHeight( width / 15 );
        player.setXpos( ( width / 2 ) - ( player.getWidth() / 2 ) );
        player.setYpos( height - player.getHeight() - ( height / 20 ) );
        player.setColor( 50, 120, 200 );
        player.addComponent( new PlayerMovement() );
        player.addComponent( new SoftCollision() );
        player.addComponent( new PlayerInput() );
        player.addComponent( new BulletShoot() );
        player.addComponent( new Render() );

        eventManager.register( player );

        // Initialize all enemies
        for ( int i = 0; i < enemyRows; i++ ) {
            for ( int j = 0; j < enemiesPerRow; j++ ) {
                final GameObject enemy = new GameObject();

                enemy.setWidth( ( width / enemiesPerRow ) * .5 );
                enemy.setHeight( ( width / enemiesPerRow ) * .25 );
                enemy.setXpos( ( width / enemiesPerRow ) * j );
                enemy.setYpos( ( height / 3 ) - ( enemy.getHeight() * i * 2 ) );
                enemy.setXvel( alienSpeed );
                enemy.addComponent( new Render() );
                enemy.addComponent( new DeathCollision() );

                final double sideDistance = ( width / enemiesPerRow ) - enemy.getWidth();
                final double downDistance = enemy.getHeight() * 2;
                enemy.addComponent( new AlienMovement( sideDistance, downDistance, enemy.getXpos() ) );

                aliens.add( enemy );
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
                eventManager.raiseAndHandleWithScript( EventManager.E_TYPE_MOVEMENT );

                detectCollision();

                eventManager.handleEventsWithScript();

                baseTic = timeManager.getRelativeTime();
            }
        }
    }

    public static void detectCollision () {

        final ArrayList<Integer> wallsIds = walls.getIds();
        final ArrayList<Integer> bulletIds = bullets.getIds();
        final ArrayList<Integer> alienIds = aliens.getIds();

        // Wall collision with player
        for ( final int wallId : wallsIds ) {

            final GameObject wall = walls.get( wallId );
            final Rectangle2D wallRect = new Rectangle.Double( wall.getXpos(), wall.getYpos(), wall.getWidth(),
                    wall.getHeight() );
            final Rectangle2D playerRect = new Rectangle.Double( player.getXpos(), player.getYpos(), player.getWidth(),
                    player.getHeight() );

            if ( wallRect.intersects( playerRect ) ) {
                eventManager.raise( EventManager.E_TYPE_COLLISION, new Object[] { player, wall },
                        EventManager.PRIORITY_HIGH, timeManager.getRelativeTime() );
            }

        }

        // bullet collision with aliens and walls
        for ( final int bulletId : bulletIds ) {
            final GameObject bullet = bullets.get( bulletId );

            final Rectangle2D bulletRect = new Rectangle.Double( bullet.getXpos(), bullet.getYpos(), bullet.getWidth(),
                    bullet.getHeight() );

            // Bullet collide with wall?
            for ( final int wallId : wallsIds ) {
                final GameObject wall = walls.get( wallId );
                final Rectangle2D wallRect = new Rectangle.Double( wall.getXpos(), wall.getYpos(), wall.getWidth(),
                        wall.getHeight() );
                if ( wallRect.intersects( bulletRect ) ) {
                    eventManager.raise( EventManager.E_TYPE_COLLISION, new Object[] { bullet, bullets, eventManager },
                            EventManager.PRIORITY_HIGH, timeManager.getRelativeTime() );
                }
            }

            // Bullet collide with alien?
            for ( final int alienId : alienIds ) {
                final GameObject alien = aliens.get( alienId );
                final Rectangle2D alienRect = new Rectangle.Double( alien.getXpos(), alien.getYpos(), alien.getWidth(),
                        alien.getHeight() );

                if ( alienRect.intersects( bulletRect ) ) {
                    eventManager.raise( EventManager.E_TYPE_COLLISION, new Object[] { bullet, bullets, eventManager },
                            EventManager.PRIORITY_HIGH, timeManager.getRelativeTime() );
                    eventManager.raise( EventManager.E_TYPE_COLLISION, new Object[] { alien, aliens, eventManager },
                            EventManager.PRIORITY_HIGH, timeManager.getRelativeTime() );
                }
            }
        }

        // alien collide with floor or player? - lose game
        for ( final int alienId : alienIds ) {
            final GameObject alien = aliens.get( alienId );
            final Rectangle2D alienRect = new Rectangle.Double( alien.getXpos(), alien.getYpos(), alien.getWidth(),
                    alien.getHeight() );
            final Rectangle2D playerRect = new Rectangle.Double( player.getXpos(), player.getYpos(), player.getWidth(),
                    player.getHeight() );
            final Rectangle2D floorRect = new Rectangle.Double( floor.getXpos(), floor.getYpos(), floor.getWidth(),
                    floor.getHeight() );

            if ( !winGame && ( alienRect.intersects( playerRect ) || alienRect.intersects( floorRect ) ) ) {
                loseGame = true;
            }

        }

        // No aliens left? - win game
        if ( !loseGame && aliens.getGameObjects().isEmpty() ) {
            winGame = true;
        }

    }

    @Override
    public void settings () {
        size( width, height );
    }

    @Override
    public void keyReleased () {

        eventManager.raise( EventManager.E_TYPE_INPUT, new Object[] { Input.KEY_RELEASED, key },
                EventManager.PRIORITY_LOW, timeManager.getRelativeTime() );

    }

    @Override
    public void keyPressed () {
        if ( key == ' ' ) {
            eventManager.raise( EventManager.E_TYPE_SPAWN,
                    new Object[] { bullets, eventManager, timeManager.getRelativeTime() }, EventManager.PRIORITY_HIGH,
                    timeManager.getRelativeTime() );
        }

        if ( key == 'p' ) {
            scriptManager.executeScript();
        }
        else {
            eventManager.raise( EventManager.E_TYPE_INPUT, new Object[] { Input.KEY_PRESSED, key },
                    EventManager.PRIORITY_LOW, timeManager.getRelativeTime() );
        }
    }

    @Override
    public void draw () {
        background( 120, 120, 120 );

        if ( winGame ) {
            textSize( width / 20 );
            text( "Congratulations!\nYou Won!", width / 4, height / 4 );
        }
        else if ( loseGame ) {
            textSize( width / 20 );
            text( "Sorry!\nYou Lost!", width / 4, height / 4 );
        }
        else {
            eventManager.raiseAndHandleWithScript( EventManager.E_TYPE_RENDER, new Object[] { this } );
        }
    }

}
