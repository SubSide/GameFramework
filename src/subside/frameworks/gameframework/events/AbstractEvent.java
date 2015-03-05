package subside.frameworks.gameframework.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AbstractEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public AbstractEvent() {}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}