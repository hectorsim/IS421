package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import util.Constants;
import application.MainApp;

public class DataController implements Initializable {
	@FXML
	private Label budgetLabel;
	@FXML
	private Label tripLengthLabel;
	@FXML
	private Label destinationListLabel;
	@FXML
	private Button resetBtn;
	@FXML
	private Button generateDataBtn;
	@FXML
	private TextField budgetText;
	@FXML
	private Spinner<Integer> tripLengthSpinner;
	@FXML
	private ListView<String> destinationList;
	@FXML
	private ComboBox<String> countryOfOrigin;

	protected static final String INITAL_VALUE = "0";
	private final int ALERT_WARNING = 1;
	private final int ALERT_INFO = 2;

	final ObservableList<String> destinationListData = FXCollections.observableArrayList(
			"[SIN]\t\tSingapore", "[PEK]\tBeijing", "[PVG]\tShanghai", "[HKG]\tHong Kong",
			"[KIX]\t\tOsaka", "[HND]\tTokyo", "[ICN]\t\tSeoul", "[KHH]\tKaohsiung",
			"[TPE]\tTaipei", "[PNH]\tPhnom Penh", "[REP]\tSiem Reap", "[DPS]\tBali",
			"[CGK]\tJakarta", "[KUL]\tKuala Lumpur", "[PEN]\tPenang", "[RGN]\tYangon",
			"[MNL]\tManila", "[BKK]\tBangkok", "[HKT]\tPhuket", "[HAN]\tHanoi",
			"[SGN]\tHo Chi Minh City");

	// Reference to the main application.
	private MainApp mainApp;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public DataController() {
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		destinationList.setItems(destinationListData);
		destinationList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		destinationList.setOnMouseClicked(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				ObservableList<String> selectedItems = destinationList.getSelectionModel()
						.getSelectedItems();
				countryOfOrigin.setItems(selectedItems);
			}
		});

		tripLengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,
				100, Integer.parseInt(INITAL_VALUE)));

		budgetText.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				String c = event.getCharacter();
				if (!"1234567890".contains(c)) {
					event.consume();
				}
			}
		});

		resetBtn.setOnAction((event) -> {
			resetFields();
		});

		generateDataBtn.setOnAction((event) -> {
			// trip length
			int tripLength = Integer.parseInt(tripLengthSpinner.getEditor().getText());

			// budget
			int budget = Integer.parseInt(budgetText.getText());

			// country of origin
			String startLocation = countryOfOrigin.getValue();
			startLocation = startLocation.substring(1,4);
			
			// selected destinations
			ObservableList<String> selectedItems = destinationList.getSelectionModel()
					.getSelectedItems();
			ArrayList<String> airportCodes = new ArrayList<String>();
			for (String airport : selectedItems) {
				String code = airport.substring(1, 4);
				if(code.equals(startLocation))
					airportCodes.add(0, code);
				else
					airportCodes.add(code);
			}
			
			boolean destinationCheck = false;
			boolean budgetCheck = false;
			boolean tripLengthCheck = false;
			// check number of destinations
				if (selectedItems.size() < 3) {
					showErrorDialog(ALERT_WARNING, "Destination Error",
							"Number of Destinations must be more than 3");
					destinationCheck = false;
					budgetCheck = false;
					tripLengthCheck = false;
				} else {
					destinationCheck = true;
				}

				if (budgetText.getText().length() == 0) {
					showErrorDialog(ALERT_WARNING, "Budget Error", "Budget cannot be empty");
					destinationCheck = false;
					budgetCheck = false;
					tripLengthCheck = false;
				} else {
					budgetCheck = true;
				}

				if (tripLength < 3) {
					showErrorDialog(ALERT_WARNING, "Trip Length Error",
							"Trip must be more than 2 days");
					destinationCheck = false;
					budgetCheck = false;
					tripLengthCheck = false;
				} else {
					tripLengthCheck = true;
				}

				if (destinationCheck && budgetCheck && tripLengthCheck) {
					String saveDirectory = null;
					DirectoryChooser directoryChooser = new DirectoryChooser();
					File selectedDirectory = directoryChooser.showDialog(mainApp.primaryStage);
					if (selectedDirectory != null) {
						saveDirectory = selectedDirectory.getAbsolutePath();

						String filePath = savePriceMatrix(saveDirectory, tripLength, airportCodes, budget);
						String msg = "data file has been generated at the following path:\n"
								+ filePath;
						showErrorDialog(ALERT_INFO, null, msg);
					} else {
						showErrorDialog(ALERT_WARNING, "Saving Error", "Save directory not set");
					}
				}
			});
	}

	protected static String savePriceMatrix(String saveDirectory, int numberOfDays,
			ArrayList<String> airportCodes, int budget) {
		PriceMatrix pm = new PriceMatrix(airportCodes, numberOfDays);
		String results;

		try {
			results = pm.generateDAT();

			String budgetString = "budget = " + budget + ";";
			String unitDecrease = "UnitDecreaseInSatisfactionPerDay = "
					+ Constants.satisfactionDecreaseStep + ";";

			results = budgetString + "\n" + unitDecrease + "\n\n" + results;

			System.out.println(results);

			// write to file
			final String fileName = "/travel_" + airportCodes.size() + ".dat";
			File file = new File(saveDirectory + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(results);
			bw.close();
			return saveDirectory+fileName;
		} catch (IOException e) {
			System.out.println("Error: " + e);
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private void resetFields() {
		tripLengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,
				100, Integer.parseInt(INITAL_VALUE)));
		budgetText.clear();
		destinationList.getSelectionModel().clearSelection();
	}

	private void showErrorDialog(int alertType, String topic, String message) {
		Alert alert = null;

		switch (alertType) {
		case ALERT_WARNING:
			alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			break;
		case ALERT_INFO:
			alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Completed");
			break;
		}
		if (alert != null) {
			alert.setHeaderText(topic);
			alert.setContentText(message);
			alert.showAndWait();
		}
	}
}
