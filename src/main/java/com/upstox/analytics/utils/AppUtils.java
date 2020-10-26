package com.upstox.analytics.utils;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upstox.analytics.core.Context;
import com.upstox.analytics.core.DataPipeline;
import com.upstox.analytics.pojo.OhlcNotifyEvent;
import com.upstox.analytics.pojo.Trade;
import com.upstox.analytics.pojo.UserSubscription;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppUtils {

	private static ObjectMapper objectMapper = new ObjectMapper();

	public static Trade convertJsonToTrade(String tradeDataLine) {
		Trade trade = null;
		try {
			trade = objectMapper.readValue(tradeDataLine, Trade.class);

		} catch (JsonMappingException e) {
			log.error("Error while Json Mapping - {}", tradeDataLine, e);
		} catch (JsonProcessingException e) {
			log.error("Error while Json processing- {}", tradeDataLine, e);
		}

		return trade;
	}

	public static String convertOHLCEventToJson(OhlcNotifyEvent event) {
		String ohlc = null;
		try {
			ohlc = objectMapper.writeValueAsString(event);

		} catch (JsonMappingException e) {
			log.error("Error while Json Mapping - {}", event, e);
		} catch (JsonProcessingException e) {
			log.error("Error while Json processing - ", event, e);
		}

		return ohlc;
	}

	public static UserSubscription convertJsonToSubscription(String userSubscriptionJson) {
		UserSubscription subscription = null;
		try {
			subscription = objectMapper.readValue(userSubscriptionJson, UserSubscription.class);

		} catch (JsonMappingException e) {
			log.error("Error while Json Mapping - {}", userSubscriptionJson, e);
		} catch (JsonProcessingException e) {
			log.error("Error while Json processing- {}", userSubscriptionJson, e);
		}

		return subscription;
	}

	public static void validateDataPipeline(DataPipeline pipeline) {
		if (Objects.isNull(pipeline)) {
			throw new RuntimeException("Please configure Data Pipeline first before starting the analytics process.");
		}
	}

	public static Instant getStartInstant(long timestamp) {
		return Instant.ofEpochSecond(0L, timestamp);
	}

	public static Instant getEndInstant(long timestamp, int interval) {
		return Instant.ofEpochSecond(0L, timestamp).plusSeconds(interval);
	}

	public static void downEngineLatch(Context context) {
		if (Objects.nonNull(context)) {
			context.getEngineLatch().countDown();
		}

	}

}
