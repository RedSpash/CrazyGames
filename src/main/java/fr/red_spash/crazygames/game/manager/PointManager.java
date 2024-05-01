package fr.red_spash.crazygames.game.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PointManager {

    private final HashMap<UUID, Integer> points = new HashMap<>();
    private final ArrayList<String> extraScoreboard = new ArrayList<>();
    private final GameManager gameManager;

    public PointManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void addPoint(UUID uuid, int amount){
        this.points.put(uuid, this.getPoint(uuid)+amount);
        this.updateGlobalScoreboard();
    }

    public Integer getPoint(UUID uuid){
        return this.points.getOrDefault(uuid,0);
    }

    private void updateGlobalScoreboard(){
        ArrayList<String> temporaryScoreboard = new ArrayList<>();
        List<Map.Entry<UUID, Integer>> list = new LinkedList<>(points.entrySet());

        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        int index = 1;
        for(Map.Entry<UUID, Integer> entry : list){
            UUID playerUUID = entry.getKey();
            Integer point = entry.getValue();
            Player pl = Bukkit.getPlayer(playerUUID);
            String name = "??????";
            if(pl != null){
                name = pl.getName();
            }
            temporaryScoreboard.add("§a"+index+" §d"+name+" §7- §e"+point);

            index += 1;
        }
        this.extraScoreboard.clear();
        this.extraScoreboard.addAll(temporaryScoreboard);
    }

    public ArrayList<Integer> getOrderedPoints() {
        ArrayList<Integer> scores = new ArrayList<>(this.points.values());
        scores.sort(Collections.reverseOrder());
        return scores;
    }


    public List<String> getExtraScoreboard() {
        return this.extraScoreboard;
    }

    public boolean isUsed() {
        return !this.points.isEmpty();
    }

    public List<UUID> getQualifiedUUID(){
        ArrayList<UUID> qualified = new ArrayList<>();
        ArrayList<Integer> scores = this.getOrderedPoints();
        int requiredPoint = scores.get(scores.size()-this.gameManager.getPlayerManager().getAmountEliminatedPlayer());
        int amountOfEliminatedPlayer = this.gameManager.getPlayerManager().getAmountEliminatedPlayer();
        for(UUID uuid : this.points.keySet()){
            int point = this.getPoint(uuid);
            if(scores.size() >= amountOfEliminatedPlayer){
                if(point > requiredPoint){
                    qualified.add(uuid);
                }
            }
        }
        return qualified;
    }
}
