package fr.red_spash.crazygames.world;

import fr.red_spash.crazygames.Main;
import fr.red_spash.crazygames.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class WorldManager {

    private final Lobby lobby;

    public WorldManager(Lobby lobby) {
        this.lobby = lobby;
    }

    public void deleteWorldFiles(File worldFolder) {
        if (worldFolder.exists() && worldFolder.isDirectory()) {
            File[] files = worldFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorldFiles(file);
                    }
                    try {
                        Files.delete(file.toPath());
                    } catch (IOException e) {
                        Bukkit.getLogger().warning("Impossible de supprimer le fichier: "+file.getName()+"!");
                    }
                }
            }
            try {
                Files.delete(worldFolder.toPath());
            } catch (IOException e) {
                Bukkit.getLogger().warning("Impossible de supprimer le monde: "+worldFolder.getName()+"!");
            }
        }
    }

    public void teleportPlayersAndRemoveWorld(World world, boolean save) {
        for(Player p : world.getPlayers()){
            p.teleport(Main.spawn);
            p.sendMessage("§cLe monde vient d'être détruit! Vous êtes désormais au spawn.");
            this.lobby.giveItems(p);
        }
        Bukkit.unloadWorld(world,save);
    }

}
