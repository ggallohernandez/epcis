//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.09.15 at 01:39:48 PM KST 
//


package org.oliot.model.epcis;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import org.w3c.dom.Element;


/**
 * 
 * 				Sensor Event
 * 				captures an
 * 				event in which its (Time-series) data senses the nearby environments
 * 				or target objects (e.g. People)
 * 			
 * 
 * <p>Java class for SensorEventType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SensorEventType">
 *   &lt;complexContent>
 *     &lt;extension base="{axis.epcis.oliot.org}EPCISEventType">
 *       &lt;sequence>
 *         &lt;element name="finishTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="action" type="{axis.epcis.oliot.org}ActionType" minOccurs="0"/>
 *         &lt;element name="bizStep" type="{axis.epcis.oliot.org}BusinessStepIDType" minOccurs="0"/>
 *         &lt;element name="disposition" type="{axis.epcis.oliot.org}DispositionIDType" minOccurs="0"/>
 *         &lt;element name="readPoint" type="{axis.epcis.oliot.org}ReadPointType" minOccurs="0"/>
 *         &lt;element name="bizLocation" type="{axis.epcis.oliot.org}BusinessLocationType" minOccurs="0"/>
 *         &lt;element name="bizTransactionList" type="{axis.epcis.oliot.org}BusinessTransactionListType" minOccurs="0"/>
 *         &lt;element name="targetObject" type="{axis.epcis.oliot.org}TargetObjectType" minOccurs="0"/>
 *         &lt;element name="targetArea" type="{axis.epcis.oliot.org}TargetAreaType" minOccurs="0"/>
 *         &lt;element name="sensingList" type="{axis.epcis.oliot.org}SensingListType"/>
 *         &lt;element name="extension" type="{axis.epcis.oliot.org}SensorEventExtensionType" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SensorEventType", propOrder = {
    "finishTime",
    "action",
    "bizStep",
    "disposition",
    "readPoint",
    "bizLocation",
    "bizTransactionList",
    "targetObject",
    "targetArea",
    "sensingList",
    "extension",
    "any"
})
public class SensorEventType
    extends EPCISEventType
{

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar finishTime;
    protected ActionType action;
    protected String bizStep;
    protected String disposition;
    protected ReadPointType readPoint;
    protected BusinessLocationType bizLocation;
    protected BusinessTransactionListType bizTransactionList;
    protected String targetObject;
    protected String targetArea;
    @XmlElement(required = true)
    protected SensingListType sensingList;
    protected SensorEventExtensionType extension;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    /**
     * Gets the value of the finishTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFinishTime() {
        return finishTime;
    }

    /**
     * Sets the value of the finishTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFinishTime(XMLGregorianCalendar value) {
        this.finishTime = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link ActionType }
     *     
     */
    public ActionType getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionType }
     *     
     */
    public void setAction(ActionType value) {
        this.action = value;
    }

    /**
     * Gets the value of the bizStep property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBizStep() {
        return bizStep;
    }

    /**
     * Sets the value of the bizStep property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBizStep(String value) {
        this.bizStep = value;
    }

    /**
     * Gets the value of the disposition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisposition() {
        return disposition;
    }

    /**
     * Sets the value of the disposition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisposition(String value) {
        this.disposition = value;
    }

    /**
     * Gets the value of the readPoint property.
     * 
     * @return
     *     possible object is
     *     {@link ReadPointType }
     *     
     */
    public ReadPointType getReadPoint() {
        return readPoint;
    }

    /**
     * Sets the value of the readPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReadPointType }
     *     
     */
    public void setReadPoint(ReadPointType value) {
        this.readPoint = value;
    }

    /**
     * Gets the value of the bizLocation property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessLocationType }
     *     
     */
    public BusinessLocationType getBizLocation() {
        return bizLocation;
    }

    /**
     * Sets the value of the bizLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessLocationType }
     *     
     */
    public void setBizLocation(BusinessLocationType value) {
        this.bizLocation = value;
    }

    /**
     * Gets the value of the bizTransactionList property.
     * 
     * @return
     *     possible object is
     *     {@link BusinessTransactionListType }
     *     
     */
    public BusinessTransactionListType getBizTransactionList() {
        return bizTransactionList;
    }

    /**
     * Sets the value of the bizTransactionList property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessTransactionListType }
     *     
     */
    public void setBizTransactionList(BusinessTransactionListType value) {
        this.bizTransactionList = value;
    }

    /**
     * Gets the value of the targetObject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetObject() {
        return targetObject;
    }

    /**
     * Sets the value of the targetObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetObject(String value) {
        this.targetObject = value;
    }

    /**
     * Gets the value of the targetArea property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetArea() {
        return targetArea;
    }

    /**
     * Sets the value of the targetArea property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetArea(String value) {
        this.targetArea = value;
    }

    /**
     * Gets the value of the sensingList property.
     * 
     * @return
     *     possible object is
     *     {@link SensingListType }
     *     
     */
    public SensingListType getSensingList() {
        return sensingList;
    }

    /**
     * Sets the value of the sensingList property.
     * 
     * @param value
     *     allowed object is
     *     {@link SensingListType }
     *     
     */
    public void setSensingList(SensingListType value) {
        this.sensingList = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link SensorEventExtensionType }
     *     
     */
    public SensorEventExtensionType getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link SensorEventExtensionType }
     *     
     */
    public void setExtension(SensorEventExtensionType value) {
        this.extension = value;
    }

    /**
     * Gets the value of the any property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the any property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAny().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * {@link Element }
     * 
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

}
