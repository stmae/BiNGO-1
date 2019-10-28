package bingo.internal.ui;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: Class that extends JPanel and implements ItemListener and ActionListener and
 * * which takes care of making a save panel with checkbox, button for choosing the
 * * location and name for the file to be saved and a textfield with the result of
 * * the selection, file name .     
 **/

import javax.swing.*;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


/**
 * ***************************************************************
 * SaveResultsPanel.java:       Steven Maere & Karel Heymans (c) 	March 2005
 * -----------------------
 * <p/>
 * Class that extends JPanel and implements ItemListener and ActionListener and
 * which takes care of making a save panel with checkbox, button for choosing the
 * location and name for the file to be saved and a textfield with the result of
 * the selection.
 * ******************************************************************
 */


public class SaveResultsPanel extends JPanel implements ItemListener, ActionListener {

    /*--------------------------------------------------------------
    Fields.
    --------------------------------------------------------------*/

    /**
     * JCheckBox for making choice of saving or not.
     */
    private JCheckBox checkBox;
    /**
     * the textfield for the save directory name
     */
    private JTextField fileTextField;
    /**
     * the button to open the dir chooser window.
     */
    private JButton browseDirectoryButton;
    /**
     * the place where the file is to be saved.
     */
    private File saveFile;
    /**
     * parent component
     */
    private Component settingsPanel;

    /*-----------------------------------------------------------------
    CONSTRUCTOR.
    -----------------------------------------------------------------*/

    /**
     * Constructor
     *
     */
    public SaveResultsPanel(Component settingsPanel, boolean save, String filePath) {
        super();
        this.settingsPanel = settingsPanel;
        if (filePath != null && !"".equals(filePath)) {
            this.saveFile = new File(filePath);
        }
        makeJComponents(save, filePath);
        setOpaque(false);

        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder());

        add(checkBox, BorderLayout.WEST);
        add(fileTextField, BorderLayout.CENTER);
        add(browseDirectoryButton, BorderLayout.EAST);
    }

    /*----------------------------------------------------------------
    METHODS.
    ----------------------------------------------------------------*/

    /**
     * Method that creates the JComponents.
     */
    private void makeJComponents(boolean save, String filePath) {

        // JCheckBox
        checkBox = new JCheckBox("Save BiNGO data file in:  ", save);
        checkBox.addItemListener(this);
        checkBox.setBorder(BorderFactory.createEmptyBorder());

        // textfield
        fileTextField = new JTextField(filePath);
        fileTextField.setEnabled(save);
        fileTextField.setEditable(false);
        fileTextField.setBackground(Color.white);
        fileTextField.setForeground(Color.black);

        // JButton
        browseDirectoryButton = new JButton("Browse...");
        browseDirectoryButton.setEnabled(save);
        browseDirectoryButton.addActionListener(this);
    }


    /**
     * Getter for the file dir.
     *
     * @return String file dir.
     */
    public String getFileDir() {
        return fileTextField.getText();
    }


    /**
     * Reset checkBox and JTextField()
     */

    public void reset() {
        checkBox.setSelected(false);
        fileTextField.setEnabled(false);
        fileTextField.setText(null);
        browseDirectoryButton.setEnabled(false);
    }


    /**
     * Boolean method for checking whether box is checked or not.
     *
     * @return boolean checked or not checked.
     */
    public boolean checked() {
        return checkBox.isSelected();
    }

    /**
     * Method for checking whether the selected file is legal are not.
     *
     * @return String with error or LOADCORRECT.
     */
    public String isFileNameLegal(String clusterName) {

        String resultString = "LOADCORRECT";

        if (checkBox.isSelected()) {
            try {
                new BufferedWriter(new FileWriter(new File(saveFile, clusterName)));
            }
            catch (Exception e) {
                resultString = "FILE NAMING ERROR:  " + e;
            }
        }
        return resultString;
    }

    /*----------------------------------------------------------------
    ITEMLISTENER-PART.
    ----------------------------------------------------------------*/

    /**
     * Method performed when checkbox checked or unchecked.
     *
     * @param e event that triggers action, here checking or unchecking checkbox.
     */
    public void itemStateChanged(ItemEvent e) {

        if (checkBox.isSelected()) {
            fileTextField.setEnabled(true);
            browseDirectoryButton.setEnabled(true);
        } else {
            fileTextField.setEnabled(false);
            browseDirectoryButton.setEnabled(false);
        }
    }

    /*----------------------------------------------------------------
    ACTIONLISTENER-PART.
    ----------------------------------------------------------------*/

    /**
     * Method performed when button clicked.
     *
     * @param e event that triggers action, here clicking of the button.
     */

    public void actionPerformed(ActionEvent e) {
//        JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
//        chooser.setDialogTitle("Select output directory");
//        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        chooser.setApproveButtonText("Select");
//        int returnVal = chooser.showOpenDialog(settingsPanel);
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            saveFile = chooser.getSelectedFile();
//            fileTextField.setText(saveFile.toString());
//        }

        final String currentDirectoryPath;
        if (saveFile != null && !"".equals(saveFile.getPath())) {
            currentDirectoryPath = saveFile.getPath();
        } else {
            currentDirectoryPath = null;
        }
        Frame parentFrame = (Frame) ((JComponent) settingsPanel).getTopLevelAncestor();
        FileChooserOS fileChooser = new FileChooserOS(parentFrame, currentDirectoryPath);
        fileChooser.setTitle("Select output directory");
        fileChooser.setApproveButtonText("Select");
        FileChooserOS.ReturnState returnState = fileChooser.showDialog(true);
        if (returnState == FileChooserOS.ReturnState.APPROVE) {
            saveFile = fileChooser.getSelectedFile();
            fileTextField.setText(saveFile.toString());
        }
    }
}
