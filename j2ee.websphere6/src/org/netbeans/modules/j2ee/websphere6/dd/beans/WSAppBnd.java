/*
 * AppBndXmi.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.modules.j2ee.websphere6.dd.beans;
import org.w3c.dom.*;
import org.netbeans.modules.schema2beans.*;
import java.beans.*;
import java.util.*;
import java.io.*;

/**
 *
 * @author dlm198383
 */
public class WSAppBnd extends DDXmi{
    
    public  static final String AUTH_TABLE="AuthorizationTable";
    public  static final String AUTH_TABLE_XMI_ID="AuthorizationTableXmiId";
    public  static final String APPLICATION="Application";
    private static final String ROOT="applicationbnd:ApplicationBinding";
    private static final String ROOT_NAME="WebSphereApplicationBnd";
    
    
    /** Creates a new instance of AppBndXmi */
    public WSAppBnd() {
        this(null, Common.USE_DEFAULT_VALUES);
    }
    public WSAppBnd(org.w3c.dom.Node doc, int options) {
        this(Common.NO_DEFAULT_VALUES);
        try {
            initFromNode(doc, options);
        } catch (Schema2BeansException e) {
            throw new RuntimeException(e);
        }
    }
    
    public WSAppBnd(int options) {
        super(options,ROOT);
        //initOptions(options);
    }
    
   
    public WSAppBnd(File f,boolean validate) throws IOException{
        this(GraphManager.createXmlDocument(new FileInputStream(f), validate), Common.NO_DEFAULT_VALUES);
    }
    
    public WSAppBnd(InputStream in, boolean validate) {
        this(GraphManager.createXmlDocument(in, validate), Common.NO_DEFAULT_VALUES);
    }
    
    protected void initFromNode(org.w3c.dom.Node doc, int options) throws Schema2BeansException {
        if (doc == null) {
            doc = GraphManager.createRootElementNode(ROOT);	// NOI18N
            if (doc == null)
                throw new Schema2BeansException(Common.getMessage(
                        "CantCreateDOMRoot_msg", ROOT));
        }
        Node n = GraphManager.getElementNode(ROOT, doc);	// NOI18N
        if (n == null) {
            throw new Schema2BeansException(Common.getMessage("DocRootNotInDOMGraph_msg", ROOT, doc.getFirstChild().getNodeName()));
        }
        this.graphManager.setXmlDocument(doc);
        this.createBean(n, this.graphManager());
        this.initialize(options);
    };
    
    public void initialize(int options) {
        
    }
    
    
    public void setDefaults() {
        setNsApp();
        setNsAppBnd();
        setNsCommon();
        setNsXmi();
        setXmiId("Application_ID_Bnd");
        setAuthTableId("AthorizationTable_1");
        setApplication("");
        setApplicationHref("Application_ID");
    }
    protected void initOptions(int options) {
        this.graphManager = new GraphManager(this);
        this.createRoot(ROOT, ROOT_NAME,	// NOI18N
                Common.TYPE_1 | Common.TYPE_BEAN, WSAppBnd.class);
        
        this.createAttribute(XMI_ID_ID,    XMI_ID,AttrProp.CDATA | AttrProp.IMPLIED,null, null);
        this.createAttribute(NS_APP_ID,    NS_APP,AttrProp.CDATA | AttrProp.IMPLIED,null, null);
        this.createAttribute(NS_COMMON_ID, NS_COMMON,AttrProp.CDATA | AttrProp.IMPLIED,null, null);
        this.createAttribute(NS_APP_BND_ID,NS_APP_BND,AttrProp.CDATA | AttrProp.IMPLIED,null, null);
        this.createAttribute(NS_XMI_ID,    NS_XMI,AttrProp.CDATA | AttrProp.IMPLIED,null, null);
        this.createAttribute(XMI_VERSION_ID,XMI_VERSION,AttrProp.CDATA | AttrProp.IMPLIED,null, null);
        // Properties (see root bean comments for the bean graph)
        initPropertyTables(2);
        
        this.createProperty("authorizationTable", 	// NOI18N
                AUTH_TABLE,
                Common.TYPE_1 | Common.TYPE_BEAN | Common.TYPE_KEY,
                AuthorizationTableType.class);
    this.createAttribute(AUTH_TABLE,XMI_ID_ID,AUTH_TABLE_XMI_ID,AttrProp.CDATA | AttrProp.IMPLIED,null, null);
        
        this.createProperty(APPLICATION_ID, 	// NOI18N
                APPLICATION,
                Common.TYPE_1 | Common.TYPE_STRING | Common.TYPE_KEY,
                java.lang.String.class);
        this.createAttribute(APPLICATION,HREF_ID,APPLICATION_HREF,AttrProp.CDATA | AttrProp.IMPLIED,null, null);
        
        this.initialize(options);
    }
    
    public void setAuthTable(AuthorizationTableType value) {
        this.setValue(AUTH_TABLE, value);
    }
    
    //
    public AuthorizationTableType getAuthorizationTable() {
        return (AuthorizationTableType)this.getValue(AUTH_TABLE);
    }
    public int sizeAuthorizationTable() {
        return this.size(AUTH_TABLE);
    }
    
    public void setAuthTableId(String value)  {
        setAttributeValue(AUTH_TABLE,AUTH_TABLE_XMI_ID,value);
    }
    public String getAuthTableId(){
        return (String)getAttributeValue(AUTH_TABLE,AUTH_TABLE_XMI_ID);
    }
    
    
    public void validate() throws org.netbeans.modules.schema2beans.ValidateException {
        
        if (getAuthorizationTable()!= null) {
            getAuthorizationTable().validate();
        } else {
            throw new org.netbeans.modules.schema2beans.ValidateException("getAuthorizationTable() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, AUTH_TABLE, this);	// NOI18N
        }
        
        if (getApplication()== null) {
            throw new org.netbeans.modules.schema2beans.ValidateException("getApplication() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, APPLICATION, this);	// NOI18N
        }
        if(getApplicationHref()==null) {
            throw new org.netbeans.modules.schema2beans.ValidateException("getApplicationHref() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, APPLICATION, this);	// NOI18N
        }
        if(getNsApp()==null) {
            throw new org.netbeans.modules.schema2beans.ValidateException("getNsApp() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, ROOT, this);	// NOI18N
        }
        if(getNsAppBnd()==null) {
            throw new org.netbeans.modules.schema2beans.ValidateException("getNsAppBnd() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, ROOT, this);	// NOI18N
        }
        if(getNsCommon()==null) {
            throw new org.netbeans.modules.schema2beans.ValidateException("getNsCommon() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, ROOT, this);	// NOI18N
        }
        if(getNsXmi()==null) {
            throw new org.netbeans.modules.schema2beans.ValidateException("getNsXmi() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, ROOT, this);	// NOI18N
        }
        if(getAuthorizationTable()!=null)
        if(getAuthTableId()==null) {
            throw new org.netbeans.modules.schema2beans.ValidateException("getAuthTableId() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, AUTH_TABLE, this);	// NOI18N
        }
        if(getXmiId()==null) {
            throw new org.netbeans.modules.schema2beans.ValidateException("getXmiId() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, ROOT, this);	// NOI18N
        }
        if(getXmiVersion()==null) {
            throw new org.netbeans.modules.schema2beans.ValidateException("getXmiVersion() == null", org.netbeans.modules.schema2beans.ValidateException.FailureType.NULL_VALUE, ROOT, this);	// NOI18N
        }
    }
    
    public void dump(StringBuffer str, String indent){
        String s;
        Object o;
        org.netbeans.modules.schema2beans.BaseBean n;
        
        str.append(indent);
        str.append(AUTH_TABLE);	// NOI18N
        n = (org.netbeans.modules.schema2beans.BaseBean) this.getAuthorizationTable();
        if (n != null)
            n.dump(str, indent + "\t");	// NOI18N
        else
            str.append(indent+"\tnull");	// NOI18N
        this.dumpAttributes(AUTH_TABLE, 0, str, indent);
        
        str.append(indent);
        str.append(APPLICATION);	// NOI18N
        str.append(indent+"\t");	// NOI18N
        str.append("<");	// NOI18N
        o = this.getApplication();
        str.append((o==null?"null":o.toString().trim()));	// NOI18N
        str.append(">\n");	// NOI18N
        this.dumpAttributes(APPLICATION, 0, str, indent);
    }
    public String dumpBeanNode(){
        StringBuffer str = new StringBuffer();
        str.append(getClass().getName());	// NOI18N
        this.dump(str, "\n  ");	// NOI18N
        return str.toString();
    }
}
