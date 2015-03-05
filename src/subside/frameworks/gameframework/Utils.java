package subside.frameworks.gameframework;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

class Utils {
	protected static boolean hasSocialSpy(Player player){
		if(Perms.SocialSpy.has(player)){
			if(player.hasMetadata("GF_SocialSpy")){
				List<MetadataValue> list = player.getMetadata("GF_SocialSpy");
				return list.get(0).asBoolean();
			}
			return true;
		}
		return false;
	}
	
	protected static void setSocialSpy(Player player, boolean bool){
		if(Perms.SocialSpy.has(player)){
			player.setMetadata("GF_SocialSpy", new FixedMetadataValue(GameFramework.getPlugin(GameFramework.class), bool));
		}
	}
	
	protected static void sendMessage(CommandSender player, String msg){
		player.sendMessage(ChatColor.DARK_GRAY+"[GameFramework] "+ChatColor.DARK_AQUA+msg);
	}
}
