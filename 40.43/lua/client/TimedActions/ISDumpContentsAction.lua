--***********************************************************
--**                    Erasmus Crowley                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISDumpContentsAction = ISBaseTimedAction:derive("ISDumpContentsAction");

function ISDumpContentsAction:isValid()
	return self.character:getInventory():contains(self.item);
end

function ISDumpContentsAction:start()
    if self.item ~= nil then
	    self.item:setJobType(getText("IGUI_JobType_PourOut"));
	    self.item:setJobDelta(0.0);
    end
end

function ISDumpContentsAction:update()
	if self.item ~= nil then
        self.item:setJobDelta(self:getJobDelta());
    end
end

function ISDumpContentsAction:stop()
    ISBaseTimedAction.stop(self);
    if self.item ~= nil then
        self.item:setJobDelta(0.0);
     end
end

function ISDumpContentsAction:perform()
    if self.item ~= nil then
        self.item:getContainer():setDrawDirty(true);
        self.item:setJobDelta(0.0);
        local itemType = self:finalItem(self.item:getFullType())
        if itemType then
			self.item:setReplaceOnUse(itemType)
		end
        self.item:Use();
    end
    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

-- RemouladeFull -> RemouladeHalf -> RemouladeEmpty
function ISDumpContentsAction:finalItem(itemType)
	local item = ScriptManager.instance:FindItem(itemType)
	if item == nil then return nil end
	if item:getCanStoreWater() then
		return itemType
	end
	if not item:getReplaceOnUse() then return nil end
	return self:finalItem(item:getModuleName()..'.'..item:getReplaceOnUse())
end

function ISDumpContentsAction:new (character, item, time)
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
