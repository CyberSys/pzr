require "ISBaseObject"

MetaSurvivorGroup = ISBaseObject:derive("MetaSurvivorGroup");

function MetaSurvivorGroup:initialise()

end

function MetaSurvivorGroup:removeMember(member)
	self.javaObject:removeMember(member);
	if(self.leader == member) then
		self.leader = nil;
	end

end

function MetaSurvivorGroup:addMember(member)
	self.javaObject:addMember(member);


end



function MetaSurvivorGroup:addRandomMember(type)
	local m = SurvivorFactory.CreateSurvivor(type);

	--print("adding member "..m:getForename().." "..m:getSurname());
	if self.members:isEmpty() then
		self.javaObject:setLeader(m);
	end
	self.javaObject:addMember(m);
end

function MetaSurvivorGroup:instansiate()

end

function MetaSurvivorGroup:removeRandomWithObservation(obs)
	local l = self:getAllWithObservation(obs);

	if l:isEmpty() then
		return nil;
	end

	local d= l:get(ZombRand(l:size()));
	self:removeMember(d);
	return d;
end

function MetaSurvivorGroup:removeRandomWithoutObservation(obs)
	local l = self:getAllWithoutObservation(obs);

	if l:isEmpty() then
		return nil;
	end

	local d= l:get(ZombRand(l:size()));

	self:removeMember(d);

	return d;
end
 
function MetaSurvivorGroup:killOff(percentDelta)
	local count = math.floor(self.members:size() * percentDelta);
	if(count == 0) then
		count = 1;
	end
	--print("killing off characters: "..count);

	local remainCount = self.members:size() - count;

	if(remainCount < 0) then
		remainCount = 0;
	end

	--print("remaining characters: "..remainCount);

	while self.members:size() > 0 and self.members:size() > remainCount do

		local dead = self.members:get(ZombRand(self.members:size()));
		if dead ~= nil then
			self.javaObject:removeMember(dead);
			if dead == self.leader then
				self:pickNewLeader();
			end
			--print("killed off "..dead:getForename().." "..dead:getSurname());
		end

	end

	if self.members:isEmpty() then
		--print("Group depleted, removing group from AI Director");
		SadisticAIDirector.instance:kill(self);
	end
end

function MetaSurvivorGroup:pickNewLeader()
	if self.members:isEmpty() then
		self.leader = nil;
		return;
	end

	-- for now make it random.
	local d= self.members:get(ZombRand(self.members:size()));

	self.leader = d;
	self.javaObject:setLeader(self.leader);
end

function MetaSurvivorGroup:countWithObservation(observation)
	local count = 0;

	for i=0, self.members:size()-1 do
		local member = self.members:get(i);
		if member:hasObservation(observation) then
			count = count + 1;
		end
	end

	return count;
end

function MetaSurvivorGroup:getRandomMembers(num)
	local list = LuaList:new();
	for i=0, self.members:size()-1 do
		local member = self.members:get(i);

		list:add(member);

	end

	while(list:size() > num) do
		list:remove(list:get(ZombRand(list:size())));
	end

	return list;
end

function MetaSurvivorGroup:getAllWithObservation(observation)
	local list = LuaList:new();

	for i=0, self.members:size()-1 do
		local member = self.members:get(i);
		if member:hasObservation(observation) then
			list:add(member);
		end
	end

	return list;
end

function MetaSurvivorGroup:getAllWithoutObservation(observation)
	local list = LuaList:new();

	for i=0, self.members:size()-1 do
		local member = self.members:get(i);
		if not member:hasObservation(observation) then
			list:add(member);
		end
	end

	return list;
end

function MetaSurvivorGroup:has(type)
	for i=0, self.members:size()-1 do
		local group = self.members:get(i);
		if group:getType() == type then
			return true;
		end
	end

	return false;
end

function MetaSurvivorGroup:getChunkX()
   local x = math.floor(self.x / 10);
   return x;
end

function MetaSurvivorGroup:getChunkY()
	local y = math.floor(self.y / 10);
	return y;
end

function MetaSurvivorGroup:isFlag(flag)

	if self.flags[flag] ~= nil then
		return true;
	end

	return false;

end

function MetaSurvivorGroup:setFlag(flag)

	self.flags[flag] = true;

end

function MetaSurvivorGroup:unsetFlag(flag)

	self.flags[flag] = nil;

end

function MetaSurvivorGroup:getX() return self.x; end
function MetaSurvivorGroup:getY() return self.y; end

function MetaSurvivorGroup:size()
	return self.members:size();
end

function MetaSurvivorGroup:hasChildren()
	return not self.subgroups:isEmpty();
end

function MetaSurvivorGroup:gotoBuildingOrder(building)
	--	--print("going to building");
	if building == nil then
		--print("passed null building");
		return;
	end
	self.itx = building:getX() + (building:getW() / 2);
	self.ity = building:getY() + (building:getH() / 2);
	--print("going to "..self.itx..", "..self.ity);
	--print("dif "..self.itx-self.x..", "..self.ity - self.y);
	self.targetbuilding = building;
	if self.building ~= nil then
		self.building:getTable().groups:remove(self);
		self.building = nil;
	end
	--print("going to building");
	self.tx = self.itx;
	self.ty = self.ity;
	self.javaObject:gotoBuildingOrder(building);
end

function MetaSurvivorGroup:onReachBuilding(event, inst)
	self.eventsOnReachBuilding:add({event=event, inst=inst});
	--print("adding trigger for onReachBuilding event "..event.name .. " " .. self.eventsOnReachBuilding:size().." events in queue.");
end

function MetaSurvivorGroup:gotoOrder(x, y)

	self.itx = x;
	self.ity = y;
	self.javaObject:gotoOrder(x, y);
end

function MetaSurvivorGroup:sizeIncludingSubGroups()
   local size = self:size();

	for i=0, self.subgroups:size()-1 do
		size = size + self.subgroups:get(i):size();
	end

	return size;
end
function MetaSurvivorGroup:addAll(group)
   self.javaObject:addAll(group.javaObject);
end
function MetaSurvivorGroup:rejoinParent()

	if self.parent == nil then
		return;
	end
	if (self.parent.building == self.building and self.building ~= nil)  or (math.abs(self.parent.x - self.x) < 20 and math.abs(self.parent.y - self.y) < 20) then
		--print("joining subgroup with parent")
        --print(self.members:size().." to absorb into "..self.parent.members:size())
        self.parent:addAll(self);
		self.parent.subgroups:remove(self);
		self.parent.loot = self.loot;

        --print("killing sub group")
        SadisticAIDirector.instance:kill(self);
	end

end

function MetaSurvivorGroup:absorb(group)

--	if group.x == self.x and group.y == self.y then
        if self.building ~= nil then
            self.building:getTable().groups:remove(group);
        end
        if group.parent ~= nil then
            group.parent.subgroups:remove(self);
        end
        self.loot = self.loot + group.loot;
        self:addAll(group);
		SadisticAIDirector.instance:kill(group);
--	end

end

function MetaSurvivorGroup:getTotalJoinedSize()
    local count = 0;

    if self.parent == nil then
        count = self:getTotalJoinedSizeBelow();
       return count;
    end

    local parent = self.parent;

    while parent.parent ~= nil do
        count = count + parent.members:size();
        parent = parent.parent;
    end

    return parent:getTotalJoinedSizeBelow();
end

function MetaSurvivorGroup:getTotalJoinedSizeBelow()
    local count = self.members:size();

    for i=0, self.subgroups:size() - 1 do
        count = count + self.subgroups:get(i):getTotalJoinedSizeBelow();
    end

    return count;
end

function MetaSurvivorGroup:splitSubgroup(membersToSplit)
	local sub = MetaSurvivorGroup:new(self.x, self.y);
	--print("created new group");
	sub.safehouse = self.safehouse;
	--print("set safehouse");
	SadisticAIDirector.instance.groups:add(sub);
	--print("added new group");
	sub.parent = self;
    sub.topmostid = self.topmostid;

    self.subgroups:add(sub);
	--print("added subgroup");
	--print(membersToSplit:size()-1);
	for i=0,membersToSplit:size()-1 do
		local mem = membersToSplit:get(i);
		--print(mem);
		self:removeMember(mem);
		--print("removed member");
		sub:addMember(mem);
		--print("added member");
	end
	return sub;
end


function MetaSurvivorGroup:splitGroup(membersToSplit)
	if membersToSplit:size() == self:size() then
		return self;
	end
	local sub = MetaSurvivorGroup:new(self.x, self.y);
	sub.safehouse = self.safehouse;
	SadisticAIDirector.instance.groups:add(sub);

	for i=0,membersToSplit:size()-1 do
		local mem = membersToSplit:get(i);
		self:removeMember(mem);
		sub:addMember(mem);
	end

	return sub;
end

function MetaSurvivorGroup:printFlags()

	for k, v in pairs(self.flags) do
		--print(k);
	end

end

function MetaSurvivorGroup:setSafehouse(building)
	self.safehouse = building;
end

function MetaSurvivorGroup:instanceGroup()

	--print("spawning group");


	local gs = getCell():getGridSquare(self.x, self.y, 0);

	if not gs:isSafeToSpawn() then
		return;
	end

	self.javaObject:instanceGroup(self.x, self.y);
	if self.tx == self.x and self.ty == self.y then
		self.itx = nil;
		self.ity = nil;
	else
		self.itx = self.tx;
		self.ity = self.ty;
		self.javaObject:gotoOrder(self.tx, self.ty);
	end

end

function MetaSurvivorGroup:deSpawn()
	--print("despawning group");
	self.javaObject:Despawn();
	if self.itx ~= nil then
		self.tx = self.itx;
		self.ty = self.ity;
	end
end

function MetaSurvivorGroup:getRemoteness()

	if self.building ~= nil and self.building:getTable().remoteness == nil then
		--print("error with remoteness");
		self.building:getTable().remoteness = 0;
	end

	if self.building ~= nil then
		return self.building:getTable().remoteness;
	end

	return SadisticAIDirector:getRemotenessOfWorldPosition(self.x, self.y);
end

function MetaSurvivorGroup:getNearbyZombieDensity()

	local metaChunk = getWorld():getMetaChunkFromTile(self:getX(), self:getY());
	-- more likely to happen in high zombie population areas
	if metaChunk == nil then
		return 0;
	end
	local zombiePop = metaChunk:getZombieIntensity();

	return zombiePop;
end

function MetaSurvivorGroup:getLeader()
	self.leader = self.javaObject:getLeader();
	return self.leader;
end

function MetaSurvivorGroup:isSubgroup(other)

    local test = self;

    if test.parent == nil then return false; end

    while(test.parent ~= nil) do
        if test.parent == other then return true; end
        test = test.parent;
    end

    return false;
end

function MetaSurvivorGroup:getSelector()
    return SurvivorSelector:new(self);
end

function MetaSurvivorGroup:setIdle(id)
    self.idle = id;
end

function MetaSurvivorGroup:isIdle()
    return self.idle;
end

function MetaSurvivorGroup:update()

	self.javaObject:update();


    if not self.eventqueuelist:isEmpty() then
        local a = self.eventqueuelist:get(0);

        if a.char == nil then
            EventSystem.instance:trigger(a.event, self, a.inst);
        else
            EventSystem.instance:ctrigger(a.event, self, a.inst, a.char);
        end
        self.eventqueuelist:remove(a);

    end


--    if not self.javaObject:isInstanced() then
		local player = getPlayer();
		local dist =  math.abs( self.x - player:getX() ) + math.abs( self.y - player:getY() );
--        print(dist);
        if dist < 30 then
			if not self.javaObject:isInstanced() then
				if getCell():getGridSquare(math.floor(self.x), math.floor(self.y), 0) ~= nil then
    				self:instanceGroup();
				end
			end
		elseif self.javaObject:isInstanced() then
			print(dist);
		    self:deSpawn();
		end
--	end

	if self.members:isEmpty() then
		--print("No members in group. Deleting");
		SadisticAIDirector.instance:kill(self);
		return;
	end

	self.leader = self.javaObject:getLeader();


	if self.leader == nil then
		return;
	end
	if self.javaObject:isInstanced() then

		self.x = self.leader:getInstance():getX();
		self.y = self.leader:getInstance():getY();
		if self.itx == nil then
			self.tx = self.x;
			self.ty = self.y;
			moved = true;
		else
			self.tx = self.itx;
			self.ty = self.ity;
		end
	end

    for i=0, self.members:size() - 1 do
       self.members:get(i):getMeta():update();
    end
-- 20 seconds has passed
	local updateFrequency = SadisticAIDirector.instance.updateFrequencyInGameSeconds;

	local moved = false;
	-- into days
    updateFrequency = updateFrequency / 60.0;
	updateFrequency = updateFrequency / 60.0;
	updateFrequency = updateFrequency / 24.0;

    local gx = self.tx - self.x;
    local gy = self.ty - self.y;
    gx, gy = Vector2.normalize(gx, gy);

    -- ugly hack to get npc group walk speeds about right...
    gx = gx * 0.8;
    gy = gy * 0.8;
    self.x = self.x + gx;
    self.y = self.y + gy;

    if(gx ~= 0 or gy ~= 0) then     moved = true; end;

	if self.itx ~= nil then
		self.tx = self.itx;
	end
	if self.ity ~= nil then
		self.ty = self.ity;
	end

    self.building = SadisticAIDirector.instance:getBuildingAt(self.x, self.y);

    self.timeinbuilding = self.timeinbuilding + 1;
    if self.building == nil then self.timeinbuilding = 0; end
--	if math.abs(self.tx - self.x) < 5 and math.abs(self.ty - self.y) < 5 then
		if (self.targetbuilding == self.building and self.building ~= nil) then
			--print("arrived at building");
			self.building = self.targetbuilding;
			self.x = self.building:getX();
			self.y = self.building:getY();
			self.tx = self.x;
			self.ty = self.y;
			if self.building:getTable().groups == nil then
				self.building:getTable().metaChunk = nil;
				SadisticAIDirector:initBuilding(self.building:getTable());
			end
			self.building:getTable().groups:add(self);
			self.targetbuilding = nil;
			--print(self.id.." in building "..self.eventsOnReachBuilding:size()..  " onReachBuilding events");
			for i=0, self.eventsOnReachBuilding:size()-1 do
				--print("triggering onReachBuilding event");
				EventSystem.instance:trigger(self.eventsOnReachBuilding:get(i).event, self, self.eventsOnReachBuilding:get(i).inst);
            end
			self.eventsOnReachBuilding:clear();
		end
--	end

	EventSystem.instance:updateSurvivorGroup(self, updateFrequency);

end

MetaSurvivorGroup.id = 1;

function MetaSurvivorGroup:new (x, y, javaObject)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.x = x;
	o.y = y;
	o.subgroups = LuaList:new();
    o.eventqueuelist = LuaList:new();
	o.flags = {}
	o.parent = nil;
	o.tx = x;
	o.ty = y;
    o.idle = true;
	o.loot = 0;
	o.id = MetaSurvivorGroup.id;
	o.topmostid = o.id;
    o.timeinbuilding = 0;
    MetaSurvivorGroup.id = MetaSurvivorGroup.id + 1;
	if javaObject == nil then
	   javaObject = SurvivorGroup.new(o);
	end
	o.javaObject = javaObject;
	o.members = javaObject:getMembers();
	o.eventsOnReachBuilding = LuaList:new();
	return o
end


Events.OnPlayerSetSafehouse.Add(MetaSurvivorGroup.setSafehouse);