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
