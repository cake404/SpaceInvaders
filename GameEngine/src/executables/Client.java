package executables;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import components.Input;
import game_objects.RenderObject;
import processing.core.PApplet;

public class Client extends PApplet {

    private static ObjectInputStream       input_stream;
    private static ObjectOutputStream      output_stream;
    private static Socket                  s;

    private static int                     gameWidth;
    private static int                     gameHeight;
    private static int                     playerId;

    private static ArrayList<RenderObject> gameObjects = new ArrayList<RenderObject>();

    public static void main ( final String[] args ) {
        try {
            s = new Socket( "127.0.0.1", 6001 );
            input_stream = new ObjectInputStream( s.getInputStream() );
            output_stream = new ObjectOutputStream( s.getOutputStream() );

        }
        catch ( final IOException e ) {
            System.out.println( "Could not connect to host!" );
            System.exit( 1 );
        }

        try {
            final Object[] initialInfo = (Object[]) input_stream.readObject();
            gameWidth = (int) initialInfo[0];
            gameHeight = (int) initialInfo[1];
            playerId = (int) initialInfo[2];
        }
        catch ( final IOException | ClassNotFoundException e ) {
            e.printStackTrace();
        }

        System.out.println( gameWidth + " " + gameHeight );
        PApplet.main( "executables.Client" );

        while ( true ) {
            try {

                @SuppressWarnings ( "unchecked" )
                final ArrayList<RenderObject> readObject = (ArrayList<RenderObject>) input_stream.readObject();

                synchronized ( gameObjects ) {
                    gameObjects = readObject;
                }

            }
            catch ( ClassNotFoundException | IOException e ) {
                e.printStackTrace();
                System.exit( 1 );
            }
        }
    }

    @Override
    public void draw () {
        background( 255 );
        synchronized ( gameObjects ) {
            for ( final RenderObject ro : gameObjects ) {
                fill( ro.colors[0], ro.colors[1], ro.colors[2] );
                rect( (float) ro.rect.getX(), (float) ro.rect.getY(), (float) ro.rect.getWidth(),
                        (float) ro.rect.getHeight() );

                if ( ro.id == playerId ) {
                    fill( 0 );
                    textSize( 10 );
                    text( "You", (int) ro.rect.getX(), ( (int) ro.rect.getY() - 10 ) );
                }

            }

        }

    }

    @Override
    public void settings () {
        size( gameWidth, gameHeight );
    }

    @Override
    public void keyPressed () {

        try {
            output_stream.writeObject( new Object[] { key, Input.KEY_PRESSED } );
        }
        catch ( final IOException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyReleased () {

        try {
            output_stream.writeObject( new Object[] { key, Input.KEY_RELEASED } );
        }
        catch ( final IOException e ) {
            e.printStackTrace();
        }
    }

}
