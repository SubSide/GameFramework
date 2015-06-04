package subside.frameworks.gameframework.events;

import subside.frameworks.gameframework.framework.GamePlayer;
import subside.frameworks.gameframework.framework.RunningGame;

public class PlayerLeaveGameEvent extends AbstractEvent {
    private RunningGame<?, ?> game;
    private GamePlayer<?> player;

    public PlayerLeaveGameEvent(RunningGame<?, ?> game, GamePlayer<?> player) {
        super();
        this.game = game;
        this.player = player;
    }

    public RunningGame<?, ?> getGame() {
        return game;
    }

    public GamePlayer<?> getPlayer() {
        return player;
    }
}
