//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.03.30 at 02:58:34 PM MSK 
//


package loader.comm.model.wsdl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CDEPO" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="CDOR" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="MDATE" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cdepo",
    "cdor",
    "mdate"
})
@XmlRootElement(name = "Z63L_AS_EREPORT_GETOPER")
public class Z63LASEREPORTGETOPER {

    @XmlElement(name = "CDEPO")
    protected int cdepo;
    @XmlElement(name = "CDOR")
    protected int cdor;
    @XmlElement(name = "MDATE", required = true)
    protected String mdate;

    /**
     * Gets the value of the cdepo property.
     * 
     */
    public int getCDEPO() {
        return cdepo;
    }

    /**
     * Sets the value of the cdepo property.
     * 
     */
    public void setCDEPO(int value) {
        this.cdepo = value;
    }

    /**
     * Gets the value of the cdor property.
     * 
     */
    public int getCDOR() {
        return cdor;
    }

    /**
     * Sets the value of the cdor property.
     * 
     */
    public void setCDOR(int value) {
        this.cdor = value;
    }

    /**
     * Gets the value of the mdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMDATE() {
        return mdate;
    }

    /**
     * Sets the value of the mdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMDATE(String value) {
        this.mdate = value;
    }

}
