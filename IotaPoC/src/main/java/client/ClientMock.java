package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import iotaUtil.NodeConnector;
import jota.IotaAPI;
import jota.error.ArgumentException;
import jota.model.Transaction;
import jota.model.Transfer;
import jota.utils.SeedRandomGenerator;
import jota.utils.TrytesConverter;

public class ClientMock {

	private String seed;
	private IotaAPI api;
	public static final String TAG = TrytesConverter.toTrytes("MOCK");
	private static final Logger log = LoggerFactory.getLogger(ClientMock.class);

	private String receivingAddress = "LFCUJTNB9DCFKMJYKVIWNVDHDMYYGPOMAYHCSNCZYODZMEVQKNEUTIBYSCHZZMDMOCFKIUUVFWUQDOSHYBHQKDQUWA";
	
	private boolean send = true;
	private boolean receive = true;

	private class AnswerSender extends TimerTask {

		String[] variable;
		Integer i = 0;

		public void setVariable(String[] variable) {
			this.variable = variable;
		}

		@Override
		public void run() {			
			String cMsg = TrytesConverter.toTrytes("Answer " + variable[i]);
			List<Transfer> transfers = new ArrayList<>();
			transfers.add(new Transfer(receivingAddress, 0, cMsg, TAG));
			try {
				api.sendTransfer(seed, 2, 9, 14, transfers, null, null, false);
				log.info("send answer to " + receivingAddress);
			} catch (ArgumentException e) {
				log.error(e.getMessage());
			}
			i = (i + 1) % variable.length;
		}
	};

	public ClientMock initialize(boolean seed, String receivingAddress) {
		api = NodeConnector.getApi();
		if (seed) {
			this.seed = SeedRandomGenerator.generateNewSeed();
			log.info("Generated seed: " + this.seed);
		}
		if(receivingAddress != null) {
			this.receivingAddress = receivingAddress;
		}
		return this;
	}

	public void getTemperature() {
		String address;
		try {
			address = api.getNewAddress(seed, 2, 0, true, 5, false).getAddresses().get(0);
			String cMsg = TrytesConverter.toTrytes("Temperature " + address);
			List<Transfer> transfers = new ArrayList<>();
			transfers.add(new Transfer(receivingAddress, 0, cMsg, TAG));
			api.sendTransfer(seed, 2, 9, 14, transfers, null, null, false);
			log.info("send temperature request to " + receivingAddress);

			Thread.sleep(30000);

			log.info("Getting temperature");
			List<Transaction> tr = api.findTransactionObjectsByAddresses(new String[] { address });
			String message = tr.iterator().next().getSignatureFragments();
			message = StringUtils.substringBefore(message, "999");
			if (message.length() % 2 != 0) {
				message += "9";
			}
			message = TrytesConverter.toString(message);
			log.info("received temperature: " + message);
		} catch (Exception e) {
			log.error(" ", e.getCause());
		}
	}
	
	public void sendMessage() {		
		try {		
			String cMsg = TrytesConverter.toTrytes("Message testMessage xx x");
			List<Transfer> transfers = new ArrayList<>();
			transfers.add(new Transfer(receivingAddress, 0, cMsg, TAG));
			api.sendTransfer(seed, 2, 9, 14, transfers, null, null, false);
			log.info("send answer message to " + receivingAddress);		
		} catch (Exception e) {
			log.error(" ", e.getCause());
		}
	}
	
	public void questionCycle(boolean send, boolean receive) {
		if (send) {
			AnswerSender answerSender = new AnswerSender();
			answerSender.setVariable(
					new String[] { "tom 1 a", "tom 2 b", "tom 1 a", "franz 1 b", "franz 2 b", "franz 3 lol" });
			Timer t = new Timer();
			t.schedule(answerSender, 0, 1000);
			try {
				Thread.sleep(62000);
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}
			t.cancel();
			log.info("Finished sending answers.");
		}
		if (receive) {
			String address;
			try {
				address = api.getNewAddress(seed, 2, 0, true, 5, false).getAddresses().get(0);
				String cMsg = TrytesConverter.toTrytes("Result " + address);
				List<Transfer> transfers = new ArrayList<>();
				transfers.add(new Transfer(receivingAddress, 0, cMsg, TAG));
				api.sendTransfer(seed, 2, 9, 14, transfers, null, null, false);
				log.info("send answer request to " + receivingAddress);

				Thread.sleep(20000);

				log.info("Getting answers");
				List<Transaction> tr = api.findTransactionObjectsByAddresses(new String[] { address });
				String message = tr.iterator().next().getSignatureFragments();
				message = StringUtils.substringBefore(message, "999");
				if (message.length() % 2 != 0) {
					message += "9";
				}
				message = TrytesConverter.toString(message);
				log.info("received answers: " + message);
			} catch (Exception e) {
				log.error(" ", e.getCause());
			}
		}
	}
}
