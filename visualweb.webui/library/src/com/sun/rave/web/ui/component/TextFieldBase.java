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
package com.sun.rave.web.ui.component;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

/**
 * <p> Use the <code>ui:textField</code>  tag to create an input field for a single line of text.</p> 
 * 
 *     <h3>HTML Elements and Layout</h3>
 * 
 * <p>The textField component renders an XHTML <code>&lt;input
 *     type="text"&gt;</code>element.</p>  
 * 
 *     <h3>Configuring the <code>ui:textField</code> Tag</h3>
 * 
 * <p>Use the <code>text</code> attribute to associate the component with
 * a model object that represents the current value, by setting the
 * attribute's value to an EL expression that corresponds to a property
 * of a backing bean.</p>
 * 
 *     <p>To optionally specify a label for the component, use the
 *     <code>label</code> attribute, or specify a label facet. </p>
 * 
 *     <h3>Facets</h3>
 * 
 *     <ul>
 *     <li><code>label</code>: use this facet to specify a custom 
 *     component for the label.</li>
 *     <li><code>readOnly</code>: use this facet to specify a custom 
 *     component for displaying the value of this component when it is
 *     marked as readonly. The default is a <code>ui:staticText</code>. </li>
 *     </ul>
 * 
 * 
 *     <h3>Client-side JavaScript functions</h3>
 * 
 *     <p>In all the functions below, <code>&lt;id&gt;</code> should be
 *     the generated id of the TextField component. </p>
 * 
 *     <table cellpadding="2" cellspacing="2" border="1" 
 *            style="text-align: left; width: 100%;">
 *     <tbody>
 *     <tr>
 *     <td style="vertical-align">
 *     <code>field_setDisabled(&lt;id&gt;, &lt;disabled&gt;)</code>
 *     </td>
 *     <td style="vertical-align: top">
 *     Enable/disable the field. Set <code>&lt;disabled&gt;</code>
 *     to true to disable the component, or false to enable it.
 *     </td>
 *     </tr>
 *     <tr>
 *     <td style="vertical-align: top">
 *     <code>field_setValue(&lt;id&gt;, &lt;newValue&gt;)</code>
 *     </td>
 *     <td style="vertical-align: top">
 *     Set the value of the field to <code>&lt;newValue&gt;</code>.
 *     </td>
 *     </tr>
 *     <tr>
 *       <td style="vertical-align: top">
 *     <code>field_getValue(&lt;id&gt;)</code>
 *   </td>
 *     <td style="vertical-align: top">Get the value of the field.</td> </tr>
 *     <tr>
 *       <td style="vertical-align: top">
 *     <code>field_getInputElement(&lt;id&gt;)</code></td>
 *     <td style="vertical-align: top">
 *     Get hold of a reference to the input element rendered by this
 *     component.
 *     </td>
 *     </tr>
 *     <tr>
 *       <td style="vertical-align: top">
 *     <code>component_setVisible(&lt;id&gt;)</code>
 *   </td>
 *       <td style="vertical-align: top">Hide or show this component.
 *       </td>
 *     </tr>
 *   </tbody>
 * </table>
 * 
 * 
 *     <h3>Examples</h3>
 * 
 * <h4>Example 1: Text field with label and required icon</h4>
 * 
 * <p>This example uses a backing bean <code>FieldTest</code> with a
 * property <code>string</code>. The tag generates a label followed by
 * text input field. The required attribute is set to true, which causes
 * an icon to be rendered next to the label to indicate that the
 * application user must enter a value in the text field. The icon, label
 *   and input elements are enclosed by a span.</p>
 * <pre>
 *      &lt;ui:textField id="textfield" label="Enter a value:" 
 *                text="#{FieldTest.string}" 
 *                required="true"/&gt;
 * </pre>
 * 
 * <h4>Example 2:  Text field using a validator</h4> 
 * 
 * <p>
 * This example uses a backing bean <code>FieldTest</code> with a
 *     property <code>number</code>. The number property is an
 *     <code>int</code>, which means that the value must be converted to
 *     be displayed. It is not necessary to specify a Converter instance,
 *     however, since standard JSF conversion deals with this case. A
 *     Validator has been set to verify that any value entered by the
 *     user is within a certain range. The HTML elements are rendered as
 *     in example 1.</p> 
 * 
 * <pre>
 *      &lt;ui:textField id="test2" label="Enter a number:" 
 *                text="#{FieldTest.number}" 
 *                validator="#{FieldTest.checkNumberRange}"/&gt;
 * </pre>
 * <p>Auto-generated component class.
 * Do <strong>NOT</strong> modify; all changes
 * <strong>will</strong> be lost!</p>
 */

public abstract class TextFieldBase extends com.sun.rave.web.ui.component.Field {

    /**
     * <p>Construct a new <code>TextFieldBase</code>.</p>
     */
    public TextFieldBase() {
        super();
        setRendererType("com.sun.rave.web.ui.TextField");
    }

    /**
     * <p>Return the identifier of the component family to which this
     * component belongs.  This identifier, in conjunction with the value
     * of the <code>rendererType</code> property, may be used to select
     * the appropriate {@link Renderer} for this component instance.</p>
     */
    public String getFamily() {
        return "com.sun.rave.web.ui.TextField";
    }

    /**
     * <p>Restore the state of this component.</p>
     */
    public void restoreState(FacesContext _context,Object _state) {
        Object _values[] = (Object[]) _state;
        super.restoreState(_context, _values[0]);
    }

    /**
     * <p>Save the state of this component.</p>
     */
    public Object saveState(FacesContext _context) {
        Object _values[] = new Object[1];
        _values[0] = super.saveState(_context);
        return _values;
    }

}
