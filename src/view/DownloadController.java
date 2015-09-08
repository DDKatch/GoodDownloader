package view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import models.Download;

public class DownloadController extends Observable implements Observer, ImageButton
{
	private AnchorPane downloadField;
	private ProgressIndicator progress;
	private Text fileNameField;
	private Text conditionField;
	private ToggleButton play_pauseTB;
	private ImageView file_image;
				
	private Download download;
	
	public DownloadController(Download download) throws IOException{
		file_image = null;
		
		this.download = download;
		this.download.deleteObservers();
		this.download.addObserver(this);
				
		createView();
		refreshView(download);
	}
		
	public DownloadController(URL url, String fileName, String filePath) throws IOException{
		file_image = null;
		
		download = new Download(url, filePath, fileName);
		download.deleteObservers();
		download.addObserver(this);
		
		createView();
		refreshView(download);
	}
	
	public void startDownload()throws IOException, InterruptedException
	{
		if(download.getStatus() == download.NEWLYCREATED)
			download.startDownload();
		
		if(download.getStatus() == download.PAUSED)
			play_pauseTB.setSelected(true);
		
		startProgress();
	}
		
	@Override
	public void update(Observable object, Object event)
	{
		System.out.println((String)event);
		
	    if(object.getClass().getName() == Download.class.getName())
	    {
	    	conditionField.setText(((Download) object).getInfo());	
	    	refreshView(((Download) object));
	    }
	}
	
	private void refreshView(Download download){
		conditionField.setText(download.getInfo());	
		
		if(download.getStatus()!=download.PAUSED){
			((ImageView)play_pauseTB.getGraphic()).setImage(
					new Image("file:dist/resources/images/pause.png"));
		}
		else{
			((ImageView)play_pauseTB.getGraphic()).setImage(
					new Image("file:dist/resources/images/play.png"));
		}

    	if(download.getStatus() == download.COMPLETE)
    	{
    		conditionField.setText(download.getInfo());
			play_pauseTB.setVisible(false);
			play_pauseTB.setDisable(true);
			progress.setVisible(false);
			progress.setDisable(true);
			file_image.setVisible(true);
	        file_image.setDisable(false);
    	}
	}
	
	public AnchorPane getDownloadField() {
		return downloadField;
	}
	
	private void createView() throws IOException{
				
		FXMLLoader fxmlLoader = new FXMLLoader(DownloadController.class.getResource("Download.fxml"));
	
		try {
			downloadField = (AnchorPane) fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Rectangle background = (Rectangle) downloadField.getChildren().get(0);
		fileNameField = (Text) downloadField.getChildren().get(1);
		conditionField = (Text) downloadField.getChildren().get(2);
		progress = (ProgressIndicator) downloadField.getChildren().get(3);
		play_pauseTB = (ToggleButton) downloadField.getChildren().get(4);
		file_image = (ImageView)downloadField.getChildren().get(5);
		
		play_pauseTB.setDisable(true);
		background.requestFocus();
		
		background.setOnMouseClicked((event) -> {
			background.requestFocus();
		});
		
		play_pauseTB.setOnAction((event) -> {
			if(!play_pauseTB.isSelected()){
				try {	
					download.startDownload();
				} catch (Exception e) {e.printStackTrace();}
			}
			else{
				download.pause();
			}	
		});
		
		file_image.setOnMouseClicked((event) -> {
			final Task<Void> task = new Task<Void>() {
				public Void call() throws Exception {
					Desktop.getDesktop().open(new File(
							download.getFolderPath() +File.separator+ download.getFileName()));
			    return null;
				}	
			};
			new Thread(task).start();
		});
		
		makeImageSimilarButton(file_image);		
	}
	
	
	
	private void startProgress(){
		final Task<Void> task = new Task<Void>() {
			public Void call() throws Exception {
				
				double  download_progress = 0.0;
				choosingFileIcon();
				String fileName = download.getFileName();
			    if(fileName.length()>40)
			    	fileName = fileName.substring(0,40)+"...";
				fileNameField.setText(fileName);
				conditionField.setText(((Download) download).getInfo());
				
				while(download_progress!=100)
				{	
					if(download.getStatus()== download.DOWNLOADING)
					{
						conditionField.setText(((Download) download).getInfo());
					}
					
					if(download.getStatus()==download.CONNECTING)
					{
						play_pauseTB.setDisable(true);
						updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 100);
						progress.setMinSize(50, 50);
						progress.setMaxSize(50, 50);
						AnchorPane.setLeftAnchor(progress, 10.0);
					}
					else
					{	
						play_pauseTB.setDisable(false);
						progress.setMinSize(67, 67);
						progress.setMaxSize(67, 67);
						AnchorPane.setLeftAnchor(progress, 5.0);
						download_progress = download.getProgress();
						updateProgress(download_progress, 100);
					}
					if(download.getStatus()==download.ERROR){
						play_pauseTB.setSelected(true);
						download.pause();
					}
						
										
					Thread.sleep(180);
	            }
				return null;
			}
		 };
		 progress.progressProperty().unbind();  
         progress.progressProperty().bind(task.progressProperty()); 
         new Thread(task).start();
	}
	
	private String takeFileExtention(String fileName){
		return fileName.substring(fileName.lastIndexOf('.')+1, fileName.length());
	}
	
	private void choosingFileIcon() throws MalformedURLException{
				
		String file_extention = takeFileExtention(download.getFileName());
		
		String iconPath = "dist/resources/images/"+file_extention+".png";
				
		if((new File(iconPath)).exists())
			iconPath = "file:"+iconPath;
		else
			iconPath = "file:dist/resources/images/unknown.png";
				
		file_image.setImage(new Image(iconPath));
	}

	/**
	 * @return the download
	 */
	public Download getDownload() {
		return download;
	}
}
