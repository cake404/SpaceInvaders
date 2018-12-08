package space_invaders_components;

import java.util.Observable;

import game_objects.GameObject;
import managers.EventManager;
import managers.GameObjectManager;

public class BulletShoot extends Spawn {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void update ( final Observable o, final Object arg ) {
        final Object[] arguments = (Object[]) arg;
        final GameObjectManager gom = (GameObjectManager) arguments[0];
        final EventManager em = (EventManager) arguments[1];

        final GameObject bullet = new GameObject();
        bullet.setWidth( parent.getWidth() / 10 );
        bullet.setHeight( parent.getWidth() / 10 );
        bullet.setXpos( parent.getXpos() + ( parent.getWidth() / 2 ) - bullet.getWidth() );
        bullet.setYpos( parent.getYpos() - bullet.getHeight() );

        bullet.setYvel( -4 );
        bullet.addComponent( new BulletMovement() );
        bullet.addComponent( new DeathCollision() );

        gom.add( bullet );
        em.register( bullet );

    }

}
