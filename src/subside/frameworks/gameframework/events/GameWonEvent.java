package subside.frameworks.gameframework.events;

import subside.frameworks.gameframework.framework.GamePlayer;
import subside.frameworks.gameframework.framework.RunningGame;

public class GameWonEvent extends AbstractEvent {
    private RunningGame<?, ?> game;
    private GamePlayer<?>[] players;

    public GameWonEvent(RunningGame<?, ?> game, GamePlayer<?>[] players) {
        super();
        this.game = game;
        this.players = players;
    }

    public RunningGame<?, ?> getGame() {
        return game;
    }

    public GamePlayer<?>[] getWinners() {
        return players;
    }
}
