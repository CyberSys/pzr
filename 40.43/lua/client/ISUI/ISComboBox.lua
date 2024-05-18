require "ISUI/ISPanel"
require "ISUI/ISScrollingListBox"

ISComboBox = ISPanel:derive("ISComboBox");
ISComboBoxPopup = ISScrollingListBox:derive("ISComboBoxPopup");

function ISComboBoxPopup:new(x, y, width, height)
    local o = ISScrollingListBox:new(x, y, width, height)
    setmetatable(o, self)
    self.__index = self
    self.parentCombo = nil
    return o
end

function ISComboBoxPopup:render()
    ISScrollingListBox.render(self)
    self:drawRectBorderStatic(-1, -1, self:getWidth() + 2, self:getHeight() + 2, 0.8, 1, 1, 1)
end

function ISComboBoxPopup:doDrawItem(y, item, alt)
    local highlight = (self:isMouseOver() and not self:isMouseOverScrollBar()) and self.mouseoverselected or self.selected
    if self.parentCombo.joypadFocused then
        highlight = self.selected
    end
    if highlight == item.index then
        local selectColor = self.parentCombo.backgroundColorMouseOver
        self:drawRect(0, (y), self:getWidth(), item.height-1, selectColor.a, selectColor.r, selectColor.g, selectColor.b)
    end
    local itemPadY = self.itemPadY or (item.height - self.fontHgt) / 2
    self:drawText(item.text, 10, y + itemPadY, self.parentCombo.textColor.r, self.parentCombo.textColor.g, self.parentCombo.textColor.b, self.parentCombo.textColor.a, self.font)
    y = y + item.height
    return y
end

function ISComboBoxPopup:onMouseDown(x, y)
    if not self:isMouseOver() then -- due to setCapture()
        self.parentCombo.expanded = false
        self.parentCombo:hidePopup()
        return
    end
    return true
end

function ISComboBoxPopup:onMouseUp(x, y)
    if self.vscroll then
        self.vscroll.scrolling = false
    end
    if not self:isMouseOver() then -- due to setCapture()
        self.parentCombo.expanded = false
        self.parentCombo:hidePopup()
        return
    end
    if not self.joypadFocused then
        local row = self:rowAt(x, y)
        if row > #self.items then
            row = #self.items
        elseif row < 1 then
            row = 1
        end
        self.parentCombo.selected = row
        self.parentCombo.expanded = false
        self.parentCombo:hidePopup()
        if self.parentCombo.onChange then
            self.parentCombo.onChange(self.parentCombo.target, self.parentCombo, self.parentCombo.onChangeArgs[1], self.parentCombo.onChangeArgs[2])
        end
    end
end

function ISComboBoxPopup:setComboBox(comboBox)
    self:clear()
    for i=1,#comboBox.options do
        self:addItem(comboBox:getOptionText(i), nil)
    end
    self:setYScroll(0)
    self.selected = comboBox.selected
    self:setHeight(math.min(#comboBox.options, 8) * self.itemheight)
    
    self:setX(comboBox:getAbsoluteX())
    self:setWidth(comboBox:getWidth())
    if comboBox.openUpwards or (comboBox:getAbsoluteY() + comboBox:getHeight() + self:getHeight() > getCore():getScreenHeight()) then
        self:setY(comboBox:getAbsoluteY() - self:getHeight())
    else
        self:setY(comboBox:getAbsoluteY() + comboBox:getHeight())
    end
    self.borderColor = { r = comboBox.borderColor.r, g = comboBox.borderColor.g, b = comboBox.borderColor.b, a = comboBox.borderColor.a }
    self.backgroundColor = { r = comboBox.backgroundColor.r, g = comboBox.backgroundColor.g, b = comboBox.backgroundColor.b, a = comboBox.backgroundColor.a }
    self.parentCombo = comboBox
    self:ensureVisible(self.selected)
end

--************************************************************************--
--** ISComboBox:initialise
--**
--************************************************************************--

function ISComboBox:initialise()
	ISPanel.initialise(self);
end

function ISComboBox:createChildren()
    if ISComboBox.SharedPopup then
        self.popup = ISComboBox.SharedPopup
    else
        self.popup = ISComboBoxPopup:new(0, 0, 100, 50)
        self.popup:initialise()
        self.popup:instantiate()
        self.popup:setFont(self.font, 4)
        self.popup:setAlwaysOnTop(true)
        self.popup.drawBorder = true
        self.popup:setCapture(true)
        ISComboBox.SharedPopup = self.popup
    end
end

function ISComboBox:showPopup()
    self.popup:setFont(self.font, 4)
    self.popup:setComboBox(self)
    self.popup:addToUIManager()
end

function ISComboBox:hidePopup()
    self.popup:removeFromUIManager()
end

function ISComboBox:setJoypadFocused(focused)
    self.joypadFocused = focused;
    if self.expanded and not focused then
        self:hidePopup()
        self.expanded = false
    end
end

function ISComboBox:onJoypadDirUp(joypadData)
    self.popup:onJoypadDirUp(joypadData)
end

function ISComboBox:onJoypadDirDown(joypadData)
    self.popup:onJoypadDirDown(joypadData)
end

function ISComboBox:forceClick()
    if self.expanded then
        self.selected = self.popup.selected;
        if self.onChange then
            self.onChange(self.target, self, self.onChangeArgs[1], self.onChangeArgs[2]);
        end
    end
    self.expanded = not self.expanded;
    if self.expanded then
        self:showPopup()
--        self.joypadIndexFocused = self.selected
    else
        self:hidePopup()
    end
end

function ISComboBox:prerender()
    self.fade:setFadeIn(self.joypadFocused or self:isMouseOver())
    self.fade:update()

    self:drawRect(0, 0, self.width, self.height, self.backgroundColor.a, self.backgroundColor.r, self.backgroundColor.g, self.backgroundColor.b);

    if self.expanded then
    elseif not self.joypadFocused then
        self:drawRect(0, 0, self.width, self.height, self.backgroundColorMouseOver.a * 0.5 * self.fade:fraction(), self.backgroundColorMouseOver.r, self.backgroundColorMouseOver.g, self.backgroundColorMouseOver.b);
    else
        self:drawRect(0, 0, self.width, self.height, self.backgroundColorMouseOver.a, self.backgroundColorMouseOver.r, self.backgroundColorMouseOver.g, self.backgroundColorMouseOver.b);
    end
    local alpha = math.min(self.borderColor.a + 0.2 * self.fade:fraction(), 1.0)
	self:drawRectBorder(0, 0, self.width, self.height, alpha, self.borderColor.r, self.borderColor.g, self.borderColor.b);

	local fontHgt = getTextManager():getFontHeight(self.font)
	local y = (self.height - fontHgt) / 2
	if self.options[self.selected] then
		self:clampStencilRectToParent(0, 0, self.width - self.image:getWidthOrig() - 6, self.height)
		self:drawText(self:getOptionText(self.selected), 10, y, self.textColor.r, self.textColor.g, self.textColor.b, self.textColor.a, self.font);
		self:clearStencilRect()
	end

	if self:isMouseOver() and not self.expanded and self:getOptionTooltip(self.selected) then
		local text = self:getOptionTooltip(self.selected)
		if not self.tooltipUI then
			self.tooltipUI = ISToolTip:new()
			self.tooltipUI:setOwner(self)
			self.tooltipUI:setVisible(false)
			self.tooltipUI:setAlwaysOnTop(true)
		end
		if not self.tooltipUI:getIsVisible() then
			if string.contains(text, "\n") then
				self.tooltipUI.maxLineWidth = 1000 -- don't wrap the lines
			else
				self.tooltipUI.maxLineWidth = 300
			end
			self.tooltipUI:addToUIManager()
			self.tooltipUI:setVisible(true)
		end
		self.tooltipUI.description = text
		self.tooltipUI:setX(self:getMouseX() + 23)
		self.tooltipUI:setY(self:getMouseY() + 23)
	else
		if self.tooltipUI and self.tooltipUI:getIsVisible() then
			self.tooltipUI:setVisible(false)
			self.tooltipUI:removeFromUIManager()
		end
    end

    self:drawTexture(self.image, self.width - self.image:getWidthOrig() - 3, (self.baseHeight / 2) - (self.image:getHeight() / 2), 1, 1, 1, 1);
end

function ISComboBox:onMouseDown(x, y)
    self.sawMouseDown = true
	return true
end

--************************************************************************--
--** ISComboBox:onMouseUp
--**
--************************************************************************--
function ISComboBox:onMouseUp(x, y)
    if not self.sawMouseDown then return end
    self.sawMouseDown = false
    self.expanded = not self.expanded;
    if self.expanded then
        self:showPopup();
    else
        self:hidePopup();
        self.mouseOver = self:isMouseOver()
    end
end
function ISComboBox:onMouseDownOutside(x, y)
    self.sawMouseDown = false
	if self.expanded then
		self.expanded = false;
		self:hidePopup()
	end
end
function ISComboBox:onMouseMove(dx, dy)
	self.mouseOver = true;
end

function ISComboBox:select(option)
	for i=1,#self.options do
   	    if k == self:getOptionText(i) then
	       self.selected = i;
	       return;
		end
	end
end

function ISComboBox:addOption(option)

	table.insert(self.options, option);

	if self.selected == 0 then
		for i, k in ipairs(self.options) do
			self.selected = i;
			return;
		end
	end
end

function ISComboBox:addOptionWithData(option, data)
	local item = {}
	item.text = option
	item.data = data
	table.insert(self.options, item);
	if self.selected == 0 then
		self.selected = 1
	end
end

function ISComboBox:getOptionText(index)
	local option = self.options[index]
	if option then
		if type(option) == "table" then
			return option.text
		else
			return option
		end
	end
	return nil
end

function ISComboBox:getOptionTooltip(index)
	local option = self.options[index]
	if option then
		if type(option) == "table" then
			if option.tooltip then
				return option.tooltip
			end
			if self.tooltip then
				return self.tooltip[option.text] or self.tooltip["defaultTooltip"]
			end
		else
			if self.tooltip then
				return self.tooltip[option] or self.tooltip["defaultTooltip"]
			end
		end
	end
	return nil
end

function ISComboBox:onMouseMoveOutside(dx, dy)
	self.mouseOver = false;
end

function ISComboBox:setToolTipMap(tooltipmap)
	self.tooltip = tooltipmap;
end

function ISComboBox:setWidthToOptions(minWidth)
	local width = 0
	for i=1,#self.options do
		local text = self:getOptionText(i)
		width = math.max(width, getTextManager():MeasureStringX(self.font, text or ""))
	end
	width = 10 + width + 5 + self.image:getWidthOrig() + 3
	self:setWidth(math.max(width, minWidth or 0))
end

function ISComboBox:clear()
	self.options = {}
    self.tooltip = {}
end

function ISComboBox:new (x, y, width, height, target, onChange, onChangeArg1, onChangeArg2)
	local o = {}
	--o.data = {}
	o = ISPanel:new(x, y, width, height);
	setmetatable(o, self)
	self.__index = self
	o.x = x;
	o.y = y;
	o.backgroundColor = {r=0, g=0, b=0, a=1};
	o.backgroundColorMouseOver = {r=0.3, g=0.3, b=0.3, a=1.0};
	o.borderColor = {r=1, g=1, b=1, a=0.5};
	o.font = UIFont.Small
    o.textColor = {r=0.9, g=0.9, b=0.9, a=0.9};
	o.width = width;
	o.height = height;
    o.baseHeight = height;
	o.anchorLeft = true;
	o.anchorRight = false;
	o.anchorTop = true;
	o.anchorBottom = false;
	o.target = target;
	o.onChange = onChange;
	o.onChangeArgs = { onChangeArg1, onChangeArg2 }
	o.options = {};
	o.selected = 0;
	o.expanded = false;
	o.mouseOver = false;
	o.tooltip = nil;
    o.isCombobox = true;
--    o.joypadIndexFocused = 1;
    o.image = getTexture("media/ui/ArrowDown.png");
    o.fade = UITransition.new()
	return o
end



-- Event method to check game mode and create K&B tutorial panel if necessary.
testcomboBox = function ()

	--print("start testing combobox");
	local panel2 = ISComboBox:new(150, 0, 100, 18, nil);
	--print("panel2 newed");

	panel2:initialise();
	--print("panel2 initialised");
	panel2:addOption("1024x768");
	panel2:addOption("1920x1080");
	panel2:addToUIManager();
	--print("panel2 added ToUIManager");

--panel2 = ISInventoryPage:new(300, 300, 400+32, 400, getPlayer():getInventory());
--panel2:initialise();
--panel2:addToUIManager();
end

--Events.OnMainMenuEnter.Add(testcomboBox);

