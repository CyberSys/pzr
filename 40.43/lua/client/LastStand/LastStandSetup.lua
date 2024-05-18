LastStandData = {}
LastStandData.chosenChallenge = nil;

function preLoadLastStandInit()
    if getCore():isChallenge() then
        globalChallenge = LastStandData.chosenChallenge;
        globalChallenge.OnInitWorld();

        print("setting last stand spawn point");
        getWorld():setLuaSpawnCellX(globalChallenge.xcell);
        getWorld():setLuaSpawnCellY(globalChallenge.ycell);
        getWorld():setLuaPosX(globalChallenge.x);
        getWorld():setLuaPosY(globalChallenge.y);
        getWorld():setLuaPosZ(globalChallenge.z);

        Events.OnGameStart.Add(doLastStandInit);
        Events.OnPostUIDraw.Add(doLastStandDraw);
        Events.OnCreatePlayer.Add(doLastStandCreatePlayer);
        Events.OnPlayerDeath.Add(doLastStandPlayerDeath);
    end
end

function doLastStandDraw()
    if globalChallenge ~= nil then
        globalChallenge.Render();
    end

end

function doLastStandInit()
    if getCore():getGameMode() == "LastStand" then
        print("initialising last stand");
        getGameTime():setTimeOfDay(globalChallenge.hourOfDay);
        getGameTime():setMinutesPerDay(60 * 24);
        globalChallenge.Init();
		-- save the selected player to a text file
		-- at this stage, we only have 1 player, we're gonna save the other player if they are added via the controller
		saveLastStandPlayerInFile(getPlayer());
    end
end

function saveLastStandPlayerInFile(player)
	if not MainScreen.instance.lastStandPlayerSelect then -- if it's doesn't exist that's because we're saving from the game
		MainScreen.instance.lastStandPlayerSelect = {};
	end
	if not MainScreen.instance.lastStandPlayerSelect.playerSelected then -- if it's doesn't exist that's because we created a new player
		MainScreen.instance.lastStandPlayerSelect.playerSelected = {};
		MainScreen.instance.lastStandPlayerSelect.playerSelected.playedTime = player:getModData()["playedTime"];
	end
	if not player:getModData()["challenge2Level"] then
		player:getModData()["challenge2Level"] = MainScreen.instance.lastStandPlayerSelect.playerSelected.level or 0;
	end
	if not player:getModData()["challenge2Xp"] then
		player:getModData()["challenge2Xp"] = MainScreen.instance.lastStandPlayerSelect.playerSelected.globalXp or 0;
	end
	if not player:getModData()["challenge2BoostGoldLevel"] then
		player:getModData()["challenge2BoostGoldLevel"] = MainScreen.instance.lastStandPlayerSelect.playerSelected.boostGoldLevel or 1;
	end
	if not player:getModData()["challenge2BoostXpLevel"] then
		player:getModData()["challenge2BoostXpLevel"] = MainScreen.instance.lastStandPlayerSelect.playerSelected.boostXpLevel or 1;
	end
	if not player:getModData()["challenge2StartingGoldLevel"] then
		player:getModData()["challenge2StartingGoldLevel"] = MainScreen.instance.lastStandPlayerSelect.playerSelected.startingGoldLevel or 0;
	end
	local playedTime = MainScreen.instance.lastStandPlayerSelect.playerSelected.playedTime or 0;
	player:getModData()["playedTime"] = playedTime;
	local fileOutput = getFileWriter("Players/player" .. player:getDescriptor():getForename() .. player:getDescriptor():getSurname() .. ".txt", true, false);
	fileOutput:write("Player" .. player:getDescriptor():getForename() .. player:getDescriptor():getSurname() .. "\r\n{ \r\n");
	fileOutput:write("  Forename=" .. player:getDescriptor():getForename() .. "\r\n");
	fileOutput:write("  Surname=" .. player:getDescriptor():getSurname() .. "\r\n");
	fileOutput:write("  Toppal=" .. (player:getDescriptor():getToppal() or "") .. ";" .. player:getDescriptor():getTopColor():getR() .. "," .. player:getDescriptor():getTopColor():getG() .. "," .. player:getDescriptor():getTopColor():getB() ..  "\r\n");
	fileOutput:write("  Bottomspal=" .. (player:getDescriptor():getBottomspal() or "") .. ";" .. player:getDescriptor():getTrouserColor():getR() .. "," .. player:getDescriptor():getTrouserColor():getG() .. "," .. player:getDescriptor():getTrouserColor():getB() .. "\r\n");
	fileOutput:write("  Torso=" .. player:getDescriptor():getTorso() .. "\r\n");
	fileOutput:write("  Hair=" .. player:getDescriptor():getHair()  .. ";" .. player:getDescriptor():getHairColor():getR() .. "," .. player:getDescriptor():getHairColor():getG() .. "," .. player:getDescriptor():getHairColor():getB() ..  "\r\n");
	fileOutput:write("  Profession=" .. player:getDescriptor():getProfession() .. "\r\n");
	fileOutput:write("  Level=" .. player:getModData()["challenge2Level"] .. "\r\n");
	fileOutput:write("  GlobalXp=" .. player:getModData()["challenge2Xp"] .. "\r\n");
	fileOutput:write("  PlayedTime=" .. player:getModData()["playedTime"] .. "\r\n");
	if player:getDescriptor():isFemale() then
		fileOutput:write("  Female" .. "\r\n");
	else
		fileOutput:write("  Male" .. "\r\n");
	end
	fileOutput:write("}\r\n");
	fileOutput:write("Traits \r\n{ \r\n");
	for i=0,player:getTraits():size() -1 do
		local trait = player:getTraits():get(i);
		fileOutput:write("  addTrait="..trait.."\r\n");
	end
	fileOutput:write("}\r\n");
	fileOutput:write("Skills \r\n{ \r\n");
	fileOutput:write("  addSkills=Blunt," .. player:getPerkLevel(Perks.Blunt) .. "\r\n");
	fileOutput:write("  addSkills=Blade," .. player:getPerkLevel(Perks.Axe) .. "\r\n");
	fileOutput:write("  addSkills=Carpentry," .. player:getPerkLevel(Perks.Woodwork) .. "\r\n");
	fileOutput:write("}\r\n");
	fileOutput:write("Bonus \r\n{ \r\n");
	fileOutput:write("  addGoldBoostBonus=" .. player:getModData()["challenge2BoostGoldLevel"] .. "\r\n");
	fileOutput:write("  addStartingGoldBonus=" .. player:getModData()["challenge2StartingGoldLevel"] .. "\r\n");
	fileOutput:write("  addXpBoostBonus=" .. player:getModData()["challenge2BoostXpLevel"] .. "\r\n");
	fileOutput:write("}\r\n");
	fileOutput:close();
end

function doLastStandCreatePlayer(id)
    print(id);

    -- print(p);
    local pl = getSpecificPlayer(id);

    if getCore():getGameMode() == "LastStand" then
        print("Creating player for last stand");

        globalChallenge.AddPlayer(id);
    end
end

function doLastStandPlayerDeath(playerObj)
    if globalChallenge then
        globalChallenge.RemovePlayer(playerObj)
    end
end

function doLastStandBackButtonWheel(playerNum, dir)
	if globalChallenge then
		globalChallenge.onBackButtonWheel(playerNum, dir)
	end
end

Events.OnInitWorld.Add(preLoadLastStandInit);
