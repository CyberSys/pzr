package zombie.radio.globals;


public enum CompareMethod {

	equals,
	notequals,
	lessthan,
	morethan,
	lessthanorequals,
	morethanorequals;

	private static CompareMethod[] $values() {
		return new CompareMethod[]{equals, notequals, lessthan, morethan, lessthanorequals, morethanorequals};
	}
}
