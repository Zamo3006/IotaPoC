package iotaUtil;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jota.IotaAPI;
import jota.IotaAPI.Builder;
import jota.dto.response.GetNodeInfoResponse;

public class NodeConnector {

	private static class BuilderInfo {
		private String protocol;
		private String host;
		private String port;

		public BuilderInfo(String protocol, String host, String port) {
			this.protocol = protocol;
			this.host = host;
			this.port = port;
		}

		public Builder getBuilder() {
			return new IotaAPI.Builder().protocol(protocol).host(host).port(port);
		}

		public IotaAPI getApi() {
			return getBuilder().build();
		}
	}

	private static IotaAPI api;
	private static final Logger log = LoggerFactory.getLogger(NodeConnector.class);
	private static List<BuilderInfo> nodeList;

	public static IotaAPI getApi() {
		if (nodeList == null) {
			initializeNodeList();
		}
		for (BuilderInfo b : nodeList) {
			api = b.getApi();
			if (checkAPI()) {
				return api;
			}
		}
		log.error("Couldn't connect to any node");
		return null;

	}

	private static void initializeNodeList() {
		nodeList = new ArrayList<>();
		nodeList.add(new BuilderInfo("https", "dyn.tangle-nodes.com", "443"));
		nodeList.add(new BuilderInfo("http", "eugeneoldisoft.iotasupport.com", "14265"));
		nodeList.add(new BuilderInfo("http", "node01.iotatoken.nl", "14265"));
		nodeList.add(new BuilderInfo("http", "node02.iotatoken.nl", "14265"));
		nodeList.add(new BuilderInfo("http", "node03.iotatoken.nl", "15265"));
		nodeList.add(new BuilderInfo("http", "node04.iotatoken.nl", "14265"));
		nodeList.add(new BuilderInfo("http", "node05.iotatoken.nl", "16265"));
		nodeList.add(new BuilderInfo("http", "node06.iotatoken.nl", "14265"));
	}

	private static boolean checkAPI() {
		try {
			@SuppressWarnings("unused")
			GetNodeInfoResponse rsp = api.getNodeInfo();
			// log.info("connected to: " + rsp);
			log.info("connected to node ");
			return true;
		} catch (Exception e) {
			log.error("Failed to connect");
			return false;
		}

	}
}
