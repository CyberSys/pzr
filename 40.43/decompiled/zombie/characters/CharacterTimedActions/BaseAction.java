package zombie.characters.CharacterTimedActions;

import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.SwipeState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.ui.UIManager;

public class BaseAction {
   public long SoundEffect = -1L;
   public float CurrentTime = -2.0F;
   public float LastTime = -1.0F;
   public int MaxTime = 60;
   public float PrevLastTime = 0.0F;
   public boolean UseProgressBar = true;
   public IsoGameCharacter chr;
   public boolean StopOnWalk = true;
   public boolean StopOnRun = true;
   public boolean StopOnAim = false;
   public float caloriesModifier = 1.0F;
   public float delta = 0.0F;
   public boolean blockMovementEtc;
   public boolean overrideAnimation;
   public boolean forceStop = false;
   public boolean forceComplete = false;

   public BaseAction(IsoGameCharacter var1) {
      this.chr = var1;
   }

   public void forceStop() {
      this.forceStop = true;
   }

   public void forceComplete() {
      this.forceComplete = true;
   }

   public void PlayLoopedSoundTillComplete(String var1, int var2, float var3) {
      this.SoundEffect = this.chr.getEmitter().playSound(var1, true);
   }

   public boolean hasStalled() {
      return this.LastTime == this.CurrentTime && this.LastTime == this.PrevLastTime && this.LastTime < 0.0F || this.CurrentTime < 0.0F;
   }

   public float getJobDelta() {
      return this.delta;
   }

   public void update() {
      if (this.chr.getStateMachine().getCurrent() == SwipeState.instance()) {
         this.chr.getStateMachine().changeState(this.chr.getDefaultState());
      }

      if (this.chr.getStateMachine().getCurrent() == SwipeStatePlayer.instance()) {
         this.chr.getStateMachine().changeState(this.chr.getDefaultState());
      }

      this.PrevLastTime = this.LastTime;
      this.LastTime = this.CurrentTime;
      this.CurrentTime += GameTime.instance.getMultiplier();
      if (this.CurrentTime < 0.0F) {
         this.CurrentTime = 0.0F;
      }

      if (this.MaxTime == 0) {
         this.delta = 0.0F;
      } else if (this.MaxTime != -1) {
         this.delta = Math.min(this.CurrentTime / (float)this.MaxTime, 1.0F);
      }

      if (this.UseProgressBar && this.chr instanceof IsoPlayer && ((IsoPlayer)this.chr).isLocalPlayer() && this.MaxTime != -1) {
         UIManager.getProgressBar((double)((IsoPlayer)this.chr).getPlayerNum()).setValue(this.delta);
      }

   }

   public void start() {
      this.forceComplete = false;
      this.forceStop = false;
   }

   public void reset() {
      this.CurrentTime = 0.0F;
      this.forceComplete = false;
      this.forceStop = false;
   }

   public float getCurrentTime() {
      return this.CurrentTime;
   }

   public void stop() {
      if (this.SoundEffect > -1L) {
         this.chr.getEmitter().stopSound(this.SoundEffect);
         this.SoundEffect = -1L;
      }

      LuaEventManager.triggerEvent("OnPlayerCancelTimedAction", this);
   }

   public boolean valid() {
      return true;
   }

   public boolean finished() {
      return this.CurrentTime >= (float)this.MaxTime && this.MaxTime != -1;
   }

   public void perform() {
   }

   public void setUseProgressBar(boolean var1) {
      this.UseProgressBar = var1;
   }

   public void setBlockMovementEtc(boolean var1) {
      this.blockMovementEtc = var1;
   }

   public void setOverrideAnimation(boolean var1) {
      this.overrideAnimation = var1;
   }
}
