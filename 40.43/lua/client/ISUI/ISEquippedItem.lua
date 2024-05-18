--***********************************************************
--**                    ROBERT JOHNSON                     **
--***********************************************************

ISEquippedItem = ISPanel:derive("ISEquippedItem");
ISEquippedItem.text = nil;

function ISEquippedItem:prerender()
	self:drawTexture(self.HandSecondaryTexture, -1, 50, 1, 1, 1, 1);

    if self.invBtn == nil then
        return;
    end
	if self.inventory ~= nil and self.inventory:getIsVisible() then
		self.invBtn:setImage(self.inventoryTextureOn);
	else
		self.invBtn:setImage(self.inventoryTexture);
    end
    if getPlayerCraftingUI(0) and getPlayerCraftingUI(0):getIsVisible() then
        self.craftingBtn:setImage(self.craftingIconOn);
    else
        self.craftingBtn:setImage(self.craftingIcon);
    end
    if getPlayerInfoPanel(0) and getPlayerInfoPanel(0):getIsVisible() then
        self.healthBtn:setImage(self.heartIconOn);
    else
        self.healthBtn:setImage(self.heartIcon);
    end

    if self.movableBtn:isMouseOver() then
        self.movableTooltip:setVisible(false);
        self.movableBtn:setVisible(false);
        self.movablePopup:setVisible(true);
    elseif self.movablePopup:isMouseOver() then
        --
    elseif getCell():getDrag(0) and getCell():getDrag(0).isMoveableCursor then
        if getCell():getDrag(0):getMoveableMode() == "pickup" then
            self.movableBtn:setImage(self.movableIconPickup);
        elseif getCell():getDrag(0):getMoveableMode() == "place" then
            self.movableBtn:setImage(self.movableIconPlace);
        elseif getCell():getDrag(0):getMoveableMode() == "rotate" then
            self.movableBtn:setImage(self.movableIconRotate);
        elseif getCell():getDrag(0):getMoveableMode() == "scrap" then
            self.movableBtn:setImage(self.movableIconScrap);
        end
        self.movableTooltip:setVisible(true);
        self.movableBtn:setVisible(true);
        self.movablePopup:setVisible(false);
    else
        self.movableTooltip:setVisible(false);
        self.movableBtn:setImage(self.movableIcon);
        self.movableBtn:setVisible(true);
        self.movablePopup:setVisible(false);
    end

    if self.debugBtn then
        if ISDebugMenu.instance then
            self.debugBtn:setImage(self.debugIconOn);
        else
            self.debugBtn:setImage(self.debugIcon);
        end
    end

end

function ISEquippedItem:render()
	if self.chr:getPrimaryHandItem() then
		local item = self.chr:getPrimaryHandItem()
		if item:getTex() and item:getTex():getWidth() <= 32 and item:getTex():getHeight() <= 32 then
			self:drawTextureScaled(item:getTex(),(self.handMainTexture:getWidth() / 2) - (item:getTex():getWidth() / 2),(self.handMainTexture:getHeight() / 2) - (item:getTex():getHeight() / 2),item:getTex():getWidth(), item:getTex():getHeight(), item:getA(),item:getR(),item:getG(),item:getB());
		else
			self:drawTextureScaledAspect(item:getTex(), self.handMainTexture:getWidth() / 2 - 16, self.handMainTexture:getHeight() / 2 - 16, 32, 32, item:getA(),item:getR(),item:getG(),item:getB());
		end
		if instanceof(item,"HandWeapon") then
			local n = math.floor(((item:getCondition() / item:getConditionMax()) * 5));

			if(item:getCondition() > 0 and n == 0) then
				n = 1;
			end

			self:drawTexture(getTexture("media/ui/QualityStar_" .. n .. ".png"),5,10,1,1,1,1);
		end
	end
	if self.chr:getSecondaryHandItem() then
		local item = self.chr:getSecondaryHandItem()
		local width = 24
		local height = 24
		if item:getTex() and item:getTex():getWidth() <= width and item:getTex():getHeight() <= height then
			width = self.HandSecondaryTexture:getWidthOrig()
			height = self.HandSecondaryTexture:getHeightOrig()
			self:drawTextureScaled(item:getTex(), (width - item:getTex():getWidth()) / 2, 50 + (height - item:getTex():getHeight()) / 2, item:getTex():getWidth(), item:getTex():getHeight(), item:getA(),item:getR(),item:getG(),item:getB());
		else
			self:drawTextureScaledAspect(item:getTex(), 0 + (self.HandSecondaryTexture:getWidthOrig() - width) / 2, 50 + (self.HandSecondaryTexture:getHeightOrig() - height) / 2, width, height, item:getA(),item:getR(),item:getG(),item:getB());
		end
	end

    if self.invBtn == nil then
        return;
    end

	if ISEquippedItem.text then
		self:drawText(ISEquippedItem.text, 50,0,1,1,1,1,UIFont.Medium);
    end

    self:checkToolTip();
end

function ISEquippedItem:onOptionMouseDown(button, x, y)
	if button.internal == "INVENTORY" then
        self.inventory:setVisible(not self.inventory:getIsVisible());
        self.loot:setVisible(self.inventory:getIsVisible());

    elseif button.internal == "HEALTH" then
	--	xpUpdate.toggleCharacterInfo(self.chr);
		self.infopanel:toggleView(getText("IGUI_XP_Health"));
    elseif button.internal == "CRAFTING" then
        ISCraftingUI.toggleCraftingUI();
    elseif button.internal == "DEBUG" and (getCore():getDebug() or ISDebugMenu.forceEnable) then
        if ISDebugMenu.instance then
            ISDebugMenu.instance:close();
        else
            ISDebugMenu.OnOpenPanel();
        end
    end

end

local activateCounter = 0;
local activateTicks = 120; -- the actual value * 2;
local lastId = 0;
function ISEquippedItem:checkToolTip()
    local mx, my = getMouseX(), getMouseY();
    local mouseOverID, tooltiptext = -1, nil;
    if self.mouseOverList ~= nil then
        for k,v in ipairs(self.mouseOverList) do
            if self:checkBounds(v.object, mx, my) then
                mouseOverID = k;
                tooltiptext = v.displayString;
            end
        end
    end
    local activate = false;
    if mouseOverID > 0 then
        if activateCounter < activateTicks then
            activateCounter = activateCounter+1;
        end
        if activateCounter > activateTicks/2 then
            activate = true;
        elseif mouseOverID~=lastId then
            --reset counting
            activateCounter = 0;
        end
        lastId = mouseOverID;
    elseif activateCounter > 0 then
        activateCounter = activateCounter-1;
    end
    self:doToolTip(activate, tooltiptext);
end

function ISEquippedItem:doToolTip( _state, _text )
    if _state then
        if self.toolTip == nil then
            self.toolTip = ISToolTip:new(item);
            self.toolTip:initialise();
            self.toolTip:addToUIManager();
            self.toolTip:setOwner(self);
            self.toolTip:setWidth(100);
            self.toolTip.description = _text;
            self.toolTip:doLayout();
            self.toolTip:setVisible(true);
        else
            if self.toolTip then
                if self.toolTip:getIsVisible()==false then
                    self.toolTip:setVisible(true);
                    self.toolTip:addToUIManager();
                end
                if self.toolTip.description ~= _text then
                    self.toolTip.description = _text;
                    self.toolTip:doLayout();
                end

                self.toolTip:bringToTop();
            end
        end
    else
        if self.toolTip and self.toolTip:getIsVisible() then
            self.toolTip:removeFromUIManager();
            self.toolTip:setVisible(false);
        end
    end
end

function ISEquippedItem:checkBounds( _boundsItem, _x, _y)
    if _boundsItem~=nil and _x>=_boundsItem:getX() and _x<=_boundsItem:getX()+_boundsItem:getWidth() and _y>=_boundsItem:getY() and _y<=_boundsItem:getY()+_boundsItem:getHeight() then
        return true;
    end
    return false;
end

function ISEquippedItem:new (x, y, width, height, chr)
	local o = {}
	--o.data = {}
	o = ISPanel:new(x, y, width, height);
	setmetatable(o, self)
    self.__index = self
	o.x = x;
	o.y = y;
    o.borderColor = {r=0.4, g=0.4, b=0.4, a=1};
    o.backgroundColor = {r=0, g=0, b=0, a=0.5};
	o.width = width;
	o.height = height;
    o.inventory = getPlayerInventory(chr:getPlayerNum());
    o.loot = getPlayerLoot(chr:getPlayerNum());
    o.infopanel = getPlayerInfoPanel(chr:getPlayerNum());
    o.anchorLeft = true;
    o.chr = chr;
	o.anchorRight = false;
	o.anchorTop = true;
	o.anchorBottom = false;
	o.handMainTexture = getTexture("media/ui/HandMain2_Off.png");
	o.HandSecondaryTexture = getTexture("media/ui/HandSecondary2_Off.png");
	o.inventoryTexture = getTexture("media/ui/Inventory2_Off.png");
	o.inventoryTextureOn = getTexture("media/ui/Inventory2_On.png");
    o.craftingIcon = getTexture("media/ui/Carpentry_Off.png");
    o.craftingIconOn = getTexture("media/ui/Carpentry_On.png");
	o.heartIcon = getTexture("media/ui/Heart2_Off.png");
	o.heartIconOn = getTexture("media/ui/Heart2_On.png");
    o.movableIcon = getTexture("media/ui/Furniture_Off2.png");
    o.movableIconPickup = getTexture("media/ui/Furniture_Pickup.png");
    o.movableIconPlace = getTexture("media/ui/Furniture_Place.png");
    o.movableIconRotate = getTexture("media/ui/Furniture_Rotate.png");
    o.movableIconScrap = getTexture("media/ui/Furniture_Disassemble.png");
    o.debugIcon = getTexture("media/ui/Debug_Icon_Off.png");
    o.debugIconOn = getTexture("media/ui/Debug_Icon_On.png");
    ISEquippedItem.instance = o;
	return o;
end

local function createFakeObject(_x,_y,_w,_h)
    local self = {};
    self.x = _x;
    self.y = _y;
    self.w = _w;
    self.h = _h;
    function self:getX() return self.x end
    function self:getY() return self.y end
    function self:getWidth() return self.w end
    function self:getHeight() return self.h end
    return self;
end

function ISEquippedItem:addMouseOverToolTipItem( _object, _displayString )
    if self.mouseOverList == nil then
        self.mouseOverList = {};
    end
    if _object~=nil and _object.getX and _object.getY and _object.getWidth and _object.getHeight then
        table.insert(self.mouseOverList, { object = _object, displayString =  _displayString } );
    end
end

function ISEquippedItem:initialise()

	ISPanel.initialise(self);

    if self.chr:getPlayerNum() == 0 then
        self:addMouseOverToolTipItem(createFakeObject(11,11,44,44), getText("IGUI_PrimaryTooltip") );
        self:addMouseOverToolTipItem(createFakeObject(15,60,33,33), getText("IGUI_SecondaryTooltip") );
        -- inv btn
        self.invBtn = ISButton:new(0, 90, self.inventoryTexture:getWidthOrig(), self.inventoryTexture:getHeightOrig(), "", self, ISEquippedItem.onOptionMouseDown);
        self.invBtn:setImage(self.inventoryTexture);
        self.invBtn.internal = "INVENTORY";
        self.invBtn:initialise();
        self.invBtn:instantiate();
        self.invBtn:setDisplayBackground(false);

        self.invBtn.borderColor = {r=1, g=1, b=1, a=0.1};
        self.invBtn:ignoreWidthChange();
        self.invBtn:ignoreHeightChange();
        self:addChild(self.invBtn);
        self:addMouseOverToolTipItem(self.invBtn, getText("IGUI_InventoryTooltip") );
        -- health btn
        self.healthBtn = ISButton:new(0, self.invBtn:getY() + self.inventoryTexture:getHeightOrig() + 5, self.heartIcon:getWidthOrig(), self.heartIcon:getHeightOrig(), "", self, ISEquippedItem.onOptionMouseDown);
        self.healthBtn:setImage(self.heartIcon);
        self.healthBtn.internal = "HEALTH";
        self.healthBtn:initialise();
        self.healthBtn:instantiate();
        self.healthBtn:setDisplayBackground(false);

        self.healthBtn.borderColor = {r=1, g=1, b=1, a=0.1};
        self.healthBtn:ignoreWidthChange();
        self.healthBtn:ignoreHeightChange();
        self:addChild(self.healthBtn);
        self:addMouseOverToolTipItem(self.healthBtn, getText("IGUI_HealthTooltip") );

        self.craftingBtn = ISButton:new(0, self.healthBtn:getY() + self.heartIcon:getHeightOrig() + 5, self.craftingIcon:getWidthOrig(), self.craftingIcon:getHeightOrig(), "", self, ISEquippedItem.onOptionMouseDown);
        self.craftingBtn:setImage(self.craftingIcon);
        self.craftingBtn.internal = "CRAFTING";
        self.craftingBtn:initialise();
        self.craftingBtn:instantiate();
        self.craftingBtn:setDisplayBackground(false);

        self.craftingBtn.borderColor = {r=1, g=1, b=1, a=0.1};
        self.craftingBtn:ignoreWidthChange();
        self.craftingBtn:ignoreHeightChange();
        self:addChild(self.craftingBtn);
        self:addMouseOverToolTipItem(self.craftingBtn, getText("IGUI_CraftingTooltip") );

        self.movableBtn = ISButton:new(0, self.craftingBtn:getY() + self.craftingIcon:getHeightOrig() + 5, self.movableIcon:getWidthOrig(), self.movableIcon:getHeightOrig(), "", self, ISEquippedItem.onOptionMouseDown);
        self.movableBtn:setImage(self.movableIcon);
        self.movableBtn.internal = "MOVABLE";
        self.movableBtn:initialise();
        self.movableBtn:instantiate();
        self.movableBtn:setDisplayBackground(false);

        self.movableBtn.borderColor = {r=1, g=1, b=1, a=0.1};
        self.movableBtn:ignoreWidthChange();
        self.movableBtn:ignoreHeightChange();

        self.movableTooltip = ISMoveablesIconToolTip:new (8+(self.movableIcon:getWidthOrig()/2), self.craftingBtn:getY() + self.craftingIcon:getHeightOrig() + 5 + 3, 120, self.movableIcon:getHeightOrig()-6,self.movableIcon:getWidthOrig()/2);

        local texWid = self.movableIconPickup:getWidthOrig()
        local texHgt = self.movableIconPickup:getHeightOrig()
        self.movablePopup = ISMoveablesIconPopup:new(10 + self.movableBtn:getX(), 10 + self.movableBtn:getY(), texWid * 5, texHgt)
        self.movablePopup.owner = self
        self.movablePopup:addToUIManager()
        self.movablePopup:setVisible(false)

        self:addChild(self.movableTooltip);
        self:addChild(self.movableBtn);
        self:addMouseOverToolTipItem(self.movableBtn, getText("IGUI_MovableTooltip") );

        if getCore():getDebug() or (ISDebugMenu.forceEnable and not isClient()) then
            local texWid = self.debugIcon:getWidthOrig()
            self.debugBtn = ISButton:new(5, self.movableBtn:getY() + self.movableIcon:getHeightOrig() + 20, self.debugIcon:getWidthOrig(), self.debugIcon:getHeightOrig(), "", self, ISEquippedItem.onOptionMouseDown);
            self.debugBtn:setImage(self.debugIcon);
            self.debugBtn.internal = "DEBUG";
            self.debugBtn:initialise();
            self.debugBtn:instantiate();
            self.debugBtn:setDisplayBackground(false);

            self.debugBtn.borderColor = {r=1, g=1, b=1, a=0.1};
            self.debugBtn:ignoreWidthChange();
            self.debugBtn:ignoreHeightChange();

            self:addChild(self.debugBtn);

            self:setHeight(self.debugBtn:getBottom())
        else
            self:setHeight(self.movableBtn:getBottom())
        end
    end


	self.mainHand = ISImage:new(0, 0, 1, 1, self.handMainTexture);
	self.mainHand:initialise();
	self:addChild(self.mainHand);
end

function ISEquippedItem:removeFromUIManager()
	if self.movablePopup then
		self.movablePopup:removeFromUIManager()
	end
	ISPanel.removeFromUIManager(self)
end

-----

ISMoveablesIconPopup = ISPanel:derive("ISMoveablesIconPopup")

function ISMoveablesIconPopup:prerender()
    self:setAlwaysOnTop(true)
end

function ISMoveablesIconPopup:render()

    local playerID = self.owner.chr:getPlayerNum()
    local mode = nil
    if getCell():getDrag(playerID) and getCell():getDrag(playerID).isMoveableCursor then
        mode = getCell():getDrag(playerID):getMoveableMode()
    end

    local fontHgt = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight()
    self:drawRect(0, 0, self.width, self.height + fontHgt + 2 * 2, UIManager.isFBOActive() and 0.5 or 0.35, 0, 0, 0)

    local index = math.floor(self:getMouseX() / 50)
    if index > 0 or mode then
        self:drawRect(index * 50, 0, 50, self.height, 0.15, 1, 1, 1)
    end
    
    local texts = { getText("IGUI_Exit"), getText("IGUI_Pickup"), getText("IGUI_Place"), getText("IGUI_Rotate"), getText("IGUI_Scrap") }
    if not mode then
        texts[1] = ""
    end
    local text = texts[index+1]
    self:drawText(text, 2, self.height + 2, 1.0, 0.85, 0.05, 1.0, UIFont.Small)

    local x = 0
    local y = 0
    local tex = self.owner.movableIcon
    self:drawTexture(tex, x, y, 1, 1, 1, 1)

    if mode == "pickup" then
        self:drawRectBorder(x + 50, 0, 50, self.height, 0.5, 1, 1, 1)
    end
    tex = self.owner.movableIconPickup
    self:drawTexture(tex, x + 50, y, 1, 1, 1, 1)

    if mode == "place" then
        self:drawRectBorder(x + 50 * 2, 0, 50, self.height, 0.5, 1, 1, 1)
    end
    tex = self.owner.movableIconPlace
    self:drawTexture(tex, x + 50 * 2, y, 1, 1, 1, 1)

    if mode == "rotate" then
        self:drawRectBorder(x + 50 * 3, 0, 50, self.height, 0.5, 1, 1, 1)
    end
    tex = self.owner.movableIconRotate
    self:drawTexture(tex, x + 50 * 3, y, 1, 1, 1, 1)

    if mode == "scrap" then
        self:drawRectBorder(x + 50 * 4, 0, 50, self.height, 0.5, 1, 1, 1)
    end
    tex = self.owner.movableIconScrap
    self:drawTexture(tex, x + 50 * 4, y, 1, 1, 1, 1)
end

function ISMoveablesIconPopup:onMouseDown(x, y)
    return true
end

function ISMoveablesIconPopup:onMouseUp(x, y)
    local playerID = self.owner.chr:getPlayerNum()
    local cursor
    if getCell():getDrag(playerID) and getCell():getDrag(playerID).isMoveableCursor then
        cursor = getCell():getDrag(playerID)
    end

    local index = math.floor(x / 50)
    local mode = nil
    if index == 0 then
        if cursor then
            cursor:exitCursor()
        end
        return
    elseif index == 1 then
        mode = "pickup"
    elseif index == 2 then
        mode = "place"
    elseif index == 3 then
        mode = "rotate"
    elseif index == 4 then
        mode = "scrap"
    end
    if not cursor then
        cursor = ISMoveableCursor:new(self.owner.chr)
        getCell():setDrag(cursor, cursor.player)
    end
    cursor:setMoveableMode(mode)
    self:setVisible(false)
    return true
end

function ISMoveablesIconPopup:new (x, y, width, height)
    local o = ISPanel:new(x, y, width, height);
    setmetatable(o, self)
    self.__index = self
    return o
end

-----

function launchEquippedItem(chr)
	local panel = ISEquippedItem:new(10, 10, 100, 250, chr);
    panel:initialise();
	panel:addToUIManager();
    return panel;
end

--Events.OnCreateUI.Add(launchEquippedItem);
