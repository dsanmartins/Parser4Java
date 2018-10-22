package br.parser.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import br.parser.controller.Controller;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Analysis extends Tab{

	Button btnRunStaticAnalysis = new Button("Run");
	Button btnRunDynamicAnalysis = new Button("Run");
	ComboBox<String> system= new ComboBox<String>();
	ComboBox<String> options = new ComboBox<String>();
	ComboBox<String> call = new ComboBox<String>();
	ComboBox<String> selfNodeCall = new ComboBox<String>();
	ProgressBar indeterminateInd = new ProgressBar(0);
	String systemPath= "";
	Label progress = new Label("");
	Tab tab = null;

	Controller controller = new Controller();
	Properties p = new Properties();
	InputStream in = Analysis.class.getClassLoader().getResourceAsStream("configuration.properties");

	public Analysis(String text, Node graphic) {

		try {
			p.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.setText(text);
		this.setGraphic(graphic);
		init();
	}

	public void init() {

		this.setOnSelectionChanged(e -> onTabSelected());
		system.setOnAction(e->loadConfigurations());

		try {
			this.loadSystems();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		call.setOnAction(e -> handleValueChangeRepresentation());
		String operations[] = p.getProperty("typeofoperation").split("\\,");
		options.getItems().addAll(operations);
		call.getItems().addAll("Method Call", "Package Call");
		selfNodeCall.getItems().addAll("Yes", "No");

		btnRunStaticAnalysis.setOnAction(e -> {
			try {

				runStaticAnalysis();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnRunDynamicAnalysis.setOnAction(e -> runDynamicAnalysis());

		GridPane grid1 = new GridPane();
		grid1.addRow(0, new Label("Type of Representation: "), call);
		grid1.addRow(1, new Label("Type of Operation: "), options);
		grid1.addRow(2, new Label("Self Node Call: "), selfNodeCall);
		grid1.addRow(3, new Label(""), btnRunStaticAnalysis);

		GridPane grid2 = new GridPane();
		grid2.addRow(0, new Label(""), btnRunDynamicAnalysis);

		TitledPane staticAnalysis = new TitledPane();
		staticAnalysis.setText("Static Analysis");
		staticAnalysis.setContent(grid1);

		TitledPane dynamicAnalysis = new TitledPane();
		dynamicAnalysis.setText("Dynamic Analysis");
		dynamicAnalysis.setContent(grid2);

		HBox hbox = new HBox();
		hbox.getChildren().addAll(new Label("System Name: "), system, indeterminateInd);
		
		HBox hbox2 = new HBox();
		hbox2.getChildren().addAll(new Label("                           "), new Label("               "),progress);

		VBox vbox = new VBox();
		vbox.getChildren().addAll(new Label (""),hbox,hbox2, staticAnalysis,dynamicAnalysis);

		this.setContent(vbox);

	}

	private void handleValueChangeRepresentation() {

		if (call.getValue().equals("Method Call"))
		{
			options.setDisable(false);
			selfNodeCall.setDisable(false);
		}
		else
		{
			options.setDisable(true);
			selfNodeCall.setDisable(true);
		}
	}

	private void runStaticAnalysis() throws Exception {

		if (system.getSelectionModel().isEmpty() == false)
		{
			if (call.getSelectionModel().isEmpty() == false) {

				//Package Calls
				if (options.isDisable())
				{
					Task<Void> task = new Task<Void>() {
						@Override
						public Void call() {

							try {
								if (!system.getSelectionModel().isEmpty())
									controller.createPackageCalls(systemPath);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							this.updateProgress(100, 100);
							return null;
						}
					};

					indeterminateInd.progressProperty().unbind();
					indeterminateInd.progressProperty().bind(task.progressProperty());
					progress.setText("Analysing..");
					call.setDisable(true);
					options.setDisable(true);
					btnRunDynamicAnalysis.setDisable(true);
					btnRunStaticAnalysis.setDisable(true);
					tab = this.getTabPane().getTabs().get(1);
					tab.setDisable(true);
					system.setDisable(true);
					selfNodeCall.setDisable(true);
					new Thread(task).start();

					task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {

							progress.setText("Done!");
							call.setDisable(false);
							options.setDisable(false);
							btnRunDynamicAnalysis.setDisable(false);
							btnRunStaticAnalysis.setDisable(false); 
							tab.setDisable(false);
							selfNodeCall.setDisable(false);
							system.setDisable(false);
						}
					});
				}
				else
				{
					if (options.getSelectionModel().isEmpty() == false)
					{
						if (selfNodeCall.getSelectionModel().isEmpty() == false)
						{
							Task<Void> task = new Task<Void>() {
								@Override
								public Void call() {

									try {
										if (!system.getSelectionModel().isEmpty())
											controller.createMethodCalls(systemPath,options.getValue(), selfNodeCall.getValue());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									this.updateProgress(100, 100);
									return null;
								}
							};

							indeterminateInd.progressProperty().unbind();
							indeterminateInd.progressProperty().bind(task.progressProperty());
							progress.setText("Analysing..");
							call.setDisable(true);
							options.setDisable(true);
							btnRunDynamicAnalysis.setDisable(true);
							btnRunStaticAnalysis.setDisable(true);
							tab = this.getTabPane().getTabs().get(1);
							tab.setDisable(true);
							system.setDisable(true);
							selfNodeCall.setDisable(true);
							new Thread(task).start();

							task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
								@Override
								public void handle(WorkerStateEvent event) {

									progress.setText("Done!");
									call.setDisable(false);
									options.setDisable(false);
									btnRunDynamicAnalysis.setDisable(false);
									btnRunStaticAnalysis.setDisable(false); 
									tab.setDisable(false);
									selfNodeCall.setDisable(false);
									system.setDisable(false);
								}
							});
						}
						else
						{
							Alert alert = new Alert(AlertType.INFORMATION); 
							alert.setTitle("Information Dialog");
							alert.setHeaderText(null);
							alert.setContentText("You have to chose self node call!");
							alert.showAndWait();
						}
					}
					else
					{
						Alert alert = new Alert(AlertType.INFORMATION); 
						alert.setTitle("Information Dialog");
						alert.setHeaderText(null);
						alert.setContentText("You have to chose a type of operation!");
						alert.showAndWait();
					}
				}
			}
			else {

				Alert alert = new Alert(AlertType.INFORMATION); 
				alert.setTitle("Information Dialog");
				alert.setHeaderText(null);
				alert.setContentText("You have to chose a type of representation!");
				alert.showAndWait();
			}
		}
		else
		{
			Alert alert = new Alert(AlertType.INFORMATION); 
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("You have to chose a system!");
			alert.showAndWait();
		}
	}

	private void runDynamicAnalysis() {

		if (system.getSelectionModel().isEmpty() == false)
		{
			try {
				String path = controller.getXML(system.getValue());
				if (path.length() >0)
				{


					Task<Void> task = new Task<Void>() {
						@Override
						public Void call() {

							try {
								if (!system.getSelectionModel().isEmpty())
									controller.runDynamicAnalysis(system.getValue());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							this.updateProgress(100, 100);
							return null;
						}
					};

					indeterminateInd.progressProperty().unbind();
					indeterminateInd.progressProperty().bind(task.progressProperty());
					progress.setText("Analysing..");
					call.setDisable(true);
					options.setDisable(true);
					btnRunDynamicAnalysis.setDisable(true);
					btnRunStaticAnalysis.setDisable(true);
					tab = this.getTabPane().getTabs().get(1);
					tab.setDisable(true);
					system.setDisable(true);
					selfNodeCall.setDisable(true);
					new Thread(task).start();

					task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {

							progress.setText("Done!");
							call.setDisable(false);
							options.setDisable(false);
							btnRunDynamicAnalysis.setDisable(false);
							btnRunStaticAnalysis.setDisable(false); 
							tab.setDisable(false);
							selfNodeCall.setDisable(false);
							system.setDisable(false);
						}
					});

				}
				else
				{
					Alert alert = new Alert(AlertType.INFORMATION); 
					alert.setTitle("Information Dialog");
					alert.setHeaderText(null);
					alert.setContentText("Please, add an xml file in the configuration tab!");
					alert.showAndWait();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}
		else
		{
			Alert alert = new Alert(AlertType.INFORMATION); 
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("You have to chose a system!");
			alert.showAndWait();
		}

	}

	private void loadSystems() throws Exception {

		system.getItems().clear();
		system.getItems().addAll(controller.loadSystems());
	}

	private void onTabSelected() {

		if (this.isSelected()) {
			try {
				this.loadSystems();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void loadConfigurations() {

		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {

				try {
					if (!system.getSelectionModel().isEmpty())
						systemPath = controller.loadConfigurations(system.getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.updateProgress(100, 100);
				return null;
			}
		};

		indeterminateInd.progressProperty().unbind();
		indeterminateInd.progressProperty().bind(task.progressProperty());
		progress.setText("Loading..");
		call.setDisable(true);
		options.setDisable(true);
		btnRunDynamicAnalysis.setDisable(true);
		btnRunStaticAnalysis.setDisable(true);
		tab = this.getTabPane().getTabs().get(1);
		tab.setDisable(true);
		selfNodeCall.setDisable(true);
		system.setDisable(true);
		new Thread(task).start();

		task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {

				progress.setText("Done!");
				call.setDisable(false);
				options.setDisable(false);
				btnRunDynamicAnalysis.setDisable(false);
				btnRunStaticAnalysis.setDisable(false); 
				tab.setDisable(false);
				system.setDisable(false);
				selfNodeCall.setDisable(false);
			}
		});

	}
}
