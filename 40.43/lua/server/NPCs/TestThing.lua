require "ISBaseObject"

TestThing = ISBaseObject:derive("TestThing");

function TestThing:getTest(inc)
    if inc == nil then inc = 1 end
    self.thing = self.thing + inc;
   return self.thing;
end

function TestThing:new ()
    local o = {}
    setmetatable(o, self)
    self.__index = self;
    o.thing = 1;
    return o
end
