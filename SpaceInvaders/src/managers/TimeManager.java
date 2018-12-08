package managers;

public class TimeManager {

    private volatile long       tic;

    private final long          originalTic;

    private final long          anchor;

    private static final String HALF_SPEED   = "half";
    private static final String NORMAL_SPEED = "normal";
    private static final String DOUBLE_SPEED = "double";

    private static final String DECREASE     = "decrease";
    private static final String INCREASE     = "increase";

    private String              speedState   = NORMAL_SPEED;

    private boolean             paused       = false;

    private long                lastTic      = 0;

    public TimeManager ( final long tic ) {
        this.tic = tic;
        this.originalTic = tic;
        this.anchor = System.currentTimeMillis();

    }

    public long getRelativeTime () {
        if ( paused ) {
            return lastTic;
        }
        return ( System.currentTimeMillis() - anchor ) / tic;
    }

    public void setTic ( final long tic ) {
        this.tic = tic;
    }

    public double getTic () {
        return tic;
    }

    public void decreaseTic () {
        speedFSM( DECREASE );
    }

    public void increaseTic () {
        speedFSM( INCREASE );
    }

    private void speedFSM ( final String direction ) {
        switch ( speedState ) {
            case ( NORMAL_SPEED ):
                if ( direction.equals( INCREASE ) ) {
                    speedState = DOUBLE_SPEED;
                    tic /= 2;
                }
                else if ( direction.equals( DECREASE ) ) {
                    speedState = HALF_SPEED;
                    tic *= 2;
                }
                break;
            case ( DOUBLE_SPEED ):
                if ( direction.equals( DECREASE ) ) {
                    speedState = NORMAL_SPEED;
                    tic *= 2;
                }

                break;
            case ( HALF_SPEED ):
                if ( direction.equals( INCREASE ) ) {
                    speedState = NORMAL_SPEED;
                    tic /= 2;
                }
                break;
            default:
                break;
        }

    }

    public void togglePause () {
        if ( paused ) {
            resume();
        }
        else {
            pause();
        }

    }

    private void pause () {
        lastTic = getRelativeTime();
        paused = true;

    }

    private void resume () {
        paused = false;
    }

    public void resetTic () {
        tic = originalTic;
        speedState = NORMAL_SPEED;
    }

    public static void main ( final String[] args ) throws InterruptedException {
        final TimeManager tm = new TimeManager( 1000 / 60 );

        while ( true ) {
            System.out.println( tm.getRelativeTime() );

            Thread.sleep( 1000 );
        }

    }

}
