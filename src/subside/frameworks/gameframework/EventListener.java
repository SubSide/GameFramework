package subside.frameworks.gameframework;

import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import subside.frameworks.gameframework.framework.Game;
import subside.frameworks.gameframework.framework.GamePlayer;
import subside.frameworks.gameframework.framework.RunningGame;
import subside.frameworks.gameframework.framework.Team;
import subside.frameworks.gameframework.lobby.LobbyManager;

class EventListener implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        for (Game<?, ?> game : GameManager.getGameManager().getGames()) {
            for (RunningGame<?, ?> rGame : game.getRunningGames()) {
                try {
                    rGame.leave(e.getPlayer());
                }
                catch (Exception f) {
                    f.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if (Perms.COMMANDBYPASS.has(e.getPlayer())) return;

        GamePlayer<?> player = GameManager.getGameManager().getGamePlayer(e.getPlayer());
        if (player != null) {
            if (!player.getGame().getGame().isCommandAllowed(player, e.getMessage())) {
                e.setCancelled(true);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent e) {
        if (Perms.CHATBYPASS.has(e.getPlayer())) {
            if (e.getMessage().startsWith(ConfigHandler.getConfig().getGlobalChatPrefix())) {
                e.setMessage(e.getMessage().substring(ConfigHandler.getConfig().getGlobalChatPrefix().length()));
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

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        for (Game<?, ?> game : GameManager.getGameManager().getGames()) {
            try {
                if (game.getHideOtherPlayers()) {
                    for (RunningGame<?, ?> rGame : game.getRunningGames()) {
                        for (GamePlayer<?> player : rGame.getAllPlayers()) {
                            player.getPlayer().hidePlayer(e.getPlayer());
                        }
                    }
                }
            }
            catch (Exception f) {
                f.printStackTrace();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getState() instanceof Sign) {
                    if (LobbyManager.getManager().onSignClick((Sign) e.getClickedBlock().getState(), e.getPlayer())) e.setCancelled(true);
                }
            }
        }
    }
}
