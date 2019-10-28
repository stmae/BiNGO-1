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
* * Date: Mar.25.2005
* * Description: Class that extends JPanel ;
* * makes panel that allows user to choose the type of assessment and
* * whether he/she wants to visualize the results or not.
**/


import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;


/**
 * ***************************************************************
 * OverUnderVizPanel.java:       Steven Maere (c) March 2005
 * -----------------------
 * <p/>
 * Class that extends JPanel ; makes panel that allows user to choose the type
 * of assessment and whether he/she wants to visualize the results or not.
 * <p/>
 * ******************************************************************
 */


public class OverUnderVizPanel extends JPanel {

    /*--------------------------------------------------------------
    Fields.
    --------------------------------------------------------------*/

    /**
     * Radio button overrepresentation
     */
    private JRadioButton overButton;

    /**
     * Radio button underrepresentation
     */
    private JRadioButton underButton;

    /**
     * Check box visualization
     */
    private final JCheckBox visualizationCheckBox;

    public static String OVERSTRING = "Overrepresentation";
    private static String UNDERSTRING = "Underrepresentation";
    public static String VIZSTRING = "Visualization";
    private static String NOVIZSTRING = "No visualization";


    /*-----------------------------------------------------------------
    CONSTRUCTOR.
    -----------------------------------------------------------------*/

    public OverUnderVizPanel(String overUnder, String viz) {
        super();
        setOpaque(false);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder());

        // Create label for radio buttons
        add(new JLabel("Assess: "), BorderLayout.WEST);

        // Create panel with row of radio buttons
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        overButton = new JRadioButton(OVERSTRING, OVERSTRING.equalsIgnoreCase(overUnder));
        overButton.setBorder(BorderFactory.createEmptyBorder());
        overButton.setMnemonic(KeyEvent.VK_G);
        overButton.setActionCommand(OVERSTRING);
        radioPanel.add(overButton);

        radioPanel.add(Box.createHorizontalStrut(5));

        underButton = new JRadioButton(UNDERSTRING, UNDERSTRING.equalsIgnoreCase(overUnder));
        underButton.setBorder(BorderFactory.createEmptyBorder());
        underButton.setMnemonic(KeyEvent.VK_S);
        underButton.setActionCommand(UNDERSTRING);
        radioPanel.add(underButton);

        // Group the radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(overButton);
        group.add(underButton);

        add(radioPanel, BorderLayout.CENTER);

        // Create visualization check box
        visualizationCheckBox = new JCheckBox(VIZSTRING, VIZSTRING.equalsIgnoreCase(viz));
        visualizationCheckBox.setBorder(BorderFactory.createEmptyBorder());
        add(visualizationCheckBox, BorderLayout.EAST);
    }

    /*----------------------------------------------------------------
    METHODS.
    ----------------------------------------------------------------*/

    /**
     * Method for checking which radio button is selected.
     *
     * @return string "Overrepresentation" or "Underrepresentation"
     */
    public String getCheckedRadioButton() {
        if (overButton.isSelected()) {
            return OVERSTRING;
        } else if (underButton.isSelected()) {
            return UNDERSTRING;
        } else {
            throw new IllegalStateException("None of the radio buttons is selected");
        }
    }

    /**
     * Method for getting state of visualization check box, checked or not.
     *
     * @return string "Visualization" or "No visualization"
     */
    public String getVizCheckBoxState() {
        if (visualizationCheckBox.isSelected()) {
            return VIZSTRING;
        } else {
            return NOVIZSTRING;
        }
    }
}
