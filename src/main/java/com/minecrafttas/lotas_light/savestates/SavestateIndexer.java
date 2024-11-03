package com.minecrafttas.lotas_light.savestates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;

import com.minecrafttas.lotas_light.LoTASLight;
import com.minecrafttas.lotas_light.config.AbstractDataFile;
import com.minecrafttas.lotas_light.savestates.exceptions.LoadstateException;
import com.minecrafttas.lotas_light.savestates.exceptions.SavestateDeleteException;
import com.minecrafttas.lotas_light.savestates.exceptions.SavestateException;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.phys.Vec3;

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

		Path savestateDat = savesDir.resolve(worldname).resolve(savestateDatPath);
		if (Files.exists(savestateDat)) {
			currentSavestate = new Savestate(savestateDat);
			currentSavestate.loadFromXML();
		} else {
			currentSavestate = new Savestate(savestateDat, 0, null, null, null);
		}
		reload();
	}

	private void createSavestateDir() {
		try {
			Files.createDirectories(currentSavestateDir);
		} catch (IOException e) {
			logger.catching(e);
		}
	}

	public SavestatePaths createSavestate(int index, Vec3 motion) {
		return createSavestate(index, null, true, motion);
	}

	public SavestatePaths createSavestate(int index, String name, boolean changeIndex, Vec3 motion) {
		logger.trace("Creating savestate in indexer");
		if (index < 0) {
			index = currentSavestate.getIndex() + 1;
		}

		if (name == null) {
			name = "Savestate #" + index;
		}

		int savedIndex = index;

		currentSavestate.index = index;
		currentSavestate.name = name;
		currentSavestate.date = new Date();

		currentSavestate.saveToXML();

		if (!changeIndex)
			currentSavestate.index = savedIndex;

		savestateList.put(index, currentSavestate.clone());
		sortSavestateList();

		Path sourceDir = savesDir.resolve(worldname);
		Path targetDir = currentSavestateDir.resolve(worldname + index);

		return SavestatePaths.of(currentSavestate.clone(), sourceDir, targetDir);
	}

	public SavestatePaths loadSavestate(int index, boolean changeIndex) throws Exception {
		logger.trace("Loading savestate in indexer");
		if (index < 0) {
			index = currentSavestate.getIndex();

			if (savestateList.containsKey(index)) {
				index = findLatestIndex(index);
			}
		}

		Savestate savestateToLoad = savestateList.get(index);

		if (savestateToLoad == null) {
			throw new LoadstateException(I18n.get("msg.lotaslight.savestate.error.noexist", index));
		}

		int savedIndex = currentSavestate.index;
		this.currentSavestate = savestateToLoad.clone();

		Path sourceDir = currentSavestateDir.resolve(worldname + currentSavestate.index);
		Path targetDir = savesDir.resolve(worldname);

		if (!Files.exists(sourceDir)) {
			Path missingFile = savesDir.relativize(sourceDir);
			throw new LoadstateException(I18n.get("msg.lotaslight.savestate.error.filenoexist", missingFile));
		}

		SavestatePaths out = SavestatePaths.of(currentSavestate.clone(), sourceDir, targetDir);

		if (!changeIndex)
			currentSavestate.index = savedIndex;

		return out;
	}

	public SavestatePaths renameSavestate(int index, String name) throws Exception {
		Savestate savestateToRename = savestateList.get(index);

		if (savestateToRename == null) {
			throw new SavestateException(I18n.get("msg.lotaslight.savestate.error.noexist", index));
		} else if (savestateToRename instanceof FailedSavestate) {
			throw new SavestateException(I18n.get("msg.lotaslight.savestate.rename.error"));
		}

		if (name.isEmpty()) {
			name = "Savestate #" + index;
		}

		savestateToRename.name = name;
		savestateToRename.saveToXML();

		return SavestatePaths.of(savestateToRename, null, null);
	}

	public SavestatePaths deleteSavestate(int index) throws Exception {
		logger.trace("Deleting savestate {}", index);

		if (!savestateList.containsKey(index)) {
			throw new SavestateDeleteException(I18n.get("msg.lotaslight.savestate.error.noexist", index));
		}

		Savestate toDelete = savestateList.get(index);
		Path targetDir = currentSavestateDir.resolve(worldname + toDelete.index);
		SavestatePaths out = SavestatePaths.of(toDelete, null, targetDir);

		savestateList.remove(index);

		if (!savestateList.containsKey(currentSavestate.index)) {
			currentSavestate.index = findLatestIndex(currentSavestate.index);
		}

		return out;
	}

	public void deleteMultipleSavestates(int from, int to, DeletionRunnable onDelete, ErrorRunnable onError) {
		for (int i = from; i <= to; i++) {
			try {
				onDelete.run(deleteSavestate(i));
			} catch (Exception e) {
				onError.run(e);
			}
		}
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

	public void reload() {
		logger.trace("Reloading savestate indexes");
		savestateList.clear();
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				Stream<Path> stream = null;
				try {
					stream = Files.list(currentSavestateDir); // Get a list of paths in the specified directory
				} catch (IOException e) {
					logger.catching(e);
					return;
				}

				//@formatter:off
				Set<Path> pathSet = stream
						.filter(file -> Files.isDirectory(file))
						.filter(file -> file.getFileName().toString().startsWith(worldname))
						.collect(Collectors.toSet());
				//@formatter:on

				stream.close();

				Pattern pattern = Pattern.compile(worldname + "(\\d+)$");

				pathSet.forEach(path -> {
					Path savestateDat = path.resolve(savestateDatPath);

					Savestate savestate = null;
					if (!Files.exists(savestateDat)) {
						String filename = path.getFileName().toString();
						Matcher matcher = pattern.matcher(filename);
						int backupIndex = -1;
						if (matcher.find()) {
							backupIndex = Integer.parseInt(matcher.group(1));
						}

						logger.warn("Savestate {} does not contain a valid savestate.xml, skipping", backupIndex);
						Throwable error = new SavestateException("Savestate.xml data file not found in " + savestateBaseDirectory.relativize(savestateDat));
						savestate = new FailedSavestate(path, backupIndex, null, null, error);
					} else {
						savestate = new Savestate(savestateDat);
						savestate.loadFromXML();
					}
					savestateList.put(savestate.getIndex(), savestate.clone());
				});
				sortSavestateList();
				try {
					currentSavestate.index = findLatestIndex(currentSavestate.index);
				} catch (Exception e) {
					logger.catching(e);
				}
			}
		}, "Savestate Reload");
		t.run();
	}

	public Set<Integer> getIndexList() {
		return savestateList.keySet();
	}

	public List<Savestate> getSavestateList() {
		return getSavestateList(currentSavestate.index);
	}

	public List<Savestate> getSavestateList(int center) {
		return getSavestateList(center, 10);
	}

	public List<Savestate> getSavestateList(int center, int amount) {
		List<Savestate> out = new LinkedList<>();
		if (center < 0) {
			savestateList.forEach((key, value) -> out.add(value));
			return out;
		}

		LinkedHashMap<Integer, Savestate> copy = new LinkedHashMap<>(savestateList);
		int delta = ((int) amount / 2);

		for (int i = center - delta; i <= center + delta; i++) {
			Savestate entry = copy.get(i);
			if (entry != null)
				out.add(entry);
		}
		return out;
	}

	public int findLatestIndex(int start) throws Exception {
		if (savestateList.containsKey(start))
			return start;

		for (int i = start; i >= 0; i--) {
			if (savestateList.containsKey(i) && !(savestateList.get(i) instanceof FailedSavestate)) {
				return i;
			}
		}
		return 0;
	}

	public Savestate getCurrentSavestate() {
		return currentSavestate;
	}

	public class Savestate extends AbstractDataFile {

		protected Integer index;
		protected String name;
		protected Date date;
		protected Vec3 motion;

		private Savestate(Path file) {
			this(file, -1, null, null, null);
		}

		private Savestate(Path file, Integer index, String name, Date date, Vec3 motion) {
			super(LoTASLight.LOGGER, file, "Savestate", "Stores savestate related data");
			this.index = index;
			this.name = name;
			this.date = date;
			this.motion = motion;
		}

		private Savestate(Path file, Properties properties, Integer index, String name, Date date, Vec3 motion) {
			this(file, index, name, date, motion);
			this.properties = properties;
		}

		private enum Options {
			INDEX,
			NAME,
			DATE,
			MOTION_X,
			MOTION_Y,
			MOTION_Z;

			@Override
			public String toString() {
				return super.toString().toLowerCase();
			}
		}

		public Integer getIndex() {
			return index;
		}

		public String getName() {
			return name;
		}

		public Date getDate() {
			return date;
		}

		public Vec3 getMotion() {
			return motion;
		}

		@Override
		public void saveToXML() {
			if (index != null)
				properties.setProperty(Options.INDEX.toString(), Integer.toString(index));
			if (name != null)
				properties.setProperty(Options.NAME.toString(), name);
			if (date != null)
				properties.setProperty(Options.DATE.toString(), Long.toString(ChronoUnit.SECONDS.between(Instant.EPOCH, date.toInstant())));
			if (motion != null) {
				properties.setProperty(Options.MOTION_X.toString(), Double.toString(motion.x));
				properties.setProperty(Options.MOTION_Y.toString(), Double.toString(motion.y));
				properties.setProperty(Options.MOTION_Z.toString(), Double.toString(motion.z));
			}
			super.saveToXML();
		}

		@Override
		public void loadFromXML() {
			super.loadFromXML();
			try {
				String loadedIndex = properties.getProperty(Options.INDEX.toString());
				if (loadedIndex != null)
					this.index = Integer.parseInt(loadedIndex);
			} catch (Exception e) {
				logger.error("Can't parse '{}' in {}", Options.INDEX.toString(), currentSavestateDir.resolve(savestateDatPath));
				logger.catching(e);
			}
			this.name = properties.getProperty(Options.NAME.toString());
			try {
				String loadedDate = properties.getProperty(Options.DATE.toString());
				if (loadedDate != null)
					this.date = parseDate(loadedDate);
			} catch (Exception e) {
				logger.error("Can't parse '{}' in {}", Options.DATE.toString(), currentSavestateDir.resolve(savestateDatPath));
				logger.catching(e);
			}

			String x = properties.getProperty(Options.MOTION_X.toString());
			String y = properties.getProperty(Options.MOTION_Y.toString());
			String z = properties.getProperty(Options.MOTION_Z.toString());
			if (x != null && y != null && z != null) {
				try {
					this.motion = new Vec3(Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
				} catch (Exception e) {
					logger.error("Can't parse '{}' in {}", Options.DATE.toString(), currentSavestateDir.resolve(savestateDatPath));
					logger.catching(e);
				}
			}
		}

		@Override
		protected Savestate clone() {
			return new Savestate(file, properties, index, name, date, motion);
		}

		private static Date parseDate(String dateString) throws Exception {
			long unixTimestamp = Long.parseLong(dateString);
			return Date.from(Instant.ofEpochSecond(unixTimestamp));
		}
	}

	public class FailedSavestate extends Savestate {

		private final Throwable t;

		public FailedSavestate(Path file, Throwable t) {
			this(file, null, null, null, t);
		}

		public FailedSavestate(Path file, Integer index, String name, Date date, Throwable t) {
			super(file, index, name, date, null);
			this.t = t;
		}

		public FailedSavestate(Path file, Properties properties, Integer index, String name, Date date, Throwable t) {
			super(file, index, name, date, null);
			this.t = t;
		}

		public Throwable getError() {
			return t;
		}

		@Override
		public void saveToXML() {
		}

		@Override
		public void save() {
		}

		@Override
		public void loadFromXML() {
		}

		@Override
		public void load() {
		}

		@Override
		protected FailedSavestate clone() {
			return new FailedSavestate(file, properties, index, name, date, t);
		}
	}

	public static class SavestatePaths {
		private final Savestate savestate;
		private final Path sourceFolder;
		private final Path targetFolder;

		private SavestatePaths(Savestate savestate, Path sourceFolder, Path targetFolder) {
			this.savestate = savestate;
			this.sourceFolder = sourceFolder;
			this.targetFolder = targetFolder;
		}

		public Savestate getSavestate() {
			return savestate;
		}

		public Path getSourceFolder() {
			return sourceFolder;
		}

		public Path getTargetFolder() {
			return targetFolder;
		}

		public static SavestatePaths of(Savestate savestate, Path sourceFolder, Path targetFolder) {
			return new SavestatePaths(savestate, sourceFolder, targetFolder);
		}
	}

	public static void copyFolder(Path src, Path dest) {
		try {
			Files.walk(src).forEach(s -> {
				try {
					Path d = dest.resolve(src.relativize(s));
					if (Files.isDirectory(s)) {
						if (!Files.exists(d))
							Files.createDirectory(d);
						return;
					}
					Files.copy(s, d, StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteFolder(Path toDelete) {
		try {
			Files.walk(toDelete).forEach(s -> {
				if (toDelete.equals(s))
					return;
				if (Files.isDirectory(s)) {
					deleteFolder(s);
				} else {
					try {
						Files.delete(s);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			Files.delete(toDelete);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FunctionalInterface
	public interface DeletionRunnable {
		public void run(SavestatePaths paths);
	}

	@FunctionalInterface
	public interface ErrorRunnable {
		public void run(Exception e);
	}
}
