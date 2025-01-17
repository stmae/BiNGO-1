/*
 * ResultPanel.java
 *
 * Created on July 31, 2006, 3:37 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * The software and documentation provided hereunder is on an "as is" basis,
 * and the Pasteur Institut
 * has no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall the
 * Pasteur Institut
 * be liable to any party for direct, indirect, special,
 * incidental or consequential damages, including lost profits, arising
 * out of the use of this software and its documentation, even if
 * the Pasteur Institut
 * has been advised of the possibility of such damage. See the
 * GNU General Public License for more details: 
 *                http://www.gnu.org/licenses/gpl.txt.
 *
 * Authors: Olivier Garcia
 */

package bingo.internal.GOlorize;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.view.model.CyNetworkView;

import bingo.internal.Browser;
import bingo.internal.SignificantFigures;
import bingo.internal.ontology.Annotation;
import bingo.internal.ontology.Ontology;


/**
 * 
 * @author ogarcia
 */
public class ResultPanel extends JPanel implements ResultAndStartPanel {

	public static final String HEADER_GO_ID = "GO ID";
	public static final String HEADER_GO_DESCRIPTION = "GO Description";
	public static final String HEADER_P_VAL = "p-val";
	public static final String HEADER_CORRECTED_P_VAL = "Corrected p-val";
	public static final String HEADER_CLUSTER_FREQUENCY = "Cluster frequency";
	public static final String HEADER_TOTAL_FREQUENCY = "Total frequency";
	public static final String HEADER_GENES = "Genes";

	private Map testMap;
	/** hashmap with key termID and value corrected pvalue. */
	private Map correctionMap;
	/** hashmap with key termID and value x. */
	private Map mapSmallX;
	/** hashmap with key termID and value n. */
	private Map mapSmallN;
	/** hashmap with X. */
	private Map mapBigX;
	/** hashmap with N. */
	private Map mapBigN;
	/** String with alpha value. */
	private String alphaString;
	/** String with used test. */
	private String testString;
	/** String with used correction. */
	private String correctionString;
	/** String for over- or underrepresentation. */
	private String overUnderString;
	/** the annotation (remapped, i.e. including all parent annotations) */

	Annotation annotation;
	/** the ontology. */
	Ontology ontology;
	/** the annotation file path. */

	private Map<String, Set<String>> alias;

	private String annotationFile;
	/** the ontology file path. */
	private String ontologyFile;
	/** the dir for saving the data file. */
	private String dirName;
	/** the file name for the data file. */
	private String fileName;
	/** the clusterVsString. */
	private String clusterVsString;
	/** the categoriesString. */
	private String catString;
	/** HashSet with the names of the selected nodes. */
	private Set selectedCanonicalNameVector;
	/**
	 * hashmap with keys the GO categories and values HashSets of test set genes
	 * annotated to that category
	 */
	private HashMap annotatedGenes;

	private final String NONE = "---";
	/**
	 * constant string for the checking of numbers of categories, all
	 * categories.
	 */
	private final String CATEGORY_ALL = "All categories";
	/**
	 * constant string for the checking of numbers of categories, before
	 * correction.
	 */
	private final String CATEGORY_BEFORE_CORRECTION = "Overrepresented categories before correction";
	/**
	 * constant string for the checking of numbers of categories, after
	 * correction.
	 */
	private final String CATEGORY_CORRECTION = "Overrepresented categories after correction";

	javax.swing.JTable jTable1;
	JScrollPane jScrollPane;
	private JPanel jPanelTableau;
	private JPanel jPanelDeBase;
	private JPanel jPanelBoutons;
	Object[][] data;
	private JButton jButton1;
	private JButton jButton2;
	private JButton jButton3;
	private Label label;
	private Cursor hand;
	private String urlGO;
	private Object[] columnNames;

	private CyNetwork originalNetwork;

	private CyNetworkView originalNetworkView;
	// JComboBox genesLinked;
	// JComboBox goNodesView;
	// JComboBox genesLinkedView;
	HashMap goColor = new HashMap();// ////////

	GoBin goBin;
	final static int SELECT_COLUMN = 0;
	final static int GO_TERM_COLUMN = 1;
	final static int DESCRIPTION_COLUMN = 2;
	
	private final CySwingAppAdapter adapter;

	/**
	 * Creates a new instance of ResultPanel
	 */

	public ResultPanel(Map testMap, Map correctionMap, Map mapSmallX, Map mapSmallN, Map mapBigX,
			Map mapBigN, String alphaString, Annotation annotation, Map alias, Ontology ontology,
			String annotationFile, String ontologyFile, String testString, String correctionString,
			String overUnderString, String dirName, String fileNam, String clusterVsString, String catString,
			Set selectedCanonicalNameVector, CyNetwork currentNetwork, CyNetworkView currentNetworkview, GoBin goB, final CySwingAppAdapter adapter) {
		this.fileName = fileNam.substring(0, fileNam.length() - 4);
		
		this.adapter = adapter;
		this.testMap = testMap;
		this.correctionMap = correctionMap;
		this.mapSmallX = mapSmallX;
		this.mapSmallN = mapSmallN;
		this.mapBigX = mapBigX;
		this.mapBigN = mapBigN;
		this.alphaString = alphaString;
		this.annotation = annotation;
		this.alias = alias;
		this.ontology = ontology;
		this.annotationFile = annotationFile;
		this.ontologyFile = ontologyFile;
		this.testString = testString;
		this.correctionString = correctionString;
		this.overUnderString = overUnderString;
		this.dirName = dirName;
		this.clusterVsString = clusterVsString;
		this.catString = catString;
		this.selectedCanonicalNameVector = selectedCanonicalNameVector;
		this.annotatedGenes = new HashMap();
		this.hand = new Cursor(Cursor.HAND_CURSOR);
		this.originalNetwork = currentNetwork;
		this.originalNetworkView = currentNetworkview;
		this.goBin = goB;

		// the result panels share the annotation structure when their
		// description are identic
		// System.out.println("avant annotation "+annotation.hashCode());
		// System.out.println("avant ontology "+ontology.hashCode());
		if (this.goBin.getResultPanelCount() != 0)
			for (int i = 1; i < this.goBin.getResultPanelCount() + 1; i++) {
				if (goBin.getResultPanelAt(i).ontology.getCurator().equals(ontology.getCurator())
						&& goBin.getResultPanelAt(i).ontology.getType().equals(ontology.getType())) {
					ontology = goBin.getResultPanelAt(i).ontology;
					annotation.setOntology(ontology);
				}
				if (goBin.getResultPanelAt(i).annotation.getCurator().equals(annotation.getCurator())
						&& goBin.getResultPanelAt(i).annotation.getSpecies().equals(annotation.getSpecies())
						&& goBin.getResultPanelAt(i).annotation.getType().equals(annotation.getType())) {
					annotation = goBin.getResultPanelAt(i).annotation;
					ontology = goBin.getResultPanelAt(i).ontology;
					annotation.setOntology(ontology);
				}

			}
		// System.out.println("ontology:"+ontology.getDescription().getCurator()+" "+ontology.getDescription().getType());
		// System.out.println("curator:"+annotation.getCurator()+" species:"+annotation.getSpecies()+" type:"+annotation.getType());
		// System.out.println("apres annotation "+annotation.hashCode());
		// System.out.println("apres ontology "+ontology.hashCode());
		initComponents();

	}

	void initComponents() {
		columnNames = makeHeadersForJTable();
		data = makeDataForJTable(columnNames.length);
		correctionMap = null;// je sais ca n'a rien a faire en tant que membre
								// de classe
		mapSmallX = null;
		this.mapSmallN = null;
		this.alphaString = null;
		this.annotationFile = null;
		this.correctionString = null;
		this.overUnderString = null;
		this.clusterVsString = null;
		this.catString = null;
		this.selectedCanonicalNameVector = null;
		this.annotatedGenes = null;

		jPanelDeBase = new javax.swing.JPanel();
		this.setLayout(new java.awt.BorderLayout());
		jPanelDeBase.setLayout(new BorderLayout());

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();

		JPanel jPanelButtons = new JPanel();

		// JLabel ontology_Description = new
		// JLabel(annotation.getOntology().getDescription().getType()+" "+annotation.getOntology().getDescription().getCurator());
		// ontology_Description.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));
		// jPanelButtons.add(ontology_Description);
		JLabel annotation_Description = new JLabel("Annotation: Curator = " + annotation.getCurator() + ", " +
												   "Species or file = " + annotation.getSpecies() + ", " +
												   "Type = " + annotation.getType() + "   " +
												   "Ontology: Curator = " + ontology.getCurator() + ", " +
												   "Type = " + ontology.getType());
//		annotation_Description.setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));
		jPanelButtons.add(annotation_Description);

		JButton jCloseButton = new JButton("Close tab");
		jCloseButton.addActionListener(new CloseResultPanelListener(this));
		jPanelButtons.add(jCloseButton);

		jPanelDeBase.add(jPanelButtons, java.awt.BorderLayout.NORTH);
                if(!(data.length == 0)){
                    final ResultTableModel tableModel = new ResultTableModel(columnNames, data);
                    final ResultTableSortFilterModel sortedTableModel;
                    if (testString.equals(NONE)) {
                        // data is sorted by smallX values which are in column 3
                        // in reverse (descending) order
                        sortedTableModel = new ResultTableSortFilterModel(tableModel, 3, true);
                    } else {
                        // data is sorted by p-values which are in column 3
                        // in ascending order
                        sortedTableModel = new ResultTableSortFilterModel(tableModel, 3, false);
                    }
                    jTable1 = new JTable(sortedTableModel);
                    jTable1.setDragEnabled(false);
                    jTable1.setCellSelectionEnabled(true);
//                  jTable1.addMouseListener(new MouseT1Handler(this));
//                  jTable1.addMouseMotionListener(new MouseMotionT1Handler());
                    TableCellRenderer headerRenderer = jTable1.getTableHeader().getDefaultRenderer();
                    TableColumnModel tcModel = jTable1.getColumnModel();
                    for (int i = 0; i < columnNames.length; i++) {
			final TableColumn column = tcModel.getColumn(i);
			if (columnNames[i].equals(" ")) {
				// check box column (1st column)
//				tcModel.getColumn(i).setPreferredWidth(15);
				Object rowZeroValue = tableModel.getValueAt(0, i);
				TableCellRenderer defaultRenderer = jTable1.getDefaultRenderer(tableModel.getColumnClass(i));
				Component comp = defaultRenderer.getTableCellRendererComponent(jTable1, rowZeroValue,
																			   false, false, 0, i);
				int cellWidth = comp.getPreferredSize().width;
				column.setMinWidth(cellWidth);
				column.setMaxWidth(cellWidth);
				column.setPreferredWidth(cellWidth);
			}
			if (columnNames[i].equals(HEADER_GO_ID)) {
//				tcModel.getColumn(i).setPreferredWidth(50);
				TableCellRenderer defaultRenderer = jTable1.getDefaultRenderer(tableModel.getColumnClass(i));
				Component comp = defaultRenderer.getTableCellRendererComponent(jTable1, 999_999,
																			   false, false, 0, i);
				int cellWidth = comp.getPreferredSize().width;
				column.setPreferredWidth((int) (cellWidth * 1.2));
				column.setMaxWidth((int) (cellWidth * 1.3));
			}
			if (columnNames[i].equals(HEADER_GO_DESCRIPTION)) {
//				tcModel.getColumn(i).setPreferredWidth((screenSize.width - 15 - 50 - 85 - 85 - 70 - 70 - 40) / 3);
//				tcModel.getColumn(i).setPreferredWidth(300);
			}
			if (columnNames[i].equals(HEADER_P_VAL)) {
//				tcModel.getColumn(i).setPreferredWidth(70);
				Component comp = headerRenderer.getTableCellRendererComponent(jTable1, column.getHeaderValue(),
																			  false, false, 0, i);
				int headerWidth = comp.getPreferredSize().width;
				TableCellRenderer defaultRenderer = jTable1.getDefaultRenderer(tableModel.getColumnClass(i));
				comp = defaultRenderer.getTableCellRendererComponent(jTable1, 9.99999E-222,
																	 false, false, 0, i);
				int cellWidth = comp.getPreferredSize().width;
				int width = Math.max(cellWidth, headerWidth);
				column.setPreferredWidth((int) (width * 1.2));
				column.setMaxWidth((int) (width * 1.3));
			}
			if (columnNames[i].equals(HEADER_CORRECTED_P_VAL)) {
//				tcModel.getColumn(i).setPreferredWidth(70);
				Component comp = headerRenderer.getTableCellRendererComponent(jTable1, column.getHeaderValue(),
																			  false, false, 0, i);
				int headerWidth = comp.getPreferredSize().width;
				TableCellRenderer defaultRenderer = jTable1.getDefaultRenderer(tableModel.getColumnClass(i));
				comp = defaultRenderer.getTableCellRendererComponent(jTable1, 9.99999E-222,
																	 false, false, 0, i);
				int cellWidth = comp.getPreferredSize().width;
				int width = Math.max(cellWidth, headerWidth);
				column.setPreferredWidth((int) (width * 1.2));
				column.setMaxWidth((int) (width * 1.3));
			}
			if (columnNames[i].equals(HEADER_CLUSTER_FREQUENCY)) {
//				tcModel.getColumn(i).setPreferredWidth(85);
				Component comp = headerRenderer.getTableCellRendererComponent(jTable1, column.getHeaderValue(),
																			  false, false, 0, i);
				int headerWidth = comp.getPreferredSize().width;
				TableCellRenderer defaultRenderer = jTable1.getDefaultRenderer(tableModel.getColumnClass(i));
				comp = defaultRenderer.getTableCellRendererComponent(jTable1, "99999/99999  99.99%",
																			   false, false, 0, i);
				int cellWidth = comp.getPreferredSize().width;
				int width = Math.max(cellWidth, headerWidth);
				column.setPreferredWidth((int) (width * 1.2));
				column.setMaxWidth((int) (width * 1.3));
			}
			if (columnNames[i].equals(HEADER_TOTAL_FREQUENCY)) {
//				tcModel.getColumn(i).setPreferredWidth(85);
				Component comp = headerRenderer.getTableCellRendererComponent(jTable1, column.getHeaderValue(),
																			  false, false, 0, i);
				int headerWidth = comp.getPreferredSize().width;
				TableCellRenderer defaultRenderer = jTable1.getDefaultRenderer(tableModel.getColumnClass(i));
				comp = defaultRenderer.getTableCellRendererComponent(jTable1, "99999/99999  99.99%",
																	 false, false, 0, i);
				int cellWidth = comp.getPreferredSize().width;
				int width = Math.max(cellWidth, headerWidth);
				column.setPreferredWidth((int) (width * 1.2));
				column.setMaxWidth((int) (width * 1.3));
			}
			if (columnNames[i].equals(HEADER_GENES)) {
//				tcModel.getColumn(i).setPreferredWidth((screenSize.width - 15 - 50 - 85 - 85 - 70 - 70 - 40) / 3);
//				tcModel.getColumn(i).setPreferredWidth(500);
			}
		}
		jTable1.getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() < 2) {
					return;
				}
				int tableColumn = jTable1.columnAtPoint(e.getPoint());
				int modelColumn = jTable1.convertColumnIndexToModel(tableColumn);
				sortedTableModel.sort(modelColumn);
			}
		});
		// ////////////////////////////////////////////////les environs 40
		// lignes precedentes peut etre a virer

		// jPanelTableau = new javax.swing.JPanel();
		// jPanelTableau.add(jTable1);
		jScrollPane = new javax.swing.JScrollPane(jTable1);

		// couleur
		TableColumn coloredColumn = jTable1.getColumnModel().getColumn(DESCRIPTION_COLUMN);
//		coloredColumn.setCellRenderer(new ColorRenderer(false, goColor, this));
		// suite des couleurs dans :ouseT1Handler column2;

		jPanelDeBase.add(jScrollPane);
            

                // ////////a faire : utiliser le meme listener pour tout les tabs
                // displayPieChartListener = new DisplayPieChart2(this,
                // this.goBin.getNetwork_Options());

                JPanel jPanelApplyButtons = new JPanel();
                GridBagLayout blayout = new GridBagLayout();
                GridBagConstraints constr = new GridBagConstraints();
                jPanelApplyButtons.setLayout(blayout);
                constr.weightx = 0;

                JButton selectAllButton = new JButton();
                selectAllButton.setText("Select All");
                selectAllButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        selectAll();
                    }
                });
                jPanelApplyButtons.add(selectAllButton, constr);
                constr.weightx = 100;
                constr.anchor = constr.WEST;

                JButton unSelectAllButton = new JButton();
                unSelectAllButton.setText("Unselect All");
                unSelectAllButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        	unselectAll();
                    }
                });
                jPanelApplyButtons.add(unSelectAllButton, constr);

                /*
                * jButton1 = new JButton(); jButton1.setText("Validate");
                * jButton1.addActionListener(displayPieChartListener); ////// 1
                * constr.weightx=0; jPanelApplyButtons.add(jButton1,constr);
                */

                // jButton2 = new JButton();
                // jButton2.setText("Recalculate");
                // jButton2.addActionListener(displayPieChartListener);
                // jPanelApplyButtons.add(jButton2);

                /*
                * jButton3 = new JButton(); jButton3.setText("Reset");
                * jButton3.addActionListener(displayPieChartListener);
                * jPanelApplyButtons.add(jButton3,constr);
                */

                constr.weightx = 100;
                constr.anchor = constr.EAST;
                JButton jButton4 = new JButton("Select nodes");
                jButton4.addActionListener(new ZSelectNodes((ResultAndStartPanel) this, adapter));
                jPanelApplyButtons.add(jButton4, constr);

                jPanelDeBase.add(jPanelApplyButtons, java.awt.BorderLayout.SOUTH);

                // jPanelApplyButtons.add(new
                // JLabel("WeightGo"),java.awt.BorderLayout.SOUTH);
                // final JTextField jTextWeightGo = new JTextField(5);
                // jPanelApplyButtons.add(jTextWeightGo);

                // jPanelApplyButtons.add(new
                // JLabel("krep"),java.awt.BorderLayout.SOUTH);
                // final JTextField jTextKrep = new JTextField(5);

                // jPanelApplyButtons.add(jTextKrep);
                // jPanelApplyButtons.add(new
                // JLabel("katt"),java.awt.BorderLayout.SOUTH);
                // final JTextField jTextKatt = new JTextField(5);
                // jPanelApplyButtons.add(jTextKatt);
                // jPanelApplyButtons.add(new JLabel("Iterations"));
                // final JTextField jTextK = new JTextField(5);
                // jPanelApplyButtons.add(jTextK);
                // jPanelApplyButtons.add(new JLabel("Temperature"));
                // final JTextField jTextL = new JTextField(5);
                // jPanelApplyButtons.add(jTextL);
                // JButton jButton5 = new JButton("Layout");
                // jButton5.addActionListener(new ActionListener(){
                // public void actionPerformed(ActionEvent e){
                // new
                // Fruchterman(Cytoscape.getCurrentNetworkView(),jTextWeightGo.getText(),jTextKrep.getText(),jTextKatt.getText(),
                // jTextK.getText(),jTextL.getText());
                // }
                // });
                // jPanelApplyButtons.add(jButton5);
                // JButton jButton6 = new JButton("Layout2");
                // jButton6.addActionListener(new ActionListener(){
                // public void actionPerformed(ActionEvent e){
                // new
                // FruchtermanTheEnd(Cytoscape.getCurrentNetworkView(),jTextWeightGo.getText(),jTextKrep.getText(),jTextKatt.getText(),
                // jTextK.getText(),jTextL.getText());
                // }
                // });
                // jPanelApplyButtons.add(jButton6);
                jPanelDeBase.add(jPanelApplyButtons, java.awt.BorderLayout.SOUTH);
            }
                this.add(jPanelDeBase, java.awt.BorderLayout.CENTER);
                
            if(!(data.length == 0)){
                goBin.synchroColor(this);
                goBin.synchroSelections(this);
            }

	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public Annotation getAnnotation(String term) {
		return annotation;
	}

	public Ontology getOntology() {
		return ontology;
	}

	public Ontology getOntology(String term) {
		return ontology;
	}

	public int getSelectColumn() {
		return this.SELECT_COLUMN;
	}

	public int getGoTermColumn() {
		return this.GO_TERM_COLUMN;
	}

	public int getDescriptionColumn() {
		return this.DESCRIPTION_COLUMN;
	}

	public JTable getJTable() {
		return this.jTable1;
	}

	public boolean isSelected(String term) {
		for (int i = 0; i < jTable1.getRowCount(); i++) {
			if (((Boolean) jTable1.getValueAt(i, this.getSelectColumn())).booleanValue()
					&& ((String) jTable1.getValueAt(i, this.getGoTermColumn())) == term)
				return true;

		}
		return false;
	}

	public void unselectAll() {
		for (int i = 0; i < jTable1.getRowCount(); i++) {
			jTable1.setValueAt(new Boolean(false), i, this.getSelectColumn());
		}
	}

	public void selectAll() {
		for (int i = 0; i < jTable1.getRowCount(); i++) {
			jTable1.setValueAt(new Boolean(true), i, this.getSelectColumn());
		}
	}

	public boolean unselect(String term) {
		for (int i = 0; i < jTable1.getRowCount(); i++) {
			if (((Boolean) jTable1.getValueAt(i, this.getSelectColumn())).booleanValue()
					&& ((String) jTable1.getValueAt(i, this.getGoTermColumn())) == term)
				jTable1.setValueAt(new Boolean(true), i, this.getSelectColumn());
			return true;

		}
		return false;
	}

	public boolean select(String term) {
		for (int i = 0; i < jTable1.getRowCount(); i++) {
			if (!((Boolean) jTable1.getValueAt(i, this.getSelectColumn())).booleanValue()
					&& ((String) jTable1.getValueAt(i, this.getGoTermColumn())) == term)
				jTable1.setValueAt(new Boolean(false), i, this.getSelectColumn());
			return true;

		}
		return false;
	}

	private Object[][] makeDataForJTable(int nb_columns) {

		// orden GO labels by increasing corrected p-value or increasing smallX

		Object[][] data;
		ArrayList dataList = new ArrayList();
		int j = 0; // c'est le nombre de lignes du tableau = le nombre de GO

		// on rempli le hashMap annotatedGenes (cle = termID = IDGO) avec des
		// valeurs = hashset de string (=nom des genes ayant cette annotation
		// GO);
		Iterator it2 = selectedCanonicalNameVector.iterator();
		while (it2.hasNext()) {
			String name = it2.next() + "";
			Set tmp = alias.get(name);
			if (tmp != null) {
				Iterator it = tmp.iterator();
				while (it.hasNext()) {
					int[] nodeClassifications = annotation.getClassifications(it.next() + "");
					for (int k = 0; k < nodeClassifications.length; k++) {
						String cat = new Integer(nodeClassifications[k]).toString();
						if (!annotatedGenes.containsKey(cat)) {
							HashSet catset = new HashSet();
							annotatedGenes.put(cat, catset);
						}
						((HashSet) annotatedGenes.get(cat)).add(name);
					}
				}
			}
		}

		HashSet keySet;
		if (!testString.equals(NONE)) {
			keySet = new HashSet(testMap.keySet());
		}
		// /////// au cas ou on decide de ne pas utiliser de test (pas tres
		// utile !) :
		else {
			keySet = new HashSet(mapSmallX.keySet());
		}
		// ///// fin du truc inutile

		Iterator it = keySet.iterator();
		String[] keyLabels = new String[keySet.size()];
		for (int i = 0; it.hasNext(); i++) {
			keyLabels[i] = it.next().toString();
		}
		String[] ordenedKeySet;
		if (!testString.equals(NONE)) {
			ordenedKeySet = ordenKeysByPvalues(keyLabels);
		}
		// ///// encore un truc inutile :
		else {
			ordenedKeySet = ordenKeysBySmallX(keyLabels);
		}
		// ///// re fin truc inutile

		boolean ok = true;

		for (int i = 0; (i < ordenedKeySet.length) && (ok == true); i++) {

			String termID = ordenedKeySet[i];
			String pvalue = "";
			String correctedPvalue = "";
			String smallX;
			String smallN;
			String bigX;
			String bigN;
			String description;
			// pvalue

			if (!testString.equals(NONE)) {
				try {
					pvalue = SignificantFigures.sci_format(testMap.get(new Integer(termID)).toString(), 5);

					/*
					 * double debut =
					 * Double.parseDouble(pvalue.substring(0,pvalue
					 * .indexOf("E"))); debut =
					 * ((double)Math.round(debut*10))/10.0; pvalue =
					 * Double.toString
					 * (debut)+" "+pvalue.substring(pvalue.indexOf("E"));
					 */

				} catch (Exception e) {
					pvalue = "N/A";
				}
			}
			// /////ON PEUT TOUT VIRER A PARTIR DE LA JUSQU'A.... (cf plus bas)
			else {
				pvalue = "N/A";
			}
			// ///// la
			if (!correctionString.equals(NONE)) {
				try {
					correctedPvalue = SignificantFigures.sci_format(correctionMap.get(termID).toString(), 5);
					// System.out.println(correctedPvalue);
					/*
					 * double debut =
					 * Double.parseDouble(correctedPvalue.substring
					 * (0,correctedPvalue.indexOf("E"))); debut =
					 * ((double)Math.round(debut*10))/10.0; correctedPvalue =
					 * Double.toString(debut)+" "+correctedPvalue.substring(
					 * correctedPvalue.indexOf("E"));
					 */

				} catch (Exception e) {
					correctedPvalue = "N/A";
				}

			}
			// ////// au cas ou on n'a pas utilise correction de la P-value : a
			// virer a mon avis
			else {
				correctedPvalue = "N/A";
			}
			// ///// fin de truc inutile
			try {
				smallX = mapSmallX.get(new Integer(termID)).toString();
			} catch (Exception e) {
				smallX = "N/A";
			}
			// n
			try {
				smallN = mapSmallN.get(new Integer(termID)).toString();
			} catch (Exception e) {
				smallN = "N/A";
			}
			try {
				bigX = mapBigX.get(new Integer(termID)).toString();
			} catch (Exception e) {
				bigX = "N/A";
			}
			// n
			try {
				bigN = mapBigN.get(new Integer(termID)).toString();
			} catch (Exception e) {
				bigN = "N/A";
			}
			// name
			try {
				description = ontology.getTerm(Integer.parseInt(termID)).getName();
			} catch (Exception e) {
				description = "?";
			}

			int percentmp = (Integer.parseInt(smallX) * 1000) / Integer.parseInt(bigX);

			int percentmp2 = (Integer.parseInt(smallN) * 1000) / Integer.parseInt(bigN);
			double percentClusterFreq = (double) percentmp / 10;
			double percentTotalFreq = (double) percentmp2 / 10;

			StringBuffer genesAnnotated = new StringBuffer();

			if (testString.equals(NONE)) {

				Object[] dataTmp = new Object[6];
				dataTmp[0] = new Boolean(false);
				dataTmp[1] = termID;
//				dataTmp[2] = new JLabel(description);
				dataTmp[2] = description;
				dataTmp[3] = smallX + "/" + bigX + " " + percentClusterFreq;
				dataTmp[4] = smallN + "/" + bigN + " " + percentTotalFreq;

				if (annotatedGenes.containsKey(termID)) {
					Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
					while (k.hasNext()) {
						genesAnnotated.append(k.next().toString());
						if (k.hasNext()) {
							genesAnnotated.append(" ");
						}

					}
				}
				dataTmp[5] = genesAnnotated.toString();
				dataList.add(dataTmp);
				j++;
				this.goColor.put(termID, Color.getColor("white"));

				// output.write("\n") ;
			} else if (correctionString.equals(NONE)) {
				if (catString.equals(CATEGORY_BEFORE_CORRECTION)) {

					if ((new BigDecimal(testMap.get(new Integer(ordenedKeySet[i])).toString()))
							.compareTo(new BigDecimal(alphaString)) < 0) {

						Object[] dataTmp = new Object[7];
						dataTmp[0] = new Boolean(false);
						dataTmp[1] = termID;
						dataTmp[3] = pvalue;
//						dataTmp[2] = new JLabel(description);
						dataTmp[2] = description;
						dataTmp[4] = smallX + "/" + bigX + "  " + percentClusterFreq + "%";
						dataTmp[5] = smallN + "/" + bigN + "  " + percentTotalFreq + "%";

						if (annotatedGenes.containsKey(termID)) {
							Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
							while (k.hasNext()) {
								genesAnnotated.append(k.next().toString());
								if (k.hasNext()) {
									genesAnnotated.append(" ");
								}

							}
						}
						dataTmp[6] = genesAnnotated.toString();
						dataList.add(dataTmp);
						j++;
						this.goColor.put(termID, Color.getColor("white"));
					} else {
						ok = false;
					}
				} else { // /INUTILE : "pas correction" + "apres correction"
							// =impossible
							// output.write(termID + "\t" + pvalue + "\t" +
							// smallX + "\t" + smallN + "\t" + description +
							// "\t");
					if (annotatedGenes.containsKey(termID)) {
						Object[] dataTmp = new Object[7];
						dataTmp[0] = new Boolean(false);
						dataTmp[1] = termID;
						dataTmp[3] = pvalue;
//						dataTmp[2] = new JLabel(description);
						dataTmp[2] = description;
						dataTmp[4] = smallX + "/" + bigX + "  " + percentClusterFreq + "%";
						dataTmp[5] = smallN + "/" + bigN + "  " + percentTotalFreq + "%";

						Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
						while (k.hasNext()) {
							genesAnnotated.append(k.next().toString());
							if (k.hasNext()) {
								genesAnnotated.append(" ");
							}

						}
						dataTmp[6] = genesAnnotated.toString();
						dataList.add(dataTmp);
						j++;
						this.goColor.put(termID, Color.getColor("white"));
					}
				}
			}

			else { // /SI TEST=true + CORRECTION=true
				if (catString.equals(CATEGORY_CORRECTION)) { // /SI TEST=true +
																// CORRECTION=true
																// +
																// AFTERCORRECTION

					if ((new BigDecimal(correctionMap.get(ordenedKeySet[i]).toString())).compareTo(new BigDecimal(
							alphaString)) < 0) {

						Object[] dataTmp = new Object[8];
						dataTmp[0] = new Boolean(false);
						dataTmp[1] = termID;
						dataTmp[3] = pvalue;
//						dataTmp[2] = new JLabel(description);
						dataTmp[2] = description;
						dataTmp[4] = correctedPvalue;
						dataTmp[5] = smallX + "/" + bigX + "  " + percentClusterFreq + "%";
						dataTmp[6] = smallN + "/" + bigN + "  " + percentTotalFreq + "%";

						if (annotatedGenes.containsKey(termID)) {
							Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
							while (k.hasNext()) {
								genesAnnotated.append(k.next().toString());
								if (k.hasNext()) {
									genesAnnotated.append(" ");
								}
							}
						}
						dataTmp[7] = genesAnnotated.toString();
						dataList.add(dataTmp);
						j++;
						this.goColor.put(termID, Color.getColor("white"));

					} else {
						ok = false;
					}
				}

				else if (catString.equals(CATEGORY_BEFORE_CORRECTION)) {// /SI
																		// TEST=true
																		// +
																		// CORRECTION=true
																		// +
																		// BEFORECORRECTION
					if ((new BigDecimal(testMap.get(new Integer(ordenedKeySet[i])).toString()))
							.compareTo(new BigDecimal(alphaString)) < 0) {
						Object[] dataTmp = new Object[8];
						dataTmp[0] = new Boolean(false);
						dataTmp[1] = termID;
						dataTmp[3] = pvalue;
//						dataTmp[2] = new JLabel(description);
						dataTmp[2] = description;
						dataTmp[4] = correctedPvalue;
						dataTmp[5] = smallX + "/" + bigX + "  " + percentClusterFreq + "%";
						dataTmp[6] = smallN + "/" + bigN + "  " + percentTotalFreq + "%";

						if (annotatedGenes.containsKey(termID)) {
							Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
							while (k.hasNext()) {
								genesAnnotated.append(k.next().toString());
								if (k.hasNext()) {
									genesAnnotated.append(" ");
								}

							}
						}
						dataTmp[7] = genesAnnotated.toString();
						dataList.add(dataTmp);
						j++;
						this.goColor.put(termID, Color.getColor("white"));
					} else {
						ok = false;
					}

				} else {// /SI TEST=true + CORRECTION=true +
						// AFTERBEFORECORRECTION="---" =>on s'en fout d'alpha
					Object[] dataTmp = new Object[8];
					dataTmp[0] = new Boolean(false);
					dataTmp[1] = termID;
					dataTmp[3] = pvalue;
//					dataTmp[2] = new JLabel(description);
					dataTmp[2] = description;
					dataTmp[4] = correctedPvalue;
					dataTmp[5] = smallX + "/" + bigX + "  " + percentClusterFreq + "%";
					dataTmp[6] = smallN + "/" + bigN + "  " + percentTotalFreq + "%";

					if (annotatedGenes.containsKey(termID)) {
						Iterator k = ((HashSet) annotatedGenes.get(termID)).iterator();
						while (k.hasNext()) {
							genesAnnotated.append(k.next().toString());
							if (k.hasNext()) {
								genesAnnotated.append(" ");
							}

						}
					}
					dataTmp[7] = genesAnnotated.toString();
					dataList.add(dataTmp);
					j++;
					this.goColor.put(termID, Color.getColor("white"));

				}
			}
		}

		// //// remplissage du tableau qui sera donne au modele de swing.table
		Iterator iter = dataList.listIterator();
		data = new Object[j][nb_columns];

		for (int i = 0; i < j; i++) {// le "+1" utile que pour combobox choix
										// SGD amigo
			data[i] = (Object[]) iter.next();

		}
		return data;

	}

	private Object[] makeHeadersForJTable() {

		// String header; pour plus tard
		int j = 1;

		Object[] header;
		// Headers

		// jTable1.getTableHeader().getTable().setCellEditor(new
		// DefaultCellEditor(comboGOID));

		if (testString.equals(NONE)) {
			header = new Object[6];

			header[1] = HEADER_GO_ID;
			header[2] = HEADER_GO_DESCRIPTION;
			header[3] = HEADER_CLUSTER_FREQUENCY;
			header[4] = HEADER_TOTAL_FREQUENCY;
			header[5] = HEADER_GENES;
		} else if (correctionString.equals(NONE)) {
			header = new Object[7];

			header[1] = HEADER_GO_ID;
			header[2] = HEADER_GO_DESCRIPTION;
			header[3] = HEADER_P_VAL;
			header[4] = HEADER_CLUSTER_FREQUENCY;
			header[5] = HEADER_TOTAL_FREQUENCY;
			header[6] = HEADER_GENES;
		} else {
			header = new Object[8];

			header[1] = HEADER_GO_ID;
			header[2] = HEADER_GO_DESCRIPTION;
			header[3] = HEADER_P_VAL;
			header[4] = HEADER_CORRECTED_P_VAL;
			header[5] = HEADER_CLUSTER_FREQUENCY;
			header[6] = HEADER_TOTAL_FREQUENCY;
			header[7] = HEADER_GENES;
		}

		header[0] = " ";
		return header;

	}

	public String[] ordenKeysByPvalues(String[] labels) {

		for (int i = 1; i < labels.length; i++) {
			int j = i;
			// get the first unsorted value ...
			String insert_label = labels[i];
			BigDecimal val = new BigDecimal(testMap.get(new Integer(labels[i])).toString());
			// ... and insert it among the sorted
			while ((j > 0) && (val.compareTo(new BigDecimal(testMap.get(new Integer(labels[j - 1])).toString())) < 0)) {
				labels[j] = labels[j - 1];
				j--;
			}
			// reinsert value
			labels[j] = insert_label;
		}
		return labels;
	}

	public String[] ordenKeysBySmallX(String[] labels) {

		for (int i = 1; i < labels.length; i++) {
			int j = i;
			// get the first unsorted value ...
			String insert_label = labels[i];
			BigDecimal val = new BigDecimal(mapSmallX.get(new Integer(labels[i])).toString());
			// ... and insert it among the sorted
			while ((j > 0) && (val.compareTo(new BigDecimal(mapSmallX.get(new Integer(labels[j - 1])).toString())) > 0)) {
				labels[j] = labels[j - 1];
				j--;
			}
			// reinsert value
			labels[j] = insert_label;
		}
		return labels;
	}

	private class MouseT1Handler extends MouseAdapter {
		// /Listeners for table links
		Point point;
		String urlGO = "http://godatabase.org/cgi-bin/go.cgi?view=details&depth=1&query=";
		String urlSGD = "http://db.yeastgenome.org/cgi-bin/GO/go.pl?goid=";
		ResultPanel resultPanel;

		public MouseT1Handler(ResultPanel resultPane) {
			super();
			this.resultPanel = resultPane;
			// /Browser.getDialogPanel(); a voir ce truc peut etre bien pour pas
			// nouvelle fenetre a chque fois ???
		}

		public void mouseClicked(MouseEvent ev) {
			point = ev.getPoint();

			int tableColumn = jTable1.columnAtPoint(ev.getPoint());
			// int modelColumn = jTable1.convertColumnIndexToModel(tableColumn);
			int tableRow = jTable1.rowAtPoint(ev.getPoint());
			// int modelRow = jTable1.convertColumnIndexToModel(tableRow);

			// listen to column "go term"
			if (/* jTable1.columnAtPoint(point) */tableColumn == resultPanel.GO_TERM_COLUMN) {

				// ligne en dessous a arevoir je ne la comprends pas trop les
				// getmodifiers me donnent pas les valeurs que je veux!!!!!!!!!
				if (SwingUtilities.isLeftMouseButton(ev)) {
					String GOid = (String) jTable1.getValueAt(jTable1.rowAtPoint(point),/*
																						 * jTable1
																						 * .
																						 * columnAtPoint
																						 * (
																						 * point
																						 * )
																						 */tableColumn);

					try {

						Browser.init(); // /A REFAIRE CHOIX PAS PREVU + NOUVELLE
										// FENETRE A CHAQUE FOIS ??
						// if (jTable1.getColumnName(1)=="GO-ID : SGD")
						// Browser.displayURL(urlSGD+GOid);
						// else
						Browser.displayURL(urlGO + GOid);

					} catch (IOException ee) {
						JOptionPane.showMessageDialog(jPanelTableau, "Could not open website :" + ee);
					}
				}
			}

			// //DE LA COULEUR !!!!!
			// if (tableColumn==resultPanel.DESCRIPTION_COLUMN){
			/*
			 * //j'aimerais bien pouvoir faire un choix de couleurs automatique
			 * mais ca couille : //ca reconnais pas les clicks droits des clicks
			 * gauches boolean automaticReussi=false; Color
			 * previousColor=(Color)
			 * goColor.get(jTable1.getValueAt(jTable1.rowAtPoint
			 * (point),resultPanel.GO_TERM_COLUMN));
			 */
			// String
			// term=(String)jTable1.getValueAt(jTable1.rowAtPoint(point),resultPanel.getGoTermColumn());
			// if ((ev.getModifiersEx()&InputEvent.BUTTON1_DOWN_MASK) !=0 ){
			/*
			 * if (previousColor!=null){
			 * 
			 * ((JLabel)jTable1.getValueAt(jTable1.rowAtPoint(point),resultPanel.
			 * DESCRIPTION_COLUMN)).setBackground(null);
			 * ((JLabel)jTable1.getValueAt
			 * (jTable1.rowAtPoint(point),resultPanel.
			 * DESCRIPTION_COLUMN)).revalidate();
			 * goBin.freeAutomaticColor(term); goBin.goColor.remove(term);
			 * 
			 * jTable1.validate(); //goBin.repaint(); goBin.synchroColor();
			 * automaticReussi=true; } else { Color newColor =
			 * goBin.getNextAutomaticColor(term); if (newColor !=null){
			 * ((JLabel)
			 * jTable1.getValueAt(jTable1.rowAtPoint(point),resultPanel.
			 * DESCRIPTION_COLUMN)).setBackground(newColor);
			 * ((JLabel)jTable1.getValueAt
			 * (jTable1.rowAtPoint(point),resultPanel.
			 * DESCRIPTION_COLUMN)).revalidate();
			 * goBin.goColor.put(term,newColor);
			 * 
			 * jTable1.validate(); goBin.synchroColor(); automaticReussi=true; }
			 * }
			 * 
			 * //}
			 * 
			 * if (!automaticReussi){
			 */
			/*
			 * JColorChooser colorChooser = new JColorChooser();
			 * 
			 * Color newColor = JColorChooser.showDialog( jTable1,
			 * "Choose GO Color", (Color)goColor.get(term) );
			 * ((JLabel)jTable1.getValueAt
			 * (jTable1.rowAtPoint(point),resultPanel.
			 * DESCRIPTION_COLUMN)).setBackground(newColor);
			 * //((JLabel)jTable1.getValueAt
			 * (jTable1.rowAtPoint(point),2)).setText(jTable1.getValueAt())
			 * goColor.put(term,newColor);
			 * goBin.getGoColor().put(jTable1.getValueAt
			 * (jTable1.rowAtPoint(point),ResultPanel.GO_TERM_COLUMN),newColor);
			 * jTable1.validate(); resultPanel.goBin.synchroColor();
			 */
			// }
			// jTable1.

			// goBin.repaint();

			// }

			// Listen to column "gene machined to term"
			if (jTable1.columnAtPoint(point) == columnNames.length - 1) {
				if (SwingUtilities.isRightMouseButton(ev)) {
					String[] genes = ((String) jTable1.getValueAt(jTable1.rowAtPoint(point), tableColumn/*
																										 * jTable1
																										 * .
																										 * columnAtPoint
																										 * (
																										 * point
																										 * )
																										 */))
							.split(" ");
					JPopupMenu pop = new JPopupMenu();
					JMenu jm = new JMenu("yeast genome");

					/*
					 * 
					 * JFrame PopupFrame = new JFrame();
					 * 
					 * JScrollPane jScrollMenu = new
					 * JScrollPane();//////testpeut etre vire si marche pas
					 * jm.add(jScrollMenu);///////////// suite test
					 * jScrollMenu.setMaximumSize(new Dimension(200,50));/////
					 */

					// /*
					for (int i = 0; i < genes.length; i++) {
						JMenuItem jmi = jm.add(genes[i]);

						jmi.addActionListener(new ActionListener() {

							public void actionPerformed(ActionEvent ev) {
								try {
									Browser.init(); // /A REFAIRE CHOIX PAS
													// PREVU + NOUVELLE FENETRE
													// A CHAQUE FOIS ??
									Browser.displayURL("http://db.yeastGenome.org/cgi-bin/locus.pl?locus="
											+ ((JMenuItem) (ev.getSource())).getText());
								} catch (IOException ee) {
									JOptionPane.showMessageDialog(jPanelTableau, "Could not open website :" + ee);
								}
							}
						});
					}
					// */
					pop.add(jm);
					pop.show(jTable1, point.x - pop.getSize().width, point.y);

				}

			}
		}
	}

	private class MouseMotionT1Handler extends MouseMotionAdapter {
		public void mouseMoved(MouseEvent ev) {
			if (jTable1.columnAtPoint(ev.getPoint()) == ResultPanel.GO_TERM_COLUMN) {
				jTable1.setCursor(hand);
			} else {
				jTable1.setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	public CyNetworkView getNetworkView() {
		return originalNetworkView;
	}

	public Map getAlias() {
		return alias;
	}

	public GoBin getGoBin() {
		return goBin;
	}

	public void setTabName(String name) {
		this.fileName = name;
	}

	public String getTabName() {
		return this.fileName;

	}

}
