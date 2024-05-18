require "ISBaseObject"

MetaSurvivor = ISBaseObject:derive("MetaSurvivor");

function MetaSurvivor:addRelationshipModifier(character, modifierName)


    if instanceof(character, "SurvivorDesc") then
        character = character:getMeta();
    end

    local characterID = character.javaObject:getID();
   if(self.relationshipmodifiers[characterID] == nil) then
       self.relationshipmodifiers[characterID] = LuaList:new();
   end

    if not self.relationshipmodifiers[characterID]:contains(modifierName) then
        self.relationshipmodifiers[characterID]:add(modifierName);
    end
end


function MetaSurvivor:setIdle(id)
    self.idle = id;
end

function MetaSurvivor:isIdle()
    return self.idle;
end

function MetaSurvivor:hasObservation(obs)
    return self.javaObject:hasObservation(obs);
end

function MetaSurvivor:getBaseChanceOfCooperationDelta()
    local delta = 0.5;

    if self:hasObservation("Friendly") then
        delta = delta * 1.6;
    end
    if self:hasObservation("Trusting") then
        delta = delta * 3.0;
    end
    if self:hasObservation("Kind-hearted") then
        delta = delta * 1.6;
    end
    if(self:hasObservation("Coward")) then
        delta = delta * 1.2;
    end

    -- you never know what an insane / unstable person will do.
    if self:hasObservation("Unstable") then
        delta = delta * (1.0  + (((ZombRand(1000) / 2000.0) - 0.25)));
    end
    if self:hasObservation("Insane") then
        delta = delta * (ZombRand(1000) / 1000.0);
    end

    -- if 3 or less people in group, more likely to cooperate.
    if self.javaObject:getGroup():getLuaGroup():getTotalJoinedSize() < 4 then
        delta = delta * 1.4;
    end

    return delta;
end

function MetaSurvivor:getBaseChanceOfGettingTrustDelta()
    local delta = 0.5;

    if self:hasObservation("Friendly") then
        delta = delta * 1.5;
    end
    if self:hasObservation("Kind-hearted") then
        delta = delta * 1.5;
    end
    if(self:hasObservation("Charasmatic")) then
        delta = delta * 1.5;
    end
    if(self:hasObservation("Devious")) then
        delta = delta * 1.5;
    end
    if(self:hasObservation("Aggressive")) then
        delta = delta * 0.7;
    end


    return delta;
end

function MetaSurvivor:getBaseChanceOfViolenceDelta()
    local delta = 0.3;

    if self:hasObservation("Aggressive") then
        delta = delta * 1.4;
    end

    if self:hasObservation("Friendly") then
        delta = delta * 0.3;
    end
    if self:hasObservation("Kind-hearted") then
        delta = delta * 0.3;
    end

    -- you never know what an insane / unstable person will do.
    if self:hasObservation("Unstable") then
        delta = delta * (1.0  + (((ZombRand(1000) / 2000.0) - 0.25)));
    end

    if self:hasObservation("Insane") then
        delta = delta *  (1.0 + (((ZombRand(1000) / 1000.0) - 0.5)));
    end
    if self:hasObservation("Cruel") then
        delta = delta * 1.4;
    end
    if self:hasObservation("Brave") then
        delta = delta * 1.1;
    end
    if self:hasObservation("Tough") then
        delta = delta * 1.1;
    end

    return delta;
end

function MetaSurvivor:calculateRelationship(otherCharacter)

    local tot = 0;

    for k, v in ipairs(TraitRelationshipModifiers) do
        local bPass = true;
        if v.A ~= nil then
            if(not self.javaObject:hasObservation(v.A)) then
                bPass = false;
            end
        end
        if bPass and v.B ~= nil then
            if(not otherCharacter.javaObject:hasObservation(v.B)) then
                bPass = false;
            end
        end

        if bPass and v.Test ~= nil then
            if(not v.Test(self, otherCharacter)) then
                bPass = false;
            end
        end

        if bPass then
           tot = tot + v.modifier;
        end
    end

    return tot;
end

function MetaSurvivor:getCalculatedToughness()

    local tough = 0;

    if(self.javaObject:hasObservation("Tough")) then
        tough = tough + 3;
    end
    if(self.javaObject:hasObservation("Confident")) then
        tough = tough + 1;
    end
    if(self.javaObject:hasObservation("Weak")) then
        tough = tough - 3;
    end
    if(self.javaObject:hasObservation("Clumsy")) then
        tough = tough - 2;
    end
    if(self.javaObject:hasObservation("Coward")) then
        tough = tough - 2;
    end
    if(self.javaObject:hasObservation("Aggressive")) then
        tough = tough + 2;
    end
    if(self.javaObject:hasObservation("Brave"))then
        tough = tough + 2;
    end

    return tough;
end

function MetaSurvivor:isLeaderOf(otherCharacter)
    if instanceof(otherCharacter, "SurvivorDesc") then
        otherCharacter = otherCharacter:getMeta();
    end

    if not self.javaObject: IsLeader() then
        return false;
    end
    local goUpGroup = otherCharacter.javaObject:getGroup():getLuaGroup();
    local topGroup = self.javaObject:getGroup():getLuaGroup();
    if(goUpGroup==nil) then
        return false;
    end
    if(topGroup==nil) then
        return false;
    end

    while(goUpGroup ~= topGroup) do
        if goUpGroup == nil or goUpGroup.parent == nil then
            return false;
        end
        goUpGroup = goUpGroup.parent;
    end

    return true;
end

function MetaSurvivor:getID()
   return self.javaObject:getID();
end

function MetaSurvivor:isPlotting()
    return self.plot ~= nil;
end

function MetaSurvivor:startPlot(target, plottype)

    local o = {}
    setmetatable(o, Plots[plottype])
    Plots[plottype].__index = Plots[plottype]
    o.target = target;
    o.plotleader = self;
    o.backers = LuaList:new();
    self.plot = o;
    return o;
end

function MetaSurvivor:isInvolvedInPlot(plot)

    return self.backedplots:contains(plot) or self.plot == plot;
end

function MetaSurvivor:backPlot(plot)
    plot.backers:add(self.javaObject);
    return self.backedplots:add(plot);
end

function MetaSurvivor:abandonPlot(plot)
    plot.backers:remove(self.javaObject);
    if self.plot == plot then plot = nil end
    return self.backedplots:remove(plot);
end

function MetaSurvivor:endPlot()
    if self.plot == nil then return end;
    for i=0, self.plot.backers:size()-1 do
        self.plot.backers:get(i):getMeta():abandonPlot(self.plot);
    end
    self.plot = nil;
end

function MetaSurvivor:getRelationship(otherCharacter)


    if instanceof(otherCharacter, "SurvivorDesc") then
        otherCharacter = otherCharacter:getMeta();
    end

    if self.relationshipvalues[otherCharacter:getID()] == nil then
        local v = self:calculateRelationship(otherCharacter);
        self.relationshipvalues[otherCharacter:getID()] = v;
    end

    --print("relationship of ".. self.javaObject:getForename().. " " .. self.javaObject:getSurname() .. " and " .. otherCharacter.javaObject:getForename().. " " .. otherCharacter.javaObject:getSurname() .. " : " .. self.relationshipvalues[otherCharacter:getID()] + self:getRelationshipModifiers(otherCharacter) )

    return self.relationshipvalues[otherCharacter:getID()] + self:getRelationshipModifiers(otherCharacter);
end

function MetaSurvivor:getRelationshipModifiers(otherCharacter)

    local tot = 0;
    if instanceof(otherCharacter, "SurvivorDesc") then
        otherCharacter = otherCharacter:getMeta();
    end

    if self.relationshipmodifiers[otherCharacter:getID()] ~= nil then
         for i=0, self.relationshipmodifiers[otherCharacter:getID()]:size()-1 do
             local mod = self.relationshipmodifiers[otherCharacter:getID()]:get(i);
             tot = tot + StaticRelationshipModifiers[mod].modifier;
         end
    end

    return tot;
end

function MetaSurvivor:getInstance()
    local inst = self.javaObject:getInstance();

    return inst;
end

function MetaSurvivor:doIdle()

    if self.behaviour == nil then
        self.behaviour = GuardBehaviour:new(self);
    end

    if self.behaviour ~= nil then
        self.behaviour:process();
    end
end

function MetaSurvivor:getRelationshipBreakdown(otherCharacter)

end
function MetaSurvivor:getRoom()
    local sq = getCell():getGridSquare(self.x, self.y, self.z);

    if sq ~= nil then
        return sq:getRoom();
    end

    return nil;
end

function MetaSurvivor:isReal()
	local inst = self.javaObject:getInstance();
	if inst ~= nil then
		return true;
	end
	return false;
end

function MetaSurvivor:update()

    self.x = self.group.x;
    self.y = self.group.y;
    self.z = 0;

    if self.javaObject ~= nil then
        local inst = self.javaObject:getInstance();
       if inst ~= nil then
           self.x = inst:getX()
           self.y = inst:getY()
           self.z = inst:getZ();
           if self.idle then
              self:doIdle();
           end
       end
   end
   if self.plot ~= nil then
       if not self.plot.isValid(self.javaObject, self.plot.target) then
           self:endPlot();
           return;
       end
      if self.plot.testTrigger(self.plot.plotleader.javaObject, self.plot.target, self.plot.backers) then
          self.plot.onTrigger(self.plot.plotleader.javaObject, self.plot.target, self.plot.backers);
      end

   end
end

function MetaSurvivor:new (javaObject)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.javaObject = javaObject;
    o.relationshipmodifiers = {}
    o.relationshipvalues = {}
    o.backedplots = LuaList:new();
    o.idle = true;

    o.group = javaObject:getGroup():getLuaGroup();

    return o
end

function createMetaSurvivor(desc)
   return MetaSurvivor:new(desc);
end