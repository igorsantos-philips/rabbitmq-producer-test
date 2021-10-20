package com.philips.rabbitmqproducertest.config;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.philips.rabbitmqproducertest.services.exceptions.OrderException;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@Component
public class PoolConnections {
	@Autowired
	private ConnectioFactoryNodes connectioFactoryNodes;
	
	private Map<String, VirtualHostConnections> virtualHostConnectionsMap = new TreeMap<>();
	
	public Connection getConnection(String vHost) throws IOException, TimeoutException {
		if(!virtualHostConnectionsMap.containsKey(vHost)) {
			createConnections(vHost);
		}
		Set<Entry<String, Connection>> setConnections = virtualHostConnectionsMap.get(vHost).getVirtualHostConnections().entrySet();
		for(Entry<String, Connection> entry: setConnections) {
			Connection connection = entry.getValue();
			if (connection.isOpen())
				return connection;
		}
		createConnections(vHost);
		return getConnection(vHost);
	}
	private void createConnections(String vHost) throws IOException, TimeoutException {
		Set<Entry<String, ConnectionFactory>> entrySet = this.connectioFactoryNodes.getVirtualHostNodeConnectionsFactories(vHost); 
		VirtualHostConnections vhConnectionsTemp = new VirtualHostConnections(vHost);
		vhConnectionsTemp.createConnections(entrySet);
		this.virtualHostConnectionsMap.put(vHost, vhConnectionsTemp);
	}
	public void closePool(String vHost) {
		if(this.virtualHostConnectionsMap.containsKey(vHost)) {
			this.virtualHostConnectionsMap.get(vHost).closeConnections();
		};
	}
	
	
	private class VirtualHostConnections{
		private String virtualHost;
		private Map<String, Connection> virtualHostConnections = new TreeMap<>();
		
		VirtualHostConnections(String virtualHost){
			this.virtualHost = virtualHost;
		}
		public void closeConnections(){
			try {
				for(Entry<String, Connection>entry: virtualHostConnections.entrySet()) {
					entry.getValue().close();
				}
				
			}catch(IOException e) {
				throw new OrderException(e);
			}
		}
		void createConnections(Set<Entry<String, ConnectionFactory>> entrySetConnectionsFactories) throws IOException, TimeoutException {
			for(Entry<String, ConnectionFactory>entry: entrySetConnectionsFactories) {
				this.virtualHostConnections.put(entry.getKey(), entry.getValue().newConnection());
			}
		}
		Map<String, Connection> getVirtualHostConnections(){
			return this.virtualHostConnections;
		}
		public String getVirtualHost() {
			return virtualHost;
		}
		
	}
}
