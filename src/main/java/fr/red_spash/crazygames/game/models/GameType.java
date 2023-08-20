package fr.red_spash.crazygames.game.models;

import fr.red_spash.crazygames.game.games.Spleef;

public enum GameType {
    SPLEEF(Spleef.class,"Spleef","Éliminez vos adversaires en creusant sous leurs pieds!","Spleef vous plonge dans une frénésie de creusage, où vous devez éliminer habilement vos rivaux en les faisant chuter de la plateforme. Affinez votre stratégie pour demeurer le dernier en pied dans ce jeu de survie compétitif sur fond de glace et de neige.");

    private final Class<? extends Game> gameClass;
    private final String name;
    private final String shortDescription;
    private final String longDescription;

    GameType(Class<? extends Game> gameClass,String name, String shortDescription, String longDescription) {
        this.gameClass = gameClass;
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
    }

    public Game createInstance(){
        switch (this){
            case SPLEEF -> {
                return new Spleef(this);
            }
        }
        return null;
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
