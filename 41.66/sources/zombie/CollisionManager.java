package zombie;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import zombie.characters.IsoSurvivor;
import zombie.characters.ZombieFootstepManager;
import zombie.characters.ZombieThumpManager;
import zombie.characters.ZombieVocalsManager;
import zombie.core.collision.Polygon;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoPushableObject;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;


public final class CollisionManager {
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
	public static final CollisionManager instance = new CollisionManager();

	private void ProjectPolygonA(Vector2 vector2, Polygon polygon) {
		float float1 = vector2.dot((Vector2)polygon.points.get(0));
		this.minA = float1;
		this.maxA = float1;
		for (int int1 = 0; int1 < polygon.points.size(); ++int1) {
			float1 = ((Vector2)polygon.points.get(int1)).dot(vector2);
			if (float1 < this.minA) {
				this.minA = float1;
			} else if (float1 > this.maxA) {
				this.maxA = float1;
			}
		}
	}

	private void ProjectPolygonB(Vector2 vector2, Polygon polygon) {
		float float1 = vector2.dot((Vector2)polygon.points.get(0));
		this.minB = float1;
		this.maxB = float1;
		for (int int1 = 0; int1 < polygon.points.size(); ++int1) {
			float1 = ((Vector2)polygon.points.get(int1)).dot(vector2);
			if (float1 < this.minB) {
				this.minB = float1;
			} else if (float1 > this.maxB) {
				this.maxB = float1;
			}
		}
	}

	public CollisionManager.PolygonCollisionResult PolygonCollision(Vector2 vector2) {
		this.result.Intersect = true;
		this.result.WillIntersect = true;
		this.result.MinimumTranslationVector.x = 0.0F;
		this.result.MinimumTranslationVector.y = 0.0F;
		int int1 = polygonA.edges.size();
		int int2 = polygonB.edges.size();
		float float1 = Float.POSITIVE_INFINITY;
		Vector2 vector22 = new Vector2();
		for (int int3 = 0; int3 < int1 + int2; ++int3) {
			Vector2 vector23;
			if (int3 < int1) {
				vector23 = (Vector2)polygonA.edges.get(int3);
			} else {
				vector23 = (Vector2)polygonB.edges.get(int3 - int1);
			}

			axis.x = -vector23.y;
			axis.y = vector23.x;
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

			float float2 = axis.dot(vector2);
			if (float2 < 0.0F) {
				this.minA += float2;
			} else {
				this.maxA += float2;
			}

			float float3 = this.IntervalDistance(this.minA, this.maxA, this.minB, this.maxB);
			if (float3 > 0.0F) {
				this.result.WillIntersect = false;
			}

			if (!this.result.Intersect && !this.result.WillIntersect) {
				break;
			}

			float3 = Math.abs(float3);
			if (float3 < float1) {
				float1 = float3;
				vector22.x = axis.x;
				vector22.y = axis.y;
				temp.x = polygonA.Center().x - polygonB.Center().x;
				temp.y = polygonA.Center().y - polygonB.Center().y;
				if (temp.dot(vector22) < 0.0F) {
					vector22.x = -vector22.x;
					vector22.y = -vector22.y;
				}
			}
		}

		if (this.result.WillIntersect) {
			this.result.MinimumTranslationVector.x = vector22.x * float1;
			this.result.MinimumTranslationVector.y = vector22.y * float1;
		}

		return this.result;
	}

	public float IntervalDistance(float float1, float float2, float float3, float float4) {
		return float1 < float3 ? float3 - float2 : float1 - float4;
	}

	public void initUpdate() {
		int int1;
		if (this.longArray[0] == null) {
			for (int1 = 0; int1 < this.longArray.length; ++int1) {
				this.longArray[int1] = new Long(0L);
			}
		}

		for (int1 = 0; int1 < this.ContactMap.size(); ++int1) {
			((CollisionManager.Contact)this.ContactMap.get(int1)).a = null;
			((CollisionManager.Contact)this.ContactMap.get(int1)).b = null;
			this.contacts.push((CollisionManager.Contact)this.ContactMap.get(int1));
		}

		this.ContactMap.clear();
	}

	public void AddContact(IsoMovingObject movingObject, IsoMovingObject movingObject2) {
		if (!(movingObject instanceof IsoSurvivor) && !(movingObject2 instanceof IsoSurvivor) || !(movingObject instanceof IsoPushableObject) && !(movingObject2 instanceof IsoPushableObject)) {
			if (movingObject.getID() < movingObject2.getID()) {
				this.ContactMap.add(this.contact(movingObject, movingObject2));
			}
		}
	}

	CollisionManager.Contact contact(IsoMovingObject movingObject, IsoMovingObject movingObject2) {
		if (this.contacts.isEmpty()) {
			for (int int1 = 0; int1 < 50; ++int1) {
				this.contacts.push(new CollisionManager.Contact((IsoMovingObject)null, (IsoMovingObject)null));
			}
		}

		CollisionManager.Contact contact = (CollisionManager.Contact)this.contacts.pop();
		contact.a = movingObject;
		contact.b = movingObject2;
		return contact;
	}

	public void ResolveContacts() {
		CollisionManager.s_performance.profile_ResolveContacts.invokeAndMeasure(this, CollisionManager::resolveContactsInternal);
	}

	private void resolveContactsInternal() {
		Vector2 vector2 = CollisionManager.l_ResolveContacts.vel;
		Vector2 vector22 = CollisionManager.l_ResolveContacts.vel2;
		List list = CollisionManager.l_ResolveContacts.pushables;
		ArrayList arrayList = IsoWorld.instance.CurrentCell.getPushableObjectList();
		int int1 = arrayList.size();
		int int2;
		for (int2 = 0; int2 < int1; ++int2) {
			IsoPushableObject pushableObject = (IsoPushableObject)arrayList.get(int2);
			if (pushableObject.getImpulsex() != 0.0F || pushableObject.getImpulsey() != 0.0F) {
				if (pushableObject.connectList != null) {
					list.add(pushableObject);
				} else {
					pushableObject.setNx(pushableObject.getNx() + pushableObject.getImpulsex());
					pushableObject.setNy(pushableObject.getNy() + pushableObject.getImpulsey());
					pushableObject.setImpulsex(pushableObject.getNx() - pushableObject.getX());
					pushableObject.setImpulsey(pushableObject.getNy() - pushableObject.getY());
					pushableObject.setNx(pushableObject.getX());
					pushableObject.setNy(pushableObject.getY());
				}
			}
		}

		int2 = list.size();
		float float1;
		int int3;
		for (int3 = 0; int3 < int2; ++int3) {
			IsoPushableObject pushableObject2 = (IsoPushableObject)list.get(int3);
			float float2 = 0.0F;
			float1 = 0.0F;
			int int4;
			for (int4 = 0; int4 < pushableObject2.connectList.size(); ++int4) {
				float2 += ((IsoPushableObject)pushableObject2.connectList.get(int4)).getImpulsex();
				float1 += ((IsoPushableObject)pushableObject2.connectList.get(int4)).getImpulsey();
			}

			float2 /= (float)pushableObject2.connectList.size();
			float1 /= (float)pushableObject2.connectList.size();
			for (int4 = 0; int4 < pushableObject2.connectList.size(); ++int4) {
				((IsoPushableObject)pushableObject2.connectList.get(int4)).setImpulsex(float2);
				((IsoPushableObject)pushableObject2.connectList.get(int4)).setImpulsey(float1);
				int int5 = list.indexOf(pushableObject2.connectList.get(int4));
				list.remove(pushableObject2.connectList.get(int4));
				if (int5 <= int3) {
					--int3;
				}
			}

			if (int3 < 0) {
				int3 = 0;
			}
		}

		list.clear();
		int3 = this.ContactMap.size();
		for (int int6 = 0; int6 < int3; ++int6) {
			CollisionManager.Contact contact = (CollisionManager.Contact)this.ContactMap.get(int6);
			if (!(Math.abs(contact.a.getZ() - contact.b.getZ()) > 0.3F)) {
				vector2.x = contact.a.getNx() - contact.a.getX();
				vector2.y = contact.a.getNy() - contact.a.getY();
				vector22.x = contact.b.getNx() - contact.b.getX();
				vector22.y = contact.b.getNy() - contact.b.getY();
				if (vector2.x != 0.0F || vector2.y != 0.0F || vector22.x != 0.0F || vector22.y != 0.0F || contact.a.getImpulsex() != 0.0F || contact.a.getImpulsey() != 0.0F || contact.b.getImpulsex() != 0.0F || contact.b.getImpulsey() != 0.0F) {
					float1 = contact.a.getX() - contact.a.getWidth();
					float float3 = contact.a.getX() + contact.a.getWidth();
					float float4 = contact.a.getY() - contact.a.getWidth();
					float float5 = contact.a.getY() + contact.a.getWidth();
					float float6 = contact.b.getX() - contact.b.getWidth();
					float float7 = contact.b.getX() + contact.b.getWidth();
					float float8 = contact.b.getY() - contact.b.getWidth();
					float float9 = contact.b.getY() + contact.b.getWidth();
					polygonA.Set(float1, float4, float3, float5);
					polygonB.Set(float6, float8, float7, float9);
					CollisionManager.PolygonCollisionResult polygonCollisionResult = this.PolygonCollision(vector2);
					if (polygonCollisionResult.WillIntersect) {
						contact.a.collideWith(contact.b);
						contact.b.collideWith(contact.a);
						float float10 = 1.0F - contact.a.getWeight(polygonCollisionResult.MinimumTranslationVector.x, polygonCollisionResult.MinimumTranslationVector.y) / (contact.a.getWeight(polygonCollisionResult.MinimumTranslationVector.x, polygonCollisionResult.MinimumTranslationVector.y) + contact.b.getWeight(polygonCollisionResult.MinimumTranslationVector.x, polygonCollisionResult.MinimumTranslationVector.y));
						if (contact.a instanceof IsoPushableObject && contact.b instanceof IsoSurvivor) {
							((IsoSurvivor)contact.b).bCollidedWithPushable = true;
							((IsoSurvivor)contact.b).collidePushable = (IsoPushableObject)contact.a;
						} else if (contact.b instanceof IsoPushableObject && contact.a instanceof IsoSurvivor) {
							((IsoSurvivor)contact.a).bCollidedWithPushable = true;
							((IsoSurvivor)contact.a).collidePushable = (IsoPushableObject)contact.b;
						}

						ArrayList arrayList2;
						int int7;
						int int8;
						IsoPushableObject pushableObject3;
						if (contact.a instanceof IsoPushableObject) {
							arrayList2 = ((IsoPushableObject)contact.a).connectList;
							if (arrayList2 != null) {
								int7 = arrayList2.size();
								for (int8 = 0; int8 < int7; ++int8) {
									pushableObject3 = (IsoPushableObject)arrayList2.get(int8);
									pushableObject3.setImpulsex(pushableObject3.getImpulsex() + polygonCollisionResult.MinimumTranslationVector.x * float10);
									pushableObject3.setImpulsey(pushableObject3.getImpulsey() + polygonCollisionResult.MinimumTranslationVector.y * float10);
								}
							}
						} else {
							contact.a.setImpulsex(contact.a.getImpulsex() + polygonCollisionResult.MinimumTranslationVector.x * float10);
							contact.a.setImpulsey(contact.a.getImpulsey() + polygonCollisionResult.MinimumTranslationVector.y * float10);
						}

						if (contact.b instanceof IsoPushableObject) {
							arrayList2 = ((IsoPushableObject)contact.b).connectList;
							if (arrayList2 != null) {
								int7 = arrayList2.size();
								for (int8 = 0; int8 < int7; ++int8) {
									pushableObject3 = (IsoPushableObject)arrayList2.get(int8);
									pushableObject3.setImpulsex(pushableObject3.getImpulsex() - polygonCollisionResult.MinimumTranslationVector.x * (1.0F - float10));
									pushableObject3.setImpulsey(pushableObject3.getImpulsey() - polygonCollisionResult.MinimumTranslationVector.y * (1.0F - float10));
								}
							}
						} else {
							contact.b.setImpulsex(contact.b.getImpulsex() - polygonCollisionResult.MinimumTranslationVector.x * (1.0F - float10));
							contact.b.setImpulsey(contact.b.getImpulsey() - polygonCollisionResult.MinimumTranslationVector.y * (1.0F - float10));
						}
					}
				}
			}
		}

		ArrayList arrayList3 = IsoWorld.instance.CurrentCell.getObjectList();
		int int9 = arrayList3.size();
		MovingObjectUpdateScheduler.instance.postupdate();
		IsoMovingObject.treeSoundMgr.update();
		ZombieFootstepManager.instance.update();
		ZombieThumpManager.instance.update();
		ZombieVocalsManager.instance.update();
	}

	public class PolygonCollisionResult {
		public boolean WillIntersect;
		public boolean Intersect;
		public Vector2 MinimumTranslationVector = new Vector2();
	}

	public class Contact {
		public IsoMovingObject a;
		public IsoMovingObject b;

		public Contact(IsoMovingObject movingObject, IsoMovingObject movingObject2) {
			this.a = movingObject;
			this.b = movingObject2;
		}
	}

	private static class s_performance {
		static final PerformanceProfileProbe profile_ResolveContacts = new PerformanceProfileProbe("CollisionManager.ResolveContacts");
		static final PerformanceProfileProbe profile_MovingObjectPostUpdate = new PerformanceProfileProbe("IsoMovingObject.postupdate");
	}

	private static class l_ResolveContacts {
		static final Vector2 vel = new Vector2();
		static final Vector2 vel2 = new Vector2();
		static final List pushables = new ArrayList();
		static IsoMovingObject[] objectListInvoking = new IsoMovingObject[1024];
	}
}
