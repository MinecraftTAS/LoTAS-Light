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
						.then(Commands.literal("true").executes(LoTASLightCommand::showControls))
						.then(Commands.literal("false").executes(LoTASLightCommand::hideControls))
				)
				.then(Commands.literal("trc_defaultTickrate")
						.then(Commands.argument("tickrate", FloatArgumentType.floatArg(.1f, 60f)).executes(LoTASLightCommand::defaultTickrate)))
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
		LoTASLightClient.config.set(ConfigOptions.SAVESTATE_SHOW_CONTROLS, true);
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.showctrls.true"), true);
		return 1;
	}

	public static int hideControls(CommandContext<CommandSourceStack> context) {
		LoTASLightClient.config.set(ConfigOptions.SAVESTATE_SHOW_CONTROLS, false);
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.showctrls.false"), true);
		return 1;
	}

	public static int defaultTickrate(CommandContext<CommandSourceStack> context) {
		float tickrate = context.getArgument("tickrate", Float.class);
		LoTASLightClient.config.set(ConfigOptions.DEFAULT_TICKRATE, Float.toString(tickrate));
		return 1;
	}
}
