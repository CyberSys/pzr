NPCFetchQuest = {}
NPCFetchQuest.LastScriptCompleteNPCQuest = {}



-- Gives an item to the player from an npc
NPCFetchQuest.NPCQuestGiveItem = function()

    local params = NPCFetchQuest.LastScriptCompleteNPCQuest.params;
    --print("Giving item "..params.give);

    if not getPlayer():hasItems(params.want, params.wantnum) then
        LastScriptCompleteNPCQuest.params = nil;
        return;
    end
    for i=1, params.givenum do
        getPlayer():getInventory():AddItem(params.give);
    end

    NPCFetchQuest.LastScriptCompleteNPCQuest.params = nil;
end

NPCFetchQuest.ScriptCompleteNPCQuest = function(params)

    NPCFetchQuest.LastScriptCompleteNPCQuest.params = params;

    local survivor = params.desc:getInstance();

    getScriptManager():PlayInstanceScript("questnpccomplete", "Base.NPCQuestComplete", {Intruder = getPlayer(), Occupant = survivor } );

end

NPCFetchQuest.ScriptTestNPCFetchQuest = function(params)

    local survivorInstance = params.desc:getInstance();

    -- make sure survivor is spooled in.
    if survivorInstance == nil then
        return false;
    end

    if not getPlayer():hasItems(params.want, params.wantnum) then
        return false;
    end

    if survivorInstance:DistTo(getPlayer()) < 4 then

        -- if in same building
        if survivorInstance:getBuilding() == getPlayer():getBuilding() then
            -- and survivor can see player...
            if(survivorInstance:CanSee(getPlayer())) then
                return true;
            end
        end

    end

    return false;
end

-- called for fetch quests for npcs
NPCFetchQuest.ScriptCreateNPCQuest = function(want, wantnum, give, givenum, character)

    -- get the descriptor (safe if character spools out)
    local desc = character:getDescriptor();

    print ("setting params")
    local params = {}
    params.want = want;
    params.wantnum = wantnum;
    params.give = give;
    params.givenum = givenum;
    params.desc = desc;
    print ("creating quest")
    QuestCreator.ClearQuest("GenericFetch"..desc:getID());
    QuestCreator.CreateQuest("GenericFetch"..desc:getID(), "Do the booze thing");
    --print("adding lua condition");
    QuestCreator.AddQuestTask_LuaCondition("test", "Find booze for guy.", ScriptTestNPCFetchQuest, params);
    --print("unlocking quest stage");
    QuestCreator.Unlock();
    QuestCreator.AddQuestAction_CallLua(ScriptCompleteNPCQuest, params);
    --print("unlocking quest");
    QuestCreator.UnlockQuest("GenericFetch"..desc:getID());
end

NPCFetchQuest.Trigger =  function(type, data, context)

    local square = nil;

    while square == nil do
        local roomList = context:getRooms();

        local roomCount = roomList:size();

        --print("getting room")
        local room = roomList:get(ZombRand(roomCount));

        --print("getting square")
        square = getCell():getFreeTile(room);

    end

    if(square ~= nil) then
        -- create npc
        local x = square:getX();
        local y = square:getY();
        local z = square:getZ();
        --print("creating survivor");
        local survivordesc = SurvivorFactory.CreateSurvivor();
        --print("creating survivor at "..x..", "..y..", "..z);
        local survivor = IsoSurvivor.new(survivordesc, getCell(), x, y, z);
        survivor:setAllowBehaviours(false);
        data.survivor = survivor;
        data.survivordesc = survivordesc;
        -- add the survivor desc to the building, so he'll be here next time we spool in.
        context:addCharacter(data.survivordesc);
    end

end


NPCFetchQuest.FutureTrigger = function(type, data, context)
	if data.survivor ~= nil and data.done ~= true then
		if data.survivor:getBuilding() == getPlayer():getBuilding() then
			getScriptManager():PlayInstanceScript("questnpc"..data.survivor:getDescriptor():getID(), "Base.NPCQuest", {Intruder = getPlayer(), Occupant = data.survivor } );
			data.done = true;
		end
	end
end