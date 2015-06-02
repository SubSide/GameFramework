package subside.frameworks.gameframework.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

class AbstractEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}