package default_components;

import java.util.Observable;

import processing.core.PApplet;

public class Render extends Component {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void update ( final Observable o, final Object arg ) {
        final Object[] arguments = (Object[]) arg;
        final PApplet renderer = (PApplet) arguments[0];
        renderer.fill( parent.getRed(), parent.getGreen(), parent.getBlue() );
        renderer.rect( (float) parent.getXpos(), (float) parent.getYpos(), (float) parent.getWidth(),
                (float) parent.getHeight() );
    }

}
