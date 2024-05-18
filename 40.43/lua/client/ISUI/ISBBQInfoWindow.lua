require "ISUI/ISCollapsableWindow"

ISBBQInfoWindow = ISCollapsableWindow:derive("ISBBQInfoWindow")
ISBBQInfoWindow.windows = {}

function ISBBQInfoWindow:createChildren()
	ISCollapsableWindow.createChildren(self)
	self.panel = ISToolTip:new()
	self.panel.followMouse = false
	self.panel:initialise()
	self:setObject(self.object)
	self:addView(self.panel)
end

local function timeString(timeInMinutes)
	local hourStr = getText("IGUI_Gametime_hour")
	local minuteStr = getText("IGUI_Gametime_minute")
	local hours = math.floor(timeInMinutes / 60)
	local minutes = timeInMinutes % 60
	if hours ~= 1 then hourStr = getText("IGUI_Gametime_hours") end
	if minutes ~= 1 then minuteStr = getText("IGUI_Gametime_minutes") end
	local str = ""
	if hours ~= 0 then
		str = hours .. ' ' .. hourStr
	end
	if str == '' or minutes ~= 0 then
		if str ~= '' then str = str .. ', ' end
		str = str .. minutes .. ' ' .. minuteStr
	end
	return str
end

function ISBBQInfoWindow:update()
	ISCollapsableWindow.update(self)

	if self:getIsVisible() and (not self.object or self.object:getObjectIndex() == -1) then
		if self.joyfocus then
			self.joyfocus.focus = nil
			updateJoypadFocus(self.joyfocus)
		end
		self:removeFromUIManager()
		return
	end

	if self.fuelAmount ~= self.object:getFuelAmount() or self.spriteName ~= self.object:getTextureName() then
		self:setObject(self.object)
	end
	self:setWidth(self.panel:getWidth())
	self:setHeight(self:titleBarHeight() + self.panel:getHeight())
end

function ISBBQInfoWindow:onJoypadDown(button)
	if button == Joypad.BButton then
		self:removeFromUIManager()
		setJoypadFocus(self.playerNum, nil)
	end
end

function ISBBQInfoWindow:setObject(bbq)
	self.object = bbq
	self.panel:setName(bbq:isPropaneBBQ() and getText("IGUI_BBQ_TypePropane") or getText("IGUI_BBQ_TypeCharcoal"))
	self.spriteName = bbq:getTextureName()
	self.fuelAmount = bbq:getFuelAmount()
	self.panel:setTexture(self.spriteName)
	self.panel.description = getText("IGUI_BBQ_FuelAmount", timeString(self.fuelAmount))
	if bbq:isPropaneBBQ() and not bbq:hasPropaneTank() then
		self.panel.description = self.panel.description .. " <LINE> <RGB:1,0,0> " .. getText("IGUI_BBQ_NeedsPropaneTank")
	end
end

function ISBBQInfoWindow:onGainJoypadFocus(joypadData)
	self.drawJoypadFocus = true
end

function ISBBQInfoWindow:close()
	self:removeFromUIManager()
end

function ISBBQInfoWindow:new(x, y, character, bbq)
	local width = 320
	local height = 16 + 64 + 16 + 16
	local o = ISCollapsableWindow:new(x, y, width, height)
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.playerNum = character:getPlayerNum()
	o.object = bbq
	o:setResizable(false)
	return o
end
