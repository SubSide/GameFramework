package subside.frameworks.gameframework;

import org.bukkit.entity.Player;

public abstract class GamePlayer<T extends RunningGame<?,?>> {
	private final Player player;
	private final T game;
	
	public GamePlayer(Player player, T game){
		this.player = player;
		this.game = game;
	}

	/**
	 * Gets the actual Player
	 */
	public final Player getPlayer(){
		return player;
	}

	/**
	 * return the running game
	 */
	public final T getGame(){
		return game;
	}

	/**
	 * This function is called every tick
	 */
	public abstract void update();
}
