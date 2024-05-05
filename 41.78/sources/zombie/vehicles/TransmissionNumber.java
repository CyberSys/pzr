package zombie.vehicles;



public enum TransmissionNumber {

	R,
	N,
	Speed1,
	Speed2,
	Speed3,
	Speed4,
	Speed5,
	Speed6,
	Speed7,
	Speed8,
	index;

	private TransmissionNumber(int int1) {
		this.index = int1;
	}
	public int getIndex() {
		return this.index;
	}
	public static TransmissionNumber fromIndex(int int1) {
		switch (int1) {
		case -1: 
			return R;
		
		case 0: 
			return N;
		
		case 1: 
			return Speed1;
		
		case 2: 
			return Speed2;
		
		case 3: 
			return Speed3;
		
		case 4: 
			return Speed4;
		
		case 5: 
			return Speed5;
		
		case 6: 
			return Speed6;
		
		case 7: 
			return Speed7;
		
		case 8: 
			return Speed8;
		
		default: 
			return N;
		
		}
	}
	public TransmissionNumber getNext(int int1) {
		return this.index != -1 && this.index != int1 ? fromIndex(this.index + 1) : this;
	}
	public TransmissionNumber getPrev(int int1) {
		return this.index != -1 && this.index != int1 ? fromIndex(this.index - 1) : this;
	}
	public String getString() {
		switch (this.index) {
		case -1: 
			return "R";
		
		case 0: 
			return "N";
		
		case 1: 
			return "1";
		
		case 2: 
			return "2";
		
		case 3: 
			return "3";
		
		case 4: 
			return "4";
		
		case 5: 
			return "5";
		
		case 6: 
			return "6";
		
		case 7: 
			return "7";
		
		case 8: 
			return "8";
		
		default: 
			return "";
		
		}
	}
	private static TransmissionNumber[] $values() {
		return new TransmissionNumber[]{R, N, Speed1, Speed2, Speed3, Speed4, Speed5, Speed6, Speed7, Speed8};
	}
}
