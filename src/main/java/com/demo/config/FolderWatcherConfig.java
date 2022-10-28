package com.demo.config;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.demo.service.FileProcessingService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FolderWatcherConfig {

	private final WatchService watcher;

	private final FileProcessingService fileProcessingService;

	public FolderWatcherConfig(FileProcessingProps props, FileProcessingService fileService) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.fileProcessingService = fileService;
		Path logDir = Paths.get(props.getSourceDir());
		logDir.register(watcher, ENTRY_MODIFY);
		
	}

	@Scheduled(initialDelay = 500, fixedDelay = 1000)
	public void configFileWatcher() throws InterruptedException, IOException {

		WatchKey key = watcher.take();
		Path watchPath = (Path) key.watchable();
		for (WatchEvent<?> event : key.pollEvents()) {
			WatchEvent.Kind<?> kind = event.kind();
			if (ENTRY_MODIFY.equals(kind)) {
				try {					
					Path fullPath = watchPath.resolve(event.context().toString());
					File file = fullPath.toFile();					
					if(checkFileModificationCompleted(file)) {
						log.info("Start processing the File -> {} ", event.context());
						fileProcessingService.processFile(file);
						log.info("Processing Completed -> {} ", event.context());
					}
				} catch (Exception e) {

				}
			}
		}
		key.reset();
	}

	public boolean checkFileModificationCompleted(File file) throws IOException {
		try (FileChannel channel = new RandomAccessFile(file, "rw").getChannel()) {
			channel.close();
			return true;
		} catch (Exception e) {
		}
		return false;
	}

}
