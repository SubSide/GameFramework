package subside.frameworks.gameframework.framework;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import subside.frameworks.gameframework.GameManager;
import subside.frameworks.gameframework.Utils;
import subside.frameworks.gameframework.events.GameEndEvent;
import subside.frameworks.gameframework.events.GameStartEvent;
import subside.frameworks.gameframework.events.PlayerJoinGameEvent;
import subside.frameworks.gameframework.exceptions.AlreadyIngameException;
import subside.frameworks.gameframework.exceptions.MaxPlayersReachedException;
import subside.frameworks.gameframework.lobby.LobbySign;

public abstract class RunningGame <T extends GamePlayer<?>, U extends Game<?, ?>> {
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
        players = new ArrayList<>();
        spectators = new ArrayList<>();
        tManager = new TeamManager<>(this);
        this.game = game;
        this.c = c;
    }

    /**
     * Get all players in this game
     * This includes players in spectator.
     * @return all the players in this game
     */
    public final ArrayList<T> getAllPlayers() {
        ArrayList<T> ret = new ArrayList<>();
        ret.addAll(players);
        ret.addAll(spectators);
        return ret;
    }

    /**
     * Sets the max players that can join. Automatically denies if it is
     * reached. -1 sets it to unlimited
     * @param maxPlayers
     *            max amount of players that can join
     */
    protected final void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Move said player to spectators
     * @param player
     *            the player
     */
    public final void moveToSpectator(T player) {
        players.remove(player);
        player.getPlayer().setGameMode(GameMode.SPECTATOR);
        if (!spectators.contains(player)) spectators.add(player);
    }

    /**
     * Move said player back to players
     * @param player
     *            the player
     */
    public final void moveOutOfSpectator(T player) {
        spectators.remove(player);
        player.getPlayer().setGameMode(GameMode.SURVIVAL);
        if (!players.contains(player)) players.add(player);
    }

    /**
     * Get only the playing players in this game
     * Does not include players in spectator.
     * @return List of playing players
     */
    public final ArrayList<T> getPlayers() {
        return players;
    }

    /**
     * This makes the player only able to chat to the team he is in.
     * @param bool
     *            true if teamcheat shoul dbe enabled
     */
    protected final void setTeamChat(boolean bool) {
        teamChat = bool;
    }

    /**
     * @return true if private team chat is active or not.
     */
    protected final boolean hasTeamChat() {
        return teamChat;
    }

    /**
     * @return the team manager.
     */
    public final TeamManager<T> getTeamManager() {
        return tManager;
    }

    /**
     * @return the main game class
     */
    public final U getGame() {
        return game;
    }

    /**
     * @return true if the game is running
     */
    public final boolean isRunning() {
        return isRunning;
    }

    /**
     * @return if the game is created by the lobby manager
     */
    public final boolean getIsLobbyCreated() {
        return lobbySign != null;
    }

    /**
     * This function is called if the game is created by the lobby
     * @param sign
     *            the lobby sign
     */
    @Deprecated
    public final void lobbyCreated(LobbySign sign) {
        lobbySign = sign;
        onLobbyCreated(sign);
    }

    /**
     * This should be called to start the game This function does nothing if the
     * game is already running.
     * This function calls onStart()
     * Note: calling this event will send an GameStartEvent. So excessively
     * toggling it on and off is not recommended.
     */
    public final void start() {
        if (isRunning) return;
        isRunning = true;
        onStart();
        Bukkit.getServer().getPluginManager().callEvent(new GameStartEvent(this));
    }

    /**
     * This should be called to end the game This function does nothing if the
     * game is not running.
     * This function calls onEnd()
     * Note: calling this event will send an GameEndEvent. So excessively
     * toggling it on and off is not recommended.
     * IMPORTANT: calling this function DOES NOT remove it! call game.remove()
     * for that. If you don't do this this will cause serious issues such as
     * memory leaks!
     */
    public final void end() {
        if (!isRunning) return;
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
     * @param player
     *            the player
     * @return true if player was able to join.
     * @throws AlreadyIngameException
     * @throws MaxPlayersReachedException
     */
    @SuppressWarnings({
            "unchecked", "deprecation"
    })
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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * This should be called to make the player leave the game
     * @param player
     *            the player
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
     * @param player
     *            the player
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
     * This is automaticly fired on every tick. Do not use this as it can cause
     * instability in the game!
     */
    @Deprecated
    public final void tick() {
        if (!isRunning()) return;
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
     * @param sign
     *            the LobbySign which created the game
     */
    public void onLobbyCreated(LobbySign sign) {

    }

    /**
     * Overwrite this to show your own sign I don't recommend changing much to
     * it for consistency
     * You can overwrite getSignInfo to show data on the 3th line, like map
     * name, or game type
     * @param sign
     *            the LobbySign
     * @return The text shown on the LobbySign
     */
    public String[] getSignText(LobbySign sign) {
        return new String[] {
                ChatColor.DARK_GRAY + "[" + getGame().getName() + "]",
                ChatColor.DARK_AQUA + getSignInfo(sign),
                ChatColor.GRAY + "" + getAllPlayers().size() + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + maxPlayers,
                (getAllPlayers().size() < maxPlayers || maxPlayers == -1) ? (ChatColor.DARK_GREEN + "Click to join!") : (ChatColor.DARK_RED + "Full!")
        };
    }

    /**
     * This is automatically placed on the 3rd line if getSignText is not
     * overwritten.
     * @param sign
     *            the LobbySign
     * @return the info to show
     */
    protected String getSignInfo(LobbySign sign) {
        return " ";
    }

    /**
     * Overwrite this as start event
     */
    public void onStart() {

    }

    /**
     * Overwrite this as end event
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
    public void onTick() {}

    /**
     * This function will be called when a player joins the game
     * Can be overwritten, should return wether or not the player
     * can actually join.
     * @param player
     *            The GamePlayer
     */
    public boolean onPlayerJoin(T player) {
        return true;
    }

    /**
     * This function will be called when a player leaves the game.
     * This will also be called when the player quits the server
     * Can be overwritten
     * @param player
     *            The GamePlayer
     */
    public void onPlayerLeave(T player) {}

    /**
     * Sends a global message to all the players in this game
     * @param bc
     *            the broadcast message
     */
    public void broadcast(String bc) {
        for (GamePlayer<?> player : getAllPlayers()) {
            player.getPlayer().sendMessage(bc);
        }
    }

    /**
     * Sends a global message to all the players in this game
     * @param bc
     *            the broadcast message
     */
    public void broadcast(String[] bc) {
        for (GamePlayer<?> player : getAllPlayers()) {
            player.getPlayer().sendMessage(bc);
        }
    }

    /**
     * Can be overwritten to have your own chat handling
     * privateChatroom needs to be enabled for this!
     * Note: Best to be left alone if teams are enabled And instead overwrite
     * handleTeamChat
     * Make sure to also show the chat to players with chat bypass!
     * (Utils.hasSocialSpy(Player))
     * @param player
     *            the Player who sent the message
     * @param message
     *            the actual message
     */
    @SuppressWarnings("deprecation")
    public void handleGameChat(GamePlayer<?> player, String message) {
        if (hasTeamChat()) {
            handleTeamChat(player, tManager.getPlayersTeam(player), message);
        } else {
            String msg = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + getGame().getName() + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + player.getPlayer().getDisplayName() + ChatColor.GRAY + ": " + ChatColor.WHITE + message;
            for (GamePlayer<?> pl : getAllPlayers()) {
                if (Utils.hasSocialSpy(pl.getPlayer())) continue;

                pl.getPlayer().sendMessage(msg);
            }

            for (Player pl2 : Bukkit.getOnlinePlayers())
                if (Utils.hasSocialSpy(pl2)) pl2.sendMessage(msg);
        }
    }

    /**
     * Can be overwritten to have your own team chat handling
     * privateChatroom needs to be enabled for this!
     * team can be null if player is not in a team! Which will result in no
     * message at all!
     * Make sure to also show the chat to players with chat bypass!
     * (Utils.hasSocialSpy(Player))
     * @param player
     *            The player sending the message
     * @param team
     *            The team the player is in
     * @param message
     *            The actual message
     */
    @SuppressWarnings("deprecation")
    public void handleTeamChat(GamePlayer<?> player, Team team, String message) {
        if (team == null) return;

        String msg = ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + team.getName() + ChatColor.DARK_GRAY + "] " + ChatColor.BLUE + player.getPlayer().getDisplayName() + ChatColor.GRAY + ": " + message;
        for (GamePlayer<?> pl : team.getPlayers()) {
            if (Utils.hasSocialSpy(pl.getPlayer())) continue;

            pl.getPlayer().sendMessage(msg);
        }

        for (Player pl2 : Bukkit.getOnlinePlayers())
            if (Utils.hasSocialSpy(pl2)) pl2.sendMessage(msg);
    }
}
