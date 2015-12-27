package application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.Download;
import models.Manager;
import models.RootPath;
import models.XMLParser;
import view.InputRootController;
import view.ViewController;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application implements XMLParser{

	private Stage primaryStage;
	private InputRootController inputRootController;
	private ViewController viewController;
	private Manager manager;
	private RootPath rootPath;
		
	@Override
	public void start(Stage primaryStage) throws IOException {
		
		this.primaryStage = primaryStage;
		rootPath = ((RootPath)loadDataFromFile("RootPath.xml",  RootPath.class));
		if(rootPath.getPath().isEmpty())
		{			
			showInputRootDialog();
            manager = new Manager(rootPath.getPath());
		}
		else
			showDownloader();
    }
	
	private void showInputRootDialog() throws IOException{
		FXMLLoader fxmlLoader = new FXMLLoader(InputRootController.class.getResource("inputRootPath.fxml"));
		
		inputRootController = new InputRootController();
		inputRootController.setRootPath(rootPath);
        
		Scene scene = new Scene((AnchorPane) fxmlLoader.load());
		
		Stage inputStage = new Stage();
		inputRootController.setInputStage(inputStage);
		inputStage.setScene(scene);
		inputStage.resizableProperty().set(false);
		inputStage.show();
	}
	
	private void showDownloader() throws IOException{
		
		primaryStage.setTitle("GoodDownloader");
        FXMLLoader fxmlLoader = new FXMLLoader(ViewController.class.getResource("view.fxml"));
		
        viewController = new ViewController();
         
        manager = (Manager)loadDataFromFile("ManagerData.xml",  Manager.class);
        //manager = new Manager(rootPath.getPath());
      	manager.addObserver(viewController);
      	manager.setRootPath(rootPath.getPath());
      	
      	viewController.addObserver(manager);
    	viewController.setManager(manager);
        
      	
        fxmlLoader.setController(viewController);
        
		Scene scene  = new Scene((BorderPane) fxmlLoader.load());		
		
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.resizableProperty().set(false);
	    
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          @Override
	          public void handle(WindowEvent event) {
	        	  	pauseAllDownloads();
	        		saveDataToFile("ManagerData.xml", Manager.class, manager);
	        		saveDataToFile("RootPath.xml", RootPath.class, rootPath);
	        	  	System.exit(0);
	          }
	      });
	}
	
	private void pauseAllDownloads(){
		ArrayList<Download> allDownloads = manager.allDownloads();
		
		for(int i=0; i< allDownloads.size(); i++)
			allDownloads.get(i).pause();		
	}
	
	public static void main(String[] args) {
		launch(args);
	}	
}
