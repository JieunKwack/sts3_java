package sts3;

public class Ans {
	private static final double nan = -1;
	double jac = nan;
	int index;
	public Ans(double jac, int index) {
		this.jac = jac;
		this.index = index;
	}
	
	public boolean isempty() {
		if (this.jac == nan) {
			return true;
		}
		return false;
	}
	
	public void init() {
		this.jac = nan;
	}
	
	public double getJac(){
		return this.jac;
	}
}