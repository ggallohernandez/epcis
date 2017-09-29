package org.oliot.epcis.converter.mongodb;

import static org.oliot.epcis.converter.mongodb.MongoWriterUtil.*;

import org.bson.BsonDocument;
import org.oliot.model.epcis.EPCISDocumentType;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
 *
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class EpcisDocumentWriteConverter {

	public BsonDocument convert(EPCISDocumentType epcisDocument, Integer gcpLength) {
		BsonDocument dbo = new BsonDocument();
		
		dbo.put("header", getEPCISHeaderObject(epcisDocument.getEPCISHeader(), gcpLength));
		
		return dbo;
	}
}
