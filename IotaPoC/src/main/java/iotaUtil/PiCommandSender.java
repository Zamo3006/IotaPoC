package iotaUtil;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jota.IotaAPI;
import jota.error.ArgumentException;
import jota.model.Transfer;
import jota.utils.TrytesConverter;

public class PiCommandSender implements Runnable {

	/** Format: Temperature receivingAddress */
	public static final String TEMEPRATURE = "Temperature";
	/** Format: Answer name question answer */
	public static final String ANSWER = "Answer";
	/** Format: Message message */
	public static final String MESSAGE = "Message";
	/** Format: Result receivingAddress */
	public static final String RESULT = "Result";

	public static final String TAG = TrytesConverter.toTrytes("CLIENT");
	private static final Logger log = LoggerFactory.getLogger(PiCommandSender.class);

	private String address;
	private IotaAPI api;
	private String seed;
	private String command;
	private String name;
	private String question;
	private String answer;
	private String message;
	private PiAnswerReceiver answerer;

	public PiCommandSender(String command, String address, String seed) {
		this.command = command;
		this.seed = seed;
		this.address = address;
		
		answerer = new PiAnswerReceiver();
	}

	public PiAnswerReceiver getAnswerer() {
		return answerer;
	}
	
	public void sendMessage() {
		String cMsg = TrytesConverter.toTrytes(MESSAGE + " " + message);
		List<Transfer> transfers = new ArrayList<>();
		transfers.add(new Transfer(address, 0, cMsg, TAG));
		try {
			api.sendTransfer(seed, 2, 9, 14, transfers, null, null, false);
			log.info("send message \"" + message + "\" to " + address);
		} catch (ArgumentException e) {
			log.error(e.getMessage());
		}
	}

	public void requestTemperature() {
		String receiver;
		try {
			receiver = api.getNewAddress(seed, 2, 0, true, 5, false).getAddresses().get(0);
			String cMsg = TrytesConverter.toTrytes(TEMEPRATURE + " " + receiver);
			List<Transfer> transfers = new ArrayList<>();
			transfers.add(new Transfer(address, 0, cMsg, TAG));
			api.sendTransfer(seed, 2, 9, 14, transfers, null, null, false);
			log.info("send temperature request to " + address);
			answerer.setAddress(receiver);
			new Thread(answerer).start();
		} catch (ArgumentException e) {
			log.error(e.getMessage());
		}
		
	}

	public void sendAnswer() {
		String cMsg = TrytesConverter.toTrytes(ANSWER + " " + name + " " + question + " " + answer);
		List<Transfer> transfers = new ArrayList<>();
		transfers.add(new Transfer(address, 0, cMsg, TAG));
		try {
			api.sendTransfer(seed, 2, 9, 14, transfers, null, null, false);
			log.info("send answer to " + address);
		} catch (ArgumentException e) {
			log.error(e.getMessage());
		}
	}

	public void requestAnswer() {
		String receiver;
		try {
			receiver = api.getNewAddress(seed, 2, 0, true, 5, false).getAddresses().get(0);
			String cMsg = TrytesConverter.toTrytes(RESULT + " " + receiver);
			List<Transfer> transfers = new ArrayList<>();
			transfers.add(new Transfer(address, 0, cMsg, TAG));
			api.sendTransfer(seed, 2, 9, 14, transfers, null, null, false);
			log.info("requested result from " + address);
			answerer.setAddress(receiver);
			new Thread(answerer).start();
		} catch (ArgumentException e) {
			log.error(e.getMessage());
		}
	}

	public void prepareAnswer(String name, String question, String answer) {
		this.name = name;
		this.question = question;
		this.answer = answer;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void run() {
		api = NodeConnector.getApi();
		switch (getCommand()) {
		case 0:
			break;
		case 1:
			sendAnswer();
			break;
		case 2:
			sendMessage();
			break;
		case 3:
			requestTemperature();
			break;
		case 4:
			requestAnswer();
			break;
		default:
			log.info("Unkown command!");
			break;
		}
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

}
