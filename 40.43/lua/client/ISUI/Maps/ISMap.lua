--***********************************************************
--**              	  ROBERT JOHNSON                       **
--***********************************************************

require "ISUI/ISPanelJoypad"

ISMap = ISPanelJoypad:derive("ISMap");


--************************************************************************--
--** ISMap:initialise
--**
--************************************************************************--

function ISMap:initialise()
	ISPanelJoypad.initialise(self);
    --
    self.zoom = ISImage:new(0,0,self.width,self:getHeight(), self.tex);
    --self.zoom.parent = self;
    self.zoom.scaledWidth = self:getWidth();
    self.zoom.scaledHeight = self.zoom.scaledWidth * self.prop;
    self.zoom.prerender = ISMap.onPrerenderMap
    self.zoom.onMouseDown = ISMap.onMouseDownMap;
    self.zoom.onMouseUp = ISMap.onMouseUpMap;
    self.zoom:initialise();
    self.zoom:instantiate();
    self:addChild(self.zoom);

    local buttonHgt = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight() + 6
    local buttonPadBottom = 4
    local buttonY = self.height - buttonPadBottom - buttonHgt

    self.ok = ISButton:new(10, buttonY, 100, buttonHgt, getText("UI_Close"), self, ISMap.onClick);
    self.ok.internal = "OK";
    self.ok:initialise();
    self.ok:instantiate();
    self.ok.borderColor = {r=1, g=1, b=1, a=0.4};
    self:addChild(self.ok);

    self.addnoteBtn = ISButton:new(self.ok.x + self.ok.width + 10, buttonY, 150, buttonHgt, getText("IGUI_Map_AddNote"), self, ISMap.onClick);
    self.addnoteBtn.internal = "ADDNOTE";
    self.addnoteBtn:initialise();
    self.addnoteBtn:instantiate();
    self.addnoteBtn.borderColor = {r=1, g=1, b=1, a=0.4};
    self:addChild(self.addnoteBtn);

    self.removenoteBtn = ISButton:new(self.addnoteBtn.x + self.addnoteBtn.width + 10, buttonY, 150, buttonHgt, getText("IGUI_Map_RemoveElement"), self, ISMap.onClick);
    self.removenoteBtn.internal = "REMOVENOTE";
    self.removenoteBtn:initialise();
    self.removenoteBtn:instantiate();
    self.removenoteBtn.borderColor = {r=1, g=1, b=1, a=0.4};
    self:addChild(self.removenoteBtn);

    self.symbolBtn = ISButton:new(self.removenoteBtn.x + self.removenoteBtn.width + 10, buttonY, 150, buttonHgt, getText("IGUI_Map_AddSymbol"), self, ISMap.onClick);
    self.symbolBtn.internal = "ADDSYMBOL";
    self.symbolBtn:initialise();
    self.symbolBtn:instantiate();
    self.symbolBtn.borderColor = {r=1, g=1, b=1, a=0.4};
    self:addChild(self.symbolBtn);

    self.testBtn = ISButton:new(self.symbolBtn.x + self.symbolBtn.width + 10, buttonY, 50, buttonHgt, getText("IGUI_Map_Scale"), self, ISMap.onClick);
    self.testBtn.internal = "SCALE";
    self.testBtn:initialise();
    self.testBtn:instantiate();
    self.testBtn.borderColor = {r=1, g=1, b=1, a=0.4};
    self:addChild(self.testBtn);
	
    self.placeSymbBtn = ISButton:new(self.symbolBtn.x + self.symbolBtn.width + 10, buttonY, 150, buttonHgt, getText("IGUI_Map_PlaceSymbol"), self, ISMap.onClick);
    self.placeSymbBtn.internal = "PLACESYMBOL";
    self.placeSymbBtn:initialise();
    self.placeSymbBtn:instantiate();
    self.placeSymbBtn.borderColor = {r=1, g=1, b=1, a=0.4};
	self.placeSymbBtn:setVisible(false)
    self:addChild(self.placeSymbBtn);

--    self.testBtn2 = ISButton:new(self.testBtn.x + self.testBtn.width + 10, self:getHeight() - 22, 50, 20, "Del All", self, ISMap.onClick);
--    self.testBtn2.internal = "REMOVALL";
--    self.testBtn2:initialise();
--    self.testBtn2:instantiate();
--    self.testBtn2.borderColor = {r=1, g=1, b=1, a=0.4};
--    self:addChild(self.testBtn2);

    --    local newNote = {};
--    newNote.text = "noooooooooooooooote";
--    newNote.x = 200;
--    newNote.y = 200;
--    print("added new note", newNote.x, newNote.y, newNote.text);
--    table.insert(self.notes, newNote);

--	self:insertNewLineOfButtons(self.ok, self.removenoteBtn, self.symbolBtn, self.testBtn)

end

function ISMap:destroy()
    if self.symbolList then
        self.symbolList:destroy();
    end
    if self.modal then
        self.modal:setVisible(false);
        self.modal:removeFromUIManager();
    end
    self:setVisible(false);
    self:removeFromUIManager();
    if JoypadState.players[self.playerNum+1] then
        setJoypadFocus(self.playerNum, nil)
    end
end

function ISMap:onClick(button)
    if button.internal == "OK" then
        self.wrap:close();
        if JoypadState.players[self.playerNum+1] then
            setJoypadFocus(self.playerNum, nil)
        end
    end
    if button.internal == "ADDNOTE" then
        if self.symbolList then
            self.symbolList:destroy();
        end
        self.addingNote = not self.addingNote;
        self.removingElement = false;
        if self.symbolList then self.symbolList:destroy() end
    end
    if button.internal == "REMOVENOTE" then
		if self.joyfocus ~= nil then
			self:checkElementsForRemoveFromJoy()
            if self.mouseOverNote then
                self.modal = ISModalDialog:new(0,0, 250, 150, getText("IGUI_Map_ConfirmRemoveNote",self.mouseOverNote.text), true, self, ISMap.onConfirmRemove, self.playerNum, self.mouseOverNote);
                self.modal:initialise()
                self.modal:addToUIManager()
                self.modal:setAlwaysOnTop(true)
                self.removingElement = false;
				local player = self.character:getPlayerNum()
				if JoypadState.players[player+1] then
					setJoypadFocus(player, self.modal)
				end
            end
            if self.mouseOverSymbol then
                self.modal = ISModalDialog:new(0,0, 250, 150, getText("IGUI_Map_ConfirmRemoveSymbol"), true, self, ISMap.onConfirmRemoveSymbol, self.playerNum, self.mouseOverSymbol);
                self.modal:initialise()
                self.modal:addToUIManager()
                self.modal:setAlwaysOnTop(true)
                self.removingElement = false;
				local player = self.character:getPlayerNum()
				if JoypadState.players[player+1] then
					setJoypadFocus(player, self.modal)
				end
            end
			return
		end
        self.removingElement = not self.removingElement;
        self.addingNote = false;
        if self.symbolList then self.symbolList:destroy() end
    end
    if button.internal == "ADDSYMBOL" then
        self.removingElement = false;
        self.addingNote = false;
		local player = self.character:getPlayerNum()
        if not self.symbolList then
            self.symbolList = ISMapSymbols:new(self.wrap.x + self.wrap.width, self.wrap.y, 200, self.wrap.height, self, self.character);
            self.symbolList:initialise();
            self.symbolList:addToUIManager();
			self.symbolList:setAlwaysOnTop(true)
			if JoypadState.players[player+1] then
				setJoypadFocus(player, self.symbolList)
			end
        else
            self.symbolList:destroy();
        end
    end
    if button.internal == "SCALE" then
        self.zoom.scaledWidth = self.tex:getWidth();
        self.zoom.scaledHeight = self.tex:getHeight();
    end
    if button.internal == "REMOVALL" then
        self.mapObj:getModData()["notes"] = {};
        self.mapObj:getModData()["symbols"] = {};
    end
    if button.internal == "PLACESYMBOL" then
		local xScale = self.zoom.scaledWidth/self.tex:getWidth();
		local yScale = self.zoom.scaledHeight/self.tex:getHeight();
		local newSymbol = {};
		-- store the X/Y at a 1:1 scale
		newSymbol.x =  math.floor((self.width/2-self.zoom.javaObject:getX()-(9*xScale))/xScale);
		newSymbol.y =  math.floor((self.height/2-self.zoom.javaObject:getY()-(9*yScale))/yScale);
		print("add new symbol", newSymbol.x ..",".. newSymbol.y, newSymbol.r,newSymbol.g,newSymbol.b,newSymbol.symbol)
		debugLuaTable(child,1)
		if self.selectedSymbol ~= nil then
            newSymbol.symbol = self.selectedSymbol.tex;
            newSymbol.r = self.selectedColor:getR();
            newSymbol.g = self.selectedColor:getG();
            newSymbol.b = self.selectedColor:getB();
            print("add new symbol", newSymbol.x ..",".. newSymbol.y, newSymbol.r,newSymbol.g,newSymbol.b,newSymbol.symbol)
            table.insert(self.mapObj:getModData()["symbols"],newSymbol);
		end
    end
end

function ISMap:truePrerender()
    ISPanelJoypad.prerender(self);
	self:drawRect(0, 0, self.width, self.height, self.backgroundColor.a, self.backgroundColor.r, self.backgroundColor.g, self.backgroundColor.b);
	self:drawRectBorder(0, 0, self.width, self.height, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);
--    self.zoom:setStencilRect(-self.zoom.x+1,-self.zoom.y+1,self.width-2, self.height-2);
end

function ISMap:onMouseDownMap()
--    print("OnMouseDownMap");
    self.parent:onMouseDown();
end

function ISMap:onMouseDown()
--    print("OnMouseDown");
    self.draggingStartingX = self:getMouseX();
    self.draggingStartingY = self:getMouseY();
    self.dragging = true;
end

function ISMap:onMouseUpMap()
    self.parent:onMouseUp();
end

function ISMap:setWrapVisible(bVisible)
    if self.javaObject then
        self.javaObject:setVisible(bVisible)
        if not bVisible then
            self.mapUI:destroy();
        end
    end
end

function ISMap:onMouseUp()
    -- get the current scale we are on
    local xScale = self.zoom.scaledWidth/self.tex:getWidth();
    local yScale = self.zoom.scaledHeight/self.tex:getHeight();
--    print("size", self.zoom.scaledWidth.."/"..self.zoom.scaledHeight, self.tex:getWidth().."/"..self.tex:getHeight())
    --meeh
--    print("OnMouseUpX", self.zoom:getX(), self.zoom:getMouseX(), xScale);
--    print("OnMouseUpY", self.zoom:getY(), self.zoom:getMouseY(), yScale);

    self:replaceMap();
    if self.modal then
        self.modal:setVisible(false);
        self.modal:removeFromUIManager();
    end
    self.dragging = false;
    if self:getMouseX() == self.draggingStartingX and self:getMouseY() == self.draggingStartingY then
--        print("symbolX/Y",math.floor((self.zoom:getMouseX())/xScale), math.floor((self.zoom:getMouseY())/yScale), self:getMouseX(), self:getMouseY())
        if self.addingNote then
            -- store the X/Y at a 1:1 scale
            self.noteX =  math.floor(self.zoom:getMouseX()/xScale);
            self.noteY =  math.floor((self.zoom:getMouseY()-(9*xScale))/yScale);
--            print("add note on ", self.noteX, self.noteY)
            self.modal = ISTextBoxMap:new(0, 0, 280, 180, getText("IGUI_Map_AddNote"), "", nil, ISMap.onAddNote, self.playerNum, self);
            self.modal:initialise();
            self.modal.noEmpty = true;
            self.modal:addToUIManager();
            self.modal.entry:focus();
            self.addingNote = false;
            self.modal.maxChars = 45;
        end
        if self.removingElement then
            if self.mouseOverNote then
                self.modal = ISModalDialog:new(0,0, 250, 150, getText("IGUI_Map_ConfirmRemoveNote",self.mouseOverNote.text), true, self, ISMap.onConfirmRemove, self.playerNum, self.mouseOverNote);
                self.modal:initialise()
                self.modal:addToUIManager()
                self.modal:setAlwaysOnTop(true)
                self.removingElement = false;
            end
            if self.mouseOverSymbol then
                self.modal = ISModalDialog:new(0,0, 250, 150, getText("IGUI_Map_ConfirmRemoveSymbol"), true, self, ISMap.onConfirmRemoveSymbol, self.playerNum, self.mouseOverSymbol);
                self.modal:initialise()
                self.modal:addToUIManager()
                self.modal:setAlwaysOnTop(true)
                self.removingElement = false;
            end
        end
        if self.symbolList and self.symbolList.selectedSymbol then
            local newSymbol = {};
            -- store the X/Y at a 1:1 scale
            newSymbol.x =  math.floor((self.zoom:getMouseX()-(9*xScale))/xScale);
            newSymbol.y =  math.floor((self.zoom:getMouseY()-(9*xScale))/yScale);
            newSymbol.symbol = self.symbolList.selectedSymbol.tex;
            newSymbol.r = self.symbolList.currentColor:getR();
            newSymbol.g = self.symbolList.currentColor:getG();
            newSymbol.b = self.symbolList.currentColor:getB();
            print("add new symbol", newSymbol.x ..",".. newSymbol.y, newSymbol.r,newSymbol.g,newSymbol.b,newSymbol.symbol)
            table.insert(self.mapObj:getModData()["symbols"],newSymbol);
        end
--        if self.drawLine then
--            self.drawLineOriginalX = self:getMouseX();
--            self.drawLineOriginalY = self:getMouseY();
--            print("original x/y", self.drawLineOriginalX, self.drawLineOriginalY)
--        end
    end
    self.draggingStartingX = 0;
    self.draggingStartingY = 0;
end

function ISMap:canWrite()
    local inv = self.character:getInventory();
    return inv:contains("Pen", true) or inv:contains("Pencil", true) or inv:contains("BluePen", true) or inv:contains("RedPen", true)
end

function ISMap:canErase()
    return self.character:getInventory():contains("Eraser", true);
end

function ISMap:onConfirmRemove(button, note)
    self.removingElement = false;
    if button.internal == "YES" then
        local newNote = {};
        for i,v in ipairs(self.mapObj:getModData()["notes"]) do
            if v ~= note then
                table.insert(newNote, v);
            end
        end
        self.mapObj:getModData()["notes"] = newNote;
    end
	self.getJoypadFocus = true;
end

function ISMap:onConfirmRemoveSymbol(button, symbol)
    self.removingElement = false;
    if button.internal == "YES" then
        local newSymbols = {};
        for i,v in ipairs(self.mapObj:getModData()["symbols"]) do
            if v ~= symbol then
                table.insert(newSymbols, v);
            end
        end
        self.mapObj:getModData()["symbols"] = newSymbols;
    end
	self.getJoypadFocus = true;
end

function ISMap:onAddNote(button, map)
    if button.internal == "OK" and button.parent.entry:getText() and string.trim(button.parent.entry:getText()) ~= "" then
        local newNote = {};
        newNote.text = string.trim(button.parent.entry:getText());
        newNote.x = map.noteX;
        newNote.y = map.noteY;
        newNote.r = button.parent.currentColor:getR();
        newNote.g = button.parent.currentColor:getG();
        newNote.b = button.parent.currentColor:getB();
        map.addingNote = false;
        local notes = map.mapObj:getModData()["notes"];
        if not notes then
            notes = {};
        end
        table.insert(notes, newNote);
        map.mapObj:getModData()["notes"] = notes;
    end
end

function ISMap:replaceMap()
    local marginX = self.width / 2
    local marginY = self.height / 2
    if self.zoom:getX() > marginX then
        self.zoom:setX(marginX);
    end
    if self.zoom:getY() > marginY then
        self.zoom:setY(marginY);
    end
    if self.zoom.x + self.zoom.scaledWidth < self:getWidth() - marginX then
--        self.zoom:setX(-self.zoomed * self.zoomingPerc);
        self.zoom:setX(self:getWidth() - marginX - self.zoom.scaledWidth);
    end
    if self.zoom.y + self.zoom.scaledHeight < self:getHeight() - marginY then
        self.zoom:setY(self:getHeight() - marginY - self.zoom.scaledHeight);
    end
end

function ISMap:onMouseUpOutside()
--    print("OnMouseUpOutside");
    self.dragging = false;
    self:replaceMap();
end

function ISMap:onMouseWheel(del)
    local xScale = self.zoom.scaledWidth/self.tex:getWidth();
	local yScale = self.zoom.scaledHeight/self.tex:getHeight();
    -- limit the zoom to x3
    if xScale > 3 and del < 0 then return true; end
    -- blaah
    local originalWidth = self.zoom.scaledWidth;
    local originalHeight = self.zoom.scaledHeight;
	
	local oldCenterX =  (self.width/2-self.zoom.javaObject:getX()-(9*xScale))/xScale;
	local oldCenterY =  (self.height/2-self.zoom.javaObject:getY()-(9*yScale))/yScale;

    self.zoom.scaledWidth = self.zoom.scaledWidth + del * -self.zoomingPerc;
    self.zoom.scaledHeight = self.zoom.scaledWidth * self.prop;
    originalWidth = self.zoom.scaledWidth - originalWidth;
    originalHeight = self.zoom.scaledHeight - originalHeight;
    local xScale = self.zoom.scaledWidth/self.tex:getWidth();
	local yScale = self.zoom.scaledHeight/self.tex:getHeight();
    originalWidth = originalWidth / 2;
    originalHeight = originalHeight / 2;
	
	local newCenterXLocal = (self.width+(-2*oldCenterX-18)*xScale)/2;
	local newCenterYLocal = (self.height+(-2*oldCenterY-18)*yScale)/2;
	
    local doMove = true;
    --    print("OnMouseWheel", self.zoom:getMouseX(), self.width);
    if self.zoom.scaledWidth < self.width then
        self.zoom.scaledWidth = self.width;
        self.zoom.scaledHeight = self.zoom.scaledWidth * self.prop;
        doMove = false;
    end
    if self.zoom.scaledHeight < self.height then
        self.zoom.scaledHeight = self.height;
        doMove = false;
    end
    if doMove then
        --local movingX = (del*-self.zoomingPerc)/2;
        self.zoom:setX(newCenterXLocal);
        self.zoom:setY(newCenterYLocal);
	else
		self.zoom:setX(0)
		self.zoom:setY(0)
    end
    self:replaceMap();
    --    print("OnMouseWheel2", self.zoom:getX(), self.zoom:getY())
    return true;
end

function ISMap:onMouseMove(dx, dy)
    if self.dragging then
        self.zoom:setX(self.zoom:getX() + dx);
        self.zoom:setY(self.zoom:getY() + dy);
        self:replaceMap();
    end
end

function ISMap:onMouseMoveOutside(dx, dy)
    if self.dragging then
        self.zoom:setX(self.zoom:getX() + dx);
        self.zoom:setY(self.zoom:getY() + dy);
        self:replaceMap();
    end
end

--************************************************************************--
--** ISMap:render
--**
--************************************************************************--
function ISMap:noRender()

end

function ISMap:onPrerenderMap()
    self.parent:setStencilRect(0, 0, self.parent.width, self.parent.height)
    ISImage.prerender(self)
    self.parent:clearStencilRect()
    self.parent.wrap:repaintStencilRect(0, 0, self.parent.wrap.width, self.parent.wrap.height)
    if JoypadState.players[self.parent.playerNum+1] then
        local rectY = self.parent.ok:getY() - 4
        self.parent:drawRectStatic(0, rectY, self.width, self.height - rectY, 0.75, 0, 0, 0)
    end
end

function ISMap:trueRender()
--    ISPanelJoypad.render(self);
--    print("render map")
--    self.zoom:clearStencilRect();

    if not self.wrap.isCollapsed then
        local stencilBottom = self.ok:getY() - 4 -- don't draw symbols over the buttons
        
        self:setStencilRect(0, 0, self.width, stencilBottom)
        -- render each symbols/notes, including the scaling we are on
        local xScale = self.zoom.scaledWidth/self.tex:getWidth();
        local yScale = self.zoom.scaledHeight/self.tex:getHeight();
        for i,v in ipairs(self.mapObj:getModData()["notes"]) do
            local x = v.x*xScale + self.zoom:getX();
            local y = v.y*yScale + self.zoom:getY();
    --        print("drawing text", v.x*xScale + self.zoom:getX(), self:getMouseX());
                self:drawTextZoomed(v.text, x, y, xScale,v.r,v.g,v.b,1, UIFont.Handwritten);
                if JoypadState.players[self.playerNum+1] then
                    self:checkElementsForRemoveFromJoy()
                    if self.mouseOverNote == v then
                        local textW = getTextManager():MeasureStringX(UIFont.Handwritten, v.text)
                        local textH = getTextManager():MeasureStringY(UIFont.Handwritten, v.text)
                        self:drawRectBorder(x, y, textW * xScale, textH * yScale, 1, 1, 0, 0)
                    end
                end
        end

        -- bleeh
        for i,v in ipairs(self.mapObj:getModData()["symbols"]) do
            local x = (v.x*xScale) + self.zoom:getX();
            local y = (v.y*yScale) + self.zoom:getY();
                if not self.symbolTexList[v.symbol] then
                    self.symbolTexList[v.symbol] = getTexture(v.symbol);
                end
                local tex = self.symbolTexList[v.symbol]
    --            self:drawTexture(self.symbolTexList[v.symbol], x, y, 1, v.r,v.b,v.g);
                self:drawTextureScaled(tex, x, y, tex:getWidth() * xScale, tex:getHeight() * yScale,1, v.r,v.g, v.b);
                if JoypadState.players[self.playerNum+1] then
                    self:checkElementsForRemoveFromJoy()
                    if self.mouseOverSymbol == v then
                        self:drawRectBorder(x, y, tex:getWidthOrig() * xScale, tex:getHeightOrig() * yScale, 1, 1, 0, 0)
                    end
                end
        end

    --    self:drawRectBorder(self.width/2, 0, 1, self.height, self.borderColor.a, 0, 0, 0);
    --    self:drawRectBorder(0, self.height/2, self.width, 1, self.borderColor.a, 0, 0, 0);
	self:drawTexture(self.cross, self.width/2-12, self.height/2-12, 1, 1,1,1);
        self:clearStencilRect()
    end

    self:renderMouseIcons();
end

function ISMap:prerenderWrap()
    ISCollapsableWindow.prerender(self);
    self.closeButton:prerender();
    self.collapseButton:prerender();
    self.pinButton:prerender();
    self.infoButton:prerender();
end

function ISMap:renderWrap()
--    print("render col")
    ISCollapsableWindow.render(self);
    self.mapUI:trueRender();
    self.closeButton:render();
    self.collapseButton:render();
    self.pinButton:render();
    self.infoButton:render();
    -- Moved this from update() so it is called more often
	self.mapUI:updateJoypad();
end

function ISMap:checkElementsForRemoveFromJoy()
        self.mouseOverNote = nil;
        self.mouseOverSymbol = nil;
        local xScale = self.zoom.scaledWidth/self.tex:getWidth();
        local yScale = self.zoom.scaledHeight/self.tex:getHeight();
		local crossX =  math.floor((self.width/2-self.zoom.javaObject:getX()-(0*xScale))/xScale);
		local crossY =  math.floor((self.height/2-self.zoom.javaObject:getY()-(0*yScale))/yScale);
        for i,v in ipairs(self.mapObj:getModData()["notes"]) do
            local x = v.x;
            local y = v.y;
            local textW = getTextManager():MeasureStringX(UIFont.Handwritten, v.text);
            local textH = getTextManager():MeasureStringY(UIFont.Handwritten, v.text);

            if (crossX > x and crossX < x + textW) and (crossY > y and crossY < y + textH) then
                --self:drawText("X", self:getMouseX() + 15, self:getMouseY(), 1,0,0,1, UIFont.Medium);
--                self:drawRectBorder(x-5, y-(1*yScale), (size*xScale), (15*yScale), self.borderColor.a, 1, 0, 0);
                self.mouseOverNote = v;
                break;
            end
        end

        for i,v in ipairs(self.mapObj:getModData()["symbols"]) do
            local x = v.x;
            local y = v.y;
            if (crossX > x and crossX < x+20) and (crossY > y and crossY < y + 20) then
                --self:drawText("X", self:getMouseX() + 15, self:getMouseY(), 1,0,0,1, UIFont.Medium);
--				self:drawRectBorder(x-1, y-(1*yScale), (16*xScale), (16*yScale), self.borderColor.a, 1, 0, 0);
                self.mouseOverSymbol = v;
                break;
            end
        end
end
		
function ISMap:checkElementsForRemove()
        self.mouseOverNote = nil;
        self.mouseOverSymbol = nil;
        local xScale = self.zoom.scaledWidth/self.tex:getWidth();
        local yScale = self.zoom.scaledHeight/self.tex:getHeight();
        for i,v in ipairs(self.mapObj:getModData()["notes"]) do
            local x = v.x*xScale + self.zoom:getX();
            local y = v.y*yScale + self.zoom:getY();
            local size = getTextManager():MeasureStringX(UIFont.Handwritten, v.text);

            if (self:getMouseX() > x-5 and self:getMouseX() < x + (size*xScale) + 5) and (self:getMouseY() > y-(1*yScale) and self:getMouseY() < y + (15*yScale)) then
                self:drawText("X", self:getMouseX() + 15, self:getMouseY(), 1,0,0,1, UIFont.Medium);
--                self:drawRectBorder(x-5, y-(1*yScale), (size*xScale), (15*yScale), self.borderColor.a, 1, 0, 0);
                self.mouseOverNote = v;
                break;
            end
        end

        for i,v in ipairs(self.mapObj:getModData()["symbols"]) do
            local x = v.x*xScale + self.zoom:getX();
            local y = v.y*yScale + self.zoom:getY();
            if (self:getMouseX() > x-1 and self:getMouseX() < x + (16*xScale)) and (self:getMouseY() > y-(1*yScale) and self:getMouseY() < y + (16*yScale)) then
                self:drawText("X", self:getMouseX() + 15, self:getMouseY(), 1,0,0,1, UIFont.Medium);
                self.mouseOverSymbol = v;
                break;
            end
        end
end

function ISMap:renderMouseIcons()
	if (self.playerNum ~= 0) or (JoypadState.players[self.playerNum+1] and not wasMouseActiveMoreRecentlyThanJoypad()) then return end
	
    if self:getMouseX() < 0 or self:getMouseX() > self:getWidth() - 1 or self:getMouseY() < 0 or self:getMouseY() > self:getHeight() - 1 then
       return;
    end
    if self.addingNote then
        self:drawTexture(self.textCursor, self:getMouseX() + 15, self:getMouseY(), 1,1,1,1);
    end
    if self.removingElement then
        self:checkElementsForRemove()
    end

    if self.symbolList and self.symbolList.selectedSymbol then
        local xScale = self.zoom.scaledWidth/self.tex:getWidth();
        local yScale = self.zoom.scaledHeight/self.tex:getHeight();
        self:drawTextureScaled(self.symbolList.selectedSymbol.image,self:getMouseX()-(9*xScale), self:getMouseY()-(9*yScale), self.symbolList.selectedSymbol.image:getWidth() * xScale, self.symbolList.selectedSymbol.image:getHeight() * yScale,1, self.symbolList.selectedSymbol.textureColor.r,self.symbolList.selectedSymbol.textureColor.b,self.symbolList.selectedSymbol.textureColor.g);
    end

--    if self.drawLineOriginalX > -1 and self.drawLineOriginalY > -1 then
--        local xScale = self.zoom.scaledWidth/self.tex:getWidth();
--        local yScale = self.zoom.scaledHeight/self.tex:getHeight();
--        local originalX = self.drawLineOriginalX;
--        local originalY = self.drawLineOriginalY;
--        self:drawLine2(originalX,originalY,originalX + self:getMouseX(),originalY + self:getMouseY(),1,1,1,1);
--    end
end

function ISMap:update()
    ISPanelJoypad.update(self);
    if not self.character:getInventory():contains(self.mapObj, true) then self.wrap:setVisible(false) end
    if self.symbolList then -- move the symbol list when moving the map
        self.symbolList:setX(self.wrap.x + self.wrap.width)
        self.symbolList:setY(self.wrap.y)
--        print(self.wrap.isCollapsed)
        self.symbolList:setVisible(not self.wrap.isCollapsed);
    end
    self:updateButtons();
end

function ISMap:updateJoypad()
	if self.getJoypadFocus then
		self.getJoypadFocus = false;
		if JoypadState.players[self.playerNum+1] then
			setJoypadFocus(self.playerNum, self)
		end
	end

	self.updateMS = self.updateMS or getTimestampMs()
	local dt = getTimestampMs() - self.updateMS
	self.updateMS = getTimestampMs()

	if self.joyfocus == nil then return end

	self.JPZoomInc = 0;
	if isJoypadPressed(self.joyfocus.id, Joypad.LBumper) then
		self.JPZoomInc = dt / 1000 * 10
	end
	if isJoypadPressed(self.joyfocus.id, Joypad.RBumper) then
		self.JPZoomInc = -dt / 1000 * 10
	end
	if self.JPZoomInc ~= 0 then
		self:onMouseWheel(self.JPZoomInc)
	end

	local x = getControllerPovX(self.joyfocus.id);
	local y = getControllerPovY(self.joyfocus.id);
	if x~=0 or y ~= 0 then
		local scale = self.zoom.scaledWidth / self.tex:getWidth()
		local scrollDelta = -dt / 1000 * self.tex:getWidth() * 0.25 / scale
		self.zoom:setX(self.zoom:getX() + scrollDelta * x);
		self.zoom:setY(self.zoom:getY() + scrollDelta * y);
		self:replaceMap();
	end
end

function ISMap:updateButtons()
    self.addnoteBtn.enable = self:canWrite();
    self.symbolBtn.enable = self:canWrite();
    self.placeSymbBtn.enable = self:canWrite() and (self.selectedSymbol ~= nil)
    self.removenoteBtn.enable = self:canErase();
    if not self.addnoteBtn.enable then
        self.addnoteBtn.tooltip = getText("Tooltip_Map_CantWrite");
        self.symbolBtn.tooltip = getText("Tooltip_Map_CantWrite");
    else
        self.addnoteBtn.tooltip = nil;
        self.symbolBtn.tooltip = nil;
    end
    if not self.removenoteBtn.enable then
        self.removenoteBtn.tooltip = getText("Tooltip_Map_CantErase");
    else
        self.removenoteBtn.tooltip = nil;
    end
    if self.addingNote then
        self.addnoteBtn:setTitle(getText("UI_Cancel"));
    else
        self.addnoteBtn:setTitle(getText("IGUI_Map_AddNote"));
    end
    if #self.mapObj:getModData()["notes"] == 0 and #self.mapObj:getModData()["symbols"] == 0 then
        self.removenoteBtn.enable = false;
    elseif self.removenoteBtn.enable then
        self.removenoteBtn:setTitle(getText("IGUI_Map_RemoveElement"));
    end
    if self.removingElement then
        self.removenoteBtn:setTitle(getText("UI_Cancel"));
    else
        self.removenoteBtn:setTitle(getText("IGUI_Map_RemoveElement"));
    end
    if self.symbolList then
        self.symbolBtn:setTitle(getText("UI_Cancel"));
    elseif JoypadState.players[self.playerNum+1] then
        self.symbolBtn:setTitle(getText("IGUI_Map_ChooseSymbol"))
    else
        self.symbolBtn:setTitle(getText("IGUI_Map_AddSymbol"));
    end
end

function ISMap:onGainJoypadFocus(joypadData)
	ISPanelJoypad.onGainJoypadFocus(self, joypadData)
	--self.joypadIndexY = 1
	--self.joypadIndex = 1
	--self.joypadButtons = self.joypadButtonsY[self.joypadIndexY]
	--self.joypadButtons[self.joypadIndex]:setJoypadFocused(true)
	self.addnoteBtn:setVisible(false)
	self.testBtn:setVisible(false)
	self.placeSymbBtn:setVisible(true)
	self.ok:setJoypadButton(getTexture("media/ui/bbutton.png"))
--	self.ok.textColor = {r=0.0, g=0.0, b=0.0, a=1.0};
	self.symbolBtn:setJoypadButton(getTexture("media/ui/xbutton.png"))
--	self.symbolBtn.textColor = {r=0.0, g=0.0, b=0.0, a=1.0};
	self.placeSymbBtn:setJoypadButton(getTexture("media/ui/abutton.png"))
--	self.placeSymbBtn.textColor = {r=0.0, g=0.0, b=0.0, a=1.0};
	self.removenoteBtn:setJoypadButton(getTexture("media/ui/ybutton.png"))
--	self.removenoteBtn.textColor = {r=0.0, g=0.0, b=0.0, a=1.0};
end

function ISMap:onJoypadDown(button)
	ISPanelJoypad.onJoypadDown(self, button)
	if button == Joypad.BButton then
		self.ok:forceClick()
	end
	if button == Joypad.XButton then
		self.symbolBtn:forceClick()
	end
	if button == Joypad.AButton then
		self.placeSymbBtn:forceClick()
	end
	if button == Joypad.YButton then
		self.removenoteBtn:forceClick()
	end
end

--************************************************************************--
--** ISMap:new
--**
--************************************************************************--
function ISMap:new(x, y, width, height, map, player)
	local o = {}
	o = ISPanelJoypad:new(x, y, width, height);
	setmetatable(o, self)
    self.__index = self
    o.tex = getTexture(map:getMap());
--    o.tex = getTexture("media/ui/LootableMaps/marchridgemap.png")
--    print("original tex", o.tex:getWidth(), o.tex:getHeight());
--    print("screen", getCore():getScreenWidth(), getCore():getScreenHeight());
--    o.normalSized = false;
--    if o.tex:getHeight() > o.tex:getWidth() then
--        o.normalSized = false;
--    end
    o.prop = o.tex:getHeight() / o.tex:getWidth();
    if o.tex:getWidth() > 800 then
        width = 800
        height = width * o.prop;
    else
        width = o.tex:getWidth();
    end
    if height > getCore():getScreenHeight() - 70 then
        height = getCore():getScreenHeight() - 70;
    end
--    print("final img", width, height);
    o.character = getSpecificPlayer(player);
    o.playerNum = player;
    o.x = (getCore():getScreenWidth() - width) / 2;
    o.y = (getCore():getScreenHeight() - height) / 2;
    o:setY(o.y)
    o:setX(o.x)
	o.width = width;
	o.height = height;
	o.anchorLeft = true;
	o.anchorRight = true;
	o.anchorTop = true;
	o.anchorBottom = true;
    o.zooming = false;
    o.dragging = false;
    o.mapObj = map;
    o.zoomX = 0;
    o.zoomY = 0;
    o.zoomingPerc = 100;
    o.addingNote = false;
    o.removingElement = false;
    o.drawLine = true;
    o.drawLineOriginalX = -1;
    o.drawLineOriginalY = -1;
    o.textCursor = getTexture("media/ui/LootableMaps/textCursor.png");
	o.cross = getTexture("media/ui/LootableMaps/mapCross.png");
    o.lineTex = getTexture("media/ui/LootableMaps/line.png");
    o.symbolTexList = {};
    if not map:getModData()["notes"] then
        map:getModData()["notes"] = {};
    end
    if not map:getModData()["symbols"] then
        map:getModData()["symbols"] = {};
    end
    o.draggingStartingX = 0;
    o.draggingStartingY = 0;
	o.JPZoomInc = 0.0;
	o.selectedSymbol = nil;
	o.selectedColor = nil;
	o.getJoypadFocus = false
    return o;
end
