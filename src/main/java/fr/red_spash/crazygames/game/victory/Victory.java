package fr.red_spash.crazygames.game.victory;

import fr.red_spash.crazygames.game.models.Game;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.UUID;

public class Victory extends Game {
    private final UUID winner;
    private Location winnerLocation;
    private Location looserLocation;

    public Victory(UUID uuid) {
        super(null);
        this.winner = uuid;
    }

    @Override
    public void startGame() {
        super.gameManager.getGameInteractions().setMoveItemInventory(true).setShootProjectile(true).setExplosion(true);
        this.winnerLocation = super.getGameMap().getOtherLocations().get(0);
        this.looserLocation = super.getGameMap().getSpawnLocation();
        this.looserLocation.getWorld().setTime(0L);
        super.registerListener(new VictoryListener(this));
        super.registerTask(new VictoryTask(this, this.gameManager.getWorldManager()),2,2);

        String title = ChatColor.of(Color.ORANGE)+ "§lÉGALITÉ !";
        String subTitle = "§cPersonne ne gagne.";
        if(this.winner != null){
            Player p = Bukkit.getPlayer(this.winner);
            if(p != null){
                title = ChatColor.of(Color.GREEN)+"§lVictoire de "+p.getName()+" !";
                subTitle = "§a"+p.getName()+" remporte la partie !";
            }
        }

        for(Player p : Bukkit.getOnlinePlayers()){
            p.sendTitle(title,subTitle,0,20*10,20*3);
            p.setGameMode(GameMode.SURVIVAL);
            if(p.getUniqueId().equals(this.winner)){
                p.teleport(this.winnerLocation);
                p.setGlowing(true);
            }else{
                p.teleport(this.looserLocation);
            }
            for(Player pl : Bukkit.getOnlinePlayers()){
                if(!p.canSee(pl)){
                    p.showPlayer(super.gameManager.getMain(),pl);
                }
            }
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH,0.5F,1);
        }
    }

    public Location getLooserLocation() {
        return looserLocation.clone();
    }

    public Location getWinnerLocation() {
        return winnerLocation.clone();
    }
}
