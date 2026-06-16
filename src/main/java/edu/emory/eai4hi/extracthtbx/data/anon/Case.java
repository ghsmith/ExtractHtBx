package edu.emory.eai4hi.extracthtbx.data.anon;

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

    @XmlTransient
    public String accNo;
    @XmlAttribute(name = "accNoStudy")
    public String accNoHash;
    @XmlTransient
    public Date collectionDate;
    @XmlAttribute
    public long collectionDayDelta;
    @XmlTransient
    public Map<String, Slide> slideMap = new TreeMap<>();
    @XmlElementWrapper(name = "slides")
    @XmlElement(name = "slide")
    public Set<Slide> slides = new TreeSet<>();
    
    @XmlElement
    public String finalDx;
    @XmlElement
    public String addendumDx;

    @Override
    public int compareTo(Object o) {
        if(collectionDayDelta > ((Case)o).collectionDayDelta) {
            return 1;
        }
        else if(collectionDayDelta < ((Case)o).collectionDayDelta) {
            return -1;
        }
        else if(collectionDayDelta == ((Case)o).collectionDayDelta) {
            return 0;
        }
        else {
            throw new RuntimeException("what!!!!");
        }
    }

    public Case() {
    }

    public Case(String accNo, String accNoHash, Date collectionDate) {
        this.accNo = accNo;
        this.accNoHash = accNoHash;
        this.collectionDate = collectionDate;
    }

}