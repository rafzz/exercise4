package wdsr.exercise4.sender;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsSender {

	private final String topicName;
	private Connection connection;
	private Session session;
	Destination destination;
	MessageProducer producer;
	ConnectionFactory connectionFactory;

	public JmsSender(final String queueName) {
		this.topicName = queueName;
		connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		try {
		connect();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private void connect() throws JMSException {

		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}



	public void sendTextToTopic(String text,int mode) {
		try {

			//connect();
			destination = session.createTopic(topicName);
			producer = session.createProducer(destination);
			//connection.start();
			TextMessage message = session.createTextMessage(text);
			producer.setDeliveryMode(mode);
			producer.send(message);
			//session.close();
			//connection.close();

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		try {
			session.close();
			connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}
	
	


}
