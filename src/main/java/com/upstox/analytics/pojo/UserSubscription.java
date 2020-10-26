package com.upstox.analytics.pojo;

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
public class UserSubscription {

	@JsonProperty("symbol")
	private String symbol;
	
	@JsonProperty("event")
	private String event;
	
	@JsonProperty("interval")
	private int interval;
	
}
