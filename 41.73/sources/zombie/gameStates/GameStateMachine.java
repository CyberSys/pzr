package zombie.gameStates;

import java.util.ArrayList;
import java.util.Stack;


public final class GameStateMachine {
	public boolean firstrun = true;
	public boolean Loop = true;
	public int StateIndex = 0;
	public int LoopToState = 0;
	public final ArrayList States = new ArrayList();
	public GameState current = null;
	private final Stack yieldStack = new Stack();
	public GameState forceNext = null;

	public void render() {
		if (this.current != null) {
			this.current.render();
		}
	}

	public void update() {
		if (this.States.size() == 0) {
			if (this.forceNext == null) {
				return;
			}

			this.States.add(this.forceNext);
			this.forceNext = null;
		}

		if (this.firstrun) {
			if (this.current == null) {
				this.current = (GameState)this.States.get(this.StateIndex);
			}

			System.out.println("STATE: enter " + this.current.getClass().getName());
			this.current.enter();
			this.firstrun = false;
		}

		if (this.current == null) {
			if (!this.Loop) {
				return;
			}

			this.StateIndex = this.LoopToState;
			if (this.States.isEmpty()) {
				return;
			}

			this.current = (GameState)this.States.get(this.StateIndex);
			if (this.StateIndex < this.States.size()) {
				System.out.println("STATE: enter " + this.current.getClass().getName());
				this.current.enter();
			}
		}

		if (this.current != null) {
			GameState gameState = null;
			if (this.forceNext != null) {
				System.out.println("STATE: exit " + this.current.getClass().getName());
				this.current.exit();
				gameState = this.forceNext;
				this.forceNext = null;
			} else {
				GameStateMachine.StateAction stateAction = this.current.update();
				if (stateAction == GameStateMachine.StateAction.Continue) {
					System.out.println("STATE: exit " + this.current.getClass().getName());
					this.current.exit();
					if (!this.yieldStack.isEmpty()) {
						this.current = (GameState)this.yieldStack.pop();
						System.out.println("STATE: reenter " + this.current.getClass().getName());
						this.current.reenter();
						return;
					}

					gameState = this.current.redirectState();
				} else {
					if (stateAction != GameStateMachine.StateAction.Yield) {
						return;
					}

					System.out.println("STATE: yield " + this.current.getClass().getName());
					this.current.yield();
					this.yieldStack.push(this.current);
					gameState = this.current.redirectState();
				}
			}

			if (gameState == null) {
				++this.StateIndex;
				if (this.StateIndex < this.States.size()) {
					this.current = (GameState)this.States.get(this.StateIndex);
					System.out.println("STATE: enter " + this.current.getClass().getName());
					this.current.enter();
				} else {
					this.current = null;
				}
			} else {
				System.out.println("STATE: enter " + gameState.getClass().getName());
				gameState.enter();
				this.current = gameState;
			}
		}
	}

	public void forceNextState(GameState gameState) {
		this.forceNext = gameState;
	}

	public static enum StateAction {

		Continue,
		Remain,
		Yield;

		private static GameStateMachine.StateAction[] $values() {
			return new GameStateMachine.StateAction[]{Continue, Remain, Yield};
		}
	}
}
