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

import com.rabbitmq.client.ConnectionFactory;
@Configuration
public class ConnectioFactoryNodes implements InitializingBean{

	private Map<String,ConnectionFactory> connectionsFactories = new TreeMap<String, ConnectionFactory>();
	

	
	@Value("${philips.rabbitmq.hosts}")
	private String[] rabbitMQHosts;
	
	@Value("${philips.rabbitmq.virtual-host}")
	private String virtualHost;
	
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
			
			for (String rabbitMQHost : rabbitMQHosts) {
				String[] hostAndPort = rabbitMQHost.split(":");
				String host = hostAndPort[0];
				int port = StringUtils.hasText(hostAndPort[1])?5672:Integer.parseInt(hostAndPort[1]);
				ConnectionFactory connectionFactory =createConnectionFactory(host,port); 
				this.connectionsFactories.put(host, connectionFactory);
			}
		}
	}
	private ConnectionFactory createConnectionFactory(String host, int port) throws FileNotFoundException, GeneralSecurityException, IOException {
		ConnectionFactory connectionFactoryTemp = new ConnectionFactory();
		connectionFactoryTemp.setHost(host);
		connectionFactoryTemp.setPort(port);
		connectionFactoryTemp.setUsername(this.rabbitMQUsername);;
		connectionFactoryTemp.setPassword(this.rabbitMQPassword);
		connectionFactoryTemp.setVirtualHost(this.virtualHost);
		connectionFactoryTemp.setAutomaticRecoveryEnabled(true);
		connectionFactoryTemp.setConnectionTimeout(this.connectionTimeout);
		connectionFactoryTemp.setHandshakeTimeout(this.connectionTimeout);
		if(StringUtils.hasText(this.sslAlgorithm)) {
			SSLContext sslContext = getSSLContext(host);
			connectionFactoryTemp.useSslProtocol(sslContext);
			connectionFactoryTemp.setSocketFactory(sslContext.getSocketFactory());
		}
		return connectionFactoryTemp;
		
	}
	private SSLContext getSSLContext(String host) throws GeneralSecurityException, FileNotFoundException, IOException {
        KeyStore tks = KeyStore.getInstance(this.trustStoreType);
        tks.load(new FileInputStream(this.trustStoreLocation), this.trustStorePassword.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(this.sslAlgorithm);
        tmf.init(tks);
        SecureRandom secureRandom = SecureRandom.getInstance(this.sslAlgorithm);
 
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(this.sslAlgorithm);
        
        SSLContext sslContext = SSLContext.getInstance(this.sslAlgorithm);
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);
        
        List<SNIServerName> sniHostNames = new ArrayList<SNIServerName>();
        sniHostNames.add(new SNIHostName(host));
        sslContext.getDefaultSSLParameters().setServerNames(sniHostNames);
        return sslContext;
	}
	private String message(String property) {
		return "The property ["+property+"] cannot be null";
	}
	

	public Set<Entry<String,ConnectionFactory>> getNodeConnectionsFactories(){
		return this.connectionsFactories.entrySet();
		
	}
}
