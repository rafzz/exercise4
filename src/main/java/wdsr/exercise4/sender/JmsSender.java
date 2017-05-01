package wdsr.exercise4.sender;

import java.math.BigDecimal;
import java.util.Map;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wdsr.exercise4.Order;

public class JmsSender {
	private static final Logger log = LoggerFactory.getLogger(JmsSender.class);
	
	private final String queueName;
	private final String topicName;
	
	private final ActiveMQConnectionFactory connectionFactory;

	public JmsSender(final String queueName, final String topicName) {
		this.queueName = queueName;
		this.topicName = topicName;
		connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:62616");
	}
	
	


	/**
	 * This method creates an Order message with the given parameters and sends it as an ObjectMessage to the queue.
	 * @param orderId ID of the product
	 * @param product Name of the product
	 * @param price Price of the product
	 */
	public void sendOrderToQueue(final int orderId, final String product, final BigDecimal price) {
		
		try {
			Connection connection = connectionFactory.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(queueName);
			MessageProducer messageProducer = session.createProducer(destination);
			
			ObjectMessage message = session.createObjectMessage( new Order(orderId,product,price) );
			
			message.setJMSType("Order");
			message.setStringProperty("WDSR-System", "OrderProcessor");
			
			messageProducer.send(message);
			
			session.close();
			connection.close();
			
			
		} catch (JMSException e) {
			
			e.printStackTrace();
		}
		
	}

	/**
	 * This method sends the given String to the queue as a TextMessage.
	 * @param text String to be sent
	 */
	public void sendTextToQueue(String text) {
		
		try {
			Connection connection = connectionFactory.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(queueName);
			MessageProducer messageProducer = session.createProducer(destination);
			
			TextMessage message = session.createTextMessage( text );
			
			message.setJMSType("Order");
			message.setStringProperty("WDSR-System", "OrderProcessor");
			
			messageProducer.send(message);
			
			session.close();
			connection.close();
			
			
		} catch (JMSException e) {
			
			e.printStackTrace();
		}
	}

	/**
	 * Sends key-value pairs from the given map to the topic as a MapMessage.
	 * @param map Map of key-value pairs to be sent.
	 */
	public void sendMapToTopic(Map<String, String> map) {
		try {
			Connection connection = connectionFactory.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createTopic(topicName);
			MessageProducer messageProducer = session.createProducer(destination);
			
			MapMessage message = session.createMapMessage();
			message.setJMSType("Order");
			message.setStringProperty("WDSR-System", "OrderProcessor");
			
			for (Map.Entry<String, String> entry : map.entrySet()) {
				message.setString(entry.getKey(), entry.getValue());
			}
			
			messageProducer.send(message);
			
			session.close();
			connection.close();
			
			
		} catch (JMSException e) {
			
			e.printStackTrace();
		}
	}
}
