require "ISUI/ISCollapsableWindow"

DebugOptionsWindow = ISCollapsableWindow:derive("DebugOptionsWindow")

function DebugOptionsWindow:onTickBox(index, selected, option)
	option:setValue(selected)
	getDebugOptions():save()
end

function DebugOptionsWindow:createChildren()
    local fontHgt = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight()
    local entryHgt = fontHgt + 2 * 2

	local x = 12
	local y = self:titleBarHeight() + 6
	local maxWidth = 0

	local options = getDebugOptions()
	for i=1,options:getOptionCount() do
		local option = options:getOptionByIndex(i-1)
		if option:getType() == "boolean" then
			local tickBox = ISTickBox:new(x, y, self.width, entryHgt, "", self, self.onTickBox, option)
			tickBox:initialise()
			tickBox:addOption(option:getName(), option)
			tickBox:setSelected(1, option:getValue())
			tickBox:setWidthToFit()
			self:addChild(tickBox)
			maxWidth = math.max(maxWidth, tickBox:getRight())
		end
		y = y + entryHgt + 6
		if self.y + y + entryHgt + 6 >= getCore():getScreenHeight() then
			x = x + maxWidth
			y = self:titleBarHeight() + 6
			maxWidth = 0
		end
	end

	local width = 0
	local height = 0
	for _,child in pairs(self:getChildren()) do
		width = math.max(width, child:getRight())
		height = math.max(height, child:getBottom())
	end
	self:setWidth(width + 12)
	self:setHeight(height + self:resizeWidgetHeight())
end

function DebugOptionsWindow:onMouseDownOutside(x, y)
	self:setVisible(false);
	self:removeFromUIManager();
end

function DebugOptionsWindow:new (x, y, width, height)
	local o = ISCollapsableWindow:new(x, y, width, height)
	setmetatable(o, self)
	self.__index = self
	o.backgroundColor = {r=0, g=0, b=0, a=1.0}
	o.resizable = false
	return o
end

