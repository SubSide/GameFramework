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
	
	public RunningGame<?,?> getGame(){
		return game;
	}
	
	public ArrayList<Team> getTeams(){
		return teams;
	}
	
	public Team getPlayersTeam(GamePlayer<?> player){
		for(Team team : teams){
			if(team.isInTeam(player)){
				return team;
			}
		}
		return null;
	}
	
	public void setFriendlyFire(boolean bool){
		friendlyFire = bool;
	}
	
	public boolean hasFriendlyFire(){
		return friendlyFire;
	}
}
