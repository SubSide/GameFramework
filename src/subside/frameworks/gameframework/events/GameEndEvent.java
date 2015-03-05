package subside.frameworks.gameframework.events;

import subside.frameworks.gameframework.RunningGame;

public class GameEndEvent extends AbstractEvent {
	private RunningGame<?,?> game;
	public GameEndEvent(RunningGame<?,?> game){
		super();
		this.game = game;
	}
	
	public RunningGame<?,?> getGame(){
		return game;
	}
}
