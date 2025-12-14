package edu.emory.eai4hi.extracthtbx.data;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author geoffrey.smith@emory.edu
 */
@XmlRootElement
public class Case implements Comparable<Object> {

    @XmlAttribute
    public String accNo;
    @XmlAttribute
    public String accNoHash;
    @XmlAttribute
    public Date collectionDate;
    @XmlAttribute
    public Date recallInitiatedDate;
    @XmlTransient
    public Map<String, Slide> slideMap = new TreeMap<>();
    @XmlElementWrapper(name = "slides")
    @XmlElement(name = "slide")
    public Set<Slide> slides = new TreeSet<>();
    
    @Override
    public int compareTo(Object o) {
        return(collectionDate.compareTo(((Case)o).collectionDate));
    }

    public Case() {
    }

    public Case(String accNo, String accNoHash, Date collectionDate) {
        this.accNo = accNo;
        this.accNoHash = accNoHash;
        this.collectionDate = collectionDate;
    }

}