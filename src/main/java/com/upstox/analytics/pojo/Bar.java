package com.upstox.analytics.pojo;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bar {

	private int id;
	
	private Instant start;
	
	private Instant end;
	
	private List<Trade> trades;
	
	private List<OhlcNotifyEvent> ohlcEvent;
		
}
