require "ISUI/ISCollapsableWindow"

MetaWorldWindow = ISCollapsableWindow:derive("MetaWorldWindow");


function MetaWorldWindow:initialise()

	ISCollapsableWindow.initialise(self);


end

function MetaWorldWindow:createChildren()
	--print("instance");
	ISCollapsableWindow.createChildren(self);

	self.renderPanel = MetaWorldRenderPanel:new(0, 16, self.width, self.height-16);
	self.renderPanel:initialise();
	self.renderPanel:setAnchorRight(true);
	self.renderPanel:setAnchorBottom(true);
	self:addChild(self.renderPanel);

end


function MetaWorldWindow:new (x, y, width, height)
	local o = {}
	--o.data = {}
	o = ISCollapsableWindow:new(x, y, width, height);
	setmetatable(o, self)
	self.__index = self
	o.backgroundColor = {r=0, g=0, b=0, a=1.0};
	return o
end



DoDebugMetaWorldWindow = function ()

	local panel2 = MetaWorldWindow:new(0, 0, 700, 700);
	panel2:initialise();
	panel2:addToUIManager();
 --   panel2:collapse();

--	getWorld():setDrawWorld(false);
--	CharacterInfoPage.doInfo(SurvivorFactory:CreateSurvivor());
end

--Events.OnGameStart.Add(DoDebugMetaWorldWindow);

