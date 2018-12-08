package space_invaders_components;

import java.util.Observable;

import components.Move;

public class AlienMovement extends Move {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final double      sideDistance;
    private final double      downDistance;
    private double            startPos;

    public static final int   RIGHT            = 0;
    public static final int   LEFT             = 1;
    public static final int   DOWN_AFTER_RIGHT = 2;
    public static final int   DOWN_AFTER_LEFT  = 3;

    private int               direction        = RIGHT;

    public AlienMovement ( final double sideDistance, final double downDistance, final double startPos ) {
        this.sideDistance = sideDistance;
        this.downDistance = downDistance;
        this.startPos = startPos;

    }

    @Override
    public void update ( final Observable o, final Object arg ) {
        parent.setXpos( parent.getXpos() + parent.getXvel() );
        parent.setYpos( parent.getYpos() + parent.getYvel() );

        switch ( direction ) {
            case RIGHT:
                if ( parent.getXpos() >= startPos + sideDistance ) {
                    direction = DOWN_AFTER_RIGHT;
                    parent.setYvel( parent.getXvel() );
                    parent.setXvel( 0 );
                    startPos = parent.getYpos();
                }
                break;
            case DOWN_AFTER_RIGHT:
                if ( parent.getYpos() >= startPos + downDistance ) {
                    direction = LEFT;
                    parent.setXvel( -parent.getYvel() );
                    parent.setYvel( 0 );
                    startPos = parent.getXpos();
                }

                break;

            case LEFT:
                if ( parent.getXpos() <= startPos - sideDistance ) {
                    direction = DOWN_AFTER_LEFT;
                    parent.setYvel( -parent.getXvel() );
                    parent.setXvel( 0 );
                    startPos = parent.getYpos();
                }

                break;
            case DOWN_AFTER_LEFT:
                if ( parent.getYpos() >= startPos + downDistance ) {
                    direction = RIGHT;
                    parent.setXvel( parent.getYvel() );
                    parent.setYvel( 0 );
                    startPos = parent.getXpos();
                }
                break;

            default:
                break;
        }

    }

}
