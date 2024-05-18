--***********************************************************
--**                    Erasmus Crowley                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISDumpWaterAction = ISBaseTimedAction:derive("ISDumpWaterAction");

function ISDumpWaterAction:isValid()
	return self.character:getInventory():contains(self.item);
end

function ISDumpWaterAction:start()
    if self.item ~= nil then
	    self.item:setJobType(getText("IGUI_JobType_PourOut"));
	    self.item:setJobDelta(0.0);
		self.startUsedDelta = self.item:getUsedDelta()
    end
end

function ISDumpWaterAction:update()
	if self.item ~= nil then
        self.item:setJobDelta(self:getJobDelta());
        self.item:setUsedDelta(self.startUsedDelta * (1 - self:getJobDelta()));
    end
end

function ISDumpWaterAction:stop()
    ISBaseTimedAction.stop(self);
    if self.item ~= nil then
        self.item:setJobDelta(0.0);
     end
end

function ISDumpWaterAction:perform()
    if self.item ~= nil then
        self.item:getContainer():setDrawDirty(true);
        self.item:setJobDelta(0.0);
        self.item:setUsedDelta(0.0);
        self.item:Use();
    end
    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISDumpWaterAction:new (character, item)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.item = item;
	o.stopOnWalk = false;
	o.stopOnRun = false;
	o.maxTime = (item:getUsedDelta() / item:getUseDelta()) * 10;
	return o
end
