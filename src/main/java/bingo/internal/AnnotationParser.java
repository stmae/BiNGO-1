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
 * * Date: Apr.11.2005
 * * Description: Class that parses the annotation files in function of the chosen ontology.         
 **/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import org.cytoscape.work.TaskMonitor;

import bingo.internal.ontology.Annotation;
import bingo.internal.ontology.Ontology;
import bingo.internal.ontology.OntologyTerm;


/**
 * ************************************************************
 * AnnotationParser.java --------------------------
 * <p/>
 * Steven Maere (c) April 2005
 * <p/>
 * Class that parses the annotation files in function of the chosen ontology.
 * *************************************************************
 */

public class AnnotationParser extends BingoTask {

	/**
	 * Enum to designate expected annotation file types/formats and versions
	 */
	private enum AnnotationFileType {
		GAF_2_0,
		GAF_2_1,
                GAF_2_2,
		GAF_UNKNOWN_VERSION,
		GAF_UNSUPPORTED_VERSION,
		GENE_ASSOCIATION_FILE,
		FLAT_FILE,
		UNKNOWN
	}

	/**
	 * string with path to some GO structure files.
	 */
	private String fullGoPath;
	private String processGoPath;
	private String functionGoPath;
	private String componentGoPath;

	/**
	 * annotation and ontology
	 */
	private Annotation annotation;
	private Annotation parsedAnnotation;
	private Ontology ontology;
	private Map<String, HashSet<String>> alias;

	/**
	 * full ontology which is used for remapping the annotations to one of the
	 * default ontologies (not for custom ontologies)
	 */
	private Ontology fullOntology;
	private Map synonymHash;

	private BingoParameters params;
	private Set<String> genes;
	/**
	 * boolean loading/parsing correctly ?
	 */
	private boolean parsingStatusOK = false;
	/**
	 * true if found annotation categories which are not in ontology
	 */
	private boolean orphansFound = false;
	/**
	 * false if none of the categories in the annotation match the ontology
	 */
	private boolean annotationConsistent = false;

	private String warningMessage = "";

	// Keep track of progress for monitoring:
	//private int maxValue;

	private Set<Integer> parentsSet;


	public AnnotationParser(BingoParameters params, HashSet<String> genes) {
		this.params = params;
		this.genes = genes;

		this.fullGoPath = openResourceFile("GO_Full");
		this.processGoPath = openResourceFile("GO_Biological_Process");
		this.functionGoPath = openResourceFile("GO_Molecular_Function");
		this.componentGoPath = openResourceFile("GO_Cellular_Component");

	//	this.maxValue = -1;
	}
	
	public AnnotationParser(BingoParameters params, HashSet<String> genes, TaskMonitor taskMonitor) {
		this(params, genes);
		this.taskMonitor = taskMonitor;
	}

	private String openResourceFile(String name) {
		return getClass().getResource("/data/" + name).toString();
	}

	/**
	 * method that governs loading and remapping of annotation files
	 *
     * @throws Exception if an error occurs during reading/parsing of the files
	 */
	public void calculate() throws Exception {
		if (taskMonitor != null) {
			taskMonitor.setTitle("Parsing Annotation");
		}

		parsingStatusOK = false;
		warningMessage = "";

        if (params.isOntology_default()) {
            // load full ontology for full remap to GOSlim ontologies, and for
            // defining synonymHash
            System.out.println("parsing default ontology");
            loadFullOntology();

                        loadDefaultOntology();

			loadAnnotation();

			// full remap not needed for non-Slim ontologies,
			// instead custom remap
			// bug 20/9/2005 changed annotationPanel to
			// ontologyPanel
			if (params.getOntologyFile().equals(fullGoPath) ||
				params.getOntologyFile().equals(processGoPath) ||
				params.getOntologyFile().equals(functionGoPath) ||
				params.getOntologyFile().equals(componentGoPath))
			{
				parsedAnnotation = customRemap(annotation, ontology, genes);
			}
			// full remap for Slim Ontologies
			else {
				parsedAnnotation = remap(annotation, ontology, genes);
			}
        } else {
            // always perform full remap for .obo files, allows definition of
            // custom GOSlims
            System.out.println("parsing custom ontology");
            if (params.getOntologyFile().toLowerCase().endsWith(".obo")) {
                loadFullOntology();
            }

			loadCustomOntology();

			loadAnnotation();

			if (params.getOntologyFile().toLowerCase().endsWith(".obo")) {
				parsedAnnotation = remap(annotation, ontology, genes);
			} else {
				parsedAnnotation = customRemap(annotation, ontology, genes);
			}
        }

		parsingStatusOK = true;
	}

    private void loadFullOntology() throws Exception {
        taskMonitor.setStatusMessage("loading full ontology");
        setFullOntology();

		// check for cycles
		checkOntology(fullOntology);
    }

    private void loadDefaultOntology() throws Exception {
        taskMonitor.setStatusMessage("loading default ontology");
        setDefaultOntology(synonymHash);

		// check for cycles
		checkOntology(ontology);
    }

    private void loadCustomOntology() throws Exception {
        taskMonitor.setStatusMessage("loading custom ontology");
        setCustomOntology();

        // check for cycles
		checkOntology(ontology);
    }

    private void loadAnnotation() throws Exception {

        if (params.isAnnotation_default()) {
            taskMonitor.setStatusMessage("loading default annotation");
            setDefaultAnnotation();
        } else {
            taskMonitor.setStatusMessage("loading custom annotation");
            setCustomAnnotation();
		}

        if (!annotationConsistent) {
            throw new Exception("None of the labels in your annotation match with the \n" +
								"chosen ontology, please check their compatibility.");
        }
    }

    /**
	 * Method that parses the custom annotation file into an annotation-object.
	 *
	 * @throws Exception if an error occurs during reading/parsing the file
	 */
	private void setCustomAnnotation() throws Exception {

		annotation = null;

		String filePath = params.getAnnotationFile();
		AnnotationFileType annotationFileType;
		try {
			annotationFileType = getAnnotationFileType(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new Exception("Could not load annotation file " + filePath + ":\n" +
								"file not found.", e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Could not load annotation file " + filePath + ":\n" +
								"file could not be read or parsed.", e);
		}

        switch (annotationFileType) {
			case GAF_UNKNOWN_VERSION:
                warningMessage = "GAF file with unknown format version detected, proceed with caution!";
                taskMonitor.setStatusMessage(warningMessage);
				// intended fall through
			case GAF_2_0:				// intended fall through
			case GAF_2_1:				// intended fall through   
			case GENE_ASSOCIATION_FILE:
				// GO Consortium annotation files (GAF 2.1 or older gene association file)
				try {
					BiNGOConsortiumAnnotationReader readerAnnotation = new BiNGOConsortiumAnnotationReader(filePath,
							synonymHash, params, "Consortium", "GO");
					annotation = readerAnnotation.getAnnotation();
					if (readerAnnotation.getOrphans()) {
						orphansFound = true;
					}
					if (readerAnnotation.getConsistency()) {
						annotationConsistent = true;
					}
					alias = readerAnnotation.getAlias();
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("Could not load annotation file " + filePath + ":\n" +
										"file could not be read or parsed.", e);
				}
				break;
                        case GAF_2_2:				
                                // GO Consortium annotation files (GAF 2.2)
				try {
					BiNGOGaf22Reader readerAnnotation = new BiNGOGaf22Reader(filePath,
							synonymHash, params, "Consortium", "GO");
					annotation = readerAnnotation.getAnnotation();
					if (readerAnnotation.getOrphans()) {
						orphansFound = true;
					}
					if (readerAnnotation.getConsistency()) {
						annotationConsistent = true;
					}
					alias = readerAnnotation.getAlias();
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("Could not load annotation file " + filePath + ":\n" +
										"file could not be read or parsed.", e);
				}
				break;                            
			case FLAT_FILE:
				// flat file reader for custom annotation
				try {
					BiNGOAnnotationFlatFileReader readerAnnotation = new BiNGOAnnotationFlatFileReader(filePath,
							synonymHash);
					annotation = readerAnnotation.getAnnotation();
					if (readerAnnotation.getOrphans()) {
						orphansFound = true;
					}
					if (readerAnnotation.getConsistency()) {
						annotationConsistent = true;
					}
					alias = readerAnnotation.getAlias();
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("Could not load annotation file " + filePath + ":\n" +
										"file could not be read or parsed.", e);
				}
				break;

			case GAF_UNSUPPORTED_VERSION:
				// we do not support GAF versions older than 2.0
                // or future GAF versions by default
				throw new Exception("Could not load annotation file " + filePath + ":\n" +
									"unsupported old or new GAF format version detected.");

			case UNKNOWN:
				throw new Exception("Could not load annotation file " + filePath + ":\n" +
									"unrecognized annotation file format.");

			default:
				throw new AssertionError("Unknown annotationFileType:" + annotationFileType);
		}
	}

	/**
	 * Attempts to detect the file type of a given annotation file, by parsing
	 * the first line (or successive header or comment lines) for file type and
	 * version information (e.g., !gaf-version: 2.1), or based on file
	 * extension or name.
	 *
	 * @param filePath string with a full path to an annotation file
	 * @return <code><AnnotationFileType/code> detected for the given file
	 * @throws IOException if the first line of the file could not be read
	 */
	private AnnotationFileType getAnnotationFileType(String filePath) throws IOException {

		String currentLine;
		try (Scanner fileScanner = new Scanner(new File(filePath))) {
			if (!fileScanner.hasNextLine()) {
				throw new IOException("Annotation file error: could not read first line of file.");
			} else {
				boolean isCommentOrHeaderLine;
				do {
					currentLine = fileScanner.nextLine();
					if (currentLine.startsWith("!gaf-version: 2.0")) {
						taskMonitor.setStatusMessage("GAF 2.0 file detected: " + currentLine);
						return AnnotationFileType.GAF_2_0;
					} else if (currentLine.startsWith("!gaf-version: 2.1")) {
						taskMonitor.setStatusMessage("GAF 2.1 file detected: " + currentLine);
						return AnnotationFileType.GAF_2_1;
                                        } else if (currentLine.startsWith("!gaf-version: 2.2")) {
						taskMonitor.setStatusMessage("GAF 2.2 file detected: " + currentLine);
						return AnnotationFileType.GAF_2_2;
					} else if (currentLine.startsWith("!gaf-version: ")) {
						// we do not support old or future versions by default
						taskMonitor.setStatusMessage("GAF file with unsupported version detected: " + currentLine);
						return AnnotationFileType.GAF_UNSUPPORTED_VERSION;
					} else if (BiNGOAnnotationFlatFileReader.checkHeader(currentLine)) {
						taskMonitor.setStatusMessage("Flat-format annotation file detected: " + currentLine);
						return AnnotationFileType.FLAT_FILE;
					}
					// allow header/comment lines starting with '!' or '#'
					isCommentOrHeaderLine = (currentLine.startsWith("!") || currentLine.startsWith("#"));
					if (isCommentOrHeaderLine) {
						System.out.println("   header/comment line: " + currentLine);
					}
				} while (fileScanner.hasNextLine() && isCommentOrHeaderLine);
			}
		}

		if (filePath.toLowerCase().endsWith(".gaf")) {
			taskMonitor.setStatusMessage("GAF file detected by file extension with unknown version "
										 + "(no version header)");
			return AnnotationFileType.GAF_UNKNOWN_VERSION;
		} else if (filePath.toLowerCase().contains("gene_association")) {
			// kept for backward-compatibility reasons, this used to be the
			// (only) code to determine if the annotation file is a GO
			// Consortium gene association annotation files
			taskMonitor.setStatusMessage("gene_association file detected by file name");
			return AnnotationFileType.GENE_ASSOCIATION_FILE;
		} else {
			taskMonitor.setStatusMessage("could not detect file format, no known header or file name/extension");
			return AnnotationFileType.UNKNOWN;
		}
	}

	/**
	 * Method that parses the default annotation file into an annotation-object
	 * given the choice of ontology and term identifier.
	 *
	 * @throws Exception if an error occurs during reading/parsing the file
	 */
	private void setDefaultAnnotation() throws Exception {

		annotation = null;

		String filePath = params.getAnnotationFile();

		// flat file
		try {
			BiNGOAnnotationDefaultReader readerAnnotation = new BiNGOAnnotationDefaultReader(filePath, synonymHash,
					params, "default", "GO");
			annotation = readerAnnotation.getAnnotation();
			if (readerAnnotation.getOrphans()) {
				orphansFound = true;
			}
			if (readerAnnotation.getConsistency()) {
                            annotationConsistent = true;
			}
			alias = readerAnnotation.getAlias();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Could not load default annotation file " + filePath + ":\n" +
								"file could not be read or parsed.", e);
		}
	}

	/**
	 * Method that parses the ontology file into an ontology-object and returns
	 * a string containing whether the operation is correct or not.
	 *
	 * @throws Exception if an error occurs during reading/parsing the file
	 */
	private void setCustomOntology() throws Exception {

		ontology = null;

		String filePath = params.getOntologyFile();
		String namespace = params.getNameSpace();

		// obo file
		if (filePath.toLowerCase().endsWith(".obo")) {
			try {
				BiNGOOntologyOboReader readerOntology = new BiNGOOntologyOboReader(filePath, namespace);
				ontology = readerOntology.getOntology();
				if (ontology.size() == 0) {
					throw new Exception("Could not load ontology file " + filePath + ":\n" +
										"ontology seems empty.");
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Could not load ontology file " + filePath + ":\n" +
									"file could not be read or parsed.", e);
			}
		} else {
			synonymHash = null;
			// flat file.
			try {
				BiNGOOntologyFlatFileReader readerOntology = new BiNGOOntologyFlatFileReader(filePath);
				ontology = readerOntology.getOntology();
				synonymHash = readerOntology.getSynonymHash();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Could not load ontology file " + filePath + ":\n" +
									"file could not be read or parsed.", e);
			}
		}
	}

	/**
	 * Method that parses the default ontology file into an ontology-object
	 * (using the full Ontology synonymHash) and returns a string containing
	 * whether the operation is correct or not.
	 *
	 * @throws Exception if an error occurs during reading/parsing the file
	 */

	private void setDefaultOntology(Map synonymHash) throws Exception {

		ontology = null;

		String filePath = params.getOntologyFile();

		// flat file.
		try {
			BiNGOOntologyDefaultReader readerOntology = new BiNGOOntologyDefaultReader(filePath, synonymHash);
			ontology = readerOntology.getOntology();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Could not load default ontology file " + filePath + ":\n" +
								"file could not be read or parsed.", e);
		}

	}

	/**
	 * Method that parses the ontology file into an ontology-object and returns
	 * a string containing whether the operation is correct or not.
	 *
	 * @throws Exception if an error occurs during reading/parsing the file
	 */
	private void setFullOntology() throws Exception {

		fullOntology = null;
		synonymHash = null;

		String filePath = params.getOntologyFile();

		if (filePath.toLowerCase().endsWith(".obo")) {
			// read full ontology.
			try {
				BiNGOOntologyOboReader readerOntology = new BiNGOOntologyOboReader(filePath, BingoAlgorithm.NONE);
				fullOntology = readerOntology.getOntology();
				if (fullOntology.size() == 0) {
					throw new Exception("Could not load full ontology file " + filePath + ":\n" +
										"ontology seems empty.");
				}
				synonymHash = readerOntology.getSynonymHash();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Could not load full ontology file " + filePath + ":\n" +
									"file could not be read or parsed.", e);
			}
		} else {
			// read full ontology.
			try {
				BiNGOOntologyFlatFileReader readerOntology = new BiNGOOntologyFlatFileReader(fullGoPath);
				fullOntology = readerOntology.getOntology();
				synonymHash = readerOntology.getSynonymHash();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("Could not load full ontology file " + filePath + ":\n" +
									"file could not be read or parsed.", e);
			}
		}
	}

	private void checkOntology(Ontology ontology) throws Exception {
        taskMonitor.setStatusMessage("checking ontology for cycles");

		HashMap ontMap = ontology.getTerms();
		for (Object o : ontMap.keySet()) {
			parentsSet = new HashSet<>();
			int childNode = (Integer) o;
			up_go(childNode, childNode, ontology);
		}
	}

	/**
	 * method for remapping annotation to reduced ontology e.g. GOSlim, and
	 * explicitly including genes in all parental categories
	 * 
	 * @throws InterruptedException if task was cancelled
	 */

	private Annotation remap(Annotation annotation, Ontology ontology, Set<String> genes) throws InterruptedException {
		Annotation parsedAnnotation = new Annotation(annotation.getSpecies(), annotation.getType(),
                                                     annotation.getCurator());
		HashSet<String> ids = new HashSet<>();
		for (String gene : genes) {
			if (alias.get(gene) != null) {
				ids.addAll(alias.get(gene));
			}
		}

        HashMap annMap = annotation.getMap();
        Iterator it = annMap.keySet().iterator();
        //maxValue = annMap.keySet().size();
        //int onePercentOfMaxValue = maxValue / 100;
        //int currentProgress = 0;
        while (it.hasNext()) {
            //currentProgress++;
            // Update the Task Monitor.
            // This automatically updates the UI Component w/ progress bar.
            /*if (taskMonitor != null && (currentProgress == 1 || currentProgress % onePercentOfMaxValue == 0)) {
                taskMonitor.setStatusMessage("remapping " + currentProgress + " of " + maxValue);
                // Calculate percentage, must be a value between 0..1.
                double percentComplete = (double) currentProgress / maxValue;
                taskMonitor.setProgress(percentComplete);
            }
            */

			parentsSet = new HashSet<>();
			String node = it.next() + "";
			if (genes.size() == 0 || ids.contains(node)) {
				// array with go labels for gene it.next().
				int[] goIDs = annotation.getClassifications(node);
				for (int goID : goIDs) {
					if (ontology.getTerm(goID) != null) {
						parsedAnnotation.add(node, goID);
					}
					// all parent classes of GO class that node is assigned
					// to are also explicitly included in classifications
					// CHECK IF goID EXISTS IN fullOntology...
					if (fullOntology.getTerm(goID) != null) {
						up(node, goID, parsedAnnotation, ontology, fullOntology);
					} else {
						// System.out.println ("Orphan found " + goID) ;
						orphansFound = true;
					}
				}
			}
			if (cancelled) {
				throw new InterruptedException();
			}
		}
        if (taskMonitor != null)
            taskMonitor.setProgress(1.0);
		return parsedAnnotation;
	}

	/**
	 * method for explicitly including genes in custom annotation in all
	 * parental categories of custom ontology
	 * 
	 * @throws InterruptedException if task was cancelled
	 */

	private Annotation customRemap(Annotation annotation, Ontology ontology, Set<String> genes)
			throws InterruptedException
    {
		HashSet<String> ids = new HashSet<>();
		for (String gene : genes) {
			if (alias.get(gene) != null) {
				ids.addAll(alias.get(gene));
			}
		}

        Annotation parsedAnnotation = new Annotation(annotation.getSpecies(), annotation.getType(),
                                                     annotation.getCurator());
        HashMap annMap = annotation.getMap();
        Iterator it = annMap.keySet().iterator();
        //maxValue = annMap.keySet().size();
        //int onePercentOfMaxValue = maxValue / 100;
        //int currentProgress = 0;
        while (it.hasNext()) {
            //currentProgress++;
            // Update the Task Monitor.
            // This automatically updates the UI Component w/ progress bar.
            /*if (taskMonitor != null && (currentProgress == 1 || currentProgress % onePercentOfMaxValue == 0)) {
                taskMonitor.setStatusMessage("custom remapping " + currentProgress + " of " + maxValue);
                // Calculate percentage, must be a value between 0..1.
                double percentComplete = (double) currentProgress / maxValue;
                taskMonitor.setProgress(percentComplete);
            }*/

			parentsSet = new HashSet<>();
			String node = it.next() + "";
			if (genes.isEmpty() || ids.contains(node)) {
				// array with go labels for gene it.next().
				int[] goIDs = annotation.getClassifications(node);
				for (int goID : goIDs) {
					if (ontology.getTerm(goID) != null) {
						parsedAnnotation.add(node, goID);
						// 200905 NEXT LINE WITHIN LOOP <-> REMAP IN ORDER
						// TO AVOID TRYING TO PARSE LABELS NOT DEFINED IN
						// 'ONTOLOGY'...
						// all parent classes of GO class that node is
						// assigned to are also explicitly included in
						// classifications
						up(node, goID, parsedAnnotation, ontology, ontology);
					}
				}
			}
			if (cancelled) {
				throw new InterruptedException();
			}
		}
        if (taskMonitor != null)
            taskMonitor.setProgress(1.0);
		return parsedAnnotation;
	}

	/**
	 * method for recursion through tree to root
	 */

	private void up(String node, int id, Annotation parsedAnnotation, Ontology ontology, Ontology flOntology) {
		OntologyTerm child = flOntology.getTerm(id);
		int[] parents = child.getParentsAndContainers();
		for (int parent : parents) {
			if (!parentsSet.contains(parent)) {
				parentsSet.add(parent);
				if (ontology.getTerm(parent) != null) {
					parsedAnnotation.add(node, parent);
				}
				up(node, parent, parsedAnnotation, ontology, flOntology);
				// else{System.out.println("term not in ontology: "+
				// parents[t]);}
			}
		}
	}

	/**
	 * method for recursion through tree to root and detecting cycles
	 * 
	 * @throws Exception if ontology contains a cycle
	 */

	private void up_go(int startID, int id, Ontology ontology) throws Exception {
		OntologyTerm child = ontology.getTerm(id);
		int[] parents = child.getParentsAndContainers();
		for (int parent : parents) {
			if (parent == startID) {
				throw new Exception("Error: your ontology file contains a cycle at ID " + startID);
			} else if (!parentsSet.contains(parent)) {
				if (ontology.getTerm(parent) != null) {
					parentsSet.add(parent);
					up_go(startID, parent, ontology);
				} else {
					System.out.println("term not in ontology: " + parent);
				}
			}
		}
	}

	/**
	 * @return the parsed annotation
	 */
	public Annotation getAnnotation() {
		return parsedAnnotation;
	}

	/**
	 * @return the ontology
	 */
	public Ontology getOntology() {
		return ontology;
	}

	public Map<String, HashSet<String>> getAlias() {
		return alias;
	}

	/**
	 * @return true if there are categories in the annotation which are not
	 *         found in the ontology
	 */
	public boolean hasOrphans() {
		return orphansFound;
	}
	
	/**
	 * @return the parser parsingStatusOK : true if OK, false if something's wrong
	 */
	public boolean isParsingStatusOK() {
		return parsingStatusOK;
	}

    public String getWarningMessage() {
        return warningMessage;
    }
}
