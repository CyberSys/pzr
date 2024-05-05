package org.luaj.kahluafork.compiler;


public class ExpDesc {
	int k;
	int info;
	int aux;
	private double _nval;
	private boolean has_nval;
	int t;
	int f;

	public void setNval(double double1) {
		this._nval = double1;
		this.has_nval = true;
	}

	public double nval() {
		return this.has_nval ? this._nval : (double)this.info;
	}

	void init(int int1, int int2) {
		this.f = -1;
		this.t = -1;
		this.k = int1;
		this.info = int2;
	}

	boolean hasjumps() {
		return this.t != this.f;
	}

	boolean isnumeral() {
		return this.k == 5 && this.t == -1 && this.f == -1;
	}

	public void setvalue(ExpDesc expDesc) {
		this.k = expDesc.k;
		this._nval = expDesc._nval;
		this.has_nval = expDesc.has_nval;
		this.info = expDesc.info;
		this.aux = expDesc.aux;
		this.t = expDesc.t;
		this.f = expDesc.f;
	}
}
