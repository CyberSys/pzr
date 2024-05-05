package zombie.radio.globals;


public enum EditGlobalOps {

	set,
	add,
	sub;

	private static EditGlobalOps[] $values() {
		return new EditGlobalOps[]{set, add, sub};
	}
}
