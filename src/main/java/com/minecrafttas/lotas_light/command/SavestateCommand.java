package com.minecrafttas.lotas_light.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

public class SavestateCommand {
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
		//@formatter:off
		commandDispatcher
		.register(Commands.literal("savestate")
				.then(Commands.literal("save")
						.executes(SavestateCommand::saveNew)
						.then(Commands.argument("index", IntegerArgumentType.integer(1))
								.executes(SavestateCommand::saveIndex)
						)
				)
				.then(Commands.literal("load")
						.executes(SavestateCommand::loadRecent)
						.then(Commands.argument("index", IntegerArgumentType.integer(0))
								.executes(SavestateCommand::loadIndex)
						)
				)
				.then(Commands.literal("delete")
						.then(Commands.argument("index", IntegerArgumentType.integer(1))
								.executes(SavestateCommand::delete)
								.then(Commands.argument("indexTo", IntegerArgumentType.integer(1))
										.executes(SavestateCommand::deleteMore)
										.then(Commands.literal("force").executes(SavestateCommand::deleteDis))
								)
						)
				)
				.then(Commands.literal("reload")
						.executes(SavestateCommand::reload)
				)
		);
		//@formatter:on
	}

	private static int saveNew(CommandContext<CommandSourceStack> context) {
		int index = -1;
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.savestate.save"), true);
		return 0;
	}

	private static int saveIndex(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.savestate.save"), true);
		return index;
	}

	private static int loadRecent(CommandContext<CommandSourceStack> context) {
		int index = -1;
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.savestate.load"), true);
		return 0;
	}

	private static int loadIndex(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.savestate.load"), true);
		return index;
	}

	private static int delete(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.savestate.delete"), true);
		return index;
	}

	private static int deleteMore(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		int indexTo = context.getArgument("indexTo", Integer.class);
		int count = (index + 1) - indexTo;

		String translationKey = "msg.lotaslight.savestate.deleteMore";

		String key2 = translationKey += count == 1 ? ".singular" : ".plural";
		//@formatter:off
		context.getSource().sendSuccess(
			() -> Component.translatable(key2, count, "e")
				.withStyle(
					style -> style.withClickEvent(
								new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/savestate delete %s %s force", index, indexTo))
					)
				),
		true);
		//@formatter:on
		return index;
	}

	private static int deleteDis(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		int indexTo = context.getArgument("indexTo", Integer.class);
		context.getSource().sendSuccess(() -> Component.literal("Yeet"), true);
		return index;
	}

	private static int reload(CommandContext<CommandSourceStack> context) {
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.savestate.reload"), true);
		return 0;
	}
}
