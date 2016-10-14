package net.pl3x.bukkit.mobdrops.command;

import net.pl3x.bukkit.mobdrops.Logger;
import net.pl3x.bukkit.mobdrops.MobDrops;
import net.pl3x.bukkit.mobdrops.configuration.Config;
import net.pl3x.bukkit.mobdrops.configuration.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;

public class CmdMobDrops implements TabExecutor {
    private MobDrops plugin;

    public CmdMobDrops(MobDrops plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "reload".startsWith(args[0].toLowerCase())) {
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

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            Logger.debug("Reloading config...");
            Config.reload();

            Logger.debug("Reloading language file...");
            Lang.reload();

            Lang.send(sender, Lang.RELOAD
                    .replace("{plugin}", plugin.getName())
                    .replace("{version}", plugin.getDescription().getVersion()));
            return true;
        }

        Lang.send(sender, Lang.VERSION
                .replace("{version}", plugin.getDescription().getVersion())
                .replace("{plugin}", plugin.getName()));
        return true;
    }
}
