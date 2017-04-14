package sts3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class sts3Naive {
	ArrayList<TimeSeriesTranstoSet> data, query;
	int errorCounts;
	double errorRate;
	
	public sts3Naive (ArrayList<TimeSeriesTranstoSet> d, ArrayList<TimeSeriesTranstoSet> q) {
		this.data = d;
		this.query = q;
		this.errorCounts = 0;
	}
	
	public void run (int k) {
		int numQuery = this.query.size();
		int numData = this.data.size();
		for (int i = 0; i < numQuery; i++) {//query index
			TimeSeriesTranstoSet q = this.query.get(i);
			AnsList ans = new AnsList();
			for (int j = 0; j < numData; j++) {
				TimeSeriesTranstoSet d  = this.data.get(j);
				double jac_sim = getJacSIM(q, d);
				
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
		this.errorRate = (double)this.errorCounts / numQuery ;
	}
	
	public double getJacSIM(TimeSeriesTranstoSet a, TimeSeriesTranstoSet b) {
		Set<Integer> U = new HashSet<Integer>(a.set);
		Set<Integer> I = new HashSet<Integer>(a.set);
		
		U.addAll(b.set);
		I.retainAll(b.set);
		
		return (double)I.size()/U.size();
	}
	
	public int getErrorCounts() {
		return this.errorCounts;
	}
	
	public double getErrorRate() {
		return this.errorRate;
	}
}