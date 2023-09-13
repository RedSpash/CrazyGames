package fr.red_spash.crazygames.game.games.colorshuffle;

import fr.red_spash.crazygames.Utils;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class ColorShuffle extends Game {

    private static final Map<Material,ColorTranslation> allowedMaterials = Map.ofEntries(
            entry(Material.WHITE_CONCRETE, new ColorTranslation("Blanc",Color.WHITE,'f')),
                    entry(Material.BLACK_CONCRETE, new ColorTranslation("Noir", new Color(106,106,106),'0')),
                    entry(Material.BLUE_CONCRETE, new ColorTranslation("Bleu",Color.BLUE,'9')),
                    entry(Material.BROWN_CONCRETE, new ColorTranslation("Marron", new Color(88,41,0),'6')),
                    entry(Material.CYAN_CONCRETE, new ColorTranslation("Cyan",Color.CYAN,'3')),
                    entry(Material.GRAY_CONCRETE,new ColorTranslation("Gris",Color.GRAY,'8')),
                    entry(Material.GREEN_CONCRETE,new ColorTranslation("Vert",new Color(100,255,100),'2')),
                    entry(Material.LIGHT_BLUE_CONCRETE,new ColorTranslation("Bleu Clair", new Color(50,50,255),'b')),
                    entry(Material.LIGHT_GRAY_CONCRETE, new ColorTranslation("Gris Clair",Color.LIGHT_GRAY,'7')),
                    entry(Material.LIME_CONCRETE, new ColorTranslation("Vert Clair", Color.GREEN,'a')),
                    entry(Material.MAGENTA_CONCRETE, new ColorTranslation("Magenta",Color.MAGENTA,'d')),
                    entry(Material.ORANGE_CONCRETE, new ColorTranslation("Orange", Color.ORANGE,'6')),
                    entry(Material.PURPLE_CONCRETE, new ColorTranslation("Violet",new Color(155,58,205),'5')),
                    entry(Material.PINK_CONCRETE, new ColorTranslation("Rose", Color.PINK,'d')),
                    entry(Material.RED_CONCRETE, new ColorTranslation("Rouge",Color.RED,'c')),
                    entry(Material.YELLOW_CONCRETE,  new ColorTranslation("Jaune", Color.YELLOW,'e'))
    );

    private final ArrayList<Block> blocks;
    private ColorShuffleTask colorShuffleTask;


    public ColorShuffle() {
        super(GameType.COLOR_SHUFFLE);
        this.blocks = new ArrayList<>();
    }

    @Override
    public void initializePlayers() {
        this.blocks.addAll(super.getBlockPlatform(new ArrayList<>(allowedMaterials.keySet()),null));

        super.gameManager.getGameInteractions().setDeathUnderSpawn(2);
        super.initializePlayers();
    }

    @Override
    public void startGame() {

        this.colorShuffleTask = new ColorShuffleTask(this.blocks,new HashMap<>(allowedMaterials),super.gameManager);
        super.initializeTask(this.colorShuffleTask,0,1);
    }

    @Override
    public List<String> updateScoreboard() {
        if(this.colorShuffleTask != null){
            ArrayList<String> list = new ArrayList<>();
            list.add("Tour n°"+this.colorShuffleTask.getRoundNumber());
            list.add("Couleur: "+this.colorShuffleTask.getChosenMaterialName());
            list.add("Temps max: §c"+Utils.onXString(4,String.valueOf(Utils.round(this.colorShuffleTask.getMaxTime(),100.0)))+"s");
            return list;
        }
        return super.updateScoreboard();
    }
}
