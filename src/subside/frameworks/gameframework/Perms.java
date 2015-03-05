package subside.frameworks.gameframework;

import org.bukkit.command.CommandSender;

public enum Perms {
	ChatBypass("chatbypass"), CommandBypass("commandbypass"), SocialSpy("socialspy"), Admin("admin");
	
	
	Perms(String perm){
		this.perm = "gameframework."+perm;
	}
	private String perm;
	
	public boolean has(CommandSender player){
		return player.hasPermission(perm);
	}
	
	public String getPerm(){
		return perm;
	}
}
