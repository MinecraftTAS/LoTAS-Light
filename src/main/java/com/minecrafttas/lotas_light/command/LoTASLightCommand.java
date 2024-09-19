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
				.then(Commands.literal("showMessages")
						.then(Commands.literal("true").executes(LoTASLightCommand::showMessages))
						.then(Commands.literal("false").executes(LoTASLightCommand::hideMessages))
				)
				.then(Commands.literal("defaultTickrate")
						.then(Commands.argument("tickrate", FloatArgumentType.floatArg(.1f, 60f)).executes(LoTASLightCommand::defaultTickrate)))
		);
		//@formatter:on
	}

	public static int showMessages(CommandContext<CommandSourceStack> context) {
		LoTASLightClient.config.set(ConfigOptions.SHOW_MESSAGES, true);
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.showmsg.true"), true);
		return 1;
	}

	public static int hideMessages(CommandContext<CommandSourceStack> context) {
		LoTASLightClient.config.set(ConfigOptions.SHOW_MESSAGES, false);
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.showmsg.false"), true);
		return 1;
	}

	public static int defaultTickrate(CommandContext<CommandSourceStack> context) {
		float tickrate = context.getArgument("tickrate", Float.class);
		LoTASLightClient.config.set(ConfigOptions.DEFAULT_TICKRATE, Float.toString(tickrate));
		return 1;
	}
}
