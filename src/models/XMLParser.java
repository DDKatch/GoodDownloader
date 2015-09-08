package models;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public interface XMLParser {

	@SuppressWarnings("rawtypes")
	default Object loadDataFromFile(String fileName, Class myClass) {
		Object object = null;
		String fullPath = "dist/resources/data/"+fileName;
		File file = new File(fullPath);
	    try {
	        JAXBContext context = JAXBContext.newInstance(myClass);
	        Unmarshaller unmarshaller = context.createUnmarshaller();
	        object = unmarshaller.unmarshal(file);
	    } catch (Exception e) { e.printStackTrace(); }
	    
	    return object;
	}

	@SuppressWarnings("rawtypes")
	default void saveDataToFile(String fileName, Class myClass, Object object) {

		String fullPath = "dist/resources/data/"+fileName;

		File file = new File(fullPath);
	    try {
	        JAXBContext context = JAXBContext .newInstance(myClass);
	        Marshaller marshaller = context.createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        marshaller.marshal(object, file);

	    } catch (Exception e) { e.printStackTrace();  }
	}
	
}
