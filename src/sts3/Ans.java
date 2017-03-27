package sts3;

public class Ans {
	private static final double nan = -1;
	double jac = nan;
	int index;
	
//	public Ans() {
//		
//	}
	
	public boolean isempty() {
		if (this.jac == nan) {
			return true;
		}
		return false;
	}
	
	public void init() {
		this.jac = nan;
	}
}