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
		log.info("Reading data from file: {}", file.getPath());
		Stream<String> stream = Files.lines(Paths.get(file.getPath()), StandardCharsets.UTF_8);
		return stream;
	}

}
