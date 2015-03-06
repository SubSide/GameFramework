package subside.frameworks.gameframework;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import subside.frameworks.gameframework.events.GameEndEvent;
import subside.frameworks.gameframework.events.GameStartEvent;
import subside.frameworks.gameframework.events.PlayerJoinGameEvent;
import subside.frameworks.gameframework.exceptions.AlreadyIngameException;
import subside.frameworks.gameframework.exceptions.MaxPlayersReachedException;
import subside.frameworks.gameframework.lobby.LobbySign;

public abstract class RunningGame<T extends GamePlayer<?>, U extends Game<?, ?>> {
	private final ArrayList<T> players;
	private final ArrayList<T> spectators;
	private final Class<? extends GamePlayer<?>> c;
	private final U game;
	private boolean isRunning = false;
	private boolean teamChat = false;
	private LobbySign lobbySign = null;
	private int maxPlayers = -1;

	private TeamManager<T> tManager;

	public RunningGame(Class<T> c, U game) {
		players = new ArrayList<T>();
		spectators = new ArrayList<T>();
		tManager = new TeamManager<T>(this);
		this.game = game;
		this.c = c;
	}

	/**
	 * Get all the players in this game
	 */
	public final ArrayList<T> getAllPlayers() {
		ArrayList<T> ret = new ArrayList<T>();
		ret.addAll(players);
		ret.addAll(spectators);
		return ret;
	}

	/**
	 * Sets the max players that can join. Automatically denies if it is
	 * reached. -1 sets it to unlimited
	 */
	protected final void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	/**
	 * Move said player to spectators
	 */
	public final void moveToSpectator(T player) {
		players.remove(player);
		player.getPlayer().setGameMode(GameMode.SPECTATOR);
		if (!spectators.contains(player))
			spectators.add(player);
	}

	/**
	 * Move said player back to players
	 */
	public final void moveOutOfSpectator(T player) {
		spectators.remove(player);
		player.getPlayer().setGameMode(GameMode.SURVIVAL);
		if (!players.contains(player))
			players.add(player);
	}

	/**
	 * Get only the playing players in this game
	 */
	public final ArrayList<T> getPlayers() {
		return players;
	}

	/**
	 * Sets the team chat. This will make it so chat will only be shown to the
	 * same team
	 */
	protected final void setTeamChat(boolean bool) {
		teamChat = bool;
	}

	/**
	 * returns if team chat is active or not.
	 */
	protected final boolean hasTeamChat() {
		return teamChat;
	}

	/**
	 * Returns the team manager.
	 */
	protected final TeamManager<T> getTeamManager() {
		return tManager;
	}

	/**
	 * Get the main game class
	 */
	public final U getGame() {
		return game;
	}

	/**
	 * Is the game running?
	 */
	public final boolean isRunning() {
		return isRunning;
	}

	/**
	 * Is the game created by the lobby manager?
	 */
	public final boolean getIsLobbyCreated(){
		return lobbySign != null;
	}
	
	/**
	 * This function is called if the game is created by the lobby
	 */
	@Deprecated
	public final void lobbyCreated(LobbySign sign) {
		lobbySign = sign;
		onLobbyCreated(sign);
	}

	/**
	 * This should be called to start the game This function does nothing if the
	 * game is already running.
	 * 
	 * This function calls onStart()
	 * 
	 * Note: calling this event will send an GameStartEvent. So excessively
	 * toggling it on and off is not recommended.
	 */
	public final void start() {
		if (isRunning)
			return;
		isRunning = true;
		onStart();
		Bukkit.getServer().getPluginManager().callEvent(new GameStartEvent(this));
	}

	/**
	 * This should be called to end the game This function does nothing if the
	 * game is not running.
	 * 
	 * This function calls onEnd()
	 * 
	 * Note: calling this event will send an GameEndEvent. So excessively
	 * toggling it on and off is not recommended.
	 * 
	 * IMPORTANT: calling this function DOES NOT remove it! call game.remove()
	 * for that. If you don't do this this will cause serious issues!
	 */
	public final void end() {
		if (!isRunning)
			return;
		isRunning = false;
		onEnd();
		Bukkit.getServer().getPluginManager().callEvent(new GameEndEvent(this));
	}

	/**
	 * Tries to remove the game as gracefully as possible. if the game is still
	 * running it will execute an onEnd Then it will leave() all the players
	 * still in the game Then it will call the onRemove event which can be
	 * overwritten.
	 */
	@SuppressWarnings("deprecation")
	public final void remove() {
		end();
		for (GamePlayer<?> p : getAllPlayers()) {
			leave(p.getPlayer());
		}
		onRemove();
		getGame().removeGame(this);
	}

	/**
	 * This should be called to add players to the game
	 * 
	 * @throws AlreadyIngameException
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public final boolean join(Player player) throws AlreadyIngameException, MaxPlayersReachedException {
		if (GameManager.getGameManager().getGamePlayer(player) != null) {
			throw new AlreadyIngameException();
		}
		if (getAllPlayers().size() >= maxPlayers && maxPlayers != -1) {
			throw new MaxPlayersReachedException();
		}

		try {
			T gPlayer = (T) (c.getConstructor(Player.class, this.getClass()).newInstance(player, this));
			players.add(gPlayer);
			if (onPlayerJoin(gPlayer)) {
				showPlayers(player);
				Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinGameEvent(this, gPlayer));
				return true;
			} else {
				players.remove(gPlayer);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * This should be called to make the player leave the game
	 */
	public final void leave(Player player) {
		for (T pl : players) {
			if (pl.getPlayer().equals(player)) {
				T pl2 = pl;
				players.remove(pl2);
				onPlayerLeave(pl2);
				showPlayers(player);
				Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinGameEvent(this, pl2));
				break;
			}
		}
		for (T pl : spectators) {
			if (pl.getPlayer().equals(player)) {
				T pl2 = pl;
				spectators.remove(pl2);
				onPlayerLeave(pl2);
				showPlayers(player);
				Bukkit.getServer().getPluginManager().callEvent(new PlayerJoinGameEvent(this, pl2));
				break;
			}
		}
	}

	/**
	 * This is called between onJoin/onLeave and
	 * PlayerJoinGameEvent/PlayerLeaveGameEvent respectively It is used to
	 * update the hidePlayers feature
	 */
	@Deprecated
	protected final void showPlayers(Player player) {
		GamePlayer<?> pl = GameManager.getGameManager().getGamePlayer(player);

		if (this.getGame().getHideOtherPlayers()) {
			for (GamePlayer<?> pl2 : this.getAllPlayers()) {
				for (Player p : Bukkit.getOnlinePlayers())
					pl2.getPlayer().hidePlayer(p);
				for (GamePlayer<?> pl3 : this.getAllPlayers()) {
					pl2.getPlayer().showPlayer(pl3.getPlayer());
				}
			}
		}
		if (pl == null) {
			for (Player p : Bukkit.getOnlinePlayers())
				player.showPlayer(p);
		}
	}

	/**
	 * This is automaticly fired on every tick. Do not use as it can cause
	 * instability in the game!
	 */
	@Deprecated
	protected final void tick() {
		if (!isRunning())
			return;
		onTick();
		for (T pl : players) {
			pl.update();
		}
	}

	/**
	 * Only called when the game is created by the LobbyManager This should give
	 * better control on what kind of game should be started for example, this
	 * can be used to load specific maps and such (Also to make sure that a
	 * certain game is not loaded twice on the same map.)
	 */
	public void onLobbyCreated(LobbySign sign) {

	}

	/**
	 * Overwrite this to show your own sign I don't recommend changing much to it for consistency
	 * You can overwrite getSignInfo to show data on the 3th line, like map name, or game type
	 */
	public String[] getSignText(LobbySign sign) {
		return new String[] { ChatColor.DARK_GRAY + "[" + getGame().getName() + "]", ChatColor.DARK_AQUA + getSignInfo(sign), ChatColor.GRAY + "" + getAllPlayers().size() + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + maxPlayers, (getAllPlayers().size() < maxPlayers || maxPlayers == -1) ? (ChatColor.DARK_GREEN + "Click to join!") : (ChatColor.DARK_RED + "Full!") };
	}
	
	/**
	 * This is automatically placed on the 3rd line if getSignText is not overwritten.
	 */
	protected String getSignInfo(LobbySign sign){
		return " ";
	}

	/**
	 * @param player
	 *            Overwrite this as start event
	 */
	public void onStart() {

	}

	/**
	 * @param player
	 *            Overwrite this as end event
	 */
	public void onEnd() {

	}

	/**
	 * This function is called when the game will be removed. Players are
	 * automaticly removed BEFORE this function
	 */
	public void onRemove() {

	}

	/**
	 * This function will be called when every tick Should be overwritten
	 */
	public void onTick() {
	}

	/**
	 * @param player
	 *            This function will be called when a player joins the game
	 *            Should be overwritten should return wether or not the player
	 *            can actually join.
	 */
	public boolean onPlayerJoin(T player) {
		return true;
	}

	/**
	 * @param player
	 *            This function will be called when a player leaves the game.
	 *            This will also be called when the player quits the server
	 *            Should be overwritten
	 */
	public void onPlayerLeave(T player) {
	}

	/**
	 * Sends a global message to all the players in this game
	 */
	public final void broadcast(String bc) {
		for (GamePlayer<?> player : getAllPlayers()) {
			player.getPlayer().sendMessage(bc);
		}
	}

	/**
	 * Sends a global message to all the players in this game
	 */
	public final void broadcast(String[] bc) {
		for (GamePlayer<?> player : getAllPlayers()) {
			player.getPlayer().sendMessage(bc);
		}
	}

	/**
	 * Can be overwritten to have your own chat handling
	 * 
	 * privateChatroom needs to be enabled for this!
	 * 
	 * Note: Best to be left alone if teams are enabled And instead overwrite
	 * handleTeamChat
	 * 
	 * Make sure to also show the chat to players with chat bypass!
	 * (Perms.SocialSpy.has(Player))
	 */
	@SuppressWarnings("deprecation")
	public void handleGameChat(GamePlayer<?> player, String message) {
		if (hasTeamChat()) {
			handleTeamChat(player, tManager.getPlayersTeam(player), message);
		} else {
			String msg = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + getGame().getName() + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + player.getPlayer().getDisplayName() + ChatColor.GRAY + ": " + ChatColor.WHITE + message;
			for (GamePlayer<?> pl : getAllPlayers()) {
				if (Utils.hasSocialSpy(pl.getPlayer()))
					continue;

				pl.getPlayer().sendMessage(msg);
			}

			for (Player pl2 : Bukkit.getOnlinePlayers())
				if (Utils.hasSocialSpy(pl2))
					pl2.sendMessage(msg);
		}
	}

	/**
	 * Can be overwritten to have your own team chat handling
	 * 
	 * privateChatroom needs to be enabled for this!
	 * 
	 * team can be null if player is not in a team! Which will result in no
	 * message at all!
	 * 
	 * Make sure to also show the chat to players with chat bypass!
	 * (Perms.SocialSpy.has(Player))
	 */
	@SuppressWarnings("deprecation")
	public void handleTeamChat(GamePlayer<?> player, Team team, String message) {
		if (team == null)
			return;

		String msg = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + team.getName() + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + player.getPlayer().getDisplayName() + ChatColor.GRAY + ": " + message;
		for (GamePlayer<?> pl : team.getPlayers()) {
			if (Utils.hasSocialSpy(pl.getPlayer()))
				continue;

			pl.getPlayer().sendMessage(msg);
		}

		for (Player pl2 : Bukkit.getOnlinePlayers())
			if (Utils.hasSocialSpy(pl2))
				pl2.sendMessage(msg);
	}
}
