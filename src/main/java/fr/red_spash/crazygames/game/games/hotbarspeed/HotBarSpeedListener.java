package fr.red_spash.crazygames.game.games.hotbarspeed;

import fr.red_spash.crazygames.game.manager.GameStatus;
import fr.red_spash.crazygames.game.manager.PlayerData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class HotBarSpeedListener implements Listener {
    private final HashMap<UUID, Integer> mistakes;
    private final ArrayList<HotBarPreset> hotBarPresets;
    private final HashMap<UUID,Long> cooldown;
    private final HotBarSpeed hotBarSpeed;

    public HotBarSpeedListener(HotBarSpeed hotBarSpeed, List<HotBarPreset> hotBarPresets) {
        this.hotBarPresets = new ArrayList<>(hotBarPresets);
        this.hotBarSpeed = hotBarSpeed;
        this.cooldown = new HashMap<>();
        this.mistakes = new HashMap<>();
    }

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e){
        if(!e.getFrom().getWorld().getName().equals(this.hotBarSpeed.getGameMap().getWorld().getName()))return;
        Player p = e.getPlayer();
        PlayerData playerData = this.hotBarSpeed.getGameManager().getPlayerData(p.getUniqueId());
        if(!playerData.isDead() && !playerData.isEliminated() && !playerData.isQualified()){
            if(e.getFrom().getBlock() != e.getTo().getBlock()){
                Location to = e.getFrom();
                to.setPitch(e.getTo().getPitch());
                to.setYaw(e.getTo().getYaw());
                e.setTo(to);
            }
        }
    }

    @EventHandler
    public void liquidFlow(BlockFromToEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e){
        Player p = e.getPlayer();
        PlayerData playerData = this.hotBarSpeed.getGameManager().getPlayerData(p.getUniqueId());
        if(playerData.isDead() || playerData.isEliminated()) return;
        if(this.hotBarSpeed.getGameManager().getActualGameStatus() != GameStatus.PLAYING)return;

        Integer reachedPreset = this.hotBarSpeed.getPresetReached(p.getUniqueId());
        if(reachedPreset == null)return;
        e.setCancelled(true);
        if(cooldown.containsKey(p.getUniqueId())){
            if(cooldown.get(p.getUniqueId()) > System.currentTimeMillis()){
                return;
            }
        }

        cooldown.put(p.getUniqueId(),System.currentTimeMillis()+100);

        HotBarPreset hotBarPreset = this.hotBarPresets.get(reachedPreset);
        ItemStack itemStack = e.getItem();
        if(itemStack != null){
            if(itemStack.getType().equals(hotBarPreset.rightMaterial())){
                p.teleport(p.getLocation().add(0,1,0));
                Location location = p.getLocation();
                location.add(0,-1,0);
                location.getBlock().setType(Material.SEA_LANTERN);
                location.add(0,-1,0);
                if(location.getBlock().getType() == Material.SEA_LANTERN){
                    for(int x = -1; x<=1; x+=1){
                        for(int z = -1; z<=1; z+=1){
                            location.clone().add(x,0,z).getBlock().setType(Material.LIGHT);
                        }
                    }
                    location.getBlock().setType(Material.WATER);
                }
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent("§a§lComplétés: "+(reachedPreset+1)+"/"+HotBarSpeed.MAX_HOTBAR));
                this.hotBarSpeed.setNextPreset(p);
                this.hotBarSpeed.getGameManager().getPointManager().addPoint(p.getUniqueId(),1);
                if(reachedPreset+1 == HotBarSpeed.MAX_HOTBAR){
                    String comment = "§a§lSANS FAUTE!";
                    if(mistakes.containsKey(p.getUniqueId())){
                        comment = "§c§lAVEC "+mistakes.get(p.getUniqueId())+" FAUTES!";
                    }
                    this.hotBarSpeed.getGameManager().getPlayerManager().qualifiedPlayer(p,comment);
                    return;
                }
                p.getWorld().spawnParticle(Particle.VILLAGER_HAPPY,p.getLocation(),20,0.35,1,0.35);
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);
            }else{
                if(!mistakes.containsKey(p.getUniqueId())){
                    mistakes.put(p.getUniqueId(),0);
                }
                mistakes.put(p.getUniqueId(),mistakes.get(p.getUniqueId())+1);
                p.sendHurtAnimation(10F);
                p.getWorld().spawnParticle(Particle.FLAME,p.getLocation(),50,0.5,1,0.5,0.1);
                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE,1,1);
                p.getInventory().clear();
                Bukkit.getScheduler().runTaskLater(this.hotBarSpeed.getGameManager().getMain(), () -> hotBarSpeed.giveActualPreset(p),20L);
            }
        }
    }
}
