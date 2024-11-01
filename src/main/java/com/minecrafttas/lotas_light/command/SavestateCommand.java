package com.minecrafttas.lotas_light.command;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.UnaryOperator;

import com.minecrafttas.lotas_light.LoTASLight;
import com.minecrafttas.lotas_light.savestates.SavestateHandler.SavestateCallback;
import com.minecrafttas.lotas_light.savestates.SavestateIndexer.ErrorRunnable;
import com.minecrafttas.lotas_light.savestates.SavestateIndexer.FailedSavestate;
import com.minecrafttas.lotas_light.savestates.SavestateIndexer.Savestate;
import com.minecrafttas.lotas_light.savestates.gui.SavestateDoneGui;
import com.minecrafttas.lotas_light.savestates.gui.SavestateGui;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;

public class SavestateCommand {

	public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
		//@formatter:off
		commandDispatcher
		.register(Commands.literal("savestate")
				.executes(SavestateCommand::info)
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
				.then(Commands.literal("rename")
						.then(Commands.argument("index", IntegerArgumentType.integer(0))
								.then(Commands.argument("name", StringArgumentType.greedyString())
										.executes(SavestateCommand::rename)
								)
						)
				)
		);
		//@formatter:on

	}

	private static int saveNew(CommandContext<CommandSourceStack> context) {
		int index = -1;
		Minecraft mc = Minecraft.getInstance();

		SavestateCallback doneSavingCallback = (paths -> {
			//@formatter:off
			mc.setScreen(
					new SavestateDoneGui(
							Component.translatable("gui.lotaslight.savestate.save.name"), 
							Component.translatable("gui.lotaslight.savestate.save.end", 
									Component.literal(paths.getSavestate().getName()).withStyle(ChatFormatting.YELLOW),
									Component.literal(Integer.toString(paths.getSavestate().getIndex())).withStyle(ChatFormatting.AQUA)
									).withStyle(ChatFormatting.GREEN)
							)
					);
			//@formatter:on
		});

		Minecraft.getInstance().execute(() -> {
			try {
				for (ServerLevel level : Minecraft.getInstance().getSingleplayerServer().getAllLevels()) {
					level.noSave = true;
				}
				setSavestateScreen();
				LoTASLight.savestateHandler.saveState(index, doneSavingCallback);
			} catch (Exception e) {
				sendFailure(context, e);
			}
		});
		return 0;
	}

	private static int saveIndex(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		Minecraft mc = Minecraft.getInstance();

		SavestateCallback doneSavingCallback = (paths -> {
			//@formatter:off
			mc.setScreen(
					new SavestateDoneGui(
							Component.translatable("gui.lotaslight.savestate.save.name"), 
							Component.translatable("gui.lotaslight.savestate.save.end", 
									Component.literal(paths.getSavestate().getName()).withStyle(ChatFormatting.YELLOW),
									Component.literal(Integer.toString(paths.getSavestate().getIndex())).withStyle(ChatFormatting.AQUA)
									).withStyle(ChatFormatting.GREEN)
							)
					);
			//@formatter:on
		});

		mc.execute(() -> {
			try {
				for (ServerLevel level : Minecraft.getInstance().getSingleplayerServer().getAllLevels()) {
					level.noSave = true;
				}
				setSavestateScreen();
				LoTASLight.savestateHandler.saveState(index, doneSavingCallback);
			} catch (Exception e) {
				sendFailure(context, e);
			}
		});
		return index;
	}

	private static int saveName(CommandContext<CommandSourceStack> context) {
		String name = context.getArgument("name", String.class);
		Minecraft mc = Minecraft.getInstance();

		SavestateCallback doneSavingCallback = (paths -> {
			//@formatter:off
			mc.setScreen(
					new SavestateDoneGui(
							Component.translatable("gui.lotaslight.savestate.save.name"), 
							Component.translatable("gui.lotaslight.savestate.save.end", 
									Component.literal(paths.getSavestate().getName()).withStyle(ChatFormatting.YELLOW),
									Component.literal(Integer.toString(paths.getSavestate().getIndex())).withStyle(ChatFormatting.AQUA)
									).withStyle(ChatFormatting.GREEN)
							)
					);
			//@formatter:on
		});

		mc.execute(() -> {
			try {
				for (ServerLevel level : Minecraft.getInstance().getSingleplayerServer().getAllLevels()) {
					level.noSave = true;
				}
				setSavestateScreen();
				LoTASLight.savestateHandler.saveState(name, doneSavingCallback);
			} catch (Exception e) {
				sendFailure(context, e);
			}
		});
		return 0;
	}

	private static int saveNameIndex(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		String name = context.getArgument("name", String.class);
		Minecraft mc = Minecraft.getInstance();

		SavestateCallback doneSavingCallback = (paths -> {
			//@formatter:off
			mc.setScreen(
					new SavestateDoneGui(
							Component.translatable("gui.lotaslight.savestate.save.name"), 
							Component.translatable("gui.lotaslight.savestate.save.end", 
									Component.literal(paths.getSavestate().getName()).withStyle(ChatFormatting.YELLOW),
									Component.literal(Integer.toString(paths.getSavestate().getIndex())).withStyle(ChatFormatting.AQUA)
									).withStyle(ChatFormatting.GREEN)
							)
					);
			//@formatter:on
		});

		mc.execute(() -> {
			try {
				for (ServerLevel level : Minecraft.getInstance().getSingleplayerServer().getAllLevels()) {
					level.noSave = true;
				}
				setSavestateScreen();
				LoTASLight.savestateHandler.saveState(index, name, doneSavingCallback);
			} catch (Exception e) {
				sendFailure(context, e);
			}
		});
		return 0;
	}

	private static int loadRecent(CommandContext<CommandSourceStack> context) {
		int index = -1;
		Minecraft mc = Minecraft.getInstance();

		SavestateCallback doneLoadingCallback = (paths -> {
			//@formatter:off
			mc.setScreen(
					new SavestateDoneGui(
							Component.translatable("gui.lotaslight.savestate.load.name"), 
							Component.translatable("gui.lotaslight.savestate.load.end", 
									Component.literal(paths.getSavestate().getName()).withStyle(ChatFormatting.YELLOW),
									Component.literal(Integer.toString(paths.getSavestate().getIndex())).withStyle(ChatFormatting.AQUA)
									).withStyle(ChatFormatting.GREEN)
							)
					);
			//@formatter:on
		});

		mc.execute(() -> {
			try {
				for (ServerLevel level : Minecraft.getInstance().getSingleplayerServer().getAllLevels()) {
					level.noSave = true;
				}
				setLoadstateScreen();
				LoTASLight.savestateHandler.loadState(index, doneLoadingCallback);
			} catch (Exception e) {
				sendFailure(context, e);
			}
		});

		return 0;
	}

	private static int loadIndex(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		Minecraft mc = Minecraft.getInstance();

		SavestateCallback doneLoadingCallback = (paths -> {
			//@formatter:off
			mc.setScreen(
					new SavestateDoneGui(
							Component.translatable("gui.lotaslight.savestate.load.name"), 
							Component.translatable("gui.lotaslight.savestate.load.end", 
									Component.literal(paths.getSavestate().getName()).withStyle(ChatFormatting.YELLOW),
									Component.literal(Integer.toString(paths.getSavestate().getIndex())).withStyle(ChatFormatting.AQUA)
									).withStyle(ChatFormatting.GREEN)
							)
					);
			//@formatter:on
		});

		mc.execute(() -> {
			try {
				for (ServerLevel level : Minecraft.getInstance().getSingleplayerServer().getAllLevels()) {
					level.noSave = true;
				}
				setLoadstateScreen();
				LoTASLight.savestateHandler.loadState(index, doneLoadingCallback);
			} catch (Exception e) {
				sendFailure(context, e);
			}
		});
		return index;
	}

	private static int delete(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		try {
			LoTASLight.savestateHandler.delete(index, (paths) -> {
				context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.savestate.delete", paths.getSavestate().getIndex()).withStyle(ChatFormatting.GREEN), true);
			});
		} catch (Exception e) {
			sendFailure(context, e);
		}
		return index;
	}

	private static int deleteMore(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		int indexTo = context.getArgument("indexTo", Integer.class);
		int count = (indexTo + 1) - index;

		if (count < 0) {
			context.getSource().sendFailure(Component.translatable("msg.lotaslight.savestate.deleteMore.error.negative", count));
			return -1;
		}

		String translationKey = "msg.lotaslight.savestate.deleteMore" + (count == 1 ? ".singular" : ".plural");

		//@formatter:off
		Component countComponent = Component.literal(Integer.toString(count)).withStyle(ChatFormatting.RED);
		
		Component confirmationComponent = ComponentUtils.wrapInSquareBrackets(Component.translatable("msg.lotaslight.savestate.deleteMore.clickable", true)
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

		SavestateCallback cb = (paths) -> {
			context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.savestate.delete", paths.getSavestate().getIndex()).withStyle(ChatFormatting.GREEN), true);
		};

		ErrorRunnable onErr = (exception) -> {
			sendFailure(context, exception);
		};

		LoTASLight.savestateHandler.delete(index, indexTo, cb, onErr);
		return index;
	}

	private static int reload(CommandContext<CommandSourceStack> context) {
		context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.savestate.reload").withStyle(ChatFormatting.GREEN), true);
		LoTASLight.savestateHandler.reload();
		return 0;
	}

	private static int info(CommandContext<CommandSourceStack> context) {
		List<Savestate> savestateList = LoTASLight.savestateHandler.getSavestateInfo();
		int currentIndex = LoTASLight.savestateHandler.getCurrentIndex();
		String format = I18n.get("msg.lotaslight.savestate.dateformat");

		context.getSource().sendSystemMessage(Component.literal(""));
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		for (Savestate savestate : savestateList) {

			String index = savestate.getIndex() == null ? "" : Integer.toString(savestate.getIndex());
			boolean isCurrentIndex = savestate.getIndex() == currentIndex;
			String name = savestate.getName() == null ? "" : savestate.getName();
			String date = savestate.getDate() == null ? "" : dateFormat.format(savestate.getDate());

			//@formatter:off
			UnaryOperator<Style> click = t -> 
							t.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
								String.format("/savestate load %s", index)));
			
			UnaryOperator<Style> hover = t -> 
							t.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								Component.translatable("msg.lotaslight.savestate.info.hover", 
								Component.literal(date).withStyle(ChatFormatting.GOLD),
								index
							).withStyle(ChatFormatting.GREEN)));
			
			Component msg = null;
					
			if(savestate instanceof FailedSavestate) {
				FailedSavestate failedSavestate = (FailedSavestate) savestate;
				msg = Component.translatable("%s: %s %s",
						Component.literal(index).withStyle(isCurrentIndex ? ChatFormatting.AQUA : ChatFormatting.BLUE), 
						Component.literal(name).withStyle(isCurrentIndex ? ChatFormatting.YELLOW : ChatFormatting.GOLD),
						Component.translatable("msg.lotaslight.savestates.info.error", failedSavestate.getError().getMessage())
					.withStyle(ChatFormatting.RED))
					.withStyle(t -> 
						t.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							Component.literal(date).withStyle(ChatFormatting.GOLD)
						)));
			} else {
				msg = Component.translatable("%s: %s",
						Component.literal(index).withStyle(isCurrentIndex ? ChatFormatting.AQUA : ChatFormatting.BLUE), 
						Component.literal(name).withStyle(isCurrentIndex ? ChatFormatting.YELLOW : ChatFormatting.GOLD))
					.withStyle(click)
					.withStyle(hover);
			}
		
			//@formatter:on
			context.getSource().sendSystemMessage(msg);
		}
		return 0;
	}

	private static int rename(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		String name = context.getArgument("name", String.class);

		SavestateCallback cb = (paths) -> {
			//@formatter:off
			context.getSource().sendSuccess(() -> Component.translatable("msg.lotaslight.savestate.rename",
						Component.literal(Integer.toString(paths.getSavestate().getIndex())).withStyle(ChatFormatting.AQUA),
						Component.literal(paths.getSavestate().getName()).withStyle(ChatFormatting.YELLOW)
					).withStyle(ChatFormatting.GREEN), true);
			//@formatter:on
		};

		try {
			LoTASLight.savestateHandler.rename(index, name, cb);
		} catch (Exception e) {
			sendFailure(context, e);
		}
		return 0;
	}

	private static void sendFailure(CommandContext<CommandSourceStack> context, Throwable e) {
		context.getSource().sendFailure(Component.literal(e.getMessage()));
		LoTASLight.LOGGER.catching(e);
		LoTASLight.savestateHandler.resetState();
	}

	private static void setSavestateScreen() {
		Minecraft.getInstance().setScreen(new SavestateGui(Component.translatable("gui.lotaslight.savestates.save.name"), Component.translatable("gui.lotaslight.savestates.save.start")));
	}

	private static void setLoadstateScreen() {
		Minecraft.getInstance().setScreen(new SavestateGui(Component.translatable("gui.lotaslight.savestates.load.name"), Component.translatable("gui.lotaslight.savestates.load.start")));
	}
}
