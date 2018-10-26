package br.parser.ui;

import java.io.File;
import java.util.List;
import java.util.Optional;

import br.parser.controller.Controller;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class Configuration extends Tab {


	// Create a file dialog
	FileChooser xmlFile = new FileChooser();
	FileChooser sourcePath = new FileChooser();
	FileChooser jarPath = new FileChooser();

	DirectoryChooser repoPath = new DirectoryChooser();
	ComboBox<String> cmbSystemName = new ComboBox<String>();
	Controller controller = new Controller();

	Button btnSourcePath = new Button("Source");
	Button btnJarPath = new Button("Jar Libraries");
	Button btnRepoPath = new Button("Repo Systems");
	Button btnXmlFile = new Button("XML File");
	Button btnSave = new Button("Save");
	Button btnDelete = new Button("Delete");

	TextField txtSourcePath = new TextField();
	TextField txtJarPath = new TextField();
	TextField txtXmlFile = new TextField();
	TextField txtRepoPath = new TextField();

	public Configuration(String text, Node graphic) {
		this.setText(text);
		this.setGraphic(graphic);
		init();
	}

	public void init() {

		this.setOnSelectionChanged(e -> onTabSelected());
		cmbSystemName.setOnAction(e -> onAction());

		txtSourcePath.setEditable(false);
		txtJarPath.setEditable(false);
		txtXmlFile.setEditable(false);
		txtRepoPath.setEditable(false);

		cmbSystemName.setEditable(true);
		try {
			this.loadSystems();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		xmlFile.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML", "*.xml"));
		sourcePath.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt"));
		jarPath.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("TXT", "*.txt"));

		btnSave.setOnAction(e -> {
			try {
				saveConfiguration();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		btnSourcePath.setOnAction(e -> handleSourcePath());
		btnJarPath.setOnAction(e -> handleJarPath());
		btnRepoPath.setOnAction(e -> handleRepoPath());
		btnXmlFile.setOnAction(e -> handleXMLFile());
		btnDelete.setOnAction(e-> deleteConfiguration());

		GridPane grid = new GridPane();

		HBox buttonsPlace = new HBox();
		buttonsPlace.getChildren().addAll(btnSave, btnDelete);

		grid.addRow(0, new Label("System Name: "),cmbSystemName, new Label("*"));
		grid.addRow(1, btnRepoPath, txtRepoPath, new Label("*"));
		grid.addRow(2, btnSourcePath,txtSourcePath, new Label("*"));
		grid.addRow(3, btnJarPath, txtJarPath, new Label("*"));
		grid.addRow(4, btnXmlFile, txtXmlFile);
		grid.addRow(6, new Label(""), buttonsPlace);
		VBox vbox = new VBox();
		vbox.getChildren().addAll(grid, new Label (""), new Label("(*) Required for the static analysis."));
		
		TitledPane path = new TitledPane();
		path.setText("Path Configurator");
		path.setContent(vbox);

		

		this.setContent(path);
	}

	private void handleSourcePath() {

		try
		{	
			File file = sourcePath.showOpenDialog(null);
			txtSourcePath.setText(file.toString());
		}catch(NullPointerException ex)
		{

		}

	}

	private void handleJarPath() {

		try
		{	
			File file = jarPath.showOpenDialog(null);
			txtJarPath.setText(file.toString());
		}catch(NullPointerException ex)
		{

		}
	}

	private void handleXMLFile() {

		try 
		{
			File file = xmlFile.showOpenDialog(null);
			txtXmlFile.setText(file.toString());
		}catch(NullPointerException ex)
		{

		}

	}

	private void handleRepoPath() {

		try 
		{
			File file = repoPath.showDialog(null);
			txtRepoPath.setText(file.toString() + "/");

		}catch(NullPointerException ex)
		{

		}
	}

	private void saveConfiguration() throws Exception {

		try {

			if (cmbSystemName.getValue().length() !=0 &&
					txtSourcePath.getText().length() != 0 && 
					txtJarPath.getText().length() != 0 && 
					txtRepoPath.getText().length() != 0 )
			{

				if (controller.checkSystem(cmbSystemName.getValue())) {

					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Confirmation Dialog");
					alert.setHeaderText(null);
					alert.setContentText("The system already exist in the database. Do you want to update the information?");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK){

						controller.updateSystem(cmbSystemName.getValue(), txtRepoPath.getText(), txtSourcePath.getText(), txtJarPath.getText(), txtXmlFile.getText());
						alert = new Alert(AlertType.INFORMATION); 
						alert.setTitle("Information Dialog");
						alert.setHeaderText(null);
						alert.setContentText("Updated!");
						alert.showAndWait();
					} 
				}	
				else
				{
					controller.insertSystem(cmbSystemName.getValue(), txtRepoPath.getText(), txtSourcePath.getText(), txtJarPath.getText(), txtXmlFile.getText());
					Alert alert = new Alert(AlertType.INFORMATION); 
					alert.setTitle("Information Dialog");
					alert.setHeaderText(null);
					alert.setContentText("Saved!");
					alert.showAndWait();
				}

			}
			else {

				Alert alert = new Alert(AlertType.INFORMATION); 
				alert.setTitle("Information Dialog");
				alert.setHeaderText(null);
				alert.setContentText("One or more entries marked with (*) are empty!");
				alert.showAndWait();
			}
		}catch (NullPointerException ex)
		{
			Alert alert = new Alert(AlertType.INFORMATION); 
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("One or more entries marked with (*) are empty!");
			alert.showAndWait();
		}

	}

	private void loadSystems() throws Exception {

		cmbSystemName.getItems().clear();
		cmbSystemName.getItems().addAll(controller.loadSystems());
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

	private void onAction() {

		try {
			List<String> lst = controller.getConfiguration(cmbSystemName.getValue());
			if (lst.size() !=0)
			{
				txtRepoPath.setText(lst.get(0));
				txtSourcePath.setText(lst.get(1));
				txtJarPath.setText(lst.get(2));
				txtXmlFile.setText(lst.get(3));
			}
			else
			{
				txtRepoPath.setText("");
				txtSourcePath.setText("");
				txtJarPath.setText("");
				txtXmlFile.setText("");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void deleteConfiguration() {

		try
		{
			if (cmbSystemName.getValue().length() !=0 )
			{
				try {
					controller.deleteSystem(cmbSystemName.getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Alert alert = new Alert(AlertType.INFORMATION); 
				alert.setTitle("Information Dialog");
				alert.setHeaderText(null);

				cmbSystemName.setValue("");
				txtSourcePath.setText("");
				txtJarPath.setText("");
				txtXmlFile.setText("");
				txtRepoPath.setText("");

				alert.setContentText("Deleted!");
				alert.showAndWait();
			}
			else
			{
				Alert alert = new Alert(AlertType.INFORMATION); 
				alert.setTitle("Information Dialog");
				alert.setHeaderText(null);
				alert.setContentText("You have to select a system!");
				alert.showAndWait();
			}
		}catch (NullPointerException e)
		{
			Alert alert = new Alert(AlertType.INFORMATION); 
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("You have to select a system!");
			alert.showAndWait();
		}
	}
}
