/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.websvc.design.schema2java;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.websvc.api.jaxws.wsdlmodel.WsdlModel;
import org.netbeans.modules.websvc.api.jaxws.wsdlmodel.WsdlModelListener;
import org.netbeans.modules.websvc.api.jaxws.wsdlmodel.WsdlModeler;
import org.netbeans.modules.websvc.api.jaxws.wsdlmodel.WsdlModeler;
import org.netbeans.modules.websvc.api.jaxws.wsdlmodel.WsdlModelerFactory;
import org.netbeans.modules.websvc.design.util.WSDLUtils;
import org.netbeans.modules.websvc.design.view.actions.ParamModel;
import org.netbeans.modules.websvc.design.view.actions.Utils;
import org.netbeans.modules.websvc.jaxws.api.JAXWSSupport;
import org.netbeans.modules.xml.schema.model.ElementReference;
import org.netbeans.modules.xml.schema.model.GlobalComplexType;
import org.netbeans.modules.xml.schema.model.GlobalElement;
import org.netbeans.modules.xml.schema.model.GlobalSimpleType;
import org.netbeans.modules.xml.schema.model.GlobalType;
import org.netbeans.modules.xml.schema.model.LocalElement;
import org.netbeans.modules.xml.schema.model.ReferenceableSchemaComponent;
import org.netbeans.modules.xml.schema.model.Schema;
import org.netbeans.modules.xml.schema.model.SchemaModel;
import org.netbeans.modules.xml.schema.model.Sequence;
import org.netbeans.modules.xml.wsdl.model.Binding;
import org.netbeans.modules.xml.wsdl.model.BindingFault;
import org.netbeans.modules.xml.wsdl.model.BindingInput;
import org.netbeans.modules.xml.wsdl.model.BindingOperation;
import org.netbeans.modules.xml.wsdl.model.BindingOutput;
import org.netbeans.modules.xml.wsdl.model.Definitions;
import org.netbeans.modules.xml.wsdl.model.Fault;
import org.netbeans.modules.xml.wsdl.model.Input;
import org.netbeans.modules.xml.wsdl.model.Message;
import org.netbeans.modules.xml.wsdl.model.Operation;
import org.netbeans.modules.xml.wsdl.model.Output;
import org.netbeans.modules.xml.wsdl.model.Part;
import org.netbeans.modules.xml.wsdl.model.PortType;
import org.netbeans.modules.xml.wsdl.model.Types;
import org.netbeans.modules.xml.wsdl.model.WSDLComponentFactory;
import org.netbeans.modules.xml.wsdl.model.WSDLModel;
import org.netbeans.modules.xml.wsdl.model.extensions.soap.SOAPBinding;
import org.netbeans.modules.xml.wsdl.model.extensions.soap.SOAPBinding.Style;
import org.netbeans.modules.xml.wsdl.model.extensions.soap.SOAPBody;
import org.netbeans.modules.xml.wsdl.model.extensions.soap.SOAPFault;
import org.netbeans.modules.xml.wsdl.model.extensions.soap.SOAPMessageBase;
import org.netbeans.modules.xml.wsdl.model.extensions.soap.SOAPOperation;
import org.netbeans.modules.xml.wsdl.model.extensions.xsd.WSDLSchema;
import org.netbeans.modules.xml.xam.dom.NamedComponentReference;
import org.openide.ErrorManager;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.netbeans.modules.websvc.api.jaxws.project.config.Service;
import org.netbeans.modules.websvc.customization.model.CustomizationComponentFactory;
import org.netbeans.modules.websvc.customization.model.JavaMethod;
import org.netbeans.modules.websvc.customization.model.PortTypeOperationCustomization;
import org.netbeans.modules.websvc.design.javamodel.MethodModel;
import org.netbeans.modules.websvc.design.javamodel.ServiceModel;
import org.netbeans.modules.xml.schema.model.ComplexType;
import org.netbeans.modules.xml.schema.model.ComplexTypeDefinition;
import org.netbeans.modules.xml.schema.model.SchemaComponentFactory;
import org.netbeans.modules.xml.schema.model.SequenceDefinition;


/**
 *
 * @author mkuchtiak, rcruz
 */
public class OperationGeneratorHelper {
    File wsdlFile;
    Map<String, String> types;
    Collection<GlobalSimpleType> primitives;
    WSDLModel wsdlModel;
    
    /** Creates a new instance of MethodGeneratorHelper */
    public OperationGeneratorHelper(File wsdlFile) {
        this.wsdlFile=wsdlFile;
        wsdlModel = WSDLUtils.getWSDLModel(FileUtil.toFileObject(wsdlFile), true);
        
    }
    
    public Operation addWsOperation(
            String portTypeName,
            String operationName,
            List<ParamModel> parameterTypes,
            ReferenceableSchemaComponent returnType,
            List<ParamModel> faultTypes) {
        
        boolean isDocOriented = WSDLUtils.isDocumentOriented(wsdlModel);
        Definitions definitions = wsdlModel.getDefinitions();
        Collection<Binding> bindings = definitions.getBindings();
        WSDLComponentFactory factory = wsdlModel.getFactory();
        Operation operation = null;
        try {
            wsdlModel.startTransaction();
            operation = wsdlModel.getFactory().createRequestResponseOperation();
            operation.setName(operationName);
            SchemaModel schemaModel = null;
            Schema schema = null;
            Types types = wsdlModel.getDefinitions().getTypes();
            if(types != null){
                Collection<Schema> schemas = types.getSchemas();
                Iterator<Schema> it = schemas.iterator();
                if (it.hasNext()) {
                    schema = it.next();
                    schemaModel = schema.getModel();
                }
            }
            if(schemaModel == null) {
                schemaModel = createSchemaModel(factory, definitions, types);
                schema = schemaModel.getSchema();
            }
            
            String schemaNamespace = schema.getTargetNamespace();
            if (schemaNamespace == null) {
                String parentTns = wsdlModel.getDefinitions().getTargetNamespace();
                if (parentTns != null) {
                    schema.setTargetNamespace(parentTns);
                }
            }
            
            Message inputMessage =  addInputMessageToOperation(operation, parameterTypes,
                    schemaModel,  isDocOriented);
            Message outputMessage = addOutputMessageToOperation(operation, returnType,
                    schemaModel, isDocOriented);
            List<Message> faultMessages = addFaultsToOperation(operation, faultTypes, schemaModel);
            
            Collection<PortType> portTypes = definitions.getPortTypes();
            PortType portType = null;
            for(PortType p : portTypes){
                if(p.getName().equals(portTypeName)){
                    portType = p;
                    break;
                }
            }
            //Add binding section for operation, if there is a binding section
            if(portType != null ){
                if(bindings.size() > 0){
                    addSOAPBindingForOperation(operation, portType, isDocOriented,
                            inputMessage, outputMessage, faultMessages);
                }
                portType.addOperation(operation);
            }
            
        }finally{
            wsdlModel.endTransaction();
        }
        return operation;
    }
    
    
    
    private boolean isPrimitiveType(ParamModel parameterType){
        return (Utils.getPrimitiveType(parameterType.getParamType().getName()) != null);
    }
    
    private boolean isPrimitiveType(ReferenceableSchemaComponent comp){
        return (Utils.getPrimitiveType(comp.getName()) !=  null);
    }
    
    
    private void addElementToSequence(Sequence sequence, SchemaModel schemaModel, ParamModel param) {
        
        ReferenceableSchemaComponent paramType = param.getParamType();
        if (paramType instanceof GlobalType) {
            LocalElement el = schemaModel.getFactory().createLocalElement();
            NamedComponentReference<GlobalType> typeRef = schemaModel.getSchema().createReferenceTo((GlobalType)paramType, GlobalType.class);
            el.setName(param.getParamName());
            el.setType(typeRef);
            sequence.appendContent(el);
        } else if (paramType instanceof GlobalElement) {
            ElementReference el = schemaModel.getFactory().createElementReference();
            NamedComponentReference<GlobalElement> typeRef = schemaModel.getSchema().createReferenceTo((GlobalElement)paramType, GlobalElement.class);
            el.setRef(typeRef);
            sequence.appendContent(el);
        }
    }
    
    
  
    private Binding findBindingForPortType(Collection<Binding> bindings, PortType portType){
        for(Binding b : bindings){
            NamedComponentReference<PortType> portTypeRef = b.getType();
            if(portTypeRef.references(portType)){
                return b;
            }
        }
        return null;
    }
    
    public void removeWSOperation(WSDLModel wsdlModel,
            String portTypeName,
            String operationName){
        
        PortType portType = null;
        Operation operation = null;
        try{
            wsdlModel.startTransaction();
            Definitions definitions = wsdlModel.getDefinitions();
            Collection<PortType> portTypes = definitions.getPortTypes();
            for(PortType pt : portTypes){
                if(pt.getName().equals(portTypeName)){
                    portType = pt;
                    break;
                }
            }
            if(portType != null){
                Collection<Operation> operations = portType.getOperations();
                for(Operation op : operations){
                    String opName = convertOperationName(op.getName());
                    if(opName.equals(operationName)){
                        operation = op;
                        break;
                    }
                }
                if(operation != null){
                    portType.removeOperation(operation);
                    
                    Collection<Binding> bindings = definitions.getBindings();
                    Binding binding = null;
                    if(bindings.size() > 0){
                        //find binding for portType
                        binding = findBindingForPortType(bindings, portType);
                        if(binding != null){
                            Collection<BindingOperation> bindingOperations = binding.getBindingOperations();
                            BindingOperation bindingOperation = null;
                            for(BindingOperation bindingOp : bindingOperations){
                                //TODO: Is this enough??
                                //TODO: should we resolve the binding operation reference??
                                if(operationName.equals(bindingOp.getName())){
                                    bindingOperation = bindingOp;
                                    break;
                                }
                            }
                            if(bindingOperation != null){
                                binding.removeBindingOperation(bindingOperation);
                            }
                        }
                    }
                }
            }
        }finally{
            wsdlModel.endTransaction();
        }
        
    }
    
    private Message addInputMessageToOperation(Operation operation, List<ParamModel> parameterTypes,
            SchemaModel schemaModel, boolean isDocOriented){
        Schema schema = schemaModel.getSchema();
        WSDLComponentFactory factory = wsdlModel.getFactory();
        SchemaComponentFactory schemaFactory = schemaModel.getFactory();
        GlobalComplexType paramComplexType = null;
        
        GlobalElement paramElement = null;
        String operationName = operation.getName();
        String paramTypeName = operationName+"Type"; //NOI18N
        String inputMessageName = operationName+"Message"; //NOI18N
        String partName = operationName+"Part"; //NOI18N
        Message inputMessage=null;
        Definitions definitions = wsdlModel.getDefinitions();
        
        if(isDocOriented){  //document/literal/wrapped
            if (parameterTypes.size()==0 || parameterTypes.size() > 1 || (parameterTypes.size() == 1 && isPrimitiveType(parameterTypes.get(0)))) {
                
                paramComplexType = schemaFactory.createGlobalComplexType();
                paramComplexType.setName(paramTypeName);
                Sequence seq = schemaFactory.createSequence();
                for(ParamModel param : parameterTypes) {
                    addElementToSequence(seq, schemaModel, param);
                }
                paramComplexType.setDefinition(seq);
                schema.addComplexType(paramComplexType);
                paramElement = schemaFactory.createGlobalElement();
                paramElement.setName(getUniqueGlobalElementName(schema, operationName)); //NOI18N
                NamedComponentReference<GlobalType> complexTypeRef = schema.createReferenceTo((GlobalType)paramComplexType, GlobalType.class);
                paramElement.setType(complexTypeRef);
            } else{ //there is only one parameter and it is not primitive
                ParamModel paramModel = parameterTypes.get(0);
                ReferenceableSchemaComponent ref = paramModel.getParamType();
                if (ref instanceof GlobalElement){
                    paramElement = (GlobalElement)ref;
                } else if (ref instanceof GlobalType) {
                    paramElement = schemaFactory.createGlobalElement();
                    paramElement.setName(getUniqueGlobalElementName(schema, operationName)); //NOI18N
                    NamedComponentReference<GlobalType> typeRef = schema.createReferenceTo((GlobalType)ref, GlobalType.class);
                    paramElement.setType(typeRef);
                }
            }
            
            if (paramElement!=null) {
                schema.addElement(paramElement);
                inputMessage = factory.createMessage();
                inputMessage.setName(inputMessageName);
                definitions.addMessage(inputMessage);
                Part part = factory.createPart();
                part.setName(partName);
                NamedComponentReference<GlobalElement> ref = part.createSchemaReference(paramElement, GlobalElement.class);
                part.setElement(ref);
                inputMessage.addPart(part);
                
            }
        }else{  //rpc/literal
            inputMessage = factory.createMessage();
            inputMessage.setName(inputMessageName);
            definitions.addMessage(inputMessage);
            Part part = null;
            for(ParamModel param : parameterTypes) {
                ReferenceableSchemaComponent ref = param.getParamType();
                NamedComponentReference<GlobalType> typeRef = schema.createReferenceTo((GlobalType)ref, GlobalType.class);
                part = factory.createPart();
                part.setName(getUniquePartName(inputMessage, partName));
                part.setType(typeRef);
                inputMessage.addPart(part);
            }
        }
        Input input = factory.createInput();
        NamedComponentReference<Message> inputRef = input.createReferenceTo(inputMessage, Message.class);
        input.setName(operationName);
        input.setMessage(inputRef);
        operation.setInput(input);
        
        return inputMessage;
    }
    
    private Message addOutputMessageToOperation(Operation operation,  ReferenceableSchemaComponent returnType,
            SchemaModel schemaModel, boolean isDocOriented){
        WSDLComponentFactory factory = wsdlModel.getFactory();
        SchemaComponentFactory schemaFactory = schemaModel.getFactory();
        GlobalComplexType responseComplexType = null;
        String operationName = operation.getName();
        String responseTypeName = operationName+"ResponseType"; //NOI18N
        String responseElementName = operationName+"Response"; //NOI18N
        String responseMessageName = operationName+"ResponseMessage"; //NOI18N
        String responsePartName = operationName+"ResponsePart"; //NOI18N
        Schema schema = schemaModel.getSchema();
        GlobalElement responseElement = null;
        Message outputMessage=null;
        Definitions definitions = wsdlModel.getDefinitions();
        
        if(isDocOriented){ //document/literal/wrapped
            if(returnType != null) {
                responseComplexType = schemaModel.getFactory().createGlobalComplexType();
                responseComplexType.setName(responseTypeName); //NOI18N
                if (isPrimitiveType(returnType)){
                    Sequence seq1 = schemaFactory.createSequence();
                    responseComplexType.setDefinition(seq1);
                    schema.addComplexType(responseComplexType);
                    LocalElement el = schemaFactory.createLocalElement();
                    NamedComponentReference<GlobalType> typeRef = schema.createReferenceTo((GlobalType)returnType, GlobalType.class);
                    el.setName("result"); //NOI18N
                    el.setType(typeRef);
                    seq1.appendContent(el);
                    responseElement = schemaFactory.createGlobalElement();
                    responseElement.setName(this.getUniqueGlobalElementName(schema, responseElementName));
                    NamedComponentReference<GlobalType> responseTypeRef = schema.createReferenceTo((GlobalType)responseComplexType, GlobalType.class);
                    responseElement.setType(responseTypeRef);
                    schema.addElement(responseElement);
                } else if (returnType instanceof GlobalElement) {
                    responseElement = (GlobalElement)returnType;
                } else if (returnType instanceof  GlobalType) {
                    responseElement = schemaModel.getFactory().createGlobalElement(); //NOI18N
                    responseElement.setName(getUniqueGlobalElementName(schema, responseElementName)); //NOI18N
                    NamedComponentReference<GlobalType> typeRef = schema.createReferenceTo((GlobalType)returnType, GlobalType.class);
                    responseElement.setType(typeRef);
                    schema.addElement(responseElement);
                }
            } else {// return type == null
                responseComplexType = schemaModel.getFactory().createGlobalComplexType();
                responseComplexType.setName(responseTypeName); //NOI18N
                Sequence seq1 = schemaModel.getFactory().createSequence();
                responseComplexType.setDefinition(seq1);
                schema.addComplexType(responseComplexType);
                responseElement = schemaModel.getFactory().createGlobalElement();
                responseElement.setName(this.getUniqueGlobalElementName(schema, responseElementName));
                NamedComponentReference<GlobalType> responseTypeRef = schema.createReferenceTo((GlobalType)responseComplexType, GlobalType.class);
                responseElement.setType(responseTypeRef);
                schema.addElement(responseElement);
            }
            
            if (responseElement!=null) {
                outputMessage = factory.createMessage();
                outputMessage.setName(responseMessageName);
                Part outpart = factory.createPart();
                outpart.setName(responsePartName);
                NamedComponentReference<GlobalElement> outref = outpart.createSchemaReference(responseElement, GlobalElement.class);
                outpart.setElement(outref);
                outputMessage.addPart(outpart);
                definitions.addMessage(outputMessage);
                Output output = factory.createOutput();
                NamedComponentReference<Message> outputRef = output.createReferenceTo(outputMessage, Message.class);
                output.setName(responseElementName);
                output.setMessage(outputRef);
                operation.setOutput(output);
            }
        }else{  //rpc/literal
            if(returnType != null){
                outputMessage = factory.createMessage();
                outputMessage.setName(responseMessageName);
                definitions.addMessage(outputMessage);
                Part part = factory.createPart();
                part.setName(responsePartName);
                NamedComponentReference<GlobalType> typeRef = schema.createReferenceTo((GlobalType)returnType, GlobalType.class);
                part.setType(typeRef);
                outputMessage.addPart(part);
                Output output = factory.createOutput();
                output.setName(responseElementName);
                NamedComponentReference<Message> outputRef = output.createReferenceTo(outputMessage, Message.class);
                output.setMessage(outputRef);
                operation.setOutput(output);
            }
        }
        return outputMessage;
        
    }
    
    private List<Message> addFaultsToOperation(Operation operation, List<ParamModel> faultTypes,
            SchemaModel schemaModel){
        List<GlobalElement> faultElements = new ArrayList<GlobalElement>();
        List<Message> faultMessages = new ArrayList<Message>();
        Schema schema = schemaModel.getSchema();
        WSDLComponentFactory factory = wsdlModel.getFactory();
        SchemaComponentFactory schemaFactory = schemaModel.getFactory();
        Definitions definitions = wsdlModel.getDefinitions();
        
        // create schema elements for faults
        for(ParamModel faultModel : faultTypes) {
            ReferenceableSchemaComponent faultType = faultModel.getParamType();
            if (faultType instanceof GlobalType) {
                GlobalElement faultElement = schemaFactory.createGlobalElement();
                NamedComponentReference<GlobalType> typeRef = schemaModel.getSchema().createReferenceTo((GlobalType)faultType, GlobalType.class);
                faultElement.setName(getUniqueGlobalElementName(schema,faultModel.getParamName()));
                faultElement.setType(typeRef);
                schema.addElement(faultElement);
                faultElements.add(faultElement);
            } else if (faultType instanceof GlobalElement) {
                faultElements.add((GlobalElement)faultType);
            }
        }
        for (GlobalElement faultElement:faultElements) {
            Message faultMessage = factory.createMessage();
            faultMessage.setName(faultElement.getName());
            definitions.addMessage(faultMessage);
            
            Part part = factory.createPart();
            part.setName("fault"); //NOI18N
            NamedComponentReference<GlobalElement> ref = part.createSchemaReference(faultElement, GlobalElement.class);
            part.setElement(ref);
            
            faultMessage.addPart(part);
            faultMessages.add(faultMessage);
        }
        
        for (Message faultMessage:faultMessages) {
            Fault fault = factory.createFault();
            NamedComponentReference<Message> ref = fault.createReferenceTo(faultMessage, Message.class);
            fault.setName(faultMessage.getName());
            fault.setMessage(ref);
            operation.addFault(fault);
        }
        
        return faultMessages;
    }
    
    private void addSOAPBindingForOperation(Operation operation, PortType portType, boolean isDocOriented,
            Message inputMessage, Message outputMessage, List<Message> faultMessages){
        Definitions definitions = wsdlModel.getDefinitions();
        Collection<Binding> bindings = definitions.getBindings();
        WSDLComponentFactory factory = wsdlModel.getFactory();
        
        
        //Assume SOAP binding only
        Binding binding = null;
        //find binding for portType
        binding = findBindingForPortType(bindings, portType);
        if(binding != null){
            //determine if it is soap binding
            List<SOAPBinding> soapBindings = binding.getExtensibilityElements(SOAPBinding.class);
            if(soapBindings.size() > 0){  //it is SOAP binding
                //get the SOAP Binding
                SOAPBinding soapBinding = soapBindings.iterator().next();
                //is style specified at the soap binding level?
                Style style = soapBinding.getStyle();
                BindingOperation bOp = factory.createBindingOperation();
                bOp.setName(operation.getName());
                
                SOAPOperation soapOperation = factory.createSOAPOperation();
                soapOperation.setSoapAction("");
                if(style == null){
                    soapOperation.setStyle(Style.DOCUMENT);
                }
                bOp.addExtensibilityElement(soapOperation);
                //create input binding to SOAP
                if(inputMessage != null){
                    //For now, assume all parms are to be put in the body
                    //TODO: based on the WebParm annotation, we need to determine
                    //if a certain part is for a header
                    BindingInput bindingInput = factory.createBindingInput();
                    SOAPBody soapBody = factory.createSOAPBody();
                    //TODO: for multiple messages, need to specify parts
                    //Always has to be literal
                    soapBody.setUse(SOAPMessageBase.Use.LITERAL);
                    if(!isDocOriented){
                        soapBody.setNamespace(wsdlModel.getDefinitions().getTargetNamespace());
                    }
                    bindingInput.addExtensibilityElement(soapBody);
                    bOp.setBindingInput(bindingInput);
                }
                //create output binding to SOAP
                if(outputMessage != null){
                    //TODO: same comments as in InputMessage
                    BindingOutput bindingOutput = factory.createBindingOutput();
                    SOAPBody soapBody = factory.createSOAPBody();
                    soapBody.setUse(SOAPMessageBase.Use.LITERAL);
                    if(!isDocOriented){
                        soapBody.setNamespace(wsdlModel.getDefinitions().getTargetNamespace());
                    }
                    bindingOutput.addExtensibilityElement(soapBody);
                    bOp.setBindingOutput(bindingOutput);
                }
                //create fault binding to SOAP
                for (Message faultMessage:faultMessages) {
                    BindingFault bindingFault = factory.createBindingFault();
                    bindingFault.setName(faultMessage.getName());
                    SOAPFault soapFault = factory.createSOAPFault();
                    soapFault.setName(faultMessage.getName());
                    soapFault.setUse(SOAPMessageBase.Use.LITERAL);
                    bindingFault.addExtensibilityElement(soapFault);
                    bOp.addBindingFault(bindingFault);
                }
                binding.addBindingOperation(bOp);
            }
        }
    }
    
    //converts the wsdlOperation name to Java name according to JAXWS rules
    private String convertOperationName(final String wsdlOperation){
        String name = wsdlOperation;
        String firstChar = name.substring(0,1);
        firstChar = firstChar.toLowerCase();
        name= firstChar.concat(name.substring(1));
        return name;
    }
    
    /** call wsimport to generate java artifacts
     * generate WsdlModel to find information about the new operation
     * add new menthod to implementation class
     */
    public void generateJavaArtifacts(String serviceName,
            final FileObject implementationClass, final String operationName, final boolean remove) throws IOException{
        Project project = FileOwnerQuery.getOwner(implementationClass);
       org.netbeans.modules.websvc.design.javamodel.Utils.invokeWsImport(project,serviceName);
        
        try {
            WsdlModeler modeler = WsdlModelerFactory.getDefault().getWsdlModeler(wsdlFile.toURI().toURL());
            modeler.generateWsdlModel(new WsdlModelListener() {
                public void modelCreated(WsdlModel wsdlModel) {
                    MethodGenerator generator = new MethodGenerator(wsdlModel,implementationClass);
                    if(!remove){
                        generator.generateMethod(operationName);
                    }else{
                        try                     {
                            org.netbeans.modules.websvc.core.MethodGenerator.
                                    deleteMethod(implementationClass,operationName);
                        } catch (IOException ex) {
                            ErrorManager.getDefault().notify(ex);
                        }
                    }
                }
            },true);
        } catch (MalformedURLException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    
    
    public static String getServiceEndpointInterfaceFromAnnotation(FileObject implBean){
        return  org.netbeans.modules.websvc.design.javamodel.Utils
                .getAttributeValue(implBean, "javax.jws.WebService", "endpointInterface");
    }
    
    public static String getPortTypeNameFromInterface(FileObject interfaceClass){
        //if interface, use the @WebService.name attribute. If no such attribute
        //use the simple name of the interface
        String portTypeName = 
                org.netbeans.modules.websvc.design.javamodel.Utils.getAttributeValue(interfaceClass, "javax.jws.WebService", "name");
        if(portTypeName.equals("")){
            portTypeName = interfaceClass.getName();
        }
        return portTypeName;
    }
    
    public static String getPortTypeNameFromImpl(FileObject implClass){
        
        String seiName = getServiceEndpointInterfaceFromAnnotation(implClass);
        if(!seiName.equals("")){
            ClassPath classPath = ClassPath.getClassPath(implClass, ClassPath.SOURCE);
            FileObject seiFile = classPath.findResource(seiName.replace('.', '/') + ".java");
            if(seiFile != null){
                return getPortTypeNameFromInterface(seiFile);
            }
        }else{
            String portTypeName = 
                    org.netbeans.modules.websvc.design.javamodel.Utils.getAttributeValue(implClass, "javax.jws.WebService", "name");
            if(!portTypeName.equals("")){
                return portTypeName;
            }
        }
        return implClass.getName();
    }
    
    
    //TODO: This is a temporary utility. This will go away when the copying of the
    //modified wsdl from the src/conf directory to the WEB-INF/wsdl directory is
    //done in the build script.
    public static FileObject getWsdlFolderForService(FileObject fo, String name) throws IOException {
        JAXWSSupport jaxwssupport = JAXWSSupport.getJAXWSSupport(fo);
        FileObject globalWsdlFolder = jaxwssupport.getWsdlFolder(true);
        FileObject oldWsdlFolder = globalWsdlFolder.getFileObject(name);
        if (oldWsdlFolder!=null) {
            FileLock lock = oldWsdlFolder.lock();
            try {
                oldWsdlFolder.delete(lock);
            } finally {
                lock.releaseLock();
            }
        }
        return globalWsdlFolder.createFolder(name);
    }
    
    //TODO: This is a temporary utility. This will go away when the copying of the
    //modified wsdl from the src/conf directory to the WEB-INF/wsdl directory is
    //done in the build script.
    public static FileObject getLocalWsdlFolderForService(FileObject fo, String serviceName){
        JAXWSSupport jaxwssupport = JAXWSSupport.getJAXWSSupport(fo);
        return jaxwssupport.getLocalWsdlFolderForService(serviceName, false);
    }
    
    private String getUniqueGlobalElementName(Schema schema, String baseName){
        String bName = baseName;
        int suffix = 0;
        Collection<GlobalElement> elements = schema.getElements();
        for(GlobalElement element : elements){
            if(element.getName().equals(bName)){
                bName = baseName + "_" + ++suffix;
            }
        }
        return bName;
    }
    
    private String getUniquePartName(Message message, String baseName){
        String bName = baseName;
        int suffix = 0;
        Collection<Part> parts = message.getParts();
        for(Part part : parts){
            if(part.getName().equals(bName)){
                bName = baseName + ++suffix;
            }
        }
        return bName;
    }
    
    private SchemaModel createSchemaModel(WSDLComponentFactory factory, Definitions definitions, Types types) {
        WSDLSchema wsdlSchema = factory.createWSDLSchema();
        types.addExtensibilityElement(wsdlSchema);
        SchemaModel schemaModel = wsdlSchema.getSchemaModel();
        schemaModel.getSchema().setTargetNamespace(definitions.getTargetNamespace());
        return schemaModel;
    }
    
    public static void changeWSDLOperationName(final ServiceModel serviceModel, final Service service, MethodModel methodModel, String newOperationName){
        String oldOperationName = methodModel.getOperationName();
        if(oldOperationName.equals(newOperationName)) return;
        
        //before making the change, make sure SEI is generated.
        FileObject implClass = serviceModel.getImplementationClass();
        ClassPath classPath = ClassPath.getClassPath(implClass, ClassPath.SOURCE);
        final Project project = FileOwnerQuery.getOwner(implClass);
        String seiPath = "build/generated/wsimport/service/" + serviceModel.getEndpointInterface().replace('.', '/') + ".java"; //NOI18N
        FileObject seiClass = project.getProjectDirectory().getFileObject(seiPath);
        if(seiClass == null){
            org.netbeans.modules.websvc.design.javamodel.Utils.invokeWsImport(project, service.getName());
        }
        File wsdlFile = getWSDLFile(service, methodModel);
        WSDLModel wsdlModel = WSDLUtils.getWSDLModel(FileUtil.toFileObject(wsdlFile), true);
        PortType portType = null;
        Operation operation = null;
        String portTypeName = getPortTypeNameFromImpl(methodModel.getImplementationClass());
        Definitions definitions = wsdlModel.getDefinitions();
        Collection<PortType> portTypes = definitions.getPortTypes();
        for(PortType pt : portTypes){
            if( pt.getName().equals(portTypeName)){
                portType = pt;
                break;
            }
        }
        if(portType != null){
            Collection<Operation> operations = portType.getOperations();
            for(Operation op : operations){
                if(op.getName().equals(oldOperationName)){
                    operation = op;
                    try{
                        wsdlModel.startTransaction();
                        operation.setName(newOperationName);
                    }finally{
                        wsdlModel.endTransaction();
                    }
                    break;
                }
            }
            
            //Need to maintain wrapped style
            if(operation != null){
                if(isWrapperQualified(operation, oldOperationName)){
                    Input input = operation.getInput();
                    if(input != null){
                        NamedComponentReference<Message> messageRef = input.getMessage();
                        Message message = messageRef.get();
                        Part part = message.getParts().iterator().next();
                        NamedComponentReference<GlobalElement> elementRef = part.getElement();
                        GlobalElement element = elementRef.get();
                        SchemaModel schemaModel = element.getModel();
                        //TODO: global elements in schemas that import other schemas
                        
                        //create a new Global Element
                        GlobalElement gb = getGlobalElement(schemaModel.getSchema(), newOperationName);
                        if(gb == null){
                            try{
                                schemaModel.startTransaction();
                                gb = schemaModel.getFactory().createGlobalElement();
                                gb.setName(newOperationName);
                                //get the type that the previous GlobalElement refers to:
                                NamedComponentReference<? extends GlobalType> typeRef = element.getType();
                                GlobalType gt = typeRef.get();
                                NamedComponentReference<GlobalType> gtRef = gb.createReferenceTo(gt, GlobalType.class);
                                gb.setType(gtRef);
                                schemaModel.getSchema().addElement(gb);
                            }finally{
                                schemaModel.endTransaction();
                            }
                        }
                        try{
                            wsdlModel.startTransaction();
                            message.removePart(part);
                            Part newPart = wsdlModel.getFactory().createPart();
                            newPart.setName(newOperationName + "Part");
                            NamedComponentReference<GlobalElement> gbRef = newPart.createSchemaReference(gb, GlobalElement.class);
                            newPart.setElement(gbRef);
                            message.addPart(newPart);
                        }finally{
                            wsdlModel.endTransaction();
                        }
                    }
                }
            }
            
            //add Customization to maintain generated Java method name
            String javaName = org.netbeans.modules.websvc.design.javamodel.Utils.getCurrentJavaName(methodModel);
            addMethodCustomization(wsdlModel, operation, javaName);
            
            //Change the names in the binding section too
            Collection<Binding> bindings = definitions.getBindings();
            if(bindings.size() == 0) return;
            Binding binding = null;
            for(Binding b: bindings){
                NamedComponentReference<PortType> portTypeRef = b.getType();
                PortType pt = portTypeRef.get();
                if(portType.getName().equals(pt.getName())){
                    binding = b;
                    break;
                }
            }
            if(binding != null){
                Collection<BindingOperation>bindingOps = binding.getBindingOperations();
                for(BindingOperation bindingOp : bindingOps){
                    if(bindingOp.getName().equals(oldOperationName)){
                        try{
                            wsdlModel.startTransaction();
                            bindingOp.setName(newOperationName);
                        }finally{
                            wsdlModel.endTransaction();
                        }
                        break;
                    }
                }
            }
            
        }
    }
    
    private static GlobalElement getGlobalElement(Schema schema, String elementName){
        Collection<GlobalElement> elements = schema.getElements();
        for(GlobalElement element : elements ){
            if(element.getName().equals(elementName)){
                return element;
            }
        }
        return null;
    }
    
    private static boolean isWrapperQualified(Operation operation, String oldName){
        Input input = operation.getInput();
        Output output = operation.getOutput();
        if(input == null && output == null) return true;
        if(input != null){
            Message inputMessage = operation.getInput().getMessage().get();
            if(inputMessage != null){
                if(! isWrapperQualified(inputMessage, oldName, true)){
                    return false;
                }
            }
        }
        if(output != null){
            Message outputMessage = operation.getOutput().getMessage().get();
            if(outputMessage != null){
                if(! isWrapperQualified(outputMessage, oldName, false)){
                    return false;
                }
            }
        }
        return true;
    }
    
    private static boolean isWrapperQualified(Message message, String oldName, boolean isInput){
        if(message.getParts().size() != 1){
            return false;
        }
        NamedComponentReference<GlobalElement> geRef = message.getParts().iterator().next().getElement();
        GlobalElement ge = geRef.get();
        if(isInput){
            if(!ge.getName().equals(oldName)){
                return false;
            }
        }
        NamedComponentReference<? extends GlobalType> typeRef = ge.getType();
        GlobalType gt = typeRef.get();
        if(!( gt  instanceof ComplexType)){
            return false;
        }
        ComplexType ct = (ComplexType)gt;
        ComplexTypeDefinition ctdef = ct.getDefinition();
        if(!(ctdef instanceof SequenceDefinition)){
            return false;
        }
        return true;
    }
    
    private static File getWSDLFile(Service service, MethodModel methodModel){
        String localWsdlUrl = service.getLocalWsdlFile();
        if (localWsdlUrl!=null) { //WS from e
            JAXWSSupport support = JAXWSSupport.getJAXWSSupport(methodModel.getImplementationClass());
            if (support!=null) {
                FileObject localWsdlFolder = support.getLocalWsdlFolderForService(service.getName(),false);
                if (localWsdlFolder!=null) {
                    File wsdlFolder = FileUtil.toFile(localWsdlFolder);
                    return  new File(wsdlFolder.getAbsolutePath()+File.separator+localWsdlUrl);
                }
            }
        }
        return null;
    }
    
    private static void addMethodCustomization(WSDLModel wsdlModel, Operation operation, String javaName){
        //if there is an existing customization, reset it if necessary
        List<PortTypeOperationCustomization> operationBindings = operation.getExtensibilityElements(PortTypeOperationCustomization.class);
        if(operationBindings.size() > 0){
            PortTypeOperationCustomization operationBinding = operationBindings.get(0);
            JavaMethod javaMethod = operationBinding.getJavaMethod();
            if(!javaMethod.getName().equals(javaName)){
                try{
                    wsdlModel.startTransaction();
                    javaMethod.setName(javaName);
                }finally{
                    wsdlModel.endTransaction();
                }
            }
            return;
        }
        try{
            wsdlModel.startTransaction();
            CustomizationComponentFactory factory = CustomizationComponentFactory.getDefault();
            PortTypeOperationCustomization ptoCustom = factory.createPortTypeOperationCustomization(wsdlModel);
            JavaMethod javaMethod = factory.createJavaMethod(wsdlModel);
            javaMethod.setName(javaName);
            ptoCustom.setJavaMethod(javaMethod);
            operation.addExtensibilityElement(ptoCustom);
        }finally{
            wsdlModel.endTransaction();
        }
    }
    
    
    
}

