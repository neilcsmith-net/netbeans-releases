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
package org.netbeans.modules.vmd.midp.components.categories;

import org.netbeans.modules.vmd.api.inspector.InspectorPositionPresenter;
import org.netbeans.modules.vmd.api.model.*;
import org.netbeans.modules.vmd.api.model.presenters.InfoPresenter;
import org.netbeans.modules.vmd.api.model.presenters.actions.AddActionPresenter;
import org.netbeans.modules.vmd.midp.components.MidpVersionDescriptor;
import org.netbeans.modules.vmd.midp.components.points.PointCD;
import org.netbeans.modules.vmd.midp.general.AcceptTypePresenter;
import org.netbeans.modules.vmd.midp.inspector.controllers.CategoryPC;

import java.util.Arrays;
import java.util.List;

/**
 * @author David Kaspar
 */

public final class PointsCategoryCD extends ComponentDescriptor {

    public static final TypeID TYPEID = new TypeID (TypeID.Kind.COMPONENT, "#PointsCategory"); // NOI18N

    public TypeDescriptor getTypeDescriptor() {
        return new TypeDescriptor (CategoryCD.TYPEID, PointsCategoryCD.TYPEID, true, true);
    }

    public VersionDescriptor getVersionDescriptor() {
        return MidpVersionDescriptor.FOREVER;
    }

    public List<PropertyDescriptor> getDeclaredPropertyDescriptors() {
        return null;
    }

    protected java.util.List<? extends Presenter> createPresenters() {
        return Arrays.asList (
            // general
            InfoPresenter.createStatic ("Points", null, CategorySupport.ICON_PATH_CATEGORY_POINTS),
            // accept
            new AcceptTypePresenter (PointCD.TYPEID),
            // inspector
            InspectorPositionPresenter.create(new CategoryPC()),
            // actions
            AddActionPresenter.create(AddActionPresenter.ADD_ACTION, 30, PointCD.TYPEID) //NOI18N //TODO Localization
        );
    }

}
