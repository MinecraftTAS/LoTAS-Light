package com.minecrafttas.lotas_light.savestates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;

import com.minecrafttas.lotas_light.LoTASLight;
import com.minecrafttas.lotas_light.config.AbstractDataFile;

/**
 * Manages the savestates on the filesystem and assignes new indizes
 * 
 * @author Scribble
 */
public class SavestateIndexer {

	private final Logger logger;
	private final Path savestateBaseDirectory;
	private Path savesDir;
	private final String worldname;
	private final Path currentSavestateDir;
	private final LinkedHashMap<Integer, Savestate> savestateList;
	private Savestate currentSavestate;

	private static final Path savestateDatPath = Path.of("tas/savestate.xml");

	public SavestateIndexer(Logger logger, Path savesDir, Path savestateBaseDirectory, String worldname) {
		this.logger = logger;
		this.savestateBaseDirectory = savestateBaseDirectory;
		this.savesDir = savesDir;
		this.worldname = worldname;
		this.currentSavestateDir = savestateBaseDirectory.resolve(String.format("%s-Savestates", worldname));
		savestateList = new LinkedHashMap<>();
		createSavestateDir();
		currentSavestate = new Savestate(savesDir.resolve(worldname).resolve(savestateDatPath));
	}

	private void createSavestateDir() {
		try {
			Files.createDirectories(currentSavestateDir);
		} catch (IOException e) {
			logger.catching(e);
		}
	}

	public SavestatePaths createSavestate(int index) {
		return createSavestate(index, null);
	}

	public SavestatePaths createSavestate(int index, String name) {
		if (index < 0) {
			index = currentSavestate.getIndex() + 1;
		}

		if (name == null) {
			name = "Savestate #" + index;
		}

		currentSavestate.index = index;
		currentSavestate.name = name;
		currentSavestate.date = new Date();

		currentSavestate.saveToXML();

		savestateList.put(index, currentSavestate.clone());
		sortSavestateList();

		Path sourceDir = savesDir.resolve(worldname);
		Path targetDir = currentSavestateDir.resolve(worldname + index);

		return SavestatePaths.of(index, name, sourceDir, targetDir);
	}

	private void sortSavestateList() {
		LinkedHashMap<Integer, Savestate> copy = new LinkedHashMap<>();
		//@formatter:off
		savestateList.entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.forEach(entry -> copy.put(entry.getKey(), entry.getValue()));
		//@formatter:on
		savestateList.clear();
		savestateList.putAll(copy);
	}

	public void refresh() {
		logger.trace("Refreshing savestate indexes");
		Path savestateDir = savestateBaseDirectory.resolve(worldname);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				Stream<Path> stream = null;
				try {
					stream = Files.list(savestateDir); // Get a list of paths in the specified directory
				} catch (IOException e) {
					logger.catching(e);
					return;
				}

				//@formatter:off
				Set<Path> pathSet = stream
						.filter(file -> Files.isDirectory(file))
						.filter(file -> file.getFileName().startsWith(String.format("%s-Savestate", worldname)))
						.collect(Collectors.toSet());
				//@formatter:on

				stream.close();

				pathSet.forEach(path -> {

				});
			}
		});
		t.run();
	}

	public Set<Integer> getIndexList() {
		return savestateList.keySet();
	}

	public List<Savestate> getSavestateList(int amount) {
		List<Savestate> out = new LinkedList<>();
		if (amount <= 0) {
			savestateList.forEach((key, value) -> out.add(value));
			return out;
		}

		LinkedHashMap<Integer, Savestate> copy = new LinkedHashMap<>(savestateList);
		for (int i = 0; i < amount; i++) {
			Entry<Integer, Savestate> entry = copy.pollLastEntry();
			if (entry == null)
				break;
			out.addFirst(entry.getValue());
		}
		return out;
	}

	public class Savestate extends AbstractDataFile {

		private int index;
		private String name;
		private Date date;

		private Savestate(Path file) {
			super(LoTASLight.LOGGER, file, "Savestate", "Stores savestate related data");
		}

		private Savestate(Path file, Properties properties, int index, String name, Date date) {
			super(LoTASLight.LOGGER, file, "Savestate", "Stores savestate related data");
			this.index = index;
			this.name = name;
			this.date = date;
			this.properties = properties;
		}

		private enum Options {
			INDEX,
			NAME,
			DATE;

			@Override
			public String toString() {
				return super.toString().toLowerCase();
			}
		}

		public int getIndex() {
			return index;
		}

		public String getName() {
			return name;
		}

		public Date getDate() {
			return date;
		}

		@Override
		public void saveToXML() {
			properties.setProperty(Options.INDEX.toString(), Integer.toString(index));
			properties.setProperty(Options.NAME.toString(), name);
			properties.setProperty(Options.DATE.toString(), Long.toString(ChronoUnit.SECONDS.between(Instant.EPOCH, date.toInstant())));
			super.saveToXML();
		}

		@Override
		public void loadFromXML() {
			super.loadFromXML();
			try {
				this.index = Integer.parseInt(properties.getProperty(Options.INDEX.toString()));
			} catch (Exception e) {
				logger.error("Can't parse '{}' in {}", Options.INDEX.toString(), currentSavestateDir.resolve(savestateDatPath));
				logger.catching(e);
			}
			this.name = properties.getProperty(Options.NAME.toString());
			try {
				this.date = parseDate(properties.getProperty(Options.DATE.toString()));
			} catch (Exception e) {
				logger.error("Can't parse '{}' in {}", Options.DATE.toString(), currentSavestateDir.resolve(savestateDatPath));
				logger.catching(e);
			}
		}

		@Override
		protected Savestate clone() {
			return new Savestate(file, properties, index, name, date);
		}

		private static Date parseDate(String dateString) throws Exception {
			long unixTimestamp = Long.parseLong(dateString);
			return Date.from(Instant.ofEpochSecond(unixTimestamp));
		}
	}

	public static class SavestatePaths {
		private final int index;
		private final String name;
		private final Path sourceFolder;
		private final Path targetFolder;

		private SavestatePaths(int index, String name, Path sourceFolder, Path targetFolder) {
			this.index = index;
			this.name = name;
			this.sourceFolder = sourceFolder;
			this.targetFolder = targetFolder;
		}

		public int getIndex() {
			return index;
		}

		public String getName() {
			return name;
		}

		public Path getSourceFolder() {
			return sourceFolder;
		}

		public Path getTargetFolder() {
			return targetFolder;
		}

		public static SavestatePaths of(int index, String name, Path sourceFolder, Path targetFolder) {
			return new SavestatePaths(index, name, sourceFolder, targetFolder);
		}
	}
}
