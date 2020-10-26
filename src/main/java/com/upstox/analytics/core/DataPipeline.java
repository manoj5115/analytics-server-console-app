package com.upstox.analytics.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.stereotype.Component;

import com.upstox.analytics.pojo.Bar;
import com.upstox.analytics.pojo.Trade;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class DataPipeline {
	
	private BlockingQueue<Trade> tradeQueue;
		
	private BlockingQueue<Bar> ohlcEventDataQueue;
	
	private volatile boolean tradeStreamClosed;
	
	private volatile boolean tradeProcessed;
	
	public DataPipeline() {
		tradeQueue = new LinkedBlockingQueue<>();
		ohlcEventDataQueue = new LinkedBlockingQueue<>();
	}

}
