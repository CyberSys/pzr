require "ISUI/ISPanel"
require "ISUI/ISButton"
require "ISUI/ISInventoryPane"
require "ISUI/ISResizeWidget"
require "ISUI/ISMouseDrag"

require "defines"

ChatNotLoggedOn = ISPanel:derive("ChatNotLoggedOn");


function ChatNotLoggedOn:initialise()
	ISPanel.initialise(self);
end

--************************************************************************--
--** ISPanel:instantiate
--**
--************************************************************************--
function ChatNotLoggedOn:createChildren()


	self.textEntryLabel = ISLabel:new(170, 110, 50, "Username: ", 1, 1, 1, 1, UIFont.Medium);
	self.textEntryLabel:initialise();
	self.textEntryLabel:instantiate();
	self.textEntryLabel:setAnchorLeft(true);
	self.textEntryLabel:setAnchorRight(true);
	self.textEntryLabel:setAnchorTop(true);
	self.textEntryLabel:setAnchorBottom(true);
	self:addChild(self.textEntryLabel);

	self.textEntry = ISTextEntryBox:new("BobSmith", self.textEntryLabel:getX() + self.textEntryLabel:getWidth() + 17, 128, 128, 18);
	self.textEntry:initialise();
	self.textEntry:instantiate();
	self.textEntry:setAnchorLeft(true);
	self.textEntry:setAnchorRight(true);
	self.textEntry:setAnchorTop(true);
	self.textEntry:setAnchorBottom(true);

	self:addChild(self.textEntry);


	self.playButton = ISButton:new(self.textEntry:getX() + self.textEntry:getWidth() + 17, 124, 100, 25, "LOGIN", self, ChatNotLoggedOn.onOptionMouseDown);
	self.playButton.internal = "LOGIN";
	self.playButton:initialise();
	self.playButton:instantiate();
	self.playButton:setAnchorLeft(false);
	self.playButton:setAnchorRight(true);
	self.playButton:setAnchorTop(false);
	self.playButton:setAnchorBottom(true);
	self.playButton.borderColor = {r=1, g=1, b=1, a=0.1};
	self:addChild(self.playButton);

end


function ChatNotLoggedOn:onOptionMouseDown(button, x, y)
	--print(button.internal);
	if button.internal == "LOGIN" then
		self.parent:logon(self.textEntry:getText());
	end
end


function ChatNotLoggedOn:prerender()
	ISPanel.prerender(self);

	self:drawTextCentre("Join #projectzomboid chat server on irc.freenode.net!", self.width / 2, 80, 1, 1, 1, 1, UIFont.Small);
end

function ChatNotLoggedOn:new (x, y, width, height)
	local o = {}
	--o.data = {}
	o = ISPanel:new(x, y, width, height);

	setmetatable(o, self)
	self.__index = self
	o.x = x;
	o.y = y;
	o.backgroundColor = {r=0, g=0, b=0, a=0.8};
	o.borderColor = {r=1, g=1, b=1, a=0.2};
	o.width = width;
	o.height = height;
	o.anchorLeft = true;
	o.anchorRight = false;
	o.anchorTop = true;
	o.anchorBottom = false;
	o.addY = 0;
	return o
end

