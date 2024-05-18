package zombie.gameStates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameTime;
import zombie.VirtualZombieManager;
import zombie.ai.astar.Mover;
import zombie.ai.states.PathFindState;
import zombie.ai.states.WalkTowardState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.chat.ChatElement;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.properties.PropertyContainer;
import zombie.core.textures.Texture;
import zombie.core.utils.BooleanGrid;
import zombie.debug.LineDrawer;
import zombie.erosion.ErosionData;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LightingThread;
import zombie.iso.LosUtil;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.objects.IsoFire;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.ui.TextDrawObject;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.vehicles.BaseVehicle;

public class DebugChunkState extends GameState {
   private boolean keyDown = false;
   private int gridX = -1;
   private int gridY = -1;
   private UIFont FONT;
   static boolean keyQpressed = false;

   public DebugChunkState() {
      this.FONT = UIFont.DebugConsole;
   }

   public void enter() {
      this.keyDown = GameKeyboard.isKeyDown(60);
   }

   public void exit() {
      LightingThread.instance.disable = false;
   }

   public void render() {
      int var1 = IsoPlayer.getPlayerIndex();
      Core.getInstance().StartFrame(var1, true);
      IsoCamera.frameState.set(var1);
      IsoWorld.instance.CurrentCell.render();
      this.drawGrid();
      this.drawCursor();
      Stack var2 = IsoWorld.instance.getCell().getLamppostPositions();
      int var3 = 0;

      for(int var4 = 0; var4 < var2.size(); ++var4) {
         IsoLightSource var5 = (IsoLightSource)var2.get(var4);
         if (var5.z == (int)IsoPlayer.instance.getZ()) {
            this.paintSquare(var5.x, var5.y, var5.z, 1.0F, 1.0F, 0.0F, 0.5F);
         }

         if (var5.bActive) {
            ++var3;
         }
      }

      IsoMetaGrid.Zone var21 = IsoPlayer.instance.getCurrentZone();
      if (var21 != null) {
         this.DrawIsoLine((float)var21.x, (float)var21.y, (float)(var21.x + var21.w), (float)var21.y, 1.0F, 1.0F, 0.0F, 1.0F, 1);
         this.DrawIsoLine((float)var21.x, (float)(var21.y + var21.h), (float)(var21.x + var21.w), (float)(var21.y + var21.h), 1.0F, 1.0F, 0.0F, 1.0F, 1);
         this.DrawIsoLine((float)var21.x, (float)var21.y, (float)var21.x, (float)(var21.y + var21.h), 1.0F, 1.0F, 0.0F, 1.0F, 1);
         this.DrawIsoLine((float)(var21.x + var21.w), (float)var21.y, (float)(var21.x + var21.w), (float)(var21.y + var21.h), 1.0F, 1.0F, 0.0F, 1.0F, 1);
      }

      IsoGridSquare var22 = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, (int)IsoPlayer.instance.getZ());
      if (var22 != null && var22.getBuilding() != null) {
         BuildingDef var6 = var22.getBuilding().getDef();
         this.DrawIsoLine((float)var6.getX(), (float)var6.getY(), (float)var6.getX2(), (float)var6.getY(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
         this.DrawIsoLine((float)var6.getX2(), (float)var6.getY(), (float)var6.getX2(), (float)var6.getY2(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
         this.DrawIsoLine((float)var6.getX2(), (float)var6.getY2(), (float)var6.getX(), (float)var6.getY2(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
         this.DrawIsoLine((float)var6.getX(), (float)var6.getY2(), (float)var6.getX(), (float)var6.getY(), 1.0F, 1.0F, 1.0F, 1.0F, 2);
      }

      LineDrawer.render();
      LineDrawer.clear();
      Core.getInstance().EndFrame(var1);
      Core.getInstance().RenderOffScreenBuffer();

      int var23;
      for(var23 = 0; var23 < IsoPlayer.numPlayers; ++var23) {
         TextDrawObject.NoRender(var23);
         ChatElement.NoRender(var23);
      }

      if (Core.getInstance().StartFrameUI()) {
         var23 = 10;
         boolean var24 = false;
         int var7 = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
         int var25 = (int)IsoPlayer.instance.getZ();
         IsoGridSquare var8 = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, var25);
         int var12;
         if (var8 != null) {
            String var9 = "x,y,z=" + var8.getX() + "," + var8.getY() + "," + var8.getZ();
            if (var8.getZone() != null) {
               var9 = var9 + " zone=" + var8.getZone().getName() + "/" + var8.getZone().getType();
            } else {
               var9 = var9 + " zone=<none>";
            }

            this.DrawString(10, var23 += var7, var9);
            this.DrawString(10, var23 += var7, "chunk: ObjectsSyncCount=" + var8.chunk.ObjectsSyncCount + " Hash=" + var8.chunk.getHashCodeObjects());
            this.DrawString(10, var23 += var7, "square: ObjectsSyncCount=" + var8.ObjectsSyncCount + " Hash=" + var8.getHashCodeObjects());
            this.DrawString(10, var23 += var7, "darkMulti=" + var8.getDarkMulti(var1) + "    targetDarkMulti=" + var8.getTargetDarkMulti(var1) + "    seen=" + var8.getSeen(var1) + "    couldSee=" + var8.isCouldSee(var1) + "    canSee=" + var8.getCanSee(var1));
            this.DrawString(10, var23 += var7, "getVertLight()=" + Integer.toHexString(var8.getVertLight(0, var1)) + "," + Integer.toHexString(var8.getVertLight(1, var1)) + "," + Integer.toHexString(var8.getVertLight(2, var1)) + "," + Integer.toHexString(var8.getVertLight(3, var1)));
            int var11;
            if (var8.getRoom() != null) {
               String var10 = var8.getRoom().building.def.bAlarmed && !var8.getRoom().building.isAllExplored() ? "    ALARM" : "    (no alarm)";
               var11 = 0;
               var12 = var8.getRoom().lightSwitches.size();
               if (var12 != 0) {
                  var11 = ((IsoLightSwitch)var8.getRoom().lightSwitches.get(0)).lights.size();
               }

               this.DrawString(10, var23 += var7, "buildingID=" + var8.getBuilding().ID + " roomID=" + var8.getRoomID() + "    room=" + var8.getRoom().RoomDef + "    explored=" + var8.getRoom().def.bExplored + "    #lights=" + var11 + "    #switches=" + var12 + var10);
            } else {
               this.DrawString(10, var23 += var7, "roomID=" + var8.getRoomID() + "    room=<none>");
            }

            if (var8.roofHideBuilding != null) {
               this.DrawString(10, var23 += var7, "ROOF-HIDE=" + var8.roofHideBuilding.def.getID());
            }

            int var30;
            for(var30 = 0; var30 < var2.size(); ++var30) {
               IsoLightSource var32 = (IsoLightSource)var2.get(var30);
               if (var32.getX() == var8.getX() && var32.getY() == var8.getY() && var32.getZ() == var8.getZ()) {
                  this.DrawString(10, var23 += var7, "LIGHT SOURCE" + (var32.bHydroPowered ? " hydro=true" : " hydro=false") + " active=" + var32.bActive + " localToBuilding=" + (var32.localToBuilding != null ? var32.localToBuilding.ID : -1) + " " + var32);
               }
            }

            this.DrawString(10, var23 += var7, "exterior=" + var8.getProperties().Is(IsoFlagType.exterior) + "    haveElectricity=" + var8.haveElectricity() + "    haveRoof=" + var8.haveRoof + "    solidfloor=" + var8.getProperties().Is(IsoFlagType.solidfloor) + "    TreatAsSolidFloor=" + (var8.TreatAsSolidFloor() ? "true" : "false") + "    solid=" + var8.Is(IsoFlagType.solid) + "    solidTrans=" + var8.Is(IsoFlagType.solidtrans) + "    burning=" + var8.getProperties().Is(IsoFlagType.burning) + "    burntOut=" + var8.getProperties().Is(IsoFlagType.burntOut));
            this.DrawString(10, var23 += var7, "HasRaindrop=" + var8.getProperties().Is(IsoFlagType.HasRaindrop) + "    HasRainSplashes=" + var8.getProperties().Is(IsoFlagType.HasRainSplashes));

            float var13;
            IsoSprite var34;
            for(var30 = 0; var30 < var8.getObjects().size(); ++var30) {
               IsoObject var33 = (IsoObject)var8.getObjects().get(var30);
               this.DrawString(10, var23 += var7, "object=" + var33.getClass().getName() + " name=" + var33.getName() + " type=" + var33.getType() + " alpha=" + var33.alpha[0] + " targetAlpha=" + var33.targetAlpha[0]);
               if (var33 instanceof IsoFire) {
                  this.DrawString(10, var23 += var7, "IsoFire Energy=" + ((IsoFire)var33).Energy);
               }

               var34 = var33.getSprite();
               var13 = var34 != null && var34.def != null ? var34.def.alpha : 0.0F;
               this.DrawString(20, var23 += var7, "sprite.name=" + (var34 != null ? var34.name : "<none>") + "    spriteName=" + var33.getSpriteName() + "    type=" + (var34 != null ? var34.getType() : "<none>") + "    alpha=" + var13 + "    ID=" + (var34 != null ? var34.ID : "<none>") + " renderYOffset=" + var33.getRenderYOffset());
               if (var34 != null && var34.firerequirement > 0) {
                  this.DrawString(20, var23 += var7, "fireRequirement=" + var34.firerequirement);
               }

               if (var33.AttachedAnimSpriteActual != null) {
                  for(int var14 = 0; var14 < var33.AttachedAnimSpriteActual.size(); ++var14) {
                     IsoSprite var15 = (IsoSprite)var33.AttachedAnimSpriteActual.get(var14);
                     this.DrawString(20, var23 += var7, "attached.name=" + var15.name + "    type=" + var15.getType() + "    ID=" + var15.ID);
                  }
               }

               int var19;
               String var20;
               if (var34 != null && var34.getProperties() != null) {
                  PropertyContainer var43 = var34.getProperties();
                  ArrayList var45 = var43.getPropertyNames();
                  String var16 = "";
                  if (!var45.isEmpty()) {
                     int var17 = 0;
                     int var18 = 0;
                     Collections.sort(var45);

                     for(var19 = 0; var19 < var45.size(); ++var19) {
                        var20 = (String)var45.get(var19);
                        var16 = var16 + var20 + "=" + var43.Val(var20) + "  ";
                        if (var16.substring(var18).length() > 80 && var19 + 1 < var45.size()) {
                           var16 = var16 + "\n    ";
                           var18 = var16.length();
                           ++var17;
                        }
                     }

                     if (!var16.isEmpty()) {
                        this.DrawString(20, var23 += var7, "properties: " + var16);
                        var23 += var17 * var7;
                        var16 = "";
                     }
                  }

                  if (var43.Is(IsoFlagType.collideN)) {
                     var16 = var16 + "collideN  ";
                  }

                  if (var43.Is(IsoFlagType.collideW)) {
                     var16 = var16 + "collideW  ";
                  }

                  if (var43.Is(IsoFlagType.cutN)) {
                     var16 = var16 + "cutN  ";
                  }

                  if (var43.Is(IsoFlagType.cutW)) {
                     var16 = var16 + "cutW  ";
                  }

                  if (var43.Is(IsoFlagType.doorN)) {
                     var16 = var16 + "doorN  ";
                  }

                  if (var43.Is(IsoFlagType.doorW)) {
                     var16 = var16 + "doorW  ";
                  }

                  if (var43.Is(IsoFlagType.windowN)) {
                     var16 = var16 + "windowN  ";
                  }

                  if (var43.Is(IsoFlagType.windowW)) {
                     var16 = var16 + "windowW  ";
                  }

                  if (var43.Is(IsoFlagType.climbSheetTopN)) {
                     var16 = var16 + "climbSheetTopN  ";
                  }

                  if (var43.Is(IsoFlagType.climbSheetTopS)) {
                     var16 = var16 + "climbSheetTopS  ";
                  }

                  if (var43.Is(IsoFlagType.climbSheetTopW)) {
                     var16 = var16 + "climbSheetTopW  ";
                  }

                  if (var43.Is(IsoFlagType.climbSheetTopE)) {
                     var16 = var16 + "climbSheetTopE  ";
                  }

                  if (var43.Is(IsoFlagType.HoppableN)) {
                     var16 = var16 + "HoppableN  ";
                  }

                  if (var43.Is(IsoFlagType.HoppableW)) {
                     var16 = var16 + "HoppableW  ";
                  }

                  if (var43.Is(IsoFlagType.solid)) {
                     var16 = var16 + "solid  ";
                  }

                  if (var43.Is(IsoFlagType.vegitation)) {
                     var16 = var16 + "vegitation  ";
                  }

                  if (!var16.isEmpty()) {
                     this.DrawString(20, var23 += var7, "flags: " + var16);
                  }
               }

               if (!var33.getModData().isEmpty()) {
                  String var44 = "modData: ";
                  int var46 = 0;
                  int var53 = 0;
                  KahluaTableIterator var51 = var33.getModData().iterator();
                  ArrayList var52 = new ArrayList();

                  while(var51.advance()) {
                     var52.add(var51.getKey().toString());
                  }

                  Collections.sort(var52);

                  for(var19 = 0; var19 < var52.size(); ++var19) {
                     var20 = (String)var52.get(var19);
                     var44 = var44 + var20 + "=" + var33.getModData().rawget(var20).toString() + "  ";
                     if (var44.substring(var53).length() > 80 && var19 + 1 < var52.size()) {
                        var44 = var44 + "\n    ";
                        var53 = var44.length();
                        ++var46;
                     }
                  }

                  this.DrawString(20, var23 += var7, var44);
                  var23 += var46 * var7;
               }

               if (var33 instanceof IsoWindow) {
                  this.DrawString(20, var23 += var7, "Window: canAddSheetRope=" + ((IsoWindow)var33).canAddSheetRope() + " PermaLocked=" + ((IsoWindow)var33).isPermaLocked());
               }
            }

            Iterator var31 = var8.getMovingObjects().iterator();

            while(true) {
               IsoMovingObject var35;
               if (!var31.hasNext()) {
                  var31 = var8.getStaticMovingObjects().iterator();

                  while(var31.hasNext()) {
                     var35 = (IsoMovingObject)var31.next();
                     this.DrawString(10, var23 += var7, "static-object=" + var35.getClass().getName() + " name=" + var35.getName());
                     var34 = var35.getSprite();
                     var13 = var34 != null && var34.def != null ? var34.def.alpha : 0.0F;
                     this.DrawString(20, var23 += var7, "sprite=" + (var34 != null ? var34.name : "<none>") + "    alpha=" + var13);
                     if (var35 instanceof IsoDeadBody) {
                        IsoDeadBody var48 = (IsoDeadBody)var35;
                        var34 = var48.legsSprite;
                        this.DrawString(20, var23 += var7, "legsSprite=" + (var34 != null ? var34.name : "<none>") + "    alpha=" + var13);
                     }
                  }

                  var31 = var8.getWorldObjects().iterator();

                  while(var31.hasNext()) {
                     IsoWorldInventoryObject var39 = (IsoWorldInventoryObject)var31.next();
                     this.DrawString(10, var23 += var7, "world-object=" + var39.getClass().getName() + " name=" + var39.getName());
                     var34 = var39.getSprite();
                     var13 = var34 != null && var34.def != null ? var34.def.alpha : 0.0F;
                     this.DrawString(20, var23 += var7, "sprite=" + (var34 != null ? var34.name : "<none>") + "    alpha=" + var13);
                  }

                  StringBuilder var37 = new StringBuilder();
                  var37.append("nav[] ");

                  for(var11 = 0; var11 < 8; ++var11) {
                     IsoGridSquare var50 = var8.nav[var11];
                     var37.append(IsoDirections.fromIndex(var11).toString().toLowerCase() + " = ");
                     var37.append(var50 != null ? var50.getX() + "," + var50.getY() + "," + var50.getZ() : "null");
                     var37.append("  ");
                  }

                  this.DrawString(10, var23 += var7, var37.toString());
                  var37.setLength(0);
                  var37.append("n = " + (var8.n != null ? var8.n.getX() + "," + var8.n.getY() + "," + var8.n.getZ() : "null"));
                  var37.append(" nw = " + (var8.nw != null ? var8.nw.getX() + "," + var8.nw.getY() + "," + var8.nw.getZ() : "null"));
                  var37.append(" w = " + (var8.w != null ? var8.w.getX() + "," + var8.w.getY() + "," + var8.w.getZ() : "null"));
                  var37.append(" sw = " + (var8.sw != null ? var8.sw.getX() + "," + var8.sw.getY() + "," + var8.sw.getZ() : "null"));
                  var37.append(" s = " + (var8.s != null ? var8.s.getX() + "," + var8.s.getY() + "," + var8.s.getZ() : "null"));
                  var37.append(" se = " + (var8.se != null ? var8.se.getX() + "," + var8.se.getY() + "," + var8.se.getZ() : "null"));
                  var37.append(" e = " + (var8.e != null ? var8.e.getX() + "," + var8.e.getY() + "," + var8.e.getZ() : "null"));
                  var37.append(" ne = " + (var8.ne != null ? var8.ne.getX() + "," + var8.ne.getY() + "," + var8.ne.getZ() : "null"));
                  this.DrawString(10, var23 += var7, var37.toString());
                  var37.setLength(0);
                  var37.append("collideMatrix n=" + (var8.collideMatrix[1][0][1] ? "true" : "false"));
                  var37.append(" nw=" + (var8.collideMatrix[0][0][1] ? "true" : "false"));
                  var37.append(" w=" + (var8.collideMatrix[0][1][1] ? "true" : "false"));
                  var37.append(" sw=" + (var8.collideMatrix[0][2][1] ? "true" : "false"));
                  var37.append(" s=" + (var8.collideMatrix[1][2][1] ? "true" : "false"));
                  var37.append(" se=" + (var8.collideMatrix[2][2][1] ? "true" : "false"));
                  var37.append(" e=" + (var8.collideMatrix[2][1][1] ? "true" : "false"));
                  var37.append(" ne=" + (var8.collideMatrix[2][0][1] ? "true" : "false"));
                  var37.append(" above=" + (var8.collideMatrix[0][0][2] ? "true" : "false"));
                  this.DrawString(10, var23 += var7, var37.toString());
                  var37.setLength(0);
                  var37.append("pathMatrix n=" + (var8.pathMatrix[1][0][1] ? "true" : "false"));
                  var37.append(" nw=" + (var8.pathMatrix[0][0][1] ? "true" : "false"));
                  var37.append(" w=" + (var8.pathMatrix[0][1][1] ? "true" : "false"));
                  var37.append(" sw=" + (var8.pathMatrix[0][2][1] ? "true" : "false"));
                  var37.append(" s=" + (var8.pathMatrix[1][2][1] ? "true" : "false"));
                  var37.append(" se=" + (var8.pathMatrix[2][2][1] ? "true" : "false"));
                  var37.append(" e=" + (var8.pathMatrix[2][1][1] ? "true" : "false"));
                  var37.append(" ne=" + (var8.pathMatrix[2][0][1] ? "true" : "false"));
                  this.DrawString(10, var23 += var7, var37.toString());
                  var37.setLength(0);
                  var37.append("pathMatrix(below) n=" + (var8.pathMatrix[1][0][0] ? "true" : "false"));
                  var37.append(" nw=" + (var8.pathMatrix[0][0][0] ? "true" : "false"));
                  var37.append(" w=" + (var8.pathMatrix[0][1][0] ? "true" : "false"));
                  var37.append(" sw=" + (var8.pathMatrix[0][2][0] ? "true" : "false"));
                  var37.append(" s=" + (var8.pathMatrix[1][2][0] ? "true" : "false"));
                  var37.append(" se=" + (var8.pathMatrix[2][2][0] ? "true" : "false"));
                  var37.append(" e=" + (var8.pathMatrix[2][1][0] ? "true" : "false"));
                  var37.append(" ne=" + (var8.pathMatrix[2][0][0] ? "true" : "false"));
                  this.DrawString(10, var23 += var7, var37.toString());
                  var37.setLength(0);
                  var37.append("visionMatrix n=" + (var8.visionMatrix[1][0][1] ? "true" : "false"));
                  var37.append(" nw=" + (var8.visionMatrix[0][0][1] ? "true" : "false"));
                  var37.append(" w=" + (var8.visionMatrix[0][1][1] ? "true" : "false"));
                  var37.append(" sw=" + (var8.visionMatrix[0][2][1] ? "true" : "false"));
                  var37.append(" s=" + (var8.visionMatrix[1][2][1] ? "true" : "false"));
                  var37.append(" se=" + (var8.visionMatrix[2][2][1] ? "true" : "false"));
                  var37.append(" e=" + (var8.visionMatrix[2][1][1] ? "true" : "false"));
                  var37.append(" ne=" + (var8.visionMatrix[2][0][1] ? "true" : "false"));
                  this.DrawString(10, var23 += var7, var37.toString());
                  var37.setLength(0);
                  var37.append("visionMatrix(above) n=" + (var8.visionMatrix[1][0][2] ? "true" : "false"));
                  var37.append(" nw=" + (var8.visionMatrix[0][0][2] ? "true" : "false"));
                  var37.append(" w=" + (var8.visionMatrix[0][1][2] ? "true" : "false"));
                  var37.append(" sw=" + (var8.visionMatrix[0][2][2] ? "true" : "false"));
                  var37.append(" s=" + (var8.visionMatrix[1][2][2] ? "true" : "false"));
                  var37.append(" se=" + (var8.visionMatrix[2][2][2] ? "true" : "false"));
                  var37.append(" e=" + (var8.visionMatrix[2][1][2] ? "true" : "false"));
                  var37.append(" ne=" + (var8.visionMatrix[2][0][2] ? "true" : "false"));
                  var37.append(" @=" + (var8.visionMatrix[1][1][2] ? "true" : "false"));
                  this.DrawString(10, var23 += var7, var37.toString());
                  var37.setLength(0);
                  var37.append("visionMatrix(below) n=" + (var8.visionMatrix[1][0][0] ? "true" : "false"));
                  var37.append(" nw=" + (var8.visionMatrix[0][0][0] ? "true" : "false"));
                  var37.append(" w=" + (var8.visionMatrix[0][1][0] ? "true" : "false"));
                  var37.append(" sw=" + (var8.visionMatrix[0][2][0] ? "true" : "false"));
                  var37.append(" s=" + (var8.visionMatrix[1][2][0] ? "true" : "false"));
                  var37.append(" se=" + (var8.visionMatrix[2][2][0] ? "true" : "false"));
                  var37.append(" e=" + (var8.visionMatrix[2][1][0] ? "true" : "false"));
                  var37.append(" ne=" + (var8.visionMatrix[2][0][0] ? "true" : "false"));
                  var37.append(" @=" + (var8.visionMatrix[1][1][0] ? "true" : "false"));
                  this.DrawString(10, var23 += var7, var37.toString());
                  var23 += var7;
                  break;
               }

               var35 = (IsoMovingObject)var31.next();
               String var36 = "";
               if (var35 instanceof IsoGameCharacter && ((IsoGameCharacter)var35).getCurrentState() != null) {
                  var36 = var36 + " " + ((IsoGameCharacter)var35).getCurrentState().getClass().getSimpleName();
                  if (((IsoGameCharacter)var35).getCurrentState() == WalkTowardState.instance() || ((IsoGameCharacter)var35).getCurrentState() == PathFindState.instance()) {
                     var36 = var36 + " " + ((IsoGameCharacter)var35).getPathTargetX() + "," + ((IsoGameCharacter)var35).getPathTargetY() + "," + ((IsoGameCharacter)var35).getPathTargetZ();
                  }
               }

               if (var35 instanceof IsoZombie && ((IsoZombie)var35).hasActiveModel()) {
                  var36 = var36 + " dir=" + var35.getDir() + " angle=" + ((IsoGameCharacter)var35).getAngle().x + "," + ((IsoGameCharacter)var35).getAngle().y + " reqMovement=" + ((IsoGameCharacter)var35).reqMovement.x + "," + ((IsoGameCharacter)var35).reqMovement.y;
               }

               if (var35 instanceof BaseVehicle) {
                  var36 = var36 + " ID=" + ((BaseVehicle)var35).VehicleID;
               }

               if (var35 instanceof IsoZombie && GameClient.bClient) {
                  var36 = var36 + " OnlineID=" + ((IsoZombie)var35).OnlineID + " DescriptorID=" + ((IsoZombie)var35).getDescriptor().getID();
               }

               this.DrawString(10, var23 += var7, "moving-object=" + var35 + " name=" + var35.getName() + var36);
               IsoSprite var40 = var35.getSprite();
               float var47 = var40 != null && var40.def != null ? var40.def.alpha : 0.0F;
               this.DrawString(20, var23 += var7, "sprite=" + (var40 != null ? var40.name : "<none>") + "    alpha=" + var47);
               if (var35 instanceof IsoPlayer && GameClient.bClient) {
                  this.DrawString(20, var23 += var7, "username=" + ((IsoPlayer)var35).getDisplayName() + " OnlineID=" + ((IsoPlayer)var35).OnlineID);
               }

               if (var35 instanceof IsoZombie && ((IsoZombie)var35).getThumpTarget() != null) {
                  this.DrawString(20, var23 += var7, "thumpTarget=" + ((IsoZombie)var35).getThumpTarget());
               }

               if (var35 instanceof IsoZombie) {
                  this.DrawString(20, var23 += var7, "states=[Client:" + ((IsoZombie)var35).getStateMachine().getCurrent().toString() + " Server:" + ((IsoZombie)var35).serverState + "]");
               }
            }
         }

         if (UIManager.LastPicked != null) {
            String var26 = UIManager.LastPicked.getSprite().getName();
            if (var26 == null) {
               var26 = UIManager.LastPicked.getSpriteName();
            }

            this.DrawString(10, var23 += var7, "UIManager.LastPicked=" + UIManager.LastPicked.getClass().getName() + " sprite=" + var26 + " x,y,z=" + UIManager.LastPicked.getX() + "," + UIManager.LastPicked.getY() + "," + UIManager.LastPicked.getZ());
         }

         IsoObject var27 = IsoObjectPicker.Instance.PickWindow(Mouse.getXA(), Mouse.getYA());
         this.DrawString(10, var23 += var7, "PickWindow=" + var27);
         var27 = IsoObjectPicker.Instance.PickCorpse(Mouse.getXA(), Mouse.getYA());
         this.DrawString(10, var23 += var7, "PickCorpse=" + var27);
         IsoChunk var28 = IsoWorld.instance.CurrentCell.getChunkForGridSquare(this.gridX, this.gridY, var25);
         if (var28 != null) {
            var23 += 24;
            this.DrawString(10, var23, "FloorBloodSplats " + var28.FloorBloodSplats.size());
         }

         LosUtil.TestResults var29 = LosUtil.lineClear(IsoWorld.instance.getCell(), (int)IsoPlayer.instance.x, (int)IsoPlayer.instance.y, (int)IsoPlayer.instance.z, this.gridX, this.gridY, (int)IsoPlayer.instance.z, false);
         LosUtil.TestResults var38 = LosUtil.lineClear(IsoWorld.instance.getCell(), (int)IsoPlayer.instance.x, (int)IsoPlayer.instance.y, (int)IsoPlayer.instance.z, this.gridX, this.gridY, (int)IsoPlayer.instance.z + 1, false);
         LosUtil.TestResults var41 = LosUtil.lineClear(IsoWorld.instance.getCell(), (int)IsoPlayer.instance.x, (int)IsoPlayer.instance.y, (int)IsoPlayer.instance.z, this.gridX, this.gridY, (int)IsoPlayer.instance.z - 1, false);
         this.DrawString(10, var23 + var7, "lineClear=" + var29.toString() + (var25 < 7 ? "    above=" + var38.toString() : "") + (var25 > 0 ? "    below=" + var41.toString() : ""));
         var12 = IsoWorld.instance.CurrentCell.getZombieList().size();
         GameTime var42 = GameTime.getInstance();
         this.DrawString(10, Core.getInstance().getScreenHeight() - 80, "Real zombies: " + var12);
         this.DrawString(10, Core.getInstance().getScreenHeight() - 40, "Dawn/Dusk: " + var42.getDawn() + "/" + var42.getDusk() + " Night min/cur/max: " + var42.getNightMin() + "/" + var42.getNight() + "/" + var42.getNightMax() + " Night Tint: " + var42.getNightTint());
         this.DrawString(10, Core.getInstance().getScreenHeight() - 20, "Lights active/total: " + var3 + "/" + var2.size());
         IsoPlayer var49 = IsoPlayer.instance;
         TextManager.instance.DrawStringCentre(this.FONT, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 40), "Player current x,y,z=" + var49.getX() + "," + var49.getY() + "," + var49.getZ() + "(" + (var49.getCurrentSquare() != null ? var49.getCurrentSquare().x + "," + var49.getCurrentSquare().y + "," + var49.getCurrentSquare().z : "<null>") + ")", 1.0D, 1.0D, 1.0D, 1.0D);
         if (IsoPlayer.instance.last != null) {
            TextManager.instance.DrawStringCentre(this.FONT, (double)(Core.getInstance().getScreenWidth() / 2), (double)(Core.getInstance().getScreenHeight() - 20), "Player last x,y,z=" + var49.last.getX() + "," + var49.last.getY() + "," + var49.last.getZ(), 1.0D, 1.0D, 1.0D, 1.0D);
         }

         this.drawModData();
         this.drawPlayerInfo();
      }

      Core.getInstance().EndFrameUI();
   }

   public GameStateMachine.StateAction update() {
      if (GameKeyboard.isKeyDown(60)) {
         if (!this.keyDown) {
            this.keyDown = true;
            return GameStateMachine.StateAction.Continue;
         }

         this.keyDown = true;
      } else if (this.keyDown) {
         this.keyDown = false;
      }

      if (GameKeyboard.isKeyDown(16)) {
         if (!keyQpressed) {
            IsoGridSquare var1 = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, 0);
            if (var1 != null) {
               GameClient.instance.worldObjectsSyncReq.putRequestSyncIsoChunk(var1.chunk);
            }

            keyQpressed = true;
         }
      } else {
         keyQpressed = false;
      }

      if (GameKeyboard.isKeyDown(19)) {
         if (!keyQpressed) {
            IsoCell.newRender = true;
            keyQpressed = true;
         }
      } else {
         keyQpressed = false;
      }

      if (GameKeyboard.isKeyDown(20)) {
         if (!keyQpressed) {
            IsoCell.newRender = false;
            keyQpressed = true;
         }
      } else {
         keyQpressed = false;
      }

      IsoCamera.update();
      this.updateCursor();
      return GameStateMachine.StateAction.Remain;
   }

   private void updateCursor() {
      int var1 = Core.TileScale;
      float var2 = (float)org.lwjgl.input.Mouse.getX();
      float var3 = (float)(Core.getInstance().getScreenHeight() - org.lwjgl.input.Mouse.getY() - 1);
      var2 -= (float)IsoCamera.getScreenLeft(IsoPlayer.getPlayerIndex());
      var3 -= (float)IsoCamera.getScreenTop(IsoPlayer.getPlayerIndex());
      var2 *= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
      var3 *= Core.getInstance().getZoom(IsoPlayer.getPlayerIndex());
      int var4 = (int)IsoPlayer.instance.getZ();
      this.gridX = (int)IsoUtils.XToIso(var2 - (float)(0 * var1), var3 - 0.0F, (float)var4);
      this.gridY = (int)IsoUtils.YToIso(var2 - (float)(0 * var1), var3 - 0.0F, (float)var4);
   }

   private void DrawIsoLine(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9) {
      float var10 = (float)((int)IsoPlayer.instance.getZ());
      float var11 = IsoUtils.XToScreenExact(var1, var2, var10, 0);
      float var12 = IsoUtils.YToScreenExact(var1, var2, var10, 0);
      float var13 = IsoUtils.XToScreenExact(var3, var4, var10, 0);
      float var14 = IsoUtils.YToScreenExact(var3, var4, var10, 0);
      LineDrawer.drawLine(var11, var12, var13, var14, var5, var6, var7, var8, var9);
   }

   private void drawGrid() {
      float var1 = IsoUtils.XToIso(-128.0F, -256.0F, 0.0F);
      float var2 = IsoUtils.YToIso((float)(Core.getInstance().getOffscreenWidth(IsoPlayer.getPlayerIndex()) + 128), -256.0F, 0.0F);
      float var3 = IsoUtils.XToIso((float)(Core.getInstance().getOffscreenWidth(IsoPlayer.getPlayerIndex()) + 128), (float)(Core.getInstance().getOffscreenHeight(IsoPlayer.getPlayerIndex()) + 256), 6.0F);
      float var4 = IsoUtils.YToIso(-128.0F, (float)(Core.getInstance().getOffscreenHeight(IsoPlayer.getPlayerIndex()) + 256), 6.0F);
      int var6 = (int)var2;
      int var8 = (int)var4;
      int var5 = (int)var1;
      int var7 = (int)var3;
      var5 -= 2;
      var6 -= 2;

      int var9;
      for(var9 = var6; var9 <= var8; ++var9) {
         if (var9 % 10 == 0) {
            this.DrawIsoLine((float)var5, (float)var9, (float)var7, (float)var9, 1.0F, 1.0F, 1.0F, 0.5F, 1);
         }
      }

      for(var9 = var5; var9 <= var7; ++var9) {
         if (var9 % 10 == 0) {
            this.DrawIsoLine((float)var9, (float)var6, (float)var9, (float)var8, 1.0F, 1.0F, 1.0F, 0.5F, 1);
         }
      }

      if (GameClient.bClient) {
         for(var9 = var6; var9 <= var8; ++var9) {
            if (var9 % 70 == 0) {
               this.DrawIsoLine((float)var5, (float)var9, (float)var7, (float)var9, 1.0F, 0.0F, 0.0F, 0.5F, 1);
            }
         }

         for(var9 = var5; var9 <= var7; ++var9) {
            if (var9 % 70 == 0) {
               this.DrawIsoLine((float)var9, (float)var6, (float)var9, (float)var8, 1.0F, 0.0F, 0.0F, 0.5F, 1);
            }
         }
      }

   }

   private void drawCursor() {
      int var1 = Core.TileScale;
      float var2 = (float)((int)IsoPlayer.instance.getZ());
      int var3 = (int)IsoUtils.XToScreenExact((float)this.gridX, (float)(this.gridY + 1), var2, 0);
      int var4 = (int)IsoUtils.YToScreenExact((float)this.gridX, (float)(this.gridY + 1), var2, 0);
      SpriteRenderer.instance.renderPoly(var3, var4, var3 + 32 * var1, var4 - 16 * var1, var3 + 64 * var1, var4, var3 + 32 * var1, var4 + 16 * var1, 0.0F, 0.0F, 1.0F, 0.5F);
      IsoChunkMap var5 = IsoWorld.instance.getCell().ChunkMap[IsoPlayer.getPlayerIndex()];

      int var6;
      for(var6 = var5.getWorldYMinTiles(); var6 < var5.getWorldYMaxTiles(); ++var6) {
         for(int var7 = var5.getWorldXMinTiles(); var7 < var5.getWorldXMaxTiles(); ++var7) {
            IsoGridSquare var8 = IsoWorld.instance.getCell().getGridSquare((double)var7, (double)var6, (double)var2);
            if (var8 != null) {
               if (var8 != var5.getGridSquare(var7, var6, (int)var2)) {
                  var3 = (int)IsoUtils.XToScreenExact((float)var7, (float)(var6 + 1), var2, 0);
                  var4 = (int)IsoUtils.YToScreenExact((float)var7, (float)(var6 + 1), var2, 0);
                  SpriteRenderer.instance.renderPoly(var3, var4, var3 + 32, var4 - 16, var3 + 64, var4, var3 + 32, var4 + 16, 1.0F, 0.0F, 0.0F, 0.8F);
               }

               if (var8 == null || var8.getX() != var7 || var8.getY() != var6 || (float)var8.getZ() != var2 || var8.e != null && var8.e.w != null && var8.e.w != var8 || var8.w != null && var8.w.e != null && var8.w.e != var8 || var8.n != null && var8.n.s != null && var8.n.s != var8 || var8.s != null && var8.s.n != null && var8.s.n != var8 || var8.nw != null && var8.nw.se != null && var8.nw.se != var8 || var8.se != null && var8.se.nw != null && var8.se.nw != var8) {
                  var3 = (int)IsoUtils.XToScreenExact((float)var7, (float)(var6 + 1), var2, 0);
                  var4 = (int)IsoUtils.YToScreenExact((float)var7, (float)(var6 + 1), var2, 0);
                  SpriteRenderer.instance.renderPoly(var3, var4, var3 + 32, var4 - 16, var3 + 64, var4, var3 + 32, var4 + 16, 1.0F, 0.0F, 0.0F, 0.5F);
               }

               if (var8 != null) {
                  IsoGridSquare var9 = var8.testPathFindAdjacent((IsoMovingObject)null, -1, 0, 0) ? null : var8.nav[IsoDirections.W.index()];
                  IsoGridSquare var10 = var8.testPathFindAdjacent((IsoMovingObject)null, 0, -1, 0) ? null : var8.nav[IsoDirections.N.index()];
                  IsoGridSquare var11 = var8.testPathFindAdjacent((IsoMovingObject)null, 1, 0, 0) ? null : var8.nav[IsoDirections.E.index()];
                  IsoGridSquare var12 = var8.testPathFindAdjacent((IsoMovingObject)null, 0, 1, 0) ? null : var8.nav[IsoDirections.S.index()];
                  IsoGridSquare var13 = var8.testPathFindAdjacent((IsoMovingObject)null, -1, -1, 0) ? null : var8.nav[IsoDirections.NW.index()];
                  IsoGridSquare var14 = var8.testPathFindAdjacent((IsoMovingObject)null, 1, -1, 0) ? null : var8.nav[IsoDirections.NE.index()];
                  IsoGridSquare var15 = var8.testPathFindAdjacent((IsoMovingObject)null, -1, 1, 0) ? null : var8.nav[IsoDirections.SW.index()];
                  IsoGridSquare var16 = var8.testPathFindAdjacent((IsoMovingObject)null, 1, 1, 0) ? null : var8.nav[IsoDirections.SE.index()];
                  if (var9 != var8.w || var10 != var8.n || var11 != var8.e || var12 != var8.s || var13 != var8.nw || var14 != var8.ne || var15 != var8.sw || var16 != var8.se) {
                     this.paintSquare(var7, var6, (int)var2, 1.0F, 0.0F, 0.0F, 0.5F);
                  }
               }

               if (var8 != null && (var8.nav[IsoDirections.NW.index()] != null && var8.nav[IsoDirections.NW.index()].nav[IsoDirections.SE.index()] != var8 || var8.nav[IsoDirections.NE.index()] != null && var8.nav[IsoDirections.NE.index()].nav[IsoDirections.SW.index()] != var8 || var8.nav[IsoDirections.SW.index()] != null && var8.nav[IsoDirections.SW.index()].nav[IsoDirections.NE.index()] != var8 || var8.nav[IsoDirections.SE.index()] != null && var8.nav[IsoDirections.SE.index()].nav[IsoDirections.NW.index()] != var8 || var8.nav[IsoDirections.N.index()] != null && var8.nav[IsoDirections.N.index()].nav[IsoDirections.S.index()] != var8 || var8.nav[IsoDirections.S.index()] != null && var8.nav[IsoDirections.S.index()].nav[IsoDirections.N.index()] != var8 || var8.nav[IsoDirections.W.index()] != null && var8.nav[IsoDirections.W.index()].nav[IsoDirections.E.index()] != var8 || var8.nav[IsoDirections.E.index()] != null && var8.nav[IsoDirections.E.index()].nav[IsoDirections.W.index()] != var8)) {
                  var3 = (int)IsoUtils.XToScreenExact((float)var7, (float)(var6 + 1), var2, 0);
                  var4 = (int)IsoUtils.YToScreenExact((float)var7, (float)(var6 + 1), var2, 0);
                  SpriteRenderer.instance.renderPoly(var3, var4, var3 + 32, var4 - 16, var3 + 64, var4, var3 + 32, var4 + 16, 1.0F, 0.0F, 0.0F, 0.5F);
               }

               if (var8.getObjects().isEmpty()) {
                  this.paintSquare(var7, var6, (int)var2, 1.0F, 1.0F, 0.0F, 0.5F);
               }

               if (var8.getRoom() != null && var8.isFree(false) && !VirtualZombieManager.instance.canSpawnAt(var7, var6, (int)var2)) {
                  this.paintSquare(var7, var6, (int)var2, 1.0F, 1.0F, 1.0F, 1.0F);
               }

               if (var8.roofHideBuilding != null) {
                  this.paintSquare(var7, var6, (int)var2, 0.0F, 0.0F, 1.0F, 0.25F);
               }
            }
         }
      }

      if (Math.abs(this.gridX - (int)IsoPlayer.instance.x) <= 1 && Math.abs(this.gridY - (int)IsoPlayer.instance.y) <= 1) {
         IsoGridSquare var17 = IsoWorld.instance.CurrentCell.getGridSquare(this.gridX, this.gridY, (int)IsoPlayer.instance.z);
         IsoObject var18 = IsoPlayer.instance.getCurrentSquare().testCollideSpecialObjects(var17);
         if (var18 != null) {
            var18.getSprite().RenderGhostTileRed((int)var18.getX(), (int)var18.getY(), (int)var18.getZ());
         }
      }

      this.lineClearCached(IsoWorld.instance.CurrentCell, this.gridX, this.gridY, (int)var2, (int)IsoPlayer.instance.getX(), (int)IsoPlayer.instance.getY(), (int)IsoPlayer.instance.getZ(), false);

      for(var6 = 0; var6 < IsoWorld.instance.CurrentCell.getVehicles().size(); ++var6) {
         BaseVehicle var19 = (BaseVehicle)IsoWorld.instance.CurrentCell.getVehicles().get(var6);

         for(int var20 = -4; var20 <= 4; ++var20) {
            for(int var21 = -4; var21 <= 4; ++var21) {
               if (var19.isIntersectingSquare((int)var19.getX() + var21, (int)var19.getY() + var20, (int)var19.getZ())) {
                  this.paintSquare((int)var19.getX() + var21, (int)var19.getY() + var20, (int)var19.getZ(), 1.0F, 0.0F, 0.0F, 0.5F);
               }
            }
         }
      }

   }

   private void DrawBehindStuff() {
      this.IsBehindStuff(IsoPlayer.instance.getCurrentSquare());
   }

   private boolean IsBehindStuff(IsoGridSquare var1) {
      for(int var2 = 1; var2 < 8 && var1.getZ() + var2 < 8; ++var2) {
         for(int var3 = -5; var3 <= 6; ++var3) {
            for(int var4 = -5; var4 <= 6; ++var4) {
               if (var4 >= var3 - 5 && var4 <= var3 + 5) {
                  this.paintSquare(var1.getX() + var4 + var2 * 3, var1.getY() + var3 + var2 * 3, var1.getZ() + var2, 1.0F, 1.0F, 0.0F, 0.25F);
               }
            }
         }
      }

      return true;
   }

   private boolean IsBehindStuffRecY(int var1, int var2, int var3) {
      IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, var3);
      if (var3 >= 15) {
         return false;
      } else {
         this.paintSquare(var1, var2, var3, 1.0F, 1.0F, 0.0F, 0.25F);
         return this.IsBehindStuffRecY(var1, var2 + 1, var3 + 1);
      }
   }

   private boolean IsBehindStuffRecXY(int var1, int var2, int var3, int var4) {
      IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, var3);
      if (var3 >= 15) {
         return false;
      } else {
         this.paintSquare(var1, var2, var3, 1.0F, 1.0F, 0.0F, 0.25F);
         return this.IsBehindStuffRecXY(var1 + var4, var2 + var4, var3 + 1, var4);
      }
   }

   private boolean IsBehindStuffRecX(int var1, int var2, int var3) {
      IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, var3);
      if (var3 >= 15) {
         return false;
      } else {
         this.paintSquare(var1, var2, var3, 1.0F, 1.0F, 0.0F, 0.25F);
         return this.IsBehindStuffRecX(var1 + 1, var2, var3 + 1);
      }
   }

   private void paintSquare(int var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      int var8 = Core.TileScale;
      int var9 = (int)IsoUtils.XToScreenExact((float)var1, (float)(var2 + 1), (float)var3, 0);
      int var10 = (int)IsoUtils.YToScreenExact((float)var1, (float)(var2 + 1), (float)var3, 0);
      SpriteRenderer.instance.renderPoly(var9, var10, var9 + 32 * var8, var10 - 16 * var8, var9 + 64 * var8, var10, var9 + 32 * var8, var10 + 16 * var8, var4, var5, var6, var7);
   }

   void drawModData() {
      int var1 = (int)IsoPlayer.instance.getZ();
      IsoGridSquare var2 = IsoWorld.instance.getCell().getGridSquare(this.gridX, this.gridY, var1);
      int var3 = Core.getInstance().getScreenWidth() - 250;
      int var4 = 10;
      int var5 = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
      if (GameClient.bClient && var2 != null) {
         this.DrawString(var3, var4 += var5, "randomID=" + var2.chunk.randomID);
      }

      if (var2 != null && var2.getModData() != null) {
         KahluaTable var6 = var2.getModData();
         this.DrawString(var3, var4 += var5, "MOD DATA x,y,z=" + var2.getX() + "," + var2.getY() + "," + var2.getZ());
         KahluaTableIterator var7 = var6.iterator();

         label65:
         while(true) {
            do {
               if (!var7.advance()) {
                  var4 += var5;
                  break label65;
               }

               this.DrawString(var3, var4 += var5, var7.getKey().toString() + " = " + var7.getValue().toString());
            } while(!(var7.getValue() instanceof KahluaTable));

            KahluaTableIterator var8 = ((KahluaTable)var7.getValue()).iterator();

            while(var8.advance()) {
               this.DrawString(var3 + 8, var4 += var5, var8.getKey().toString() + " = " + var8.getValue().toString());
            }
         }
      }

      if (var2 != null) {
         PropertyContainer var12 = var2.getProperties();
         ArrayList var14 = var12.getPropertyNames();
         if (!var14.isEmpty()) {
            this.DrawString(var3, var4 += var5, "PROPERTIES x,y,z=" + var2.getX() + "," + var2.getY() + "," + var2.getZ());
            Collections.sort(var14);
            Iterator var15 = var14.iterator();

            while(var15.hasNext()) {
               String var9 = (String)var15.next();
               this.DrawString(var3, var4 += var5, var9 + " = \"" + var12.Val(var9) + "\"");
            }
         }

         IsoFlagType[] var16 = IsoFlagType.values();
         int var17 = var16.length;

         for(int var10 = 0; var10 < var17; ++var10) {
            IsoFlagType var11 = var16[var10];
            if (var12.Is(var11)) {
               this.DrawString(var3, var4 += var5, var11.toString());
            }
         }
      }

      if (var2 != null) {
         ErosionData.Square var13 = var2.getErosionData();
         if (var13 != null) {
            var4 += var5;
            this.DrawString(var3, var4 += var5, "EROSION x,y,z=" + var2.getX() + "," + var2.getY() + "," + var2.getZ());
            this.DrawString(var3, var4 += var5, "init=" + var13.init);
            this.DrawString(var3, var4 += var5, "doNothing=" + var13.doNothing);
            this.DrawString(var3, var4 + var5, "chunk.init=" + var2.chunk.getErosionData().init);
         }
      }

   }

   void drawPlayerInfo() {
      int var1 = Core.getInstance().getScreenWidth() - 250;
      int var2 = Core.getInstance().getScreenHeight() / 2;
      int var3 = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
      this.DrawString(var1, var2 += var3, "bored = " + IsoPlayer.instance.getBodyDamage().getBoredomLevel());
      this.DrawString(var1, var2 += var3, "endurance = " + IsoPlayer.instance.getStats().endurance);
      this.DrawString(var1, var2 += var3, "fatigue = " + IsoPlayer.instance.getStats().fatigue);
      this.DrawString(var1, var2 += var3, "hunger = " + IsoPlayer.instance.getStats().hunger);
      this.DrawString(var1, var2 += var3, "pain = " + IsoPlayer.instance.getStats().Pain);
      this.DrawString(var1, var2 += var3, "panic = " + IsoPlayer.instance.getStats().Panic);
      this.DrawString(var1, var2 += var3, "stress = " + IsoPlayer.instance.getStats().getStress());
      this.DrawString(var1, var2 += var3, "clothingTemp = " + IsoPlayer.instance.getPlayerClothingTemperature());
      this.DrawString(var1, var2 += var3, "temperature = " + IsoPlayer.instance.getTemperature());
      this.DrawString(var1, var2 += var3, "thirst = " + IsoPlayer.instance.getStats().thirst);
      this.DrawString(var1, var2 += var3, "foodPoison = " + IsoPlayer.instance.getBodyDamage().getFoodSicknessLevel());
      this.DrawString(var1, var2 += var3, "poison = " + IsoPlayer.instance.getBodyDamage().getPoisonLevel());
      this.DrawString(var1, var2 += var3, "unhappy = " + IsoPlayer.instance.getBodyDamage().getUnhappynessLevel());
      this.DrawString(var1, var2 += var3, "infected = " + IsoPlayer.instance.getBodyDamage().isInfected());
      this.DrawString(var1, var2 += var3, "InfectionLevel = " + IsoPlayer.instance.getBodyDamage().getInfectionLevel());
      this.DrawString(var1, var2 += var3, "FakeInfectionLevel = " + IsoPlayer.instance.getBodyDamage().getFakeInfectionLevel());
      var2 += var3;
      this.DrawString(var1, var2 += var3, "WORLD");
      this.DrawString(var1, var2 + var3, "globalTemperature = " + IsoWorld.instance.getGlobalTemperature());
   }

   public LosUtil.TestResults lineClearCached(IsoCell var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8) {
      int var12 = var3 - var6;
      int var13 = var2 - var5;
      int var14 = var4 - var7;
      int var15 = var13 + 100;
      int var16 = var12 + 100;
      int var17 = var14 + 16;
      if (var15 >= 0 && var16 >= 0 && var17 >= 0 && var15 < 200 && var16 < 200) {
         LosUtil.TestResults var18 = LosUtil.TestResults.Clear;
         byte var19 = 1;
         float var20 = 0.5F;
         float var21 = 0.5F;
         IsoGridSquare var25 = var1.getGridSquare(var5, var6, var7);
         int var23;
         int var24;
         float var26;
         float var27;
         IsoGridSquare var28;
         if (Math.abs(var13) > Math.abs(var12) && Math.abs(var13) > Math.abs(var14)) {
            var26 = (float)var12 / (float)var13;
            var27 = (float)var14 / (float)var13;
            var20 += (float)var6;
            var21 += (float)var7;
            var13 = var13 < 0 ? -1 : 1;
            var26 *= (float)var13;

            for(var27 *= (float)var13; var5 != var2; var24 = (int)var21) {
               var5 += var13;
               var20 += var26;
               var21 += var27;
               var28 = var1.getGridSquare(var5, (int)var20, (int)var21);
               this.paintSquare(var5, (int)var20, (int)var21, 1.0F, 1.0F, 1.0F, 0.5F);
               if (var28 != null && var25 != null && var28.testVisionAdjacent(var25.getX() - var28.getX(), var25.getY() - var28.getY(), var25.getZ() - var28.getZ(), true, var8) == LosUtil.TestResults.Blocked) {
                  this.paintSquare(var5, (int)var20, (int)var21, 1.0F, 0.0F, 0.0F, 0.5F);
                  this.paintSquare(var25.getX(), var25.getY(), var25.getZ(), 1.0F, 0.0F, 0.0F, 0.5F);
                  var19 = 4;
               }

               var25 = var28;
               var23 = (int)var20;
            }
         } else {
            int var22;
            if (Math.abs(var12) >= Math.abs(var13) && Math.abs(var12) > Math.abs(var14)) {
               var26 = (float)var13 / (float)var12;
               var27 = (float)var14 / (float)var12;
               var20 += (float)var5;
               var21 += (float)var7;
               var12 = var12 < 0 ? -1 : 1;
               var26 *= (float)var12;

               for(var27 *= (float)var12; var6 != var3; var24 = (int)var21) {
                  var6 += var12;
                  var20 += var26;
                  var21 += var27;
                  var28 = var1.getGridSquare((int)var20, var6, (int)var21);
                  this.paintSquare((int)var20, var6, (int)var21, 1.0F, 1.0F, 1.0F, 0.5F);
                  if (var28 != null && var25 != null && var28.testVisionAdjacent(var25.getX() - var28.getX(), var25.getY() - var28.getY(), var25.getZ() - var28.getZ(), true, var8) == LosUtil.TestResults.Blocked) {
                     this.paintSquare((int)var20, var6, (int)var21, 1.0F, 0.0F, 0.0F, 0.5F);
                     this.paintSquare(var25.getX(), var25.getY(), var25.getZ(), 1.0F, 0.0F, 0.0F, 0.5F);
                     var19 = 4;
                  }

                  var25 = var28;
                  var22 = (int)var20;
               }
            } else {
               var26 = (float)var13 / (float)var14;
               var27 = (float)var12 / (float)var14;
               var20 += (float)var5;
               var21 += (float)var6;
               var14 = var14 < 0 ? -1 : 1;
               var26 *= (float)var14;

               for(var27 *= (float)var14; var7 != var4; var23 = (int)var21) {
                  var7 += var14;
                  var20 += var26;
                  var21 += var27;
                  var28 = var1.getGridSquare((int)var20, (int)var21, var7);
                  this.paintSquare((int)var20, (int)var21, var7, 1.0F, 1.0F, 1.0F, 0.5F);
                  if (var28 != null && var25 != null && var28.testVisionAdjacent(var25.getX() - var28.getX(), var25.getY() - var28.getY(), var25.getZ() - var28.getZ(), true, var8) == LosUtil.TestResults.Blocked) {
                     var19 = 4;
                  }

                  var25 = var28;
                  var22 = (int)var20;
               }
            }
         }

         if (var19 == 1) {
            return LosUtil.TestResults.Clear;
         } else if (var19 == 2) {
            return LosUtil.TestResults.ClearThroughOpenDoor;
         } else if (var19 == 3) {
            return LosUtil.TestResults.ClearThroughWindow;
         } else {
            return var19 == 4 ? LosUtil.TestResults.Blocked : LosUtil.TestResults.Blocked;
         }
      } else {
         return LosUtil.TestResults.Blocked;
      }
   }

   private void DrawString(int var1, int var2, String var3) {
      int var4 = TextManager.instance.MeasureStringX(this.FONT, var3);
      int var5 = TextManager.instance.getFontFromEnum(this.FONT).getLineHeight();
      SpriteRenderer.instance.render((Texture)null, var1 - 1, var2, var4 + 2, var5, 0.0F, 0.0F, 0.0F, 0.8F);
      TextManager.instance.DrawString(this.FONT, (double)var1, (double)var2, var3, 1.0D, 1.0D, 1.0D, 1.0D);
   }

   private class FloodFill {
      private IsoGridSquare start = null;
      private final int FLOOD_SIZE = 11;
      private BooleanGrid visited = new BooleanGrid(11, 11);
      private Stack stack = new Stack();
      private IsoBuilding building = null;
      private Mover mover = null;

      void calculate(Mover var1, IsoGridSquare var2) {
         this.start = var2;
         this.mover = var1;
         if (this.start.getRoom() != null) {
            this.building = this.start.getRoom().getBuilding();
         }

         boolean var3 = false;
         boolean var4 = false;
         if (this.push(this.start.getX(), this.start.getY())) {
            while((var2 = this.pop()) != null) {
               int var6 = var2.getX();

               int var5;
               for(var5 = var2.getY(); this.shouldVisit(var6, var5, var6, var5 - 1); --var5) {
               }

               var4 = false;
               var3 = false;

               while(true) {
                  this.visited.setValue(this.gridX(var6), this.gridY(var5), true);
                  if (!var3 && this.shouldVisit(var6, var5, var6 - 1, var5)) {
                     if (!this.push(var6 - 1, var5)) {
                        return;
                     }

                     var3 = true;
                  } else if (var3 && !this.shouldVisit(var6, var5, var6 - 1, var5)) {
                     var3 = false;
                  } else if (var3 && !this.shouldVisit(var6 - 1, var5, var6 - 1, var5 - 1) && !this.push(var6 - 1, var5)) {
                     return;
                  }

                  if (!var4 && this.shouldVisit(var6, var5, var6 + 1, var5)) {
                     if (!this.push(var6 + 1, var5)) {
                        return;
                     }

                     var4 = true;
                  } else if (var4 && !this.shouldVisit(var6, var5, var6 + 1, var5)) {
                     var4 = false;
                  } else if (var4 && !this.shouldVisit(var6 + 1, var5, var6 + 1, var5 - 1) && !this.push(var6 + 1, var5)) {
                     return;
                  }

                  ++var5;
                  if (!this.shouldVisit(var6, var5 - 1, var6, var5)) {
                     break;
                  }
               }
            }

         }
      }

      boolean shouldVisit(int var1, int var2, int var3, int var4) {
         if (this.gridX(var3) < 11 && this.gridX(var3) >= 0) {
            if (this.gridY(var4) < 11 && this.gridY(var4) >= 0) {
               if (this.visited.getValue(this.gridX(var3), this.gridY(var4))) {
                  return false;
               } else {
                  IsoGridSquare var5 = IsoWorld.instance.CurrentCell.getGridSquare(var3, var4, this.start.getZ());
                  if (var5 == null) {
                     return false;
                  } else if (!var5.Has(IsoObjectType.stairsBN) && !var5.Has(IsoObjectType.stairsMN) && !var5.Has(IsoObjectType.stairsTN)) {
                     if (!var5.Has(IsoObjectType.stairsBW) && !var5.Has(IsoObjectType.stairsMW) && !var5.Has(IsoObjectType.stairsTW)) {
                        if (var5.getRoom() != null && this.building == null) {
                           return false;
                        } else if (var5.getRoom() == null && this.building != null) {
                           return false;
                        } else {
                           return !IsoWorld.instance.CurrentCell.blocked(this.mover, var3, var4, this.start.getZ(), var1, var2, this.start.getZ());
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      boolean push(int var1, int var2) {
         IsoGridSquare var3 = IsoWorld.instance.CurrentCell.getGridSquare(var1, var2, this.start.getZ());
         this.stack.push(var3);
         return true;
      }

      IsoGridSquare pop() {
         return this.stack.isEmpty() ? null : (IsoGridSquare)this.stack.pop();
      }

      int gridX(int var1) {
         return var1 - (this.start.getX() - 5);
      }

      int gridY(int var1) {
         return var1 - (this.start.getY() - 5);
      }

      int gridX(IsoGridSquare var1) {
         return var1.getX() - (this.start.getX() - 5);
      }

      int gridY(IsoGridSquare var1) {
         return var1.getY() - (this.start.getY() - 5);
      }

      void draw() {
         int var1 = this.start.getX() - 5;
         int var2 = this.start.getY() - 5;

         for(int var3 = 0; var3 < 11; ++var3) {
            for(int var4 = 0; var4 < 11; ++var4) {
               if (this.visited.getValue(var4, var3)) {
                  int var5 = (int)IsoUtils.XToScreenExact((float)(var1 + var4), (float)(var2 + var3 + 1), (float)this.start.getZ(), 0);
                  int var6 = (int)IsoUtils.YToScreenExact((float)(var1 + var4), (float)(var2 + var3 + 1), (float)this.start.getZ(), 0);
                  SpriteRenderer.instance.renderPoly(var5, var6, var5 + 32, var6 - 16, var5 + 64, var6, var5 + 32, var6 + 16, 1.0F, 1.0F, 0.0F, 0.5F);
               }
            }
         }

      }
   }
}
