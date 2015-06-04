package subside.frameworks.gameframework.framework;

import java.util.ArrayList;

public class TeamManager <U extends GamePlayer<?>> {
    private boolean friendlyFire = true;
    private ArrayList<Team> teams;
    private RunningGame<?, ?> game;

    public TeamManager(RunningGame<?, ?> game) {
        this.teams = new ArrayList<>();
        this.game = game;
    }

    /**
     * @return the game this teammanager is for
     */
    public RunningGame<?, ?> getGame() {
        return game;
    }

    /**
     * @return all the teams registered
     */
    public ArrayList<Team> getTeams() {
        return teams;
    }

    /**
     * @param player
     *            the GamePlayer
     * @return the team the player is in
     *         null if in no team.
     */
    public Team getPlayersTeam(GamePlayer<?> player) {
        for (Team team : teams) {
            if (team.isInTeam(player)) {
                return team;
            }
        }
        return null;
    }

    /**
     * If set to false, will automatically block all damage between teammates.
     * @param bool
     *            boolean
     */
    public void setFriendlyFire(boolean bool) {
        friendlyFire = bool;
    }

    /**
     * @return true if friendly fire is on.
     */
    public boolean hasFriendlyFire() {
        return friendlyFire;
    }
}
