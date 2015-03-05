package subside.frameworks.gameframework;

import java.util.ArrayList;

public abstract class Game <T extends RunningGame<?,?>, U extends GamePlayer<?>> {
	private final ArrayList<T> runningGames;
	private final Class<T> rGameClass;
	private final Class<U> gPlayerClass;
	private final Class<?> gameClass;
	
	private boolean commandsDisabled = false;
	private final ArrayList<String> whitelistedCommands;
	private boolean privateChat = false;
	private boolean hideOtherPlayers = false;
	
	private String gameName;
	
	@SuppressWarnings("deprecation")
	public Game(Class<T> runningGameClass, Class<U> gamePlayerClass, Class<?> gameClass){
		runningGames = new ArrayList<T>();
		whitelistedCommands = new ArrayList<String>();
		this.rGameClass = runningGameClass;
		this.gPlayerClass = gamePlayerClass;
		this.gameClass = gameClass;
		this.gameName = this.getClass().getSimpleName();
		
		GameManager.getGameManager().registerGame(this);
	}


	/**
	 * returns the name of the game.
	 * @return
	 * the game name
	 */
	public final String getName(){
		return gameName;
	}
	
	/**
	 * Sets the name of the game
	 * @param name
	 * the name of the game
	 */
	protected final void setName(String name){
		this.gameName = name;
	}
	
	/**
	 * Used to deny command usage while in game.
	 * This function can be overwritten to have an advanced
	 * control over the commands.
	 * 
	 * disableCommands() has to be called for this function is used
	 * 
	 * KEEP IN MIND THAT THIS IS AN UNPROCESSED, RAW COMMAND
	 * So unessesary spaces and such annoyances can occur.
	 * 
	 * @param player
	 * The gameplayer
	 * @param cmd
	 * The raw command given by PlayerCommandPreprocessEvent
	 */
	@Deprecated
	public boolean isCommandAllowed(GamePlayer<?> player, String cmd){
		String cmd2 = cmd.trim().split(" ")[0];
		if(commandsDisabled){
			if(whitelistedCommands.contains(cmd2.toLowerCase())){
				return true;
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Sets it to show or hide other players on the server (that are not in the game)
	 */
	@SuppressWarnings("deprecation")
	public final void setHideOtherPlayers(boolean bool){
		hideOtherPlayers = bool;
		for(RunningGame<?,?> game : this.getRunningGames()){
			for(GamePlayer<?> player : game.getAllPlayers()){
				game.showPlayers(player.getPlayer());
			}
		}
	}
	
	/**
	 * returns if players should be hidden.
	 */
	public final boolean getHideOtherPlayers(){
		return hideOtherPlayers;
	}
	
	/**
	 * Add a whitelisted command.
	 * This only accepts the command, no arguments
	 * 
	 * disableCommands() has to be called before this takes effect
	 * 
	 * For more control over commands you should overwrite the isCommandAllowed function.
	 * Keep in mind that that isCommandAllowed is using a raw command.
	 * Read more on that in the function itself.
	 */
	protected final void addWhitelistedCommand(String cmd){
		whitelistedCommands.add(cmd.toLowerCase());
	}
	
	/**
	 * This disables command usage while ingame.
	 * Keep in mind that NO commands are allowed once this is turned on.
	 * 
	 * This NEEDS to be turned on for addWhitelistedcommands to work.
	 */
	protected final void setDisableCommands(boolean bool){
		commandsDisabled = bool;
	}

	
	/**
	 * This will block all chats going in and out except to the players in that game.
	 */
	protected final void setPrivateChat(boolean bool){
		privateChat = bool;
	}
	
	/**
	 * Returns if the game haws its own private chatroom
	 */
	public final boolean getHasPrivateChat(){
		return privateChat;
	}


	/**
	 * Use this to create a game.
	 * game.start() needs to be run to actually make it run.
	 */
	public final T createGame(){
		try {
			T rGame = (T)rGameClass.getConstructor(gPlayerClass.getClass(), gameClass).newInstance(gPlayerClass, this);
			runningGames.add(rGame);
			return rGame;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This function should not be used, use game.end() instead.
	 */
	@Deprecated
	public final void removeGame(RunningGame<?, ?> game){
		runningGames.remove(game);
	}
	
	/**
	 * Returns all the current games.
	 * 
	 * IMPORTANT:
	 * This also returns the games that are NOT running.
	 * Make sure that if the game is completely done, to run the remove() function in the game.
	 */
	public final ArrayList<T> getRunningGames(){
		return runningGames;
	}
	
}
