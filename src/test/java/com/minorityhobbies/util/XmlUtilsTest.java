package com.minorityhobbies.util;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlUtilsTest {
    @Test
    public void stream() throws IOException {
        String line1 = "<test><value><v1 y=\"1\" x=\"1\">a</v1></value></test>";
        String line2 = "<test><value><v1 y=\"1\" x=\"2\">b</v1></value></test>";
        String line3 = "<test><value><v1 y=\"2\" x=\"3\">c</v1></value></test>";
        String line4 = "<test><value><v1 y=\"1\" x=\"3\">d</v1></value></test>";
        String line5 = "<test><value><v1 y=\"3\" x=\"3\">e</v1></value></test>";
        String file = String.format("%s%n%s%n%s%n%s%n%s%n", line1, line2, line3, line4, line5);
        StringWriter writer = new StringWriter();
        XmlUtils.streamEvaluateXPath("//test/value/v1[@y='1']/@x", new StringReader(file), writer);
        System.out.println(writer.toString());
    }
}
