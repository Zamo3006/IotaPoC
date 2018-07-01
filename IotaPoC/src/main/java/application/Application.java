package application;

import java.util.Timer;

import client.ClientMock;
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
		if (args[0].equals(developer)) {
			runAsClient(true);
		} else if (args[0].equals(client)) {
			runAsClient(false);
		} else if (args[0].equals(node)) {
			Integer index = null;
			if (args.length > 1) {
				index = Integer.parseInt(args[1]);
			}
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

	private static void runAsClient(boolean developer) {
		ClientMock mock = new ClientMock().initialize(true, "AMNUWDLJFATMJRWQIUTEVYCTYRDKMKYAAEIMTWFMAEQAN9DJXVVRVFWJGKSESKOQNVHPHKQEVNR9VSXZWNXCPOZPKY");
		//mock.questionCycle(true, true);
		 mock.sendMessage("ä!");
		// mock.getTemperature();
	}
}
