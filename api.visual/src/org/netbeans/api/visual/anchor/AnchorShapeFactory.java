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
package org.netbeans.api.visual.anchor;

import org.netbeans.modules.visual.anchor.ImageAnchorShape;
import org.netbeans.modules.visual.anchor.TriangleAnchorShape;
import org.netbeans.modules.visual.anchor.ArrowAnchorShape;

import java.awt.*;

/**
 * The factory class of all built-in anchor shapes.
 * The instances of all built-in anchor shapes can be used multiple connection widgets.
 *
 * @author David Kaspar
 */
public class AnchorShapeFactory {

    private AnchorShapeFactory () {
    }

    /**
     * Creates an image anchor shape.
     * @param image the image
     * @return the anchor shape
     */
    public static AnchorShape createImageAnchorShape (Image image) {
        return createImageAnchorShape (image, false);
    }

    /**
     * Creates an image anchor shape with ability to specify line orientation.
     * @param image the image
     * @param lineOriented if true, then the image is line oriented
     * @return the anchor shape
     */
    public static AnchorShape createImageAnchorShape (Image image, boolean lineOriented) {
        return new ImageAnchorShape (image, lineOriented);
    }

    /**
     * Creates a triangular anchor shape.
     * @param size the size of triangle
     * @param filled if true, then the triangle is filled
     * @param output if true, then it is output triangle
     * @return the anchor shape
     */
    public static AnchorShape createTriangleAnchorShape (int size, boolean filled, boolean output) {
        return new TriangleAnchorShape (size, filled, output, false, 0.0);
    }

    /**
     * Creates a triangular anchor shape.
     * @param size the size of triangle
     * @param filled if true, then the triangle is filled
     * @param output if true, then it is output triangle
     * @param cutDistance the distance where the related line is cut (usually 1px smaller than the size)
     * @return the anchor shape
     */
    public static AnchorShape createTriangleAnchorShape (int size, boolean filled, boolean output, int cutDistance) {
        return new TriangleAnchorShape (size, filled, output, false, cutDistance);
    }

    /**
     * Creates an arrow anchor shape.
     * @param degrees the angle of the arrow in degrees (not radians)
     * @param size the size of the arrow
     * @return the anchor shape
     * @since 2.4
     */
    public static AnchorShape createArrowAnchorShape (int degrees, int size) {
        return new ArrowAnchorShape (degrees, size);
    }

}
