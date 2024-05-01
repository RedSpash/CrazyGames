package fr.red_spash.crazygames.game.games.hotblock;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.manager.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class HotBlockTask implements Runnable {

    private final ArrayList<Block> blocks;
    private final HotBlock hotBlock;
    private final HashMap<UUID, Location> lastLocation;
    private int timer;

    public HotBlockTask(HotBlock hotBlock) {
        this.hotBlock = hotBlock;
        this.blocks = hotBlock.getBlocks();
        this.timer = 1;
        this.lastLocation = new HashMap<>();
    }

    @Override
    public void run() {
        for(int i =0; i<=5; i++){
            if(!this.blocks.isEmpty()){
                Block block = blocks.get(Utils.randomNumber(0,blocks.size()-1));
                hotBlock.changeBlock(block);
            }
        }

        if(this.timer % 10 == 0){
            this.timer = 1;
            for(PlayerData playerData : this.hotBlock.getGameManager().getPlayerManager().getAlivePlayerData()){
                if(!playerData.isEliminated()){
                    Player p = Bukkit.getPlayer(playerData.getUuid());
                    if(p != null && p.isOnline()){
                        Location location = p.getLocation();
                        location.setY(this.hotBlock.getHeight());

                        if(location.getBlock().getType() != Material.AIR){
                            this.hotBlock.changeBlock(location.getBlock());
                        }

                        if(this.lastLocation.containsKey(p.getUniqueId())){
                            Location lastPlayerLocation = this.lastLocation.get(p.getUniqueId());
                            if(lastPlayerLocation.distance(p.getLocation()) <= 0.5){
                                for(int x = -1; x <=1; x++){
                                    for(int z = -1; z <=1; z++){
                                        if(x != 0 && z != 0){
                                            Location blockLocation = location.clone().add(x,0,z);
                                            this.hotBlock.changeBlock(blockLocation.getBlock());
                                        }
                                    }
                                }
                            }
                        }
                        this.lastLocation.put(playerData.getUuid(),p.getLocation());
                    }
                }
            }
        }

        this.timer = this.timer + 1;
    }

    public int getBlocksNumber() {
        return blocks.size();
    }
}
