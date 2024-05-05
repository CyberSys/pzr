--
-- Created by IntelliJ IDEA.
-- User: RJ
-- Date: 07/02/2017
-- Time: 14:57
-- To change this template use File | Settings | File Templates.
--

require "StashDescriptions/StashUtil";

-- guns
local stashMap1 = StashUtil.newStash("MulStashMap1", "Map", "Base.MuldraughMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
--stashMap1.daysToSpawn = "0-30";
--stashMap1.zombies = 5
--stashMap1.traps = "1-5";
--stashMap1.barricades = 50;
stashMap1.buildingX = 10662;
stashMap1.buildingY = 9764;
stashMap1.spawnTable = "GunCache1";
stashMap1:addContainer("GunBox","floors_interior_tilesandwood_01_57",nil,"closet",nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,197,798,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap1_Text1",217,798,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap1_Text2",160,116,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap1_Text3",73,494,0,0,1);

local stashMap1 = StashUtil.newStash("MulStashMap2", "Map", "Base.MuldraughMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.zombies = 3
stashMap1.buildingX = 10874;
stashMap1.buildingY = 10190;
stashMap1.spawnTable = "GunCache1";
stashMap1:addContainer("GunBox",nil,"Base.Bag_DuffelBagTINT",nil,nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,514,1443,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap2_Text1",470,1467,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,153,228,0,0,1);
stashMap1:addStamp(nil,"Stash_MulMap2_Text2",175,228,0,0,1);
stashMap1:addStamp("media/ui/LootableMaps/map_o.png",nil,235,899,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap2_Text3",257,899,0,0,0);

local stashMap1 = StashUtil.newStash("MulStashMap11", "Map", "Base.MuldraughMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.buildingX = 10622;
stashMap1.buildingY = 9654;
stashMap1.spawnTable = "GunCache1";
stashMap1:addContainer("GunBox",nil,"Base.Bag_DuffelBagTINT",nil,nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,143,637,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap11_Text1",163,635,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_exclamation.png",nil,523,1208,0,0,1);
stashMap1:addStamp(nil,"Stash_MulMap11_Text2",490,1231,0,0,1);
stashMap1:addStamp("media/ui/LootableMaps/map_o.png",nil,235,899,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap11_Text3",257,899,0,0,0);

-- shotgun
local stashMap1 = StashUtil.newStash("MulStashMap3", "Map", "Base.MuldraughMap", "Stash_AnnotedMap");
stashMap1.buildingX = 10671;
stashMap1.buildingY = 10187;
stashMap1.spawnTable = "ShotgunCache1";
stashMap1:addContainer("ShotgunBox","floors_interior_tilesandwood_01_62",nil,"hall",nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,211,1429,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap3_Text1",235,1429,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap3_Text2",86,99,0,0,1);
stashMap1:addStamp("media/ui/LootableMaps/map_o.png",nil,349,1240,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap3_Text3",372,1240,0,0,0);

local stashMap1 = StashUtil.newStash("MulStashMap4", "Map", "Base.MuldraughMap", "Stash_AnnotedMap");
stashMap1.buildingX = 10760;
stashMap1.buildingY = 10083;
stashMap1.spawnTable = "ShotgunCache1";
stashMap1:addContainer("ShotgunBox","floors_interior_tilesandwood_01_62",nil,"bedroom",nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,344,1284,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap4_Text1",367,1281,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_exclamation.png",nil,523,1208,0,0,1);
stashMap1:addStamp(nil,"Stash_MulMap4_Text2",490,1231,0,0,1);
stashMap1:addStamp("media/ui/LootableMaps/map_skull.png",nil,204,1072,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap4_Text3",235,1069,0,0,0);

local stashMap1 = StashUtil.newStash("MulStashMap12", "Map", "Base.MuldraughMap", "Stash_AnnotedMap");
stashMap1.buildingX = 10619;
stashMap1.buildingY = 10529;
stashMap1.zombies = 2;
stashMap1.barricades = 50;
stashMap1.spawnTable = "ShotgunCache1";
stashMap1:addContainer("ShotgunBox","carpentry_01_16",nil,nil,nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,126,1953,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap12_Text1",146,1951,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_exclamation.png",nil,523,1208,0,0,1);
stashMap1:addStamp(nil,"Stash_MulMap12_Text2",490,1231,0,0,1);
stashMap1:addStamp("media/ui/LootableMaps/map_skull.png",nil,299,1840,1,0,0);
stashMap1:addStamp(nil,"Stash_MulMap12_Text3",320,1837,1,0,0);

-- tools
local stashMap1 = StashUtil.newStash("MulStashMap5", "Map", "Base.MuldraughMap", "Stash_AnnotedMap");
stashMap1.buildingX = 10873;
stashMap1.buildingY = 10078;
stashMap1.spawnTable = "ToolsCache1";
stashMap1:addContainer("ToolsBox","carpentry_01_16",nil,"kitchen",nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_o.png",nil,515,1284,0,0,0);

local stashMap1 = StashUtil.newStash("MulStashMap13", "Map", "Base.MuldraughMap", "Stash_AnnotedMap");
stashMap1.buildingX = 10689;
stashMap1.buildingY = 10359;
stashMap1.spawnOnlyOnZed = true;
stashMap1.barricades = 80;
stashMap1.spawnTable = "ToolsCache1";
stashMap1:addContainer("ToolsBox","carpentry_01_16",nil,nil,nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,234,1703,0,0,1);
stashMap1:addStamp(nil,"Stash_MulMap13_Text1",257,1700,0,0,1);
stashMap1:addStamp("media/ui/LootableMaps/map_exclamation.png",nil,255,1841,0,0,1);
stashMap1:addStamp(nil,"Stash_MulMap13_Text2",210,1868,0,0,1);
stashMap1:addStamp("media/ui/LootableMaps/map_skull.png",nil,299,1590,1,0,0);
stashMap1:addStamp(nil,"Stash_MulMap13_Text3",320,1587,1,0,0);


local stashMap1 = StashUtil.newStash("MulStashMap6", "Map", "Base.MuldraughMap", "Stash_AnnotedMap");
stashMap1.buildingX = 10698;
stashMap1.buildingY = 9524;
stashMap1.spawnOnlyOnZed = true;
stashMap1.spawnTable = "ToolsCache1";
stashMap1:addContainer("ToolsBox","carpentry_01_16",nil,nil,nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_o.png",nil,252,453,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap6_Text1",200,477,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_exclamation.png",nil,523,1208,0,0,1);
stashMap1:addStamp(nil,"Stash_MulMap6_Text2",490,1231,0,0,1);
stashMap1:addStamp(nil,"Stash_MulMap6_Text21",470,1253,0,0,1);
stashMap1:addStamp(nil,"Stash_MulMap6_Text3",434,1124,1,0,0);

-- survivor houses
local stashMap1 = StashUtil.newStash("MulStashMap7", "Map", "Base.MuldraughMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.barricades = 80;
stashMap1.buildingX = 10882;
stashMap1.buildingY = 9888;
stashMap1.spawnTable = "SurvivorCache1";
stashMap1:addStamp("media/ui/LootableMaps/map_house.png",nil,529,995,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap7_Text1",553,991,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap7_Text2",433,854,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_arroweast.png",nil,348,1242,1,0,0);
stashMap1:addStamp(nil,"Stash_MulMap7_Text3",372,1240,1,0,0);

local stashMap1 = StashUtil.newStash("MulStashMap8", "Map", "Base.MuldraughMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.zombies = 7;
stashMap1.buildingX = 10854;
stashMap1.buildingY = 9927;
stashMap1.spawnTable = "SurvivorCache1";
stashMap1:addStamp("media/ui/LootableMaps/map_house.png",nil,487,1048,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap8_Text1",510,1045,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_o.png",nil,175,1042,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap8_Text2",100,1070,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_arrowsouth.png",nil,91,796,1,0,0);
stashMap1:addStamp(nil,"Stash_MulMap8_Text3",20,750,1,0,0);

-- danger houses
local stashMap1 = StashUtil.newStash("MulStashMap9", "Map", "Base.MuldraughMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.zombies = 10;
stashMap1.buildingX = 10684;
stashMap1.buildingY = 9907;
stashMap1.spawnTable = "SurvivorCache1";
stashMap1:addStamp("media/ui/LootableMaps/map_skull.png",nil,225,1025,0,0,0);

local stashMap1 = StashUtil.newStash("MulStashMap10", "Map", "Base.MuldraughMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.zombies = 10;
stashMap1.buildingX = 10725;
stashMap1.buildingY = 9984;
stashMap1.spawnTable = "SurvivorCache1";
stashMap1:addStamp("media/ui/LootableMaps/map_skull.png",nil,289,1128,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap10_Text1",315,1125,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap10_Text2",111,642,0,0,0);
stashMap1:addStamp(nil,"Stash_MulMap10_Text3",335,949,0,0,0);
