--***********************************************************
--**                    ROBERT JOHNSON                     **
--** Simple collapsable window wich handle our farming info panel **
--***********************************************************

require "ISUI/ISCollapsableWindow"

ISFarmingWindow = ISCollapsableWindow:derive("ISFarmingWindow");


function ISFarmingWindow:initialise()

	ISCollapsableWindow.initialise(self);
end

function ISFarmingWindow:visible(visible)
	ISFarmingWindow.instance:setVisible(visible);
end


--************************************************************************--
--** ISPanel:instantiate
--**
--************************************************************************--
function ISFarmingWindow:createChildren()
	ISCollapsableWindow.createChildren(self);
	self.farmingPanel = ISFarmingInfo:new(0, 16, self.width, self.height-16, self.character, self.plant);
	self.farmingPanel:initialise();
	self:addChild(self.farmingPanel);
end

function ISFarmingWindow:new (x, y, width, height, character, plant)
	local o = {}
	--o.data = {}
	o = ISCollapsableWindow:new(x, y, width, height);
	o:setResizable(false)
	setmetatable(o, self)
	self.__index = self
	self.farmingPanel = {};
	o.character = character
	o.plant = plant;
	return o
end
