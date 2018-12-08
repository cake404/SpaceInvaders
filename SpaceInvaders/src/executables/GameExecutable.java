package executables;

import game_objects.GameObject;
import managers.GameObjectManager;
import managers.ScriptManager;
import processing.core.PApplet;

public class GameExecutable extends PApplet {

    private static int               width   = 500;
    private static int               height  = 600;

    private static GameObject        player  = new GameObject();

    private static GameObjectManager enemies = new GameObjectManager();

    public static void main ( final String[] args ) {

        // Initialize player
        ScriptManager.loadScript( "scripts/initialize_player.js" );
        ScriptManager.bindArgument( "player", player );
        ScriptManager.executeScript( width, height );

        // Initialize all enemies
        // ScriptManager.loadScript( "scripts/intialize_enemies.js" );

        PApplet.main( "executables.GameExecutable" );

    }

    @Override
    public void settings () {
        size( width, height );
    }

    @Override
    public void draw () {
        rect( (float) player.getXpos(), (float) player.getYpos(), (float) player.getWidth(),
                (float) player.getHeight() );
    }

}
