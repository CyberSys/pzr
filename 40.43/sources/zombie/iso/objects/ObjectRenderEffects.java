package zombie.iso.objects;

import java.util.ArrayDeque;
import java.util.ArrayList;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoObject;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameServer;


public class ObjectRenderEffects {
	public static final boolean ENABLED = true;
	private static ArrayDeque pool = new ArrayDeque();
	public double x1;
	public double y1;
	public double x2;
	public double y2;
	public double x3;
	public double y3;
	public double x4;
	public double y4;
	private double tx1;
	private double ty1;
	private double tx2;
	private double ty2;
	private double tx3;
	private double ty3;
	private double tx4;
	private double ty4;
	private double lx1;
	private double ly1;
	private double lx2;
	private double ly2;
	private double lx3;
	private double ly3;
	private double lx4;
	private double ly4;
	private double maxX;
	private double maxY;
	private float curTime = 0.0F;
	private float maxTime = 0.0F;
	private float totalTime = 0.0F;
	private float totalMaxTime = 0.0F;
	private RenderEffectType type;
	private IsoObject parent;
	private boolean finish = false;
	private boolean isTree = false;
	private boolean isBig = false;
	private boolean gust = false;
	private int windType = 1;
	private static float T_MOD = 1.0F;
	private static int windCount = 0;
	private static int windCountTree = 0;
	private static final int EFFECTS_COUNT = 15;
	private static final int TYPE_COUNT = 3;
	private static final ObjectRenderEffects[][] WIND_EFFECTS = new ObjectRenderEffects[3][15];
	private static final ObjectRenderEffects[][] WIND_EFFECTS_TREES = new ObjectRenderEffects[3][15];
	private static final ArrayList DYNAMIC_EFFECTS = new ArrayList();
	private static ObjectRenderEffects RANDOM_RUSTLE;
	private static float randomRustleTime = 0.0F;
	private static float randomRustleTotalTime = 0.0F;
	private static int randomRustleTarget = 0;
	private static int randomRustleType = 0;

	public static ObjectRenderEffects alloc() {
		ObjectRenderEffects objectRenderEffects = !pool.isEmpty() ? (ObjectRenderEffects)pool.pop() : new ObjectRenderEffects();
		return objectRenderEffects;
	}

	public static void release(ObjectRenderEffects objectRenderEffects) {
		assert !pool.contains(objectRenderEffects);
		pool.push(objectRenderEffects.reset());
	}

	private ObjectRenderEffects() {
	}

	private ObjectRenderEffects reset() {
		this.parent = null;
		this.finish = false;
		this.isBig = false;
		this.isTree = false;
		this.curTime = 0.0F;
		this.maxTime = 0.0F;
		this.totalTime = 0.0F;
		this.totalMaxTime = 0.0F;
		this.x1 = 0.0;
		this.y1 = 0.0;
		this.x2 = 0.0;
		this.y2 = 0.0;
		this.x3 = 0.0;
		this.y3 = 0.0;
		this.x4 = 0.0;
		this.y4 = 0.0;
		this.tx1 = 0.0;
		this.ty1 = 0.0;
		this.tx2 = 0.0;
		this.ty2 = 0.0;
		this.tx3 = 0.0;
		this.ty3 = 0.0;
		this.tx4 = 0.0;
		this.ty4 = 0.0;
		this.swapTargetToLast();
		return this;
	}

	public static ObjectRenderEffects getNew(IsoObject object, RenderEffectType renderEffectType, boolean boolean1) {
		return getNew(object, renderEffectType, boolean1, false);
	}

	public static ObjectRenderEffects getNew(IsoObject object, RenderEffectType renderEffectType, boolean boolean1, boolean boolean2) {
		if (GameServer.bServer) {
			return null;
		} else if (renderEffectType == RenderEffectType.Hit_Door && !Core.getInstance().getOptionDoDoorSpriteEffects()) {
			return null;
		} else {
			ObjectRenderEffects objectRenderEffects = null;
			try {
				boolean boolean3 = false;
				if (boolean1 && object != null && object.getObjectRenderEffects() != null && object.getObjectRenderEffects().type == renderEffectType) {
					objectRenderEffects = object.getObjectRenderEffects();
					boolean3 = true;
				} else {
					objectRenderEffects = alloc();
				}

				objectRenderEffects.type = renderEffectType;
				objectRenderEffects.parent = object;
				objectRenderEffects.finish = false;
				objectRenderEffects.isBig = false;
				objectRenderEffects.totalTime = 0.0F;
				switch (renderEffectType) {
				case Hit_Tree_Shudder: 
					objectRenderEffects.totalMaxTime = Rand.Next(45.0F, 60.0F) * T_MOD;
					break;
				
				case Vegetation_Rustle: 
					objectRenderEffects.totalMaxTime = Rand.Next(45.0F, 60.0F) * T_MOD;
					if (object != null && object instanceof IsoTree) {
						objectRenderEffects.isTree = true;
						objectRenderEffects.isBig = ((IsoTree)object).size > 4;
					}

					break;
				
				case Hit_Door: 
					objectRenderEffects.totalMaxTime = Rand.Next(15.0F, 30.0F) * T_MOD;
				
				}

				if (!boolean3 && object != null && object.getWindRenderEffects() != null && Core.getInstance().getOptionDoWindSpriteEffects()) {
					objectRenderEffects.copyMainFromOther(object.getWindRenderEffects());
				}

				if (!boolean3 && !boolean2) {
					DYNAMIC_EFFECTS.add(objectRenderEffects);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

			return objectRenderEffects;
		}
	}

	public static ObjectRenderEffects getNextWindEffect(int int1, boolean boolean1) {
		int int2 = int1 - 1;
		if (int2 >= 0 && int2 < 3) {
			if (boolean1) {
				if (++windCountTree >= 15) {
					windCountTree = 0;
				}

				return WIND_EFFECTS_TREES[int2][windCountTree];
			} else {
				if (++windCount >= 15) {
					windCount = 0;
				}

				return WIND_EFFECTS[int2][windCount];
			}
		} else {
			return null;
		}
	}

	public static void init() {
		if (!GameServer.bServer) {
			for (int int1 = 0; int1 < 3; ++int1) {
				int int2;
				ObjectRenderEffects objectRenderEffects;
				for (int2 = 0; int2 < 15; ++int2) {
					objectRenderEffects = new ObjectRenderEffects();
					objectRenderEffects.windType = int1 + 1;
					WIND_EFFECTS[int1][int2] = objectRenderEffects;
				}

				for (int2 = 0; int2 < 15; ++int2) {
					objectRenderEffects = new ObjectRenderEffects();
					objectRenderEffects.isTree = true;
					objectRenderEffects.windType = int1 + 1;
					WIND_EFFECTS_TREES[int1][int2] = objectRenderEffects;
				}
			}

			DYNAMIC_EFFECTS.clear();
			windCount = 0;
			windCountTree = 0;
			RANDOM_RUSTLE = null;
			randomRustleTime = 0.0F;
			randomRustleTotalTime = 0.0F;
			randomRustleTarget = 0;
		}
	}

	public boolean update() {
		this.curTime += 1.0F * GameTime.getInstance().getMultiplier();
		this.totalTime += 1.0F * GameTime.getInstance().getMultiplier();
		if (this.curTime > this.maxTime) {
			if (this.finish) {
				return false;
			}

			this.curTime = 0.0F;
			this.swapTargetToLast();
			float float1 = ClimateManager.clamp01(this.totalTime / this.totalMaxTime);
			float float2 = 1.0F - float1;
			double double1;
			double double2;
			switch (this.type) {
			case Hit_Tree_Shudder: 
				if (this.totalTime > this.totalMaxTime) {
					this.maxTime = 10.0F * T_MOD;
					this.tx1 = 0.0;
					this.tx2 = 0.0;
					this.finish = true;
				} else {
					this.maxTime = (3.0F + 15.0F * float1) * T_MOD;
					double1 = this.isBig ? (double)Rand.Next(-0.01F + -0.08F * float2, 0.01F + 0.08F * float2) : (double)Rand.Next(-0.02F + -0.16F * float2, 0.02F + 0.16F * float2);
					this.tx1 = double1;
					this.tx2 = double1;
				}

				break;
			
			case Vegetation_Rustle: 
				if (this.totalTime > this.totalMaxTime) {
					this.maxTime = 3.0F * T_MOD;
					this.tx1 = 0.0;
					this.tx2 = 0.0;
					this.finish = true;
				} else {
					this.maxTime = (2.0F + 6.0F * float1) * T_MOD;
					double1 = this.isBig ? (double)Rand.Next(-0.00625F, 0.00625F) : (double)Rand.Next(-0.015F, 0.015F);
					double2 = this.isBig ? (double)Rand.Next(-0.00625F, 0.00625F) : (double)Rand.Next(-0.015F, 0.015F);
					if (ClimateManager.getWindTickFinal() < 0.15) {
						double1 *= 0.6;
						double2 *= 0.6;
					}

					this.tx1 = double1;
					this.ty1 = double2;
					this.tx2 = double1;
					this.ty2 = double2;
				}

				break;
			
			case Hit_Door: 
				if (this.totalTime > this.totalMaxTime) {
					this.maxTime = 3.0F * T_MOD;
					this.tx1 = 0.0;
					this.tx2 = 0.0;
					this.finish = true;
				} else {
					this.maxTime = (1.0F + 2.0F * float1) * T_MOD;
					double1 = (double)Rand.Next(-0.005F, 0.005F);
					double2 = (double)Rand.Next(-0.0075F, 0.0075F);
					this.tx1 = double1;
					this.ty1 = double2;
					this.tx2 = double1;
					this.ty2 = double2;
					this.tx3 = double1;
					this.ty3 = double2;
					this.tx4 = double1;
					this.ty4 = double2;
				}

				break;
			
			default: 
				this.finish = true;
			
			}
		}

		this.lerpAll(this.curTime / this.maxTime);
		if (this.parent != null && this.parent.getWindRenderEffects() != null && Core.getInstance().getOptionDoWindSpriteEffects()) {
			this.add(this.parent.getWindRenderEffects());
		}

		return true;
	}

	private void update(float float1, float float2) {
		this.curTime += 1.0F * GameTime.getInstance().getMultiplier();
		if (this.curTime >= this.maxTime) {
			this.swapTargetToLast();
			float float3;
			float float4;
			float float5;
			if (this.isTree) {
				float3 = 0.0F;
				float4 = 0.04F;
				if (this.windType == 1) {
					float3 = 0.6F;
					float1 = float1 <= 0.08F ? 0.0F : (float1 - 0.08F) / 0.92F;
				} else if (this.windType == 2) {
					float3 = 0.3F;
					float4 = 0.06F;
					float1 = float1 <= 0.15F ? 0.0F : (float1 - 0.15F) / 0.85F;
				} else if (this.windType == 3) {
					float3 = 0.15F;
					float1 = float1 <= 0.3F ? 0.0F : (float1 - 0.3F) / 0.7F;
				}

				float5 = ClimateManager.clamp01(1.0F - float1);
				this.curTime = 0.0F;
				this.maxTime = Rand.Next(20.0F + 100.0F * float5, 70.0F + 200.0F * float5) * T_MOD;
				if (float1 <= 0.01F || !Core.OptionDoWindSpriteEffects) {
					this.tx1 = 0.0;
					this.tx2 = 0.0;
					this.ty1 = 0.0;
					this.ty2 = 0.0;
					return;
				}

				float float6 = 0.6F * float1 + 0.4F * float1 * float1;
				double double1;
				if (this.gust) {
					double1 = (double)(Rand.Next(-0.1F + 0.6F * float1, 1.0F) * float2);
					if (Rand.Next(0.0F, 1.0F) > Rand.Next(0.0F, 0.75F * float1)) {
						this.gust = false;
					}
				} else {
					double1 = (double)(Rand.Next(-0.1F, 0.2F) * float2);
					this.gust = true;
				}

				double1 *= (double)(float3 * float6);
				this.tx1 = double1;
				this.tx2 = double1;
				double double2 = (double)Rand.Next(-1.0F, 1.0F);
				double2 *= 0.01 + (double)(float4 * float6);
				this.ty1 = double2;
				double2 = (double)Rand.Next(-1.0F, 1.0F);
				double2 *= 0.01 + (double)(float4 * float6);
				this.ty2 = double2;
			} else {
				float3 = 0.0F;
				if (this.windType == 1) {
					float3 = 0.575F;
					float1 = float1 <= 0.02F ? 0.0F : (float1 - 0.02F) / 0.98F;
				} else if (this.windType == 2) {
					float3 = 0.375F;
					float1 = float1 <= 0.2F ? 0.0F : (float1 - 0.2F) / 0.8F;
				} else if (this.windType == 3) {
					float3 = 0.175F;
					float1 = float1 <= 0.6F ? 0.0F : (float1 - 0.6F) / 0.4F;
				}

				float4 = ClimateManager.clamp01(1.0F - float1);
				this.curTime = 0.0F;
				this.maxTime = Rand.Next(20.0F + 50.0F * float4, 60.0F + 100.0F * float4) * T_MOD;
				if (float1 <= 0.05F || !Core.OptionDoWindSpriteEffects) {
					this.tx1 = 0.0;
					this.tx2 = 0.0;
					this.ty1 = 0.0;
					this.ty2 = 0.0;
					return;
				}

				float5 = 0.55F * float1 + 0.45F * float1 * float1;
				double double3;
				if (this.gust) {
					double3 = (double)(Rand.Next(-0.1F + 0.9F * float1, 1.0F) * float2);
					if (Rand.Next(0.0F, 1.0F) > Rand.Next(0.0F, 0.95F * float1)) {
						this.gust = false;
					}
				} else {
					double3 = (double)(Rand.Next(-0.1F, 0.2F) * float2);
					this.gust = true;
				}

				double3 *= (double)(0.025F + float3 * float5);
				this.tx1 = double3;
				this.tx2 = double3;
				if (float1 > 0.5F) {
					double double4 = (double)Rand.Next(-1.0F, 1.0F);
					double4 *= (double)(0.05F * float5);
					this.ty1 = double4;
					double4 = (double)Rand.Next(-1.0F, 1.0F);
					double4 *= (double)(0.05F * float5);
					this.ty2 = double4;
				} else {
					this.ty1 = 0.0;
					this.ty2 = 0.0;
				}
			}
		} else {
			this.lerpAll(this.curTime / this.maxTime);
		}
	}

	private void updateOLD(float float1, float float2) {
		this.curTime += 1.0F * GameTime.getInstance().getMultiplier();
		if (this.curTime >= this.maxTime) {
			this.curTime = 0.0F;
			float float3 = ClimateManager.clamp01(1.0F - float1);
			this.maxTime = Rand.Next(20.0F + 100.0F * float3, 70.0F + 200.0F * float3) * T_MOD;
			this.swapTargetToLast();
			float float4 = float1;
			float1 = ClimateManager.clamp01(float1 * 1.25F);
			double double1 = (double)Rand.Next(-0.65F, 0.65F);
			double1 += (double)(float4 * float2 * 0.7F);
			double1 *= (double)(0.4F * float1);
			this.tx1 = double1;
			this.tx2 = double1;
			double double2 = (double)Rand.Next(-1.0F, 1.0F);
			double2 *= (double)(0.05F * float1);
			this.ty1 = double2;
			double2 = (double)Rand.Next(-1.0F, 1.0F);
			double2 *= (double)(0.05F * float1);
			this.ty2 = double2;
		} else {
			this.lerpAll(this.curTime / this.maxTime);
		}
	}

	private void lerpAll(float float1) {
		this.x1 = (double)ClimateManager.clerp(float1, (float)this.lx1, (float)this.tx1);
		this.y1 = (double)ClimateManager.clerp(float1, (float)this.ly1, (float)this.ty1);
		this.x2 = (double)ClimateManager.clerp(float1, (float)this.lx2, (float)this.tx2);
		this.y2 = (double)ClimateManager.clerp(float1, (float)this.ly2, (float)this.ty2);
		this.x3 = (double)ClimateManager.clerp(float1, (float)this.lx3, (float)this.tx3);
		this.y3 = (double)ClimateManager.clerp(float1, (float)this.ly3, (float)this.ty3);
		this.x4 = (double)ClimateManager.clerp(float1, (float)this.lx4, (float)this.tx4);
		this.y4 = (double)ClimateManager.clerp(float1, (float)this.ly4, (float)this.ty4);
	}

	private void swapTargetToLast() {
		this.lx1 = this.tx1;
		this.ly1 = this.ty1;
		this.lx2 = this.tx2;
		this.ly2 = this.ty2;
		this.lx3 = this.tx3;
		this.ly3 = this.ty3;
		this.lx4 = this.tx4;
		this.ly4 = this.ty4;
	}

	public void copyMainFromOther(ObjectRenderEffects objectRenderEffects) {
		this.x1 = objectRenderEffects.x1;
		this.y1 = objectRenderEffects.y1;
		this.x2 = objectRenderEffects.x2;
		this.y2 = objectRenderEffects.y2;
		this.x3 = objectRenderEffects.x3;
		this.y3 = objectRenderEffects.y3;
		this.x4 = objectRenderEffects.x4;
		this.y4 = objectRenderEffects.y4;
	}

	public void add(ObjectRenderEffects objectRenderEffects) {
		this.x1 += objectRenderEffects.x1;
		this.y1 += objectRenderEffects.y1;
		this.x2 += objectRenderEffects.x2;
		this.y2 += objectRenderEffects.y2;
		this.x3 += objectRenderEffects.x3;
		this.y3 += objectRenderEffects.y3;
		this.x4 += objectRenderEffects.x4;
		this.y4 += objectRenderEffects.y4;
	}

	public static void updateStatic() {
		if (!GameServer.bServer) {
			try {
				float float1 = (float)ClimateManager.getWindTickFinal();
				float float2 = ClimateManager.getInstance().getWindAngleIntensity();
				if (float2 < 0.0F) {
					float2 = -1.0F;
				} else {
					float2 = 1.0F;
				}

				int int1;
				for (int1 = 0; int1 < 3; ++int1) {
					int int2;
					ObjectRenderEffects objectRenderEffects;
					for (int2 = 0; int2 < 15; ++int2) {
						objectRenderEffects = WIND_EFFECTS[int1][int2];
						objectRenderEffects.update(float1, float2);
					}

					for (int2 = 0; int2 < 15; ++int2) {
						objectRenderEffects = WIND_EFFECTS_TREES[int1][int2];
						objectRenderEffects.update(float1, float2);
					}
				}

				randomRustleTime += 1.0F * GameTime.getInstance().getMultiplier();
				if (randomRustleTime > randomRustleTotalTime && RANDOM_RUSTLE == null) {
					float float3 = 1.0F - float1;
					RANDOM_RUSTLE = getNew((IsoObject)null, RenderEffectType.Vegetation_Rustle, false, true);
					RANDOM_RUSTLE.isBig = false;
					if (float1 > 0.45F && Rand.Next(0.0F, 1.0F) < Rand.Next(0.0F, 0.8F * float1)) {
						RANDOM_RUSTLE.isBig = true;
					}

					randomRustleType = Rand.Next(3);
					randomRustleTarget = Rand.Next(15);
					randomRustleTime = 0.0F;
					randomRustleTotalTime = Rand.Next(400.0F + 400.0F * float3, 1200.0F + 3200.0F * float3);
				}

				if (RANDOM_RUSTLE != null) {
					if (!RANDOM_RUSTLE.update()) {
						release(RANDOM_RUSTLE);
						RANDOM_RUSTLE = null;
					} else {
						ObjectRenderEffects objectRenderEffects2 = WIND_EFFECTS_TREES[randomRustleType][randomRustleTarget];
						objectRenderEffects2.add(RANDOM_RUSTLE);
					}
				}

				for (int1 = DYNAMIC_EFFECTS.size() - 1; int1 >= 0; --int1) {
					ObjectRenderEffects objectRenderEffects3 = (ObjectRenderEffects)DYNAMIC_EFFECTS.get(int1);
					if (!objectRenderEffects3.update()) {
						if (objectRenderEffects3.parent != null) {
							objectRenderEffects3.parent.removeRenderEffect(objectRenderEffects3);
						}

						DYNAMIC_EFFECTS.remove(int1);
						release(objectRenderEffects3);
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}
}
