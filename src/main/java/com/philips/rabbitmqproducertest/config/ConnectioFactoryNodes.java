package com.philips.rabbitmqproducertest.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.philips.rabbitmqproducertest.services.exceptions.OrderException;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.ConnectionFactory;
@Configuration
public class ConnectioFactoryNodes implements InitializingBean{

	private Map<String,VhostConnectiosFactories> connectionsFactories = new TreeMap<>();
	

	
	@Value("${philips.rabbitmq.hosts}")
	private String rabbitMQHosts;
	
	@Value("${philips.rabbitmq.virtual-host}")
	private String[] virtualHost;
	
	@Value("${philips.rabbitmq.username}")
	private String rabbitMQUsername;
	
	@Value("${philips.rabbitmq.password}")
	private String rabbitMQPassword;
	
	@Value("${philips.rabbitmq.ssl.algorithm:}")
	private String sslAlgorithm;
	
	@Value("${philips.rabbitmq.ssl.trust-store-location:}")
	private String trustStoreLocation;
	
	@Value("${philips.rabbitmq.ssl.trust-store-type:}")
	private String trustStoreType;
	
	@Value("${philips.rabbitmq.ssl.trust-store-password:}")
	private String trustStorePassword;
	
	@Value("${philips.rabbitmq.ssl.sni.host.name:}")
	private String sniHostName;

	@Value("${philips.rabbitmq.reply-timeout:5000}")
	private int replyTimeout;
	
	@Value("${philips.rabbitmq.consume-timeout:10000}")
	private int consumeTimeout;
	
	@Value("${philips.rabbitmq.connection-timeout:10000}")
	private int connectionTimeout;
	
	public void afterPropertiesSet() throws Exception {
		if(this.connectionsFactories.isEmpty() ) {
			Assert.notNull(this.rabbitMQHosts,message("philips.rabbitmq.hosts"));
			Assert.notNull(this.virtualHost,message("philips.rabbitmq.virtual-host"));
			Assert.notNull(this.rabbitMQUsername,message("philips.rabbitmq.username"));
			Assert.notNull(this.rabbitMQPassword,message("philips.rabbitmq.password"));
			Address[] rabbitMQNodes = Address.parseAddresses(rabbitMQHosts);
			for (String vhost : this.virtualHost) {
				VhostConnectiosFactories vhostConnectiosFactoriesTemp = new VhostConnectiosFactories(vhost);
				vhostConnectiosFactoriesTemp.creatConnectionsFactories(rabbitMQNodes);
				this.connectionsFactories.put(vhost, vhostConnectiosFactoriesTemp);
			}
		}
	}

	private String message(String property) {
		return "The property ["+property+"] cannot be null";
	}
	

	public Set<Entry<String,ConnectionFactory>> getVirtualHostNodeConnectionsFactories(String vHost){
		if(this.connectionsFactories.containsKey(vHost)) {
			return this.connectionsFactories.get(vHost).getConnectionsFactories();
		}
		throw new OrderException("There is no restaurant ["+vHost+"]");
		
	}
	
	private class VhostConnectiosFactories{
		String virtualHostName;
		private Map<String,ConnectionFactory> connectionsFactories = new TreeMap<String, ConnectionFactory>();
		
		VhostConnectiosFactories(String virtualHostName){
			this.virtualHostName=virtualHostName;
		}
		void creatConnectionsFactories(Address[] adresses) throws FileNotFoundException, GeneralSecurityException, IOException{
			for(Address adsTemp : adresses) {
				ConnectionFactory connectionFactoryTemp = new ConnectionFactory();
				connectionFactoryTemp.setHost(adsTemp.getHost());
				connectionFactoryTemp.setPort(adsTemp.getPort());
				connectionFactoryTemp.setUsername(rabbitMQUsername);;
				connectionFactoryTemp.setPassword(rabbitMQPassword);
				connectionFactoryTemp.setVirtualHost(virtualHostName);
				connectionFactoryTemp.setAutomaticRecoveryEnabled(true);
				connectionFactoryTemp.setConnectionTimeout(connectionTimeout);
				connectionFactoryTemp.setHandshakeTimeout(connectionTimeout);
				if(StringUtils.hasText(sslAlgorithm)) {
					SSLContext sslContext = getSSLContext(adsTemp.getHost());
					connectionFactoryTemp.useSslProtocol(sslContext);
					connectionFactoryTemp.setSocketFactory(sslContext.getSocketFactory());
				}	
				connectionsFactories.put(adsTemp.getHost(), connectionFactoryTemp);
			}
		}
		private SSLContext getSSLContext(String host) throws GeneralSecurityException, FileNotFoundException, IOException {
	        KeyStore tks = KeyStore.getInstance(trustStoreType);
	        tks.load(new FileInputStream(trustStoreLocation), trustStorePassword.toCharArray());
	        TrustManagerFactory tmf = TrustManagerFactory.getInstance(sslAlgorithm);
	        tmf.init(tks);
	        SecureRandom secureRandom = SecureRandom.getInstance(sslAlgorithm);
	        KeyManagerFactory kmf = KeyManagerFactory.getInstance(sslAlgorithm);
	        SSLContext sslContext = SSLContext.getInstance(sslAlgorithm);
	        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);
	        List<SNIServerName> sniHostNames = new ArrayList<SNIServerName>();
	        sniHostNames.add(new SNIHostName(host));
	        sslContext.getDefaultSSLParameters().setServerNames(sniHostNames);
	        return sslContext;
		}
		Set<Entry<String,ConnectionFactory>>getConnectionsFactories(){
			return this.connectionsFactories.entrySet();
		}
		
	}
}
