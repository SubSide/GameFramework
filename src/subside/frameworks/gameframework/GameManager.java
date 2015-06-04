package subside.frameworks.gameframework;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import subside.frameworks.gameframework.framework.Game;
import subside.frameworks.gameframework.framework.GamePlayer;
import subside.frameworks.gameframework.framework.RunningGame;

public class GameManager {
    private static GameManager manager;

    static {
        manager = new GameManager();
    }

    private ArrayList<Game<?, ?>> games;

    private GameManager() {
        games = new ArrayList<>();
    }

    /**
     * Games are automatically registered!
     * @param game
     *            the game
     */
    @Deprecated
    public void registerGame(Game<?, ?> game) {
        if (!games.contains(game)) games.add(game);
    }

    /**
     * @return returns all the registered games.
     */
    @Deprecated
    public ArrayList<Game<?, ?>> getGames() {
        return games;
    }

    /**
     * @return the gamemanager
     */
    public static GameManager getGameManager() {
        return manager;
    }

    /**
     * @param player
     *            the player
     * @return the GamePlayer
     */
    @Deprecated
    public GamePlayer<?> getGamePlayer(Player player) {
        GamePlayer<?> gP;
        for (Game<?, ?> game : games) {
            try {
                gP = game.getGamePlayer(player);
                if (gP != null) return gP;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * The heart of the GameFramework
     */
    @Deprecated
    protected void runTick() {
        for (Game<?, ?> game : games) {
            try {
                for (RunningGame<?, ?> rGame : game.getRunningGames()) {
                    rGame.tick();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This function is called to end all running games.
     * Could cause problems if the game plugin is unloaded before this one.
     */
    @Deprecated
    public void shutDown() {
        for (Game<?, ?> game : games) {
            try {
                for (RunningGame<?, ?> rGame : game.getRunningGames()) {
                    rGame.end();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
