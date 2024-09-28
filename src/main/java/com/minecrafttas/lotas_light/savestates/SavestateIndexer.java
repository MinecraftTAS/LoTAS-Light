package com.minecrafttas.lotas_light.savestates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;

/**
 * Manages the savestates on the filesystem and assignes new indizes
 * 
 * @author Scribble
 */
public class SavestateIndexer {

	private final Logger logger;
	private final Path savestateBaseDirectory;
	private final String worldname;
	private final Map<Integer, SavestateDataFile> savestateIndex;

	private static final Path savestateDatPath = Path.of("tas/savestate.dat");

	public SavestateIndexer(Logger logger, Path savestateBaseDirectory, String worldname) {
		this.logger = logger;
		this.savestateBaseDirectory = savestateBaseDirectory;
		this.worldname = worldname;
		savestateIndex = new HashMap<>();
	}

	private void createSavestateDir() {
		Path savestateDir = savestateBaseDirectory.resolve(worldname);
		try {
			Files.createDirectories(savestateDir);
		} catch (IOException e) {
			logger.catching(e);
		}
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

				pathSet.forEach(path -> {

				});
			}
		});
		t.run();
	}
}
