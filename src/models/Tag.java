package models;

import java.io.File;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

//                    http://www.priorbank.by/download/cb/updates/60/HBUPSAvest5_1_0_613.rar

@XmlType(propOrder = {"folderPath","tagName","fileAmount"})
public class Tag implements Comparable<Object>{
	
	private String folderPath;
	private String tagName;
	private int fileAmount;
	
	public Tag(){
		folderPath = "";
		tagName = "";
		fileAmount = 0;
	};
	
	public Tag(String folderPath, String tagName){
		String tagPath = folderPath+File.separator+tagName;
		new File(tagPath).mkdir();
		this.folderPath = tagPath;
		this.tagName = tagName;
		
		fileAmount = 0;
	}
	
	@Override 
	public String toString(){
		String res = "tag{" + 
				"folderPath" + folderPath+
				"tagName" + tagName+
				"fileAmount" + Integer.toString(fileAmount);
		res += "}";
		return res;
	}

	/**
	 * @return the folderPath
	 */
	public String getFolderPath() {
		return folderPath;
	}

	/**
	 * @param folderPath the folderPath to set
	 */
	@XmlElement(name = "folderPath")
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	/**
	 * @return the tagName
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName the tagName to set
	 */
	@XmlElement(name = "tagName")
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	/**
	 * @return the fileAmount
	 */
	public int getFileAmount() {
		return fileAmount;
	}	
	
	@XmlElement(name = "fileAmount")
	public void setFileAmount(int fileAmount) {
		this.fileAmount = fileAmount;
	}
	
	public void incFileAmount(){
		fileAmount++;
		
	}

	@Override
	public int compareTo(Object obj){
		Tag tag = ((Tag)obj);
		long compareResult = 0;
		
		if(tagName.equals(tag.getTagName()))
			if(folderPath.equals(tag.getFolderPath()))
				compareResult = 0;
			else
				compareResult = tagName.hashCode()+fileAmount+
				folderPath.hashCode() - tag.getFolderPath().hashCode();
		else
			compareResult = tag.getTagName().hashCode() - tagName.hashCode();
				
		return (int)compareResult;
	}
}
