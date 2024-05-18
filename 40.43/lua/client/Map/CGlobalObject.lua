--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "ISBaseObject"

CGlobalObject = ISBaseObject:derive("CGlobalObject")

function CGlobalObject:noise(message)
	self.luaSystem:noise(message)
end

function CGlobalObject:new(luaSystem, isoObject)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.luaSystem = luaSystem
	o.x = isoObject:getX()
	o.y = isoObject:getY()
	o.z = isoObject:getZ()
	o:fromModData(isoObject:getModData())
	return o
end

function CGlobalObject:getIsoObject()
	return self.luaSystem:getIsoObjectAt(self.x, self.y, self.z)
end

function CGlobalObject:getSquare()
	return getCell():getGridSquare(self.x, self.y, self.z)
end

function CGlobalObject:fromModData(modData)
	for k,v in pairs(modData) do
		self[k] = v
	end
end

-- TimedAction and UI code should call this repeatedly to keep this luaObject
-- in sync with the isoObject, which may be updated at any time.
function CGlobalObject:updateFromIsoObject()
	local isoObject = self:getIsoObject()
	if isoObject then
		self:fromModData(isoObject:getModData())
	end
end

