package subside.frameworks.gameframework.framework;

import java.util.ArrayList;

public class Team {
	protected final ArrayList<GamePlayer<?>> players;
	private final TeamManager<?> manager;
	private String name;
	
	public Team(TeamManager<?> manager, String name){
		players = new ArrayList<GamePlayer<?>>();
		this.manager = manager;
		this.name = name;
	}
	
	/**
	 * @return the name of the team
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * @return the TeamManager
	 */
	public TeamManager<?> getManager(){
		return manager;
	}
	
	/**
	 * @return all the players in this team
	 */
	public ArrayList<GamePlayer<?>> getPlayers(){
		return players;
	}
	
	/**
	 * @param player the game player
	 * @return true if the player is in this team
	 */
	public boolean isInTeam(GamePlayer<?> player){
		return players.contains(player);
	}

}
