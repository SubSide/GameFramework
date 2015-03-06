package subside.frameworks.gameframework.lobby;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import subside.frameworks.gameframework.Game;
import subside.frameworks.gameframework.RunningGame;
import subside.frameworks.gameframework.exceptions.AlreadyIngameException;
import subside.frameworks.gameframework.exceptions.MaxPlayersReachedException;

public class LobbySign {
	private final Location loc;
	private String info;
	private final String identifier;
	private final Game<?,?> game;
	private RunningGame<?,?> rGame;
	
	public LobbySign(Location loc, Game<?,?> game, String identifier){
		this.loc = loc;
		this.identifier = identifier;
		info = "";
		this.game = game;
	}
	
	public String getIdentifier(){
		return identifier;
	}
	
	public Location getLocation(){
		return loc;
	}
	
	protected final void onUpdate(){
		if(loc.getBlock().getState() instanceof Sign){
			String[] str = rGame.getSignText(this);
			for(int x = 0; x < str.length; x++) ((Sign)loc.getBlock().getState()).setLine(x, str[0]);
		}
	}

	public void isRemoved(){
		rGame = game.createGame();
	}
	
	public void onClick(Player player){
		try {
			rGame.join(player);
		} catch (AlreadyIngameException | MaxPlayersReachedException e) {
			player.sendMessage(e.getMessage());
		}
	}
	
	public void setSignInfo(String info){
		this.info = info;
	}
	
	public String getSignInfo(){
		return info;
	}
}
