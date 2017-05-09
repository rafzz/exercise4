package wdsr.exercise4;

import java.util.List;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import wdsr.exercise4.receiver.JmsQueueReceiver;

public class Main {

	private static final Logger log = LogManager.getLogger(Main.class);

	public static void main(String[] args) {

		JmsQueueReceiver queuereceiver = new JmsQueueReceiver("RAFZZ.QUEUE");

		try {
			queuereceiver.createSession();
			
			List<String> messages = queuereceiver.getMessage();
			
			for (String message : messages) {
				log.info("message: " + message);
			}
			
			log.info("consumed messages: " + messages.size());

			
		} catch (JMSException e) {
			log.error(e.getMessage());
		}

	}

}
