require "ISBaseObject"

SurvivorSelector = ISBaseObject:derive("SurvivorSelector");

function SurvivorSelector:initialise()

end

SurvivorSelector.compareByPlot = function (a, b)
--   SurvivorSelector.contextwith = with;
-- SurvivorSelector.contextagainst = against;
    local totala = a:getMeta():getRelationship(SurvivorSelector.contextwith) + (-a:getMeta():getRelationship(SurvivorSelector.contextagainst))
    local totalb = b:getMeta():getRelationship(SurvivorSelector.contextwith) + (-b:getMeta():getRelationship(SurvivorSelector.contextagainst))
    if(totala > totalb) then
        return true
    else
        return false
    end
end

SurvivorSelector.compareByPlotInv = function (a, b)
--   SurvivorSelector.contextwith = with;
-- SurvivorSelector.contextagainst = against;
    local totala = a:getMeta():getRelationship(SurvivorSelector.contextwith) + (-a:getMeta():getRelationship(SurvivorSelector.contextagainst))
    local totalb = b:getMeta():getRelationship(SurvivorSelector.contextwith) + (-b:getMeta():getRelationship(SurvivorSelector.contextagainst))
    if(totala <= totalb) then
        return true
    else
        return false
    end
end

SurvivorSelector.compareByStrength = function (a, b)
    if(a:getCalculatedToughness() > b:getCalculatedToughness()) then
        return true
    else
        return false
    end
end

SurvivorSelector.compareByStrengthInv = function (a, b)
    if(a:getCalculatedToughness() <= b:getCalculatedToughness()) then
        return true
    else
        return false
    end
end

SurvivorSelector.compareByLiked = function (a, b)
    if(SurvivorSelector.context:getMeta():getRelationship(a) > SurvivorSelector.context:getMeta():getRelationship(b)) then
        return true
    else
        return false
    end
end

SurvivorSelector.compareByLikedInv = function (a, b)
    if(SurvivorSelector.context:getMeta():getRelationship(a) <= SurvivorSelector.context:getMeta():getRelationship(b)) then
        return true
    else
        return false
    end
end

function SurvivorSelector:removeWeakest(count)

    if count == nil then count = 1; end

    if count <= 0 then
        return;
    end

    if count > self:size() then
        count = self:size();
    end
    self.list:sort(SurvivorSelector.compareByStrength);
    -- sort them into strongest...
    while (count > 0) do
        self.list:pop();
        count = count - 1;
    end

end

function SurvivorSelector:removeStrongest(count)
    if count == nil then count = 1; end

    if count < 0 then
        return;
    end

    if count > self:size() then
        count = self:size();
    end
    self.list:sort(SurvivorSelector.compareByStrengthInv);
    -- sort them into strongest...
    while (count > 0) do
        self.list:pop();
        count = count - 1;
    end


end

function SurvivorSelector:removeMostLikelyToPlot(with, against, count)
    if count == nil then count = 1; end

    if count < 0 then
        return;
    end

    if count > self:size() then
        count = self:size();
    end
    SurvivorSelector.contextwith = with;
    SurvivorSelector.contextagainst = against;
    self.list:sort(SurvivorSelector.compareByPlotInv);
    SurvivorSelector.contextwith = nil;
    SurvivorSelector.contextagainst = nil;
    -- sort them into strongest...
    while (count > 0) do
        self.list:pop();
        count = count - 1;
    end


end

function SurvivorSelector:removeLeastLikelyToPlot(with, against, count)
    if count == nil then count = 1; end

    if count < 0 then
        return;
    end

    if count > self:size() then
        count = self:size();
    end
    SurvivorSelector.contextwith = with;
    SurvivorSelector.contextagainst = against;
    self.list:sort(SurvivorSelector.compareByPlot);
    SurvivorSelector.contextwith = nil;
    SurvivorSelector.contextagainst = nil;
    -- sort them into strongest...
    while (count > 0) do
        self.list:pop();
        count = count - 1;
    end


end


function SurvivorSelector:removeMostLiked(by, count)
    if count == nil then count = 1; end

    if count < 0 then
        return;
    end

    if count > self:size() then
        count = self:size();
    end
    SurvivorSelector.context = by;
    self.list:sort(SurvivorSelector.compareByLikedInv);
    SurvivorSelector.context = nil;
    -- sort them into strongest...
    while (count > 0) do
        self.list:pop();
        count = count - 1;
    end


end


function SurvivorSelector:removeLeastLiked(by, count)
    if count == nil then count = 1; end

    if count < 0 then
        return;
    end

    if count > self:size() then
        count = self:size();
    end
    SurvivorSelector.context = by;

    self.list:sort(SurvivorSelector.compareByLiked);
    SurvivorSelector.context = nil;
    -- sort them into strongest...
    while (count > 0) do
        self.list:pop();
        count = count - 1;
    end


end

function SurvivorSelector:removeLeader()
    local leader = self.group:getLeader();

    if not self.list:contains(leader) then
        return nil;
    end

    self.list:remove(leader);
    return leader;
end

function SurvivorSelector:removeRandom(count)
    if count == nil then count = 1; end
    local r = nil;
    while count > 0 do
        local r = self.list:get(ZombRand(self.list:size()));
        r = self.list:remove(r);
        count = count - 1;
    end
    return r;
end

function SurvivorSelector:remove(r)
    self.list:remove(r);
    return r;
end

function SurvivorSelector:removeList(r)

    for i=0, r:size()-1 do
        self.list:remove(r:get(i));
    end
    return r;
end

function SurvivorSelector:size()
    return self.list:size();
end

function SurvivorSelector:get(i)
    return self.list:get(i);
end


function SurvivorSelector:new (group)
    local o = {}
    setmetatable(o, self)
    self.__index = self

    o.list = LuaList:new();

    for i=0, group.members:size()-1 do
        o.list:add(group.members:get(i));
    end
    o.group = group;
    return o
end

