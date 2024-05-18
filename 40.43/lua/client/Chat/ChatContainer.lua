require "ISUI/ISPanel"
require "ISUI/ISButton"
require "ISUI/ISInventoryPane"
require "ISUI/ISResizeWidget"
require "ISUI/ISMouseDrag"

require "defines"

ChatContainer = ISPanel:derive("ChatContainer");


function ChatContainer:initialise()
	ISPanel.initialise(self);
end

--************************************************************************--
--** ISPanel:instantiate
--**
--************************************************************************--
function ChatContainer:createChildren()


	self.chatPanel = ChatPanel:new(0, 0, self.width, self.height);
	self.chatPanel:initialise();
	self.chatPanel:setAnchorBottom(true);
	self.chatPanel:setVisible(false);
	self:addChild(self.chatPanel);

	self.chatLogin = ChatNotLoggedOn:new(0, 0, self.width, self.height);

	self.chatLogin:initialise();
	self.chatLogin:setAnchorBottom(true);
	self:addChild(self.chatLogin);
end

function ChatContainer:logon(name)

	self.chatPanel:setVisible(true);
	self.chatLogin:setVisible(false);
	self.chatPanel:print("Logging in...");
	self.connected = true;
	IRCClient.initClient(name);
end

function ChatContainer:update()
	if self.targX ~= self.x then
		if(self.x < self.targX) then
			self:setX(self.x + 40);
			if self.x > self.targX then
				self:setX(self.targX);
			end
		elseif(self.x > self.targX) then
			self:setX(self.x - 40);
			if self.x < self.targX then
				self:setX(self.targX);
			end
		end
	end
	if self.x == -516 then
		self:setVisible(false);
	end

end

function ChatContainer:new (x, y, width, height)

	local o = {}
	--o.data = {}
	o = ISPanel:new(x, y, width, height);

	setmetatable(o, self)
	self.__index = self
	o.x = x;
	o.y = y;
	o.backgroundColor = {r=0, g=0, b=0, a=0.2};
	o.borderColor = {r=1, g=1, b=1, a=0.2};
	o.width = width;
	o.height = height;
	o.anchorLeft = true;
	o.anchorRight = false;
	o.anchorTop = true;
	o.anchorBottom = false;
	o.addY = 0;
	o.targX = x;
	ChatContainer.instance = o;


	return o
end

function onTabToggleChatWindow(key)

	if key == 15 then
		if ChatContainer.instance ~= nil then
			if ChatContainer.instance:getIsVisible()==false then
				ChatContainer.instance.targX = 0;
				ChatContainer.instance:bringToTop();
				ChatContainer.instance:setVisible(true);
				if ChatContainer.instance.chatPanel:getIsVisible() then
					ChatContainer.instance.chatPanel.textEntry:focus();
				end
			else
				ChatContainer.instance.targX = -516;
				ChatContainer.instance.chatPanel.textEntry:unfocus();
			end
		end
	end
end

--Events.OnKeyPressed.Add(onTabToggleChatWindow);


