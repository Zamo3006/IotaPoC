package node;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import iotaUtil.NodeConnector;
import jota.IotaAPI;
import jota.error.ArgumentException;
import jota.model.Transaction;
import jota.utils.TrytesConverter;
import rpi.sensehat.api.SenseHat;

public class Node extends TimerTask {

	private static final Logger log = LoggerFactory.getLogger(Node.class);
	private IotaAPI api;
	private SenseHat senseHat;
	private static final String seed = "RGLPRYUEZGRQSXCPREUEDERAISYKSRWUGLMMM9QFCTTSKMCTBDOUU9XAME9TOFQWUGNEDUTBHHPIOFCNG";
	private String receivingAddress;
	private Set<Transaction> transactions;

	public Node(IotaAPI api, Integer index) {
		this.api = api;
		initialize(index);
	}

	private void initialize(Integer index) {
		transactions = new HashSet<>();
		senseHat = new SenseHat();
		PiCommands.setSenseHat(senseHat);
		try {
			index = index == null ? 0 : index;
			receivingAddress = api.getNewAddress(seed, 2, index, true, 5, false).getAddresses().get(0);
			log.info("receiving address: " + receivingAddress);
		} catch (ArgumentException e) {
			log.error(e.getMessage());
		}
	}

	public void setApi(IotaAPI api) {
		this.api = api;
	}

	public void setSenseHat(SenseHat senseHat) {
		this.senseHat = senseHat;
	}

	public String getReceivingAddress() {
		return receivingAddress;
	}

	public Set<Transaction> getTransactions() {
		return transactions;
	}

	private void handleTransaction(Transaction t) {
		if (new Timestamp(t.getTimestamp() * 1000)
				.before(new Timestamp(System.currentTimeMillis() - DateUtils.MILLIS_PER_DAY))) {
			log.info("Transaction is older than one day. Ignoring it.");
			return;
		}
		String msg = getMessageFromTrytes(t.getSignatureFragments());
		log.info("Transaction message: " + msg);
		PiCommands pi = new PiCommands(msg, seed);
		new Thread(pi).start();
	}

	private String getMessageFromTrytes(String msg) {
		String ms = StringUtils.substringBefore(msg, "999");
		if (ms.length() % 2 != 0) {
			ms += "9";
		}
		return TrytesConverter.toString(ms);
	}

	@Override
	public void run() {
		try {
			List<Transaction> tr = api.findTransactionObjectsByAddresses(new String[] { receivingAddress });
			if (tr.size() > transactions.size()) {
				tr.sort(new TimeStampComparator());
				for (int i = transactions.size(); i < tr.size(); i++) {
					handleTransaction(tr.get(i));
					transactions.add(tr.get(i));
				}
			} else {
				log.info("no new messages");
			}
		} catch (Exception e) {
			api = NodeConnector.getApi();
			log.error(e.getMessage());
		}
	}

	private class TimeStampComparator implements Comparator<Transaction> {
		@Override
		public int compare(Transaction o1, Transaction o2) {
			return (int) (o1.getTimestamp() - o2.getTimestamp());
		}
	}
}
