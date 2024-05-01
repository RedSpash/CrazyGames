package fr.red_spash.crazygames.scoreboard;

import fr.red_spash.crazygames.game.manager.GameManager;
import fr.red_spash.crazygames.game.manager.PlayerData;
import fr.red_spash.crazygames.game.manager.PlayerManager;
import fr.red_spash.crazygames.game.models.Game;
import fr.red_spash.crazygames.game.models.GameType;
import fr.red_spash.crazygames.game.tasks.GameTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.List;

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

            this.setScoreBoard(playerData,p,board);
            this.updateTeam(board);
        }
    }

    private void setScoreBoard(PlayerData playerData,Player p, RedScoreBoard board) {
        board.setLine(14,"§f");

        if(this.gameManager.getActualGame() != null){
            Game game = this.gameManager.getActualGame();
            GameType gameType = game.getGameType();

            PlayerManager playerManager = this.gameManager.getPlayerManager();
            if(gameType == null){
                    board.setLine(13,"§6§lFIN DE LA PARTIE");
                    if(board.lineExist(12)){
                        for(int i = 12; i>= 1; i--){
                            board.removeLine(i);
                        }
                    }
                return;
            }else{
                if(gameType.isQualificationMode()){
                    board.setLine(12,SYMBOL+"§fQualifiés: §a"+playerManager.getQualifiedPlayers().size()+"/"+(playerManager.getAmountQualifiedPlayer()));
                }else{
                    board.setLine(12,SYMBOL+"§fÉliminés: §c"+playerManager.getEliminatedPlayer().size()+"/"+playerManager.getAmountEliminatedPlayer());
                }
            }

            board.setLine(13,SYMBOL+"Temps: §a"+getTimeRemaining());
            board.setLine(11,"§f§1");
            board.setLine(10,SYMBOL+"Jeu: §a"+gameType.getName());
            board.setLine(9,SYMBOL+"État: "+playerData.getPlayerState());

            this.addOtherLineOfGameMode(board,p,game);

            if(board.lineExist(7)){
                board.setLine(8,"§f§r§1");
            }else{
                board.removeLine(8);
            }
        }else{
            board.setLine(13,"§c"+SYMBOL+"En attente du");
            board.setLine(12,"§c"+SYMBOL+"lancement de la partie.");
        }

        board.setLine(1,"§f");
        board.setLine(0,"§7Développé par Red_Spash");
    }

    private void addOtherLineOfGameMode(RedScoreBoard board,Player p, Game game) {
        List<String> otherLines = game.updateScoreboard(p);
        int index = 0;
        for(int line = 7; line >= 2; line--){
            if(otherLines.size() > index){
                String stringLine = otherLines.get(index);
                if(stringLine.length() == stringLine.chars().filter(ch -> ch == '§').count()*2){
                    board.setLine(line,otherLines.get(index));
                }else{
                    board.setLine(line,SYMBOL+otherLines.get(index));
                }
                index ++;
            }else{
                if(board.lineExist(line)){
                    board.removeLine(line);
                }
            }
        }
    }

    private void updateTeam(RedScoreBoard board) {
        for(Player pl : Bukkit.getOnlinePlayers()){
            PlayerData plData = this.gameManager.getPlayerData(pl.getUniqueId());

            if(plData.getDefaultLife() >0){
                this.showLife(board,pl);
            }else{
                this.addDefaultTeam(board,pl);
            }
        }
    }

    private void addDefaultTeam(RedScoreBoard board, Player pl) {
        Team defaultTeam = board.getTeam("defaultTeam");
        if(defaultTeam == null){
            defaultTeam = board.createTeam("defaultTeam");
            defaultTeam.setColor(ChatColor.LIGHT_PURPLE);
        }

        if(!defaultTeam.hasEntry(pl.getName())){
            defaultTeam.addEntry(pl.getName());
        }
    }

    private void showLife(RedScoreBoard board, Player pl) {
        PlayerData plData = this.gameManager.getPlayerData(pl.getUniqueId());
        Team health = board.getTeam("heal"+plData.getVisualLife());

        if(health == null){
            health = board.createTeam("heal"+plData.getVisualLife());
            health.setPrefix("§c§l"+plData.getVisualLife()+" ❤ ");
            health.setColor(ChatColor.LIGHT_PURPLE);
        }
        if(!health.hasEntry(pl.getName())){
            health.addEntry(pl.getName());
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
            minute = 0;
            second = 0;
        }
        return minute+"m "+second+"s";
    }
}
