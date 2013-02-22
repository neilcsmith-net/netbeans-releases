/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.j2ee.ejbcore.api.codegeneration;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ModifiersTree;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.TypeElement;
import org.netbeans.api.j2ee.core.Profile;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TreeMaker;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.common.Util;
import org.netbeans.modules.j2ee.core.api.support.java.GenerationUtils;
import org.netbeans.modules.j2ee.deployment.common.api.MessageDestination;
import org.netbeans.modules.j2ee.dd.api.common.VersionNotSupportedException;
import org.netbeans.modules.j2ee.dd.api.ejb.ActivationConfig;
import org.netbeans.modules.j2ee.dd.api.ejb.ActivationConfigProperty;
import org.netbeans.modules.j2ee.dd.api.ejb.AssemblyDescriptor;
import org.netbeans.modules.j2ee.dd.api.ejb.ContainerTransaction;
import org.netbeans.modules.j2ee.dd.api.ejb.DDProvider;
import org.netbeans.modules.j2ee.dd.api.ejb.EnterpriseBeans;
import org.netbeans.modules.j2ee.dd.api.ejb.MessageDriven;
import org.netbeans.modules.j2ee.dd.api.ejb.Method;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.ejbcore.EjbGenerationUtil;
import org.netbeans.modules.j2ee.ejbcore.ejb.wizard.mdb.ActivationConfigProperties;
import org.netbeans.modules.j2ee.ejbcore.naming.EJBNameOptions;
import org.netbeans.modules.javaee.resources.api.JndiResourcesDefinition;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 * Generator of MessageDriven EJBs for EJB 2.1 and 3.0
 *
 * @author Martin Adamek
 */
public final class MessageGenerator {

    private static final String EJB21_EJBCLASS = "Templates/J2EE/EJB21/MessageDrivenEjbClass.java"; // NOI18N
    private static final String EJB30_MESSAGE_DRIVEN_BEAN = "Templates/J2EE/EJB30/MessageDrivenBean.java"; // NOI18N

    // informations collected in wizard
    private final FileObject pkg;
    private final MessageDestination messageDestination;
    private final boolean isSimplified;
    private final boolean isXmlBased;
    private final Map<String, String> properties;

    // EJB naming options
    private final EJBNameOptions ejbNameOptions;
    private final String ejbName;
    private final String ejbClassName;
    private final String displayName;
    
    private final String packageName;
    private final String packageNameWithDot;
    
    private final Map<String, Object> templateParameters;

    public static MessageGenerator create(Profile profile, String wizardTargetName, FileObject pkg, MessageDestination messageDestination, boolean isSimplified, Map<String, String> properties) {
        return new MessageGenerator(profile, wizardTargetName, pkg, messageDestination, isSimplified, properties, false);
    }
    
    protected MessageGenerator(Profile profile, String wizardTargetName, FileObject pkg, MessageDestination messageDestination, boolean isSimplified, Map<String, String> properties, boolean isTest) {
        this.pkg = pkg;
        this.messageDestination = messageDestination;
        this.isSimplified = isSimplified;
        this.isXmlBased = !isSimplified;
        this.properties = properties;
        this.ejbNameOptions = new EJBNameOptions();
        this.ejbName = ejbNameOptions.getMessageDrivenEjbNamePrefix() + wizardTargetName + ejbNameOptions.getMessageDrivenEjbNameSuffix();
        this.ejbClassName = ejbNameOptions.getMessageDrivenEjbClassPrefix() + wizardTargetName + ejbNameOptions.getMessageDrivenEjbClassSuffix();
        this.displayName = ejbNameOptions.getMessageDrivenDisplayNamePrefix() + wizardTargetName + ejbNameOptions.getMessageDrivenDisplayNameSuffix();
        this.packageName = EjbGenerationUtil.getSelectedPackageName(pkg);
        this.packageNameWithDot = packageName + ".";
        this.templateParameters = new HashMap<String, Object>();
        // fill all possible template parameters
        this.templateParameters.put("package", packageName);
        this.templateParameters.put("messageDestinationName", messageDestination.getName());
        this.templateParameters.put("activationConfigProperties", transformProperties(properties));
        this.templateParameters.put("useMappedName", useMappedName(profile, properties));
        if (isTest) {
            // set date, time and user to values used in goldenfiles
            this.templateParameters.put("date", "{date}");
            this.templateParameters.put("time", "{time}");
            this.templateParameters.put("user", "{user}");
        }
    }

    private static List<KeyValuePair> transformProperties(Map<String, String> properties) {
        List<KeyValuePair> props = new ArrayList<KeyValuePair>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            props.add(new KeyValuePair(entry.getKey(), entry.getValue()));
        }
        return props;
    }

    public FileObject generate() throws IOException {
        FileObject resultFileObject = null;
        if (isSimplified) {
            resultFileObject = generateEJB30Classes();
            if (isXmlBased) {
                generateEJB30Xml();
            }
        } else {
            resultFileObject = generateEJB21Classes();
            if (isXmlBased) {
                try {
                    generateEJB21Xml();
                } catch (VersionNotSupportedException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            try {
                Project project = FileOwnerQuery.getOwner(pkg);
                J2eeModuleProvider j2eeModuleProvider = project.getLookup().lookup(J2eeModuleProvider.class);
                j2eeModuleProvider.getConfigSupport().bindMdbToMessageDestination(
                        ejbName,
                        messageDestination.getName(),
                        messageDestination.getType());
            } catch (ConfigurationException ce) {
                Exceptions.printStackTrace(ce);
            }
        }
        return resultFileObject;
    }
    
    private FileObject generateEJB21Classes() throws IOException {
        return GenerationUtils.createClass(EJB21_EJBCLASS,  pkg, ejbClassName, null, templateParameters);
    }
    
    private boolean isQueue() {
        return MessageDestination.Type.QUEUE.equals(messageDestination.getType());
    }
    
    private FileObject generateEJB30Classes() throws IOException {
        FileObject mdb = GenerationUtils.createClass(EJB30_MESSAGE_DRIVEN_BEAN,  pkg, ejbClassName, null, templateParameters);
        if (messageDestination instanceof JmsDestinationDefinition
                && ((JmsDestinationDefinition) messageDestination).isToGenerate()) {
            generateJMSDestinationDefinition(mdb);
        }
        return mdb;
    }

    private void generateJMSDestinationDefinition(FileObject classFile) throws IOException {
        JavaSource js = JavaSource.forFileObject(classFile);
        js.runModificationTask(new Task<WorkingCopy>() {
            @Override
            public void run(WorkingCopy parameter) throws Exception {
                parameter.toPhase(JavaSource.Phase.RESOLVED);
                TypeElement classElement = parameter.getElements().getTypeElement(packageNameWithDot + ejbClassName);
                ClassTree classTree = parameter.getTrees().getTree(classElement);
                ModifiersTree modifiers = classTree.getModifiers();
                TypeElement el = parameter.getElements().getTypeElement(JndiResourcesDefinition.ANN_JMS_DESTINATION);

                TreeMaker tm = parameter.getTreeMaker();
                List<ExpressionTree> values = new ArrayList<ExpressionTree>(2);
                ExpressionTree nameQualIdent = tm.QualIdent("name"); //NOI18N
                values.add(tm.Assignment(nameQualIdent, tm.Literal(properties.get("destinationLookup")))); //NOI18N
                ExpressionTree classnameQualIdent = tm.QualIdent("className"); //NOI18N
                values.add(tm.Assignment(classnameQualIdent, tm.Literal(properties.get("destinationType")))); //NOI18N

                List<AnnotationTree> annotations = new ArrayList<AnnotationTree>(modifiers.getAnnotations());
                annotations.add(0, tm.Annotation(tm.QualIdent(el), values));
                ModifiersTree nueMods = tm.Modifiers(modifiers, annotations);
                parameter.rewrite(modifiers, nueMods);
            }
        }).commit();
    }
    
    @SuppressWarnings("deprecation") //NOI18N
    private void generateEJB21Xml() throws IOException, VersionNotSupportedException {
        org.netbeans.modules.j2ee.api.ejbjar.EjbJar ejbModule = org.netbeans.modules.j2ee.api.ejbjar.EjbJar.getEjbJar(pkg);
        org.netbeans.modules.j2ee.dd.api.ejb.EjbJar ejbJar = DDProvider.getDefault().getDDRoot(ejbModule.getDeploymentDescriptor()); // EJB 2.1
        EnterpriseBeans beans = ejbJar.getEnterpriseBeans();
        MessageDriven messageDriven = null;
        if (beans == null) {
            beans  = ejbJar.newEnterpriseBeans();
            ejbJar.setEnterpriseBeans(beans);
        }
        messageDriven = beans.newMessageDriven();
        ActivationConfig config = messageDriven.newActivationConfig();
        ActivationConfigProperty destProp = config.newActivationConfigProperty();
        destProp.setActivationConfigPropertyName("destinationType"); // NOI18N
        ActivationConfigProperty ackProp = config.newActivationConfigProperty();
        ackProp.setActivationConfigPropertyName("acknowledgeMode"); // NOI18N
        ackProp.setActivationConfigPropertyValue("Auto-acknowledge"); // NOI18N
        config.addActivationConfigProperty(ackProp);
        if (isQueue()) {
            String queue = "javax.jms.Queue"; // NOI18N
            messageDriven.setMessageDestinationType(queue);
            destProp.setActivationConfigPropertyValue(queue);
        } else {
            String topic = "javax.jms.Topic"; // NOI18N
            messageDriven.setMessageDestinationType(topic);
            destProp.setActivationConfigPropertyValue(topic);
            ActivationConfigProperty durabilityProp = config.newActivationConfigProperty();
            durabilityProp.setActivationConfigPropertyName("subscriptionDurability"); // NOI18N
            durabilityProp.setActivationConfigPropertyValue("Durable"); // NOI18N
            config.addActivationConfigProperty(durabilityProp);
            
            ActivationConfigProperty clientIdProp = config.newActivationConfigProperty();
            clientIdProp.setActivationConfigPropertyName("clientId"); // NOI18N
            clientIdProp.setActivationConfigPropertyValue(ejbName); // NOI18N
            config.addActivationConfigProperty(clientIdProp);
            
            ActivationConfigProperty subscriptionNameProp = config.newActivationConfigProperty();
            subscriptionNameProp.setActivationConfigPropertyName("subscriptionName"); // NOI18N
            subscriptionNameProp.setActivationConfigPropertyValue(ejbName); // NOI18N
            config.addActivationConfigProperty(subscriptionNameProp);
            
        }
        config.addActivationConfigProperty(destProp);
        messageDriven.setActivationConfig(config);
        messageDriven.setEjbName(ejbName);
        messageDriven.setDisplayName(displayName);
        messageDriven.setEjbClass(packageNameWithDot + ejbClassName);
        messageDriven.setTransactionType(MessageDriven.TRANSACTION_TYPE_CONTAINER);
        
        beans.addMessageDriven(messageDriven);
        // add transaction requirements
        AssemblyDescriptor assemblyDescriptor = ejbJar.getSingleAssemblyDescriptor();
        if (assemblyDescriptor == null) {
            assemblyDescriptor = ejbJar.newAssemblyDescriptor();
            ejbJar.setAssemblyDescriptor(assemblyDescriptor);
        }
        org.netbeans.modules.j2ee.dd.api.common.MessageDestination ddMessageDestination = assemblyDescriptor.newMessageDestination();
        String destinationLink = messageDestination.getName();
        ddMessageDestination.setDisplayName("Destination for " + displayName);
        ddMessageDestination.setMessageDestinationName(destinationLink);
        assemblyDescriptor.addMessageDestination(ddMessageDestination);
        
        messageDriven.setMessageDestinationLink(destinationLink);
        ContainerTransaction containerTransaction = assemblyDescriptor.newContainerTransaction();
        containerTransaction.setTransAttribute("Required"); //NOI18N
        Method method = containerTransaction.newMethod();
        method.setEjbName(ejbName);
        method.setMethodName("*"); //NOI18N
        containerTransaction.addMethod(method);
        assemblyDescriptor.addContainerTransaction(containerTransaction);
        ejbJar.write(ejbModule.getDeploymentDescriptor());
    }
    
    private void generateEJB30Xml() throws IOException {
        throw new UnsupportedOperationException("Method not implemented yet.");
    }

    private static boolean useMappedName(Profile profile, Map<String, String> acProperties) {
        if (Util.isAtLeastJavaEE7Web(profile)) {
            String destinationLookup = acProperties.get(ActivationConfigProperties.DESTINATION_LOOKUP);
            return destinationLookup == null || destinationLookup.isEmpty();
        }
        return true;
    }

    public static final class KeyValuePair {

        private String key;
        private String value;

        public KeyValuePair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}
