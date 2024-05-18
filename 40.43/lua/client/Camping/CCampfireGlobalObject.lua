--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "Map/CGlobalObject"

CCampfireGlobalObject = CGlobalObject:derive("CCampfireGlobalObject")

function CCampfireGlobalObject:new(luaSystem, isoObject)
	local o = CGlobalObject.new(self, luaSystem, isoObject)
	return o
end

function CCampfireGlobalObject:getObject()
	return self:getIsoObject()
end

