package com.philips.rabbitmqproducertest.config;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@FunctionalInterface
public interface MessagesQueueingCommand<E> {
	public void sendMessage(E entityMessage) throws IOException, TimeoutException;
}
