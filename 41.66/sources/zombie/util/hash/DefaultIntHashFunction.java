package zombie.util.hash;

import java.io.Serializable;


public class DefaultIntHashFunction implements IntHashFunction,Serializable {
	private static final long serialVersionUID = 1L;
	public static final IntHashFunction INSTANCE = new DefaultIntHashFunction();

	protected DefaultIntHashFunction() {
	}

	public int hash(int int1) {
		return int1;
	}
}
