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

package org.openide.src.nodes;

import java.awt.BorderLayout;
import java.beans.Customizer;
import java.beans.PropertyEditor;
import java.util.ResourceBundle;

import javax.swing.*;
import javax.swing.border.*;

import org.openide.src.*;
import org.openide.explorer.propertysheet.*;
import org.openide.explorer.propertysheet.editors.ModifierEditor;
import org.openide.*;
import org.openide.util.Utilities;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;

/** Customizer for MethodElement and ConstructorElement
 *
 * @author Petr Hamernik
 */
public class MethodCustomizer extends JPanel {
    /** Source of the localized human presentable strings. */
    static ResourceBundle bundle = NbBundle.getBundle(MethodCustomizer.class);

    /** Predefined types in the type combo */
    private static final String[] COMMON_TYPES = {
        "void", // NOI18N
        "String", // NOI18N
        "boolean", // NOI18N
        "char", // NOI18N
        "int", // NOI18N
        "long", // NOI18N
        "float", // NOI18N
        "double" // NOI18N
    };

    /** Edited constructor */
    ConstructorElement element;

    /** In case that method is edited - this field holds
    * the reference to it. Otherwise (Constructor) this field
    * is <CODE>null</CODE>.
    */
    MethodElement method;

    /** Create new MethodCustomizer component
    * @param element The method or constructor to be customized
    */
    public MethodCustomizer(ConstructorElement element) {
        this.element = element;
        this.method = (element instanceof MethodElement) ?
                      (MethodElement) element : null;

        initComponents ();

        // borders
        methodPanel.setBorder (new CompoundBorder(
                                   new TitledBorder(bundle.getString("CTL_MethodFrame")),
                                   new EmptyBorder(new java.awt.Insets(5, 5, 5, 5)))
                              );
        modifierPanel.setBorder (new TitledBorder(bundle.getString("CTL_Modifiers")));
        paramsPanel.setBorder (new TitledBorder(bundle.getString("CTL_Parameters")));
        exceptionsPanel.setBorder (new TitledBorder(bundle.getString("CTL_Exceptions")));

        // modifiers
        PropertyPanel modifEditor = new PropertyPanel (
                                        element,
                                        ElementProperties.PROP_MODIFIERS,
                                        PropertyPanel.PREF_CUSTOM_EDITOR
                                    );
        modifierPanel.add(modifEditor, BorderLayout.CENTER);
        PropertyEditor propEdit = modifEditor.getPropertyEditor();
        if (propEdit instanceof ModifierEditor) {
            ((ModifierEditor)propEdit).setMask(element.getModifiersMask());
        }

        // name
        nameTextField.setText(element.getName().toString());
        if (method == null) {
            nameTextField.setEnabled(false);
            returnCombo.setEnabled(false);
        }
        else {
            returnCombo.setSelectedItem(method.getReturn().toString());
        }

        // parameters
        paramsPanel.add (
            new PropertyPanel (
                element,
                ElementProperties.PROP_PARAMETERS,
                PropertyPanel.PREF_CUSTOM_EDITOR
            ),
            BorderLayout.CENTER
        );

        // exceptions
        exceptionsPanel.add (
            new PropertyPanel (
                element,
                ElementProperties.PROP_EXCEPTIONS,
                PropertyPanel.PREF_CUSTOM_EDITOR
            ),
            BorderLayout.CENTER
        );

        HelpCtx.setHelpIDString (this, MethodCustomizer.class.getName ());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {//GEN-BEGIN:initComponents
        methodPanel = new javax.swing.JPanel ();
        jLabel1 = new javax.swing.JLabel ();
        nameTextField = new javax.swing.JTextField ();
        jLabel2 = new javax.swing.JLabel ();
        returnCombo = new javax.swing.JComboBox(COMMON_TYPES);
        jPanel1 = new javax.swing.JPanel ();
        modifierPanel = new javax.swing.JPanel ();
        paramsPanel = new javax.swing.JPanel ();
        exceptionsPanel = new javax.swing.JPanel ();
        setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints1;
        setBorder (new javax.swing.border.EmptyBorder(new java.awt.Insets(6, 6, 6, 6)));

        methodPanel.setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints2;

        jLabel1.setText (bundle.getString("CTL_Name"));

        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.insets = new java.awt.Insets (10, 0, 8, 8);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.EAST;
        methodPanel.add (jLabel1, gridBagConstraints2);

        nameTextField.addFocusListener (new java.awt.event.FocusAdapter () {
                                            public void focusLost (java.awt.event.FocusEvent evt) {
                                                nameTextFieldFocusLost (evt);
                                            }
                                        }
                                       );

        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridwidth = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.insets = new java.awt.Insets (10, 0, 8, 0);
        gridBagConstraints2.weightx = 1.0;
        methodPanel.add (nameTextField, gridBagConstraints2);

        jLabel2.setText (bundle.getString("CTL_ReturnType"));

        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.insets = new java.awt.Insets (0, 0, 0, 8);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.EAST;
        methodPanel.add (jLabel2, gridBagConstraints2);

        returnCombo.setEditable (true);
        returnCombo.addActionListener (new java.awt.event.ActionListener () {
                                           public void actionPerformed (java.awt.event.ActionEvent evt) {
                                               returnComboActionPerformed (evt);
                                           }
                                       }
                                      );

        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridwidth = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.weightx = 1.0;
        methodPanel.add (returnCombo, gridBagConstraints2);


        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints2.weighty = 1.0;
        methodPanel.add (jPanel1, gridBagConstraints2);


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0;
        add (methodPanel, gridBagConstraints1);



        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets (0, 5, 0, 0);
        add (modifierPanel, gridBagConstraints1);

        paramsPanel.setLayout (new java.awt.BorderLayout ());


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets (5, 0, 0, 0);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add (paramsPanel, gridBagConstraints1);

        exceptionsPanel.setLayout (new java.awt.BorderLayout ());


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets (5, 0, 0, 0);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add (exceptionsPanel, gridBagConstraints1);

    }//GEN-END:initComponents

    private void returnComboActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnComboActionPerformed
                           String text = returnCombo.getSelectedItem().toString();
                           Type oldValue = method.getReturn();
                           boolean ok = false;
                           try {
                               Type newValue = Type.parse(text);
                               if (!oldValue.equals(newValue)) {
                                   try {
                                       method.setReturn(newValue);
                                       ok = true;
                                   }
                                   catch (SourceException e) {
                                       TopManager.getDefault().notifyException(e);
                                   }
                               }
                           }
                           catch (IllegalArgumentException e) {
                               TopManager.getDefault().notify(
                                   new NotifyDescriptor.Message(
                                       bundle.getString("MSG_Not_Valid_Type"),
                                       NotifyDescriptor.ERROR_MESSAGE
                                   )
                               );
                           }
                           if (!ok)
                               returnCombo.setSelectedItem(oldValue.toString());
                       }//GEN-LAST:event_returnComboActionPerformed

                       private void nameTextFieldFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_nameTextFieldFocusLost
                           if (evt.isTemporary() || (method == null))
                               return;

                           String newName = nameTextField.getText();
                           String oldName = method.getName().toString();
                           boolean ok = false;
                           if (Utilities.isJavaIdentifier(newName)) {
                               if (!oldName.equals(newName)) {
                                   Identifier id = Identifier.create(newName);
                                   try {
                                       method.setName(id);
                                       ok = true;
                                   }
                                   catch (SourceException e) {
                                       TopManager.getDefault().notifyException(e);
                                   }
                               }
                           }
                           else {
                               TopManager.getDefault().notify(
                                   new NotifyDescriptor.Message(
                                       bundle.getString("MSG_Not_Valid_Identifier"),
                                       NotifyDescriptor.ERROR_MESSAGE
                                   )
                               );
                           }
                           if (!ok)
                               nameTextField.setText(oldName);
                       }//GEN-LAST:event_nameTextFieldFocusLost


                       // Variables declaration - do not modify//GEN-BEGIN:variables
                       private javax.swing.JPanel methodPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox returnCombo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel modifierPanel;
    private javax.swing.JPanel paramsPanel;
    private javax.swing.JPanel exceptionsPanel;
    // End of variables declaration//GEN-END:variables

}

/*
* Log
*  5    Gandalf   1.4         1/12/00  Petr Hamernik   i18n using perl script 
*       (//NOI18N comments added)
*  4    Gandalf   1.3         1/11/00  Jesse Glick     Context help.
*  3    Gandalf   1.2         1/6/00   Petr Hamernik   fixed 5040
*  2    Gandalf   1.1         12/9/99  Jan Jancura     PropertyPanel changes 
*       reflected.
*  1    Gandalf   1.0         11/29/99 Petr Hamernik   
* $
*/
