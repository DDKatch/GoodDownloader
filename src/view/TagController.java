package view;

import java.io.IOException;
import java.util.Observable;

import models.Tag;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;

public class TagController extends Observable{
	private AnchorPane tagField;
	private ToggleButton tag_TB;
	private TextField fileAmountTF;
		
	private Tag tag;
	
	public TagController(Tag tag){
		this.tag = tag;
		
		try {
			createView();
		} catch (IOException e) {e.printStackTrace();}
	}
		
	private void createView() throws IOException{
		
		FXMLLoader fxmlLoader = new FXMLLoader(TagController.class.getResource("Tag.fxml"));
		
		try {
			tagField = (AnchorPane) fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		tag_TB = (ToggleButton) tagField.getChildren().get(0);
		if(tag.getTagName().isEmpty())
			tag_TB.setText(" ");
		else
			tag_TB.setText(tag.getTagName());
		
		fileAmountTF = (TextField) tag_TB.getGraphic();
		fileAmountTF.setText(String.valueOf(tag.getFileAmount()));
				
	
		tag_TB.setOnMouseClicked((event) -> {
			setChanged();
			notifyObservers(tag);//to viewController
		});
		
		tag_TB.setOnDragDone(new EventHandler <DragEvent>() {
			@Override
            public void handle(DragEvent event) {
                if (event.getTransferMode() == TransferMode.MOVE) {
                	tag_TB.setText("");
                }
                
                event.consume();
            }
        });
		
		tag_TB.setOnDragDetected(new EventHandler <MouseEvent>() {
			@Override
            public void handle(MouseEvent event) {
                Dragboard db = tag_TB.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(tag.getTagName());
                db.setContent(content);
                
                event.consume();
            }
        });
	}

	public AnchorPane getTagField() {
		return tagField;
	}
	
	public void selectTag(){
		tag_TB.setSelected(true);
	}
}
