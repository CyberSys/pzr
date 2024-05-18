require "ISUI/ISPanel"

DebugToolstrip = ISPanel:derive("DebugToolstrip");

function DebugToolstrip:onMapClick()
    self.mapWindow = StreamMapWindow:new(150, 150, 700+200, 700);
    self.mapWindow:initialise();
    self.mapWindow:addToUIManager();
end

function DebugToolstrip:onOptionsClick()
    self.settingsWindow = DebugOptionsWindow:new(self.debugOptions:getX(), self.debugOptions:getBottom(), 300, 400)
    self.settingsWindow:initialise()
    self.settingsWindow:addToUIManager()
end

function DebugToolstrip:createChildren()
    local x = 24;
    self.mapView = ISButton:new(x, 12, 48, 28, "Map", self, DebugToolstrip.onMapClick);
    self.mapView:initialise();
    self:addChild(self.mapView);

    self.debugOptions = ISButton:new(self.mapView:getRight() + 24, 12, 48, 28, "Options", self, DebugToolstrip.onOptionsClick)
    self.debugOptions:initialise()
    self:addChild(self.debugOptions)
end

function DebugToolstrip:new (x, y, width, height)
    local o = ISPanel:new(x, y, width, height);
    setmetatable(o, self)
    self.__index = self
    o.x = x;
    o.y = y;
    o.backgroundColor = {r=0.2, g=0.3, b=0.4, a=0.3};
    o.borderColor = {r=1, g=1, b=1, a=0.7};

    return o
end
