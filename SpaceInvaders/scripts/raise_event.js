/**
 * 
 */
function update(eventType, args, priority,  timeStamp) {
	tli.eventType = eventType;
	tli.args = args;
	tli.priority = priority;
	tli.timeStamp = timeStamp;
	timeLine.add(tli);
}