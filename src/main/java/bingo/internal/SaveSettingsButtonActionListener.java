package bingo.internal;

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
 * * Authors: Steven Maere
 * * Date: Jul.09.2010
 * * Description: Class which implements ActionListener and saves default settings
 **/


import javax.swing.*;

import bingo.internal.ui.SettingsPanel;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Properties;


/**
 * *****************************************************************
 * SaveSettingsButtonActionListener.java     Steven Maere & Karel Heymans (c) March 2005
 * ----------------
 * <p/>
 * Class which implements ActionListener and saves default settings.
 * ******************************************************************
 */


public class SaveSettingsButtonActionListener implements ActionListener {


    /*--------------------------------------------------------------
    FIELD.
    --------------------------------------------------------------*/
    private SettingsPanel settingsPanel;

    /*--------------------------------------------------------------
       CONSTRUCTOR.
    --------------------------------------------------------------*/

    public SaveSettingsButtonActionListener(SettingsPanel settingsPanel) {
        this.settingsPanel = settingsPanel;

    }

    /*--------------------------------------------------------------
    LISTENER-PART.
    --------------------------------------------------------------*/

    /**
     * Method performed when button clicked.
     *
     * @param e event that triggers action
     */
    public void actionPerformed(ActionEvent e) {
        try {
            //adapt settings in properties file
            Properties bingoProps = settingsPanel.getbingoProps();
            bingoProps.setProperty("tests_def", (String) settingsPanel.getTestBox().getSelectedItem());
            bingoProps.setProperty("correction_def", (String) settingsPanel.getCorrectionBox().getSelectedItem());
            bingoProps.setProperty("categories_def", (String) settingsPanel.getCategoriesBox().getSelectedItem());
            bingoProps.setProperty("signif_def", settingsPanel.getAlphaField().getText());
            bingoProps.setProperty("refset_def", settingsPanel.getClusterVsPanel().getSelection());
            bingoProps.setProperty("species_def", settingsPanel.getAnnotationPanel().getSelection());
            bingoProps.setProperty("ontology_file_def", settingsPanel.getOntologyPanel().getSelection());
            bingoProps.setProperty("namespace_def", settingsPanel.getNamespacePanel().getSelection());
            if (settingsPanel.getAnnotationPanel().isDefault()) {
                bingoProps.setProperty("annotation_default", "true");
            } else {
                bingoProps.setProperty("annotation_default", "false");
            }
            if (settingsPanel.getOntologyPanel().isDefault()) {
                bingoProps.setProperty("ontology_default", "true");
            } else {
                bingoProps.setProperty("ontology_default", "false");
            }
            final boolean graphButtonChecked = settingsPanel.getTextOrGraphPanel().graphButtonChecked();
            bingoProps.setProperty("graph_def", String.valueOf(graphButtonChecked));
            if (graphButtonChecked) {
                bingoProps.setProperty("text_def", "");
            } else {
                bingoProps.setProperty("text_def", settingsPanel.getTextOrGraphPanel().getInputText());
            }
            bingoProps.setProperty("overunder_def", settingsPanel.getOverUnderVizPanel().getCheckedRadioButton());
            bingoProps.setProperty("visual_def", settingsPanel.getOverUnderVizPanel().getVizCheckBoxState());
            bingoProps.setProperty("ec_def", settingsPanel.getEcField().getText());
            final boolean saveButtonChecked = settingsPanel.getDataPanel().checked();
            bingoProps.setProperty("file_output", String.valueOf(saveButtonChecked));
            if (saveButtonChecked) {
                bingoProps.setProperty("outputdir_def", settingsPanel.getDataPanel().getFileDir());
            } else {
                bingoProps.setProperty("outputdir_def", "");
            }
            settingsPanel.getParams().storeParameterSettings();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(settingsPanel, "Could not save settings: " + ex);
        }
    }
}
