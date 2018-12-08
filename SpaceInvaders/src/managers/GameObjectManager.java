package managers;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Hashtable;

import game_objects.GameObject;
import game_objects.RenderObject;

public class GameObjectManager {

    private final Hashtable<Integer, GameObject>  gameObjects           = new Hashtable<Integer, GameObject>();

    private static Hashtable<Integer, GameObject> savedFirstGameObjects = new Hashtable<Integer, GameObject>();

    private static Hashtable<Integer, GameObject> savedLastGameObjects  = new Hashtable<Integer, GameObject>();

    public GameObjectManager () {

    }

    public void add ( final GameObject player ) {
        synchronized ( gameObjects ) {
            gameObjects.put( player.getId(), player );
        }
    }

    public ArrayList<GameObject> getGameObjects () {
        final ArrayList<GameObject> gos = new ArrayList<GameObject>();
        synchronized ( gameObjects ) {
            gameObjects.values().forEach( go -> gos.add( go ) );
        }
        return gos;
    }

    public ArrayList<Integer> getIds () {
        final ArrayList<Integer> ids = new ArrayList<Integer>();
        synchronized ( gameObjects ) {
            for ( final Integer i : gameObjects.keySet() ) {
                ids.add( i );
            }
        }

        return ids;
    }

    public GameObject get ( final int id ) {
        synchronized ( gameObjects ) {
            return gameObjects.get( id );
        }
    }

    public void remove ( final int id ) {
        synchronized ( gameObjects ) {

            gameObjects.remove( id );
        }
    }

    public ArrayList<RenderObject> getSendableObjects () {
        final ArrayList<RenderObject> sendObjects = new ArrayList<RenderObject>();
        synchronized ( gameObjects ) {
            for ( final GameObject go : gameObjects.values() ) {
                if ( go.isRenderable() ) {
                    sendObjects.add( new RenderObject( go.getId(),
                            new Rectangle2D.Double( go.getXpos(), go.getYpos(), go.getWidth(), go.getHeight() ),
                            new int[] { go.getRed(), go.getGreen(), go.getBlue() } ) );
                }
            }
        }
        return sendObjects;
    }

    public void saveFirstState () {
        savedFirstGameObjects = new Hashtable<Integer, GameObject>();
        synchronized ( gameObjects ) {
            for ( final GameObject go : gameObjects.values() ) {
                final GameObject savedObject = new GameObject( go );
                savedFirstGameObjects.put( savedObject.getId(), savedObject );
            }
        }
    }

    public void restoreToFirstState () {
        synchronized ( gameObjects ) {
            for ( final GameObject go : gameObjects.values() ) {
                final GameObject goCopy = savedFirstGameObjects.get( go.getId() );
                if ( goCopy != null ) {
                    go.restore( savedFirstGameObjects.get( go.getId() ) );
                }
            }
        }
    }

    public void saveLastState () {
        savedLastGameObjects = new Hashtable<Integer, GameObject>();
        synchronized ( gameObjects ) {
            for ( final GameObject go : gameObjects.values() ) {
                final GameObject savedObject = new GameObject( go );
                savedLastGameObjects.put( savedObject.getId(), savedObject );
            }
        }

    }

    public void restoreToLastState () {
        synchronized ( gameObjects ) {
            for ( final GameObject go : gameObjects.values() ) {
                go.restore( savedLastGameObjects.get( go.getId() ) );
            }
        }

    }

}
