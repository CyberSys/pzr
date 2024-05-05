package zombie.iso;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.core.Core;
import zombie.inventory.ItemContainer;
import zombie.iso.objects.IsoWheelieBin;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;


public class IsoPushableObject extends IsoMovingObject {
	public int carryCapacity = 100;
	public float emptyWeight = 4.5F;
	public ArrayList connectList = null;
	public float ox = 0.0F;
	public float oy = 0.0F;

	public IsoPushableObject(IsoCell cell) {
		super(cell);
		this.getCell().getPushableObjectList().add(this);
	}

	public IsoPushableObject(IsoCell cell, int int1, int int2, int int3) {
		super(cell, true);
		this.getCell().getPushableObjectList().add(this);
	}

	public IsoPushableObject(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
		super(cell, square, sprite, true);
		this.setX((float)square.getX() + 0.5F);
		this.setY((float)square.getY() + 0.5F);
		this.setZ((float)square.getZ());
		this.ox = this.getX();
		this.oy = this.getY();
		this.setNx(this.getX());
		this.setNy(this.getNy());
		this.offsetX = (float)(6 * Core.TileScale);
		this.offsetY = (float)(-30 * Core.TileScale);
		this.setWeight(6.0F);
		this.square = square;
		this.setCurrent(square);
		this.getCurrentSquare().getMovingObjects().add(this);
		this.Collidable = true;
		this.solid = true;
		this.shootable = false;
		this.width = 0.5F;
		this.setAlphaAndTarget(0.0F);
		this.getCell().getPushableObjectList().add(this);
	}

	public String getObjectName() {
		return "Pushable";
	}

	public void update() {
		if (this.connectList != null) {
			Iterator iterator = this.connectList.iterator();
			float float1 = 0.0F;
			while (iterator.hasNext()) {
				IsoPushableObject pushableObject = (IsoPushableObject)iterator.next();
				float float2 = pushableObject.getAlpha();
				if (float2 > float1) {
					float1 = float2;
				}
			}

			this.setAlphaAndTarget(float1);
		}

		super.update();
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		if (!(this instanceof IsoWheelieBin)) {
			this.sprite = IsoSpriteManager.instance.getSprite(byteBuffer.getInt());
		}

		if (byteBuffer.get() == 1) {
			this.container = new ItemContainer();
			this.container.load(byteBuffer, int1);
		}
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		if (!(this instanceof IsoWheelieBin)) {
			byteBuffer.putInt(this.sprite.ID);
		}

		if (this.container != null) {
			byteBuffer.put((byte)1);
			this.container.save(byteBuffer);
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public float getWeight(float float1, float float2) {
		if (this.container == null) {
			return this.emptyWeight;
		} else {
			float float3 = this.container.getContentsWeight() / (float)this.carryCapacity;
			if (float3 < 0.0F) {
				float3 = 0.0F;
			}

			if (float3 > 1.0F) {
				return this.getWeight() * 8.0F;
			} else {
				float float4 = this.getWeight() * float3 + this.emptyWeight;
				return float4;
			}
		}
	}

	public boolean Serialize() {
		return true;
	}

	public void DoCollideNorS() {
		if (this.connectList == null) {
			super.DoCollideNorS();
		} else {
			Iterator iterator = this.connectList.iterator();
			while (iterator.hasNext()) {
				IsoPushableObject pushableObject = (IsoPushableObject)iterator.next();
				if (pushableObject != this) {
					if (pushableObject.ox < this.ox) {
						pushableObject.setNx(this.getNx() - 1.0F);
						pushableObject.setX(this.getX() - 1.0F);
					} else if (pushableObject.ox > this.ox) {
						pushableObject.setNx(this.getNx() + 1.0F);
						pushableObject.setX(this.getX() + 1.0F);
					} else {
						pushableObject.setNx(this.getNx());
						pushableObject.setX(this.getX());
					}

					if (pushableObject.oy < this.oy) {
						pushableObject.setNy(this.getNy() - 1.0F);
						pushableObject.setY(this.getY() - 1.0F);
					} else if (pushableObject.oy > this.oy) {
						pushableObject.setNy(this.getNy() + 1.0F);
						pushableObject.setY(this.getY() + 1.0F);
					} else {
						pushableObject.setNy(this.getNy());
						pushableObject.setY(this.getY());
					}

					pushableObject.setImpulsex(this.getImpulsex());
					pushableObject.setImpulsey(this.getImpulsey());
				}
			}
		}
	}

	public void DoCollideWorE() {
		if (this.connectList == null) {
			super.DoCollideWorE();
		} else {
			Iterator iterator = this.connectList.iterator();
			while (iterator.hasNext()) {
				IsoPushableObject pushableObject = (IsoPushableObject)iterator.next();
				if (pushableObject != this) {
					if (pushableObject.ox < this.ox) {
						pushableObject.setNx(this.getNx() - 1.0F);
						pushableObject.setX(this.getX() - 1.0F);
					} else if (pushableObject.ox > this.ox) {
						pushableObject.setNx(this.getNx() + 1.0F);
						pushableObject.setX(this.getX() + 1.0F);
					} else {
						pushableObject.setNx(this.getNx());
						pushableObject.setX(this.getX());
					}

					if (pushableObject.oy < this.oy) {
						pushableObject.setNy(this.getNy() - 1.0F);
						pushableObject.setY(this.getY() - 1.0F);
					} else if (pushableObject.oy > this.oy) {
						pushableObject.setNy(this.getNy() + 1.0F);
						pushableObject.setY(this.getY() + 1.0F);
					} else {
						pushableObject.setNy(this.getNy());
						pushableObject.setY(this.getY());
					}

					pushableObject.setImpulsex(this.getImpulsex());
					pushableObject.setImpulsey(this.getImpulsey());
				}
			}
		}
	}
}
