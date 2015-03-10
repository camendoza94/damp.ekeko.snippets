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

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import org.openide.*;
import org.openide.src.*;

/**
 *
 * @author   spsenicka
 */
public class RMIMethodsPanel extends AbstractWizardPanel {

    // ---------------------------------------------------------------------------------------
    // WizardPanel initialization

    static final long serialVersionUID =-7248488315456764258L;
    /** Creates new BeanBusinessPanel */
    public RMIMethodsPanel() {
        initComponents ();
        list.setCellRenderer(new ElementListCellRenderer(RMIWizardData.METHOD_HEADER_FORMAT));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {//GEN-BEGIN:initComponents
        setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints1;
        setBorder (new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        setPreferredSize (new java.awt.Dimension(480, 320));

        titleLabel = new javax.swing.JLabel ();
        titleLabel.setText ("RMI methods");
        titleLabel.setFont (new java.awt.Font ("Dialog", 0, 18));


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets (2, 2, 10, 2);
        gridBagConstraints1.weightx = 1.0;
        add (titleLabel, gridBagConstraints1);

        contentPanel = new javax.swing.JPanel ();
        contentPanel.setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints2;

        listScroll = new javax.swing.JScrollPane ();

        list = new javax.swing.JList ();

        listScroll.setViewportView (list);

        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        contentPanel.add (listScroll, gridBagConstraints2);

        buttonsPanel = new javax.swing.JPanel ();
        buttonsPanel.setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints3;
        buttonsPanel.setBorder (new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 8, 0, 0)));

        addButton = new javax.swing.JButton ();
        addButton.setText ("Add...");
        addButton.addActionListener (new java.awt.event.ActionListener () {
                                         public void actionPerformed (java.awt.event.ActionEvent evt) {
                                             addButtonPressed (evt);
                                         }
                                     }
                                    );

        gridBagConstraints3 = new java.awt.GridBagConstraints ();
        gridBagConstraints3.gridwidth = 0;
        gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.insets = new java.awt.Insets (0, 0, 5, 0);
        gridBagConstraints3.weightx = 1.0;
        buttonsPanel.add (addButton, gridBagConstraints3);

        removeButton = new javax.swing.JButton ();
        removeButton.setText ("Remove");
        removeButton.addActionListener (new java.awt.event.ActionListener () {
                                            public void actionPerformed (java.awt.event.ActionEvent evt) {
                                                removeButtonPressed (evt);
                                            }
                                        }
                                       );

        gridBagConstraints3 = new java.awt.GridBagConstraints ();
        gridBagConstraints3.gridwidth = 0;
        gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.insets = new java.awt.Insets (0, 0, 5, 0);
        gridBagConstraints3.weightx = 1.0;
        buttonsPanel.add (removeButton, gridBagConstraints3);

        paddingPanel = new javax.swing.JPanel ();
        paddingPanel.setLayout (new java.awt.FlowLayout ());

        gridBagConstraints3 = new java.awt.GridBagConstraints ();
        gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.weighty = 1.0;
        buttonsPanel.add (paddingPanel, gridBagConstraints3);

        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.weighty = 1.0;
        contentPanel.add (buttonsPanel, gridBagConstraints2);


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add (contentPanel, gridBagConstraints1);

    }//GEN-END:initComponents

    private void removeButtonPressed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonPressed
        int[] indexes = list.getSelectedIndices();
        for (int i = indexes.length - 1; i >= 0 ; i--) methods.removeElementAt (indexes[i]);
        list.setListData (methods);
        list.revalidate ();
        fireChange();
    }//GEN-LAST:event_removeButtonPressed

    private void addButtonPressed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonPressed
        final Dialog[] dd = new Dialog [1];
        final MethodPanel methodPanel = new MethodPanel (new MethodElement());

        final DialogDescriptor methodDesc = new DialogDescriptor (
                                                methodPanel,                       // inside panel
                                                "Add Method",               // title
                                                true,                              // modal
                                                NotifyDescriptor.OK_CANCEL_OPTION, // option type
                                                NotifyDescriptor.OK_OPTION,        // default value
                                                new ActionListener () {
                                                    public void actionPerformed (ActionEvent evt) {
                                                        if (evt.getSource () == NotifyDescriptor.OK_OPTION) {
                                                            try {
                                                                methods.addElement (methodPanel.getMethodElement());
                                                                list.setListData (methods);
                                                                list.revalidate ();
                                                                fireChange();
                                                                dd[0].setVisible (false);
                                                                dd[0].dispose ();
                                                            } catch (Exception ex) {
                                                            }
                                                        }
                                                        if (evt.getSource () == NotifyDescriptor.CANCEL_OPTION) {
                                                            dd[0].setVisible (false);
                                                            dd[0].dispose ();
                                                        }
                                                    }
                                                }
                                            );

        dd[0] = TopManager.getDefault ().createDialog (methodDesc);
        dd[0].show ();
    }//GEN-LAST:event_addButtonPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JScrollPane listScroll;
    private javax.swing.JList list;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton addButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JPanel paddingPanel;
    // End of variables declaration//GEN-END:variables

    Vector methods = new Vector (10);

    // ---------------------------------------------------------------------------------------
    // WizardDescriptor.Panel implementation

    public void storeRMISettings(RMIWizardData data) {
        /*
          CreateBeanWizardData.EJBMethod[] mets = (CreateBeanWizardData.EJBMethod[])methods.toArray(new CreateBeanWizardData.EJBMethod[methods.size()]);
          wizard.getData().setBusinessMethods(mets);
          */
        data.methods = (MethodElement[]) methods.toArray(new  MethodElement[] {});
    }

    public void readRMISettings(RMIWizardData data) {
        methods = new Vector(10);
        for(int i = 0; i < data.methods.length; i++) {
            methods.add(data.methods[i]);
        }
        list.setListData(methods);
        list.revalidate();
    }

}
/*
 * <<Log>>
 *  5    Gandalf   1.4         11/27/99 Patrik Knakal   
 *  4    Gandalf   1.3         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  3    Gandalf   1.2         7/28/99  Martin Ryzl     
 *  2    Gandalf   1.1         7/22/99  Martin Ryzl     first working version
 *  1    Gandalf   1.0         7/20/99  Martin Ryzl     
 * $
 */
