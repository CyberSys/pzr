--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISGetCompost = ISBaseTimedAction:derive("ISGetCompost");

function ISGetCompost:isValid()
    return self.compost:getCompost() > 10 and self.character:getInventory():contains(self.sandbag);
end

function ISGetCompost:update()
end

function ISGetCompost:start()
end

function ISGetCompost:stop()
    ISBaseTimedAction.stop(self);
end

function ISGetCompost:perform()
    -- needed to remove from queue / start next.
    ISBaseTimedAction.perform(self);
    self.character:getInventory():Remove(self.sandbag);
    self.compost:setCompost(self.compost:getCompost() - 10);
    self.compost:updateSprite();
    local compostBag = self.character:getInventory():AddItem("Base.CompostBag");
    self.character:setPrimaryHandItem(compostBag);
    if isClient() then
        self.compost:syncCompost();
    end
end

function ISGetCompost:new(character, compost, sandbag, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.compost = compost;
    o.sandbag = sandbag;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
	return o;
end
