package components;

import java.util.Observable;

public class PlayerControls extends Input {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void update ( final Observable o, final Object arg ) {
        final Object[] arguments = (Object[]) arg;
        final char key = Character.toLowerCase( (char) arguments[0] );
        final int keyState = (int) arguments[1];
        final int id = (int) arguments[2];
        if ( id != parent.getId() ) {
            return;
        }

        if ( keyState == Input.KEY_RELEASED ) {
            parent.setXacc( 0 );
            parent.setXvel( 0 );
        }
        else if ( keyState == Input.KEY_PRESSED ) {
            switch ( key ) {
                case 'a':
                    parent.setXvel( -3 );
                    break;
                case 'd':
                    parent.setXvel( 3 );
                    break;
                case ' ':
                    if ( parent.isAbleToJump() ) {
                        parent.setYvel( parent.getJumpAmount() );
                        parent.setFloorVel( 0 );
                        parent.setAbleToJump( false );
                    }
                default:
                    break;
            }
        }

    }

}
