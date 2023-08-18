package fr.red_spash.crazygames.game.models;

import fr.red_spash.crazygames.game.games.Spleef;

public enum GameType {
    SPLEEF(Spleef.class);

    private final Class<? extends Game> gameClass;

    GameType(Class<? extends Game > gameClass) {
        this.gameClass = gameClass;
    }

    public Class<? extends Game> getGameClass() {
        return gameClass;
    }
}
