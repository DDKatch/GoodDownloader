package models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;

@XmlType(propOrder = {"downloads"})
public class DownloadsWrapper {
	
	private ArrayList<Download> downloads;
	
	public DownloadsWrapper(){
		downloads =  new ArrayList<>();
	}
	
	public DownloadsWrapper(ArrayList<Download> downloads){
		this.downloads = downloads;
	}

	public ArrayList<Download> getDownloads() {
		return downloads;
	}
	
	public void addDownload(Download download){
		downloads.add(download);
	}

	@XmlElement(name = "downloads")
	@XmlElementWrapper
	public void setDownloads(ArrayList<Download> downloads) {
		this.downloads = downloads;
	}
	
	@Override 
	public String toString(){
		String res = "wrapper{";
		
		res += downloads.toString();
		
		res += "}";		
		return res;
	}
}
