package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import iotaUtil.NodeConnector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jota.IotaAPI;
import jota.error.ArgumentException;

public class Client extends Application {
	
	private ClientController controller;
	private Scene scene;
	private boolean developer = false;
	private static final Logger log = LoggerFactory.getLogger(Client.class);
	
	public Client() {
	}
	
	private String generateAddress(int index) {
		IotaAPI api =  NodeConnector.getApi();
		try {
			 String a = api.getNewAddress("RGLPRYUEZGRQSXCPREUEDERAISYKSRWUGLMMM9QFCTTSKMCTBDOUU9XAME9TOFQWUGNEDUTBHHPIOFCNG",2 , index, true, 5, false).getAddresses().get(0);
			 log.info("generated address: "+a);
			 return a;
		} catch (ArgumentException e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		developer = new Boolean(getParameters().getUnnamed().get(0));
		log.info("Launched in "+ (developer ? "Developer" : "Cient") + "-mode");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Client.fxml"));
		Parent root = loader.load();
		controller = loader.<ClientController>getController();
		log.info(controller+"");
		controller.setDeveloper(developer);
		controller.setAddress(generateAddress(new Integer(getParameters().getUnnamed().get(1))));
		scene = new Scene(root);
		
		primaryStage.setTitle("Blockchain Quiz Client");
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
		
		
	}

}