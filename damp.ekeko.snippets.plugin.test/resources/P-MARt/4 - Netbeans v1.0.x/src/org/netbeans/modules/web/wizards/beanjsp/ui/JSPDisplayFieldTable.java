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

package  org.netbeans.modules.web.wizards.beanjsp.ui;

import org.netbeans.modules.web.wizards.beanjsp.model.*;
import org.netbeans.modules.web.wizards.wizardfw.*;
import org.netbeans.modules.web.wizards.beanjsp.ide.netbeans.*;

import org.netbeans.modules.web.util.*;
import org.netbeans.modules.web.wizards.beanjsp.util.*;

import org.openide.util.*;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;


public class JSPDisplayFieldTable extends JScrollPane {



    // ---------------------------------------------------------------------------------------
    // WizardPanel initialization

    /** Creates new JspColumnsPanel */
    public JSPDisplayFieldTable() {
        super();
        initComponents ();
    }

    private void initComponents () {

        beanFieldTable = new JTable();
        this.setViewportView(beanFieldTable);

        setTableModel(new JSPDisplayFieldTableModel(new JSPVector()));

    }

    private javax.swing.JTable beanFieldTable;
    private JSPDisplayFieldTableModel beanFieldTableModel;

    public void setTableModel(JSPDisplayFieldTableModel tableModel) {
        beanFieldTableModel = tableModel;
        beanFieldTable.setModel(beanFieldTableModel);
        beanFieldTable.setPreferredScrollableViewportSize(new Dimension(200,100));

        TableColumn htmlElementColumn = beanFieldTable.getColumnModel().getColumn(JSPDisplayFieldTableModel.FIELD_ELEMENT_COL);
        JComboBox htmlElementCombo = new JComboBox();
        htmlElementColumn.setCellEditor(new HTMLElementCellEditor(htmlElementCombo));

    }

    public JTable getTable() { return beanFieldTable; }

    public class HTMLElementCellEditor extends DefaultCellEditor {

        public HTMLElementCellEditor(JComboBox htmlElementCombo) {
            super(htmlElementCombo);
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {

            JSPDisplayFieldTableModel jspDisplayFieldTableModel = (JSPDisplayFieldTableModel) table.getModel();
            DefaultComboBoxModel model = new DefaultComboBoxModel(jspDisplayFieldTableModel.getHTMLElementTypes(row,column));
            ((JComboBox)editorComponent).setModel(model);

            return super.getTableCellEditorComponent(table, value, isSelected, row, column);
        }
    }

    // End of variables declaration//GEN-END:variables

    public static void main(String[] args) {
        if(Debug.TEST) {
            JFrame testFrame = new JFrame("This is Test Frame");			 // NOI18N
            BeanManager beanManager = new BeanManager();
            Collection jspBeans = beanManager.getValidJSPBeans("d:\\netbeans\\ide-source\\src_modules","jspbeans");		 // NOI18N

            Iterator jspBeansIte = jspBeans.iterator();
            JSPVector jspBeanFields = new JSPVector();
            for(;jspBeansIte.hasNext();) {
                Collection fields = beanManager.getAllValidJSPBeanFields((JSPBean)jspBeansIte.next());
                jspBeanFields.addAll(fields);
            }

            JSPDisplayFieldTable jspDisplayFieldTable = new JSPDisplayFieldTable();
            jspDisplayFieldTable.setTableModel(new JSPDisplayFieldTableModel(jspBeanFields));

            testFrame.getContentPane().add(jspDisplayFieldTable,SwingConstants.CENTER);
            testFrame.setSize(500,300);
            testFrame.pack();
            testFrame.show();
        }

    }

}



