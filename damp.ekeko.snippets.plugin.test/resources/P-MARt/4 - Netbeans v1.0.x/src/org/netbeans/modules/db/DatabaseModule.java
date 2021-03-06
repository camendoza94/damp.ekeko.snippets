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

package org.netbeans.modules.db;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.openide.*;
import org.openide.filesystems.*;
import org.openide.loaders.DataFolder;
import org.openide.loaders.InstanceDataObject;
import org.openide.modules.*;
import org.openide.TopManager;
import org.openide.util.NbBundle;

/**
* DB module.
* @author Slavek Psenicka
*/
public class DatabaseModule extends ModuleInstall {
    private ResourceBundle bundle = NbBundle.getBundle("org.netbeans.modules.db.resources.Bundle");

    static final long serialVersionUID =5426465356344170725L;

    public void installed() {
        TopManager tm = TopManager.getDefault();

        try {
            FileSystem rfs = tm.getRepository().getDefaultFileSystem();
            FileObject rootFolder = rfs.getRoot();
            FileObject databaseFileObject = rootFolder.getFileObject("Database");
            if (databaseFileObject == null) {
                databaseFileObject = rootFolder.createFolder("Database");
                FileObject adaptorsFileObject = databaseFileObject.createFolder("Adaptors");
                InstanceDataObject.create(DataFolder.findFolder(adaptorsFileObject), "DefaultAdaptor", org.netbeans.lib.ddl.adaptors.DefaultAdaptor.class);
            }
        } catch (LinkageError ex) {
            String msg = MessageFormat.format(bundle.getString("FMT_CLASSNOTFOUND"), new String[] {ex.getMessage()});
            if (tm != null)
                tm.notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
        } catch (Exception ex) {
            String msg = MessageFormat.format(bundle.getString("FMT_EXCEPTIONINSTALL"), new String[] {ex.getMessage()});
            if (tm != null)
                tm.notify(new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE));
        }
    }
}

/*
* <<Log>>
*  10   Gandalf   1.9         3/3/00   Radko Najman    
*  9    Gandalf   1.8         11/27/99 Patrik Knakal   
*  8    Gandalf   1.7         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun 
*       Microsystems Copyright in File Comment
*  7    Gandalf   1.6         10/12/99 Radko Najman    debug messages removed
*  6    Gandalf   1.5         10/1/99  Petr Hrebejk    org.openide.modules.ModuleInstall
*        changed to class + some methods added
*  5    Gandalf   1.4         9/27/99  Slavek Psenicka new Database/Adaptors 
*       folder
*  4    Gandalf   1.3         6/9/99   Ian Formanek    ---- Package Change To 
*       org.openide ----
*  3    Gandalf   1.2         5/21/99  Slavek Psenicka new version
*  2    Gandalf   1.1         5/14/99  Slavek Psenicka new version
*  1    Gandalf   1.0         4/23/99  Slavek Psenicka 
* $
*/
