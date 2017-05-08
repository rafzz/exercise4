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

	private final String queueName;
	private Connection connection;
	private Session session;
	Destination destination;
	MessageProducer producer;
	ConnectionFactory connectionFactory;

	public JmsSender(final String queueName) {
		this.queueName = queueName;
		connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
	}

	private void connect() throws JMSException {

		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}



	public void sendTextToQueue(String text,int mode) {
		try {

			connect();
			destination = session.createQueue(queueName);
			producer = session.createProducer(destination);
			connection.start();
			TextMessage message = session.createTextMessage(text);
			producer.setDeliveryMode(mode);
			producer.send(message);
			session.close();
			connection.close();

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	


}
