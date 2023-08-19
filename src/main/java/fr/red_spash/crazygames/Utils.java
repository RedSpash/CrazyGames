package fr.red_spash.crazygames;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    public static int random_number(Integer min, Integer max){
        max = max +1;
        return (int) (Math.random()*(max-min)) + min;
    }

    public static void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) {
        try {
            Files.walk(Paths.get(sourceDirectoryLocation))
                    .forEach(source -> {
                        Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                                .substring(sourceDirectoryLocation.length()));
                        try {
                            Files.copy(source, destination);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteWorldFiles(File worldFolder) {
        if (worldFolder.exists() && worldFolder.isDirectory()) {
            File[] files = worldFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorldFiles(file);
                    }
                    boolean isDeleted = file.delete();
                    if(!isDeleted){
                        Bukkit.getLogger().warning("Impossible de supprimer le fichier: "+file.getName()+"!");
                    }
                }
            }
            boolean isDeleted = worldFolder.delete();
            if(!isDeleted){
                Bukkit.getLogger().warning("Impossible de supprimer le monde: "+worldFolder.getName()+"!");
            }
        }
    }
}
