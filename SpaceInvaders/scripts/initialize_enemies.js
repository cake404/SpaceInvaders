/**
 * 
 */
function update(numEnemies, width, height, currentRow, currentEnemy) {
	enemy.setWidth((width / numEnemies) * .4);
	enemy.setHeight((width / numEnemies) * .25);
	
	enemy.setXpos((width / numEnemies) * currentEnemy);
	enemy.setYpos((height / 3) - (enemy.getHeight() * currentRow * 2));

}