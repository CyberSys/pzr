--***********************************************************
--**              	  ROBERT JOHNSON                       **
--***********************************************************

ISMapSymbols = ISPanelJoypad:derive("ISMapSymbols");
ISMapSymbols.messages = {};

--************************************************************************--
--** ISMapSymbols:initialise
--**
--************************************************************************--

function ISMapSymbols:initialise()
    ISPanelJoypad.initialise(self);
    local btnWid = 100
    local btnHgt = 25
    local btnHgt2 = 20
    local padBottom = 10

    self.no = ISButton:new(10, self:getHeight() - padBottom - btnHgt, btnWid, btnHgt, getText("UI_Close"), self, ISMapSymbols.onClick);
    self.no.internal = "CANCEL";
    self.no.anchorTop = false
    self.no.anchorBottom = true
    self.no:initialise();
    self.no:instantiate();
    self.no.borderColor = {r=1, g=1, b=1, a=0.4};
    self:addChild(self.no);

    self:populateSymbolList();
    local inv = self.character:getInventory();

    self.blackColorBtn = ISButton:new(30, 60, btnHgt2, btnHgt2, "", self, ISMapSymbols.onClick);
    self.blackColorBtn:initialise();
    self.blackColorBtn.internal = "COLOR";
    self.blackColorBtn.backgroundColor = {r = 0, g = 0, b = 0, a = 1};
    self.blackColorBtn.borderColor = {r=1, g=1, b=1, a=0.4};
    self:addChild(self.blackColorBtn);
    self.blackColorBtn.enable = inv:contains("Pen", true);
    if not self.blackColorBtn.enable then
        self.blackColorBtn.tooltip = getText("Tooltip_Map_NeedBlackPen");
    end

    self.greyColorBtn = ISButton:new(self.blackColorBtn.x + self.blackColorBtn.width + 5, 60, btnHgt2, btnHgt2, "", self, ISMapSymbols.onClick);
    self.greyColorBtn:initialise();
    self.greyColorBtn.internal = "COLOR";
    self.greyColorBtn.backgroundColor = {r = 0.2, g = 0.2, b = 0.2, a = 1};
    self.greyColorBtn.borderColor = {r=1, g=1, b=1, a=0.4};
    self:addChild(self.greyColorBtn);
    self.greyColorBtn.enable = inv:contains("Pencil", true);
    if not self.greyColorBtn.enable then
        self.greyColorBtn.tooltip = getText("Tooltip_Map_NeedPencil");
    end

    self.redColorBtn = ISButton:new(self.greyColorBtn.x + self.greyColorBtn.width + 5, 60, btnHgt2, btnHgt2, "", self, ISMapSymbols.onClick);
    self.redColorBtn:initialise();
    self.redColorBtn.internal = "COLOR";
    self.redColorBtn.backgroundColor = {r = 1, g = 0, b = 0, a = 1};
    self.redColorBtn.borderColor = {r=1, g=1, b=1, a=0.4};
    self:addChild(self.redColorBtn);
    self.redColorBtn.enable = (inv:contains("RedPen", true));
    if not self.redColorBtn.enable then
        self.redColorBtn.tooltip = getText("Tooltip_Map_NeedRedPen");
    end

    self.blueColorBtn = ISButton:new(self.redColorBtn.x + self.redColorBtn.width + 5, 60, btnHgt2, btnHgt2, "", self, ISMapSymbols.onClick);
    self.blueColorBtn:initialise();
    self.blueColorBtn.internal = "COLOR";
    self.blueColorBtn.backgroundColor = {r = 0, g = 0, b = 1, a = 1};
    self.blueColorBtn.borderColor = {r=1, g=1, b=1, a=0.4};
    self:addChild(self.blueColorBtn);
    self.blueColorBtn.enable = (inv:contains("BluePen", true));
    if not self.blueColorBtn.enable then
        self.blueColorBtn.tooltip = getText("Tooltip_Map_NeedBluePen");
    end
	
	self:insertNewLineOfButtons(self.blackColorBtn, self.greyColorBtn, self.redColorBtn, self.blueColorBtn)
	
	local symbolButtons = {}
    local x = 30;
    local y = 90;
    for i,v in ipairs(self.symbolList) do
        local symbolBtn = ISButton:new(x, y, btnHgt2, btnHgt2, "", self, ISMapSymbols.onClick);
        symbolBtn:initialise();
        symbolBtn:instantiate();
        symbolBtn.borderColor = {r=0, g=0, b=0, a=0};
        symbolBtn.backgroundColor = {r = 0.5, g = 0.5, b = 0.5, a = 1};
        symbolBtn.textureColor = {r = 0, g = 0, b = 0, a = 1};
        self:addChild(symbolBtn);
        symbolBtn.image = getTexture(v);
        symbolBtn.tex = v;
        symbolBtn.symbol = true;
        table.insert(self.buttonList, symbolBtn);
        if not self.selectedSymbol then
            self.selectedSymbol = symbolBtn;
        end
		table.insert(symbolButtons, self.buttonList[#self.buttonList])
        x = x + 60;
        if x == 210 then
            x = 30;
            y = y + 30;
			self:insertNewListOfButtonsList(symbolButtons)
			symbolButtons = {}
        end
    end

    if self.blackColorBtn.enable then
        self.currentColor = ColorInfo.new(0, 0, 0,1);
        self.blackColorBtn.borderColor = {r=1, g=1, b=1, a=1};
    elseif self.greyColorBtn.enable then
        self.currentColor = ColorInfo.new(0.2, 0.2, 0.2,1);
        self.greyColorBtn.borderColor = {r=1, g=1, b=1, a=1};
        self:updateSymbolColors(self.greyColorBtn);
    elseif self.redColorBtn.enable then
        self.currentColor = ColorInfo.new(1, 0, 0,1);
        self.redColorBtn.borderColor = {r=1, g=1, b=1, a=1};
        self:updateSymbolColors(self.redColorBtn);
    else
        self.currentColor = ColorInfo.new(0, 0, 1,1);
        self.blueColorBtn.borderColor = {r=1, g=1, b=1, a=1};
        self:updateSymbolColors(self.blueColorBtn);
    end
    y = y + 30;
--    self.lineBtn = ISButton:new(30, y, 100, btnHgt2, "Draw Line", self, ISMapSymbols.onClick);
--    self.lineBtn:initialise();
--    self.lineBtn.internal = "LINE";
--    self.lineBtn.borderColor = {r=1, g=1, b=1, a=0.4};
--    self:addChild(self.lineBtn);

	self:insertNewLineOfButtons(self.no)
end

function ISMapSymbols:populateSymbolList()
    table.insert(self.symbolList, "media/ui/LootableMaps/map_arroweast.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_arrownorth.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_arrowsouth.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_arrowwest.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_exclamation.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_garbage.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_house.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_lock.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_o.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_skull.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_target.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_x.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_apple.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_lightning.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_question.png");
    table.insert(self.symbolList, "media/ui/LootableMaps/map_trap.png");
end

function ISMapSymbols:prerender()
    ISPanelJoypad.prerender(self);
    local z = 20;
    self:drawText(getText("IGUI_Map_MapSymbol"), self.width/2 - (getTextManager():MeasureStringX(UIFont.Medium, getText("IGUI_Map_MapSymbol")) / 2), z, 1,1,1,1, UIFont.Medium);
end

function ISMapSymbols:render()
    ISPanelJoypad.render(self);
    if self.selectedSymbol then
        self:drawRectBorder(self.selectedSymbol.x - 1, self.selectedSymbol.y - 1, self.selectedSymbol.width + 1, self.selectedSymbol.height + 1, 1, 1, 1, 1);
    end
end

function ISMapSymbols:onClick(button)
    if button.internal == "CANCEL" then
        self:destroy();
    end
    if button.symbol then
        self.selectedSymbol = button;
    end
    if button.internal == "COLOR" then
        self.blackColorBtn.borderColor = {r=1, g=1, b=1, a=0.4};
        self.greyColorBtn.borderColor = {r=1, g=1, b=1, a=0.4};
        self.redColorBtn.borderColor = {r=1, g=1, b=1, a=0.4};
        self.blueColorBtn.borderColor = {r=1, g=1, b=1, a=0.4};
        button.borderColor = {r=1, g=1, b=1, a=1};
        self.currentColor = ColorInfo.new(button.backgroundColor.r, button.backgroundColor.g, button.backgroundColor.b,1);
        self:updateSymbolColors(button);
    end
    if button.internal == "LINE" then
        self.map.drawLine = not self.map.drawLine;
        self.selectedSymbol = nil;
    end
end

function ISMapSymbols:updateSymbolColors(button)
    for i,v in ipairs(self.buttonList) do
        v.textureColor = {r = button.backgroundColor.r, g = button.backgroundColor.b, b = button.backgroundColor.g, a = 1};
    end
end

function ISMapSymbols:destroy()
	self.map.selectedSymbol = self.selectedSymbol
	self.map.selectedColor = self.currentColor
	if JoypadState.players[self.character:getPlayerNum()+1] then
		setJoypadFocus(self.character:getPlayerNum(), self.map)
	end
    self:setVisible(false);
    self:removeFromUIManager();
    self.map.symbolList = nil;
end

function ISMapSymbols:onGainJoypadFocus(joypadData)
    ISPanelJoypad.onGainJoypadFocus(self, joypadData)
    self.joypadIndexY = 1
    self.joypadIndex = 1
    self.joypadButtons = self.joypadButtonsY[self.joypadIndexY]
    self.joypadButtons[self.joypadIndex]:setJoypadFocused(true)
	self.no:setJoypadButton(getTexture("media/ui/xbutton.png"))
end

function ISMapSymbols:onJoypadDown(button)
    ISPanelJoypad.onJoypadDown(self, button)
    if button == Joypad.XButton then
        self:onClick(self.no)
    end
end

--************************************************************************--
--** ISMapSymbols:new
--**
--************************************************************************--
function ISMapSymbols:new(x, y, width, height, map, character)
    local o = {}
    o = ISPanelJoypad:new(x, y, width, height);
    setmetatable(o, self)
    self.__index = self
    o.borderColor = {r=0.4, g=0.4, b=0.4, a=1};
    o.backgroundColor = {r=0, g=0, b=0, a=0.8};
    o.width = width;
    o.height = height;
    o.selectedSymbol = nil;
    o.symbolList = {};
    o.map = map;
    o.buttonList = {};
    o.character = character;
    return o;
end
