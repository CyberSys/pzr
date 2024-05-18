--
-- Created by IntelliJ IDEA.
-- User: RJ
-- Date: 07/02/2017
-- Time: 14:57
-- To change this template use File | Settings | File Templates.
--

require "StashDescriptions/StashUtil";

-- guns
local stashMap1 = StashUtil.newStash("WpStashMap1", "Map", "Base.WestpointMap", "Stash_AnnotedMap");
--stashMap1.spawnOnlyOnZed = true;
--stashMap1.daysToSpawn = "0-30";
--stashMap1.zombies = 5
--stashMap1.traps = "1-5";
--stashMap1.barricades = 50;
stashMap1.buildingX = 10941;
stashMap1.buildingY = 6724;
stashMap1.spawnTable = "GunCache1";
stashMap1:addContainer("GunBox","carpentry_01_16",nil,nil,nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,208,358,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap1_Text1",228,358,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap1_Text2",900,492,1,0,0);

local stashMap1 = StashUtil.newStash("WpStashMap2", "Map", "Base.WestpointMap", "Stash_AnnotedMap");
stashMap1.barricades = 30;
stashMap1.buildingX = 11040;
stashMap1.buildingY = 6731;
stashMap1.spawnTable = "GunCache1";
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,361,367,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap2_Text1",330,387,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_o.png",nil,1696,463,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap2_Text2",1716,459,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_exclamation.png",nil,713,535,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap2_Text3",733,530,0,0,0);

-- shotgun
local stashMap1 = StashUtil.newStash("WpStashMap3", "Map", "Base.WestpointMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.zombies = 3;
stashMap1.buildingX = 11316;
stashMap1.buildingY = 6726;
stashMap1.spawnTable = "ShotgunCache1";
stashMap1:addStamp("media/ui/LootableMaps/map_target.png",nil,777,352,0,0,0);
stashMap1:addContainer(nil,nil,"Base.Duffelbag",nil,nil,nil,nil);

local stashMap1 = StashUtil.newStash("WpStashMap4", "Map", "Base.WestpointMap", "Stash_AnnotedMap");
stashMap1.barricades = 20;
stashMap1.buildingX = 11196;
stashMap1.buildingY = 6714;
stashMap1.spawnTable = "ShotgunCache1";
stashMap1:addStamp("media/ui/LootableMaps/map_target.png",nil,589,327,0.2,0.2,0.2);
stashMap1:addStamp(nil,"Stash_WpMap4_Text1",616,323,0.2,0.2,0.2);
stashMap1:addStamp(nil,"Stash_WpMap4_Text3",1404,648,1,0,0);
stashMap1:addStamp(nil,"Stash_WpMap4_Text31",1404,671,1,0,0);
stashMap1:addContainer(nil,nil,"Base.Duffelbag",nil,nil,nil,nil);

local stashMap1 = StashUtil.newStash("WpStashMap11", "Map", "Base.WestpointMap", "Stash_AnnotedMap");
stashMap1.barricades = 40;
stashMap1.buildingX = 11977;
stashMap1.buildingY = 6813;
stashMap1.spawnTable = "ShotgunCache1";
stashMap1:addStamp("media/ui/LootableMaps/map_target.png",nil,1762,477,0.2,0.2,0.2);
stashMap1:addStamp(nil,"Stash_WpMap11_Text1",1700,503,0.2,0.2,0.2);
stashMap1:addStamp(nil,"Stash_WpMap11_Text11",1703,529,0.2,0.2,0.2);
stashMap1:addStamp("media/ui/LootableMaps/map_o.png",nil,1755,627,0.2,0.2,0.2);
stashMap1:addStamp(nil,"Stash_WpMap11_Text2",1777,625,0.2,0.2,0.2);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,1606,395,0.2,0.2,0.2);
stashMap1:addStamp(nil,"Stash_WpMap11_Text3",1630,392,0.2,0.2,0.2);
stashMap1:addContainer(nil,nil,"Base.Duffelbag",nil,nil,nil,nil);

-- tools
local stashMap1 = StashUtil.newStash("WpStashMap5", "Map", "Base.WestpointMap", "Stash_AnnotedMap");
stashMap1.barricades = 30;
stashMap1.buildingX = 11438;
stashMap1.buildingY = 6733;
stashMap1.spawnTable = "ToolsCache1";
stashMap1:addStamp("media/ui/LootableMaps/map_o.png",nil,957,364,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap5_Text1",980,354,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap5_Text11",980,377,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap5_Text2",1282,891,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap5_Text3",932,438,0,0,0);
stashMap1:addContainer("ToolsBox","carpentry_01_16",nil,"bedroom",nil,nil,nil);

local stashMap1 = StashUtil.newStash("WpStashMap6", "Map", "Base.WestpointMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.zombies = 7;
stashMap1.buildingX = 11494;
stashMap1.buildingY = 6702;
stashMap1.spawnTable = "ToolsCache1";
stashMap1:addStamp("media/ui/LootableMaps/map_o.png",nil,1040,311,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap6_Text1",1000,335,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap6_Text2",1898,457,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap6_Text3",989,689,1,0,0);
stashMap1:addContainer("ToolsBox","carpentry_01_16",nil,nil,nil,nil,nil);

-- survivor houses
local stashMap1 = StashUtil.newStash("WpStashMap7", "Map", "Base.WestpointMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.buildingX = 11606;
stashMap1.buildingY = 6777;
stashMap1.spawnTable = "SurvivorCache1";
stashMap1:addStamp("media/ui/LootableMaps/map_house.png",nil,1203,429,0,0,0);

local stashMap1 = StashUtil.newStash("WpStashMap8", "Map", "Base.WestpointMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.zombies = 3;
stashMap1.barricades = 40;
stashMap1.buildingX = 11505;
stashMap1.buildingY = 6801;
stashMap1.spawnTable = "SurvivorCache1";
stashMap1:addStamp("media/ui/LootableMaps/map_house.png",nil,1055,471,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap8_Text1",1079,468,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_lightning.png",nil,1898,400,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap8_Text2",1920,387,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_exclamation.png",nil,1201,429,1,0,0);
stashMap1:addStamp(nil,"Stash_WpMap8_Text3",1228,425,1,0,0);

-- danger houses
local stashMap1 = StashUtil.newStash("WpStashMap9", "Map", "Base.WestpointMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.zombies = 20;
stashMap1.barricades = 40;
stashMap1.buildingX = 11654;
stashMap1.buildingY = 6815;
stashMap1.spawnTable = "SurvivorCache2";
stashMap1:addStamp("media/ui/LootableMaps/map_skull.png",nil,1274,486,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap9_Text1",1300,483,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap9_Text2",1773,585,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_question.png",nil,1892,458,1,0,0);
stashMap1:addStamp(nil,"Stash_WpMap9_Text3",1920,455,1,0,0);

local stashMap1 = StashUtil.newStash("WpStashMap10", "Map", "Base.WestpointMap", "Stash_AnnotedMap");
stashMap1.spawnOnlyOnZed = true;
stashMap1.traps = "1-3";
stashMap1.barricades = 30;
stashMap1.buildingX = 11644;
stashMap1.buildingY = 6694;
stashMap1.spawnTable = "SurvivorCache2";
stashMap1:addStamp("media/ui/LootableMaps/map_skull.png",nil,1260,297,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap10_Text1",1285,295,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,1731,628,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap10_Text2",1753,625,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_exclamation.png",nil,1470,601,0,0,0);
stashMap1:addStamp(nil,"Stash_WpMap10_Text3",1495,598,0,0,0);

local stashMap1 = StashUtil.newStash("WpStashMap12", "Map", "Base.WestpointMap", "Stash_AnnotedMap");
stashMap1.barricades = 80;
stashMap1.zombies = 10;
stashMap1.buildingX = 11979;
stashMap1.buildingY = 6944;
stashMap1.spawnTable = "SurvivorCache2";
stashMap1:addStamp("media/ui/LootableMaps/map_skull.png",nil,1767,669,1,0,0);
stashMap1:addStamp(nil,"Stash_WpMap12_Text1",1790,665,1,0,0);
stashMap1:addStamp(nil,"Stash_WpMap12_Text11",1790,693,1,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,1893,402,0.2,0.2,0.2);
stashMap1:addStamp(nil,"Stash_WpMap12_Text2",1916,400,0.2,0.2,0.2);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,2149,501,1,0,0);
stashMap1:addStamp(nil,"Stash_WpMap12_Text3",2171,499,1,0,0);