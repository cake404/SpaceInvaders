package space_invaders_components;

import java.util.Observable;

import components.Input;

public class PlayerInput extends Input {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void update ( final Observable arg0, final Object arg1 ) {
        final Object[] arguments = (Object[]) arg1;
        final int keyState = (int) arguments[0];
        final char key = Character.toLowerCase( (char) arguments[1] );

        if ( keyState != KEY_PRESSED && keyState != KEY_RELEASED ) {
            return;
        }

        switch ( key ) {
            case 'a':
                parent.setXvel( -4 * keyState );
                break;
            case 'd':
                parent.setXvel( 4 * keyState );
                break;
            default:
                break;
        }

    }
}
