package com.upstox.analytics.tasks;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

import com.upstox.analytics.config.PropertiesConfig;
import com.upstox.analytics.core.Context;
import com.upstox.analytics.core.DataPipeline;
import com.upstox.analytics.pojo.Trade;
import com.upstox.analytics.utils.AppUtils;
import com.upstox.analytics.utils.FileReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TradeDataReaderTask implements Runnable {

	private Context context;
	private DataPipeline pipeline;
	private BlockingQueue<Trade> tradeQueue;
	private TradeDataReaderTask _self;

	private PropertiesConfig propConfig;

	public TradeDataReaderTask(Context context) {
		super();
		this.propConfig = context.getPropConfig();
		this.context = context;
		AppUtils.validateDataPipeline(context.getPipeline());
		this.pipeline = context.getPipeline();
		this.tradeQueue = context.getPipeline().getTradeQueue();
		this._self = this;
	}

	@Override
	public void run() {
		try (Stream<String> stream = FileReader.stream(propConfig.getTradeFilePath())) {
			stream.map(AppUtils::convertJsonToTrade).filter(Objects::nonNull).forEach(_self::putInTradeQueue);

		} catch (IOException e) {
			log.error("Error while reading Trades data", e);

		} finally {
			log.info("Closing trades data stream.");
			pipeline.setTradeStreamClosed(true);
		}
		log.info("End of Trade data stream. Exiting TradeDataReader thread.");
		AppUtils.downEngineLatch(context);
	}

	private void putInTradeQueue(Trade trade) {
		try {
			tradeQueue.put(trade);
		} catch (InterruptedException e) {
			log.error("Error while putting trade in the queue : {}", trade, e);
		}
	}
}
