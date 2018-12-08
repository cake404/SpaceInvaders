package space_invaders_components;

import java.util.Observable;

import components.Move;

public class BulletMovement extends Move {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void update ( final Observable o, final Object arg ) {
        parent.setYvel( parent.getYvel() + parent.getYacc() );
        parent.setYpos( parent.getYpos() + parent.getYvel() );

    }

}
