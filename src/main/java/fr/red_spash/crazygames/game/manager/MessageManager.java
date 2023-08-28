package fr.red_spash.crazygames.game.manager;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.awt.*;

public class MessageManager {

    public static final String PREFIX = "§d§lCrazyGames";
    public static final String SEPARATOR = "§6§l>>>";

    public void sendEliminateMessage(String playerName){
        Bukkit.broadcastMessage("§d§l"+ MessageManager.PREFIX+" §6§l"+ MessageManager.SEPARATOR+" "+ChatColor.of(new Color(255,0,0))+"§l"+playerName+" §cvient d'être éliminé !");
    }

    public void sendVictoryMessage(String name) {
        Bukkit.broadcastMessage("§d§l"+ MessageManager.PREFIX+" §6"+ MessageManager.SEPARATOR+" "+ChatColor.of(new Color(0,255,0))+"§l"+name+" §avient de gagner la partie !");
    }
}
