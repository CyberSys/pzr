--***********************************************************
--**                    ROBERT JOHNSON                     **
--**              Panel wich display all our skills        **
--***********************************************************

require "ISUI/ISPanelJoypad"

ISCharacterInfo = ISPanelJoypad:derive("ISCharacterInfo");
ISCharacterInfo.timerMultiplierAnim = 0;


--************************************************************************--
--** ISPanel:initialise
--**
--************************************************************************--

function ISCharacterInfo:initialise()
	ISPanelJoypad.initialise(self);
end

function ISCharacterInfo:createChildren()
	self.togglePassiveBtn = ISButton:new(0, 8, 15, 15, "", self, ISCharacterInfo.onTogglePassive);
	self.togglePassiveBtn.internal = "TOGGLEPASSIVE";
	self.togglePassiveBtn:initialise();
	self.togglePassiveBtn:instantiate();
	self.togglePassiveBtn:setImage(getTexture("media/ui/ArrowRight.png"));
	self:addChild(self.togglePassiveBtn);
end

function ISCharacterInfo:setVisible(visible)
--    self.parent:setVisible(visible);
    self.javaObject:setVisible(visible);
    for i,v in pairs(self.progressBars) do
        if v.tooltip then
            v.tooltip:setVisible(false);
            v.tooltip:removeFromUIManager();
            v.tooltip = nil;
        end
    end
end

function ISCharacterInfo:render()
	local tabHeight = self.y
	local maxHeight = getCore():getScreenHeight() - ISWindow.TitleBarHeight - tabHeight
	local y = 8
	ISSkillProgressBar.updateAlpha() -- FIXME: do this once per frame, not for each player
	-- how much skills pts we got ?
	if self.char:getNumberOfPerksToPick() > 0 then
		self:drawText(xpSystemText.skillPtAvailable, self.x + 5, y + 1, 1, 1, 1, 1, UIFont.Small);
		local labelWidth = getTextManager():MeasureStringX(UIFont.Small, xpSystemText.skillPtAvailable)
		-- the background of the available skill pt
		local ptString = self.char:getNumberOfPerksToPick() .. ""
		local width = getTextManager():MeasureStringX(UIFont.Small, ptString) + 8
		local height = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight() + 2
--		width = math.max(width + 8, self.SKillPtAvailableRound:getWidth())
		local availX = math.max(self.x + 20 + self.txtLen + 45, self.x + 5 + labelWidth + 4)
		self:drawTextureScaled(self.ProgressSkilMultiplier, availX, y, width, height, 1,1,1,1);
		self:drawText(ptString, availX + 4, y, 1, 1, 1, 1, UIFont.Small);
		-- we also reload our progress bar to make them a bit lower
		if not self.reloadSkillBar then
			self.progressBarLoaded = false;
			for i,v in pairs(self.progressBars) do
				self:removeChild(v);
			end
			self.progressBars = {};
			self.reloadSkillBar = true;
		end
		y = y + height + 1;
	elseif self.reloadSkillBar then
		self.progressBarLoaded = false;
		self.reloadSkillBar = false;
		for i,v in pairs(self.progressBars) do
			self:removeChild(v);
		end
		self.progressBars = {}
	end
	-- how much xp needed for next skill point ?
	-- if you're over the max lvl require in xp, the amount won't change for the next lvl
	local xp = self.char:getXpForLevel(self.char:getXp():getLevel())
	-- now the progress bar to the next skill pts, every % we gonna display more of the progress bar
	-- first, the rectangle over the progress bar
	self:drawText(xpSystemText.nextSkillIn, self.x + 5, y, 1, 1, 1, 1, UIFont.Small);
	self:drawTextureScaled(self.SkillPtsProgressBarEmpty, self.x + 20 + self.txtLen + 45, y + 2, 100, 10, 1,1,1,1);
	-- progress in % of the current skill pt
	-- we gonna substract all the previous xp required for the lvl to only get the xp in the current lvl for the progress
	local previousXp = 0;
--~ 	for i=0, (self.char:getXp():getLevel() - 1) do
	if self.char:getXp():getLevel() > 0 then
		previousXp = self.char:getXpForLevel(self.char:getXp():getLevel() - 1)
	end
--~ 	end
--~ 	local percentProgress = ((self.char:getXp():getTotalXp() - previousXp) / xp) * 100;
	local percentProgress = ((self.char:getXp():getTotalXp() - previousXp) / (xp - previousXp)) * 100;
	-- the bar is 100 in width, so 1% xp = 1% progress bar
	local sliceWidth = 1;
	if percentProgress > 0 then -- the first slice is a 1 pixel rounded rectangle
		self:drawTexture(self.SkillPtsProgressBarStart, self.x + 20 + self.txtLen + 45, y + 2, 1,1,1,1);
	end
	if percentProgress > 1 then
		self:drawTextureScaled(self.SkillPtsProgressBar, self.x + 20 + 1 + self.txtLen + 45, y + 2, sliceWidth * (percentProgress - 1), 10, 1,1,1,1);
	end
	xp = xp - self.char:getXp():getTotalXp();
	xp = toInt(xp);
	self:drawTextCentre(xp .. xpSystemText.xp, self.x + 20 + self.txtLen + 45 + 100 / 2, y - 2, 1, 1, 1, 1, UIFont.Small);

	y = y + 25;
	local top = y

	-- if we got a multiplier, we gonna anim that with ">, >>, >>>"
	 -- FIXME: do this once per frame, not for each player
	local ms = UIManager.getMillisSinceLastRender()
	ISCharacterInfo.timerMultiplierAnim = ISCharacterInfo.timerMultiplierAnim + ms;
	if ISCharacterInfo.timerMultiplierAnim <= 500 then
        ISCharacterInfo.animOffset = -1;
	elseif ISCharacterInfo.timerMultiplierAnim <= 1000 then
        ISCharacterInfo.animOffset = 0;
	elseif ISCharacterInfo.timerMultiplierAnim <= 1500 then
        ISCharacterInfo.animOffset = 15;
	elseif ISCharacterInfo.timerMultiplierAnim <= 2000 then
        ISCharacterInfo.animOffset = 30;
	else
		ISCharacterInfo.timerMultiplierAnim = 0;
	end

	local sorted = {}
	local nameToPerk = {}
	for k,v in pairs(self.perks) do
		local parentPerk = PerkFactory.getPerk(k)
		table.insert(sorted, parentPerk:getName())
		nameToPerk[parentPerk:getName()] = k
	end

	if self.showPassive then
		sorted = {}
		nameToPerk = {}
		for k,v in pairs(self.perksPassive) do
			local parentPerk = PerkFactory.getPerk(k)
			table.insert(sorted, parentPerk:getName())
			nameToPerk[parentPerk:getName()] = k
		end
	end
	
	-- FIXME: why is string.sort(a,b) doing "return a > b" ???
	table.sort(sorted, function(a,b) return not string.sort(a,b) end)

	local left = 0
	local maxY = y
	local fontHgt = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight()
	local progressHgt = 18
	local rowHgt = math.max(fontHgt, progressHgt + 2)
	for _,name in ipairs(sorted) do
		local parentName = nameToPerk[name]
		local perkList = self.showPassive and self.perksPassive[parentName] or self.perks[parentName]
		-- if it's too tall, create another column
		if y + math.max(25, fontHgt) + #perkList * 20 + 10 > maxHeight then
			left = left + 20 + self.txtLen + 45 + 180 + 40
			y = top
		end
		-- we first draw our parent name
		local parentPerk = PerkFactory.getPerk(parentName);
		self:drawText(parentPerk:getName(), left + 5, y, 1, 1, 1, 1, UIFont.Small);
		self:drawTexture(self.SkillBarSeparator, left, y + fontHgt + 2, 1,1,1,1);
		y = y + math.max(25, fontHgt);
		-- then all the skills with their progress bar
		for ind, perk in ipairs(perkList) do
            local xpBoost = self.char:getXp():getPerkBoost(perk:getType());
            local r = 1;
            local g = 1;
            local b = 1;
            if xpBoost == 0 then
                r = 0.54;
                g = 0.54;
                b = 0.54;
            elseif xpBoost == 1 then
                r = 0.8;
                g = 0.8;
                b = 0.8;
            elseif xpBoost == 3 then
                r = 1;
                g = 0.83;
                b = 0;
            end
			self:drawText(perk:getName(), left + 20, y, r, g, b, 1, UIFont.Small);
			-- if we got a multiplier, we gonna anim that with ">, >>, >>>"
            if self.char:getXp():getMultiplier(perk:getType()) > 0 then
                self:drawTexture(self.disabledArrow, left + 20 + self.txtLen, y, 1, 1, 1, 1);
                self:drawTexture(self.disabledArrow, left + 35 + self.txtLen, y, 1, 1, 1, 1);
                self:drawTexture(self.disabledArrow, left + 50 +self.txtLen, y, 1, 1, 1, 1);

                if ISCharacterInfo.animOffset > -1 then
                    self:drawTexture(self.arrow, left + 20 + self.txtLen + ISCharacterInfo.animOffset, y, 1, 1, 1, 1);
                end
            end
			if not self.progressBarLoaded then
				local progressBar = ISSkillProgressBar:new(left + 20 + self.txtLen + 45, y + (rowHgt - progressHgt) / 2, 0, 0, self.playerNum, perk);
				progressBar:initialise();
				self:addChild(progressBar);
				table.insert(self.progressBars, progressBar);
			end
			y = y + rowHgt;
		end
		y = y + 10;
		maxY = math.max(maxY, y)
	end

--~ 	self:drawText("Strong : " .. getPlayer():getPerkLevel(Perks.Strength), self.x + 8, y, 1, 1, 1, 1, UIFont.Small);
	y = maxY + 10;
--~ 	for i = 0, getPlayer():getTraits():size() - 1 do
--~ 		local v = getPlayer():getTraits():get(i);
--~ 		self:drawText("Trait : " .. v, self.x + 8, y, 1, 1, 1, 1, UIFont.Small);
--~ 		y = y + 20;
--~ 	end
--~ 	self:drawText("Hauling : " .. getPlayer():getXp():getXP(Perks.Hauling), self.x + 8, y, 1, 1, 1, 1, UIFont.Small);

    self:setWidthAndParentWidth(left + self.txtLen + 280);
	self:setHeightAndParentHeight(y);

	self.togglePassiveBtn:setX(self.width - 15 - 16)
	self.togglePassiveBtn:setY(8)

	self.progressBarLoaded = true;

	if self.joyfocus then
		if self.joypadIndex and self.joypadIndex >= 1 and self.joypadIndex <= #self.progressBars then
			local bar = self.progressBars[self.joypadIndex]
			local left = bar:getX() - (self.txtLen + 45)
			local right = bar:getX() + bar:getWidth()
			self:drawRectBorder(left-2, bar:getY()-2, (right - left) + 2, bar:getHeight() + 3, 0.4, 0.2, 1.0, 1.0);
			if bar.tooltip then
				bar.tooltip.followMouse = false
				bar.tooltip:setX(bar:getAbsoluteX())
				local tty = bar:getAbsoluteY() + bar:getHeight() + 1
				if tty + bar.tooltip:getHeight() > getCore():getScreenHeight() then
					tty = bar:getAbsoluteY() - bar.tooltip:getHeight() - 1
				end
				bar.tooltip:setY(tty)
			end
		end
	end
end

function ISCharacterInfo:new (x, y, width, height, playerNum)
	local o = {};
	o = ISPanelJoypad:new(x, y, width, height);
	setmetatable(o, self);
    self.__index = self;
    o.progressBars = {}
	o.progressBarLoaded = false;
	o.playerNum = playerNum
	o.char = getSpecificPlayer(playerNum);
	o:noBackground();
	o.txtLen = 0;
	o.perks = ISCharacterInfo.loadPerk(o, false);
	o.perksPassive = ISCharacterInfo.loadPerk(o, true);
    o.arrow = getTexture("media/ui/ArrowRight.png");
    o.arrowLeft = getTexture("media/ui/ArrowLeft.png");
    o.yButton = getTexture("media/ui/ybutton.png");
    o.disabledArrow = getTexture("media/ui/ArrowRight_Disabled.png");
    o.SkillPtsProgressBarEmpty = getTexture("media/ui/XpSystemUI/SkillPtsProgressBarEmpty.png")
    o.SkillPtsProgressBarStart = getTexture("media/ui/XpSystemUI/SkillPtsProgressBarStart.png")
    o.SkillPtsProgressBar = getTexture("media/ui/XpSystemUI/SkillPtsProgressBar.png")
    o.SkillBarSeparator = getTexture("media/ui/XpSystemUI/SkillBarSeparator.png")
    o.ProgressSkilMultiplier = getTexture("media/ui/XpSystemUI/ProgressSkilMultiplier.png")
    o.showingPassive = false
    ISCharacterInfo.instance = o;
   return o;
end

ISCharacterInfo.loadPerk = function(self, passive)
	local perks = {};
	-- we start to fetch all our perks
	for i = 0, PerkFactory.PerkList:size() - 1 do
		local perk = PerkFactory.PerkList:get(i);
		-- we only add in our list the child perk
		-- here we just display the active skill, not the passive ones (they are in another tab)
		if perk:getParent() ~= Perks.None and perk:isPassiv() == passive then
			-- we take the longest skill's name as width reference
			local pixLen = getTextManager():MeasureStringX(UIFont.Small, perk:getName());
			if pixLen > self.txtLen then
				self.txtLen = pixLen;
			end
			if not perks[perk:getParent()] then
				perks[perk:getParent()] = {};
			end
			table.insert(perks[perk:getParent()], perk);
		end
	end
	return perks
end

function ISCharacterInfo:onTogglePassive()
	self.showPassive = not self.showPassive
	if not self.joyfocus then
		self.togglePassiveBtn:setImage(self.showPassive and self.arrowLeft or self.arrow)
	end
	self.progressBarLoaded = false
	for i,v in pairs(self.progressBars) do
		self:removeChild(v)
	end
	self.progressBars = {}
	self.joypadIndex = nil
end

function ISCharacterInfo:updateTooltipForJoypad()
	if self.joypadIndex and self.joypadIndex >= 1 and self.joypadIndex <= #self.progressBars then
		if self.barWithTooltip then
			self.barWithTooltip:onMouseMoveOutside()
		end
		self.barWithTooltip = self.progressBars[self.joypadIndex]
		self.barWithTooltip:updateTooltip(self.barWithTooltip.level)
	elseif self.barWithTooltip then
		self.barWithTooltip:onMouseMoveOutside()
		self.barWithTooltip = nil
	end
end

function ISCharacterInfo:onGainJoypadFocus(joypadData)
	self.togglePassiveBtn:setImage(self.yButton)
	self.togglePassiveBtn:forceImageSize(24, 24)
	self.togglePassiveBtn.borderColor.a = 0
    ISPanelJoypad.onGainJoypadFocus(self, joypadData);
	self.joypadIndex = nil
	self.barWithTooltip = nil
end

function ISCharacterInfo:onLoseJoypadFocus(joypadData)
    ISPanelJoypad.onLoseJoypadFocus(self, joypadData);
end

function ISCharacterInfo:onJoypadDown(button)
	if button == Joypad.AButton then
		if self.joypadIndex and self.joypadIndex >= 1 and self.joypadIndex <= #self.progressBars then
			self.progressBars[self.joypadIndex]:activate()
		end
	end
	if button == Joypad.YButton then
		self:onTogglePassive()
	end
	if button == Joypad.BButton then
		getPlayerInfoPanel(self.playerNum):toggleView(xpSystemText.skills)
		setJoypadFocus(self.playerNum, nil)
	end
	if button == Joypad.LBumper then
		getPlayerInfoPanel(self.playerNum):onJoypadDown(button)
	end
	if button == Joypad.RBumper then
		getPlayerInfoPanel(self.playerNum):onJoypadDown(button)
	end
end

function ISCharacterInfo:onJoypadDirUp()
	if not self.joypadIndex or self.joypadIndex == 1 then
		self.joypadIndex = #self.progressBars
	else
		self.joypadIndex = self.joypadIndex - 1
	end
	self:updateTooltipForJoypad()
end

function ISCharacterInfo:onJoypadDirDown()
	if not self.joypadIndex or self.joypadIndex == #self.progressBars then
		self.joypadIndex = 1
	else
		self.joypadIndex = self.joypadIndex + 1
	end
	self:updateTooltipForJoypad()
end

function ISCharacterInfo:onJoypadDirLeft()
end

function ISCharacterInfo:onJoypadDirRight()
end

function ISCharacterInfo.onResolutionChange(oldw, oldh, neww, newh)
	if getPlayer() == null then return end -- back in main menu
	for pn=0,getNumActivePlayers()-1 do
		if getPlayerData(pn) then
			local charInfo = getPlayerInfoPanel(pn).characterView
			if charInfo and charInfo.progressBarLoaded then
				charInfo.progressBarLoaded = false;
				for i,v in pairs(charInfo.progressBars) do
					charInfo:removeChild(v);
				end
				charInfo.progressBars = {};
			end
		end
	end
end

Events.OnResolutionChange.Add(ISCharacterInfo.onResolutionChange)

