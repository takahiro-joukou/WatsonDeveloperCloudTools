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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for conceptNodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="conceptNodeType">
 *   &lt;complexContent>
 *     &lt;extension base="{}chatflowNode">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="grammar" type="{}grammarType" minOccurs="0"/>
 *         &lt;element name="concept" type="{}conceptNodeType" minOccurs="0"/>
 *         &lt;element name="folder" type="{}folderNodeType" minOccurs="0"/>
 *       &lt;/choice>
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ref" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="labelRef" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "conceptNodeType", propOrder = {
    "grammarOrConceptOrFolder"
})
public class ConceptNodeType
    extends ChatflowNode
{

    @XmlElements({
        @XmlElement(name = "grammar", type = GrammarType.class),
        @XmlElement(name = "concept", type = ConceptNodeType.class),
        @XmlElement(name = "folder", type = FolderNodeType.class)
    })
    protected List<Object> grammarOrConceptOrFolder;
    @XmlAttribute(name = "description")
    protected String description;
    @XmlAttribute(name = "ref")
    protected String ref;
    @XmlAttribute(name = "labelRef")
    protected String labelRef;

    /**
     * Gets the value of the grammarOrConceptOrFolder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the grammarOrConceptOrFolder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGrammarOrConceptOrFolder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GrammarType }
     * {@link ConceptNodeType }
     * {@link FolderNodeType }
     * 
     * 
     */
    public List<Object> getGrammarOrConceptOrFolder() {
        if (grammarOrConceptOrFolder == null) {
            grammarOrConceptOrFolder = new ArrayList<Object>();
        }
        return this.grammarOrConceptOrFolder;
    }

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
     * Gets the value of the ref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRef(String value) {
        this.ref = value;
    }

    /**
     * Gets the value of the labelRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabelRef() {
        return labelRef;
    }

    /**
     * Sets the value of the labelRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabelRef(String value) {
        this.labelRef = value;
    }

}
