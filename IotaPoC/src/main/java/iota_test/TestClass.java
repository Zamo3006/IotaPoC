package iota_test;

import java.util.ArrayList;
import java.util.List;

import jota.IotaAPI;
import jota.dto.response.GetNewAddressResponse;
import jota.dto.response.GetNodeInfoResponse;
import jota.dto.response.GetTransferResponse;
import jota.dto.response.SendTransferResponse;
import jota.model.Bundle;
import jota.model.Transaction;
import jota.model.Transfer;

public class TestClass {

	private static final String seed = "RGLPRYUEZGRQSXCPREUEDERAISYKSRWUGLMMM9QFCTTSKMCTBDOUU9XAME9TOFQWUGNEDUTBHHPIOFCNG";
	private static final String receiver = "PLRGVTRVZDQFCYUMCQYPGZOELHAUBXRQDALB9OUNFRTUAPUMPVU9BMKBSHYYKHATVHVKDZDXVLCOLVXKDUXGDTIQ9X";
	private static final String address = "CQKODTHRWGQHD9MYWSSZKKQFOVJMCYPIEGFXECCNBGOBIC9CYPFGYYKERCO9MKGQGJDPCSQRGAJMAAGY9YBMLOOAWA";

	static boolean send = false;
	static boolean receive = true;

	public static void main(String[] args) throws Exception {
		// https://dyn.tangle-nodes.com:443
		IotaAPI api = new IotaAPI.Builder().protocol("https").host("dyn.tangle-nodes.com").port("443").build();
		GetNodeInfoResponse response = api.getNodeInfo();
		System.out.println(response.getLatestMilestoneIndex());

		if (send) {

			final GetNewAddressResponse res1 = api.getNewAddress(seed, 1, 0, true, 5, false);
			System.out.println(res1);
			List<Transfer> transfers = new ArrayList<>();
			transfers.add(new Transfer(receiver, 0, "TESTMSG", "TESTTAG"));
			@SuppressWarnings("unused")
			SendTransferResponse str = api.sendTransfer(seed, 2, 9, 14, transfers, null, null, false);
		}
		if (receive) {
			GetTransferResponse tr = api.getTransfers(seed, 1, 0, 0, false);
			System.out.println(tr.getTransfers().length);
			for (Bundle b : tr.getTransfers()) {
				for (Transaction t : b.getTransactions()) {
					System.out.println(t);
				}
			}
			List<Transaction> trs = api.findTransactionObjectsByAddresses(new String[] { address });
			System.out.println(trs.size());
			for (Transaction t : trs) {
				System.out.println(t);
			}
		}
	}
}
