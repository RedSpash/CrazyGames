package fr.red_spash.crazygames.game.games.pickablock;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.games.hotblock.HotBlock;
import fr.red_spash.crazygames.game.manager.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.w3c.dom.Text;

import java.util.*;

public class PickABlockTask implements Runnable {

    private static final int MAX_TIME = 20;
    private double timer;
    private final PickABlock pickABlock;

    public PickABlockTask(PickABlock pickABlock) {
        this.pickABlock = pickABlock;
        this.resetTimer();
    }

    @Override
    public void run() {
        if(this.timer <= 0){
            this.pickABlock.chooseAnotherBlock();
            this.resetTimer();
        }else{
            if((this.timer % 5 == 0 || this.timer <= 5) && this.timer % 1 == 0){
                Bukkit.broadcastMessage("§cChangement de block dans "+this.timer+" secondes !");
                for(Player p : Bukkit.getOnlinePlayers()){
                    p.playSound(p.getLocation().add(0,2,0), Sound.UI_BUTTON_CLICK,1,1);
                }
            }
        }

        for(PlayerData playerData : this.pickABlock.getGameManager().getPlayerManager().getAlivePlayerData()){
            Player p = Bukkit.getPlayer(playerData.getUuid());
            if(p != null && p.isOnline()){
                int point = this.pickABlock.getPoints().getOrDefault(p.getUniqueId(),0);
                ArrayList<Integer> points = new ArrayList<>(this.pickABlock.getPoints().values());
                points.sort(Collections.reverseOrder());
                String top = "?";
                for(int i = 0; i < points.size(); i++){
                    if(points.get(i) == point){
                        top = (i+1)+"";
                    }
                }
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent("§aVous avez §l"+point+" §7§l| §e§lTOP "+top+" "));
            }
        }


        this.timer = this.timer - 0.5;
    }

    public void resetTimer() {
        this.timer = MAX_TIME;
    }
}
