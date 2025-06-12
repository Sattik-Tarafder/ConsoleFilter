package com.example.consolefilter;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConsoleFilter implements ModInitializer {
	public static final String MOD_ID = "consolefilter";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static List<String> filterKeyword = new ArrayList<>();


	@Override
	public void onInitialize() {
		LOGGER.info("ConsoleFilter Mod initialized");
		loadConfig();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ConsoleFilterCommand.register(dispatcher);
		});
	}

	public static void loadConfig() {
		Path configPath = Path.of("config", "consolefilter", "filter_config.txt");

		List<String> defaultConfig = Arrays.asList(
				"# Add the keywords you want to filter separated by commas",
				"Filters = [fell from a high place, drowned]"
		);

		List<String> defaultFilters = Arrays.asList("fell from a high place", "drowned");

		try {
			if (!Files.exists(configPath)) {
				Files.createDirectories(configPath.getParent());
				Files.write(configPath, defaultConfig);
			}

			List<String> lines = Files.readAllLines(configPath);
			boolean filterLoaded = false;

			for (String line : lines) {
				line = line.trim();

				if (line.startsWith("Filters")) {
					int start = line.indexOf('[');
					int end = line.indexOf(']');

					if (start != -1 && end != -1 && end > start) {
						String[] keywords = line.substring(start + 1, end).split(",");

						filterKeyword.clear();

						for (String keyword : keywords) {
							filterKeyword.add(keyword.trim());
						}
						filterLoaded = true;
                        LOGGER.info("Filters loaded from config: {}", filterKeyword);
					} else {
						LOGGER.warn("'Filters' line malformed. Resetting config...");
					}
					break;
				}
			}

			if (!filterLoaded) {
				Files.write(configPath, defaultConfig);

				filterKeyword.clear();
				filterKeyword.addAll(defaultFilters);
                LOGGER.info("Config reset. Default filters applied: {}", filterKeyword);
			}

		} catch (IOException e) {
			LOGGER.error("Failed to create config file", e);
		}
	}

}
