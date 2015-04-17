package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import controller.DataController;

public class MainApp extends Application {

	public Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Data Generator");
		this.primaryStage.setResizable(false);
		try {
			FXMLLoader loader = new FXMLLoader();
			
//			Parent root = FXMLLoader.load(getClass().getResource("/view/OPLDataGenerator.fxml"));
			loader.setLocation(getClass().getResource("/view/OPLDataGenerator.fxml"));
			AnchorPane mainPane = (AnchorPane) loader.load();
			Scene scene = new Scene(mainPane);
			primaryStage.setScene(scene);
			primaryStage.show();
			
			DataController dataController = loader.getController();
			dataController.setMainApp(this);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}