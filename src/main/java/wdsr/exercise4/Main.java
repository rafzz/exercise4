package wdsr.exercise4;

import java.util.List;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wdsr.exercise4.receiver.JmsTopicReceiver;


public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);
    
	 public static void main(String[] args) {
		 JmsTopicReceiver service = new JmsTopicReceiver("RAFZZ.TOPIC");
	        try {
	            service.createSession();
	            List<String> messageList = service.getMessage();
	            for (String message : messageList) {
	                log.info("message '" + message + "'");
	            }
	            log.info("messages:" +messageList.size());
	            service.shutdown();
	        } catch (JMSException e) {
	            e.printStackTrace();
	        }
	    }

}
