package iotaUtil;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import jota.IotaAPI;
import jota.error.ArgumentException;
import jota.model.Transaction;
import jota.utils.TrytesConverter;

public class PiAnswerReceiver implements Runnable{

	
	private static final Logger log = LoggerFactory.getLogger(PiAnswerReceiver.class);
	private IotaAPI api;
	private String address;
	private TextArea response;
	private Button[] buttons;
	
	public PiAnswerReceiver() {
		api = NodeConnector.getApi();
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setText(TextArea response) {
		this.response = response;
	}
	
	public void setButtons(Button... button) {
		this.buttons = button;
	}
	
	@Override
	public void run() {
		final long time = System.currentTimeMillis();
		boolean received = false;
		String stuff = "";
		while(!received) {
			List<Transaction> tr;
			try {
				tr = api.findTransactionObjectsByAddresses(new String[] { address });
				if(!tr.isEmpty()) {
					try{
						Transaction t = tr.stream().filter(trans -> (trans.getTimestamp() * 1000 > time)).findFirst().get();
						stuff = t.getSignatureFragments();
						stuff = StringUtils.substringBefore(stuff, "999");
						if (stuff.length() % 2 != 0) {
							stuff += "9";
						}
						stuff = TrytesConverter.toString(stuff);
						received = true;
						log.info("received response");
					}catch(NoSuchElementException e) {
						try {
							log.info("No new message, sleeping for 5 seconds");
							Thread.sleep(5000);
						} catch (InterruptedException e1) {
							log.error(e.getMessage());
						}
						continue;
					}
				}
			} catch (ArgumentException e) {
				log.error(e.getMessage());
			}
		}
		response.setText(stuff);
		response.setEditable(true);
		if(buttons != null) {
			for(Button b : buttons) {
				b.setDisable(false);
			}
		}
	}
	
	

}
