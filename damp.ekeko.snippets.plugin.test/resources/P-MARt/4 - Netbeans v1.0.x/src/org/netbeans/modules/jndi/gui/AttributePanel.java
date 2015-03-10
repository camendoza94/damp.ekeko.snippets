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

package org.netbeans.modules.jndi.gui;

import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;
import javax.naming.directory.DirContext;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.CompositeName;
import javax.naming.NamingException;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import org.openide.TopManager;
import org.openide.DialogDescriptor;
import org.netbeans.modules.jndi.JndiRootNode;
import org.netbeans.modules.jndi.JndiNode;
import org.netbeans.modules.jndi.utils.SimpleListModel;
/**
 * This class represents the Customizer for properties of jndi objects
 * @author  tzezula
 * @version 
 */
public class AttributePanel extends javax.swing.JPanel implements ListSelectionListener, ActionListener {

    private Dialog dlg;
    private DirContext ctx;
    private CompositeName offset;
    private SimpleListModel model;
    private JndiNode owner;

    /** Creates new form AttributePanel */
    public AttributePanel(DirContext ctx, CompositeName offset, JndiNode owner) {
        this.ctx=ctx;
        this.offset = offset;
        this.owner = owner;
        initComponents ();
        addButton.addActionListener(this);
        removeButton.setEnabled(false);
        removeButton.addActionListener(this);
        editButton.setEnabled(false);
        editButton.addActionListener(this);
        attrList.addListSelectionListener(this);
        model = new SimpleListModel();
        attrList.setPrototypeCellValue("012345678901234567890123456789");
        attrList.setModel(model);
        initData();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel ();
        addButton = new javax.swing.JButton ();
        removeButton = new javax.swing.JButton ();
        editButton = new javax.swing.JButton ();
        jScrollPane1 = new javax.swing.JScrollPane ();
        attrList = new javax.swing.JList ();
        jLabel1 = new javax.swing.JLabel ();
        setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints1;

        jPanel1.setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints2;

        addButton.setText (JndiRootNode.getLocalizedString("TXT_AddAttribute"));

        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridwidth = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets (8, 8, 4, 8);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add (addButton, gridBagConstraints2);

        removeButton.setText (JndiRootNode.getLocalizedString("TXT_RemoveAttribute"));

        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 2;
        gridBagConstraints2.gridwidth = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets (4, 8, 4, 8);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add (removeButton, gridBagConstraints2);

        editButton.setText (JndiRootNode.getLocalizedString("TXT_ModifyAttribute"));

        gridBagConstraints2 = new java.awt.GridBagConstraints ();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.gridwidth = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.insets = new java.awt.Insets (4, 8, 8, 8);
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add (editButton, gridBagConstraints2);


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add (jPanel1, gridBagConstraints1);


        attrList.setValueIsAdjusting (true);

        jScrollPane1.setViewportView (attrList);


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets (4, 8, 8, 0);
        gridBagConstraints1.weightx = 0.8;
        gridBagConstraints1.weighty = 0.8;
        add (jScrollPane1, gridBagConstraints1);

        jLabel1.setText (JndiRootNode.getLocalizedString("TXT_AttributeList"));


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.insets = new java.awt.Insets (8, 8, 4, 8);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add (jLabel1, gridBagConstraints1);

    }//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton addButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton editButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList attrList;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

    /** Sets the data
     */
    private void initData(){
        try{
            Attributes attrs = this.ctx.getAttributes(this.offset);
            java.util.Enumeration enum = attrs.getAll();
            while (enum.hasMoreElements()){
                Attribute attr = (Attribute) enum.nextElement();
                String id = attr.getID();
                String value = attr.get().toString();
                model.addElement(id+"="+value);
            }
        }catch(NamingException ne){}
    }

    /** Context sensitive button handling
     *  @param ListSelectionEvent event
     */
    public void valueChanged(final javax.swing.event.ListSelectionEvent event) {
        if (this.attrList.getSelectedIndex()!=-1){
            this.removeButton.setEnabled(true);
            this.editButton.setEnabled(true);
        }
        else{
            this.removeButton.setEnabled(false);
            this.editButton.setEnabled(false);
        }
    }

    /** Action performed
     *  @param ActionEvent event
     */
    public void actionPerformed(final ActionEvent event){
        if (event.getSource()==this.addButton){
            final CreateAttributePanel p = new CreateAttributePanel();
            DialogDescriptor dd = new DialogDescriptor(p,JndiRootNode.getLocalizedString("TITLE_CreateAttribute"),
                                  true,
                                  DialogDescriptor.OK_CANCEL_OPTION,
                                  DialogDescriptor.OK_OPTION,
                                  new ActionListener(){
                                      public void actionPerformed(ActionEvent event){
                                          if (event.getSource() == DialogDescriptor.OK_OPTION){
                                              try{
                                                  BasicAttributes attrs = new BasicAttributes();
                                                  BasicAttribute attr = new BasicAttribute(p.getName(),p.getValue());
                                                  attrs.put(attr);
                                                  AttributePanel.this.ctx.modifyAttributes(AttributePanel.this.offset,DirContext.ADD_ATTRIBUTE,attrs);
                                                  AttributePanel.this.model.addElement(p.getName()+"="+p.getValue());
                                                  AttributePanel.this.owner.updateData();
                                              }catch(NamingException ne){
                                                  JndiRootNode.notifyForeignException(ne);
                                              }
                                          }
                                          AttributePanel.this.dlg.setVisible(false);
                                      }
                                  });
            dlg = TopManager.getDefault().createDialog(dd);
            dlg.setVisible(true);
        }
        else if (event.getSource()==this.removeButton){
            String item = (String) this.attrList.getSelectedValue();
            if (item != null){
                StringTokenizer tk = new StringTokenizer(item,"=");
                String key = tk.nextToken();
                try{
                    BasicAttributes attrs = new BasicAttributes();
                    BasicAttribute attr = new BasicAttribute(key);
                    attrs.put(attr);
                    this.ctx.modifyAttributes(this.offset,DirContext.REMOVE_ATTRIBUTE,attrs);
                    AttributePanel.this.model.removeElementAt(AttributePanel.this.attrList.getSelectedIndex());
                    AttributePanel.this.owner.updateData();
                }catch(NamingException ne){
                    JndiRootNode.notifyForeignException(ne);
                }
            }
        }
        else if (event.getSource()==this.editButton){
            final CreateAttributePanel p = new CreateAttributePanel();
            String item = (String)AttributePanel.this.attrList.getSelectedValue();
            if (item != null){
                StringTokenizer tk = new StringTokenizer(item,"=");
                final String name = tk.nextToken();
                final String value = tk.nextToken();
                p.setName(name);
                p.setValue(value);
                DialogDescriptor dd = new DialogDescriptor(p,JndiRootNode.getLocalizedString("TITLE_ModifyAttribute"),
                                      true,
                                      DialogDescriptor.OK_CANCEL_OPTION,
                                      DialogDescriptor.OK_OPTION,
                                      new ActionListener(){
                                          public void actionPerformed(ActionEvent event){
                                              if (event.getSource() == DialogDescriptor.OK_OPTION){
                                                  try{
                                                      BasicAttributes attrs;
                                                      BasicAttribute attr;
                                                      if (!p.getName().equals(name)){
                                                          attrs = new BasicAttributes();
                                                          attr = new BasicAttribute(name,value);
                                                          attrs.put(attr);
                                                          AttributePanel.this.ctx.modifyAttributes(AttributePanel.this.offset,DirContext.REMOVE_ATTRIBUTE,attrs);
                                                      }
                                                      attrs = new BasicAttributes();
                                                      attr = new BasicAttribute(p.getName(),p.getValue());
                                                      attrs.put(attr);
                                                      AttributePanel.this.ctx.modifyAttributes(AttributePanel.this.offset,DirContext.REPLACE_ATTRIBUTE,attrs);
                                                      AttributePanel.this.model.removeElementAt(AttributePanel.this.attrList.getSelectedIndex());
                                                      AttributePanel.this.model.addElement(p.getName()+"="+p.getValue());
                                                      AttributePanel.this.owner.updateData();
                                                  }catch(NamingException ne){
                                                      JndiRootNode.notifyForeignException(ne);
                                                  }
                                              }
                                              AttributePanel.this.dlg.setVisible(false);
                                          }
                                      });
                dlg = TopManager.getDefault().createDialog(dd);
                dlg.setVisible(true);
            }
        }
    }
}