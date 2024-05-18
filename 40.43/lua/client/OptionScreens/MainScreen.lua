--***********************************************************
--**                LEMMY/ROBERT JOHNSON                   **
--***********************************************************

require "ISUI/ISPanel"
require "ISUI/ISButton"
require "ISUI/ISInventoryPane"
require "ISUI/ISResizeWidget"
require "ISUI/ISMouseDrag"

require "defines"

MainScreen = ISPanelJoypad:derive("MainScreen");

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)

function MainScreen:initialise()
	ISPanel.initialise(self);
end

function MainScreen:getLatestSave()
    local latestSave = getLatestSave();
    MainScreen.latestSaveWorld = latestSave[1];
    MainScreen.latestSaveGameMode = latestSave[2];
end

--************************************************************************--
--** ISPanel:instantiate
--**
--************************************************************************--
function MainScreen:setBottomPanelVisible(visible)
    self.javaObject:setVisible(visible);
    if self.parent and self.parent.versionDetail then
        self.parent.versionDetail:setVisible(visible);
    end
end

function MainScreen:instantiate()

	MainScreen.instance = self;
    self:getLatestSave();
	--self:initialise();
	self.javaObject = UIElement.new(self);
	self.javaObject:setX(self.x);
	self.javaObject:setY(self.y);
	self.javaObject:setHeight(self.height);
	self.javaObject:setWidth(self.width);
	self.javaObject:setAnchorLeft(self.anchorLeft);
	self.javaObject:setAnchorRight(self.anchorRight);
	self.javaObject:setAnchorTop(self.anchorTop);
	self.javaObject:setAnchorBottom(self.anchorBottom);

    -- These should be the same as in prerender()
    local logoScale = getCore():getScreenWidth() / 1920
    local tex = self.logoTexture
    local logoX = 50 * logoScale
    local logoY = 50 * logoScale
    local logoWidth = tex:getWidth() * logoScale
    local logoHgt = tex:getHeight() * logoScale

    self.bottomPanel = ISPanel:new(100, logoY + logoHgt + 30, 400, 400);
    self.bottomPanel:initialise();
    self.bottomPanel:setAnchorRight(false);
    self.bottomPanel:setAnchorLeft(true);
    self.bottomPanel:setAnchorBottom(false);
    self.bottomPanel:setAnchorTop(false);
    self:addChild(self.bottomPanel);
    self.bottomPanel.backgroundColor = {r=0, g=0, b=0, a=0.0};
    self.bottomPanel:noBackground()
    MainScreen.instance.bottomPanel.setVisible = MainScreen.setBottomPanelVisible;


    if not self.inGame and not isDemo() then


        self.joinServer = ServerList:new(0, 0, self.width, self.height);

        self.joinServer:initialise();
        self.joinServer:setVisible(false);
        self.joinServer:setAnchorRight(true);
        self.joinServer:setAnchorLeft(true);
        self.joinServer:setAnchorBottom(true);
        self.joinServer:setAnchorTop(true);
        self.joinServer.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.joinServer.borderColor = {r=1, g=1, b=1, a=0.5};

        self.bootstrapConnectPopup = BootstrapConnectPopup:new(0, 0, self.width, self.height);
        self.bootstrapConnectPopup:initialise();
        self.bootstrapConnectPopup:setVisible(false);
        self.bootstrapConnectPopup:setAnchorRight(true);
        self.bootstrapConnectPopup:setAnchorLeft(true);
        self.bootstrapConnectPopup:setAnchorBottom(true);
        self.bootstrapConnectPopup:setAnchorTop(true);
        self.bootstrapConnectPopup.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.bootstrapConnectPopup.borderColor = {r=1, g=1, b=1, a=0.5};

        self.connectToServer = ConnectToServer:new(0, 0, self.width, self.height);
        self.connectToServer:initialise();
        self.connectToServer:setVisible(false);
        self.connectToServer:setAnchorRight(true);
        self.connectToServer:setAnchorLeft(true);
        self.connectToServer:setAnchorBottom(true);
        self.connectToServer:setAnchorTop(true);
        self.connectToServer.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.connectToServer.borderColor = {r=1, g=1, b=1, a=0.5};

        self.serverConnectPopup = ServerConnectPopup:new(0, 0, self.width, self.height);
        self.serverConnectPopup:initialise();
        self.serverConnectPopup:setVisible(false);
        self.serverConnectPopup:setAnchorRight(true);
        self.serverConnectPopup:setAnchorLeft(true);
        self.serverConnectPopup:setAnchorBottom(true);
        self.serverConnectPopup:setAnchorTop(true);
        self.serverConnectPopup.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.serverConnectPopup.borderColor = {r=1, g=1, b=1, a=0.5};


        self.joinPublicServer = PublicServerList:new(0, 0, self.width, self.height);

        self.joinPublicServer:initialise();
        self.joinPublicServer:setVisible(false);
        self.joinPublicServer:setAnchorRight(true);
        self.joinPublicServer:setAnchorLeft(true);
        self.joinPublicServer:setAnchorBottom(true);
        self.joinPublicServer:setAnchorTop(true);
        self.joinPublicServer.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.joinPublicServer.borderColor = {r=1, g=1, b=1, a=0.5};

        self.soloScreen = NewGameScreen:new(0, 0, self.width, self.height);
        self.soloScreen:initialise();
        self.soloScreen:setVisible(false);
        self.soloScreen:setAnchorRight(true);
        self.soloScreen:setAnchorLeft(true);
        self.soloScreen:setAnchorBottom(true);
        self.soloScreen:setAnchorTop(true);
        self.soloScreen.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.soloScreen.borderColor = {r=1, g=1, b=1, a=0.5};

        self.loadScreen = LoadGameScreen:new(0, 0, self.width, self.height);
        self.loadScreen:initialise();
        self.loadScreen:setVisible(false);
        self.loadScreen:setAnchorRight(true);
        self.loadScreen:setAnchorLeft(true);
        self.loadScreen:setAnchorBottom(true);
        self.loadScreen:setAnchorTop(true);
        self.loadScreen.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.loadScreen.borderColor = {r=1, g=1, b=1, a=0.5};

        self.onlineCoopScreen = CoopOptionsScreen:new(0, 0, self.width, self.height);
        self.onlineCoopScreen:initialise();
        self.onlineCoopScreen:setVisible(false);
        self.onlineCoopScreen:setAnchorRight(true);
        self.onlineCoopScreen:setAnchorLeft(true);
        self.onlineCoopScreen:setAnchorTop(true);
        self.onlineCoopScreen:setAnchorBottom(true);
        self.onlineCoopScreen.backgroundColor = { r = 0, g = 0, b = 0, a = 0.8 };
        self.onlineCoopScreen.borderColor = { r = 1, g = 1, b = 1, a = 0.5 };

        if WorkshopSubmitScreen.TEST or getSteamModeActive() then
            self.workshopSubmit = WorkshopSubmitScreen:new(0, 0, self.width, self.height)
            self.workshopSubmit:initialise()
            self.workshopSubmit:setVisible(false)
            self.workshopSubmit:setAnchorRight(true)
            self.workshopSubmit:setAnchorLeft(true)
            self.workshopSubmit:setAnchorBottom(true)
            self.workshopSubmit:setAnchorTop(true)
            self.workshopSubmit.backgroundColor = {r=0, g=0, b=0, a=0.8}
            self.workshopSubmit.borderColor = {r=1, g=1, b=1, a=0.5}
        end

        if getSteamModeActive() then
            self.serverWorkshopItem = ServerWorkshopItemScreen:new(0, 0, self.width, self.height)
            self.serverWorkshopItem:initialise()
            self.serverWorkshopItem:setVisible(false)
            self.serverWorkshopItem:setAnchorRight(true)
            self.serverWorkshopItem:setAnchorLeft(true)
            self.serverWorkshopItem:setAnchorBottom(true)
            self.serverWorkshopItem:setAnchorTop(true)
            self.serverWorkshopItem.backgroundColor = {r=0, g=0, b=0, a=0.8}
            self.serverWorkshopItem.borderColor = {r=1, g=1, b=1, a=0.5}
        end
    end



    self.mainOptions = MainOptions:new(0, 0, self.width, self.height);

    self.mainOptions:initialise();
    self.mainOptions:setVisible(false);
    self.mainOptions:setAnchorRight(true);
    self.mainOptions:setAnchorLeft(true);
    self.mainOptions:setAnchorBottom(true);
    self.mainOptions:setAnchorTop(true);

    self.mainOptions.backgroundColor = {r=0, g=0, b=0, a=0.8};
    self.mainOptions.borderColor = {r=1, g=1, b=1, a=0.5};

    if self.inGame then
        if isClient() then
            self.scoreboard = ISScoreboard:new(0, 0, self.width, self.height);
            self.scoreboard:initialise()
            self.scoreboard:setVisible(false);
            self.scoreboard:setAnchorRight(true)
            self.scoreboard:setAnchorBottom(true)

            self.inviteFriends = InviteFriends:new(0, 0, self.width, self.height)
            self.inviteFriends:initialise();
            self.inviteFriends:setVisible(false);
            self.inviteFriends:setAnchorRight(true);
            self.inviteFriends:setAnchorBottom(true);
        end
    elseif not isDemo() then
        self.sandOptions = SandboxOptionsScreen:new(0, 0, self.width, self.height);

        self.sandOptions:initialise();
        self.sandOptions:setVisible(false);
        self.sandOptions:setAnchorRight(true);
        self.sandOptions:setAnchorLeft(true);
        self.sandOptions:setAnchorBottom(true);
        self.sandOptions:setAnchorTop(true);

        self.sandOptions.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.sandOptions.borderColor = {r=1, g=1, b=1, a=0.5};

        self.worldSelect = WorldSelect:new(0, 0, self.width, self.height);

        self.worldSelect:initialise();
        self.worldSelect:setVisible(false);
        self.worldSelect:setAnchorRight(true);
        self.worldSelect:setAnchorLeft(true);
        self.worldSelect:setAnchorBottom(true);
        self.worldSelect:setAnchorTop(true);
        self.worldSelect.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.worldSelect.borderColor = {r=1, g=1, b=1, a=0.5};

        self.mapSpawnSelect = MapSpawnSelect:new(0, 0, self.width, self.height)
        self.mapSpawnSelect:initialise()
        self.mapSpawnSelect:setVisible(false)
        self.mapSpawnSelect:setAnchorRight(true);
        self.mapSpawnSelect:setAnchorLeft(true);
        self.mapSpawnSelect:setAnchorBottom(true);
        self.mapSpawnSelect:setAnchorTop(true);
        self.mapSpawnSelect.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.mapSpawnSelect.borderColor = {r=1, g=1, b=1, a=0.5};

        self.modSelect = ModSelector:new(0, 0, self.width, self.height);

        self.modSelect:initialise();
        self.modSelect:setVisible(false);
        self.modSelect:setAnchorRight(true);
        self.modSelect:setAnchorLeft(true);
        self.modSelect:setAnchorBottom(true);
        self.modSelect:setAnchorTop(true);

        self.modSelect.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.modSelect.borderColor = {r=1, g=1, b=1, a=0.5};

        self.charCreationMain = CharacterCreationMain:new(0, 0, self:getWidth(), self:getHeight());

        self.charCreationMain:initialise();
        self.charCreationMain:setVisible(false);
        self.charCreationMain:setAnchorRight(true);
        self.charCreationMain:setAnchorLeft(true);
        self.charCreationMain:setAnchorBottom(true);
        self.charCreationMain:setAnchorTop(true);

        self.charCreationMain.backgroundColor = {r=0, g=0, b=0, a=0.0};
        self.charCreationMain.borderColor = {r=1, g=1, b=1, a=0.0};

        self.charCreationProfession = CharacterCreationProfession:new(0, 0, self:getWidth(), self:getHeight());

        self.charCreationProfession:initialise();
        self.charCreationProfession:setVisible(false);
        self.charCreationProfession:setAnchorRight(true);
        self.charCreationProfession:setAnchorLeft(true);
        self.charCreationProfession:setAnchorBottom(true);
        self.charCreationProfession:setAnchorTop(true);

        self.charCreationProfession.backgroundColor = {r=0, g=0, b=0, a=0.0};
        self.charCreationProfession.borderColor = {r=1, g=1, b=1, a=0.0};

        self.charCreationHeader = CharacterCreationHeader:new(0, 0, 600, 200);
        self.charCreationHeader:initialise();
        self.charCreationHeader:setVisible(true);
        self.charCreationHeader:setAnchorRight(true);
        self.charCreationHeader:setAnchorLeft(true);
        self.charCreationHeader:setAnchorBottom(false);
        self.charCreationHeader:setAnchorTop(true);

        self.charCreationHeader.backgroundColor = {r=0, g=0, b=0, a=0.0};
        self.charCreationHeader.borderColor = {r=1, g=1, b=1, a=0.0};


        self.storyScreen = StoryScreen:new(0, 0, self.width, self.height);

        self.storyScreen:initialise();
        self.storyScreen:setVisible(false);
        self.storyScreen:setAnchorRight(true);
        self.storyScreen:setAnchorLeft(true);
        self.storyScreen:setAnchorBottom(true);
        self.storyScreen:setAnchorTop(true);

        self.storyScreen.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.storyScreen.borderColor = {r=1, g=1, b=1, a=0.5};

        self.storyNewOrLoadScreen = StoryNewOrLoad:new(0, 0, self.width, self.height);

        self.storyNewOrLoadScreen:initialise();
        self.storyNewOrLoadScreen:setVisible(false);
        self.storyNewOrLoadScreen:setAnchorRight(true);
        self.storyNewOrLoadScreen:setAnchorLeft(true);
        self.storyNewOrLoadScreen:setAnchorBottom(true);
        self.storyNewOrLoadScreen:setAnchorTop(true);

        self.storyNewOrLoadScreen.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.storyNewOrLoadScreen.borderColor = {r=1, g=1, b=1, a=0.5};

        self.savedStory = getStorySavedTable();

		self.lastStandPlayerSelect = LastStandPlayerSelect:new(0, 0, self.width, self.height);

        self.lastStandPlayerSelect:initialise();
        self.lastStandPlayerSelect:setVisible(false);
        self.lastStandPlayerSelect:setAnchorRight(true);
        self.lastStandPlayerSelect:setAnchorLeft(true);
        self.lastStandPlayerSelect:setAnchorBottom(true);
        self.lastStandPlayerSelect:setAnchorTop(true);

        self.lastStandPlayerSelect.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.lastStandPlayerSelect.borderColor = {r=1, g=1, b=1, a=0.5};


        self.serverSettingsScreen = ServerSettingsScreen:new(0, 0, self.width, self.height);
        self.serverSettingsScreen:initialise();
        self.serverSettingsScreen:setVisible(false);
        self.serverSettingsScreen:setAnchorRight(true);
        self.serverSettingsScreen:setAnchorLeft(true);
        self.serverSettingsScreen:setAnchorBottom(true);
        self.serverSettingsScreen:setAnchorTop(true);
        self.serverSettingsScreen.backgroundColor = {r=0, g=0, b=0, a=0.8};
        self.serverSettingsScreen.borderColor = {r=1, g=1, b=1, a=0.5};
    end

    if self.inGame then
        if isClient() then
            self:addChild(self.scoreboard)
            self:addChild(self.inviteFriends);
        end
    elseif not isDemo() then
        self:addChild(self.charCreationMain);

    	self:addChild(self.charCreationProfession);
        self:addChild(self.sandOptions);
        self:addChild(self.onlineCoopScreen);
        self:addChild(self.soloScreen);
        self:addChild(self.loadScreen);
        self:addChild(self.worldSelect);
        self:addChild(self.mapSpawnSelect);
        self:addChild(self.modSelect);
        self:addChild(self.joinServer);
        self:addChild(self.bootstrapConnectPopup);
        self:addChild(self.connectToServer);
        self:addChild(self.serverConnectPopup);
        if isPublicServerListAllowed() then
            self:addChild(self.joinPublicServer);
        end
        self:addChild(self.storyScreen);
        self:addChild(self.storyNewOrLoadScreen);
		self:addChild(self.lastStandPlayerSelect);
        if self.workshopSubmit then
            self:addChild(self.workshopSubmit)
        end
        if self.serverWorkshopItem then
            self:addChild(self.serverWorkshopItem)
        end
		self:addChild(self.serverSettingsScreen);
    end

    self:addChild(self.mainOptions);


	-- resize to screen window / cause resize
	local w = getCore():getScreenWidth();
	local h = getCore():getScreenHeight();
	self:setWidth(w);
	self:setHeight(h);
	self:recalcSize();
	if w > 1024 then
		self.mainOptions:shrinkX(0.7);
		self.mainOptions:shrinkY(0.8);
    else
		self.mainOptions:shrinkX(0.95);
		self.mainOptions:shrinkY(0.95);
    end
    self.mainOptions:ignoreWidthChange();
    self.mainOptions:ignoreHeightChange();

	local scaleX = 0.6
	local scaleY = 0.7
	if self.width <= 1024 then
		scaleX = 0.95
		scaleY = 0.95
	end

    if self.inGame then
        if isClient() then
            self.scoreboard:shrinkX(scaleX);
            self.scoreboard:shrinkY(scaleY);
            self.scoreboard:ignoreWidthChange();
            self.scoreboard:ignoreHeightChange();

            self.inviteFriends:shrinkX(scaleX);
            self.inviteFriends:shrinkY(scaleY);
            self.inviteFriends:ignoreWidthChange();
            self.inviteFriends:ignoreHeightChange();

        end
    elseif not isDemo() then
		scaleX = 0.7
		scaleY = 0.8
		if self.width <= 1024 then
			scaleX = 0.95
			scaleY = 0.95
		end
		
        self.joinServer:shrinkX(scaleX);
        self.joinServer:shrinkY(scaleY);
        self.joinServer:ignoreWidthChange();
        self.joinServer:ignoreHeightChange();

        self.bootstrapConnectPopup:shrinkX(scaleX);
        self.bootstrapConnectPopup:shrinkY(scaleY);
        self.bootstrapConnectPopup:ignoreWidthChange();
        self.bootstrapConnectPopup:ignoreHeightChange();

        self.connectToServer:shrinkX(scaleX);
        self.connectToServer:shrinkY(scaleY);
        self.connectToServer:ignoreWidthChange();
        self.connectToServer:ignoreHeightChange();

        self.serverConnectPopup:shrinkX(scaleX);
        self.serverConnectPopup:shrinkY(scaleY);
        self.serverConnectPopup:ignoreWidthChange();
        self.serverConnectPopup:ignoreHeightChange();

        self.joinPublicServer:shrinkX(scaleX);
        self.joinPublicServer:shrinkY(scaleY);
        self.joinPublicServer:ignoreWidthChange();
        self.joinPublicServer:ignoreHeightChange();

        self.soloScreen:shrinkX(scaleX);
        self.soloScreen:shrinkY(scaleY);
        self.soloScreen:ignoreWidthChange();
        self.soloScreen:ignoreHeightChange();

        self.loadScreen:shrinkX(scaleX);
        self.loadScreen:shrinkY(scaleY);
        self.loadScreen:ignoreWidthChange();
        self.loadScreen:ignoreHeightChange();

        self.onlineCoopScreen:shrinkX(scaleX);
        self.onlineCoopScreen:shrinkY(scaleY);
        self.onlineCoopScreen:ignoreWidthChange();
        self.onlineCoopScreen:ignoreHeightChange();

        self.worldSelect:shrinkX(scaleX);
        self.worldSelect:shrinkY(scaleY);
        self.worldSelect:ignoreWidthChange();
        self.worldSelect:ignoreHeightChange();

        self.mapSpawnSelect:shrinkX(scaleX);
        self.mapSpawnSelect:shrinkY(scaleY);
        self.mapSpawnSelect:ignoreWidthChange();
        self.mapSpawnSelect:ignoreHeightChange();

		scaleX = 0.8
		scaleY = 0.8
		if self.width <= 1024 then
			scaleX = 0.95
			scaleY = 0.95
		end
		
        self.sandOptions:shrinkX(scaleX);
        self.sandOptions:shrinkY(scaleY);
        self.sandOptions:ignoreWidthChange();
        self.sandOptions:ignoreHeightChange();

		scaleX = 0.9
		scaleY = 0.9
		if self.width <= 1024 then
			scaleX = 0.95
			scaleY = 0.95
		end

        self.storyScreen:shrinkX(scaleX);
        self.storyScreen:shrinkY(scaleY);
        self.storyScreen:ignoreWidthChange();
        self.storyScreen:ignoreHeightChange();

        self.modSelect:shrinkX(scaleX);
        self.modSelect:shrinkY(scaleY);
        self.modSelect:ignoreWidthChange();
        self.modSelect:ignoreHeightChange();

        self.storyNewOrLoadScreen:shrinkX(0.3);
        self.storyNewOrLoadScreen:shrinkY(0.15);
        self.storyNewOrLoadScreen:ignoreWidthChange();
        self.storyNewOrLoadScreen:ignoreHeightChange();

		self.lastStandPlayerSelect:shrinkX(scaleX);
        self.lastStandPlayerSelect:shrinkY(scaleY);
        self.lastStandPlayerSelect:ignoreWidthChange();
        self.lastStandPlayerSelect:ignoreHeightChange();

        if self.workshopSubmit then
            self.workshopSubmit:shrinkX(scaleX)
            self.workshopSubmit:shrinkY(scaleY)
            self.workshopSubmit:ignoreWidthChange()
            self.workshopSubmit:ignoreHeightChange()
        end
        if self.serverWorkshopItem then
            self.serverWorkshopItem:shrinkX(scaleX)
            self.serverWorkshopItem:shrinkY(scaleY)
            self.serverWorkshopItem:ignoreWidthChange()
            self.serverWorkshopItem:ignoreHeightChange()
        end
        
        self.serverSettingsScreen:shrinkX(scaleX)
        self.serverSettingsScreen:shrinkY(scaleY)
        self.serverSettingsScreen:ignoreWidthChange()
        self.serverSettingsScreen:ignoreHeightChange()

		deleteAllGameModeSaves("LastStand");
		deleteAllGameModeSaves("Tutorial");

    end

    local labelHgt = getTextManager():getFontHeight(UIFont.Large) + 8 * 2
    local labelX = 0
    local labelY = 0
    local labelSeparator = 16

    if not self.inGame then

        if isXBOXController() and JoypadState.joypads[0] == nil then
            self.controllerLabel = ISLabel:new(self.width/2 - 50, 15, 32, "Press", 1, 1, 1, 0.7, UIFont.Small);
            self.controllerLabel:initialise();
            self.bottomPanel:addChild(self.controllerLabel);

            self.abutton = ISImage:new (self.controllerLabel.x + 31, 17, 32, 32, getTexture("media/ui/abutton.png"));
            self.abutton:initialise();
            self.bottomPanel:addChild(self.abutton);

            self.controllerLabel2 = ISLabel:new(self.abutton.x + 35, 15, 32, "to activate controller.", 1, 1, 1, 0.7, UIFont.Small, true);
            self.controllerLabel2:initialise();
            self.bottomPanel:addChild(self.controllerLabel2);
        end

        self.debOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_debug"), 1, 1, 1, 0.7, UIFont.Small);
        self.debOption.internal = "DEBUG";
        self.debOption:initialise();
        --self:addChild(self.debOption);
        self.debOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
        self.debOption:setVisible(true);

        if isDemo() then

            self.survivalOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_demoBtn"), 1, 1, 1, 1, UIFont.Large, true);
            self.survivalOption.internal = "APOCALYPSE";
            self.survivalOption:initialise();
            self.bottomPanel:addChild(self.survivalOption);
            self.survivalOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
            labelY = labelY + labelHgt

            self.tutorialOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_tutorial"), 1, 1, 1, 1, UIFont.Large, true);
            self.tutorialOption.internal = "TUTORIAL";
            self.tutorialOption:initialise();
            self.tutorialOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
            self.bottomPanel:addChild(self.tutorialOption);
            labelY = labelY + labelHgt

        else

            local hasSaveFiles = #getFullSaveDirectoryTable() > 0

            self.latestSaveOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_continue"), 1, 1, 1, 1, UIFont.Large, true);
            self.latestSaveOption.internal = "LATESTSAVE";
            self.latestSaveOption:initialise();
            self.bottomPanel:addChild(self.latestSaveOption);
            self.latestSaveOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
            self.latestSaveOption:setVisible(false);
            if (MainScreen.latestSaveGameMode and MainScreen.latestSaveWorld) and hasSaveFiles then
                self.latestSaveOption:setVisible(true);
                labelY = labelY + labelHgt
            end

            self.loadOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_load"), 1, 1, 1, 1, UIFont.Large, true);
            self.loadOption.internal = "LOAD";
            self.loadOption:initialise();
            self.bottomPanel:addChild(self.loadOption);
            self.loadOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
            if not hasSaveFiles then
                self.loadOption:setVisible(false)
            else
                labelY = labelY + labelHgt
                labelY = labelY + labelSeparator
            end

            self.survivalOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_solo"), 1, 1, 1, 1, UIFont.Large, true);
            self.survivalOption.internal = "SOLO";
            self.survivalOption:initialise();
            self.bottomPanel:addChild(self.survivalOption);
            self.survivalOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
            labelY = labelY + labelHgt

            self.onlineOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_online2"), 1, 1, 1, 1, UIFont.Large, true);
            self.onlineOption.internal = "JOINSERVER";
            self.onlineOption:initialise();
            self.bottomPanel:addChild(self.onlineOption);
            self.onlineOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
            labelY = labelY + labelHgt

            self.onlineCoopOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_coop"), 1, 1, 1, 1, UIFont.Large, true); -- TODO: make the title translatable
            self.onlineCoopOption.internal = "COOP";
            self.onlineCoopOption:initialise();
            self.bottomPanel:addChild(self.onlineCoopOption);
            self.onlineCoopOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
            labelY = labelY + labelHgt

            labelY = labelY + labelSeparator

            self.optionsOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_option"), 1, 1, 1, 1, UIFont.Large, true);
            self.optionsOption.internal = "OPTIONS";
            self.optionsOption:initialise();
            self.bottomPanel:addChild(self.optionsOption);
            self.optionsOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
            labelY = labelY + labelHgt

            self.modsOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_mods"), 1, 1, 1, 1, UIFont.Large, true);
            self.modsOption.internal = "MODS";
            self.modsOption:initialise();
            self.bottomPanel:addChild(self.modsOption);
            self.modsOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
            labelY = labelY + labelHgt

            if WorkshopSubmitScreen.TEST or getSteamModeActive() then
                self.workshopOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_workshop"), 1, 1, 1, 1, UIFont.Large, true)
                self.workshopOption.internal = "WORKSHOP"
                self.workshopOption:initialise()
                self.workshopOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu
                self.bottomPanel:addChild(self.workshopOption)
                labelY = labelY + labelHgt
            end
        end
        self.defaultJoypadOption = self.survivalOption
    else
        self.returnOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_return"), 1, 1, 1, 1, UIFont.Large, true)
        self.returnOption.internal = "RETURN"
        self.returnOption:initialise()
        self.returnOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu
        self.bottomPanel:addChild(self.returnOption)
        labelY = labelY + labelHgt

        labelY = labelY + labelSeparator

        if isClient() then
            self.scoreOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_scoreboard"), 1, 1, 1, 1, UIFont.Large, true);
            self.scoreOption.internal = "SCOREBOARD";
            self.scoreOption:initialise();
            self.bottomPanel:addChild(self.scoreOption);
            self.scoreOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
            labelY = labelY + labelHgt

            self.adminPanel = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_adminpanel"), 1, 1, 1, 1, UIFont.Large, true);
            self.adminPanel.internal = "ADMINPANEL";
            self.adminPanel:initialise();
            self.bottomPanel:addChild(self.adminPanel);
            self.adminPanel.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
            self.adminPanel:setVisible(false);
            if canSeePlayerStats() or isCoopHost() then
                labelY = labelY + labelHgt
                self.adminPanel:setVisible(true);
            end

            if canInviteFriends() then
                self.inviteOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_invite"), 1, 1, 1, 1, UIFont.Large, true);
                self.inviteOption.internal = "INVITE";
                self.inviteOption:initialise();
                self.bottomPanel:addChild(self.inviteOption);
                self.inviteOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
                labelY = labelY + labelHgt
            end

            self.userPanel = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_userpanel"), 1, 1, 1, 1, UIFont.Large, true);
            self.userPanel.internal = "USERPANEL";
            self.userPanel:initialise();
            self.bottomPanel:addChild(self.userPanel);
            self.userPanel.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
            labelY = labelY + labelHgt

            labelY = labelY + labelSeparator
        end

        self.optionsOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_option"), 1, 1, 1, 1, UIFont.Large, true);
        self.optionsOption.internal = "OPTIONS";
        self.optionsOption:initialise();
        self.bottomPanel:addChild(self.optionsOption);
        self.optionsOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
        labelY = labelY + labelHgt

        if isClient() then
            self.defaultJoypadOption = self.scoreOption
        else
            self.defaultJoypadOption = self.optionsOption
        end
    end

    labelY = labelY + labelSeparator

    self.exitOption = ISLabel:new(labelX, labelY, labelHgt, getText("UI_mainscreen_exit"), 1, 1, 1, 1, UIFont.Large, true);
	self.exitOption.internal = "EXIT";
	self.exitOption:initialise();
	self.bottomPanel:addChild(self.exitOption);
	self.exitOption.onMouseDown = MainScreen.onMenuItemMouseDownMainMenu;
	labelY = labelY + labelHgt

    self.maxMenuItemWidth = 0
    for _,child in pairs(self.bottomPanel:getChildren()) do
        if child.Type == "ISLabel" then
            self.maxMenuItemWidth = math.max(self.maxMenuItemWidth, child:getWidth())
        end
    end
    local maxWidth = math.max(logoWidth / 2, self.maxMenuItemWidth)
    for _,child in pairs(self.bottomPanel:getChildren()) do
        if child.Type == "ISLabel" then
            child:setWidth(maxWidth)
        end
    end
    self.bottomPanel:setX(logoX + math.max(0, logoWidth - maxWidth) / 2)
--    self.bottomPanel:setX(logoX)
    self.bottomPanel:setWidth(maxWidth)
    self.bottomPanel:setHeight(labelY)

    self.versionDetail = ISButton:new(self.width - 40 - 60, self.height - FONT_HGT_SMALL - 20, 60, FONT_HGT_SMALL + 1 * 2, getText("UI_Details") , self, MainScreen.onClickVersionDetail);
    self.versionDetail:initialise();
    self.versionDetail.borderColor = {r=1, g=1, b=1, a=0.7};
    self.versionDetail.textColor =  {r=1, g=1, b=1, a=0.7};
    self:addChild(self.versionDetail);
    self.versionDetail:setAnchorLeft(false)
    self.versionDetail:setAnchorTop(false)
    self.versionDetail:setAnchorRight(true)
    self.versionDetail:setAnchorBottom(true)
    self.versionDetail.internal = "VERSIONDETAIL";

    local version = getCore():getVersionNumber();
    if getSteamModeActive() then
        version = getText("UI_mainscreen_version_steam", version);
    else
        version = getText("UI_mainscreen_version", version);
    end
	self.versionLabel = ISLabel:new(-12, 0, FONT_HGT_SMALL, version , 1, 1, 1, 0.7, UIFont.Small);
	self.versionLabel:initialise();
	self.versionDetail:addChild(self.versionLabel);

    self.mainOptions:create();

    for _,child in pairs(self.bottomPanel:getChildren()) do
        if child ~= self.controllerLabel and child ~= self.controllerLabel2 and child ~= self.abutton and child ~= self.versionLabel then
            child.fade = UITransition.new()
            child.fade:setFadeIn(false)
            child.prerender = MainScreen.prerenderBottomPanelLabel
        end
    end

    if self.inGame then
        if isClient() then
            self.scoreboard:create()
            self.inviteFriends:create();
        end
    elseif not isDemo() then
        -- Queries all the challenges...
        triggerEvent("OnChallengeQuery");

        self.charCreationMain:create();
	    self.charCreationProfession:create();
	    self.charCreationHeader:create();
		self.lastStandPlayerSelect:create();
        self.sandOptions:create();
        self.soloScreen:create();
        self.loadScreen:create();
        self.onlineCoopScreen:create();
	    self.storyScreen:create();
        self.joinServer:create();
        self.joinPublicServer:create();
        self.bootstrapConnectPopup:create();
        self.connectToServer:create();
        self.serverConnectPopup:create();
	    self.storyNewOrLoadScreen:create();
	    self.worldSelect:create();
	    self.mapSpawnSelect:create();
        self.modSelect:create();
        if self.workshopSubmit then
            self.workshopSubmit:create()
        end
        if self.serverWorkshopItem then
            self.serverWorkshopItem:create()
        end
        self.serverSettingsScreen:create()

        -- do the update news
        if getCore():getVersionNumber() ~= getCore():getSeenUpdateText() then
			self:onClickVersionDetail()
        end
    end
	--MainScreen.instance.bottomPanel:setVisible(false);

--	local teamOverview = TeamOverview:new(200, 200, 300, 200, self.desc);
--	teamOverview:initialise();
--	self:addChild(teamOverview);

--	local teampicker = TeamPicker:new(200, 200, 300, 200, "Choose members to send with "..self.desc:getSurname(), self.desc);

--	teampicker:initialise();
--	self:addChild(teampicker);
    GameWindow.doRenderEvent(true);
end

function MainScreen:OnClickNews()
    getCore():setSeenUpdateText(getCore():getVersionNumber());
    getCore():saveOptions();
end

function MainScreen:render()
  --  local textManager = getTextManager();
--    textManager:DrawString(UIFont.Small, 240+100, getCore():getOffscreenHeight()-38, "TEST RELEASE WARNING: This version of the game is for closed testing only. It may contain serious bugs or missing features. We recommend awaiting an official release.", 1, 0.4, 0.4, 1.0);

--    local fontHgt = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight()
--    self:drawTextRight(getCore():getVersionNumber(), self:getWidth() - 42, self.bottomPanel:getY() - fontHgt, 1, 1, 1, 0.7, UIFont.Small);
    if self.inGame and isClient() then
        local labelSeparator = 16
        local newY = self.scoreOption:getBottom()
        if canSeePlayerStats() or isCoopHost() then
            self.adminPanel:setVisible(true);
            newY = self.adminPanel:getBottom()
        else
            self.adminPanel:setVisible(false);
        end
        if self.inviteOption then
            self.inviteOption:setY(newY)
            newY = self.inviteOption:getBottom()
        end
        if self.userPanel then
            self.userPanel:setY(newY)
            newY = self.userPanel:getBottom()
        end
        newY = newY + labelSeparator
        self.optionsOption:setY(newY)
        newY = self.optionsOption:getBottom()

        newY = newY + labelSeparator
        self.exitOption:setY(newY)

        self.bottomPanel:setHeight(self.exitOption:getBottom())
    end
end

function MainScreen:calcLogoHeight()
    local menuHeight = self.bottomPanel:getHeight()
    local screenWidth = getCore():getScreenWidth()
    local screenHeight = getCore():getScreenHeight()
    local padding = 50 * (screenWidth / 1920)
    return screenHeight - menuHeight - padding * 3
end

function MainScreen:prerender()

	ISPanel.prerender(self);
    if(self.inGame) then
        self:drawRect(0, 0, self.width, self.height, UIManager.isFBOActive() and 0.6 or 0.5, self.backgroundColor.r, self.backgroundColor.g, self.backgroundColor.b);
    end
    if self.delay > 0 then
        if self.firstFrame then
            self.delay = self.delay - UIManager.getMillisSinceLastRender()
        else
            self.firstFrame = true
        end
    end
    local textManager = getTextManager();

    self.time = self.time + ((1.0 / 60)*getGameTime():getMultiplier());

    local lastIsSameTitle = false;
    local nextIsSameTitle = false;

    if self.time > 11.8 then
        if self.credits:size() > self.creditsIndex then
            if self.creditsIndex > 0 then
                if self.credits:get(self.creditsIndex-1).title == self.credits:get(self.creditsIndex).title then
                    lastIsSameTitle = true;
                end
            end

            if self.credits:size()-1 > self.creditsIndex then
                if self.credits:get(self.creditsIndex+1).title == self.credits:get(self.creditsIndex).title then
                    nextIsSameTitle = true;
                end
            end
        end
        local del = self.creditTime / self.creditTimeMax;
        local credAlpha = self.creditTime / self.creditTimeMax;
        if(credAlpha <= 0.5) then
            credAlpha = credAlpha * 2;
        elseif (credAlpha >= 0.8) then
            credAlpha = 1.0 - ((credAlpha - 0.8) * 5);
        else
            credAlpha = 1;
        end
        local credAlpha2 = self.creditTime / self.creditTimeMax;
        if(credAlpha2 <= 0.1) then
            credAlpha2 = credAlpha2 * 10;
            if lastIsSameTitle then credAlpha2 = 1; end
        elseif (credAlpha2 >= 0.9) then
            credAlpha2 = 1.0 - ((credAlpha2 - 0.9) * 10);
            if nextIsSameTitle then credAlpha2 = 1; end
        else
            credAlpha2 = 1;
        end

        self.creditTime = self.creditTime +  ((1.0 / 60)*getGameTime():getMultiplier());
        if self.creditTime > self.creditTimeMax then
            self.creditTime = 0;
            self.creditsIndex = self.creditsIndex + 1;
        end
        if self.credits:size() > self.creditsIndex and not self.inGame and ISDemoPopup.instance == nil then
            textManager:DrawString(UIFont.Cred1, (getCore():getScreenWidth()*0.75)+50 , getCore():getScreenHeight()*0.1, self.credits:get(self.creditsIndex).title, 1, 1, 1, credAlpha2);

            local x = (getCore():getScreenWidth()*0.75);
            local xwid = textManager:MeasureStringX(UIFont.Cred2, self.credits:get(self.creditsIndex).name);
            if(x + xwid > getCore():getScreenWidth()) then
               x = x - ((x + xwid) - getCore():getScreenWidth()) - 10;
            end
            textManager:DrawString(UIFont.Cred2, x, (getCore():getScreenHeight()*0.1) + 26, self.credits:get(self.creditsIndex).name, 1, 1, 1, credAlpha);
        end

    end

    local mainScreen = MainScreenState.getInstance();
	if mainScreen ~= nil and (ISDemoPopup.instance == nil) then
        local x = 50;
        local y = 50;
        local sw = getCore():getScreenWidth();

        local tex = self.logoTexture
        local w = tex:getWidth();
        local h = tex:getHeight();
        local resdelta = math.min(self:calcLogoHeight() / h, sw / 1920)
        x = x * (sw / 1920);
        y = y * (sw / 1920);
        w = w * resdelta;
        h = h * resdelta;
        self:drawTextureScaled(tex, x, y, w, h, 1-(self.warningFade / self.warningFadeMax), 1, 1, 1.0);
        if getDebug() and getDebugOptions():getBoolean("UI.Render.Outline") then
            self:drawRectBorder(x, y, w, h, 1, 1, 1, 1)
        end

        self.warningFade = self.warningFade - ((1.5 / 60)*getGameTime():getMultiplier());

        if self.warningFade < 0 then self.warningFade = 0; end

        local maxWidth = math.max(w / 2, self.maxMenuItemWidth)
        for _,child in pairs(self.bottomPanel:getChildren()) do
            if child.Type == "ISLabel" then
                child:setWidth(maxWidth)
            end
        end
        self.bottomPanel:setWidth(maxWidth)
        self.bottomPanel:setX(x + (w - self.bottomPanel:getWidth()) / 2)
        self.bottomPanel:setY(x + h + 50 * (sw / 1920))
	end

    if isDemo() and not self.inGame then
        if self.bottomPanel:getIsVisible() then
            if not self.demoMessagePanel then
                local y = self.bottomPanel:getY() - 35 * 3
                self.demoMessagePanel = ISRichTextPanel:new(self.width / 2 - 800 / 2, 0, 800, 35 * 3)
                self.demoMessagePanel:setAnchorTop(false)
                self.demoMessagePanel:setAnchorBottom(true)
                self.demoMessagePanel.font = UIFont.Medium
                self:addChild(self.demoMessagePanel)
                self.demoMessagePanel.text = getText("UI_Demo_Welcome")
                self.demoMessagePanel:paginate()
            end
            self.demoMessagePanel:setX(self.width / 2 - self.demoMessagePanel:getWidth() / 2)
            self.demoMessagePanel:setY(self.bottomPanel:getY() - 24 - self.demoMessagePanel:getHeight())
        end
        if self.demoMessagePanel then
            self.demoMessagePanel:setVisible(self.bottomPanel:getIsVisible())
        end
    end
end

function MainScreen:prerenderBottomPanelLabel()
    self.fade:update()
    local alpha = 0.5 * self.fade:fraction()
    if alpha > 0 then
        local padLeft = 6
        local padRight = 6
        if self.joypadFocused and self.joypadTexture then
--            padLeft = 2 + 20 + 2
        end
        self:drawRect(0 - padLeft, 0, self:getWidth() + padLeft + padRight, self:getHeight(), alpha, 0.3, 0.3, 0.3)
    end
    ISLabel.prerender(self)
end

function MainScreen:onMouseMove(dx, dy)
	ISPanelJoypad.onMouseMove(self, dx, dy)
	-- Do this here also because update() is called less frequently.
	self:updateBottomPanelButtons()
end

function MainScreen:updateBottomPanelButtons()
    local overButton = nil
    for _,child in pairs(self.bottomPanel:getChildren()) do
        if child.fade and (child:isMouseOver() or child.joypadFocused) then
            overButton = child
            break
        end
    end
    if overButton ~= self.overBottomPanelButton then
        if self.overBottomPanelButton then
            self.overBottomPanelButton.fade:setFadeIn(false)
        end
        self.overBottomPanelButton = overButton
        if self.overBottomPanelButton then
            self.overBottomPanelButton.fade:setFadeIn(true)
        end
    end
end

function MainScreen:setDefaultSandboxVars()
    getSandboxOptions():resetToDefault()
    getSandboxOptions():toLua()
end

MainScreen.checkTutorial = function(button)
    if not getCore():isTutorialDone() then
        MainScreen.instance.tutorialButton = button
        local modal = ISModalRichText:new(getCore():getScreenWidth() / 2 - 145, getCore():getScreenHeight() / 2 - 60, 290, 120, getText("UI_Tooltip_Popup"), true, nil, MainScreen.onTutorialModalClick);
        modal:initialise();
        modal:addToUIManager();
        modal:setAlwaysOnTop(true);
        if JoypadState[1] then
            JoypadState[1].focus = modal;
            updateJoypadFocus(JoypadState[1])
        end
        return false;
    end
    return true;
end

function MainScreen:onTutorialModalClick(button)
    local tutorialButton = MainScreen.instance.tutorialButton
    MainScreen.instance.tutorialButton = nil

    if JoypadState[1] then
        JoypadState[1].focus = MainScreen.instance
        updateJoypadFocus(JoypadState[1])
        if button.internal == "YES" then
            MainScreen.onTutorialControllerWarn()
            return
        end
    end

    if button.internal == "YES" then
        MainScreen.startTutorial();
    else
        getCore():setTutorialDone(true);
        getCore():saveOptions();
        MainScreen.onMenuItemMouseDownMainMenu(tutorialButton, 0, 0)
    end
end

function MainScreen.onTutorialControllerWarn()
    local modal = ISModalRichText:new(getCore():getScreenWidth() / 2 - 145, getCore():getScreenHeight() / 2 - 60, 290, 120,
        getText("UI_mainscreen_TutorialControllerWarn"), true, nil, MainScreen.onTutorialControllerWarn2)
    modal:initialise()
    modal:addToUIManager()
    modal:setAlwaysOnTop(true)
    JoypadState[1].focus = modal
    updateJoypadFocus(JoypadState[1])
end

function MainScreen:onTutorialControllerWarn2(button)
    JoypadState[1].focus = MainScreen.instance
    updateJoypadFocus(JoypadState[1])

    if button.internal == "YES" then
        MainScreen.startTutorial()
    end
end

MainScreen.startTutorial = function()
    deleteAllGameModeSaves("Tutorial");
    MainScreen.instance:setDefaultSandboxVars()
    getWorld():setGameMode("Tutorial");
    local worldName = ZombRand(100000)..ZombRand(100000)..ZombRand(100000)..ZombRand(100000);
    getWorld():setWorld(worldName);
    doTutorial(Tutorial1);
    TutorialData = {}
    TutorialData.chosenTutorial = Tutorial1;
    createWorld(worldName);
    -- menu activated via joypad, we disable the joypads and will re-set them automatically when the game is started
    if MainScreen.instance.joyfocus then
        local joypadData = MainScreen.instance.joyfocus
        joypadData.focus = nil;
        updateJoypadFocus(joypadData)
        JoypadState.count = 0
        JoypadState.players = {};
        JoypadState.joypads = {};
        JoypadState.forceActivate = joypadData.id;
    end
    GameWindow.doRenderEvent(false);
    forceChangeState(GameLoadingState.new());
end

function MainScreen.checkMapsAvailable(mapName)
	local mapNames = luautils.split(mapName, ";")
	for i=1,#mapNames do
		if not getMapInfo(mapNames[i]:trim()) then return false end
	end
	return true
end

function MainScreen.checkSaveFile()
    local saveInfo = getSaveInfo(getWorld():getWorld())
    local worldVersion = tonumber(saveInfo.worldVersion)
    local errorMsg = nil
    local lastPlayed = getLastPlayedDate(getWorld():getWorld())
    if not worldVersion or not saveInfo.mapName then
        worldVersion = worldVersion or '???'
        saveInfo.mapName = saveInfo.mapName or '???'
        errorMsg = getText("UI_mainscreen_SavefileNotFound")
    elseif not MainScreen.checkMapsAvailable(saveInfo.mapName) then
        errorMsg = getText("UI_worldscreen_MapNotFound")
    elseif worldVersion == 0 then
        errorMsg = getText("UI_worldscreen_SavefileCorrupt")
    elseif worldVersion <= 23 then
        errorMsg = getText("UI_worldscreen_SavefileOld")
    elseif worldVersion <= 115 then
        errorMsg = getText("UI_worldscreen_SavefileVehicle",worldVersion, IsoWorld.getWorldVersion());
    elseif worldVersion > IsoWorld.getWorldVersion() then
        errorMsg = getText("UI_worldscreen_SavefileNewerThanGame")
    end
    if errorMsg then
        local text = " <H1> " .. getText("UI_mainscreen_ErrorLoadingSavefile") .. " <H2> <LINE> <LINE> "
        text = text .. getText("UI_mainscreen_SavefileName", getWorld():getWorld()) .. " <LINE> <H2> "
        text = text .. lastPlayed .. " <LINE> "
        text = text .. getText("UI_Map") .. saveInfo.mapName .. " <LINE> "
        text = text .. getText("UI_WorldVersion") .. worldVersion .. " <LINE> "
        text = text .. " <TEXT> <RED> " .. errorMsg .. " <RGB:1,1,1> <LINE> "
        local modal = ISModalRichText:new(getCore():getScreenWidth() / 2 - 360 / 2,
            getCore():getScreenHeight() / 2 - 60, 360, 120, text, false,
            nil, MainScreen.onCheckSavefileModalClick)
        modal:initialise()
        modal:addToUIManager()
        modal:setAlwaysOnTop(true)
        if JoypadState[1] then
            JoypadState[1].focus = modal
            updateJoypadFocus(JoypadState[1])
        end
        MainScreen.instance.checkSavefileModal = modal
        return false
    end
    return true
end

function MainScreen.onCheckSavefileModalClick(button)
    MainScreen.instance.checkSavefileModal = nil
    if JoypadState[1] then
        JoypadState[1].focus = MainScreen.instance
        updateJoypadFocus(JoypadState[1])
    end
end

MainScreen.continueLatestSaveAux = function()
    if not MainScreen.checkSaveFile() then
        return
    end
    MainScreen.instance.bottomPanel:setVisible(false)
    -- This function is similar to LoadGameScreen.clickPlay(). Duplicate code!
	if not checkSaveFileExists("map_p.bin") then
        MainScreen.instance.createWorld = false
        if getCore():isChallenge() then
            CharacterCreationProfession.instance.doSpawnPoint = false
            MainScreen.instance.charCreationMain.previousScreen = "LoadGameScreen"
            MainScreen.instance.charCreationMain:setVisible(true, MainScreen.instance.joyfocus)
            return
        end
        local saveInfo = getSaveInfo(getWorld():getWorld())
        local map = saveInfo.mapName or "DEFAULT"
         -- Needed if map_ver.bin doesn't exist since we aren't showing WorldSelect
        getWorld():setMap(map)
        if getWorld():getGameMode() == "Sandbox" then
            -- Ensure CharacterFreeTraits is available
            getSandboxOptions():loadCurrentGameBinFile()
        end
        if MapSpawnSelect.instance:hasChoices() then
            MapSpawnSelect.instance:fillList()
            MapSpawnSelect.instance.previousScreen = "LoadGameScreen"
            MapSpawnSelect.instance:setVisible(true, MainScreen.instance.joyfocus)
        else
            MapSpawnSelect.instance:useDefaultSpawnRegion()
            MainScreen.instance.charCreationMain.previousScreen = "LoadGameScreen"
            MainScreen.instance.charCreationMain:setVisible(true, MainScreen.instance.joyfocus)
        end
	else
		-- menu activated via joypad, we disable the joypads and will re-set them automatically when the game is started
		if MainScreen.instance.joyfocus then
			local joypadData = MainScreen.instance.joyfocus
			joypadData.focus = nil
			updateJoypadFocus(joypadData)
			JoypadState.count = 0
			JoypadState.players = {}
			JoypadState.joypads = {}
			JoypadState.forceActivate = joypadData.id
		end
		GameWindow.doRenderEvent(false)
		forceChangeState(GameLoadingState.new())
	end
end

MainScreen.continueLatestSave = function(gameMode, saveName)
	if gameMode == "LastStand" then
		return -- LastStand has no savefiles
	end

    if gameMode == "Multiplayer" then
        return
    end

	if gameMode == "Beginner" then
		getWorld():setGameMode("Beginner")
		getWorld():setWorld(saveName)
		MainScreen.instance:setBeginnerPreset()
		MainScreen.continueLatestSaveAux()
		return
	end

	if gameMode == "Sandbox" then
		getWorld():setGameMode("Sandbox")
		getWorld():setWorld(saveName)
		MainScreen.instance:setDefaultSandboxVars() -- in case map_sand.bin doesn't exist, FIXME: use SandboxOptions.getDefaultPreset()?
		MainScreen.continueLatestSaveAux()
		return
	end

	if gameMode == "Survival" then
		getWorld():setGameMode("Survival")
		getWorld():setWorld(saveName)
		MainScreen.instance:setDefaultSandboxVars()
		MainScreen.continueLatestSaveAux()
		return
	end

	-- None of the above? Must be a challenge.  Ignore obsolete challenges.
	for i,challenge in ipairs(LastStandChallenge) do
		if challenge.gameMode == gameMode then
			LastStandData.chosenChallenge = challenge
			doChallenge(challenge)
			getWorld():setWorld(saveName)
			MainScreen.instance:setDefaultSandboxVars()
			MainScreen.continueLatestSaveAux()
			return
		end
    end

    getWorld():setGameMode(gameMode)
    getWorld():setWorld(saveName)

    if gameMode == "Initial Infection" then
        MainScreen.instance:setSandboxPreset(MainScreen.instance.sandOptions:getBeginnerPreset());
        MainScreen.continueLatestSaveAux();
        return;
    end

    if gameMode == "One Week Later" then
        MainScreen.instance:setSandboxPreset(MainScreen.instance.sandOptions:getNormalPreset());
        MainScreen.continueLatestSaveAux();
        return;
    end

    if gameMode == "Six Months Later" then
        MainScreen.instance:setSandboxPreset(MainScreen.instance.sandOptions:getHardPreset());
        MainScreen.continueLatestSaveAux();
        return;
    end

    if gameMode == "Survival" then
        MainScreen.instance:setSandboxPreset(MainScreen.instance.sandOptions:getDefaultPreset());
        MainScreen.continueLatestSaveAux();
        return;
    end

    getWorld():setGameMode(gameMode)
    getWorld():setWorld(saveName)
    MainScreen.instance:setDefaultSandboxVars()
    MainScreen.continueLatestSaveAux()
end

MainScreen.onMenuItemMouseDownMainMenu = function(item, x, y)

    if DebugScenarios.instance ~= nil then
        DebugScenarios.instance:setVisible(false);
    end

    if MainScreen.instance.delay > 0 then return; end
    if MainScreen.instance.tutorialButton then return end
    if MainScreen.instance.checkSavefileModal then return end

    if (item.internal == "JOINSERVER" or item.internal == "CHALLENGE" or item.internal == "SANDBOX" or item.internal == "BEGINNER" or item.internal == "APOCALYPSE" or item.internal == "SOLO") and not MainScreen.checkTutorial(item) then
        return;
    end

    if item.internal == "LATESTSAVE" then
        MainScreen.continueLatestSave(MainScreen.latestSaveGameMode, MainScreen.latestSaveWorld)
        return
    end

    if item.internal == "EXIT" then
--        if MainScreen.instance.inGame then
--            saveGame();
--            MainScreen.instance:getLatestSave();
--        end
        setGameSpeed(1);
        pauseSoundAndMusic();
        setShowPausedMessage(true);
        getCore():quit();
    end
    if item.internal == "RETURN" then
        ToggleEscapeMenu(getCore():getKey("Main Menu"))
        return
    end
    if item.internal == "JOINSERVER" then
        MainScreen.instance.bottomPanel:setVisible(false);
        MainScreen.instance.joinServer:pingServers(true);
        MainScreen.instance.joinServer:setVisible(true, MainScreen.instance.joyfocus);
    end
    if item.internal == "COOP" then
        MainScreen.instance.bottomPanel:setVisible(false);
        MainScreen.instance.onlineCoopScreen:aboutToShow()
        MainScreen.instance.onlineCoopScreen:setVisible(true, MainScreen.instance.joyfocus);
    end
    if item.internal == "SCOREBOARD" then
        MainScreen.instance.scoreboard:setVisible(true, MainScreen.instance.joyfocus);
        scoreboardUpdate()
    end

    if item.internal == "OPTIONS" then
        MainScreen.instance.mainOptions:toUI()
        MainScreen.instance.mainOptions:setVisible(true, MainScreen.instance.joyfocus);
    end
    if item.internal == "SOLO" then
        MainScreen.instance.soloScreen:setVisible(true, MainScreen.instance.joyfocus);
        MainScreen.instance.soloScreen.onMenuItemMouseDown(MainScreen.instance.soloScreen.tutorial,0,0);
    end
    if item.internal == "LOAD" then
        MainScreen.instance.loadScreen:setSaveGamesList();
        MainScreen.instance.loadScreen:setVisible(true, MainScreen.instance.joyfocus);
    end
    if item.internal == "MODS" then
        MainScreen.instance.modSelect:setVisible(true, MainScreen.instance.joyfocus);
        MainScreen.instance.modSelect:populateListBox(getModDirectoryTable());
    end
    if item.internal == "ADMINPANEL" then
        if ISAdminPanelUI.instance then
            ISAdminPanelUI.instance:close()
        end
        local modal = ISAdminPanelUI:new(200, 200, 350, 400)
        modal:initialise();
        modal:addToUIManager();
        ToggleEscapeMenu(getCore():getKey("Main Menu"))
        return
    end
    if item.internal == "USERPANEL" then
        if ISUserPanelUI.instance then
            ISUserPanelUI.instance:close()
        end
        local modal = ISUserPanelUI:new(200, 200, 350, 250, getPlayer())
        modal:initialise();
        modal:addToUIManager();
        ToggleEscapeMenu(getCore():getKey("Main Menu"))
        return
    end
    if item.internal == "TUTORIAL" then
        if JoypadState[1] then
            MainScreen.onTutorialControllerWarn()
            return
        end
        MainScreen.startTutorial();
    end
    if item.internal == "APOCALYPSE" then
        MainScreen.instance:setDefaultSandboxVars()
            getWorld():setGameMode("Sandbox");
            getWorld():setMap("DEFAULT")
            getWorld():setWorld("demo");
            deleteSave("Sandbox/demo");
            createWorld("demo");
            -- menu activated via joypad, we disable the joypads and will re-set them automatically when the game is started
            if MainScreen.instance.joyfocus then
                local joypadData = MainScreen.instance.joyfocus
                joypadData.focus = nil;
                updateJoypadFocus(joypadData)
                JoypadState.count = 0
                JoypadState.players = {};
                JoypadState.joypads = {};
                JoypadState.forceActivate = joypadData.id;
            end
            GameWindow.doRenderEvent(false);
            forceChangeState(GameLoadingState.new());
    end

    if item.internal == "INVITE" then
        InviteFriends.instance:fillList();
--        InviteFriends.instance:setVisible(true);
        MainScreen.instance.inviteFriends:setVisible(true, MainScreen.instance.joyfocus);
    end

    if item.internal == "WORKSHOP" then
        MainScreen.instance.workshopSubmit:fillList()
        MainScreen.instance.workshopSubmit:setVisible(true, MainScreen.instance.joyfocus)
    end

    MainScreen.instance.bottomPanel:setVisible(false);
end

function MainScreen:onClickVersionDetail()
    if not self.infoRichText then
        self.infoRichText = ISNewsUpdate:new(0,0,getCore():getScreenWidth() - 300,getCore():getScreenHeight()-150, false, self, MainScreen.OnClickNews);
        self.infoRichText:setX(getCore():getScreenWidth() / 2 - self.infoRichText.width/2);
        self.infoRichText:setY(getCore():getScreenHeight() / 2 - self.infoRichText.height/2);
        self.infoRichText:initialise();
        self.infoRichText:addToUIManager();
        self.infoRichText.backgroundColor = {r=0, g=0, b=0, a=0.9};
        self.infoRichText.alwaysOnTop = true;
        self.infoRichText:setVisible(true);
        self.infoRichText:bringToTop();
    else
        self.infoRichText:setVisible(not self.infoRichText:isVisible());
        self.infoRichText:bringToTop();
    end
end

function MainScreen:presentServerConnectPopup ()
    MainScreen.instance.bottomPanel:setVisible(false);
    MainScreen.instance.serverConnectPopup:setVisible(true);
end

function MainScreen:update ()

    if MainScreen.instance == nil then MainScreen.instance = MainScreenInstance; end

--	local w = getCore():getScreenWidth();
--	local h = getCore():getScreenHeight();

--	MainScreen.instance:setWidth(w);
--	MainScreen.instance:setHeight(h);

    self:updateBottomPanelButtons()
end

function MainScreen:setSandboxPreset(preset)
    getSandboxOptions():copyValuesFrom(preset.options)
    local waterShut = getSandboxOptions():getOptionByName("WaterShut"):getValue();
    local elecShut = getSandboxOptions():getOptionByName("ElecShut"):getValue();
    if getSandboxOptions():getOptionByName("WaterShutModifier"):getValue() == getSandboxOptions():getOptionByName("WaterShutModifier"):getDefaultValue() then
        getSandboxOptions():set("WaterShutModifier", getSandboxOptions():randomWaterShut(waterShut))
    end
    if getSandboxOptions():getOptionByName("ElecShutModifier"):getValue() == getSandboxOptions():getOptionByName("ElecShutModifier"):getDefaultValue() then
        getSandboxOptions():set("ElecShutModifier", getSandboxOptions():randomElectricityShut(elecShut))
    end
    getSandboxOptions():toLua()
end

function MainScreen:setBeginnerPreset()
    local beginnerPreset = MainScreen.instance.sandOptions:getBeginnerPreset()
    self:setSandboxPreset(beginnerPreset);
end

function MainScreen:setEasyPreset()
    SandboxVars.XpMultiplier = "2.0";
    SandboxVars.StatsDecrease = 5;
    SandboxVars.EndRegen = 5;
    SandboxVars.InjurySeverity = 1;
    SandboxVars.CarGasConsumption = 0.7;
--    SandboxVars.Nutrition = false;
end

function MainScreen:setNormalPreset()
    SandboxVars.XpMultiplier = "1.5";
    SandboxVars.StatsDecrease = 4;
    SandboxVars.EndRegen = 4;
--    SandboxVars.Nutrition = false;
end

function MainScreen:setHardPreset()
    SandboxVars.XpMultiplier = "1.2";
    SandboxVars.InjurySeverity = 3;
    SandboxVars.CarGasConsumption = 1.2;
end

function MainScreen:setHardcorePreset()
    SandboxVars.XpMultiplier = 1.0;
    SandboxVars.InjurySeverity = 3;
    SandboxVars.CarGasConsumption = 1.2;
end

function MainScreen:onEnterFromGame()
	GameSounds.fix3DListenerPosition(true)
	for _,child in pairs(self:getChildren()) do
		if child.onEnterFromGame then
			child:onEnterFromGame()
		end
	end
end

function MainScreen:onReturnToGame()
	GameSounds.fix3DListenerPosition(false)
	for _,child in pairs(self:getChildren()) do
		if child.onReturnToGame then
			child:onReturnToGame()
		end
	end
end

function  MainScreen:addCredit(title, name)
    self.credits:add({title = title, name = name});
end

function MainScreen:doArtCredits()
    self:addCredit("ART", "Andy Hodgetts");
    self:addCredit("ART", "Marina Siu Chong");
end
function MainScreen:doCodeCredits()
    self:addCredit("CODE", "Chris Simpson");
    self:addCredit("CODE", "Romain Dron");
    self:addCredit("CODE", "Tim Baker");
    self:addCredit("CODE", "Kees Bekkema");
end
function MainScreen:doWritingCredits()
    self:addCredit("WRITTEN BY", "Will Porter");
end
function MainScreen:doScriptingCredits()
    self:addCredit("SCRIPTING", "Paul Ring");
end
function MainScreen:doCredits()
    local r = ZombRand(5);
    self:addCredit("MUSIC", "Zach Beever");
    if r == 0 then
        self:doArtCredits();
        self:doCodeCredits();
        self:doWritingCredits();
        self:doScriptingCredits();
    end
    if r == 1 then
        self:doCodeCredits();
        self:doArtCredits();
        self:doWritingCredits();
        self:doScriptingCredits();
    end
    if r == 2 then
        self:doWritingCredits();
        self:doScriptingCredits();
        self:doArtCredits();
        self:doCodeCredits();
    end
    if r == 3 then
        self:doWritingCredits();
        self:doScriptingCredits();
        self:doCodeCredits();
        self:doArtCredits();
    end
    if r == 4 then
        self:doCodeCredits();
        self:doWritingCredits();
        self:doScriptingCredits();
        self:doArtCredits();
    end
    
    
    self:addCredit("DESIGN", "The Team");
    
    self:addCredit("TOOLS", "Audio engine : FMOD Studio by Firelight Technologies");
    self:addCredit("TOOLS", "Tim Baker");
    self:addCredit("TOOLS", "Andy Hodgetts");
    
    self:addCredit("CAR MODELS", "Martin Greenall");
    
    self:addCredit("ADDITIONAL CODE", "General Arcade");
    self:addCredit("ADDITIONAL CODE", "BitBaboon");
    self:addCredit("ADDITIONAL CODE", "Connall 'Connall' Lindsay");
    self:addCredit("ADDITIONAL CODE", "Chris Wood");
    self:addCredit("ADDITIONAL CODE", "Caspian Prince");
    self:addCredit("ADDITIONAL CODE", "Nick Cowen");
    
    self:addCredit("CONTRIBUTOR", "Kieran 'Stormy' Rafferty");
    self:addCredit("CONTRIBUTOR", "Christian 'Thuztor' Walber");
    self:addCredit("CONTRIBUTOR", "Adric 'AdricTheGreat' Hartin");
    self:addCredit("CONTRIBUTOR", "Benjamin 'blindCoder' Schieder");
    
    
    self:addCredit("SYS ADMIN", "Johnathon Tinsley");
    self:addCredit("OPERATIONS MANAGER", "Jake 'EnigmaGrey' Snow");
    self:addCredit("LEAD TESTER", "Sasha Mihailova");
    self:addCredit("COMMUNITY MANAGER", "nasKo");
    self:addCredit("SENIOR TECH SUPPORT", "Drake 'Rathlord' Barron");
    self:addCredit("TECH SUPPORT", "Richard 'Beard' Caban");
    self:addCredit("ONLINE MAP COORDINATOR", "Benjamin 'blindCoder' Schieder");
    self:addCredit("WEBSITE WIZARD", "Connall 'Connall' Lindsay");
    self:addCredit("HEAD RACCOON", "Spiffo");
    
    self:addCredit("VIDEOS", "Josh Henning");
    
    self:addCredit("SOUNDS", "NJ Apostol");
    self:addCredit("LADY ZOMBIE SCREAMS", "Elspeth Edmonds");
    
    self:addCredit("DEDICATED TO THE MEMORY OF...", "Romuald Dron");
    
    self:addCredit("SPECIAL INFECTED", "Luke 'Onto' Bosdet");
    self:addCredit("SPECIAL INFECTED", "Paul 'mendonca' Wright.");
    self:addCredit("SPECIAL INFECTED", "Peter 'rdsqc22' LoVerso");
    self:addCredit("SPECIAL INFECTED", "Dr_Cox1911");
    self:addCredit("SPECIAL INFECTED", "Mark.exe");
    self:addCredit("SPECIAL INFECTED", "Rekkie");
    self:addCredit("SPECIAL INFECTED", "Clément 'deprav' Giraudeau");
    self:addCredit("SPECIAL INFECTED", "François 'FlashFire' Desforges");
    self:addCredit("SPECIAL INFECTED", "Edward 'Zargo1z' Smallwood");
    self:addCredit("SPECIAL INFECTED", "Rasmus 'LennyLeak' Nielsen");
    self:addCredit("SPECIAL INFECTED", "RoboMat");
    self:addCredit("SPECIAL INFECTED", "MiddleAgedBob");
    self:addCredit("SPECIAL INFECTED", "Charlie 'ToastedFishSandwich' Sloan");
    self:addCredit("SPECIAL INFECTED", "Bear 'Markofbear' Granander");
    self:addCredit("SPECIAL INFECTED", "titopei");
    self:addCredit("SPECIAL INFECTED", "Jacob 'Freeplayo7' Haley");
    self:addCredit("SPECIAL INFECTED", "Antti 'harakka' Riikonen");
    self:addCredit("SPECIAL INFECTED", "Marcus 'dThunder' Hill");
    self:addCredit("SPECIAL INFECTED", "Christopher 'Augur' Green");
    self:addCredit("SPECIAL INFECTED", "Louis 'Keepbro' Keep");
    self:addCredit("SPECIAL INFECTED", "Steve 'syefye' Young");
    self:addCredit("SPECIAL INFECTED", "Paul 'Dr Gonzo' McAloon");
    self:addCredit("SPECIAL INFECTED", "Connall 'Connall' Lindsay");
    self:addCredit("SPECIAL INFECTED", "Leo Ivanov");
    self:addCredit("SPECIAL INFECTED", "Mark 'EreWeGo' Sanders");
    self:addCredit("SPECIAL INFECTED", "Bill Redpath");
    self:addCredit("SPECIAL INFECTED", "Marc 'The Pleasure' Tookey");
    self:addCredit("SPECIAL INFECTED", "Henry Patterson");
    self:addCredit("SPECIAL INFECTED", "Anonymous");
    
    
    self:addCredit("SPECIAL THANKS TO", "The PZ Community");
    self:addCredit("SPECIAL THANKS TO", "Mathas");
    self:addCredit("SPECIAL THANKS TO", "Jas Purewal");
    self:addCredit("SPECIAL THANKS TO", "Graeme Struthers");
    self:addCredit("SPECIAL THANKS TO", "Dean Trotman");
    self:addCredit("SPECIAL THANKS TO", "Matt Schillaci");
    self:addCredit("SPECIAL THANKS TO", "BobHeckling");
    self:addCredit("SPECIAL THANKS TO", "Paul Soares Jr");
    self:addCredit("SPECIAL THANKS TO", "MayaTutors");
    self:addCredit("SPECIAL THANKS TO", "Eckyman");
    self:addCredit("SPECIAL THANKS TO", "Twiggy");
    self:addCredit("SPECIAL THANKS TO", "Robbaz");
    self:addCredit("SPECIAL THANKS TO", "SaudiGamer");
    self:addCredit("SPECIAL THANKS TO", "Kiwon");
    self:addCredit("SPECIAL THANKS TO", "Phill Cameron");
    self:addCredit("SPECIAL THANKS TO", "Emily Richardson");
    self:addCredit("SPECIAL THANKS TO", "Tom Porter");
    self:addCredit("SPECIAL THANKS TO", "Dean 'Rocket' Hall");
    self:addCredit("SPECIAL THANKS TO", "Shannon Z Killer");
    self:addCredit("SPECIAL THANKS TO", "Sacriel");
    self:addCredit("SPECIAL THANKS TO", "Notch");
    self:addCredit("SPECIAL THANKS TO", "Brian Hicks");
    self:addCredit("SPECIAL THANKS TO", "Scott Reismanis");
    self:addCredit("SPECIAL THANKS TO", "Chet Faliszek");
    self:addCredit("SPECIAL THANKS TO", "Tim Buckley");
    self:addCredit("SPECIAL THANKS TO", "Ashton Raze");
    self:addCredit("SPECIAL THANKS TO", "Jaime Byrne");
    self:addCredit("SPECIAL THANKS TO", "Angela Bateman");
    self:addCredit("SPECIAL THANKS TO", "Cally");
    self:addCredit("SPECIAL THANKS TO", "Chief");
    self:addCredit("SPECIAL THANKS TO", "Alan Carter");
end

function MainScreen:new (inGame)
	-- using a virtual 100 height res for doing the UI, so it resizes properly on different rez's.
	MainScreen.StaticHeight = 100;
	MainScreen.StaticWidth = MainScreen.StaticHeight * 1.7777777;
	local o = {}

	--o.data = {}
	o = ISPanelJoypad:new(0, 0, MainScreen.StaticWidth, MainScreen.StaticHeight);
	setmetatable(o, self)
	self.__index = self
	o.x = 0;
	o.y = 0;
	o.backgroundColor = {r=0, g=0, b=0, a=0.0};
	o.borderColor = {r=1, g=1, b=1, a=0.0};

	o.anchorLeft = true;
	o.anchorRight = false;
	o.anchorTop = true;
	o.anchorBottom = false;
    o.warningFadeMax = 10;
    o.warningFade = o.warningFadeMax;
    o.credits = LuaList:new();
    o.creditsIndex = 0;
    o.delay = inGame and -1 or 500 -- milliseconds
    o.firstFrame = false
    o.creditTimeMax = 7;
    o.creditTime = 0;
    o:doCredits();
    o.time = 0;
    o.inGame =inGame;

    useTextureFiltering(true)
    o.logoTexture = getTexture("media/ui/PZ_Logo_New.png")
    useTextureFiltering(false)
 
    MainScreen.instance = o;
    MainScreenInstance = o;
    return o
end

MainScreen.OnTick = function (totalTicks)
	if MainScreen.instance ~= nil then
		MainScreen.instance:update();
	end
end

LoadMainScreenPanel = function ()
    LoadMainScreenPanelInt(false);
end

function isPlayerDoingActionThatCanBeCancelled(playerObj)
	if not playerObj then return false end
	if playerObj:isDead() then return false end
	if not playerObj:getCharacterActions():isEmpty() then return true end
	local state = playerObj:getCurrentState()
	if instanceof(state, "OpenWindowState") then return true end
	return false
end

local function StartPressCancelActionKey(key)
	if key ~= getCore():getKey("CancelAction") then return end
	if not MainScreen.instance or not MainScreen.instance.inGame then return end
	if MainScreen.instance:getIsVisible() then return end
	if getCell() and getCell():getDrag(0) then return end
	local playerObj = getSpecificPlayer(0)
	if isPlayerDoingActionThatCanBeCancelled(playerObj) then
		playerObj:StopAllActionQueue()
		GameKeyboard.eatKeyPress(getCore():getKey("CancelAction"))
	end
end

ToggleEscapeMenu = function (key)
	local mainMenuKey = getCore():getKey("Main Menu")
	if (key == mainMenuKey) or (mainMenuKey == 0 and key == Keyboard.KEY_ESCAPE) then
		if getCell() and getCell():getDrag(0) then -- if we press escape we first try to remove the dragged item (carpentry for exemple)
			getCell():setDrag(nil, 0);
		elseif MainScreen.instance ~= nil and MainScreen.instance.inGame == true then
			if MainOptions.instance and MainOptions.instance.modal then return end -- confirm-changes dialog
			ISUIHandler.setVisibleAllUI(MainScreen.instance:getIsVisible())

			MainScreen.instance:setVisible(not MainScreen.instance:getIsVisible());

			if MainScreen.instance:getIsVisible() then
				MainScreen.instance:addToUIManager()
				setGameSpeed(0);
				setShowPausedMessage(false);
				JoypadState.saveAllFocus()
				if JoypadState[1] then
					JoypadState[1].focus = MainScreen.instance
				end
				MainScreen.instance:onEnterFromGame()
			else
				MainScreen.instance:removeFromUIManager()
				MainScreen.instance:onReturnToGame()
				setGameSpeed(1);
				setShowPausedMessage(true);
				JoypadState.restoreAllFocus()
			end
		end
   end
end

LoadMainScreenPanelIngame = function ()

    LoadMainScreenPanelInt(true);

end

LoadMainScreenPanelInt = function (ingame)

    local panel2 = MainScreen:new(ingame);
    panel2:initialise();
    panel2:addToUIManager();
    if ingame then
        panel2:setVisible(false);
    end
    if not ingame and JoypadState[1] ~= nil then
        JoypadState[1].focus = MainScreen.instance;
        updateJoypadFocus(JoypadState[1]);
    end
    if not ingame and not isDemo() then
        local argsServer = getServerAddressFromArgs();
        if argsServer then
			local ss = argsServer:split(":")
			if #ss == 2 then
				local ip = ss[1]
				local port = ss[2]
				MainScreen.instance.bottomPanel:setVisible(false)
				MainScreen.instance.bootstrapConnectPopup:connect(ip, port, getServerPasswordFromArgs())
			end
        end
    end
--CharacterInfoPage.doInfo(SurvivorFactory:CreateSurvivor());


    if getDebug() and not ingame then
        doDebugScenarios();
    end

end



MainScreenPanelJoinSteam = function ()
	if isIngameState() then
		local player = 0
		local modal = ISModalDialog:new(0,0, 250, 150, getText("IGUI_ConfirmLeaveGame"), true, nil, MainScreenPanelJoinSteam_onConfirmLeaveGame, player, player, bed);
		modal:initialise()
		modal:addToUIManager()
		if JoypadState.players[player+1] then
			setJoypadFocus(player, modal)
		end
	else 
		local argsServer = getServerAddressFromArgs();
		if argsServer then
			local ss = argsServer:split(":")
			if #ss == 2 then
				local ip = ss[1]
				local port = ss[2]
				MainScreen.instance.bottomPanel:setVisible(false)
				MainScreen.instance.bootstrapConnectPopup:connect(ip, port, getServerPasswordFromArgs())
			end
		end
	end
end

function MainScreenPanelJoinSteam_onConfirmLeaveGame(this, button, player, bed)
    if button.internal == "YES" then
        setGameSpeed(1);
        pauseSoundAndMusic();
        setShowPausedMessage(true);
        getCore():quit();
    end
end

LoadChatPanel = function ()

	local panel2 = nil;

	if ChatContainer.instance ~= nil then
		panel2 = ChatContainer.instance;
	else
		panel2 = ChatContainer:new(-516, 0, 516, 720);
		panel2:initialise();
		panel2:setVisible(false);
		panel2:setAnchorBottom(true);
	end

	panel2:addToUIManager();


--	CharacterInfoPage.doInfo(SurvivorFactory:CreateSurvivor());
end

function MainScreen:onGainJoypadFocus(joypadData)
    ISPanelJoypad.onGainJoypadFocus(self, joypadData);
	local uis = {
		{ self.latestSaveOption },
		{ self.loadOption },
		{ self.survivalOption },
		{ self.onlineOption },
		{ self.onlineCoopOption },
		{ self.returnOption },
		{ self.tutorialOption },
		{ self.scoreOption },
		{ self.adminPanel },
		{ self.inviteOption },
		{ self.userPanel },
		{ self.optionsOption },
		{ self.modsOption },
		{ self.exitOption },
	}
	for _,ui in ipairs(uis) do
		if ui[1] then
			ui[1]:setJoypadFocused(false)
		end
	end
    if #self.joypadButtonsY == 0 then
		for _,ui in ipairs(uis) do
			if ui[1] and ui[1]:getIsVisible() then
				table.insert(self.joypadButtonsY, { ui[1] })
			end
		end
	end
    if self.controllerLabel then
        self.controllerLabel:setVisible(false);
        self.abutton:setVisible(false);
        self.controllerLabel2:setVisible(false);
    end
    if self.defaultJoypadOption then
        for k,v in ipairs(self.joypadButtonsY) do
            if v[1] == self.defaultJoypadOption then
                self.joypadIndexY = k
                self.joypadIndex = 1
                self.joypadButtons = self.joypadButtonsY[k]
                self.defaultJoypadOption:setJoypadFocused(true)
                break
            end
        end
    end
end

function MainScreen:onLoseJoypadFocus(joypadData)
	self:clearJoypadFocus(joypadData)
	ISPanelJoypad.onLoseJoypadFocus(self, joypadData)
end

function MainScreen:onJoypadDown(button)
    if button == Joypad.AButton and self.joypadButtonsY[self.joypadIndex] then
        MainScreen.onMenuItemMouseDownMainMenu(self.joypadButtons[self.joypadIndex], 0,0);
    end
end

function MainScreen:onJoypadDirUp(joypadData)
	ISPanelJoypad.onJoypadDirUp(self, joypadData)
	self:updateBottomPanelButtons()
end

function MainScreen:onJoypadDirDown(joypadData)
	ISPanelJoypad.onJoypadDirDown(self, joypadData)
	self:updateBottomPanelButtons()
end

function MainScreen.onResetLua(reason)
	if reason == "joinServer" then
		MainScreen.instance.bottomPanel:setVisible(false);
		MainScreen.instance.joinServer:pingServers(true)
		MainScreen.instance.joinServer:setVisible(true)
	end
end

function MainScreen.onResolutionChange(oldw, oldh, neww, newh)
	print('MainScreen.onResolutionChange '..oldw..'x'..oldh..' -> '..neww..'x'..newh)
	if not MainScreen.instance then return end
	local self = MainScreen.instance
	self:setWidth(neww)
	self:setHeight(newh)
	self:recalcSize()

	local uis = {
		{ self.scoreboard, 0.6, 0.7 },
		{ self.joinServer, 0.7, 0.8 },
        { self.joinPublicServer, 0.7, 0.8 },
        { self.onlineCoopScreen, 0.7, 0.8 },
        { self.soloScreen, 0.7, 0.8 },
        { self.loadScreen, 0.7, 0.8 },
		{ self.sandOptions, 0.8, 0.8 },
		{ self.worldSelect, 0.7, 0.8 },
		{ self.mapSpawnSelect, 0.7, 0.8 },
        { self.inviteFriends, 0.7, 0.8 },
        { self.storyScreen, 0.7, 0.8 },
		{ self.modSelect, 0.7, 0.8 },
		{ self.lastStandPlayerSelect, 0.9, 0.9 },
		{ self.mainOptions, 0.7, 0.8 },
		{ self.workshopSubmit, 0.9, 0.9 },
		{ self.serverWorkshopItem, 0.9, 0.9 },
		{ self.serverSettingsScreen, 0.9, 0.9 },
	}

	for _,ui in ipairs(uis) do
		if ui[1] and ui[1].javaObject and instanceof(ui[1].javaObject, 'UIElement') then
			local width = neww * ui[2]
			local height = newh * ui[3]
			if neww <= 1024 then
				width = neww * 0.95
				height = newh * 0.95
			end
			ui[1]:setWidth(width)
			ui[1]:setHeight(height)
			ui[1]:setX((neww - width) / 2)
			ui[1]:setY((newh - height) / 2)
			ui[1]:recalcSize()
		end
	end

	if self.mainOptions then
		self.mainOptions:onResolutionChange(oldw, oldh, neww, newh)
	end
	if self.charCreationMain then
		self.charCreationMain:onResolutionChange(oldw, oldh, neww, newh)
	end
	if self.charCreationProfession then
		self.charCreationProfession:onResolutionChange(oldw, oldh, neww, newh)
	end
	if self.worldSelect then
		self.worldSelect:onResolutionChange(oldw, oldh, neww, newh)
	end
	if self.modSelect then
		self.modSelect:onResolutionChange(oldw, oldh, neww, newh)
	end
	if self.sandOptions then
		self.sandOptions:onResolutionChange(oldw, oldh, neww, newh)
	end
	if self.soloScreen then
		self.soloScreen:onResolutionChange(oldw, oldh, neww, newh)
    end
    if self.loadScreen then
        self.loadScreen:onResolutionChange(oldw, oldh, neww, newh)
    end
	if self.serverSettingsScreen then
		self.serverSettingsScreen:onResolutionChange(oldw, oldh, neww, newh)
	end
end

function MainScreen:showInviteFailDialog(message)
	local w,h = 350,120
	local modal = ISModalDialog:new((getCore():getScreenWidth() / 2) - w / 2,
		(getCore():getScreenHeight() / 2) - h / 2, w, h,
		getText(message), false, self, self.onInviteFailDialogButton);
	modal:initialise()
	modal:setCapture(true)
	modal:setAlwaysOnTop(true)
	modal:addToUIManager()
	if JoypadState[1] then
		modal.param1 = JoypadState[1].focus
		JoypadState[1].focus = modal
		updateJoypadFocus(JoypadState[1])
	end
end

function MainScreen:onInviteFailDialogButton(button, focus)
	if JoypadState[1] then
		JoypadState[1].focus = focus
		updateJoypadFocus(JoypadState[1])
	end
end

function MainScreen.onAcceptInvite(connectionString)
	if MainScreen.instance.inGame then
		MainScreen.instance:showInviteFailDialog(getText("UI_mainscreen_InviteInGame"))
		return
	end
	if not MainScreen.instance.bottomPanel:getIsVisible() then
		MainScreen.instance:showInviteFailDialog(getText("UI_mainscreen_InviteMainMenu"))
		return
	end
	local ss = string.gsub(connectionString:gsub('"', ""), "+connect ", ""):split(":")
	if #ss == 2 then
		local ip = ss[1]
		local port = ss[2]
		MainScreen.instance.bottomPanel:setVisible(false)
		BootstrapConnectPopup.instance:connect(ip, port, "")
	else
        MainScreen.instance:showInviteFailDialog(getText("UI_mainscreen_InviteFormat", connectionString))
    end
end

Events.OnResolutionChange.Add(MainScreen.onResolutionChange)

Events.OnMainMenuEnter.Add(LoadMainScreenPanel);
--Events.OnMainMenuEnter.Add(LoadChatPanel);

Events.OnGameStart.Add(LoadMainScreenPanelIngame);

--Events.OnTick.Add(MainScreen.OnTick);
--Events.OnGameStart.Add(LoadChatPanel);
Events.OnKeyPressed.Add(ToggleEscapeMenu);
Events.OnKeyStartPressed.Add(StartPressCancelActionKey);

Events.OnResetLua.Add(MainScreen.onResetLua);

Events.OnAcceptInvite.Add(MainScreen.onAcceptInvite);

Events.OnSteamGameJoin.Add(MainScreenPanelJoinSteam);

