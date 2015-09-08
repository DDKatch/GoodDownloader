package view;

import models.Download;
import models.Tag;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class NewURLDialogController extends Observable{
	
	@FXML
	private TextField url_TF;
	
	@FXML
	private TextField fileName_TF;
	
	@FXML
	private Button ok_B;
	
	@FXML
	private Button cancel_B;
		
	private ChoiceBox<String> tags_CB;
	
	private Download download;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public NewURLDialogController(Stage stage, ArrayList<Tag> tags) throws MalformedURLException{
		
		Scene scene = stage.getScene();
		AnchorPane pane = (AnchorPane)scene.getRoot();
		
		url_TF = (TextField) pane.getChildren().get(0);
		fileName_TF = (TextField) pane.getChildren().get(1);
		ok_B = (Button) pane.getChildren().get(2);
		cancel_B = (Button) pane.getChildren().get(3);
		tags_CB = (ChoiceBox) pane.getChildren().get(4);
		
		for(int i=0; i< tags.size(); i++)
			tags_CB.getItems().add(tags.get(i).getTagName());
		
		tags_CB.getAccessibleText();
		
		cancel_B.requestFocus();
		
		ok_B.setOnMouseClicked((event) -> {		
			String filePath = "";
			int index = tags_CB.getSelectionModel().getSelectedIndex();
			final String url = url_TF.getText();
			boolean correctURL = false;
			
			try {
				new URL(url);
				correctURL = true;
			} 
			catch (IOException e) {
				correctURL = false;
			} 
			
			if(index != -1 && correctURL)
			{
				filePath = tags.get(index).getFolderPath();
					
				try {
					download = new Download(new URL(url), filePath, fileName_TF.getText());
				} catch (Exception e) {	e.printStackTrace();}
				
				setChanged();
				notifyObservers(download);
				stage.close();
			}
		});
		
		cancel_B.setOnMouseClicked((event) -> {
			stage.close();
		});
	}

	public Download getDownload() {
		return download;
	}
}
