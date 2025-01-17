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
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: Class that calculates the hypergeometric test results for a given cluster    
 * */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.cytoscape.work.TaskMonitor;

/**
 * ************************************************************
 * HypergeometricTestCalculate.java -----------------------------------------
 * <p/>
 * Steven Maere & Karel Heymans (c) March 2005
 * <p/>
 * Class that calculates the hypergeometric test results for a given cluster
 * *************************************************************
 */

public class HypergeometricTestCalculateUnder extends CalculateTestTask {

	/*--------------------------------------------------------------
	FIELDS.
	--------------------------------------------------------------*/

	/**
	 * hashmap with as values the values of small n ; keys = GO labels.
	 */
	private static Map mapSmallN;

	/**
	 * hashmap with as values the values of small x ; keys = GO labels.
	 */
	private static Map mapSmallX;

	/**
	 * hashmap with values for big N.
	 */
	private static Map mapBigN;

	/**
	 * hashmap with values for big X.
	 */
	private static Map mapBigX;

	/**
	 * hashmap with the hypergeometric distribution results as values ; keys =
	 * GO labels
	 */
	private static Map<Integer, String> hypergeometricTestMap;

	// Keep track of progress for monitoring:
//	private int maxValue;

	/**
	 * constructor with as argument the selected cluster and the annotation,
	 * ontology and alpha.
	 */
	public HypergeometricTestCalculateUnder(DistributionCount dc) {
		dc.calculate();
		mapSmallN = dc.getMapSmallN();
		mapSmallX = dc.getMapSmallX();
		mapBigN = dc.getMapBigN();
		mapBigX = dc.getMapBigX();
//		this.maxValue = mapSmallX.size();

	}
	
	public HypergeometricTestCalculateUnder(DistributionCount dc, TaskMonitor taskMonitor) {
		this(dc);
		this.taskMonitor = taskMonitor;
	}

	/**
	 * method that redirects the calculation of hypergeometric distribution.
	 * 
	 * @throws InterruptedException
	 */
	public void calculate() throws InterruptedException {
		if (taskMonitor != null)
			taskMonitor.setTitle("Calculating Hypergeometric Distribution");
		
		HypergeometricDistributionUnder hd;
		hypergeometricTestMap = new HashMap<Integer, String>();

		HashSet set = new HashSet(mapSmallX.keySet());
		Iterator iterator = set.iterator();
		Integer id;
		Integer smallXvalue;
		Integer smallNvalue;
		Integer bigXvalue;
		Integer bigNvalue;
	//	int onePercentOfMaxValue = maxValue / 100;
	//	int currentProgress = 0;
		while (iterator.hasNext()) {
	//		currentProgress++;
			// Update the Task Monitor.
			// This automatically updates the UI Component w/ progress bar.
	/*		if (taskMonitor != null && (currentProgress == 1 || currentProgress % onePercentOfMaxValue == 0)) {
				taskMonitor.setStatusMessage("calculating hypergeometric p-value " + currentProgress + " of "
											 + maxValue);
				// Calculate percentage, must be a value between 0..1.
				double percentComplete = (double) currentProgress / maxValue;
				taskMonitor.setProgress(percentComplete);
			}
        */                

			id = new Integer(iterator.next().toString());
			smallXvalue = new Integer(mapSmallX.get(id).toString());
			smallNvalue = new Integer(mapSmallN.get(id).toString());
			bigXvalue = new Integer(mapBigX.get(id).toString());
			bigNvalue = new Integer(mapBigN.get(id).toString());
			hd = new HypergeometricDistributionUnder(smallXvalue, bigXvalue, smallNvalue, bigNvalue);
			hypergeometricTestMap.put(id, hd.calculateHypergDistr());

			if (cancelled) {
				throw new InterruptedException("Hypergeometric P-value calculation cancelled");
			}
		}
		if (taskMonitor != null)
			taskMonitor.setProgress(1.0);
	}

	/*--------------------------------------------------------------
	  GETTERS.
	--------------------------------------------------------------*/

	/**
	 * getter for the hypergeometric test map.
	 * 
	 * @return HashMap hypergeometricTestMap
	 */
	public Map<Integer, String> getTestMap() {
		return hypergeometricTestMap;
	}

	/**
	 * getter for the mapSmallX.
	 * 
	 * @return HashMap mapSmallX
	 */
	public Map getMapSmallX() {
		return mapSmallX;
	}

	/**
	 * getter for the mapSmallN.
	 * 
	 * @return HashMap mapSmallN
	 */
	public Map getMapSmallN() {
		return mapSmallN;
	}

	/**
	 * getter for the bigX.
	 * 
	 * @return HashMap
	 */
	public Map getMapBigX() {
		return mapBigX;
	}

	/**
	 * getter for the bigN.
	 * 
	 * @return HashMap
	 */
	public Map getMapBigN() {
		return mapBigN;
	}
}
