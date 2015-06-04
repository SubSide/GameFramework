package subside.frameworks.gameframework;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class Utils {
    public static boolean hasSocialSpy(Player player) {
        if (Perms.SOCIALSPY.has(player)) {
            if (player.hasMetadata("GF_SocialSpy")) {
                List<MetadataValue> list = player.getMetadata("GF_SocialSpy");
                return list.get(0).asBoolean();
            }
            return true;
        }
        return false;
    }

    protected static String getFromArray(String[] args, int from) {
        String build = "";
        for (int x = from; x < args.length; x++) {
            build += args[x] + " ";
        }
        return build.trim();
    }

    public static void setSocialSpy(Player player, boolean bool) {
        if (Perms.SOCIALSPY.has(player)) {
            player.setMetadata("GF_SocialSpy", new FixedMetadataValue(GameFramework.getPlugin(GameFramework.class), bool));
        }
    }

    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ConfigHandler.getConfig().getChatPrefix()) + msg);
    }

    public static void sendCMessage(CommandSender player, String msg) {
        player.sendMessage(ChatColor.DARK_GRAY + "[GameFramework] " + ChatColor.DARK_AQUA + msg);
    }

    public static void sendCMessage(CommandSender player, String msg, boolean bool) {
        player.sendMessage(bool ? (ChatColor.DARK_GRAY + "[GameFramework] ") : "" + ChatColor.DARK_AQUA + msg);
    }
}
