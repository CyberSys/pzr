package zombie.radio.script;

import java.util.ArrayList;
import java.util.List;
import zombie.radio.globals.CompareMethod;
import zombie.radio.globals.CompareResult;
import zombie.radio.globals.RadioGlobal;


public class ConditionContainer implements ConditionIter {
	private List conditions;
	private OperatorType operatorType;

	public ConditionContainer() {
		this(OperatorType.NONE);
	}

	public ConditionContainer(OperatorType operatorType) {
		this.conditions = new ArrayList();
		this.operatorType = OperatorType.NONE;
		this.operatorType = operatorType;
	}

	public CompareResult Evaluate() {
		boolean boolean1 = false;
		for (int int1 = 0; int1 < this.conditions.size(); ++int1) {
			ConditionIter conditionIter = (ConditionIter)this.conditions.get(int1);
			CompareResult compareResult = conditionIter != null ? conditionIter.Evaluate() : CompareResult.Invalid;
			if (compareResult.equals(CompareResult.Invalid)) {
				return compareResult;
			}

			OperatorType operatorType = conditionIter.getNextOperator();
			if (int1 == this.conditions.size() - 1) {
				return !operatorType.equals(OperatorType.NONE) ? CompareResult.Invalid : (!boolean1 ? compareResult : CompareResult.False);
			}

			if (operatorType.equals(OperatorType.OR)) {
				if (!boolean1 && compareResult.equals(CompareResult.True)) {
					return compareResult;
				}

				boolean1 = false;
			} else if (operatorType.equals(OperatorType.AND)) {
				boolean1 = boolean1 || compareResult.equals(CompareResult.False);
			} else if (operatorType.equals(OperatorType.NONE)) {
				return CompareResult.Invalid;
			}
		}

		return CompareResult.Invalid;
	}

	public OperatorType getNextOperator() {
		return this.operatorType;
	}

	public void setNextOperator(OperatorType operatorType) {
		this.operatorType = operatorType;
	}

	public void Add(ConditionContainer conditionContainer) {
		this.conditions.add(conditionContainer);
	}

	public void Add(RadioGlobal radioGlobal, RadioGlobal radioGlobal2, CompareMethod compareMethod, OperatorType operatorType) {
		ConditionContainer.Condition condition = new ConditionContainer.Condition(radioGlobal, radioGlobal2, compareMethod, operatorType);
		this.conditions.add(condition);
	}

	private static class Condition implements ConditionIter {
		private OperatorType operatorType;
		private CompareMethod compareMethod;
		private RadioGlobal valueA;
		private RadioGlobal valueB;

		public Condition(RadioGlobal radioGlobal, RadioGlobal radioGlobal2, CompareMethod compareMethod) {
			this(radioGlobal, radioGlobal2, compareMethod, OperatorType.NONE);
		}

		public Condition(RadioGlobal radioGlobal, RadioGlobal radioGlobal2, CompareMethod compareMethod, OperatorType operatorType) {
			this.operatorType = OperatorType.NONE;
			this.valueA = radioGlobal;
			this.valueB = radioGlobal2;
			this.operatorType = operatorType;
			this.compareMethod = compareMethod;
		}

		public CompareResult Evaluate() {
			return this.valueA != null && this.valueB != null ? this.valueA.compare(this.valueB, this.compareMethod) : CompareResult.Invalid;
		}

		public OperatorType getNextOperator() {
			return this.operatorType;
		}

		public void setNextOperator(OperatorType operatorType) {
			this.operatorType = operatorType;
		}
	}
}
