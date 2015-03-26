package subside.frameworks.gameframework.lobby;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import subside.frameworks.gameframework.GameFramework;
import subside.frameworks.gameframework.framework.Game;

public class LobbyManager implements Listener {
	static final ArrayList<LobbySign> lSigns = new ArrayList<LobbySign>();
	static final HashMap<String, Game<?,?>> signToGame = new HashMap<String, Game<?,?>>();

	public static void registerSign(String str, Game<?,?> game) {
		signToGame.put(str.toLowerCase(), game);
	}
	
	@Deprecated
	public static void cleanUpSigns(){
		signToGame.entrySet();
	}
	
	/**
	 * used to get the game class from the game name used for the sign
	 */
	@Deprecated
	public static Game<?,?> getGameFromSign(String str){
		return signToGame.get(str.toLowerCase());
	}

	/**
	 * Called on interaction with the sign
	 */
	@Deprecated
	public static boolean onSignClick(Sign sign, Player player) {
		LobbySign lS = getSignAt(sign.getLocation());
		if(lS != null){
			lS.onClick(player);
			return true;
		}
		return false;
	}
	
	@Deprecated
	protected static LobbySign getSignAt(Location location){
		for (LobbySign lS : lSigns) {
			Location loc = location.clone().subtract(lS.getLocation());
			if (lS.getLocation().getWorld() == location.getWorld() && loc.getBlockX() == 0 && loc.getBlockY() == 0 && loc.getBlockZ() == 0) {
				return lS;
			}
		}
		return null;
	}
	
	/**
	 * Called every X ticks
	 * Configurable in config
	 */
	@Deprecated
	public static void update(){
		for(LobbySign sign : lSigns){
			sign.onSignUpdate();
		}
	}
	
	/**
	 * Add a sign to the list
	 */
	public static boolean addSign(Location loc, String gameName, String identifier){
		if(getSignAt(loc) == null){
			lSigns.add(new LobbySign(loc, gameName, identifier));
			return true;
		}
		return false;
	}

	/**
	 * Called on onEnable
	 */
	@Deprecated
	public static void loadSigns() {
		File folder = GameFramework.getPlugin(GameFramework.class).getDataFolder();
		File file = new File(folder, "signs.yml");
		if (!file.exists()) {
			try {
				folder.mkdirs();
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileConfiguration cfg2 = YamlConfiguration.loadConfiguration(file);
		
		ConfigurationSection cfg = cfg2.getConfigurationSection("signs");
		if(cfg != null){
			for(String str : cfg.getKeys(false)){
				try {
					addSign(new Location(Bukkit.getWorld(cfg.getString(str+".location.world")), cfg.getInt(str+".location.x"), cfg.getInt(str+".location.y"), cfg.getInt(str+".location.z")), cfg.getString(str+".gamename"), cfg.getString(str+".identifier"));
				} catch(Exception e){
					System.out.println("Error while reading sign: "+str);
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Called on onDisable
	 */
	@Deprecated
	public static void saveSigns(){
		try {
			File file = new File(GameFramework.getPlugin(GameFramework.class).getDataFolder(), "signs.yml");
			
			if (!file.exists()) {
				file.createNewFile();
			}
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			
			cfg.set("signs", null);
			int x = 0;
			for(LobbySign lS : lSigns){
				String prfx = "signs.sign"+x+++".";
				cfg.set(prfx+"location.world", lS.getLocation().getWorld().getName());
				cfg.set(prfx+"location.x", lS.getLocation().getBlockX());
				cfg.set(prfx+"location.y", lS.getLocation().getBlockY());
				cfg.set(prfx+"location.z", lS.getLocation().getBlockZ());
				cfg.set(prfx+"gamename",  lS.getGameName());
				cfg.set(prfx+"identifier", lS.getIdentifier());
			}
				
			cfg.save(file);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public static boolean removeSign(Sign sign) {
		LobbySign lS = getSignAt(sign.getLocation());
		if(lS != null){
			lS.remove();
			lSigns.remove(lS);
			return true;
		}
		return false;
	}
	
	public static void cleanup(){
		for(LobbySign lS : lSigns){
			try {
				if(lS.getLocation().getBlock().getState() instanceof Sign){
					continue;
				}
			} catch(Exception e){}
			lS.remove();
			lSigns.remove(lS);
		}
	}
}
