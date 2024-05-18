package zombie.scripting.objects;

import java.util.ArrayList;
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
import zombie.Lua.LuaManager;
import zombie.core.BoxedStaticValues;
import zombie.core.physics.Bullet;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.scripting.ScriptManager;


public class VehicleScript extends BaseScriptObject {
	private String name;
	private ArrayList models = new ArrayList();
	private float mass = 800.0F;
	private Vector3f centerOfMassOffset = new Vector3f();
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
	private Vector3f extents = new Vector3f(0.75F, 0.5F, 2.0F);
	private Vector4f shadowOffset = new Vector4f(0.0F, 0.0F, 0.0F, 0.0F);
	private Vector2f extentsOffset = new Vector2f(0.5F, 0.5F);
	private Vector3f physicsChassisShape = new Vector3f(0.75F, 0.5F, 1.0F);
	private ArrayList wheels = new ArrayList();
	private ArrayList passengers = new ArrayList();
	public float maxSpeed = 20.0F;
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
	private String engineRPMType = "jeep";
	private float offroadEfficiency = 1.0F;
	public int gearRatioCount = 0;
	public float[] gearRatio = new float[9];
	private ArrayList skins = new ArrayList();
	private ArrayList areas = new ArrayList();
	private ArrayList parts = new ArrayList();
	private boolean hasSiren = false;
	private VehicleScript.LightBar lightbar = new VehicleScript.LightBar();
	private VehicleScript.Sounds sound = new VehicleScript.Sounds();
	public boolean textureMaskEnable = false;
	public String textureRust = null;
	public String textureMask = null;
	public String textureLights = null;
	public String textureDamage1Overlay = null;
	public String textureDamage1Shell = null;
	public String textureDamage2Overlay = null;
	public String textureDamage2Shell = null;
	public Texture[] textureDataSkins;
	public Texture textureDataRust;
	public Texture textureDataMask;
	public Texture textureDataLights;
	public Texture textureDataDamage1Overlay;
	public Texture textureDataDamage1Shell;
	public Texture textureDataDamage2Overlay;
	public Texture textureDataDamage2Shell;

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
		this.name = string;
		VehicleScript.Block block = new VehicleScript.Block();
		this.readBlock(string2, 0, block);
		block = (VehicleScript.Block)block.children.get(0);
		Iterator iterator = block.elements.iterator();
		while (true) {
			while (iterator.hasNext()) {
				VehicleScript.BlockElement blockElement = (VehicleScript.BlockElement)iterator.next();
				String string3;
				String string4;
				String[] stringArray;
				if (blockElement.asValue() != null) {
					String[] stringArray2 = blockElement.asValue().string.split("=");
					string3 = stringArray2[0].trim();
					string4 = stringArray2[1].trim();
					if ("extents".equals(string3)) {
						this.LoadVector3f(string4, this.extents);
					} else if ("shadowOffset".equals(string3)) {
						this.LoadVector4f(string4, this.shadowOffset);
					} else if ("physicsChassisShape".equals(string3)) {
						this.LoadVector3f(string4, this.physicsChassisShape);
					} else if ("extentsOffset".equals(string3)) {
						this.LoadVector2f(string4, this.extentsOffset);
					} else if ("mass".equals(string3)) {
						this.mass = Float.valueOf(string4);
					} else if ("offRoadEfficiency".equalsIgnoreCase(string3)) {
						this.offroadEfficiency = Float.valueOf(string4);
					} else if ("centerOfMassOffset".equals(string3)) {
						this.LoadVector3f(string4, this.centerOfMassOffset);
					} else if ("engineForce".equals(string3)) {
						this.engineForce = Float.valueOf(string4);
					} else if ("engineIdleSpeed".equals(string3)) {
						this.engineIdleSpeed = Float.valueOf(string4);
					} else if ("gearRatioCount".equals(string3)) {
						this.gearRatioCount = Integer.valueOf(string4);
					} else if ("gearRatioR".equals(string3)) {
						this.gearRatio[0] = Float.valueOf(string4);
					} else if ("gearRatio1".equals(string3)) {
						this.gearRatio[1] = Float.valueOf(string4);
					} else if ("gearRatio2".equals(string3)) {
						this.gearRatio[2] = Float.valueOf(string4);
					} else if ("gearRatio3".equals(string3)) {
						this.gearRatio[3] = Float.valueOf(string4);
					} else if ("gearRatio4".equals(string3)) {
						this.gearRatio[4] = Float.valueOf(string4);
					} else if ("gearRatio5".equals(string3)) {
						this.gearRatio[5] = Float.valueOf(string4);
					} else if ("gearRatio6".equals(string3)) {
						this.gearRatio[6] = Float.valueOf(string4);
					} else if ("gearRatio7".equals(string3)) {
						this.gearRatio[7] = Float.valueOf(string4);
					} else if ("gearRatio8".equals(string3)) {
						this.gearRatio[8] = Float.valueOf(string4);
					} else if ("textureMaskEnable".equals(string3)) {
						this.textureMaskEnable = Boolean.valueOf(string4);
					} else if ("textureRust".equals(string3)) {
						this.textureRust = string4;
					} else if ("textureMask".equals(string3)) {
						this.textureMask = string4;
					} else if ("textureLights".equals(string3)) {
						this.textureLights = string4;
					} else if ("textureDamage1Overlay".equals(string3)) {
						this.textureDamage1Overlay = string4;
					} else if ("textureDamage1Shell".equals(string3)) {
						this.textureDamage1Shell = string4;
					} else if ("textureDamage2Overlay".equals(string3)) {
						this.textureDamage2Overlay = string4;
					} else if ("textureDamage2Shell".equals(string3)) {
						this.textureDamage2Shell = string4;
					} else if ("rollInfluence".equals(string3)) {
						this.rollInfluence = Float.valueOf(string4);
					} else if ("steeringIncrement".equals(string3)) {
						this.steeringIncrement = Float.valueOf(string4);
					} else if ("steeringClamp".equals(string3)) {
						this.steeringClamp = Float.valueOf(string4);
					} else if ("suspensionStiffness".equals(string3)) {
						this.suspensionStiffness = Float.valueOf(string4);
					} else if ("suspensionDamping".equals(string3)) {
						this.suspensionDamping = Float.valueOf(string4);
					} else if ("suspensionCompression".equals(string3)) {
						this.suspensionCompression = Float.valueOf(string4);
					} else if ("suspensionRestLength".equals(string3)) {
						this.suspensionRestLength = Float.valueOf(string4);
					} else if ("maxSuspensionTravelCm".equals(string3)) {
						this.maxSuspensionTravelCm = Float.valueOf(string4);
					} else if ("wheelFriction".equals(string3)) {
						this.wheelFriction = Float.valueOf(string4);
					} else if ("stoppingMovementForce".equals(string3)) {
						this.stoppingMovementForce = Float.valueOf(string4);
					} else if ("maxSpeed".equals(string3)) {
						this.maxSpeed = Float.valueOf(string4);
					} else if ("frontEndDurability".equals(string3)) {
						this.frontEndHealth = Integer.valueOf(string4);
					} else if ("rearEndDurability".equals(string3)) {
						this.rearEndHealth = Integer.valueOf(string4);
					} else if ("storageCapacity".equals(string3)) {
						this.storageCapacity = Integer.valueOf(string4);
					} else if ("engineLoudness".equals(string3)) {
						this.engineLoudness = Integer.valueOf(string4);
					} else if ("engineQuality".equals(string3)) {
						this.engineQuality = Integer.valueOf(string4);
					} else if ("seats".equals(string3)) {
						this.seats = Integer.valueOf(string4);
					} else if ("hasSiren".equals(string3)) {
						this.hasSiren = Boolean.valueOf(string4);
					} else if ("mechanicType".equals(string3)) {
						this.mechanicType = Integer.valueOf(string4);
					} else if ("forcedColor".equals(string3)) {
						stringArray = string4.split(" ");
						this.setForcedHue(Float.parseFloat(stringArray[0]));
						this.setForcedSat(Float.parseFloat(stringArray[1]));
						this.setForcedVal(Float.parseFloat(stringArray[2]));
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
						this.engineRepairLevel = Integer.valueOf(string4);
					} else if ("playerDamageProtection".equals(string3)) {
						this.setPlayerDamageProtection(Float.valueOf(string4));
					}
				} else {
					VehicleScript.Block block2 = blockElement.asBlock();
					if ("area".equals(block2.type)) {
						this.LoadArea(block2);
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
						} else {
							String string5;
							if (!"skin".equals(block2.type)) {
								if ("wheel".equals(block2.type)) {
									this.LoadWheel(block2);
								} else {
									Iterator iterator3;
									String string6;
									if ("lightbar".equals(block2.type)) {
										for (iterator3 = block2.values.iterator(); iterator3.hasNext(); this.lightbar.enable = true) {
											string4 = (String)iterator3.next();
											stringArray = string4.split("=");
											string6 = stringArray[0].trim();
											string5 = stringArray[1].trim();
											if ("soundSiren".equals(string6)) {
												this.lightbar.soundSiren0 = string5 + "Yelp";
												this.lightbar.soundSiren1 = string5 + "Wall";
												this.lightbar.soundSiren2 = string5 + "Alarm";
											}

											if ("soundSiren0".equals(string6)) {
												this.lightbar.soundSiren0 = string5;
											}

											if ("soundSiren1".equals(string6)) {
												this.lightbar.soundSiren1 = string5;
											}

											if ("soundSiren2".equals(string6)) {
												this.lightbar.soundSiren2 = string5;
											}
										}
									} else if ("sound".equals(block2.type)) {
										iterator3 = block2.values.iterator();
										while (iterator3.hasNext()) {
											string4 = (String)iterator3.next();
											stringArray = string4.split("=");
											string6 = stringArray[0].trim();
											string5 = stringArray[1].trim();
											if ("horn".equals(string6)) {
												this.sound.horn = string5;
												this.sound.hornEnable = true;
											}

											if ("backSignal".equals(string6)) {
												this.sound.backSignal = string5;
												this.sound.backSignalEnable = true;
											}
										}
									}
								}
							} else {
								VehicleScript.Skin skin = new VehicleScript.Skin();
								iterator2 = block2.values.iterator();
								while (iterator2.hasNext()) {
									String string7 = (String)iterator2.next();
									String[] stringArray3 = string7.split("=");
									string5 = stringArray3[0].trim();
									String string8 = stringArray3[1].trim();
									if ("texture".equals(string5)) {
										skin.texture = string8;
									}
								}

								if (skin.texture != null && !skin.texture.isEmpty()) {
									this.skins.add(skin);
								}
							}
						}
					}
				}
			}

			return;
		}
	}

	public void Loaded() {
		int int1;
		int int2;
		for (int1 = 0; int1 < this.passengers.size(); ++int1) {
			VehicleScript.Passenger passenger = (VehicleScript.Passenger)this.passengers.get(int1);
			for (int2 = 0; int2 < passenger.switchSeats.size(); ++int2) {
				VehicleScript.Passenger.SwitchSeat switchSeat = (VehicleScript.Passenger.SwitchSeat)passenger.switchSeats.get(int2);
				switchSeat.seat = this.getPassengerIndex(switchSeat.id);
				assert switchSeat.seat != -1;
			}
		}

		for (int1 = 0; int1 < this.parts.size(); ++int1) {
			VehicleScript.Part part = (VehicleScript.Part)this.parts.get(int1);
			if (part.container != null && part.container.seatID != null && !part.container.seatID.isEmpty()) {
				part.container.seat = this.getPassengerIndex(part.container.seatID);
				assert part.container.seat != -1;
			}

			if (part.specificItem && part.itemType != null) {
				for (int2 = 0; int2 < part.itemType.size(); ++int2) {
					part.itemType.set(int2, (String)part.itemType.get(int2) + this.mechanicType);
				}
			}
		}

		float[] floatArray = new float[100];
		byte byte1 = 0;
		int int3 = byte1 + 1;
		floatArray[byte1] = this.getModelScale();
		floatArray[int3++] = this.extents.x;
		floatArray[int3++] = this.extents.y;
		floatArray[int3++] = this.extents.z;
		floatArray[int3++] = this.physicsChassisShape.x;
		floatArray[int3++] = this.physicsChassisShape.y;
		floatArray[int3++] = this.physicsChassisShape.z;
		floatArray[int3++] = this.mass;
		floatArray[int3++] = this.centerOfMassOffset.x;
		floatArray[int3++] = this.centerOfMassOffset.y;
		floatArray[int3++] = this.centerOfMassOffset.z;
		floatArray[int3++] = this.rollInfluence;
		floatArray[int3++] = this.suspensionStiffness;
		floatArray[int3++] = this.suspensionCompression;
		floatArray[int3++] = this.suspensionDamping;
		floatArray[int3++] = this.maxSuspensionTravelCm;
		floatArray[int3++] = this.suspensionRestLength;
		floatArray[int3++] = this.wheelFriction;
		floatArray[int3++] = this.stoppingMovementForce;
		floatArray[int3++] = (float)this.getWheelCount();
		for (int2 = 0; int2 < this.getWheelCount(); ++int2) {
			floatArray[int3++] = this.getWheel(int2).front ? 1.0F : 0.0F;
			floatArray[int3++] = this.getWheel(int2).offset.x;
			floatArray[int3++] = this.getWheel(int2).offset.y;
			floatArray[int3++] = this.getWheel(int2).offset.z;
			floatArray[int3++] = this.getWheel(int2).radius;
		}

		Bullet.defineVehicleScript(this.getFullName(), floatArray);
	}

	private void LoadVector2f(String string, Vector2f vector2f) {
		String[] stringArray = string.split(" ");
		vector2f.set(Float.valueOf(stringArray[0]), Float.valueOf(stringArray[1]));
	}

	private void LoadVector3f(String string, Vector3f vector3f) {
		String[] stringArray = string.split(" ");
		vector3f.set(Float.valueOf(stringArray[0]), Float.valueOf(stringArray[1]), Float.valueOf(stringArray[2]));
	}

	private void LoadVector4f(String string, Vector4f vector4f) {
		String[] stringArray = string.split(" ");
		vector4f.set(Float.valueOf(stringArray[0]), Float.valueOf(stringArray[1]), Float.valueOf(stringArray[2]), Float.valueOf(stringArray[3]));
	}

	private void LoadVector2i(String string, Vector2i vector2i) {
		String[] stringArray = string.split(" ");
		vector2i.set(Integer.valueOf(stringArray[0]), Integer.valueOf(stringArray[1]));
	}

	private VehicleScript.Model LoadModel(VehicleScript.Block block, ArrayList arrayList) {
		VehicleScript.Model model = this.getModelById(block.id, arrayList);
		if (model == null) {
			model = new VehicleScript.Model();
			model.id = block.id;
			arrayList.add(model);
		}

		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			String[] stringArray = string.split("=");
			String string2 = stringArray[0].trim();
			String string3 = stringArray[1].trim();
			if ("file".equals(string2)) {
				model.file = string3;
			} else if ("offset".equals(string2)) {
				this.LoadVector3f(string3, model.offset);
			} else if ("rotate".equals(string2)) {
				this.LoadVector3f(string3, model.rotate);
			} else if ("scale".equals(string2)) {
				model.scale = Float.parseFloat(string3);
			}
		}

		return model;
	}

	private VehicleScript.Wheel LoadWheel(VehicleScript.Block block) {
		VehicleScript.Wheel wheel = this.getWheelById(block.id);
		if (wheel == null) {
			wheel = new VehicleScript.Wheel();
			wheel.id = block.id;
			this.wheels.add(wheel);
		}

		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			String[] stringArray = string.split("=");
			String string2 = stringArray[0].trim();
			String string3 = stringArray[1].trim();
			if ("model".equals(string2)) {
				wheel.model = string3;
			} else if ("front".equals(string2)) {
				wheel.front = Boolean.valueOf(string3);
			} else if ("offset".equals(string2)) {
				this.LoadVector3f(string3, wheel.offset);
			} else if ("radius".equals(string2)) {
				wheel.radius = Float.valueOf(string3);
			} else if ("width".equals(string2)) {
				wheel.width = Float.valueOf(string3);
			}
		}

		return wheel;
	}

	private VehicleScript.Passenger LoadPassenger(VehicleScript.Block block) {
		VehicleScript.Passenger passenger = this.getPassengerById(block.id);
		if (passenger == null) {
			passenger = new VehicleScript.Passenger();
			passenger.id = block.id;
			this.passengers.add(passenger);
		}

		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			String[] stringArray = string.split("=");
			String string2 = stringArray[0].trim();
			String string3 = stringArray[1].trim();
			if ("area".equals(string2)) {
				passenger.area = string3;
			} else if ("door".equals(string2)) {
				passenger.door = string3;
			} else if ("door2".equals(string2)) {
				passenger.door2 = string3;
			} else if ("hasRoof".equals(string2)) {
				passenger.hasRoof = Boolean.parseBoolean(string3);
			}
		}

		iterator = block.children.iterator();
		while (iterator.hasNext()) {
			VehicleScript.Block block2 = (VehicleScript.Block)iterator.next();
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

	private VehicleScript.Anim LoadAnim(VehicleScript.Block block, ArrayList arrayList) {
		VehicleScript.Anim anim = this.getAnimationById(block.id, arrayList);
		if (anim == null) {
			anim = new VehicleScript.Anim();
			anim.id = block.id;
			arrayList.add(anim);
		}

		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			String[] stringArray = string.split("=");
			String string2 = stringArray[0].trim();
			String string3 = stringArray[1].trim();
			if ("angle".equals(string2)) {
				this.LoadVector3f(string3, anim.angle);
			} else if ("anim".equals(string2)) {
				anim.anim = string3;
			} else if ("rate".equals(string2)) {
				anim.rate = Float.valueOf(string3);
			} else if ("offset".equals(string2)) {
				this.LoadVector3f(string3, anim.offset);
			} else if ("sound".equals(string2)) {
				anim.sound = string3;
			}
		}

		return anim;
	}

	private VehicleScript.Passenger.SwitchSeat LoadPassengerSwitchSeat(VehicleScript.Block block, VehicleScript.Passenger passenger) {
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
				String string = (String)iterator.next();
				String[] stringArray = string.split("=");
				String string2 = stringArray[0].trim();
				String string3 = stringArray[1].trim();
				if ("anim".equals(string2)) {
					switchSeat.anim = string3;
				} else if ("rate".equals(string2)) {
					switchSeat.rate = Float.valueOf(string3);
				} else if ("sound".equals(string2)) {
					switchSeat.sound = string3.isEmpty() ? null : string3;
				}
			}

			return switchSeat;
		}
	}

	private VehicleScript.Area LoadArea(VehicleScript.Block block) {
		VehicleScript.Area area = this.getAreaById(block.id);
		if (area == null) {
			area = new VehicleScript.Area();
			area.id = block.id;
			this.areas.add(area);
		}

		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			String[] stringArray = string.split("=");
			String string2 = stringArray[0].trim();
			String string3 = stringArray[1].trim();
			if ("xywh".equals(string2)) {
				String[] stringArray2 = string3.split(" ");
				area.x = Float.valueOf(stringArray2[0]);
				area.y = Float.valueOf(stringArray2[1]);
				area.w = Float.valueOf(stringArray2[2]);
				area.h = Float.valueOf(stringArray2[3]);
			}
		}

		return area;
	}

	private VehicleScript.Part LoadPart(VehicleScript.Block block) {
		VehicleScript.Part part = this.getPartById(block.id);
		if (part == null) {
			part = new VehicleScript.Part();
			part.id = block.id;
			this.parts.add(part);
		}

		Iterator iterator = block.values.iterator();
		while (true) {
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				String[] stringArray = string.split("=");
				String string2 = stringArray[0].trim();
				String string3 = stringArray[1].trim();
				if ("area".equals(string2)) {
					part.area = string3.isEmpty() ? null : string3;
				} else if ("itemType".equals(string2)) {
					part.itemType = new ArrayList();
					String[] stringArray2 = string3.split(";");
					String[] stringArray3 = stringArray2;
					int int1 = stringArray2.length;
					for (int int2 = 0; int2 < int1; ++int2) {
						String string4 = stringArray3[int2];
						part.itemType.add(string4);
					}
				} else if ("parent".equals(string2)) {
					part.parent = string3.isEmpty() ? null : string3;
				} else if ("mechanicRequireKey".equals(string2)) {
					part.mechanicRequireKey = Boolean.parseBoolean(string3);
				} else if ("repairMechanic".equals(string2)) {
					part.setRepairMechanic(Boolean.parseBoolean(string3));
				} else if ("wheel".equals(string2)) {
					part.wheel = string3;
				} else if ("category".equals(string2)) {
					part.category = string3;
				} else if ("specificItem".equals(string2)) {
					part.specificItem = Boolean.parseBoolean(string3);
				} else if ("hasLightsRear".equals(string2)) {
					part.hasLightsRear = Boolean.parseBoolean(string3);
				}
			}

			iterator = block.children.iterator();
			while (iterator.hasNext()) {
				VehicleScript.Block block2 = (VehicleScript.Block)iterator.next();
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

	private VehicleScript.Door LoadDoor(VehicleScript.Block block) {
		VehicleScript.Door door = new VehicleScript.Door();
		String[] stringArray;
		String string;
		for (Iterator iterator = block.values.iterator(); iterator.hasNext(); string = stringArray[1].trim()) {
			String string2 = (String)iterator.next();
			stringArray = string2.split("=");
			String string3 = stringArray[0].trim();
		}

		return door;
	}

	private VehicleScript.Window LoadWindow(VehicleScript.Block block) {
		VehicleScript.Window window = new VehicleScript.Window();
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			String[] stringArray = string.split("=");
			String string2 = stringArray[0].trim();
			String string3 = stringArray[1].trim();
			if ("openable".equals(string2)) {
				window.openable = Boolean.valueOf(string3);
			}
		}

		return window;
	}

	private VehicleScript.Container LoadContainer(VehicleScript.Block block, VehicleScript.Container container) {
		VehicleScript.Container container2 = container == null ? new VehicleScript.Container() : container;
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			String[] stringArray = string.split("=");
			String string2 = stringArray[0].trim();
			String string3 = stringArray[1].trim();
			if ("capacity".equals(string2)) {
				container2.capacity = Integer.valueOf(string3);
			} else if ("conditionAffectsCapacity".equals(string2)) {
				container2.conditionAffectsCapacity = Boolean.valueOf(string3);
			} else if ("contentType".equals(string2)) {
				container2.contentType = string3;
			} else if ("seat".equals(string2)) {
				container2.seatID = string3;
			} else if ("test".equals(string2)) {
				container2.luaTest = string3;
			}
		}

		return container2;
	}

	private HashMap LoadLuaFunctions(VehicleScript.Block block) {
		HashMap hashMap = new HashMap();
		Iterator iterator = block.values.iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			String[] stringArray = string.split("=");
			String string2 = stringArray[0].trim();
			String string3 = stringArray[1].trim();
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

	private KahluaTable LoadTable(VehicleScript.Block block, KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = kahluaTable == null ? LuaManager.platform.newTable() : kahluaTable;
		Iterator iterator;
		String string;
		String string2;
		for (iterator = block.values.iterator(); iterator.hasNext(); kahluaTable2.rawset(this.checkIntegerKey(string), string2)) {
			String string3 = (String)iterator.next();
			String[] stringArray = string3.split("=");
			string = stringArray[0].trim();
			string2 = stringArray[1].trim();
			if (string2.isEmpty()) {
				string2 = null;
			}
		}

		iterator = block.children.iterator();
		while (iterator.hasNext()) {
			VehicleScript.Block block2 = (VehicleScript.Block)iterator.next();
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

	private VehicleScript.Position LoadPosition(VehicleScript.Block block, ArrayList arrayList) {
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
				String string = (String)iterator.next();
				String[] stringArray = string.split("=");
				String string2 = stringArray[0].trim();
				String string3 = stringArray[1].trim();
				if ("rotate".equals(string2)) {
					this.LoadVector3f(string3, position.rotate);
				} else if ("offset".equals(string2)) {
					this.LoadVector3f(string3, position.offset);
				} else if ("area".equals(string2)) {
					position.area = string3.isEmpty() ? null : string3;
				}
			}

			return position;
		}
	}

	private int readBlock(String string, int int1, VehicleScript.Block block) {
		int int2;
		for (int2 = int1; int2 < string.length(); ++int2) {
			if (string.charAt(int2) == '{') {
				VehicleScript.Block block2 = new VehicleScript.Block();
				block.children.add(block2);
				block.elements.add(block2);
				String string2 = string.substring(int1, int2).trim();
				String[] stringArray = string2.split("\\s+");
				block2.type = stringArray[0];
				block2.id = stringArray.length > 1 ? stringArray[1] : null;
				int2 = this.readBlock(string, int2 + 1, block2);
				int1 = int2;
			} else {
				if (string.charAt(int2) == '}') {
					return int2 + 1;
				}

				if (string.charAt(int2) == ',') {
					VehicleScript.Value value = new VehicleScript.Value();
					value.string = string.substring(int1, int2);
					block.values.add(value.string);
					block.elements.add(value);
					int1 = int2 + 1;
				}
			}
		}

		return int2;
	}

	public String getName() {
		return this.name;
	}

	public String getFullName() {
		return this.getModule().getName() + "." + this.getName();
	}

	public VehicleScript.Model getModel() {
		return this.models.isEmpty() ? null : (VehicleScript.Model)this.models.get(0);
	}

	public float getModelScale() {
		return this.getModel() == null ? 1.0F : this.getModel().scale;
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
			if (model.id != null && model.id.equals(string)) {
				return model;
			}
		}

		return null;
	}

	public VehicleScript.Model getModelById(String string) {
		return this.getModelById(string, this.models);
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

	public Vector4f getShadowOffset() {
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

	public int getFrontEndHealth() {
		return this.frontEndHealth;
	}

	public int getRearEndHealth() {
		return this.rearEndHealth;
	}

	public int getStorageCapacity() {
		return this.storageCapacity;
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

	private static class Block implements VehicleScript.BlockElement {
		public String type;
		public String id;
		public ArrayList elements;
		public ArrayList values;
		public ArrayList children;

		private Block() {
			this.elements = new ArrayList();
			this.values = new ArrayList();
			this.children = new ArrayList();
		}

		public VehicleScript.Block asBlock() {
			return this;
		}

		public VehicleScript.Value asValue() {
			return null;
		}

		public boolean isEmpty() {
			return this.elements.isEmpty();
		}

		Block(Object object) {
			this();
		}
	}

	private static class Value implements VehicleScript.BlockElement {
		String string;

		private Value() {
		}

		public VehicleScript.Block asBlock() {
			return null;
		}

		public VehicleScript.Value asValue() {
			return this;
		}

		Value(Object object) {
			this();
		}
	}

	private interface BlockElement {

		VehicleScript.Block asBlock();

		VehicleScript.Value asValue();
	}

	public static class Position {
		public String id;
		public Vector3f offset = new Vector3f();
		public Vector3f rotate = new Vector3f();
		public String area = null;

		public Vector3f getOffset() {
			return this.offset;
		}

		public Vector3f getRotate() {
			return this.rotate;
		}

		VehicleScript.Position makeCopy() {
			VehicleScript.Position position = new VehicleScript.Position();
			position.id = this.id;
			position.offset.set((Vector3fc)this.offset);
			position.rotate.set((Vector3fc)this.rotate);
			return position;
		}
	}

	public static class Window {
		public boolean openable;

		VehicleScript.Window makeCopy() {
			VehicleScript.Window window = new VehicleScript.Window();
			window.openable = this.openable;
			return window;
		}
	}

	public static class Door {

		VehicleScript.Door makeCopy() {
			VehicleScript.Door door = new VehicleScript.Door();
			return door;
		}
	}

	public static class Part {
		public String id = "Unknown";
		public String parent;
		public ArrayList itemType;
		public VehicleScript.Container container;
		public String area;
		public String wheel;
		public HashMap tables;
		public HashMap luaFunctions;
		public ArrayList models;
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
					part.tables.put(entry.getKey(), kahluaTable);
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

	public static class Container {
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

	public static class Area {
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

	public static class Skin {
		public String texture;
	}

	public static class Model {
		public String id;
		public String file;
		public float scale = 1.0F;
		public Vector3f offset = new Vector3f();
		public Vector3f rotate = new Vector3f();

		VehicleScript.Model makeCopy() {
			VehicleScript.Model model = new VehicleScript.Model();
			model.id = this.id;
			model.file = this.file;
			model.scale = this.scale;
			model.offset = new Vector3f(this.offset);
			model.rotate = new Vector3f(this.rotate);
			return model;
		}
	}

	public static class Passenger {
		public String id;
		public ArrayList anims = new ArrayList();
		public ArrayList switchSeats = new ArrayList();
		public boolean hasRoof = true;
		public String door;
		public String door2;
		public String area;
		public ArrayList positions = new ArrayList();

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
			passenger.door = this.door;
			passenger.door2 = this.door2;
			passenger.area = this.area;
			for (int1 = 0; int1 < this.positions.size(); ++int1) {
				passenger.positions.add(((VehicleScript.Position)this.positions.get(int1)).makeCopy());
			}

			return passenger;
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

		public static class SwitchSeat {
			public String id;
			public int seat;
			public String anim;
			public float rate = 1.0F;
			public String sound;

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

	public static class Anim {
		public String id;
		public String anim;
		public float rate = 1.0F;
		public Vector3f offset = new Vector3f();
		public Vector3f angle = new Vector3f();
		public String sound;

		VehicleScript.Anim makeCopy() {
			VehicleScript.Anim anim = new VehicleScript.Anim();
			anim.id = this.id;
			anim.anim = this.anim;
			anim.rate = this.rate;
			anim.offset = new Vector3f(this.offset);
			anim.angle = new Vector3f(this.angle);
			anim.sound = this.sound;
			return anim;
		}
	}

	public static class Wheel {
		public String id;
		public String model;
		public boolean front;
		public Vector3f offset = new Vector3f();
		public float radius = 0.5F;
		public float width = 0.4F;

		public Vector3f getOffset() {
			return this.offset;
		}

		VehicleScript.Wheel makeCopy() {
			VehicleScript.Wheel wheel = new VehicleScript.Wheel();
			wheel.id = this.id;
			wheel.model = this.model;
			wheel.front = this.front;
			wheel.offset = new Vector3f(this.offset);
			wheel.radius = this.radius;
			wheel.width = this.width;
			return wheel;
		}
	}

	public static class Sounds {
		public boolean hornEnable = false;
		public String horn = new String();
		public boolean backSignalEnable = false;
		public String backSignal = new String();
	}

	public static class LightBar {
		public boolean enable = false;
		public String soundSiren0 = new String();
		public String soundSiren1 = new String();
		public String soundSiren2 = new String();
	}
}
