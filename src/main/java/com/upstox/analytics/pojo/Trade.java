package com.upstox.analytics.pojo;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trade {

	@JsonProperty("sym")
	private String symbol;
	
	@JsonProperty("P")
	private BigDecimal price;
	
	@JsonProperty("Q")
	private BigDecimal quantity;
	
	@JsonProperty("TS2")
	private long timestamp;
	
}
