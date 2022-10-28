package com.demo.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "folder.watcher")
public class FileProcessingProps {
	private String sourceDir;
	private String completedDir;
	private String errorDir;
	
	private List<String> allowedFileFormate;
}
