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

package org.netbeans.modules.form;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.text.MessageFormat;

/** The RADMenuItemComponent represents one menu item component placed on the Form.
*
* @author Petr Hamernik, Ian Formanek
*/
public class RADMenuItemComponent extends RADComponent {

    static final Object DUMMY_SEPARATOR_INSTANCE = new Object ();

    /** A JDK 1.1 serial version UID */
    //  static final long serialVersionUID = -6333847833552116543L;

    /** Type of container */
    private int type;

    /** Possible constants for type variable */
    static final int T_MENUBAR              = 0x01110;
    static final int T_MENUITEM             = 0x00011;
    static final int T_CHECKBOXMENUITEM     = 0x00012;
    static final int T_MENU                 = 0x00113;
    static final int T_POPUPMENU            = 0x01114;

    static final int T_JPOPUPMENU           = 0x01125;
    static final int T_JMENUBAR             = 0x01126;
    static final int T_JMENUITEM            = 0x00027;
    static final int T_JCHECKBOXMENUITEM    = 0x00028;
    static final int T_JMENU                = 0x00129;
    static final int T_JRADIOBUTTONMENUITEM = 0x0002A;

    static final int T_SEPARATOR            = 0x1001B;
    static final int T_JSEPARATOR           = 0x1002C;

    /** Masks for the T_XXX constants */
    static final int MASK_AWT               = 0x00010;
    static final int MASK_SWING             = 0x00020;
    static final int MASK_CONTAINER         = 0x00100;
    static final int MASK_ROOT              = 0x01000;
    static final int MASK_SEPARATOR         = 0x10000;

    /** The MessageFormat for component names */
    private static MessageFormat menuNameFormat =
        new MessageFormat(FormEditor.getFormBundle().getString("FMT_MenuName"));

    // -----------------------------------------------------------------------------
    // Private properties

    transient private RADMenuComponent parent;

    /**
     * @associates DesignTimeMenus 
     */
    private static java.util.HashMap menusByFM = new java.util.HashMap ();


    // -----------------------------------------------------------------------------
    // Initialization

    void initParent (RADMenuComponent parent) {
        this.parent = parent;
    }

    /** No synthetic properties for AWT Separator */
    protected org.openide.nodes.Node.Property[] createSyntheticProperties () {
        if (type == T_SEPARATOR) return RADComponent.NO_PROPERTIES;
        else return super.createSyntheticProperties ();
    }

    public RADMenuComponent getParentMenu () {
        return parent;
    }

    // -----------------------------------------------------------------------------
    // Public interface

    public void setComponent (Class beanClass) {
        type = recognizeType(beanClass);
        // to initialize the type before calling super.setComponent is crucial,
        // as the type is used in various methods called from the setComponent
        // (e.g. the createSyntheticProperties () relies on this order to correctly
        //  provide no properties for AWT menu separators)

        super.setComponent (beanClass);


        Object o = getBeanInstance();

        // XXX(-tdt) PopupMenu is a subclass of MenuItem !!
        /* if (o instanceof MenuItem) { */
        if (o.getClass() == MenuItem.class) {
            JMenuItem designItem =
                ((JMenuItem) getDesignTimeMenus(getFormManager()).getDesignTime(o));
            designItem.addActionListener(getDefaultActionListener());
        }
        else if (o instanceof JMenuItem) {
            ((JMenuItem)o).addActionListener(getDefaultActionListener());
        }
    }

    int getMenuItemType () {
        return type;
    }

    /** Test the given class if is is subclass of one of four basic classes and
    * return adequate T_XXX constant.
    */
    static int recognizeType(Class cl) {
        if (JSeparator.class.isAssignableFrom(cl)) return T_JSEPARATOR;
        if (org.netbeans.modules.form.Separator.class.isAssignableFrom(cl)) return T_SEPARATOR;
        if (PopupMenu.class.isAssignableFrom(cl)) return T_POPUPMENU;
        if (Menu.class.isAssignableFrom(cl)) return  T_MENU;
        if (CheckboxMenuItem.class.isAssignableFrom(cl)) return T_CHECKBOXMENUITEM;
        if (MenuItem.class.isAssignableFrom(cl)) return  T_MENUITEM;
        if (MenuBar.class.isAssignableFrom(cl)) return T_MENUBAR;
        if (JRadioButtonMenuItem.class.isAssignableFrom(cl)) return T_JRADIOBUTTONMENUITEM;
        if (JMenu.class.isAssignableFrom(cl)) return T_JMENU;
        if (JCheckBoxMenuItem.class.isAssignableFrom(cl)) return T_JCHECKBOXMENUITEM;
        if (JMenuItem.class.isAssignableFrom(cl)) return T_JMENUITEM;
        if (JMenuBar.class.isAssignableFrom(cl)) return T_JMENUBAR;
        if (JPopupMenu.class.isAssignableFrom(cl)) return T_JPOPUPMENU;

        throw new InternalError ("Cannot create RADMenuItemComponent for nonmenu class:"+cl.getName()); // NOI18N
    }


    private ActionListener getDefaultActionListener() {
        return new ActionListener() {
                   public void actionPerformed(ActionEvent ev) {
                       if (!getFormManager ().isTestMode () && hasDefaultEvent ()) attachDefaultEvent ();
                   }
               };
    }

    static DesignTimeMenus getDesignTimeMenus (FormManager2 fm) {
        DesignTimeMenus dtm = (DesignTimeMenus) menusByFM.get (fm);
        if (dtm == null) {
            dtm = new DesignTimeMenus (fm);
            menusByFM.put (fm, dtm);
        }
        return dtm;
    }

    // to find existing menu if caller does not know about formManager
    public static Object findDesignTimeMenu (Object awtMenu) {
        Object result;
        for (java.util.Iterator it = menusByFM.keySet().iterator(); it.hasNext(); ) {
            DesignTimeMenus dtm = (DesignTimeMenus) menusByFM.get (it.next());
            if ((result = dtm.getDesignTime (awtMenu)) != null) {
                return result;
            }
        }
        return null;
    }
    // -----------------------------------------------------------------------------
    // Inner classes
    static class DesignTimeMenus {
        /**
         * @associates Object 
         */
        final java.util.HashMap designTimeMenus = new java.util.HashMap ();
        DesignTimeMenus (FormManager2 fm) {
            fm.addFormListener (new FormAdapter () {
                                    public void propertyChanged (FormPropertyEvent evt) {
                                        if (evt.getRADComponent () instanceof RADMenuItemComponent) {
                                            RADMenuItemComponent comp = (RADMenuItemComponent) evt.getRADComponent ();
                                            copyMenuProperties (comp.getBeanInstance(), getDesignTime (comp.getBeanInstance()));
                                        }
                                    }
                                    public void formLoaded () {
                                        for (java.util.Iterator it = designTimeMenus.keySet().iterator(); it.hasNext(); ) {
                                            Object menu = it.next();
                                            copyMenuProperties (menu, getDesignTime(menu));
                                        }
                                    }
                                });
        }

        Object getDesignTime (Object awtMenu) {
            Object swingMenu = designTimeMenus.get(awtMenu);
            if (swingMenu == null) {
                // create swingMenu with copy of awtMenu aplicable properties
                switch (recognizeType (awtMenu.getClass())) {
                case T_MENUBAR:          swingMenu = new JMenuBar ();          break;
                case T_MENU:             swingMenu = new JMenu ();             break;
                case T_POPUPMENU:        swingMenu = new JPopupMenu ();        break;
                case T_MENUITEM:         swingMenu = new JMenuItem ();         break;
                case T_CHECKBOXMENUITEM: swingMenu = new JCheckBoxMenuItem (); break;
                case T_JMENUBAR:
                case T_JMENU:
                case T_JPOPUPMENU:
                case T_JMENUITEM:
                case T_JCHECKBOXMENUITEM:
                case T_JRADIOBUTTONMENUITEM:
                case T_JSEPARATOR:
                    swingMenu = awtMenu;
                    break;
                    // PENDING - T_SEPARATOR
                }
                designTimeMenus.put (awtMenu, swingMenu);
                copyMenuProperties (awtMenu, swingMenu);
            }
            return swingMenu;
        }

        // copy all aplicable properties into swing equivalent of awt component
        void copyMenuProperties (Object awtMenu, Object swingMenu) {
            switch (recognizeType (awtMenu.getClass())) {
            case T_MENUBAR:
                MenuBar mb = (MenuBar) awtMenu;
                JMenuBar jmb = (JMenuBar) swingMenu;
                jmb.setFont(mb.getFont());
                jmb.setName(mb.getName());
                break;
            case T_MENU:
                Menu m = (Menu) awtMenu;
                JMenu jm = (JMenu) swingMenu;
                jm.setActionCommand(m.getActionCommand());
                jm.setEnabled(m.isEnabled());
                jm.setFont(m.getFont());
                jm.setLabel(m.getLabel());
                jm.setName(m.getName());
                jm.getPopupMenu().setLightWeightPopupEnabled(false);
                break;
            case T_POPUPMENU:
                PopupMenu pm = (PopupMenu) awtMenu;
                JPopupMenu jpm = (JPopupMenu) swingMenu;
                jpm.setEnabled(pm.isEnabled());
                jpm.setFont(pm.getFont());
                jpm.setLabel(pm.getLabel());
                jpm.setName(pm.getName());
                jpm.setLightWeightPopupEnabled(false);
                break;
            case T_MENUITEM:
                MenuItem mi = (MenuItem) awtMenu;
                JMenuItem jmi = (JMenuItem) swingMenu;
                jmi.setActionCommand(mi.getActionCommand());
                jmi.setEnabled(mi.isEnabled());
                jmi.setFont(mi.getFont());
                jmi.setLabel(mi.getLabel());
                jmi.setName(mi.getName());
                break;
            case T_CHECKBOXMENUITEM:
                CheckboxMenuItem cm = (CheckboxMenuItem) awtMenu;
                JCheckBoxMenuItem jcm = (JCheckBoxMenuItem) swingMenu;
                jcm.setActionCommand(cm.getActionCommand());
                jcm.setEnabled(cm.isEnabled());
                jcm.setFont(cm.getFont());
                jcm.setLabel(cm.getLabel());
                jcm.setName (cm.getName());
                jcm.setState(cm.getState());
                break;
                // PENDING - T_SEPARATOR
            }
        }
    }
}

/*
 * Log
 *  10   Gandalf-post-FCS1.8.1.0     3/27/00  Tran Duc Trung  FIX #6021: cannot add 
 *       AWT PopMenu
 *  9    Gandalf   1.8         1/5/00   Ian Formanek    NOI18N
 *  8    Gandalf   1.7         12/2/99  Pavel Buzek     AWT menu is displayed in
 *       form at design time (a swing equivalent is created for each awt menu 
 *       and displyed instead)
 *  7    Gandalf   1.6         11/10/99 Pavel Buzek     while menu is selectedf 
 *       in form in test mode do not go to event handler
 *  6    Gandalf   1.5         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  5    Gandalf   1.4         10/9/99  Ian Formanek    Fixed bug 4411 - Delete 
 *       of a jMenuItem does not work. (No action is performed.)
 *  4    Gandalf   1.3         9/6/99   Ian Formanek    Correctly works with 
 *       separators - fixes bug 3703 - When a new separator is created usng New 
 *       > Separator in a menu, an exception is thrown.
 *  3    Gandalf   1.2         8/6/99   Ian Formanek    setComponent is public
 *  2    Gandalf   1.1         7/16/99  Ian Formanek    default action
 *  1    Gandalf   1.0         7/5/99   Ian Formanek    
 * $
 */
