package mg.ituprom16.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {
   public String readFile(String path) {
        try {
            // Lire le fichier en tant que String
            return Files.readString(Path.of(path));
        } catch (IOException e) {
        // Gérer les erreurs
            e.printStackTrace();
            return null; // Vous pouvez aussi retourner une chaîne vide ou lever une exception personnalisée
        }
    }
}
