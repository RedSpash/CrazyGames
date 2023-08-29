package fr.red_spash.crazygames.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class RedScoreBoard {

    private HashMap<Integer,Team> Lines = new HashMap<>();
    private Scoreboard board;
    private String title;
    private Objective objective;

    private static ArrayList<ChatColor> chatColors = new ArrayList<>(Arrays.asList(
            ChatColor.AQUA,
            ChatColor.BLACK,
            ChatColor.BLUE,
            ChatColor.DARK_AQUA,
            ChatColor.DARK_BLUE,
            ChatColor.DARK_GRAY,
            ChatColor.DARK_RED,
            ChatColor.DARK_GREEN,
            ChatColor.DARK_PURPLE,
            ChatColor.GRAY,
            ChatColor.GREEN,
            ChatColor.LIGHT_PURPLE,
            ChatColor.RED,
            ChatColor.WHITE,
            ChatColor.YELLOW,
            ChatColor.BOLD
    ));

    public RedScoreBoard(String title){
        this.title = title;
        this.board = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = board.registerNewObjective(title, Criteria.DUMMY,title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public Team getTeam(String name){
        return this.board.getTeam(name);
    }

    public Team createTeam(String name){
        return this.board.registerNewTeam(name);
    }


    public void setLine(Integer position, String text){
        if(!Lines.containsKey(position)){
            this.createTeam(position,text);
        }else{
            this.setTeamName(Lines.get(position),text,position);
        }
    }

    private void createTeam(Integer position,String text) {
        Team team = board.registerNewTeam(chatColors.get(position)+"");

        team.addEntry(chatColors.get(position)+"");
        this.objective.getScore(chatColors.get(position)+"").setScore(position);

        setTeamName(team,text,position);

        Lines.put(position,team);

    }

    private void setTeamName(Team team, String text, Integer position) {
        team.setPrefix(text);
        team.setSuffix("§f");
    }

    public Scoreboard getBoard() {
        return board;
    }
}