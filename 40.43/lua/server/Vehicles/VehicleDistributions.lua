VehicleDistributions = VehicleDistributions or {};

VehicleDistributions.GloveBox = {
	rolls = 1,
	items = {
		"WaterBottleEmpty", 3,
		"Plasticbag", 3,
		"Pen", 4,
		"Tissue", 5,
		"Lighter", 5,
		"Matches", 5,
		"Cigarettes", 5,
		"Notebook", 5,
		"Torch", 3,
		"Pills", 3,
		"PillsBeta", 3,
		"PillsAntiDep", 3,
		"Bandaid", 3,
		"Bricktoys", 4,
		"Toothbrush", 4,
		"Cube", 4,
		"Dice", 4,
		"Yoyo", 4,
		"Button", 5,
		"Headphones", 3,
		"Paperclip", 4,
		"DigitalWatch2", 3,
		"Base.MuldraughMap", 1.5,
		"Base.WestpointMap", 1.5,
		"Base.MarchRidgeMap",1.5,
		"Base.RosewoodMap",1.5,
		"Base.RiversideMap",1.5,
		"Bullets9mm", 2,
		"DuctTape", 3,
		"Twine", 3,
		"Needle", 3,
		"Magazine", 3,
		"Mirror", 4,
		"Disc", 4,
		"WalkieTalkie1", 3,
		"ToyCar", 4,
		"Ring", 4,
		"Crayons", 4,
		"LightBulb", 3,
		"CatToy", 3,
		"TennisBall", 3,
		"DogChew", 2,
		"Hammer", 1,
		"Screwdriver", 2,
		"Lipstick", 2,
		"Comb", 2,
		"Wallet", 2,
		"Wallet2", 2,
		"Wallet3", 2,
		"Wallet4", 2,
		"ToiletPaper", 1,
		"Pistol", 0.1,
		"Cockroach", 0.5,
	}
}

VehicleDistributions.TruckBed = {
	rolls = 2,
	items = {
		"Plasticbag", 3,
		"Tote", 3,
		"EmptyPetrolCan", 1.5,
		"Umbrella", 3,
		"Football", 2,
		"PetrolCan", 1,
		"Spanner", 2.5,
		"LugWrench",2.5,
		"Jack", 2.5,
		"TirePump", 2.5,
		"OldTire1", 1,
		"OldTire2", 1,
		"OldTire3", 1,
		"CorpseMale", 0.03,
		"CorpseFemale", 0.03,
		"Extinguisher", 2,
		"Vest", 2,
		"Shirt", 2,
		"Blouse", 2,
		"Trousers", 2,
		"Skirt", 2,
		"Shoes", 2,
		"Schoolbag", 2,
		"Purse", 3,
		"CarBatteryCharger", 1,
	},
}

VehicleDistributions.DriverSeat = {
	rolls = 1,
	items = {
		"CorpseMale", 0.01,
		"CorpseFemale", 0.01,
	},
}

VehicleDistributions.Seat = {
	rolls = 1,
	items = {
		"WaterBottleEmpty", 1,
		"Plasticbag", 1,
		"Pen", 1,
		"Tissue", 1,
		"Lighter", 1,
		"Matches", 1,
		"Cigarettes", 1,
		"Notebook", 1,
		"Bricktoys", 1,
		"Toothbrush", 1,
		"Base.MuldraughMap", 0.3,
		"Base.WestpointMap", 0.3,
		"Base.MarchRidgeMap",0.3,
		"Base.RosewoodMap",0.3,
		"Base.RiversideMap",0.3,
		"Bullets9mm", 0.3,
		"Magazine", 1,
		"Disc", 1,
		"WalkieTalkie1", 0.5,
		"ToyCar", 1,
		"Ring", 1,
		"Crayons", 1,
		"CatToy", 1,
		"DogChew", 1,
		"Hammer", 0.3,
		"Screwdriver", 0.5,
		"Lipstick", 1,
		"Comb", 1,
		"Wallet", 1,
		"Wallet2", 1,
		"Pistol", 0.1,
		"CorpseMale", 0.02,
		"CorpseFemale", 0.02,
	}
}

VehicleDistributions.Normal = {
	TruckBed = VehicleDistributions.TruckBed;
	
	TruckBedOpen = VehicleDistributions.TruckBed;
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.SurvivalistTruckBed = {
	rolls = 3,
	items = {
		"Crisps",5,
		"Crisps2", 5,
		"Crisps3", 5,
		"Crisps4", 5,
		"Cereal", 5,
		"Dogfood", 5,
		"TVDinner",5,
		"TinnedSoup", 5,
		"TinnedBeans", 5,
		"CannedCornedBeef", 5,
		"Macandcheese", 5,
		"CannedChili", 5,
		"CannedBolognese", 5,
		"CannedCarrots2", 5,
		"CannedCorn", 5,
		"CannedMushroomSoup", 5,
		"CannedPeas", 5,
		"CannedPotato2", 5,
		"CannedSardines", 5,
		"CannedTomato2", 5,
		"ShotgunShellsBox", 3,
		"ShotgunShellsBox", 3,
		"Shotgun", 0.8,
		"Pillow", 0.8,
		"SmokeBomb", 0.5,
		"FlameTrap", 0.5,
		"Aerosolbomb", 0.5,
		"FireWoodKit", 0.9,
	}
}

VehicleDistributions.Survivalist = {
	TruckBed = VehicleDistributions.SurvivalistTruckBed;
	
	TruckBedOpen = VehicleDistributions.SurvivalistTruckBed;
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.FishermanTruckBed = {
	rolls = 3,
	items = {
		"BaitFish", 2,
		"BaitFish", 2,
		"Pike", 1,
		"Trout", 1,
		"Panfish", 1,
		"Crappie", 1,
		"Perch", 1,
		"Bass", 1,
		"Catfish", 1,
		"FishingTackle", 10,
		"FishingTackle", 10,
		"FishingTackle", 10,
		"FishingTackle2", 10,
		"FishingTackle2", 10,
		"FishingTackle2", 10,
		"FishingLine", 10,
		"FishingLine", 10,
		"FishingRod", 8,
		"FishingRod", 8,
		"FishingNet", 8,
		"FishingNet", 8,
	}
}

VehicleDistributions.Fisherman = {
	TruckBed = VehicleDistributions.FishermanTruckBed;
	
	TruckBedOpen = VehicleDistributions.FishermanTruckBed;
	
	GloveBox = {
		rolls = 2,
		items = {
			"Base.MuldraughMap", 5,
			"Base.WestpointMap", 5,
			"Base.MarchRidgeMap",5,
			"Base.RosewoodMap",5,
			"Base.RiversideMap",5,
		}
	},
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.GroceriesTruckBed = {
	rolls = 4,
	items = {
		"Apple", 3,
		"Orange", 3,
		"Banana", 3,
		"farming.RedRadish", 3,
		"farming.Strewberrie", 3,
		"Cherry", 3,
		"farming.Tomato", 3,
		"farming.Cabbage", 3,
		"Lettuce", 3,
		"Pickles", 3,
		"BellPepper", 3,
		"CannedChili", 2,
		"CannedBolognese", 2,
		"CannedCarrots2", 2,
		"CannedCorn", 2,
		"CannedMushroomSoup", 2,
		"CannedPeas", 2,
		"CannedPotato2", 2,
		"CannedSardines", 2,
		"CannedTomato2", 2,
	}
}

VehicleDistributions.Groceries = {
	TruckBed = VehicleDistributions.GroceriesTruckBed;
	
	TruckBedOpen = VehicleDistributions.GroceriesTruckBed;
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.GolfTruckBed = {
	rolls = 2,
	items = {
		"Golfclub", 10,
		"Golfclub", 3,
		"GolfBall", 10,
		"GolfBall", 10,
		"GolfBall", 10,
		"GolfBall", 10,
		"NormalHikingBag", 3,
	}
}

VehicleDistributions.Golf = {
	TruckBed = VehicleDistributions.GolfTruckBed;
	
	TruckBedOpen = VehicleDistributions.GolfTruckBed;
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.ClothingTruckBed = {
	rolls = 3,
	items = {
		"Thread", 3,
		"Needle", 3,
		"Vest", 5,
		"Shirt", 5,
		"Blouse", 5,
		"Trousers", 5,
		"Skirt", 7,
		"Shoes", 7,
		"Socks", 5,
		"Underwear1", 5,
		"Underwear2", 5,
		"Belt", 5,
		"Schoolbag", 3,
		"Purse", 7,
	}
}

VehicleDistributions.Clothing = {
	TruckBed = VehicleDistributions.ClothingTruckBed;

	TruckBedOpen = VehicleDistributions.ClothingTruckBed;
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.CarpenterTruckBed = {
	rolls = 3,
	items = {
		"Hammer", 17,
		"NailsBox", 15,
		"NailsBox", 15,
		"Plank", 5,
		"Plank", 5,
		"Plank", 5,
		"Plank", 5,
		"Screwdriver", 5,
		"Saw", 10,
		"DuctTape", 2,
		"Glue", 2,
		"Twine", 2,
		"Woodglue", 10,
		"BookCarpentry1", 4,
		"BookCarpentry2", 3,
		"BookCarpentry3", 2,
		"BookCarpentry4", 1,
		"BookCarpentry5", 0.6,
		"Screwdriver", 1,
	}
}

VehicleDistributions.Carpenter = {
	TruckBed = VehicleDistributions.CarpenterTruckBed;
	
	TruckBedOpen = VehicleDistributions.CarpenterTruckBed;
	
	GloveBox = {
		rolls = 1,
		items = {
			"Wallet", 0.2,
			"DuctTape", 5,
			"Glue", 5,
			"Twine", 5,
			"Woodglue", 10,
			"BookCarpentry1", 5,
			"BookCarpentry2", 4,
			"BookCarpentry3", 3,
			"BookCarpentry4", 1.5,
			"BookCarpentry5", 1,
		}
	},
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.ElectricianTruckBed = {
	rolls = 4,
	items = {
		"MetalPipe", 5,
		"ElectronicsScrap", 10,
		"ElectronicsScrap", 10,
		"ElectronicsScrap", 10,
		"ScrapMetal", 4,
		"Twine", 10,
		"Amplifier", 7,
		"Sparklers", 7,
		"Aluminum", 7,
		"TriggerCrafted", 7,
		"MotionSensor", 7,
		"DuctTape", 3,
		"TimerCrafted", 7,
		"RemoteCraftedV1", 7,
		"RemoteCraftedV2", 5,
		"RemoteCraftedV3", 3,
		"Screwdriver", 5,
		"ElectronicsMag1", 3,
		"ElectronicsMag2", 3,
		"ElectronicsMag3", 3,
		"ElectronicsMag4", 1.5,
		"ElectronicsMag5", 3,
		"EngineerMagazine1", 2,
		"EngineerMagazine2", 2,
		"Radio.RadioMag1", 3,
		"Radio.RadioMag2", 2,
		"Radio.RadioMag3", 1,
		"Radio.ElectricWire", 7,
		"BookElectrician1",0.9,
		"BookElectrician2",0.5,
	}
}

VehicleDistributions.Electrician = {
	TruckBed = VehicleDistributions.ElectricianTruckBed;
	
	TruckBedOpen = VehicleDistributions.ElectricianTruckBed;
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.FarmerTruckBed = {
	rolls = 4,
	items = {
		"farming.CarrotBagSeed", 8,
		"farming.BroccoliBagSeed", 8,
		"farming.RedRadishBagSeed", 8,
		"farming.StrewberrieBagSeed", 8,
		"farming.TomatoBagSeed", 8,
		"farming.PotatoBagSeed", 8,
		"farming.CabbageBagSeed", 8,
		"farming.HandShovel", 6,
		"farming.Shovel", 6,
		"farming.WateredCan", 6,
		"BookFarming1", 4,
		"BookForaging1", 2,
		"BookForaging2", 1,
		"BookForaging3", 0.7,
		"BookForaging4", 0.5,
		"BookForaging5", 0.3,
		"FarmingMag1", 4,
		"BookFarming2", 3,
		"BookFarming3", 2,
		"BookFarming4", 1,
		"BookFarming5", 0.6,
		"CompostBag", 1,
		"Fertilizer", 1,
	}
}

VehicleDistributions.Farmer = {
	TruckBed = VehicleDistributions.FarmerTruckBed;
	
	TruckBedOpen = VehicleDistributions.FarmerTruckBed;
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.MetalWelderTruckBed = {
	rolls = 4,
	items = {
		"ScrapMetal", 2,
		"ScrapMetal", 2,
		"ScrapMetal", 2,
		"BlowTorch", 2,
		"WeldingRods", 3,
		"WeldingRods", 3,
		"SheetMetal", 2,
		"SheetMetal", 2,
		"SmallSheetMetal", 2,
		"SmallSheetMetal", 2,
		"SmallSheetMetal", 2,
		"SmallSheetMetal", 2,
		"MetalPipe", 2,
		"MetalPipe", 2,
		"MetalPipe", 2,
		"MetalBar", 2,
		"MetalBar", 2,
		"WeldingMask", 3,
		"WeldingMask", 3,
		"BookMetalWelding1", 1,
		"BookMetalWelding2", 0.5,
		"BookMetalWelding3", 0.4,
		"BookMetalWelding4", 0.3,
		"BookMetalWelding5", 0.1,
	}
}

VehicleDistributions.MetalWelder = {
	TruckBed = VehicleDistributions.MetalWelderTruckBed;
	
	TruckBedOpen = VehicleDistributions.MetalWelderTruckBed;
	
	GloveBox = {
		rolls = 1,
		items = {
			"Wallet", 1,
			"BookMetalWelding1", 5,
			"BookMetalWelding2", 4,
			"BookMetalWelding3", 3,
			"BookMetalWelding4", 1.5,
			"BookMetalWelding5", 1,
			"MetalworkMag1", 2,
			"MetalworkMag2", 2,
			"MetalworkMag3", 2,
			"MetalworkMag4", 2,
		}
	},
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.DoctorTruckBed = {
	rolls = 4,
	items = {
		"Pills", 8,
		"PillsBeta", 8,
		"PillsAntiDep", 8,
		"PillsSleepingTablets", 8,
		"PillsVitamins", 8,
		"Bandage", 10,
		"Bandage", 10,
		"Bandaid", 10,
		"Bandaid", 10,
		"Bandage", 10,
		"Bandage", 10,
		"Bandaid", 10,
		"Bandaid", 10,
		"FirstAidKit", 5,
		"Tweezers", 8,
		"Disinfectant", 8,
		"AlcoholWipes", 8,
		"SutureNeedle", 8,
	}
}

VehicleDistributions.Doctor = {
	TruckBed = VehicleDistributions.DoctorTruckBed;
	
	TruckBedOpen = VehicleDistributions.DoctorTruckBed;
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.Radio = {
	TruckBed =
	{
		rolls = 2,
		items = {
			"Radio.WalkieTalkie1",2,
			"Radio.WalkieTalkie2",2,
			"Radio.WalkieTalkie3",2,
			"Radio.WalkieTalkie4",2,
			"Radio.WalkieTalkie5",2,
			"Radio.RadioBlack",2,
			"Radio.RadioRed",2,
			"Radio.HamRadio1",2,
			"ElectronicsScrap", 5,
			"ElectronicsScrap", 5,
			"Amplifier", 3,
			"Amplifier", 3,
		}
	},
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.PainterTruckBed = {
	rolls = 4,
	items = {
		"PaintbucketEmpty", 3,
		"Paintbrush", 5,
		"PaintRed", 2,
		"PaintBlack", 2,
		"PaintBlue", 2,
		"PaintBrown", 2,
		"PaintCyan", 2,
		"PaintGreen", 2,
		"PaintGrey", 2,
		"PaintLightBlue", 2,
		"PaintLightBrown", 2,
		"PaintOrange", 2,
		"PaintPink", 2,
		"PaintPurple", 2,
		"PaintTurquoise", 2,
		"PaintWhite", 2,
		"PaintYellow", 2,
	}
}

VehicleDistributions.Painter = {
	TruckBed = VehicleDistributions.PainterTruckBed;
	
	TruckBedOpen = VehicleDistributions.PainterTruckBed;
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.ConstructionWorkerTruckBed = {
	rolls = 5,
	items = {
		"Wire", 1,
		"BarbedWire", 1,
		"Gravelbag", 1,
		"BucketEmpty", 1,
		"Hammer", 2,
		"NailsBox", 5,
		"NailsBox", 5,
		"Plank", 5,
		"Screwdriver", 5,
		"Saw", 10,
		"DuctTape", 2,
		"Glue", 2,
		"Twine", 2,
		"Woodglue", 2,
		"ScrapMetal", 2,
		"BlowTorch", 2,
		"WeldingRods", 3,
		"WeldingRods", 3,
		"SheetMetal", 2,
		"SmallSheetMetal", 2,
		"SmallSheetMetal", 2,
		"MetalPipe", 1,
		"MetalBar", 1,
		"WeldingMask", 2,
		"EmptySandbag", 2,
		"PlasterPowder", 1,
		"Screwdriver", 1,
	}
}

VehicleDistributions.ConstructionWorker = {
	TruckBed = VehicleDistributions.ConstructionWorkerTruckBed;
	
	TruckBedOpen = VehicleDistributions.ConstructionWorkerTruckBed;
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.Taxi = {
	TruckBed =
	{
		rolls = 3,
		items = {
			"Vest", 3,
			"Shirt", 3,
			"Blouse", 3,
			"Trousers", 3,
			"Skirt", 3,
			"Shoes", 3,
			"Socks", 1,
			"Underwear1", 1,
			"Underwear2", 1,
			"Belt", 1,
			"Purse", 3,
			"Schoolbag", 3,
			"NormalHikingBag", 2,
			"BigHikingBag", 1.5,
		}
	},
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.Police = {
	TruckBed =
	{
		rolls = 4,
		items = {
			"CorpseMale", 0.2,
			"CorpseFemale", 0.2,
			"Shotgun", 2,
			"Pistol", 2,
			"BulletsBox", 4,
			"Gunpowder", 3,
			"ShotgunShellsBox", 4,
			"223Box", 3,
			"308Box", 3,
			"BulletsBox", 2,
			"ShotgunShellsBox", 2,
			"223Box", 2,
			"308Box", 2,
			"NormalHikingBag", 1,
			"HuntingKnife", 3,
			"Radio.WalkieTalkie4",10,
			"Radio.WalkieTalkie5",1,
			"Radio.HamRadio1",5,
			"Radio.HamRadio2",1,
			"x2Scope", 0.7,
			"x4Scope", 0.5,
			"x8Scope", 0.3,
			"AmmoStraps", 0.7,
			"Sling", 0.7,
			"RecoilPad",  0.7,
		}
	},
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.RangerTruckBed = {
	rolls = 4,
	items = {
		"Shotgun", 1,
		"ShotgunShellsBox", 3,
		"Radio.WalkieTalkie4",10,
		"Radio.WalkieTalkie5",1,
		"farming.HandShovel", 6,
		"farming.Shovel", 6,
		"farming.WateredCan", 6,
		"DeadRabbit", 1,
		"DeadSquirrel", 1,
		"DeadBird", 1,
		"CompostBag", 1,
		"TrapSnare", 1,
		"TrapCage", 1,
		"TrapBox", 1,
		"TrapCrate", 1,
		"TrapStick", 1,
	}
}

VehicleDistributions.Ranger = {
	TruckBed = VehicleDistributions.RangerTruckBed;
	
	TruckBedOpen = VehicleDistributions.RangerTruckBed;
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.FireTruckBed = {
	rolls = 4,
	items = {
		"Axe", 3,
		"Radio.WalkieTalkie4",10,
		"Radio.WalkieTalkie5",1,
		"farming.WateredCan", 6,
		"Bandage", 5,
		"Bandage", 5,
		"FirstAidKit", 3,
		"FirstAidKit", 3,
		"Bandaid", 5,
		"Bandaid", 5,
		"Tweezers", 8,
		"Disinfectant", 8,
		"AlcoholWipes", 8,
		"SutureNeedle", 8,
	}
}

VehicleDistributions.Fire = {
	TruckBed = VehicleDistributions.FireTruckBed;
	
	TruckBedOpen = VehicleDistributions.FireTruckBed;
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.McCoyTruckBed = {
	rolls = 5,
	items = {
		"Log", 3,
		"Log", 3,
		"Log", 3,
		"Log", 3,
		"Log", 3,
		"Saw", 2,
	}
}

VehicleDistributions.McCoy = {
	TruckBed = VehicleDistributions.McCoyTruckBed;
	
	TruckBedOpen = VehicleDistributions.McCoyTruckBed;
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.FossoilTruckBed = {
	rolls = 5,
	items = {
		"EmptyPetrolCan", 3,
		"EmptyPetrolCan", 3,
		"EmptyPetrolCan", 3,
		"EmptyPetrolCan", 3,
		"PetrolCan", 2,
		"PetrolCan", 2,
		"PetrolCan", 2,
		"PetrolCan", 2,
	}
}

VehicleDistributions.Fossoil = {
	TruckBed = VehicleDistributions.FossoilTruckBed;
	
	TruckBedOpen = VehicleDistributions.FossoilTruckBed;
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.Postal = {
	TruckBed =
	{
		rolls = 4,
		items = {
			"Book", 10,
			"Newspaper", 30,
			"Newspaper", 20,
			"Newspaper", 20,
			"Newspaper", 20,
			"Magazine", 30,
			"Magazine", 20,
			"Magazine", 20,
			"Magazine", 20,
			"Journal", 20,
			"Journal", 20,
			"Journal", 20,
			"ComicBook", 10,
			"BookCarpentry1", 1,
			"BookFarming1", 1,
			"BookForaging1", 1,
			"FarmingMag1", 1,
			"BookCooking1", 1,
			"BookFishing1", 1,
			"BookTrapping1", 1,
			"BookCarpentry2", 0.5,
			"BookFarming2", 0.5,
			"BookForaging2", 0.5,
			"BookCooking2", 0.5,
			"BookFishing2", 0.5,
			"BookTrapping2", 0.5,
			"BookCarpentry3", 0.3,
			"BookFarming3", 0.3,
			"BookForaging3", 0.3,
			"BookCooking3", 0.3,
			"BookFishing3", 0.3,
			"BookTrapping3", 0.3,
			"BookFirstAid1", 1,
			"BookFirstAid2", 0.5,
			"BookFirstAid3", 0.3,
			"BookMetalWelding1", 1,
			"BookMetalWelding2", 0.5,
			"BookMetalWelding3", 0.3,
			"BookElectrician1", 1,
			"BookElectrician2", 0.5,
			"BookElectrician3", 0.3,
		}
	},
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

VehicleDistributions.Spiffo = {
	TruckBed =
	{
		rolls = 3,
		items = {
			"Spiffo", 0.0005,
			"Bread", 7,
			"Steak", 4,
			"Burger", 4,
			"Burger", 4,
			"Burger", 4,
			"Burger", 4,
			"Burger", 4,
			"Burger", 4,
			"Fries", 4,
			"Fries", 4,
			"Chicken", 3,
			"Ham", 3,
			"Cheese", 4,
			"Cheese", 4,
			"Pop", 4,
			"Pop2", 4,
			"Pop3", 4,
			"PopBottle", 3,
			"farming.Tomato", 4,
			"Lettuce", 3,
			"Mustard", 3,
			"Ketchup", 3,
			"Processedcheese", 3,
			"Processedcheese", 3,
			"farming.Cabbage", 4,
			"farming.Bacon", 4,
			"farming.Bacon", 4,
			"Worm", 2,
		}
	},
	
	GloveBox = VehicleDistributions.GloveBox;
	
	SeatRearLeft = VehicleDistributions.Seat;
	SeatRearRight = VehicleDistributions.Seat;
}

local distributionTable = {
	
	-- Classic cars
	SportsCar = {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Doctor, VehicleDistributions.Groceries, VehicleDistributions.Golf, VehicleDistributions.Clothing },
	},
	
	ModernCar = {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Doctor, VehicleDistributions.Groceries, VehicleDistributions.Golf, VehicleDistributions.Clothing },
	},
	
	ModernCar02 = {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Doctor, VehicleDistributions.Groceries, VehicleDistributions.Golf, VehicleDistributions.Clothing },
	},
	
	CarLuxury = {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Doctor, VehicleDistributions.Groceries, VehicleDistributions.Golf, VehicleDistributions.Clothing },
	},
		
	CarNormal =  {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Groceries, VehicleDistributions.Carpenter, VehicleDistributions.Farmer, VehicleDistributions.Electrician, VehicleDistributions.Survivalist, VehicleDistributions.Clothing, VehicleDistributions.ConstructionWorker, VehicleDistributions.Painter },
	},
		
	CarLights =  {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Groceries, VehicleDistributions.Fisherman, VehicleDistributions.Carpenter, VehicleDistributions.Electrician, VehicleDistributions.Clothing, VehicleDistributions.ConstructionWorker, VehicleDistributions.Painter },
	},
		
	SmallCar =  {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Groceries, VehicleDistributions.Fisherman, VehicleDistributions.Electrician, VehicleDistributions.Clothing },
	},
	
	SmallCar02 =  {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Groceries, VehicleDistributions.Fisherman, VehicleDistributions.Electrician, VehicleDistributions.Clothing },
	},
		
	CarStationWagon =  {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Groceries, VehicleDistributions.Fisherman, VehicleDistributions.Carpenter, VehicleDistributions.Farmer, VehicleDistributions.Electrician, VehicleDistributions.MetalWelder, VehicleDistributions.Survivalist, VehicleDistributions.Clothing, VehicleDistributions.ConstructionWorker, VehicleDistributions.Painter },
	},
	
	CarStationWagon2 =  {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Groceries, VehicleDistributions.Fisherman, VehicleDistributions.Carpenter, VehicleDistributions.Farmer, VehicleDistributions.Electrician, VehicleDistributions.MetalWelder, VehicleDistributions.Survivalist, VehicleDistributions.Clothing, VehicleDistributions.ConstructionWorker, VehicleDistributions.Painter },
	},
		
	Van =  {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Groceries, VehicleDistributions.Fisherman, VehicleDistributions.Carpenter, VehicleDistributions.Farmer, VehicleDistributions.Electrician, VehicleDistributions.MetalWelder, VehicleDistributions.ConstructionWorker, VehicleDistributions.Painter },
	},
		
	StepVan = {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Groceries, VehicleDistributions.Fisherman, VehicleDistributions.Carpenter, VehicleDistributions.Farmer, VehicleDistributions.Electrician, VehicleDistributions.MetalWelder, VehicleDistributions.ConstructionWorker, VehicleDistributions.Painter },
	},
		
	VanSeats = {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Groceries, VehicleDistributions.Fisherman, VehicleDistributions.Golf, VehicleDistributions.Clothing },
	},
	
	OffRoad = {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Fisherman, VehicleDistributions.Carpenter, VehicleDistributions.Farmer, VehicleDistributions.Electrician, VehicleDistributions.MetalWelder, VehicleDistributions.Survivalist, VehicleDistributions.ConstructionWorker, VehicleDistributions.Painter },
	},
	
	SUV = {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Fisherman, VehicleDistributions.Carpenter, VehicleDistributions.Farmer, VehicleDistributions.Electrician, VehicleDistributions.MetalWelder, VehicleDistributions.Survivalist, VehicleDistributions.ConstructionWorker, VehicleDistributions.Painter },
	},
	
	PickUpVan = {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Fisherman, VehicleDistributions.Carpenter, VehicleDistributions.Farmer, VehicleDistributions.Electrician, VehicleDistributions.MetalWelder, VehicleDistributions.Survivalist, VehicleDistributions.ConstructionWorker, VehicleDistributions.Painter },
	},
		
	PickUpVanLights = {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Fisherman, VehicleDistributions.Carpenter, VehicleDistributions.Farmer, VehicleDistributions.Electrician, VehicleDistributions.MetalWelder, VehicleDistributions.Survivalist, VehicleDistributions.ConstructionWorker, VehicleDistributions.Painter },
	},
		
	PickUpTruck = {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Fisherman, VehicleDistributions.Carpenter, VehicleDistributions.Farmer, VehicleDistributions.Electrician, VehicleDistributions.MetalWelder, VehicleDistributions.Survivalist, VehicleDistributions.ConstructionWorker, VehicleDistributions.Painter },
	},
		
	PickUpTruckLights = {
		Normal = VehicleDistributions.Normal,
		Specific = { VehicleDistributions.Fisherman, VehicleDistributions.Carpenter, VehicleDistributions.Farmer, VehicleDistributions.Electrician, VehicleDistributions.MetalWelder, VehicleDistributions.Survivalist, VehicleDistributions.ConstructionWorker, VehicleDistributions.Painter },
	},
		
	-- Specific cars like police, fire, ranger... We simply add their skin index to the loot table's name if they have one.
		
	-- Taxi
	CarTaxi = { Normal = VehicleDistributions.Taxi; },
	CarTaxi2 = { Normal = VehicleDistributions.Taxi; },
	
	-- Police
	PickUpVanLightsPolice = { Normal = VehicleDistributions.Police; },
	CarLightsPolice = { Normal = VehicleDistributions.Police; },
		
	-- Fire dept
	PickUpTruckLightsFire = { Normal = VehicleDistributions.Fire; },
	PickUpVanLightsFire = { Normal = VehicleDistributions.Fire; },
		
	-- Ranger
	PickUpVanLights0 = { Normal = VehicleDistributions.Ranger; },
	PickUpTruckLights0 = { Normal = VehicleDistributions.Ranger; },
	CarLights0 = { Normal = VehicleDistributions.Ranger; },
	
	-- McCoy
	PickUpVanMccoy = { Normal = VehicleDistributions.McCoy; },
	PickUpTruckMccoy = { Normal = VehicleDistributions.McCoy; },
	VanSpecial1 = { Normal = VehicleDistributions.McCoy; },
	
	-- Fossoil
	PickUpVanLights1 = { Normal = VehicleDistributions.Fossoil; },
	PickUpTruckLights1 = { Normal = VehicleDistributions.Fossoil; },
	VanSpecial0 = { Normal = VehicleDistributions.Fossoil; },
	
	-- Postal
	StepVanMail = { Normal = VehicleDistributions.Postal; },
	VanSpecial2 = { Normal = VehicleDistributions.Postal; },
	
	-- Ambulance
	VanAmbulance = { Normal = VehicleDistributions.Doctor; },
		
	-- Radio
	VanRadio = { Normal = VehicleDistributions.Radio; },
	
	-- Spiffo
	VanSpiffo = { Normal = VehicleDistributions.Spiffo; },
}

table.insert(VehicleDistributions, 1, distributionTable);
