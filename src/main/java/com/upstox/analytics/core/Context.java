package com.upstox.analytics.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.upstox.analytics.config.PropertiesConfig;
import com.upstox.analytics.pojo.UserSubscription;
import com.upstox.analytics.utils.AppUtils;
import com.upstox.analytics.utils.FileReader;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Getter
@Slf4j
public class Context {

	final private DataPipeline pipeline;

	final private PropertiesConfig propConfig;

	private Map<String, UserSubscription> subscription;
	
	final private CountDownLatch engineLatch = new CountDownLatch(3);

	public Context(DataPipeline pipeline, PropertiesConfig propConfig) {
		super();
		this.pipeline = pipeline;
		this.propConfig = propConfig;
		this.subscription = new HashMap<>();
		updateSubscriptionInContext();
	}

	private void updateSubscriptionInContext() {
		try (Stream<String> stream = FileReader.stream(propConfig.getSubscriptionFilePath())) {
			this.subscription = stream.map(AppUtils::convertJsonToSubscription).filter(Objects::nonNull).collect(
					Collectors.toMap(UserSubscription::getSymbol, Function.identity(), (e1, e2) -> e2, HashMap::new));

		} catch (IOException e) {
			log.error("Error while reading user subscription data", e);
		}
	}

}
