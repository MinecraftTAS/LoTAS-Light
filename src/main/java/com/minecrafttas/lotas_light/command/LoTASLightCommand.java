package com.minecrafttas.lotas_light.command;

import com.minecrafttas.lotas_light.LoTASLightClient;
import com.minecrafttas.lotas_light.config.Configuration.ConfigOptions;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class LoTASLightCommand {
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
		//@formatter:off
		commandDispatcher
		.register(Commands.literal("lotaslight")
				.then(Commands.literal("trc_showMessages")
						.then(Commands.literal("true").executes(LoTASLightCommand::showMessages))
						.then(Commands.literal("false").executes(LoTASLightCommand::hideMessages))
				)
				.then(Commands.literal("savestate_showControls")
						.executes(LoTASLightCommand::showControls))
				.then(Commands.literal("trc_defaultTickrate")
						.then(Commands.argument("tickrate", FloatArgumentType.floatArg(.1f, 60f)).executes(LoTASLightCommand::defaultTickrate)))
				.then(Commands.literal("trc_tickIndicator")
						.executes(LoTASLightCommand::tickIndicator))
				.then(Commands.literal("trc_pauseIndicator")
						.executes(LoTASLightCommand::tickPauseIndicator))
				.then(Commands.literal("trc_indicatorLocation")
						.then(Commands.literal("top_right")
								.executes(context -> LoTASLightCommand.tickIndicatorLocation(context, "top_right")))
						.then(Commands.literal("top_left")
								.executes(context -> LoTASLightCommand.tickIndicatorLocation(context, "top_left")))
						.then(Commands.literal("bottom_right")
								.executes(context -> LoTASLightCommand.tickIndicatorLocation(context, "bottom_right")))
						.then(Commands.literal("bottom_left")
								.executes(context -> LoTASLightCommand.tickIndicatorLocation(context, "bottom_left")))
						)
				
		);
		//@formatter:on
	}

	public static int showMessages(CommandContext<CommandSourceStack> context) {
		LoTASLightClient.config.set(ConfigOptions.TICKRATE_SHOW_MESSAGES, true);
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.showmsg.true"), true);
		return 1;
	}

	public static int hideMessages(CommandContext<CommandSourceStack> context) {
		LoTASLightClient.config.set(ConfigOptions.TICKRATE_SHOW_MESSAGES, false);
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.showmsg.false"), true);
		return 1;
	}

	public static int showControls(CommandContext<CommandSourceStack> context) {
		boolean enable = LoTASLightClient.config.toggle(ConfigOptions.SAVESTATE_SHOW_CONTROLS);
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.showctrls." + enable), true);
		return 1;
	}

	public static int defaultTickrate(CommandContext<CommandSourceStack> context) {
		float tickrate = context.getArgument("tickrate", Float.class);
		LoTASLightClient.config.set(ConfigOptions.DEFAULT_TICKRATE, Float.toString(tickrate));
		return 1;
	}

	public static int tickIndicator(CommandContext<CommandSourceStack> context) {
		LoTASLightClient.config.toggle(ConfigOptions.TICKRATE_INDICATOR);
		return 1;
	}

	public static int tickPauseIndicator(CommandContext<CommandSourceStack> context) {
		LoTASLightClient.config.toggle(ConfigOptions.TICKRATE_PAUSE_INDICATOR);
		return 1;
	}

	public static int tickIndicatorLocation(CommandContext<CommandSourceStack> context, String corner) {
		LoTASLightClient.config.set(ConfigOptions.TICKRATE_INDICATOR_LOCATION, corner);
		return 1;
	}
}
