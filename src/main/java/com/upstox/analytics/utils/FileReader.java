package com.upstox.analytics.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.util.ResourceUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileReader {

	public static Stream<String> stream(String fileName) throws IOException {
		File file = ResourceUtils.getFile(fileName);
		String path = file.getAbsolutePath();
		log.info("Reading data from file: {}", path);
		return Files.lines(Paths.get(path), StandardCharsets.UTF_8);
	}

}
