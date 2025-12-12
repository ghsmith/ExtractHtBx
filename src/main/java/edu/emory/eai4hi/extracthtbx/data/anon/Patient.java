package edu.emory.eai4hi.extracthtbx.data.anon;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author geoffrey.smith@emory.edu
 */
@XmlRootElement
public class Patient implements Comparable<Object> {
  
    @XmlTransient
    public String patId;
    @XmlAttribute
    public String patIdHash;
    @XmlTransient
    public Map<String, Case> caseMap = new TreeMap<>();
    @XmlElementWrapper(name = "cases")
    @XmlElement(name = "case")
    public Set<Case> cases = new TreeSet<>();
    
    @Override
    public int compareTo(Object o) {
        return(patIdHash.compareTo(((Patient)o).patIdHash));
    }

    public Patient() {
    }

    public Patient(String patId, String patIdHash) {
        this.patId = patId;
        this.patIdHash = patIdHash;
    }

}