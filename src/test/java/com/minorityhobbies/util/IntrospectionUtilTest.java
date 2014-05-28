package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;

import java.beans.IntrospectionException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class IntrospectionUtilTest {
	public class Foo {
		private String member;
		private int value;
		private Date timestamp;
		
		public String getMember() {
			return member;
		}

		public void setMember(String member) {
			this.member = member;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public Date getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Date timestamp) {
			this.timestamp = timestamp;
		}
	}
	
	public class Bar extends Foo {
		private String derivedValue;

		public String getDerivedValue() {
			return derivedValue;
		}

		public void setDerivedValue(String derivedValue) {
			this.derivedValue = derivedValue;
		}
	}
	
	private IntrospectionUtil introspectionUtil;
	
	@Before
	public void setUp() throws Exception {
		introspectionUtil = new IntrospectionUtil(Foo.class);
	}

	@Test
	public void testGetNamedProperty() {
		Foo testFoo = new Foo();
		String v1 = "v1Value";
		testFoo.setMember(v1);
		assertEquals(v1, introspectionUtil.getNamedProperty("member", testFoo));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetNamedPropertyForMissingProperty() {
		Foo testFoo = new Foo();
		String v1 = "v1Value";
		testFoo.setMember(v1);
		assertEquals(v1, introspectionUtil.getNamedProperty("v1", testFoo));
	}
	
	@Test
	public void testMapRow() {
		Foo testFoo = new Foo();
		String v1 = "v1Value";
		int value = 42;
		Date ts = new Date();
		testFoo.setMember(v1);
		testFoo.setValue(value);
		testFoo.setTimestamp(ts);
		Map<String, Object> map = introspectionUtil.propertiesToMap(testFoo);
		assertEquals(map.toString(), 3, map.size());
		assertEquals(v1, map.get("member"));
		assertEquals(value, map.get("value"));
		assertEquals(ts, map.get("timestamp"));
	}
	
	@Test
	public void testMapToProperty() {
		Foo testFoo = new Foo();
		String v1 = "v1Value";
		Map<String, Object> map = new HashMap<>();
		map.put("member", v1);
		introspectionUtil.mapToProperties(testFoo, map);
		assertEquals(v1, testFoo.getMember());
	}
	
	@Test
	public void testMapToPropertyOfNumericType() {
		Foo testFoo = new Foo();
		Map<String, Object> map = new HashMap<>();
		map.put("value", 55);
		introspectionUtil.mapToProperties(testFoo, map);
		assertEquals(55, testFoo.getValue());
	}
	
	@Test
	public void testMapToPropertyOfDateType() {
		Foo testFoo = new Foo();
		Map<String, Object> map = new HashMap<>();
		map.put("value", 55);
		introspectionUtil.mapToProperties(testFoo, map);
		assertEquals(55, testFoo.getValue());
	}
	
	@Test
	public void testMapSubclassRow() throws IntrospectionException {
		introspectionUtil = new IntrospectionUtil(Bar.class);
		
		Bar testBar = new Bar();
		String v1 = "v1Value";
		int value = 42;
		Date ts = new Date();
		testBar.setMember(v1);
		testBar.setValue(value);
		testBar.setTimestamp(ts);
		testBar.setDerivedValue("dv");
		Map<String, Object> map = introspectionUtil.propertiesToMap(testBar);
		assertEquals(map.toString(), 4, map.size());
		assertEquals(v1, map.get("member"));
		assertEquals(value, map.get("value"));
		assertEquals(ts, map.get("timestamp"));
		assertEquals("dv", map.get("derivedValue"));
	}
}
