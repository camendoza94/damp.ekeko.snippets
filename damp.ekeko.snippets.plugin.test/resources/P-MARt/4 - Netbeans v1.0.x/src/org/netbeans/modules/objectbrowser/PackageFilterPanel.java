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

package org.netbeans.modules.objectbrowser;

import org.openide.*;
import org.openide.util.HelpCtx;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.util.NbBundle;

import java.util.*;

/**
* Property Editor for PackagesFilter.
*
* @author Jan Jancura
*/
class PackageFilterPanel extends javax.swing.JPanel
    implements EnhancedCustomPropertyEditor {

    static final long serialVersionUID =1505758508947990920L;


    // variables ..........................................................................

    private PackagesFilter pf = new PackagesFilter ();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton bNew;
    private javax.swing.JButton bRename;
    private javax.swing.JButton bDelete;
    private javax.swing.JList lFilter;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton bAdd;
    private javax.swing.JButton bChange;
    private javax.swing.JButton bRemove;
    private javax.swing.JList lDetails;
    private javax.swing.JRadioButton rbPackage;
    private javax.swing.JTextField tfPackage;
    private javax.swing.JRadioButton rbRegular;
    private javax.swing.JTextField tfRegular;
    private javax.swing.JRadioButton rbPackageList;
    private javax.swing.JTextField tfPackageList;
    private javax.swing.JButton bPackage;
    // End of variables declaration//GEN-END:variables


    // init ..................................................................................

    /** Initializes the Form */
    PackageFilterPanel() {
        initComponents ();
        javax.swing.ButtonGroup bg = new javax.swing.ButtonGroup ();
        bg.add (rbPackage);
        bg.add (rbRegular);
        //    bg.add (rbPackageList);
        HelpCtx.setHelpIDString (this, PackageFilterPanel.class.getName ());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {//GEN-BEGIN:initComponents
        // This code was developed using a non-commercially licensed version of NetBeans Developer 2.x.
        // For details, see http://www.netbeans.com/non_commercial.html

        setPreferredSize (new java.awt.Dimension(450, 350));
        setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints1;

        jPanel1 = new javax.swing.JPanel ();
        jPanel1.setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints2;

        // North panel - Filters .............................
        jScrollPane1 = new javax.swing.JScrollPane ();

        lFilter = new javax.swing.JList ();
        lFilter.setSelectionMode (javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lFilter.addListSelectionListener (new javax.swing.event.ListSelectionListener () {
                                              public void valueChanged (javax.swing.event.ListSelectionEvent evt) {
                                                  lFilterValueChanged (evt);
                                              }
                                          }
                                         );
        jScrollPane1.add (lFilter);

        jScrollPane1.setViewportView (lFilter);
        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridheight = 4;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.insets = new java.awt.Insets (4, 4, 4, 4);
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        jPanel1.add (jScrollPane1, gridBagConstraints2);

        bNew = new javax.swing.JButton ();
        bNew.setText (NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_New"));
        bNew.addActionListener (new java.awt.event.ActionListener () {
                                    public void actionPerformed (java.awt.event.ActionEvent evt) {
                                        bNewActionPerformed (evt);
                                    }
                                }
                               );
        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridwidth = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets (4, 4, 4, 4);
        jPanel1.add (bNew, gridBagConstraints2);

        bRename = new javax.swing.JButton ();
        bRename.setText (NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_Rename"));
        bRename.addActionListener (new java.awt.event.ActionListener () {
                                       public void actionPerformed (java.awt.event.ActionEvent evt) {
                                           bRenameActionPerformed (evt);
                                       }
                                   }
                                  );
        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridwidth = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets (4, 4, 4, 4);
        jPanel1.add (bRename, gridBagConstraints2);

        bDelete = new javax.swing.JButton ();
        bDelete.setText (NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_Delete"));
        bDelete.addActionListener (new java.awt.event.ActionListener () {
                                       public void actionPerformed (java.awt.event.ActionEvent evt) {
                                           bDeleteActionPerformed (evt);
                                       }
                                   }
                                  );
        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridwidth = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets (4, 4, 4, 4);
        jPanel1.add (bDelete, gridBagConstraints2);
        // North panel - Filters end .............................

        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add (jPanel1, gridBagConstraints1);

        jPanel2 = new javax.swing.JPanel ();
        jPanel2.setBorder (new javax.swing.border.TitledBorder (
                               new javax.swing.border.EtchedBorder (),
                               NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_Details")
                           ));
        jPanel2.setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints3;

        // South panel - Filter settings .............................
        jPanel3 = new javax.swing.JPanel ();
        jPanel3.setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints4;

        jScrollPane2 = new javax.swing.JScrollPane ();

        lDetails = new javax.swing.JList ();
        lDetails.setSelectionMode (javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lDetails.addListSelectionListener (new javax.swing.event.ListSelectionListener () {
                                               public void valueChanged (javax.swing.event.ListSelectionEvent evt) {
                                                   lDetailsValueChanged (evt);
                                               }
                                           }
                                          );
        jScrollPane2.add (lDetails);

        jScrollPane2.setViewportView (lDetails);
        gridBagConstraints4 = new java.awt.GridBagConstraints ();
        gridBagConstraints4.gridheight = 4;
        gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints4.insets = new java.awt.Insets (4, 4, 4, 4);
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.weighty = 1.0;
        jPanel3.add (jScrollPane2, gridBagConstraints4);

        bAdd = new javax.swing.JButton ();
        bAdd.setText (NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_Add"));
        bAdd.addActionListener (new java.awt.event.ActionListener () {
                                    public void actionPerformed (java.awt.event.ActionEvent evt) {
                                        bAddActionPerformed (evt);
                                    }
                                }
                               );
        gridBagConstraints4 = new java.awt.GridBagConstraints ();
        gridBagConstraints4.gridwidth = 0;
        gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints4.insets = new java.awt.Insets (4, 4, 4, 4);
        jPanel3.add (bAdd, gridBagConstraints4);

        bChange = new javax.swing.JButton ();
        bChange.setText (NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_Change"));
        bChange.addActionListener (new java.awt.event.ActionListener () {
                                       public void actionPerformed (java.awt.event.ActionEvent evt) {
                                           bChangeActionPerformed (evt);
                                       }
                                   }
                                  );
        gridBagConstraints4 = new java.awt.GridBagConstraints ();
        gridBagConstraints4.gridwidth = 0;
        gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints4.insets = new java.awt.Insets (4, 4, 4, 4);
        jPanel3.add (bChange, gridBagConstraints4);

        bRemove = new javax.swing.JButton ();
        bRemove.setText (NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_Remove"));
        bRemove.addActionListener (new java.awt.event.ActionListener () {
                                       public void actionPerformed (java.awt.event.ActionEvent evt) {
                                           bRemoveActionPerformed (evt);
                                       }
                                   }
                                  );
        gridBagConstraints4 = new java.awt.GridBagConstraints ();
        gridBagConstraints4.gridwidth = 0;
        gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints4.insets = new java.awt.Insets (4, 4, 4, 4);
        jPanel3.add (bRemove, gridBagConstraints4);

        gridBagConstraints3 = new java.awt.GridBagConstraints ();
        gridBagConstraints3.gridwidth = 0;
        gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.weighty = 1.0;
        jPanel2.add (jPanel3, gridBagConstraints3);

        jPanel4 = new javax.swing.JPanel ();
        jPanel4.setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints5;

        rbPackage = new javax.swing.JRadioButton ();
        rbPackage.setSelected (true);
        rbPackage.setText (NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_Wildcard"));
        rbPackage.addActionListener (new java.awt.event.ActionListener () {
                                         public void actionPerformed (java.awt.event.ActionEvent evt) {
                                             rbPackageActionPerformed (evt);
                                         }
                                     }
                                    );
        gridBagConstraints5 = new java.awt.GridBagConstraints ();
        gridBagConstraints5.insets = new java.awt.Insets (4, 4, 4, 4);
        gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add (rbPackage, gridBagConstraints5);

        tfPackage = new javax.swing.JTextField ();
        tfPackage.setText (NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_Default_filter"));
        gridBagConstraints5 = new java.awt.GridBagConstraints ();
        gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints5.insets = new java.awt.Insets (4, 4, 4, 4);
        gridBagConstraints5.weightx = 1.0;
        jPanel4.add (tfPackage, gridBagConstraints5);

        rbRegular = new javax.swing.JRadioButton ();
        rbRegular.setText (NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_Regular_expression"));
        rbRegular.addActionListener (new java.awt.event.ActionListener () {
                                         public void actionPerformed (java.awt.event.ActionEvent evt) {
                                             rbRegularActionPerformed (evt);
                                         }
                                     }
                                    );
        gridBagConstraints5 = new java.awt.GridBagConstraints ();
        gridBagConstraints5.gridy = 2;
        gridBagConstraints5.insets = new java.awt.Insets (4, 4, 4, 4);
        gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
        jPanel4.add (rbRegular, gridBagConstraints5);

        tfRegular = new javax.swing.JTextField ();
        gridBagConstraints5 = new java.awt.GridBagConstraints ();
        gridBagConstraints5.gridy = 2;
        gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints5.insets = new java.awt.Insets (4, 4, 4, 4);
        gridBagConstraints5.weightx = 1.0;
        jPanel4.add (tfRegular, gridBagConstraints5);

        /*        rbPackageList = new javax.swing.JRadioButton ();
                rbPackageList.setText ("package list: ");
                rbPackageList.addActionListener (new java.awt.event.ActionListener () {
                    public void actionPerformed (java.awt.event.ActionEvent evt) {
                      rbPackageListActionPerformed (evt);
                    }
                  }
                );
                gridBagConstraints5 = new java.awt.GridBagConstraints ();
                gridBagConstraints5.gridy = 3;
                gridBagConstraints5.insets = new java.awt.Insets (4, 4, 4, 4);
                gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
                jPanel4.add (rbPackageList, gridBagConstraints5);

                tfPackageList = new javax.swing.JTextField ();
                gridBagConstraints5 = new java.awt.GridBagConstraints ();
                gridBagConstraints5.gridy = 3;
                gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
                gridBagConstraints5.insets = new java.awt.Insets (4, 4, 4, 4);
                gridBagConstraints5.weightx = 1.0;
                jPanel4.add (tfPackageList, gridBagConstraints5);

                bPackage = new javax.swing.JButton ();
                bPackage.setText ("...");
                bPackage.addActionListener (new java.awt.event.ActionListener () {
                    public void actionPerformed (java.awt.event.ActionEvent evt) {
                      bPackageActionPerformed (evt);
                    }
                  }
                );
                gridBagConstraints5 = new java.awt.GridBagConstraints ();
                gridBagConstraints5.gridy = 3;
                gridBagConstraints5.insets = new java.awt.Insets (4, 4, 4, 4);
                jPanel4.add (bPackage, gridBagConstraints5);
        */
        gridBagConstraints3 = new java.awt.GridBagConstraints ();
        gridBagConstraints3.gridwidth = 0;
        gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints3.weightx = 1.0;
        jPanel2.add (jPanel4, gridBagConstraints3);
        // South panel - Filter settings end .............................

        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add (jPanel2, gridBagConstraints1);

    }//GEN-END:initComponents

    // main methods ..........................................................................

    /**
    * Sets PackagesFilter to customize.
    */
    void setPackagesFilter (PackagesFilter pf) {
        this.pf = pf;
        lFilter.setListData (pf.filterNames);
        lFilter.setSelectedIndex (pf.index);
    }

    /**
    * @return customized PackagesFilter.
    */
    PackagesFilter getPackagesFilter () {
        return pf;
    }

    /**
    * @return customized PackagesFilter.
    */
    public Object getPropertyValue () {
        return getPackagesFilter ();
    }


    // private helper methods ................................................................

    /**
    * Change current subfilter current filter.
    */
    private void bChangeActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bChangeActionPerformed
        int i = lFilter.getSelectedIndex ();
        if (i < 0) return;
        int j = lDetails.getSelectedIndex ();
        if (j < 0) return;

        PackagesFilter.Filter f = null;
        if (rbPackage.isSelected ()) {
            f = new PackagesFilter.PackageFilter ();
            ((PackagesFilter.PackageFilter) f).packageName = tfPackage.getText ();
            tfRegular.setText (""); // NOI18N
            //tfPackageList.setText (""); // NOI18N
        } else
            if (rbRegular.isSelected ()) {
                f = new PackagesFilter.RegularFilter ();
                ((PackagesFilter.RegularFilter) f).expression = tfRegular.getText ();
                tfPackage.setText (""); // NOI18N
                //tfPackageList.setText (""); // NOI18N
            } /*else
        if (rbPackageList.isSelected ()) {
    }*/

        Vector v = (Vector) pf.filterValues.elementAt (i);
        v.setElementAt (f, j);
        lDetails.setListData (v);
        lDetails.setSelectedIndex (j);
    }//GEN-LAST:event_bChangeActionPerformed

    /**
    * Packagelist type of subfilter selected.
    */
    /*  private void rbPackageListActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPackageListActionPerformed
        tfPackage.setEnabled (false);
        tfRegular.setEnabled (false);
        tfPackageList.setEnabled (true);
      }//GEN-LAST:event_rbPackageListActionPerformed
    */

    /**
    * Regular type of subfilter selected.
    */
    private void rbRegularActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbRegularActionPerformed
        tfPackage.setEnabled (false);
        tfRegular.setEnabled (true);
        //tfPackageList.setEnabled (false);
    }//GEN-LAST:event_rbRegularActionPerformed

    /**
    * Wildcard type of subfilter selected.
    */
    private void rbPackageActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPackageActionPerformed
        tfPackage.setEnabled (true);
        tfRegular.setEnabled (false);
        //tfPackageList.setEnabled (false);
    }//GEN-LAST:event_rbPackageActionPerformed

    /*  private void bPackageActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bPackageActionPerformed
        // Add your handling code here:
      }//GEN-LAST:event_bPackageActionPerformed
    */

    /**
    * Remove currently selected subfilter of currently selected filter.
    */
    private void bRemoveActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRemoveActionPerformed
        int i = lFilter.getSelectedIndex ();
        if (i < 0) return;

        Vector v = (Vector) pf.filterValues.elementAt (i);
        int j = lDetails.getSelectedIndex ();
        if (j < 0) return;
        v.removeElementAt (j);
        lDetails.setListData (v);

        int k = v.size ();
        if (k == 0) return;
        lDetails.setSelectedIndex (Math.min (k - 1, j));
    }//GEN-LAST:event_bRemoveActionPerformed


    /**
    * Add new default subfilter for currently selected filter.
    */
    private void bAddActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddActionPerformed
        int i = lFilter.getSelectedIndex ();
        if (i < 0) return;
        Vector v = (Vector) pf.filterValues.elementAt (i);
        v.addElement (new PackagesFilter.PackageFilter ());
        lDetails.setListData (v);
        lDetails.setSelectedIndex (v.size () - 1);
    }//GEN-LAST:event_bAddActionPerformed

    /**
    * Delete currently selected filter.
    */
    private void bDeleteActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeleteActionPerformed
        int i = lFilter.getSelectedIndex ();
        if (i < 0) return;

        pf.filterNames.removeElementAt (i);
        pf.filterValues.removeElementAt (i);
        lFilter.setListData (pf.filterNames);
        int k = pf.filterNames.size ();
        if (k == 0) return;
        lFilter.setSelectedIndex (Math.min (k - 1, i));
    }//GEN-LAST:event_bDeleteActionPerformed

    /**
    * Rename currently selected filter.
    */
    private void bRenameActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRenameActionPerformed
        int i = lFilter.getSelectedIndex ();
        if (i < 0) return;

        NotifyDescriptor.InputLine descr = new NotifyDescriptor.InputLine (
                                               NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_Input_name"),
                                               NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_Change_filter_name")
                                           );
        if (!TopManager.getDefault ().notify (descr).equals (NotifyDescriptor.OK_OPTION)) return;

        pf.filterNames.setElementAt (descr.getInputText (), i);
        lFilter.setListData (pf.filterNames);
        lFilter.setSelectedIndex (i);
    }//GEN-LAST:event_bRenameActionPerformed

    /**
    * Create new filter.
    */
    private void bNewActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bNewActionPerformed
        NotifyDescriptor.InputLine descr = new NotifyDescriptor.InputLine (
                                               NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_Input_name"),
                                               NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_Create_new_filter")
                                           );
        if (!TopManager.getDefault ().notify (descr).equals (NotifyDescriptor.OK_OPTION)) return;
        String newName = descr.getInputText ().trim ();
        if (newName.length () < 1) return;

        // check for duplicity
        int i, k = pf.filterNames.size ();
        for (i = 0; i < k; i++)
            if (newName.equals (pf.filterNames.elementAt (i))) {
                NotifyDescriptor.Message mass = new NotifyDescriptor.Message (
                                                    NbBundle.getBundle (PackageFilterPanel.class).getString ("CTL_Duplicate_filter_name")
                                                );
                TopManager.getDefault ().notify (mass);
                return;
            }

        pf.filterNames.add (newName);
        lFilter.setListData (pf.filterNames);
        Vector v = new Vector ();
        v.add (new PackagesFilter.PackageFilter ());
        pf.filterValues.add (v);
        lFilter.setSelectedIndex (pf.filterNames.size () - 1);
    }//GEN-LAST:event_bNewActionPerformed

    /**
    * Change selection of subfilters.
    */
    private void lDetailsValueChanged (javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lDetailsValueChanged
        PackagesFilter.Filter f = getFilter ();
        if (f != null) update (f);
        update ();
    }//GEN-LAST:event_lDetailsValueChanged

    /**
    * Change selection of filters.
    */
    private void lFilterValueChanged (javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lFilterValueChanged
        int i = lFilter.getSelectedIndex ();
        if (i >= 0) {
            lDetails.setListData ((Vector) pf.filterValues.elementAt (i));
            pf.setSelected (i);
        } else {
            lDetails.setListData (new Vector ());
            pf.setSelected (-1);
        }
        update ();
    }//GEN-LAST:event_lFilterValueChanged


    /**
    * @return currently selected subfilter. 
    */
    private PackagesFilter.Filter getFilter () {
        int i = lDetails.getSelectedIndex ();
        if (i >= 0) {
            int j = lFilter.getSelectedIndex ();
            if (j >= 0) {
                PackagesFilter.Filter f = (PackagesFilter.Filter)
                                          ((Vector) pf.filterValues.elementAt (j)).elementAt (i);
                return f;
            }
        }
        return null;
    }

    /**
    * Sets given subfilter for detiails (South) panel. 
    */
    private void update (PackagesFilter.Filter f) {
        if (f instanceof PackagesFilter.PackageFilter) {
            rbPackage.setSelected (true);
            tfPackage.setText (((PackagesFilter.PackageFilter) f).packageName);
            tfRegular.setText (""); // NOI18N
            //      tfPackageList.setText (""); // NOI18N
        } else
            if (f instanceof PackagesFilter.RegularFilter) {
                rbRegular.setSelected (true);
                tfPackage.setText (""); // NOI18N
                tfRegular.setText (((PackagesFilter.RegularFilter) f).expression);
                //      tfPackageList.setText (""); // NOI18N
            } /*else
        if (f instanceof PackagesFilter.PackageListFilter) {
          rbPackageList.setSelected (true);
          tfPackage.setText ("");
          tfRegular.setText ("");
          tfPackageList.setText ("");
    }*/
    }

    /**
    * Updates selection in details panel and state (disable/enable) 
    * of buttons. Called when selection of filters or subfilters is changed.
    */
    private void update () {
        boolean e = lFilter.getSelectedIndex () >= 0;

        bRename.setEnabled (e);
        bDelete.setEnabled (e);
        bAdd.setEnabled (e);

        e &= lDetails.getSelectedIndex () >= 0;

        bChange.setEnabled (e);
        bRemove.setEnabled (e);
        rbPackage.setEnabled (e);
        rbRegular.setEnabled (e);
        //    rbPackageList.setEnabled (e);
        tfPackage.setEnabled (e & rbPackage.isSelected ());
        tfRegular.setEnabled (e & rbRegular.isSelected ());
        //    tfPackageList.setEnabled (e);
        //    bPackage.setEnabled (e);
        if (!e) {
            tfPackage.setText (""); // NOI18N
            tfRegular.setText (""); // NOI18N
            //      tfPackageList.setText (""); // NOI18N
        }
    }
}

/*
 * Log
 *  12   Gandalf   1.11        1/13/00  Radko Najman    I18N
 *  11   Gandalf   1.10        12/15/99 Jan Jancura     Bug 3039 + Bug 4917
 *  10   Gandalf   1.9         12/15/99 Jan Jancura     Bug 4906
 *  9    Gandalf   1.8         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  8    Gandalf   1.7         8/18/99  Jan Jancura     Localization
 *  7    Gandalf   1.6         8/9/99   Ian Formanek    Generated Serial Version
 *       UID
 *  6    Gandalf   1.5         8/2/99   Jan Jancura     
 *  5    Gandalf   1.4         7/8/99   Jesse Glick     Context help.
 *  4    Gandalf   1.3         6/30/99  Ian Formanek    reflecting change in 
 *       enhanced property editors interfaces
 *  3    Gandalf   1.2         6/10/99  Jan Jancura     OB settings & save of 
 *       filters
 *  2    Gandalf   1.1         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  1    Gandalf   1.0         5/12/99  Jan Jancura     
 * $
 */
