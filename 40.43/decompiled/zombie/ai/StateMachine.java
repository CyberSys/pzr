package zombie.ai;

import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;

public class StateMachine {
   public boolean Lock = false;
   State CurrentState;
   State GlobalState;
   State NextState;
   IsoGameCharacter Owner;
   State PreviousState;

   public StateMachine(IsoGameCharacter var1) {
      this.Owner = var1;
   }

   public void changeState(State var1) {
      if (!this.Lock) {
         if (this.CurrentState != var1) {
            this.PreviousState = this.CurrentState;
            if (this.CurrentState != null) {
               this.CurrentState.exit(this.Owner);
            }

            this.CurrentState = var1;
            this.NextState = null;
            if (this.CurrentState != null) {
               this.CurrentState.enter(this.Owner);
            }

         }
      }
   }

   public void changeState(State var1, State var2) {
      if (var1 != this.CurrentState) {
         if (!this.Lock) {
            if (var1 != this.CurrentState) {
               LuaEventManager.triggerEvent("OnAIStateChange", this.Owner, var1, this.CurrentState);
            }

            this.PreviousState = this.CurrentState;
            if (this.CurrentState != null) {
               this.CurrentState.exit(this.Owner);
            }

            this.CurrentState = var1;
            this.NextState = var2;
            if (this.CurrentState != null) {
               this.CurrentState.enter(this.Owner);
            }

         }
      }
   }

   public State getCurrent() {
      return this.CurrentState;
   }

   public State getGlobal() {
      return this.GlobalState;
   }

   public State getPrevious() {
      return this.PreviousState;
   }

   public void RevertToPrevious() {
      if (!this.Lock) {
         this.changeState(this.PreviousState);
      }
   }

   public void setCurrent(State var1) {
      if (!this.Lock) {
         this.CurrentState = var1;
      }
   }

   public void setGlobal(State var1) {
      this.GlobalState = var1;
   }

   public void setPrevious(State var1) {
      if (!this.Lock) {
         this.PreviousState = var1;
      }
   }

   public void update() {
      if (this.GlobalState != null) {
         this.GlobalState.execute(this.Owner);
      }

      if (this.CurrentState != null) {
         this.CurrentState.execute(this.Owner);
      }

   }
}
