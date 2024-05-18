--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISLockDoor = ISBaseTimedAction:derive("ISLockDoor");

function ISLockDoor:isValid()
	local keyId = instanceof(self.door, "IsoDoor") and self.door:checkKeyId() or self.door:getKeyId()
	if self.character:getInventory():haveThisKeyId(keyId) then return true end
	if self.door:getProperties():Is("forceLocked") then return false end
	return not self.character:getCurrentSquare():Is(IsoFlagType.exterior)
end

function ISLockDoor:update()
	self.character:faceThisObject(self.door)
end

function ISLockDoor:start()
end

function ISLockDoor:stop()
	if not self:isValid() then
		self.character:faceThisObject(self.door)
		self.character:getEmitter():playSound("DoorIsLocked")
	end
    ISBaseTimedAction.stop(self);
end

function ISLockDoor:perform()
    if self.lock then
        self.door:setLockedByKey(true);
        self.character:getEmitter():playSound("LockDoor");
--        getSoundManager():PlayWorldSound("lockDoor", self.door:getSquare(), 0, 10, 0.7, true);
    else
        self.door:setLockedByKey(false);
        self.character:getEmitter():playSound("UnlockDoor");
--        getSoundManager():PlayWorldSound("unlockDoor", self.door:getSquare(), 0, 10, 0.7, true);
    end
	-- needed to remove from queue / start next.
	ISBaseTimedAction.perform(self);
end

function ISLockDoor:new(character, door, lock)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.door = door;
    o.lock = lock
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = 0;
	return o;
end
