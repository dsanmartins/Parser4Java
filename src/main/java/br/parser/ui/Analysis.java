package br.parser.ui;

import java.sql.SQLException;

import br.parser.controller.Controller;
import br.parser.utils.Property;
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
	ComboBox<String> systemDynamic = new ComboBox<String>();
	ComboBox<String> options = new ComboBox<String>();
	ComboBox<String> call = new ComboBox<String>();
	ComboBox<String> selfNodeCall = new ComboBox<String>();
	ProgressBar indeterminateInd = new ProgressBar(0);
	String systemPath= "";
	Label progress = new Label("");
	Tab tab1 = null;
	Tab tab2 = null;
	Tab tab3 = null;

	Controller controller = new Controller();

	public Analysis(String text, Node graphic) {

		this.setText(text);
		this.setGraphic(graphic);
		try {
			controller.initializeDB();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			init();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init() throws SQLException {

		this.setOnSelectionChanged(e -> onTabSelected());
		system.setOnAction(e->loadConfigurations());
		try {
			this.loadSystems();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		call.setOnAction(e -> handleValueChangeRepresentation());
		String operations[] = (Property.getProperty("typeofoperation")).split("\\,");
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
		grid1.addRow(0, new Label("System Name: "), system);
		grid1.addRow(1, new Label("Type of Representation: "), call);
		grid1.addRow(2, new Label("Type of Operation: "), options);
		grid1.addRow(3, new Label("Self Node Call: "), selfNodeCall);
		grid1.addRow(4, new Label(""), btnRunStaticAnalysis);

		GridPane grid2 = new GridPane();
		grid2.addRow(0, new Label("System Name: "), systemDynamic);
		grid2.addRow(1, new Label(""), btnRunDynamicAnalysis);

		TitledPane staticAnalysis = new TitledPane();
		staticAnalysis.setText("Static Analysis");
		staticAnalysis.setContent(grid1);

		TitledPane dynamicAnalysis = new TitledPane();
		dynamicAnalysis.setText("Dynamic Analysis");
		dynamicAnalysis.setContent(grid2);

		HBox hbox = new HBox();
		hbox.getChildren().addAll(new Label ("Progress: "),indeterminateInd,progress);
		
		VBox vbox = new VBox();
		vbox.getChildren().addAll(new Label (""),hbox, staticAnalysis,dynamicAnalysis);

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
					tab1 = this.getTabPane().getTabs().get(1);
					tab1.setDisable(true);
					tab2 = this.getTabPane().getTabs().get(2);
					tab2.setDisable(true);
					tab3 = this.getTabPane().getTabs().get(3);
					tab3.setDisable(true);
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
							tab1.setDisable(false);
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
							tab1 = this.getTabPane().getTabs().get(1);
							tab1.setDisable(true);
							tab2 = this.getTabPane().getTabs().get(2);
							tab2.setDisable(true);
							tab3 = this.getTabPane().getTabs().get(3);
							tab3.setDisable(true);
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
									tab1.setDisable(false);
									tab2.setDisable(false);
									tab3.setDisable(false);
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

		if (systemDynamic.getSelectionModel().isEmpty() == false)
		{
			try {
				String path = controller.getXML(systemDynamic.getValue());
				if (path.length() >0)
				{


					Task<Void> task = new Task<Void>() {
						@Override
						public Void call() {

							try {
								if (!systemDynamic.getSelectionModel().isEmpty())
									controller.runDynamicAnalysis(systemDynamic.getValue());
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
					tab1 = this.getTabPane().getTabs().get(1);
					tab1.setDisable(true);
					tab2 = this.getTabPane().getTabs().get(2);
					tab2.setDisable(true);
					tab3 = this.getTabPane().getTabs().get(3);
					tab3.setDisable(true);
					system.setDisable(true);
					selfNodeCall.setDisable(true);
					systemDynamic.setDisable(true);
					new Thread(task).start();

					task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {

							progress.setText("Done!");
							call.setDisable(false);
							options.setDisable(false);
							btnRunDynamicAnalysis.setDisable(false);
							btnRunStaticAnalysis.setDisable(false); 
							tab1.setDisable(false);
							tab2.setDisable(false);
							tab3.setDisable(false);
							selfNodeCall.setDisable(false);
							system.setDisable(false);
							systemDynamic.setDisable(false);
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
		
		systemDynamic.getItems().clear();
		systemDynamic.getItems().addAll(controller.loadSystems());
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
		tab1 = this.getTabPane().getTabs().get(1);
		tab1.setDisable(true);
		tab2 = this.getTabPane().getTabs().get(2);
		tab2.setDisable(true);
		tab3 = this.getTabPane().getTabs().get(3);
		tab3.setDisable(true);
		selfNodeCall.setDisable(true);
		systemDynamic.setDisable(true);
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
				tab1.setDisable(false);
				tab2.setDisable(false);
				tab3.setDisable(false);
				system.setDisable(false);
				selfNodeCall.setDisable(false);
				systemDynamic.setDisable(false);
			}
		});

	}
}
