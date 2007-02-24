/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.

 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.uml.project.ui.nodes;

import org.netbeans.modules.uml.core.eventframework.IEventPayload;
import org.netbeans.modules.uml.core.support.umlsupport.Log;
import java.awt.dnd.DnDConstants;

import java.util.List;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.Repository;
import org.openide.loaders.DataObject;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import java.awt.datatransfer.Transferable;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.Action;
import javax.swing.JSeparator;
import java.io.IOException;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import org.netbeans.modules.uml.ui.controls.projecttree.IProjectTreeDragVerify;
import org.netbeans.modules.uml.ui.controls.projecttree.IProjectTreeEventDispatcher;
import org.netbeans.modules.uml.ui.controls.projecttree.IProjectTreeItem;
import org.netbeans.modules.uml.ui.controls.projecttree.ProjectTreeDragVerifyImpl;
import org.netbeans.modules.uml.ui.support.DispatchHelper;
import org.netbeans.modules.uml.ui.support.projecttreesupport.ITreeItem;
import org.netbeans.modules.uml.ui.support.ADTransferable;
import org.netbeans.modules.uml.ui.support.diagramsupport.IProxyDiagramManager;
import org.netbeans.modules.uml.ui.support.diagramsupport.ProxyDiagramManager;
import org.netbeans.modules.uml.core.metamodel.core.foundation.IElement;
import org.netbeans.modules.uml.core.metamodel.core.foundation.INamedElement;
import org.netbeans.modules.uml.core.metamodel.core.foundation.INamespace;
import org.netbeans.modules.uml.core.support.umlutils.ETList;
import org.netbeans.modules.uml.core.support.umlutils.ETArrayList;
import org.netbeans.modules.uml.core.support.umlutils.ElementLocator;
import org.netbeans.modules.uml.core.support.umlutils.IElementLocator;
import org.netbeans.modules.uml.project.ui.nodes.actions.NewDiagramType;
import org.netbeans.modules.uml.project.ui.nodes.actions.NewPackageType;
import org.netbeans.modules.uml.project.ui.nodes.actions.NewElementType;
import org.netbeans.modules.uml.project.ui.nodes.actions.NewOperationType;
import org.netbeans.modules.uml.project.ui.nodes.actions.NewAttributeType;

import org.netbeans.modules.uml.propertysupport.DefinitionPropertyBuilder;
import org.openide.actions.*;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node.PropertySet;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.datatransfer.NewType;
import org.openide.util.datatransfer.PasteType;


/**
 * AbstractModelElementNode model element node is a base node for all nodes that
 * model represent model elements.  AbstractModelElementNode provides the basic
 * NetBeans node functionality.
 * <p>
 * The properties and children will be dynamically discovered by using the
 * model element to determine the properties and children.
 * <p>
 * The DnD operations will be supported in this class as well.
 *
 * @author Trey Spiva
 * @author Craig Conover, craig.conover@sun.com
 */
public abstract class AbstractModelElementNode extends AbstractNode
{
//	private static IPropertyDefinitionFactory mDefFactory = null;
//	private static IPropertyElementManager   mPropEleMgr = null;
    
    private boolean mIsInitalized = false;
    
    //private WeakReference < Node.PropertySet[] > mPropertySheet = null;
    
    public final static String ELEMENT_TYPE_PROJECT = "Project"; // NOI18N
    public final static String ELEMENT_TYPE_PACKAGE = "Package"; // NOI18N
    public final static String ELEMENT_TYPE_CLASS = "Class"; // NOI18N
    public final static String ELEMENT_TYPE_DIAGRAM = "Diagram"; // NOI18N
    public final static String ELEMENT_TYPE_PROXY_DIAGRAM = "ProxyDiagram"; // NOI18N
    public final static String ELEMENT_TYPE_ACTIVITY = "Activity"; // NOI18N
    public final static String ELEMENT_TYPE_STATE_MACHINE = "StateMachine"; // NOI18N
    public final static String ELEMENT_TYPE_INTERACTION = "Interaction"; // NOI18N
    public final static String ELEMENT_TYPE_INTERFACE = "Interface"; // NOI18N
    public final static String ELEMENT_TYPE_DATA_TYPE = "DataType"; // NOI18N
	public final static String ELEMENT_TYPE_ALIASED_TYPE = "AliasedType"; // NOI18N
    public final static String ELEMENT_TYPE_ATTRIBUTE = "Attribute"; // NOI18N
    public final static String ELEMENT_TYPE_OPERATION = "Operation"; // NOI18N
    public final static String ELEMENT_TYPE_PART_FACADE = "PartFacade"; // NOI18N
    public final static String ELEMENT_TYPE_ARTIFACT = "Artifact"; // NOI18N
    public final static String ELEMENT_TYPE_NODE = "Node"; // NOI18N
	public final static String ELEMENT_TYPE_ACTOR = "Actor"; // NOI18N
    public final static String ELEMENT_TYPE_ENUMERATION = "Enumeration"; // NOI18N
    public final static String ELEMENT_TYPE_DERIVATION_CLASSIFIER = "DerivationClassifier"; // NOI18N
	public final static String ELEMENT_TYPE_ASSOCIATION_CLASS = "AssociationClass"; // NOI18N
    public final static String ELEMENT_TYPE_COLLABORATION = "Collaboration"; // NOI18N
	public final static String ELEMENT_TYPE_USECASE = "UseCase"; // NOI18N
	public final static String ELEMENT_TYPE_ACTIVITYGROUP = "ActivityGroup"; // NOI18N
	public final static String ELEMENT_TYPE_FINALNODE = "FinalNode"; // NOI18N
	public final static String ELEMENT_TYPE_FINALSTATE = "FinalState"; // NOI18N
	public final static String ELEMENT_TYPE_FORKNODE = "ForkNode"; // NOI18N
	public final static String ELEMENT_TYPE_INITIALNODE = "InitialNode"; // NOI18N
	public final static String ELEMENT_TYPE_INVOCATIONNODE = "InvocationNode"; // NOI18N
	public final static String ELEMENT_TYPE_LIFELINE = "Lifeline"; // NOI18N
	public final static String ELEMENT_TYPE_STEREOTYPE = "Stereotype"; // NOI18N
	public final static String ELEMENT_TYPE_COMMENT = "Comment"; // NOI18N
	public final static String ELEMENT_TYPE_DEPLOYMENTSPECIFICATION = "DeploymentSpecification"; // NOI18N
	public final static String ELEMENT_TYPE_COMPONENT = "Component"; // NOI18N
	
	
    public final static String ELEMENT_TYPE_SOURCE_FILE_ARTIFACT = "SourcFileArtifact"; // NOI18N
    public static final String ELEMENT_TYPE_DEPENDENCY = "Dependency"; // NOI18N
    public static final String ELEMENT_TYPE_REALIZATION = "Realization"; // NOI18N
    public static final String ELEMENT_TYPE_USAGE = "Usage"; // NOI18N
    public static final String ELEMENT_TYPE_PERMISSION = "Permission"; // NOI18N
    public static final String ELEMENT_TYPE_ABSTRACTION = "Abstraction"; // NOI18N
    public static final String ELEMENT_TYPE_GENERALIZATION = "Generalization"; // NOI18N
    public static final String ELEMENT_TYPE_ASSOCIATION = "Association"; // NOI18N
    public static final String ELEMENT_TYPE_AGGREGATION = "Aggregation"; // NOI18N
    
    public final static int NEW_TYPE_DIAGRAM = 0;
    public final static int NEW_TYPE_PACKAGE = 1;
    public final static int NEW_TYPE_ELEMENT = 2;
    public final static int NEW_TYPE_ATTRIBUTE = 3;
    public final static int NEW_TYPE_OPERATION = 4;
    
    public final static Integer[] AVAILABLE_NEW_TYPES = new Integer[]
    {
        NEW_TYPE_DIAGRAM, NEW_TYPE_PACKAGE, NEW_TYPE_ELEMENT,
                NEW_TYPE_ATTRIBUTE, NEW_TYPE_OPERATION
    };
    
    public final static int TOTAL_NEW_TYPES = AVAILABLE_NEW_TYPES.length;
    
    public final static String ADDIN_ID_ASSOCIATE_WITH = "AssociateWith"; // NOI18N
    public final static String ADDIN_ID_DEPENDENCY_DIAGRAM = "DependencyDiagram"; // NOI18N
    public final static String ADDIN_ID_DIAGRAM_CREATOR = "DiagramCreator"; // NOI18N
    public final static String ADDIN_ID_GENERATOR_CODE = "GenerateCode"; // NOI18N
    public final static String ADDIN_ID_REDEFINE_OPERATIONS = "RedefineOperations"; // NOI18N
    public final static String ADDIN_ID_RE_OPERATION = "REOperation"; // NOI18N
    public final static String ADDIN_ID_SHOW_AS_XML = "ShowAsXML"; // NOI18N
    
    //Cache to improve performance
	Action newAction=null;
    
    /**
     * Create a new abstract model element node.
     */
    //public AbstractModelElementNode(Node node)
    public AbstractModelElementNode()
    {
        //super(node, new Children.SortedArray());
        this(new UMLChildren());
    }
    
    public AbstractModelElementNode(Children ch)
    {
        //super(node, new Children.SortedArray());
        this(ch, null);
    }
    
    /**
     * Create a new abstract model element node and associated lookup.
     *
     * @param lookup The lookup to provide content of Node.getLookup() and also
     *               getCookie(java.lang.Class).
     */
    //public AbstractModelElementNode(Node node, Lookup lookup)
    public AbstractModelElementNode(Lookup lookup)
    {
        //super(node, new Children.SortedArray(), lookup);
        this(new UMLChildren(), lookup);
    }
    
    public AbstractModelElementNode(Children ch, Lookup lookup)
    {
        super(ch, lookup);
    }
    
    /**
     * Retrieve the model element associated with the node.
     *
     * @return The associated model element.
     * @see IElement
     */
    public abstract IElement getModelElement();
    
    /**
     * Retrieve the meta-data name for the model element.
     *
     * @param The meta data name.
     */
    public String getElementType()
    {
        String retVal = ""; // NOI18N
        
        IElement element = getModelElement();
        if(element != null)
        {
            retVal = element.getElementType();
        }
        
        return retVal;
    }
	
    
    public void setDisplayName(String s)
    {
        setDisplayName(s, true);
    }

    // conover
    public void setDisplayName(String s, boolean notify)
    {
        super.setDisplayName(s);

        if (notify)
           notifyPropertySetsChange();
    }
    
//	/**
//	 * Gets the property element manager for the node.
//	 */
//	public IPropertyElementManager getPropertyElementManager()
//	{
//		if (mPropEleMgr == null)
//		{
//			mPropEleMgr = new PropertyElementManager();
//			mPropEleMgr.setPDFactory(getFactory());
//		}
//		return mPropEleMgr;
//	}
    
    //**************************************************
    // Node Overrides
    //**************************************************
    
    /**
     * Get the new types that can be created in this node. For example, a node
     * representing a class will permit attributes, operations, classes,
     * interfaces, and enumerations to be added.
     *
     * @return An array of new type operations that are allowed.
     */
    public NewType[] getNewTypes()
    {
        String elType = getElementType();
        NewType[] retVal = null;
        
        if (getModelElement() instanceof INamespace)
        {
            // Diagram types: Use Case Diagram, Deployment Diagram
            if (
                    elType.equals(ELEMENT_TYPE_PROXY_DIAGRAM) ||
                    elType.equals(ELEMENT_TYPE_DIAGRAM))
            {
                return new NewType[]
                {
                    new NewPackageType(this),
                            new NewElementType(this)
                };
            }
            
            else if (elType.equals(ELEMENT_TYPE_PROJECT))
            {
                return new NewType[]
                {
                    new NewDiagramType(this),
                            new NewPackageType(this),
                            new NewElementType(this)
                };
            }
            
            // Interaction types: Sequence Diagram, Collaboration Diagram
            // StateMachine types: State Diagram
            // Activity types: Activity Diagram
            else if (elType.equals(ELEMENT_TYPE_INTERACTION) ||
                    elType.equals(ELEMENT_TYPE_STATE_MACHINE) ||
                    elType.equals(ELEMENT_TYPE_ACTIVITY))
            {
                return new NewType[]
                {
                    new NewDiagramType(this),
                            new NewPackageType(this),
                            new NewElementType(this),
                            new NewAttributeType(this),
                            new NewOperationType(this)
                };
            }
        } // if getModelElement() instanceof INamespace
        
        // The NewAction code does not check for null.  Therefore, we have
        // to create a new object just to keep them from throwing.
        if (retVal == null)
        {
            retVal = new NewType[0];
        }
        
        return retVal;
    }
    
    
    /**
     * Retrieves the actions for the node.  This method only returns
     * the context sensitive actions.
     *
     * @param context Whether to find actions for context meaning or for the
     *                node itself
     * @return A list of actions (you may include nulls for separators)
     */
    public Action[] getActions(boolean context)
    {
        ArrayList<Action> actions = new ArrayList <Action>();
        
        actions.add(SystemAction.get(OpenAction.class));
		
		// cvc - CR 6287660 & 6276911
		//commented out - moved to getNewAction() to improve performance
		/*if (!(getParentNode() instanceof UMLDiagramsRootNode) &&
			!getElementType().equals(ELEMENT_TYPE_ABSTRACTION) &&
			!getElementType().equals(ELEMENT_TYPE_AGGREGATION) &&
			!getElementType().equals(ELEMENT_TYPE_ASSOCIATION) &&
			!getElementType().equals(ELEMENT_TYPE_DEPENDENCY) &&
			!getElementType().equals(ELEMENT_TYPE_GENERALIZATION) &&
			!getElementType().equals(ELEMENT_TYPE_PERMISSION) &&
			!getElementType().equals(ELEMENT_TYPE_REALIZATION) &&
			!getElementType().equals(ELEMENT_TYPE_USAGE))*/
		Action newAction=getNewAction();
		if(newAction!=null)
		{
			actions.add(newAction);
		}
		
		// cvc - CR 6276911
		// Save and Close actions were on every node, but are only 
		// enabled/valid for Diagram nodes
		// if (this instanceof UMLDiagramNode)
		// {
		// 	actions.add(SystemAction.get(SaveDiagramAction.class));
		// 	actions.add(SystemAction.get(CloseDiagramAction.class));
		// }
		
        actions.add(null);
        
		String eType=null;
		if(getParentNode()!=null && getParentNode().getParentNode()!=null)
			eType=getParentNode().getParentNode().getName();
		if(eType==null || !eType.equals("Imported Elements"))//NoI18n
			actions.add(SystemAction.get(DeleteAction.class));
        actions.add(SystemAction.get(RenameAction.class));
        
		actions.add(null);
        
        addContextMenus(actions);
        
		actions.add(null);  
        
//      actions.add(SystemAction.get(SourceControlSubMenuAction.class));
		//To improve performance, cache this action

		// Source control Sub menu Actions are created based on the SCM status of the nodes so every time we need
		//to get the actions freshly so they can't be checked for null
/*		if(scSubMenuAction==null)
			scSubMenuAction=new SourceControlSubMenuAction();		
		actions.add(scSubMenuAction);
*/       
		actions.add(null);
        
        actions.add(SystemAction.get(PropertiesAction.class));;
        
        Action[] retVal = new Action[actions.size()];
        actions.toArray(retVal);
        return retVal;
    }
	
	private Action getNewAction()
	{
		// TODO: this needs to be reviewed as to what node should have new type action
		
		if(newAction==null)
		{
			if (!(getParentNode() instanceof UMLDiagramsRootNode) &&
				getModelElement()!=null &&
				!getElementType().equals(ELEMENT_TYPE_ABSTRACTION) &&
				!getElementType().equals(ELEMENT_TYPE_AGGREGATION) &&
				!getElementType().equals(ELEMENT_TYPE_ASSOCIATION) &&
				!getElementType().equals(ELEMENT_TYPE_DEPENDENCY) &&
				!getElementType().equals(ELEMENT_TYPE_GENERALIZATION) &&
				!getElementType().equals(ELEMENT_TYPE_PERMISSION) &&
				!getElementType().equals(ELEMENT_TYPE_REALIZATION) &&
				!getElementType().equals(ELEMENT_TYPE_USAGE) &&
				!getElementType().equals(ELEMENT_TYPE_ACTOR) &&
				!getElementType().equals(ELEMENT_TYPE_DIAGRAM) &&
				!getElementType().equals(ELEMENT_TYPE_PROXY_DIAGRAM) &&
				!getElementType().equals(""))
			{
				newAction=SystemAction.get(NewAction.class);
			}
		}
		return newAction;
	}
	
    
    /**
     * Retrievse the for the model element using property elements and property
     * definitions.  The property elements allow us to use a configuration file
     * to specify the properties that should be displayed.
     *
     * @see org.netbeans.modules.uml.core.support.umlutils.IPropertyDefinition
     * @see org.netbeans.modules.uml.core.support.umlutils.IPropertyElement
     * @see org.netbeans.modules.uml.core.support.umlutils.IPropertyDefinitionFactory
     * @see org.openide.nodes.Node#getPropertySets()
     */
    public Node.PropertySet[] getPropertySets()
    {
        Node.PropertySet[] retVal = null;
        
        Node.PropertySet[] parentSet = super.getPropertySets();
        
        // The model element my not be set yet.  If the model element is not set
        // then we do not want only want to return the parents property set.
        String elementType = getElementType();
        
        if (elementType.length() > 0)
        {
            //PropertySet[] elementProperties = retreiveProperties(elementType);
            
            PropertySet[] elementProperties = retreiveProperties();
            if (elementProperties != null)
            {
                retVal = new PropertySet[
                        parentSet.length + elementProperties.length];
                System.arraycopy(
                        elementProperties, 0, retVal, 0, elementProperties.length);
                System.arraycopy(
                        parentSet, 0, retVal, elementProperties.length, parentSet.length);
            }
            
            else
            {
                retVal = parentSet;
            }
        }
        
        else
        {
            retVal = parentSet;
        }
        
        return retVal;
    }
    
    public Transferable clipboardCopy()
    throws IOException
    {
        ADTransferable retVal = new ADTransferable("DRAGGEDITEMS"); // NOI18N
        retVal.addModelElement(getModelElement());
        
        DispatchHelper heleper = new DispatchHelper();
        IProjectTreeEventDispatcher disp = heleper.getProjectTreeDispatcher();
        
        if (disp != null)
        {
            IEventPayload payload = disp.createPayload("ProjectTreeBeginDrag"); //$NON-NLS-1$
            IProjectTreeDragVerify context = new ProjectTreeDragVerifyImpl();
            
            if (this instanceof ITreeItem)
            {
                IProjectTreeItem[] items = {((ITreeItem)this).getData()};
                disp.fireBeginDrag(null, items, context, payload);
            }
            
        }
        return retVal;
    }
    
    public Transferable clipboardCut()
    throws IOException
    {
        return clipboardCopy();
    }
    
    public boolean canDestroy()
    {
        Lookup lkp = getLookup();
        boolean canDestroy = lkp.lookup(IProjectTreeItem.class) != null;
        return canDestroy;
    }
    
    public void destroy() throws IOException
    {
        // Gather up all the diagrams and model elements
        //  in preparation for deleting.
        final ETList<IElement> modelElements = new ETArrayList<IElement>();
        final List<String> diagrams = new LinkedList<String>();
        
//			ModelElementCookie meCookie = (ModelElementCookie)
//					curNode.getCookie(ModelElementCookie.class);
//			IElement element = (IElement)
//					curNode.getCookie(IElement.class);
        
        IProjectTreeItem treeItem = (IProjectTreeItem)
        this.getLookup().lookup(IProjectTreeItem.class);
        
        if (treeItem == null)
            return;
        
        
        // If this item is a model element then add to the list of model
        // elements, if it's a diagram then add it to the list of names
        // associated with the diagrams.
        if (treeItem.isImportedPackage())
        {
            if (treeItem.getImportedPackage() != null)
                modelElements.add(treeItem.getImportedPackage());
        }
        
        else if (treeItem.isImportedModelElement())
        {
            if (treeItem.getImportedModelElement() != null)
                modelElements.add(treeItem.getImportedModelElement());
        } else if (treeItem.isDiagram())
        {
            if (treeItem.getDescription() != null &&
                    treeItem.getDescription().length() > 0)
            {
                diagrams.add(treeItem.getDescription());
            }
        } else if (treeItem.getModelElement() != null)
        {
            IElement elem = treeItem.getModelElement();
            modelElements.add(elem);
        }
        
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                // Now actually do the delete of the model elements
                for (final IElement curModelElement: modelElements)
                {
                    curModelElement.delete();
                }
                
                // Now whack the diagrams
                final IProxyDiagramManager proxyDiagramManager =
                        ProxyDiagramManager.instance();
                
                //for (int k=0; k<numDias; k++)
                for (final String curDiagramName: diagrams)
                {
                    proxyDiagramManager.removeDiagram(curDiagramName);
                }
            }
        };
        
        EventQueue.invokeLater(runnable);
    }
    
    public boolean canCut()
    {
        return true;
    }
    
//   /**
//    * Determine which paste operations are allowed when a given transferable
//	  * is in the clipboard.
//    * For example, a node representing a Java package will permit classes to
//	  * be pasted into it.
//    * @param t the transferable in the clipboard
//    * @return array of operations that are allowed
//    */
//    public abstract PasteType[] getPasteTypes (Transferable t)
//    {
//      PasteType[] retVal = new PasteType[0];
//      PasteType type = getDropType(t, DnDConstants.ACTION_COPY);
//      if(type != null)
//      {
//         retVal = new PasteType[1];
//         retVal[0] = type;
//      }
//
//      return retVal;
//    }
    
    protected void createPasteTypes(Transferable t, List s)
    {
        super.createPasteTypes(t, s);
        PasteType type = getDropType(t, DnDConstants.ACTION_COPY, -1);
        if (type != null)
        {
            s.add(type);
        }
    }
    
    
    public PasteType getDropType(Transferable trans, int action, int index)
    {
        PasteType retVal = null;
        
        // cvc - CR 6363187 - can't DnD more than one model element in tree
        // This method has been overhauled to fix the bug.

        try
        {
            if (trans instanceof ADTransferable)
            {
                if (trans.isDataFlavorSupported(ADTransferable.ADDataFlavor))
                {
                    Object obj = trans.getTransferData(ADTransferable.ADDataFlavor);
                    ADTransferable adTrans = (ADTransferable)trans;
    
                    if (obj != null)
                    {
                        if (this instanceof ITreeItem)
                        {
                            ITreeItem item = (ITreeItem)this;
                            IProjectTreeItem data = item.getData();
                            retVal = new ModelPasteType(trans, data, action);
                        }
                    }
                } // if - isDataFlavorSupported
            } // if - ADTransferable
            
            else if (trans instanceof ExTransferable)
            {
                ExTransferable exTrans = (ExTransferable)trans;
    
                if (this instanceof ITreeItem)
                {
                    ITreeItem item = (ITreeItem)this;
                    IProjectTreeItem data = item.getData();
                    retVal = new ModelPasteType(exTrans, data, action);
                }
            }
            
            else
            {
                Log.write(
                    "Unexpected Transferrable subtype parameter, "  // NOI18N
                    + trans.getClass().getName() + ". Should not be " // NOI18N
                    + "an issue but proactively logging ocurrence."); // NOI18N
            }
        } // try
            
        catch (Exception e)
        {
            // e.printStackTrace();
            Log.stackTrace(e);
        }
        
//            DispatchHelper heleper = new DispatchHelper();
//            IProjectTreeEventDispatcher disp = heleper.getProjectTreeDispatcher();
//            
//            if (disp != null)
//            {
//                IEventPayload payload = disp.createPayload("ProjectTreeBeginDrag"); //$NON-NLS-1$
//                IProjectTreeDragVerify context = new ProjectTreeDragVerifyImpl();
//                
//                if (this instanceof ITreeItem)
//                {
//                    IProjectTreeItem[] items = {((ITreeItem)this).getData()};
//                    disp.fireBeginDrag(null, items, context, payload);
//                }
//            }
        
        return retVal;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Helper Methods
    ////////////////////////////////////////////////////////////////////////////
    
//	/**
//	 * Gets the property definition factory for the node.  The property
//	 * definitions factory uses a configration file to generate property
//	 * defintions.  The propety defintion is basically an abstract description
//	 * of the properties to be displayed for the model elements.
//	 *
//	 * @return The property definition factory.
//	 */
//	protected IPropertyDefinitionFactory getFactory()
//	{
//		if( mDefFactory == null)
//		{
//			String file = getDefinitionFile();
//			mDefFactory = new PropertyDefinitionFactory();
//			mDefFactory.setDefinitionFile(file);
//			mDefFactory.buildDefinitionsUsingFile();
//		}
//
//		return mDefFactory;
//	}
    
    
    
    /**
     * Retrieve the context actions added by other modules.
     *
     * @param actions The action collection to add the actions to.
     */
    protected void addContextMenus(ArrayList<Action> actions)
    {
        Action[] nodeActions =
                getActionsFromRegistry("contextmenu/uml/element"); // NOI18N
        
        for(Action curAction : nodeActions)
        {
            if (curAction == null)
                // Make Sure the Seperators are kept.
                actions.add(null);
            
            else if (curAction.isEnabled())
                actions.add(curAction);
        }
    }
    
    
    /**
     * The registry information that is retrieved from layer files to build
     * the list of actions supported by this node.
     *
     * @param path The registry path that is used for the lookup.
     * @return The list of actions in the path.  null will be used if when
     *         seperators can be placed.
     */
    protected Action[] getActionsFromRegistry(String path)
    {
        ArrayList<Action> actions = new ArrayList<Action>();
        FileSystem system = Repository.getDefault().getDefaultFileSystem();
        
		if (system != null)
		{
			FileObject fo = system.findResource(path);
			DataFolder df = fo != null ? DataFolder.findFolder(fo) : null;
			if (df != null) {
				DataObject actionObjects[] = df.getChildren();
				for (int i = 0; i < actionObjects.length; i++) 
				{
					InstanceCookie ic = (InstanceCookie) actionObjects[i].getCookie(InstanceCookie.class);
					if (ic == null) continue;
					Object instance;
					try {
						instance = ic.instanceCreate();
					} catch (IOException e) {
						// ignore
						e.printStackTrace();
						continue;
					} catch (ClassNotFoundException e) {
						// ignore
						e.printStackTrace();
						continue;
					}
					if (instance instanceof Action)
						actions.add((Action)instance);
					else if (instance instanceof JSeparator)
						actions.add(null);
				}
			}
		}
				
        Action[] retVal = new Action[actions.size()];
        actions.toArray(retVal);
        return retVal;
    }
    
    
    protected Node.PropertySet[] retreiveProperties()
    {
        Node.PropertySet[] retVal = null;
        
//        if ((mPropertySheet != null) && (mPropertySheet.get() != null))
//        {
//            retVal = mPropertySheet.get();
//        }
//        
//        else
        {
            retVal = buildProperties();
//            mPropertySheet = new WeakReference(retVal);
        }
        
        return retVal;
    }
    
    // Jyothi: Fix for Bug#6258627-Naming a component doesn't update the property sheet Name to the new value.
    // This method is a hack.. and is NOT supposed to be used in any other scenario 
    public void notifyPropertySetsChange() {
        firePropertySetsChange(null, retreiveProperties());
    }
    
    /**
     * Builds the proerty set structure for all model elements.
     */
    protected Node.PropertySet[] buildProperties()
    {
        Node.PropertySet[] retVal = null;
        
        IElement element = getModelElement();
        
        if (element != null)
        {
            DefinitionPropertyBuilder builder = DefinitionPropertyBuilder.instance();
            retVal = builder.retreiveProperties(element.getElementType(), element);
        }
        
        return retVal;
    }
    
    public class ModelPasteType extends PasteType
    {
        // cvc - CR 6363187 - can't DnD more than one model element in tree
        // This inner class has been overhauled to fix the bug.

        private IProjectTreeItem mTreeItem = null;
        private Transferable mTransferable = null;
        private int mAction = DnDConstants.ACTION_NONE;
        
        public ModelPasteType(
            Transferable transferable,
            IProjectTreeItem data,
            int action)
        {
            mTreeItem = data;
            mTransferable = transferable;
            mAction = action;
        }
        
        public Transferable paste() 
            throws IOException
        {
            fireEndDrag();
            return null;
        }
        
        /**
         * Notifies listeners that the drag process has been completed.  This event
         * is only fired if project tree is the drop target.
         */
        public boolean fireEndDrag()
        {
            boolean retVal = true;
            
            if (mAction != DnDConstants.ACTION_NONE)
            {
                //m_InDragProcess = false;
                
                DispatchHelper dispatcherHelper = new DispatchHelper();
                IProjectTreeEventDispatcher disp = dispatcherHelper.getProjectTreeDispatcher();
                
                if (disp != null)
                {
                    IEventPayload payload = disp.createPayload("ProjectTreeEndDrag"); //$NON-NLS-1$
                    IProjectTreeDragVerify context = new ProjectTreeDragVerifyImpl();
                    context.setTargetNode(mTreeItem);
                    
                    disp.fireEndDrag(null, mTransferable, mAction, context, payload);
                    
                    retVal = !context.isCancel();
                    //m_DragPaths = null;
                }
            }
            return retVal;
        }
    }
}
