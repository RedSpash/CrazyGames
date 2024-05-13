package fr.red_spash.crazygames.game.models;

import fr.red_spash.crazygames.game.error.ConstructorError;
import fr.red_spash.crazygames.game.games.anvilfall.AnvilFall;
import fr.red_spash.crazygames.game.games.blastvillage.BlastVillage;
import fr.red_spash.crazygames.game.games.bridgeit.BridgeIt;
import fr.red_spash.crazygames.game.games.colorshuffle.ColorShuffle;
import fr.red_spash.crazygames.game.games.hotbarspeed.HotBarSpeed;
import fr.red_spash.crazygames.game.games.hotblock.HotBlock;
import fr.red_spash.crazygames.game.games.pickablock.PickABlock;
import fr.red_spash.crazygames.game.games.spleef.Spleef;
import fr.red_spash.crazygames.game.games.maze.Maze;
import fr.red_spash.crazygames.game.games.race.Race;
import fr.red_spash.crazygames.game.games.test.Test;
import org.bukkit.Material;

import java.awt.*;

public enum GameType {
    SPLEEF(Spleef.class,false,"Spleef",Color.WHITE, Material.GOLDEN_SHOVEL,"Éliminez vos adversaires en creusant sous leurs pieds!","Spleef vous plonge dans une frénésie de creusage, où vous devez éliminer habilement vos rivaux en les faisant chuter de la plateforme. Affinez votre stratégie pour demeurer le dernier en pied dans ce jeu de survie compétitif sur fond de glace et de neige."),
    MAZE(Maze.class,true,"Labyrinthe",new Color(250,100,100), Material.JUNGLE_LEAVES ,"Sortez le plus rapidement du labyrinthe pour gagner !","Explorez les mystères du Labyrinthe ! Trouvez votre chemin à travers des passages sinueux et remportez la partie en sortant ! Parviendrez-vous à percer ses secrets et à sortir victorieux ? C'est l'heure de l'aventure dans le Labyrinthe !"),
    BRIDGE_IT(BridgeIt.class, true, "Bridge It",Color.BLUE, Material.BLUE_WOOL ,"Passez de l'autre coté aussi rapidement que possible","Votre objectifs est d'être le premier à traverser le gouffre à l'aide des blocs de votre inventaire! Attention à ne pas donner la victoire à vos ennemis."),
    BLAST_VILLAGE(BlastVillage.class,false,"Blast Village" ,Color.RED, Material.BELL,"Survivez le plus longtemps à la pluie de météore !","Votre mission est de survivre aussi longtemps que possible à la pluie de météores qui s'abat sur le village. Évitez les impacts dévastateurs, trouvez un abri sûr, et démontrez votre habileté à réagir rapidement face à cette catastrophe imminente."),
    HOT_BLOCK(HotBlock.class,false,"Hot Block", Color.ORANGE, Material.LAVA_BUCKET ,"Restez sur les blocks le plus longtemps !","Restez sur les blocs le plus longtemps possible. Plus les blocs deviennent rouges, plus ils risquent de se briser rapidement. Sois agile et rapide pour devenir le champion de Hot Block."),
    COLOR_SHUFFLE(ColorShuffle.class,false,"Color Shuffle",Color.MAGENTA , Material.PINK_CONCRETE ,"Positionnez vous sur la couleur demandé pour survivre !", "Votre mission est de trouver rapidement la couleur demandée et de vous y positionner. Les couleurs sont en constante évolution, alors restez vif et agile pour suivre le rythme. Serez-vous capable de faire correspondre les teintes et devenir le maître du mélange chromatique ?"),
    RACE(Race.class,true,"Course",Color.green,Material.IRON_BOOTS,"Terminez la course pour vous qualifier.","§c§lAucune description pour le moment"),
    HOTBAR_SPEED(HotBarSpeed.class,true,"HotBar Speed",new Color(255, 123, 0),Material.TARGET ,"Cliquez le plus rapidement sur l'item ayant le moins de rapport avec les autres !","Votre but est de cliquer sur l'item ayant le moins de rapport avec les autres. Par exemple: si vos items sont en rapport avec l'eau et que vous avez de la lave, cliquez sur la lave pour passez à l'étape suivante."),
    ANVIL_FALL(AnvilFall.class,false,"Chute d'Enclume", new Color(255, 0, 106), Material.ANVIL ,"Évitez les enclumes qui tombent du ciel !","§c§lAucune description pour le moment."),
    PICK_A_BLOCK(PickABlock.class,false,"Pick A Block",new Color(211, 0, 255),Material.GOLD_BLOCK,"Cassez rapidement le block demandé !","Chaque manche, un block aléatoire de la carte est demandé. A vous de le trouver et de le casser pour ne pas être éliminé !"),
    TEST(Test.class,false,"§4§lUnder Test" , new Color(255,0,0) ,Material.BARRIER ,"Mode de jeu en test" , "Mode de jeu en test" );
    private final Class<? extends Game> gameClass;
    private final String name;
    private final String shortDescription;
    private final String longDescription;
    private final boolean qualificationMode;
    private final Color color;
    private final Material material;

    GameType(Class<? extends Game> gameClass, boolean qualificationMode, String name, Color color , Material material, String shortDescription, String longDescription) {
        this.qualificationMode = qualificationMode;
        this.material = material;
        this.color = color;
        this.gameClass = gameClass;
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
    }

    public Game createInstance() throws ConstructorError {
        try {
            return this.gameClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ConstructorError("Impossible de créer une nouvelle instance du jeu "+this.gameClass);
        }
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isQualificationMode() {
        return qualificationMode;
    }

    public String getName() {
        return name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public Class<? extends Game> getGameClass() {
        return gameClass;
    }

    public Color getColor() {
        return color;
    }
}
