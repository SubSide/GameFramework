package subside.frameworks.gameframework;

import org.bukkit.configuration.file.FileConfiguration;

class ConfigHandler {
	public static boolean debug = false;
	public static String gChatPrefix = "!";
	public static void readConfig(FileConfiguration cfg){
		debug = cfg.getBoolean("debug");
		gChatPrefix = cfg.getString("global-chat-prefix");
	}
}
