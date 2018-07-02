package application;

import java.util.Timer;

import client.Client;
import iotaUtil.NodeConnector;
import node.Node;

public class Application {

	private static final String developer = "-developer";
	private static final String node = "-node";
	private static final String client = "-client";
	private static final int period = 10000;

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage -node , -client or -developer");
			System.exit(1);
		}
		Integer index = null;
		if (args.length > 1) {
			index = Integer.parseInt(args[1]);
		}
		if (args[0].equals(developer)) {
			runAsClient(true, index);
		} else if (args[0].equals(client)) {
			runAsClient(false, index);
		} else if (args[0].equals(node)) {			
			runAsNode(index);
		} else {
			System.out.println("Usage -node , -client or -developer");
			System.exit(1);
		}
	}

	private static void runAsNode(Integer index) {
		Node node = new Node(NodeConnector.getApi(), index);
		Timer timer = new Timer();
		timer.schedule(node, 0, period);
	}

	private static void runAsClient(boolean developer, int index) {
		Client.launch(Client.class, String.valueOf(developer), String.valueOf(index));
	}
}
