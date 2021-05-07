package fr.ph1lou.coffee_dispenser.utils;

import fr.ph1lou.coffee_dispenser.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FilesUtils {

    public static String loadContent(Main main, String fileName) {

        File file = new File(main.getDataFolder(), fileName);

        if (file.exists()) {

            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8))) {


                final StringBuilder text = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    text.append(line).append('\n');
                }

                return text.toString();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static void generate(Main main, String fileName) {

        File file = new File(main.getDataFolder(), fileName);

        if (!file.exists()) {
            try (InputStream in = main.getResourceAsStream(fileName)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
