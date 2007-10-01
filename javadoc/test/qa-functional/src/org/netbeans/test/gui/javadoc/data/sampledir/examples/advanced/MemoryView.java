/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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

package examples.advanced;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.Timer;

/** Frame to display amount of free memory in the running application.
* <P>
* Handy for use with the IDE's internal execution. Then the statistic
* of free memory in the whole environment is displayed.
*/
public class MemoryView extends javax.swing.JFrame {
    /** bundle to use */
    private static ResourceBundle bundle = ResourceBundle.getBundle ("examples.advanced.MemoryViewLocale");
    /** message of free memory */
    private static MessageFormat msgMemory = new MessageFormat (bundle.getString ("TXT_STATUS"));

    /** default update time */
    private static final int UPDATE_TIME = 1000;
    /** timer to invoke updating */
    private Timer timer;


    /** Initializes the Form */
    public MemoryView() {
        initComponents ();

        setTitle (bundle.getString ("TXT_TITLE"));
        doGarbage.setText (bundle.getString ("TXT_GARBAGE"));
        doRefresh.setText (bundle.getString ("TXT_REFRESH"));
        doClose.setText (bundle.getString ("TXT_CLOSE"));

        txtTime.setText (bundle.getString ("TXT_TIME"));
        doTime.setText (bundle.getString ("TXT_SET_TIME"));
        time.setText (String.valueOf (UPDATE_TIME));
        time.selectAll ();
        time.requestFocus ();

        updateStatus ();

        timer = new Timer (UPDATE_TIME, new ActionListener () {
                               public void actionPerformed (ActionEvent ev) {
                                   updateStatus ();
                               }
                           });
        timer.setRepeats (true);

        pack ();
    }

    /** Starts the timer.
    */
    public void addNotify () {
        super.addNotify ();
        timer.start ();
    }

    /** Stops the timer.
    */
    public void removeNotify () {
        super.removeNotify ();
        timer.stop ();
    }

    /** Updates the status of all components */
    private void updateStatus () {
        Runtime r = Runtime.getRuntime ();
        long free = r.freeMemory ();
        long total = r.totalMemory ();

        // when bigger than integer then divide by two
        while (total > Integer.MAX_VALUE) {
            total = total >> 1;
            free = free >> 1;
        }

        int taken = (int) (total - free);

        status.setMaximum ((int)total);
        status.setValue (taken);

        text.setText (msgMemory.format (new Object[] {
                                            new Long (total),
                                            new Long (free),
                                            new Integer (taken)
                                        }));
        text.invalidate ();
        validate ();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        text = new javax.swing.JLabel();
        status = new javax.swing.JProgressBar();
        jPanel2 = new javax.swing.JPanel();
        doGarbage = new javax.swing.JButton();
        doRefresh = new javax.swing.JButton();
        doClose = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        txtTime = new javax.swing.JLabel();
        time = new javax.swing.JTextField();
        doTime = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel1.add(text, java.awt.BorderLayout.SOUTH);

        jPanel1.add(status, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        doGarbage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doGarbageActionPerformed(evt);
            }
        });

        jPanel2.add(doGarbage);

        doRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doRefreshActionPerformed(evt);
            }
        });

        jPanel2.add(doRefresh);

        doClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doCloseActionPerformed(evt);
            }
        });

        jPanel2.add(doClose);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        jPanel3.setLayout(new java.awt.BorderLayout(0, 20));

        jPanel3.add(txtTime, java.awt.BorderLayout.WEST);

        jPanel3.add(time, java.awt.BorderLayout.CENTER);

        doTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setRefreshTime(evt);
            }
        });

        jPanel3.add(doTime, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanel3, java.awt.BorderLayout.NORTH);

    }//GEN-END:initComponents

    /** Exit the Application */
    private void exitForm (java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit( 0 );
    }//GEN-LAST:event_exitForm

    private void setRefreshTime (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setRefreshTime
        try {
            int rate = Integer.valueOf (time.getText ()).intValue ();
            timer.setDelay (rate);
        } catch (NumberFormatException ex) {
            time.setText (String.valueOf (timer.getDelay ()));
        }
        time.selectAll ();
        time.requestFocus ();
    }//GEN-LAST:event_setRefreshTime


    private void doCloseActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doCloseActionPerformed
        exitForm (null);
    }//GEN-LAST:event_doCloseActionPerformed


    private void doRefreshActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doRefreshActionPerformed
        updateStatus ();
    }//GEN-LAST:event_doRefreshActionPerformed

    private void doGarbageActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doGarbageActionPerformed
        System.gc ();
        updateStatus ();
    }//GEN-LAST:event_doGarbageActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar status;
    private javax.swing.JTextField time;
    private javax.swing.JLabel txtTime;
    private javax.swing.JButton doGarbage;
    private javax.swing.JButton doClose;
    private javax.swing.JButton doTime;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton doRefresh;
    private javax.swing.JLabel text;
    // End of variables declaration//GEN-END:variables


    /** Opens memory view window in middle of screen
    */
    public static void main(java.lang.String[] args) {
        MemoryView mv = new MemoryView ();
        Dimension d = Toolkit.getDefaultToolkit ().getScreenSize ();
        Dimension m = mv.getSize ();
        d.width -= m.width;
        d.height -= m.height;
        d.width /= 2;
        d.height /= 2;
        mv.setLocation (d.width, d.height);
        mv.setVisible (true);
    }

}
