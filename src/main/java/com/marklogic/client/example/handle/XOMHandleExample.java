/*
 * Copyright 2012-2016 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.example.handle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.example.cookbook.Util;
import com.marklogic.client.example.cookbook.Util.ExampleProperties;
import com.marklogic.client.extra.xom.XOMHandle;

/**
 * XOMHandleExample illustrates writing and reading content as a XOM structure
 * using the XOM extra library.  You must install the library first.
 */
public class XOMHandleExample {
	public static void main(String[] args)
	throws IOException, ValidityException, ParsingException {
		run(Util.loadProperties());
	}

	public static void run(ExampleProperties props)
	throws ValidityException, ParsingException, IOException {
		System.out.println("example: "+XOMHandleExample.class.getName());

		// use either shortcut or strong typed IO
		runShortcut(props);
		runStrongTyped(props);
	}
	public static void runShortcut(ExampleProperties props)
	throws ValidityException, ParsingException, IOException {
		String filename = "flipper.xml";

		// register the handle from the extra library
		DatabaseClientFactory.getHandleRegistry().register(
				XOMHandle.newFactory()
				);

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword,
				props.authType);

		// create a manager for documents of any format
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// read the example file
		InputStream docStream = Util.openStream("data"+File.separator+filename);
		if (docStream == null)
			throw new IOException("Could not read document example");

		// create an identifier for the document
		String docId = "/example/"+filename;

		// parse the example file with XOM
		Document writeDocument = new Builder(false).build(
				new InputStreamReader(docStream, "UTF-8"));

		// write the document
		docMgr.writeAs(docId, writeDocument);

		// ... at some other time ...

		// read the document content
		Document readDocument = docMgr.readAs(docId, Document.class);

		String rootName = readDocument.getRootElement().getQualifiedName();

		// delete the document
		docMgr.delete(docId);

		System.out.println("(Shortcut) Wrote and read /example/"+filename+
				" content with the <"+rootName+"/> root element using XOM");

		// release the client
		client.release();
	}
	public static void runStrongTyped(ExampleProperties props)
	throws ValidityException, ParsingException, IOException {
		String filename = "flipper.xml";

		// create the client
		DatabaseClient client = DatabaseClientFactory.newClient(
				props.host, props.port, props.writerUser, props.writerPassword,
				props.authType);

		// create a manager for documents of any format
		XMLDocumentManager docMgr = client.newXMLDocumentManager();

		// read the example file
		InputStream docStream = Util.openStream("data"+File.separator+filename);
		if (docStream == null)
			throw new IOException("Could not read document example");

		// create an identifier for the document
		String docId = "/example/"+filename;

		// create a handle for the document
		XOMHandle writeHandle = new XOMHandle();

		// parse the example file with XOM
		Document writeDocument = writeHandle.getBuilder().build(
				new InputStreamReader(docStream, "UTF-8"));
		writeHandle.set(writeDocument);

		// write the document
		docMgr.write(docId, writeHandle);

		// ... at some other time ...

		// create a handle to receive the document content
		XOMHandle readHandle = new XOMHandle();

		// read the document content
		docMgr.read(docId, readHandle);

		// access the document content
		Document readDocument = readHandle.get();

		String rootName = readDocument.getRootElement().getQualifiedName();

		// delete the document
		docMgr.delete(docId);

		System.out.println("(Strong Typed) Wrote and read /example/"+filename+
				" content with the <"+rootName+"/> root element using XOM");

		// release the client
		client.release();
	}
}
