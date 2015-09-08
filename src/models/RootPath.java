package models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "RootPath")
@XmlType(propOrder = {"path"})
public class RootPath {

	private String path;
	
	public RootPath(){
		path = "";
	}
	
	public RootPath(String path){
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	@XmlElement(name = "path")
	public void setPath(String path) {
		this.path = path;
	}
	
	@Override
    public String toString() {
		
        String res = "RootPath{"; 
        
        res+= "path = " + path;
        
        res += "}";
        return res;
    }
}
