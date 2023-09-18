package fr.red_spash.crazygames.game.games.race;

import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Race extends Game {

    ArrayList<Block> departBlocks;
    public Race() {
        super(GameType.RACE);
        this.departBlocks = new ArrayList<>();
    }

    @Override
    public void initializePlayers() {
        for(int x = -15; x<=15; x++){
            for(int y = -15; y<=15; y++){
                for(int z = -15; z<=15; z++){
                    Block block = super.getGameMap().getSpawnLocation().add(x,y,z).getBlock();
                    if(block.getType().toString().toLowerCase().contains("stained_glass")){
                        this.departBlocks.add(block);
                    }
                }
            }
        }

        super.initializePlayers();
        for(Player p : Bukkit.getOnlinePlayers()){
            p.setCollidable(false);
        }

    }

    @Override
    public void startGame() {
        super.registerListener(new RaceListener(super.gameManager));
        super.getGameManager().getGameInteractions()
                .addKillPlayerMaterials(Material.WATER,Material.LAVA)
                .addMaterialPotionEffectHashMap(Material.MAGENTA_GLAZED_TERRACOTTA,new PotionEffect(PotionEffectType.SPEED,20,3,false,false,true));

        for(Block block : this.departBlocks){
            FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation().add(0.5,0.5,0.5),block.getType().createBlockData());
            fallingBlock.setDropItem(false);
            fallingBlock.setDamagePerBlock(0);
            fallingBlock.setMaxDamage(0);
            fallingBlock.setTicksLived(20*2);
            fallingBlock.setVelocity(block.getLocation().toVector().subtract(this.getGameMap().getSpawnLocation().toVector()).normalize());
            block.setType(Material.AIR);
        }
    }
}
