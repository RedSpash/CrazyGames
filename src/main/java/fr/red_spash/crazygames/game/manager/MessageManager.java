package fr.red_spash.crazygames.game.manager;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;

public class MessageManager {

    private final GameManager gameManager;
    public static final String PREFIX = "§d§lCrazy§2§lGames";
    public static final String SEPARATOR = "§6§l>>>";

    public MessageManager(GameManager gameManager){
        this.gameManager = gameManager;
    }

    public void broadcastEliminateMessage(String playerName){
        Bukkit.broadcastMessage(PREFIX+" "+ SEPARATOR+" "+ChatColor.of(Color.RED)+"§l"+playerName+" §cvient d'être éliminé !");
    }

    public void broadcastVictoryMessage(String name) {
        Bukkit.broadcastMessage(PREFIX+" "+ SEPARATOR+" "+ChatColor.of(Color.GREEN)+"§l"+name+" §avient de gagner la partie !");
    }

    public void broadcastQualificationMessage(String name, int top) {
        Bukkit.broadcastMessage(PREFIX+" "+SEPARATOR+" "+ChatColor.of(Color.GREEN)+"§l"+name+"§a vient de se qualifier ! §e§lTOP "+top);
    }

    public void sendQualificationTitle(Player p){
        p.sendTitle(ChatColor.of(Color.GREEN)+"§lQUALIFIÉ !","§aVous êtes qualifié pour la prochaine épreuve!",0,20*3,20);
    }

    public void broadcastLifeLost(String name, int life) {
        Bukkit.broadcastMessage(PREFIX+" "+SEPARATOR+" "+ChatColor.of(new Color(255, 84, 0))+"§l"+name+ChatColor.of(new Color(255, 118, 0))+" vient de perdre une vie ! Il est désormais à "+life+" vie(s).");
    }

    public void sendQualifiedWithLifeLost(Player p) {
        PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());
        p.sendTitle("§a§lVous êtes qualifié !","§cIl vous reste §c§l"+playerData.getVisualLife()+" vie(s)§c !",0,20*3,20);
    }

    public void sendEliminationTitle(Player p) {
        p.sendTitle(ChatColor.of(Color.RED)+"§lÉLIMINÉ !","§cVous avez perdu !",0,20*3,20);
    }
}
