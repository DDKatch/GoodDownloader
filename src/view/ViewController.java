package view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import models.*;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewController extends Observable implements Observer, ImageButton{
		
	@FXML
	private TextField urlField;
	
	@FXML
	private VBox downloads_VB;
	
	@FXML 
	private VBox tags_VB;
	
	@FXML
	private ToggleButton allDs_TB;
	
	@FXML
	private ToggleButton pausedDs_TB;
	
	@FXML
	private ImageView seeFolder_IV;
	
	@FXML
	private ImageView trash_IV;
	
	@FXML
	private ImageView addTag_IV;
		
	private Manager manager;
			
	public ViewController(){
	};
			
	@FXML
    private void initialize() {
		trash_IV.setImage(new Image("file:dist/resources/images/trash.png"));
		seeFolder_IV.setImage(new Image("file:dist/resources/images/show_in_folder.png"));
		addTag_IV.setImage(new Image("file:dist/resources/images/add_tag.png"));
		
		trash_IV.setOnDragOver(new EventHandler <DragEvent>() {
			@Override
            public void handle(DragEvent event) {
                
                if (event.getGestureSource() != trash_IV &&
                        event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            }
        });

		trash_IV.setOnDragEntered(new EventHandler <DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getGestureSource() != trash_IV &&
                        event.getDragboard().hasString()) {
                	trash_IV.setBlendMode(BlendMode.HARD_LIGHT);
                }
                
                event.consume();
            }
        });

		trash_IV.setOnDragExited(new EventHandler <DragEvent>() {
            public void handle(DragEvent event) {
            	trash_IV.setBlendMode(BlendMode.SRC_OVER);
                
                event.consume();
            }
        });
        
		trash_IV.setOnDragDropped(new EventHandler <DragEvent>() {
            public void handle(DragEvent event) {
                String name = event.getDragboard().getString();
                
                manager.pauseTag(name);               
                manager.deleteTag(name);
                
                event.setDropCompleted(true);
                event.consume();
            }
        });
		
		allDs_TB.setOnMouseClicked(new EventHandler<MouseEvent>(){
			 @Override
			 public void handle(MouseEvent keyEvent){
				 showAllDownloads();
				 allDs_TB.setSelected(true);
				 pausedDs_TB.setSelected(false);
				 
				 refreshTags();
			 }
		});		
		
		pausedDs_TB.setOnMouseClicked(new EventHandler<MouseEvent>(){
			 @Override
			 public void handle(MouseEvent keyEvent){
				 showPausedDownloads();
				 pausedDs_TB.setSelected(true);
				 allDs_TB.setSelected(false);
				 
				 refreshTags();
			 }
		});	
		
		urlField.setOnKeyPressed(new EventHandler<KeyEvent>(){
			 @Override
			 public void handle(KeyEvent keyEvent){
				if(keyEvent.getCode() == KeyCode.ENTER)
					try {
						simpleAdd();
					} catch (IOException e) {e.printStackTrace();} 
			 }
		});		
				
		
		
		seeFolder_IV.setOnMouseClicked((event) -> {
			final Task<Void> task = new Task<Void>() {
				public Void call() throws Exception {
					Desktop.getDesktop().open(new File(manager.lastTag().getFolderPath()));
			    return null;
				}	
			};
			new Thread(task).start();
		});
		
		addTag_IV.setOnMouseClicked((event) -> {
			try {
				addTag();
			} 
			catch (Exception e) {e.printStackTrace();}
		});
		
		refreshTags();
		
		makeImageSimilarButton(seeFolder_IV);
		makeImageSimilarButton(trash_IV);
		makeImageSimilarButton(addTag_IV);
	}
		
	@FXML		
	private void simpleAdd()throws IOException{
		String url = urlField.getText();
		boolean correctURL = false;
		
		try {
			new URL(url);
			correctURL = true;
		} 
		catch (IOException e) {
			correctURL = false;
		} 
		
		if(correctURL)
		{	
			String fileName = "";
			manager.addDownload(new URL(url), fileName);
		}
	}	
	
	public Stage newDialog(FXMLLoader fxmlLoader)throws IOException{
		Stage primaryStage = new Stage();
		AnchorPane dialog;
        
		dialog = (AnchorPane) fxmlLoader.load();
			       
		Scene scene = new Scene(dialog);
		
	    primaryStage.setScene(scene);
	    primaryStage.show();
	    primaryStage.resizableProperty().set(false);
	    return primaryStage;
	}
	
	@FXML
	private void addTag() throws IOException{
		NewTagDialogController tagDC = new NewTagDialogController(
				newDialog(new FXMLLoader(ViewController.class.getResource("newTagDialog.fxml"))));	
		tagDC.addObserver(manager);
		tagDC.setRootFolderPath(manager.getRootPath());
	}
	
	@FXML
	private void addURL() throws IOException{
		NewURLDialogController urlDC = new NewURLDialogController(
				newDialog(new FXMLLoader(ViewController.class.getResource("newURLDialog.fxml"))), 
						manager.getTags());
		urlDC.addObserver(manager);
	}
	
	private void showAllDownloads(){
		if(!downloads_VB.getChildren().isEmpty())
			downloads_VB.getChildren().clear();
		
		ArrayList<Download> allDownloads  = manager.allDownloads();
		
		for(int i=0; i<allDownloads.size(); i++)
			refreshDownloadControllers(allDownloads.get(i));
	}
	
	private void showPausedDownloads(){
		if(!downloads_VB.getChildren().isEmpty())
			downloads_VB.getChildren().clear();
		
		ArrayList<Download> allDownloads  = manager.allDownloads();
		
		for(int i=0; i<allDownloads.size(); i++)
			if(allDownloads.get(i).isPaused())
				refreshDownloadControllers(allDownloads.get(i));
	}
	
	@Override
	public void update(Observable someObj, Object object) {
		if(someObj.getClass().getName() ==  TagController.class.getName())
		{
			manager.setLastTag((Tag)object);
			pausedDs_TB.setSelected(false);
			allDs_TB.setSelected(false);
		}
		refreshTags();
		refreshDownloads((Tag)object);
	}
	
	private void refreshTags(){
		
		int i=0;
		Set<Tag> keys = manager.getMap().keySet();
		
		if(!tags_VB.getChildren().isEmpty())
		{
			if(tags_VB.getChildren().size()!= manager.getTags().size()){
				tags_VB.getChildren().clear();
				for (Tag key: keys) { 
					TagController tagController = new TagController(key);
					tagController.addObserver(this);
					tags_VB.getChildren().add(tagController.getTagField());
				} 
			}
			for (Tag key: keys) { 
				TagController tagController = new TagController(key);
				tagController.addObserver(this);
				if(manager.lastTag().getTagName()==key.getTagName())
				tagController.selectTag();
				

				tags_VB.getChildren().set(i, tagController.getTagField());
				i++;
			} 
		}
		else
		{
			for (Tag key: keys) { 
				TagController tagController = new TagController(key);
				tagController.addObserver(this);
				tags_VB.getChildren().add(tagController.getTagField());
			} 
		}
	}
	
	private void refreshDownloads(Tag tag){
		if(!downloads_VB.getChildren().isEmpty())
			downloads_VB.getChildren().clear();
		
		for(int i=0; i<manager.getTagDownloads(tag).size(); i++)
			refreshDownloadControllers(manager.getTagDownloads(tag).get(i));
	}
	
	private void refreshDownloadControllers(Download download){
		try{
			DownloadController downloadController = new DownloadController(download);
			downloads_VB.getChildren().add(downloadController.getDownloadField());
			downloadController.startDownload();		
		}catch(IOException e) {e.printStackTrace();}
		catch(InterruptedException e) {e.printStackTrace();}
	}

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}
}
