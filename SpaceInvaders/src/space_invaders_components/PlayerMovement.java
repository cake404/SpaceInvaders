package space_invaders_components;

import java.util.Observable;

import default_components.Move;

public class PlayerMovement extends Move {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void update ( final Observable o, final Object arg ) {
        parent.setXvel( parent.getXvel() + parent.getXacc() );
        parent.setXpos( parent.getXpos() + parent.getXvel() );
    }

}
