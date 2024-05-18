--***********************************************************--**                    ROBERT JOHNSON                     **--***********************************************************require "ISUI/ISPanel"require "ISUI/ISButton"require "ISUI/ISInventoryPane"require "ISUI/ISResizeWidget"require "ISUI/ISMouseDrag"require "defines"StoryScreen = ISPanel:derive("StoryScreen");function StoryScreen:initialise()	ISPanel.initialise(self);end--************************************************************************----** StoryScreen:instantiate--**--************************************************************************--function StoryScreen:instantiate()	self.javaObject = UIElement.new(self);	self.javaObject:setX(self.x);	self.javaObject:setY(self.y);	self.javaObject:setHeight(self.height);	self.javaObject:setWidth(self.width);	self.javaObject:setAnchorLeft(self.anchorLeft);	self.javaObject:setAnchorRight(self.anchorRight);	self.javaObject:setAnchorTop(self.anchorTop);	self.javaObject:setAnchorBottom(self.anchorBottom);endfunction StoryScreen:populateListBox(list)	StoryScreen.instance.listbox:clear();	for i, k in ipairs(list) do		StoryScreen.instance.listbox:addItem(k, getStoryInfo(k));	endendfunction StoryScreen:create()	self.mode = "NEW";	self.listbox = ISScrollingListBox:new(16, 38, self.width-32, self.height-66-6);	self.listbox:initialise();	self.listbox:instantiate();	self.listbox:setAnchorLeft(true);	self.listbox:setAnchorRight(true);	self.listbox:setAnchorTop(true);	self.listbox:setAnchorBottom(true);	self.listbox.itemheight = 128+32+32+32;	self.listbox.doDrawItem = StoryScreen.drawMap;	self:addChild(self.listbox);	self.backButton = ISButton:new(16, self.height-30, 100, 25, "BACK", self, StoryScreen.onOptionMouseDown);	self.backButton.internal = "BACK";	self.backButton:initialise();	self.backButton:instantiate();	self.backButton:setAnchorLeft(true);	self.backButton:setAnchorTop(false);	self.backButton:setAnchorBottom(true);	self.backButton.borderColor = {r=1, g=1, b=1, a=0.1};	self.backButton:setFont(UIFont.Small);	self.backButton:ignoreWidthChange();	self.backButton:ignoreHeightChange();	self:addChild(self.backButton);	self.playButton = ISButton:new(self.width - 126, self.height-30, 100, 25, "PLAY", self, StoryScreen.onOptionMouseDown);	self.playButton.internal = "PLAY";	self.playButton:initialise();	self.playButton:instantiate();	self.playButton:setAnchorLeft(false);	self.playButton:setAnchorRight(true);	self.playButton:setAnchorTop(false);	self.playButton:setAnchorBottom(true);	self.playButton.borderColor = {r=1, g=1, b=1, a=0.1};	self.playButton:setFont(UIFont.Small);	self.playButton:ignoreWidthChange();	self.playButton:ignoreHeightChange();	self:addChild(self.playButton);	self.richTextReusablePanel = ISRichTextPanel:new(0, 0, 100, 100);	self.richTextReusablePanel:initialise();	self.richTextReusablePanel:instantiate();	self.richTextReusablePanel.backgroundColor = {r=0, g=0, b=0, a=0.1};	self.richTextReusablePanel.borderColor = {r=1, g=1, b=1, a=0.0};endfunction StoryScreen:drawMap(y, item, alt)	local tex = nil;	-- if we've selected something, we gonna display the poster on the right	if self.selected == item.index then		self:drawRect(0, (y), self:getWidth() / 2, self.itemheight-1, 0.3, 0.7, 0.35, 0.15);		tex = item.item:getTexture();		self:drawTextureScaled(tex, self:getWidth() / 2 + 10, 0, self:getWidth() / 2 - 10, self:getHeight(), 1, 1, 1, 1);	end	-- border over text and description	self:drawRectBorder(0, (y), self:getWidth() / 2, self.itemheight-1, 0.5, self.borderColor.r, self.borderColor.g, self.borderColor.b);	-- the name of the story	self:drawText(item.item:getName(), 16, (y)+15, 0.9, 0.9, 0.9, 0.9, UIFont.Large);	-- the description of our story	StoryScreen.instance.richTextReusablePanel.text = item.item:getDescription();	StoryScreen.instance.richTextReusablePanel:setWidth(self:getWidth() / 2 - 32);	StoryScreen.instance.richTextReusablePanel:setX(self:getAbsoluteX() + 16);	StoryScreen.instance.richTextReusablePanel:setY(self:getAbsoluteY() + (y) + StoryScreen.instance.listbox:getYScroll() + 32 + 8);	StoryScreen.instance.richTextReusablePanel:paginate();	StoryScreen.instance.richTextReusablePanel:prerender();	StoryScreen.instance.richTextReusablePanel:render();	self.itemheightoverride[item.item:getName()] = self.itemheight;	y = y + self.itemheightoverride[item.item:getName()];	return y;endfunction StoryScreen:prerender()	ISPanel.prerender(self);	self:drawTextCentre("SELECT STORY", self.width / 2, 10, 1, 1, 1, 1, UIFont.Large);endfunction StoryScreen:onOptionMouseDown(button, x, y)	 if button.internal == "BACK" then		self:setVisible(false);		MainScreen.instance.storyScreen:setVisible(false);		MainScreen.instance.bottomPanel:setVisible(true);	 elseif button.internal == "PLAY" then		getWorld():setMap(StoryScreen.instance.listbox.items[StoryScreen.instance.listbox.selected].item:getMap());		if self.mode == "NEW" then			createStory(StoryScreen.instance.listbox.items[StoryScreen.instance.listbox.selected].text);		else		--	getWorld():setGameMode(StoryScreen.instance.listbox.items[StoryScreen.instance.listbox.selected].text);			getWorld():setWorld("newstory");        end        GameWindow.doRenderEvent(false);		forceChangeState(GameLoadingState.new());	 endendfunction StoryScreen:new (x, y, width, height)	local o = {}	--o.data = {}	o = ISPanel:new(x, y, width, height);	StoryScreen.instance = o;	setmetatable(o, self)	self.__index = self	o.x = x;	o.y = y;	o.backgroundColor = {r=0, g=0, b=0, a=0.3};	o.borderColor = {r=1, g=1, b=1, a=0.2};	o.width = width;	o.height = height;	o.anchorLeft = true;	o.anchorRight = false;	o.anchorTop = true;	o.anchorBottom = false;	o.itemheightoverride = {}	o.selected = 1;	return oend