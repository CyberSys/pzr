require "ISBaseObject"

BaseBehaviour = ISBaseObject:derive("BaseBehaviour");

function BaseBehaviour:new ()
    local o = {}
    setmetatable(o, self)
    self.__index = self

    return o
end

