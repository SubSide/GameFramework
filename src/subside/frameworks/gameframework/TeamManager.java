package subside.frameworks.gameframework;

import java.util.ArrayList;

public class TeamManager <U extends GamePlayer<?>> {
	private boolean friendlyFire = true;
	private ArrayList<Team> teams;
	private RunningGame<?,?> game;
	
	public TeamManager(RunningGame<?,?> game){
		this.teams = new ArrayList<Team>();
		this.game = game;
	}
	
	/**
	 * Gets the game this teammanager is for
	 */
	public RunningGame<?,?> getGame(){
		return game;
	}
	
	/**
	 * Get all the teams registered
	 */
	public ArrayList<Team> getTeams(){
		return teams;
	}
	
	/**
	 * Gets the team the player is on
	 * returns null if not on team
	 */
	public Team getPlayersTeam(GamePlayer<?> player){
		for(Team team : teams){
			if(team.isInTeam(player)){
				return team;
			}
		}
		return null;
	}
	
	/**
	 * If set to true, it will automaticly disable damage to each other.
	 */
	public void setFriendlyFire(boolean bool){
		friendlyFire = bool;
	}
	
	/**
	 * Returns if friendly fire is on.
	 */
	public boolean hasFriendlyFire(){
		return friendlyFire;
	}
}
