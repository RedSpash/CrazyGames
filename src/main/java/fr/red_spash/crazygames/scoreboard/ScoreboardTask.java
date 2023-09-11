package fr.red_spash.crazygames.scoreboard;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.game.tasks.GameTimer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ScoreboardTask implements Runnable{
    private static final String SYMBOL = "◈ ";
    private final GameManager gameManager;

    public ScoreboardTask(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerData playerData = this.gameManager.getPlayerData(p.getUniqueId());
            RedScoreBoard board = playerData.getScoreboard();

            board.setLine(15,"§f");

            if(this.gameManager.getActualGame() != null){
                Game game = this.gameManager.getActualGame();
                GameType gameType = game.getGameType();

                board.setLine(14,SYMBOL+"Temps: §a"+getTimeRemaining());
                if(gameType.isQualificationMode()){
                    board.setLine(13,SYMBOL+"§fQualifiés: §a"+this.gameManager.getQualifiedPlayers().size()+"/"+(this.gameManager.getAmountQualifiedPlayer()));
                }else{
                    board.setLine(13,SYMBOL+"§fÉliminés: §c"+this.gameManager.getEliminatedPlayer().size()+"/"+this.gameManager.getAmountEliminatedPlayer());
                }
                String state = "§aVIVANT";
                if(playerData.isDead()){
                    state = "§cMORT";
                }
                board.setLine(12,"§f§1");
                board.setLine(11,SYMBOL+"Jeu: §a"+gameType.getName());
                board.setLine(10,SYMBOL+"État: "+state);
                board.setLine(9,"§f");
                int index = 8;
                for(String line : game.updateScoreboard()){
                    if(index >= 2){
                        board.setLine(index,SYMBOL+line);
                        index--;
                    }
                }
                board.setLine(1,"§f§r§1");
            }else{
                board.setLine(14,"§c"+SYMBOL+"En attente du");
                board.setLine(13,"§c"+SYMBOL+"lancement de la partie.");
            }
            board.setLine(0,"§7Développé par Red_Spash");

        }
    }

    private String getTimeRemaining() {
        GameTimer gameTimer = this.gameManager.getGameTimer();
        int minute;
        int second;

        if(gameTimer != null){
            minute = gameTimer.getTimeRemaining()/60;
            second = gameTimer.getTimeRemaining()%60;
        }else{
            minute = this.gameManager.getMaxTime()/60;
            second = this.gameManager.getMaxTime()%60;
        }
        return minute+"m "+second+"s";
    }
}
