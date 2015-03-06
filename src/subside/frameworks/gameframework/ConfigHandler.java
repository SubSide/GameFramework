package subside.frameworks.gameframework;

import org.bukkit.configuration.file.FileConfiguration;

class ConfigHandler {
	public static boolean debug = false;
	public static String gChatPrefix = "!";
	public static int signUpdateSpeed = 20;
	public static String chatPrefix = "";
	
	public static void readConfig(FileConfiguration cfg){
		debug = cfg.getBoolean("debug");
		gChatPrefix = cfg.getString("global-chat-prefix");
		signUpdateSpeed = cfg.getInt("sign-update-speed");
		chatPrefix = cfg.getString("chat-prefix");
	}
}
