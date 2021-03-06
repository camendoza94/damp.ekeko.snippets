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

package org.netbeans.modules.javadoc;

import java.io.File;
import java.util.ResourceBundle;
import java.awt.event.KeyEvent;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

import org.openide.util.NbBundle;
import org.openide.DialogDescriptor;
import org.openide.NotifyDescriptor;
import org.openide.TopManager;
import org.openide.util.HelpCtx;

/** Lets user select the destination for generating documentation
 * @author  phrebejk
 */
class DestinationPanel extends javax.swing.JPanel
    implements java.awt.event.ActionListener {

    /** The resource bundle */
    private static final ResourceBundle bundle = NbBundle.getBundle( DestinationPanel.class );


    /** Options of the dialog */
    private static javax.swing.JButton OK_BUTTON = null;
    private static javax.swing.JButton CANCEL_BUTTON = null;

    /** The dialog containing this panel */
    java.awt.Dialog dialog;

    /** The default destination directory */
    private File defaultDir = null;

    static final long serialVersionUID =1905540018208272852L;
    /** Creates new form DestinationPanel */
    public DestinationPanel() {
        initComponents ();

        // i18n
        destinationLabel.setText( bundle.getString( "CTL_Destination_label" ) );
        browseButton.setText( bundle.getString( "CTL_Destination_browseButton" ) );


        /*
        destinationField.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER));
        System.out.println( destinationField.getRegisteredKeyStrokes()[0] ); 
        System.out.println( KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));


        Keymap defaultKM = destinationField.getKeymap( javax.swing.text.JTextComponent.DEFAULT_KEYMAP );
        destinationField.removeKeymap( javax.swing.text.JTextComponent.DEFAULT_KEYMAP );

        destinationField.addKeymap( null, null );

        //f = new JTextField();



        // KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        // Keymap map = f.getKeymap();
        // map.removeKeyStrokeBinding(enter);
        */

        HelpCtx.setHelpIDString (this, DestinationPanel.class.getName ());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents () {//GEN-BEGIN:initComponents
        destinationLabel = new javax.swing.JLabel ();
        destinationField = new javax.swing.JTextField ();
        browseButton = new javax.swing.JButton ();
        setLayout (new java.awt.GridBagLayout ());
        java.awt.GridBagConstraints gridBagConstraints1;
        setPreferredSize (new java.awt.Dimension(405, 71));
        setBorder (new javax.swing.border.EmptyBorder(new java.awt.Insets(16, 16, 16, 16)));

        destinationLabel.setText ("jLabel1"); // NOI18N


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridwidth = 0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        add (destinationLabel, gridBagConstraints1);



        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets (0, 0, 0, 6);
        gridBagConstraints1.weightx = 1.0;
        add (destinationField, gridBagConstraints1);

        browseButton.setMargin (new java.awt.Insets(0, 2, 0, 2));
        browseButton.setText ("jButton1"); // NOI18N
        browseButton.addActionListener (new java.awt.event.ActionListener () {
                                            public void actionPerformed (java.awt.event.ActionEvent evt) {
                                                browseButtonActionPerformed (evt);
                                            }
                                        }
                                       );


        gridBagConstraints1 = new java.awt.GridBagConstraints ();
        gridBagConstraints1.gridwidth = 0;
        add (browseButton, gridBagConstraints1);

    }//GEN-END:initComponents

    private void browseButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle(bundle.getString("CTL_DestChooser_Title"));

        if ( defaultDir != null) {
            chooser.setSelectedFile( defaultDir );
        }

        HelpCtx.setHelpIDString (chooser, DestinationPanel.class.getName ());

        if (chooser.showDialog(TopManager.getDefault ().getWindowManager ().getMainWindow (),
                               bundle.getString("CTL_Destination_Approve_Button"))
                == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            if ( f != null && f.isDirectory() ) {
                destinationField.setText( f.getAbsolutePath() );
            }
        }
    }//GEN-LAST:event_browseButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel destinationLabel;
    private javax.swing.JTextField destinationField;
    private javax.swing.JButton browseButton;
    // End of variables declaration//GEN-END:variables


    private void setDefaultDir( File defaultDir ) {
        this.defaultDir = defaultDir;
        destinationField.setText( defaultDir.getAbsolutePath() );
    }

    private File getDestination() {
        File dest = new File( destinationField.getText() );

        if ( dest != null && dest.isDirectory() ) {
            return dest;
        }
        else {
            return null;
        }
    }

    static File showDialog( File defaultDir ) {

        DestinationPanel panel = new DestinationPanel();
        panel.setDefaultDir( defaultDir );

        OK_BUTTON = new javax.swing.JButton( bundle.getString( "CTL_Destination_OkButton" ) );
        CANCEL_BUTTON = new javax.swing.JButton( bundle.getString( "CTL_Destination_CancelButton" ) );

        DialogDescriptor dialogDescriptor = new DialogDescriptor(
                                                panel,
                                                bundle.getString( "CTL_Destination_Title" ),
                                                true,
                                                new Object[] { OK_BUTTON, CANCEL_BUTTON },
                                                OK_BUTTON,
                                                DialogDescriptor.BOTTOM_ALIGN,
                                                new HelpCtx ( DestinationPanel.class ),
                                                panel );

        panel.dialog = TopManager.getDefault().createDialog( dialogDescriptor );
        panel.dialog.show();

        if ( dialogDescriptor.getValue() == OK_BUTTON ) {
            return panel.getDestination();
        }
        else {
            return null;
        }
    }

    public void actionPerformed(final java.awt.event.ActionEvent evt) {

        if ( dialog == null )
            return;

        if ( evt.getSource() != OK_BUTTON && evt.getSource() != CANCEL_BUTTON )
            return;

        if ( evt.getSource() == OK_BUTTON ) {
            File f = getDestination();
            if ( f == null ) {
                NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
                                          bundle.getString( "MSG_NonExistingDirectory" ),
                                          NotifyDescriptor.OK_CANCEL_OPTION );

                TopManager.getDefault().notify( nd );

                if ( nd.getValue() == NotifyDescriptor.OK_OPTION ) { // Create the directory
                    File newDir = new File( destinationField.getText() );
                    newDir.mkdirs();

                    if ( !newDir.isDirectory() ) { // Can't create directory
                        NotifyDescriptor ndm = new NotifyDescriptor.Message(
                                                   bundle.getString( "MSG_CantCreateDirectory" ) );
                        TopManager.getDefault().notify( ndm );
                        return;
                    }
                    else { // Directory created
                        // Do nothing
                    }
                }
                else { // Don't create directory

                    return;
                }

            }
        }

        // Javadoc runs or cancel was pressed
        dialog.setVisible( false );
        dialog.dispose();
        dialog = null;
    }
}
/*
 * Log
 *  8    Gandalf   1.7         1/12/00  Petr Hrebejk    i18n
 *  7    Gandalf   1.6         1/11/00  Jesse Glick     Context help.
 *  6    Gandalf   1.5         1/10/00  Petr Hrebejk    Bug 4747 - closing of 
 *       output tab fixed
 *  5    Gandalf   1.4         11/27/99 Patrik Knakal   
 *  4    Gandalf   1.3         11/10/99 Petr Hrebejk    Javadoc creates 
 *       nonexisting output directories
 *  3    Gandalf   1.2         11/5/99  Jesse Glick     Context help jumbo 
 *       patch.
 *  2    Gandalf   1.1         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  1    Gandalf   1.0         9/15/99  Petr Hrebejk    
 * $
 */
