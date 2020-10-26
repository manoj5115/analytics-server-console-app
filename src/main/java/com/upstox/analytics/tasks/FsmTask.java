package com.upstox.analytics.tasks;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.upstox.analytics.core.Context;
import com.upstox.analytics.core.DataPipeline;
import com.upstox.analytics.pojo.Bar;
import com.upstox.analytics.pojo.OhlcNotifyEvent;
import com.upstox.analytics.pojo.Trade;
import com.upstox.analytics.pojo.UserSubscription;
import com.upstox.analytics.utils.AppUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FsmTask implements Runnable {

	private Context context;
	private DataPipeline pipeline;
	private BlockingQueue<Trade> tradeQueue;
	private BlockingQueue<Bar> ohlcEventDataQueue;

	public FsmTask(Context context) {
		super();
		AppUtils.validateDataPipeline(context.getPipeline());
		this.context = context;
		this.pipeline = context.getPipeline();
		this.tradeQueue = context.getPipeline().getTradeQueue();
		this.ohlcEventDataQueue = context.getPipeline().getOhlcEventDataQueue();

	}

	@Override
	public void run() {
		Map<String, List<Bar>> symbolMap = createSymbolMap();
		while (isTradeDataAvailabe()) {
			Trade curTrade = getNextTradeFromQueue();
			if (isSymbolNotSubscribed(symbolMap, curTrade)) {
				continue;
			}

			List<Bar> barList = symbolMap.get(curTrade.getSymbol());
			if (CollectionUtils.isEmpty(barList)) {
				Bar b = new Bar();
				b.setId(1);
				b.setStart(AppUtils.getStartInstant(curTrade.getTimestamp()));
				b.setEnd(AppUtils.getEndInstant(curTrade.getTimestamp(),
						getBarInervalFromSubscription(curTrade.getSymbol())));
				b.setTrades(new ArrayList<>());
				b.getTrades().add(curTrade);
				barList.add(b);

			} else {
				Bar latestBar = barList.get(barList.size() - 1);
				if (AppUtils.getStartInstant(curTrade.getTimestamp()).isBefore(latestBar.getEnd())) {
					latestBar.getTrades().add(curTrade);
				} else {
					addEmptyBars(barList, curTrade, latestBar);
					processCurBars(symbolMap);

					latestBar = barList.get(barList.size() - 1);
					barList.clear();
					Bar b = new Bar();
					b.setId(latestBar.getId() + 1);
					b.setStart(latestBar.getEnd().plusNanos(1L));
					b.setEnd(latestBar.getEnd().plusSeconds(15));
					b.setTrades(new ArrayList<>());
					b.getTrades().add(curTrade);
					barList.add(b);
				}

			}
		}
		processCurBars(symbolMap);

		pipeline.setTradeProcessed(true);
		log.info("Exiting FSM thread");
		AppUtils.downEngineLatch(context);
	}

	private int getBarInervalFromSubscription(String symbol) {
		return Optional.ofNullable(context.getSubscription().get(symbol).getInterval())
				.orElse(context.getPropConfig().getFallbackBarInterval());
	}

	private boolean isSymbolNotSubscribed(Map<String, List<Bar>> symbolMap, Trade curTrade) {

		return !symbolMap.containsKey(curTrade.getSymbol());
	}

	private void processCurBars(Map<String, List<Bar>> symbolMap) {
		for (String symbol : symbolMap.keySet()) {
			List<Bar> barList = symbolMap.get(symbol);
			for (Bar b : barList) {
				List<Trade> tradeList = b.getTrades();
				List<OhlcNotifyEvent> ohlcEventsList = new ArrayList<>();

				OhlcNotifyEvent ohlcs = OhlcNotifyEvent.builder().event("ohlc_notify").symbol(symbol).barNum(b.getId())
						.build();
				ohlcEventsList.add(ohlcs);

				if (CollectionUtils.isNotEmpty(tradeList)) {
					BigDecimal agrVolume = BigDecimal.ZERO;
					BigDecimal high = tradeList.get(0).getPrice();
					BigDecimal low = tradeList.get(0).getPrice();
					BigDecimal open = tradeList.get(0).getPrice();
					BigDecimal close = BigDecimal.ZERO;
					for (int i = 0; i < tradeList.size(); i++) {
						Trade t = tradeList.get(i);
						if (t.getPrice().compareTo(high) > 0)
							high = t.getPrice();
						else if (t.getPrice().compareTo(low) < 0)
							low = t.getPrice();

						agrVolume = agrVolume.add(t.getQuantity());
						if (i == tradeList.size() - 1)
							close = t.getPrice();

						OhlcNotifyEvent ohlc = OhlcNotifyEvent.builder().event("ohlc_notify").symbol(symbol)
								.barNum(b.getId()).open(open).close(close).high(high).low(low).volume(agrVolume)
								.build();

						ohlcEventsList.add(ohlc);
					}
				}
				b.setOhlcEvent(ohlcEventsList);
			}

			publishOhlcEventsInBar(barList);
		}

	}

	private void publishOhlcEventsInBar(List<Bar> barList) {
		for (Bar bar : barList) {
			try {
				ohlcEventDataQueue.put(bar);
			} catch (InterruptedException e) {
				log.error("Error while putting ohlc event in the queue", e);
			}
		}
	}

	private void addEmptyBars(List<Bar> barList, Trade curTrade, Bar latestBar) {
		Instant tradeInstant = AppUtils.getStartInstant(curTrade.getTimestamp());
		Instant barInstant = latestBar.getEnd();
		Instant newBarInstant = barInstant.plusSeconds(15);

		while (tradeInstant.isAfter(newBarInstant)) {
			Bar b = new Bar();
			b.setId(latestBar.getId() + 1);
			b.setStart(latestBar.getEnd().plusNanos(1L));
			b.setEnd(newBarInstant);
			b.setTrades(new ArrayList<>());
			barList.add(b);

			latestBar = b;
			newBarInstant = newBarInstant.plusSeconds(15);
		}
	}

	private Map<String, List<Bar>> createSymbolMap() {
		Map<String, UserSubscription> subscription = context.getSubscription();
		Map<String, List<Bar>> symbolMap = subscription.keySet().stream()
				.collect(Collectors.toMap(Function.identity(), k -> new ArrayList<>()));
		return symbolMap;
	}

	private Trade getNextTradeFromQueue() {
		Trade trade = null;
		try {
			trade = tradeQueue.take();
		} catch (InterruptedException e) {
			log.error("Error while getting trade from the queue", e);
		}
		return trade;
	}

	private boolean isTradeDataAvailabe() {
		while (true) {
			Trade trade = tradeQueue.peek();
			if (Objects.nonNull(trade)) {
				return true;
			} else if (pipeline.isTradeStreamClosed()) {
				return false;
			} else {
				continue;
			}
		}
	}
}
