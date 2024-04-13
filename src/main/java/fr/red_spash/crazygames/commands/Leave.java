package fr.red_spash.crazygames.commands;

import fr.red_spash.crazygames.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Leave implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player p){
            Utils.sendPlayerToSurvieServer(p);
        }
        return false;
    }
}
