package bingo.internal.ui;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere
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
 * * Authors: Steven Maere
 * * Date: Apr.20.2005
 * * Class that extends JPanel and implements ActionListener. Makes
 * * a panel with a drop-down box of organism/annotation choices.
 * * Custom... opens FileChooser
 **/


import javax.swing.*;

import bingo.internal.BingoAlgorithm;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Arrays;
//import java.util.TreeMap;


/**
 * ***************************************************************
 * ChooseAnnotationPanel.java:   Steven Maere (c) April 2005
 * -----------------------
 * <p/>
 * Class that extends JPanel and implements ActionListener. Makes
 * a panel with a drop-down box of organism/annotation choices.
 * Custom... opens FileChooser
 * ******************************************************************
 */

public class ChooseAnnotationPanel extends JPanel implements ActionListener, ItemListener {

    /*--------------------------------------------------------------
     Fields.
    --------------------------------------------------------------*/
    private final String CUSTOM = BingoAlgorithm.CUSTOM;
    private final String NONE = BingoAlgorithm.NONE;


    /**
     * JComboBox with the possible choices.
     */
    private JComboBox<String> choiceBox;
//    /**
//     * Type Of Identifier choice panel for precompiled annotations
//     */
//    private TypeOfIdentifierPanel typeOfIdentifierPanel;
    /**
     * parent window
     */
    private Component settingsPanel;
    /**
     * the selected (kind of) annotation
     */
    private String specifiedSpecies;

    private String previousSelectedItem;
    /**
     * boolean to assess default (<code>true</code>) or custom
     * input (<code>false</code>)
     */
    private boolean defaultItem;


    /*-----------------------------------------------------------------
     CONSTRUCTOR.
    -----------------------------------------------------------------*/

    /**
     * Constructor
     *
     * @param settingsPanel : parent window
     */
    public ChooseAnnotationPanel(Component settingsPanel, String[] choiceArray, String choice_def) {
        super();
        this.settingsPanel = settingsPanel;

        setOpaque(false);

        choiceBox = new JComboBox<>(choiceArray);
        choiceBox.addActionListener(this);
        choiceBox.addItemListener(this);
//        typeOfIdentifierPanel = new TypeOfIdentifierPanel(identifier_labels, identifier_def);

//        // Layout with GridBagLayout
//        GridBagLayout gridbag = new GridBagLayout();
//        GridBagConstraints c = new GridBagConstraints();
//        setLayout(gridbag);
//
//        c.weightx = 1;
//        c.weighty = 1;
//        c.gridwidth = GridBagConstraints.REMAINDER;
//        c.fill = GridBagConstraints.HORIZONTAL;
//        gridbag.setConstraints(choiceBox, c);

        // Layout with GridLayout
        setLayout(new GridLayout(1, 0));
        add(choiceBox);

//        c.gridheight = 2;
//        c.weighty = 2;
//        gridbag.setConstraints(typeOfIdentifierPanel, c);
//        add(typeOfIdentifierPanel);
//        typeOfIdentifierPanel.enableButtons();

        // select default combo box item
        if (Arrays.asList(choiceArray).contains(choice_def)) {
            choiceBox.setSelectedItem(choice_def);
            specifiedSpecies = (String) choiceBox.getSelectedItem();
            defaultItem = true;
        } else {
            choiceBox.removeActionListener(this);
            choiceBox.setEditable(true);
            choiceBox.setSelectedItem(choice_def);
            choiceBox.setEditable(false);
            specifiedSpecies = CUSTOM;
            defaultItem = false;
            choiceBox.addActionListener(this);
        }
    }

    /*----------------------------------------------------------------
    METHODS.
    ----------------------------------------------------------------*/

//    /**
//     * Method that returns the TypeOfIdentifierPanel.
//     *
//     * @return File selected file.
//     */
//    public TypeOfIdentifierPanel getTypeOfIdentifierPanel() {
//        return typeOfIdentifierPanel;
//    }


    /**
     * Method that returns the specified species.
     *
     * @return File selected file.
     */
    public String getSpecifiedSpecies() {
        return specifiedSpecies;
    }

    /**
     * Method that returns the selected item.
     *
     * @return String selection.
     */
    public String getSelection() {
        return (String) choiceBox.getSelectedItem();
    }

    /**
     * Method that returns <code>true</code> if one of teh default choices was
     * chosen, or <code>false</code> if a custom annotation was chosen
     */
    public boolean isDefault() {
        return defaultItem;
    }

    /*----------------------------------------------------------------
    LISTENER-PART.
    ----------------------------------------------------------------*/

    /**
     * Method performed when combo box item was selected.
     *
     * @param event event that triggers action
     */
    public void actionPerformed(ActionEvent event) {
        //   typeOfIdentifierPanel.enableButtons();
        if (CUSTOM.equals(choiceBox.getSelectedItem())) {
//            JFileChooser chooser = new JFileChooser(System.getProperty("user.home"));
//            int returnVal = chooser.showOpenDialog(settingsPanel);
//            if (returnVal == JFileChooser.APPROVE_OPTION) {
//                choiceBox.setEditable(true);
//                choiceBox.setSelectedItem(chooser.getSelectedFile().toString());
//                choiceBox.setEditable(false);
//                //           typeOfIdentifierPanel.disableButtons();
//                defaultItem = false;
//            }
//            if (returnVal == JFileChooser.CANCEL_OPTION) {
//                choiceBox.setSelectedItem(NONE);
//                specifiedSpecies = NONE;
//                defaultItem = true;
//            }

            Frame parentFrame = (Frame) ((JComponent) settingsPanel).getTopLevelAncestor();
            String directoryPath;
            if (CUSTOM.equals(specifiedSpecies)) {
                directoryPath = new File(previousSelectedItem).getParent();
                System.out.println("directoryPath = " + directoryPath);
            } else {
                directoryPath = System.getProperty("user.home");
            }
            FileChooserOS fileChooser = new FileChooserOS(parentFrame, directoryPath);
            FileChooserOS.ReturnState returnState = fileChooser.showDialog();
            if (returnState == FileChooserOS.ReturnState.APPROVE) {
                specifiedSpecies = CUSTOM;
                choiceBox.setEditable(true);
                choiceBox.setSelectedItem(fileChooser.getSelectedFilePath());
                choiceBox.setEditable(false);
                //           typeOfIdentifierPanel.disableButtons();
                defaultItem = false;
            } else if (returnState == FileChooserOS.ReturnState.CANCEL) {
                if (CUSTOM.equals(specifiedSpecies)) {
                    choiceBox.setEditable(true);
                    choiceBox.setSelectedItem(previousSelectedItem);
                    choiceBox.setEditable(false);
                } else {
                    choiceBox.setSelectedItem(previousSelectedItem);
                }
            }
        } else if (NONE.equals(choiceBox.getSelectedItem())) {
            specifiedSpecies = NONE;
            defaultItem = true;
        } else {
            specifiedSpecies = (String) choiceBox.getSelectedItem();
            defaultItem = true;
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            previousSelectedItem = e.getItem().toString();
        }
    }
}
