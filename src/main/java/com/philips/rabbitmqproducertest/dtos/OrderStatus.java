package com.philips.rabbitmqproducertest.dtos;

import com.philips.rabbitmqproducertest.services.OrderStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class OrderStatus {
	private Order order;
	private OrderStatusEnum status;
	private String message;

}
