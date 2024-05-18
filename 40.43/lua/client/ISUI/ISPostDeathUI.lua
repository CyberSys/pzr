--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

ISPostDeathUI = ISPanelJoypad:derive("ISPostDeathUI")
ISPostDeathUI.instance = {}

function ISPostDeathUI:createChildren()
	local buttonWid = 250
	local buttonHgt = 40
	local buttonGapY = 12
	local buttonX = 0
	local buttonY = 0
	local totalHgt = (buttonHgt * 3) + (buttonGapY * 2)

	self:setWidth(buttonWid)
	self:setHeight(totalHgt)
	-- must set these after setWidth/setHeight or getKeepOnScreen will mess them up
	self:setX(self.screenX + (self.screenWidth - buttonWid) / 2)
	self:setY(self.screenHeight - 40 - totalHgt)

	local button = ISButton:new(buttonX, buttonY, buttonWid, buttonHgt, getText("IGUI_PostDeath_Quit"), self, self.onQuit)
	self:configButton(button)
	self:addChild(button)
	self.buttonQuit = button
	buttonY = buttonY + buttonHgt + buttonGapY

	button = ISButton:new(buttonX, buttonY, buttonWid, buttonHgt, getText("IGUI_PostDeath_Exit"), self, self.onExit)
	self:configButton(button)
	self:addChild(button)
	self.buttonExit = button
	buttonY = buttonY + buttonHgt + buttonGapY

	button = ISButton:new(buttonX, buttonY, buttonWid, buttonHgt, getText("IGUI_PostDeath_Respawn"), self, self.onRespawn)
	self:configButton(button)
	self:addChild(button)
	self.buttonRespawn = button
end

function ISPostDeathUI:configButton(button)
	button.anchorLeft = false
	button.anchorTop = false
	button.backgroundColor.a = 0.8
	button.borderColor.a = 0.3
end

function ISPostDeathUI:prerender()
	ISPostDeathUI.instance[self.playerIndex] = self
	if self.screenWidth ~= getPlayerScreenWidth(self.playerIndex) or self.screenHeight ~= getPlayerScreenHeight(self.playerIndex) then
		local x = getPlayerScreenLeft(self.playerIndex)
		local y = getPlayerScreenTop(self.playerIndex)
		local w = getPlayerScreenWidth(self.playerIndex)
		local h = getPlayerScreenHeight(self.playerIndex)
		self.screenX = x
		self.screenWidth = w
		self.screenHeight = h
		self:setX(x + (w - self.width) / 2)
		self:setY(h - 40 - self.height)
	end
	if not self.waitOver then
		self.waitOver = getTimestamp() > self.timeOfDeath + 3
	end
	local allPlayersDead = IsoPlayer.allPlayersDead()
	self.buttonQuit:setVisible(self.waitOver and allPlayersDead)
	self.buttonExit:setVisible(self.waitOver and allPlayersDead)
	local allowRespawn = isClient() or (getNumActivePlayers() > 1)
	if isClient() and getServerOptions():getBoolean("DropOffWhiteListAfterDeath") then
		allowRespawn = false
	end
	self.buttonRespawn:setVisible(self.waitOver and allowRespawn)
	ISPanelJoypad.prerender(self)
end

function ISPostDeathUI:render()
	ISPanelJoypad.render(self)
	if self.waitOver and self.textY > -50 then
		local y = self.textY
		local fontHgt = getTextManager():getFontFromEnum(UIFont.Large):getLineHeight()
		for _,line in ipairs(self.lines) do
			getTextManager():DrawStringCentre(UIFont.Large, self.screenX + self.screenWidth / 2, y, line, 1, 1, 1, 1)
			y = y + fontHgt + 2
		end
		if getTimestamp() > self.timeOfDeath + 6 then
			self.textY = self.textY - 0.5 * (30 / getPerformance():getUIRenderFPS())
		end
	end
end

function ISPostDeathUI:onQuit()
	self:removeFromUIManager()
	getCore():quitToDesktop()
end

function ISPostDeathUI:onExit()
	self:removeFromUIManager()
	getCore():exitToMenu()
end

function ISPostDeathUI:onRespawn()
	self:setVisible(false)
	local joypadData = JoypadState.players[self.playerIndex+1]
	if joypadData then
		CoopCharacterCreation.newPlayer(joypadData.id, joypadData)
	else
		CoopCharacterCreation:newPlayerMouse()
	end
end

function ISPostDeathUI:onMouseDown(x, y)
	return false
end

function ISPostDeathUI:onMouseUp(x, y)
	return false
end

function ISPostDeathUI:onMouseMove(dx, dy)
	return false
end

function ISPostDeathUI:onMouseWheel(del)
	return false
end

function ISPostDeathUI:onGainJoypadFocus(joypadData)
	self:setISButtonForB(self.buttonQuit)
	self:setISButtonForX(self.buttonExit)
	self:setISButtonForA(self.buttonRespawn)
end

function ISPostDeathUI:new(playerIndex)
	local x = getPlayerScreenLeft(playerIndex)
	local y = getPlayerScreenTop(playerIndex)
	local w = getPlayerScreenWidth(playerIndex)
	local h = getPlayerScreenHeight(playerIndex)
	local o = ISPanelJoypad:new(x, y, w, h)
	setmetatable(o, self)
	self.__index = self
	o:setAnchorLeft(false)
	o:setAnchorTop(false)
	o.background = false
	o.screenX = x
	o.screenWidth = w
	o.screenHeight = h
	o.playerIndex = playerIndex
	o.textY = h / 2 - 30
	o:instantiate()
	o:setAlwaysOnTop(true)
	o.javaObject:setIgnoreLossControl(true)
	ISPostDeathUI.instance[playerIndex] = o
	return o
end

function ISPostDeathUI.OnPlayerDeath(playerObj)
	local playerNum = playerObj:getPlayerNum()
	local panel = ISPostDeathUI:new(playerNum)
	panel.timeOfDeath = getTimestamp()
	panel.lines = {}
	table.insert(panel.lines, getGameTime():getDeathString(playerObj))
	local s = getGameTime():getZombieKilledText(playerObj)
	if s then
		table.insert(panel.lines, s)
	end
	s = getGameTime():getGameModeText()
	if s then
		table.insert(panel.lines, s)
	end
	panel:addToUIManager()
	if JoypadState.players[playerNum+1] then
		JoypadState.players[playerNum+1].focus = panel
	end
end

Events.OnPlayerDeath.Add(ISPostDeathUI.OnPlayerDeath)

