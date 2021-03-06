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

package org.netbeans.modules.db.explorer.actions;

import java.util.ResourceBundle;
import org.openide.*;
import org.openide.util.NbBundle;
import java.util.*;
import java.io.*;
import java.sql.Connection;
import java.text.MessageFormat;
import org.openide.nodes.*;
import org.netbeans.modules.db.explorer.nodes.*;
import org.netbeans.modules.db.explorer.infos.*;
import org.netbeans.modules.db.explorer.dlg.*;
import javax.swing.JFileChooser;
import org.netbeans.lib.ddl.impl.*;

public class RecreateTableAction extends DatabaseAction
{
    static final long serialVersionUID =6992569917995229492L;
    public void performAction (Node[] activatedNodes)
    {
        Node node;
        if (activatedNodes != null && activatedNodes.length>0) node = activatedNodes[0];
        else return;
        try {

            final ResourceBundle bundle = NbBundle.getBundle("org.netbeans.modules.db.resources.Bundle");
            DatabaseNodeInfo info = (DatabaseNodeInfo)node.getCookie(DatabaseNodeInfo.class);
            TableListNodeInfo nfo = (TableListNodeInfo)info.getParent(nodename);
            Specification spec = (Specification)nfo.getSpecification();
            String tablename = (String)nfo.get(DatabaseNode.TABLE);
            AbstractCommand cmd;

            // Get filename

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            chooser.setDialogTitle(bundle.getString("RecreateTableFileOpenDialogTitle"));
            chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                                      public boolean accept(File f) {
                                          return (f.isDirectory() || f.getName().endsWith(".grab"));
                                      }
                                      public String getDescription() {
                                          return bundle.getString("GrabTableFileTypeDescription");
                                      }
                                  });

            java.awt.Component par = TopManager.getDefault().getWindowManager().getMainWindow();
            if (chooser.showOpenDialog(par) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if (file != null && file.isFile()) {
                    FileInputStream fstream = new FileInputStream(file);
                    ObjectInputStream istream = new ObjectInputStream(fstream);
                    cmd = (AbstractCommand)istream.readObject();
                    istream.close();
                    cmd.setSpecification(spec);
                } else return;
            } else return;

            String newtab = cmd.getObjectName();
            String msg = MessageFormat.format(bundle.getString("RecreateTableRenameNotes"), new String[] {cmd.getCommand()});
            LabeledTextFieldDialog dlg = new LabeledTextFieldDialog(bundle.getString("RecreateTableRenameTable"), bundle.getString("RecreateTableNewName"), msg);
            dlg.setStringValue(newtab);
            if (dlg.run()) {
                newtab = dlg.getStringValue();
                cmd.setObjectName(newtab);
                cmd.execute();
                nfo.addTable(newtab);
            }

        } catch(Exception e) {
            TopManager.getDefault().notify(new NotifyDescriptor.Message("Unable to recreate, "+e.getMessage(), NotifyDescriptor.ERROR_MESSAGE));
        }
    }
}
/*
 * <<Log>>
 *  8    Gandalf   1.7         11/27/99 Patrik Knakal   
 *  7    Gandalf   1.6         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  6    Gandalf   1.5         6/22/99  Ian Formanek    employed DEFAULT_HELP
 *  5    Gandalf   1.4         6/9/99   Ian Formanek    ---- Package Change To 
 *       org.openide ----
 *  4    Gandalf   1.3         5/21/99  Slavek Psenicka new version
 *  3    Gandalf   1.2         5/14/99  Slavek Psenicka new version
 *  2    Gandalf   1.1         4/23/99  Slavek Psenicka oprava activatedNode[0] 
 *       check
 *  1    Gandalf   1.0         4/23/99  Slavek Psenicka 
 * $
 */
