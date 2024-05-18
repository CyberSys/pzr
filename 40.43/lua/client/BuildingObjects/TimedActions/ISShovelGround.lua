--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISShovelGround = ISBaseTimedAction:derive("ISShovelGround");

function ISShovelGround:isValid()
	return self.character:getInventory():contains(self.emptyBag) and
		self.sandTile and self.sandTile:getSprite() and
		--self.sandTile:getSprite():getName() ~= self.newSprite and
		(self.emptyBag:getType() == "EmptySandbag" or self.emptyBag:getUsedDelta() < 1)
end

function ISShovelGround:update()
	self.character:faceThisObject(self.sandTile)
end

function ISShovelGround:start()
--    self.sound = getSoundManager():PlayWorldSound("shoveling", self.sandTile:getSquare(), 0, 5, 1, true);
    self.sound = self.character:getEmitter():playSound("Shoveling")
	addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 10, 1)
end

function ISShovelGround:stop()
    if self.sound ~= 0 and self.character:getEmitter():isPlaying(self.sound) then
        self.character:getEmitter():stopSound(self.sound);
    end
    ISBaseTimedAction.stop(self);
end

function ISShovelGround:perform()
    if self.sound ~= 0 and self.character:getEmitter():isPlaying(self.sound) then
        self.character:getEmitter():stopSound(self.sound);
    end
	local sq = self.sandTile:getSquare()
	local args = { x = sq:getX(), y = sq:getY(), z = sq:getZ() }
	sendClientCommand(self.character, 'object', 'shovelGround', args)

	-- FIXME: server should manage the player's inventory
	if self.emptyBag:getType() == "EmptySandbag" then
		self.character:getInventory():Remove(self.emptyBag);
		local item = self.character:getInventory():AddItem(self.newBag);
		if item ~= nil then
			item:setUsedDelta(item:getUseDelta())
		end
	elseif self.emptyBag:getUsedDelta() + self.emptyBag:getUseDelta() <= 1 then
		self.emptyBag:setUsedDelta(self.emptyBag:getUsedDelta() + self.emptyBag:getUseDelta())
    end
    if ZombRand(5) == 0 then
        self.character:getInventory():AddItem("Base.Worm");
    end
	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISShovelGround:new(character, emptyBag, sandTile, newSprite, newBag)
	local o = {};
	setmetatable(o, self);
	self.__index = self;
	o.character = character;
	o.emptyBag = emptyBag;
	o.sandTile = sandTile;
    o.newSprite = newSprite;
    o.newBag = newBag;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = 100;
    o.caloriesModifier = 8;
	return o;
end
