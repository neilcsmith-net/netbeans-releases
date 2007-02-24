package org.netbeans.modules.uml.parser.java;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.netbeans.modules.uml.parser.java.classtest.BasicClassTest;
import org.netbeans.modules.uml.parser.java.classtest.AnanymousClassTest;
import org.netbeans.modules.uml.parser.java.classtest.ClassBlockTest;
import org.netbeans.modules.uml.parser.java.classtest.ClassContainsInterfaceTest;
import org.netbeans.modules.uml.parser.java.classtest.GeneralizationImplementationClassTest;
import org.netbeans.modules.uml.parser.java.classtest.GeneralizationClassTest;
import org.netbeans.modules.uml.parser.java.classtest.ImplementationClassTest;
import org.netbeans.modules.uml.parser.java.classtest.MultipleClassTest;
import org.netbeans.modules.uml.parser.java.classtest.NestedClassTest;

public class ClassTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());

	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Java Parser Class Tests");
		suite.addTest(new TestSuite(AnanymousClassTest.class));
		suite.addTest(new TestSuite(ClassBlockTest.class));
		suite.addTest(new TestSuite(ClassContainsInterfaceTest.class));
		suite.addTest(new TestSuite(BasicClassTest.class));
		suite
				.addTest(new TestSuite(
						GeneralizationImplementationClassTest.class));
		suite.addTest(new TestSuite(GeneralizationClassTest.class));
		suite.addTest(new TestSuite(ImplementationClassTest.class));
		suite.addTest(new TestSuite(MultipleClassTest.class));
		suite.addTest(new TestSuite(NestedClassTest.class));
		return suite;
	}
}
