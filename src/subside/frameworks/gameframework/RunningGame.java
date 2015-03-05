package subside.frameworks.gameframework;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import subside.frameworks.gameframework.events.GameEndEvent;
import subside.frameworks.gameframework.events.GameStartEvent;
import subside.frameworks.gameframework.events.PlayerJoinGameEvent;
import subside.frameworks.gameframework.exceptions.AlreadyIngameException;

public abstract class RunningGame <T extends GamePlayer<?>, U extends Game<?,?>> {
	private final ArrayList<T> players;
	private final Class<? extends GamePlayer<?>> c;
	private final U game;
	private boolean isRunning = false;
	private boolean teamChat = false;
	
	private TeamManager<T> tManager;
	
	public RunningGame(Class<T> c, U game){
		players = new ArrayList<T>();
		tManager = new TeamManager<T>(this);
		this.game = game;
		this.c = c;
	}


	/**
	 * Get all the players in this game
	 */
	public final ArrayList<T> getPlayers(){
		return players;
	}
	
	protected final void setTeamChat(boolean bool){
		teamChat = bool;
	}
	
	protected final boolean hasTeamChat(){
		return teamChat;
	}
	
	protected final TeamManager<T> getTeamManager(){
		return tManager;
	}
	

	/**
	 * Get the main game class
	 */
	public final U getGame(){
		return game;
	}


	/**
	 * Is the game running?
	 */
	public final boolean isRunning(){
		return isRunning;
	}

	/**
	 * This should be called to start the game
	 * This function does nothing if the game is already running.
	 * 
	 * This function calls onStart()
	 * 
	 * Note:
	 * calling this event will send an GameStartEvent.
	 * So excessively toggling it on and off is not recommended.
	 */
	public final void start(){
		if(isRunning) return;
		isRunning = true;
		onStart();
		Bukkit.getServer().getPluginManager().callEvent(new GameStartEvent(this));
	}

	/**
	 * This should be called to end the game
	 * This function does nothing if the game is not running.
	 * 
	 * This function calls onEnd()
	 * 
	 * Note:
	 * calling this event will send an GameEndEvent.
	 * So excessively toggling it on and off is not recommended.
	 * 
	 * IMPORTANT:
	 * calling this function DOES NOT remove it!
	 * call game.remove() for that.
	 * If you don't do this this will cause serious issues!
	 */
	public final void end(){
		if(!isRunning) return;
		isRunning = false;
		onEnd();
		Bukkit.getServer().getPluginManager().callEvent(new GameEndEvent(this));
	}
	
	/**
	 * Tries to remove the game as gracefully as possible.
	 * if the game is still running it will execute an onEnd
	 * Then it will call the onRemove event which can be overwritten.
	 */
	@SuppressWarnings("deprecation")
	public final void remove(){
		end();
		onRemove();
		getGame().removeGame(this);
	}
	
	/**
	 * This should be called to add players to the game
	 * @throws AlreadyIngameException 
	 */
	@SuppressWarnings("unchecked")
	public final void join(Player player) throws AlreadyIngameException {
		if(GameManager.getGameManager().getGamePlayer(player) != null){
			throw new AlreadyIngameException();
		}
		try {
			T gPlayer = (T)(c.getConstructor(Player.class, this.getClass()).newInstance(player, this));
			players.add(gPlayer);
			onPlayerJoin(gPlayer);
			showPlayers(player);
			Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinGameEvent(this, gPlayer));
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	
	/**
	 * This should be called to make the player leave the game
	 */
	public final void leave(Player player) {
		for(T pl : players){
			if(pl.getPlayer().equals(player)){
				T pl2 = pl;
				players.remove(pl2);
				onPlayerLeave(pl2);
				showPlayers(player);
				Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinGameEvent(this, pl2));
				break;
			}
		}
	}
	
	/**
	 * This is called between onJoin/onLeave and PlayerJoinGameEvent/PlayerLeaveGameEvent respectively
	 * It is used to update the hidePlayers feature
	 */
	@SuppressWarnings("deprecation")
	private final void showPlayers(Player player){
		GamePlayer<?> pl = GameManager.getGameManager().getGamePlayer(player);
		
		if(this.getGame().shouldHidePlayers()){
			for(GamePlayer<?> pl2 : this.getPlayers()){
				for(Player p : Bukkit.getOnlinePlayers()) pl2.getPlayer().hidePlayer(p);
				for(GamePlayer<?> pl3 : this.getPlayers()){
					pl2.getPlayer().showPlayer(pl3.getPlayer());
				}
			}
		}
		if(pl == null){
			for(Player p : Bukkit.getOnlinePlayers()) player.showPlayer(p);
		}
	}
	
	/**
	 * This is automaticly fired on every tick.
	 * Do not use as it can cause instability in the game!
	 */
	@Deprecated
	protected final void tick(){
		onTick();
		for(T pl : players){
			pl.update();
		}
	}
	

	/**
	 * @param player
	 * Overwrite this as start event
	 */
	public void onStart(){
		
	}

	/**
	 * @param player
	 * Overwrite this as end event
	 */
	public void onEnd(){
		
	}
	
	/**
	 * This function is called when the game will be removed.
	 */
	public void onRemove(){
		
	}
	
	/**
	 * This function will be called when every tick
	 * Should be overwritten
	 */
	public void onTick(){}
	
	/**
	 * @param player
	 * This function will be called when a player joins the game
	 * Should be overwritten
	 */
	public void onPlayerJoin(T player){ }

	
	/**
	 * @param player
	 * This function will be called when a player leaves the game.
	 * This will also be called when the player quits the server
	 * Should be overwritten
	 */
	public void onPlayerLeave(T player){ }

	
	/**
	 * Can be overwritten to have your own chat handling
	 * 
	 * privateChatroom needs to be enabled for this!
	 * 
	 * Note:
	 * Best to be left alone if teams are enabled
	 * And instead overwrite handleTeamChat
	 * 
	 * Make sure to also show the chat to players with chat bypass!
	 * (Perms.ChatBypass.has(Player))
	 */
	@SuppressWarnings("deprecation")
	public void handleGameChat(GamePlayer<?> player, String message){
		if(hasTeamChat()){
			handleTeamChat(player, tManager.getPlayersTeam(player), message);
		} else {
			String msg = ChatColor.DARK_GRAY+"["+ChatColor.GRAY+getGame().getName()+ChatColor.DARK_GRAY+"] "+ChatColor.BLUE+player.getPlayer().getDisplayName()+ChatColor.GRAY+": "+ChatColor.WHITE+message;
			for(GamePlayer<?> pl : getPlayers()){
				if(Perms.ChatBypass.has(pl.getPlayer()))
					continue;
				
				pl.getPlayer().sendMessage(msg);
			}
			
			for(Player pl2 : Bukkit.getOnlinePlayers()) pl2.sendMessage(msg);
		}
	}
	
	
	
	/**
	 * Can be overwritten to have your own team chat handling
	 * 
	 * privateChatroom needs to be enabled for this!
	 * 
	 * team can be null if player is not in a team!
	 * Which will result in no message at all!
	 */
	@SuppressWarnings("deprecation")
	public void handleTeamChat(GamePlayer<?> player, Team team, String message){
		if(team == null)
			return;
		
		String msg = ChatColor.DARK_GRAY+"["+ChatColor.GRAY+team.getName()+ChatColor.DARK_GRAY+"] "+ChatColor.BLUE+player.getPlayer().getDisplayName()+ChatColor.GRAY+": "+message;
		for(GamePlayer<?> pl : team.getPlayers()){
			if(Perms.ChatBypass.has(pl.getPlayer()))
				continue;
			
			pl.getPlayer().sendMessage(msg);
		}
		
		for(Player pl2 : Bukkit.getOnlinePlayers()) pl2.sendMessage(msg);
	}
}
