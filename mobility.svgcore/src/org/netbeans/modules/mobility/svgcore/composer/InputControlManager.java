/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
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
package org.netbeans.modules.mobility.svgcore.composer;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 *
 * @author Pavel Benes
 */
public final class InputControlManager {
    private final SceneManager       m_sceneMgr;
    private final MouseController    m_mouseCtrl;
    private final KeyboardController m_keyCtrl;

    class MouseController implements MouseListener, MouseMotionListener {
        public void mouseClicked(MouseEvent e) {
            m_sceneMgr.processEvent(e);
        }

        public void mouseDragged(MouseEvent e) {
            m_sceneMgr.processEvent(e);
        }

        public void mouseEntered(MouseEvent e) {
            m_sceneMgr.processEvent(e);
        }

        public void mouseExited(MouseEvent e) {
            m_sceneMgr.processEvent(e);
        }

        public void mouseMoved(MouseEvent e) {
            m_sceneMgr.processEvent(e);
        }

        public void mousePressed(MouseEvent e) {
            m_sceneMgr.processEvent(e);
        }

        public void mouseReleased(MouseEvent e) {
            m_sceneMgr.processEvent(e);
        }
    }

    private class KeyboardController implements KeyListener {
        public void keyTyped(KeyEvent e) {
            m_sceneMgr.processEvent(e);
        }
        public void keyPressed(KeyEvent e) {
            m_sceneMgr.processEvent(e);
        }
        public void keyReleased(KeyEvent e) {
            m_sceneMgr.processEvent(e);
        }
    } 
    
    public InputControlManager(SceneManager sceneMgr) {
        m_sceneMgr  = sceneMgr;
        m_mouseCtrl = new MouseController();
        m_keyCtrl   = new KeyboardController();
    }

    void initialize() {        
        m_sceneMgr.getScreenManager().registerMouseController(m_mouseCtrl);
        m_sceneMgr.getScreenManager().registerKeyController(m_keyCtrl);
    }
}
