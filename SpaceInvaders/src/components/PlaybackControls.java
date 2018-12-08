package components;

import java.util.Observable;

import executables.Server;
import managers.TimeManager;

public class PlaybackControls extends Component {

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
        final TimeManager tm = (TimeManager) arguments[3];
        final int recordState = (int) arguments[4];
        if ( id != parent.getId() ) {
            return;
        }
        if ( keyState == Input.KEY_PRESSED && recordState == Server.PLAYBACK ) {
            switch ( key ) {
                case 'k':
                    tm.decreaseTic();
                    break;
                case 'l':
                    tm.increaseTic();
                    break;
                case 'p':
                    tm.togglePause();
                    break;
                default:
                    break;
            }
        }

    }

}
