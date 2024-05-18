require "ISUI/ISPanel"
require "ISUI/ISButton"
require "ISUI/ISInventoryPane"
require "ISUI/ISResizeWidget"
require "ISUI/ISMouseDrag"

require "defines"

ChatPanel = ISPanel:derive("ChatPanel");


function ChatPanel:initialise()
	ISPanel.initialise(self);
end

--************************************************************************--
--** ISPanel:instantiate
--**
--************************************************************************--
function ChatPanel:createChildren()


	self.chatOutput = ChatOutput:new(0, 0, self.width, self.height-18);
	self.chatOutput:initialise();
	self.chatOutput:setAnchorBottom(true);
	self:addChild(self.chatOutput);

	self.textEntry = ISTextEntryBox:new("", 0, self.height-18, self.width, 18);
	self.textEntry:initialise();
	self.textEntry:instantiate();
	self.textEntry:setAnchorTop(false);
	self.textEntry:setAnchorBottom(true);
	self.textEntry.onCommandEntered = ChatPanel.onCommandEntered;
	self:addChild(self.textEntry);

end

function ChatPanel:onCommandEntered()
	local command = ChatPanel.instance.textEntry:getText();
	ChatPanel.instance.textEntry:clear();
	IRCClient.send(command);

end
function ChatPanel:print(str)
	self.chatOutput:print(str);
end

function ChatPanel:new (x, y, width, height)
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
	ChatPanel.instance = o;
	return o
end

function ChatPanelPrintLine(line)
	----print(line);
	ChatPanel.instance:print(line);
end

