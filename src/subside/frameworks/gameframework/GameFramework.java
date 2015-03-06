package subside.frameworks.gameframework;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import subside.frameworks.gameframework.lobby.LobbyManager;

public class GameFramework extends JavaPlugin {
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable(){
		Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), this);
		ConfigHandler.readConfig(this.getConfig());
		getCommand("gf").setExecutor(new CommandHandler());
		LobbyManager.loadSigns();
		
		schedule();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onDisable(){
		GameManager.getGameManager().shutDown();
		LobbyManager.saveSigns();
	}
	
	public void schedule(){
		// Game ticks
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				GameManager.getGameManager().runTick();
			}
			
		}, 1, 1);
		
		// Debugging
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if(!ConfigHandler.debug)
					return;
				
				int x = 0;
				for(Game<?,?> game : GameManager.getGameManager().getGames()){
					x += game.getRunningGames().size();
				}
				System.out.println("[GameFramework-Debug] Games running: "+x);
			}
			
		}, 20*1, 20*60);
		

		// Sign updating.
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				LobbyManager.update();
			}
			
		}, 20*1, ConfigHandler.signUpdateSpeed);
	}
}
