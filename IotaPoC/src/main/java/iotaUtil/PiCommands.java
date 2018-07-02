package iotaUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jota.IotaAPI;
import jota.error.ArgumentException;
import jota.model.Transfer;
import jota.utils.TrytesConverter;
import quiz.Quiz;
import rpi.sensehat.api.SenseHat;

public class PiCommands implements Runnable {

	/** Format: Temperature receivingAddress */
	public static final String TEMEPRATURE = "Temperature";
	/** Format: Answer name question answer */
	public static final String ANSWER = "Answer";
	/** Format: Message message */
	public static final String MESSAGE = "Message";
	/** Format: Result receivingAddress */
	public static final String RESULT = "Result";

	public static final String TAG = TrytesConverter.toTrytes("PI");
	private static final Logger log = LoggerFactory.getLogger(PiCommands.class);

	private String command;
	private IotaAPI api;
	private String seed;
	private static SenseHat senseHat;
	private static Quiz quiz;

	public PiCommands(String command, String seed) {
		this.command = command;
		this.seed = seed;
		if (quiz == null) {
			quiz = new Quiz();
		}
	}

	public static void setSenseHat(SenseHat senseHat) {
		PiCommands.senseHat = senseHat;
	}

	public int getCommand() {
		if (command.contains(ANSWER)) {
			return 1;
		} else if (command.contains(MESSAGE)) {
			return 2;
		} else if (command.contains(TEMEPRATURE)) {
			return 3;
		} else if (command.contains(RESULT)) {
			return 4;
		} else {
			log.info("Unknown command!");
			return 0;
		}
	}

	private void sendTemperature() {
		String receivingAddress = StringUtils.substringAfter(command, " ");
		float msg = senseHat.environmentalSensor.getTemperature();
		String cMsg = TrytesConverter.toTrytes("Current temperature: " + String.valueOf(msg));
		List<Transfer> transfers = new ArrayList<>();
		transfers.add(new Transfer(receivingAddress, 0, cMsg, TAG));
		try {
			api.sendTransfer(seed, 2, 9, 14, transfers, null, null, false);
			log.info("send temperature " + msg + " to " + receivingAddress);
		} catch (ArgumentException e) {
			log.error(e.getMessage());
		}
	}

	private void displayMessage() {
		String message = StringUtils.substringAfter(command, " ");
		if (message.contains("'")) {
			message = message.replaceAll("'", "");
			log.info("' not supported, replacing it");
		}
		synchronized (senseHat) {
			log.info("show message: " + message);
			senseHat.ledMatrix.showMessage(message);
			senseHat.ledMatrix.waitFor(5);
			senseHat.ledMatrix.clear();
		}
	}

	private void sendAnswers() {
		String receivingAddress = StringUtils.substringAfter(command, " ");
		String cMsg;
		synchronized (quiz) {
			cMsg = TrytesConverter.toTrytes(quiz.getAnswers());
		}
		List<Transfer> transfers = new ArrayList<>();
		transfers.add(new Transfer(receivingAddress, 0, cMsg, TAG));
		try {
			api.sendTransfer(seed, 2, 9, 14, transfers, null, null, false);
			log.info("send answers to " + receivingAddress);
		} catch (ArgumentException e) {
			log.error(e.getMessage());
		}
	}

	private void receiveAnswer() {
		String name = StringUtils.substringBetween(command, " ", " ");
		String question = StringUtils.substringBetween(command, name + " ", " ");
		String answer = StringUtils.substringAfter(command, name+" "+ question + " ");
		synchronized (quiz) {
			quiz.addAnswer(name, question, answer);
		}
	}

	@Override
	public void run() {
		api = NodeConnector.getApi();
		switch (getCommand()) {
		case 0:
			break;
		case 1:
			receiveAnswer();
			break;
		case 2:
			displayMessage();
			break;
		case 3:
			sendTemperature();
			break;
		case 4:
			sendAnswers();
			break;
		default:
			log.info("Unkown command!");
			break;
		}
	}
}
