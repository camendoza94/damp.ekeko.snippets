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

package org.netbeans.modules.search.types;

import java.awt.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;

import org.openide.*;
import org.openide.loaders.*;

import org.openidex.search.*;

import org.netbeans.modules.search.res.*;


/**
 * Customizer of TextType beans.
 *
 * @author  Petr Kuzel
 * @version 1.0
 */
public class ObjectTypeCustomizer extends javax.swing.JPanel implements Customizer {

    private ObjectTypeType peer;
    private boolean setting = false;

    /** Creates new form FullTextCustomizer */
    public ObjectTypeCustomizer() {
        initComponents ();
        setBorder (new javax.swing.border.TitledBorder(Res.text("LABEL_OBJECT_TYPE")));

    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {
        jScrollPane1 = new javax.swing.JScrollPane ();
        typeList = new javax.swing.JList ();
        setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints1;


        typeList.addListSelectionListener (new javax.swing.event.ListSelectionListener () {
                                               public void valueChanged (javax.swing.event.ListSelectionEvent evt) {
                                                   typeListValueChanged (evt);
                                               }
                                           }
                                          );

        jScrollPane1.setViewportView (typeList);


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets (4, 4, 4, 4);
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add (jScrollPane1, gridBagConstraints1);

    }

    private void typeListValueChanged (javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_typeListValueChanged

        if (setting) return; //ignore calls caused by setObject() implementation

        DataLoader lds[] = TopManager.getDefault().getLoaderPool().toArray();

        Vector toret = new Vector();
        Object[] sel = typeList.getSelectedValues();

        for (int i=0; i<lds.length; i++) {
            String id = lds[i].getDisplayName();
            if (id == null) continue; //may be null :-(
            for (int j=0; j<sel.length; j++) {
                if (sel[j] == null) continue; //may be null :-(
                if (id.equals((String)sel[j])) {
                    toret.add(lds[i]);
                    break;
                }
            }
        }

        Class[] ret =  new Class[toret.size()];

        Iterator it = toret.iterator();
        int k = 0;
        while (it.hasNext()) {
            ret[k++] = it.next().getClass();
        }

        peer.setMask(ret);
    }//GEN-LAST:event_typeListValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList typeList;
    // End of variables declaration//GEN-END:variables

    /** Initialize customizer with proper values.
    */
    public void setObject(final Object obj) {

        setting = true;

        try {
            peer = (ObjectTypeType) obj;

            DataLoader lds[] = TopManager.getDefault().getLoaderPool().toArray();

            DefaultListModel lm = new DefaultListModel();

            int index = -1;
            Vector indices = new Vector();
            for (int i=0; i<lds.length; i++) {
                String id = lds[i].getDisplayName();
                if (id == null) continue;
                lm.addElement(id);
                index++;

                // create indices
                if (peer.mask == null) continue;
                for (int j=0; j<peer.mask.length; j++) {
                    if (id.equals(DataLoader.getLoader(peer.mask[j]).getDisplayName())) {
                        indices.add(new Integer(index));
                    }
                }
            }

            // select saved values
            typeList.setModel(lm);

            int[] ind = new int[indices.size()];
            for (int i=0; i<ind.length; i++) {
                ind[i] = ((Integer)indices.get(i)).intValue();
            }

            typeList.setSelectedIndices(ind);

        } finally {
            setting = false;
        }

    }

    public void addPropertyChangeListener(final java.beans.PropertyChangeListener p1) {
    }

    public void removePropertyChangeListener(final java.beans.PropertyChangeListener p1) {
    }

    public static void main(String args[]) {
        JFrame fr = new JFrame();
        ObjectTypeCustomizer me = new ObjectTypeCustomizer();
        ObjectTypeType ty = new ObjectTypeType();
        ty.mask = new Class[] {TopManager.getDefault().getLoaderPool().toArray()[3].getClass()};
        me.setObject(ty);
        fr.getContentPane().add(me);
        fr.setVisible(true);
    }
}


/*
* Log
*  4    Jaga      1.2.1.0     3/24/00  Petr Kuzel      NullPointer 2
*  3    Gandalf-post-FCS1.2         3/23/00  Petr Kuzel      NullPointer bug fix.
*  2    Gandalf-post-FCS1.1         3/9/00   Petr Kuzel      I18N
*  1    Gandalf-post-FCS1.0         2/24/00  Ian Formanek    
* $ 
*/ 

