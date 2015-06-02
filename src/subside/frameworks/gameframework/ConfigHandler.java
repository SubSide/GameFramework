package subside.frameworks.gameframework;

import org.bukkit.configuration.file.FileConfiguration;

class ConfigHandler {
	private boolean debug;
	private String globalChatPrefix;
	private int signUpdateSpeed;
	private String chatPrefix;
	private static ConfigHandler cfg;
	
	protected ConfigHandler(FileConfiguration cfg){
		debug = cfg.getBoolean("debug");
		globalChatPrefix = cfg.getString("global-chat-prefix");
		signUpdateSpeed = cfg.getInt("sign-update-speed");
		chatPrefix = cfg.getString("chat-prefix");
		ConfigHandler.cfg = this;
	}
	
	public static ConfigHandler getConfig(){
		return cfg;
	}
	
	public boolean shouldDebug(){
		return debug;
	}
	
	public void toggleDebug(){
		debug = !debug;
	}
	
	public String getGlobalChatPrefix(){
		return globalChatPrefix;
	}
	
	public int getSignUpdateSpeed(){
		return signUpdateSpeed;
	}
	
	public String getChatPrefix(){
		return chatPrefix;
	}
}
