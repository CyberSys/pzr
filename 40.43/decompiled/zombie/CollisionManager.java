package zombie;

import java.util.ArrayList;
import java.util.Stack;
import zombie.characters.IsoSurvivor;
import zombie.characters.ZombieFootstepManager;
import zombie.characters.ZombieThumpManager;
import zombie.characters.ZombieVocalsManager;
import zombie.core.collision.Polygon;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoPushableObject;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;

public class CollisionManager {
   static Vector2 temp = new Vector2();
   static Vector2 axis = new Vector2();
   static Polygon polygonA = new Polygon();
   static Polygon polygonB = new Polygon();
   float minA = 0.0F;
   float minB = 0.0F;
   float maxA = 0.0F;
   float maxB = 0.0F;
   CollisionManager.PolygonCollisionResult result = new CollisionManager.PolygonCollisionResult();
   public ArrayList ContactMap = new ArrayList();
   Long[] longArray = new Long[1000];
   Stack contacts = new Stack();
   Vector2 vel = new Vector2();
   Vector2 vel2 = new Vector2();
   static ArrayList pushables = new ArrayList();
   public static CollisionManager instance = new CollisionManager();

   private void ProjectPolygonA(Vector2 var1, Polygon var2) {
      float var3 = var1.dot((Vector2)var2.points.get(0));
      this.minA = var3;
      this.maxA = var3;

      for(int var4 = 0; var4 < var2.points.size(); ++var4) {
         var3 = ((Vector2)var2.points.get(var4)).dot(var1);
         if (var3 < this.minA) {
            this.minA = var3;
         } else if (var3 > this.maxA) {
            this.maxA = var3;
         }
      }

   }

   private void ProjectPolygonB(Vector2 var1, Polygon var2) {
      float var3 = var1.dot((Vector2)var2.points.get(0));
      this.minB = var3;
      this.maxB = var3;

      for(int var4 = 0; var4 < var2.points.size(); ++var4) {
         var3 = ((Vector2)var2.points.get(var4)).dot(var1);
         if (var3 < this.minB) {
            this.minB = var3;
         } else if (var3 > this.maxB) {
            this.maxB = var3;
         }
      }

   }

   public CollisionManager.PolygonCollisionResult PolygonCollision(Vector2 var1) {
      this.result.Intersect = true;
      this.result.WillIntersect = true;
      this.result.MinimumTranslationVector.x = 0.0F;
      this.result.MinimumTranslationVector.y = 0.0F;
      int var2 = polygonA.edges.size();
      int var3 = polygonB.edges.size();
      float var4 = Float.POSITIVE_INFINITY;
      Vector2 var5 = new Vector2();

      for(int var7 = 0; var7 < var2 + var3; ++var7) {
         Vector2 var6;
         if (var7 < var2) {
            var6 = (Vector2)polygonA.edges.get(var7);
         } else {
            var6 = (Vector2)polygonB.edges.get(var7 - var2);
         }

         axis.x = -var6.y;
         axis.y = var6.x;
         axis.normalize();
         this.minA = 0.0F;
         this.minB = 0.0F;
         this.maxA = 0.0F;
         this.maxB = 0.0F;
         this.ProjectPolygonA(axis, polygonA);
         this.ProjectPolygonB(axis, polygonB);
         if (this.IntervalDistance(this.minA, this.maxA, this.minB, this.maxB) > 0.0F) {
            this.result.Intersect = false;
         }

         float var8 = axis.dot(var1);
         if (var8 < 0.0F) {
            this.minA += var8;
         } else {
            this.maxA += var8;
         }

         float var9 = this.IntervalDistance(this.minA, this.maxA, this.minB, this.maxB);
         if (var9 > 0.0F) {
            this.result.WillIntersect = false;
         }

         if (!this.result.Intersect && !this.result.WillIntersect) {
            break;
         }

         var9 = Math.abs(var9);
         if (var9 < var4) {
            var4 = var9;
            var5.x = axis.x;
            var5.y = axis.y;
            temp.x = polygonA.Center().x - polygonB.Center().x;
            temp.y = polygonA.Center().y - polygonB.Center().y;
            if (temp.dot(var5) < 0.0F) {
               var5.x = -var5.x;
               var5.y = -var5.y;
            }
         }
      }

      if (this.result.WillIntersect) {
         this.result.MinimumTranslationVector.x = var5.x * var4;
         this.result.MinimumTranslationVector.y = var5.y * var4;
      }

      return this.result;
   }

   public float IntervalDistance(float var1, float var2, float var3, float var4) {
      return var1 < var3 ? var3 - var2 : var1 - var4;
   }

   public void initUpdate() {
      int var1;
      if (this.longArray[0] == null) {
         for(var1 = 0; var1 < this.longArray.length; ++var1) {
            this.longArray[var1] = new Long(0L);
         }
      }

      for(var1 = 0; var1 < this.ContactMap.size(); ++var1) {
         ((CollisionManager.Contact)this.ContactMap.get(var1)).a = null;
         ((CollisionManager.Contact)this.ContactMap.get(var1)).b = null;
         this.contacts.push(this.ContactMap.get(var1));
      }

      this.ContactMap.clear();
   }

   public void AddContact(IsoMovingObject var1, IsoMovingObject var2) {
      if (!(var1 instanceof IsoSurvivor) && !(var2 instanceof IsoSurvivor) || !(var1 instanceof IsoPushableObject) && !(var2 instanceof IsoPushableObject)) {
         if (var1.getID() < var2.getID()) {
            this.ContactMap.add(this.contact(var1, var2));
         }

      }
   }

   CollisionManager.Contact contact(IsoMovingObject var1, IsoMovingObject var2) {
      if (this.contacts.isEmpty()) {
         for(int var3 = 0; var3 < 50; ++var3) {
            this.contacts.push(new CollisionManager.Contact((IsoMovingObject)null, (IsoMovingObject)null));
         }
      }

      CollisionManager.Contact var4 = (CollisionManager.Contact)this.contacts.pop();
      var4.a = var1;
      var4.b = var2;
      return var4;
   }

   public void ResolveContacts() {
      ArrayList var1 = IsoWorld.instance.CurrentCell.getPushableObjectList();
      int var2 = var1.size();

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         IsoPushableObject var4 = (IsoPushableObject)var1.get(var3);
         if (var4.getImpulsex() != 0.0F || var4.getImpulsey() != 0.0F) {
            if (var4.connectList != null) {
               pushables.add(var4);
            } else {
               var4.setNx(var4.getNx() + var4.getImpulsex());
               var4.setNy(var4.getNy() + var4.getImpulsey());
               var4.setImpulsex(var4.getNx() - var4.getX());
               var4.setImpulsey(var4.getNy() - var4.getY());
               var4.setNx(var4.getX());
               var4.setNy(var4.getY());
            }
         }
      }

      var3 = pushables.size();

      float var7;
      int var21;
      for(var21 = 0; var21 < var3; ++var21) {
         IsoPushableObject var5 = (IsoPushableObject)pushables.get(var21);
         float var6 = 0.0F;
         var7 = 0.0F;

         int var8;
         for(var8 = 0; var8 < var5.connectList.size(); ++var8) {
            var6 += ((IsoPushableObject)var5.connectList.get(var8)).getImpulsex();
            var7 += ((IsoPushableObject)var5.connectList.get(var8)).getImpulsey();
         }

         var6 /= (float)var5.connectList.size();
         var7 /= (float)var5.connectList.size();

         for(var8 = 0; var8 < var5.connectList.size(); ++var8) {
            ((IsoPushableObject)var5.connectList.get(var8)).setImpulsex(var6);
            ((IsoPushableObject)var5.connectList.get(var8)).setImpulsey(var7);
            int var9 = pushables.indexOf(var5.connectList.get(var8));
            pushables.remove(var5.connectList.get(var8));
            if (var9 <= var21) {
               --var21;
            }
         }

         if (var21 < 0) {
            var21 = 0;
         }
      }

      pushables.clear();
      var21 = this.ContactMap.size();

      for(int var22 = 0; var22 < var21; ++var22) {
         CollisionManager.Contact var24 = (CollisionManager.Contact)this.ContactMap.get(var22);
         if (!(Math.abs(var24.a.getZ() - var24.b.getZ()) > 0.3F)) {
            this.vel.x = var24.a.getNx() - var24.a.getX();
            this.vel.y = var24.a.getNy() - var24.a.getY();
            this.vel2.x = var24.b.getNx() - var24.b.getX();
            this.vel2.y = var24.b.getNy() - var24.b.getY();
            if (this.vel.x != 0.0F || this.vel.y != 0.0F || this.vel2.x != 0.0F || this.vel2.y != 0.0F || var24.a.getImpulsex() != 0.0F || var24.a.getImpulsey() != 0.0F || var24.b.getImpulsex() != 0.0F || var24.b.getImpulsey() != 0.0F) {
               var7 = var24.a.getX() - var24.a.getWidth();
               float var27 = var24.a.getX() + var24.a.getWidth();
               float var28 = var24.a.getY() - var24.a.getWidth();
               float var10 = var24.a.getY() + var24.a.getWidth();
               float var11 = var24.b.getX() - var24.b.getWidth();
               float var12 = var24.b.getX() + var24.b.getWidth();
               float var13 = var24.b.getY() - var24.b.getWidth();
               float var14 = var24.b.getY() + var24.b.getWidth();
               polygonA.Set(var7, var28, var27, var10);
               polygonB.Set(var11, var13, var12, var14);
               CollisionManager.PolygonCollisionResult var15 = this.PolygonCollision(this.vel);
               if (var15.WillIntersect) {
                  var24.a.collideWith(var24.b);
                  var24.b.collideWith(var24.a);
                  float var16 = 1.0F - var24.a.getWeight(var15.MinimumTranslationVector.x, var15.MinimumTranslationVector.y) / (var24.a.getWeight(var15.MinimumTranslationVector.x, var15.MinimumTranslationVector.y) + var24.b.getWeight(var15.MinimumTranslationVector.x, var15.MinimumTranslationVector.y));
                  if (var24.a instanceof IsoPushableObject && var24.b instanceof IsoSurvivor) {
                     ((IsoSurvivor)var24.b).bCollidedWithPushable = true;
                     ((IsoSurvivor)var24.b).collidePushable = (IsoPushableObject)var24.a;
                  } else if (var24.b instanceof IsoPushableObject && var24.a instanceof IsoSurvivor) {
                     ((IsoSurvivor)var24.a).bCollidedWithPushable = true;
                     ((IsoSurvivor)var24.a).collidePushable = (IsoPushableObject)var24.b;
                  }

                  ArrayList var17;
                  int var18;
                  int var19;
                  IsoPushableObject var20;
                  if (var24.a instanceof IsoPushableObject) {
                     var17 = ((IsoPushableObject)var24.a).connectList;
                     if (var17 != null) {
                        var18 = var17.size();

                        for(var19 = 0; var19 < var18; ++var19) {
                           var20 = (IsoPushableObject)var17.get(var19);
                           var20.setImpulsex(var20.getImpulsex() + var15.MinimumTranslationVector.x * var16);
                           var20.setImpulsey(var20.getImpulsey() + var15.MinimumTranslationVector.y * var16);
                        }
                     }
                  } else {
                     var24.a.setImpulsex(var24.a.getImpulsex() + var15.MinimumTranslationVector.x * var16);
                     var24.a.setImpulsey(var24.a.getImpulsey() + var15.MinimumTranslationVector.y * var16);
                  }

                  if (var24.b instanceof IsoPushableObject) {
                     var17 = ((IsoPushableObject)var24.b).connectList;
                     if (var17 != null) {
                        var18 = var17.size();

                        for(var19 = 0; var19 < var18; ++var19) {
                           var20 = (IsoPushableObject)var17.get(var19);
                           var20.setImpulsex(var20.getImpulsex() - var15.MinimumTranslationVector.x * (1.0F - var16));
                           var20.setImpulsey(var20.getImpulsey() - var15.MinimumTranslationVector.y * (1.0F - var16));
                        }
                     }
                  } else {
                     var24.b.setImpulsex(var24.b.getImpulsex() - var15.MinimumTranslationVector.x * (1.0F - var16));
                     var24.b.setImpulsey(var24.b.getImpulsey() - var15.MinimumTranslationVector.y * (1.0F - var16));
                  }
               }
            }
         }
      }

      ArrayList var23 = IsoWorld.instance.CurrentCell.getObjectList();

      for(int var25 = 0; var25 < var23.size(); ++var25) {
         IsoMovingObject var26 = (IsoMovingObject)var23.get(var25);
         var26.postupdate();
         if (!var23.contains(var26)) {
            --var25;
         }
      }

      IsoMovingObject.treeSoundMgr.update();
      ZombieFootstepManager.instance.update();
      ZombieThumpManager.instance.update();
      ZombieVocalsManager.instance.update();
   }

   public class Contact {
      public IsoMovingObject a;
      public IsoMovingObject b;

      public Contact(IsoMovingObject var2, IsoMovingObject var3) {
         this.a = var2;
         this.b = var3;
      }
   }

   public class PolygonCollisionResult {
      public boolean WillIntersect;
      public boolean Intersect;
      public Vector2 MinimumTranslationVector = new Vector2();
   }
}
