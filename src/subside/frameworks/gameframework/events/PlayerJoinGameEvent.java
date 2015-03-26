package subside.frameworks.gameframework.events;

import subside.frameworks.gameframework.framework.GamePlayer;
import subside.frameworks.gameframework.framework.RunningGame;

public class PlayerJoinGameEvent extends AbstractEvent  {
	private RunningGame<?,?> game;
	private GamePlayer<?> player;
	public PlayerJoinGameEvent(RunningGame<?,?> game, GamePlayer<?> player){
		super();
		this.game = game;
		this.player = player;
	}
	
	public RunningGame<?,?> getGame(){
		return game;
	}
	
	public GamePlayer<?> getPlayer(){
		return player;
	}
}
