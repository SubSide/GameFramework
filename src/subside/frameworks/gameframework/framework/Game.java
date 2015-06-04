package subside.frameworks.gameframework.framework;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import subside.frameworks.gameframework.GameManager;

public abstract class Game <T extends RunningGame<?, ?>, U extends GamePlayer<?>> {
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
    public Game(Class<T> runningGameClass, Class<U> gamePlayerClass, Class<?> gameClass) {
        runningGames = new ArrayList<>();
        whitelistedCommands = new ArrayList<>();
        this.rGameClass = runningGameClass;
        this.gPlayerClass = gamePlayerClass;
        this.gameClass = gameClass;
        this.gameName = this.getClass().getSimpleName();

        GameManager.getGameManager().registerGame(this);
    }

    /**
     * @return the game name
     */
    public final String getName() {
        return gameName;
    }

    /**
     * @param name
     *            the game name
     */
    protected final void setName(String name) {
        this.gameName = name;
    }

    /**
     * Used to deny command usage while in game.
     * This function can be overwritten to have an advanced
     * control over the commands.
     * disableCommands() has to be called for this function is used
     * KEEP IN MIND THAT THIS IS AN UNPROCESSED, RAW COMMAND
     * So unessesary spaces and such annoyances can occur.
     * @param player
     *            the player that executed the command
     * @param cmd
     *            the raw command given by PlayerCommandPreprocessEvent
     */
    @Deprecated
    public boolean isCommandAllowed(GamePlayer<?> player, String cmd) {
        String cmd2 = cmd.trim().split(" ")[0];
        if (commandsDisabled) {
            if (whitelistedCommands.contains(cmd2.toLowerCase())) {
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * show or hide other players on the server (that are not in the game)
     * @param bool
     *            true if you want to hide other players
     */
    @SuppressWarnings("deprecation")
    public final void setHideOtherPlayers(boolean bool) {
        hideOtherPlayers = bool;
        for (RunningGame<?, ?> game : this.getRunningGames()) {
            for (GamePlayer<?> player : game.getAllPlayers()) {
                game.showPlayers(player.getPlayer());
            }
        }
    }

    /**
     * @return true if players not in the game should be hidden.
     */
    public final boolean getHideOtherPlayers() {
        return hideOtherPlayers;
    }

    /**
     * Add a whitelisted command.
     * This only accepts the command, no arguments
     * disableCommands() has to be called before this takes effect
     * For more control over commands you should overwrite the isCommandAllowed
     * function.
     * Keep in mind that that isCommandAllowed is using a raw command.
     * Read more on that in the function itself.
     * @param command
     *            the command to whitelist
     */
    protected final void addWhitelistedCommand(String cmd) {
        whitelistedCommands.add(cmd.toLowerCase());
    }

    /**
     * This disables command usage while ingame.
     * Keep in mind that NO commands are allowed once this is turned on.
     * This NEEDS to be turned on for addWhitelistedcommands to work.
     * @param bool
     *            true if you want to disable commands
     */
    protected final void setDisableCommands(boolean bool) {
        commandsDisabled = bool;
    }

    /**
     * This will block all chats going in and out except to the players in that
     * game.
     * @param bool
     *            true if you want to use private chat
     */
    protected final void setPrivateChat(boolean bool) {
        privateChat = bool;
    }

    /**
     * @return true if the game has its own private chatroom
     */
    public final boolean getHasPrivateChat() {
        return privateChat;
    }

    /**
     * Use this to create a game.
     * game.start() needs to be run to actually make it run.
     * @return a RunningGame object
     */
    public final T createGame() {
        try {
            T rGame = (T) rGameClass.getConstructor(gPlayerClass.getClass(), gameClass).newInstance(gPlayerClass, this);
            runningGames.add(rGame);
            return rGame;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This function should not be used, use game.end() instead.
     * @param game
     *            the RunningGame object
     */
    @Deprecated
    public final void removeGame(RunningGame<?, ?> game) {
        runningGames.remove(game);
    }

    /**
     * Returns all the current games.
     * IMPORTANT:
     * This also returns the games that are NOT running (yet). (Check
     * isRunning())
     * Make sure that if the game is completely done, to run the remove()
     * function in the game.
     * @return a list of running games.
     */
    public final ArrayList<T> getRunningGames() {
        return runningGames;
    }

    /**
     * @param player
     *            the player
     * @return the GamePlayer object
     */
    @SuppressWarnings("unchecked")
    public final U getGamePlayer(Player player) {
        for (T rGame : getRunningGames()) {
            for (GamePlayer<?> pl : rGame.getAllPlayers()) {
                if (pl.getPlayer().equals(player)) {
                    return (U) pl;
                }
            }
        }
        return null;
    }

}
