--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISBuildAction = ISBaseTimedAction:derive("ISBuildAction");

-- The FMOD events have approx. 10-second duration even though the sounds are shorter.
ISBuildAction.soundDelay = 6
ISBuildAction.soundTime = 0

function ISBuildAction:isValid()
	if not self.item.noNeedHammer and self.hammer then
		return self.hammer:getCondition() > 0;
	end
	return true;
end

function ISBuildAction:update()
    local worldSoundRadius = 0
    -- Players with the Deaf trait don't play sounds.  In multiplayer, we mustn't send multiple sounds to other clients.
    if ISBuildAction.soundTime + ISBuildAction.soundDelay < getTimestamp() then
        ISBuildAction.soundTime = getTimestamp()
        if not self.item.noNeedHammer then
            local playingSaw = self.sawSound ~= 0 and self.character:getEmitter():isPlaying(self.sawSound)
            local playingHammer = self.hammerSound ~= 0 and self.character:getEmitter():isPlaying(self.hammerSound)
            if not playingSaw and not playingHammer then
                if self.doSaw == true and self.character:getInventory():contains("Saw") then
                    self.sawSound = self.character:getEmitter():playSound("Sawing");
                    worldSoundRadius = 15
                    self.doSaw = false;
                else
                    self.hammerSound = self.character:getEmitter():playSound("Hammering");
                    worldSoundRadius = math.ceil(20 * self.character:getHammerSoundMod())
                    self.doSaw = true;
                end
            end
        end
        if self.craftingBank then
            local playingCrafting = self.craftingSound ~= 0 and not self.character:getEmitter():isPlaying(self.craftingSound)
            if not playingCrafting then
                self.craftingSound = self.character:getEmitter():playSound(self.craftingBank);
            end
            worldSoundRadius = 15
        end
    end
    if worldSoundRadius > 0 then
        ISBuildAction.worldSoundTime = getTimestamp()
        addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), worldSoundRadius, worldSoundRadius)
    end
end

function ISBuildAction:start()
    if not self.item.noNeedHammer then
        self.sawSound = 0
		self.hammer = self.character:getPrimaryHandItem();
        self.hammerSound = 0
    end
    if self.craftingBank then
        self.craftingSound = 0
    end
end

function ISBuildAction:stop()
    if self.sawSound and self.sawSound ~= 0 and self.character:getEmitter():isPlaying(self.sawSound) then
        self.character:getEmitter():stopSound(self.sawSound);
    end
    if self.hammerSound and self.hammerSound ~= 0 and self.character:getEmitter():isPlaying(self.hammerSound) then
        self.character:getEmitter():stopSound(self.hammerSound);
    end
    if self.craftingSound and self.craftingSound ~= 0 and self.character:getEmitter():isPlaying(self.craftingSound) then
        self.character:getEmitter():stopSound(self.craftingSound);
    end
    ISBaseTimedAction.stop(self);
end

function ISBuildAction:perform()
    if self.sawSound and self.sawSound ~= 0 and self.character:getEmitter():isPlaying(self.sawSound) then
        self.character:getEmitter():stopSound(self.sawSound);
    end
    if self.hammerSound and self.hammerSound ~= 0 and self.character:getEmitter():isPlaying(self.hammerSound) then
        self.character:getEmitter():stopSound(self.hammerSound);
    end
    if self.craftingSound and self.craftingSound ~= 0 and self.character:getEmitter():isPlaying(self.craftingSound) then
        self.character:getEmitter():stopSound(self.craftingSound);
    end
    -- reduce the condition of the hammer if it's a stone hammer
    local hammer = self.character:getPrimaryHandItem()
    if hammer and hammer:getType() == "HammerStone" and ZombRand(hammer:getConditionLowerChance()) == 0 then
        hammer:setCondition(hammer:getCondition() - 1)
        ISWorldObjectContextMenu.checkWeapon(self.character);
    end

    self.item.character = self.character;
	self.item:create(self.x, self.y, self.z, self.north, self.spriteName);
    self.square:RecalcAllWithNeighbours(true);
	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISBuildAction:new(character, item, x, y, z, north, spriteName, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.item = item;
	o.x = x;
	o.y = y;
	o.z = z;
	o.north = north;
	o.spriteName = spriteName;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
    o.craftingBank = item.craftingBank;
	if character:HasTrait("Handy") then
		o.maxTime = time - 50;
    end
--    o.maxTime = 500;
    o.square = getCell():getGridSquare(x,y,z);
    o.doSaw = true;
    o.caloriesModifier = 8;
	return o;
end
