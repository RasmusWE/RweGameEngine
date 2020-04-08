package dk.codemouse.RweGameEngine;

public class Pair<T> {
	public T first, last;
	public Pair(T a, T b) {
		this.first = a;
		this.last = b;
	}
	
	public void swap() {
		T tempf = first;
		
		first = last;
		last = tempf;
	}
	
	public static <T> Pair<T> swap(T first, T last) {
		Pair<T> p = new Pair<>(first, last);
		p.swap();
		return p;
	}
}