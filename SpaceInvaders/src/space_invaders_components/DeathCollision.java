package space_invaders_components;

import java.util.Observable;

import components.Collide;
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
        final GameObjectManager gom = (GameObjectManager) arguments[2];
        final EventManager em = (EventManager) arguments[3];

        gom.remove( parent.getId() );
        em.deregister( parent );

    }

}
