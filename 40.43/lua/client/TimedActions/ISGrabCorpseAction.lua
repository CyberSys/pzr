require "TimedActions/ISBaseTimedAction"

ISGrabCorpseAction = ISBaseTimedAction:derive("ISGrabCorpseAction");

function ISGrabCorpseAction:isValid()
    if self.corpseBody:getStaticMovingObjectIndex() < 0 then
        return false
    end
    return self.character:getInventory():getItemCount("Base.CorpseMale") == 0;
end

function ISGrabCorpseAction:update()
    self.corpse:setJobDelta(self:getJobDelta());
end

function ISGrabCorpseAction:start()
    self.corpse:setJobType(getText("ContextMenu_Grab"));
    self.corpse:setJobDelta(0.0);
end

function ISGrabCorpseAction:stop()
    ISBaseTimedAction.stop(self);
    self.corpse:setJobDelta(0.0);

end

function ISGrabCorpseAction:perform()
    forceDropHeavyItems(self.character)
    self.corpse:setJobDelta(0.0);
    self.character:getInventory():setDrawDirty(true);
    self.character:getInventory():AddItem(self.corpse);
    self.character:setPrimaryHandItem(self.corpse);
    self.character:setSecondaryHandItem(self.corpse);
    self.corpseBody:getSquare():removeCorpse(self.corpseBody, false);
--    self.corpseBody:getSquare():getObjects():remove(self.corpseBody);
    --self.item:setWorldItem(nil);

    local pdata = getPlayerData(self.character:getPlayerNum());
    if pdata ~= nil then
        pdata.playerInventory:refreshBackpacks();
        pdata.lootInventory:refreshBackpacks();
    end

    -- needed to remove from queue / start next.
    ISBaseTimedAction.perform(self);
end

function ISGrabCorpseAction:new (character, corpse, time)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.character = character;
    o.corpse = corpse:getItem();
    o.corpseBody = corpse;
    o.stopOnWalk = true;
    o.stopOnRun = true;
    o.maxTime = time;
    if character:getAccessLevel() ~= "None" then
        o.maxTime = 1;
    end
    return o
end
