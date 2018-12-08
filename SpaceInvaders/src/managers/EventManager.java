package managers;

import java.util.ArrayList;
import java.util.Observable;
import java.util.PriorityQueue;

import components.Collide;
import components.Component;
import components.Input;
import components.Move;
import components.PlaybackControls;
import components.PlayerControls;
import components.Render;
import game_objects.GameObject;
import space_invaders_components.Spawn;

public class EventManager {

    public static final int                          E_TYPE_RENDER         = 0;
    public static final int                          E_TYPE_INPUT          = 1;
    public static final int                          E_TYPE_MOVEMENT       = 2;
    public static final int                          E_TYPE_COLLISION      = 3;
    public static final int                          E_TYPE_RECORD         = 4;
    public static final int                          E_TYPE_PLAYBACK       = 5;
    public static final int                          E_TYPE_STOP_RECORDING = 6;
    public static final int                          E_TYPE_ALTER_PLAYBACK = 7;
    public static final int                          E_TYPE_SPAWN          = 8;

    public static final int                          PRIORITY_VERY_HIGH    = 0;
    public static final int                          PRIORITY_HIGH         = 1;
    public static final int                          PRIORITY_LOW          = 2;
    public static final int                          PRIORITY_VERY_LOW     = 3;

    private final EventHandlerList                   renderers             = new EventHandlerList();
    private final EventHandlerList                   inputListeners        = new EventHandlerList();
    private final EventHandlerList                   movers                = new EventHandlerList();
    private final EventHandlerList                   colliders             = new EventHandlerList();
    private final EventHandlerList                   playbackAlterers      = new EventHandlerList();
    private final EventHandlerList                   spawners              = new EventHandlerList();

    private static final PriorityQueue<TimeLineItem> timeLine              = new PriorityQueue<TimeLineItem>();

    private static final ArrayList<TimeLineItem>     recordingTimeItems    = new ArrayList<TimeLineItem>();

    private volatile boolean                         recording             = false;
    private volatile boolean                         playback              = false;

    public void register ( final GameObject go ) {
        for ( final Component c : go.getComponents() ) {
            if ( c instanceof Render ) {
                renderers.addObserver( c );
            }
            else if ( c instanceof Input ) {
                inputListeners.addObserver( c );
            }
            else if ( c instanceof Move ) {
                movers.addObserver( c );
            }
            else if ( c instanceof Collide ) {
                colliders.addObserver( c );
            }
            else if ( c instanceof PlaybackControls ) {
                playbackAlterers.addObserver( c );
            }
            else if ( c instanceof Spawn ) {
                spawners.addObserver( c );
            }
        }
    }

    public void deregister ( final GameObject go ) {
        if ( go == null ) {
            return;
        }
        for ( final Component c : go.getComponents() ) {
            if ( c instanceof Render ) {
                renderers.deleteObserver( c );
            }
            else if ( c instanceof PlayerControls ) {
                inputListeners.deleteObserver( c );
            }
            else if ( c instanceof Move ) {
                movers.deleteObserver( c );
            }
            else if ( c instanceof Collide ) {
                colliders.deleteObserver( c );
            }
            else if ( c instanceof PlaybackControls ) {
                playbackAlterers.deleteObserver( c );
            }
            else if ( c instanceof Spawn ) {
                spawners.deleteObserver( c );
            }
        }
    }

    public void raise ( final int eventType, final int priority, final long timeStamp ) {
        raise( eventType, null, priority, timeStamp );
    }

    public void raise ( final int eventType, final Object args, final int priority, final long timeStamp ) {

        if ( !playback ) {
            final TimeLineItem tli = new TimeLineItem( eventType, args, priority, timeStamp );
            synchronized ( timeLine ) {
                timeLine.add( tli );
            }
            if ( recording ) {

                synchronized ( recordingTimeItems ) {
                    recordingTimeItems.add( tli );
                }
            }
        }

    }

    public void handleEvents () {

        synchronized ( timeLine ) {

            while ( !timeLine.isEmpty() ) {
                final TimeLineItem tli = timeLine.poll();

                raiseAndHandle( tli.eventType, tli.arguments );
            }
        }
    }

    public void raiseAndHandle ( final int eventType ) {
        raiseAndHandle( eventType, null );
    }

    public void raiseAndHandle ( final int eventType, final Object arguments ) {
        switch ( eventType ) {
            case E_TYPE_RENDER:
                renderers.notifyHandlers( arguments );
                break;
            case E_TYPE_INPUT:
                inputListeners.notifyHandlers( arguments );
                break;
            case E_TYPE_MOVEMENT:
                movers.notifyHandlers( arguments );
                break;
            case E_TYPE_COLLISION:
                colliders.notifyHandlers( arguments );
                break;
            case E_TYPE_ALTER_PLAYBACK:
                playbackAlterers.notifyHandlers( arguments );
                break;
            case E_TYPE_SPAWN:
                spawners.notifyHandlers( arguments );
                break;
            case E_TYPE_RECORD:
                recordEvents();
                break;
            case E_TYPE_PLAYBACK:
                playbackEvents();
                break;
            case E_TYPE_STOP_RECORDING:
                stopPlayback();
                break;
            default:
                break;
        }
    }

    private void playbackEvents () {
        if ( recording ) {
            playback = true;
            recording = false;
        }
    }

    private void recordEvents () {
        recording = true;
    }

    private void stopPlayback () {
        if ( playback ) {
            playback = false;
        }
    }

    public void handleRecordedEvents ( final long tic ) {

        final ArrayList<TimeLineItem> onTicEvents = new ArrayList<TimeLineItem>();

        for ( final TimeLineItem tli : recordingTimeItems ) {
            if ( tli.timeStamp != tic ) {
                continue;
            }
            onTicEvents.add( tli );
        }

        onTicEvents.sort( timeLine.comparator() );

        onTicEvents.forEach( tli -> raiseAndHandle( tli.eventType, tli.arguments ) );

    }

    private class EventHandlerList extends Observable {
        public void notifyHandlers ( final Object args ) {
            setChanged();
            notifyObservers( args );

        }
    }

    private class TimeLineItem implements Comparable<TimeLineItem> {

        public long   timeStamp;
        public Object arguments;
        public int    eventType;
        public int    priority;

        public TimeLineItem ( final int eventType, final Object arguments, final int priority, final long timeStamp ) {
            this.timeStamp = timeStamp;
            this.arguments = arguments;
            this.eventType = eventType;
            this.priority = priority;
        }

        @Override
        public int compareTo ( final TimeLineItem tli ) {
            if ( priority > tli.priority ) {
                return 1;
            }

            if ( priority < tli.priority ) {
                return -1;
            }

            return 0;
        }

    }

}
