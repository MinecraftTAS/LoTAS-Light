package com.minecrafttas.lotas_light.command;

import java.text.SimpleDateFormat;
import java.util.List;

import com.minecrafttas.lotas_light.LoTASLight;
import com.minecrafttas.lotas_light.savestates.SavestateIndexer.Savestate;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;

public class SavestateCommand {
	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
		//@formatter:off
		commandDispatcher
		.register(Commands.literal("savestate")
				.then(Commands.literal("save")
						.executes(SavestateCommand::saveNew)
						.then(Commands.argument("index", IntegerArgumentType.integer(1))
								.executes(SavestateCommand::saveIndex)
								.then(Commands.argument("name", StringArgumentType.greedyString())
										.executes(SavestateCommand::saveNameIndex)))
						.then(Commands.argument("name", StringArgumentType.greedyString())
								.executes(SavestateCommand::saveName))
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
				.then(Commands.literal("info")
						.executes(SavestateCommand::info)
				)
				
		);
		//@formatter:on
	}

	private static int saveNew(CommandContext<CommandSourceStack> context) {
		int index = -1;
		LoTASLight.savestateHandler.saveState(index, (paths) -> {
			//@formatter:off
			context.getSource().sendSuccess(() -> 
				Component.translatable("msg.lotaslight.savestate.save", 
						Component.literal(paths.getName()).withStyle(ChatFormatting.YELLOW),
						Component.literal(Integer.toString(paths.getIndex())).withStyle(ChatFormatting.AQUA)
				).withStyle(ChatFormatting.GREEN), true);
			//@formatter:on
		});
		return 0;
	}

	private static int saveIndex(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		LoTASLight.savestateHandler.saveState(index, (paths) -> {
			//@formatter:off
			context.getSource().sendSuccess(() -> 
				Component.translatable("msg.lotaslight.savestate.save", 
						Component.literal(paths.getName()).withStyle(ChatFormatting.YELLOW),
						Component.literal(Integer.toString(paths.getIndex())).withStyle(ChatFormatting.AQUA)
				).withStyle(ChatFormatting.GREEN), true);
			//@formatter:on
		});
		return index;
	}

	private static int saveName(CommandContext<CommandSourceStack> context) {
		String name = context.getArgument("name", String.class);
		LoTASLight.savestateHandler.saveState(name, (paths) -> {
			//@formatter:off
			context.getSource().sendSuccess(() -> 
				Component.translatable("msg.lotaslight.savestate.save", 
						Component.literal(paths.getName()).withStyle(ChatFormatting.YELLOW),
						Component.literal(Integer.toString(paths.getIndex())).withStyle(ChatFormatting.AQUA)
				).withStyle(ChatFormatting.GREEN), true);
			//@formatter:on
		});
		return 0;
	}

	private static int saveNameIndex(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		String name = context.getArgument("name", String.class);
		LoTASLight.savestateHandler.saveState(index, name, (paths) -> {
			//@formatter:off
			context.getSource().sendSuccess(() -> 
				Component.translatable("msg.lotaslight.savestate.save", 
						Component.literal(paths.getName()).withStyle(ChatFormatting.YELLOW),
						Component.literal(Integer.toString(paths.getIndex())).withStyle(ChatFormatting.AQUA)
				).withStyle(ChatFormatting.GREEN), true);
			//@formatter:on
		});
		return 0;
	}

	private static int loadRecent(CommandContext<CommandSourceStack> context) {
		int index = -1;
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.savestate.load", index).withStyle(ChatFormatting.GREEN), true);
		return 0;
	}

	private static int loadIndex(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.savestate.load", index).withStyle(ChatFormatting.GREEN), true);
		return index;
	}

	private static int delete(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.savestate.delete", index).withStyle(ChatFormatting.GREEN), true);
		return index;
	}

	private static int deleteMore(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		int indexTo = context.getArgument("indexTo", Integer.class);
		int count = (index + 1) - indexTo;

		String translationKey = "msg.lotaslight.savestate.deleteMore" + (count == 1 ? ".singular" : ".plural");

		//@formatter:off
		Component countComponent = Component.literal(Integer.toString(count)).withStyle(ChatFormatting.RED);
		
		Component confirmationComponent = ComponentUtils.wrapInSquareBrackets(Component.translatable("msg.lotaslight.savestate.deleteMore.confirm", true)
				.withStyle(
						style -> style
							.withClickEvent(
										new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/savestate delete %s %s force", index, indexTo))
							)
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("msg.lotaslight.savestate.deleteMore.hover").withStyle(ChatFormatting.DARK_RED)))
				)).withStyle(ChatFormatting.GREEN);
		
		
		context.getSource().sendSuccess(
			() -> Component.translatable(translationKey, countComponent, confirmationComponent).withStyle(ChatFormatting.YELLOW),
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

	private static int info(CommandContext<CommandSourceStack> context) {
		List<Savestate> savestateList = LoTASLight.savestateHandler.getSavestateInfo(5);
		String format = I18n.get("msg.lotaslight.savestate.dateformat");
		context.getSource().sendSystemMessage(Component.literal(" "));
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		for (Savestate savestate : savestateList) {
			String index = Integer.toString(savestate.getIndex());
			//@formatter:off
			context.getSource().sendSystemMessage(
					Component.translatable("%s: %s",
						Component.literal(index).withStyle(ChatFormatting.AQUA), 
						Component.literal(savestate.getName()).withStyle(ChatFormatting.YELLOW)
					).withStyle(t->
						t.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/savestate load %s", savestate.getIndex())))
					).withStyle(t -> 
						t.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							Component.translatable("msg.lotaslight.savestate.info.hover", 
								Component.literal(dateFormat.format(savestate.getDate())).withStyle(ChatFormatting.GOLD),
								index
							).withStyle(ChatFormatting.GREEN)))
					)
				);
			//@formatter:on
		}
		return 0;
	}
}
