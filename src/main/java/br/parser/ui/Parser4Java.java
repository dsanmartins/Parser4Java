package br.parser.ui;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Parser4Java extends Application {
	
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	
	@Override
	public void start(Stage stage) {
		
		Analysis analysis = new Analysis("Analysis", null);
		analysis.closableProperty().set(false);
		Configuration configuration = new Configuration("Configuration",null);
		configuration.closableProperty().set(false);
		
		
		TabPane tabPane = new TabPane();
		tabPane.getTabs().addAll(analysis, configuration);

		BorderPane root = new BorderPane();
		
		Scene scene = new Scene(root);
		//PersonPresenter presenter = new PersonPresenter(model, view);
		root.setCenter(tabPane);
		root.setStyle("-fx-padding: 10;" +
				"-fx-border-style: solid inside;" +
				"-fx-border-width: 2;" +
				"-fx-border-radius: 5;" +
				"-fx-border-color: black;");

		stage.setScene(scene);
		stage.setTitle("Parser4Java");
		stage.show();
	}
}

