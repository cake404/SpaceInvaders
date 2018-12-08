package game_objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import default_components.Component;
import default_components.Render;

public class GameObject implements Serializable {

    /**
     *
     */
    private static final long     serialVersionUID = 1L;

    public static int             NUM_GAME_OBJECTS = 0;

    private int                   id;
    private int                   red, green, blue = 0;
    private double                width, height = 0;
    private volatile double       xpos, floorVel, xvel, xacc = 0;
    private volatile double       ypos, yvel, yacc = 0;

    private final List<Component> components       = new ArrayList<Component>();

    private double                jumpAmount       = 0;
    private boolean               ableToJump       = false;

    private boolean               isRenderable     = false;

    private boolean               isDead           = true;

    public GameObject () {
        createId();
    }

    public GameObject ( final GameObject other ) {
        xpos = other.getXpos();
        xvel = other.getXvel();
        floorVel = other.getFloorVel();
        xacc = other.getXacc();

        ypos = other.getYpos();
        yvel = other.getYvel();
        yacc = other.getYacc();

        ableToJump = other.isAbleToJump();

        id = other.getId();
    }

    public void addComponent ( final Component c ) {
        c.setParentGameObject( this );
        components.add( c );

        if ( c instanceof Render ) {
            isRenderable = true;
        }
    }

    public List<Component> getComponents () {
        return components;
    }

    private void createId () {
        NUM_GAME_OBJECTS++;
        id = NUM_GAME_OBJECTS;
    }

    public int getId () {
        return id;
    }

    public double getXvel () {
        return xvel;
    }

    public void setXvel ( final double xvel ) {
        this.xvel = xvel;
    }

    public double getXacc () {
        return xacc;
    }

    public void setXacc ( final double xacc ) {
        this.xacc = xacc;
    }

    public double getYvel () {
        return yvel;
    }

    public void setYvel ( final double yvel ) {
        this.yvel = yvel;
    }

    public double getYacc () {
        return yacc;
    }

    public void setYacc ( final double yacc ) {
        this.yacc = yacc;
    }

    public int getBlue () {
        return blue;
    }

    public int getGreen () {
        return green;
    }

    public int getRed () {
        return red;
    }

    public void setColor ( final int r, final int g, final int b ) {
        red = r;
        green = g;
        blue = b;
    }

    public boolean isAbleToJump () {
        return ableToJump;
    }

    public void setAbleToJump ( final boolean ableToJump ) {
        this.ableToJump = ableToJump;
    }

    public double getJumpAmount () {
        return jumpAmount;
    }

    public void setJumpAmount ( final double jumpAmount ) {
        this.jumpAmount = jumpAmount;
    }

    public double getFloorVel () {
        return floorVel;
    }

    public void setFloorVel ( final double floorVel ) {
        this.floorVel = floorVel;
    }

    public double getXpos () {
        return xpos;
    }

    public void setXpos ( final double xpos ) {
        this.xpos = xpos;
    }

    public double getYpos () {
        return ypos;
    }

    public void setYpos ( final double ypos ) {
        this.ypos = ypos;
    }

    public double getHeight () {
        return height;
    }

    public void setHeight ( final double height ) {
        this.height = height;
    }

    public double getWidth () {
        return width;
    }

    public void setWidth ( final double width ) {
        this.width = width;
    }

    public boolean isRenderable () {
        return isRenderable;
    }

    public boolean isDead () {
        return isDead;
    }

    public void setIsDead ( final boolean isDead ) {
        this.isDead = isDead;
    }

    public void restore ( final GameObject other ) {
        xpos = other.getXpos();
        xvel = other.getXvel();
        floorVel = other.getFloorVel();
        xacc = other.getXacc();

        ypos = other.getYpos();
        yvel = other.getYvel();
        yacc = other.getYacc();

        ableToJump = other.isAbleToJump();
    }

}
