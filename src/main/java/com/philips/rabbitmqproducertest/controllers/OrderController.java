package com.philips.rabbitmqproducertest.controllers;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.philips.rabbitmqproducertest.dtos.Order;
import com.philips.rabbitmqproducertest.dtos.OrderStatus;
import com.philips.rabbitmqproducertest.services.OrderProducerService;

@RestController
@RequestMapping(path = "/orders")
public class OrderController {
	@Autowired
	private OrderProducerService service;
	
	@PostMapping
	public ResponseEntity<OrderStatus> receiveOrder(RequestEntity<Order> order) throws IOException, TimeoutException{
		OrderStatus orderStatus = service.processMessage(order.getBody());
		return ResponseEntity.ok(orderStatus);
	}

}
