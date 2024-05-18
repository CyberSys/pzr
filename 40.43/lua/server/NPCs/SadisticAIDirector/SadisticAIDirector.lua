require "ISBaseObject"
require "Util/LuaList"

SadisticAIDirector = ISBaseObject:derive("SadisticAIDirector");

SadisticAIDirector.totalWorldNPCs = 0;

--************************************************************************--
--** SadisticAIDirector:initialise
--**
--************************************************************************--
function SadisticAIDirector:initialise()

end

function SadisticAIDirector:setup()

	for i = 0, self.buildings:size()-1 do
		local building = self.buildings:get(i);
		local btable = building:getTable();
		local metaChunk = getWorld():getMetaChunkFromTile(building:getX(), building:getY());

		local zombiePop = metaChunk:getZombieIntensity();
		btable.lootValue = zombiePop * 100;
		--print("Loot value "..zombiePop);
		if not btable.groups == nil and not btable.groups:isEmpty() then
			for i =0, btable.groups:size()-1 do
				local g = btable.groups:get(i);
				btable.lootValue = btable.lootValue + (10 * (g:size()));
			end
		end
	end
end

function SadisticAIDirector:tick()

	if self.first ~= true then
		self:setup();
		self.first = true;
	end

    SadisticMusicDirector.instance:tick();

	local size = 0;

	for i=0,self.groups:size()-1 do
		size = size + self.groups:get(i):size();
	end
	if size < SadisticAIDirector.totalWorldNPCs then
		----print("tick - need to add survivors");
		local building = self:getFreeLowZombieDensityBuilding();
		if building == nil then
			--print("no building found");
			return;
    	end

		self:addSurvivorGroup(building);
		----print("successfully added group");
	elseif SadisticAIDirector.totalWorldNPCs == 30 then
		SadisticAIDirector.totalWorldNPCs = 20;
	end


	for i=0, self.groups:size()-1 do
		local v = self.groups:get(i);
		if v == nil then
			return;
		end
		v:update();
	end

	EventSystem.instance:tick();

end

function SadisticAIDirector:getBuildingAt(x, y)
	for i = 0, self.buildings:size()-1 do
		local b = self.buildings:get(i);
        if(b:getX() <= x and b:getY() <= y and b:getX2() >= x and b:getY2() >= y)   then
            return b;
        end
	end

	return nil;
end

function SadisticAIDirector:getHighLootBuilding()
	local building = nil;
	local tab = nil;
	----print("looking for building");
	while building == nil or tab == nil or (tab ~= nil  and  (tab.lootValue == nil or tab.lootValue < 200)) do

		building = self.buildings:get(ZombRand(self.buildings:size()));
		if building ~= nil then

			--	--print(building);
			tab = building:getTable();

			--	--print(tab.metaChunk);
			--	--print(tab.groupCount);
			--	--print(tab.metaChunk:getZombieIntensity());
		else
			tab = nil;
			building = nil;
		end
	end
	--	if building ~= nil then
	--	print ("found building");
	--	end
	return building;
end

function SadisticAIDirector:getFreeLowZombieDensityBuilding()

	local building = nil;
	local tab = nil;
	----print("looking for building");
	while building == nil or tab == nil or (tab ~= nil and  tab.metaChunk ~= nil and (tab.metaChunk:getZombieIntensity() > 128 or tab.remoteness < 1 or tab.groups:size() > 0)) do
		----print("search building loop");

		building = self.buildings:get(ZombRand(self.buildings:size()));
	--	--print("got building");
		if building ~= nil then
		--	--print("get table");
		--	--print(building);
			tab = building:getTable();
		--	--print(tab.metaChunk);
		--	--print(tab.groupCount);
		--	--print(tab.metaChunk:getZombieIntensity());
		else
			tab = nil;
			building = nil;
		end
	end
--	if building ~= nil then
	--	print ("found building");
--	end
	return building;
end


function SadisticAIDirector:getRemotenessOfWorldPosition(x, y)

	local closestDist = 100000000000;
	for i=0, self.centrePoints:size() - 1 do
		local pos = self.centrePoints:get(i);
		local dist = IsoUtils.DistanceTo(pos.x, pos.y, x, y);
		if dist < closestDist then
			closestDist = dist;
		end
	end

	return closestDist / 300.0;
end


function SadisticAIDirector:initBuilding(building)
--	--print("inside init building");
	if(building == nil) then
		--print("err no building");
	end

	local buildingTable = building:getTable();
	--print(buildingTable);
	if(buildingTable.metaChunk == nil) then
	--	--print("setting buildingtable stuff");
		buildingTable.groups = LuaList:new();

	--	--print("getting metachunk");
		local metaChunk = getWorld():getMetaChunkFromTile(building:getX(), building:getY());
		if metaChunk == nil then
			--print("metachunk is nil");
			return;
		end
		buildingTable.metaChunk = metaChunk;
		buildingTable.remoteness = self:getRemotenessOfWorldPosition(building:getX(), building:getY());

		buildingTable.lootValue = 1;

	end
end

function SadisticAIDirector:addBuilding(building)
	----print(building);
	if(building == nil) then
		return;
	end
--	--print("adding building to ai director");
	self.buildings:add(building);
	if(building == nil) then
		--print("err no building");
	end
--	--print("init building");
	self:initBuilding(building);

end

function SadisticAIDirector:getOtherGroupBuilding(group)
	local i = ZombRand(0, self.groups:size());
	while(self.groups:get(i) == group ) do
		i = ZombRand(0, self.groups:size());
	end

	return self.groups:get(i).safehouse;
end

function SadisticAIDirector:addSurvivorGroup(building)

	--print("addSurvivorGroup");
	local x = building:getX();
	local y = building:getY();

	local survivorGroup = MetaSurvivorGroup:new(x, y, nil);
	survivorGroup.id = self.groups:size();
	self.groups:add(survivorGroup);
	-- start all new survivor groups in a safehouse.
	survivorGroup.safehouse = building;

	self:initBuilding(building);
    local n = ZombRand(1, 4);
 --   local n = ZombRand(8, 14);
    ----print("adding "..n.." members");
	for i=0,n-1 do

		if ZombRand(3)==0 then
			survivorGroup:addRandomMember(SurvivorType.Aggressive);
		elseif ZombRand(3)==0 then
			survivorGroup:addRandomMember(SurvivorType.Friendly);
		else
			survivorGroup:addRandomMember(SurvivorType.Neutral);
		end
	end
	----print("created members");
	local buildingTable = building:getTable();
	survivorGroup.building = building;

	buildingTable.groups:add(survivorGroup);
end

function SadisticAIDirector:kill(group)
	--print("killing group");
	if group.building ~= nil then
		local tab = group.building:getTable();
		--print("removing group from building.");

		tab.groups:remove(group);
	end
	--print("removing group from list.");
	if not group.subgroups:isEmpty() then
		for i=0, group.subgroups:size()-1 do
			group.subgroups:get(i).parent = nil;
		end
	end

	if not group.parent == nil then
	    --print("remove from parent");
		group.parent.subgroups:remove(group);
		group.parent= nil;
	end
	--print("removed from parent");
	self.groups:remove(group)
end

function SadisticAIDirector:update()

	local seconds = getGameTime():getGameWorldSecondsSinceLastUpdate();

	self.seconds  = self.seconds + seconds;
	if self.seconds > self.updateFrequencyInGameSeconds then
		self:tick();
		self.seconds = 0;
    end

    for i=0, self.buildings:size() - 1 do
        local bui = self.buildings:get(i):getTable();

        if bui.groups:size() > 1 then
           local a = bui.groups:get(0);
           local b = bui.groups:get(1);
           if(a:isSubgroup(b) or b:isSubgroup(a)) then
               break;
           end
           print("met in building");
           print(EventNPCGroupsMeetInBuilding)
           print(a);
           local inst = {}
           inst.other = b;
           print(inst);
           EventSystem.instance:queue(EventNPCGroupsMeetInBuilding, a, inst);
            break;
        end
    end
end

function SadisticAIDirector:new ()
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.seconds = 100;
	o.groups = LuaList:new();
	o.parent = nil;
	o.updateFrequencyInGameSeconds = 20;
	o.buildings = LuaList:new();
	o.centrePoints = LuaList:new();
	o.centrePoints:add({x=4.2*300, y=3.5*300});
	o.centrePoints:add({x=4.5*300, y=1.5*300});
	return o
end

SadisticAIDirector.instance = SadisticAIDirector:new();

SadisticAIDirector.run = function(ticks)
	SadisticAIDirector.instance:update();
end

SadisticAIDirector.onAddBuilding = function(building)
	SadisticAIDirector.instance:addBuilding(building);
end

SadisticAIDirector.onAddGroup = function(javaObject, x, y)
	local group = MetaSurvivorGroup:new(x, y, javaObject);
	javaObject:setLuaGroup(group);
	SadisticAIDirector.instance.groups:add(group);
end

function getSadisticAIDirector() return SadisticAIDirector.instance; end

--Events.OnTick.Add(SadisticAIDirector.run);
Events.OnAddBuilding.Add(SadisticAIDirector.onAddBuilding);
Events.OnNewSurvivorGroup.Add(SadisticAIDirector.onAddGroup);
