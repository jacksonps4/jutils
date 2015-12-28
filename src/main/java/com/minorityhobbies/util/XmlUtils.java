package com.minorityhobbies.util;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;

public class XmlUtils {
    public static void streamEvaluateXPath(String xPath, Reader reader, Writer writer) throws IOException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xp = xpf.newXPath();
            XPathExpression xpe = xp.compile(xPath);

            try (BufferedReader lineReader = new BufferedReader(reader)) {
                for (String line; (line = lineReader.readLine()) != null; ) {
                    Document document = db.parse(new InputSource(new StringReader(line)));
                    String result = (String) xpe.evaluate(document, XPathConstants.STRING);
                    if (result != null && result.trim().length() > 0) {
                        writer.write(result);
                        writer.write('\n');
                    }
                }
            }

            writer.flush();
        } catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Must specify an XPath");
        }
        String xpath = args[0];
        streamEvaluateXPath(xpath, new InputStreamReader(System.in), new OutputStreamWriter(System.out));
    }
}
