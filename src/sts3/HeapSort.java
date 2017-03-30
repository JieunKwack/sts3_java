package sts3;

import java.util.ArrayList;

public class HeapSort {
	ArrayList<Ans> ans;
	int n;
	int left;
	int right;
	int smallest;
	
	public HeapSort() {
		this.ans = new ArrayList<Ans>();
		this.n = this.ans.size() - 1;
	}
	
	public void buildheap(ArrayList<Ans> ans) {
		this.n = this.ans.size() - 1;
		for (int i = n/2; i >= 0; i--) {
			minheap(ans, i);
		}
	}
	
	public void minheap(ArrayList<Ans> ans, int i) {
		this.left = 2 * i;
		this.right = 2 * i + 1;
		if (this.left <= this.n && ans.get(this.left).jac < ans.get(i).jac) {
			this.smallest = this.left;
		}
		else {
			this.smallest = i;
		}
		
		if (this.right <= this.n && ans.get(this.right).jac < ans.get(this.smallest).jac) {
			this.smallest = this.right;
		}
		
		if (this.smallest != i) {
			swap(i, this.smallest);
			minheap(this.ans, this.smallest);
		}
	}
	
	public void swap(int i, int j) {
		Ans tmp = this.ans.get(i);
		this.ans.add(i, this.ans.get(j)); //shift
		this.ans.remove(i+1);
		this.ans.add(j, tmp);
		this.ans.remove(j+1);
	}
	
	public void sort() {
		buildheap(this.ans);
		
//		for(int i = this.n; i > 0; i--) {
//			swap(0, i);
//			this.n--;
//			minheap(this.ans, 0);
//		}
	}
}
