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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.vmd.midp.components.points;

import org.netbeans.modules.vmd.api.codegen.CodeReferencePresenter;
import org.netbeans.modules.vmd.api.model.*;
import org.netbeans.modules.vmd.api.model.presenters.InfoPresenter;
import org.netbeans.modules.vmd.api.properties.DefaultPropertiesPresenter;
import org.netbeans.modules.vmd.midp.components.MidpTypes;
import org.netbeans.modules.vmd.midp.components.MidpVersionDescriptor;
import org.netbeans.modules.vmd.midp.components.handlers.CallPointEventHandlerCD;
import org.netbeans.modules.vmd.midp.flow.FlowInfoNodePresenter;
import org.netbeans.modules.vmd.midp.palette.MidpPaletteProvider;
import org.netbeans.modules.vmd.midp.propertyeditors.PropertiesCategories;
import org.netbeans.modules.vmd.midp.propertyeditors.PropertyEditorJavaString;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.vmd.midp.actions.MidpActionsSupport;

/**
 * @author David Kaspar
 */
public class CallPointCD extends ComponentDescriptor {

    public static final TypeID TYPEID = new TypeID (TypeID.Kind.COMPONENT, "#CallPoint"); // NOI18N

    public static final String PROP_ACCESS_CODE = "accessCode"; // NOI18N

    public static final String ICON_PATH = "org/netbeans/modules/vmd/midp/resources/components/call_point_16.png"; // NOI18N

    public TypeDescriptor getTypeDescriptor () {
        return new TypeDescriptor (PointCD.TYPEID, CallPointCD.TYPEID, true, true);
    }

    public VersionDescriptor getVersionDescriptor () {
        return MidpVersionDescriptor.FOREVER;
    }

    public void postInitialize (DesignComponent component) {
        component.writeProperty (PROP_ACCESS_CODE, MidpTypes.createJavaCodeValue ("")); // NOI18N
    }

    public List<PropertyDescriptor> getDeclaredPropertyDescriptors () {
        return Arrays.asList (
                new PropertyDescriptor (PROP_ACCESS_CODE, MidpTypes.TYPEID_JAVA_CODE, PropertyValue.createNull (), false, false, Versionable.FOREVER)
        );
    }

    public PaletteDescriptor getPaletteDescriptor () {
        return new PaletteDescriptor (MidpPaletteProvider.CATEGORY_PROCESS_FLOW, "Call Point", "Call Point", ICON_PATH, null);
    }

    public DefaultPropertiesPresenter createPropertiesPresenter () {
        return new DefaultPropertiesPresenter ()
                .addPropertiesCategory (PropertiesCategories.CATEGORY_PROPERTIES)
                .addPropertiesCategory (PropertiesCategories.CATEGORY_CODE_PROPERTIES)
                    .addProperty ("Call Code", PropertyEditorJavaString.createInstance(), PROP_ACCESS_CODE);
    }

    protected void gatherPresenters (ArrayList<Presenter> presenters) {
        MidpActionsSupport.addCommonActionsPresenters (presenters, false, false, false, true, true);
        super.gatherPresenters (presenters);
    }

    protected List<? extends Presenter> createPresenters () {
        return Arrays.asList (
            // info
            InfoPresenter.create (PointSupport.createCallPointInfoResolver ()),
            // general
            CallPointEventHandlerCD.createCallPointEventHandlerCreatorPresenter (),
            // properties
            createPropertiesPresenter (),
            // flow
            new FlowInfoNodePresenter (),
            // code
            new CodeReferencePresenter () {
                protected String generateAccessCode () { return generateDirectAccessCode (); }
                protected String generateDirectAccessCode () { return MidpTypes.getJavaCode (getComponent ().readProperty (PROP_ACCESS_CODE)); }
                protected String generateTypeCode () { throw Debug.illegalState (); }
            }
        );
    }

}
