package space_invaders_components;

import java.util.Observable;

import default_components.Collide;
import game_objects.GameObject;
import managers.EventManager;
import managers.GameObjectManager;

public class DeathCollision extends Collide {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void update ( final Observable o, final Object arg ) {
        final Object[] arguments = (Object[]) arg;
        final GameObject go1 = (GameObject) arguments[0];

        if ( go1.getId() != parent.getId() ) {
            return;
        }

        final GameObjectManager gom = (GameObjectManager) arguments[1];
        final EventManager em = (EventManager) arguments[2];

        gom.remove( parent.getId() );
        em.deregister( parent );

    }

}
