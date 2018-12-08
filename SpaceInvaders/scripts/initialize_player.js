/**
 * initializes a player with their position and other attributes
 */
function update(width, height) {
	
	player.setWidth(width / 10);
	player.setHeight(width / 15);
	
	player.setXpos((width / 2) - (player.getWidth() / 2));
	player.setYpos(height - player.getHeight() - (height / 20));
	
	print(player.getXpos());
	print(player.getYpos());
}