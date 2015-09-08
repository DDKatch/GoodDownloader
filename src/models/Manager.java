package models;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.io.FileUtils;

import view.NewTagDialogController;
import view.NewURLDialogController;
import view.ViewController;

@XmlRootElement(name = "manager")
@XmlType(propOrder = {"map","lastDownload","lastTag", "rootPath"})
public class Manager extends Observable implements Observer{
	
	private TreeMap<Tag, DownloadsWrapper> map;
	
	private Download lastDownload;
	
	private Tag lastTag;
	
	private String rootPath;
	
	public Manager(){
		rootPath = "";
	};
	
	public Manager( String rootPath){
		this.rootPath = rootPath;
		
		Iterable<Path> dirs = FileSystems.getDefault().getRootDirectories();
		
		dirs.iterator().next();
		for (Path name: dirs) {
		    System.err.println(name);
		}
		
		map = new TreeMap<Tag, DownloadsWrapper>();
		map.put(new Tag(rootPath,"Applications"), new DownloadsWrapper());
		map.put(new Tag(rootPath,"Music"), new DownloadsWrapper());
		map.put(new Tag(rootPath,"Movies"), new DownloadsWrapper());
		map.put(new Tag(rootPath,"Pictures"), new DownloadsWrapper());
		map.put(new Tag(rootPath,"Other"), new DownloadsWrapper());
		
		lastTag = map.lastKey();
		lastDownload = new Download();
	}


	private Tag tagDefinition(String fileExtention){
		Tag result = null;
		switch(fileExtention){
		case("exe"):
		case("apk"):
		case("dmg"):
			result = determineTagThroughFolderPath (rootPath+File.separator+"Applications");break;
		case("mp3"):
		case("aac"):
			result = determineTagThroughFolderPath (rootPath+File.separator+"Music");break;
		case("mp4"):
		case("avi"):
			result = determineTagThroughFolderPath (rootPath+File.separator+"Movies");break;
		case("jpg"):
		case("jpeg"):
		case("png"):
		case("bmp"):
		case("gif"):
			result = determineTagThroughFolderPath (rootPath+File.separator+"Pictures");break;
		default:
			result = determineTagThroughFolderPath (rootPath+File.separator+"Other");break;
		}
		return result;
	}
	
	//http://dl.zaycev.net/218885/3512618/paola_-_a_more_dj_fisun_edit_(zaycev.net).mp3

	//http://downloads.typesafe.com/scalaide-pack/4.0.0.vfinal-luna-211-20150305/scala-SDK-4.0.0-vfinal-2.11-linux.gtk.x86.tar.gz
	
	public void addDownload(URL url, String fileName){
		String fileExtention = url.getFile().substring(url.getFile().lastIndexOf('.')+1, url.getFile().length());
		
		lastTag = tagDefinition(fileExtention);
		lastDownload = new Download(url, lastTag.getFolderPath(), fileName);
		
		if(!containDownload(url)){
			map.get(lastTag).addDownload(lastDownload);
			lastTag.incFileAmount();
		}
		
		setChanged();
		notifyObservers(lastTag);
	}

	public void deleteTag(String name){
				
		Tag temp = determineTagThroughName(name);	
		String path = temp.getFolderPath();
		
		lastTag = map.lastKey();
		
		switch(temp.getTagName())
		{
			case "Pictures":
			case "Other":
			case "Applications":
			case "Movies":
			case "Music":
				break;
			default:{
				map.remove(temp);	
				try {
					FileUtils.deleteDirectory(new File(path));
				} 
				catch (IOException e) {e.printStackTrace();}
			}
		}
		
		setChanged();
		notifyObservers(lastTag);
	}
	
	public void addDownload(Tag tag, Download download){
		lastTag = tag;
		lastTag.incFileAmount();
		lastDownload = download;
		
		map.get(lastTag).addDownload(download);
	}
	
	public void addTag(Tag tag){
		lastTag = tag;
		map.put(tag, new DownloadsWrapper());
	}
	
	public ArrayList<Tag> getTags(){
		ArrayList<Tag> tags = new ArrayList<Tag>();
		
		Set<Tag> keys = map.keySet(); 
		for (Tag key: keys) { 
			tags.add(key);
		} 
		
		return tags;
	}
	
	public ArrayList<Download> allDownloads(){
		ArrayList<Download> allDownloads  = new ArrayList<Download>();
		
		Set<Tag> keys = map.keySet(); 
		for (Tag key: keys) { 
			if(map.get(key)!=null){
				for(int i=0; i<map.get(key).getDownloads().size(); i++){
					allDownloads.add(map.get(key).getDownloads().get(i));
				}
			}
		} 
		return allDownloads;
	}

	public ArrayList<Download> getTagDownloads(Tag tag){
		return map.get(tag).getDownloads();
	}
	
	public Download lastDownload(){
		return lastDownload;
	}
	
	public Tag lastTag(){
		return lastTag;
	}
	
	public boolean containDownload(URL url){
		Set<Tag> keys = map.keySet(); 
		for (Tag key: keys) { 
			for(int i=0; i<map.get(key).getDownloads().size(); i++){
				String yetContainURL = map.get(key).getDownloads().get(i).getUrl().getFile();
				if(yetContainURL.equals(url.getFile())){
					lastTag = key;
					return true;
				}
			}
		} 
		return false;
	}
	
	private boolean containTag(Tag tag){
		Set<Tag> keys = map.keySet(); 
		for (Tag key: keys) { 
			if(key == tag){
				lastTag = key;
				return true;
			}
		} 	
		return false;
	}
	
	
	@Override
	public void update(Observable someObj, Object object) {
		if(someObj.getClass().getName() !=  ViewController.class.getName())
		{
			if(someObj.getClass().getName() ==  NewURLDialogController.class.getName() 
					&& !containDownload(((Download)object).getUrl()))
				addDownload(determineTagThroughFolderPath(((Download)object).getFolderPath()),
						((Download)object));
			
			if(someObj.getClass().getName() ==  NewTagDialogController.class.getName() 
					&& !containTag((Tag)object))
				addTag(((Tag)object));
					
			setChanged();
			notifyObservers(lastTag);
		}
	}
	
	private Tag determineTagThroughFolderPath(String folderPath){
		Set<Tag> keys = map.keySet(); 
		for (Tag key: keys) { 
			if(key.getFolderPath().equals(folderPath)){
				lastTag = key;
			}
		} 	
		return lastTag;
	}
	
	public Tag determineTagThroughName(String name){
		Set<Tag> keys = map.keySet(); 
		for (Tag key: keys) { 
			if(key.getTagName().equals(name)){
				lastTag = key;
			}
		} 	
		return lastTag;
	}
		
	public void pauseTag(String name){
		lastTag = determineTagThroughName(name);
		for(int i=0; i<map.get(lastTag).getDownloads().size(); i++){
			map.get(lastTag).getDownloads().get(i).pause();
		}
	}
	
	@XmlElement(name = "map")
	@XmlElementWrapper
	public void setMap(TreeMap<Tag, DownloadsWrapper> map) {
		this.map = map;
	}

	public Download getLastDownload() {
		return lastDownload;
	}

	@XmlElement(name = "lastDownload")
	public void setLastDownload(Download lastDownload) {
		this.lastDownload = lastDownload;
	}

	public Tag getLastTag() {
		return lastTag;
	}

	@XmlElement(name = "lastTag")
	public void setLastTag(Tag lastTag) {
		this.lastTag = lastTag;
	}

	public String getRootPath() {
		return rootPath;
	}

	@XmlElement(name = "rootPath")
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	
	@Override
    public String toString() {
		
        String res = "manager{"; 
        if(map != null){
        	for (Tag key: map.keySet()) { 
    			res+=key.toString();
    			for(int i=0; i<map.get(key).getDownloads().size(); i++){
    				res+=map.get(key).getDownloads().get(i).toString();
    			}
    		} 
        }
        
        res+= "lastDownload = " + lastDownload.toString()+
    		   "lastTag = " + lastTag.toString() + 
    		   "rootPath = " + rootPath;
        
        res += "}";
        return res;
    }

	public TreeMap<Tag, DownloadsWrapper> getMap() {
		return map;
	}	
}
