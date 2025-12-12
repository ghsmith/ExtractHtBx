package edu.emory.eai4hi.extracthtbx.data.anon;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.SchemaOutputResolver;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.transform.stream.StreamResult;

/**
 *
 * @author geoffrey.smith@emory.edu
 */
@XmlRootElement
public class HeartBxPatients {

    @XmlElement(name = "patient")
    public Set<Patient> patients = new TreeSet<>();

    public HeartBxPatients() {
    }
    
    public HeartBxPatients(Set<Patient> patients) {
        this.patients = patients;
    }
    
    public void xmlMarshal(String fileName) throws FileNotFoundException, IOException, JAXBException {
        JAXBContext jc = JAXBContext.newInstance(new Class[] { HeartBxPatients.class });
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(this, new FileOutputStream(fileName + ".xml"));
        jc.generateSchema(new SchemaOutputResolver() {
            @Override
            public StreamResult createOutput(String namespaceURI, String suggestedFileName) throws IOException {
                File file = new File(fileName + ".xsd");
                StreamResult result = new StreamResult(file);
                result.setSystemId(file.toURI().toURL().toString());
                return result;
            }                
        });
    }
 
    public static HeartBxPatients xmlUnmarshal(String fileName) throws FileNotFoundException, JAXBException {
        JAXBContext jc = JAXBContext.newInstance(new Class[] { HeartBxPatients.class });
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        return (HeartBxPatients)unmarshaller.unmarshal(new FileInputStream(fileName + ".xml"));
    }
    
}