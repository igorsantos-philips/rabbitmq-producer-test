package com.philips.rabbitmqproducertest.config;


import static com.philips.rabbitmqproducertest.config.MessageProducerConfig.ORDER_EXCHANGE_NAME;
import static com.philips.rabbitmqproducertest.config.MessageProducerConfig.ORDER_ROUTING_KEY;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.philips.rabbitmqproducertest.dtos.Order;
import com.rabbitmq.client.Channel;

@Component
@Qualifier("orderQueueCommanderImpl")
public class OrderQueueCommanderImpl implements MessagesQueueingCommand<Order>{
	@Autowired
	private PoolConnections connectins;
	
	private ObjectMapper om = new ObjectMapper();
	@Override
	public void sendMessage(Order message) throws IOException, TimeoutException {
		RabbitMQMessageBasicProperties basicProperties= new RabbitMQMessageBasicProperties();
		try(Channel channel = connectins.getConnection(message.getDemandedRestaurantName()).createChannel()){
			channel.basicPublish(ORDER_EXCHANGE_NAME, ORDER_ROUTING_KEY, false, basicProperties, om.writeValueAsBytes(message));
		}
	}

}
