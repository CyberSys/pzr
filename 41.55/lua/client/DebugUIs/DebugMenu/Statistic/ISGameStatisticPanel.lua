--***********************************************************
--**                    THE INDIE STONE                    **
--**                      Author: yuri                     **
--***********************************************************

require "ISUI/ISPanel"

ISGameStatisticPanel = ISPanel:derive("ISGameStatisticPanel");
ISGameStatisticPanel.instance = nil;

local function roundstring(_val)
    return tostring(ISDebugUtils.roundNum(_val,2));
end

function ISGameStatisticPanel.OnOpenPanel()
    if ISGameStatisticPanel.instance==nil then
        ISGameStatisticPanel.instance = ISGameStatisticPanel:new (100, 100, 800, 800, "Statistic");
        ISGameStatisticPanel.instance:initialise();
        ISGameStatisticPanel.instance:instantiate();
    end

    ISGameStatisticPanel.instance:addToUIManager();
    ISGameStatisticPanel.instance:setVisible(true);

    return ISGameStatisticPanel.instance;
end

function ISGameStatisticPanel:initialise()
    ISPanel.initialise(self);

    self.flareCount = false;
    self.colExt = { r=1, g=1, b=1 };
    self.colInt = { r=1, g=1, b=1 };
    self.flareID = -1;
	self.cGreen = { r=0,g=0.3,b=0,a=1};
    self.cRed = { r=0.3,g=0,b=0,a=1};
end

function ISGameStatisticPanel:createChildren()
    ISPanel.createChildren(self);

    _, obj = ISDebugUtils.addLabel(self, {}, self.width/2, 15, "Server Statistic", UIFont.Medium, true); obj.center = true;

	local margin = 5;
	local xmarg = 20;
	
	local columnWidth = (self.width)/3;

    local boxWidth = 100;
	
	local x = 20;
    local x1 = 20;
	local xw = 120;
	local xw2 = 40;
    local y, obj = 50, false;

    ------- COLUMN 1 ---------
	x = 20
	y = 50
	_, self.comboBoxPeriods = ISDebugUtils.addComboBox(self,"combo_periods", x, y, columnWidth-(xmarg*2), UIFont.Small, ISGameStatisticPanel.onCombo); x = x + columnWidth;
    self.comboBoxPeriods:addOption("secound");
	self.comboBoxPeriods:addOption("2 secounds");
	self.comboBoxPeriods:addOption("5 secounds");
	self.comboBoxPeriods:addOption("10 secounds");
	self.comboBoxPeriods:addOption("30 secounds");
	self.comboBoxPeriods:addOption("minute");
	self.comboBoxPeriods:addOption("2 minutes");
	self.comboBoxPeriods:addOption("5 minutes");
	self.comboBoxPeriods:addOption("10 minutes");
	self.comboBoxPeriods:addOption("30 minutes");
	self.comboBoxPeriods:addOption("hour");
	self.comboBoxPeriods:addOption("disable");
    self.comboBoxPeriods.selected = 6;
	y, self.buttonToggleMonitor = ISDebugUtils.addButton(self,"toggle_statistic",x ,y ,columnWidth*2-(xmarg*2), 20, "Toggle statistic monitor", ISGameStatisticPanel.onClick);
	self.buttonToggleMonitor.backgroundColor = self.cRed;

    y = y+margin*2; x = x1;
    _, obj = ISDebugUtils.addLabel(self, {}, x, y, "Last report:", UIFont.Small, true); x = x + xw;
	y, self.lastReport = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true);
	
	y = y+margin*3; x = x1;
    y, obj = ISDebugUtils.addLabel(self, {}, x - xmarg + columnWidth/2, y, "UPDATE PERIOD", UIFont.Small, true); obj.center = true;
	y = y+margin; x = x1;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "min:", UIFont.Small, true); x = x + xw2;
	_, self.minUpdatePeriod = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "max:", UIFont.Small, true); x = x + xw2;
	_, self.maxUpdatePeriod = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "avg:", UIFont.Small, true); x = x + xw2;
	y, self.avgUpdatePeriod = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	y = y+margin; x = x1;
	y, self.buttonChartUpdatePeriod = ISDebugUtils.addButton(self, "CHART_UPDATE_PERIOD",x, y, columnWidth-2*xmarg, 20, getText("IGUI_Chart"),ISGameStatisticPanel.onClick);
	
	y = y+margin*3; x = x1;
    y, obj = ISDebugUtils.addLabel(self, {}, x - xmarg + columnWidth/2, y, "SERVER CELL DISK OPERATIONS", UIFont.Small, true); obj.center = true;
	y = y+margin; x = x1;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "load:", UIFont.Small, true); x = x + xw2;
	_, self.loadCellFromDisk = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "save:", UIFont.Small, true); x = x + xw2;
	y, self.saveCellToDisk = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	y = y+margin; x = x1;
	y, self.buttonChartDiskOperations = ISDebugUtils.addButton(self, "CHART_DISK_OPERATIONS",x, y, columnWidth-2*xmarg, 20, getText("IGUI_Chart"),ISGameStatisticPanel.onClick);
	
	y = y+margin*3; x = x1;
    y, obj = ISDebugUtils.addLabel(self, {}, x - xmarg + columnWidth/2, y, "MEMORY", UIFont.Small, true); obj.center = true;
	y = y+margin; x = x1;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "used:", UIFont.Small, true); x = x + xw2;
	y, self.usedMemory = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	y = y+margin; x = x1;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "free:", UIFont.Small, true); x = x + xw2;
	y, self.freeMemory = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	y = y+margin; x = x1;
	y, self.buttonChartMemory = ISDebugUtils.addButton(self, "CHART_MEMORY",x, y, columnWidth-2*xmarg, 20, getText("IGUI_Chart"),ISGameStatisticPanel.onClick);
	
	y = y+margin*3; x = x1;
	y, obj = ISDebugUtils.addLabel(self, {}, x - xmarg + columnWidth/2, y, "CONNECTIONS", UIFont.Small); obj.center = true;
    y = y+3;

    y, self.connections = ISDebugUtils.addComboBox(self,"combo_floats", xmarg, y, columnWidth-(xmarg*2), UIFont.Small, ISGameStatisticPanel.onCombo);
    self.connections:addOption("- NONE -");
    self.connections.selected = 1;
    
	y = y+margin; x = x1;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "IP:", UIFont.Small, true); x = x + xw2*2;
	y, self.connection_ip = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	
	y = y+margin; x = x1;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "access:", UIFont.Small, true); x = x + xw2*2;
	y, self.connection_access = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	
	y = y+margin; x = x1;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "Username:", UIFont.Small, true); x = x + xw2*2;
	y, self.connection_username = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	
	y = y+margin; x = x1;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "Ping:", UIFont.Small, true); x = x + xw2*2;
	_, self.connection_ping = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "avg:", UIFont.Small, true); x = x + xw2;
	y, self.connection_ping_avg = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	
	y = y+margin; x = x1;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "Player count:", UIFont.Small, true); x = x + xw2*2;
	_, self.connection_players_count = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "avg:", UIFont.Small, true); x = x + xw2;
	y, self.connection_players_desync_avg = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	y = y+margin; x = x1;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "max:", UIFont.Small, true); x = x + xw2*2;
	_, self.connection_players_desync_max = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "tlprt:", UIFont.Small, true); x = x + xw2;
	y, self.connection_players_desync_teleport = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;

	y = y+margin; x = x1;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "Zombie count:", UIFont.Small, true); x = x + xw2*2;
	_, self.connection_zombies_count = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "avg:", UIFont.Small, true); x = x + xw2;
	y, self.connection_zombies_desync_avg = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	y = y+margin; x = x1;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "max:", UIFont.Small, true); x = x + xw2*2;
	_, self.connection_zombies_desync_max = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	_, obj = ISDebugUtils.addLabel(self, {}, x, y, "tlprt:", UIFont.Small, true); x = x + xw2;
	y, self.connection_zombies_desync_teleport = ISDebugUtils.addLabel(self, {}, x, y, "___", UIFont.Small, true); x = x + xw2;
	
	y = y+margin; x = x1;
    y, obj = ISDebugUtils.addLabel(self, {}, x - xmarg + columnWidth/2, y, "PLAYERS", UIFont.Small, true); obj.center = true;
	y = y+margin; x = x1;
	self.connection_players = ISScrollingListBox:new(x, y, columnWidth- 2*xmarg, self.height - y - 20);
    self.connection_players:initialise();
    self.connection_players:instantiate();
    self.connection_players.itemheight = 35;
    self.connection_players.selected = 0;
    self.connection_players.joypadParent = self;
    self.connection_players.font = UIFont.NewSmall;
    self.connection_players.doDrawItem = self.drawUsersList;
    self.connection_players.drawBorder = true;
    self.connection_players.target = self;
    self:addChild(self.connection_players);
	
	------- COLUMN 2 ---------
	x1 = columnWidth+20
	y = 80
	
	y = y+margin; x = x1;
    y, obj = ISDebugUtils.addLabel(self, {}, x - xmarg + columnWidth/2, y, "INCOME PACKETS", UIFont.Small, true); obj.center = true;
	y = y+margin; x = x1;
	self.incomePackets = ISScrollingListBox:new(x, y, columnWidth- 2*xmarg, self.height - y - 60);
    self.incomePackets:initialise();
    self.incomePackets:instantiate();
    self.incomePackets.itemheight = 35;
    self.incomePackets.selected = 0;
    self.incomePackets.joypadParent = self;
    self.incomePackets.font = UIFont.NewSmall;
    self.incomePackets.doDrawItem = self.drawIncomePacketsList;
    self.incomePackets.drawBorder = true;
    self.incomePackets.target = self;
    self:addChild(self.incomePackets);
	
	------- COLUMN 3 ---------
	x1 = 2*columnWidth+20
	y = 80
	
	y = y+margin; x = x1;
    y, obj = ISDebugUtils.addLabel(self, {}, x - xmarg + columnWidth/2, y, "OUTCOME PACKETS", UIFont.Small, true); obj.center = true;
	y = y+margin; x = x1;
	self.outcomePackets = ISScrollingListBox:new(x, y, columnWidth- 2*xmarg, self.height - y - 60);
    self.outcomePackets:initialise();
    self.outcomePackets:instantiate();
    self.outcomePackets.itemheight = 35;
    self.outcomePackets.selected = 0;
    self.outcomePackets.joypadParent = self;
    self.outcomePackets.font = UIFont.NewSmall;
    self.outcomePackets.doDrawItem = self.drawOutcomePacketsList;
    self.outcomePackets.drawBorder = true;
    self.outcomePackets.target = self;
    self:addChild(self.outcomePackets);
	------- BOTTOM ---------

    y = y + 3;

	_, self.buttonChartPackets = ISDebugUtils.addButton(self, "CHART_PACKETS",columnWidth+xmarg, self.height-40, columnWidth-2*xmarg, 20, getText("IGUI_Chart_Packets"),ISGameStatisticPanel.onClick);
    local y, obj = ISDebugUtils.addButton(self,"close",columnWidth*2+xmarg, self.height-40, columnWidth-2*xmarg, 20,getText("IGUI_CraftUI_Close"),ISGameStatisticPanel.onClickClose);

    self:populateConnectionsList();
	
	self.init = true
	
	StatisticChartUpdatePeriod.doInstance();
	StatisticChartDiskOperations.doInstance();
	StatisticChartMemory.doInstance();
	StatisticChartPackets.doInstance();
end

function ISGameStatisticPanel:onCombo(_combo)
    
end

function ISGameStatisticPanel:onClick(_button)
    if self.buttonToggleMonitor==_button then
		local enable = getServerStatisticEnable();
		local period = 60;
		if self.comboBoxPeriods.selected == 1 then
			period = 1;
		elseif self.comboBoxPeriods.selected == 2 then
			period = 2;
		elseif self.comboBoxPeriods.selected == 3 then
			period = 5;
		elseif self.comboBoxPeriods.selected == 4 then
			period = 10;
		elseif self.comboBoxPeriods.selected == 5 then
			period = 30;
		elseif self.comboBoxPeriods.selected == 6 then
			period = 60;
		elseif self.comboBoxPeriods.selected == 7 then
			period = 120;
		elseif self.comboBoxPeriods.selected == 8 then
			period = 300;
		elseif self.comboBoxPeriods.selected == 9 then
			period = 600;
		elseif self.comboBoxPeriods.selected == 10 then
			period = 1800;
		elseif self.comboBoxPeriods.selected == 11 then
			period = 3600;
		elseif self.comboBoxPeriods.selected == 12 then
			period = 0;
		end
        if enable then
			setServerStatisticEnable(false, period);
            self.buttonToggleMonitor.backgroundColor = self.cRed;
			self.comboBoxPeriods.enable = true
        else
			setServerStatisticEnable(true, period);
            self.buttonToggleMonitor.backgroundColor = self.cGreen;
			self.comboBoxPeriods.enable = false
        end
		return;
	end
	if self.buttonChartUpdatePeriod==_button then
		StatisticChartUpdatePeriod.OnOpenPanel()
		return;
	end
	if self.buttonChartDiskOperations == _button then
		StatisticChartDiskOperations.OnOpenPanel()
		return;
	end
	if self.buttonChartMemory==_button then
		StatisticChartMemory.OnOpenPanel()
		return;
	end
	if self.buttonChartPackets==_button then
		StatisticChartPackets.OnOpenPanel()
		return;
	end
	
end

function ISGameStatisticPanel:onClickClose()
    self:close();
end

function ISGameStatisticPanel:OnFlaresListMouseDown(item)
    --self:populateInfoList(item);
    self.flareID = item:getId();
end

function ISGameStatisticPanel:populateConnectionsList()
	self.data = getServerStatistic();
	if self.data ~= nil then
		local sel = self.connections.options[self.connections.selected];
		self.connections:clear();
		self.connections:addOption("- NONE -");
		local newindex = 1;
		for k, v in pairs(self.data.connections) do
			local name = v.username;
			self.connections:addOption(name);
			if sel and sel==name then
				newindex = k+2;
			end
		end
		self.connections.selected = newindex;
	end
end

function ISGameStatisticPanel:populateUsersList(connect)
	if connect ~= nil then
		self.connection_players:clear();
		for k, v in pairs(connect) do
			self.connection_players:addItem(v.username, v);
		end
	end
end

function ISGameStatisticPanel:populatePacketsList()
	self.data = getServerStatistic();
	if self.data ~= nil then
		self.incomePackets:clear();
		for k, v in pairs(self.data.incomePacketsTable) do
			self.incomePackets:addItem(v.name, v);
		end
		self.outcomePackets:clear();
		for k, v in pairs(self.data.outcomePacketsTable) do
			self.outcomePackets:addItem(v.name, v);
		end
	end
end

function ISGameStatisticPanel:drawUsersList(y, item, alt)
    local a = 0.9;

    self:drawRectBorder(0, (y), self:getWidth()-10, self.itemheight - 1, a, self.borderColor.r, self.borderColor.g, self.borderColor.b);

    self:drawText( item.text, 10, y + 2, 1, 1, 1, a, self.font);
	self:drawText( tostring(item.item.x)..", "..tostring(item.item.y)..", "..tostring(item.item.z), 10, y + 17, 1, 1, 1, a, self.font);
    return y + self.itemheight;
end


function ISGameStatisticPanel:drawOutcomePacketsList(y, item, alt)
    local a = 0.9;

    self:drawRectBorder(0, (y), self:getWidth()-14, self.itemheight - 1, a, self.borderColor.r, self.borderColor.g, self.borderColor.b);
	
	self.data = getServerStatistic();
	if self.data ~= nil then
		self:drawRect( 10, (y+19), 90.0 * math.min(1.0, item.item.count / self.data.countOutcomePackets) , 12 , a, 0.6, 0.2, 0.2);
		self:drawRect( 100, (y+19), 90.0 * math.min(1.0, item.item.bytes / self.data.countOutcomeBytes) , 12 , a, 0.2, 0.6, 0.2);
	end

    self:drawText( item.text, 10, y + 2, 1, 1, 1, a, self.font);
	self:drawText( "count: "..tostring(item.item.count), 10, y + 17, 1, 1, 1, a, self.font);
	self:drawText( "bytes: "..tostring(item.item.bytes), 110, y + 17, 1, 1, 1, a, self.font);
    return y + self.itemheight;
end

function ISGameStatisticPanel:drawIncomePacketsList(y, item, alt)
    local a = 0.9;

    self:drawRectBorder(0, (y), self:getWidth()-14, self.itemheight - 1, a, self.borderColor.r, self.borderColor.g, self.borderColor.b);
	
	self.data = getServerStatistic();
	if self.data ~= nil then
		self:drawRect( 10, (y+19), 90.0 * math.min(1.0, item.item.count / self.data.countIncomePackets) , 12 , a, 0.6, 0.2, 0.2);
		self:drawRect( 100, (y+19), 90.0 * math.min(1.0, item.item.bytes / self.data.countIncomeBytes) , 12 , a, 0.2, 0.6, 0.2);
	end

    self:drawText( item.text, 10, y + 2, 1, 1, 1, a, self.font);
	self:drawText( "count: "..tostring(item.item.count), 10, y + 17, 1, 1, 1, a, self.font);
	self:drawText( "bytes: "..tostring(item.item.bytes), 110, y + 17, 1, 1, 1, a, self.font);
    return y + self.itemheight;
end

function ISGameStatisticPanel:prerender()
    ISPanel.prerender(self);
	
	self.data = getServerStatistic();
	if self.data ~= nil and self.lastReport ~= nil then
		local indicatorWidth = ((self.width)/3) - 40;
		if (self.data.period >= 1) then
			local period = math.min(1.0, (getTimeInMillis() - self.data.lastReportTime) / (self.data.period * 1000.0));
			self:drawRect( 20, self.lastReport.y + 20, indicatorWidth * period, 6, 1.0, 0.2f, 0.6f, 0.2f);
			self:drawRect( 20 + (indicatorWidth * period), self.lastReport.y + 20, indicatorWidth * (1.0f - period), 6, 1.0, 0.6f, 0.2f, 0.2f);
		else 
			self:drawRect( 20, self.lastReport.y + 20, indicatorWidth, 6, 1.0, 0.2f, 0.2f, 0.2f);
		end
	end
end

function ISGameStatisticPanel:updateValues()
	if not self.init then
		return
	end
	local enable = getServerStatisticEnable();
	if enable then
		self.data = getServerStatistic();
		if self.data ~= nil then
			if self.data.period >= 3600 then
				self.comboBoxPeriods.selected = 11;
			elseif self.data.period >= 1800 then
				self.comboBoxPeriods.selected = 10;
			elseif self.data.period >= 600 then
				self.comboBoxPeriods.selected = 9;
			elseif self.data.period >= 300 then
				self.comboBoxPeriods.selected = 8;
			elseif self.data.period >= 120 then
				self.comboBoxPeriods.selected = 7;
			elseif self.data.period >= 60 then
				self.comboBoxPeriods.selected = 6;
			elseif self.data.period >= 30 then
				self.comboBoxPeriods.selected = 5;
			elseif self.data.period >= 10 then
				self.comboBoxPeriods.selected = 4;
			elseif self.data.period >= 5 then
				self.comboBoxPeriods.selected = 3;
			elseif self.data.period >= 2 then
				self.comboBoxPeriods.selected = 2;
			elseif self.data.period >= 1 then
				self.comboBoxPeriods.selected = 1;
			else
				self.comboBoxPeriods.selected = 12;
			end
			self.lastReport:setName(tostring(self.data.lastReport));
			self.minUpdatePeriod:setName(tostring(self.data.minUpdatePeriod));
			self.maxUpdatePeriod:setName(tostring(self.data.maxUpdatePeriod));
			self.avgUpdatePeriod:setName(tostring(self.data.avgUpdatePeriod));
			self.loadCellFromDisk:setName(tostring(self.data.loadCellFromDisk));
			self.saveCellToDisk:setName(tostring(self.data.saveCellToDisk));
			self.usedMemory:setName(tostring(self.data.usedMemory));
			self.freeMemory:setName(tostring(self.data.freeMemory));
			self:populateConnectionsList();
			local sel = self.connections.options[self.connections.selected];
			for k, v in pairs(self.data.connections) do
				if sel and sel==v.username then
					self.connection_ip:setName(tostring(v.ip));
					self.connection_access:setName(tostring(v.accessLevel));
					self.connection_username:setName(tostring(v.username));
					self.connection_ping:setName(string.format("%.2f", v.diff * 0.5f));
					self.connection_ping_avg:setName(string.format("%.2f", v.pingAVG));
					self.connection_players_count:setName(string.format("%.2f", v.remotePlayersCount));
					self.connection_players_desync_avg:setName(string.format("%.2f", v.remotePlayersDesyncAVG));
					self.connection_players_desync_max:setName(string.format("%.2f", v.remotePlayersDesyncMax));
					self.connection_players_desync_teleport:setName(string.format("%.2f", v.remotePlayersTeleports));
					self.connection_zombies_count:setName(string.format("%.2f", v.zombiesCount));
					self.connection_zombies_desync_avg:setName(string.format("%.2f", v.zombiesDesyncAVG));
					self.connection_zombies_desync_max:setName(string.format("%.2f", v.zombiesDesyncMax));
					self.connection_zombies_desync_teleport:setName(string.format("%.2f", v.zombiesTeleports));
					self:populateUsersList(v.users)
					break;
				end
			end
			self:populatePacketsList();
		end
	end
	
end

ISGameStatisticPanel.OnServerStatisticReceived = function()
	if ISGameStatisticPanel.instance==nil then
        ISGameStatisticPanel.instance = ISGameStatisticPanel:new (100, 100, 800, 800, "Statistic");
        ISGameStatisticPanel.instance:initialise();
        ISGameStatisticPanel.instance:instantiate();
    end
	ISGameStatisticPanel.instance:updateValues()
end

Events.OnServerStatisticReceived.Add(ISGameStatisticPanel.OnServerStatisticReceived);

function ISGameStatisticPanel:close()
    self:setVisible(false);
    self:removeFromUIManager();
    ISGameStatisticPanel.instance = nil
end

function ISGameStatisticPanel:new(x, y, width, height, title)
    local o = {};
    o = ISPanel:new(x, y, width, height);
    setmetatable(o, self);
    self.__index = self;
    o.variableColor={r=0.9, g=0.55, b=0.1, a=1};
    o.borderColor = {r=0.4, g=0.4, b=0.4, a=1};
    o.backgroundColor = {r=0, g=0, b=0, a=0.8};
    o.buttonBorderColor = {r=0.7, g=0.7, b=0.7, a=0.5};
    o.zOffsetSmallFont = 25;
    o.moveWithMouse = true;
    o.panelTitle = title;
    ISDebugMenu.RegisterClass(self);
    return o;
end


