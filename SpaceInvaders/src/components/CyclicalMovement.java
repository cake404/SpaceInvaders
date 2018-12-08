package components;

import java.util.Observable;

public class CyclicalMovement extends Move {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final double      minXpos;
    private final double      maxXpos;
    private final double      minYpos;
    private final double      maxYpos;

    public CyclicalMovement ( final double minXpos, final double maxXpos, final double minYpos, final double maxYpos ) {
        this.minXpos = minXpos;
        this.maxXpos = maxXpos;
        this.minYpos = minYpos;
        this.maxYpos = maxYpos;
    }

    @Override
    public void update ( final Observable o, final Object arg ) {

        if ( parent.getXpos() < minXpos || parent.getXpos() > maxXpos ) {
            parent.setXvel( parent.getXvel() * -1 );
        }
        if ( parent.getYpos() < minYpos || parent.getYpos() > maxYpos ) {
            parent.setYvel( parent.getYvel() * -1 );
        }

        parent.setXpos( parent.getXpos() + parent.getXvel() );
        parent.setYpos( parent.getYpos() + parent.getYvel() );
    }

}
