package managers;

import java.util.Observable;
import java.util.PriorityQueue;

import default_components.Collide;
import default_components.Component;
import default_components.Input;
import default_components.Move;
import default_components.Render;
import game_objects.GameObject;
import space_invaders_components.Spawn;

public class EventManager {

    public static final int                          E_TYPE_RENDER      = 0;
    public static final int                          E_TYPE_INPUT       = 1;
    public static final int                          E_TYPE_MOVEMENT    = 2;
    public static final int                          E_TYPE_COLLISION   = 3;
    public static final int                          E_TYPE_SPAWN       = 4;

    public static final int                          PRIORITY_VERY_HIGH = 0;
    public static final int                          PRIORITY_HIGH      = 1;
    public static final int                          PRIORITY_LOW       = 2;
    public static final int                          PRIORITY_VERY_LOW  = 3;

    private final EventHandlerList                   renderers          = new EventHandlerList();
    private final EventHandlerList                   inputListeners     = new EventHandlerList();
    private final EventHandlerList                   movers             = new EventHandlerList();
    private final EventHandlerList                   colliders          = new EventHandlerList();
    private final EventHandlerList                   spawners           = new EventHandlerList();

    private static final PriorityQueue<TimeLineItem> timeLine           = new PriorityQueue<TimeLineItem>();
    private static final ScriptManager               scriptManager      = new ScriptManager();

    public EventManager () {
        scriptManager.loadScript( "scripts/handle_event.js" );
        scriptManager.bindArgument( "renderers", renderers );
        scriptManager.bindArgument( "inputListeners", inputListeners );
        scriptManager.bindArgument( "movers", movers );
        scriptManager.bindArgument( "colliders", colliders );
        scriptManager.bindArgument( "spawners", spawners );
    }

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
            else if ( c instanceof Move ) {
                movers.deleteObserver( c );
            }
            else if ( c instanceof Collide ) {
                colliders.deleteObserver( c );
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

        final TimeLineItem tli = new TimeLineItem( eventType, args, priority, timeStamp );
        synchronized ( timeLine ) {
            timeLine.add( tli );
        }

    }

    public void raiseWithScript ( final int eventType, final int priority, final long timeStamp ) {
        raiseWithScript( eventType, null, priority, timeStamp );
    }

    public void raiseWithScript ( final int eventType, final Object args, final int priority, final long timeStamp ) {
        scriptManager.loadScript( "scripts/raise_event.js" );
        scriptManager.bindArgument( "tli", new TimeLineItem() );
        scriptManager.bindArgument( "timeLine", timeLine );
        scriptManager.executeScript( eventType, args, priority, timeStamp );
    }

    public void handleEventsWithScript () {

        synchronized ( timeLine ) {

            while ( !timeLine.isEmpty() ) {
                final TimeLineItem tli = timeLine.poll();

                raiseAndHandleWithScript( tli.eventType, tli.arguments );
            }
        }
    }

    public void raiseAndHandleWithScript ( final int eventType ) {
        raiseAndHandleWithScript( eventType, null );
    }

    public void raiseAndHandleWithScript ( final int eventType, final Object arguments ) {

        scriptManager.executeScript( eventType, arguments );

    }

    public class EventHandlerList extends Observable {
        public void notifyHandlers ( final Object args ) {
            setChanged();
            notifyObservers( args );

        }
    }

    public class TimeLineItem implements Comparable<TimeLineItem> {

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

        public TimeLineItem () {

        }

        @Override
        public int compareTo ( final TimeLineItem tli ) {

            if ( timeStamp > tli.timeStamp ) {
                return 1;
            }

            if ( timeStamp < tli.timeStamp ) {
                return -1;
            }

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
