--
-- Created by IntelliJ IDEA.
-- User: RJ
-- Date: 22/02/2017
-- Time: 11:30
-- To change this template use File | Settings | File Templates.
--

require "StashDescriptions/StashUtil";

-- guns
local stashMap1 = StashUtil.newStash("RosewoodStashMap1", "Map", "Base.RosewoodMap", "Stash_AnnotedMap");
--stashMap1.spawnOnlyOnZed = true;
--stashMap1.daysToSpawn = "0-30";
stashMap1.zombies = 5;
--stashMap1.traps = "1-5";
--stashMap1.barricades = 50;
stashMap1.buildingX = 8237;
stashMap1.buildingY = 11555;
stashMap1.spawnTable = "GunCache1";
stashMap1:addContainer("GunBox",nil,"Base.Duffelbag",nil,nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_target.png",nil,502,627,0,0,0);

-- shotgun
local stashMap1 = StashUtil.newStash("RosewoodStashMap2", "Map", "Base.RosewoodMap", "Stash_AnnotedMap");
stashMap1.buildingX = 8300;
stashMap1.buildingY = 11551;
stashMap1.spawnTable = "ShotgunCache1";
stashMap1:addContainer("ShotgunBox","floors_interior_tilesandwood_01_61",nil,"livingroom",nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,603,616,0,0,0);
stashMap1:addStamp(nil,"Stash_RosewoodStashMap2_Text1",625,613,0,0,0);
stashMap1:addStamp(nil,"Stash_RosewoodStashMap2_Text2",650,711,0,0,0);
stashMap1:addStamp(nil,"Stash_RosewoodStashMap2_Text3",224,783,1,0,0);

-- tools
local stashMap1 = StashUtil.newStash("RosewoodStashMap3", "Map", "Base.RosewoodMap", "Stash_AnnotedMap");
stashMap1.daysToSpawn = "0-30";
stashMap1.spawnOnlyOnZed = true;
stashMap1.zombies = 2;
stashMap1.buildingX = 8417;
stashMap1.buildingY = 11580;
stashMap1.spawnTable = "ToolsCache1";
stashMap1:addContainer("ToolsBox",nil,"Base.Duffelbag",nil,nil,nil,nil);
stashMap1:addStamp("media/ui/LootableMaps/map_o.png",nil,769,669,0,0,1);
stashMap1:addStamp(nil,"Stash_RosewoodStashMap3_Text1",500,633,0,0,1);
stashMap1:addStamp(nil,"Stash_RosewoodStashMap3_Text11",742,690,0,0,1);
stashMap1:addStamp("media/ui/LootableMaps/map_x.png",nil,345,907,0,0,0);
stashMap1:addStamp(nil,"Stash_RosewoodStashMap3_Text2",368,904,0,0,0);
stashMap1:addStamp("media/ui/LootableMaps/map_arrowsouth.png",nil,300,710,1,0,0);
stashMap1:addStamp(nil,"Stash_RosewoodStashMap3_Text3",260,669,1,0,0);