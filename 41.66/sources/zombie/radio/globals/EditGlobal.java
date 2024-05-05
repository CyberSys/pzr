package zombie.radio.globals;


public final class EditGlobal {
	private RadioGlobal global;
	private RadioGlobal value;
	private EditGlobalOps operator;

	public EditGlobal(RadioGlobal radioGlobal, EditGlobalOps editGlobalOps, RadioGlobal radioGlobal2) {
		this.global = radioGlobal;
		this.operator = editGlobalOps;
		this.value = radioGlobal2;
	}

	public RadioGlobal getGlobal() {
		return this.global;
	}

	public EditGlobalOps getOperator() {
		return this.operator;
	}

	public RadioGlobal getValue() {
		return this.value;
	}
}
