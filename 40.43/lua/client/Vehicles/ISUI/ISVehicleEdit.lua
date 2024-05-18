ISVehicleEdit = ISCollapsableWindow:derive("ISVehicleEdit")

-----

local TopView = ISPanel:derive("TopView")

function TopView_render(self)
	self:drawRectBorder(0, 0, self:getWidth(), self:getHeight(), 1.0, 0.5, 0.5, 0.5);

	if not self.vehicle then return end

	local script = self.vehicle:getScript()
	
	local extents = script:getExtents()
	local ratio = extents:x() / extents:z()
	local length = self.width * 0.6
	local width = length * ratio
	local ex = (self.width - length) / 2
	local ey = (self.height - width) / 2
	self:drawRectBorder(ex, ey, length, width, 1.0, 1.0, 1.0, 1.0)

	local selected = self.parent:selectedAreas()
	local areas = {}
	for i=1,script:getAreaCount() do
		local t = {}
		t.area = script:getArea(i-1)
		t.selected = false
		t.index = i
		for j=1,#selected do
			if selected[j] == t.area then
				t.selected = true
				break
			end
		end
		table.insert(areas, t)
	end

	-- put selected areas last
	table.sort(areas, function(a,b)
		if a.selected and not b.selected then return false end
		if not a.selected and b.selected then return true end
		return a.index < b.index
		end
		)

	local scale = length / extents:z() * script:getModelScale()
	for _,t in ipairs(areas) do
		local area = t.area
		local ax,ay,aw,ah = area:getX(),area:getY(),area:getW(),area:getH()
		local r,g,b = 1,1,1
		if t.selected then
			r = 0
			b = 0
		end
		self:drawRectBorder(
			self.width / 2 + (ay - ah / 2) * scale,
			self.height / 2 - (ax + aw / 2) * scale,
			ah * scale, aw * scale,
			1.0, r, g, b);
	end
end

function snapTo(value, size, min, max)
	if value < min then return min - size / 2 end
	if value > max then return max + size / 2 end
	return value
end

function TopView_onMouseMove(self, dx, dy)
	if self.mouseDown and self.vehicle then

		local script = self.vehicle:getScript()

		local extents = script:getExtents()
		local ratio = extents:x() / extents:z()
		local length = self.width * 0.6
		local width = length * ratio

		local ex = (self.width - length) / 2
		local ey = (self.height - width) / 2

		local scale = length / extents:z() * script:getModelScale()

		local x = self.height / 2 - self:getMouseY()
		local y = self:getMouseX() - self.width / 2

		local halfExtents = {
			extents:x() / 2 / script:getModelScale(),
			extents:z() / 2 / script:getModelScale()
		}

		local selected = self.parent:selectedAreas()
		for i=1,#selected do
			local area = selected[i]
			area:setX(snapTo(x / scale, area:getW(), -halfExtents[1], halfExtents[1]))
			area:setY(snapTo(y / scale, area:getH(), -halfExtents[2], halfExtents[2]))
			self.parent:onMouseDownListbox(area)
			self.parent:alignSymmetry(area)
			self.parent:alignPassengerPositions(area)
		end
	end
end

function TopView:render()
	TopView_render(self)
end

function TopView:onMouseDown(x, y)
	self.mouseDown = true
end

function TopView:onMouseUp(x, y)
	self.mouseDown = false
end

function TopView:onMouseUpOutside(x, y)
	self.mouseDown = false
end

function TopView:onMouseMove(dx, dy)
	TopView_onMouseMove(self, dx, dy)
end

function TopView:setVehicle(vehicle)
	self.vehicle = vehicle
end

function TopView:new(x, y, width, height)
	local o = ISPanel:new(x, y, width, height)
	setmetatable(o, self)
	self.__index = self
	return o
end


-----

function ISVehicleEdit:onMouseDownListbox(area)
	local scale = self.vehicle:getScript():getModelScale()
	self.entryX:setText(round(area:getX() * scale, 4)..'')
	self.entryY:setText(round(area:getY() * scale, 4)..'')
	self.entryW:setText(round(area:getW() * scale, 4)..'')
	self.entryH:setText(round(area:getH() * scale, 4)..'')
end

function ISVehicleEdit:alignSymmetry(area)
	if not self.symmetry:isSelected(1) then return end
	local id = area:getId()
	if id:contains('Left') then
		id = id:gsub('Left', 'Right')
	elseif id:contains('Right') then
		id = id:gsub('Right', 'Left')
	end
	local area2 = self.vehicle:getScript():getAreaById(id)
	if area2 then
		area2:setY(area:getY())
	end
end

function ISVehicleEdit:alignPassengerPositions(area)
	local script = self.vehicle:getScript()
	if not area:getId():contains('Seat') then return end
	local id = area:getId():gsub('Seat', '')
	local pngr = script:getPassengerById(id)
	if not pngr then return end
	pngr:getPositionById("inside"):getOffset():setComponent(2, area:getY())
	pngr:getPositionById("outside"):getOffset():setComponent(2, area:getY())

	if self.symmetry:isSelected(1) then
		id = id:contains('Left') and id:gsub('Left', 'Right') or id:gsub('Right', 'Left')
		local pngr2 = script:getPassengerById(id)
		if pngr2 then
			pngr2:getPositionById("inside"):getOffset():setComponent(2, area:getY())
			pngr2:getPositionById("outside"):getOffset():setComponent(2, area:getY())
		end
	end
end

function ISVehicleEdit:onTextEditedX()
	local value = tonumber(self:getText()) -- self = ISTextEntryBox
	if not value then return end
	local scale = self.parent.vehicle:getScript():getModelScale()
	value = value / scale
	self.parent.listbox.items[self.parent.listbox.selected].item:setX(value)
	self:alignSymmetry(area)
	self:alignPassengerPositions(area)
end

function ISVehicleEdit:onTextEditedY()
	local value = tonumber(self:getText()) -- self = ISTextEntryBox
	if not value then return end
	local scale = self.parent.vehicle:getScript():getModelScale()
	value = value / scale
	self.parent.listbox.items[self.parent.listbox.selected].item:setY(value)
end

function ISVehicleEdit:onTextEditedW()
	local value = tonumber(self:getText()) -- self = ISTextEntryBox
	local scale = self.parent.vehicle:getScript():getModelScale()
	value = value / scale
	self.parent.listbox.items[self.parent.listbox.selected].item:setW(value)
end

function ISVehicleEdit:onTextEditedH()
	local value = tonumber(self:getText()) -- self = ISTextEntryBox
	local scale = self.parent.vehicle:getScript():getModelScale()
	value = value / scale
	self.parent.listbox.items[self.parent.listbox.selected].item:setH(value)
end

function ISVehicleEdit:selectedAreas()
	local item = self.listbox.items[self.listbox.selected]
	return item and { item.item } or {}
end

function ISVehicleEdit:autoAlign()
	if not self.vehicle then return end
	local script = self.vehicle:getScript()

	local extents = script:getExtents()
	local modelScale = script:getModelScale()

	local ts = { 'FrontLeft', 'FrontRight', 'RearLeft', 'RearRight' }
	for _,t in ipairs(ts) do
		local wheel = script:getWheelById(t)
		local area = script:getAreaById('Tire'..t)
		area:setY(wheel:getOffset():z())
		if t:contains('Left') then
			area:setX(extents:x() / 2 / modelScale + area:getW() / 2)
		else
			area:setX(-(extents:x() / 2 / modelScale + area:getW() / 2))
		end

		local pngr = script:getPassengerById(t)
		if pngr then
			local sign = t:contains('Left') and 1 or -1
			local position = pngr:getPositionById("inside")
			if position then
				position:getOffset():setComponent(0, 0.2 * sign)
			end
			position = pngr:getPositionById("outside")
			if position then
				-- Important: don't align exactly with the edge of the vehicle, else
				-- rounding errors may result in a collision when pathfinding here.
				-- Character radius is 0.3, so add some padding.
				local radius = 0.35
				position:getOffset():setComponent(0, (extents:x() / 2 + radius) / modelScale * sign)
			end
		end
	end

	local area = script:getAreaById('Engine')
	area:setX(0)
	area:setY(extents:z() / 2 / modelScale + area:getH() / 2)

	area = script:getAreaById('TruckBed')
	area:setX(0)
end

local function xyzStr(x, y, z)
	return ''..round(x, 4)..' '..round(y, 4)..' '..round(z, 4)
end

function ISVehicleEdit:copyToClipboard()
	if not self.vehicle then return end
	local script = self.vehicle:getScript()
	local text = ''
	for i=1,script:getPassengerCount() do
		local pngr = script:getPassenger(i-1)
		local p1 = pngr:getPositionById("inside")
		local p2 = pngr:getPositionById("outside")
		text = text .. 'passenger '..pngr:getId()..'\n{'
		text = text .. '\n\tposition inside\n\t{\n'
		text = text .. '\t\toffset = '..xyzStr(p1:getOffset():x(), p1:getOffset():y(), p1:getOffset():z())..',\n'
		text = text .. '\t\trotate = 0.0 0.0 0.0,\n'
		text = text .. '\t}\n'
		text = text .. '\tposition outside\n\t{\n'
		if p2 then -- no 'outside' position for some rear seats
			text = text .. '\t\toffset = '..xyzStr(p2:getOffset():x(), p2:getOffset():y(), p2:getOffset():z())..',\n'
			text = text .. '\t\trotate = 0.0 0.0 0.0,\n'
		end
		text = text .. '\t}\n}\n'
	end
	text = text .. '\n'
	for i=1,script:getAreaCount() do
		local area = script:getArea(i-1)
		text = text .. 'area '..area:getId()..'\n{\n\txywh = '..round(area:getX(),4)..' '..round(area:getY(),4)..' '..round(area:getW(),4)..' '..round(area:getH(),4)..',\n}\n'
	end
	local lines = text:split('\n')
	local indent = ''
	for _,line in ipairs(lines) do
		indent = indent..'\t\t'..line..'\n'
	end
	Clipboard.setClipboard(indent)
end

function ISVehicleEdit:prerender()
	local vehicle = getPlayer():getVehicle()
	if vehicle then
		-- vehicle:getScript() can change via the reloadVehicles() lua command
		if vehicle ~= self.vehicle or vehicle:getScript() ~= self.script then
			self:setVehicle(getPlayer():getVehicle())
		end
	end
	ISCollapsableWindow.prerender(self)
end

function ISVehicleEdit:createChildren()
	ISCollapsableWindow.createChildren(self)

	self.scriptName = ISLabel:new(10, 30, 24, "Script: ", 1, 1, 1, 1, UIFont.Medium, true)
	self:addChild(self.scriptName)

	self.listbox = ISScrollingListBox:new(10, 60, 150, self.height - 60 - 20)
	self.listbox:initialise()
	self.listbox:instantiate()
	self.listbox:setAnchorLeft(true)
	self.listbox:setAnchorRight(false)
	self.listbox:setAnchorTop(true)
	self.listbox:setAnchorBottom(false)
	self.listbox.itemheight = 24
	self.listbox.itemPadY = 0
	self.listbox.drawBorder = false
	self.listbox.backgroundColor.a = 0
--	self.listbox.doDrawItem = ISVehicleMechanics.doDrawItem
--	self.listbox.onRightMouseUp = ISVehicleMechanics.onListRightMouseUp
--	self.listbox.onMouseDown = ISVehicleMechanics.onListMouseDown
	self.listbox:setOnMouseDownFunction(self, self.onMouseDownListbox)
	self.listbox.parent = self
	self.listbox:setFont(UIFont.Medium)
	self:addChild(self.listbox)

	local fontHgt = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight()
	local entryHgt = fontHgt + 2 * 2
	local entryWid = 100

	local x = self.listbox:getRight() + 20
	local y = self.listbox:getY()

	local label = ISLabel:new(x, y, entryHgt, "X:", 1, 1, 1, 1, UIFont.Small, true)
	self:addChild(label)
	self.entryX = ISTextEntryBox:new("0.0", x + 20, y, entryWid, entryHgt)
	self.entryX:initialise()
	self.entryX:instantiate()
	self.entryX.parent = self
	self.entryX.onCommandEntered = self.onTextEditedX
	self:addChild(self.entryX)
	y = y + entryHgt + 6

	label = ISLabel:new(x, y, entryHgt, "Y:", 1, 1, 1, 1, UIFont.Small, true)
	self:addChild(label)
	self.entryY = ISTextEntryBox:new("0.0", x + 20, y, entryWid, entryHgt)
	self.entryY:initialise()
	self.entryY:instantiate()
	self.entryY.parent = self
	self.entryY.onCommandEntered = self.onTextEditedY
	self:addChild(self.entryY)
	y = y + entryHgt + 6

	label = ISLabel:new(x, y, entryHgt, "W:", 1, 1, 1, 1, UIFont.Small, true)
	self:addChild(label)
	self.entryW = ISTextEntryBox:new("0.0", x + 20, y, entryWid, entryHgt)
	self.entryW:initialise()
	self.entryW:instantiate()
	self.entryW.parent = self
	self.entryW.onCommandEntered = self.onTextEditedW
	self:addChild(self.entryW)
	y = y + entryHgt + 6

	label = ISLabel:new(x, y, entryHgt, "H:", 1, 1, 1, 1, UIFont.Small, true)
	self:addChild(label)
	self.entryH = ISTextEntryBox:new("0.0", x + 20, y, entryWid, entryHgt)
	self.entryH:initialise()
	self.entryH:instantiate()
	self.entryH.parent = self
	self.entryH.onCommandEntered = self.onTextEditedH
	self:addChild(self.entryH)
	y = y + entryHgt + 6

	self.symmetry = ISTickBox:new(self.entryX:getRight() + 20, self.entryX:getY(), 100, entryHgt, "")
	self.symmetry:initialise()
	self:addChild(self.symmetry)
	self.symmetry:addOption("Symmetry")

	self.topView = TopView:new(x, y, 300, 150)
	self.topView.parent = self
	self:addChild(self.topView)
	y = self.topView:getBottom() + 16

	self.auto = ISButton:new(x, y, 200, 24, "Auto Align", self, self.autoAlign)
	self.auto:setTooltip("Place TireXYZ next to wheels.\nAlign Engine + TruckBed\nSet passenger offset x")
	self:addChild(self.auto)
	y = self.auto:getBottom() + 16

	self.clipboard = ISButton:new(x, y, 200, 24, "Copy To Clipboard", self, self.copyToClipboard)
	self:addChild(self.clipboard)
	y = self.clipboard:getBottom() + 16
end

function ISVehicleEdit:setVehicle(vehicle)
	self.vehicle = vehicle
	self.script = vehicle and vehicle:getScript() or nil
	self.topView.vehicle = vehicle
	self.listbox:clear()
	if self.vehicle then
		local script = vehicle:getScript()
		self.scriptName.name = 'Script: '..script:getName()
		for i=1,script:getAreaCount() do
			local area = script:getArea(i-1)
			self.listbox:addItem(area:getId(), area)
		end
	else
	end
end

function ISVehicleEdit:new()
	local width = 500
	local height = 400
	local x = (getCore():getScreenWidth() / 2) - (width / 2)
	local y = (getCore():getScreenHeight() / 2) - (height / 2)

	local o = ISCollapsableWindow:new(x, y, width, height)
	setmetatable(o, self)
	self.__index = self
--	o:setResizable(false)
	o.title = "Edit Vehicle"
	return o
end

local ui = nil

function editVehicle()
	ui = ui or ISVehicleEdit:new()
	ui:setVisible(true)
	ui:addToUIManager()
	ui:setVehicle(getPlayer():getVehicle())
end

