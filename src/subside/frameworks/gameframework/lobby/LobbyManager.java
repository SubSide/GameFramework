package subside.frameworks.gameframework.lobby;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class LobbyManager implements Listener {
	static final ArrayList<LobbySign> lSigns = new ArrayList<LobbySign>();
	
	public static void registerSign(){
	}
	
	public static void onSignClick(Sign sign, Player player){
		for(LobbySign lS : lSigns){
			Location loc = sign.getLocation().subtract(lS.getLocation());
			if(lS.getLocation().getWorld() == sign.getLocation().getWorld() && loc.getBlockX() == 0 && loc.getBlockY() == 0 && loc.getBlockZ() == 0){
				lS.onClick(player);
				break;
			}
		}
	}
}
