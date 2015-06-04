package subside.frameworks.gameframework.events;

import subside.frameworks.gameframework.framework.RunningGame;

public class GameStartEvent extends AbstractEvent {
    private RunningGame<?, ?> game;

    public GameStartEvent(RunningGame<?, ?> game) {
        super();
        this.game = game;
    }

    public RunningGame<?, ?> getGame() {
        return game;
    }
}
