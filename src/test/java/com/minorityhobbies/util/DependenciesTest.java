package com.minorityhobbies.util;

import static org.junit.Assert.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.Test;

public class DependenciesTest {
	private static interface DepTest {
		int getId();
	}

	private static final class DepTestImpl implements DepTest {
		private final int id;

		public DepTestImpl(int id) {
			super();
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	@SuppressWarnings("unused")
	private static final class DepTestModule {
		private final DepTestImpl depTestImpl;

		public DepTestModule() {
			super();
			this.depTestImpl = new DepTestImpl(2);
		}

		public DepTest getDepTest() {
			return depTestImpl;
		}
	}

	private static interface IndirectDep {
		double getAverage();
	}

	public static final class IndirectDepImpl implements IndirectDep {
		private final double average;

		public IndirectDepImpl(double average) {
			super();
			this.average = average;
		}

		public double getAverage() {
			return average;
		}
	}

	@SuppressWarnings("unused")
	private static final class DepTestReferenceModule {
		private IndirectDep indirectDep;

		public DepTestReferenceModule(DepTest depTest) {
			indirectDep = new IndirectDepImpl(depTest.getId());
		}

		public IndirectDep getIndirectDep() {
			return indirectDep;
		}
	}

	@SuppressWarnings("unused")
	private static final class DepTestSecondModule {
		private final DepTest depTest2;

		public DepTestSecondModule() {
			super();
			depTest2 = new DepTestImpl(3);
		}

		public DepTest getDepTest2() {
			return depTest2;
		}
	}

	@SuppressWarnings("unused")
	private static final class DepTestAnnotatedModule1 {
		private final DepTest depTestA1;

		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.PARAMETER, ElementType.METHOD })
		@interface Marker1 {
		}

		public DepTestAnnotatedModule1() {
			super();
			depTestA1 = new DepTestImpl(4);
		}

		@Marker1
		public DepTest getDepTestA1() {
			return depTestA1;
		}
	}

	@SuppressWarnings("unused")
	private static final class DepTestAnnotatedModule2 {
		private final DepTest depTestA2;

		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.PARAMETER, ElementType.METHOD })
		@interface Marker2 {
		}

		public DepTestAnnotatedModule2() {
			super();
			depTestA2 = new DepTestImpl(5);
		}

		@Marker2
		public DepTest getDepTestA1() {
			return depTestA2;
		}
	}

	@SuppressWarnings("unused")
	private static final class DepTestSpecificDependentModule {
		private final IndirectDep indirectDep;

		public DepTestSpecificDependentModule(
				@com.minorityhobbies.util.DependenciesTest.DepTestAnnotatedModule2.Marker2 DepTest d2) {
			super();
			this.indirectDep = new IndirectDepImpl(d2.getId());
		}

		public IndirectDep getIndirectDep() {
			return indirectDep;
		}
	}

	@SuppressWarnings("unused")
	private static final class DepTestUnannotatedDependentModule {
		private final IndirectDep indirectDep;

		public DepTestUnannotatedDependentModule(DepTest d) {
			super();
			this.indirectDep = new IndirectDepImpl(d.getId());
		}

		public IndirectDep getIndirectDep() {
			return indirectDep;
		}
	}

	@SuppressWarnings("unused")
	private static final class TestConfigModule {
		private final String property1;
		private final String property2;

		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.PARAMETER, ElementType.METHOD })
		@interface Value1 {
		}

		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.PARAMETER, ElementType.METHOD })
		@interface Value2 {
		}

		public TestConfigModule() {
			super();
			this.property1 = "p1";
			this.property2 = "p2";
		}

		@Value1
		public String getProperty1() {
			return property1;
		}

		@Value2
		public String getProperty2() {
			return property2;
		}
	}

	@SuppressWarnings("unused")
	private static final class DepTestAnnotatedMultipleModule {
		private final IndirectDep indirectDep;
		private final String derivedProperty;

		@Retention(RetentionPolicy.RUNTIME)
		@Target({ ElementType.PARAMETER, ElementType.METHOD })
		@interface Value3 {
		}

		public DepTestAnnotatedMultipleModule(
				DepTest depTest,
				@com.minorityhobbies.util.DependenciesTest.TestConfigModule.Value1 String p1,
				@com.minorityhobbies.util.DependenciesTest.TestConfigModule.Value2 String p2) {
			super();
			this.indirectDep = new IndirectDepImpl(depTest.getId());
			this.derivedProperty = p1 + "-" + p2;
		}

		public IndirectDep getIndirectDep() {
			return indirectDep;
		}

		@Value3
		public String getDerivedProperty() {
			return derivedProperty;
		}
	}

	@Test
	public void shouldResolveFromSingleton() throws DependencyException {
		DepTestImpl singleton = new DepTestImpl(1);
		try (Dependencies deps = new Dependencies(new Object[] { singleton },
				new Class<?>[0])) {
			deps.bootstrap();
			DepTest resolved = deps.resolveDependency(DepTest.class);
			assertEquals(singleton.getId(), resolved.getId());
		}
	}

	@Test
	public void shouldResolveFromModuleGetter() throws DependencyException {
		try (Dependencies deps = new Dependencies(new Object[0],
				new Class<?>[] { DepTestModule.class })) {
			deps.bootstrap();
			DepTest resolved = deps.resolveDependency(DepTest.class);
			assertEquals(2, resolved.getId());
		}
	}

	@Test
	public void shouldResolveIndirectlyFromModule() throws DependencyException {
		try (Dependencies deps = new Dependencies(new Object[0],
				new Class<?>[] { DepTestModule.class,
						DepTestReferenceModule.class })) {
			deps.bootstrap();
			IndirectDep resolved = deps.resolveDependency(IndirectDep.class);
			assertEquals(2.0, resolved.getAverage(), 0.1);
		}
	}

	@Test(expected = DependencyException.class)
	public void shouldFailWhereMultipleDependenciesOfSameTypeAreAvailable()
			throws DependencyException {
		try (Dependencies deps = new Dependencies(
				new Object[0],
				new Class<?>[] { DepTestModule.class,
						DepTestReferenceModule.class, DepTestSecondModule.class })) {
			deps.bootstrap();
		}
	}

	@Test
	public void shouldResolveFirstOfMultipleDependenciesOfTypeWhereAnnotated()
			throws DependencyException {
		try (Dependencies deps = new Dependencies(new Object[0],
				new Class<?>[] { DepTestAnnotatedModule1.class,
						DepTestAnnotatedModule2.class })) {
			deps.bootstrap();

			DepTest depTest1 = deps
					.resolveDependency(
							DepTest.class,
							com.minorityhobbies.util.DependenciesTest.DepTestAnnotatedModule1.Marker1.class);
			assertEquals(4, depTest1.getId());
		}
	}

	@Test
	public void shouldResolveSecondOfMultipleDependenciesOfTypeWhereAnnotated()
			throws DependencyException {
		try (Dependencies deps = new Dependencies(new Object[0],
				new Class<?>[] { DepTestAnnotatedModule1.class,
						DepTestAnnotatedModule2.class })) {
			deps.bootstrap();

			DepTest depTest2 = deps
					.resolveDependency(
							DepTest.class,
							com.minorityhobbies.util.DependenciesTest.DepTestAnnotatedModule2.Marker2.class);
			assertEquals(5, depTest2.getId());
		}
	}

	@Test
	public void shouldInjectCorrectDependencyOfMultipleWhereAnnotated()
			throws DependencyException {
		try (Dependencies deps = new Dependencies(new Object[0],
				new Class<?>[] { DepTestAnnotatedModule1.class,
						DepTestAnnotatedModule2.class,
						DepTestSpecificDependentModule.class })) {
			deps.bootstrap();

			IndirectDep indirectDep = deps.resolveDependency(IndirectDep.class);
			assertEquals(5.0, indirectDep.getAverage(), 0.1);
		}
	}

	@Test(expected = DependencyException.class)
	public void shouldFailWhereMultipleDepsAreAvailableButNotAnnotated()
			throws DependencyException {
		try (Dependencies deps = new Dependencies(new Object[0],
				new Class<?>[] { DepTestAnnotatedModule1.class,
						DepTestAnnotatedModule2.class,
						DepTestUnannotatedDependentModule.class })) {
			deps.bootstrap();
		}
	}

	@Test
	public void shouldResolveStringPropertyWhereModulesHaveMultipleDependencies()
			throws DependencyException {
		try (Dependencies deps = new Dependencies(new Object[0],
				new Class<?>[] { DepTestModule.class, TestConfigModule.class,
						DepTestAnnotatedMultipleModule.class })) {
			deps.bootstrap();

			assertEquals(
					"p1-p2",
					deps.resolveDependency(
							String.class,
							com.minorityhobbies.util.DependenciesTest.DepTestAnnotatedMultipleModule.Value3.class));
		}
	}

	@Test
	public void shouldResolveIndirectDepWhereModulesHaveMultipleDependencies()
			throws DependencyException {
		try (Dependencies deps = new Dependencies(new Object[0],
				new Class<?>[] { DepTestModule.class, TestConfigModule.class,
						DepTestAnnotatedMultipleModule.class })) {
			deps.bootstrap();

			assertEquals(2.0, deps.resolveDependency(IndirectDep.class)
					.getAverage(), 0.1);
		}
	}

	@Test
	public void shouldResolveDependenciesInReverseOrder()
			throws DependencyException {
		try (Dependencies deps = new Dependencies(new Object[0],
				new Class<?>[] { DepTestAnnotatedMultipleModule.class,
						TestConfigModule.class, DepTestModule.class, })) {
			deps.bootstrap();

			assertEquals(2.0, deps.resolveDependency(IndirectDep.class)
					.getAverage(), 0.1);
		}
	}
}
