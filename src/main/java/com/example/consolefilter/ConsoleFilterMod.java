package com.example.consolefilter;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import java.util.*;
import java.nio.file.*;
import java.io.IOException;

@Mod("consolefilter")
public class ConsoleFilterMod {
    public static List<String> filterKeyword = new ArrayList<>();

    public ConsoleFilterMod() {
        loadConfig();

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        context.getConfiguration().getRootLogger().addFilter(new LogFilter());
        context.updateLoggers();
    }

    private void loadConfig() {
        try {
            Path configDir = FMLPaths.CONFIGDIR.get().resolve("consolefilter");
            Files.createDirectories(configDir);

            Path configFile = configDir.resolve("filter_config.txt");


            List<String> defaultConfig = Arrays.asList(
                    "# Add the keywords you want to filter separated by commas",
                    "Filters = [fell from a high place, drowned]"
            );

            List<String> defaultFilters = Arrays.asList("fell from a high place", "drowned");


            if (!Files.exists(configFile)) {
                Files.write(configFile, defaultConfig);
            }

            List<String> lines = Files.readAllLines(configFile);
            boolean filterLoaded = false;

            for (String line : lines) {
                line = line.trim();

                if (line.startsWith("Filters")) {
                    int start = line.indexOf('[');
                    int end = line.indexOf(']');

                    if (start != -1 && end != -1 && end > start) {
                        String content = line.substring(start + 1, end);
                        String[] keywords = content.split(",");

                        filterKeyword.clear();

                        for (String keyword : keywords) {
                            filterKeyword.add(keyword.trim());
                        }
                        filterLoaded = true;
                        System.out.println("Filters loaded from config: " + filterKeyword);
                    } else{
                        System.out.println("Error: 'Filters' line malformed. Resetting config...");
                    }
                    break;
                }
            }

            if (!filterLoaded) {
                Files.write(configFile, defaultConfig);

                filterKeyword.clear();
                filterKeyword.addAll(defaultFilters);
                System.out.println("Config reset. Default filters applied: " + filterKeyword);
            }

        } catch (IOException e){
            System.err.println("Failed to load config" + e.getMessage());
        }
    }
}
