--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISDropItemAction = ISBaseTimedAction:derive("ISDropItemAction");

function ISDropItemAction:isValid()
	return self.character:getInventory():contains(self.item);
end

function ISDropItemAction:update()
	self.item:setJobDelta(self:getJobDelta());
end

function ISDropItemAction:start()
	self.item:setJobType(getText("IGUI_JobType_Dropping"));
	self.item:setJobDelta(0.0);
end

function ISDropItemAction:stop()
    ISBaseTimedAction.stop(self);
    self.item:setJobDelta(0.0);

end

function ISDropItemAction:perform()
    self.item:getContainer():setDrawDirty(true);
    self.item:setJobDelta(0.0);
	self.character:getCurrentSquare():AddWorldInventoryItem(self.item, 0.0, 0.0, 0.0);
	self.character:getInventory():Remove(self.item);

    local pdata = getPlayerData(self.character:getPlayerNum());

    pdata.playerInventory:refreshBackpacks();
    pdata.lootInventory:refreshBackpacks();
    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISDropItemAction:new (character, item, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.item = item;
	o.stopOnWalk = false;
	o.stopOnRun = false;
	o.maxTime = time;
	return o
end
