package com.upstox.analytics.pojo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "o", "h", "l", "c", "volume", "event", "symbol", "bar_num" })
@Builder
public class OhlcNotifyEvent {

	@JsonProperty("event")
	private String event;
	
	@JsonProperty("symbol")
	private String symbol;
	
	@JsonProperty("bar_num")
	private long barNum;
	
	@JsonProperty("o")
	private BigDecimal open;
	
	@JsonProperty("h")
	private BigDecimal high;
	
	@JsonProperty("l")
	private BigDecimal low;
	
	@JsonProperty("c")
	private BigDecimal close;
	
	@JsonProperty("volume")
	private BigDecimal volume;
	
}
