package com.ms.currencyexchange.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ms.currencyexchange.entity.CurrencyExchange;
import com.ms.currencyexchange.repository.CurrencyExchangeRepository;

@RestController
@RequestMapping("/api/v1")
public class CurrencyExchangeController {
	@Autowired
	CurrencyExchangeRepository currencyExchangeRepository;
	
	@GetMapping("/from/{from}/to/{to}")
	public ResponseEntity<CurrencyExchange> retrieveExchangeValue(@PathVariable String from,
												                  @PathVariable String to)
	{
		Optional<CurrencyExchange> optionalCurrencyExchange = currencyExchangeRepository.findByFromCurrencyAndToCurrency(from,to);
		
		if(optionalCurrencyExchange.isPresent())
		{
			CurrencyExchange currencyExchange = optionalCurrencyExchange.get();
			return ResponseEntity.status(HttpStatus.OK)
								 .header("status", "Data retrieved successfully")
								 .body(currencyExchange);
		}
		else
		{
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
								 .header("status", "Data is not present")
								 .body(null);
		}
	}

	
}
