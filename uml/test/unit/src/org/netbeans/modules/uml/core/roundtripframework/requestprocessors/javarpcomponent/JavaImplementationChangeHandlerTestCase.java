
/*
 * Created on Nov 24, 2003
 *
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.netbeans.modules.uml.core.roundtripframework.requestprocessors.javarpcomponent;

import org.netbeans.modules.uml.core.metamodel.core.constructs.IClass;
import org.netbeans.modules.uml.core.AbstractUMLTestCase;
import org.netbeans.modules.uml.core.metamodel.infrastructure.coreinfrastructure.IImplementation;
import org.netbeans.modules.uml.core.metamodel.infrastructure.coreinfrastructure.IInterface;
import org.netbeans.modules.uml.core.metamodel.infrastructure.coreinfrastructure.IOperation;
import org.netbeans.modules.uml.core.metamodel.infrastructure.coreinfrastructure.IParameter;
import org.netbeans.modules.uml.core.support.umlutils.ETList;
/**
 * @author schandra
 *
 */
public class JavaImplementationChangeHandlerTestCase extends
    AbstractUMLTestCase
{
    public static void main(String args[])
    {
        junit.textui.TestRunner.run(JavaImplementationChangeHandlerTestCase.class);
    }
    
    public static IClass    clazz;
    private IInterface      intf;
    private IImplementation impl;
    

/**
 * ImplementationOperationChangeTestCase
 */
    public void testChangeInInterface()
    {
// TODO: conover - temporary until fixed            
//        IOperation op;
//        IParameter pm1,pm2;
//        intf.addOperation(op = intf.createOperation("int", "delhi"));
//        op.removeAllParameters();
//        assertEquals(2, intf.getOperations().size());
//        assertEquals(3, clazz.getOperations().size());
//        
//        ETList<IOperation> interOps = intf.getOperationsByName("delhi");
//        IOperation interOp  = interOps.get(0);
//        
//        ETList<IOperation> clazzOps = clazz.getOperationsByName("delhi");
//        IOperation clazzOp  = clazzOps.get(0);
//        
//        assertEquals("delhi", interOp.getName());
//        assertEquals("delhi", clazzOp.getName());
//        
//        interOp.setName("bombay");
//        
//        assertEquals("bombay", interOp.getName());
//        assertEquals("bombay", clazzOp.getName());
//        
//        interOp.addParameter(pm1 = interOp.createParameter("int","intType"));
//        interOp.addParameter(pm2 = interOp.createParameter("double","doubleType"));
//        
//        assertEquals(2,interOp.getParameters().size());
//        assertEquals(2,clazzOp.getParameters().size());
//        
//        interOp.removeParameter(pm1);
//        
//        assertEquals(1,interOp.getParameters().size());
//        assertEquals(1,clazzOp.getParameters().size());
//        
//        interOp.removeParameter(pm2);
//        
//        assertEquals(0,interOp.getParameters().size());
//        assertEquals(0,clazzOp.getParameters().size());
//        
//        interOp.addParameter(interOp.createParameter("int","intType"));
//        interOp.addParameter(interOp.createParameter("String","stringType"));
//        
//        ETList<IParameter> interListOfAttribute = interOp.getParameters();
//        assertEquals(2,interListOfAttribute.size());
//        
//        ETList<IParameter> clazzListOfAttribute = clazzOp.getParameters();
//        assertEquals(2,clazzListOfAttribute.size());
//        
//        ETList<IParameter> slistOfAttribute = interOp.getParameters();
//        IParameter param  = slistOfAttribute.get(0);
//        
//        assertEquals("int",interOp.getParameters().get(0).getTypeName());
//        assertEquals("int",clazzOp.getParameters().get(0).getTypeName());
//        param.setTypeName("double");
//        assertEquals("double",interOp.getParameters().get(0).getTypeName());
//        assertEquals("double",clazzOp.getParameters().get(0).getTypeName());
    }

/**
 * ImplementationCreateTestCase
 */
    public void testCreate()
    {
        assertEquals(2, clazz.getOperations().size());
        assertEquals("methanol", clazz.getOperations().get(1).getName());
        assertEquals(1, clazz.getOperations().get(1).getRedefinedElementCount());
        assertEquals(1, intf.getOperations().get(0).getRedefiningElementCount());
        assertEquals(1, clazz.getClientDependencies().size());
        assertEquals(1, intf.getSupplierDependencies().size());
    }
   
/**
 * ImplementationDeleteTestCase
 */
    public void testDelete()
    {
        impl.delete();
        assertEquals(2, clazz.getOperations().size());
        assertEquals("methanol", clazz.getOperations().get(1).getName());
        assertEquals(0, clazz.getOperations().get(1).getRedefinedElementCount());
        assertEquals(0, intf.getOperations().get(0).getRedefiningElementCount());
        assertEquals(0, clazz.getClientDependencies().size());
        assertEquals(0, intf.getSupplierDependencies().size());
    }
    
/**
 * ClassifierEndMoveTestCase
 */
   	public void testClassifierEndMove()
    {
        IClass newClient = createClass("NewClient");
        impl.setClient(newClient);
        assertEquals(2, clazz.getOperations().size());
        assertEquals("methanol", clazz.getOperations().get(1).getName());
        assertEquals(0, clazz.getOperations().get(1).getRedefinedElementCount());
        
        assertEquals(0, clazz.getClientDependencies().size());
        assertEquals(1, newClient.getClientDependencies().size());
        assertEquals(1, intf.getSupplierDependencies().size());
        
        assertEquals(1, intf.getOperations().get(0).getRedefiningElementCount());
        
        assertEquals(2, newClient.getOperations().size());
        assertEquals("methanol", newClient.getOperations().get(1).getName());
        assertEquals(1, newClient.getOperations().get(1).getRedefinedElementCount());
    }
    

/**
 * ContractEndMoveTestCase
 */
    public void testContractEndMove()
    {
        IInterface newInterface = createInterface("IHighGradeCement");
        newInterface.addOperation(newInterface.createOperation("char", "ethanol"));
        
        impl.setContract(newInterface);
        
        assertEquals(1, clazz.getClientDependencies().size());
        assertEquals(1, newInterface.getSupplierDependencies().size());
        assertEquals(0, intf.getSupplierDependencies().size());
        
        assertEquals(3, clazz.getOperations().size());
        assertEquals("methanol", clazz.getOperations().get(1).getName());
        assertEquals(0, clazz.getOperations().get(1).getRedefinedElementCount());
        assertEquals("ethanol", clazz.getOperations().get(2).getName());
        assertEquals(1, clazz.getOperations().get(2).getRedefinedElementCount());
        
        assertEquals(0, intf.getOperations().get(0).getRedefiningElementCount());
        
        assertEquals(1,
            newInterface.getOperations().get(0).getRedefiningElementCount());
    }
    
    protected void setUp()
    {
        clazz = createClass("Concrete");
        intf  = createInterface("ICement");
        IOperation op;
        intf.addOperation(op = intf.createOperation("int", "methanol"));
        op.setIsAbstract(true);
        impl  = (IImplementation) relFactory.createImplementation(clazz, intf, project).getParamTwo();
    }
}

