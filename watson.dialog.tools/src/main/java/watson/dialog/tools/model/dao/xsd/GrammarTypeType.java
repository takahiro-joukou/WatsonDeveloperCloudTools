//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.04.24 at 09:06:12 AM JST 
//


package watson.dialog.tools.model.dao.xsd;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for grammarTypeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="grammarTypeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="VARIATIONS"/>
 *     &lt;enumeration value="JSGF"/>
 *     &lt;enumeration value="GRXML"/>
 *     &lt;enumeration value="AQL"/>
 *     &lt;enumeration value="DICT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "grammarTypeType")
@XmlEnum
public enum GrammarTypeType {

    VARIATIONS,
    JSGF,
    GRXML,
    AQL,
    DICT;

    public String value() {
        return name();
    }

    public static GrammarTypeType fromValue(String v) {
        return valueOf(v);
    }

}
