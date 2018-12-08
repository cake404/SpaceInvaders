package components;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Observable;

import game_objects.GameObject;

public class Spawn extends Component {

    /**
     *
     */
    private static final long            serialVersionUID = 1L;

    private final ArrayList<Rectangle2D> spawnPoints;

    public Spawn ( final ArrayList<Rectangle2D> spawnPoints ) {
        this.spawnPoints = spawnPoints;
    }

    @SuppressWarnings ( "unchecked" )
    @Override
    public void update ( final Observable arg0, final Object arg ) {
        if ( !parent.isDead() ) {
            return;
        }

        final Object[] arguments = (Object[]) arg;
        final double gravity = (double) arguments[0];

        final ArrayList<GameObject> gameObjects = (ArrayList<GameObject>) arguments[1];

        for ( final Rectangle2D spawn : spawnPoints ) {
            boolean canSpawnHere = true;
            for ( final GameObject go : gameObjects ) {
                if ( spawn.intersects( go.getXpos(), go.getYpos(), go.getWidth(), go.getHeight() ) ) {
                    canSpawnHere = false;
                }
            }
            if ( canSpawnHere ) {
                parent.setXpos( spawn.getX() );
                parent.setYpos( spawn.getY() );
                parent.setYacc( gravity );
                parent.setIsDead( false );
                return;
            }
        }
    }
}
