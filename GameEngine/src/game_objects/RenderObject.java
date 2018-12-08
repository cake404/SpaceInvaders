package game_objects;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class RenderObject implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public int                id;
    public Rectangle2D        rect;
    public int[]              colors;

    public RenderObject ( final int id, final Rectangle2D rect, final int[] colors ) {
        this.id = id;
        this.rect = rect;
        this.colors = colors;
    }
}
