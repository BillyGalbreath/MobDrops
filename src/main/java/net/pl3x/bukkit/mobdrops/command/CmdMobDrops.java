package net.pl3x.bukkit.mobdrops.command;

import net.pl3x.bukkit.mobdrops.MobDrops;
import net.pl3x.bukkit.mobdrops.configuration.Config;
import net.pl3x.bukkit.mobdrops.configuration.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;

public class CmdMobDrops implements TabExecutor {
    private final MobDrops plugin;

    public CmdMobDrops(MobDrops plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "reload".startsWith(args[0].toLowerCase()) && sender.hasPermission("command.mobdrops")) {
            return Collections.singletonList("reload");
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("command.mobdrops")) {
            Lang.send(sender, Lang.COMMAND_NO_PERMISSION);
            return true;
        }

        String response = "&d" + plugin.getName() + " v" + plugin.getDescription().getVersion();

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            Config.reload(plugin);
            Lang.reload(plugin);

            response += " reloaded";
        }

        Lang.send(sender, response);
        return true;
    }
}
