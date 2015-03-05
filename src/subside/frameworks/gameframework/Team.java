package subside.frameworks.gameframework;

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
	
	public String getName(){
		return name;
	}
	
	public TeamManager<?> getGame(){
		return manager;
	}
	
	public ArrayList<GamePlayer<?>> getPlayers(){
		return players;
	}
	
	public boolean isInTeam(GamePlayer<?> player){
		return players.contains(player);
	}

}
