package default_components;

import java.awt.geom.Rectangle2D;
import java.util.Observable;

import game_objects.GameObject;

public class SoftCollision extends Collide {

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

        final GameObject go2 = (GameObject) arguments[1];

        final Rectangle2D go1Rect = new Rectangle2D.Double( go1.getXpos(), go1.getYpos(), go1.getWidth(),
                go1.getHeight() );
        final Rectangle2D go2Rect = new Rectangle2D.Double( go2.getXpos(), go2.getYpos(), go2.getWidth(),
                go2.getHeight() );

        final Rectangle2D intersection = go1Rect.createIntersection( go2Rect );

        if ( intersection.getWidth() >= intersection.getHeight() ) {
            if ( parent.getYvel() >= 0 ) {
                // Collide below
                parent.setYpos( parent.getYpos() - intersection.getHeight() );
                parent.setYvel( 0 );
                parent.setFloorVel( go2.getXvel() );
                parent.setAbleToJump( true );
            }
            else {
                // Collide above
                parent.setYpos( parent.getYpos() + intersection.getHeight() );
                parent.setYvel( go2.getYvel() );

            }

        }
        else {
            if ( parent.getXvel() >= 0 ) {
                // Collide right
                parent.setXpos( parent.getXpos() - intersection.getWidth() );
                parent.setXvel( 0 );

            }
            else {
                // Collide left
                parent.setXpos( parent.getXpos() + intersection.getWidth() );
                parent.setXvel( 0 );
            }

        }

    }

}
