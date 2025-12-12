package edu.emory.eai4hi.extracthtbx.data;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author geoffrey.smith@emory.edu
 */
@XmlRootElement
public class Slide implements Comparable {
    
    @XmlAttribute
    public String partId;
    @XmlAttribute
    public Integer blockNo;
    @XmlAttribute
    public Integer slideNo;
    @XmlAttribute
    public String stain;
    @XmlAttribute
    public String anonSlideId;
    @XmlAttribute
    public String anonSlideFileName;

    @Override
    public int compareTo(Object o) {
        if(partId.compareTo(((Slide)o).partId) != 0) { return partId.compareTo(((Slide)o).partId); }
        else if(blockNo.compareTo(((Slide)o).blockNo) != 0) { return blockNo.compareTo(((Slide)o).blockNo); }
        else { return slideNo.compareTo(((Slide)o).slideNo); }
    }

    public Slide() {
    }

    public Slide(String partId, int blockNo, int slideNo, String stain) {
        this.partId = partId;
        this.blockNo = blockNo;
        this.slideNo = slideNo;
        this.stain = stain;
    }
    
}