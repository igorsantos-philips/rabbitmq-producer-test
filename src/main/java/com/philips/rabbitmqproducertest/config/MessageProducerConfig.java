package com.philips.rabbitmqproducertest.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageProducerConfig {
	public static final String ORDER_EXCHANGE_NAME = "order.direct";
	public static final String ORDER_ROUTING_KEY = "received";
	public static final String ORDER_QUEUE_NAME = "order.received";
	
//	@Bean
//	public ConnectioFactoryNodes connectioFactoryNodes(ConnectioFactoryNodes factory) {
//		return factory;
//	}
}
