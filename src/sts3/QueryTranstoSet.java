package sts3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import java.util.Collections;
import java.util.Comparator;

public class QueryTranstoSet extends Trans_outQuery_to_Set {
	final int ROOT = 0;
	ArrayList<Ans> ans;
//	HeapSort heap;
	public QueryTranstoSet(CSVreader Query, int index, Bound BD, double sig, double eps) {
		super(Query, index, BD, sig, eps);
		this.ans = new ArrayList<Ans>();
//		this.heap = new HeapSort();
	}
	
	public double getJacSIM(Set<Integer> data) {
		Set<Integer> U = new HashSet<Integer>(this.set);
		Set<Integer> I = new HashSet<Integer>(this.set);
		
		U.addAll(data);
		I.retainAll(data);

		return (double)I.size()/U.size();
	}
	
	public boolean AnsisEmpty() {
//		if (this.heap.ans.isEmpty()) return true;
		if (this.ans.isEmpty()) return true;
		return false;
	}
	
	public void addAns(double jac, int idx, int label) {
//		this.heap.ans.add(ROOT, new Ans(jac, idx, label));	//new answer
//		this.heap.sort();
		this.ans.add(ROOT, new Ans(jac, idx, label));
		Collections.sort(this.ans, new AscendingObj());
	}
	
	public Ans getRootOfAns() {
//		return this.heap.ans.get(ROOT);
		return this.ans.get(ROOT);
	}
	
	public Ans getBestAns() {
		int best = this.ans.size() - 1;
		return this.ans.get(best);
	}
	
	public void removeRootOfAns() {
//		this.heap.ans.remove(ROOT);
//		this.heap.sort();
		this.ans.remove(ROOT);
	}
}

class AscendingObj implements Comparator<Ans> {	 
    @Override
    public int compare(Ans lhs, Ans rhs) {
        return Double.compare(lhs.jac, rhs.jac);
    }
}