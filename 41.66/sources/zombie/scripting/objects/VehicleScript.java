package zombie.scripting.objects;

import gnu.trove.list.array.TFloatArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SystemDisabler;
import zombie.Lua.LuaManager;
import zombie.core.BoxedStaticValues;
import zombie.core.ImmutableColor;
import zombie.core.math.PZMath;
import zombie.core.physics.Bullet;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.scripting.ScriptManager;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;


public final class VehicleScript extends BaseScriptObject {
	private String fileName;
	private String name;
	private final ArrayList models = new ArrayList();
	public final ArrayList m_attachments = new ArrayList();
	private float mass = 800.0F;
	private final Vector3f centerOfMassOffset = new Vector3f();
	private float engineForce = 3000.0F;
	private float engineIdleSpeed = 750.0F;
	private float steeringIncrement = 0.04F;
	private float steeringClamp = 0.4F;
	private float steeringClampMax = 0.9F;
	private float wheelFriction = 800.0F;
	private float stoppingMovementForce = 1.0F;
	private float suspensionStiffness = 20.0F;
	private float suspensionDamping = 2.3F;
	private float suspensionCompression = 4.4F;
	private float suspensionRestLength = 0.6F;
	private float maxSuspensionTravelCm = 500.0F;
	private float rollInfluence = 0.1F;
	private final Vector3f extents = new Vector3f(0.75F, 0.5F, 2.0F);
	private final Vector2f shadowExtents = new Vector2f(0.0F, 0.0F);
	private final Vector2f shadowOffset = new Vector2f(0.0F, 0.0F);
	private boolean bHadShadowOExtents = false;
	private boolean bHadShadowOffset = false;
	private final Vector2f extentsOffset = new Vector2f(0.5F, 0.5F);
	private final Vector3f physicsChassisShape = new Vector3f(0.75F, 0.5F, 1.0F);
	private final ArrayList m_physicsShapes = new ArrayList();
	private final ArrayList wheels = new ArrayList();
	private final ArrayList passengers = new ArrayList();
	public float maxSpeed = 20.0F;
	public boolean isSmallVehicle = true;
	public float spawnOffsetY = 0.0F;
	private int frontEndHealth = 100;
	private int rearEndHealth = 100;
	private int storageCapacity = 100;
	private int engineLoudness = 100;
	private int engineQuality = 100;
	private int seats = 2;
	private int mechanicType;
	private int engineRepairLevel;
	private float playerDamageProtection;
	private float forcedHue = -1.0F;
	private float forcedSat = -1.0F;
	private float forcedVal = -1.0F;
	public ImmutableColor leftSirenCol;
	public ImmutableColor rightSirenCol;
	private String engineRPMType = "jeep";
	private float offroadEfficiency = 1.0F;
	private final TFloatArrayList crawlOffsets = new TFloatArrayList();
	public int gearRatioCount = 0;
	public final float[] gearRatio = new float[9];
	private final VehicleScript.Skin textures = new VehicleScript.Skin();
	private final ArrayList skins = new ArrayList();
	private final ArrayList areas = new ArrayList();
	private final ArrayList parts = new ArrayList();
	private boolean hasSiren = false;
	private final VehicleScript.LightBar lightbar = new VehicleScript.LightBar();
	private final VehicleScript.Sounds sound = new VehicleScript.Sounds();
	public boolean textureMaskEnable = false;
	private static final int PHYSICS_SHAPE_BOX = 1;
	private static final int PHYSICS_SHAPE_SPHERE = 2;

	public VehicleScript() {
		this.gearRatioCount = 4;
		this.gearRatio[0] = 7.09F;
		this.gearRatio[1] = 6.44F;
		this.gearRatio[2] = 4.1F;
		this.gearRatio[3] = 2.29F;
		this.gearRatio[4] = 1.47F;
		this.gearRatio[5] = 1.0F;
	}

	public void Load(String string, String string2) {
		ScriptManager scriptManager = ScriptManager.instance;
		this.fileName = scriptManager.currentFileName;
		if (!scriptManager.scriptsWithVehicles.contains(this.fileName)) {
			scriptManager.scriptsWithVehicles.add(this.fileName);
		}

		this.name = string;
		ScriptParser.Block block = ScriptParser.parse(string2);
		block = (ScriptParser.Block)block.children.get(0);
		Iterator iterator = block.elements.iterator();
		while (true) {
			while (iterator.hasNext()) {
				ScriptParser.BlockElement blockElement = (ScriptParser.BlockElement)iterator.next();
				String string3;
				if (blockElement.asValue() != null) {
					String[] stringArray = blockElement.asValue().string.split("=");
					string3 = stringArray[0].trim();
					String string4 = stringArray[1].trim();
					if ("extents".equals(string3)) {
						this.LoadVector3f(string4, this.extents);
					} else if ("shadowExtents".equals(string3)) {
						this.LoadVector2f(string4, this.shadowExtents);
						this.bHadShadowOExtents = true;
					} else if ("shadowOffset".equals(string3)) {
						this.LoadVector2f(string4, this.shadowOffset);
						this.bHadShadowOffset = true;
					} else if ("physicsChassisShape".equals(string3)) {
						this.LoadVector3f(string4, this.physicsChassisShape);
					} else if ("extentsOffset".equals(string3)) {
						this.LoadVector2f(string4, this.extentsOffset);
					} else if ("mass".equals(string3)) {
						this.mass = Float.parseFloat(string4);
					} else if ("offRoadEfficiency".equalsIgnoreCase(string3)) {
						this.offroadEfficiency = Float.parseFloat(string4);
					} else if ("centerOfMassOffset".equals(string3)) {
						this.LoadVector3f(string4, this.centerOfMassOffset);
					} else if ("engineForce".equals(string3)) {
						this.engineForce = Float.parseFloat(string4);
					} else if ("engineIdleSpeed".equals(string3)) {
						this.engineIdleSpeed = Float.parseFloat(string4);
					} else if ("gearRatioCount".equals(string3)) {
						this.gearRatioCount = Integer.parseInt(string4);
					} else if ("gearRatioR".equals(string3)) {
						this.gearRatio[0] = Float.parseFloat(string4);
					} else if ("gearRatio1".equals(string3)) {
						this.gearRatio[1] = Float.parseFloat(string4);
					} else if ("gearRatio2".equals(string3)) {
						this.gearRatio[2] = Float.parseFloat(string4);
					} else if ("gearRatio3".equals(string3)) {
						this.gearRatio[3] = Float.parseFloat(string4);
					} else if ("gearRatio4".equals(string3)) {
						this.gearRatio[4] = Float.parseFloat(string4);
					} else if ("gearRatio5".equals(string3)) {
						this.gearRatio[5] = Float.parseFloat(string4);
					} else if ("gearRatio6".equals(string3)) {
						this.gearRatio[6] = Float.parseFloat(string4);
					} else if ("gearRatio7".equals(string3)) {
						this.gearRatio[7] = Float.parseFloat(string4);
					} else if ("gearRatio8".equals(string3)) {
						this.gearRatio[8] = Float.parseFloat(string4);
					} else if ("textureMaskEnable".equals(string3)) {
						this.textureMaskEnable = Boolean.parseBoolean(string4);
					} else if ("textureRust".equals(string3)) {
						this.textures.textureRust = StringUtils.discardNullOrWhitespace(string4);
					} else if ("textureMask".equals(string3)) {
						this.textures.textureMask = StringUtils.discardNullOrWhitespace(string4);
					} else if ("textureLights".equals(string3)) {
						this.textures.textureLights = StringUtils.discardNullOrWhitespace(string4);
					} else if ("textureDamage1Overlay".equals(string3)) {
						this.textures.textureDamage1Overlay = StringUtils.discardNullOrWhitespace(string4);
					} else if ("textureDamage1Shell".equals(string3)) {
						this.textures.textureDamage1Shell = StringUtils.discardNullOrWhitespace(string4);
					} else if ("textureDamage2Overlay".equals(string3)) {
						this.textures.textureDamage2Overlay = StringUtils.discardNullOrWhitespace(string4);
					} else if ("textureDamage2Shell".equals(string3)) {
						this.textures.textureDamage2Shell = StringUtils.discardNullOrWhitespace(string4);
					} else if ("textureShadow".equals(string3)) {
						this.textures.textureShadow = StringUtils.discardNullOrWhitespace(string4);
					} else if ("rollInfluence".equals(string3)) {
						this.rollInfluence = Float.parseFloat(string4);
					} else if ("steeringIncrement".equals(string3)) {
						this.steeringIncrement = Float.parseFloat(string4);
					} else if ("steeringClamp".equals(string3)) {
						this.steeringClamp = Float.parseFloat(string4);
					} else if ("suspensionStiffness".equals(string3)) {
						this.suspensionStiffness = Float.parseFloat(string4);
					} else if ("suspensionDamping".equals(string3)) {
						this.suspensionDamping = Float.parseFloat(string4);
					} else if ("suspensionCompression".equals(string3)) {
						this.suspensionCompression = Float.parseFloat(string4);
					} else if ("suspensionRestLength".equals(string3)) {
						this.suspensionRestLength = Float.parseFloat(string4);
					} else if ("maxSuspensionTravelCm".equals(string3)) {
						this.maxSuspensionTravelCm = Float.parseFloat(string4);
					} else if ("wheelFriction".equals(string3)) {
						this.wheelFriction = Float.parseFloat(string4);
					} else if ("stoppingMovementForce".equals(string3)) {
						this.stoppingMovementForce = Float.parseFloat(string4);
					} else if ("maxSpeed".equals(string3)) {
						this.maxSpeed = Float.parseFloat(string4);
					} else if ("isSmallVehicle".equals(string3)) {
						this.isSmallVehicle = Boolean.parseBoolean(string4);
					} else if ("spawnOffsetY".equals(string3)) {
						this.spawnOffsetY = Float.parseFloat(string4) - 0.995F;
					} else if ("frontEndDurability".equals(string3)) {
						this.frontEndHealth = Integer.parseInt(string4);
					} else if ("rearEndDurability".equals(string3)) {
						this.rearEndHealth = Integer.parseInt(string4);
					} else if ("storageCapacity".equals(string3)) {
						this.storageCapacity = Integer.parseInt(string4);
					} else if ("engineLoudness".equals(string3)) {
						this.engineLoudness = Integer.parseInt(string4);
					} else if ("engineQuality".equals(string3)) {
						this.engineQuality = Integer.parseInt(string4);
					} else if ("seats".equals(string3)) {
						this.seats = Integer.parseInt(string4);
					} else if ("hasSiren".equals(string3)) {
						this.hasSiren = Boolean.parseBoolean(string4);
					} else if ("mechanicType".equals(string3)) {
						this.mechanicType = Integer.parseInt(string4);
					} else if ("forcedColor".equals(string3)) {
						String[] stringArray2 = string4.split(" ");
						this.setForcedHue(Float.parseFloat(stringArray2[0]));
						this.setForcedSat(Float.parseFloat(stringArray2[1]));
						this.setForcedVal(Float.parseFloat(stringArray2[2]));
					} else if ("engineRPMType".equals(string3)) {
						this.engineRPMType = string4.trim();
					} else if ("template".equals(string3)) {
						this.LoadTemplate(string4);
					} else if ("template!".equals(string3)) {
						VehicleTemplate vehicleTemplate = ScriptManager.instance.getVehicleTemplate(string4);
						if (vehicleTemplate == null) {
							DebugLog.log("ERROR: template \"" + string4 + "\" not found");
						} else {
							this.Load(string, vehicleTemplate.body);
						}
					} else if ("engineRepairLevel".equals(string3)) {
						this.engineRepairLevel = Integer.parseInt(string4);
					} else if ("playerDamageProtection".equals(string3)) {
						this.setPlayerDamageProtection(Float.parseFloat(string4));
					}
				} else {
					ScriptParser.Block block2 = blockElement.asBlock();
					if ("area".equals(block2.type)) {
						this.LoadArea(block2);
					} else if ("attachment".equals(block2.type)) {
						this.LoadAttachment(block2);
					} else if ("model".equals(block2.type)) {
						this.LoadModel(block2, this.models);
					} else {
						Iterator iterator2;
						if ("part".equals(block2.type)) {
							if (block2.id != null && block2.id.contains("*")) {
								string3 = block2.id;
								iterator2 = this.parts.iterator();
								while (iterator2.hasNext()) {
									VehicleScript.Part part = (VehicleScript.Part)iterator2.next();
									if (this.globMatch(string3, part.id)) {
										block2.id = part.id;
										this.LoadPart(block2);
									}
								}
							} else {
								this.LoadPart(block2);
							}
						} else if ("passenger".equals(block2.type)) {
							if (block2.id != null && block2.id.contains("*")) {
								string3 = block2.id;
								iterator2 = this.passengers.iterator();
								while (iterator2.hasNext()) {
									VehicleScript.Passenger passenger = (VehicleScript.Passenger)iterator2.next();
									if (this.globMatch(string3, passenger.id)) {
										block2.id = passenger.id;
										this.LoadPassenger(block2);
									}
								}
							} else {
								this.LoadPassenger(block2);
							}
						} else if ("physics".equals(block2.type)) {
							VehicleScript.PhysicsShape physicsShape = this.LoadPhysicsShape(block2);
							if (physicsShape != null && this.m_physicsShapes.size() < 10) {
								this.m_physicsShapes.add(physicsShape);
							}
						} else if ("skin".equals(block2.type)) {
							VehicleScript.Skin skin = this.LoadSkin(block2);
							if (!StringUtils.isNullOrWhitespace(skin.texture)) {
								this.skins.add(skin);
							}
						} else if ("wheel".equals(block2.type)) {
							this.LoadWheel(block2);
						} else {
							Iterator iterator3;
							ScriptParser.Value value;
							String string5;
							String string6;
							if ("lightbar".equals(block2.type)) {
								iterator3 = block2.values.iterator();
								while (iterator3.hasNext()) {
									value = (ScriptParser.Value)iterator3.next();
									string5 = value.getKey().trim();
									string6 = value.getValue().trim();
									if ("soundSiren".equals(string5)) {
										this.lightbar.soundSiren0 = string6 + "Yelp";
										this.lightbar.soundSiren1 = string6 + "Wall";
										this.lightbar.soundSiren2 = string6 + "Alarm";
									}

									if ("soundSiren0".equals(string5)) {
										this.lightbar.soundSiren0 = string6;
									}

									if ("soundSiren1".equals(string5)) {
										this.lightbar.soundSiren1 = string6;
									}

									if ("soundSiren2".equals(string5)) {
										this.lightbar.soundSiren2 = string6;
									}

									String[] stringArray3;
									if ("leftCol".equals(string5)) {
										stringArray3 = string6.split(";");
										this.leftSirenCol = new ImmutableColor(Float.parseFloat(stringArray3[0]), Float.parseFloat(stringArray3[1]), Float.parseFloat(stringArray3[2]));
									}

									if ("rightCol".equals(string5)) {
										stringArray3 = string6.split(";");
										this.rightSirenCol = new ImmutableColor(Float.parseFloat(stringArray3[0]), Float.parseFloat(stringArray3[1]), Float.parseFloat(stringArray3[2]));
									}

									this.lightbar.enable = true;
									VehicleScript.Part part2 = new VehicleScript.Part();
									part2.id = "lightbar";
									this.parts.add(part2);
								}
							} else if ("sound".equals(block2.type)) {
								iterator3 = block2.values.iterator();
								while (iterator3.hasNext()) {
									value = (ScriptParser.Value)iterator3.next();
									string5 = value.getKey().trim();
									string6 = value.getValue().trim();
									if ("backSignal".equals(string5)) {
										this.sound.backSignal = StringUtils.discardNullOrWhitespace(string6);
										this.sound.backSignalEnable = this.sound.backSignal != null;
									} else if ("engine".equals(string5)) {
										this.sound.engine = StringUtils.discardNullOrWhitespace(string6);
									} else if ("engineStart".equals(string5)) {
										this.sound.engineStart = StringUtils.discardNullOrWhitespace(string6);
									} else if ("engineTurnOff".equals(string5)) {
										this.sound.engineTurnOff = StringUtils.discardNullOrWhitespace(string6);
									} else if ("horn".equals(string5)) {
										this.sound.horn = StringUtils.discardNullOrWhitespace(string6);
										this.sound.hornEnable = this.sound.horn != null;
									} else if ("ignitionFail".equals(string5)) {
										this.sound.ignitionFail = StringUtils.discardNullOrWhitespace(string6);
									} else if ("ignitionFailNoPower".equals(string5)) {
										this.sound.ignitionFailNoPower = StringUtils.discardNullOrWhitespace(string6);
									}
								}
							}
						}
					}
				}
			}

			return;
		}
	}

	public String getFileName() {
		return this.fileName;
	}

	public void Loaded() {
		float float1 = this.getModelScale();
		this.extents.mul(float1);
		this.maxSuspensionTravelCm *= float1;
		this.suspensionRestLength *= float1;
		this.centerOfMassOffset.mul(float1);
		this.physicsChassisShape.mul(float1);
		if (this.bHadShadowOExtents) {
			this.shadowExtents.mul(float1);
		} else {
			this.shadowExtents.set(this.extents.x(), this.extents.z());
		}

		if (this.bHadShadowOffset) {
			this.shadowOffset.mul(float1);
		} else {
			this.shadowOffset.set(this.centerOfMassOffset.x(), this.centerOfMassOffset.z());
		}

		Iterator iterator = this.models.iterator();
		while (iterator.hasNext()) {
			VehicleScript.Model model = (VehicleScript.Model)iterator.next();
			model.offset.mul(float1);
		}

		iterator = this.m_attachments.iterator();
		while (iterator.hasNext()) {
			ModelAttachment modelAttachment = (ModelAttachment)iterator.next();
			modelAttachment.getOffset().mul(float1);
		}

		iterator = this.m_physicsShapes.iterator();
		while (iterator.hasNext()) {
			VehicleScript.PhysicsShape physicsShape = (VehicleScript.PhysicsShape)iterator.next();
			physicsShape.offset.mul(float1);
			switch (physicsShape.type) {
			case 1: 
				physicsShape.extents.mul(float1);
				break;
			
			case 2: 
				physicsShape.radius *= float1;
			
			}
		}

		iterator = this.wheels.iterator();
		while (iterator.hasNext()) {
			VehicleScript.Wheel wheel = (VehicleScript.Wheel)iterator.next();
			wheel.radius *= float1;
			wheel.offset.mul(float1);
		}

		VehicleScript.Area area;
		for (iterator = this.areas.iterator(); iterator.hasNext(); area.h *= float1) {
			area = (VehicleScript.Area)iterator.next();
			area.x *= float1;
			area.y *= float1;
			area.w *= float1;
		}

		if (!this.extents.equals(this.physicsChassisShape)) {
			DebugLog.Script.warn("vehicle \"" + this.name + "\" extents != physicsChassisShape");
		}

		int int1;
		int int2;
		for (int2 = 0; int2 < this.passengers.size(); ++int2) {
			VehicleScript.Passenger passenger = (VehicleScript.Passenger)this.passengers.get(int2);
			for (int1 = 0; int1 < passenger.getPositionCount(); ++int1) {
				VehicleScript.Position position = passenger.getPosition(int1);
				position.getOffset().mul(float1);
			}

			for (int1 = 0; int1 < passenger.switchSeats.size(); ++int1) {
				VehicleScript.Passenger.SwitchSeat switchSeat = (VehicleScript.Passenger.SwitchSeat)passenger.switchSeats.get(int1);
				switchSeat.seat = this.getPassengerIndex(switchSeat.id);
				assert switchSeat.seat != -1;
			}
		}

		for (int2 = 0; int2 < this.parts.size(); ++int2) {
			VehicleScript.Part part = (VehicleScript.Part)this.parts.get(int2);
			if (part.container != null && part.container.seatID != null && !part.container.seatID.isEmpty()) {
				part.container.seat = this.getPassengerIndex(part.container.seatID);
			}

			if (part.specificItem && part.itemType != null) {
				for (int1 = 0; int1 < part.itemType.size(); ++int1) {
					ArrayList arrayList = part.itemType;
					String string = (String)part.itemType.get(int1);
					arrayList.set(int1, string + this.mechanicType);
				}
			}
		}

		this.initCrawlOffsets();
		this.toBullet();
	}

	public void toBullet() {
		float[] floatArray = new float[200];
		byte byte1 = 0;
		int int1 = byte1 + 1;
		floatArray[byte1] = this.getModelScale();
		floatArray[int1++] = this.extents.x;
		floatArray[int1++] = this.extents.y;
		floatArray[int1++] = this.extents.z;
		floatArray[int1++] = this.physicsChassisShape.x;
		floatArray[int1++] = this.physicsChassisShape.y;
		floatArray[int1++] = this.physicsChassisShape.z;
		floatArray[int1++] = this.mass;
		floatArray[int1++] = this.centerOfMassOffset.x;
		floatArray[int1++] = this.centerOfMassOffset.y;
		floatArray[int1++] = this.centerOfMassOffset.z;
		floatArray[int1++] = this.rollInfluence;
		floatArray[int1++] = this.suspensionStiffness;
		floatArray[int1++] = this.suspensionCompression;
		floatArray[int1++] = this.suspensionDamping;
		floatArray[int1++] = this.maxSuspensionTravelCm;
		floatArray[int1++] = this.suspensionRestLength;
		if (SystemDisabler.getdoHighFriction()) {
			floatArray[int1++] = this.wheelFriction * 100.0F;
		} else {
			floatArray[int1++] = this.wheelFriction;
		}

		floatArray[int1++] = this.stoppingMovementForce;
		floatArray[int1++] = (float)this.getWheelCount();
		int int2;
		for (int2 = 0; int2 < this.getWheelCount(); ++int2) {
			VehicleScript.Wheel wheel = this.getWheel(int2);
			floatArray[int1++] = wheel.front ? 1.0F : 0.0F;
			floatArray[int1++] = wheel.offset.x + this.getModel().offset.x - 0.0F * this.centerOfMassOffset.x;
			floatArray[int1++] = wheel.offset.y + this.getModel().offset.y - 0.0F * this.centerOfMassOffset.y + 1.0F * this.suspensionRestLength;
			floatArray[int1++] = wheel.offset.z + this.getModel().offset.z - 0.0F * this.centerOfMassOffset.z;
			floatArray[int1++] = wheel.radius;
		}

		floatArray[int1++] = (float)(this.m_physicsShapes.size() + 1);
		floatArray[int1++] = 1.0F;
		floatArray[int1++] = this.centerOfMassOffset.x;
		floatArray[int1++] = this.centerOfMassOffset.y;
		floatArray[int1++] = this.centerOfMassOffset.z;
		floatArray[int1++] = this.physicsChassisShape.x;
		floatArray[int1++] = this.physicsChassisShape.y;
		floatArray[int1++] = this.physicsChassisShape.z;
		floatArray[int1++] = 0.0F;
		floatArray[int1++] = 0.0F;
		floatArray[int1++] = 0.0F;
		for (int2 = 0; int2 < this.m_physicsShapes.size(); ++int2) {
			VehicleScript.PhysicsShape physicsShape = (VehicleScript.PhysicsShape)this.m_physicsShapes.get(int2);
			floatArray[int1++] = (float)physicsShape.type;
			floatArray[int1++] = physicsShape.offset.x;
			floatArray[int1++] = physicsShape.offset.y;
			floatArray[int1++] = physicsShape.offset.z;
			if (physicsShape.type == 1) {
				floatArray[int1++] = physicsShape.extents.x;
				floatArray[int1++] = physicsShape.extents.y;
				floatArray[int1++] = physicsShape.extents.z;
				floatArray[int1++] = physicsShape.rotate.x;
				floatArray[int1++] = physicsShape.rotate.y;
				floatArray[int1++] = physicsShape.rotate.z;
			} else if (physicsShape.type == 2) {
				floatArray[int1++] = physicsShape.radius;
			}
		}

		Bullet.defineVehicleScript(this.getFullName(), floatArray);
	}

	private void LoadVector2f(String string, Vector2f vector2f) {
		String[] stringArray = string.split(" ");
		vector2f.set(Float.parseFloat(stringArray[0]), Float.parseFloat(stringArray[1]));
	}

	private void LoadVector3f(String string, Vector3f vector3f) {
		String[] stringArray = string.split(" ");
		vector3f.set(Float.parseFloat(stringArray[0]), Float.parseFloat(stringArray[1]), Float.parseFloat(stringArray[2]));
	}

	private void LoadVector4f(String string, Vector4f vector4f) {
		String[] stringArray = string.split(" ");
		vector4f.set(Float.parseFloat(stringArray[0]), Float.parseFloat(stringArray[1]), Float.parseFloat(stringArray[2]), Float.parseFloat(stringArray[3]));
	}

	private void LoadVector2i(String string, Vector2i vector2i) {
		String[] stringArray = string.split(" ");
		vector2i.set(Integer.parseInt(stringArray[0]), Integer.parseInt(stringArray[1]));
	}

	private ModelAttachment LoadAttachment(ScriptParser.Block block) {
		ModelAttachment modelAttachment = this.getAttachmentById(block.id);
		if (modelAttachment == null) {
			modelAttachment = new ModelAttachment(block.id);
			this.m_attachments.add(modelAttachment);
		}

		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string = value.getKey().trim();
			String string2 = value.getValue().trim();
			if ("bone".equals(string)) {
				modelAttachment.setBone(string2);
			} else if ("offset".equals(string)) {
				this.LoadVector3f(string2, modelAttachment.getOffset());
			} else if ("rotate".equals(string)) {
				this.LoadVector3f(string2, modelAttachment.getRotate());
			} else if ("canAttach".equals(string)) {
				modelAttachment.setCanAttach(new ArrayList(Arrays.asList(string2.split(","))));
			} else if ("zoffset".equals(string)) {
				modelAttachment.setZOffset(Float.parseFloat(string2));
			} else if ("updateconstraint".equals(string)) {
				modelAttachment.setUpdateConstraint(Boolean.parseBoolean(string2));
			}
		}

		return modelAttachment;
	}

	private VehicleScript.Model LoadModel(ScriptParser.Block block, ArrayList arrayList) {
		VehicleScript.Model model = this.getModelById(block.id, arrayList);
		if (model == null) {
			model = new VehicleScript.Model();
			model.id = block.id;
			arrayList.add(model);
		}

		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string = value.getKey().trim();
			String string2 = value.getValue().trim();
			if ("file".equals(string)) {
				model.file = string2;
			} else if ("offset".equals(string)) {
				this.LoadVector3f(string2, model.offset);
			} else if ("rotate".equals(string)) {
				this.LoadVector3f(string2, model.rotate);
			} else if ("scale".equals(string)) {
				model.scale = Float.parseFloat(string2);
			}
		}

		return model;
	}

	private VehicleScript.Skin LoadSkin(ScriptParser.Block block) {
		VehicleScript.Skin skin = new VehicleScript.Skin();
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string = value.getKey().trim();
			String string2 = value.getValue().trim();
			if ("texture".equals(string)) {
				skin.texture = StringUtils.discardNullOrWhitespace(string2);
			} else if ("textureRust".equals(string)) {
				skin.textureRust = StringUtils.discardNullOrWhitespace(string2);
			} else if ("textureMask".equals(string)) {
				skin.textureMask = StringUtils.discardNullOrWhitespace(string2);
			} else if ("textureLights".equals(string)) {
				skin.textureLights = StringUtils.discardNullOrWhitespace(string2);
			} else if ("textureDamage1Overlay".equals(string)) {
				skin.textureDamage1Overlay = StringUtils.discardNullOrWhitespace(string2);
			} else if ("textureDamage1Shell".equals(string)) {
				skin.textureDamage1Shell = StringUtils.discardNullOrWhitespace(string2);
			} else if ("textureDamage2Overlay".equals(string)) {
				skin.textureDamage2Overlay = StringUtils.discardNullOrWhitespace(string2);
			} else if ("textureDamage2Shell".equals(string)) {
				skin.textureDamage2Shell = StringUtils.discardNullOrWhitespace(string2);
			} else if ("textureShadow".equals(string)) {
				skin.textureShadow = StringUtils.discardNullOrWhitespace(string2);
			}
		}

		return skin;
	}

	private VehicleScript.Wheel LoadWheel(ScriptParser.Block block) {
		VehicleScript.Wheel wheel = this.getWheelById(block.id);
		if (wheel == null) {
			wheel = new VehicleScript.Wheel();
			wheel.id = block.id;
			this.wheels.add(wheel);
		}

		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string = value.getKey().trim();
			String string2 = value.getValue().trim();
			if ("model".equals(string)) {
				wheel.model = string2;
			} else if ("front".equals(string)) {
				wheel.front = Boolean.parseBoolean(string2);
			} else if ("offset".equals(string)) {
				this.LoadVector3f(string2, wheel.offset);
			} else if ("radius".equals(string)) {
				wheel.radius = Float.parseFloat(string2);
			} else if ("width".equals(string)) {
				wheel.width = Float.parseFloat(string2);
			}
		}

		return wheel;
	}

	private VehicleScript.Passenger LoadPassenger(ScriptParser.Block block) {
		VehicleScript.Passenger passenger = this.getPassengerById(block.id);
		if (passenger == null) {
			passenger = new VehicleScript.Passenger();
			passenger.id = block.id;
			this.passengers.add(passenger);
		}

		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string = value.getKey().trim();
			String string2 = value.getValue().trim();
			if ("area".equals(string)) {
				passenger.area = string2;
			} else if ("door".equals(string)) {
				passenger.door = string2;
			} else if ("door2".equals(string)) {
				passenger.door2 = string2;
			} else if ("hasRoof".equals(string)) {
				passenger.hasRoof = Boolean.parseBoolean(string2);
			} else if ("showPassenger".equals(string)) {
				passenger.showPassenger = Boolean.parseBoolean(string2);
			}
		}

		iterator = block.children.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Block block2 = (ScriptParser.Block)iterator.next();
			if ("anim".equals(block2.type)) {
				this.LoadAnim(block2, passenger.anims);
			} else if ("position".equals(block2.type)) {
				this.LoadPosition(block2, passenger.positions);
			} else if ("switchSeat".equals(block2.type)) {
				this.LoadPassengerSwitchSeat(block2, passenger);
			}
		}

		return passenger;
	}

	private VehicleScript.Anim LoadAnim(ScriptParser.Block block, ArrayList arrayList) {
		VehicleScript.Anim anim = this.getAnimationById(block.id, arrayList);
		if (anim == null) {
			anim = new VehicleScript.Anim();
			anim.id = block.id;
			arrayList.add(anim);
		}

		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string = value.getKey().trim();
			String string2 = value.getValue().trim();
			if ("angle".equals(string)) {
				this.LoadVector3f(string2, anim.angle);
			} else if ("anim".equals(string)) {
				anim.anim = string2;
			} else if ("animate".equals(string)) {
				anim.bAnimate = Boolean.parseBoolean(string2);
			} else if ("loop".equals(string)) {
				anim.bLoop = Boolean.parseBoolean(string2);
			} else if ("reverse".equals(string)) {
				anim.bReverse = Boolean.parseBoolean(string2);
			} else if ("rate".equals(string)) {
				anim.rate = Float.parseFloat(string2);
			} else if ("offset".equals(string)) {
				this.LoadVector3f(string2, anim.offset);
			} else if ("sound".equals(string)) {
				anim.sound = string2;
			}
		}

		return anim;
	}

	private VehicleScript.Passenger.SwitchSeat LoadPassengerSwitchSeat(ScriptParser.Block block, VehicleScript.Passenger passenger) {
		VehicleScript.Passenger.SwitchSeat switchSeat = passenger.getSwitchSeatById(block.id);
		if (block.isEmpty()) {
			if (switchSeat != null) {
				passenger.switchSeats.remove(switchSeat);
			}

			return null;
		} else {
			if (switchSeat == null) {
				switchSeat = new VehicleScript.Passenger.SwitchSeat();
				switchSeat.id = block.id;
				passenger.switchSeats.add(switchSeat);
			}

			Iterator iterator = block.values.iterator();
			while (iterator.hasNext()) {
				ScriptParser.Value value = (ScriptParser.Value)iterator.next();
				String string = value.getKey().trim();
				String string2 = value.getValue().trim();
				if ("anim".equals(string)) {
					switchSeat.anim = string2;
				} else if ("rate".equals(string)) {
					switchSeat.rate = Float.parseFloat(string2);
				} else if ("sound".equals(string)) {
					switchSeat.sound = string2.isEmpty() ? null : string2;
				}
			}

			return switchSeat;
		}
	}

	private VehicleScript.Area LoadArea(ScriptParser.Block block) {
		VehicleScript.Area area = this.getAreaById(block.id);
		if (area == null) {
			area = new VehicleScript.Area();
			area.id = block.id;
			this.areas.add(area);
		}

		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string = value.getKey().trim();
			String string2 = value.getValue().trim();
			if ("xywh".equals(string)) {
				String[] stringArray = string2.split(" ");
				area.x = Float.parseFloat(stringArray[0]);
				area.y = Float.parseFloat(stringArray[1]);
				area.w = Float.parseFloat(stringArray[2]);
				area.h = Float.parseFloat(stringArray[3]);
			}
		}

		return area;
	}

	private VehicleScript.Part LoadPart(ScriptParser.Block block) {
		VehicleScript.Part part = this.getPartById(block.id);
		if (part == null) {
			part = new VehicleScript.Part();
			part.id = block.id;
			this.parts.add(part);
		}

		Iterator iterator = block.values.iterator();
		while (true) {
			while (iterator.hasNext()) {
				ScriptParser.Value value = (ScriptParser.Value)iterator.next();
				String string = value.getKey().trim();
				String string2 = value.getValue().trim();
				if ("area".equals(string)) {
					part.area = string2.isEmpty() ? null : string2;
				} else if ("itemType".equals(string)) {
					part.itemType = new ArrayList();
					String[] stringArray = string2.split(";");
					String[] stringArray2 = stringArray;
					int int1 = stringArray.length;
					for (int int2 = 0; int2 < int1; ++int2) {
						String string3 = stringArray2[int2];
						part.itemType.add(string3);
					}
				} else if ("parent".equals(string)) {
					part.parent = string2.isEmpty() ? null : string2;
				} else if ("mechanicRequireKey".equals(string)) {
					part.mechanicRequireKey = Boolean.parseBoolean(string2);
				} else if ("repairMechanic".equals(string)) {
					part.setRepairMechanic(Boolean.parseBoolean(string2));
				} else if ("setAllModelsVisible".equals(string)) {
					part.bSetAllModelsVisible = Boolean.parseBoolean(string2);
				} else if ("wheel".equals(string)) {
					part.wheel = string2;
				} else if ("category".equals(string)) {
					part.category = string2;
				} else if ("specificItem".equals(string)) {
					part.specificItem = Boolean.parseBoolean(string2);
				} else if ("hasLightsRear".equals(string)) {
					part.hasLightsRear = Boolean.parseBoolean(string2);
				}
			}

			iterator = block.children.iterator();
			while (iterator.hasNext()) {
				ScriptParser.Block block2 = (ScriptParser.Block)iterator.next();
				if ("anim".equals(block2.type)) {
					if (part.anims == null) {
						part.anims = new ArrayList();
					}

					this.LoadAnim(block2, part.anims);
				} else if ("container".equals(block2.type)) {
					part.container = this.LoadContainer(block2, part.container);
				} else if ("door".equals(block2.type)) {
					part.door = this.LoadDoor(block2);
				} else if ("lua".equals(block2.type)) {
					part.luaFunctions = this.LoadLuaFunctions(block2);
				} else if ("model".equals(block2.type)) {
					if (part.models == null) {
						part.models = new ArrayList();
					}

					this.LoadModel(block2, part.models);
				} else if ("table".equals(block2.type)) {
					Object object = part.tables == null ? null : part.tables.get(block2.id);
					KahluaTable kahluaTable = this.LoadTable(block2, object instanceof KahluaTable ? (KahluaTable)object : null);
					if (part.tables == null) {
						part.tables = new HashMap();
					}

					part.tables.put(block2.id, kahluaTable);
				} else if ("window".equals(block2.type)) {
					part.window = this.LoadWindow(block2);
				}
			}

			return part;
		}
	}

	private VehicleScript.PhysicsShape LoadPhysicsShape(ScriptParser.Block block) {
		boolean boolean1 = true;
		String string = block.id;
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -895981619: 
			if (string.equals("sphere")) {
				byte1 = 1;
			}

			break;
		
		case 97739: 
			if (string.equals("box")) {
				byte1 = 0;
			}

		
		}
		byte byte2;
		switch (byte1) {
		case 0: 
			byte2 = 1;
			break;
		
		case 1: 
			byte2 = 2;
			break;
		
		default: 
			return null;
		
		}
		VehicleScript.PhysicsShape physicsShape = new VehicleScript.PhysicsShape();
		physicsShape.type = byte2;
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string2 = value.getKey().trim();
			String string3 = value.getValue().trim();
			if ("extents".equalsIgnoreCase(string2)) {
				this.LoadVector3f(string3, physicsShape.extents);
			} else if ("offset".equalsIgnoreCase(string2)) {
				this.LoadVector3f(string3, physicsShape.offset);
			} else if ("radius".equalsIgnoreCase(string2)) {
				physicsShape.radius = Float.parseFloat(string3);
			} else if ("rotate".equalsIgnoreCase(string2)) {
				this.LoadVector3f(string3, physicsShape.rotate);
			}
		}

		switch (physicsShape.type) {
		case 1: 
			if (physicsShape.extents.x() <= 0.0F || physicsShape.extents.y() <= 0.0F || physicsShape.extents.z() <= 0.0F) {
				return null;
			}

			break;
		
		case 2: 
			if (physicsShape.radius <= 0.0F) {
				return null;
			}

		
		}
		return physicsShape;
	}

	private VehicleScript.Door LoadDoor(ScriptParser.Block block) {
		VehicleScript.Door door = new VehicleScript.Door();
		ScriptParser.Value value;
		String string;
		for (Iterator iterator = block.values.iterator(); iterator.hasNext(); string = value.getValue().trim()) {
			value = (ScriptParser.Value)iterator.next();
			String string2 = value.getKey().trim();
		}

		return door;
	}

	private VehicleScript.Window LoadWindow(ScriptParser.Block block) {
		VehicleScript.Window window = new VehicleScript.Window();
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string = value.getKey().trim();
			String string2 = value.getValue().trim();
			if ("openable".equals(string)) {
				window.openable = Boolean.parseBoolean(string2);
			}
		}

		return window;
	}

	private VehicleScript.Container LoadContainer(ScriptParser.Block block, VehicleScript.Container container) {
		VehicleScript.Container container2 = container == null ? new VehicleScript.Container() : container;
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			String string = value.getKey().trim();
			String string2 = value.getValue().trim();
			if ("capacity".equals(string)) {
				container2.capacity = Integer.parseInt(string2);
			} else if ("conditionAffectsCapacity".equals(string)) {
				container2.conditionAffectsCapacity = Boolean.parseBoolean(string2);
			} else if ("contentType".equals(string)) {
				container2.contentType = string2;
			} else if ("seat".equals(string)) {
				container2.seatID = string2;
			} else if ("test".equals(string)) {
				container2.luaTest = string2;
			}
		}

		return container2;
	}

	private HashMap LoadLuaFunctions(ScriptParser.Block block) {
		HashMap hashMap = new HashMap();
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			if (value.string.indexOf(61) == -1) {
				String string = value.string.trim();
				throw new RuntimeException("expected \"key = value\", got \"" + string + "\" in " + this.getFullName());
			}

			String string2 = value.getKey().trim();
			String string3 = value.getValue().trim();
			hashMap.put(string2, string3);
		}

		return hashMap;
	}

	private Object checkIntegerKey(Object object) {
		if (!(object instanceof String)) {
			return object;
		} else {
			String string = (String)object;
			for (int int1 = 0; int1 < string.length(); ++int1) {
				if (!Character.isDigit(string.charAt(int1))) {
					return object;
				}
			}

			return Double.valueOf(string);
		}
	}

	private KahluaTable LoadTable(ScriptParser.Block block, KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = kahluaTable == null ? LuaManager.platform.newTable() : kahluaTable;
		Iterator iterator;
		String string;
		String string2;
		for (iterator = block.values.iterator(); iterator.hasNext(); kahluaTable2.rawset(this.checkIntegerKey(string), string2)) {
			ScriptParser.Value value = (ScriptParser.Value)iterator.next();
			string = value.getKey().trim();
			string2 = value.getValue().trim();
			if (string2.isEmpty()) {
				string2 = null;
			}
		}

		iterator = block.children.iterator();
		while (iterator.hasNext()) {
			ScriptParser.Block block2 = (ScriptParser.Block)iterator.next();
			Object object = kahluaTable2.rawget(block2.type);
			KahluaTable kahluaTable3 = this.LoadTable(block2, object instanceof KahluaTable ? (KahluaTable)object : null);
			kahluaTable2.rawset(this.checkIntegerKey(block2.type), kahluaTable3);
		}

		return kahluaTable2;
	}

	private void LoadTemplate(String string) {
		if (string.contains("/")) {
			String[] stringArray = string.split("/");
			if (stringArray.length == 0 || stringArray.length > 3) {
				DebugLog.log("ERROR: template \"" + string + "\"");
				return;
			}

			for (int int1 = 0; int1 < stringArray.length; ++int1) {
				stringArray[int1] = stringArray[int1].trim();
				if (stringArray[int1].isEmpty()) {
					DebugLog.log("ERROR: template \"" + string + "\"");
					return;
				}
			}

			String string2 = stringArray[0];
			VehicleTemplate vehicleTemplate = ScriptManager.instance.getVehicleTemplate(string2);
			if (vehicleTemplate == null) {
				DebugLog.log("ERROR: template \"" + string + "\" not found");
				return;
			}

			VehicleScript vehicleScript = vehicleTemplate.getScript();
			String string3 = stringArray[1];
			byte byte1 = -1;
			switch (string3.hashCode()) {
			case -944810854: 
				if (string3.equals("passenger")) {
					byte1 = 2;
				}

				break;
			
			case 3002509: 
				if (string3.equals("area")) {
					byte1 = 0;
				}

				break;
			
			case 3433459: 
				if (string3.equals("part")) {
					byte1 = 1;
				}

				break;
			
			case 113097563: 
				if (string3.equals("wheel")) {
					byte1 = 3;
				}

			
			}

			switch (byte1) {
			case 0: 
				if (stringArray.length == 2) {
					DebugLog.log("ERROR: template \"" + string + "\"");
					return;
				}

				this.copyAreasFrom(vehicleScript, stringArray[2]);
				break;
			
			case 1: 
				if (stringArray.length == 2) {
					DebugLog.log("ERROR: template \"" + string + "\"");
					return;
				}

				this.copyPartsFrom(vehicleScript, stringArray[2]);
				break;
			
			case 2: 
				if (stringArray.length == 2) {
					DebugLog.log("ERROR: template \"" + string + "\"");
					return;
				}

				this.copyPassengersFrom(vehicleScript, stringArray[2]);
				break;
			
			case 3: 
				if (stringArray.length == 2) {
					DebugLog.log("ERROR: template \"" + string + "\"");
					return;
				}

				this.copyWheelsFrom(vehicleScript, stringArray[2]);
				break;
			
			default: 
				DebugLog.log("ERROR: template \"" + string + "\"");
				return;
			
			}
		} else {
			String string4 = string.trim();
			VehicleTemplate vehicleTemplate2 = ScriptManager.instance.getVehicleTemplate(string4);
			if (vehicleTemplate2 == null) {
				DebugLog.log("ERROR: template \"" + string + "\" not found");
				return;
			}

			VehicleScript vehicleScript2 = vehicleTemplate2.getScript();
			this.copyAreasFrom(vehicleScript2, "*");
			this.copyPartsFrom(vehicleScript2, "*");
			this.copyPassengersFrom(vehicleScript2, "*");
			this.copyWheelsFrom(vehicleScript2, "*");
		}
	}

	public void copyAreasFrom(VehicleScript vehicleScript, String string) {
		if ("*".equals(string)) {
			for (int int1 = 0; int1 < vehicleScript.getAreaCount(); ++int1) {
				VehicleScript.Area area = vehicleScript.getArea(int1);
				int int2 = this.getIndexOfAreaById(area.id);
				if (int2 == -1) {
					this.areas.add(area.makeCopy());
				} else {
					this.areas.set(int2, area.makeCopy());
				}
			}
		} else {
			VehicleScript.Area area2 = vehicleScript.getAreaById(string);
			if (area2 == null) {
				DebugLog.log("ERROR: area \"" + string + "\" not found");
				return;
			}

			int int3 = this.getIndexOfAreaById(area2.id);
			if (int3 == -1) {
				this.areas.add(area2.makeCopy());
			} else {
				this.areas.set(int3, area2.makeCopy());
			}
		}
	}

	public void copyPartsFrom(VehicleScript vehicleScript, String string) {
		if ("*".equals(string)) {
			for (int int1 = 0; int1 < vehicleScript.getPartCount(); ++int1) {
				VehicleScript.Part part = vehicleScript.getPart(int1);
				int int2 = this.getIndexOfPartById(part.id);
				if (int2 == -1) {
					this.parts.add(part.makeCopy());
				} else {
					this.parts.set(int2, part.makeCopy());
				}
			}
		} else {
			VehicleScript.Part part2 = vehicleScript.getPartById(string);
			if (part2 == null) {
				DebugLog.log("ERROR: part \"" + string + "\" not found");
				return;
			}

			int int3 = this.getIndexOfPartById(part2.id);
			if (int3 == -1) {
				this.parts.add(part2.makeCopy());
			} else {
				this.parts.set(int3, part2.makeCopy());
			}
		}
	}

	public void copyPassengersFrom(VehicleScript vehicleScript, String string) {
		if ("*".equals(string)) {
			for (int int1 = 0; int1 < vehicleScript.getPassengerCount(); ++int1) {
				VehicleScript.Passenger passenger = vehicleScript.getPassenger(int1);
				int int2 = this.getPassengerIndex(passenger.id);
				if (int2 == -1) {
					this.passengers.add(passenger.makeCopy());
				} else {
					this.passengers.set(int2, passenger.makeCopy());
				}
			}
		} else {
			VehicleScript.Passenger passenger2 = vehicleScript.getPassengerById(string);
			if (passenger2 == null) {
				DebugLog.log("ERROR: passenger \"" + string + "\" not found");
				return;
			}

			int int3 = this.getPassengerIndex(passenger2.id);
			if (int3 == -1) {
				this.passengers.add(passenger2.makeCopy());
			} else {
				this.passengers.set(int3, passenger2.makeCopy());
			}
		}
	}

	public void copyWheelsFrom(VehicleScript vehicleScript, String string) {
		if ("*".equals(string)) {
			for (int int1 = 0; int1 < vehicleScript.getWheelCount(); ++int1) {
				VehicleScript.Wheel wheel = vehicleScript.getWheel(int1);
				int int2 = this.getIndexOfWheelById(wheel.id);
				if (int2 == -1) {
					this.wheels.add(wheel.makeCopy());
				} else {
					this.wheels.set(int2, wheel.makeCopy());
				}
			}
		} else {
			VehicleScript.Wheel wheel2 = vehicleScript.getWheelById(string);
			if (wheel2 == null) {
				DebugLog.log("ERROR: wheel \"" + string + "\" not found");
				return;
			}

			int int3 = this.getIndexOfWheelById(wheel2.id);
			if (int3 == -1) {
				this.wheels.add(wheel2.makeCopy());
			} else {
				this.wheels.set(int3, wheel2.makeCopy());
			}
		}
	}

	private VehicleScript.Position LoadPosition(ScriptParser.Block block, ArrayList arrayList) {
		VehicleScript.Position position = this.getPositionById(block.id, arrayList);
		if (block.isEmpty()) {
			if (position != null) {
				arrayList.remove(position);
			}

			return null;
		} else {
			if (position == null) {
				position = new VehicleScript.Position();
				position.id = block.id;
				arrayList.add(position);
			}

			Iterator iterator = block.values.iterator();
			while (iterator.hasNext()) {
				ScriptParser.Value value = (ScriptParser.Value)iterator.next();
				String string = value.getKey().trim();
				String string2 = value.getValue().trim();
				if ("rotate".equals(string)) {
					this.LoadVector3f(string2, position.rotate);
				} else if ("offset".equals(string)) {
					this.LoadVector3f(string2, position.offset);
				} else if ("area".equals(string)) {
					position.area = string2.isEmpty() ? null : string2;
				}
			}

			return position;
		}
	}

	private void initCrawlOffsets() {
		for (int int1 = 0; int1 < this.getWheelCount(); ++int1) {
			VehicleScript.Wheel wheel = this.getWheel(int1);
			if (wheel.id.contains("Left")) {
				this.initCrawlOffsets(wheel);
			}
		}

		float float1 = this.extents.z + BaseVehicle.PLUS_RADIUS * 2.0F;
		int int2;
		for (int2 = 0; int2 < this.crawlOffsets.size(); ++int2) {
			this.crawlOffsets.set(int2, (this.extents.z / 2.0F + BaseVehicle.PLUS_RADIUS + this.crawlOffsets.get(int2) - this.centerOfMassOffset.z) / float1);
		}

		this.crawlOffsets.sort();
		for (int2 = 0; int2 < this.crawlOffsets.size(); ++int2) {
			float float2 = this.crawlOffsets.get(int2);
			for (int int3 = int2 + 1; int3 < this.crawlOffsets.size(); ++int3) {
				float float3 = this.crawlOffsets.get(int3);
				if ((float3 - float2) * float1 < 0.15F) {
					this.crawlOffsets.removeAt(int3--);
				}
			}
		}
	}

	private void initCrawlOffsets(VehicleScript.Wheel wheel) {
		float float1 = 0.3F;
		float float2 = this.getModel() == null ? 0.0F : this.getModel().getOffset().z;
		float float3 = this.centerOfMassOffset.z + this.extents.z / 2.0F;
		float float4 = this.centerOfMassOffset.z - this.extents.z / 2.0F;
		for (int int1 = 0; int1 < 10; ++int1) {
			float float5 = float2 + wheel.offset.z + wheel.radius + float1 + float1 * (float)int1;
			if (float5 + float1 <= float3 && !this.isOverlappingWheel(float5)) {
				this.crawlOffsets.add(float5);
			}

			float5 = float2 + wheel.offset.z - wheel.radius - float1 - float1 * (float)int1;
			if (float5 - float1 >= float4 && !this.isOverlappingWheel(float5)) {
				this.crawlOffsets.add(float5);
			}
		}
	}

	private boolean isOverlappingWheel(float float1) {
		float float2 = 0.3F;
		float float3 = this.getModel() == null ? 0.0F : this.getModel().getOffset().z;
		for (int int1 = 0; int1 < this.getWheelCount(); ++int1) {
			VehicleScript.Wheel wheel = this.getWheel(int1);
			if (wheel.id.contains("Left") && Math.abs(float3 + wheel.offset.z - float1) < (wheel.radius + float2) * 0.99F) {
				return true;
			}
		}

		return false;
	}

	public String getName() {
		return this.name;
	}

	public String getFullName() {
		String string = this.getModule().getName();
		return string + "." + this.getName();
	}

	public VehicleScript.Model getModel() {
		return this.models.isEmpty() ? null : (VehicleScript.Model)this.models.get(0);
	}

	public Vector3f getModelOffset() {
		return this.getModel() == null ? null : this.getModel().getOffset();
	}

	public float getModelScale() {
		return this.getModel() == null ? 1.0F : this.getModel().scale;
	}

	public void setModelScale(float float1) {
		VehicleScript.Model model = this.getModel();
		if (model != null) {
			float float2 = model.scale;
			model.scale = 1.0F / float2;
			this.Loaded();
			model.scale = PZMath.clamp(float1, 0.01F, 100.0F);
			this.Loaded();
		}
	}

	public int getModelCount() {
		return this.models.size();
	}

	public VehicleScript.Model getModelByIndex(int int1) {
		return (VehicleScript.Model)this.models.get(int1);
	}

	public VehicleScript.Model getModelById(String string, ArrayList arrayList) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			VehicleScript.Model model = (VehicleScript.Model)arrayList.get(int1);
			if (StringUtils.isNullOrWhitespace(model.id) && StringUtils.isNullOrWhitespace(string)) {
				return model;
			}

			if (model.id != null && model.id.equals(string)) {
				return model;
			}
		}

		return null;
	}

	public VehicleScript.Model getModelById(String string) {
		return this.getModelById(string, this.models);
	}

	public int getAttachmentCount() {
		return this.m_attachments.size();
	}

	public ModelAttachment getAttachment(int int1) {
		return (ModelAttachment)this.m_attachments.get(int1);
	}

	public ModelAttachment getAttachmentById(String string) {
		for (int int1 = 0; int1 < this.m_attachments.size(); ++int1) {
			ModelAttachment modelAttachment = (ModelAttachment)this.m_attachments.get(int1);
			if (modelAttachment.getId().equals(string)) {
				return modelAttachment;
			}
		}

		return null;
	}

	public ModelAttachment addAttachment(ModelAttachment modelAttachment) {
		this.m_attachments.add(modelAttachment);
		return modelAttachment;
	}

	public ModelAttachment removeAttachment(ModelAttachment modelAttachment) {
		this.m_attachments.remove(modelAttachment);
		return modelAttachment;
	}

	public ModelAttachment addAttachmentAt(int int1, ModelAttachment modelAttachment) {
		this.m_attachments.add(int1, modelAttachment);
		return modelAttachment;
	}

	public ModelAttachment removeAttachment(int int1) {
		return (ModelAttachment)this.m_attachments.remove(int1);
	}

	public VehicleScript.LightBar getLightbar() {
		return this.lightbar;
	}

	public VehicleScript.Sounds getSounds() {
		return this.sound;
	}

	public boolean getHasSiren() {
		return this.hasSiren;
	}

	public Vector3f getExtents() {
		return this.extents;
	}

	public Vector3f getPhysicsChassisShape() {
		return this.physicsChassisShape;
	}

	public Vector2f getShadowExtents() {
		return this.shadowExtents;
	}

	public Vector2f getShadowOffset() {
		return this.shadowOffset;
	}

	public Vector2f getExtentsOffset() {
		return this.extentsOffset;
	}

	public float getMass() {
		return this.mass;
	}

	public Vector3f getCenterOfMassOffset() {
		return this.centerOfMassOffset;
	}

	public float getEngineForce() {
		return this.engineForce;
	}

	public float getEngineIdleSpeed() {
		return this.engineIdleSpeed;
	}

	public int getEngineQuality() {
		return this.engineQuality;
	}

	public int getEngineLoudness() {
		return this.engineLoudness;
	}

	public float getRollInfluence() {
		return this.rollInfluence;
	}

	public float getSteeringIncrement() {
		return this.steeringIncrement;
	}

	public float getSteeringClamp(float float1) {
		float1 = Math.abs(float1);
		float float2 = float1 / this.maxSpeed;
		if (float2 > 1.0F) {
			float2 = 1.0F;
		}

		float2 = 1.0F - float2;
		return (this.steeringClampMax - this.steeringClamp) * float2 + this.steeringClamp;
	}

	public float getSuspensionStiffness() {
		return this.suspensionStiffness;
	}

	public float getSuspensionDamping() {
		return this.suspensionDamping;
	}

	public float getSuspensionCompression() {
		return this.suspensionCompression;
	}

	public float getSuspensionRestLength() {
		return this.suspensionRestLength;
	}

	public float getSuspensionTravel() {
		return this.maxSuspensionTravelCm;
	}

	public float getWheelFriction() {
		return this.wheelFriction;
	}

	public int getWheelCount() {
		return this.wheels.size();
	}

	public VehicleScript.Wheel getWheel(int int1) {
		return (VehicleScript.Wheel)this.wheels.get(int1);
	}

	public VehicleScript.Wheel getWheelById(String string) {
		for (int int1 = 0; int1 < this.wheels.size(); ++int1) {
			VehicleScript.Wheel wheel = (VehicleScript.Wheel)this.wheels.get(int1);
			if (wheel.id != null && wheel.id.equals(string)) {
				return wheel;
			}
		}

		return null;
	}

	public int getIndexOfWheelById(String string) {
		for (int int1 = 0; int1 < this.wheels.size(); ++int1) {
			VehicleScript.Wheel wheel = (VehicleScript.Wheel)this.wheels.get(int1);
			if (wheel.id != null && wheel.id.equals(string)) {
				return int1;
			}
		}

		return -1;
	}

	public int getPassengerCount() {
		return this.passengers.size();
	}

	public VehicleScript.Passenger getPassenger(int int1) {
		return (VehicleScript.Passenger)this.passengers.get(int1);
	}

	public VehicleScript.Passenger getPassengerById(String string) {
		for (int int1 = 0; int1 < this.passengers.size(); ++int1) {
			VehicleScript.Passenger passenger = (VehicleScript.Passenger)this.passengers.get(int1);
			if (passenger.id != null && passenger.id.equals(string)) {
				return passenger;
			}
		}

		return null;
	}

	public int getPassengerIndex(String string) {
		for (int int1 = 0; int1 < this.passengers.size(); ++int1) {
			VehicleScript.Passenger passenger = (VehicleScript.Passenger)this.passengers.get(int1);
			if (passenger.id != null && passenger.id.equals(string)) {
				return int1;
			}
		}

		return -1;
	}

	public int getPhysicsShapeCount() {
		return this.m_physicsShapes.size();
	}

	public VehicleScript.PhysicsShape getPhysicsShape(int int1) {
		return int1 >= 0 && int1 < this.m_physicsShapes.size() ? (VehicleScript.PhysicsShape)this.m_physicsShapes.get(int1) : null;
	}

	public int getFrontEndHealth() {
		return this.frontEndHealth;
	}

	public int getRearEndHealth() {
		return this.rearEndHealth;
	}

	public int getStorageCapacity() {
		return this.storageCapacity;
	}

	public VehicleScript.Skin getTextures() {
		return this.textures;
	}

	public int getSkinCount() {
		return this.skins.size();
	}

	public VehicleScript.Skin getSkin(int int1) {
		return (VehicleScript.Skin)this.skins.get(int1);
	}

	public int getAreaCount() {
		return this.areas.size();
	}

	public VehicleScript.Area getArea(int int1) {
		return (VehicleScript.Area)this.areas.get(int1);
	}

	public VehicleScript.Area getAreaById(String string) {
		for (int int1 = 0; int1 < this.areas.size(); ++int1) {
			VehicleScript.Area area = (VehicleScript.Area)this.areas.get(int1);
			if (area.id != null && area.id.equals(string)) {
				return area;
			}
		}

		return null;
	}

	public int getIndexOfAreaById(String string) {
		for (int int1 = 0; int1 < this.areas.size(); ++int1) {
			VehicleScript.Area area = (VehicleScript.Area)this.areas.get(int1);
			if (area.id != null && area.id.equals(string)) {
				return int1;
			}
		}

		return -1;
	}

	public int getPartCount() {
		return this.parts.size();
	}

	public VehicleScript.Part getPart(int int1) {
		return (VehicleScript.Part)this.parts.get(int1);
	}

	public VehicleScript.Part getPartById(String string) {
		for (int int1 = 0; int1 < this.parts.size(); ++int1) {
			VehicleScript.Part part = (VehicleScript.Part)this.parts.get(int1);
			if (part.id != null && part.id.equals(string)) {
				return part;
			}
		}

		return null;
	}

	public int getIndexOfPartById(String string) {
		for (int int1 = 0; int1 < this.parts.size(); ++int1) {
			VehicleScript.Part part = (VehicleScript.Part)this.parts.get(int1);
			if (part.id != null && part.id.equals(string)) {
				return int1;
			}
		}

		return -1;
	}

	private VehicleScript.Anim getAnimationById(String string, ArrayList arrayList) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			VehicleScript.Anim anim = (VehicleScript.Anim)arrayList.get(int1);
			if (anim.id != null && anim.id.equals(string)) {
				return anim;
			}
		}

		return null;
	}

	private VehicleScript.Position getPositionById(String string, ArrayList arrayList) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			VehicleScript.Position position = (VehicleScript.Position)arrayList.get(int1);
			if (position.id != null && position.id.equals(string)) {
				return position;
			}
		}

		return null;
	}

	public boolean globMatch(String string, String string2) {
		Pattern pattern = Pattern.compile(string.replaceAll("\\*", ".*"));
		return pattern.matcher(string2).matches();
	}

	public int getGearRatioCount() {
		return this.gearRatioCount;
	}

	public int getSeats() {
		return this.seats;
	}

	public void setSeats(int int1) {
		this.seats = int1;
	}

	public int getMechanicType() {
		return this.mechanicType;
	}

	public void setMechanicType(int int1) {
		this.mechanicType = int1;
	}

	public int getEngineRepairLevel() {
		return this.engineRepairLevel;
	}

	public int getHeadlightConfigLevel() {
		return 2;
	}

	public void setEngineRepairLevel(int int1) {
		this.engineRepairLevel = int1;
	}

	public float getPlayerDamageProtection() {
		return this.playerDamageProtection;
	}

	public void setPlayerDamageProtection(float float1) {
		this.playerDamageProtection = float1;
	}

	public float getForcedHue() {
		return this.forcedHue;
	}

	public void setForcedHue(float float1) {
		this.forcedHue = float1;
	}

	public float getForcedSat() {
		return this.forcedSat;
	}

	public void setForcedSat(float float1) {
		this.forcedSat = float1;
	}

	public float getForcedVal() {
		return this.forcedVal;
	}

	public void setForcedVal(float float1) {
		this.forcedVal = float1;
	}

	public String getEngineRPMType() {
		return this.engineRPMType;
	}

	public void setEngineRPMType(String string) {
		this.engineRPMType = string;
	}

	public float getOffroadEfficiency() {
		return this.offroadEfficiency;
	}

	public void setOffroadEfficiency(float float1) {
		this.offroadEfficiency = float1;
	}

	public TFloatArrayList getCrawlOffsets() {
		return this.crawlOffsets;
	}

	public static final class Skin {
		public String texture;
		public String textureRust = null;
		public String textureMask = null;
		public String textureLights = null;
		public String textureDamage1Overlay = null;
		public String textureDamage1Shell = null;
		public String textureDamage2Overlay = null;
		public String textureDamage2Shell = null;
		public String textureShadow = null;
		public Texture textureData;
		public Texture textureDataRust;
		public Texture textureDataMask;
		public Texture textureDataLights;
		public Texture textureDataDamage1Overlay;
		public Texture textureDataDamage1Shell;
		public Texture textureDataDamage2Overlay;
		public Texture textureDataDamage2Shell;
		public Texture textureDataShadow;

		public void copyMissingFrom(VehicleScript.Skin skin) {
			if (this.textureRust == null) {
				this.textureRust = skin.textureRust;
			}

			if (this.textureMask == null) {
				this.textureMask = skin.textureMask;
			}

			if (this.textureLights == null) {
				this.textureLights = skin.textureLights;
			}

			if (this.textureDamage1Overlay == null) {
				this.textureDamage1Overlay = skin.textureDamage1Overlay;
			}

			if (this.textureDamage1Shell == null) {
				this.textureDamage1Shell = skin.textureDamage1Shell;
			}

			if (this.textureDamage2Overlay == null) {
				this.textureDamage2Overlay = skin.textureDamage2Overlay;
			}

			if (this.textureDamage2Shell == null) {
				this.textureDamage2Shell = skin.textureDamage2Shell;
			}

			if (this.textureShadow == null) {
				this.textureShadow = skin.textureShadow;
			}
		}
	}

	public static final class LightBar {
		public boolean enable = false;
		public String soundSiren0 = "";
		public String soundSiren1 = "";
		public String soundSiren2 = "";
	}

	public static final class Sounds {
		public boolean hornEnable = false;
		public String horn = "";
		public boolean backSignalEnable = false;
		public String backSignal = "";
		public String engine = null;
		public String engineStart = null;
		public String engineTurnOff = null;
		public String ignitionFail = null;
		public String ignitionFailNoPower = null;
	}

	public static final class Area {
		public String id;
		public float x;
		public float y;
		public float w;
		public float h;

		public String getId() {
			return this.id;
		}

		public Double getX() {
			return BoxedStaticValues.toDouble((double)this.x);
		}

		public Double getY() {
			return BoxedStaticValues.toDouble((double)this.y);
		}

		public Double getW() {
			return BoxedStaticValues.toDouble((double)this.w);
		}

		public Double getH() {
			return BoxedStaticValues.toDouble((double)this.h);
		}

		public void setX(Double Double1) {
			this.x = Double1.floatValue();
		}

		public void setY(Double Double1) {
			this.y = Double1.floatValue();
		}

		public void setW(Double Double1) {
			this.w = Double1.floatValue();
		}

		public void setH(Double Double1) {
			this.h = Double1.floatValue();
		}

		private VehicleScript.Area makeCopy() {
			VehicleScript.Area area = new VehicleScript.Area();
			area.id = this.id;
			area.x = this.x;
			area.y = this.y;
			area.w = this.w;
			area.h = this.h;
			return area;
		}
	}

	public static final class Model {
		public String id;
		public String file;
		public float scale = 1.0F;
		public final Vector3f offset = new Vector3f();
		public final Vector3f rotate = new Vector3f();

		public String getId() {
			return this.id;
		}

		public Vector3f getOffset() {
			return this.offset;
		}

		public Vector3f getRotate() {
			return this.rotate;
		}

		VehicleScript.Model makeCopy() {
			VehicleScript.Model model = new VehicleScript.Model();
			model.id = this.id;
			model.file = this.file;
			model.scale = this.scale;
			model.offset.set((Vector3fc)this.offset);
			model.rotate.set((Vector3fc)this.rotate);
			return model;
		}
	}

	public static final class Part {
		public String id = "Unknown";
		public String parent;
		public ArrayList itemType;
		public VehicleScript.Container container;
		public String area;
		public String wheel;
		public HashMap tables;
		public HashMap luaFunctions;
		public ArrayList models;
		public boolean bSetAllModelsVisible = true;
		public VehicleScript.Door door;
		public VehicleScript.Window window;
		public ArrayList anims;
		public String category;
		public boolean specificItem = true;
		public boolean mechanicRequireKey = false;
		public boolean repairMechanic = false;
		public boolean hasLightsRear = false;

		public boolean isMechanicRequireKey() {
			return this.mechanicRequireKey;
		}

		public void setMechanicRequireKey(boolean boolean1) {
			this.mechanicRequireKey = boolean1;
		}

		public boolean isRepairMechanic() {
			return this.repairMechanic;
		}

		public void setRepairMechanic(boolean boolean1) {
			this.repairMechanic = boolean1;
		}

		VehicleScript.Part makeCopy() {
			VehicleScript.Part part = new VehicleScript.Part();
			part.id = this.id;
			part.parent = this.parent;
			if (this.itemType != null) {
				part.itemType = new ArrayList();
				part.itemType.addAll(this.itemType);
			}

			if (this.container != null) {
				part.container = this.container.makeCopy();
			}

			part.area = this.area;
			part.wheel = this.wheel;
			if (this.tables != null) {
				part.tables = new HashMap();
				Iterator iterator = this.tables.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry entry = (Entry)iterator.next();
					KahluaTable kahluaTable = LuaManager.copyTable((KahluaTable)entry.getValue());
					part.tables.put((String)entry.getKey(), kahluaTable);
				}
			}

			if (this.luaFunctions != null) {
				part.luaFunctions = new HashMap();
				part.luaFunctions.putAll(this.luaFunctions);
			}

			int int1;
			if (this.models != null) {
				part.models = new ArrayList();
				for (int1 = 0; int1 < this.models.size(); ++int1) {
					part.models.add(((VehicleScript.Model)this.models.get(int1)).makeCopy());
				}
			}

			part.bSetAllModelsVisible = this.bSetAllModelsVisible;
			if (this.door != null) {
				part.door = this.door.makeCopy();
			}

			if (this.window != null) {
				part.window = this.window.makeCopy();
			}

			if (this.anims != null) {
				part.anims = new ArrayList();
				for (int1 = 0; int1 < this.anims.size(); ++int1) {
					part.anims.add(((VehicleScript.Anim)this.anims.get(int1)).makeCopy());
				}
			}

			part.category = this.category;
			part.specificItem = this.specificItem;
			part.mechanicRequireKey = this.mechanicRequireKey;
			part.repairMechanic = this.repairMechanic;
			part.hasLightsRear = this.hasLightsRear;
			return part;
		}
	}

	public static final class Passenger {
		public String id;
		public final ArrayList anims = new ArrayList();
		public final ArrayList switchSeats = new ArrayList();
		public boolean hasRoof = true;
		public boolean showPassenger = false;
		public String door;
		public String door2;
		public String area;
		public final ArrayList positions = new ArrayList();

		public String getId() {
			return this.id;
		}

		public VehicleScript.Passenger makeCopy() {
			VehicleScript.Passenger passenger = new VehicleScript.Passenger();
			passenger.id = this.id;
			int int1;
			for (int1 = 0; int1 < this.anims.size(); ++int1) {
				passenger.anims.add(((VehicleScript.Anim)this.anims.get(int1)).makeCopy());
			}

			for (int1 = 0; int1 < this.switchSeats.size(); ++int1) {
				passenger.switchSeats.add(((VehicleScript.Passenger.SwitchSeat)this.switchSeats.get(int1)).makeCopy());
			}

			passenger.hasRoof = this.hasRoof;
			passenger.showPassenger = this.showPassenger;
			passenger.door = this.door;
			passenger.door2 = this.door2;
			passenger.area = this.area;
			for (int1 = 0; int1 < this.positions.size(); ++int1) {
				passenger.positions.add(((VehicleScript.Position)this.positions.get(int1)).makeCopy());
			}

			return passenger;
		}

		public int getPositionCount() {
			return this.positions.size();
		}

		public VehicleScript.Position getPosition(int int1) {
			return (VehicleScript.Position)this.positions.get(int1);
		}

		public VehicleScript.Position getPositionById(String string) {
			for (int int1 = 0; int1 < this.positions.size(); ++int1) {
				VehicleScript.Position position = (VehicleScript.Position)this.positions.get(int1);
				if (position.id != null && position.id.equals(string)) {
					return position;
				}
			}

			return null;
		}

		public VehicleScript.Passenger.SwitchSeat getSwitchSeatById(String string) {
			for (int int1 = 0; int1 < this.switchSeats.size(); ++int1) {
				VehicleScript.Passenger.SwitchSeat switchSeat = (VehicleScript.Passenger.SwitchSeat)this.switchSeats.get(int1);
				if (switchSeat.id != null && switchSeat.id.equals(string)) {
					return switchSeat;
				}
			}

			return null;
		}

		public static final class SwitchSeat {
			public String id;
			public int seat;
			public String anim;
			public float rate = 1.0F;
			public String sound;

			public String getId() {
				return this.id;
			}

			public VehicleScript.Passenger.SwitchSeat makeCopy() {
				VehicleScript.Passenger.SwitchSeat switchSeat = new VehicleScript.Passenger.SwitchSeat();
				switchSeat.id = this.id;
				switchSeat.seat = this.seat;
				switchSeat.anim = this.anim;
				switchSeat.rate = this.rate;
				switchSeat.sound = this.sound;
				return switchSeat;
			}
		}
	}

	public static final class PhysicsShape {
		public int type;
		public final Vector3f offset = new Vector3f();
		public final Vector3f rotate = new Vector3f();
		public final Vector3f extents = new Vector3f();
		public float radius;

		public String getTypeString() {
			switch (this.type) {
			case 1: 
				return "box";
			
			case 2: 
				return "sphere";
			
			default: 
				throw new RuntimeException("unhandled VehicleScript.PhysicsShape");
			
			}
		}

		public Vector3f getOffset() {
			return this.offset;
		}

		public Vector3f getExtents() {
			return this.extents;
		}

		public Vector3f getRotate() {
			return this.rotate;
		}

		public float getRadius() {
			return this.radius;
		}

		public void setRadius(float float1) {
			this.radius = PZMath.clamp(float1, 0.05F, 5.0F);
		}
	}

	public static final class Wheel {
		public String id;
		public String model;
		public boolean front;
		public final Vector3f offset = new Vector3f();
		public float radius = 0.5F;
		public float width = 0.4F;

		public String getId() {
			return this.id;
		}

		public Vector3f getOffset() {
			return this.offset;
		}

		VehicleScript.Wheel makeCopy() {
			VehicleScript.Wheel wheel = new VehicleScript.Wheel();
			wheel.id = this.id;
			wheel.model = this.model;
			wheel.front = this.front;
			wheel.offset.set((Vector3fc)this.offset);
			wheel.radius = this.radius;
			wheel.width = this.width;
			return wheel;
		}
	}

	public static final class Position {
		public String id;
		public final Vector3f offset = new Vector3f();
		public final Vector3f rotate = new Vector3f();
		public String area = null;

		public String getId() {
			return this.id;
		}

		public Vector3f getOffset() {
			return this.offset;
		}

		public Vector3f getRotate() {
			return this.rotate;
		}

		public String getArea() {
			return this.area;
		}

		VehicleScript.Position makeCopy() {
			VehicleScript.Position position = new VehicleScript.Position();
			position.id = this.id;
			position.offset.set((Vector3fc)this.offset);
			position.rotate.set((Vector3fc)this.rotate);
			return position;
		}
	}

	public static final class Container {
		public int capacity;
		public int seat = -1;
		public String seatID;
		public String luaTest;
		public String contentType;
		public boolean conditionAffectsCapacity = false;

		VehicleScript.Container makeCopy() {
			VehicleScript.Container container = new VehicleScript.Container();
			container.capacity = this.capacity;
			container.seat = this.seat;
			container.seatID = this.seatID;
			container.luaTest = this.luaTest;
			container.contentType = this.contentType;
			container.conditionAffectsCapacity = this.conditionAffectsCapacity;
			return container;
		}
	}

	public static final class Anim {
		public String id;
		public String anim;
		public float rate = 1.0F;
		public boolean bAnimate = true;
		public boolean bLoop = false;
		public boolean bReverse = false;
		public final Vector3f offset = new Vector3f();
		public final Vector3f angle = new Vector3f();
		public String sound;

		VehicleScript.Anim makeCopy() {
			VehicleScript.Anim anim = new VehicleScript.Anim();
			anim.id = this.id;
			anim.anim = this.anim;
			anim.rate = this.rate;
			anim.bAnimate = this.bAnimate;
			anim.bLoop = this.bLoop;
			anim.bReverse = this.bReverse;
			anim.offset.set((Vector3fc)this.offset);
			anim.angle.set((Vector3fc)this.angle);
			anim.sound = this.sound;
			return anim;
		}
	}

	public static final class Door {

		VehicleScript.Door makeCopy() {
			VehicleScript.Door door = new VehicleScript.Door();
			return door;
		}
	}

	public static final class Window {
		public boolean openable;

		VehicleScript.Window makeCopy() {
			VehicleScript.Window window = new VehicleScript.Window();
			window.openable = this.openable;
			return window;
		}
	}
}
