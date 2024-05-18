--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISUnbarricadeAction = ISBaseTimedAction:derive("ISUnbarricadeAction");

function ISUnbarricadeAction:isValid()
	if not instanceof(self.item, "BarricadeAble") or not self.item:getBarricadeForCharacter(self.character) then
		return false
	end
	local barricade = self.item:getBarricadeForCharacter(self.character)
	if barricade:isMetal() or barricade:isMetalBar() then
		if not self.character:hasEquipped("BlowTorch") then
			return false
		end
	else
		if barricade:getNumPlanks() == 0 then
			return false
		end
		if not self.character:hasEquipped("Hammer") and not self.character:hasEquipped("HammerStone") then
			return false
		end
	end
	return true;
end

function ISUnbarricadeAction:update()
	self.character:faceThisObject(self.item)
end

function ISUnbarricadeAction:start()
--    getSoundManager():PlayWorldSound("crackwood", false, self.character:getSquare(), 1, 5, 1, false)
    if self.character:hasEquipped("BlowTorch") then
        self.sound = self.character:playSound("BeginRemoveBarricadeMetal");
        local radius = 20 * self.character:getWeldingSoundMod()
        addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), radius, radius)
    else
        self.sound = self.character:playSound("BeginRemoveBarricadePlank");
        addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 10, 1)
    end
end

function ISUnbarricadeAction:stop()
	if self.sound then
		self.character:getEmitter():stopSound(self.sound)
		self.sound = nil
	end
    ISBaseTimedAction.stop(self);
end

function ISUnbarricadeAction:perform()
    if self.sound then
        self.character:getEmitter():stopSound(self.sound)
        self.sound = nil
    end
    if not self.character:hasEquipped("BlowTorch") then
	    self.character:playSound("RemoveBarricadePlank")
        addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 10, 1)
    end

	local barricade = self.item:getBarricadeForCharacter(self.character)
	if not barricade then return end
	local isMetal = barricade:isMetal()
	local isMetalBar = barricade:isMetalBar()

	local obj = self.item
	local index = obj:getObjectIndex()
	local args = { x=obj:getX(), y=obj:getY(), z=obj:getZ(), index=index }
	sendClientCommand(self.character, 'object', 'unbarricade', args)

	if isMetal or isMetalBar then
		self.character:getPrimaryHandItem():Use()
		self.character:getPrimaryHandItem():Use()
		self.character:getPrimaryHandItem():Use()
	end

    -- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISUnbarricadeAction:new(character, item, time)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.item = item;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = time;
	if character:HasTrait("Handy") then
		o.maxTime = time - 20;
    end
    if character:getAccessLevel() ~= "None" then
        o.maxTime = 1;
    end
	return o;
end
