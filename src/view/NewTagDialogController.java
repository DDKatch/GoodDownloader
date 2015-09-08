package view;

import models.Tag;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class NewTagDialogController extends Observable{
	
	@FXML
	private TextField path_TF;
	
	@FXML
	private TextField tagName_TF;
	
	@FXML
	private Button ok_B;
	
	@FXML
	private Button cancel_B;
	
	@FXML
	private Button select_path_B;
		
	private Tag tag;
	
	private String rootFolderPath;
	
	public NewTagDialogController(Stage stage){
		
		Scene scene = stage.getScene();
		AnchorPane pane = (AnchorPane)scene.getRoot();
		
		path_TF = (TextField) pane.getChildren().get(0);
		tagName_TF = (TextField) pane.getChildren().get(1);
		ok_B = (Button) pane.getChildren().get(2);
		cancel_B = (Button) pane.getChildren().get(3);
		select_path_B = (Button) pane.getChildren().get(4);
				
		cancel_B.requestFocus();
		
		ok_B.setOnMouseClicked((event) -> {
			String path = path_TF.getText();
			String name = tagName_TF.getText();
			
			if(path.isEmpty())
				path = rootFolderPath;
			if(name.isEmpty())
				name = " ";
			
			tag = new Tag(path,name);
			
			setChanged();
			notifyObservers(tag);
			stage.close();
		});
		
		cancel_B.setOnMouseClicked((event) -> {
			stage.close();
		});
		
		select_path_B.setOnMouseClicked((event) -> {
			fileChoose();
		});
	}
	
	private void fileChoose(){
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setInitialDirectory(new File(rootFolderPath));
		File selectedDirectory = chooser.showDialog(new Stage());
		if(selectedDirectory != null)
			path_TF.setText(selectedDirectory.getPath());
	}

	public Tag getTag() {
		return tag;
	}

	public String getRootFolderPath() {
		return rootFolderPath;
	}

	public void setRootFolderPath(String rootFolderPath) {
		this.rootFolderPath = rootFolderPath;
	}
}
