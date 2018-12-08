/**
 * 
 */
function update(eventType, arguments) {
    switch ( eventType ) {
	    case 0:
	        renderers.notifyHandlers( arguments );
	        break;
	    case 1:
	        inputListeners.notifyHandlers( arguments );
	        break;
	    case 2:
	        movers.notifyHandlers( arguments );
	        break;
	    case 3:
	        colliders.notifyHandlers( arguments );
	        break;
	    case 4:
	        spawners.notifyHandlers( arguments );
	        break;
	    default:
	        break;
    }	
}