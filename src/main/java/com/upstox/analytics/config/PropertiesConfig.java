package com.upstox.analytics.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class PropertiesConfig {

	@Value("${TRADE_FILE_PATH}")
	private String tradeFilePath;
	
	@Value("${USER_SUBS_FILE_PATH}")
	private String subscriptionFilePath;
	
	@Value("${FALLBACK_BAR_INTERVAL}")
	private int fallbackBarInterval;
}
