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
 * * Date: Nov.15.2005
 * * Description: class that counts the small n, big N, small x, big X which serve as input for the statistical underrepresentation tests.     
 **/
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cytoscape.work.TaskMonitor;

import bingo.internal.ontology.Annotation;
import bingo.internal.ontology.Ontology;


/**
 * ************************************************************
 * DistributionCountNeg.java Steven Maere (c) November 2005
 * ----------------------
 * <p/>
 * class that counts the small n, big N, small x, big X which serve as input for
 * the statistical underrepresentation tests.
 * *************************************************************
 */

public class StandardDistributionCountNeg extends DistributionCount {

	/**
	 * the annotation.
	 */
	private Annotation annotation;

	private Map<String, Set<String>> alias;
	/**
	 * HashSet of selected nodes
	 */
	private Set selectedNodes;
	/**
	 * HashSet of reference nodes
	 */
	private Set refNodes;
	/**
	 * hashmap with values of small n ; keys GO labels.
	 */
	private Map mapSmallN;
	/**
	 * hashmap with values of small x ; keys GO labels.
	 */
	private Map mapSmallX;
	/**
	 * hashmap with values for big N.
	 */
	private Map mapBigN;
	/**
	 * hashmap with values for big X.
	 */
	private Map mapBigX;

	// Keep track of progress for monitoring:
	private int maxValue;

	public StandardDistributionCountNeg(Annotation annotation, Ontology ontology, Set selectedNodes, Set refNodes,
			Map<String, Set<String>> alias) {
		this.annotation = annotation;
		this.alias = alias;
		annotation.setOntology(ontology);
		this.selectedNodes = selectedNodes;
		this.refNodes = refNodes;
	}
	
	public StandardDistributionCountNeg(Annotation annotation, Ontology ontology, Set selectedNodes, Set refNodes,
			Map<String, Set<String>> alias, TaskMonitor taskMonitor) {
		this(annotation, ontology, selectedNodes, refNodes, alias);
		this.taskMonitor = taskMonitor;
	}
	
	public void calculate() {
		if(taskMonitor != null)
			taskMonitor.setTitle("Counting genes in GO categories...");
		
		countSmallX();
		countSmallN();
		countBigX();
		countBigN();
	}

	/**
	 * method for compiling GO classifications for given node
	 */

	public HashSet getNodeClassifications(String node) {

		// HashSet for the classifications of a particular node
		HashSet classifications = new HashSet();
		Set identifiers = alias.get(node + "");
		if (identifiers != null) {
			Iterator it = identifiers.iterator();
			while (it.hasNext()) {
				int[] goID = annotation.getClassifications(it.next() + "");
				for (int t = 0; t < goID.length; t++) {
					classifications.add(goID[t] + "");
					// omitted : all parent classes of GO class that node is
					// assigned to are also explicitly included in
					// classifications from the start
					// up(goID[t], classifications) ;
				}
			}
		}
		return classifications;
	}

	/**
	 * method for compiling represented GO categories for all nodes ; for
	 * underrepresentation, nodes in the set with 0 occurrences but some
	 * occurrence in the reference set are also considered
	 */

	public HashSet getAllClassifications() {

		HashSet classifications = new HashSet();

		Iterator i = refNodes.iterator();
		while (i.hasNext()) {
			Set identifiers = alias.get(i.next() + "");
			if (identifiers != null) {
				Iterator it = identifiers.iterator();
				while (it.hasNext()) {
					int[] goID = annotation.getClassifications(it.next() + "");
					for (int t = 0; t < goID.length; t++) {
						classifications.add(goID[t] + "");
					}
				}
			}
		}
		return classifications;
	}

	/**
	 * method for recursing through tree to root
	 */

	/*
	 * public void up (int goID, HashSet classifications){ OntologyTerm child =
	 * ontology.getTerm(goID); int [] parents = child.getParentsAndContainers
	 * (); for(int t = 0; t < parents.length; t++){
	 * classifications.add(parents[t] + ""); up(parents[t],classifications); } }
	 */

	/**
	 * method for making the hashmap for small n.
	 */
	public void countSmallN() {
		mapSmallN = this.count(refNodes);
	}

	/**
	 * method for making the hashmap for the small x.
	 */
	public void countSmallX() {
		mapSmallX = this.count(selectedNodes);
	}

	/**
	 * method that counts for small n and small x.
	 */
	public Map count(Set nodes) {

		HashMap map = new HashMap();
		Integer id;

		HashSet allClassifications = getAllClassifications();

		Iterator iterator1 = allClassifications.iterator();

		while (iterator1.hasNext()) {
			id = new Integer(iterator1.next().toString());
			if (!map.containsKey(id)) {
				map.put(id, new Integer(0));
			}
		}

		Iterator i = nodes.iterator();
		while (i.hasNext()) {
			HashSet classifications = getNodeClassifications(i.next().toString());

			Iterator iterator = classifications.iterator();

			// puts the classification counts in a map
			while (iterator.hasNext()) {
				id = new Integer(iterator.next().toString());
				map.put(id, new Integer(new Integer(map.get(id).toString()).intValue() + 1));
			}

		}

		return map;
	}

	/**
	 * counts big N. unclassified nodes are not counted ; no correction for
	 * function_unknown nodes (yet)(requires user input)
	 */
	public void countBigN() {
		mapBigN = new HashMap();
		int bigN = refNodes.size();
		Iterator i = refNodes.iterator();
		while (i.hasNext()) {
			HashSet classifications = getNodeClassifications(i.next().toString());
			Iterator iterator = classifications.iterator();
			if (!iterator.hasNext()) {
				bigN--;
			}
		}
		for (Object id : this.mapSmallX.keySet()) {
			mapBigN.put(id, new Integer(bigN));
		}
	}

	/**
	 * counts big X. unclassified nodes are not counted ; no correction for
	 * function_unknown nodes (yet)(requires user input)
	 */
	public void countBigX() {
		mapBigX = new HashMap();
		int bigX = selectedNodes.size();
		Iterator i = selectedNodes.iterator();
		while (i.hasNext()) {
			HashSet classifications = getNodeClassifications(i.next().toString());
			Iterator iterator = classifications.iterator();
			if (!iterator.hasNext()) {
				bigX--;
			}
		}
		for (Object id : this.mapSmallX.keySet()) {
			mapBigX.put(id, new Integer(bigX));
		}
	}


	public Map getTestMap() {
		return mapSmallX;
	}

	/**
	 * returns small n hashmap.
	 * 
	 * @return hashmap mapSmallN
	 */
	public Map getMapSmallN() {
		return mapSmallN;
	}

	/**
	 * returns small x hashmap.
	 * 
	 * @return hashmap mapSmallX
	 */
	public Map getMapSmallX() {
		return mapSmallX;
	}

	public Map getMapBigN() {
		return mapBigN;
	}

	public Map getMapBigX() {
		return mapBigX;
	}
}
