package managers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class StreamManager {

    private final HashMap<Integer, ObjectInputStream>  input_streams  = new HashMap<Integer, ObjectInputStream>();
    private final HashMap<Integer, ObjectOutputStream> output_streams = new HashMap<Integer, ObjectOutputStream>();

    public void addOutputStream ( final int id, final ObjectOutputStream os ) {
        synchronized ( output_streams ) {
            output_streams.put( id, os );
        }
    }

    public void addInputStream ( final int id, final ObjectInputStream is ) {
        synchronized ( input_streams ) {
            input_streams.put( id, is );
        }
    }

    public void writeObject ( final int id, final Object object ) throws IOException {
        synchronized ( output_streams ) {
            final ObjectOutputStream os = output_streams.get( id );

            if ( os != null ) {
                os.writeObject( object );
            }
        }
    }

    public Object readObject ( final int id ) throws IOException, ClassNotFoundException {
        ObjectInputStream is = null;
        synchronized ( input_streams ) {
            is = input_streams.get( id );
        }

        return is.readObject();
    }

    public void removeOutputStream ( final int id ) {
        synchronized ( output_streams ) {
            output_streams.remove( id );
        }
    }

    public void removeInputStream ( final int id ) {
        synchronized ( input_streams ) {
            input_streams.remove( id );
        }
    }

    public ArrayList<Integer> getOutputIds () {
        final ArrayList<Integer> ids = new ArrayList<Integer>();
        synchronized ( output_streams ) {
            for ( final Integer i : output_streams.keySet() ) {
                ids.add( i );
            }
        }

        return ids;
    }

}
