package subside.frameworks.gameframework.lobby;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import subside.frameworks.gameframework.Utils;
import subside.frameworks.gameframework.exceptions.AlreadyIngameException;
import subside.frameworks.gameframework.exceptions.MaxPlayersReachedException;
import subside.frameworks.gameframework.framework.Game;
import subside.frameworks.gameframework.framework.RunningGame;

public class LobbySign {
	private final Location loc;
	private final String identifier;
	private final String gameName;
	private Game<?,?> game = null;
	private RunningGame<?,?> rGame = null;
	
	public LobbySign(Location loc, String gameName, String identifier){
		this.loc = loc;
		this.identifier = identifier;
		this.gameName = gameName;
	}
	
	/**
	 * Returns a string bound to the sign which games can use to load specific data
	 * like a map, or game mode etc
	 */
	public String getIdentifier(){
		return identifier;
	}
	
	/**
	 * The location of the sign
	 */
	public Location getLocation(){
		return loc;
	}
	
	/**
	 * the name of the game (used to grab the Game class)
	 */
	public String getGameName(){
		return gameName;
	}
	
	/**
	 * Called every X ticks
	 * Configurable in the config
	 */
	@SuppressWarnings("deprecation")
	protected final void onSignUpdate(){
		if(game == null){
			game = LobbyManager.getGameFromSign(gameName);
			if(game == null){
				return;
			}
		}
		
		if(rGame != null){
			if(!rGame.getGame().getRunningGames().contains(rGame)){
				this.rGame = game.createGame();
			}
		} else {
			this.rGame = game.createGame();
		}

		if(loc.getBlock().getState() instanceof Sign){
			Sign sign = (Sign)loc.getBlock().getState();
			String[] str = rGame.getSignText(this);
			String[] signText = sign.getLines();
			boolean changed = false;
			try {

				for(int x = 0; x < 4; x++){
					if(!signText[x].equalsIgnoreCase(str[x])){
						changed = true;
						break;
					}
				}
			} catch(Exception e){
				e.printStackTrace();
			}

			if(changed){
				for(int x = 0; x < str.length; x++) sign.setLine(x, str[x]);
				
				sign.update();
			}
		}
	}

	/**
	 * Is called on interaction.
	 * Makes the player join if not in any other game.
	 * Makes the player leave if already in this specific game.
	 */
	public void onClick(Player player){
		if(game == null || rGame == null){
			Utils.sendMessage(player, "Something went wrong, please contact an Administrator!");
			return;
		}
		try {
			if(rGame.getAllPlayers().contains(rGame.getGame().getGamePlayer(player))){
				rGame.leave(player);
			} else {
				rGame.join(player);
			}
		} catch (AlreadyIngameException | MaxPlayersReachedException e) {
			Utils.sendMessage(player, e.getMessage());
		}
	}

	protected void remove() {
		rGame.remove();
		if(loc.getBlock().getState() instanceof Sign){
			Sign sign = (Sign)loc.getBlock().getState();
			for(int x = 0; x < 4; x++) sign.setLine(x, "");
			sign.update();
		}
		
	}

}
