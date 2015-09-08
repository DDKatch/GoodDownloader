package models;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder = {"downloads"})
public class DownloadsWrapper {
	
	private ArrayList<Download> downloads;
	
	public DownloadsWrapper(){
		downloads =  new ArrayList<Download>();
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
