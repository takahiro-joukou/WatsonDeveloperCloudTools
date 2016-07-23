//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.04.24 at 09:06:12 AM JST 
//


package watson.dialog.tools.model.dao.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for profileValueRuleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileValueRuleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="subrules" type="{}profileValueSubruleType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="engaging" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="folderid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="gid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="match" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileValueRuleType", propOrder = {
    "description",
    "subrules"
})
public class ProfileValueRuleType {

    protected String description;
    protected List<ProfileValueSubruleType> subrules;
    @XmlAttribute(name = "engaging", required = true)
    protected int engaging;
    @XmlAttribute(name = "folderid")
    protected String folderid;
    @XmlAttribute(name = "gid")
    protected String gid;
    @XmlAttribute(name = "match")
    protected String match;
    @XmlAttribute(name = "type")
    protected String type;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the subrules property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subrules property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSubrules().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProfileValueSubruleType }
     * 
     * 
     */
    public List<ProfileValueSubruleType> getSubrules() {
        if (subrules == null) {
            subrules = new ArrayList<ProfileValueSubruleType>();
        }
        return this.subrules;
    }

    /**
     * Gets the value of the engaging property.
     * 
     */
    public int getEngaging() {
        return engaging;
    }

    /**
     * Sets the value of the engaging property.
     * 
     */
    public void setEngaging(int value) {
        this.engaging = value;
    }

    /**
     * Gets the value of the folderid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolderid() {
        return folderid;
    }

    /**
     * Sets the value of the folderid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolderid(String value) {
        this.folderid = value;
    }

    /**
     * Gets the value of the gid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGid() {
        return gid;
    }

    /**
     * Sets the value of the gid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGid(String value) {
        this.gid = value;
    }

    /**
     * Gets the value of the match property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMatch() {
        return match;
    }

    /**
     * Sets the value of the match property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMatch(String value) {
        this.match = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

}
