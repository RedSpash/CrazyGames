package fr.red_spash.crazygames.game.models;

import fr.red_spash.crazygames.game.games.spleef.Spleef;
import fr.red_spash.crazygames.game.games.maze.Maze;

import java.lang.reflect.InvocationTargetException;

public enum GameType {
    SPLEEF(Spleef.class,false,"Spleef","Éliminez vos adversaires en creusant sous leurs pieds!","Spleef vous plonge dans une frénésie de creusage, où vous devez éliminer habilement vos rivaux en les faisant chuter de la plateforme. Affinez votre stratégie pour demeurer le dernier en pied dans ce jeu de survie compétitif sur fond de glace et de neige."),
    MAZE(Maze.class,true,"Labyrinthe","Sortez le plus rapidement du labyrinthe pour gagner !","Explorez les mystères du Labyrinthe ! Trouvez votre chemin à travers des passages sinueux et remportez la partie en sortant ! Parviendrez-vous à percer ses secrets et à sortir victorieux ? C'est l'heure de l'aventure dans le Labyrinthe !");

    private final Class<? extends Game> gameClass;
    private final String name;
    private final String shortDescription;
    private final String longDescription;
    private final Boolean qualificationMode;

    GameType(Class<? extends Game> gameClass, Boolean qualificationMode, String name, String shortDescription, String longDescription) {
        this.qualificationMode = qualificationMode;
        this.gameClass = gameClass;
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
    }

    public Game createInstance(){
        try {
            return this.gameClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean isQualificationMode() {
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
