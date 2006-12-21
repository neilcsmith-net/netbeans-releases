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
package org.netbeans.modules.vmd.game.dialog;

import org.netbeans.modules.vmd.game.model.AnimatedTile;
import org.netbeans.modules.vmd.game.model.GlobalRepository;
import org.netbeans.modules.vmd.game.model.ImageResource;
import org.netbeans.modules.vmd.game.model.Sprite;

public class RenameAnimatedTileDialog extends AbstractNamingDialog {

	private AnimatedTile tile;
	
	public RenameAnimatedTileDialog(AnimatedTile tile) {
		this.tile = tile;
	}
	
	protected String getInitialStateDescriptionText() {
		return "Enter animated tile name.";
	}
	
	protected String getNameLabelText() {
		return "Animated tile name:";
	}
	protected String getDialogNameText() {
		return "Rename animated tile";
	}
	protected String getCurrentStateErrorText() {
		String errMsg = null; 
		String tileName = this.fieldName.getText();
		
		if (this.tile.getImageResource().getAnimatedTileByName(tileName) != null) {
			errMsg = "Animated tile name already exists.";
		}
		return errMsg;
	}
	
	protected void handleOKButton() {
		this.tile.setName(this.fieldName.getText());
		this.frame.dispose();
	}
	
}
