package wdsr.exercise4.receiver;

import java.math.BigDecimal;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wdsr.exercise4.Order;
import wdsr.exercise4.PriceAlert;
import wdsr.exercise4.VolumeAlert;
import wdsr.exercise4.sender.JmsSender;

/**
 * TODO Complete this class so that it consumes messages from the given queue
 * and invokes the registered callback when an alert is received.
 * 
 * Assume the ActiveMQ broker is running on tcp://localhost:62616
 */
public class JmsQueueReceiver {
	private static final Logger log = LoggerFactory.getLogger(JmsQueueReceiver.class);

	private final String queueName;

	private final ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private MessageConsumer messageConsumer;

	/**
	 * Creates this object
	 * 
	 * @param queueName
	 *            Name of the queue to consume messages from.
	 */
	public JmsQueueReceiver(final String queueName) {
		this.queueName = queueName;
		connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:62616");
		connectionFactory.setTrustAllPackages(true);
	}

	/**
	 * Registers the provided callback. The callback will be invoked when a
	 * price or volume alert is consumed from the queue.
	 * 
	 * @param alertService
	 *            Callback to be registered.
	 */
	public void registerCallback(AlertService alertService) {
		
		try {
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(queueName);
			messageConsumer = session.createConsumer(destination);

			messageConsumer.setMessageListener(new MessageListener() {

				@Override
				public void onMessage(Message message) {

					try {
						switch (message.getJMSType().toString()) {
						case "PriceAlert":
							priceMessage(alertService, message);
							break;
						case "VolumeAlert":
							volumeMessage(alertService, message);
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			});

		} catch (JMSException e) {

			e.printStackTrace();
		}
	}


	private void priceMessage(AlertService alertService, Message message) {
		if (message instanceof ObjectMessage) {
			try {
				ObjectMessage objectMessage = (ObjectMessage) message;
				PriceAlert priceAlert = (PriceAlert) objectMessage.getObject();
				
				alertService.processPriceAlert(priceAlert);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (message instanceof TextMessage) {
			try {
				TextMessage textMessage = (TextMessage) message;
				PriceAlert priceAlert = (PriceAlert) processPriceAlertTextMessage(textMessage);
				
				alertService.processPriceAlert(priceAlert);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void volumeMessage(AlertService alertService, Message message) {
		if (message instanceof ObjectMessage) {
			try {
				ObjectMessage objectMessage = (ObjectMessage) message;
				VolumeAlert volumeAlert = (VolumeAlert) objectMessage.getObject();
				
				alertService.processVolumeAlert(volumeAlert);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (message instanceof TextMessage) {
			try {
				TextMessage textMessage = (TextMessage) message;
				VolumeAlert volumeAlert = (VolumeAlert)  processVolumeAlertTextMessage(textMessage);
				
				alertService.processVolumeAlert(volumeAlert);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private PriceAlert processPriceAlertTextMessage(TextMessage priceAlert) throws JMSException, NumberFormatException {

		String priceAlertText = priceAlert.getText();
		String[] priceAlertSplit = priceAlertText.split("=|\\r?\\n");
		PriceAlert priceAlertObject = null;
		if (priceAlertSplit.length == 6) {
			priceAlertObject = new PriceAlert(Long.parseLong(priceAlertSplit[1].trim()), priceAlertSplit[3].trim(),
					BigDecimal.valueOf(Long.parseLong(priceAlertSplit[5].trim())));
		}
		return priceAlertObject;

	}

	private VolumeAlert processVolumeAlertTextMessage(TextMessage volumeAlert)
			throws JMSException, NumberFormatException {

		String volumeAlertText = volumeAlert.getText();
		String[] volumeAlertSplit = volumeAlertText.split("=|\\r?\\n");
		VolumeAlert volumeAlertObject = null;
		if (volumeAlertSplit.length == 6) {
			volumeAlertObject = new VolumeAlert(Long.parseLong(volumeAlertSplit[1].trim()), volumeAlertSplit[3].trim(),
					Long.parseLong(volumeAlertSplit[5].trim()));
		}
		return volumeAlertObject;

	}

	/**
	 * Deregisters all consumers and closes the connection to JMS broker.
	 */
	public void shutdown() {
		try {

			messageConsumer.setMessageListener(null);
			session.close();
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// TODO
	// This object should start consuming messages when registerCallback method
	// is invoked.

	// This object should consume two types of messages:
	// 1. Price alert - identified by header JMSType=PriceAlert - should invoke
	// AlertService::processPriceAlert
	// 2. Volume alert - identified by header JMSType=VolumeAlert - should
	// invoke AlertService::processVolumeAlert
	// Use different message listeners for and a JMS selector

	// Each alert can come as either an ObjectMessage (with payload being an
	// instance of PriceAlert or VolumeAlert class)
	// or as a TextMessage.
	// Text for PriceAlert looks as follows:
	// Timestamp=<long value>
	// Stock=<String value>
	// Price=<long value>
	// Text for VolumeAlert looks as follows:
	// Timestamp=<long value>
	// Stock=<String value>
	// Volume=<long value>

	// When shutdown() method is invoked on this object it should remove the
	// listeners and close open connection to the broker.
}
