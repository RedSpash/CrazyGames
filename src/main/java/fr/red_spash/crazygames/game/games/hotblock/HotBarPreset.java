package fr.red_spash.crazygames.game.games.hotblock;

import fr.red_spash.crazygames.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HotBarPreset {
    private final Material rightMaterial;
    private final Material[] wrongMaterials;

    public HotBarPreset(Material rightMaterial, Material... wrongMaterials){
        this.rightMaterial = rightMaterial;
        this.wrongMaterials = wrongMaterials;
    }

    public Material getRightMaterial() {
        return rightMaterial;
    }

    public Material[] getWrongMaterials() {
        return wrongMaterials;
    }

    public void giveRandomItems(Player p) {
        ArrayList<Material> wrongItems = new ArrayList<>(List.of(this.wrongMaterials));
        Collections.shuffle(wrongItems);

        int index = Utils.randomNumber(0,8);
        for(int i =0; i<=8; i++){
            if(i == index){
                p.getInventory().setItem(i,new ItemStack(this.rightMaterial));
            }else{
                p.getInventory().setItem(i,new ItemStack(wrongItems.remove(0)));

                if(wrongItems.isEmpty()){
                    wrongItems = new ArrayList<>(List.of(this.wrongMaterials));
                    Collections.shuffle(wrongItems);
                }
            }
        }

    }
}
