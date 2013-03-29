/*
Copyright (c) 2013 Chris Wraith

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.minorityhobbies.util;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

public class MBeanUtilities {
	private MBeanUtilities() {
	}

	public static void registerMBeanWithoutFail(String beanName, Object bean) {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName name = new ObjectName(beanName);
			mbs.registerMBean(bean, name);
		} catch (Exception e) {
		}
	}

	public static void registerMBean(String beanName, Object bean)
			throws MalformedObjectNameException,
			InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = new ObjectName(beanName);
		mbs.registerMBean(bean, name);
	}

	public static TabularData createTable(String typeName, String[] fieldNames,
			OpenType<?>[] fieldTypes, String[] indexFieldNames,
			List<Map<String, Object>> values) throws OpenDataException {
		return createTable(typeName, typeName, fieldNames, fieldNames,
				fieldTypes, indexFieldNames, values);
	}

	public static TabularData createTable(String typeName,
			String typeDescription, String[] fieldNames,
			String[] fieldDescriptions, OpenType<?>[] fieldTypes,
			String[] indexFieldNames, List<Map<String, Object>> values)
			throws OpenDataException {
		CompositeType type = new CompositeType(typeName, typeDescription,
				fieldNames, fieldDescriptions, fieldTypes);

		TabularType tableType = new TabularType(typeName, typeDescription,
				type, indexFieldNames);

		TabularDataSupport tableData = new TabularDataSupport(tableType);
		for (Map<String, ?> value : values) {
			CompositeData cd = new CompositeDataSupport(type, value);
			tableData.put(cd);
		}

		return tableData;
	}
}
