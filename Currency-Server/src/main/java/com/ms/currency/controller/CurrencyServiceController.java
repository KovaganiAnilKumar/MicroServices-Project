package com.ms.currency.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.currency.entity.CurrencyConversion;
import com.ms.currency.feign.CurrencyExchange;
import com.ms.currency.feign.FeignClientCode;
import com.ms.currency.repository.CurrencyServiceRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.Data;
import lombok.NoArgsConstructor;

@RequestMapping("/api/v1")
@RestController
@Data
@NoArgsConstructor
public class CurrencyServiceController {
	@Autowired
	CurrencyServiceRepository currencyServiceRepository;
	
	@Autowired
	FeignClientCode feignClientCode;
	
	@GetMapping("/from/{from}/to/{to}/quantity/{quantity}")
	@CircuitBreaker(name = "currencyService", fallbackMethod = "currencyExchangeFallback")
	public ResponseEntity<CurrencyConversion> calculateCurrencyConversion(@PathVariable String from,
																		  @PathVariable String to,
																		  @PathVariable int quantity)
	{
//		double conversionMultiple = 87.3;
		
		CurrencyExchange currencyExchange = feignClientCode.retrieveExchangeValue(from, to);
		
		double conversionMultiple = currencyExchange.getConversionMultiple();
		
		double totalCalculateAmount = conversionMultiple*quantity;
		
		CurrencyConversion currencyConversion = new CurrencyConversion();
		
		currencyConversion.setFrom(from);
		currencyConversion.setTo(to);
		currencyConversion.setQuantity(quantity);
		currencyConversion.setConvertionMultiple(conversionMultiple);
		currencyConversion.setTotalCalculateAmount(totalCalculateAmount);
		currencyConversion.setDate(LocalDateTime.now());
		
		currencyServiceRepository.save(currencyConversion);
		
		return ResponseEntity.status(HttpStatus.OK)
							 .header("value", "amount calculated")
							 .body(currencyConversion);
	}
	
	
	
	
	
	
	public ResponseEntity<CurrencyConversion> currencyExchangeFallback(
	        String from, String to, int quantity, Throwable ex) {

	    // Log the exception if needed
	    System.out.println("Fallback triggered for from = " + from + ", to = " + to + " due to " + ex.getMessage());

	    // Provide a default response or a custom fallback response
	    CurrencyExchange fallbackResponse = new CurrencyExchange();
	    fallbackResponse.setFromCurrency(from);
	    fallbackResponse.setToCurrency(to);
	    fallbackResponse.setConversionMultiple(0.0);  // Default value if the service is down
	    
	    CurrencyConversion currencyConversion = new CurrencyConversion();
	    currencyConversion.setFrom(from);
	    currencyConversion.setTo(to);
	    currencyConversion.setQuantity(quantity);
	    currencyConversion.setConvertionMultiple(fallbackResponse.getConversionMultiple());
	    currencyConversion.setTotalCalculateAmount(quantity * fallbackResponse.getConversionMultiple());
	    currencyConversion.setDate(LocalDateTime.now());
	    
	    // You can optionally log or save the failed conversion if needed
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .header("value", "Fallback: currency conversion failed due to service unavailability")
	            .body(currencyConversion);
	}
	  
	
	
	
	
}
