package com.upstox.analytics.tasks;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

import com.upstox.analytics.core.Context;
import com.upstox.analytics.core.DataPipeline;
import com.upstox.analytics.pojo.Bar;
import com.upstox.analytics.pojo.OhlcNotifyEvent;
import com.upstox.analytics.utils.AppUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubscriptionTask implements Runnable {

	private Context context;
	private DataPipeline pipeline;
	private BlockingQueue<Bar> ohlcEventDataQueue;
	private long totalEventsPublished;

	public SubscriptionTask(Context context) {
		super();
		AppUtils.validateDataPipeline(context.getPipeline());
		this.context = context;
		this.pipeline = context.getPipeline();
		this.ohlcEventDataQueue = context.getPipeline().getOhlcEventDataQueue();
	}

	@Override
	public void run() {
		while (moreEventsToPublish()) {
			Bar bar = getNextEventBatchFromQueue();
			publishEventsToClientBasedOnSubscription(bar);
			totalEventsPublished += bar.getOhlcEvent().size();
			log.debug("Total OHLC events count so far: " + totalEventsPublished);
		}

		log.info("Exiting Subscription thread");
		AppUtils.downEngineLatch(context);
	}

	private void publishEventsToClientBasedOnSubscription(Bar bar) {
		for (OhlcNotifyEvent e : bar.getOhlcEvent()) {
			log.info(AppUtils.convertOHLCEventToJson(e));
		}
		log.info("-----------------------------------------------------------------------------------------");
	}

	private Bar getNextEventBatchFromQueue() {
		Bar events = null;
		try {
			events = ohlcEventDataQueue.take();
		} catch (InterruptedException e) {
			log.error("Error while getting events from the queue", e);
		}
		return events;
	}

	private boolean moreEventsToPublish() {
		while (true) {
			Bar events = ohlcEventDataQueue.peek();
			if (Objects.nonNull(events)) {
				return true;
			} else if (pipeline.isTradeProcessed()) {
				return false;
			} else {
				continue;
			}
		}
	}

}
