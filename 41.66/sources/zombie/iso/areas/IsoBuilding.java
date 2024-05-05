package zombie.iso.areas;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.core.Rand;
import zombie.core.opengl.RenderSettings;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemType;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LotHeader;
import zombie.iso.RoomDef;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;


public final class IsoBuilding extends IsoArea {
	public Rectangle bounds;
	public final Vector Exits = new Vector();
	public boolean IsResidence = true;
	public final ArrayList container = new ArrayList();
	public final Vector Rooms = new Vector();
	public final Vector Windows = new Vector();
	public int ID = 0;
	public static int IDMax = 0;
	public int safety = 0;
	public int transparentWalls = 0;
	private boolean isToxic = false;
	public static float PoorBuildingScore = 10.0F;
	public static float GoodBuildingScore = 100.0F;
	public int scoreUpdate = -1;
	public BuildingDef def;
	public boolean bSeenInside = false;
	public ArrayList lights = new ArrayList();
	static ArrayList tempo = new ArrayList();
	static ArrayList tempContainer = new ArrayList();
	static ArrayList RandomContainerChoices = new ArrayList();
	static ArrayList windowchoices = new ArrayList();

	public int getRoomsNumber() {
		return this.Rooms.size();
	}

	public IsoBuilding() {
		this.ID = IDMax++;
		this.scoreUpdate = -120 + Rand.Next(120);
	}

	public int getID() {
		return this.ID;
	}

	public void TriggerAlarm() {
	}

	public IsoBuilding(IsoCell cell) {
		this.ID = IDMax++;
		this.scoreUpdate = -120 + Rand.Next(120);
	}

	public boolean ContainsAllItems(Stack stack) {
		return false;
	}

	public float ScoreBuildingPersonSpecific(SurvivorDesc survivorDesc, boolean boolean1) {
		float float1 = 0.0F;
		float1 += (float)(this.Rooms.size() * 5);
		float1 += (float)(this.Exits.size() * 15);
		float1 -= (float)(this.transparentWalls * 10);
		for (int int1 = 0; int1 < this.container.size(); ++int1) {
			ItemContainer itemContainer = (ItemContainer)this.container.get(int1);
			float1 += (float)(itemContainer.Items.size() * 3);
		}

		BuildingScore buildingScore;
		if (!IsoWorld.instance.CurrentCell.getBuildingScores().containsKey(this.ID)) {
			buildingScore = new BuildingScore(this);
			buildingScore.building = this;
			IsoWorld.instance.CurrentCell.getBuildingScores().put(this.ID, buildingScore);
			this.ScoreBuildingGeneral(buildingScore);
		}

		buildingScore = (BuildingScore)IsoWorld.instance.CurrentCell.getBuildingScores().get(this.ID);
		float1 += (buildingScore.defense + buildingScore.food + (float)buildingScore.size + buildingScore.weapons + buildingScore.wood) * 10.0F;
		int int2 = -10000;
		int int3 = -10000;
		if (!this.Exits.isEmpty()) {
			IsoRoomExit roomExit = (IsoRoomExit)this.Exits.get(0);
			int2 = roomExit.x;
			int3 = roomExit.y;
		}

		float float2 = IsoUtils.DistanceManhatten(survivorDesc.getInstance().getX(), survivorDesc.getInstance().getY(), (float)int2, (float)int3);
		if (float2 > 0.0F) {
			if (boolean1) {
				float1 *= float2 * 0.5F;
			} else {
				float1 /= float2 * 0.5F;
			}
		}

		return float1;
	}

	public BuildingDef getDef() {
		return this.def;
	}

	public void update() {
		if (!this.Exits.isEmpty()) {
			byte byte1 = 0;
			int int1 = 0;
			for (int int2 = 0; int2 < this.Rooms.size(); ++int2) {
				IsoRoom room = (IsoRoom)this.Rooms.get(int2);
				if (room.layer == 0) {
					for (int int3 = 0; int3 < room.TileList.size(); ++int3) {
						++int1;
						IsoGridSquare square = (IsoGridSquare)room.TileList.get(int3);
					}
				}
			}

			if (int1 == 0) {
				++int1;
			}

			int int4 = (int)((float)byte1 / (float)int1);
			--this.scoreUpdate;
			if (this.scoreUpdate <= 0) {
				this.scoreUpdate += 120;
				BuildingScore buildingScore = null;
				if (IsoWorld.instance.CurrentCell.getBuildingScores().containsKey(this.ID)) {
					buildingScore = (BuildingScore)IsoWorld.instance.CurrentCell.getBuildingScores().get(this.ID);
				} else {
					buildingScore = new BuildingScore(this);
					buildingScore.building = this;
				}

				buildingScore = this.ScoreBuildingGeneral(buildingScore);
				buildingScore.defense += (float)(int4 * 10);
				this.safety = int4;
				IsoWorld.instance.CurrentCell.getBuildingScores().put(this.ID, buildingScore);
			}
		}
	}

	public void AddRoom(IsoRoom room) {
		this.Rooms.add(room);
		if (this.bounds == null) {
			this.bounds = (Rectangle)room.bounds.clone();
		}

		if (room != null && room.bounds != null) {
			this.bounds.add(room.bounds);
		}
	}

	public void CalculateExits() {
		Iterator iterator = this.Rooms.iterator();
		while (iterator.hasNext()) {
			IsoRoom room = (IsoRoom)iterator.next();
			Iterator iterator2 = room.Exits.iterator();
			while (iterator2.hasNext()) {
				IsoRoomExit roomExit = (IsoRoomExit)iterator2.next();
				if (roomExit.To.From == null && room.layer == 0) {
					this.Exits.add(roomExit);
				}
			}
		}
	}

	public void CalculateWindows() {
		Iterator iterator = this.Rooms.iterator();
		label118: while (iterator.hasNext()) {
			IsoRoom room = (IsoRoom)iterator.next();
			Iterator iterator2 = room.TileList.iterator();
			while (true) {
				IsoGridSquare square;
				IsoObject object;
				int int1;
				do {
					if (!iterator2.hasNext()) {
						continue label118;
					}

					IsoGridSquare square2 = (IsoGridSquare)iterator2.next();
					IsoGridSquare square3 = square2.getCell().getGridSquare(square2.getX(), square2.getY() + 1, square2.getZ());
					square = square2.getCell().getGridSquare(square2.getX() + 1, square2.getY(), square2.getZ());
					if (square2.getProperties().Is(IsoFlagType.collideN) && square2.getProperties().Is(IsoFlagType.transparentN)) {
						++room.transparentWalls;
						++this.transparentWalls;
					}

					if (square2.getProperties().Is(IsoFlagType.collideW) && square2.getProperties().Is(IsoFlagType.transparentW)) {
						++room.transparentWalls;
						++this.transparentWalls;
					}

					boolean boolean1;
					if (square3 != null) {
						boolean1 = square3.getRoom() != null;
						if (square3.getRoom() != null && square3.getRoom().building != room.building) {
							boolean1 = false;
						}

						if (square3.getProperties().Is(IsoFlagType.collideN) && square3.getProperties().Is(IsoFlagType.transparentN) && !boolean1) {
							++room.transparentWalls;
							++this.transparentWalls;
						}
					}

					if (square != null) {
						boolean1 = square.getRoom() != null;
						if (square.getRoom() != null && square.getRoom().building != room.building) {
							boolean1 = false;
						}

						if (square.getProperties().Is(IsoFlagType.collideW) && square.getProperties().Is(IsoFlagType.transparentW) && !boolean1) {
							++room.transparentWalls;
							++this.transparentWalls;
						}
					}

					for (int1 = 0; int1 < square2.getSpecialObjects().size(); ++int1) {
						object = (IsoObject)square2.getSpecialObjects().get(int1);
						if (object instanceof IsoWindow) {
							this.Windows.add((IsoWindow)object);
						}
					}

					if (square3 != null) {
						for (int1 = 0; int1 < square3.getSpecialObjects().size(); ++int1) {
							object = (IsoObject)square3.getSpecialObjects().get(int1);
							if (object instanceof IsoWindow) {
								this.Windows.add((IsoWindow)object);
							}
						}
					}
				}		 while (square == null);

				for (int1 = 0; int1 < square.getSpecialObjects().size(); ++int1) {
					object = (IsoObject)square.getSpecialObjects().get(int1);
					if (object instanceof IsoWindow) {
						this.Windows.add((IsoWindow)object);
					}
				}
			}
		}
	}

	public void FillContainers() {
		boolean boolean1 = false;
		Iterator iterator = this.Rooms.iterator();
		while (iterator.hasNext()) {
			IsoRoom room = (IsoRoom)iterator.next();
			if (room.RoomDef != null && room.RoomDef.contains("tutorial")) {
				boolean1 = true;
			}

			if (!room.TileList.isEmpty()) {
				IsoGridSquare square = (IsoGridSquare)room.TileList.get(0);
				if (square.getX() < 74 && square.getY() < 32) {
					boolean1 = true;
				}
			}

			if (room.RoomDef.contains("shop")) {
				this.IsResidence = false;
			}

			Iterator iterator2 = room.TileList.iterator();
			while (iterator2.hasNext()) {
				IsoGridSquare square2 = (IsoGridSquare)iterator2.next();
				for (int int1 = 0; int1 < square2.getObjects().size(); ++int1) {
					IsoObject object = (IsoObject)square2.getObjects().get(int1);
					if (object.hasWater()) {
						room.getWaterSources().add(object);
					}

					if (object.container != null) {
						this.container.add(object.container);
						room.Containers.add(object.container);
					}
				}

				if (square2.getProperties().Is(IsoFlagType.bed)) {
					room.Beds.add(square2);
				}
			}
		}
	}

	public ItemContainer getContainerWith(ItemType itemType) {
		Iterator iterator = this.Rooms.iterator();
		while (iterator.hasNext()) {
			IsoRoom room = (IsoRoom)iterator.next();
			Iterator iterator2 = room.Containers.iterator();
			while (iterator2.hasNext()) {
				ItemContainer itemContainer = (ItemContainer)iterator2.next();
				if (itemContainer.HasType(itemType)) {
					return itemContainer;
				}
			}
		}

		return null;
	}

	public IsoRoom getRandomRoom() {
		if (this.Rooms.size() == 0) {
			return null;
		} else {
			IsoRoom room = (IsoRoom)this.Rooms.get(Rand.Next(this.Rooms.size()));
			return room;
		}
	}

	private BuildingScore ScoreBuildingGeneral(BuildingScore buildingScore) {
		buildingScore.food = 0.0F;
		buildingScore.defense = 0.0F;
		buildingScore.weapons = 0.0F;
		buildingScore.wood = 0.0F;
		buildingScore.building = this;
		buildingScore.size = 0;
		buildingScore.defense += (float)((this.Exits.size() - 1) * 140);
		buildingScore.defense -= (float)(this.transparentWalls * 40);
		buildingScore.size = this.Rooms.size() * 10;
		buildingScore.size += this.container.size() * 10;
		return buildingScore;
	}

	public IsoGridSquare getFreeTile() {
		IsoGridSquare square = null;
		do {
			IsoRoom room = (IsoRoom)this.Rooms.get(Rand.Next(this.Rooms.size()));
			square = room.getFreeTile();
		} while (square == null);

		return square;
	}

	public boolean hasWater() {
		Iterator iterator = this.Rooms.iterator();
		while (iterator != null && iterator.hasNext()) {
			IsoRoom room = (IsoRoom)iterator.next();
			if (!room.WaterSources.isEmpty()) {
				IsoObject object = null;
				for (int int1 = 0; int1 < room.WaterSources.size(); ++int1) {
					if (((IsoObject)room.WaterSources.get(int1)).hasWater()) {
						object = (IsoObject)room.WaterSources.get(int1);
						break;
					}
				}

				if (object != null) {
					return true;
				}
			}
		}

		return false;
	}

	public void CreateFrom(BuildingDef buildingDef, LotHeader lotHeader) {
		for (int int1 = 0; int1 < buildingDef.rooms.size(); ++int1) {
			IsoRoom room = lotHeader.getRoom(((RoomDef)buildingDef.rooms.get(int1)).ID);
			room.building = this;
			this.Rooms.add(room);
		}
	}

	public void setAllExplored(boolean boolean1) {
		this.def.bAlarmed = false;
		for (int int1 = 0; int1 < this.Rooms.size(); ++int1) {
			IsoRoom room = (IsoRoom)this.Rooms.get(int1);
			room.def.setExplored(boolean1);
			for (int int2 = room.def.getX(); int2 <= room.def.getX2(); ++int2) {
				for (int int3 = room.def.getY(); int3 <= room.def.getY2(); ++int3) {
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int2, int3, room.def.level);
					if (square != null) {
						square.setHourSeenToCurrent();
					}
				}
			}
		}
	}

	public boolean isAllExplored() {
		for (int int1 = 0; int1 < this.Rooms.size(); ++int1) {
			IsoRoom room = (IsoRoom)this.Rooms.get(int1);
			if (!room.def.bExplored) {
				return false;
			}
		}

		return true;
	}

	public void addWindow(IsoWindow window, boolean boolean1, IsoGridSquare square, IsoBuilding building) {
		this.Windows.add(window);
		IsoGridSquare square2 = null;
		if (boolean1) {
			square2 = window.square;
		} else {
			square2 = square;
		}

		if (square2 != null) {
			if (square2.getRoom() == null) {
				float float1 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
				float float2 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
				float float3 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
				byte byte1 = 7;
				IsoLightSource lightSource = new IsoLightSource(square2.getX(), square2.getY(), square2.getZ(), float1, float2, float3, byte1, building);
				this.lights.add(lightSource);
				IsoWorld.instance.CurrentCell.getLamppostPositions().add(lightSource);
			}
		}
	}

	public void addWindow(IsoWindow window, boolean boolean1) {
		this.addWindow(window, boolean1, window.square, (IsoBuilding)null);
	}

	public void addDoor(IsoDoor door, boolean boolean1, IsoGridSquare square, IsoBuilding building) {
		IsoGridSquare square2 = null;
		if (boolean1) {
			square2 = door.square;
		} else {
			square2 = square;
		}

		if (square2 != null) {
			if (square2.getRoom() == null) {
				float float1 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
				float float2 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
				float float3 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
				byte byte1 = 7;
				IsoLightSource lightSource = new IsoLightSource(square2.getX(), square2.getY(), square2.getZ(), float1, float2, float3, byte1, building);
				this.lights.add(lightSource);
				IsoWorld.instance.CurrentCell.getLamppostPositions().add(lightSource);
			}
		}
	}

	public void addDoor(IsoDoor door, boolean boolean1) {
		this.addDoor(door, boolean1, door.square, (IsoBuilding)null);
	}

	public boolean isResidential() {
		return this.containsRoom("bedroom");
	}

	public boolean containsRoom(String string) {
		for (int int1 = 0; int1 < this.Rooms.size(); ++int1) {
			if (string.equals(((IsoRoom)this.Rooms.get(int1)).getName())) {
				return true;
			}
		}

		return false;
	}

	public IsoRoom getRandomRoom(String string) {
		tempo.clear();
		for (int int1 = 0; int1 < this.Rooms.size(); ++int1) {
			if (string.equals(((IsoRoom)this.Rooms.get(int1)).getName())) {
				tempo.add((IsoRoom)this.Rooms.get(int1));
			}
		}

		if (tempo.isEmpty()) {
			return null;
		} else {
			return (IsoRoom)tempo.get(Rand.Next(tempo.size()));
		}
	}

	public ItemContainer getRandomContainer(String string) {
		RandomContainerChoices.clear();
		String[] stringArray = null;
		if (string != null) {
			stringArray = string.split(",");
		}

		int int1;
		if (stringArray != null) {
			for (int1 = 0; int1 < stringArray.length; ++int1) {
				RandomContainerChoices.add(stringArray[int1]);
			}
		}

		tempContainer.clear();
		for (int1 = 0; int1 < this.Rooms.size(); ++int1) {
			IsoRoom room = (IsoRoom)this.Rooms.get(int1);
			for (int int2 = 0; int2 < room.Containers.size(); ++int2) {
				ItemContainer itemContainer = (ItemContainer)room.Containers.get(int2);
				if (string == null || RandomContainerChoices.contains(itemContainer.getType())) {
					tempContainer.add(itemContainer);
				}
			}
		}

		if (tempContainer.isEmpty()) {
			return null;
		} else {
			return (ItemContainer)tempContainer.get(Rand.Next(tempContainer.size()));
		}
	}

	public IsoWindow getRandomFirstFloorWindow() {
		windowchoices.clear();
		windowchoices.addAll(this.Windows);
		for (int int1 = 0; int1 < windowchoices.size(); ++int1) {
			if (((IsoWindow)windowchoices.get(int1)).getZ() > 0.0F) {
				windowchoices.remove(int1);
			}
		}

		if (!windowchoices.isEmpty()) {
			return (IsoWindow)windowchoices.get(Rand.Next(windowchoices.size()));
		} else {
			return null;
		}
	}

	public boolean isToxic() {
		return this.isToxic;
	}

	public void setToxic(boolean boolean1) {
		this.isToxic = boolean1;
	}

	public void forceAwake() {
		for (int int1 = this.def.getX(); int1 <= this.def.getX2(); ++int1) {
			for (int int2 = this.def.getY(); int2 <= this.def.getY2(); ++int2) {
				for (int int3 = 0; int3 <= 4; ++int3) {
					IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, int3);
					if (square != null) {
						for (int int4 = 0; int4 < square.getMovingObjects().size(); ++int4) {
							if (square.getMovingObjects().get(int4) instanceof IsoGameCharacter) {
								((IsoGameCharacter)square.getMovingObjects().get(int4)).forceAwake();
							}
						}
					}
				}
			}
		}
	}
}
