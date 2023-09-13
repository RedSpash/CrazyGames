package fr.red_spash.crazygames.game.models;

import fr.red_spash.crazygames.game.error.ConstructorError;
import fr.red_spash.crazygames.game.games.blastvillage.BlastVillage;
import fr.red_spash.crazygames.game.games.bridgeit.BridgeIt;
import fr.red_spash.crazygames.game.games.colorshuffle.ColorShuffle;
import fr.red_spash.crazygames.game.games.hotblock.HotBlock;
import fr.red_spash.crazygames.game.games.spleef.Spleef;
import fr.red_spash.crazygames.game.games.maze.Maze;

import java.lang.reflect.InvocationTargetException;

public enum GameType {
    SPLEEF(Spleef.class,false,"Spleef","Éliminez vos adversaires en creusant sous leurs pieds!","Spleef vous plonge dans une frénésie de creusage, où vous devez éliminer habilement vos rivaux en les faisant chuter de la plateforme. Affinez votre stratégie pour demeurer le dernier en pied dans ce jeu de survie compétitif sur fond de glace et de neige."),
    MAZE(Maze.class,true,"Labyrinthe","Sortez le plus rapidement du labyrinthe pour gagner !","Explorez les mystères du Labyrinthe ! Trouvez votre chemin à travers des passages sinueux et remportez la partie en sortant ! Parviendrez-vous à percer ses secrets et à sortir victorieux ? C'est l'heure de l'aventure dans le Labyrinthe !"),
    BRIDGE_IT(BridgeIt.class, true, "Bridge It","Passez de l'autre coté aussi rapidement que possible","Votre objectifs est d'être le premier à traverser le gouffre à l'aide des blocs de votre inventaire! Attention à ne pas donner la victoire à vos ennemis."),
    BLAST_VILLAGE(BlastVillage.class,false,"Blast Village","Survivez le plus longtemps à la pluie de météore !","Votre mission est de survivre aussi longtemps que possible à la pluie de météores qui s'abat sur le village. Évitez les impacts dévastateurs, trouvez un abri sûr, et démontrez votre habileté à réagir rapidement face à cette catastrophe imminente."),
    HOT_BLOCK(HotBlock.class,false,"Hot Block","Restez sur les blocks le plus longtemps !","Restez sur les blocs le plus longtemps possible. Plus les blocs deviennent rouges, plus ils risquent de se briser rapidement. Sois agile et rapide pour devenir le champion de Hot Block."),
    COLOR_SHUFFLE(ColorShuffle.class,false,"Color Shuffle" ,"Positionnez vous sur la couleur demandé pour survivre !", "Votre mission est de trouver rapidement la couleur demandée et de vous y positionner. Les couleurs sont en constante évolution, alors restez vif et agile pour suivre le rythme. Serez-vous capable de faire correspondre les teintes et devenir le maître du mélange chromatique ?");

    private final Class<? extends Game> gameClass;
    private final String name;
    private final String shortDescription;
    private final String longDescription;
    private final boolean qualificationMode;

    GameType(Class<? extends Game> gameClass, boolean qualificationMode, String name, String shortDescription, String longDescription) {
        this.qualificationMode = qualificationMode;
        this.gameClass = gameClass;
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
    }

    public Game createInstance(){
        try {
            return this.gameClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            new ConstructorError("Impossible de créer une nouvelle instance du jeu "+this.gameClass).printStackTrace();
            return null;
        }
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
}
