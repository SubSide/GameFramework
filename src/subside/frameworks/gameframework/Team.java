package subside.frameworks.gameframework;

import java.util.ArrayList;

class Team {
	protected final ArrayList<GamePlayer<?>> players;
	private final TeamManager<?> manager;
	private String name;
	
	public Team(TeamManager<?> manager, String name){
		players = new ArrayList<GamePlayer<?>>();
		this.manager = manager;
		this.name = name;
	}
	
	/**
	 * Returns the name of the team
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Returns the TeamManager
	 */
	public TeamManager<?> getManager(){
		return manager;
	}
	
	/**
	 * Returns all the players in this team
	 */
	public ArrayList<GamePlayer<?>> getPlayers(){
		return players;
	}
	
	/**
	 * Returns true if the player is in this team
	 */
	public boolean isInTeam(GamePlayer<?> player){
		return players.contains(player);
	}

}
