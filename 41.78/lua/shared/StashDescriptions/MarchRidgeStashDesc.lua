--
-- Created by IntelliJ IDEA.
-- User: RJ
-- Date: 21/02/2017
-- Time: 15:26
-- To change this template use File | Settings | File Templates.
--

require "StashDescriptions/StashUtil";

-- guns
local stashMap = StashUtil.newStash("MarchRidgeStashMap1", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap");
stashMap.spawnOnlyOnZed = true;
--stashMap.daysToSpawn = "0-30";
--stashMap.zombies = 5
--stashMap.traps = "1-5";
--stashMap.barricades = 50;
stashMap.buildingX = 9885
stashMap.buildingY = 12647
stashMap.spawnTable = "GunCache2";
stashMap:addContainer("GunBox",nil,"Base.Bag_DuffelBagTINT",nil,nil,nil,nil);
stashMap:addStamp("X", nil, 9886, 12647, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap1_Text1", 9896, 12638, 0, 0, 0)
stashMap:addStamp("X", nil, 10125, 12788, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap1_Text2", 10087, 12794, 0, 0, 0)
stashMap:addStamp("ArrowWest", nil, 10028, 12648, 1, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap1_Text3", 10037, 12640, 1, 0, 0)

local stashMap = StashUtil.newStash("MarchRidgeStashMap2", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap");
stashMap.spawnOnlyOnZed = true;
stashMap.buildingX = 9926
stashMap.buildingY = 12676
stashMap.spawnTable = "GunCache1";
stashMap:addContainer("GunBox",nil,"Base.Bag_DuffelBagTINT",nil,nil,nil,nil);
stashMap:addStamp("X", nil, 9927, 12676, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap2_Text1", 9938, 12667, 0, 0, 0)
stashMap:addStamp("X", nil, 10075, 12783, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap2_Text2", 10047, 12791, 0, 0, 0)
stashMap:addStamp("Lightning", nil, 9910, 12893, 1, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap2_Text3", 9919, 12884, 1, 0, 0)

-- shotgun
local stashMap = StashUtil.newStash("MarchRidgeStashMap3", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap");
stashMap.spawnOnlyOnZed = true;
stashMap.traps = "1";
stashMap.buildingX = 9979
stashMap.buildingY = 12720
stashMap.spawnTable = "ShotgunCache2";
stashMap:addStamp("Target", nil, 9978, 12720, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap3_Text1", 9988, 12711, 0, 0, 0)
stashMap:addStamp("X", nil, 10074, 12805, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap3_Text2", 10054, 12813, 0, 0, 0)
stashMap:addStamp("Question", nil, 10309, 12684, 1, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap3_Text3", 10318, 12676, 1, 0, 0)

local stashMap = StashUtil.newStash("MarchRidgeStashMap4", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap");
stashMap.buildingX = 9886
stashMap.buildingY = 12748
stashMap.spawnTable = "ShotgunCache1";
stashMap:addContainer("ShotgunBox",nil,"Base.Bag_DuffelBagTINT",nil,nil,nil,nil);
stashMap:addStamp("X", nil, 9887, 12747, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap4_Text1", 9900, 12738, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap4_Text11", 9900, 12754, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap4_Text2", 9982, 12718, 0, 0, 1)
stashMap:addStamp(nil, "Stash_MarchRidgeMap4_Text3", 9813, 12817, 1, 0, 0)

-- tools
local stashMap = StashUtil.newStash("MarchRidgeStashMap5", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap");
stashMap.buildingX = 9970;
stashMap.buildingY = 12779;
stashMap.spawnTable = "ToolsCache1";
stashMap:addContainer("ToolsBox",nil,"Base.Bag_DuffelBagTINT",nil,nil,nil,nil);
stashMap:addStamp("Circle", nil, 9970, 12781, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap5_Text1", 9980, 12771, 0, 0, 0)
stashMap:addStamp("Exclamation", nil, 10162, 12754, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap5_Text2", 10170, 12743, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap5_Text3", 10245, 12656, 0, 0, 0)

local stashMap = StashUtil.newStash("MarchRidgeStashMap6", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap");
stashMap.spawnOnlyOnZed = true;
stashMap.buildingX = 9947;
stashMap.buildingY = 12832;
stashMap.spawnTable = "ToolsCache1";
stashMap:addStamp("Circle", nil, 9947, 12832, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap6_Text1", 9957, 12821, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap6_Text2", 10111, 12779, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap6_Text3", 10143, 12635, 0, 0, 0)

-- survivor houses
local stashMap = StashUtil.newStash("MarchRidgeStashMap7", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap");
stashMap.spawnOnlyOnZed = true;
stashMap.barricades = 50;
stashMap.buildingX = 9885
stashMap.buildingY = 12813
stashMap.spawnTable = "SurvivorCache1";
stashMap:addStamp("House", nil, 9885, 12811, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap7_Text1", 9897, 12803, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap7_Text2", 10111, 12777, 0, 0, 0)
stashMap:addStamp("ArrowSouth", nil, 9867, 12766, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap7_Text3", 9794, 12731, 0, 0, 0)

-- tool cache
local stashMap = StashUtil.newStash("MarchRidgeStashMap8", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap")
stashMap.buildingX = 9833
stashMap.buildingY = 13128
stashMap:addStamp("Target", nil, 9832, 13128, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap8_Text1", 9841, 13118, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap8_Text2", 9842, 13138, 0, 0, 0)
stashMap.spawnTable = "ToolsCache1"

-- survivor building/general caches
local stashMap = StashUtil.newStash("MarchRidgeStashMap9", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap")
stashMap.buildingX = 10092
stashMap.buildingY = 12619
stashMap:addStamp("X", nil, 10092, 12618, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap9_Text1", 10077, 12635, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap9_Text2", 10078, 12659, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap9_Text3", 10076, 12684, 0, 0, 0)
stashMap.spawnTable = "SurvivorCache1"

-- floorboard medical stash, beside church piano
local stashMap = StashUtil.newStash("MarchRidgeStashMap10", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap")
stashMap.buildingX = 10332
stashMap.buildingY = 12794
stashMap:addStamp("Cross", nil, 10330, 12795, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap10_Text1", 10255, 12731, 0, 0, 0)
stashMap:addStamp(nil, "Stash_MarchRidgeMap10_Text2", 10253, 12752, 0, 0, 0)
stashMap.spawnTable = "MedicalCache1"
stashMap:addContainer("MedicalBox", "floors_interior_tilesandwood_01_62", nil, nil, 10318, 12780, 0)

