package com.minorityhobbies.util;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class IntrospectionUtilTest {
	public class Foo {
		private String member;

		public String getMember() {
			return member;
		}

		public void setMember(String member) {
			this.member = member;
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
		testFoo.setMember(v1);
		Map<String, Object> map = introspectionUtil.propertiesToMap(testFoo);
		assertEquals(map.toString(), 1, map.size());
		assertEquals(v1, map.get("member"));
	}
}
