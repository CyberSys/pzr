--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISRemoveBush = ISBaseTimedAction:derive("ISRemoveBush")

function ISRemoveBush:isValid()
	return (self.weapon and self.weapon:getCondition() > 0) or not self.weapon;
end

function ISRemoveBush:update()

	self.character:PlayAnim("Attack_" .. self.weapon:getSwingAnim())
	local val = self.weapon:getSwingTime()
	if self.weapon:isUseEndurance() then
		local moodleLevel = self.character:getMoodles():getMoodleLevel(MoodleType.Endurance)
		if moodleLevel == 1 then val = val * 1.1
		elseif moodleLevel == 2 then val = val * 1.2
		elseif moodleLevel == 3 then val = val * 1.3
		elseif moodleLevel == 4 then val = val * 1.4 end
	end
	if val < self.weapon:getMinimumSwingTime() then
		val = self.weapon:getMinimumSwingTime()
	end
	val = val * self.weapon:getSpeedMod(self.character)
	local AttackDelayMax = val * 0.6
	local numFrames = self.character:getSpriteDef():getFrameCount()
	local perFrame = numFrames / 60 / AttackDelayMax
	self.character:getSpriteDef():setFrameSpeedPerFrame(perFrame * 2)
	
	self.character:setIgnoreMovementForDirection(false)
	if self.wallVine then
		local object = self:getWallVineObject(self.square)
		if object then self.character:faceThisObject(object) end
	else
		self.character:faceLocation(self.square:getX(), self.square:getY())
	end
	self.character:setIgnoreMovementForDirection(true)

	local AttackDelayUse = 0.3 * numFrames
	if self.spriteFrame < AttackDelayUse and self.character:getSpriteDef():getFrame() >= AttackDelayUse then
--		getSoundManager():PlayWorldSound("PZ_ChopTree", self.square, 0.2, 20, 1.0, 4, true)
        self.square:playSound("ChopTree");
        addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 20, 1)
		self.character:PlayAnimUnlooped("Attack_" .. self.weapon:getSwingAnim())
		self:useEndurance()
		if ZombRand(self.weapon:getConditionLowerChance() * 4) == 0 then
			self.weapon:setCondition(self.weapon:getCondition() - 1)
			ISWorldObjectContextMenu.checkWeapon(self.character)
		end
	end
	self.spriteFrame = self.character:getSpriteDef():getFrame()
--	self:setJobDelta(1 - self.tree:getHealth() / self.tree:getMaxHealth())
end

function ISRemoveBush:start()
    self.weapon = self.character:getPrimaryHandItem()
	addSound(self.character, self.character:getX(), self.character:getY(), self.character:getZ(), 20, 10)
end

function ISRemoveBush:stop()
	if self.character:getPrimaryHandItem() and self.character:getPrimaryHandItem() == self.weapon then
		self.character:PlayAnimUnlooped("Attack_" .. self.weapon:getSwingAnim())
	else
		self.character:PlayAnim("Idle")
	end
	self.character:setIgnoreMovementForDirection(false)
    ISBaseTimedAction.stop(self)
end

function ISRemoveBush:getWallVineObject(square)
	if not square then return end
	for i=0,square:getObjects():size()-1 do
		local object = square:getObjects():get(i);
		local attached = object:getAttachedAnimSprite()
		if attached then
			for n=1,attached:size() do
				local sprite = attached:get(n-1)
--					if sprite and sprite:getParentSprite() and sprite:getParentSprite():getProperties():Is(IsoFlagType.canBeCut) then
				if sprite and sprite:getParentSprite() and sprite:getParentSprite():getName() and luautils.stringStarts(sprite:getParentSprite():getName(), "f_wallvines_") then
					return object, n-1
				end
			end
		end
	end
end

function ISRemoveBush:perform()
	if self.character:getPrimaryHandItem() and self.character:getPrimaryHandItem() == self.weapon then
		self.character:PlayAnimUnlooped("Attack_" .. self.weapon:getSwingAnim())
	else
		self.character:PlayAnim("Idle")
	end
	self.character:setIgnoreMovementForDirection(false)

	local sq = self.square
	local args = { x = sq:getX(), y = sq:getY(), z = sq:getZ(), wallVine = self.wallVine }
	sendClientCommand(self.character, 'object', 'removeBush', args)

    -- needed to remove from queue / start next.
    ISBaseTimedAction.perform(self)
end

function ISRemoveBush:useEndurance()
	if self.weapon:isUseEndurance() then
		local use = self.weapon:getWeight() * self.weapon:getFatigueMod(self.character) * self.character:getFatigueMod() * self.weapon:getEnduranceMod() * 0.1
		local useChargeDelta = 1.0
		use = use * useChargeDelta * 0.041
		if self.weapon:isTwoHandWeapon() and self.character:getSecondaryHandItem() ~= self.weapon then
			use = use + self.weapon:getWeight() / 1.5 / 10 / 20
		end
		self.character:getStats():setEndurance(self.character:getStats():getEndurance() - use)
	end
end

function ISRemoveBush:new(character, square, wallVine)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.square = square
	o.stopOnWalk = true
	o.stopOnRun = true
	o.maxTime = 100
	o.spriteFrame = 0
	o.wallVine = wallVine
    if character:getAccessLevel() ~= "None" then
        o.maxTime = 1;
    end
	return o
end
