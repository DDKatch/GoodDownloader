package models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Observable;

// This class downloads a file from a URL.
@XmlType(propOrder = {"DOWNLOADING",
"PAUSED", "COMPLETE", "NEWLYCREATED", "ERROR", "CONNECTING",		
		"url" ,"size", "downloaded", "status", 
		"folderPath", "fileName", "info" , "start_time", "time",
		"current_time", "remaining_time", "speed", "goodTime"})
public class Download extends Observable implements Runnable {
	
	// Max size of download buffer.
	private final int MAX_BUFFER_SIZE = 1024;

	// These are the status names.
	public static final String STATUSES[] = {"Downloading",
	"Paused", "Complete", "Newly created", "Error", "Connecting..."};
	
	// These are the status codes.
	public final int DOWNLOADING = 0;
	public final int PAUSED = 1;
	public final int COMPLETE = 2;
	public final int NEWLYCREATED = 3;
	public final int ERROR = 4;
	public final int CONNECTING = 5;
		
	private URL url;	// download URL
	private int size;	// size of download in bytes
	private int downloaded;	// number of bytes downloaded
	private volatile int status;	// current status of download
	private String folderPath;	// file path to save
	private String fileName;	// file name from url
	private String info;	// download info
	private long start_time;	// download start time
	private long time;			// full download time
	private long current_time; // after start or pause download time
	private long remaining_time; // remaining time to end download
	private float speed;	// download speed
	private Time goodTime;	// full time in good format
	
	public Download(){
		status = NEWLYCREATED;	
	};
	
	public Download(URL url, String folderPath, String fileName ){ 
		this.url = url;
		this.folderPath = folderPath;
	    if(fileName.isEmpty())
	    	this.fileName = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
	    else{
	    	this.fileName = fileName + url.getFile().substring(url.getFile().lastIndexOf('.'));
	    }	
	    goodTime = new Time();
	    size = -1;
	    downloaded = 0;
	    start_time = 0;
	    speed = 0;
	    time = 0;
	    current_time = 0;
	    status = NEWLYCREATED;
	    info = "";
	}
	
	@Override
	public String toString(){
		String res = "download{";
		
		res+= "url" + url.toString() +
				"size" + Integer.toString(size)+
				"downloaded" + Integer.toString(downloaded)+
				"status" + Integer.toString(status)+
				"folderPath" + folderPath+
				"fileName" + fileName+
				"info" + info+
				"start_time" + Long.toString(start_time)+
				"time" + Long.toString(time)+
				"current_time" + Long.toString(current_time)+
				"remaining_time" + Long.toString(remaining_time)+
				"speed" + Float.toString(speed)+
				"goodTime" + goodTime.toString();
								
		res+="}";
		return res;
	}
	
	/**
	 * Get this download's URL.
	 * @return the url
	 */
	public URL getUrl() {
		return url;
	}
	
	/**
	 * Get this download's size.
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Get bytes was download.
	 * @return the downloaded
	 */
	public int getDownloaded() {
		return downloaded;
	}
	
	/**
	 * Get this download's status.
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * @return the folderPath
	 */
	public String getFolderPath() {
		return folderPath;
	}
	
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	
	
	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
	
	/**
	 * Get this download's info.
	 * @return the info
	 */
	public String getInfo() {
		refreshInfo();
		return info;
	}
	
	/**
	 * @return the speed
	 */
	public float getSpeed() {
		return speed;
	}
	
	/**
	 * @return the remaining_time
	 */
	public long getRemaining_time() {
		return remaining_time;
	}
	
	// Get this download's progress.
	public float getProgress() {
	    return ((float) downloaded / size) * 100;
	}
	
	// Pause this download.
	public void pause() {
		if (status != COMPLETE)
			status = PAUSED;
	    statusChanged();
	}
		
	// Mark this download as having an error.
	private void error() {
		status = ERROR;
	    statusChanged();
	}
	
	// Start or resume downloading.
	public void startDownload() {
		if(downloaded != size){
		    Thread thread = new Thread(this);
		    thread.setDaemon(true);
		    thread.start();
		}
		else{
			status = COMPLETE;
		    statusChanged();
		}
			
	}
			
	// Download file.
	public void run() {
	    RandomAccessFile file = null;
	    InputStream stream = null;
	    
	    try {
			status = CONNECTING;
		    statusChanged();
	        // Open connection to URL.
	        HttpURLConnection connection =
	                (HttpURLConnection) url.openConnection();
	        
	        connection.setReadTimeout(3000);
	              
	        // Specify what portion of file to download.
	        connection.setRequestProperty("Range",
	                "bytes=" + downloaded + "-");
	
	        // Connect to server.
	        connection.connect();
	
	        // Make sure response code is in the 200 range.
		    if (connection.getResponseCode() / 100 != 2) {
		    	error();
		    }
		    
		    
	        // Check for valid content length.
	        int contentLength = connection.getContentLength();
	        if (contentLength < 1) {
	            error();
	        }
	
	        /* Set the size for this download if it
	     		hasn't been already set. */
	        if (size == -1) {
	        	size = contentLength;
	        }
	
	        // Open file and seek to the end of it.
	        file = new RandomAccessFile(folderPath +"/"+ fileName, "rw");
	        file.seek(downloaded);
	        
	        stream = connection.getInputStream();
	        if(status !=  ERROR)
	        {
	        	status = DOWNLOADING;
	        	statusChanged();
	        }	        
	        start_time = System.currentTimeMillis();
	        while (status ==  DOWNLOADING) {
	        	/* Size buffer according to how much of the
	       			file is left to download. */
	        	
	            byte buffer[];
	            if(size - downloaded >= 0)
		            if (size - downloaded > MAX_BUFFER_SIZE) {
		                buffer = new byte[MAX_BUFFER_SIZE];
		            } else {
		                buffer = new byte[size - downloaded];
		            }
	            else{
	            	status =  COMPLETE;
		            statusChanged();
		            break;
	            }
	
	            // Read from server into buffer.
	            int read = stream.read(buffer);
	            if (read == -1)
	                break;
	
	            // Write buffer to file.
	            file.write(buffer, 0, read);
	            downloaded = downloaded+read;
	        }
	        
	        /* Change status to complete if this point was
	     		reached because downloading has finished. */
	        if (status  ==  DOWNLOADING) {
	        	status =  COMPLETE;
	            statusChanged();
	        }
	    }catch (Exception e) {
	    	e.printStackTrace();
	        error();
	    } 
	    finally {
	        // Close file.
	        if (file != null) {
	            try {
	                file.close();
	            } catch (Exception e) {}
	        }
	        
	        // Close connection to server.
	        if (stream != null) {
	            try {
	                stream.close();
	            } catch (Exception e) {}
	        }
	        
	        // if was pause need save time 
	        time = current_time;
	        //if(status == DOWNLOADING)
	        	//pause();
	        System.out.println("close Download thread");
	        }
	}
	
	// Refresh info that this download's status has changed and notify observers.
	private void statusChanged() {
		refreshInfo();
		
		setChanged();
		notifyObservers(STATUSES[status]);
	}
	
	// Refresh download's data
	private void refreshData(){
		long newTime = System.currentTimeMillis() - start_time;
		
		current_time = time + newTime;
		if(current_time != 0)
			speed = downloaded/current_time;
		
		remaining_time = (long)((size - downloaded)/speed);
		
		goodTime = new Time(remaining_time);
	}
	
	// Refresh download's info
	private void refreshInfo(){
		if (status == DOWNLOADING)
			refreshData();
		
		// set format of numbers
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(2);
		formatter.setMinimumIntegerDigits (1);
		
		String s_dowloaded = formatter.format ((float)downloaded/1000000);
		String s_size = formatter.format ((float)size/1000000);
		String s_speed = formatter.format(speed);
		
		switch(status){
		case 0: 
			info = STATUSES[0]+": "+s_dowloaded + " MB"+
					" of "+s_size+ "MB"+
					" | Speed: "+s_speed + " kB/s"+ '\n'+
					"Remaining time: " + goodTime.getTime(); 
			break;
		case 1:
			info = STATUSES[1]+ " | Downloaded: "+s_dowloaded+ " MB"+
					" | Speed: "+s_speed + " kB/s"+ '\n'+
					"Remaining time: " + goodTime.getTime(); 
			break;
			
		case 2:
			goodTime.setTime(current_time);
			info = STATUSES[2] + " | Size: "+s_size+ " MB"+
					" | Speed: "+s_speed + " kB/s"+ '\n'+
					"Downloading time: " + goodTime.getTime(); 
			break;
		case 3:	info = STATUSES[3]; break;
		case 4:	info = STATUSES[4]; break;
		case 5:	info = STATUSES[5]; break;
		}
	}

	@XmlElement(name = "folderPath")
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	@XmlElement(name = "url")
	public void setUrl(URL url) {
		this.url = url;
	}

	@XmlElement(name = "downloaded")
	public void setDownloaded(int downloaded) {
		this.downloaded = downloaded;
	}

	@XmlElement(name = "size")
	public void setSize(int size) {
		this.size = size;
	}

	@XmlElement(name = "status")
	public void setStatus(int status) {
		if(status == CONNECTING)
			this.status = NEWLYCREATED;
		else
			this.status = status;
	}

	@XmlElement(name = "fileName")
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@XmlElement(name = "info")
	public void setInfo(String info) {
		this.info = info;
	}

	public long getStart_time() {
		return start_time;
	}

	@XmlElement(name = "start_time")
	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}

	@XmlElement(name = "time")
	public void setTime(long time) {
		this.time = time;
	}

	public long getCurrent_time() {
		return current_time;
	}

	@XmlElement(name = "current_time")
	public void setCurrent_time(long current_time) {
		this.current_time = current_time;
	}

	@XmlElement(name = "remaining_time")
	public void setRemaining_time(long remaining_time) {
		this.remaining_time = remaining_time;
	}

	@XmlElement(name = "speed")
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public Time getGoodTime() {
		return goodTime;
	}

	@XmlElement(name = "goodTime")
	public void setGoodTime(Time goodTime) {
		this.goodTime = goodTime;
	}
	
	public boolean isPaused(){
		if (status == PAUSED)
			return true;
		return false;
	}
}