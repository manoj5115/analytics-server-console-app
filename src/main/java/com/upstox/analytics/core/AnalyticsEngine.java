package com.upstox.analytics.core;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.upstox.analytics.tasks.FsmTask;
import com.upstox.analytics.tasks.SubscriptionTask;
import com.upstox.analytics.tasks.TradeDataReaderTask;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AnalyticsEngine implements CommandLineRunner {

	private final Context context;

	public AnalyticsEngine(Context context) {
		this.context = context;
	}

	@Override
	public void run(String... args) throws Exception {
		this.startEngine();
		this.shutdownEngine();
	}

	private void shutdownEngine() {
		try {
			context.getEngineLatch().await();
		} catch (InterruptedException e) {
			log.error("Upstox Analytics Engine was shut down abruptly.", e);
			System.exit(1);
		}
		log.info("Upstox Analytics Engine was shut down gracefully.");
	}

	private void startEngine() {
		log.info("Starting Upstox Analytics Engine...");

		ExecutorService executorService = null;
		try {
			executorService = Executors.newFixedThreadPool(3);
			executorService.execute(new TradeDataReaderTask(context));
			executorService.execute(new FsmTask(context));
			executorService.execute(new SubscriptionTask(context));
		} finally {
			if (Objects.nonNull(executorService)) {
				executorService.shutdown();
			}
		}
	}

}
