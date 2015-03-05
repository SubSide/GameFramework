package subside.frameworks.gameframework;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class CommandHandler implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		try {
			if(!Perms.Admin.has(sender) && !Perms.SocialSpy.has(sender)){
				throw new Exception("You don't have the permissions for this!");
			}
			
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("debug")) {
					if (Perms.Admin.has(sender)) {
						ConfigHandler.debug = !ConfigHandler.debug;
						Utils.sendMessage(sender, "Debugging has been turned " + (ConfigHandler.debug ? "on" : "off"));
					} else {
						throw new Exception("You don't have the permissions for this!");
					}
				} else if(args[0].equalsIgnoreCase("ss") || args[0].equalsIgnoreCase("socialspy")){
					if(Perms.SocialSpy.has(sender)){
						if(sender instanceof Player){
							Utils.setSocialSpy((Player)sender, !Utils.hasSocialSpy((Player)sender));
							Utils.sendMessage(sender, "Game-socialspy has been turned " + (Utils.hasSocialSpy((Player)sender) ? "on" : "off"));
						} else {
							throw new Exception("Can only be executed from in-game!");
						}
					} else {
						throw new Exception("You don't have the permissions for this!");
					}
				}
			} else {
				Utils.sendMessage(sender, "GameFrameworks commands:");
				Utils.sendMessage(sender, "/gf debug - toggles framework debugging");
				Utils.sendMessage(sender, "/gf socialspy - toggles socialspy for games with private game chat");
			}
		} catch (Exception e) {
			Utils.sendMessage(sender, e.getMessage());
		}

		return false;
	}
}
