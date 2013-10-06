/////////////////////////////////////////////////
// ANode.hh created in /wordchains-solver/include/WordNode.hh
//
// Author : Simon CORSIN <corsin_s@epitech.net>
// File created on Apr 14, 2012 at 10:42:21 AM
////////

package com.kerious.framework.utils.path;

public class ANode <T> {

	protected T value;
	protected ANode<T> father;
	protected int Gscore;
	protected int Hscore;
	protected int Fscore;

	public ANode(T value, int G, int H, int F, ANode<T> father) {
		this.father = father;
		if (this.father == null) {
			this.father = this;
		}
		this.Gscore = G;
		this.Fscore = F;
		this.Hscore = H;
		this.value = value;
	}

	public ANode(T value) {
		this(value, 0, 0, 0, null);
	}
	
	@Override
	public String toString() {
		return "Fscore: " + this.Fscore;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.value.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return this.value.hashCode();
	}

	//GETTERS / SETTERS
	public final int getGScore() {
		return Gscore;
	}

	public final int getHScore() {
		return Hscore;
	}

	public final int getFScore() {
		return Fscore;
	}

	public final T getValue()  {
		return value;
	}

	public final ANode<T> getFather() {
		return father;
	}

	public final void setGScore(int score) {
		this.Gscore = score;
	}

	public final void setHScore(int score) {
		this.Hscore = score;
	}

	public final void setFScore(int score) {
		this.Fscore = score;
	}

	public final void setFather(ANode<T> father) {
		this.father = father;
	}
}