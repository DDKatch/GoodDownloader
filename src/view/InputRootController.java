package view;

import java.io.File;
import java.io.IOException;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import models.RootPath;
import models.XMLParser;

public class InputRootController implements XMLParser{
	
	@FXML
	private TextField path_TF;
		
	@FXML
	private Button ok_B;
		
	@FXML
	private Button select_path_B;
		
	private RootPath rootPath;
	
	private static Stage inputStage;
	
	public InputRootController(){
		rootPath = new RootPath();
	}
		
	@FXML
	private void okClicked(){
		RootPath newRootPath = new RootPath(path_TF.getText());
		saveDataToFile("RootPath.xml", RootPath.class, newRootPath);

		if(!newRootPath.getPath().isEmpty())
			try {
				new Main().start(new Stage());
				inputStage.close();
			} catch (IOException e) {e.printStackTrace();}
	}
	
	@FXML
	private void chooseFolder(){
		DirectoryChooser chooser = new DirectoryChooser();
		File selectedDirectory = chooser.showDialog(new Stage());
		if(selectedDirectory != null)
			path_TF.setText(selectedDirectory.getPath());
	}
	
	public RootPath getRootPath() {
		return rootPath;
	}

	public void setRootPath(RootPath rootPath) {
		this.rootPath = rootPath;
	}

	public Stage getInputStage() {
		return inputStage;
	}

	public void setInputStage(Stage inputStage) {
		InputRootController.inputStage = inputStage;
	}
}

	
