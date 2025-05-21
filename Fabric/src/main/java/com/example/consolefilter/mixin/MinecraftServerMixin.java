package com.example.consolefilter.mixin;

import com.example.consolefilter.LogFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Inject(at = @At("HEAD"), method = "loadWorld")

	private void injectFilter(CallbackInfo ci) {
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		context.getConfiguration().getRootLogger().addFilter(new LogFilter());
		context.updateLoggers();
	}
}
