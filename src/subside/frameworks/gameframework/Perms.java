package subside.frameworks.gameframework;

import org.bukkit.entity.Player;

public enum Perms {
	ChatBypass("chatbypass"), CommandBypass("commandbypass");
	
	
	Perms(String perm){
		this.perm = "gameframework."+perm;
	}
	private String perm;
	
	public boolean has(Player player){
		return player.hasPermission(perm);
	}
	
	public String getPerm(){
		return perm;
	}
}
