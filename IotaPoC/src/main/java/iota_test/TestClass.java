package iota_test;

import jota.IotaAPI;
import jota.dto.response.GetNodeInfoResponse;

public class TestClass {
	public static void main(String[] args) throws Exception {
		//https://dyn.tangle-nodes.com:443
		IotaAPI api = new IotaAPI.Builder()
		        .protocol("https")
		        .host("dyn.tangle-nodes.com")
		        .port("443")
		        .build();

		GetNodeInfoResponse response = api.getNodeInfo();
		System.out.println(response.getLatestMilestoneIndex());
	}
}
