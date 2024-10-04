package com.minecrafttas.lotas_light.savestates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
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
	private final ArrayList<Savestate> savestateList;
	private Savestate currentSavestate;

	private static final Path savestateDatPath = Path.of("tas/savestate.dat");

	public SavestateIndexer(Logger logger, Path savesDir, Path savestateBaseDirectory, String worldname) {
		this.logger = logger;
		this.savestateBaseDirectory = savestateBaseDirectory;
		this.savesDir = savesDir;
		this.worldname = worldname;
		this.currentSavestateDir = savestateBaseDirectory.resolve(String.format("%s-Savestates", worldname));
		savestateList = new ArrayList<>();
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

	public Savestate createMostRecentSavestate() {
		return null;
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

	public class Savestate extends AbstractDataFile {

		protected Savestate(Path file) {
			super(LoTASLight.LOGGER, file, "Savestate", "Stores savestate related data");
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

		private int index;
		private Path sourceDir;
		private Path targetDir;
		private String name;
		private Date date;

		public int getIndex() {
			return index;
		}

		public Path getSourceDir() {
			return sourceDir;
		}

		public Path getTargetDir() {
			return targetDir;
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

		private Date parseDate(String dateString) throws Exception {
			long unixTimestamp = Long.parseLong(dateString);
			return Date.from(Instant.ofEpochSecond(unixTimestamp));
		}
	}
}
