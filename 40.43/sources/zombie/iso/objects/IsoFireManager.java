package zombie.iso.objects;

import java.util.ArrayList;
import java.util.Stack;
import zombie.WorldSoundManager;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;


public class IsoFireManager {
	public static int NumActiveFires = 0;
	public static double Red_Oscilator = 0.0;
	public static double Green_Oscilator = 0.0;
	public static double Blue_Oscilator = 0.0;
	public static double Red_Oscilator_Rate = 0.10000000149011612;
	public static double Green_Oscilator_Rate = 0.12999999523162842;
	public static double Blue_Oscilator_Rate = 0.08760000020265579;
	public static double Red_Oscilator_Val = 0.0;
	public static double Green_Oscilator_Val = 0.0;
	public static double Blue_Oscilator_Val = 0.0;
	public static double OscilatorSpeedScalar = 15.600000381469727;
	public static double OscilatorEffectScalar = 0.0038999998942017555;
	public static int MaxFireObjects = 75;
	public static int FireRecalcDelay = 25;
	public static int FireRecalc;
	public static boolean LightCalcFromBurningCharacters;
	public static float FireAlpha;
	public static float SmokeAlpha;
	public static float FireAnimDelay;
	public static float SmokeAnimDelay;
	public static ColorInfo FireTintMod;
	public static ColorInfo SmokeTintMod;
	public static Stack FireStack;
	public static Stack CharactersOnFire_Stack;
	public static long fireSound;
	public static BaseSoundEmitter fireEmitter;
	private static Stack updateStack;

	public static void Add(IsoFire fire) {
		if (FireStack.contains(fire)) {
			System.out.println("IsoFireManager.Add already added fire, ignoring");
		} else {
			if (NumActiveFires < MaxFireObjects) {
				FireStack.add(fire);
				++NumActiveFires;
			} else {
				IsoFire fire2 = null;
				int int1 = 0;
				for (int int2 = 0; int2 < FireStack.size(); ++int2) {
					if (((IsoFire)FireStack.get(int2)).Age > int1) {
						int1 = ((IsoFire)FireStack.get(int2)).Age;
						fire2 = (IsoFire)FireStack.get(int2);
					}
				}

				if (fire2 != null && fire2.square != null) {
					fire2.square.getProperties().UnSet(IsoFlagType.burning);
					fire2.square.getProperties().UnSet(IsoFlagType.smoke);
					fire2.RemoveAttachedAnims();
					fire2.removeFromWorld();
					fire2.removeFromSquare();
				}

				FireStack.add(fire);
				++NumActiveFires;
			}
		}
	}

	public static void AddBurningCharacter(IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < CharactersOnFire_Stack.size(); ++int1) {
			if (CharactersOnFire_Stack.get(int1) == gameCharacter) {
				return;
			}
		}

		CharactersOnFire_Stack.add(gameCharacter);
	}

	public static void Fire_LightCalc(IsoGridSquare square, IsoGridSquare square2, int int1) {
		if (square2 != null && square != null) {
			byte byte1 = 0;
			byte byte2 = 8;
			int int2 = byte1 + Math.abs(square2.getX() - square.getX());
			int2 += Math.abs(square2.getY() - square.getY());
			int2 += Math.abs(square2.getZ() - square.getZ());
			if (int2 <= byte2) {
				float float1 = 0.199F / (float)byte2 * (float)(byte2 - int2);
				float float2 = float1 * 0.6F;
				float float3 = float1 * 0.4F;
				if (square2.getLightInfluenceR() == null) {
					square2.setLightInfluenceR(new ArrayList());
				}

				square2.getLightInfluenceR().add(float1);
				if (square2.getLightInfluenceG() == null) {
					square2.setLightInfluenceG(new ArrayList());
				}

				square2.getLightInfluenceG().add(float2);
				if (square2.getLightInfluenceB() == null) {
					square2.setLightInfluenceB(new ArrayList());
				}

				square2.getLightInfluenceB().add(float3);
				ColorInfo colorInfo = square2.lighting[int1].lightInfo();
				colorInfo.r += float1;
				colorInfo.g += float2;
				colorInfo.b += float3;
				if (colorInfo.r > 1.0F) {
					colorInfo.r = 1.0F;
				}

				if (colorInfo.g > 1.0F) {
					colorInfo.g = 1.0F;
				}

				if (colorInfo.b > 1.0F) {
					colorInfo.b = 1.0F;
				}
			}
		}
	}

	public static void LightTileWithFire(IsoGridSquare square) {
	}

	public static void explode(IsoCell cell, IsoGridSquare square, int int1) {
		if (square != null) {
			IsoGridSquare square2 = null;
			Object object = null;
			FireRecalc = 1;
			for (int int2 = -2; int2 <= 2; ++int2) {
				for (int int3 = -2; int3 <= 2; ++int3) {
					for (int int4 = 0; int4 <= 1; ++int4) {
						square2 = cell.getGridSquare(square.getX() + int2, square.getY() + int3, square.getZ() + int4);
						if (square2 != null && Rand.Next(100) < int1 && IsoFire.CanAddFire(square2, true)) {
							StartFire(cell, square2, true, Rand.Next(100, 250 + int1));
							square2.BurnWalls(true);
						}
					}
				}
			}
		}
	}

	public static void MolotovSmash(IsoCell cell, IsoGridSquare square) {
		if (square != null) {
			IsoGridSquare square2 = null;
			Object object = null;
			FireRecalc = 1;
			square2 = cell.getGridSquare(square.getX(), square.getY() - 1, square.getZ());
			StartFire(cell, square2, true, 50);
			square2 = cell.getGridSquare(square.getX() + 1, square.getY() - 1, square.getZ());
			StartFire(cell, square2, true, 50);
			square2 = cell.getGridSquare(square.getX() + 1, square.getY(), square.getZ());
			StartFire(cell, square2, true, 50);
			square2 = cell.getGridSquare(square.getX() + 1, square.getY() + 1, square.getZ());
			StartFire(cell, square2, true, 50);
			square2 = cell.getGridSquare(square.getX(), square.getY() + 1, square.getZ());
			StartFire(cell, square2, true, 50);
			square2 = cell.getGridSquare(square.getX() - 1, square.getY() + 1, square.getZ());
			StartFire(cell, square2, true, 50);
			square2 = cell.getGridSquare(square.getX() - 1, square.getY(), square.getZ());
			StartFire(cell, square2, true, 50);
			square2 = cell.getGridSquare(square.getX() - 1, square.getY() - 1, square.getZ());
			StartFire(cell, square2, true, 50);
			square2 = cell.getGridSquare(square.getX(), square.getY(), square.getZ());
			StartFire(cell, square2, true, 50);
		}
	}

	public static void Remove(IsoFire fire) {
		if (!FireStack.contains(fire)) {
			System.out.println("IsoFireManager.Remove unknown fire, ignoring");
		} else {
			FireStack.remove(fire);
			--NumActiveFires;
		}
	}

	public static void RemoveBurningCharacter(IsoGameCharacter gameCharacter) {
		CharactersOnFire_Stack.remove(gameCharacter);
	}

	public static void StartFire(IsoCell cell, IsoGridSquare square, boolean boolean1, int int1, int int2) {
		if (square.getFloor() != null && square.getFloor().getSprite() != null) {
			int1 -= square.getFloor().getSprite().firerequirement;
		}

		if (int1 < 5) {
			int1 = 5;
		}

		if (IsoFire.CanAddFire(square, boolean1)) {
			if (GameClient.bClient) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)75, byteBufferWriter);
				byteBufferWriter.putInt(square.getX());
				byteBufferWriter.putInt(square.getY());
				byteBufferWriter.putInt(square.getZ());
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putBoolean(boolean1);
				byteBufferWriter.putInt(int2);
				byteBufferWriter.putBoolean(false);
				GameClient.connection.endPacketImmediate();
			} else if (GameServer.bServer) {
				GameServer.startFireOnClient(square, int1, boolean1, int2, false);
			} else {
				IsoFire fire = new IsoFire(cell, square, boolean1, int1, int2);
				Add(fire);
				square.getObjects().add(fire);
				if (Rand.Next(5) == 0) {
					WorldSoundManager.instance.addSound(fire, square.getX(), square.getY(), square.getZ(), 20, 20);
				}
			}
		}
	}

	public static void StartSmoke(IsoCell cell, IsoGridSquare square, boolean boolean1, int int1, int int2) {
		if (IsoFire.CanAddSmoke(square, boolean1)) {
			if (GameClient.bClient) {
				ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
				PacketTypes.doPacket((short)75, byteBufferWriter);
				byteBufferWriter.putInt(square.getX());
				byteBufferWriter.putInt(square.getY());
				byteBufferWriter.putInt(square.getZ());
				byteBufferWriter.putInt(int1);
				byteBufferWriter.putBoolean(boolean1);
				byteBufferWriter.putInt(int2);
				byteBufferWriter.putBoolean(true);
				GameClient.connection.endPacketImmediate();
			} else if (GameServer.bServer) {
				GameServer.startFireOnClient(square, int1, boolean1, int2, true);
			} else {
				IsoFire fire = new IsoFire(cell, square, boolean1, int1, int2, true);
				Add(fire);
				square.getObjects().add(fire);
			}
		}
	}

	public static void StartFire(IsoCell cell, IsoGridSquare square, boolean boolean1, int int1) {
		StartFire(cell, square, boolean1, int1, 0);
	}

	public static void Update() {
		Red_Oscilator_Val = Math.sin(Red_Oscilator += Blue_Oscilator_Rate * OscilatorSpeedScalar);
		Green_Oscilator_Val = Math.sin(Green_Oscilator += Blue_Oscilator_Rate * OscilatorSpeedScalar);
		Blue_Oscilator_Val = Math.sin(Blue_Oscilator += Blue_Oscilator_Rate * OscilatorSpeedScalar);
		Red_Oscilator_Val = (Red_Oscilator_Val + 1.0) / 2.0;
		Green_Oscilator_Val = (Green_Oscilator_Val + 1.0) / 2.0;
		Blue_Oscilator_Val = (Blue_Oscilator_Val + 1.0) / 2.0;
		Red_Oscilator_Val *= OscilatorEffectScalar;
		Green_Oscilator_Val *= OscilatorEffectScalar;
		Blue_Oscilator_Val *= OscilatorEffectScalar;
		updateStack.clear();
		updateStack.addAll(FireStack);
		for (int int1 = 0; int1 < updateStack.size(); ++int1) {
			((IsoFire)updateStack.get(int1)).update();
		}

		--FireRecalc;
		if (FireRecalc < 0) {
			FireRecalc = FireRecalcDelay;
		}

		if (fireEmitter != null) {
			fireEmitter.tick();
		}
	}

	public static void RemoveAllOn(IsoGridSquare square) {
		for (int int1 = 0; int1 < FireStack.size(); ++int1) {
			if (((IsoFire)FireStack.get(int1)).square == square) {
				IsoFire fire = (IsoFire)FireStack.get(int1);
				fire.extinctFire();
				--NumActiveFires;
				--int1;
			}
		}
	}

	public static void Reset() {
		FireStack.clear();
		NumActiveFires = 0;
		CharactersOnFire_Stack.clear();
	}

	static  {
		FireRecalc = FireRecalcDelay;
		LightCalcFromBurningCharacters = false;
		FireAlpha = 1.0F;
		SmokeAlpha = 0.3F;
		FireAnimDelay = 0.2F;
		SmokeAnimDelay = 0.2F;
		FireTintMod = new ColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
		SmokeTintMod = new ColorInfo(0.5F, 0.5F, 0.5F, 1.0F);
		FireStack = new Stack();
		CharactersOnFire_Stack = new Stack();
		fireSound = -1L;
		fireEmitter = null;
		updateStack = new Stack();
	}
}
