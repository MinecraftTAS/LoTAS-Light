package com.minecrafttas.lotas_light.command;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.UnaryOperator;

import com.minecrafttas.lotas_light.LoTASLight;
import com.minecrafttas.lotas_light.LoTASLightClient;
import com.minecrafttas.lotas_light.config.Configuration.ConfigOptions;
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
				.then(Commands.argument("index", IntegerArgumentType.integer())
						.executes(SavestateCommand::infoIndex)
						.then(Commands.argument("amount", IntegerArgumentType.integer(0))
							.executes(SavestateCommand::infoIndexAmount)
						)
				)
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
				.then(Commands.literal("info")
						.executes(SavestateCommand::info)
						.then(Commands.argument("index", IntegerArgumentType.integer(0))
								.executes(SavestateCommand::infoIndex)
								.then(Commands.argument("amount", IntegerArgumentType.integer(0))
										.executes(SavestateCommand::infoIndexAmount)
								)
						)
						.then(Commands.literal("all")
								.executes(SavestateCommand::infoAll)
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

		if (name.equals("0")) {
			context.getSource().sendFailure(Component.translatable("msg.lotaslight.savestate.save.error2"));
			return -1;
		}

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
		showInfo(context);
		return 0;
	}

	private static int infoAll(CommandContext<CommandSourceStack> context) {
		showInfo(context, -1, 0);
		return 0;
	}

	private static int infoIndex(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		showInfo(context, index, null);
		return 0;
	}

	private static int infoIndexAmount(CommandContext<CommandSourceStack> context) {
		int index = context.getArgument("index", Integer.class);
		int amount = context.getArgument("amount", Integer.class);
		showInfo(context, index, amount);
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

	private static void showInfo(CommandContext<CommandSourceStack> context) {
		showInfo(context, null, null);
	}

	private static void showInfo(CommandContext<CommandSourceStack> context, Integer indexToDisplay, Integer amount) {

		int currentIndex = LoTASLight.savestateHandler.getCurrentIndex();
		if (indexToDisplay == null) {
			indexToDisplay = currentIndex;
		}
		if (amount == null) {
			amount = 10;
		}

		context.getSource().sendSystemMessage(Component.literal("")); // Print an empty line
		String format = I18n.get("msg.lotaslight.savestate.dateformat");
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);

		List<Savestate> savestateList = LoTASLight.savestateHandler.getSavestateInfo(indexToDisplay, amount);
		for (Savestate savestate : savestateList) {

			String index = savestate.getIndex() == null ? "" : Integer.toString(savestate.getIndex());
			boolean isCurrentIndex = savestate.getIndex() == currentIndex;
			String name = savestate.getName() == null ? "" : savestate.getName();
			String date = savestate.getDate() == null ? "" : dateFormat.format(savestate.getDate());

			ChatFormatting indexColor = isCurrentIndex ? ChatFormatting.AQUA : ChatFormatting.BLUE;
			ChatFormatting nameColor = isCurrentIndex ? ChatFormatting.WHITE : ChatFormatting.GRAY;
			ChatFormatting dateColor = isCurrentIndex ? ChatFormatting.AQUA : ChatFormatting.DARK_AQUA;
			ChatFormatting saveColor = isCurrentIndex ? ChatFormatting.LIGHT_PURPLE : ChatFormatting.DARK_PURPLE;
			ChatFormatting deleteColor = isCurrentIndex ? ChatFormatting.RED : ChatFormatting.DARK_RED;
			ChatFormatting renameColor = isCurrentIndex ? ChatFormatting.YELLOW : ChatFormatting.GOLD;
			ChatFormatting loadColor = isCurrentIndex ? ChatFormatting.GREEN : ChatFormatting.DARK_GREEN;

			//@formatter:off
			UnaryOperator<Style> hover = t -> 
							t.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(date).withStyle(dateColor)));
			
			Component msg = null;
					
			if(savestate instanceof FailedSavestate) {
				FailedSavestate failedSavestate = (FailedSavestate) savestate;
				msg = Component.translatable("%s: %s %s",
						Component.literal(index).withStyle(indexColor), 
						Component.literal(name).withStyle(nameColor),
						Component.translatable("msg.lotaslight.savestates.info.error", failedSavestate.getError().getMessage())
					.withStyle(ChatFormatting.RED))
					.withStyle(t -> 
						t.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							Component.literal(date).withStyle(ChatFormatting.GOLD)
						)));
			} else {
				if(!LoTASLightClient.config.getBoolean(ConfigOptions.SAVESTATE_SHOW_CONTROLS)) {
					msg = Component.translatable("%s: %s", 
							Component.literal(index).withStyle(indexColor), 
							Component.literal(name).withStyle(nameColor))
							.withStyle(hover);
					
				}
				else {
					Component saveComponent = Component.translatable("msg.lotaslight.savestate.save.clickable").withStyle(saveColor)
							.withStyle(t->
								t.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/savestate save %s", index)))
							)
							.withStyle(t->
								t.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("msg.lotaslight.savestate.save.hover", name).withStyle(saveColor)))
							);
					
					Component deleteComponent = Component.translatable("msg.lotaslight.savestate.delete.clickable").withStyle(deleteColor)
							.withStyle(t->
								t.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/savestate delete %s", index)))
							)
							.withStyle(t->
								t.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("msg.lotaslight.savestate.delete.hover", name).withStyle(deleteColor)))
							);
					
					Component renameComponent = Component.translatable("msg.lotaslight.savestate.rename.clickable").withStyle(renameColor)
							.withStyle(t->
								t.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/savestate rename %s ", index)))
							)
							.withStyle(t->
								t.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("msg.lotaslight.savestate.rename.hover", name).withStyle(renameColor)))
							);
					
					Component loadComponent = Component.translatable("msg.lotaslight.savestate.load.clickable").withStyle(loadColor)
							.withStyle(t->
								t.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/savestate load %s", index)))
							)
							.withStyle(t->
								t.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("msg.lotaslight.savestate.load.hover", name).withStyle(loadColor)))
							);
					
					msg = Component.translatable("%s: %s     %s %s %s %s",
							Component.literal(index).withStyle(indexColor), 
							Component.literal(name).withStyle(nameColor),
							wrap(saveComponent, nameColor),
							wrap(deleteComponent, nameColor),
							wrap(renameComponent, nameColor),
							wrap(loadComponent, nameColor)
						).withStyle(hover);
				}
			}
			
			//@formatter:on
			context.getSource().sendSystemMessage(msg);
		}
	}

	private static Component wrap(Component component, ChatFormatting color) {
		return ComponentUtils.wrapInSquareBrackets(component).withStyle(color);
	}
}
