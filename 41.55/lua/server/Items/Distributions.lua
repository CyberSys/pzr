Distributions = Distributions or {};

local distributionTable = {

-- =====================
--    Room List (A-Z)   
-- =====================

    aesthetic = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="SalonCounter", min=0, max=99},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="SalonShelfTowels", min=0, max=99, weightChance=50},
                {name="SalonShelfHaircare", min=0, max=99, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="SalonShelfHaircare", min=0, max=99, weightChance=100},
                {name="SalonShelfTowels", min=0, max=99, weightChance=10},
            }
        }
    },
    
    aestheticstorage = {
        isShop = true,
        metal_shelves = {
            procedural = true,
            procList = {
                {name="SalonShelfHaircare", min=0, max=99},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="SalonShelfHaircare", min=0, max=99},
            }
        }
    },
    
    all = {
        bin = {
            rolls = 1,
            items = {
                "Cockroach", 6,
                "Cockroach", 6,
                "Cockroach", 4,
                "Cockroach", 4,
                "DeadRat", 4,
                "DeadMouse", 2,
                "TinCanEmpty", 4,
                "TinCanEmpty", 4,
                "TinCanEmpty", 4,
                "TinCanEmpty", 4,
                "PopEmpty", 4,
                "PopEmpty", 4,
                "PopEmpty", 4,
                "PopEmpty", 4,
                "WhiskeyEmpty", 1,
                "BeerEmpty", 1,
                "WineEmpty", 1,
                "WineEmpty2", 1,
                "BandageDirty", 2,
                "BandageDirty", 2,
                "ElectronicsScrap", 2,
                "ScrapMetal", 2,
                "PopBottleEmpty", 2,
                "PopBottleEmpty", 2,
                "WaterBottleEmpty", 2,
                "WaterBottleEmpty", 2,
                "SmashedBottle", 1,
                "SmashedBottle", 1,
                "Garbagebag", 100,
            }
        },
        campfire = {
            rolls = 0,
            items = {
                
            }
        },
        cashregister = {
            rolls = 4,
            items = {
                "Money", 2,
                "Money", 100,
                "Money", 100,
            }
        },
        clothingdryer = {
            rolls = 0,
            items = {
                
            }
        },
        clothingrack = {
            procedural = true,
            procList = {
                {name="ClothingStoresDress", min=0, max=1, weightChance=100},
                {name="ClothingStoresWoman", min=0, max=10, weightChance=100},
                {name="ClothingStoresShirts", min=0, max=3, weightChance=100},
                {name="ClothingStoresPants", min=0, max=2, weightChance=100},
                {name="ClothingStoresJumpers", min=0, max=2, weightChance=100},
                {name="ClothingStoresJackets", min=0, max=1, weightChance=100},
                {name="ClothingPoor", min=0, max=99, forceForZones="Poor"},
            }
        },
        clothingwasher = {
            rolls = 0,
            items = {
                
            }
        },
        corn = {
            rolls = 2,
            items = {
                "Corn", 1,
                "Corn", 1,
                "Corn", 1,
            }
        },
        counter = {
            rolls = 1,
            items = {
                "Battery", 2,
                "Lighter", 2,
                "Torch", 2,
                "HandTorch", 3,
                "HuntingKnife", 0.5,
                "DeadRat", 0.5,
                "DeadMouse", 0.5,
                "farming.CarrotBagSeed", 1,
                "farming.BroccoliBagSeed", 1,
                "farming.RedRadishBagSeed", 1,
                "farming.StrewberrieBagSeed", 1,
                "farming.TomatoBagSeed", 1,
                "farming.PotatoBagSeed", 1,
                "farming.CabbageBagSeed", 1,
                "Pistol", 0.3,
                "Revolver_Short", 0.3,
                "DoubleBarrelShotgun", 0.5,
                "ShotgunShells", 0.6,
                "BaseballBat", 0.3,
                "Bullets9mm", 0.6,
                "Bullets38", 0.6,
                "Radio.RadioBlack",2,
                "Radio.RadioRed",1,
                "Radio.WalkieTalkie1",0.05,
                "Radio.WalkieTalkie2",0.03,
                "Radio.WalkieTalkie3",0.001,
            },
            junk = {
                rolls = 1,
                items = {
                    "Pen", 4,
                    "BluePen", 2,
                    "RedPen", 2,
                    "Pencil", 4,
                    "RubberBand", 2,
                    "Tissue", 4,
                    "Candle", 4,
                    "Matches", 4,
                    "Mugl", 2,
                    "MugRed", 2,
                    "MugWhite", 3,
                    "EmptyJar", 1,
                    "JarLid", 1,
                    "Vinegar", 1,
                }
            }
        },
        crate = {
            rolls = 1,
            items = {
                "NailsBox", 1,
                "PaperclipBox", 0.5,
                "DuctTape", 0.8,
                "Glue", 0.8,
                "Scotchtape", 0.8,
                "Twine", 0.8,
                "Thread", 1.5,
                "Needle", 0.8,
                "Woodglue", 0.8,
                "Rope", 0.8,
                "NailsBox", 4,
                "NailsBox", 4,
                "NailsBox", 4,
                "Hammer", 4,
                "Wire", 2,
                "Crowbar", 1,
                "Tarp", 1,
                "Saw", 1,
                "GardenSaw", 1,
                "Plank", 3,
                "Plank", 3,
                "Battery", 4,
                "Lighter", 4,
                "camping.TentPeg", 7,
                "Sledgehammer", 0.4,
                "Sledgehammer2", 0.4,
                "Paperclip", 0.8,
                "Axe", 0.4,
                "WoodAxe", 0.4,
                "farming.CarrotBagSeed", 1.5,
                "farming.BroccoliBagSeed", 1.5,
                "farming.RedRadishBagSeed", 1.5,
                "farming.StrewberrieBagSeed", 1.5,
                "farming.TomatoBagSeed", 1.5,
                "farming.PotatoBagSeed", 1.5,
                "farming.CabbageBagSeed", 1.5,
                "farming.HandShovel", 2.7,
                "HandScythe", 0.5,
                "HandFork", 0.5,
                "Shovel", 0.5,
                "Shovel2", 0.5,
                "SnowShovel", 0.5,
                "farming.WateredCan", 1,
                "Paintbrush", 1.5,
                "PaintBlue", 0.8,
                "PaintBlack", 0.8,
                "PaintRed", 0.8,
                "PaintBrown", 0.8,
                "PaintCyan", 0.8,
                "PaintGreen", 0.8,
                "PaintGrey", 0.8,
                "PaintLightBlue", 0.8,
                "PaintLightBrown", 0.8,
                "PaintOrange", 0.8,
                "PaintPink", 0.8,
                "PaintPurple", 0.8,
                "PaintTurquoise",0.8,
                "PaintWhite", 0.8,
                "PaintYellow", 0.8,
                "PlasterPowder", 2,
                "ConcretePowder", 1,
                "BucketEmpty", 2,
                "Shotgun", 0.5,
                "DoubleBarrelShotgun", 0.2,
                "ShotgunShells", 1,
                "ShotgunShells", 1,
                "ShotgunShells", 1,
                "ShotgunShells", 0.5,
                "ShotgunShells", 0.5,
                "Torch", 1,
                "HandTorch", 1,
                "BarbedWire", 1,
                "Sandbag", 0.5,
                "Gravelbag", 0.5,
                "EmptySandbag", 2.5,
                "Fertilizer", 0.5,
                "Charcoal", 6,
                --                "BallPeenHammer", 0.01,
                --                "Tongs", 0.01,
                "Radio.WalkieTalkie1",0.05,
                "Radio.WalkieTalkie2",0.03,
                "Radio.WalkieTalkie3",0.001,
                "Radio.HamRadio1",0.005,
                "BlowTorch", 0.8,
                "WeldingRods", 2,
                "SheetMetal", 2,
                "SmallSheetMetal", 2.4,
                "MetalPipe", 2,
                "MetalBar", 1.2,
                "WeldingMask",0.7,
                "Wrench", 0.5,
                "LugWrench",0.4,
                "Jack", 0.2,
                "TirePump", 0.2,
                "LeadPipe", 0.4,
                "HandAxe", 0.2,
                "PipeWrench", 0.4,
                "Plunger", 0.5,
                "ClubHammer", 0.3,
                "WoodenMallet", 0.3,
            }
        },
        desk = {
            procedural = true,
            procList = {
                {name="DeskGeneric", min=0, max=99},
            }
        },
        dishescabinet = {
            rolls = 6,
            items = {
                "ButterKnife", 5,
                "BreadKnife", 5,
                "Spoon", 10,
                "Fork", 10,
                "Bowl", 10,
            }
        },
        displaycasebakery = {
            procedural = true,
            procList = {
                {name="BakeryBread", min=0, max=99, weightChance=100},
                {name="BakeryPie", min=0, max=99, weightChance=60},
                {name="BakeryCake", min=0, max=99, weightChance=80},
                {name="BakeryMisc", min=0, max=99, weightChance=20},
            }
        },
        dresser = { 
            procedural = true,
            procList = {
                {name="DresserGeneric", min=0, max=99},
            }
        },
        filingcabinet = {
            rolls = 1,
            items = {
                "Magazine", 4,
                "Newspaper", 4,
                "Book", 4,
                "ComicBook", 2,
            },
            junk = {
                rolls = 1,
                items = {
                    "SheetPaper2", 20,
                    "SheetPaper2", 20,
                    "SheetPaper2", 20,
                    "SheetPaper2", 20,
                    "Notebook", 4,
                }
            }
        },
        freezer = {
            rolls = 3,
            items = {
                "Icecream", 3,
                "Icecream", 3,
                "Icecream", 3,
                "Pizza", 1,
                "BurgerRecipe", 1,
                "Peas", 3,
                "Pie", 1,
                "Steak", 1,
                "Chicken", 1,
                "Salmon", 1,
                "Coldpack",2,
                "Coldpack",2,
                "PorkChop", 1,
                "MuttonChop", 1,
                "IcePick", 0.2,
            }
        },
        fridge = {
            rolls = 1,
            items = {
                "Milk", 8,
                "Milk", 4,
                "BeefJerky", 3,
                "Bread", 4,
                "Carrots", 4,
                "Steak", 3,
                "MeatPatty", 3,
                "Chicken", 3,
                "Ham", 3,
                "Salmon", 3,
                "Cheese", 4,
                "Watermelon", 4,
                "Broccoli", 4,
                "Pie", 3,
                "PopBottle", 3,
                "PopBottle", 3,
                "Butter", 3,
                "EggCarton", 4,
                "EggCarton", 2,
                "Apple", 4,
                "Orange", 4,
                "Banana", 4,
                "farming.RedRadish", 4,
                "farming.Strewberrie", 4,
                "Cherry", 4,
                "farming.Tomato", 4,
                "farming.Cabbage", 4,
                "Lettuce", 3,
                "Pickles", 3,
                "BellPepper", 3,
                "Peach", 3,
                "CakeSlice", 3,
                "Mustard", 2,
                "Ketchup", 2,
                "Processedcheese", 5,
                "Corndog", 2,
                "PorkChop", 3,
                "MuttonChop", 3,
                "Onion", 3,
                "Lemon", 3,
                "WaterBottleFull", 3,
                "WaterBottleFull", 3,
                "WaterBottleFull", 3,
                "Wine", 2,
                "Corn", 4,
                "Eggplant", 4,
                "Leek", 4,
                "Grapes", 4,
                "farming.Bacon", 4,
                "farming.MayonnaiseFull", 2,
                "farming.RemouladeFull", 0.5,
                "Worm", 0.3,
                "Avocado", 3,
                "Pineapple", 3,
                "Zucchini", 3,
                "Tofu", 2,
                "Yoghurt", 3,
                "JuiceBox", 1,
                "BeerCan", 1,
                "BeerCan", 1,
                "BeerCan", 1,
                "BeerBottle", 1,
            }
        },
        fruitbusha = {
            rolls = 10,
            items = {
                "BerryBlack", 50,
            },
            noAutoAge = true,
        },
        fruitbushb = {
            rolls = 10,
            items = {
                "BerryBlue", 50,
            },
            noAutoAge = true,
        },
        fruitbushc = {
            rolls = 10,
            items = {
                "BerryGeneric1", 50,
            },
            noAutoAge = true,
        },
        fruitbushd = {
            rolls = 10,
            items = {
                "BerryGeneric2", 25,
                "BerryGeneric5", 25,
            },
            noAutoAge = true,
        },
        fruitbushe = {
            rolls = 10,
            items = {
                "BerryGeneric3", 25,
                "BerryGeneric4", 25,
            },
            noAutoAge = true,
        },
        inventoryfemale = {
            rolls = 1,
            items = {
                "MuldraughMap", 0.2,
                "WestpointMap", 0.2,
                "MarchRidgeMap",0.1,
                "RosewoodMap",0.1,
                "RiversideMap",0.1,
                "Lipstick", 1,
                "MakeupEyeshadow", 1,
                "MakeupFoundation", 1,
                "Earbuds", 1,
                "Locket", 1,
                "Comb", 1,
                "Magazine", 1,
                "Newspaper", 1,
                "Notebook", 1,
                "Pencil", 1,
                "Pen", 1,
                "BluePen", 1,
                "RedPen", 1,
                "Doll", 0.5,
                "Pills", 0.1,
                "PillsBeta", 0.1,
                "PillsAntiDep", 0.1,
                "PillsVitamins", 0.1,
                "Pistol", 0.05,
                "Pistol2", 0.02,
                "Revolver_Short", 0.05,
                "CreditCard", 1,
                "Perfume", 0.5,
                "Radio.WalkieTalkie1",0.05,
                "Radio.WalkieTalkie2",0.03,
                "Radio.WalkieTalkie3",0.001,
                "Cigarettes", 0.5,
                "Matches", 0.4,
                "Lighter", 0.4,
            }
        },
        inventorymale = {
            rolls = 1,
            items = {
                "MuldraughMap", 0.2,
                "WestpointMap", 0.2,
                "MarchRidgeMap",0.1,
                "RosewoodMap",0.1,
                "RiversideMap",0.1,
                "Wallet", 1,
                "Wallet2", 1,
                "Wallet3", 1,
                "Wallet4", 1,
                "Locket", 1,
                "Comb", 1,
                "Magazine", 1,
                "Newspaper", 1,
                "Notebook", 1,
                "Pencil", 1,
                "Pen", 0.1,
                "BluePen", 0.1,
                "RedPen", 0.1,
                "Pills", 0.1,
                "PillsBeta", 0.1,
                "PillsAntiDep", 0.1,
                "PillsVitamins", 0.1,
                "Pistol", 0.05,
                "Pistol2", 0.02,
                "Revolver_Short", 0.05,
                "Cologne", 0.2,
                "CreditCard", 1,
                "Radio.WalkieTalkie1",0.05,
                "Radio.WalkieTalkie2",0.03,
                "Radio.WalkieTalkie3",0.001,
                "Cigarettes", 0.5,
                "Matches", 0.4,
                "Lighter", 0.4,
            }
        },
        locker = {
            procedural = true,
            procList = {
                {name="Locker", min=0, max=99, weightChance=100},
                {name="LockerClassy", min=0, max=99, forceForZones="Rich"},
            }
        },
        logs = {
            rolls = 1,
            items = {
                "Log", 100,
                "Log", 100,
                "Log", 7,
                "Log", 7,
                "Log", 7,
                "Log", 7,
                "Log", 7,
            }
        },
        medicine = {
            rolls = 3,
            items = {
                "Bandaid", 7,
                "Pills", 7,
                "PillsBeta", 7,
                "PillsAntiDep", 7,
                "PillsSleepingTablets", 7,
                "PillsVitamins", 7,
                "Tweezers", 5,
                "Antibiotics", 5,
            }
        },
        metal_shelves = {
            rolls = 3,
            items = {
                "NailsBox", 1,
                "PaperclipBox", 0.8,
                "DuctTape", 0.8,
                "Glue", 0.8,
                "Scotchtape", 0.8,
                "Twine", 0.8,
                "Thread", 1.5,
                "Needle", 0.8,
                "Woodglue", 0.8,
                "Rope", 0.8,
                "Nails", 3,
                "Nails", 3,
                "Poolcue", 2,
                "Hammer", 3,
                "Wire", 1.5,
                "Saw", 1,
                "GardenSaw", 1,
                "Torch", 2,
                "HandTorch", 3,
                "Battery", 3,
                "Screwdriver", 1,
                "Toolbox", 0.5,
                "Radio.ElectricWire", 2,
                "Golfclub", 2,
                "Crowbar", 2,
                "Paperclip", 2,
                "Radio.RadioBlack",2,
                "Radio.RadioRed",1,
                "Radio.WalkieTalkie1",0.05,
                "Radio.WalkieTalkie2",0.03,
                "Radio.WalkieTalkie3",0.01,
                "Radio.WalkieTalkie4",0.005,
                "Radio.WalkieTalkie5",0.001,
                "LeadPipe", 1,
                "HandAxe", 0.8,
                "PipeWrench", 1,
                "ClubHammer", 1,
                "WoodenMallet", 1,
            }
        },
        microwave = {
            rolls = 0,
            items = {
            }
        },
        officedrawers = {
            rolls = 1,
            items = {
                "Battery", 3,
                "Nails", 3,
                "Nails", 3,
                "Nails", 2,
                "Lighter", 3,
                "Torch", 1,
                "HandTorch", 3,
                "WhiskeyFull", 2,
                "Chocolate", 3,
                "Pills", 1,
                "PillsBeta", 1,
                "PillsAntiDep", 1,
                "PillsSleepingTablets", 1,
                "Crisps", 2,
                "Crisps2", 2,
                "Crisps3", 2,
                "Pop", 2,
                "Pop2", 2,
                "Pop3", 2,
                "Magazine", 3,
                "Newspaper", 3,
                "Book", 3,
                "ComicBook", 1,
                "Lollipop", 2,
                "MintCandy", 2,
                "SheetPaper2", 3,
                "Matches", 2,
                "PillsVitamins", 1,
                "Magazine", 2,
                "Newspaper", 2,
                "Book", 2,
                "Cigarettes", 1,
                "Radio.RadioBlack",2,
                "Radio.RadioRed",1,
                "LetterOpener",1,
            },
            junk = {
                rolls = 1,
                items = {
                    "SheetPaper2", 2,
                    "Notebook", 2,
                    "Pencil", 3,
                    "Pen", 3,
                    "BluePen", 1,
                    "RedPen", 1,
                    "Scissors", 3,
                    "Cologne", 1,
                    "Perfume", 1,
                    "CardDeck", 1,
                    "Comb", 2,
                    "Toothbrush", 1,
                    "Notebook", 3,
                    "Razor", 1,
                    "Lipstick", 1,
                    "MakeupEyeshadow", 1,
                    "MakeupFoundation", 1,
                    "Pen", 4,
                    "BluePen", 2,
                    "RedPen", 2,
                    "Pencil", 4,
                    "RubberBand", 4,
                    "Eraser", 4,
                    "Paperclip", 4,
                    "Paperclip", 4,
                    "Tissue", 3,
                }
            }
        },
        other = {
            procedural = true,
            procList = {
                {name="OtherGeneric", min=0, max=99},
            }
        },
        plankstash = {
            procedural = true,
            procList = {
                {name="PlankStashMoney", min=0, max=1, weightChance=100},
                {name="PlankStashWeapon", min=0, max=1, weightChance=100},
            }
        },
        postbox = {
            rolls = 3,
            items = {
                "Newspaper", 2,
                "Magazine", 2,
                "FishingMag1", 0.2,
                "FishingMag2", 0.2,
                "HuntingMag1", 0.2,
                "HuntingMag2", 0.2,
                "HuntingMag3", 0.2,
                "HerbalistMag", 0.2,
                "CookingMag1", 0.2,
                "CookingMag2", 0.2,
                "ElectronicsMag1", 0.2,
                "ElectronicsMag2", 0.2,
                "ElectronicsMag3", 0.2,
                "ElectronicsMag4", 0.2,
                "ElectronicsMag5", 0.2,
                "MechanicMag1", 0.2,
                "MechanicMag2", 0.2,
                "MechanicMag3", 0.2,
                "EngineerMagazine1", 0.2,
                "EngineerMagazine2", 0.2,
                "MetalworkMag1", 0.2,
                "MetalworkMag2", 0.2,
                "MetalworkMag3", 0.2,
                "MetalworkMag4", 0.2,
            }
        },
        restaurantdisplay = {
            procedural = true,
            procList = {
                {name="ServingTrayBurgers", min=0, max=99, weightChance=100},
                {name="ServingTrayChicken", min=0, max=99, weightChance=100},
                {name="ServingTrayFries", min=0, max=99, weightChance=100},
                {name="ServingTrayHotdogs", min=0, max=99, weightChance=100},
                {name="ServingTrayPie", min=0, max=99, weightChance=100},
                {name="ServingTrayPizza", min=0, max=99, weightChance=100},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="ShelfGeneric", min=0, max=99},
            }
        },
        shelvesmag = {
            procedural = true,
            procList = {
                {name="MagazineRackMaps", min=0, max=1, weightChance=50},
                {name="MagazineRackNewspaper", min=0, max=1, weightChance=50},
                {name="MagazineRackMixed", min=0, max=99, weightChance=100},
            }
        },
        sidetable = {
            rolls = 1,
            items = {
                "Battery", 2,
                "Lighter", 2,
                "Torch", 1,
                "HandTorch", 1.5,
                "Journal", 2,
                "Magazine", 4,
                "Newspaper", 4,
                "Book", 4,
                "ComicBook", 2,
                "Cigarettes", 4,
                "BookCarpentry1", 1,
                --                "BookBlacksmith1", 1,
                "BookFirstAid1", 1,
                "BookMetalWelding1", 1,
                "BookMetalWelding1", 1,
                "BookElectrician1", 1,
                "BookMechanic1" , 1,
                "BookFarming1", 1,
                "BookForaging1", 1,
                "BookCooking1", 1,
                "BookFishing1", 1,
                "BookTrapping1", 1,
                "BookCarpentry2", 0.5,
                "BookFarming2", 0.5,
                "BookForaging2", 0.5,
                "BookCooking2", 0.5,
                "BookFishing2", 0.5,
                "BookTrapping2", 0.5,
                --                "BookBlacksmith2", 0.5,
                "BookFirstAid2", 0.5,
                "BookMetalWelding2", 0.5,
                "BookElectrician2", 0.5,
                "BookMechanic2", 0.5,
                "Key1", 1,
                "Key2", 1,
                "Key3", 1,
                "Key4", 1,
                "Key5", 1,
                "HomeAlarm", 1,
                "Radio.RadioBlack",2,
                "Radio.RadioRed",1,
                "Radio.WalkieTalkie1",0.05,
                "Radio.WalkieTalkie2",0.03,
                "Radio.WalkieTalkie3",0.001,
                "Remote", 8,
                "MuldraughMap", 0.05,
                "WestpointMap", 0.05,
                "MarchRidgeMap",0.01,
                "RosewoodMap",0.01,
                "RiversideMap",0.01,
                "FishingMag1", 0.7,
                "FishingMag2", 0.7,
                "HuntingMag1", 0.7,
                "HuntingMag2", 0.7,
                "HuntingMag3", 0.7,
                "HerbalistMag", 0.7,
                "CookingMag1", 0.7,
                "CookingMag2", 0.7,
                "ElectronicsMag1", 0.7,
                "ElectronicsMag2", 0.7,
                "ElectronicsMag3", 0.7,
                "ElectronicsMag4", 0.7,
                "ElectronicsMag5", 0.7,
                "MechanicMag1", 0.7,
                "MechanicMag2", 0.7,
                "MechanicMag3", 0.7,
                "EngineerMagazine1", 0.7,
                "EngineerMagazine2", 0.7,
                "MetalworkMag1", 0.7,
                "MetalworkMag2", 0.7,
                "MetalworkMag3", 0.7,
                "MetalworkMag4", 0.7,
            },
            junk = {
                rolls = 1,
                items = {
                    "Pen", 4,
                    "BluePen", 2,
                    "RedPen", 2,
                    "Paperclip", 1,
                    "Pencil", 4,
                    "RubberBand", 2,
                    "Eraser", 4,
                    "Tissue", 9,
                    "Candle", 4,
                    "Matches", 3,
                    "SheetPaper2", 4,
                    "Notebook", 4,
                }
            }
        },
        stove = {
            rolls = 0,
            items = {
            }
        },
        vendingpop = {
            rolls = 4,
            items = {
                "Pop", 4,
                "Pop2", 4,
                "Pop3", 4,
                "PopBottle", 3,
                "Pop", 4,
                "Pop2", 4,
                "Pop3", 4,
                "PopBottle", 3,
                "Pop", 4,
                "Pop2", 4,
                "Pop3", 4,
                "PopBottle", 3,
            }
        },
        vendingsnack = {
            rolls = 4,
            items = {
                "Crisps", 4,
                "Crisps2", 4,
                "Crisps3", 4,
                "Crisps", 4,
                "Crisps2", 4,
                "Crisps3", 4,
                "Crisps", 4,
                "Crisps2", 4,
                "Crisps3", 4,
            }
        },
        wardrobe = {
            procedural = true,
            procList = {
                {name="WardrobeMan", min=0, max=2, weightChance=100},
                {name="WardrobeManClassy", min=0, max=1, weightChance=100},
                {name="WardrobeWoman", min=0, max=2, weightChance=100},
            }
        },
        
        -- This is an example to add a specific distribution on a dead corpse according to his outfit
        Outfit_Generic99 = {
            rolls = 100,
            items = {
                "Wallet", 100,
            }
        },
    },
    
    armyhanger = {
        counter = {
            procedural = true,
            procList = {
                {name="ArmyHangarTools", min=0, max=99},
            }
        },
        locker = {
            procedural = true,
            procList = {
                {name="ArmyHangarOutfit", min=0, max=99},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="ArmyHangarTools", min=0, max=99},
            }
        }
    },
    
    armystorage = {
        locker = {
            procedural = true,
            procList = {
                {name="ArmyStorageGuns", min=0, max=99, forceForTiles="furniture_storage_02_8;furniture_storage_02_9;furniture_storage_02_10;furniture_storage_02_11"},
                {name="ArmyStorageOutfit", min=0, max=99, forceForTiles="furniture_storage_02_4;furniture_storage_02_5;furniture_storage_02_6;furniture_storage_02_7"},
            },
            dontSpawnAmmo = true,
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="ArmyStorageGuns", min=0, max=99, weightChance=100},
                {name="ArmyStorageElectronics", min=0, max=1, weightChance=20},
            },
            dontSpawnAmmo = true,
        }
    },
    
    armysurplus = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="ArmySurplusHeadwear", min=0, max=4, weightChance=100},
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="ArmySurplusOutfit", min=0, max=4, weightChance=100},
                {name="ArmySurplusBackpacks", min=0, max=4, weightChance=40},
                {name="CampingStoreGear", min=0, max=4, weightChance=100},
                {name="ArmyStorageElectronics", min=0, max=1, weightChance=10},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="ArmySurplusOutfit", min=0, max=4, weightChance=100},
                {name="ArmySurplusBackpacks", min=0, max=4, weightChance=40},
                {name="CampingStoreGear", min=0, max=4, weightChance=100},
                {name="ArmyStorageElectronics", min=0, max=1, weightChance=10},
            }
        },
        clothingrack = {
            procedural = true,
            procList = {
                {name="ArmySurplusOutfit", min=0, max=99},
            }
        }
    },
    
    bakery = {
        isShop = true,
        displaycase = {
            procedural = true,
            procList = {
                {name="BakeryMisc", min=0, max=99},
            }
        },
        displaycasebakery = {
            procedural = true,
            procList = {
                {name="BakeryBread", min=0, max=4, weightChance=100},
                {name="BakeryPie", min=0, max=2, weightChance=60},
                {name="BakeryCake", min=0, max=2, weightChance=80},
                {name="BakeryMisc", min=0, max=2, weightChance=20},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreKitchenBaking", min=0, max=8, weightChance=20},
                {name="StoreCounterBagsFancy", min=0, max=1, weightChance=100},
                {name="BakeryBread", min=0, max=4, weightChance=100},
                {name="BakeryPie", min=0, max=2, weightChance=60},
                {name="BakeryCake", min=0, max=2, weightChance=80},
                {name="BakeryMisc", min=0, max=2, weightChance=20},
            }
        },
        grocerstand = {
            procedural = true,
            procList = {
                {name="BakeryBread", min=0, max=4, weightChance=100},
                {name="BakeryPie", min=0, max=2, weightChance=50},
                {name="BakeryCake", min=0, max=2, weightChance=50},
                {name="BakeryMisc", min=0, max=2, weightChance=20},
            }
        }
    },
    
    bar = {
        isShop = true,
        shelves = {
            procedural = true,
            procList = {
                {name="BarShelfLiquor", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="BarCounterMisc", min=0, max=4, weightChance=50},
                {name="BarCounterWeapon", min=0, max=1, weightChance=10},
                {name="BarCounterLiquor", min=0, max=4, weightChance=100},
            }
        },
        bin = {
            procedural = true,
            procList = {
                {name="BinBar", min=0, max=99},
            }
        }
    },
    
    barkitchen = {
        isShop = true,
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeSnacks", min=0, max=99},
            }
        },
        freezer = {
            rolls = 0,
            items = {
            
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="BarShelfLiquor", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="BarCounterMisc", min=0, max=4, weightChance=50},
                {name="BarCounterWeapon", min=0, max=1, weightChance=10},
                {name="BarCounterLiquor", min=0, max=4, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="BarShelfLiquor", min=0, max=99},
            }
        },
        bin = {
            procedural = true,
            procList = {
                {name="BinBar", min=0, max=99},
            }
        }
    },
    
    barstorage = {
        isShop = true,
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeSnacks", min=0, max=99},
            }
        },
        freezer = {
            rolls = 1,
            items = {
            
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="BarShelfLiquor", min=0, max=99},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="BarShelfLiquor", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="BarCounterMisc", min=0, max=4, weightChance=50},
                {name="BarCounterWeapon", min=0, max=1, weightChance=10},
                {name="BarCounterLiquor", min=0, max=4, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="BarCratePool", min=0, max=99, weightChance=100},
                {name="BarCrateDarts", min=0, max=99, weightChance=100},
            }
        },
        bin = {
            procedural = true,
            procList = {
                {name="BinBar", min=0, max=99},
            }
        }
    },
    
    bathroom = {
        counter = {
            procedural = true,
            procList = {
                {name="BathroomCounter", min=0, max=99},
                {name="BathroomCounterEmpty", min=0, max=99, forceForRooms="bank;breakroom;church;daycare;meetingroom;motelroom;policestorage;spiffoskitchen;restaurant"},
                {name="BathroomCounterNoMeds", min=0, max=99, forceForItems="fixtures_bathroom_01_28;fixtures_bathroom_01_29;fixtures_bathroom_01_37;fixtures_bathroom_01_38"},
            }
        },
        medicine = {
            procedural = true,
            procList = {
                {name="BathroomCabinet", min=0, max=99},
            }
        }
    },
    
    bedroom = {
        crate = {
            procedural = true,
            procList = {
                {name="ClothingStorageWinter", min=0, max=1, weightChance=100},
                {name="CrateBooks", min=0, max=1, weightChance=40},
                {name="CrateCannedFood", min=0, max=1, weightChance=20},
                {name="CrateCanning", min=0, max=1, weightChance=5},
                {name="CrateCamping", min=0, max=1, weightChance=20},
                {name="CrateClothesRandom", min=0, max=1, weightChance=40},
                {name="CrateComics", min=0, max=1, weightChance=40},
                {name="CrateCompactDisks", min=0, max=1, weightChance=5},
                {name="CrateElectronics", min=0, max=1, weightChance=20},
                {name="CrateFitnessWeights", min=0, max=1, weightChance=5},
                {name="CrateInstruments", min=0, max=1, weightChance=20},
                {name="CrateMagazines", min=0, max=1, weightChance=40},
                {name="CratePaint", min=0, max=1, weightChance=20},
                {name="CrateSports", min=0, max=1, weightChance=40},
                {name="CrateTailoring", min=0, max=1, weightChance=20},
                {name="CrateTools", min=0, max=1, weightChance=10},
                {name="CrateVHSTapes", min=0, max=1, weightChance=5},
            }
        },
        desk = {
            procedural = true,
            procList = {
                {name="OfficeDeskHome", min=0, max=99}
            }
        },
        dresser = {
            procedural = true,
            procList = {
                {name="BedroomDresser", min=0, max=99},
            }
        },
        locker = {
            procedural = true,
            procList = {
                {name="LockerArmyBedroom", min=0, max=99, forceForZones="Army"},
            }
        },
        plankstash = {
            procedural = true,
            procList = {
                {name="PlankStashMagazine", min=0, max=99},
            }
        },
        sidetable = {
            procedural = true,
            procList = {
                {name="BedroomSideTable", min=0, max=99},
            }
        },
        wardrobe = {
            procedural = true,
            procList = {
                {name="WardrobeChild", min=0, max=2, forceForItems="furniture_bedding_01_36;furniture_bedding_01_38"},
                {name="WardrobeMan", min=0, max=2, weightChance=100},
                {name="WardrobeManClassy", min=0, max=2, forceForZones="Rich"},
                {name="WardrobeRedneck", min=0, max=2, forceForZones="TrailerPark"},
                {name="WardrobeWomanClassy", min=0, max=2, forceForZones="Rich"},
                -- Search entire room for listed sprites. If found, force container to spawn.
                {name="WardrobeWoman", min=0, max=2, weightChance=100},
            }
        },
    },
    
    bookstore = {
        shelves = {
            procedural = true,
            procList = {
                {name="BookstoreBooks", min=6, max=99, weightChance=100},
                {name="BookstoreMisc", min=0, max=99, weightChance=20},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="BookstoreBags", min=0, max=1, weightChance=20},
                {name="BookstoreStationery", min=0, max=12, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="BookstoreBooks", min=0, max=99},
            }
        },
    },
    
    breakroom = {
        counter = {
            procedural = true,
            procList = {
                {name="BreakRoomCounter", min=0, max=99},
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeBreakRoom", min=0, max=99},
            }
        },
        overhead = {
            procedural = true,
            procList = {
                {name="BreakRoomShelves", min=0, max=99},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="BreakRoomShelves", min=0, max=99},
            }
        }
    },
    
    burgerstorage = {
        isShop = true,
        metal_shelves = {
            procedural = true,
            procList = {
                {name="StoreKitchenDishes", min=0, max=2, weightChance=20},
                {name="StoreKitchenPots", min=0, max=2, weightChance=20},
                {name="StoreKitchenBaking", min=0, max=12, weightChance=100},
                {name="StoreKitchenSauce", min=0, max=12, weightChance=80},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="StoreKitchenBaking", min=0, max=12, weightChance=100},
                {name="StoreKitchenSauce", min=0, max=12, weightChance=80},
            }
        }
    },
    
    burgerkitchen = {
        isShop = true,
        freezer = {
            procedural = true,
            procList = {
                {name="BurgerKitchenFreezer", min=0, max=99},
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="BurgerKitchenFridge", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreKitchenBaking", min=0, max=2, weightChance=40},
                {name="BurgerKitchenButcher", min=0, max=2, weightChance=40},
                {name="StoreKitchenDishes", min=0, max=2, weightChance=100},
                {name="StoreKitchenPotatoes", min=0, max=2, weightChance=60},
                {name="StoreKitchenPots", min=0, max=2, weightChance=100},
                {name="StoreKitchenSauce", min=0, max=2, weightChance=80},
            }
        }
    },
    
    butcher = {
        displaycasebutcher = {
            procedural = true,
            procList = {
                {name="ButcherChops", min=1, max=4, weightChance=100},
                {name="ButcherGround", min=1, max=2, weightChance=60},
                {name="ButcherChicken", min=1, max=1, weightChance=80},
                {name="ButcherSmoked", min=1, max=4, weightChance=40},
                {name="ButcherFish", min=0, max=1, weightChance=20},
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="Meat", min=0, max=99},
            }
        },
        freezer = {
            procedural = true,
            procList = {
                {name="Meat", min=0, max=99},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="StoreShelfSpices", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreKitchenButcher", min=0, max=99, weightChance=100},
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
            }
        }
    },
    
    cafe = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreKitchenBaking", min=0, max=99, weightChance=100},
            }
        },
        displaycase = {
            procedural = true,
            procList = {
                {name="BakeryBread", min=0, max=2, weightChance=20},
                {name="BakeryPie", min=0, max=2, weightChance=100},
                {name="BakeryCake", min=0, max=2, weightChance=80},
                {name="BakeryMisc", min=0, max=4, weightChance=60},
            }
        }
    },
    
    cafekitchen = {
        isShop = true,
        fridge = {
            procedural = true,
            procList = {
                {name="CafeKitchenFridge", min=0, max=99},
            }
        },
        freezer = {
            rolls = 1,
            items = {
            
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreKitchenBaking", min=0, max=4, weightChance=60},
                {name="StoreKitchenCafe", min=0, max=8, weightChance=100},
                {name="StoreKitchenDishes", min=0, max=2, weightChance=20},
                {name="StoreKitchenPots", min=0, max=2, weightChance=20},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="StoreKitchenCafe", min=1, max=8},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="StoreKitchenBaking", min=0, max=99},
            }
        },
    },
    
    camping = {
        isShop = true,
        clothingrack = {
            procedural = true,
            procList = {
                {name="CampingStoreClothes", min=0, max=99},
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeSnacks", min=0, max=99},
            }
        },
        freezer = {
            rolls = 0,
            items = {

            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
                {name="CampingStoreBooks", min=0, max=4, weightChance=80},
                {name="CampingStoreLegwear", min=0, max=2, weightChance=60},
                {name="CampingStoreBackpacks", min=0, max=2, weightChance=40},
                {name="CampingStoreGear", min=0, max=4, weightChance=100},
                {name="FishingStoreGear", min=0, max=2, weightChance=20},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="CampingStoreBackpacks", min=0, max=2, weightChance=40},
                {name="CampingStoreBooks", min=0, max=4, weightChance=80},
                {name="CampingStoreLegwear", min=0, max=2, weightChance=60},
                {name="CampingStoreGear", min=0, max=4, weightChance=100},
                {name="FishingStoreGear", min=0, max=2, weightChance=20},
            }
        }
    },
    
    campingstorage = {
        crate = {
            procedural = true,
            procList = {
                {name="CampingStoreClothes", min=0, max=2, weightChance=60},
                {name="CampingStoreLegwear", min=0, max=2, weightChance=60},
                {name="CampingStoreBackpacks", min=0, max=2, weightChance=40},
                {name="CampingStoreGear", min=0, max=4, weightChance=100},
                {name="FishingStoreGear", min=0, max=2, weightChance=20},
            }
        }
    },
    
    candystorage = {
        isShop = true,
        metal_shelves = {
            procedural = true,
            procList = {
                {name="StoreKitchenDishes", min=0, max=2, weightChance=20},
                {name="StoreKitchenPots", min=0, max=2, weightChance=20},
                {name="CandyStoreSugar", min=0, max=12, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="GigamartCandy", min=0, max=12, weightChance=40},
                {name="CandyStoreSugar", min=0, max=12, weightChance=100},
            }
        }
    },
    
    candystore = {
        isShop = true,
        displaycase = {
            procedural = true,
            procList = {
                {name="CandyStoreSnacks", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="CandyStoreSnacks", min=0, max=12, weightChance=20},
                {name="CandyStoreSugar", min=0, max=12, weightChance=10},
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
                {name="StoreKitchenDishes", min=0, max=2, weightChance=20},
                {name="StoreKitchenPots", min=0, max=2, weightChance=20},
            }
        }
    },
    
    changeroom = {
        locker = {
            procedural = true,
            procList = {
                {name="PoliceLockers", min=0, max=99, forceForRooms="policestorage"},
                {name="FactoryLockers", min=0, max=99, forceForRooms="factory"},
                {name="FireDeptLockers", min=0, max=99, forceForRooms="firestorage"},
                {name="PrisonGuardLockers", min=0, max=99, forceForRooms="cells"},
                {name="SchoolLockers", min=0, max=99, forceForRooms="classroom"},
            }
        },
        
        counter = {
            procedural = true,
            procList = {
                {name="ChangeroomCounters", min=0, max=99},
            }
        }
    },
    
    classroom = {
        counter = {
            procedural = true,
            procList = {
                {name="ClassroomMisc", min=0, max=99},
            }
        },
        desk = {
            procedural = true,
            procList = {
                {name="ClassroomDesk", min=0, max=99},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="ClassroomShelves", min=0, max=99},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="ClassroomShelves", min=0, max=99},
            }
        }
    },
    
    clothingstorage = {
        clothingrack = {
            procedural = true,
            procList = {
                {name="ClothingStorageAllJackets", min=0, max=4, weightChance=100},
                {name="ClothingStorageAllShirts", min=0, max=4, weightChance=100},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="ClothingStorageFootwear", min=0, max=2, weightChance=40},
                {name="ClothingStorageHeadwear", min=0, max=1, weightChance=20},
                {name="ClothingStorageLegwear", min=0, max=4, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="ClothingStorageAllJackets", min=0, max=2, weightChance=40},
                {name="ClothingStorageAllShirts", min=0, max=2, weightChance=80},
                {name="ClothingStorageFootwear", min=0, max=1, weightChance=40},
                {name="ClothingStorageHeadwear", min=0, max=1, weightChance=20},
                {name="ClothingStorageLegwear", min=0, max=2, weightChance=80},
                {name="ClothingStorageWinter", min=0, max=4, weightChance=100},
            }
        }
    },
    
    clothingstore = {
        isShop = true,
        displaycase = {
            procedural = true,
            procList = {
                {name="StoreDisplayWatches", min=0, max=99},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="ClothingStoresBoots", min=0, max=99, weightChance=50},
                {name="ClothingStoresShoes", min=0, max=99, weightChance=100},
            }
        },
        clothingrack = {
            procedural = true,
            procList = {
                {name="ClothingStoresDress", min=0, max=2, weightChance=20},
                {name="ClothingStoresJackets", min=0, max=4, weightChance=40},
                {name="ClothingStoresJacketsFormal", min=0, max=2, weightChance=10},
                {name="ClothingStoresJumpers", min=0, max=4, weightChance=60},
                {name="ClothingStoresOvershirts", min=0, max=8, weightChance=80},
                {name="ClothingStoresPants", min=0, max=8, weightChance=100},
                {name="ClothingStoresPantsFormal", min=0, max=2, weightChance=10},
                {name="ClothingStoresShirts", min=0, max=8, weightChance=100},
                {name="ClothingStoresShirtsFormal", min=0, max=2, weightChance=10},
                {name="ClothingStoresSport", min=0, max=4, weightChance=40},
                {name="ClothingStoresSummer", min=0, max=4, weightChance=40},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBagsFancy", min=0, max=1, weightChance=100},
                {name="ClothingStoresGloves", min=0, max=2, weightChance=40},
                {name="ClothingStoresEyewear", min=0, max=2, weightChance=100},
                {name="ClothingStoresHeadwear", min=0, max=2, weightChance=60},
                {name="ClothingStoresSocks", min=0, max=2, weightChance=20},
                {name="ClothingStoresUnderwear", min=0, max=2, weightChance=20},
            }
        }
    },
    
    conveniencestore = {
        isShop = true,
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeSnacks", min=0, max=8, weightChance=100},
                {name="FridgeSoda", min=0, max=8, weightChance=100},
                {name="FridgeWater", min=0, max=4, weightChance=40},
                {name="FridgeOther", min=0, max=4, weightChance=60},
            }
        },
        freezer = {
            rolls = 0,
            items = {

            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterTobacco", min=0, max=2, weightChance=40},
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="StoreShelfDrinks", min=0, max=4, weightChance=100},
                {name="StoreShelfSnacks", min=0, max=4, weightChance=100},
                {name="StoreShelfMedical", min=0, max=1, weightChance=20},
                {name="StoreShelfMechanics", min=0, max=1, weightChance=10},
            }
        }
    },
    
    cornerstore = {
        isShop = true,
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeSnacks", min=0, max=8, weightChance=100},
                {name="FridgeSoda", min=0, max=8, weightChance=100},
                {name="FridgeWater", min=0, max=4, weightChance=40},
                {name="FridgeOther", min=0, max=4, weightChance=60},
            }
        },
        freezer = {
            rolls = 0,
            items = {

            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterTobacco", min=0, max=2, weightChance=100},
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=10},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="StoreShelfDrinks", min=0, max=4, weightChance=100},
                {name="StoreShelfSnacks", min=0, max=4, weightChance=100},
                {name="StoreShelfMedical", min=0, max=1, weightChance=20},
                {name="StoreShelfMechanics", min=0, max=1, weightChance=10},
            }
        }
    },
    
    daycare = {
        counter = {
            procedural = true,
            procList = {
                {name="DaycareCounter", min=0, max=99},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="DaycareShelves", min=0, max=99},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="DaycareShelves", min=0, max=99},
            }
        },
    },
    
    dentiststorage = {
        isShop = true,
        metal_shelves = {
            procedural = true,
            procList = {
                {name="MedicalStorageDrugs", min=0, max=6, weightChance=100},
                {name="MedicalStorageTools", min=0, max=4, weightChance=80},
                {name="MedicalStorageOutfit", min=0, max=2, weightChance=40},
            }
        }
    },
    
    departmentstorage = {
        isShop = true,
        crate = {
            procedural = true,
            procList = {
                {name="CrateTV", min=0, max=2, weightChance=10},
                {name="CrateTVWide", min=0, max=2, weightChance=10},
                {name="ClothingStorageWinter", min=0, max=4, weightChance=100},
                {name="ClothingStorageHeadwear", min=0, max=2, weightChance=20},
                {name="ClothingStorageFootwear", min=0, max=2, weightChance=20},
                {name="ClothingStorageAllJackets", min=0, max=2, weightChance=80},
                {name="ClothingStorageAllShirts", min=0, max=2, weightChance=80},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="ClothingStorageFootwear", min=0, max=2, weightChance=20},
                {name="ClothingStorageHeadwear", min=0, max=2, weightChance=20},
                {name="ClothingStorageLegwear", min=0, max=4, weightChance=80},
                {name="GigamartHousewares", min=0, max=2, weightChance=60},
                {name="GigamartBedding", min=0, max=2, weightChance=60},
                {name="GigamartPots", min=0, max=2, weightChance=60},
                {name="GigamartLightbulb", min=0, max=2, weightChance=60},
                {name="GigamartHouseElectronics", min=0, max=4, weightChance=100},
            }
        },
        wardrobe = {
            rolls = 0,
            items = {
            
            }
        },
        sidetable = {
            rolls = 0,
            items = {
            
            }
        }
    },
    
    departmentstore = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
            }
        },
        displaycase = {
            procedural = true,
            procList = {
                {name="JewelrySilver", min=0, max=4, weightChance=80},
                {name="JewelryGold", min=0, max=4, weightChance=40},
                {name="JewelryGems", min=0, max=2, weightChance=10},
                {name="JewelryWeddingRings", min=0, max=8, weightChance=100},
                {name="JewelryWrist", min=0, max=2, weightChance=80},
                {name="JewelryOthers", min=0, max=99, weightChance=10},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="ClothingStoresBoots", min=0, max=12, weightChance=50},
                {name="ClothingStoresShoes", min=0, max=24, weightChance=100},
            }
        },
        wardrobe = {
            rolls = 0,
            items = {
            
            }
        },
        sidetable = {
            rolls = 0,
            items = {
            
            }
        }
    },
    
    dining = {
        counter = {
            rolls = 0,
            items = {

            }
        },
        shelves = {
            rolls = 0,
            items = {

            }
        }
    },
    
    dinerkitchen = {
        isShop = true,
        freezer = {
            procedural = true,
            procList = {
                {name="DinerKitchenFreezer", min=0, max=99},
            },
        },
        fridge = {
            procedural = true,
            procList = {
                {name="DinerKitchenFridge", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreKitchenBaking", min=0, max=2, weightChance=40},
                {name="BurgerKitchenButcher", min=0, max=2, weightChance=40},
                {name="StoreKitchenDishes", min=0, max=2, weightChance=100},
                {name="StoreKitchenPotatoes", min=0, max=2, weightChance=60},
                {name="StoreKitchenPots", min=0, max=2, weightChance=100},
                {name="StoreKitchenSauce", min=0, max=2, weightChance=80},
            }
        }
    },
    
    electronicsstorage = {
        isShop = true,
        crate = {
            procedural = true,
            procList = {
                {name="ElectronicStoreComputer", min=0, max=12, weightChance=100},
                {name="CrateTV", min=0, max=12, weightChance=40},
                {name="CrateTVWide", min=0, max=12, weightChance=20},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="ElectronicStoreMusic", min=0, max=2, weightChance=100},
                {name="ElectronicStoreLights", min=0, max=2, weightChance=20},
                {name="ElectronicStoreMagazines", min=0, max=2, weightChance=40},
                {name="ElectronicStoreMisc", min=0, max=99, weightChance=40},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="ElectronicStoreComputer", min=0, max=2, weightChance=100},
                {name="ElectronicStoreHAMRadio", min=0, max=1, weightChance=20},
                {name="ElectronicStoreMisc", min=0, max=99, weightChance=40},
            }
        }
    },
    
    electronicsstore = {
        isShop = true,
        displaycase = {
            procedural = true,
            procList = {
                {name="StoreDisplayWatches", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
                {name="ElectronicStoreHAMRadio", min=0, max=1, weightChance=20},
                {name="ElectronicStoreComputer", min=0, max=4, weightChance=100},
                {name="ElectronicStoreMusic", min=0, max=4, weightChance=100},
                {name="ElectronicStoreLights", min=0, max=2, weightChance=20},
                {name="ElectronicStoreMagazines", min=0, max=2, weightChance=40},
                {name="ElectronicStoreMisc", min=0, max=99, weightChance=40},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="ElectronicStoreHAMRadio", min=0, max=1, weightChance=20},
                {name="ElectronicStoreComputer", min=0, max=4, weightChance=100},
                {name="ElectronicStoreMusic", min=0, max=4, weightChance=100},
                {name="ElectronicStoreLights", min=0, max=2, weightChance=20},
                {name="ElectronicStoreMagazines", min=1, max=2, weightChance=40},
                {name="ElectronicStoreMisc", min=0, max=99, weightChance=40},
            }
        }
    },
    
    empty = {
        crate = {
            procedural = true,
            procList = {
                {name="RandomFiller", min=0, max=99},
            }
        },
        other = {
            rolls = 0,
            items = {
            
            }
        },
    },
    
    factorystorage = {
        locker = {
            procedural = true,
            procList = {
                {name="FactoryLockers", min=0, max=99, weightChance=100},
                {name="MechanicShelfOutfit", min=0, max=99, forceForRooms="mechanic"},
            }
        }
    },
    
    farmstorage = {
        crate = {
            procedural = true,
            procList = {
                {name="CrateFertilizer", min=0, max=99, weightChance=100},
                {name="CrateGravelBags", min=0, max=4, weightChance=40},
                {name="CratePlaster", min=0, max=4, weightChance=20},
                {name="CrateSandbags", min=0, max=4, weightChance=60},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="GardenStoreTools", min=0, max=4, weightChance=40},
                {name="GardenStoreMisc", min=0, max=99, weightChance=100},
            }
        },
    },
    
    firestorage = {
        metal_shelves = {
            procedural = true,
            procList = {
                {name="FireStorageTools", min=0, max=99, weightChance=100},
                {name="FireStorageOutfit", min=0, max=99, weightChance=40},
            }
        }
    },
    
    fishingstorage = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
                {name="CampingStoreBooks", min=0, max=2, weightChance=80},
                {name="CampingStoreLegwear", min=0, max=2, weightChance=40},
                {name="CampingStoreBackpacks", min=0, max=2, weightChance=20},
                {name="CampingStoreGear", min=0, max=2, weightChance=60},
                {name="FishingStoreGear", min=0, max=12, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="FishingStoreGear", min=0, max=99},
            }
        }
    },
    
    fossoil = {
        isShop = true,
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeSnacks", min=0, max=8, weightChance=100},
                {name="FridgeSoda", min=0, max=8, weightChance=100},
                {name="FridgeWater", min=0, max=4, weightChance=40},
                {name="FridgeOther", min=0, max=4, weightChance=60},
            }
        },
        freezer = {
            rolls = 0,
            items = {

            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterTobacco", min=0, max=2, weightChance=40},
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="StoreShelfDrinks", min=0, max=4, weightChance=60},
                {name="StoreShelfSnacks", min=0, max=4, weightChance=60},
                {name="StoreShelfMedical", min=0, max=1, weightChance=20},
                {name="StoreShelfMechanics", min=0, max=4, weightChance=100},
            }
        }
    },
    
    furniturestorage = {
        isShop = true,
        freezer = {
            rolls = 0,
            items = {
            
            }
        },
        fridge = {
            rolls = 0,
            items = {
            
            }
        },
        wardrobe = {
            rolls = 0,
            items = {
            
            }
        },
        sidetable = {
            rolls = 0,
            items = {
            
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="CrateBlueComfyChair", min=0, max=1, weightChance=60},
                {name="CrateBluePlasticChairs", min=0, max=1, weightChance=100},
                {name="CrateBlueRattanChair", min=0, max=1, weightChance=40},
                {name="CrateBrownComfyChair", min=0, max=1, weightChance=60},
                {name="CrateBrownLowTables", min=0, max=1, weightChance=40},
                {name="CrateChromeSinks", min=0, max=1, weightChance=20},
                {name="CrateDarkBlueChairs", min=0, max=1, weightChance=80},
                {name="CrateDarkWoodenChairs", min=0, max=1, weightChance=80},
                {name="CrateFancyBlackChairs", min=0, max=1, weightChance=60},
                {name="CrateFancyDarkTables", min=0, max=1, weightChance=40},
                {name="CrateFancyLowTables", min=0, max=1, weightChance=40},
                {name="CrateFancyToilets", min=0, max=1, weightChance=20},
                {name="CrateFancyWhiteChairs", min=0, max=1, weightChance=60},
                {name="CrateFoldingChairs", min=0, max=1, weightChance=100},
                {name="CrateGreenChairs", min=0, max=1, weightChance=80},
                {name="CrateGreenComfyChair", min=0, max=1, weightChance=60},
                {name="CrateGreenOven", min=0, max=1, weightChance=20},
                {name="CrateGreyChairs", min=0, max=1, weightChance=80},
                {name="CrateGreyComfyChair", min=0, max=1, weightChance=60},
                {name="CrateGreyOven", min=0, max=1, weightChance=20},
                {name="CrateIndustrialSinks", min=0, max=1, weightChance=20},
                {name="CrateLightRoundTable", min=0, max=1, weightChance=60},
                {name="CrateMetalLockers", min=0, max=1, weightChance=40},
                {name="CrateModernOven", min=0, max=1, weightChance=20},
                {name="CrateOakRoundTable", min=0, max=1, weightChance=60},
                {name="CrateOfficeChairs", min=0, max=1, weightChance=100},
                {name="CrateOrangeModernChair", min=0, max=1, weightChance=60},
                {name="CratePlasticChairs", min=0, max=1, weightChance=100},
                {name="CratePurpleRattanChair", min=0, max=1, weightChance=40},
                {name="CratePurpleWoodenChairs", min=0, max=1, weightChance=80},
                {name="CrateRedBBQs", min=0, max=1, weightChance=40},
                {name="CrateRedChairs", min=0, max=1, weightChance=80},
                {name="CrateRedOven", min=0, max=1, weightChance=20},
                {name="CrateRedWoodenChairs", min=0, max=1, weightChance=80},
                {name="CrateRoundTable", min=0, max=1, weightChance=40},
                {name="CrateWhiteComfyChair", min=0, max=1, weightChance=60},
                {name="CrateWhiteSimpleChairs", min=0, max=1, weightChance=80},
                {name="CrateWhiteSinks", min=0, max=1, weightChance=20},
                {name="CrateWhiteWoodenChairs", min=0, max=1, weightChance=80},
                {name="CrateWoodenChairs", min=0, max=1, weightChance=80},
                {name="CrateWoodenStools", min=0, max=1, weightChance=80},
                {name="CrateYellowModernChair", min=0, max=1, weightChance=60},
            }
        }
    },
    
    furniturestore = {
        isShop = true,
        freezer = {
            rolls = 0,
            items = {
            
            }
        },
        fridge = {
            rolls = 0,
            items = {
            
            }
        },
        wardrobe = {
            rolls = 0,
            items = {
            
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="RandomFiller", min=0, max=99, weightChance=100},
            }
        }
    },
    
    garagestorage = {
        locker = {
            procedural = true,
            procList = {
                {name="GarageTools", min=0, max=99},
                {name="FireDeptLockers", min=0, max=99, forceForRooms="firestorage"},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="GarageCarpentry", min=0, max=1, weightChance=100},
                {name="GarageTools", min=0, max=1, weightChance=100},
                {name="GarageMechanic", min=0, max=1, weightChance=100},
                {name="GarageMetalwork", min=0, max=1, weightChance=100},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="GarageCarpentry", min=0, max=2, weightChance=100},
                {name="GarageMechanic", min=0, max=1, weightChance=100},
                {name="GarageMetalwork", min=0, max=2, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="CrateBooks", min=0, max=1, weightChance=40},
                {name="CrateCamping", min=0, max=1, weightChance=10},
                {name="CrateCannedFood", min=0, max=1, weightChance=2},
                {name="CrateCannedFoodSpoiled", min=0, max=1, weightChance=1},
                {name="CrateCanning", min=0, max=1, weightChance=4},
                {name="CrateClothesRandom", min=0, max=1, weightChance=10},
                {name="ClothingStorageWinter", min=0, max=1, weightChance=10},
                {name="CrateComics", min=0, max=1, weightChance=20},
                {name="CrateComputer", min=0, max=1, weightChance=8},
                {name="CrateConcrete", min=0, max=1, weightChance=20},
                {name="CrateDishes", min=0, max=1, weightChance=40},
                {name="CrateElectronics", min=0, max=1, weightChance=40},
                {name="CrateEmptyBottles1", min=0, max=1, weightChance=100},
                {name="CrateEmptyBottles2", min=0, max=1, weightChance=100},
                {name="CrateEmptyMixed", min=0, max=1, weightChance=100},
                {name="CrateEmptyTinCans", min=0, max=1, weightChance=100},
                {name="CrateFarming", min=0, max=1, weightChance=10},
                {name="CrateFertilizer", min=0, max=1, weightChance=20},
                {name="CrateFishing", min=0, max=1, weightChance=10},
                {name="CrateFitnessWeights", min=0, max=1, weightChance=20},
                {name="CrateFootwearRandom", min=0, max=1, weightChance=10},
                {name="CrateFoldingChairs", min=0, max=1, weightChance=10},
                {name="CrateGravelBags", min=0, max=1, weightChance=20},
                {name="CrateInstruments", min=0, max=1, weightChance=10},
                {name="CrateLinens", min=0, max=1, weightChance=10},
                {name="CrateLumber", min=0, max=1, weightChance=10},
                {name="CrateMagazines", min=0, max=1, weightChance=40},
                {name="CrateMechanics", min=0, max=1, weightChance=60},
                {name="CrateMetalwork", min=0, max=1, weightChance=10},
                {name="CrateNewspapers", min=0, max=1, weightChance=40},
                {name="CrateOfficeSupplies", min=0, max=1, weightChance=40},
                {name="CratePaint", min=0, max=1, weightChance=60},
                {name="CratePetSupplies", min=0, max=1, weightChance=10},
                {name="CratePlaster", min=0, max=1, weightChance=20},
                {name="CrateRandomJunk", min=0, max=1, weightChance=100},
                {name="CrateSports", min=0, max=1, weightChance=20},
                {name="CrateTailoring", min=0, max=1, weightChance=10},
                {name="CrateTools", min=0, max=1, weightChance=20},
                {name="CrateToys", min=0, max=1, weightChance=20},
                {name="CrateTV", min=0, max=1, weightChance=10},
            }
        }
    },
    
    gardenstore = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
                {name="GardenStoreMisc", min=0, max=99, weightChance=100},
            }
        },
        clothingrack = {
            procedural = true,
            procList = {
                {name="CampingStoreClothes", min=0, max=99},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="GardenStoreTools", min=0, max=99},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="GardenStoreTools", min=0, max=99},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="CrateFertilizer", min=0, max=12, weightChance=100},
                {name="CrateGravelBags", min=0, max=4, weightChance=40},
                {name="CrateSandBags", min=0, max=4, weightChance=40},
            }
        }
    },
    
    gasstorage = {
        isShop = true,
        crate = {
            procedural = true,
            procList = {
                {name="StoreShelfSnacks", min=0, max=99, weightChance=40},
                {name="StoreShelfDrinks", min=0, max=99, weightChance=40},
                {name="StoreShelfMechanics", min=0, max=99, weightChance=100},
                {name="StoreShelfMedical", min=0, max=99, weightChance=10},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="StoreShelfSnacks", min=0, max=99, weightChance=40},
                {name="StoreShelfDrinks", min=0, max=99, weightChance=40},
                {name="StoreShelfMechanics", min=1, max=99, weightChance=100},
                {name="StoreShelfMedical", min=0, max=99, weightChance=10},
            }
        }
    },
    
    gasstore = {
        isShop = true,
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeSnacks", min=0, max=8, weightChance=100},
                {name="FridgeSoda", min=0, max=8, weightChance=100},
                {name="FridgeWater", min=0, max=4, weightChance=40},
                {name="FridgeOther", min=0, max=4, weightChance=60},
            }
        },
        freezer = {
            rolls = 0,
            items = {

            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterTobacco", min=0, max=2, weightChance=40},
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="StoreShelfDrinks", min=0, max=4, weightChance=40},
                {name="StoreShelfSnacks", min=0, max=4, weightChance=40},
                {name="StoreShelfMedical", min=0, max=1, weightChance=10},
                {name="StoreShelfMechanics", min=0, max=4, weightChance=100},
            }
        }
    },
    
    generalstore = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterTobacco", min=0, max=2, weightChance=100},
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="GigamartBakingMisc", min=0, max=99, weightChance=40},
                {name="GigamartCannedFood", min=0, max=99, weightChance=100},
                {name="GigamartDryGoods", min=0, max=99, weightChance=60},
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeSnacks", min=0, max=2, weightChance=100},
                {name="FridgeSoda", min=0, max=4, weightChance=100},
                {name="FridgeWater", min=0, max=4, weightChance=100},
                {name="FridgeOther", min=0, max=2, weightChance=100},
            }
        },
        freezer = {
            rolls = 1,
            items = {

            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="GigamartTools", min=1, max=6, weightChance=100},
                {name="GigamartFarming", min=1, max=4, weightChance=100},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="GigamartBottles", min=0, max=6, weightChance=100},
                {name="GigamartCrisps", min=0, max=4, weightChance=100},
                {name="GigamartCandy", min=0, max=4, weightChance=100},
                {name="StoreShelfMechanics", min=0, max=2, weightChance=20},
                {name="StoreShelfMedical", min=0, max=2, weightChance=40},
                {name="GigamartBakingMisc", min=0, max=2, weightChance=60},
                {name="GigamartDryGoods", min=0, max=8, weightChance=60},
                {name="GigamartHousewares", min=0, max=2, weightChance=40},
                {name="GigamartCannedFood", min=0, max=8, weightChance=100},
                {name="GigamartSauce", min=0, max=1, weightChance=100},
                {name="GigamartToys", min=0, max=1, weightChance=40},
                {name="GigamartSchool", min=0, max=1, weightChance=40},
                {name="GigamartBedding", min=0, max=1, weightChance=40},
                {name="GigamartPots", min=0, max=2, weightChance=40},
                {name="GigamartLightbulb", min=0, max=1, weightChance=40},
                {name="GigamartHouseElectronics", min=0, max=1, weightChance=40},
            }
        }
    },
    
    generalstorestorage = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterTobacco", min=1, max=2, weightChance=100},
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="GigamartBakingMisc", min=0, max=99, weightChance=40},
                {name="GigamartCannedFood", min=0, max=99, weightChance=100},
                {name="GigamartDryGoods", min=0, max=99, weightChance=60},
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeSnacks", min=1, max=2, weightChance=100},
                {name="FridgeSoda", min=1, max=4, weightChance=100},
                {name="FridgeWater", min=1, max=4, weightChance=100},
                {name="FridgeOther", min=1, max=2, weightChance=100},
            }
        },
        freezer = {
            rolls = 1,
            items = {

            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="GigamartBottles", min=0, max=6, weightChance=100},
                {name="GigamartCrisps", min=0, max=4, weightChance=100},
                {name="GigamartCandy", min=0, max=4, weightChance=100},
                {name="StoreShelfMechanics", min=0, max=2, weightChance=20},
                {name="StoreShelfMedical", min=0, max=2, weightChance=40},
                {name="GigamartBakingMisc", min=0, max=2, weightChance=60},
                {name="GigamartDryGoods", min=0, max=8, weightChance=60},
                {name="GigamartHousewares", min=0, max=2, weightChance=40},
                {name="GigamartCannedFood", min=0, max=8, weightChance=100},
                {name="GigamartSauce", min=0, max=2, weightChance=100},
                {name="GigamartToys", min=0, max=1, weightChance=40},
                {name="GigamartSchool", min=0, max=1, weightChance=40},
                {name="GigamartBedding", min=0, max=1, weightChance=40},
                {name="GigamartPots", min=0, max=2, weightChance=40},
                {name="GigamartLightbulb", min=0, max=1, weightChance=40},
                {name="GigamartHouseElectronics", min=0, max=1, weightChance=40},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="GigamartTools", min=0, max=99, weightChance=100},
                {name="GigamartFarming", min=0, max=99, weightChance=100},
            }
        },

    },
    
    giftstorage = {
        isShop = true,
        crate = {
            procedural = true,
            procList = {
                {name="GigamartToys", min=1, max=99},
            }
        }
    },
    
    giftstore = {
        isShop = true,
        shelves = {
            procedural = true,
            procList = {
                {name="GigamartToys", min=1, max=99},
            }
        },
        displaycase = {
            procedural = true,
            procList = {
                {name="StoreDisplayWatches", min=1, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
                {name="GigamartToys", min=1, max=99, weightChance=100},
            }
        }
    },
    
    gigamart = {
        isShop = true,
        grocerstand = {
            procedural = true,
            procList = {
                {name="GroceryStandVegetables1", min=1, max=10, weightChance=100},
                {name="GroceryStandVegetables2", min=1, max=10, weightChance=100},
                {name="GroceryStandFruits1", min=1, max=10, weightChance=100},
                {name="GroceryStandFruits2", min=1, max=10, weightChance=100},
                {name="GroceryStandFruits3", min=1, max=10, weightChance=100},
                {name="GroceryStandLettuce", min=1, max=4, weightChance=100},
            }
        },
        displaycasebutcher = {
            procedural = true,
            procList = {
                {name="ButcherChops", min=1, max=4, weightChance=100},
                {name="ButcherGround", min=1, max=2, weightChance=100},
                {name="ButcherChicken", min=1, max=1, weightChance=100},
                {name="ButcherSmoked", min=1, max=4, weightChance=100},
                {name="ButcherFish", min=0, max=1, weightChance=100},
            }
        },
        displaycasebakery = {
            procedural = true,
            procList = {
                {name="BakeryBread", min=1, max=2, weightChance=100},
                {name="BakeryPie", min=1, max=2, weightChance=100},
                {name="BakeryCake", min=1, max=2, weightChance=100},
                {name="BakeryMisc", min=0, max=4, weightChance=100},
            }
        },
        freezer = {
            rolls = 0,
            items = {

            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeSnacks", min=0, max=8, weightChance=100},
                {name="FridgeSoda", min=0, max=8, weightChance=100},
                {name="FridgeWater", min=0, max=4, weightChance=40},
                {name="FridgeOther", min=0, max=4, weightChance=60},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="GigamartBottles", min=2, max=6, weightChance=100},
                {name="GigamartCrisps", min=2, max=4, weightChance=100},
                {name="GigamartCandy", min=1, max=4, weightChance=100},
                {name="GigamartBakingMisc", min=1, max=4, weightChance=100},
                {name="GigamartDryGoods", min=2, max=16, weightChance=100},
                {name="GigamartHousewares", min=1, max=4, weightChance=100},
                {name="GigamartCannedFood", min=2, max=16, weightChance=100},
                {name="GigamartSauce", min=1, max=2, weightChance=100},
                {name="GigamartToys", min=0, max=2, weightChance=100},
                {name="GigamartTools", min=1, max=2, weightChance=100},
                {name="GigamartSchool", min=0, max=2, weightChance=100},
                {name="GigamartBedding", min=0, max=2, weightChance=100},
                {name="GigamartPots", min=1, max=3, weightChance=100},
                {name="GigamartFarming", min=1, max=3, weightChance=100},
                {name="GigamartLightbulb", min=1, max=1, weightChance=100},
                {name="GigamartHouseElectronics", min=1, max=2, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="GigamartCrisps", min=0, max=4, weightChance=100},
                {name="GigamartCandy", min=0, max=4, weightChance=100},
                {name="GigamartCannedFood", min=0, max=16, weightChance=100},
                {name="GigamartSauce", min=0, max=2, weightChance=100},
            }
        }
    },
    
    gigamartkitchen = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreKitchenBaking", min=0, max=4, weightChance=100},
                {name="StoreKitchenButcher", min=0, max=2, weightChance=100},
                {name="StoreKitchenDishes", min=0, max=2, weightChance=100},
                {name="StoreKitchenPots", min=0, max=2, weightChance=100},
            }
        }
    },
    
    grocery = {
        isShop = true,
        grocerstand = {
            procedural = true,
            procList = {
                {name="GroceryStandVegetables1", min=1, max=10, weightChance=100},
                {name="GroceryStandVegetables2", min=1, max=10, weightChance=100},
                {name="GroceryStandFruits1", min=1, max=10, weightChance=100},
                {name="GroceryStandFruits2", min=1, max=10, weightChance=100},
                {name="GroceryStandFruits3", min=1, max=10, weightChance=100},
                {name="GroceryStandLettuce", min=1, max=2, weightChance=100},
            }
        },
        displaycasebakery = {
            procedural = true,
            procList = {
                {name="BakeryBread", min=1, max=2, weightChance=100},
                {name="BakeryPie", min=1, max=2, weightChance=100},
                {name="BakeryCake", min=1, max=2, weightChance=100},
                {name="BakeryMisc", min=0, max=4, weightChance=100},
            },
        },
        displaycasebutcher = {
            procedural = true,
            procList = {
                {name="ButcherChops", min=1, max=4, weightChance=100},
                {name="ButcherGround", min=1, max=2, weightChance=100},
                {name="ButcherChicken", min=1, max=1, weightChance=100},
                {name="ButcherSmoked", min=1, max=4, weightChance=100},
                {name="ButcherFish", min=0, max=1, weightChance=100},
            }
        },
        freezer = {
            rolls = 0,
            items = {

            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeSnacks", min=1, max=2, weightChance=100},
                {name="FridgeSoda", min=1, max=4, weightChance=100},
                {name="FridgeWater", min=1, max=4, weightChance=100},
                {name="FridgeOther", min=1, max=2, weightChance=100},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="GigamartBottles", min=1, max=6, weightChance=100},
                {name="GigamartCrisps", min=1, max=4, weightChance=100},
                {name="GigamartCandy", min=1, max=4, weightChance=100},
                {name="GigamartBakingMisc", min=1, max=6, weightChance=100},
                {name="GigamartDryGoods", min=1, max=16, weightChance=100},
                {name="GigamartCannedFood", min=1, max=16, weightChance=100},
                {name="GigamartSauce", min=1, max=2, weightChance=100},
            }
        },
        smallcrate = {
            procedural = true,
            procList = {
                {name="GigamartCannedFood", min=0, max=16, weightChance=100},
                {name="GigamartBakingMisc", min=0, max=6, weightChance=100},
                {name="GigamartDryGoods", min=0, max=16, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="GigamartCrisps", min=0, max=4, weightChance=100},
                {name="GigamartCandy", min=0, max=4, weightChance=100},
                {name="GigamartCannedFood", min=0, max=16, weightChance=100},
                {name="GigamartSauce", min=0, max=2, weightChance=100},
            }
        },
    },
    
    grocerystorage = {
        isShop = true,
        metal_shelves = {
            procedural = true,
            procList = {
                {name="GigamartBakingMisc", min=0, max=16, weightChance=100},
                {name="GigamartCannedFood", min=0, max=16, weightChance=100},
                {name="GigamartDryGoods", min=0, max=16, weightChance=100},
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeWater", min=1, max=12, weightChance=100},
                {name="FridgeOther", min=1, max=12, weightChance=100},
            }
        },
        freezer = {
            rolls = 0,
            items = {

            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="GigamartBakingMisc", min=0, max=16, weightChance=100},
                {name="GigamartCannedFood", min=0, max=16, weightChance=100},
                {name="GigamartDryGoods", min=0, max=16, weightChance=100},
            }
        }
    },
    
    gunstore = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="GunStoreCounter", min=0, max=99},
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=10},
            }
        },
        displaycase = {
            procedural = true,
            procList = {
                {name="GunStoreDisplayCase", min=0, max=99},
            },
            dontSpawnAmmo = true,
        },
        locker = {
            procedural = true,
            procList = {
                {name="GunStoreShelf", min=0, max=99, weightChance=20},
                {name="GunStoreAmmunition", min=0, max=99, weightChance=100},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="GunStoreShelf", min=0, max=99, weightChance=100},
                {name="GunStoreAmmunition", min=0, max=99, weightChance=40},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="GunStoreShelf", min=0, max=99, weightChance=100},
                {name="GunStoreAmmunition", min=0, max=99, weightChance=40},
            }
        },
        shelvesmag = {
            procedural = true,
            procList = {
                {name="GunStoreMagazineRack", min=0, max=99},
            }  
        }
    },
    
    gunstorestorage = {
        isShop = true,
        crate = {
            procedural = true,
            procList = {
                {name="GunStoreAmmunition", min=0, max=99, weightChance=100},
                {name="GunStoreDisplayCase", min=0, max=99, weightChance=20},
            },
            dontSpawnAmmo = true,
        },
        locker = {
            procedural = true,
            procList = {
                {name="GunStoreShelf", min=0, max=99, weightChance=20},
                {name="GunStoreAmmunition", min=0, max=99, weightChance=100},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="GunStoreShelf", min=0, max=99, weightChance=100},
                {name="GunStoreAmmunition", min=0, max=99, weightChance=20},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="GunStoreShelf", min=0, max=99, weightChance=100},
                {name="GunStoreAmmunition", min=0, max=99, weightChance=20},
            }
        }
    },
    
    hall = {
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="RandomFiller", min=0, max=99, weightChance=100},
            }
        },
        locker = {
            procedural = true,
            procList = {
                {name="SchoolLockers", min=0, max=99, forceForRooms="classroom"},
            }
        }
    },
    
    housewarestore = {
        isShop = true,
        shelves = {
            procedural = true,
            procList = {
                {name="GigamartHousewares", min=1, max=12, weightChance=100},
                {name="GigamartBedding", min=0, max=2, weightChance=100},
                {name="GigamartPots", min=1, max=6, weightChance=100},
                {name="GigamartLightbulb", min=1, max=2, weightChance=100},
                {name="GigamartHouseElectronics", min=1, max=2, weightChance=100},
            }
        }
    },
    
    hunting = {
        isShop = true,
        clothingrack = {
            procedural = true,
            procList = {
                {name="CampingStoreClothes", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=10},
                {name="CampingStoreBooks", min=0, max=4, weightChance=80},
                {name="CampingStoreLegwear", min=0, max=2, weightChance=60},
                {name="CampingStoreBackpacks", min=0, max=2, weightChance=40},
                {name="CampingStoreGear", min=0, max=4, weightChance=100},
                {name="FishingStoreGear", min=0, max=2, weightChance=20},
            }
        },
        displaycase = {
            procedural = true,
            procList = {
                {name="GunStoreDisplayCase", min=0, max=99},
            },
            dontSpawnAmmo = true,
        },
        locker = {
            procedural = true,
            procList = {
                {name="GunStoreDisplayCase", min=0, max=99, weightChance=20},
                {name="GunStoreAmmunition", min=0, max=99, weightChance=100},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="GunStoreShelf", min=0, max=99, weightChance=100},
                {name="GunStoreAmmunition", min=0, max=99, weightChance=40},
            }
        },
        shelvesmag = {
            procedural = true,
            procList = {
                {name="GunStoreMagazineRack", min=0, max=99},
            }  
        }
    },
    
    icecreamkitchen = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreKitchenDishes", min=1, max=2, weightChance=100},
                {name="StoreKitchenPots", min=1, max=2, weightChance=100},
            }
        },
        freezer = {
            procedural = true,
            procList = {
                {name="IceCreamKitchenFreezer", min=1, max=99},
            }
        },
        fridge = {
            rolls = 0,
            items = {

            }
        },
        displaycasebakery = {
            procedural = true,
            procList = {
                {name="IceCreamKitchenFreezer", min=1, max=99},
            }
        },
        restaurantdisplay = {
            procedural = true,
            procList = {
                {name="IceCreamKitchenFreezer", min=1, max=99},
            }
        }
    },
    
    janitor = {
        metal_shelves = {
            procedural = true,
            procList = {
                {name="JanitorTools", min=1, max=1, weightChance=100},
                {name="JanitorCleaning", min=1, max=1, weightChance=100},
                {name="JanitorChemicals", min=0, max=99, weightChance=100},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="JanitorMisc", min=1, max=1, weightChance=100},
                {name="JanitorTools", min=0, max=1, weightChance=100},
                {name="JanitorCleaning", min=0, max=1, weightChance=100},
                {name="JanitorChemicals", min=0, max=99, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="JanitorChemicals", min=0, max=99},
            }
        }
    },
    
    jayschicken_kitchen = {
        isShop = true,
        restaurantdisplay = {
            procedural = true,
            procList = {
                {name="ServingTrayChicken", min=1, max=99, weightChance=100},
                {name="ServingTrayFries", min=1, max=2, weightChance=100},
            }
        },
        freezer = {
            procedural = true,
            procList = {
                {name="JaysKitchenFreezer", min=0, max=99},
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="JaysKitchenFridge", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="JaysKitchenButcher", min=1, max=1, weightChance=100},
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreKitchenSauce", min=1, max=2, weightChance=100},
                {name="StoreKitchenDishes", min=1, max=2, weightChance=100},
                {name="StoreKitchenPots", min=1, max=2, weightChance=100},
                {name="StoreKitchenPotatoes", min=1, max=1, weightChance=100},
                {name="StoreKitchenBaking", min=1, max=1, weightChance=100},
            }
        }
    },
    
    jewelrystorage = {
        isShop = true,
        metal_shelves = {
            procedural = true,
            procList = {
                {name="JewelryStorageAll", min=0, max=99},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="JewelryStorageAll", min=0, max=99},
            }
        },
    },
    
    jewelrystore = {
        isShop = true,
        displaycase = {
            procedural = true,
            procList = {
                {name="JewelrySilver", min=1, max=7, weightChance=100},
                {name="JewelryGold", min=1, max=3, weightChance=100},
                {name="JewelryGems", min=1, max=2, weightChance=100},
                {name="JewelryWeddingRings", min=1, max=2, weightChance=100},
                {name="JewelryWrist", min=1, max=3, weightChance=100},
                {name="JewelryOthers", min=1, max=50, weightChance=100},
            }
        }
    },
    
    kitchen = {
        counter = {
            procedural = true,
            procList = {
                {name="KitchenDishes", min=1, max=1, weightChance=100},
                {name="KitchenPots", min=1, max=1, weightChance=100},
                {name="KitchenCannedFood", min=1, max=1, weightChance=100},
                {name="KitchenDryFood", min=0, max=1, weightChance=100},
                {name="KitchenBreakfast", min=0, max=1, weightChance=100},
                {name="KitchenBottles", min=0, max=1, weightChance=100},
                {name="KitchenRandom", min=0, max=1, weightChance=100},
            }
        },
    
        overhead = {
            procedural = true,
            procList = {
                {name="KitchenDishes", min=1, max=1, weightChance=100},
                {name="KitchenCannedFood", min=1, max=1, weightChance=100},
                {name="KitchenDryFood", min=0, max=1, weightChance=100},
                {name="KitchenBreakfast", min=0, max=1, weightChance=100},
                {name="KitchenBottles", min=0, max=1, weightChance=100},
                {name="KitchenBook", min=0, max=1, weightChance=100},
            }
        },
        
        shelves = {
            procedural = true,
            procList = {
                {name="KitchenDishes", min=1, max=1, weightChance=100},
                {name="KitchenDryFood", min=1, max=1, weightChance=100},
                {name="KitchenBook", min=0, max=1, forceForTiles="furniture_shelving_01_19;furniture_shelving_01_23;furniture_shelving_01_51;furniture_shelving_01_55"},
                {name="KitchenBottles", min=0, max=1, weightChance=100},
            }
        },
    },
    
    kitchen_crepe = {
        isShop = true,
        fridge = {
            procedural = true,
            procList = {
                {name="CrepeKitchenFridge", min=1, max=99},
            }
        },
        freezer = {
            rolls = 0,
            items = {

            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreKitchenDishes", min=1, max=2, weightChance=100},
                {name="StoreKitchenPots", min=1, max=2, weightChance=100},
                {name="StoreKitchenBaking", min=1, max=4, weightChance=100},
            }
        }
    },
    
    laundry = {
        clothingdryer = {
            procedural = true,
            procList = {
                {name="LaundryLoadEmpty", min=0, max=99, weightChance=100},
                {name="LaundryLoad1", min=0, max=2, weightChance=60},
                {name="LaundryLoad2", min=0, max=2, weightChance=60},
                {name="LaundryLoad3", min=0, max=2, weightChance=20},
                {name="LaundryLoad4", min=0, max=2, weightChance=40},
                {name="LaundryLoad5", min=0, max=2, weightChance=20},
                {name="LaundryLoad6", min=0, max=12, weightChance=10},
                {name="LaundryLoad7", min=0, max=2, weightChance=40},
                {name="LaundryLoad8", min=0, max=2, weightChance=60},
            }
        },
        clothingdryerbasic = {
            procedural = true,
            procList = {
                {name="LaundryLoadEmpty", min=0, max=99, weightChance=100},
                {name="LaundryLoad1", min=0, max=2, weightChance=60},
                {name="LaundryLoad2", min=0, max=2, weightChance=60},
                {name="LaundryLoad3", min=0, max=2, weightChance=20},
                {name="LaundryLoad4", min=0, max=2, weightChance=40},
                {name="LaundryLoad5", min=0, max=2, weightChance=20},
                {name="LaundryLoad6", min=0, max=12, weightChance=10},
                {name="LaundryLoad7", min=0, max=2, weightChance=40},
                {name="LaundryLoad8", min=0, max=2, weightChance=60},
            }
        },
        clothingwasher = {
            procedural = true,
            procList = {
                {name="LaundryLoadEmpty", min=0, max=99, weightChance=100},
                {name="LaundryLoad1", min=0, max=2, weightChance=60},
                {name="LaundryLoad2", min=0, max=2, weightChance=60},
                {name="LaundryLoad3", min=0, max=2, weightChance=20},
                {name="LaundryLoad4", min=0, max=2, weightChance=40},
                {name="LaundryLoad5", min=0, max=2, weightChance=20},
                {name="LaundryLoad6", min=0, max=12, weightChance=10},
                {name="LaundryLoad7", min=0, max=2, weightChance=40},
                {name="LaundryLoad8", min=0, max=2, weightChance=60},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="LaundryCleaning", min=0, max=99, weightChance=100},
                {name="LaundryLoad1", min=0, max=2, weightChance=60},
                {name="LaundryLoad2", min=0, max=2, weightChance=60},
                {name="LaundryLoad3", min=0, max=2, weightChance=20},
                {name="LaundryLoad4", min=0, max=2, weightChance=40},
                {name="LaundryLoad5", min=0, max=2, weightChance=20},
                {name="LaundryLoad6", min=0, max=12, weightChance=10},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="LaundryCleaning", min=0, max=99},
            }
        },
    },
    
    library = {
        shelves = {
            procedural = true,
            procList = {
                {name="LibraryBooks", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="LibraryCounter", min=0, max=99},
            }
        }
    },
    
    liquorstore = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterTobacco", min=1, max=2, weightChance=100},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="StoreShelfWhiskey", min=1, max=12, weightChance=100},
                {name="StoreShelfWine", min=1, max=12, weightChance=100},
                {name="StoreShelfBeer", min=1, max=24, weightChance=100},
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeBeer", min=0, max=99},
            }
        },
        freezer = {
            rolls = 0,
            items = {
                
            }
        },
    },
    
    livingroom = {
        shelves = {
            procedural = true,
            procList = {
                {name="LivingRoomShelfNoTapes", min=0, max=99, weightChance=100},
                {name="LivingRoomShelf", min=0, max=99, forceForItems="appliances_television_01_0;appliances_television_01_1;appliances_television_01_2;appliances_television_01_3;appliances_television_01_4;appliances_television_01_5;appliances_television_01_6;appliances_television_01_7"},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="KitchenDishes", min=1, max=1, weightChance=100},
                {name="KitchenPots", min=1, max=1, weightChance=100},
                {name="KitchenCannedFood", min=1, max=1, weightChance=100},
                {name="KitchenDryFood", min=0, max=1, weightChance=100},
                {name="KitchenBreakfast", min=0, max=1, weightChance=100},
                {name="KitchenBottles", min=0, max=1, weightChance=100},
                {name="KitchenRandom", min=0, max=1, weightChance=100},
            }
        },
        overhead = {
            procedural = true,
            procList = {
                {name="KitchenDishes", min=1, max=1, weightChance=100},
                {name="KitchenCannedFood", min=1, max=1, weightChance=100},
                {name="KitchenDryFood", min=0, max=1, weightChance=100},
                {name="KitchenBreakfast", min=0, max=1, weightChance=100},
                {name="KitchenBottles", min=0, max=1, weightChance=100},
                {name="KitchenBook", min=0, max=1, weightChance=100},
            }
        }
    },
    
    lobby = {
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="RandomFiller", min=0, max=99, weightChance=100},
            }
        }
    },
    
    loggingfactory = {
        crate = {
            procedural = true,
            procList = {
                {name="CrateLumber", min=0, max=99},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="CrateLumber", min=0, max=99, weightChance=100},
            }
        },
    },
    
    loggingtruck = {
        crate = {
            procedural = true,
            procList = {
                {name="CrateLumber", min=0, max=99},
            }
        }
    },
    
    mechanic = {
        isShop = true,
        wardrobe = {
            procedural = true,
            procList = {
                {name="MechanicShelfOutfit", min=1, max=2, weightChance=100},
                {name="MechanicShelfMisc", min=1, max=2, weightChance=100},
                {name="MechanicShelfBooks", min=0, max=2, weightChance=100},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="MechanicShelfOutfit", min=1, max=2, weightChance=100},
                {name="MechanicShelfTools", min=1, max=4, weightChance=100},
                {name="MechanicShelfElectric", min=1, max=2, weightChance=100},
                {name="MechanicShelfMufflers", min=0, max=2, weightChance=100},
                {name="MechanicShelfBrakes", min=0, max=2, weightChance=100},
                {name="MechanicShelfSuspension", min=0, max=2, weightChance=100},
                {name="MechanicShelfWheels", min=1, max=6, weightChance=100},
                {name="MechanicShelfBooks", min=1, max=1, weightChance=100},
            }
        }
    },
    
    medclinic = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="MedicalClinicDrugs", min=1, max=4, weightChance=100},
                {name="MedicalClinicTools", min=1, max=2, weightChance=100},
                {name="MedicalClinicOutfit", min=1, max=2, weightChance=100},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="MedicalStorageDrugs", min=1, max=6, weightChance=100},
                {name="MedicalStorageTools", min=1, max=4, weightChance=100},
                {name="MedicalStorageOutfit", min=1, max=2, weightChance=100},
            }
        }
    },
    
    medical = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="MedicalClinicDrugs", min=1, max=4, weightChance=100},
                {name="MedicalClinicTools", min=1, max=2, weightChance=100},
                {name="MedicalClinicOutfit", min=1, max=2, weightChance=100},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="MedicalStorageDrugs", min=1, max=6, weightChance=100},
                {name="MedicalStorageTools", min=1, max=4, weightChance=100},
                {name="MedicalStorageOutfit", min=1, max=2, weightChance=100},
            }
        }
    },
    
    medicaloffice = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="MedicalClinicDrugs", min=1, max=4, weightChance=100},
                {name="MedicalClinicTools", min=1, max=2, weightChance=100},
                {name="MedicalClinicOutfit", min=1, max=2, weightChance=100},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="MedicalStorageDrugs", min=1, max=6, weightChance=100},
                {name="MedicalStorageTools", min=1, max=4, weightChance=100},
                {name="MedicalStorageOutfit", min=1, max=2, weightChance=100},
            }
        }
    },
    
    medicalstorage = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="MedicalClinicDrugs", min=1, max=4, weightChance=100},
                {name="MedicalClinicTools", min=1, max=2, weightChance=100},
                {name="MedicalClinicOutfit", min=1, max=2, weightChance=100},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="MedicalStorageDrugs", min=1, max=6, weightChance=100},
                {name="MedicalStorageTools", min=1, max=4, weightChance=100},
                {name="MedicalStorageOutfit", min=1, max=2, weightChance=100},
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeWater", min=0, max=12},
            }
        },
        freezer = {
            rolls = 0,
            items = {
            
            }
        },
    },
    
    motelroom = {
        bin = {
            rolls = 0,
            items = {
            
            },
        },
        dresser = {
            rolls = 0,
            items = {
            
            },
        },
        freezer = {
            rolls = 0,
            items = {
            
            },
        },
        fridge = {
            rolls = 0,
            items = {
            
            },
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="MotelLinens", min=0, max=99, weightChance=100},
                {name="MotelTowels", min=0, max=99, weightChance=100},
            }
        },
        sidetable = {
            rolls = 1,
            items = {
                "Book", 200,
            },
        },
        wardrobe = {
            procedural = true,
            procList = {
                {name="MotelLinens", min=0, max=1, weightChance=100},
                {name="MotelTowels", min=0, max=1, weightChance=100},
            }
        },
    },
    
    motelroomoccupied = {
        bin = {
            procedural = true,
            procList = {
                {name="BinGeneric", min=0, max=99},
            }
        },
        dresser = {
            rolls = 1,
            items = {
                "Bag_DuffelBagTINT", 0.5,
                "Bag_Schoolbag", 0.5,
                "Bag_NormalHikingBag", 0.2,
                "Bag_BigHikingBag", 0.2,
            }
        },
        freezer = {
            rolls = 1,
            items = {
                "IcePick", 0.01,
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="MotelFridge", min=1, max=1},
            }
        },
        sidetable = {
            rolls = 1,
            items = {
                "Book", 200,
                "Earbuds", 2,
                "Comb", 2,
                "Magazine", 2,
                "Newspaper", 2,
                "Notebook", 2,
                "ComicBook", 2,
                "Pencil", 2,
                "Pen", 2,
                "BluePen", 1,
                "RedPen", 1,
                "Pills", 1,
                "PillsBeta", 1,
                "PillsAntiDep", 1,
                "PillsVitamins", 1,
            }
        },
        wardrobe = {
            procedural = true,
            procList = {
                {name="MotelLinens", min=1, max=1, weightChance=100},
                {name="MotelTowels", min=1, max=1, weightChance=100},
            }
        },

    },
    
    movierental = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="MovieRentalShelves", min=0, max=99},
            }
        }
    },
    
    musicstore = {
        isShop = true,
        shelves = {
            procedural = true,
            procList = {
                {name="MusicStoreCDs", min=1, max=4, weightChance=100},
                {name="MusicStoreCases", min=1, max=2, weightChance=100},
                {name="MusicStoreAcoustic", min=1, max=6, weightChance=100},
                {name="MusicStoreBass", min=1, max=2, weightChance=100},
                {name="MusicStoreOthers", min=1, max=4, weightChance=100},
                {name="MusicStoreSpeaker", min=1, max=6, weightChance=100},
            }
        }
    },
    
    office = {
        crate = {
            procedural = true,
            procList = {
                {name="CrateOfficeSupplies", min=0, max=99},
            }
        },
        desk = {
            procedural = true,
            procList = {
                {name="OfficeDesk", min=0, max=99, weightChance=100},
                {name="PoliceDesk", min=0, max=99, forceForRooms="policestorage"},
            }
        },
        metal_shelves = {
            procedural= true,
            procList = {
                {name="OfficeShelfSupplies", min=0, max=99},
            }
        }
    },
    
    officestorage = {
        crate = {
            procedural = true,
            procList = {
                {name="CrateOfficeSupplies", min=0, max=99},
            }
        },
        metal_shelves = {
            procedural= true,
            procList = {
                {name="OfficeShelfSupplies", min=0, max=99},
            }
        }
    },
    
    optometrist = {
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBagsFancy", min=0, max=1, weightChance=100},
                {name="OptometristGlasses", min=1, max=99, weightChance=100},
            }
        }
    },
    
    pharmacy = {
        isShop = true,
        counter = {
            procedural = true,
            procList = {
                {name="MedicalClinicDrugs", min=0, max=99, weightChance=100},
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=10},
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeSnacks", min=0, max=2, weightChance=100},
                {name="FridgeSoda", min=0, max=6, weightChance=100},
                {name="FridgeWater", min=0, max=4, weightChance=100},
                {name="FridgeOther", min=0, max=2, weightChance=100},
            }
        },
        freezer = {
            rolls = 0,
            items = {
            
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="PharmacyCosmetics", min=1, max=4, weightChance=60},
                {name="StoreShelfDrinks", min=1, max=4, weightChance=40},
                {name="StoreShelfSnacks", min=1, max=4, weightChance=40},
                {name="StoreShelfMedical", min=4, max=24, weightChance=100},
            }
        }
    },
    
    pharmacystorage = {
        isShop = true,
        metal_shelves = {
            procedural = true,
            procList = {
                {name="MedicalStorageDrugs", min=1, max=6, weightChance=100},
                {name="MedicalStorageTools", min=1, max=4, weightChance=100},
                {name="MedicalStorageOutfit", min=1, max=2, weightChance=100},
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeWater", min=0, max=12},
            }
        },
        freezer = {
            rolls = 0,
            items = {
            
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="MedicalClinicDrugs", min=1, max=4, weightChance=100},
                {name="MedicalClinicTools", min=1, max=2, weightChance=100},
                {name="MedicalClinicOutfit", min=1, max=2, weightChance=100},
            }
        }
    },
    
    picnic = {
        crate = {
            rolls = 0,
            items = {
            
            }
        },
    },
    
    pizzakitchen = {
        isShop = true,
        fridge = {
            procedural = true,
            procList = {
                {name="PizzaKitchenFridge", min=0, max=99},
            }
        },
        freezer = {
            rolls = 0,
            items = {

            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreKitchenBaking", min=0, max=4, weightChance=100},
                {name="StoreKitchenDishes", min=0, max=2, weightChance=100},
                {name="StoreKitchenPots", min=0, max=2, weightChance=100},
                {name="PizzaKitchenSauce", min=0, max=2, weightChance=100},
                {name="PizzaKitchenCheese", min=0, max=2, weightChance=100},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="StoreKitchenBaking", min=0, max=99},
            }
        },
        displaycase = {
            procedural = true,
            procList = {
                {name="ServingTrayPizza", min=0, max=99},
            }
        },
        restaurantdisplay = {
            procedural = true,
            procList = {
                {name="ServingTrayPizza", min=0, max=99},
            }
        },
    },
    
    pizzawhirled = {
        isShop = true,
        wardrobe = {
            rolls = 0,
            items = {

            }
        },
        displaycase = {
            procedural = true,
            procList = {
                {name="ServingTrayPizza", min=0, max=99},
            }
        }
    },
    
    plazastore1 = {
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=100},
                {name="RandomFiller", min=0, max=99, weightChance=100},
            }
        }
    },
    
    policestorage = {
        locker = {
            procedural = true,
            procList = {
                {name="PoliceStorageGuns", min=0, max=99, forceForTiles="furniture_storage_02_8;furniture_storage_02_9;furniture_storage_02_10;furniture_storage_02_11"},
                {name="PoliceStorageOutfit", min=0, max=99, forceForTiles="furniture_storage_02_4;furniture_storage_02_5;furniture_storage_02_6;furniture_storage_02_7"},
            },
            dontSpawnAmmo = true,
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="PoliceStorageGuns", min=0, max=99, weightChance=100},
            },
            dontSpawnAmmo = true,
        }
    },
    
    post = {
        counter = {
            procedural = true,
            procList = {
                {name="PostOfficeSupplies", min=1, max=99, weightChance=100},
                {name="PostOfficeBoxes", min=1, max=4, weightChance=100},
            }
        },
    },
    
    poststorage = {
        metal_shelves = {
            procedural = true,
            procList = {
                {name="PostOfficeBoxes", min=1, max=2, weightChance=100},
                {name="PostOfficeNewspapers", min=0, max=2, weightChance=100},
                {name="PostOfficeMagazines", min=0, max=2, weightChance=100},
                {name="PostOfficeBooks", min=0, max=99, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="PostOfficeNewspapers", min=0, max=2, weightChance=100},
                {name="PostOfficeMagazines", min=0, max=2, weightChance=100},
                {name="PostOfficeBooks", min=0, max=99, weightChance=100},
            }
        },
    },
    
    prisoncells = {
        wardrobe = {
            procedural = true,
            procList = {
                {name="PrisonCellRandom", min=0, max=99},
            }
        }
    },
    
    restaurant = {
        counter = {
            rolls = 0,
            items = {

            }
        },
        displaycase = {
            procedural = true,
            procList = {
                {name="ServingTrayPizza", min=0, max=99, forceForRooms="pizzakitchen"},
            }
        },
        shelves = {
            rolls = 0,
            items = {

            }
        },
    },
    
    restaurantkitchen = {
        isShop = true,
        freezer = {
            procedural = true,
            procList = {
                {name="RestaurantKitchenFreezer", min=1, max=99},
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="RestaurantKitchenFridge", min=1, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreKitchenBaking", min=0, max=2, weightChance=100},
                {name="StoreKitchenButcher", min=0, max=1, weightChance=100},
                {name="StoreKitchenDishes", min=0, max=2, weightChance=100},
                {name="StoreKitchenPotatoes", min=0, max=2, weightChance=100},
                {name="StoreKitchenPots", min=0, max=2, weightChance=100},
                {name="StoreKitchenSauce", min=0, max=2, weightChance=100},
            }
        }
    },
    
    schoolstorage = {
        counter = {
            procedural = true,
            procList = {
                {name="JanitorMisc", min=1, max=1, weightChance=100},
                {name="JanitorTools", min=0, max=1, weightChance=100},
                {name="JanitorCleaning", min=0, max=1, weightChance=100},
                {name="JanitorChemicals", min=0, max=99, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="SportStorageBats", min=0, max=1, weightChance=100},
                {name="SportStorageBalls", min=0, max=1, weightChance=100},
                {name="SportStorageHelmets", min=0, max=1, weightChance=100},
                {name="SportStoragePaddles", min=0, max=1, weightChance=100},
                {name="SportStorageRacquets", min=0, max=2, weightChance=100},
                {name="SportStorageSticks", min=0, max=2, weightChance=100},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="ClassroomMisc", min=0, max=99},
            }
        },
        desk = {
            rolls = 0,
            items = {
                
            }
        }
    },
    
    security = {
        locker = {
            procedural = true,
            procList = {
                {name="PoliceStorageGuns", min=0, max=99},
            },
            dontSpawnAmmo = true,
        }
    },
    
    sewingstorage = {
        isShop = true,
        metal_shelves = {
            procedural = true,
            procList = {
                {name="SewingStoreTools", min=0, max=99, weightChance=100},
                {name="SewingStoreFabric", min=0, max=99, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="SewingStoreTools", min=0, max=99, weightChance=100},
                {name="SewingStoreFabric", min=0, max=99, weightChance=100},
            }
        }
    },
    
    sewingstore = {
        isShop = true,
        counter ={
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBagsFancy", min=0, max=1, weightChance=100},
                {name="BookstoreTailoring", min=1, max=2, weightChance=100},
                {name="SewingStoreTools", min=1, max=99, weightChance=100},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="SewingStoreTools", min=1, max=99, weightChance=100},
                {name="SewingStoreFabric", min=1, max=99, weightChance=100},
            }
        }
    },
    
    shed = {
        locker = {
            procedural = true,
            procList = {
                {name="GarageTools", min=0, max=99},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="GarageCarpentry", min=0, max=1, weightChance=100},
                {name="GarageTools", min=0, max=1, weightChance=100},
                {name="GarageMetalwork", min=0, max=1, weightChance=100},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="GarageCarpentry", min=0, max=2, weightChance=100},
                {name="GarageMetalwork", min=0, max=2, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="CrateConcrete", min=0, max=1, weightChance=100},
                {name="CrateFarming", min=0, max=1, weightChance=100},
                {name="CrateFertilizer", min=0, max=1, weightChance=100},
                {name="CrateGravelBags", min=0, max=1, weightChance=100},
                {name="CrateLumber", min=0, max=1, weightChance=100},
                {name="CrateMetalwork", min=0, max=1, weightChance=100},
                {name="CratePaint", min=0, max=1, weightChance=100},
                {name="CratePlaster", min=0, max=1, weightChance=100},
                {name="CrateTools", min=0, max=1, weightChance=100},
            }
        }
    },
    
    shoestore = {
        isShop = true,
        shelves = {
            procedural = true,
            procList = {
                {name="ClothingStoresBoots", min=0, max=99, weightChance=40},
                {name="ClothingStoresShoes", min=0, max=99, weightChance=100},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBagsFancy", min=0, max=1, weightChance=20},
                {name="ClothingStoresBoots", min=0, max=2, weightChance=20},
                {name="ClothingStoresShoes", min=0, max=4, weightChance=40},
                {name="ClothingStoresSocks", min=0, max=4, weightChance=100},
            }
        }
    },
    
    spiffoskitchen = {
        isShop = true,
        freezer = {
            procedural = true,
            procList = {
                {name="SpiffosKitchenFreezer", min=0, max=99},
            }
        },
        fridge = {
            procedural = true,
            procList = {
                {name="SpiffosKitchenFridge", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreKitchenBaking", min=0, max=2, weightChance=40},
                {name="BurgerKitchenButcher", min=0, max=2, weightChance=40},
                {name="StoreKitchenDishes", min=0, max=2, weightChance=100},
                {name="StoreKitchenPotatoes", min=0, max=2, weightChance=60},
                {name="StoreKitchenPots", min=0, max=2, weightChance=100},
                {name="StoreKitchenSauce", min=0, max=2, weightChance=80},
                {name="CounterKitchenSpiffo", min=0, max=1, weightChance=5},
            }
        }
    },
    
    spiffosstorage = {
        isShop = true,
        metal_shelves = {
            procedural = true,
            procList = {
                {name="StoreKitchenDishes", min=0, max=2, weightChance=100},
                {name="StoreKitchenPots", min=0, max=2, weightChance=100},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="StoreKitchenBaking", min=0, max=12, weightChance=100},
                {name="StoreKitchenSauce", min=0, max=12, weightChance=100},
            }
        }
    },
    
    sportstorage = {
        isShop = true,
        metal_shelves = {
            procedural = true,
            procList = {
                {name="SportStorageBats", min=0, max=2, weightChance=100},
                {name="SportStorageHelmets", min=0, max=2, weightChance=80},
                {name="SportStoragePaddles", min=0, max=1, weightChance=10},
                {name="SportStorageRacquets", min=0, max=4, weightChance=20},
                {name="SportStorageSticks", min=0, max=4, weightChance=80},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="SportsStoreSneakers", min=0, max=99, weightChance=100},
                {name="SportStorageBats", min=0, max=2, weightChance=100},
                {name="SportStorageHelmets", min=0, max=2, weightChance=80},
                {name="SportStoragePaddles", min=0, max=1, weightChance=10},
                {name="SportStorageRacquets", min=0, max=4, weightChance=20},
                {name="SportStorageSticks", min=0, max=4, weightChance=80},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="SportStorageBats", min=0, max=2, weightChance=100},
                {name="SportStorageBalls", min=0, max=2, weightChance=20},
                {name="SportStorageHelmets", min=0, max=2, weightChance=80},
                {name="SportStoragePaddles", min=0, max=1, weightChance=10},
                {name="SportStorageRacquets", min=0, max=4, weightChance=20},
                {name="SportStorageSticks", min=0, max=4, weightChance=80},
            }
        },
        clothingrack = {
            procedural = true,
            procList = {
                {name="ClothingStoresSport", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBagsFancy", min=0, max=1, weightChance=20},
                {name="ClothingStoresEyewear", min=0, max=2, weightChance=60},
                {name="ClothingStoresHeadwear", min=0, max=4, weightChance=100},
                {name="SportsStoreSneakers", min=0, max=99, weightChance=80},
            }
        }
    },
    
    sportstore = {
        isShop = true,
        shelves = {
            procedural = true,
            procList = {
                {name="SportsStoreSneakers", min=0, max=99, weightChance=100},
                {name="SportStorageBats", min=0, max=2, weightChance=100},
                {name="SportStorageHelmets", min=0, max=2, weightChance=80},
                {name="SportStoragePaddles", min=0, max=1, weightChance=10},
                {name="SportStorageRacquets", min=0, max=4, weightChance=20},
                {name="SportStorageSticks", min=0, max=4, weightChance=80},
            }
        },
        clothingrack = {
            procedural = true,
            procList = {
                {name="ClothingStoresSport", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBagsFancy", min=0, max=1, weightChance=20},
                {name="ClothingStoresEyewear", min=0, max=2, weightChance=60},
                {name="ClothingStoresHeadwear", min=0, max=4, weightChance=100},
                {name="SportsStoreSneakers", min=0, max=99, weightChance=80},
            }
        }
    },
    
    storageunit = {
        crate = {
            procedural = true,
            procList = {
                {name="CrateBooks", min=0, max=1, weightChance=10},
                {name="CrateCamping", min=0, max=1, weightChance=20},
                {name="CrateCanning", min=0, max=1, weightChance=4},
                {name="CrateClothesRandom", min=0, max=1, weightChance=20},
                {name="ClothingStorageWinter", min=0, max=1, weightChance=10},
                {name="CrateComics", min=0, max=1, weightChance=4},
                {name="CrateComputer", min=0, max=1, weightChance=8},
                {name="CrateDishes", min=0, max=1, weightChance=4},
                {name="CrateElectronics", min=0, max=1, weightChance=60},
                {name="CrateFarming", min=0, max=1, weightChance=20},
                {name="CrateFishing", min=0, max=1, weightChance=20},
                {name="CrateFitnessWeights", min=0, max=1, weightChance=10},
                {name="CrateFootwearRandom", min=0, max=1, weightChance=20},
                {name="CrateInstruments", min=0, max=1, weightChance=20},
                {name="CrateLinens", min=0, max=1, weightChance=20},
                {name="CrateMagazines", min=0, max=1, weightChance=10},
                {name="CrateMechanics", min=0, max=1, weightChance=20},
                {name="CrateMetalwork", min=0, max=1, weightChance=20},
                {name="CrateNewspapers", min=0, max=1, weightChance=10},
                {name="CrateOfficeSupplies", min=0, max=1, weightChance=20},
                {name="CratePaint", min=0, max=1, weightChance=60},
                {name="CratePetSupplies", min=0, max=1, weightChance=20},
                {name="CrateRandomJunk", min=0, max=1, weightChance=100},
                {name="CrateSports", min=0, max=1, weightChance=20},
                {name="CrateTailoring", min=0, max=1, weightChance=10},
                {name="CrateTools", min=0, max=1, weightChance=20},
                {name="CrateToys", min=0, max=1, weightChance=20},
                {name="CrateTV", min=0, max=1, weightChance=10},
            }
        },
        dresser = {
            rolls = 0,
            items = {
            
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="CrateBooks", min=0, max=1, weightChance=40},
                {name="CrateCamping", min=0, max=1, weightChance=10},
                {name="CrateCanning", min=0, max=1, weightChance=4},
                {name="CrateClothesRandom", min=0, max=1, weightChance=10},
                {name="ClothingStorageWinter", min=0, max=1, weightChance=10},
                {name="CrateComics", min=0, max=1, weightChance=20},
                {name="CrateDishes", min=0, max=1, weightChance=40},
                {name="CrateElectronics", min=0, max=1, weightChance=40},
                {name="CrateFarming", min=0, max=1, weightChance=10},
                {name="CrateFishing", min=0, max=1, weightChance=10},
                {name="CrateFootwearRandom", min=0, max=1, weightChance=10},
                {name="CrateInstruments", min=0, max=1, weightChance=10},
                {name="CrateLinens", min=0, max=1, weightChance=10},
                {name="CrateMagazines", min=0, max=1, weightChance=40},
                {name="CrateMechanics", min=0, max=1, weightChance=60},
                {name="CrateMetalwork", min=0, max=1, weightChance=10},
                {name="CrateNewspapers", min=0, max=1, weightChance=40},
                {name="CrateOfficeSupplies", min=0, max=1, weightChance=40},
                {name="CratePaint", min=0, max=1, weightChance=60},
                {name="CratePetSupplies", min=0, max=1, weightChance=10},
                {name="CrateRandomJunk", min=0, max=1, weightChance=100},
                {name="CrateSports", min=0, max=1, weightChance=20},
                {name="CrateTailoring", min=0, max=1, weightChance=10},
                {name="CrateTools", min=0, max=1, weightChance=20},
                {name="CrateToys", min=0, max=1, weightChance=20},
            }
        },
        wardrobe = {
            rolls = 0,
            items = {
            
            }
        },
        sidetable = {
            rolls = 0,
            items = {
            
            }
        },
    },
    
    theatre = {
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="RandomFiller", min=0, max=99, weightChance=100},
            }
        }
    },
    
    theatrekitchen = {
        isShop = true,
        freezer = {
            procedural = true,
            procList = {
                {name="TheatreKitchenFreezer", min=0, max=99},
            }
        },
        fridge = {
            rolls = 0,
            items = {

            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreKitchenDishes", min=0, max=2, weightChance=100},
                {name="StoreKitchenPots", min=0, max=2, weightChance=100},
            }
        }
    },
    
    theatrestorage = {
        crate = {
            procedural = true,
            procList = {
                {name="TheatrePopcorn", min=0, max=99, weightChance=100},
                {name="TheatreSnacks", min=0, max=99, weightChance=40},
                {name="TheatreDrinks", min=0, max=99, weightChance=60},
            }
        }
    },
    
    toolstorage = {
        isShop = true,
        crate = {
            procedural = true,
            procList = {
                {name="CrateConcrete", min=0, max=99, weightChance=40},
                {name="CrateLumber", min=0, max=99, weightChance=100},
                {name="CratePlaster", min=0, max=99, weightChance=80},
                {name="ToolStoreTools", min=0, max=99, weightChance=20},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="CrateConcrete", min=0, max=99, weightChance=40},
                {name="CrateLumber", min=0, max=99, weightChance=100},
                {name="CratePlaster", min=0, max=99, weightChance=80},
                {name="ToolStoreTools", min=0, max=99, weightChance=20},
                {name="ToolStoreFarming", min=0, max=99, weightChance=10},
                {name="ToolStoreCarpentry", min=0, max=99, weightChance=10},
                {name="ToolStoreMetalwork", min=0, max=99, weightChance=10},
            }
        }
    },
    
    toolstore = {
        isShop = true,
        clothingrack = {
            procedural = true,
            procList = {
                {name="ToolStoreOutfit", min=0, max=99},
            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="ToolStoreFootwear", min=0, max=2, weightChance=40},
                {name="ToolStoreBooks", min=0, max=2, weightChance=20},
                {name="ToolStoreAccessories", min=0, max=2, weightChance=80},
                {name="ToolStoreMisc", min=0, max=99, weightChance=100},
                {name="ToolStoreTools", min=0, max=99, weightChance=5},
            }
        },
        crate = {
            procedural = true,
            procList = {
                {name="CrateConcrete", min=0, max=99, weightChance=40},
                {name="CrateLumber", min=0, max=99, weightChance=100},
                {name="CratePlaster", min=0, max=99, weightChance=80},
                {name="ToolStoreTools", min=0, max=99, weightChance=20},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="CrateConcrete", min=0, max=99, weightChance=40},
                {name="CrateLumber", min=0, max=99, weightChance=100},
                {name="CratePlaster", min=0, max=99, weightChance=80},
                {name="ToolStoreTools", min=0, max=99, weightChance=20},
                {name="ToolStoreFarming", min=0, max=99, weightChance=10},
                {name="ToolStoreCarpentry", min=0, max=99, weightChance=10},
                {name="ToolStoreMetalwork", min=0, max=99, weightChance=10},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="FishingStoreGear", min=0, max=99, weightChance=10},
                {name="ToolStoreBooks", min=0, max=99, forceForTiles="furniture_shelving_01_40;furniture_shelving_01_41;furniture_shelving_01_42;furniture_shelving_01_43"},
                {name="ToolStoreCarpentry", min=0, max=99, weightChance=60},
                {name="ToolStoreFarming", min=0, max=99, weightChance=40},
                {name="ToolStoreMetalwork", min=0, max=99, weightChance=60},
                {name="ToolStoreMisc", min=0, max=99, weightChance=20},
                {name="ToolStoreTools", min=0, max=99, weightChance=100},
                {name="ToolStoreOutfit", min=0, max=99, weightChance=20},
            }
        }
    },
    
    toystore = {
        isShop = true,
        shelves = {
            procedural = true,
            procList = {
                {name="GigamartToys", min=0, max=99},
            }
        }
    },
    
    warehouse = {
        crate = {
            procedural = true,
            procList = {
                {name="CratePaint", min=0, max=50, weightChance=100},
                {name="CrateFarming", min=0, max=50, weightChance=100},
                {name="CrateTools", min=0, max=50, weightChance=100},
                {name="CrateMetalwork", min=0, max=50, weightChance=100},
                {name="CrateAntiqueStove", min=0, max=2, weightChance=100},
            }
        },
    },
    
    zippeestorage = {
        isShop = true,
        crate = {
            procedural = true,
            procList = {
                {name="StoreShelfSnacks", min=0, max=12, weightChance=100},
                {name="StoreShelfDrinks", min=0, max=12, weightChance=100},
            }
        },
        metal_shelves = {
            procedural = true,
            procList = {
                {name="StoreShelfSnacks", min=0, max=12, weightChance=100},
                {name="StoreShelfDrinks", min=0, max=12, weightChance=100},
            }
        }
    },
    
    zippeestore = {
        isShop = true,
        fridge = {
            procedural = true,
            procList = {
                {name="FridgeSnacks", min=0, max=8, weightChance=100},
                {name="FridgeSoda", min=0, max=8, weightChance=100},
                {name="FridgeWater", min=0, max=4, weightChance=40},
                {name="FridgeOther", min=0, max=4, weightChance=60},
            }
        },
        freezer = {
            rolls = 0,
            items = {

            }
        },
        counter = {
            procedural = true,
            procList = {
                {name="StoreCounterTobacco", min=0, max=2, weightChance=100},
                {name="StoreCounterCleaning", min=0, max=99, forceForTiles="location_shop_accessories_01_0;location_shop_accessories_01_1;location_shop_accessories_01_2;location_shop_accessories_01_3;location_shop_accessories_01_20;location_shop_accessories_01_21;location_shop_accessories_01_22;location_shop_accessories_01_23;fixtures_sinks_01_0;fixtures_sinks_01_1;fixtures_sinks_01_2;fixtures_sinks_01_3;fixtures_sinks_01_4;fixtures_sinks_01_5;fixtures_sinks_01_6;fixtures_sinks_01_7;fixtures_sinks_01_8;fixtures_sinks_01_9;fixtures_sinks_01_10;fixtures_sinks_01_11;fixtures_sinks_01_16;fixtures_sinks_01_17;fixtures_sinks_01_18;fixtures_sinks_01_19"},
                {name="StoreCounterBags", min=0, max=1, weightChance=20},
            }
        },
        shelves = {
            procedural = true,
            procList = {
                {name="StoreShelfDrinks", min=0, max=4, weightChance=100},
                {name="StoreShelfSnacks", min=0, max=4, weightChance=100},
                {name="StoreShelfMedical", min=0, max=1, weightChance=20},
                {name="StoreShelfMechanics", min=0, max=1, weightChance=10},
            }
        }
    },

-- =====================    
--    Bags/Containers   
-- =====================    

    Bag_BigHikingBag = {
        rolls = 2,
        items = {
            "Vest_DefaultTEXTURE_TINT", 2,
            "Tshirt_DefaultTEXTURE_TINT", 2,
            "Trousers_DefaultTEXTURE_TINT", 2,
            "Skirt_Knees", 0.6,
            "Skirt_Long", 0.6,
            "Skirt_Normal", 0.6,
            "Socks_Ankle", 2,
            "camping.CampingTentKit", 0.2,
        },
        fillRand = 1,
    },
    
    Bag_GolfBag = {
        rolls = 1,
        items = {
            "Golfclub", 100,
            "Golfclub", 2,
            "Golfclub", 2,
            "Golfclub", 2,
            "GolfBall", 100,
            "GolfBall", 10,
            "GolfBall", 10,
            "GolfBall", 10,
            "GolfBall", 10,
            "GolfBall", 10,
            "Gloves_LeatherGloves", 7,
            "Hat_GolfHatTINT", 3,
            "Hat_VisorBlack", 3,
            "Hat_VisorRed", 3,
            "Tshirt_PoloTINT", 10,
            "Shoes_Random", 10,
            "Trousers_SuitWhite", 10,
        },
        fillRand = 0,
    },
    
    Bag_DuffelBag = {
        rolls = 2,
        items = {
            "Hat_Sweatband",1,
            "Tshirt_Sport", 2,
            "Shorts_LongSport",2,
            "Shorts_ShortSport",2,
            "Tshirt_Sport", 2,
            "Shorts_LongSport",2,
            "Shorts_ShortSport",2,
            "Socks_Ankle", 2,
            "Hat_BaseballCap", 1,
        },
        fillRand = 1,
    },
    
    Bag_DuffelBagTINT = {
        rolls = 2,
        items = {
            "Hat_Sweatband",1,
            "Tshirt_Sport", 2,
            "Shorts_LongSport",2,
            "Shorts_ShortSport",2,
            "Tshirt_Sport", 2,
            "Shorts_LongSport",2,
            "Shorts_ShortSport",2,
            "Socks_Ankle", 2,
            "Hat_BaseballCap", 1,
        },
        fillRand = 1,
    },
    
    Bag_InmateEscapedBag = {
        rolls = 5,
        items = {
            "Rope", 15,
            "RippedSheets", 15,
            "Screwdriver", 3,
            "Hammer", 3,
            "DuctTape", 10,
        },
        fillRand = 0,
    },
    
    Bag_JanitorToolbox = {
        rolls = 1,
        items = {
            "PipeWrench", 50,
            "Wrench", 50,
            "HandTorch", 50,
            "Hammer", 50,
            "Saw", 50,
            "Screwdriver", 50,
            "Crowbar", 25,
            "DuctTape", 10,
            "Scotchtape", 10,
            "RippedSheets", 10,
            "RippedSheets", 10,
            "RippedSheetsDirty", 25,
            "RippedSheetsDirty", 25,
        },
        fillRand = 0,
    },
    
    Bag_MoneyBag = {
        rolls = 10,
        items = {
            "Money", 100,
            "Money", 100,
            "Money", 100,
            "Money", 100,
            "Money", 100,
            "Money", 100,
            "Money", 100,
            "Money", 100,
        },
        fillRand = 0,
    },
    
    Bag_NormalHikingBag = {
        rolls = 2,
        items = {
            "Vest_DefaultTEXTURE_TINT", 2,
            "Tshirt_DefaultTEXTURE_TINT", 2,
            "Trousers_DefaultTEXTURE_TINT", 2,
            "Skirt_Knees", 0.6,
            "Skirt_Long", 0.6,
            "Skirt_Normal", 0.6,
            "Socks_Ankle", 2,
        },
        fillRand = 1,
    },
    
    Bag_Schoolbag = {
        rolls = 3,
        items = {
            "Pen", 3,
            "BluePen", 3,
            "RedPen", 3,
            "Pen", 3,
            "Pencil", 3,
            "RubberBand", 3,
            "Pencil", 3,
            "Scissors", 2,
            "Cigarettes", 2,
            "Cube", 0.2,
            "Book", 3,
            "Crisps", 0.3,
            "Crisps2", 0.3,
            "Crisps3", 0.3,
            "Pop", 1,
        },
        fillRand = 0,
    },
    
    Bag_ShotgunBag = {
        rolls = 1,
        items = {
            "Shotgun", 100,
            "ShotgunShellsBox", 25,
            "ShotgunShellsBox", 25,
            "ShotgunShellsBox", 25,
            "ShotgunShellsBox", 25,
        },
        fillRand = 0,
    },
    
    Bag_ShotgunDblBag = {
        rolls = 1,
        items = {
            "DoubleBarrelShotgun", 100,
            "ShotgunShellsBox", 25,
            "ShotgunShellsBox", 25,
            "ShotgunShellsBox", 25,
            "ShotgunShellsBox", 25,
        },
        fillRand = 0,
    },
    
    Bag_ShotgunDblSawnoffBag = {
        rolls = 1,
        items = {
            "DoubleBarrelShotgunSawnoff", 100,
            "ShotgunShellsBox", 25,
            "ShotgunShellsBox", 25,
            "ShotgunShellsBox", 25,
            "ShotgunShellsBox", 25,
        },
        fillRand = 0,
    },
    
    Bag_ShotgunSawnoffBag = {
        rolls = 1,
        items = {
            "ShotgunSawnoff", 100,
            "ShotgunShellsBox", 25,
            "ShotgunShellsBox", 25,
            "ShotgunShellsBox", 25,
            "ShotgunShellsBox", 25,
        },
        fillRand = 0,
    },
    
    Bag_SurvivorBag = {
        rolls = 5,
        items = {
            "MuldraughMap", 30,
            "WestpointMap", 30,
            "MarchRidgeMap",30,
            "RosewoodMap",30,
            "RiversideMap",30,
            "Crisps",25,
            "Crisps2", 25,
            "Crisps3", 25,
            "Crisps4", 25,
            "Cereal", 25,
            "Dogfood", 25,
            "TVDinner",25,
            "TinnedSoup", 25,
            "TinnedBeans", 25,
            "CannedCornedBeef", 25,
            "Macandcheese", 25,
            "CannedChili", 25,
            "CannedBolognese", 25,
            "CannedCarrots2", 25,
            "CannedCorn", 25,
            "CannedMushroomSoup", 25,
            "CannedPeas", 25,
            "CannedPotato2", 25,
            "CannedSardines", 25,
            "CannedTomato2", 25,
            "Shotgun", 3,
            "DoubleBarrelShotgun", 1.5,
            "ShotgunShellsBox", 4,
            "ShotgunShellsBox", 4,
            "Machete", 4,
            "Bandage", 10,
            "Bandaid", 10,
            "FirstAidKit", 3,
            "SewingKit", 2,
        },
        -- only two map allowed
        maxMap = 2,
        -- this mean 90% chance on normal sandbox settings to have an annoted map
        stashChance = 10,
        fillRand = 0,
    },
    
    Bag_WeaponBag = {
        rolls = 3,
        items = {
            "Shotgun", 5,
            "DoubleBarrelShotgun", 3,
            "ShotgunShellsBox", 10,
            "ShotgunShellsBox", 10,
            "ShotgunShellsBox", 10,
            "Machete", 5,
            "Pistol", 2,
            "Pistol2", 2,
            "Pistol3", 1,
            "Revolver_Short", 1.5,
            "Revolver", 1,
            "Revolver_Long", 0.5,
            "Bullets9mmBox", 10,
            "Bullets9mmBox", 10,
            "Bullets9mmBox", 10,
            "Bullets38Box", 10,
            "Bullets44Box", 10,
            "Bullets45Box", 10,
            "BaseballBat", 8,
            "Crowbar", 7,
        },
        fillRand = 0,
    },
    
    Bag_WorkerBag = {
        rolls = 3,
        items = {
            "Pen", 3,
            "BluePen", 3,
            "RedPen", 3,
            "Pen", 3,
            "Pencil", 3,
            "RubberBand", 3,
            "Pencil", 3,
            "Scissors", 2,
            "Cigarettes", 2,
            "GrilledCheese", 2,
            "PeanutButterSandwich", 2,
            "Pop", 1,
            "WaterBottleFull", 1,
        },
        fillRand = 0,
    },
    
    Briefcase = {
        rolls = 4,
        items = {
            "Tshirt_DefaultTEXTURE_TINT", 3,
            "Jumper_RoundNeck", 1,
            "TrousersMesh_DenimLight", 1,
            "Trousers_DefaultTEXTURE_TINT", 2,
            "Vest_DefaultTEXTURE_TINT", 1,
            "Skirt_Knees", 0.3,
            "Skirt_Long", 0.3,
            "Skirt_Normal", 0.3,
        },
        fillRand = 1,
    },
    
    FirstAidKit = {
        rolls = 1,
        items = {
            "AlcoholWipes", 200,
            "AlcoholWipes", 30,
            "AlcoholWipes", 70,
            "Bandage", 10,
            "Bandage", 20,
            "Bandage", 200,
            "Bandaid", 200,
            "Bandaid", 200,
            "Bandaid", 200,
            "Bandaid", 70,
            "CottonBalls", 200,
            "CottonBalls", 30,
            "CottonBalls", 70,
            "Disinfectant", 100,
            "Gloves_Surgical", 5,
            "Scalpel", 5,
            "Scissors", 5,
            "SutureNeedle", 100,
            "SutureNeedle", 50,
            "SutureNeedleHolder", 50,
            "Tweezers", 100,
        },
        fillRand = 0,
    },
    
    Flightcase = {
        rolls = 1,
        items = {
            "GuitarAcoustic", 5,
            "GuitarElectricBlack", 5,
            "GuitarElectricBlue", 5,
            "GuitarElectricRed", 5,
            "GuitarElectricBassBlue", 5,
            "GuitarElectricBassBlack", 5,
            "GuitarElectricBassRed", 5,
        },
        fillRand = 0,
    },
    
    Garbagebag = {
        rolls = 0,
        items = {

        }
    },
    
    Guitarcase = {
        rolls = 1,
        items = {
            "GuitarAcoustic", 5,
            "GuitarElectricBlack", 5,
            "GuitarElectricBlue", 5,
            "GuitarElectricRed", 5,
            "GuitarElectricBassBlue", 5,
            "GuitarElectricBassBlack", 5,
            "GuitarElectricBassRed", 5,
        },
        fillRand = 0,
    },
    
    Handbag = {
        rolls = 1,
        items = {
            "Lipstick", 3,
            "Perfume", 2,
            "MakeupEyeshadow", 1,
            "MakeupFoundation", 1,
            "Necklace_Silver", 1,
            "NecklaceLong_Silver", 1,
            "Earring_LoopLrg_Silver", 1,
            "Earring_LoopMed_Silver", 1,
            "Earring_LoopSmall_Silver_Both", 1,
            "Earring_Stud_Silver", 1,
            "Ring_Left_RingFinger_Silver", 1,
            "Necklace_SilverCrucifix", 1,
            "Necklace_Gold", 0.5,
            "NecklaceLong_Gold", 0.5,
            "NoseStud_Gold", 0.5,
            "Earring_LoopLrg_Gold", 0.5,
            "Earring_LoopMed_Gold", 0.5,
            "Earring_LoopSmall_Gold_Both", 0.5,
            "Earring_Stud_Gold", 0.5,
            "Ring_Left_RingFinger_Gold", 0.5,
            "Earbuds", 3,
            "Locket", 3,
            "Comb", 3,
            "Magazine", 3,
            "CreditCard", 3,
        },
        fillRand = 0,
    },
    
    Lunchbox = {
        rolls = 4,
        items = {
            "Apple", 0.8,
            "Banana", 0.8,
            "Orange", 0.8,
            "Pop", 0.8,
            "Pop2", 0.8,
            "Pop3", 0.8,
            "Crisps", 0.5,
            "Crisps2", 0.5,
            "Crisps3", 0.5,
            "Crisps4", 0.5,
            "Chocolate", 1,
            "PeanutButterSandwich", 10,
            "Peanuts", 0.8,
            "SunflowerSeeds", 0.8,
            "CandyPackage", 0.2,
            "Cupcake", 1,
            "CookieJelly", 0.8,
            "CookieChocolateChip", 1,
            "BeefJerky", 1,
        }
    },
    
    Lunchbox2 = {
        rolls = 4,
        items = {
            "Apple", 0.8,
            "Banana", 0.8,
            "Orange", 0.8,
            "Pop", 0.8,
            "Pop2", 0.8,
            "Pop3", 0.8,
            "Crisps", 0.5,
            "Crisps2", 0.5,
            "Crisps3", 0.5,
            "Crisps4", 0.5,
            "Chocolate", 1,
            "CheeseSandwich", 10,
            "Peanuts", 0.8,
            "SunflowerSeeds", 0.8,
            "CandyPackage", 0.2,
            "Cupcake", 1,
            "CookieJelly", 0.8,
            "CookieChocolateChip", 1,
            "BeefJerky", 1,
        }
    },
    
    PistolCase1 = {
        rolls = 1,
        items = {
            "Pistol", 100,
            "9mmClip", 100,
        },
        fillRand = 0,
    },
    
    PistolCase2 = {
        rolls = 1,
        items = {
            "Pistol2", 100,
            "45Clip", 100,
        },
        fillRand = 0,
    },
    
    PistolCase3 = {
        rolls = 1,
        items = {
            "Pistol3", 100,
            "44Clip", 100,
        },
        fillRand = 0,
    },
    
    Plasticbag = {
        rolls = 0,
        items = {

        }
    },
    
    Purse = {
        rolls = 2,
        items = {
            "Lipstick", 3,
            "Perfume", 2,
            "MakeupEyeshadow", 1,
            "MakeupFoundation", 1,
            "Earbuds", 3,
            "Necklace_Silver", 1,
            "NecklaceLong_Silver", 1,
            "Earring_LoopLrg_Silver", 1,
            "Earring_LoopMed_Silver", 1,
            "Earring_LoopSmall_Silver_Both", 1,
            "Earring_Stud_Silver", 1,
            "Ring_Left_RingFinger_Silver", 1,
            "Necklace_SilverCrucifix", 1,
            "Necklace_Gold", 0.5,
            "NecklaceLong_Gold", 0.5,
            "NoseStud_Gold", 0.5,
            "Earring_LoopLrg_Gold", 0.5,
            "Earring_LoopMed_Gold", 0.5,
            "Earring_LoopSmall_Gold_Both", 0.5,
            "Earring_Stud_Gold", 0.5,
            "Ring_Left_RingFinger_Gold", 0.5,
            "Locket", 3,
            "Comb", 3,
            "Magazine", 3,
            "CreditCard", 3,
        },
        fillRand = 0,
    },
    
    Satchel = {
        rolls = 1,
        items = {
            "Apple", 17,
            "Banana", 17,
            "PeanutButterSandwich", 12,
            "Pop", 12,
            "WaterBottleFull", 15,
            "Crisps", 7,
            "Notebook", 10,
            "Doodle", 7,
            "Journal", 7,
            "Magazine", 7,
            "Pencil", 15,
        },
        fillRand = 0,
    },
    
    SeedBag = {
        rolls = 2,
        items = {
            "farming.GardeningSprayEmpty", 1,
            "TrapMouse", 0.5,
            "farming.HandShovel", 0.9,
            "FarmingMag1", 1,
            "BookFarming1", 1,
            "BookFarming2", 0.7,
            "farming.CarrotBagSeed", 5,
            "farming.BroccoliBagSeed", 5,
            "farming.RedRadishBagSeed", 5,
            "farming.StrewberrieBagSeed", 5,
            "farming.TomatoBagSeed", 5,
            "farming.PotatoBagSeed", 5,
            "farming.CabbageBagSeed", 5,
        },
        fillRand = 0,
    },
    
    SewingKit = {
        rolls = 1,
        items = {
            "Thread", 200,
            "Thread", 10,
            "Thread", 10,
            "Thread", 10,
            "Needle", 200,
            "Needle", 10,
            "Needle", 10,
            "Needle", 10,
            "Scissors", 8,
            "KnittingNeedles", 4,
            "Yarn", 4,
        },
        fillRand = 0,
    },
    
    Suitcase = {
        rolls = 4,
        items = {
            "Tshirt_DefaultTEXTURE_TINT", 3,
            "Jumper_RoundNeck", 1,
            "TrousersMesh_DenimLight", 1,
            "Trousers_DefaultTEXTURE_TINT", 2,
            "Vest_DefaultTEXTURE_TINT", 1,
            "Skirt_Knees", 0.3,
            "Skirt_Long", 0.3,
            "Skirt_Normal", 0.3,
        },
        fillRand = 1,
    },
    
    Toolbox = {
        rolls = 1,
        items = {
            "BallPeenHammer", 6,
            "ClubHammer", 4,
            "Crowbar", 4,
            "DuctTape", 8,
            "GardenSaw", 10,
            "Hammer", 8,
            "HandTorch", 10,
            "Nails", 10,
            "Nails", 10,
            "NailsBox", 10,
            "PipeWrench", 6,
            "Saw", 8,
            "Screwdriver", 10,
            "ScrewsBox", 8,
            "Twine", 10,
            "WoodenMallet", 4,
            "Woodglue", 4,
            "Wrench", 6,
        },
        fillRand = 0,
    },
    
    Tote = {
        rolls = 0,
        items = {

        }
    },
    
-- ===================
--  Profession Houses 
-- ===================    

    BandPractice = {
        crate = {
            rolls = 5,
            items = {
                "Disc_Retail", 10,
                "Disc_Retail", 10,
                "Radio.CDplayer", 2,
                "Earbuds", 4,
                "Speaker", 7,
                "Speaker", 7,
                "Headphones", 10,
                "Keytar", 2,
                "GuitarAcoustic", 2,
                "GuitarElectricBlack", 2,
                "GuitarElectricBlue", 2,
                "GuitarElectricRed", 2,
                "GuitarElectricBassBlue", 2,
                "GuitarElectricBassBlack", 2,
                "GuitarElectricBassRed", 2,
            }
        },
        
        metal_shelves = {
            rolls = 2,
            items = {
                "Disc_Retail", 10,
                "Disc_Retail", 10,
                "Radio.CDplayer", 2,
                "Earbuds", 4,
                "Speaker", 7,
                "Speaker", 7,
                "Headphones", 10,
                "Keytar", 2,
                "GuitarAcoustic", 2,
                "GuitarElectricBlack", 2,
                "GuitarElectricBlue", 2,
                "GuitarElectricRed", 2,
                "GuitarElectricBassBlue", 2,
                "GuitarElectricBassBlack", 2,
                "GuitarElectricBassRed", 2,
            }
        },
    },
    
    Carpenter = {
        counter = {
            rolls = 4,
            items = {
                "Hammer", 17,
                "NailsBox", 2,
                "NailsBox", 2,
                "Nails", 7,
                "Nails", 7,
                "Screwdriver", 5,
                "Toolbox", 3,
                "Saw", 10,
                "GardenSaw", 10,
                "Axe", 7,
                "WoodAxe", 7,
                "LeadPipe", 2,
                "HandAxe", 7,
                "PipeWrench", 2,
                "ClubHammer", 10,
                "WoodenMallet", 13,
                "DuctTape", 2,
                "Glue", 2,
                "Twine", 2,
                "Woodglue", 10,
                "BookCarpentry1", 4,
                "BookCarpentry2", 3,
                "BookCarpentry3", 2,
                "BookCarpentry4", 1,
                "BookCarpentry5", 0.6,
            }
        },
    },
    
    Chef = {
        counter = {
            rolls = 4,
            items = {
                "BookCooking1", 4,
                "BookCooking2", 3,
                "BookCooking3", 2,
                "BookCooking4", 1,
                "BookCooking5", 0.6,
            }
        },
    },
    
    Electrician = {
        counter = {
            rolls = 3,
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
                "Toolbox", 3,
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
        },
    },
    
    Farmer = {
        counter = {
            rolls = 4,
            items = {
                "farming.CarrotBagSeed", 10,
                "farming.BroccoliBagSeed", 10,
                "farming.RedRadishBagSeed", 10,
                "farming.StrewberrieBagSeed", 10,
                "farming.TomatoBagSeed", 10,
                "farming.PotatoBagSeed", 10,
                "farming.CabbageBagSeed", 10,
                "farming.HandShovel", 6,
                "LeafRake", 2,
                "GardenFork", 2,
                "Rake", 2,
                "HandScythe", 3,
                "HandFork", 3,
                "Shovel", 3,
                "Shovel2", 3,
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
                
            }
        },
    },
    
    Nurse = {
        medicine = {
            rolls = 3,
            items = {
                "Pills", 20,
                "PillsBeta", 20,
                "PillsAntiDep", 20,
                "PillsSleepingTablets", 20,
                "PillsVitamins", 20,
                "Bandage", 20,
                "Bandage", 20,
                "Bandaid", 20,
                "Bandaid", 20,
                "FirstAidKit", 2,
                "Tweezers", 10,
                "Disinfectant", 20,
                "AlcoholWipes", 20,
                "SutureNeedle", 10,
                "SutureNeedleHolder", 10,
                "Antibiotics", 10,
                "Scalpel", 10,
            }
        },
        
        counter = {
            rolls = 3,
            items = {
                "Pills", 20,
                "PillsBeta", 20,
                "PillsAntiDep", 20,
                "PillsSleepingTablets", 20,
                "PillsVitamins", 20,
                "Bandage", 20,
                "Bandage", 20,
                "Bandaid", 20,
                "Bandaid", 20,
                "FirstAidKit", 2,
                "Tweezers", 10,
                "Disinfectant", 20,
                "AlcoholWipes", 20,
                "SutureNeedle", 10,
                "SutureNeedleHolder", 10,
                "Antibiotics", 5,
                "Scalpel", 10,
            }
        },
    },

-- ================
--      Caches     
-- ================

    SafehouseLoot = {
        counter = {
            procedural = true,
            procList = {
                {name="KitchenCannedFood", min=1, max=7, weightChance=100},
                {name="KitchenDryFood", min=1, max=2, weightChance=100},
                {name="MeleeWeapons", min=1, max=2, weightChance=100},
                {name="FirearmWeapons", min=1, max=1, weightChance=100},
            },
        },
        
        medicine = {
            rolls = 3,
            items = {
                "Pills", 10,
                "PillsBeta", 10,
                "PillsAntiDep", 10,
                "PillsSleepingTablets", 10,
                "PillsVitamins", 10,
                "Bandage", 10,
                "Bandage", 10,
                "Bandaid", 10,
                "Bandaid", 10,
                "FirstAidKit", 2,
                "Tweezers", 5,
                "Disinfectant", 10,
                "AlcoholWipes", 5,
                "SutureNeedle", 5,
                "SutureNeedleHolder", 5,
                "Antibiotics", 5,
                "Scalpel", 5,
            }
        },
    },
    
    ShotgunCache1 = {
        ShotgunBox = {
            rolls = 2,
            items = {
                "Shotgun", 500,
                "ShotgunShells", 30,
                "ShotgunShells", 30,
                "ShotgunShells", 30,
                "ShotgunShells", 30,
                "ShotgunShellsBox", 20,
                "ShotgunShellsBox", 20,
                "ShotgunShellsBox", 20,
            }
        },
        
        Bag_DuffelBagTINT = {
            rolls = 2,
            items = {
                "Shotgun", 500,
                "ShotgunShells", 30,
                "ShotgunShells", 30,
                "ShotgunShells", 30,
                "ShotgunShells", 30,
                "ShotgunShellsBox", 20,
                "ShotgunShellsBox", 20,
                "ShotgunShellsBox", 20,
            },
            fillRand=1,
        },
    },
    
    ShotgunCache2 = {
        ShotgunBox = {
            rolls = 2,
            items = {
                "Shotgun", 500,
                "Shotgun", 5,
                "ShotgunShells", 30,
                "ShotgunShells", 30,
                "ShotgunShells", 30,
                "ShotgunShells", 30,
                "ShotgunShellsBox", 20,
                "ShotgunShellsBox", 20,
                "ShotgunShellsBox", 20,
                "ShotgunShells", 10,
                "ShotgunShells", 10,
                "ShotgunShells", 10,
                "ShotgunShells", 10,
                "ShotgunShellsBox", 8,
                "ShotgunShellsBox", 8,
                "ShotgunShellsBox", 8,
            }
        },
        
        counter = {
            rolls = 2,
            items = {
                "Shotgun", 8,
                "ShotgunShells", 8,
                "ShotgunShells", 8,
                "ShotgunShellsBox", 5,
            }
        },
    },
    
    ToolsCache1 = {
        ToolsBox = {
            rolls = 2,
            items = {
                "Nails", 30,
                "Nails", 30,
                "Hammer", 10,
                "Tarp", 10,
                "Saw", 10,
                "GardenSaw", 10,
                "Plank", 10,
                "Plank", 10,
                "Axe", 3,
                "WoodAxe", 3,
                "NailsBox", 4,
                "NailsBox", 4,
                "DuctTape", 8,
                "Glue", 8,
                "Scotchtape", 8,
                "Woodglue", 8,
                "Rope", 8,
                "LeadPipe", 10,
                "HandAxe", 5,
                "PipeWrench", 7,
                "ClubHammer", 7,
                "WoodenMallet", 7,
            },
        },
        
        counter = {
            rolls = 2,
            items = {
                "Nails", 10,
                "Nails", 10,
                "Hammer", 5,
                "Tarp", 5,
                "Saw", 5,
                "GardenSaw", 5,
                "Plank", 5,
                "Plank", 5,
                "Axe", 2,
                "WoodAxe", 2,
                "NailsBox", 4,
                "NailsBox", 4,
                "DuctTape", 3,
                "Glue", 3,
                "Scotchtape", 3,
                "Woodglue", 3,
                "Rope", 3,
                "Shovel", 0.5,
                "Shovel2", 0.5,
                "farming.HandShovel", 3,
                "HandScythe", 3,
                "HandFork", 3,
                "LeadPipe", 10,
                "HandAxe", 5,
                "PipeWrench", 7,
                "ClubHammer", 7,
                "WoodenMallet", 7,
            },
        },
        
        Bag_DuffelBagTINT = {
            rolls = 2,
            items = {
                "NailsBox", 2,
                "NailsBox", 2,
                "Hammer", 10,
                "Tarp", 10,
                "Saw", 10,
                "GardenSaw", 10,
                "Plank", 10,
                "Plank", 10,
                "Axe", 3,
                "WoodAxe", 3,
                "NailsBox", 2,
                "NailsBox", 2,
                "DuctTape", 8,
                "Glue", 8,
                "Scotchtape", 8,
                "Woodglue", 8,
                "Rope", 8,
                "LeadPipe", 10,
                "HandAxe", 5,
                "PipeWrench", 7,
                "ClubHammer", 7,
                "WoodenMallet", 7,
            },
            fillRand = 1,
        },
    },
    
    GunCache1 = {
        GunBox = {
            rolls = 2,
            items = {
                "Pistol", 50,
                "Pistol2", 50,
                "Pistol3", 5,
                "Revolver_Short", 3,
                "Revolver", 2,
                "Revolver_Long", 1,
                "Bullets9mm", 30,
                "Bullets9mm", 30,
                "Bullets9mm", 30,
                "Bullets9mmBox", 20,
                "Bullets9mmBox", 20,
                "Bullets9mmBox", 20,
                "Bullets9mmBox", 20,
                "Bullets38Box", 10,
                "Bullets44Box", 10,
                "Bullets45Box", 10,
                "Bullets38Box", 10,
                "Bullets44Box", 10,
                "Bullets45Box", 10,
            },
            dontSpawnAmmo = true,
        },
        
        counter = {
            rolls = 2,
            items = {
                "Pistol", 2,
                "Pistol2", 2,
                "Pistol3", 1,
                "Revolver_Short", 1.5,
                "Revolver", 1,
                "Revolver_Long", 0.5,
            },
        },
        
        Bag_DuffelBagTINT = {
            rolls = 2,
            items = {
                "Pistol", 50,
                "Pistol2", 50,
                "Pistol3", 5,
                "Revolver_Short", 5,
                "Revolver", 3,
                "Revolver_Long", 2,
                "Shotgun", 10,
                "DoubleBarrelShotgun", 7,
            },
            fillRand = 1,
        },
    },
    
    GunCache2 = {
        GunBox = {
            rolls = 2,
            items = {
                "Pistol", 50,
                "Pistol2", 50,
                "Pistol3", 5,
                "Revolver_Short", 3,
                "Revolver", 2,
                "Revolver_Long", 1,
                "Bullets9mm", 30,
                "Bullets9mm", 30,
                "Bullets9mm", 30,
                "Bullets9mmBox", 20,
                "Bullets9mmBox", 20,
                "Bullets9mmBox", 20,
                "Bullets9mmBox", 20,
                "Bullets9mm", 10,
                "Bullets9mm", 10,
                "Bullets9mm", 10,
                "Bullets9mmBox", 8,
                "Bullets9mmBox", 8,
                "Bullets9mmBox", 8,
                "Bullets9mmBox", 8,
            },
            dontSpawnAmmo = true,
        },
        
        Bag_DuffelBagTINT = {
            rolls = 2,
            items = {
                "Pistol", 50,
                "Pistol2", 50,
                "Pistol3", 5,
                "Revolver_Short", 3,
                "Revolver", 2,
                "Revolver_Long", 1,
                "Bullets9mm", 30,
                "Bullets9mm", 30,
                "Bullets9mm", 30,
                "Bullets9mmBox", 20,
                "Bullets9mmBox", 20,
                "Bullets9mmBox", 20,
                "Bullets9mmBox", 20,
                "Bullets9mm", 10,
                "Bullets9mm", 10,
                "Bullets9mm", 10,
                "Bullets9mmBox", 8,
                "Bullets9mmBox", 8,
                "Bullets9mmBox", 8,
                "Bullets9mmBox", 8,
                "Bullets38Box", 10,
                "Bullets44Box", 10,
                "Bullets45Box", 10,
                "Bullets38Box", 10,
                "Bullets44Box", 10,
                "Bullets45Box", 10,
            },
            fillRand=1,
        },
    },
    
    SurvivorCache1 = {
        counter = {
            procedural = true,
            procList = {
                {name="KitchenCannedFood", min=1, max=7, weightChance=100},
                {name="KitchenDryFood", min=1, max=2, weightChance=100},
                {name="MeleeWeapons", min=1, max=2, weightChance=100},
                {name="FirearmWeapons", min=1, max=1, weightChance=100},
            },
        },
        
        medicine = {
            rolls = 2,
            items = {
                "Pills", 10,
                "PillsBeta", 10,
                "PillsAntiDep", 10,
                "PillsSleepingTablets", 10,
                "PillsVitamins", 10,
                "Bandage", 10,
                "Bandage", 10,
                "Bandaid", 10,
                "Bandaid", 10,
                "FirstAidKit", 2,
                "Tweezers", 5,
                "Disinfectant", 10,
                "AlcoholWipes", 5,
                "SutureNeedle", 5,
                "SutureNeedleHolder", 5,
                "Antibiotics", 5,
            }
        },
        
        SurvivorCrate = {
            rolls = 2,
            items = {
                "Crisps",15,
                "Crisps2", 15,
                "Crisps3", 15,
                "Crisps4", 15,
                "Cereal", 15,
                "Dogfood", 15,
                "TVDinner",15,
                "TinnedSoup", 15,
                "TinnedBeans", 15,
                "CannedCornedBeef", 15,
                "Macandcheese", 15,
                "CannedChili", 15,
                "CannedBolognese", 15,
                "CannedCarrots2", 15,
                "CannedCorn", 15,
                "CannedMushroomSoup", 15,
                "CannedPeas", 15,
                "CannedPotato2", 15,
                "CannedSardines", 15,
                "CannedTomato2", 15,
                "Shotgun", 1,
                "DoubleBarrelShotgun", 0.5,
                "ShotgunShellsBox", 5,
                "ShotgunShellsBox", 5,
                "Machete", 1,
            }
        },
    },
    
    SurvivorCache2 = {
        counter = {
            procedural = true,
            procList = {
                {name="KitchenCannedFood", min=1, max=7, weightChance=100},
                {name="KitchenDryFood", min=1, max=2, weightChance=100},
                {name="MeleeWeapons", min=1, max=2, weightChance=100},
                {name="FirearmWeapons", min=1, max=1, weightChance=100},
            },
        },
        
        medicine = {
            rolls = 2,
            items = {
                "Pills", 10,
                "PillsBeta", 10,
                "PillsAntiDep", 10,
                "PillsSleepingTablets", 10,
                "PillsVitamins", 10,
                "Bandage", 10,
                "Bandage", 10,
                "Bandaid", 10,
                "Bandaid", 10,
                "FirstAidKit", 2,
                "Tweezers", 5,
                "Disinfectant", 10,
                "AlcoholWipes", 5,
                "SutureNeedle", 5,
                "SutureNeedleHolder", 5,
                "Antibiotics", 5,
            }
        },
        
        SurvivorCrate = {
            rolls = 2,
            items = {
                "Crisps",15,
                "Crisps2", 15,
                "Crisps3", 15,
                "Crisps4", 15,
                "Cereal", 15,
                "Dogfood", 15,
                "TVDinner",15,
                "TinnedSoup", 15,
                "TinnedBeans", 15,
                "CannedCornedBeef", 15,
                "Macandcheese", 15,
                "CannedChili", 15,
                "CannedBolognese", 15,
                "CannedCarrots2", 15,
                "CannedCorn", 15,
                "CannedMushroomSoup", 15,
                "CannedPeas", 15,
                "CannedPotato2", 15,
                "CannedSardines", 15,
                "CannedTomato2", 15,
                "Shotgun", 1,
                "DoubleBarrelShotgun", 0.5,
                "ShotgunShellsBox", 5,
                "ShotgunShellsBox", 5,
                "Machete", 1,
            }
        },
    },
}

table.insert(Distributions, 1, distributionTable);

--for mod compat:
SuburbsDistributions = distributionTable;
