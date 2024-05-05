--
-- Created by IntelliJ IDEA.
-- User: RJ
-- Date: 21/02/2017
-- Time: 15:26
-- To change this template use File | Settings | File Templates.
--

require "StashDescriptions/StashUtil";

-- guns
local stashMap1 = StashUtil.newStash("MarchRidgeStashMap1", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
--stashMap1.daysToSpawn = "0-30";
--stashMap1.zombies = 5
--stashMap1.traps = "1-5";
--stashMap1.barricades = 50;
stashMap1.buildingX = 9882;
stashMap1.buildingY = 12646;
stashMap1.spawnTable = "GunCache2";
stashMap1:addContainer("GunBox",nil,"Base.Bag_DuffelBagTINT",nil,nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,343,281,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap1_Text1",366,278,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,705,474,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap1_Text2",650,496,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_arrowwest.png",nil,552,282,1,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap1_Text3",575,279,1,0,0);

local stashMap1 = StashUtil.newStash("MarchRidgeStashMap2", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.buildingX = 9926;
stashMap1.buildingY = 12676;
stashMap1.spawnTable = "GunCache1";
stashMap1:addContainer("GunBox",nil,"Base.Bag_DuffelBagTINT",nil,nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,401,317,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap2_Text1",423,314,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,622,484,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap2_Text2",590,506,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_lightning.png",nil,375,641,1,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap2_Text3",397,638,1,0,0);

-- shotgun
local stashMap1 = StashUtil.newStash("MarchRidgeStashMap3", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.traps = "1";
stashMap1.buildingX = 9979;
stashMap1.buildingY = 12720;
stashMap1.spawnTable = "ShotgunCache2";
stashMap1:addStamp("media/ui/LootableMaps/map_target.png",nil,480,374,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap3_Text1",502,372,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,620,507,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap3_Text2",600,529,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_question.png",nil,973,335,1,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap3_Text3",996,332,1,0,0);

local stashMap1 = StashUtil.newStash("MarchRidgeStashMap4", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap");
stashMap1.buildingX = 9885;
stashMap1.buildingY = 12748;
stashMap1.spawnTable = "ShotgunCache1";
stashMap1:addContainer("ShotgunBox",nil,"Base.Bag_DuffelBagTINT",nil,nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,346,428,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap4_Text1",368,426,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap4_Text11",368,449,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap4_Text2",492,395,0,0,1);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap4_Text3",238,544,1,0,0);

-- tools
local stashMap1 = StashUtil.newStash("MarchRidgeStashMap5", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap");
stashMap1.buildingX = 9970;
stashMap1.buildingY = 12779;
stashMap1.spawnTable = "ToolsCache1";
stashMap1:addContainer("ToolsBox",nil,"Base.Bag_DuffelBagTINT",nil,nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_o.png",nil,472,466,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap5_Text1",495,464,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_exclamation.png",nil,750,425,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap5_Text2",772,423,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap5_Text3",888,309,0,0,0);

local stashMap1 = StashUtil.newStash("MarchRidgeStashMap6", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.buildingX = 9944;
stashMap1.buildingY = 12830;
stashMap1.spawnTable = "ToolsCache1";
stashMap1:addStamp("media/ui/LootableMaps/map_o.png",nil,440,539,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap6_Text1",462,536,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap6_Text2",693,477,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap6_Text3",733,271,0,0,0);

-- survivor houses
local stashMap1 = StashUtil.newStash("MarchRidgeStashMap7", "Map", "Base.MarchRidgeMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.barricades = 50;
stashMap1.buildingX = 9884;
stashMap1.buildingY = 12812;
stashMap1.spawnTable = "SurvivorCache1";
stashMap1:addStamp("media/ui/LootableMaps/map_house.png",nil,348,518,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap7_Text1",370,515,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap7_Text2",693,477,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_arrowsouth.png",nil,318,459,0,0,0);
stashMap1:addStamp(nil,"Stash_MarchRidgeMap7_Text3",210,415,0,0,0);
