package fr.red_spash.crazygames.game.manager;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.awt.*;

public class MessageManager {

    public static final String PREFIX = "§d§lCrazy§9§lGames";
    public static final String SEPARATOR = "§6§l>>>";

    public void sendEliminateMessage(String playerName){
        Bukkit.broadcastMessage(PREFIX+" "+ SEPARATOR+" "+ChatColor.of(new Color(255,0,0))+"§l"+playerName+" §cvient d'être éliminé !");
    }

    public void sendVictoryMessage(String name) {
        Bukkit.broadcastMessage(PREFIX+" "+ SEPARATOR+" "+ChatColor.of(new Color(0,255,0))+"§l"+name+" §avient de gagner la partie !");
    }

    public void sendQualificationMessage(String name, int top) {
        Bukkit.broadcastMessage(PREFIX+" "+SEPARATOR+" "+ChatColor.of(new Color(0,255,0))+"§l"+name+"§a vient de se qualifier ! §e§lTOP "+top);
    }
}
