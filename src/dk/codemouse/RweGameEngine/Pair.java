package dk.codemouse.RweGameEngine;

public class Pair<T> {
	T first, last;
	public Pair(T a, T b) {
		this.first = a;
		this.last = b;
	}
	
	public void swap() {
		T tempa = first;
		
		first = last;
		last = tempa;
	}
	
	public static <T> Pair<T> swap(T first, T last) {
		Pair<T> p = new Pair<>(first, last);
		p.swap();
		return p;
	}
}