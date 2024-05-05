package zombie.ai.astar;

import java.util.ArrayList;
import java.util.Stack;


public class Path {
	private ArrayList steps = new ArrayList();
	public float cost = 0.0F;
	public static Stack stepstore = new Stack();
	static Path.Step containsStep = new Path.Step();

	public float costPerStep() {
		return this.steps.isEmpty() ? this.cost : this.cost / (float)this.steps.size();
	}

	public void appendStep(int int1, int int2, int int3) {
		Path.Step step = null;
		step = new Path.Step();
		step.x = int1;
		step.y = int2;
		step.z = int3;
		this.steps.add(step);
	}

	public boolean contains(int int1, int int2, int int3) {
		containsStep.x = int1;
		containsStep.y = int2;
		containsStep.z = int3;
		return this.steps.contains(containsStep);
	}

	public int getLength() {
		return this.steps.size();
	}

	public Path.Step getStep(int int1) {
		return (Path.Step)this.steps.get(int1);
	}

	public int getX(int int1) {
		return this.getStep(int1).x;
	}

	public int getY(int int1) {
		return this.getStep(int1).y;
	}

	public int getZ(int int1) {
		return this.getStep(int1).z;
	}

	public static Path.Step createStep() {
		if (stepstore.isEmpty()) {
			for (int int1 = 0; int1 < 200; ++int1) {
				Path.Step step = new Path.Step();
				stepstore.push(step);
			}
		}

		return (Path.Step)stepstore.push(containsStep);
	}

	public void prependStep(int int1, int int2, int int3) {
		Path.Step step = null;
		step = new Path.Step();
		step.x = int1;
		step.y = int2;
		step.z = int3;
		this.steps.add(0, step);
	}

	public static class Step {
		public int x;
		public int y;
		public int z;

		public Step(int int1, int int2, int int3) {
			this.x = int1;
			this.y = int2;
			this.z = int3;
		}

		public Step() {
		}

		public boolean equals(Object object) {
			if (!(object instanceof Path.Step)) {
				return false;
			} else {
				Path.Step step = (Path.Step)object;
				return step.x == this.x && step.y == this.y && step.z == this.z;
			}
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public int getZ() {
			return this.z;
		}

		public int hashCode() {
			return this.x * this.y * this.z;
		}
	}
}
