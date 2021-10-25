package com.philips.rabbitmqproducertest.controllers;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.philips.rabbitmqproducertest.config.MessageProducerConfig;
import com.philips.rabbitmqproducertest.config.PoolConnections;
import com.rabbitmq.client.Channel;

@RestController
@RequestMapping(path = "/setup")
public class SetupController {
	
	@Value("${philips.rabbitmq.virtual-host}")
	private String[] virtualHost;
	
	@Autowired
	private PoolConnections connections;
	
	@PostMapping
	public ResponseEntity<String> setup() throws IOException, TimeoutException{
		for (String vhost : this.virtualHost) {
			try(Channel channel = connections.getConnection(vhost).createChannel()){
				channel.exchangeDeclare(MessageProducerConfig.ORDER_EXCHANGE_NAME, "direct", true);
				channel.queueDeclare(MessageProducerConfig.ORDER_QUEUE_NAME, true, false, false, null);
				channel.queueBind(MessageProducerConfig.ORDER_QUEUE_NAME, MessageProducerConfig.ORDER_EXCHANGE_NAME, MessageProducerConfig.ORDER_ROUTING_KEY);
			}
		}
		return ResponseEntity.ok("Setup successfully done!");
	}

}
