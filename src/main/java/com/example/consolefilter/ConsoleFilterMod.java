package com.example.consolefilter;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.nio.file.*;
import java.io.IOException;

@Mod("consolefilter")
@EventBusSubscriber(modid = "consolefilter", bus = EventBusSubscriber.Bus.GAME)
public class ConsoleFilterMod {
    public static final String MOD_ID = "consolefilter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static List<String> filterKeyword = new ArrayList<>();

    public ConsoleFilterMod() {
        LOGGER.info("ConsoleFilter Mod initialized");
        loadConfig();

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        context.getConfiguration().getRootLogger().addFilter(new LogFilter());
        context.updateLoggers();
    }

    public static void loadConfig() {
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
                        LOGGER.info("Filters loaded from config: {}", filterKeyword);
                    } else{
                        LOGGER.warn("'Filters' line malformed. Resetting config...");
                    }
                    break;
                }
            }

            if (!filterLoaded) {
                Files.write(configFile, defaultConfig);

                filterKeyword.clear();
                filterKeyword.addAll(defaultFilters);
                LOGGER.info("Config reset. Default filters applied: {}", filterKeyword);
            }

        } catch (IOException e){
            LOGGER.error("Failed to create config file", e);
        }
    }

    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("consolefilter")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("reload")
                        .executes(context -> {
                            loadConfig();
                            LOGGER.info("ConsoleFilter config reloaded by {}", context.getSource().getTextName());

                            MinecraftServer server = context.getSource().getServer();
                            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                                if (server.getPlayerList().isOp(player.getGameProfile())) {
                                    player.sendSystemMessage(Component.literal("§a[ConsoleFilter] config reloaded."));
                                }
                            }

                            return 1;
                        })
                )
        );
    }
}
