/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.rmi.wizard;

import javax.swing.*;

/**
 *
 * @author  mryzl
 * @version 
 */
public class RMITypePanel extends AbstractWizardPanel {

    static final long serialVersionUID =-4227236938717048298L;
    /** Creates new form RMITypePanel */
    public RMITypePanel() {
        initComponents ();
        jRButtons = new JRadioButton[] { jRadioButton4, jRadioButton5, jRadioButton6 };
        bgroup = new ButtonGroup();
        bgroup.add(jRadioButton4);
        bgroup.add(jRadioButton5);
        bgroup.add(jRadioButton6);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {//GEN-BEGIN:initComponents
        setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints1;
        setPreferredSize (new java.awt.Dimension(480, 320));
        setBorder (new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));

        jLabel1 = new javax.swing.JLabel ();
        jLabel1.setFont (new java.awt.Font ("Dialog", 0, 18));
        jLabel1.setText ("Type of RMI Object");


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.ipadx = 5;
        gridBagConstraints1.ipady = 5;
        gridBagConstraints1.insets = new java.awt.Insets (2, 2, 10, 2);
        add (jLabel1, gridBagConstraints1);

        jPanel1 = new javax.swing.JPanel ();
        jPanel1.setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints2;

        jRadioButton4 = new javax.swing.JRadioButton ();
        jRadioButton4.setText ("Extends java.rmi.server.UnicastRemoteObject");
        jRadioButton4.addActionListener (new java.awt.event.ActionListener () {
                                             public void actionPerformed (java.awt.event.ActionEvent evt) {
                                                 jRadioButton1ActionPerformed (evt);
                                             }
                                         }
                                        );

        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets (5, 5, 5, 5);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints2.weightx = 1.0;
        jPanel1.add (jRadioButton4, gridBagConstraints2);

        jRadioButton5 = new javax.swing.JRadioButton ();
        jRadioButton5.setText ("Extends java.rmi.activation.Activatable");
        jRadioButton5.addActionListener (new java.awt.event.ActionListener () {
                                             public void actionPerformed (java.awt.event.ActionEvent evt) {
                                                 jRadioButton2ActionPerformed (evt);
                                             }
                                         }
                                        );

        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets (5, 5, 5, 5);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints2.weightx = 1.0;
        jPanel1.add (jRadioButton5, gridBagConstraints2);

        jRadioButton6 = new javax.swing.JRadioButton ();
        jRadioButton6.setText ("Extends java.lang.Object");
        jRadioButton6.addActionListener (new java.awt.event.ActionListener () {
                                             public void actionPerformed (java.awt.event.ActionEvent evt) {
                                                 jRadioButton3ActionPerformed (evt);
                                             }
                                         }
                                        );

        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 2;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets (5, 5, 15, 5);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints2.weightx = 1.0;
        jPanel1.add (jRadioButton6, gridBagConstraints2);


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0;
        add (jPanel1, gridBagConstraints1);

        jPanel2 = new javax.swing.JPanel ();
        jPanel2.setLayout (new java.awt.FlowLayout ());


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weighty = 1.0;
        add (jPanel2, gridBagConstraints1);

    }//GEN-END:initComponents

    private void jRadioButton3ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        // Add your handling code here:
        type = RMIWizardData.TYPE_OTHER;
        fireChange();
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton2ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        // Add your handling code here:
        type = RMIWizardData.TYPE_ACTIVATABLE;
        fireChange();
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton1ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // Add your handling code here:
        type = RMIWizardData.TYPE_UNICAST_REMOTE_OBJECT;
        fireChange();
    }//GEN-LAST:event_jRadioButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables

    protected JRadioButton[] jRButtons;

    protected void setEnabledAllButtons(boolean en) {
        for(int i = 0; i < jRButtons.length; i++) jRButtons[i].setEnabled(en);
    }

    protected void enableRButton(JRadioButton button) {
        setEnabledAllButtons(false);
        button.setEnabled(true);
    }


    // WizardDescriptor.Panel methods

    private ButtonGroup bgroup;
    protected int type;

    /** Provides the wizard panel with the current data--either
    * the default data or already-modified settings, if the user used the previous and/or next buttons.
    * This method can be called multiple times on one instance of <code>WizardDescriptor.Panel</code>.
    * @param settings the object representing wizard panel state, as originally supplied to {@link WizardDescriptor#WizardDescriptor(WizardDescriptor.Iterator,Object)}
    */
    public void readRMISettings (RMIWizardData data) {
        type = data.type;
        try {
            if (data.lockType) enableRButton(jRButtons[type]);
            else setEnabledAllButtons(true);
            jRButtons[type].setSelected(true);
        } catch (IndexOutOfBoundsException ex) {
            // missed
        }
    }

    /** Provides the wizard panel with the opportunity to update the
    * settings with its current customized state.
    * Rather than updating its settings with every change in the GUI, it should collect them,
    * and then only save them when requested to by this method.
    * Also, the original settings passed to {@link #readSettings} should not be modified (mutated);
    * rather, the (copy) passed in here should be mutated according to the collected changes.
    * This method can be called multiple times on one instance of <code>WizardDescriptor.Panel</code>.
    * @param settings the object representing a settings of the wizard
    */
    public void storeRMISettings (RMIWizardData data) {
        data.type = type;
    }

    /** Test whether the panel is finished and it is safe to proceed to the next one.
    * If the panel is valid, the "Next" (or "Finish") button will be enabled.
    * @return <code>true</code> if the user has entered satisfactory information
    */
    public boolean isValid () {
        return true;
    }

}
/*
 * <<Log>>
 *  6    Gandalf   1.5         11/27/99 Patrik Knakal   
 *  5    Gandalf   1.4         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  4    Gandalf   1.3         8/12/99  Martin Ryzl     hints on executors and 
 *       compiler, debug executors
 *  3    Gandalf   1.2         7/27/99  Martin Ryzl     
 *  2    Gandalf   1.1         7/20/99  Martin Ryzl     
 *  1    Gandalf   1.0         7/19/99  Martin Ryzl     
 * $
 */
