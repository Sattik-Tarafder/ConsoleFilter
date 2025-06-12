package com.example.consolefilter;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConsoleFilterCommand {
    public static final String MOD_ID = "consolefilter";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("consolefilter")
                .requires(source -> source.hasPermissionLevel(2)) // OPs only
                .then(CommandManager.literal("reload")
                        .executes(context -> {
                            ConsoleFilter.loadConfig(); // Your method to reload config
                            LOGGER.info("ConsoleFilter config reloaded by {}", context.getSource().getName());

                            // Broadcast message to all OPs
                            MinecraftServer server = context.getSource().getServer();
                            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                                if (server.getPlayerManager().isOperator(player.getGameProfile())) {
                                    player.sendMessage(Text.literal("§a[ConsoleFilter] config reloaded."), false);
                                }
                            }

                            // Also notify the command executor
                            return 1;
                        })
                )
        );
    }
}
