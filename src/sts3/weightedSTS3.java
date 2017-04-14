package sts3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;

public class weightedSTS3 {
	ArrayList<TimeSeriesTranstoSet> data, query;
	int errorCounts;
	double errorRate;
	
	HashMap<Integer, Integer> countPerClass;	//<class_id=key, count=mapped value>
	HashMap<Integer, HashMap<Integer, Integer>> countPerCell;	//<cell_id, <class_id, count>>
	HashMap<Integer, Double> entropyPerCell;	//<cell_id, entropy>
	HashMap<Integer, Double> infoGainPerCell;	//<cell_id, information_gain>
	double entropyInitial;
	
	public weightedSTS3(ArrayList<TimeSeriesTranstoSet> d, ArrayList<TimeSeriesTranstoSet> q) {
		this.data = d;
		this.query = q;
		this.errorCounts = 0;
		
		//HEASOO
		buildCountPerClass();
		this.entropyInitial = computeEntropy(this.countPerClass);
		buildCountPerCell();
		buildEntropyPerCell();
		buildInfoGainPerCell();
	}
	
	private void buildCountPerClass() {
        // calculate the # of ts for each class
		this.countPerClass = new HashMap<Integer, Integer>(); // <cell_id, count>
		int numData = this.data.size();
		for (int i = 0; i < numData; i++) { // the#OfTrain = this.data.size()
			TimeSeriesTranstoSet d = this.data.get(i); // d = i-th train data
			if (!this.countPerClass.containsKey(d.label)) this.countPerClass.put(d.label, 0);
			int oldCount = this.countPerClass.get(d.label);
			this.countPerClass.replace(d.label, oldCount+1);
		}
		//System.out.println(countPerClass);
	}
	
	private void buildCountPerCell() {
		// calculate the # of ts for each cell
		// STEP1: Build countPerCell
		this.countPerCell = new HashMap<Integer, HashMap<Integer, Integer>>(); //<cell_id, <class_id, count>>
		
		int numData = this.data.size();
		for (int i = 0; i < numData; i++) {
			TimeSeriesTranstoSet d = this.data.get(i);
			//System.out.println("d.label="+d.label+"\td.set="+d.set);
			for (Iterator<Integer> it = d.set.iterator(); it.hasNext(); ) {
				Integer cell_id = it.next();
				if(!this.countPerCell.containsKey(cell_id)) this.countPerCell.put(cell_id, new HashMap<Integer, Integer>());
				HashMap<Integer, Integer> dic = this.countPerCell.get(cell_id); // countPerCell 안에 반영이 되는 지,
				if(!dic.containsKey(d.label)) dic.put(d.label, 0);
				int oldCount = dic.get(d.label);
				dic.replace(d.label, oldCount+1);
				this.countPerCell.replace(cell_id, dic);
			}
			//System.out.println(countPerCell);
		}
	}
	
	private void buildEntropyPerCell() {
		//STEP2: Build entropyPerCell
		this.entropyPerCell = new HashMap<Integer, Double>();
		for(Map.Entry<Integer, HashMap<Integer, Integer>> entry : this.countPerCell.entrySet()) {// countPerCell: <cell_id, <class_id, count>>
			int cell_id = entry.getKey();
//			System.out.println(entry);
			HashMap<Integer, Integer> dicPerCell = entry.getValue();
			this.entropyPerCell.put(cell_id, computeEntropy(dicPerCell)); // <class_id, count>
		}
		//System.out.println(entropyPerCell);
	}
	
	private void buildInfoGainPerCell() {
		this.infoGainPerCell = new HashMap<Integer, Double>(); //<cell_id, information gain ratio>
		for(Map.Entry<Integer, Double> entry : this.entropyPerCell.entrySet()) // <cell_id, entropy>
			infoGainPerCell.put(entry.getKey(), this.entropyInitial - entry.getValue());
		//System.out.println(infoGainPerCell);
	}
	
	private double computeEntropy(HashMap<Integer, Integer> dic) {//input: countPerClass <class_id, count>
		int totalCount = 0;
		for(Map.Entry<Integer, Integer> entry : dic.entrySet())
			totalCount += entry.getValue();
		
		//System.out.println("totalNumClasses="+totalNumClasses+"\ttotalCount="+totalCount);
		double entropy = 0;
		for(Map.Entry<Integer, Integer> entry : dic.entrySet()) {
			double p_i = (double) entry.getValue() / totalCount;
			entropy -= p_i * (Math.log(p_i) / Math.log(2));
			//System.out.println("p_i="+p_i+"\tMath.log(p_i)/Math.log(2)="+Math.log(p_i)/Math.log(2)+"\tentropy="+entropy);
		}
		return entropy;
	}
	
	public void run (int k) {
		int numQuery = this.query.size();
		int numData = this.data.size();
		for (int i = 0; i < numQuery; i++) {//query index
			TimeSeriesTranstoSet q = this.query.get(i);
			AnsList ans = new AnsList();
			for (int j = 0; j < numData; j++) {
				TimeSeriesTranstoSet d  = this.data.get(j);		
				double jac_sim = getWeightedJacSIM(q, d);
				
				if (ans.ansList.size() < k) {
					Ans newAns = new Ans(jac_sim, j, d.label);
					ans.addAns(newAns);
				} else if (ans.getRootOfAns().jac < jac_sim) {
					Ans newAns = new Ans(jac_sim, j, d.label);
					ans.addAns(newAns);
					if (ans.ansList.size() > k) ans.removeRootOfAns();
				}
			}
			
			if (ans.getAnswer().label != q.label) {
				// query ans 1NN output in file
//				sb.append("query\n"+query.set.toString().replace("[", "").replace("]", "")+"\n");
//				sb.append("1NN\n"+d.get(query.getBestAns().index).set.toString().replace("[","").replace("[", ""));
				
//				sb.append(query.getBestAns().label+","+query.label+","+(error+1)+"\n"); // make string for file output				
//				System.out.println(query.getBestAns().label+", "+query.label+", "+(error+1)); // console output
				this.errorCounts++;
			}
		}
		errorRate = (double) this.errorCounts / numQuery;	
	}
	
	public double getWeightedJacSIM(TimeSeriesTranstoSet a, TimeSeriesTranstoSet b) {
		Set<Integer> U = new HashSet<Integer>(a.set);
		Set<Integer> I = new HashSet<Integer>(a.set);
		
		U.addAll(b.set);
		I.retainAll(b.set);
		
		double sumInfoGainU = 0;
		double sumInfoGainI = 0;
		
		for (Iterator<Integer> it = U.iterator(); it.hasNext(); ) {
			int cell_id = it.next();
			if( this.infoGainPerCell.containsKey(cell_id) )
				sumInfoGainU += this.infoGainPerCell.get(cell_id);
		}
		
		for (Iterator<Integer> it = I.iterator(); it.hasNext(); ) {
			int cell_id = it.next();
			sumInfoGainI += this.infoGainPerCell.get(cell_id);
		}
//		Iterator<Integer> it = U.iterator();
//		int cell_id = 0;
//		while(it.hasNext()) {
//			cell_id = it.next();
//			if( infoGainPerCell.containsKey(cell_id) )
//				sumInfoGainU += infoGainPerCell.get(cell_id);
//		}
//		it = I.iterator();
//		while(it.hasNext()) {
//			cell_id = it.next();
//			sumInfoGainI += infoGainPerCell.get(cell_id);
//		}			
		
		return sumInfoGainI/sumInfoGainU;
	}
	
	public int getErrorCounts() {
		return this.errorCounts;
	}
	
	public double getErrorRate() {	
		return this.errorRate;
	}
}