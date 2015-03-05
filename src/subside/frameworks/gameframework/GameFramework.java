package subside.frameworks.gameframework;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class GameFramework extends JavaPlugin {
	@Override
	public void onEnable(){
		Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), this);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				GameManager.getGameManager().runTick();
			}
			
		}, 1, 1);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onDisable(){
		GameManager.getGameManager().shutDown();
	}
}
