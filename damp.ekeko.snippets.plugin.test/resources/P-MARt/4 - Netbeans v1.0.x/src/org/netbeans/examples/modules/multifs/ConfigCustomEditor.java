/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is Forte for Java, Community Edition. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.examples.modules.multifs;

/**
 *
 * @author
 * @version
 */
public class ConfigCustomEditor extends javax.swing.JPanel {
    ConfigEditor ed;
    /** Initializes the Form */
    public ConfigCustomEditor(ConfigEditor ed) {
        this.ed = ed;
        initComponents ();
        loadValues ();
        checkValid ();
    }
    void checkValid () {
        valid.setText("maybe...");
    }
    void updateEd () {
        // XXX
        checkValid ();
    }
    void loadValues () {
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {//GEN-BEGIN:initComponents
        // This code was developed using a non-commercially licensed version of NetBeans Developer 2.x.
        // For details, see http://www.netbeans.com/non_commercial.html

        setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints1;

        read1 = new javax.swing.JComboBox ();
        read1.addActionListener (new java.awt.event.ActionListener () {
                                     public void actionPerformed (java.awt.event.ActionEvent evt) {
                                         read1ActionPerformed (evt);
                                     }
                                 }
                                );
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        add (read1, gridBagConstraints1);

        read2 = new javax.swing.JComboBox ();
        read2.addActionListener (new java.awt.event.ActionListener () {
                                     public void actionPerformed (java.awt.event.ActionEvent evt) {
                                         read2ActionPerformed (evt);
                                     }
                                 }
                                );
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        add (read2, gridBagConstraints1);

        defwr = new javax.swing.JComboBox ();
        defwr.addActionListener (new java.awt.event.ActionListener () {
                                     public void actionPerformed (java.awt.event.ActionEvent evt) {
                                         defwrActionPerformed (evt);
                                     }
                                 }
                                );
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 2;
        add (defwr, gridBagConstraints1);

        wr1 = new javax.swing.JComboBox ();
        wr1.addActionListener (new java.awt.event.ActionListener () {
                                   public void actionPerformed (java.awt.event.ActionEvent evt) {
                                       wr1ActionPerformed (evt);
                                   }
                               }
                              );
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 3;
        add (wr1, gridBagConstraints1);

        wr2 = new javax.swing.JComboBox ();
        wr2.addActionListener (new java.awt.event.ActionListener () {
                                   public void actionPerformed (java.awt.event.ActionEvent evt) {
                                       wr2ActionPerformed (evt);
                                   }
                               }
                              );
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 4;
        add (wr2, gridBagConstraints1);

        jLabel1 = new javax.swing.JLabel ();
        jLabel1.setText ("Read #1");
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add (jLabel1, gridBagConstraints1);

        jLabel2 = new javax.swing.JLabel ();
        jLabel2.setText ("Read #2");
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add (jLabel2, gridBagConstraints1);

        jLabel3 = new javax.swing.JLabel ();
        jLabel3.setText ("Default Write");
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add (jLabel3, gridBagConstraints1);

        jLabel4 = new javax.swing.JLabel ();
        jLabel4.setText ("Write #1");
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add (jLabel4, gridBagConstraints1);

        jLabel5 = new javax.swing.JLabel ();
        jLabel5.setText ("Write #2");
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add (jLabel5, gridBagConstraints1);

        crit1 = new javax.swing.JTextField ();
        crit1.setPreferredSize (new java.awt.Dimension(100, 21));
        crit1.addActionListener (new java.awt.event.ActionListener () {
                                     public void actionPerformed (java.awt.event.ActionEvent evt) {
                                         crit1ActionPerformed (evt);
                                     }
                                 }
                                );
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 3;
        add (crit1, gridBagConstraints1);

        crit2 = new javax.swing.JTextField ();
        crit2.setPreferredSize (new java.awt.Dimension(100, 21));
        crit2.addActionListener (new java.awt.event.ActionListener () {
                                     public void actionPerformed (java.awt.event.ActionEvent evt) {
                                         crit2ActionPerformed (evt);
                                     }
                                 }
                                );
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 4;
        add (crit2, gridBagConstraints1);

        ext1 = new javax.swing.JRadioButton ();
        ext1.setText ("Extension");
        ext1.addActionListener (new java.awt.event.ActionListener () {
                                    public void actionPerformed (java.awt.event.ActionEvent evt) {
                                        ext1ActionPerformed (evt);
                                    }
                                }
                               );
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 3;
        gridBagConstraints1.gridy = 3;
        add (ext1, gridBagConstraints1);

        mime1 = new javax.swing.JRadioButton ();
        mime1.setText ("MIME Type");
        mime1.addActionListener (new java.awt.event.ActionListener () {
                                     public void actionPerformed (java.awt.event.ActionEvent evt) {
                                         mime1ActionPerformed (evt);
                                     }
                                 }
                                );
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 4;
        gridBagConstraints1.gridy = 3;
        add (mime1, gridBagConstraints1);

        ext2 = new javax.swing.JRadioButton ();
        ext2.setText ("Extension");
        ext2.addActionListener (new java.awt.event.ActionListener () {
                                    public void actionPerformed (java.awt.event.ActionEvent evt) {
                                        ext2ActionPerformed (evt);
                                    }
                                }
                               );
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 3;
        gridBagConstraints1.gridy = 4;
        add (ext2, gridBagConstraints1);

        mime2 = new javax.swing.JRadioButton ();
        mime2.setText ("MIME Type");
        mime2.addActionListener (new java.awt.event.ActionListener () {
                                     public void actionPerformed (java.awt.event.ActionEvent evt) {
                                         mime2ActionPerformed (evt);
                                     }
                                 }
                                );
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 4;
        gridBagConstraints1.gridy = 4;
        add (mime2, gridBagConstraints1);

        valid = new javax.swing.JLabel ();
        valid.setText ("valid??");
        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = 3;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        add (valid, gridBagConstraints1);

    }//GEN-END:initComponents

    private void mime2ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mime2ActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_mime2ActionPerformed

    private void ext2ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ext2ActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_ext2ActionPerformed

    private void mime1ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mime1ActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_mime1ActionPerformed

    private void ext1ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ext1ActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_ext1ActionPerformed

    private void crit2ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crit2ActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_crit2ActionPerformed

    private void crit1ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crit1ActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_crit1ActionPerformed

    private void wr2ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wr2ActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_wr2ActionPerformed

    private void wr1ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wr1ActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_wr1ActionPerformed

    private void defwrActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defwrActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_defwrActionPerformed

    private void read2ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_read2ActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_read2ActionPerformed

    private void read1ActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_read1ActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_read1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox read1;
    private javax.swing.JComboBox read2;
    private javax.swing.JComboBox defwr;
    private javax.swing.JComboBox wr1;
    private javax.swing.JComboBox wr2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField crit1;
    private javax.swing.JTextField crit2;
    private javax.swing.JRadioButton ext1;
    private javax.swing.JRadioButton mime1;
    private javax.swing.JRadioButton ext2;
    private javax.swing.JRadioButton mime2;
    private javax.swing.JLabel valid;
    // End of variables declaration//GEN-END:variables

}
