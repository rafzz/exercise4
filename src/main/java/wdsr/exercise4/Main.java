package wdsr.exercise4;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import wdsr.exercise4.sender.JmsSender;

public class Main {
	static final String QUEUE_NAME = "RAFZZ.QUEUE";
	static final int NON_PERSISTENT_MODE = 1;
	static final int PERSISTENT_MODE = 2;


	private static final Logger log = LogManager.getLogger(Main.class);
	
	public static void main(String[] args) {
		
		
		log.info(String.format("%d non persistent messages sent in %d milliseconds", 10000, sendMessages(NON_PERSISTENT_MODE)));

		log.info(String.format("%d persistent messages sent in %d milliseconds", 10000, sendMessages(PERSISTENT_MODE)));
		
		
	}
	
	private static long sendMessages(int mode){
		JmsSender sendingService = new JmsSender(QUEUE_NAME);
		//sendingService.connect();
		long start;
		long stop;
		start = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			sendingService.sendTextToQueue(String.format("test_" + "%d", i), mode);
		}

		stop = System.currentTimeMillis();
		
		sendingService.close();
		return stop - start;
	}

}
