package com.ms.currency.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CurrencyExchange-Service")
public interface FeignClientCode {
	
	@GetMapping("/api/v1/from/{from}/to/{to}")
	
	CurrencyExchange retrieveExchangeValue(@PathVariable String from,
            @PathVariable String to);
	

}
