package components;

import java.io.Serializable;
import java.util.Observer;

import game_objects.GameObject;

public abstract class Component implements Observer, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    GameObject                parent;

    public void setParentGameObject ( final GameObject go ) {
        parent = go;
    }
}
