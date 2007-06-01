/* * The contents of this file are subject to the terms of the Common Development * and Distribution License (the License). You may not use this file except in * compliance with the License. * * You can obtain a copy of the License at http://www.netbeans.org/cddl.html * or http://www.netbeans.org/cddl.txt. * When distributing Covered Code, include this CDDL Header Notice in each file * and include the License file at http://www.netbeans.org/cddl.txt. * If applicable, add the following below the CDDL Header, with the fields * enclosed by brackets [] replaced by your own identifying information: * "Portions Copyrighted [year] [name of copyright owner]" * * The Original Software is NetBeans. The Initial Developer of the Original * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun * Microsystems, Inc. All Rights Reserved. */package org.netbeans.modules.uml.codegen.ui;import org.netbeans.modules.uml.propertysupport.options.api.UMLOptionsPanel;import java.util.Hashtable;import javax.swing.JComponent;import org.openide.util.NbBundle;/** * * @author krichard */public class DomainTemplatesOptionsPanel implements UMLOptionsPanel{    private final boolean debug = true ;    private DomainTemplatesManagerPanel form = null ;        /** Creates a new instance of OptionsPanel */        public DomainTemplatesOptionsPanel()    {    }        public void update() {        form.load ();    }        public void applyChanges()    {        form.store() ;    }        public JComponent create()    {        if (form == null)            form = new DomainTemplatesManagerPanel() ;        return form ;    }        public Hashtable getCurrentValues()    {        Hashtable p = new Hashtable() ;        return p ;    }        public Hashtable getUpdatedValues()    {        Hashtable p = new Hashtable() ;        return p ;    }        public String getDisplayName()    {        return loc("CODE_GEN_OPTIONS") ; // NOI18N    }        private String loc(String key)    {        return NbBundle.getMessage(DomainTemplatesOptionsPanel.class, key) ; // NOI18N    }        private void log(String s)    {        if (debug) System.out.println(this.getClass().toString()+"::"+s); // NOI18N    }}