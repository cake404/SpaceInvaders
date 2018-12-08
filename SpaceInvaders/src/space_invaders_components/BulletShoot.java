package space_invaders_components;

import java.util.Observable;

import default_components.Render;
import game_objects.GameObject;
import managers.EventManager;
import managers.GameObjectManager;

public class BulletShoot extends Spawn {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private long              timeOfLastBullet = 0;

    @Override
    public void update ( final Observable o, final Object arg ) {
        final Object[] arguments = (Object[]) arg;
        final GameObjectManager bullets = (GameObjectManager) arguments[0];
        final EventManager em = (EventManager) arguments[1];
        final long currentTime = (long) arguments[2];

        if ( timeOfLastBullet + 10 > currentTime ) {
            return;
        }

        timeOfLastBullet = currentTime;

        final GameObject bullet = new GameObject();
        bullet.setWidth( parent.getWidth() / 10 );
        bullet.setHeight( parent.getWidth() / 5 );
        bullet.setXpos( parent.getXpos() + ( parent.getWidth() / 2 ) - bullet.getWidth() );
        bullet.setYpos( parent.getYpos() - bullet.getHeight() );
        bullet.setColor( 255, 0, 0 );

        bullet.setYvel( -4 );
        bullet.addComponent( new BulletMovement() );
        bullet.addComponent( new DeathCollision() );
        bullet.addComponent( new Render() );

        bullets.add( bullet );
        em.register( bullet );

    }

}
