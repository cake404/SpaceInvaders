package components;

import java.util.Observable;

import game_objects.GameObject;

public class DeathCollision extends Collide {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void update ( final Observable o, final Object arg ) {
        final Object[] arguments = (Object[]) arg;
        final GameObject go1 = (GameObject) arguments[0];

        if ( go1.getId() != parent.getId() ) {
            return;
        }

        final GameObject go2 = (GameObject) arguments[1];

        go2.setIsDead( true );

        go2.setXpos( -1000 );
        go2.setYpos( -1000 );

        go2.setXvel( 0 );
        go2.setFloorVel( 0 );
        go2.setYvel( 0 );

        go2.setXacc( 0 );
        go2.setYacc( 0 );

        // go2.setXpos( 100 );
        // go2.setYpos( 100 );
        //
        // go2.setXvel( 0 );
        // go2.setYvel( 0 );
        //
        // go2.setAbleToJump( false );
    }

}
