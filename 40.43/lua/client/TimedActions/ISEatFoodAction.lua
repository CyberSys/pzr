--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISEatFoodAction = ISBaseTimedAction:derive("ISEatFoodAction");

function ISEatFoodAction:isValid()
	return self.character:getInventory():contains(self.item);
end

function ISEatFoodAction:update()
	self.item:setJobDelta(self:getJobDelta());
    if self.eatAudio ~= 0 and not self.character:getEmitter():isPlaying(self.eatAudio) then
        self.eatAudio = self.character:getEmitter():playSound(self.eatSound);
--        self.eatAudio = getSoundManager():PlayWorldSoundWav( self.eatSound, self.character:getCurrentSquare(), 0.5, 2, 0.5, true);
    end
end

function ISEatFoodAction:start()
	if self.eatSound ~= '' then
        self.eatAudio = self.character:getEmitter():playSound(self.eatSound);
--		self.eatAudio = getSoundManager():PlayWorldSoundWav( self.eatSound, self.character:getCurrentSquare(), 0.5, 2, 0.5, true);
    end
	if self.item:getCustomMenuOption() then
		self.item:setJobType(self.item:getCustomMenuOption())
	else
		self.item:setJobType(getText("ContextMenu_Eat"));
	end
	self.item:setJobDelta(0.0);
end

function ISEatFoodAction:stop()
    ISBaseTimedAction.stop(self);
    self.item:setJobDelta(0.0);
    if self.character:getInventory():contains(self.item) then
		self:eat(self.item, self:getJobDelta());
    end
end

function ISEatFoodAction:perform()
    if self.eatAudio ~= 0 and self.character:getEmitter():isPlaying(self.eatAudio) then
        self.character:getEmitter():stopSound(self.eatAudio);
    end
    if self.item:getHungChange() ~= 0 then
        self.character:getEmitter():playSound("Swallowing");
--        getSoundManager():PlayWorldSoundWav( "PZ_Swallowing", self.character:getCurrentSquare(), 0.3, 2, 0.7, true);
    end
    self.item:getContainer():setDrawDirty(true);
    self.item:setJobDelta(0.0);
    self.character:Eat(self.item, self.percentage);
    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISEatFoodAction:eat(food, percentage)
    -- calcul the percentage ate
    if percentage > 0.95 then
        percentage = 1.0;
    end
    percentage = self.percentage * percentage;
    self.character:Eat(self.item, percentage);
end

function ISEatFoodAction:new (character, item, percentage)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.item = item;
	o.stopOnWalk = false;
	o.stopOnRun = true;
    o.percentage = percentage;
    if not o.percentage then
        o.percentage = 1;
    end

	o.maxTime = math.abs(item:getBaseHunger() * 100 * o.percentage) * 8;

    if o.maxTime > math.abs(item:getHungerChange() * 100 * 8) then
        o.maxTime = math.abs(item:getHungerChange() * 100 * 8);
    end

	-- Cigarettes don't reduce hunger
	if o.maxTime == 0 then o.maxTime = 100 end

    o.eatSound = item:getCustomEatSound() or "Eating";
    o.eatAudio = 0

--	local w = item:getActualWeight();
--    if w > 3 then w = 3; end;
--
--    o.maxTime = o.maxTime * w;

    o.ignoreHandsWounds = true;
	return o
end
