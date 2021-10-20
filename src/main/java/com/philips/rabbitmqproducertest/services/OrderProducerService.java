package com.philips.rabbitmqproducertest.services;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.philips.rabbitmqproducertest.config.OrderQueueCommanderImpl;
import com.philips.rabbitmqproducertest.dtos.Order;
import com.philips.rabbitmqproducertest.dtos.OrderStatus;

@Service
public class OrderProducerService {
	@Autowired
	private OrderQueueCommanderImpl template;
	
	public OrderStatus processMessage(Order order) throws IOException, TimeoutException {
		UUID uuid= UUID.randomUUID();
		order.setOrderId(uuid.toString());
		template.sendMessage(order);
		return new OrderStatus(order, OrderStatusEnum.SENT, "Your order have've been sent to restaurant");
		
	}

}
