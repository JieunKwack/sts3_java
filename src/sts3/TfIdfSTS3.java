package sts3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

public class TfIdfSTS3 {
	ArrayList<TimeSeriesTranstoSet> data, query;
	int errorCounts;
	double errorRate;
	int numData;
	HashMap<Integer, Integer> cellFreqInData;	// <cell_id=key, freq=mapped value> = IDF(count cell freq in all data)
	HashMap<Integer, HashMap<Integer, Integer>> cellFreqPerClass;	// <class_id, <cell_id, freq>> = TF(count cell freq in each class)
	HashMap<Integer, HashMap<Integer, Double>> tfIdfPerCell; //<class_id, <cell_id, tfIdf>
	// suppose query's class label is same as target time series, then compute tfIdf score and compare with candidate
	
	public TfIdfSTS3(ArrayList<TimeSeriesTranstoSet> d, ArrayList<TimeSeriesTranstoSet> q) {
		this.data = d;
		this.query = q;
		this.errorCounts = 0;
		this.numData = this.data.size();
		
		buildCellFreqInData();
		buildCellFreqPerClass();
		buildTfIdfPerCell();
	}
	
	private void buildCellFreqInData() {
        // calculate the # of ts for each cell
		this.cellFreqInData = new HashMap<Integer, Integer>(); // <cell_id, freq>

		for (int i = 0; i < this.numData; i++) { // the#OfTrain = this.data.size()
			TimeSeriesTranstoSet d = this.data.get(i); // d = i-th train data
			for (Iterator<Integer> it = d.set.iterator(); it.hasNext(); ) {
				Integer cell_id = it.next();
				if (!this.cellFreqInData.containsKey(cell_id)) this.cellFreqInData.put(cell_id, 0);
				int oldFreq = this.cellFreqInData.get(cell_id);
				this.cellFreqInData.replace(cell_id, oldFreq+1);
			}
		}
//		System.out.println(this.cellFreqInData);
	}
	
	private void buildCellFreqPerClass() {
		// calculate the # of ts for each cell for each class
		// tf(cell_id, class_id) = freq(cell_id, class_id)
		this.cellFreqPerClass = new HashMap<Integer, HashMap<Integer, Integer>>(); //<class_id, <cell_id, freq>>
		
		for (int i = 0; i < this.numData; i++) {
			TimeSeriesTranstoSet d = this.data.get(i);
//			System.out.println(d.label);
			
			if (!this.cellFreqPerClass.containsKey(d.label)) 
				this.cellFreqPerClass.put(d.label, new HashMap<Integer, Integer>());
			
			HashMap<Integer, Integer> dic = this.cellFreqPerClass.get(d.label); // dic = {<cell_id, freq>}
//			System.out.println(dic);
			for (Iterator<Integer> it = d.set.iterator(); it.hasNext(); ) {
				Integer cell_id = it.next();
				if(!dic.containsKey(cell_id)) dic.put(cell_id, 0);
				int oldFreq = dic.get(cell_id);
				dic.replace(cell_id, oldFreq+1);
			}
			this.cellFreqPerClass.replace(d.label, dic);
		}
	}
	
	private void buildTfIdfPerCell() {
		this.tfIdfPerCell = new HashMap<Integer, HashMap<Integer, Double>>();
		
		for (Iterator<Integer> it = this.cellFreqPerClass.keySet().iterator(); it.hasNext(); ) {
			Integer class_id = it.next();
			if(!this.tfIdfPerCell.containsKey(class_id)) this.tfIdfPerCell.put(class_id, new HashMap<Integer, Double>());
			HashMap<Integer, Integer> dic = this.cellFreqPerClass.get(class_id); // get tf
			HashMap<Integer, Double> tfIdfPerCell = this.tfIdfPerCell.get(class_id);
			for (Iterator<Integer> sub_it = dic.keySet().iterator(); sub_it.hasNext(); ) {
				Integer cell_id = sub_it.next();
				Double tfIdf = dic.get(cell_id) * Math.log((double)this.numData / this.cellFreqInData.get(cell_id));
				tfIdfPerCell.put(cell_id, tfIdf);
			}
			this.tfIdfPerCell.replace(class_id, tfIdfPerCell);
//			System.out.println(this.tfIdfPerCell.get(class_id));
		}
	}
	
	public void run (int k) {
		int numQuery = this.query.size();
		
		for (int i = 0; i < numQuery; i++) {//query index
			TimeSeriesTranstoSet q = this.query.get(i);
			AnsList ans = new AnsList();
			for (int j = 0; j < this.numData; j++) {
				TimeSeriesTranstoSet d  = this.data.get(j);		
				double jac_sim = getWeightedJacSIM(q, d);
//				System.out.println(jac_sim);
				
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
	
	public double getWeightedJacSIM(TimeSeriesTranstoSet a, TimeSeriesTranstoSet b) { // a = q, b = train
		Set<Integer> U = new HashSet<Integer>(a.set);
		Set<Integer> I = new HashSet<Integer>(a.set);
		
		U.addAll(b.set);
		I.retainAll(b.set);
		
		double sumTfIdfI = 0;
		double sumTfIdfU = 0;
		
		// 식2.
		HashMap<Integer, Double> tfIdfPerCellWithFixedLabel = this.tfIdfPerCell.get(b.label);
		for (Iterator<Integer> it = U.iterator(); it.hasNext(); ) {
			int cell_id = it.next();
			if (tfIdfPerCellWithFixedLabel.containsKey(cell_id)) 
				sumTfIdfU += tfIdfPerCellWithFixedLabel.get(cell_id); // idf : 분모가 0인 것을 방지하기 위

//			System.out.println(cellFreq);
//			System.out.println(sumIdfU);
		}
		
//		HashMap<Integer, Integer> cellFreqPerCellInClass = this.cellFreqPerClass.get(b.label);
		for (Iterator<Integer> it = I.iterator(); it.hasNext(); ) {
			int cell_id = it.next();
//			if ( cellFreqPerCellInClass.containsKey(cell_id) )
//			sumTfI += cellFreqPerCellInClass.get(cell_id);
			sumTfIdfI += tfIdfPerCellWithFixedLabel.get(cell_id);
		}
//		System.out.println(sumTfI +"\t"+sumIdfU);
		return sumTfIdfI/sumTfIdfU;
	}
	
	public int getErrorCounts() {
		return this.errorCounts;
	}
	
	public double getErrorRate() {	
		return this.errorRate;
	}
}