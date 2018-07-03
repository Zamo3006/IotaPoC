package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import iotaUtil.PiCommandSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import jota.utils.InputValidator;
import jota.utils.SeedRandomGenerator;

public class ClientController {

	private static final Logger log = LoggerFactory.getLogger(ClientController.class);

	private boolean developer;

	@FXML
	private GridPane QuizInterface;
	@FXML
	private GridPane DeveloperInterface;
	@FXML
	private GridPane Welcome;
	@FXML
	private TextArea SeedInput;
	@FXML
	private TextField NameInput;
	@FXML
	private TextArea Message;
	@FXML
	private TextArea AnswersField;
	@FXML
	private Button sendMessage;
	@FXML
	private Button Temperature;
	@FXML
	private Button Answers;
	@FXML
	private TextArea AnswerStats;
	
	private String user;
	private String seed;
	private String address;

	public void setDeveloper(boolean developer) {
		this.developer = developer;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@FXML
	public void buttonMechanic(ActionEvent event) {

	}

	@FXML
	public void login(ActionEvent event) {
		String seed = SeedInput.getText();
		log.info("" + InputValidator.isValidSeed(seed));
		if (seed.length() != 81 || !InputValidator.isValidSeed(seed)) {
			Alert alert = new Alert(AlertType.ERROR, "Input is invalid Seed", ButtonType.OK);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.OK) {
				alert.close();
			}
		} else {
			if (NameInput.getText().isEmpty() || NameInput.getText().contains(" ") || NameInput.getText().length()>15) {
				Alert alert = new Alert(AlertType.ERROR, "Name must not be empty or contains spaces or be more than 15 characters", ButtonType.OK);
				alert.showAndWait();
				if (alert.getResult() == ButtonType.OK) {
					alert.close();
				}
			} else {
				this.user = NameInput.getText();
				this.seed = SeedInput.getText();
				Welcome.setDisable(true);
				Welcome.setVisible(false);
				if (developer) {
					DeveloperInterface.setDisable(false);
					DeveloperInterface.setVisible(true);
				} else {
					QuizInterface.setDisable(false);
					QuizInterface.setVisible(true);
				}
			}

		}
	}

	@FXML
	public void generateSeed(ActionEvent event) {
		String seed = SeedRandomGenerator.generateNewSeed();
		SeedInput.setText(seed);
	}

	@FXML
	public void sendMessage(ActionEvent event) {
		PiCommandSender sender = new PiCommandSender(PiCommandSender.MESSAGE, address, seed);
		sender.setMessage(Message.getText());
		new Thread(sender).start();
		log.info("send message " + Message.getText());
	}

	@FXML
	public void getTemperature(ActionEvent event) {
		PiCommandSender sender = new PiCommandSender(PiCommandSender.TEMEPRATURE, address, seed);
		new Thread(sender).start();
		log.info("requested temperature");
		Message.setText("Warte auf Temperatur Antwort");
		Message.setEditable(false);
		sendMessage.setDisable(true);
		Temperature.setDisable(true);
		sender.getAnswerer().setButtons(sendMessage, Temperature);
		sender.getAnswerer().setText(Message);
	}

	@FXML
	public void sendAnswer(ActionEvent event) {
		PiCommandSender sender = new PiCommandSender(PiCommandSender.ANSWER, address, seed);
		String id = ((Button) (event.getSource())).getId();
		sender.prepareAnswer(user, id.substring(0, 1), id.substring(1));
		new Thread(sender).start();
		log.info("send answer "+user+" "+id.substring(0,1)+" "+id.substring(1));
	}

	@FXML
	public void receiveAnswers(ActionEvent event) {
		PiCommandSender sender = new PiCommandSender(PiCommandSender.RESULT, address, seed);
		new Thread(sender).start();
		log.info("send answer request");
		AnswersField.setText("Waiting for answer");
		AnswersField.setEditable(false);
		Answers.setDisable(true);
		sender.getAnswerer().setButtons(Answers);
		sender.getAnswerer().setText(AnswersField);
	}	
}
