package subside.frameworks.gameframework;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import subside.frameworks.gameframework.lobby.LobbyManager;

class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        try {
            if (!Perms.ADMIN.has(sender) && !Perms.SOCIALSPY.has(sender)) {
                info(sender);
                return false;
            }

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("debug")) {
                    debug(sender, args);
                } else if (args[0].equalsIgnoreCase("ss") || args[0].equalsIgnoreCase("socialspy")) {
                    socialSpy(sender, args);
                } else if (args[0].equalsIgnoreCase("sign")) {
                    sign(sender, args);
                } else if (args[0].equalsIgnoreCase("info")) {
                    info(sender);
                } else {
                    help(sender, args);
                }
            } else {
                help(sender, args);
            }
        }
        catch (Exception e) {
            Utils.sendCMessage(sender, e.getMessage());
        }

        return false;
    }

    public void debug(CommandSender sender, String[] args) throws Exception {
        if (Perms.ADMIN.has(sender)) {
            ConfigHandler.getConfig().toggleDebug();
            Utils.sendCMessage(sender, "Debugging has been turned " + (ConfigHandler.getConfig().shouldDebug() ? "on" : "off"));
        } else {
            throw new Exception("You don't have the permissions for this!");
        }
    }

    public void socialSpy(CommandSender sender, String[] args) throws Exception {
        if (Perms.SOCIALSPY.has(sender)) {
            if (sender instanceof Player) {
                Utils.setSocialSpy((Player) sender, !Utils.hasSocialSpy((Player) sender));
                Utils.sendCMessage(sender, "Game-socialspy has been turned " + (Utils.hasSocialSpy((Player) sender) ? "on" : "off"));
            } else {
                throw new Exception("Can only be executed from in-game!");
            }
        } else {
            throw new Exception("You don't have the permissions for this!");
        }
    }

    public void help(CommandSender sender, String[] args) {
        Utils.sendCMessage(sender, "");
        if (Perms.ADMIN.has(sender)) Utils.sendCMessage(sender, "/gf debug - toggles framework debugging.", false);
        if (Perms.SOCIALSPY.has(sender)) Utils.sendCMessage(sender, "/gf socialspy - toggles game socialspy", false);
        if (Perms.SIGN.has(sender)) {
            Utils.sendCMessage(sender, "/gf sign - creates a lobby sign.", false);
            Utils.sendCMessage(sender, "/gf cleanup - removes all signs that don't exist.", false);
        }
        Utils.sendCMessage(sender, "/gf info", false);
    }

    public void info(CommandSender sender) {
        Utils.sendCMessage(sender, "");
        Utils.sendCMessage(sender, ChatColor.DARK_AQUA + "Version: " + ChatColor.GRAY + GameFramework.getVersion(), false);
        Utils.sendCMessage(sender, ChatColor.DARK_AQUA + "Author: " + ChatColor.GRAY + "SubSide", false);
        Utils.sendCMessage(sender, ChatColor.GRAY + "https://github.com/SubSide/GameFramework", false);
    }

    @SuppressWarnings("deprecation")
    public void sign(CommandSender sender, String[] args) throws Exception {
        if (Perms.SIGN.has(sender)) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length > 2) {
                    Block b = player.getTargetBlock(null, 5);
                    if (b != null) {
                        if (b.getState() instanceof Sign) {
                            if (LobbyManager.getManager().addSign(b.getLocation(), args[1], Utils.getFromArray(args, 2))) {
                                Utils.sendCMessage(sender, "Sign created!");
                            } else {
                                Utils.sendCMessage(sender, "This sign is already registered!");
                            }
                            return;
                        }
                    }
                    Utils.sendCMessage(sender, "You must be looking at a sign!");
                } else if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("remove")) {
                        Block b = player.getTargetBlock(null, 5);
                        if (b != null) {
                            if (b.getState() instanceof Sign) {
                                if (LobbyManager.getManager().removeSign((Sign) b.getState())) {
                                    Utils.sendCMessage(sender, "Sign removed!");
                                } else {
                                    Utils.sendCMessage(sender, "This is not a valid Lobby Sign!");
                                }
                                return;
                            }
                        }
                        Utils.sendCMessage(sender, "You must be looking at a sign!");
                    } else if (args[1].equalsIgnoreCase("cleanup")) {
                        LobbyManager.getManager().cleanup();
                        Utils.sendCMessage(sender, "All invalid signs have been removed!");
                        return;
                    }
                }
                Utils.sendCMessage(sender, "");
                Utils.sendCMessage(sender, "usage: /gf sign [game] [data]", false);
                Utils.sendCMessage(sender, "[game] is the name that the Game has registered for signs", false);
                Utils.sendCMessage(sender, "[data] is what the game uses to save extra info", false);
                Utils.sendCMessage(sender, "", false);
                Utils.sendCMessage(sender, "/gf sign remove - to remove sign", false);
                Utils.sendCMessage(sender, "Removing a sign will end the game!", false);
            } else {
                throw new Exception("This can only be ran from ingame!");
            }
        }
    }
}
