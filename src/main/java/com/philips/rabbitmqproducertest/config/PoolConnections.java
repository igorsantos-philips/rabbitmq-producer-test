package com.philips.rabbitmqproducertest.config;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@Component
public class PoolConnections {
	@Autowired
	private ConnectioFactoryNodes connectioFactoryNodes;
	
	private Map<String, Connection> connections = new TreeMap<String, Connection>();
	
	public Connection getConnection( ) throws IOException, TimeoutException {
		for(Entry<String, Connection> entry: this.connections.entrySet()) {
			Connection connection = entry.getValue();
			if (connection.isOpen())
				return connection;
		}
		createConnections();
		return getConnection( );
	}
	private void createConnections() throws IOException, TimeoutException {
		Set<Entry<String, ConnectionFactory>> entrySet =this.connectioFactoryNodes.getNodeConnectionsFactories(); 
		for (Entry<String, ConnectionFactory> entry :entrySet ) {
		
			ConnectionFactory connectionFactory = entry.getValue();
			String node = entry.getKey();
			this.connections.put(node, connectionFactory.newConnection());
		}
	}
	public void closePool() {
		connections.forEach((host,connection)->{
			try {
				connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	public void closeConnectionNode(String node) throws IOException {
		connections.get(node).close();
	}
}
