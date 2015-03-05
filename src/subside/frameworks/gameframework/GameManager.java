package subside.frameworks.gameframework;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class GameManager {
	private static GameManager manager;

	private ArrayList<Game<?, ?>> games;
	
	private GameManager(){
		games = new ArrayList<Game<?, ?>>();
	}
	
	/**
	 * Don't forget to register the game!
	 */
	public void registerGame(Game<?, ?> game){
		games.add(game);
	}

	/**
	 * Gets all the games registered
	 * (This are not running games)
	 */
	protected ArrayList<Game<?, ?>> getGames(){
		return games;
	}
	
	static {
		manager = new GameManager();
	}

	/**
	 * Use this function to get the game manager.
	 */
	public static GameManager getGameManager(){
		return manager;
	}
	

	/**
	 * Get the GamePlayer from a player
	 * returns null if not in a game
	 */
	public GamePlayer<?> getGamePlayer(Player player){
		for(Game<?, ?> game : games){
			for(RunningGame<?,?> rGame : game.getRunningGames()){
				for(GamePlayer<?> pl : rGame.getPlayers()){
					if(pl.getPlayer().equals(player)){
						return pl;
					}
				}
			}
		}
		return null;
	}

	/**
	 * This function will send a tick to all the running games.
	 * This is done automaticly. please leave alone as it can cause instabillity in games.
	 */
	@Deprecated
	protected void runTick(){
		for(Game<?, ?> game : games){
			for(RunningGame<?,?> rGame : game.getRunningGames()){
				rGame.tick();
			}
		}
	}

	/**
	 * This function is used to end all the running games.
	 * Could cause problems if the game plugin is unloaded before this one.
	 */
	@Deprecated
	public void shutDown(){
		for(Game<?, ?> game : games){
			for(RunningGame<?,?> rGame : game.getRunningGames()){
				rGame.end();
			}
		}
	}
}
