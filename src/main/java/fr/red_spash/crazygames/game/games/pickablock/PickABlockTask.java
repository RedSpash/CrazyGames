package fr.red_spash.crazygames.game.games.pickablock;

import fr.red_spash.crazygames.game.manager.MessageManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.manager.PointManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.awt.*;

public class PickABlockTask implements Runnable {

    private static final int MAX_TIME = 20;
    private final PointManager pointManager;
    private double timer;
    private final PickABlock pickABlock;

    public PickABlockTask(PickABlock pickABlock) {
        this.pickABlock = pickABlock;
        this.pointManager = this.pickABlock.getGameManager().getPointManager();
        this.resetTimer();
    }

    @Override
    public void run() {
        if(this.timer <= 0){
            this.pickABlock.replaceBlocks();
            this.pickABlock.chooseAnotherBlock();
            this.resetTimer();
        }else{
            if(((this.timer % 5 == 0 && this.timer <= 10) || this.timer <= 5) && this.timer % 1 == 0 ){
                Bukkit.broadcastMessage(MessageManager.PREFIX+" "+MessageManager.SEPARATOR+ChatColor.of(new Color(255, 85, 0))+" Changement de block dans "+((int) this.timer)+" secondes !");
                for(Player p : Bukkit.getOnlinePlayers()){
                    p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK,0.5f,2);
                }
            }
        }

        for(PlayerData playerData : this.pickABlock.getGameManager().getPlayerManager().getAlivePlayerData()){
            Player p = Bukkit.getPlayer(playerData.getUuid());
            if(p != null && p.isOnline()){
                int point = this.pointManager.getPoint(p.getUniqueId());
                String top = this.pickABlock.getTop(p);
                String qualification = this.pickABlock.isQualified(p);

                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        new TextComponent("§aVous avez §l"+point+" point(s) §7§l| §e§lTOP "+top+" §7§l| "+qualification));
            }
        }
        this.timer = this.timer - 0.5;
    }

    public void resetTimer() {
        this.timer = MAX_TIME;
    }
}
