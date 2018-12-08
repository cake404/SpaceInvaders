package components;

import java.util.Observable;

public class NormalMovement extends Move {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void update ( final Observable o, final Object arg ) {
        parent.setXvel( parent.getXvel() + parent.getXacc() );
        parent.setYvel( parent.getYvel() + parent.getYacc() );

        parent.setXpos( parent.getXpos() + parent.getXvel() + parent.getFloorVel() );
        parent.setYpos( parent.getYpos() + parent.getYvel() );

    }

}
