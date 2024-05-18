require "ISBaseObject"

ISDoor = ISBaseObject:derive("ISDoor");

ISDoor.IDMax = 1;

--************************************************************************--
--** ISDoor:new
--**
--************************************************************************--
function ISDoor:new (x, y, z, spriteName, north)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.x = x;
	o.y = y;
	o.z = z;
	local cell = getWorld():getCell();
	local sq = cell:getGridSquare(x, y, z);
	o.javaObject = IsoDoor.new(cell, sq, spriteName, north, true);
	sq:AddSpecialObject(o.javaObject);
	return o
end

--************************************************************************--
--** ISDoor:new
--**
--************************************************************************--
function ISDoor:new (x, y, z, door)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.x = x;
	o.y = y;
	o.z = z;
	o.javaObject = door;
	return o
end

