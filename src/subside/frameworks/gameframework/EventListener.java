package subside.frameworks.gameframework;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

class EventListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		for (Game<?, ?> game : GameManager.getGameManager().getGames()) {
			for (RunningGame<?, ?> rGame : game.getRunningGames()) {
				try {
					rGame.leave(e.getPlayer());
				} catch(Exception f){
					f.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		if (Perms.CommandBypass.has(e.getPlayer()))
			return;

		GamePlayer<?> player = GameManager.getGameManager().getGamePlayer(e.getPlayer());
		if(player != null){
			if (!player.getGame().getGame().isCommandAllowed(player, e.getMessage())) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent e) {
		if (Perms.ChatBypass.has(e.getPlayer())) {
			if (e.getMessage().startsWith(ConfigHandler.gChatPrefix)) {
				e.setMessage(e.getMessage().substring(ConfigHandler.gChatPrefix.length()));
				return;
			}
		}

		GamePlayer<?> player = GameManager.getGameManager().getGamePlayer(e.getPlayer());
		if (player != null) {
			if (player.getGame().getGame().getHasPrivateChat()) {
				player.getGame().handleGameChat(player, e.getMessage());
				e.setCancelled(true);
				return;
			}
		}
		for (Game<?, ?> game : GameManager.getGameManager().getGames()) {
			if (game.getHasPrivateChat()) {
				for (RunningGame<?, ?> rGame : game.getRunningGames()) {
					for (GamePlayer<?> pl : rGame.getAllPlayers()) {
						e.getRecipients().remove(pl.getPlayer());
					}
				}
			}
		}

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		Entity src = e.getDamager();
		if (src instanceof Projectile) {
			src = ((Projectile) src).getShooter();
		}

		if (e.getEntity() instanceof Player && src instanceof Player) {
			GamePlayer<?> source = GameManager.getGameManager().getGamePlayer((Player) src);
			GamePlayer<?> target = GameManager.getGameManager().getGamePlayer((Player) e.getEntity());
			if (target != null && source != null) {
				if (source.getGame().equals(target.getGame()) && !source.getGame().getTeamManager().hasFriendlyFire()) {
					Team sT = source.getGame().getTeamManager().getPlayersTeam(source);
					if (sT != null) {
						if (sT.isInTeam(target)) {
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		for (Game<?, ?> game : GameManager.getGameManager().getGames()) {
			try {
				if (game.getHideOtherPlayers()) {
					for (RunningGame<?, ?> rGame : game.getRunningGames()) {
						for(GamePlayer<?> player : rGame.getAllPlayers()){
							player.getPlayer().hidePlayer(e.getPlayer());
						}
					}
				}
			} catch(Exception f){
				f.printStackTrace();
			}
		}
	}
}
