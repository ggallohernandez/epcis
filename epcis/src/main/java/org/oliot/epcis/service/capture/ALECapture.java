package org.oliot.epcis.service.capture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.oliot.epcis.model.BusinessLocationType;
import org.oliot.epcis.model.ActionType;
import org.oliot.epcis.model.BusinessTransactionType;
import org.oliot.epcis.model.EPC;
import org.oliot.epcis.model.ObjectEventExtension2Type;
import org.oliot.epcis.model.ObjectEventExtensionType;
import org.oliot.epcis.model.ObjectEventType;
import org.oliot.epcis.model.QuantityElementType;
import org.oliot.epcis.model.ReadPointType;
import org.oliot.epcis.model.SourceDestType;
import org.oliot.epcis.service.ConfigurationServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

@Controller
@RequestMapping("/aleCapture")
public class ALECapture implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@RequestMapping
	public void post(HttpServletRequest request, HttpServletResponse response) {

		try {
			ConfigurationServlet.logger.info(" ECReport Capture Started.... ");
			// Identifying what the event type is
			String eventType = request.getParameter("eventType");
			// Default Event Type
			if (eventType == null)
				eventType = "ObjectEvent";

			// Get ECReport
			InputStream is = request.getInputStream();

			String xmlDocumentString = getXMLDocumentString(is);
			InputStream validateStream = getXMLDocumentInputStream(xmlDocumentString);
			// Parsing and Validating data
			String xsdPath = servletContext.getRealPath("/wsdl");
			xsdPath += "/EPCglobal-ale-1_1-ale.xsd";
			boolean isValidated = validateECReport(validateStream, xsdPath);
			if (isValidated == false) {
				return;
			}
			// Event Type branch
			if (eventType.equals("AggregationEvent")) {
				// TODO:
			} else if (eventType.equals("ObjectEvent")) {
				JSONArray epcisArray = makeObjectEvent(xmlDocumentString,
						request);
				for (int i = 0; i < epcisArray.length(); i++) {
					JSONObject epcisObject = epcisArray.getJSONObject(i);
					ObjectEventType objectEvent = makeObjectEvent(epcisObject);
					CaptureService capture = new CaptureService();
					capture.capture(objectEvent);
				}
			} else if (eventType.equals("QuantityEvent")) {
				// TODO:
			} else if (eventType.equals("TransactionEvent")) {
				// TODO:
			} else if (eventType.equals("TransformationEvent")) {
				// TODO:
			}
		} catch (IOException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
		}
	}

	private ObjectEventType makeObjectEvent(JSONObject obj) {

		try {
			ObjectEventType objectEventType = new ObjectEventType();
			if (!obj.isNull("eventTime")) {
				String eventTimeStr = obj.getString("eventTime");
				Calendar eventTime = Calendar.getInstance();
				// Example: 2014-08-11T19:57:59.717+09:00
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

				eventTime.setTime(sdf.parse(eventTimeStr));
				objectEventType.setEventTime(eventTime);
			}
			if (!obj.isNull("recordTime")) {
				String recordTimeStr = obj.getString("recordTime");
				Calendar recordTime = Calendar.getInstance();
				// Example: 2014-08-11T19:57:59.717+09:00
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

				recordTime.setTime(sdf.parse(recordTimeStr));
				objectEventType.setRecordTime(recordTime);
			}
			if (!obj.isNull("eventTimeZoneOffset")) {
				objectEventType.setEventTimeZoneOffset(obj
						.getString("eventTimeZoneOffset"));
			}
			if (!obj.isNull("epcList")) {
				JSONArray epcList = obj.getJSONArray("epcList");
				EPC[] epcs = new EPC[epcList.length()];
				for (int i = 0; i < epcList.length(); i++) {
					String epcStr = epcList.getString(i);
					EPC epc = new EPC(epcStr);
					epcs[i] = epc;
				}
				objectEventType.setEpcList(epcs);
			}
			if (!obj.isNull("action")) {
				objectEventType.setAction(new ActionType(obj
						.getString("action")));
			}
			if (!obj.isNull("bizStep")) {
				// DEBUG
				URI bizURI = new URI("http://tempuri.org");
				objectEventType.setBizStep(bizURI);
			}
			if (!obj.isNull("disposition")) {
				objectEventType.setDisposition(new URI(obj
						.getString("disposition")));
			}
			if (!obj.isNull("readPoint")) {
				JSONObject readPoint = obj.getJSONObject("readPoint");
				URI id = new URI(readPoint.getString("id"));
				ReadPointType rp = new ReadPointType();
				rp.setId(id);
				objectEventType.setReadPoint(rp);
			}
			if (!obj.isNull("bizLocation")) {
				JSONObject bizLoc = obj.getJSONObject("bizLocation");
				URI id = new URI(bizLoc.getString("id"));
				BusinessLocationType bl = new BusinessLocationType();
				bl.setId(id);
				objectEventType.setBizLocation(bl);
			}
			if(!obj.isNull("bizTransactionList"))
			{
				JSONArray bizTranArr = obj.getJSONArray("bizTransactionList");
				BusinessTransactionType[] btl = new BusinessTransactionType[bizTranArr.length()];
				for( int i = 0 ; i < bizTranArr.length() ; i++ )
				{
					String bizTransaction = bizTranArr.getString(i);
					BusinessTransactionType bt = new BusinessTransactionType();
					bt.setType(new URI(bizTransaction));
					btl[i] = bt;
				}
				objectEventType.setBizTransactionList(btl);
			}
			ObjectEventExtensionType oeet = new ObjectEventExtensionType();
			if(!obj.isNull("quantityList"))
			{
				JSONArray quantityArr = obj.getJSONArray("quantityList");
				QuantityElementType[] qel = new QuantityElementType[quantityArr.length()];
				for( int i = 0 ; i < quantityArr.length() ; i++ )
				{
					JSONObject quantityObj = quantityArr.getJSONObject(i);
					QuantityElementType qe = new QuantityElementType();
					if(!quantityObj.isNull("epcClass"))
					{
						qe.setEpcClass(new URI(quantityObj.getString("epcClass")));
					}
					if(!quantityObj.isNull("quantity"))
					{
						qe.setQuantity(Float.parseFloat(quantityObj.getString("quantity")));
					}
					if(!quantityObj.isNull("uom"))
					{
						qe.setUom(new URI(quantityObj.getString("uom")));
					}
					qel[i] = qe;
				}
				oeet.setQuantityList(qel);
			}
			if(!obj.isNull("sourceList"))
			{
				JSONArray sourceArr = obj.getJSONArray("sourceList");
				SourceDestType[] sdtl = new SourceDestType[sourceArr.length()];
				for(int i = 0 ; i < sourceArr.length() ; i++ )
				{
					String source = sourceArr.getString(i);
					SourceDestType sdt = new SourceDestType();
					sdt.setType(new URI(source));
					sdtl[i] = sdt;
				}
				oeet.setSourceList(sdtl);
			}
			if(!obj.isNull("destinationList"))
			{
				JSONArray destArr = obj.getJSONArray("destinationList");
				SourceDestType[] sdtl = new SourceDestType[destArr.length()];
				for(int i = 0 ; i < destArr.length() ; i++ )
				{
					String dest = destArr.getString(i);
					SourceDestType sdt = new SourceDestType();
					sdt.setType(new URI(dest));
					sdtl[i] = sdt;
				}
				oeet.setDestinationList(sdtl);
			}
			if(!obj.isNull("extension"))
			{
				JSONArray extension = obj.getJSONArray("extension");
				ObjectEventExtension2Type oeet2 = new ObjectEventExtension2Type();
				MessageElement[] anyl = new MessageElement[1];
				MessageElement any = new MessageElement();
				for(int i = 0 ; i < extension.length() ; i++ )
				{
					JSONObject extObj = extension.getJSONObject(i);
					String[] names =JSONObject.getNames(extObj);
					if( names.length == 1 )
					{
						any.setAttribute(names[0], extObj.getString(names[0]));
					}
				}
				anyl[0] = any;
				oeet2.set_any(anyl);
				oeet.setExtension(oeet2);
			}
			objectEventType.setExtension(oeet);
			return objectEventType;
		} catch (ParseException e) {
			e.printStackTrace();
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
		} catch (MalformedURIException e) {
			e.printStackTrace();
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
		}
		return null;
	}

	private boolean validateECReport(InputStream is, String xsdPath) {
		try {
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");
			File xsdFile = new File(xsdPath);
			Schema schema = schemaFactory.newSchema(xsdFile);
			Validator validator = schema.newValidator();
			StreamSource xmlSource = new StreamSource(is);
			validator.validate(xmlSource);
			return true;
		} catch (SAXException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
			return false;
		} catch (IOException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
			return false;
		}
	}

	private JSONObject makeObjectEventBase(String ecReportString,
			HttpServletRequest request) {
		try {
			InputStream ecReportStream = getXMLDocumentInputStream(ecReportString);
			Document doc = getXMLDocument(ecReportStream);
			// get extra param : action
			String actionType = request.getParameter("action");
			// Mandatory Field : Default - OBSERVE
			if (actionType == null)
				actionType = "OBSERVE";
			// Optional Field
			String bizStep = request.getParameter("bizStep");
			// Optional Field
			String disposition = request.getParameter("disposition");
			// Optional Field
			String readPoint = request.getParameter("readPoint");
			// Optional Field
			String bizLocation = request.getParameter("bizLocation");
			// Optional Field : Comma Separated List , ~,~,~
			String bizTransactionListStr = request
					.getParameter("bizTransactionList");
			String[] bizTransactionList = null;
			if (bizTransactionListStr != null) {
				bizTransactionList = bizTransactionListStr.split(",");
				for (int i = 0; i < bizTransactionList.length; i++) {
					bizTransactionList[i] = bizTransactionList[i].trim();
				}
			}
			// Optional Field : Comma Separated List , ~,~,~
			String sourceListStr = request.getParameter("sourceList");
			String[] sourceList = null;
			if (sourceListStr != null) {
				sourceList = sourceListStr.split(",");
				for (int i = 0; i < sourceList.length; i++) {
					sourceList[i] = sourceList[i].trim();
				}
			}
			// Optional Field : Comma Separated List , ~,~,~
			String destinationListStr = request.getParameter("destinationList");
			String[] destinationList = null;
			if (destinationListStr != null) {
				destinationList = destinationListStr.split(",");
				for (int i = 0; i < destinationList.length; i++) {
					destinationList[i] = destinationList[i].trim();
				}
			}
			Calendar eventTime = getEventTime(doc);
			Calendar recordTime = new GregorianCalendar();
			String eventTimeZoneOffset = eventTime.getTimeZone().toString();

			JSONObject ecReportObject = XML.toJSONObject(ecReportString);

			// Null Reports Check
			boolean isNull = isReportNull(ecReportObject);
			if (isNull == true) {
				return null;
			}

			// Start to make EPCIS Object
			JSONObject epcisObject = new JSONObject();
			if (eventTime != null) {
				Date eventDate = eventTime.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				String eventTimeStr = sdf.format(eventDate);
				epcisObject.put("eventTime", eventTimeStr);
			}
			if (eventTimeZoneOffset != null) {
				Date eventDate = eventTime.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("XXX");
				String eventTimeZoneOffsetStr = sdf.format(eventDate);
				epcisObject.put("eventTimeZoneOffset", eventTimeZoneOffsetStr);
			}
			if (recordTime != null) {
				Date recordDate = recordTime.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				String recordTimeStr = sdf.format(recordDate);
				epcisObject.put("recordTime", recordTimeStr);
			}
			if (actionType != null)
				epcisObject.put("action", actionType);
			if (bizStep != null)
				epcisObject.put("bizStep", bizStep);
			if (bizLocation != null)
				epcisObject.put("bizLocation", bizLocation);
			if (bizTransactionList != null) {
				JSONArray bizTranArr = new JSONArray();
				for (int i = 0; i < bizTransactionList.length; i++) {
					bizTranArr.put(bizTransactionList[i]);
				}
				epcisObject.put("bizTransactionList", bizTranArr);
			}
			if (disposition != null)
				epcisObject.put("disposition", disposition);
			if (readPoint != null)
				epcisObject.put("readPoint", readPoint);
			if (sourceList != null) {
				JSONArray sourceJSON = new JSONArray();
				for (int i = 0; i < sourceList.length; i++) {
					sourceJSON.put(sourceList[i]);
				}
				epcisObject.put("sourceList", sourceJSON);
			}

			if (destinationList != null) {
				JSONArray destJSON = new JSONArray();
				for (int i = 0; i < destinationList.length; i++) {
					destJSON.put(destinationList[i]);
				}
				epcisObject.put("destinationList", destJSON);
			}
			return epcisObject;
		} catch (ParseException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
			return null;
		}
	}

	private JSONArray makeEPCISArray(JSONObject epcisObject,
			JSONObject reportsObj) {
		String reportKey = getJSONKey(reportsObj, "report");
		JSONObject report = reportsObj.getJSONObject(reportKey);
		String groupKey = getJSONKey(report, "group");
		JSONObject group = report.getJSONObject(groupKey);
		String groupListKey = getJSONKey(group, "groupList");
		JSONObject groupList = group.getJSONObject(groupListKey);
		String memberKey = getJSONKey(groupList, "member");
		Object member = groupList.get(memberKey);
		JSONArray epcisArray = new JSONArray();
		// Each member would be one EPCIS Report
		if (member instanceof JSONObject) {
			JSONObject memberObject = (JSONObject) member;
			epcisObject = getEPCISObject(epcisObject, memberObject);
			epcisArray.put(epcisObject);
		} else if (member instanceof JSONArray) {
			JSONArray memberArray = (JSONArray) member;
			for (int i = 0; i < memberArray.length(); i++) {
				JSONObject memberObject = (JSONObject) memberArray
						.getJSONObject(i);
				epcisObject = getEPCISObject(epcisObject, memberObject);
				epcisArray.put(epcisObject);
			}
		}
		return epcisArray;
	}

	private JSONObject getEPCISObject(JSONObject base, JSONObject memberObject) {
		// Make epcList
		String epcKey = getJSONKey(memberObject, "epc");
		String epc = memberObject.getString(epcKey);
		JSONArray epcJSONArr = new JSONArray();
		epcJSONArr.put(epc);
		base.put("epcList", epcJSONArr);

		// Make Extension
		String extensionKey = getJSONKey(memberObject, "extension");
		// Extension
		if (extensionKey != null) {
			JSONObject extension = memberObject.getJSONObject(extensionKey);
			JSONArray extensionArr = getExtensionArray(extension);
			base.put("extension", extensionArr);
		}
		return base;
	}

	private JSONArray getExtensionArray(JSONObject extension) {
		String fieldListKey = getJSONKey(extension, "fieldList");
		JSONObject fieldList = extension.getJSONObject(fieldListKey);
		String fieldKey = getJSONKey(fieldList, "field");
		Object field = fieldList.get(fieldKey);
		JSONArray extensionArr = new JSONArray();
		// Single extension field
		if (field instanceof JSONObject) {
			JSONObject fieldObject = (JSONObject) field;
			JSONObject extensionObj = getExtensionObject(fieldObject);
			extensionArr.put(extensionObj);
		} else if (field instanceof JSONArray) {
			JSONArray fieldArray = (JSONArray) field;
			for (int i = 0; i < fieldArray.length(); i++) {
				JSONObject fieldObject = (JSONObject) fieldArray.get(i);
				JSONObject extensionObj = getExtensionObject(fieldObject);
				extensionArr.put(extensionObj);
			}
		}
		return extensionArr;
	}

	private JSONObject getExtensionObject(JSONObject fieldObject) {
		String nameKey = getJSONKey(fieldObject, "name");
		String name = fieldObject.getString(nameKey);
		String valueKey = getJSONKey(fieldObject, "value");
		Object value = fieldObject.get(valueKey);
		JSONObject extensionObj = new JSONObject();
		extensionObj.put(name, value.toString());
		return extensionObj;
	}

	private JSONArray makeObjectEvent(String ecReportString,
			HttpServletRequest request) {

		// Make Object Event Base
		JSONObject epcisObject = makeObjectEventBase(ecReportString, request);
		if (epcisObject == null)
			return null;

		JSONObject ecReportObject = XML.toJSONObject(ecReportString);

		String ecReportsKey = getJSONKey(ecReportObject, "ECReports");
		JSONObject ecReports = ecReportObject.getJSONObject(ecReportsKey);
		String reportsKey = getJSONKey(ecReports, "reports");
		Object reports = ecReports.get(reportsKey);

		JSONArray epcisArray = new JSONArray();
		// Single Reports
		if (reports instanceof JSONObject) {
			JSONObject reportsObj = (JSONObject) reports;
			epcisArray = makeEPCISArray(epcisObject, reportsObj);
		} else if (reports instanceof JSONArray) // Multiple Reports
		{
			JSONArray reportsArr = (JSONArray) reports;
			for (int i = 0; i < reportsArr.length(); i++) {
				JSONObject reportsObj = reportsArr.getJSONObject(i);
				epcisArray = makeEPCISArray(epcisObject, reportsObj);
			}
		}
		return epcisArray;
	}

	public String getJSONKey(JSONObject jObj, String contain) {
		String[] names = JSONObject.getNames(jObj);
		for (int i = 0; i < names.length; i++) {
			if (names[i].contains(contain)) {
				return names[i];
			}
		}
		return null;
	}

	private boolean isReportNull(JSONObject ecReportObject) {

		// Cannot Be Null
		String ecReportsKey = getJSONKey(ecReportObject, "ECReports");
		JSONObject ecReports = ecReportObject.getJSONObject(ecReportsKey);
		String reportsKey = getJSONKey(ecReports, "reports");
		Object e = ecReports.get(reportsKey);
		if (e.toString().equals("")) {
			return true;
		}
		return false;
	}

	private Calendar getEventTime(Document doc) throws ParseException {
		Element root = doc.getDocumentElement();
		String date = root.getAttribute("date");
		if (date == null)
			return null;
		Calendar eventTime = Calendar.getInstance();
		// Example: 2014-08-11T19:57:59.717+09:00
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		eventTime.setTime(sdf.parse(date));
		return eventTime;
	}

	private InputStream getXMLDocumentInputStream(String xmlString) {
		InputStream stream = new ByteArrayInputStream(
				xmlString.getBytes(StandardCharsets.UTF_8));
		return stream;
	}

	private String getXMLDocumentString(InputStream is) {
		try {
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			String xmlString = writer.toString();
			return xmlString;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Document getXMLDocument(InputStream is) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (SAXException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
			return null;
		} catch (IOException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
			return null;
		} catch (ParserConfigurationException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
			return null;
		}

	}

	public String getDataFromInputStream(ServletInputStream is)
			throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		String data = writer.toString();
		return data;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}