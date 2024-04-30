package fr.red_spash.crazygames.game.games.pickablock;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.GameStatus;
import fr.red_spash.crazygames.game.manager.PlayerData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

public class PickABlockListener implements Listener {

    private static final long COOLDOWN_MILLISECONDS = 1000 * 5L;
    private final HashMap<UUID, Long> cooldown = new HashMap<>();
    private final PickABlock pickABlock;
    private final GameManager gameManager;

    public PickABlockListener(PickABlock pickABlock) {
        this.pickABlock = pickABlock;
        this.gameManager = pickABlock.getGameManager();
    }

    @EventHandler
    public void blockFadeEvent(BlockFadeEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void blockPhysicsEvent(BlockPhysicsEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void blockFromToEvent(BlockFromToEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void blockGrowEvent(BlockGrowEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(!this.gameManager.isInWorld(p.getWorld()))return;
        PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());
        if(playerData.isDead() || playerData.isEliminated() || playerData.isQualified())return;
        if(e.getClickedBlock() == null)return;
        if(this.pickABlock.getGameStatus() != GameStatus.PLAYING)return;
        if(this.pickABlock.getEndedPlayers().contains(p.getUniqueId()))return;
        if(e.getAction() != Action.LEFT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if(this.cooldown.containsKey(p.getUniqueId())){
            if(System.currentTimeMillis() - this.cooldown.get(p.getUniqueId()) < 100){
                return;
            }else if(this.cooldown.get(p.getUniqueId()) + COOLDOWN_MILLISECONDS > System.currentTimeMillis()){
                p.sendMessage("§cVous devez attendre "+((this.cooldown.get(p.getUniqueId()) + COOLDOWN_MILLISECONDS) - System.currentTimeMillis())/1000+" secondes avant de choisir un autre block !");
                e.setCancelled(true);
                return;
            }
        }

        this.cooldown.put(p.getUniqueId(),System.currentTimeMillis());

        Block block = e.getClickedBlock();
        Material material = block.getType();

        e.setCancelled(true);
        if(material == this.pickABlock.getChoosedMaterial()){
            this.pickABlock.chosenRightBlock(p);
        }else{
            this.pickABlock.wrongBlock(p);
        }
    }

    public void resetCooldowns() {
        this.cooldown.replaceAll((u, v) -> System.currentTimeMillis() - COOLDOWN_MILLISECONDS + 100);
    }
}
