package subside.frameworks.gameframework;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import subside.frameworks.gameframework.framework.Game;
import subside.frameworks.gameframework.lobby.LobbyManager;

public class GameFramework extends JavaPlugin {
    private static String VERSION;

    @Override
    public void onEnable() {
        VERSION = this.getDescription().getVersion();
        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), this);
        new ConfigHandler(this.getConfig());
        getCommand("gf").setExecutor(new CommandHandler());
        new LobbyManager();

        schedule();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onDisable() {
        GameManager.getGameManager().shutDown();
        LobbyManager.getManager().saveSigns();
    }

    public void schedule() {
        // Game ticks
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                GameManager.getGameManager().runTick();
            }

        }, 1, 1);

        // Debugging
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                if (!ConfigHandler.getConfig().shouldDebug()) return;

                int x = 0;
                for (Game<?, ?> game : GameManager.getGameManager().getGames()) {
                    x += game.getRunningGames().size();
                }
                System.out.println("[GameFramework-Debug] Games running: " + x);
            }

        }, 20 * 1, 20 * 60);

        // Sign updating.
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                LobbyManager.getManager().update();
            }

        }, 20 * 1, ConfigHandler.getConfig().getSignUpdateSpeed());
    }

    public static String getVersion() {
        return VERSION;
    }
}
