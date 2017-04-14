//package sts3;
//
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Set;
//
//public class Trans_outQuery_to_Set {
//	Set<Integer> set;
//	int label;
//	
//	public Trans_outQuery_to_Set(CSVreader Query, int index, Bound BD, double sig, double eps) {
//		this.set = new HashSet<Integer>();
//		this.label = Query.label[index];
//		
//		divideQintoQinAndQout Qinout = new divideQintoQinAndQout(Query, index, BD);
//		
//		TimeSeriesTranstoSet Qin = new TimeSeriesTranstoSet(Qinout.qin, BD, sig, eps);
//		this.set.addAll(Qin.set);
//		
//		if (Qinout.qout.isNotEmpty()) {
//			Bound BQ = new Bound(Qinout.qout, sig, eps);
//			TimeSeriesTranstoSet Qout = new TimeSeriesTranstoSet(Qinout.qout, BQ, sig, eps);
//			this.set.addAll(gettransformQout(Qout.set, BD.maxNumber));
//		}
//	}
//	
//	public Set<Integer> gettransformQout(Set<Integer> setQout, int maxNumber){
//		Set<Integer> reQout = new HashSet<Integer>();
//		for (Iterator<Integer> i = setQout.iterator(); i.hasNext(); ) {
//			reQout.add(i.next() + maxNumber);
//		}
//		return reQout;
//	}
//}
//
//class divideQintoQinAndQout {
//	DevidedQ qin;
//	DevidedQ qout;
//	public divideQintoQinAndQout(CSVreader Query, int index, Bound BD) {
//		qin = new DevidedQ();
//		qout = new DevidedQ();
//		
//		for (int i = 0; i < Query.data[index].length; i++){
//			if (Query.data[index][i] >= BD.xmin && Query.data[index][i] <= BD.xmax) {
//				qin.time.add(i);
//				qin.data.add(Query.data[index][i]);
//			} else {
//				qout.time.add(i);
//				qout.data.add(Query.data[index][i]);
//			}
//		}
//	}
//}