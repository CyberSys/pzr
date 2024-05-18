--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "TimedActions/ISBaseTimedAction"

ISPathFindAction = ISBaseTimedAction:derive("ISPathFindAction")

function ISPathFindAction:isValid()
	return true
end

function ISPathFindAction:update()
	if instanceof(self.character, "IsoPlayer") and
			(self.character:pressedMovement() or self.character:pressedCancelAction()) then
		self:forceStop()
		return
	end
	local result = self.character:getPathFindBehavior2():update()
	if result == BehaviorResult.Failed then
		self:forceStop()
		return
	end
	if result == BehaviorResult.Succeeded then
		self:forceComplete()
	end
end

function ISPathFindAction:start()
	if self.goal[1] == 'LocationF' then
		self.character:getPathFindBehavior2():pathToLocationF(self.goal[2], self.goal[3], self.goal[4])
	end
	if self.goal[1] == 'VehicleArea' then
		self.character:getPathFindBehavior2():pathToVehicleArea(self.goal[2], self.goal[3])
	end
	if self.goal[1] == 'VehicleSeat' then
		self.character:getPathFindBehavior2():pathToVehicleSeat(self.goal[2], self.goal[3])
	end
end

function ISPathFindAction:stop()
	ISBaseTimedAction.stop(self)
	self.character:getPathFindBehavior2():cancel()
	self.character:setPath2(nil)
	if self.onFailFunc then
		local args = self.onFailArgs
		self.onFailFunc(args[1], args[2], args[3], args[4])
	end
end

function ISPathFindAction:perform()
	self.character:getPathFindBehavior2():cancel()
	self.character:setPath2(nil)
	ISBaseTimedAction.perform(self)
	if self.onCompleteFunc then
		local args = self.onCompleteArgs
		self.onCompleteFunc(args[1], args[2], args[3], args[4])
	end
end

function ISPathFindAction:setOnComplete(func, arg1, arg2, arg3, arg4)
	self.onCompleteFunc = func
	self.onCompleteArgs = { arg1, arg2, arg3, arg4 }
end

function ISPathFindAction:setOnFail(func, arg1, arg2, arg3, arg4)
	self.onFailFunc = func
	self.onFailArgs = { arg1, arg2, arg3, arg4 }
end

function ISPathFindAction:pathToLocationF(character, targetX, targetY, targetZ)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.stopOnWalk = false
	o.stopOnRun = false
	o.maxTime = -1
	o.goal = { 'LocationF', targetX, targetY, targetZ }
	return o
end

function ISPathFindAction:pathToVehicleArea(character, vehicle, areaId)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.stopOnWalk = false
	o.stopOnRun = false
	o.maxTime = -1
	o.goal = { 'VehicleArea', vehicle, areaId }
	return o
end

function ISPathFindAction:pathToVehicleSeat(character, vehicle, seat)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.stopOnWalk = false
	o.stopOnRun = false
	o.maxTime = -1
	o.goal = { 'VehicleSeat', vehicle, seat }
	return o
end

-- Debug function for testing pathfinding.
function ISPathFindAction_pathToLocationF(targetX, targetY, targetZ)
	local playerObj = getSpecificPlayer(0)
	ISTimedActionQueue.add(ISPathFindAction:pathToLocationF(playerObj, targetX, targetY, targetZ))
end

