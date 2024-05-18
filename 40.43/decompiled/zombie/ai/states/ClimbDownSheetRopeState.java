package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.skills.PerkFactory;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;

public class ClimbDownSheetRopeState extends State {
   static ClimbDownSheetRopeState _instance = new ClimbDownSheetRopeState();

   public static ClimbDownSheetRopeState instance() {
      return _instance;
   }

   public void enter(IsoGameCharacter var1) {
      var1.setbClimbing(true);
   }

   public void execute(IsoGameCharacter var1) {
      float var2 = 0.0F;
      float var3 = 0.0F;
      if (var1.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopN) || var1.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetN)) {
         var1.setDir(IsoDirections.N);
         var2 = 0.54F;
         var3 = 0.39F;
      }

      if (var1.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopS) || var1.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetS)) {
         var1.setDir(IsoDirections.S);
         var2 = 0.118F;
         var3 = 0.5756F;
      }

      if (var1.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopW) || var1.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetW)) {
         var1.setDir(IsoDirections.W);
         var2 = 0.4F;
         var3 = 0.7F;
      }

      if (var1.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetTopE) || var1.getCurrentSquare().getProperties().Is(IsoFlagType.climbSheetE)) {
         var1.setDir(IsoDirections.E);
         var2 = 0.5417F;
         var3 = 0.3144F;
      }

      float var4 = var1.x - (float)((int)var1.x);
      float var5 = var1.y - (float)((int)var1.y);
      float var6;
      if (var4 != var2) {
         var6 = (var2 - var4) / 4.0F;
         var4 += var6;
         var1.x = (float)((int)var1.x) + var4;
      }

      if (var5 != var3) {
         var6 = (var3 - var5) / 4.0F;
         var5 += var6;
         var1.y = (float)((int)var1.y) + var5;
      }

      var1.nx = var1.x;
      var1.ny = var1.y;
      var1.PlayAnim("ClimbDown_Rope");
      var6 = 0.16F;
      switch(var1.getPerkLevel(PerkFactory.Perks.Strength)) {
      case 0:
         var6 -= 0.1F;
         break;
      case 1:
         var6 -= 0.02F;
         break;
      case 2:
         var6 -= 0.03F;
         break;
      case 3:
         var6 -= 0.05F;
      case 4:
      case 5:
      default:
         break;
      case 6:
         var6 += 0.05F;
         break;
      case 7:
         var6 += 0.07F;
         break;
      case 8:
         var6 += 0.09F;
         break;
      case 9:
         var6 += 0.1F;
      }

      var1.getSpriteDef().AnimFrameIncrease = var6;
      float var7 = var1.z - var6 / 10.0F * GameTime.instance.getMultiplier();
      var7 = Math.max(var7, 0.0F);

      for(int var8 = (int)var1.z; var8 >= (int)var7; --var8) {
         IsoCell var9 = IsoWorld.instance.getCell();
         IsoGridSquare var10 = var9.getGridSquare((double)var1.getX(), (double)var1.getY(), (double)var8);
         if ((var10.Is(IsoFlagType.solidtrans) || var10.TreatAsSolidFloor() || var8 == 0) && var7 <= (float)var8) {
            var1.z = (float)var8;
            var1.StateMachineParams.clear();
            var1.getStateMachine().changeState(var1.getDefaultState());
            var1.setCollidable(true);
            var1.setbClimbing(false);
            return;
         }
      }

      var1.z = var7;
      if (var1 instanceof IsoPlayer && ((IsoPlayer)var1).isLocalPlayer()) {
         ((IsoPlayer)var1).dirtyRecalcGridStackTime = 2.0F;
      }

   }
}