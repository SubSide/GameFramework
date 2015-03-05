package subside.frameworks.gameframework;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GameFramework extends JavaPlugin {
	@Override
	public void onEnable(){
		Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), this);
		ConfigHandler.readConfig(this.getConfig());
		getCommand("gf").setExecutor(new CommandHandler());
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				GameManager.getGameManager().runTick();
			}
			
		}, 1, 1);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

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
			
		}, 20*60, 20*60);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onDisable(){
		GameManager.getGameManager().shutDown();
	}
}
