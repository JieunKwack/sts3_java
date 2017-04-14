package sts3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AnsList {
	ArrayList<Ans> ansList;
	
	public AnsList() {
		this.ansList = new ArrayList<Ans>();
	}
	
	public void addAns(Ans newAns) {
		this.ansList.add(newAns);
		Collections.sort(this.ansList, new AscendingObj());
	}
	
	public Ans getRootOfAns() {
		return this.ansList.get(0);
	}
	
	public void removeRootOfAns() {
		this.ansList.remove(0);
	}
	
	public Ans getAnswer() {
		int index = this.ansList.size()-1;
		return this.ansList.get(index);
	}
}

class AscendingObj implements Comparator<Ans> {	 
    @Override
    public int compare(Ans lhs, Ans rhs) {
        return Double.compare(lhs.jac, rhs.jac);
    }
}