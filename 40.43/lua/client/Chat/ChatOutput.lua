require "ISUI/ISPanel"
require "ISUI/ISButton"
require "ISUI/ISInventoryPane"
require "ISUI/ISResizeWidget"
require "ISUI/ISMouseDrag"

require "defines"

ChatOutput = ISPanel:derive("ChatOutput");


function ChatOutput:initialise()
	ISPanel.initialise(self);
end

--************************************************************************--
--** ISPanel:instantiate
--**
--************************************************************************--
function ChatOutput:createChildren()

	self:addScrollBars();
end

function ChatOutput:onMouseWheel(del)
	self:setYScroll(self:getYScroll() - (del*18));
    return true;
end

function ChatOutput:print(str)

	if string.len(str) > 80 then
		while(string.len(str) > 80) do
			local a = string.sub(str, 0, 80);
			self.lines:add(a);
			self:setYScroll(self:getYScroll()-18);
			str = string.sub(str, 81);
		end
	end
	self:setScrollHeight((self.lines:size()*18));
	self.lines:add(str);
	self:setYScroll(-1000000000000);
end

function ChatOutput:render()
	self:setStencilRect(0,0,self.width-16, self.height);

	local y = 3;
	for i=0, self.lines:size() -1 do
		local line = self.lines:get(i);
		self:drawText(line, 0, y, 1, 1, 1, 1, UIFont.Small);
		y = y + 18;

	end

	self:setScrollHeight(y);
	self:clearStencilRect();
end


function ChatOutput:new (x, y, width, height)
	local o = {}
	--o.data = {}
	o = ISPanel:new(x, y, width, height);

	setmetatable(o, self)
	ChatOutput.instance = o;
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
	o.lines = LuaList:new();
	return o
end


