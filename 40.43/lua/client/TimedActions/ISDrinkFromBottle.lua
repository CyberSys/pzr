--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISDrinkFromBottle = ISBaseTimedAction:derive("ISDrinkFromBottle");

function ISDrinkFromBottle:isValid()
    return self.character:getInventory():contains(self.item);
end

function ISDrinkFromBottle:update()
    self.item:setJobDelta(self:getJobDelta());
    if self.eatAudio ~= 0 and not self.character:getEmitter():isPlaying(self.eatAudio) then
        self.eatAudio = self.character:getEmitter():playSound(self.eatSound);
    end
end

function ISDrinkFromBottle:start()
    if self.eatSound ~= '' then
        self.eatAudio = self.character:getEmitter():playSound(self.eatSound);
    end
    if self.item:getCustomMenuOption() then
        self.item:setJobType(self.item:getCustomMenuOption())
    else
        self.item:setJobType(getText("ContextMenu_Drink"));
    end
    self.item:setJobDelta(0.0);
end

function ISDrinkFromBottle:stop()
    ISBaseTimedAction.stop(self);
    self.item:setJobDelta(0.0);
    if self.character:getInventory():contains(self.item) then
        self:drink(self.item, self:getJobDelta());
    end
end

function ISDrinkFromBottle:perform()
    if self.eatAudio ~= 0 and self.character:getEmitter():isPlaying(self.eatAudio) then
        self.character:getEmitter():stopSound(self.eatAudio);
    end
    self.character:getEmitter():playSound("DrinkingFromBottle");
    self.item:setJobDelta(0.0);
    self.item:getContainer():setDrawDirty(true);
    self:drink(self.item, 1);
    -- needed to remove from queue / start next.
    ISBaseTimedAction.perform(self);
end

function ISDrinkFromBottle:drink(food, percentage)
    -- calcul the percentage drank
    if percentage > 0.95 then
        percentage = 1.0;
    end
    self.uses = self.uses * percentage;

    for i=0,self.uses do
        if self.character:getStats():getThirst() > 0 then
            self.character:getStats():setThirst(self.character:getStats():getThirst() - 0.1);
            if self.character:getStats():getThirst() < 0 then
                self.character:getStats():setThirst(0);
            end
            if self.item:isTaintedWater() then
                self.character:getBodyDamage():setPoisonLevel(self.character:getBodyDamage():getPoisonLevel() + 10);
            end
            self.item:Use();
        end
    end

end

function ISDrinkFromBottle:new (character, item, uses)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.character = character;
    o.item = item;
    o.stopOnWalk = false;
    o.stopOnRun = true;
    o.uses = uses;
    if o.uses < 1 then
        o.uses = 1;
    end
    if not o.uses then
        o.uses = 1;
    end
    o.maxTime = o.uses * 30;
    o.eatSound = "DrinkingFromBottle";
    o.eatAudio = 0
    o.tick = 0;
    o.ignoreHandsWounds = true;
    return o
end
