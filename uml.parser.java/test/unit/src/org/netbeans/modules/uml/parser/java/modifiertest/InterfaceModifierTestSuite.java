package org.netbeans.modules.uml.parser.java.modifiertest;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.netbeans.modules.uml.parser.java.modifiertest.interfacemodifiertest.PrivateNestedInterfaceTest;
import org.netbeans.modules.uml.parser.java.modifiertest.interfacemodifiertest.ProtectedNestedInterfaceTest;
import org.netbeans.modules.uml.parser.java.modifiertest.interfacemodifiertest.PublicNestedInterfaceTest;
import org.netbeans.modules.uml.parser.java.modifiertest.interfacemodifiertest.PublicInterfaceTest;
import org.netbeans.modules.uml.parser.java.modifiertest.interfacemodifiertest.StaticNestedInterfaceTest;
import org.netbeans.modules.uml.parser.java.modifiertest.interfacemodifiertest.StrictfpNestedInterfaceTest;
import org.netbeans.modules.uml.parser.java.modifiertest.interfacemodifiertest.StrictfpInterfaceTest;

public class InterfaceModifierTestSuite {

	public static void main(String[] args) {
		TestRunner.run(suite());

	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Java Parser Interface Modifier Tests");

		suite.addTest(new TestSuite(PrivateNestedInterfaceTest.class));
		suite.addTest(new TestSuite(ProtectedNestedInterfaceTest.class));
		suite.addTest(new TestSuite(PublicNestedInterfaceTest.class));
		suite.addTest(new TestSuite(PublicInterfaceTest.class));
		suite.addTest(new TestSuite(StaticNestedInterfaceTest.class));
		suite.addTest(new TestSuite(StrictfpNestedInterfaceTest.class));
		suite.addTest(new TestSuite(StrictfpInterfaceTest.class));
		return suite;
	}
}
