BaseGameCharacterDetails = {}

BaseGameCharacterDetails.CreateCharacterInstance = function(s)
    local desc = s:getDescriptor();
    --print(desc);
   --- if(desc:hasObservation("Tough") or desc:hasObservation("Aggressive")) then
	    -- do combat fit...
	    local r = ZombRand(0, 6);

	    local weapon = "Base.BaseballBat";

	    s:getInventory():AddItem(weapon);
	--    s:getInventory():AddItem(weapon);
	--    s:getInventory():AddItem(weapon);
   -- end

end


BaseGameCharacterDetails.CreateCharacterStats = function(desc)

    local rand = ZombRand(0, 10);

    if ZombRand(4) == 0 then
        desc:addObservation("Clumsy");
    end
    if ZombRand(4) == 0 then
        desc:addObservation("Shifty");
    end
    if ZombRand(4) == 0 then
        desc:addObservation("Unstable");
    end
    if ZombRand(10) == 0 then
        desc:addObservation("Insane");
    end
    if ZombRand(4) == 0 then
        desc:addObservation("Depressed");
    end
    if ZombRand(4) == 0 then
        desc:addObservation("Charasmatic");
    end
    if ZombRand(4) == 0 then
        desc:addObservation("Insurbordinate");
    elseif ZombRand(4) == 0 then
        desc:addObservation("Loyal");
    end

    if rand <= 2 then
        if ZombRand(2) == 0 then
            desc:addObservation("Aggressive");
        elseif ZombRand(4) == 0 then
            desc:addObservation("Friendly");
        elseif ZombRand(4) == 0 then
            desc:addObservation("Cruel");
        end
        if ZombRand(2) == 0 then
            desc:addObservation("Brave");
        end

        -- further aggressive related things...
        if ZombRand(2) == 0 then
            desc:addObservation("Tough");
        end
        if ZombRand(2) == 0 then
            desc:addObservation("Confident");
        end
        if ZombRand(2) == 0 then
            desc:addObservation("Quiet");
        end
    elseif rand >= 7 then
        if ZombRand(2) == 0 then
            desc:addObservation("Friendly");
        elseif ZombRand(3) == 0 then
            desc:addObservation("Aggressive");
        end
        if ZombRand(2) == 0 then
            desc:addObservation("Nervous");
        end
        if ZombRand(2) == 0 then
            desc:addObservation("Kind-hearted");
        end
        if ZombRand(2) == 0 then
            desc:addObservation("Weak");
        end
        if ZombRand(2) == 0 then
            desc:addObservation("Loud");
        end
        if ZombRand(2) == 0 then
            desc:addObservation("Coward");
        end

    else
        if ZombRand(2) == 0 then
            desc:addObservation("Brave");
        elseif ZombRand(2) == 0 then
            desc:addObservation("Coward");
        end
        if ZombRand(2) == 0 then
            desc:addObservation("Nervous");
        elseif ZombRand(2) == 0 then
            desc:addObservation("Confident");
        end
        if ZombRand(2) == 0 then
            desc:addObservation("Weak");
        elseif ZombRand(2) == 0 then
            desc:addObservation("Tough");
        end
        if ZombRand(2) == 0 then
            desc:addObservation("Loud");
        elseif ZombRand(2) == 0 then
            desc:addObservation("Quiet");
        end

    end
end



BaseGameCharacterDetails.DoTraits = function()
    TraitFactory.addTrait("Axeman", getText("UI_trait_axeman"), 0, getText("UI_trait_axemandesc"), true);
    local handy = TraitFactory.addTrait("Handy", getText("UI_trait_handy"), 8, getText("UI_trait_handydesc"), false);
	handy:addXPBoost(Perks.BluntMaintenance, 1)
	handy:addXPBoost(Perks.BladeMaintenance, 1)
	handy:addXPBoost(Perks.Woodwork, 1)

	--    TraitFactory.addTrait("Patient", getText("UI_trait_patient"), 4, getText("UI_trait_patientdesc"), false);
 --   TraitFactory.addTrait("ShortTemper", getText("UI_trait_shorttemper"), -4, getText("UI_trait_shorttemperdesc"), false);
  --  TraitFactory.addTrait("Brooding", getText("UI_trait_brooding"), -2, getText("UI_trait_broodingdesc"), false);
    TraitFactory.addTrait("SpeedDemon", getText("UI_trait_SpeedDemon"), 1, getText("UI_trait_SpeedDemonDesc"), false);
	TraitFactory.addTrait("SundayDriver", getText("UI_trait_SundayDriver"), -1, getText("UI_trait_SundayDriverDesc"), false);
    TraitFactory.addTrait("Brave", getText("UI_trait_brave"), 4, getText("UI_trait_bravedesc"), false);
    TraitFactory.addTrait("Cowardly", getText("UI_trait_cowardly"), -2, getText("UI_trait_cowardlydesc"), false);
    TraitFactory.addTrait("Clumsy", getText("UI_trait_clumsy"), -2, getText("UI_trait_clumsydesc"), false);
    TraitFactory.addTrait("Graceful", getText("UI_trait_graceful"), 4, getText("UI_trait_gracefuldesc"), false);
    TraitFactory.addTrait("Hypercondriac", getText("UI_trait_hypochon"), -2, getText("UI_trait_hypochondesc"), false);
    TraitFactory.addTrait("ShortSighted", getText("UI_trait_shortsigh"), -2, getText("UI_trait_shortsighdesc"), false);
	TraitFactory.addTrait("HardOfHearing", getText("UI_trait_hardhear"), -2, getText("UI_trait_hardheardesc"), false);
	TraitFactory.addTrait("Deaf", getText("UI_trait_deaf"), -12, getText("UI_trait_deafdesc"), false);
    TraitFactory.addTrait("KeenHearing", getText("UI_trait_keenhearing"), 6, getText("UI_trait_keenhearingdesc"), false);
    TraitFactory.addTrait("EagleEyed", getText("UI_trait_eagleeyed"), 6, getText("UI_trait_eagleeyeddesc"), false);
    TraitFactory.addTrait("HeartyAppitite", getText("UI_trait_heartyappetite"), -4, getText("UI_trait_heartyappetitedesc"), false);
    TraitFactory.addTrait("LightEater", getText("UI_trait_lighteater"), 4, getText("UI_trait_lighteaterdesc"), false);
    TraitFactory.addTrait("ThickSkinned", getText("UI_trait_thickskinned"), 6, getText("UI_trait_thickskinneddesc"), false);
    local unfit = TraitFactory.addTrait("Unfit", getText("UI_trait_unfit"), -10, getText("UI_trait_unfitdesc"), false);
    unfit:addXPBoost(Perks.Fitness, -4)
    local outof = TraitFactory.addTrait("Out of Shape", getText("UI_trait_outofshape"), -6, getText("UI_trait_outofshapedesc"), false);
    outof:addXPBoost(Perks.Fitness, -2)
	local fit = TraitFactory.addTrait("Fit", getText("UI_trait_fit"), 6, getText("UI_trait_fitdesc"), false);
    fit:addXPBoost(Perks.Fitness, 2)
    local ath = TraitFactory.addTrait("Athletic", getText("UI_trait_athletic"), 10, getText("UI_trait_athleticdesc"), false);
    ath:addXPBoost(Perks.Fitness, 4)
    TraitFactory.addTrait("Nutritionist", getText("UI_trait_nutritionist"), 4, getText("UI_trait_nutritionistdesc"), false);
    TraitFactory.addTrait("Nutritionist2", getText("UI_trait_nutritionist"), 0, getText("UI_trait_nutritionistdesc"), true);
    TraitFactory.addTrait("Emaciated", getText("UI_trait_emaciated"), -10, getText("UI_trait_emaciateddesc"), true);
	TraitFactory.addTrait("Very Underweight", getText("UI_trait_veryunderweight"), -10, getText("UI_trait_veryunderweightdesc"), false);
    TraitFactory.addTrait("Underweight", getText("UI_trait_underweight"), -6, getText("UI_trait_underweightdesc"), false);
    TraitFactory.addTrait("Overweight", getText("UI_trait_overweight"), -6, getText("UI_trait_overweightdesc"), false);
    TraitFactory.addTrait("Obese", getText("UI_trait_obese"), -10, getText("UI_trait_obesedesc"), false);
    local strong = TraitFactory.addTrait("Strong", getText("UI_trait_strong"), 10, getText("UI_trait_strongdesc"), false);
    strong:addXPBoost(Perks.Strength, 4)
	local stout = TraitFactory.addTrait("Stout", getText("UI_trait_stout"), 6, getText("UI_trait_stoutdesc"), false);
    stout:addXPBoost(Perks.Strength, 2)
    local weak = TraitFactory.addTrait("Weak", getText("UI_trait_weak"), -10, getText("UI_trait_weakdesc"), false);
    weak:addXPBoost(Perks.Strength, -5)
	local feeble = TraitFactory.addTrait("Feeble", getText("UI_trait_feeble"), -6, getText("UI_trait_feebledesc"), false);
    feeble:addXPBoost(Perks.Strength, -2)
    TraitFactory.addTrait("Resilient", getText("UI_trait_resilient"), 4, getText("UI_trait_resilientdesc"), false);
    TraitFactory.addTrait("ProneToIllness", getText("UI_trait_pronetoillness"), -4, getText("UI_trait_pronetoillnessdesc"), false);
    --TraitFactory.addTrait("LightDrinker", getText("UI_trait_lightdrink"), -2, getText("UI_trait_lightdrinkdesc"), false);
   -- TraitFactory.addTrait("HeavyDrinker", getText("UI_trait_harddrink"), 3, getText("UI_trait_harddrinkdesc"), false);
    TraitFactory.addTrait("Agoraphobic", getText("UI_trait_agoraphobic"), -4, getText("UI_trait_agoraphobicdesc"), false);
    TraitFactory.addTrait("Claustophobic", getText("UI_trait_claustro"), -4, getText("UI_trait_claustrodesc"), false);
    TraitFactory.addTrait("Lucky", getText("UI_trait_lucky"), 4, getText("UI_trait_luckydesc"), false);
    TraitFactory.addTrait("Unlucky", getText("UI_trait_unlucky"), -4, getText("UI_trait_unluckydesc"), false);
    TraitFactory.addTrait("Marksman", getText("UI_trait_marksman"), 0, getText("UI_trait_marksmandesc"), true);
    TraitFactory.addTrait("NightOwl", getText("UI_trait_nightowl"), 0, getText("UI_trait_nightowldesc"), true);
   -- TraitFactory.addTrait("GiftOfTheGab", getText("UI_trait_giftgab"), 0, getText("UI_trait_giftgabdesc"), true);

	TraitFactory.addTrait("Outdoorsman", getText("UI_trait_outdoorsman"), 2, getText("UI_trait_outdoorsmandesc"), false);

	local sleepOK = (isClient() or isServer()) and getServerOptions():getBoolean("SleepAllowed") and getServerOptions():getBoolean("SleepNeeded")

	TraitFactory.addTrait("FastHealer", getText("UI_trait_FastHealer"), 6, getText("UI_trait_FastHealerDesc"), false);
	TraitFactory.addTrait("FastLearner", getText("UI_trait_FastLearner"), 6, getText("UI_trait_FastLearnerDesc"), false);
	TraitFactory.addTrait("FastReader", getText("UI_trait_FastReader"), 2, getText("UI_trait_FastReaderDesc"), false);
	TraitFactory.addTrait("AdrenalineJunkie", getText("UI_trait_AdrenalineJunkie"), 8, getText("UI_trait_AdrenalineJunkieDesc"), false);
	TraitFactory.addTrait("Inconspicuous", getText("UI_trait_Inconspicuous"), 4, getText("UI_trait_InconspicuousDesc"), false);
	TraitFactory.addTrait("NeedsLessSleep", getText("UI_trait_LessSleep"), 2, getText("UI_trait_LessSleepDesc"), false, not sleepOK);
	TraitFactory.addTrait("NightVision", getText("UI_trait_NightVision"), 2, getText("UI_trait_NightVisionDesc"), false);
	TraitFactory.addTrait("Organized", getText("UI_trait_Packmule"), 6, getText("UI_trait_PackmuleDesc"), false);
	TraitFactory.addTrait("LowThirst", getText("UI_trait_LowThirst"), 6, getText("UI_trait_LowThirstDesc"), false);

	--TraitFactory.addTrait("Injured", "Injured", -4, getText("UI_trait_outdoorsmandesc"), false);

	local selfdef = TraitFactory.addTrait("SelfDefenseClass", getText("UI_trait_SelfDefenseClass"), 6, getText("UI_trait_SelfDefenseClassDesc"), false);
	selfdef:addXPBoost(Perks.BladeGuard, 1)
	selfdef:addXPBoost(Perks.BluntGuard, 1)

	local firstAid = TraitFactory.addTrait("FirstAid", getText("UI_trait_FirstAid"), 4, getText("UI_trait_FirstAidDesc"), false);
	firstAid:addXPBoost(Perks.Doctor, 1)

	local fisher = TraitFactory.addTrait("Fishing", getText("UI_trait_Fishing"), 4, getText("UI_trait_FishingDesc"), false);
	fisher:addXPBoost(Perks.Fishing, 1)
    fisher:getFreeRecipes():add("Make Fishing Rod");
    fisher:getFreeRecipes():add("Fix Fishing Rod");

	local gardener = TraitFactory.addTrait("Gardener", getText("UI_trait_Gardener"), 4, getText("UI_trait_GardenerDesc"), false);
    gardener:addXPBoost(Perks.Farming, 1)
    gardener:getFreeRecipes():add("Make Mildew Cure");
    gardener:getFreeRecipes():add("Make Flies Cure");

	local jogger = TraitFactory.addTrait("Jogger", getText("UI_trait_Jogger"), 4, getText("UI_trait_JoggerDesc"), false);
	jogger:addXPBoost(Perks.Sprinting, 1)

	TraitFactory.addTrait("SlowHealer", getText("UI_trait_SlowHealer"), -6, getText("UI_trait_SlowHealerDesc"), false);
	TraitFactory.addTrait("SlowLearner", getText("UI_trait_SlowLearner"), -6, getText("UI_trait_SlowLearnerDesc"), false);
	TraitFactory.addTrait("SlowReader", getText("UI_trait_SlowReader"), -2, getText("UI_trait_SlowReaderDesc"), false);
	TraitFactory.addTrait("NeedsMoreSleep", getText("UI_trait_MoreSleep"), -4, getText("UI_trait_MoreSleepDesc"), false, not sleepOK);
	TraitFactory.addTrait("Conspicuous", getText("UI_trait_Conspicuous"), -4, getText("UI_trait_ConspicuousDesc"), false);
	TraitFactory.addTrait("Disorganized", getText("UI_trait_Disorganized"), -4, getText("UI_trait_DisorganizedDesc"), false);
	TraitFactory.addTrait("HighThirst", getText("UI_trait_HighThirst"), -6, getText("UI_trait_HighThirstDesc"), false);
	TraitFactory.addTrait("Illiterate", getText("UI_trait_Illiterate"), -8, getText("UI_trait_IlliterateDesc"), false);
	TraitFactory.addTrait("Insomniac", getText("UI_trait_Insomniac"), -6, getText("UI_trait_InsomniacDesc"), false, not sleepOK);
	TraitFactory.addTrait("Pacifist", getText("UI_trait_Pacifist"), -4, getText("UI_trait_PacifistDesc"), false);
	TraitFactory.addTrait("Thinskinned", getText("UI_trait_ThinSkinned"), -6, getText("UI_trait_ThinSkinnedDesc"), false);
    TraitFactory.addTrait("Smoker", getText("UI_trait_Smoker"), -4, getText("UI_trait_SmokerDesc"), false);

	TraitFactory.addTrait("Dextrous", getText("UI_trait_Dexterous"), 2, getText("UI_trait_DexterousDesc"), false);
	TraitFactory.addTrait("AllThumbs", getText("UI_trait_AllThumbs"), -2, getText("UI_trait_AllThumbsDesc"), false);

	TraitFactory.addTrait("Desensitized", getText("UI_trait_Desensitized"), 0, getText("UI_trait_DesensitizedDesc"), true);
    TraitFactory.addTrait("WeakStomach", getText("UI_trait_WeakStomach"), -3, getText("UI_trait_WeakStomachDesc"), false);
    TraitFactory.addTrait("IronGut", getText("UI_trait_IronGut"), 3, getText("UI_trait_IronGutDesc"), false);
    TraitFactory.addTrait("Hemophobic", getText("UI_trait_Hemophobic"), -3, getText("UI_trait_HemophobicDesc"), false);
    TraitFactory.addTrait("Asthmatic", getText("UI_trait_Asthmatic"), -5, getText("UI_trait_AsthmaticDesc"), false);
--    local blacksmith = TraitFactory.addTrait("Blacksmith", getText("UI_trait_Blacksmith"), 6, getText("UI_trait_BlacksmithDesc"), false);
--    blacksmith:addXPBoost(Perks.Blacksmith, 1)
--    blacksmith:addXPBoost(Perks.Melting, 1)
--    doMetalWorkerRecipes(blacksmith);
--    TraitFactory.addTrait("Blacksmith2", getText("UI_trait_Blacksmith"), 0, getText("UI_trait_BlacksmithDesc"), true);

--    local metalworker = TraitFactory.addTrait("Metalworker", getText("UI_trait_Metalworker"), 6, getText("UI_trait_MetalworkerDesc"), false);
--    metalworker:addXPBoost(Perks.MetalWelding, 2)
--    metalworker:getFreeRecipes():add("Metal Walls");
--    metalworker:getFreeRecipes():add("Metal Fences");
--    metalworker:getFreeRecipes():add("Metal Containers");
--    metalworker:getFreeRecipes():add("Metal Containers");
--    metalworker:getFreeRecipes():add("Make Metal Pipe");
--    metalworker:getFreeRecipes():add("Make Metal Sheet");
--    TraitFactory.addTrait("Metalworker2", getText("UI_trait_Metalworker"), 0, getText("UI_trait_MetalworkerDesc"), true);

    local cook = TraitFactory.addTrait("Cook", getText("UI_trait_Cook"), 6, getText("UI_trait_CookDesc"), false);
    cook:addXPBoost(Perks.Cooking, 2)
    cook:getFreeRecipes():add("Make Cake Batter");
    cook:getFreeRecipes():add("Make Pie Dough");
    cook:getFreeRecipes():add("Make Bread Dough");
    TraitFactory.addTrait("Cook2", getText("UI_trait_Cook"), 0, getText("UI_trait_Cook2Desc"), true);

    local herbalist = TraitFactory.addTrait("Herbalist", getText("UI_trait_Herbalist"), 6, getText("UI_trait_HerbalistDesc"), false);
    herbalist:getFreeRecipes():add("Herbalist");

	local barfighter = TraitFactory.addTrait("Brawler", getText("UI_trait_BarFighter"), 6, getText("UI_trait_BarFighterDesc"), false);

	barfighter:addXPBoost(Perks.Axe, 1)
	barfighter:addXPBoost(Perks.Blunt, 1)

    local formerscout = TraitFactory.addTrait("Formerscout", getText("UI_trait_Scout"), 6, getText("UI_trait_ScoutDesc"), false);

    formerscout:addXPBoost(Perks.Doctor, 1)
    formerscout:addXPBoost(Perks.PlantScavenging, 1)

--	local football = TraitFactory.addTrait("PlaysFootball", "Plays Football", 4, getText("UI_trait_outdoorsmandesc"), false);

--	football:addXPBoost(Perks.Sprinting, 1)

	local baseball = TraitFactory.addTrait("BaseballPlayer", getText("UI_trait_PlaysBaseball"), 4, getText("UI_trait_PlaysBaseballDesc"), false);

	baseball:addXPBoost(Perks.Blunt, 1)

	local backpacker = TraitFactory.addTrait("Hiker", getText("UI_trait_Hiker"), 6, getText("UI_trait_HikerDesc"), false);

	backpacker:addXPBoost(Perks.PlantScavenging, 1)
	backpacker:addXPBoost(Perks.Trapping, 1)
    backpacker:getFreeRecipes():add("Make Stick Trap");
    backpacker:getFreeRecipes():add("Make Snare Trap");
    backpacker:getFreeRecipes():add("Make Wooden Cage Trap");

	local hunter = TraitFactory.addTrait("Hunter", getText("UI_trait_Hunter"), 8, getText("UI_trait_HunterDesc"), false);

	hunter:addXPBoost(Perks.Aiming, 1)
	hunter:addXPBoost(Perks.Trapping, 1)
	hunter:addXPBoost(Perks.Sneak, 1)
    hunter:getFreeRecipes():add("Make Stick Trap");
    hunter:getFreeRecipes():add("Make Snare Trap");
    hunter:getFreeRecipes():add("Make Wooden Cage Trap");
    hunter:getFreeRecipes():add("Make Trap Box");
    hunter:getFreeRecipes():add("Make Cage Trap");

	local gym = TraitFactory.addTrait("Gymnast", getText("UI_trait_Gymnast"), 5, getText("UI_trait_GymnastDesc"), false);

	gym:addXPBoost(Perks.Lightfoot, 1)
	gym:addXPBoost(Perks.Nimble, 1)
	
	local carenthusiast = TraitFactory.addTrait("Mechanics", getText("UI_trait_Mechanics"), 5, getText("UI_trait_MechanicsDesc"), false);
	carenthusiast:addXPBoost(Perks.Mechanics, 1);
	carenthusiast:getFreeRecipes():add("Basic Mechanics");
	carenthusiast:getFreeRecipes():add("Intermediate Mechanics");
	TraitFactory.addTrait("Mechanics2", getText("UI_trait_Mechanics"), 0, getText("UI_trait_Mechanics2Desc"), true);
	
	
	--    TraitFactory.setMutualExclusive("Blacksmith", "Blacksmith2");
--    TraitFactory.setMutualExclusive("Metalworker", "Metalworker2");
	TraitFactory.setMutualExclusive("SpeedDemon", "SundayDriver");
	TraitFactory.setMutualExclusive("Dextrous", "AllThumbs");
    TraitFactory.setMutualExclusive("Nutritionist", "Nutritionist2");
    TraitFactory.setMutualExclusive("Cook", "Cook2");
	TraitFactory.setMutualExclusive("Mechanics", "Mechanics2");

	TraitFactory.setMutualExclusive("FastHealer", "SlowHealer");
	TraitFactory.setMutualExclusive("FastLearner", "SlowLearner");
	TraitFactory.setMutualExclusive("FastReader", "SlowReader");
    TraitFactory.setMutualExclusive("Illiterate", "SlowReader");
    TraitFactory.setMutualExclusive("Illiterate", "FastReader");
	TraitFactory.setMutualExclusive("NeedsLessSleep", "NeedsMoreSleep");
	TraitFactory.setMutualExclusive("ThickSkinned", "Thinskinned");
	TraitFactory.setMutualExclusive("LowThirst", "HighThirst");

	--   TraitFactory.setMutualExclusive("ShortTemper", "Patient");
	TraitFactory.setMutualExclusive("Conspicuous", "Inconspicuous");
	TraitFactory.setMutualExclusive("Weak", "Strong");
	TraitFactory.setMutualExclusive("Weak", "Stout");
	TraitFactory.setMutualExclusive("Weak", "Feeble");
	TraitFactory.setMutualExclusive("Stout", "Feeble");
	TraitFactory.setMutualExclusive("Strong", "Feeble");
    TraitFactory.setMutualExclusive("Strong", "Stout");
    TraitFactory.setMutualExclusive("Overweight", "Obese");
    TraitFactory.setMutualExclusive("Overweight", "Underweight");
    TraitFactory.setMutualExclusive("Very Underweight", "Underweight");
    TraitFactory.setMutualExclusive("Overweight", "Very Underweight");
    TraitFactory.setMutualExclusive("Overweight", "Emaciated");
    TraitFactory.setMutualExclusive("Obese", "Underweight");
    TraitFactory.setMutualExclusive("Obese", "Very Underweight");
    TraitFactory.setMutualExclusive("Obese", "Emaciated");
    TraitFactory.setMutualExclusive("Athletic", "Overweight");
    TraitFactory.setMutualExclusive("Athletic", "Fit");
    TraitFactory.setMutualExclusive("Athletic", "Obese");
    TraitFactory.setMutualExclusive("Athletic", "Out of Shape");
    TraitFactory.setMutualExclusive("Athletic", "Unfit");
    TraitFactory.setMutualExclusive("Fit", "Out of Shape");
    TraitFactory.setMutualExclusive("Fit", "Unfit");
    TraitFactory.setMutualExclusive("Fit", "Overweight");
    TraitFactory.setMutualExclusive("Fit", "Obese");
    TraitFactory.setMutualExclusive("Unfit", "Out of Shape");
    TraitFactory.setMutualExclusive("Organized", "Disorganized");
    TraitFactory.setMutualExclusive("Resilient", "ProneToIllness");
   -- TraitFactory.setMutualExclusive("LightDrinker", "HeavyDrinker");
    TraitFactory.setMutualExclusive("HardOfHearing", "KeenHearing");
    TraitFactory.setMutualExclusive("HeartyAppitite", "LightEater");
    TraitFactory.setMutualExclusive("Clumsy", "Graceful");
    TraitFactory.setMutualExclusive("Brave", "Cowardly");
    TraitFactory.setMutualExclusive("ShortSighted", "EagleEyed");
    TraitFactory.setMutualExclusive("Lucky", "Unlucky");
    TraitFactory.setMutualExclusive("Deaf", "HardOfHearing");
    TraitFactory.setMutualExclusive("Deaf", "KeenHearing");
    TraitFactory.setMutualExclusive("Desensitized", "Hemophobic");
    TraitFactory.setMutualExclusive("Desensitized", "Cowardly");
    TraitFactory.setMutualExclusive("Desensitized", "Brave");
    TraitFactory.setMutualExclusive("Desensitized", "Agoraphobic");
    TraitFactory.setMutualExclusive("Desensitized", "Claustophobic");
    TraitFactory.setMutualExclusive("Desensitized", "AdrenalineJunkie");
    TraitFactory.setMutualExclusive("IronGut", "WeakStomach");

    TraitFactory.sortList();

	local traitList = TraitFactory.getTraits()
	for i=1,traitList:size() do
		local trait = traitList:get(i-1)
		BaseGameCharacterDetails.SetTraitDescription(trait)
	end
end

function BaseGameCharacterDetails.SetTraitDescription(trait)
	local desc = trait:getDescription() or ""
	local boost = transformIntoKahluaTable(trait:getXPBoostMap())
	local infoList = {}
	for perk,level in pairs(boost) do
		local perkName = PerkFactory.getPerkName(perk)
		if perk == Perks.Axe then
			perkName = getText("IGUI_perks_Blade") .. " " .. perkName
		elseif perk == Perks.Blunt then
			perkName = getText("IGUI_perks_Blunt") .. " " .. perkName
		elseif perk == Perks.BluntMaintenance or perk == Perks.BluntGuard then
			perkName = getText("IGUI_perks_Blunt") .. " " .. perkName
		elseif perk == Perks.BladeMaintenance or perk == Perks.BladeGuard then
			perkName = getText("IGUI_perks_Blade") .. " " .. perkName
		end
		-- "+1 Cooking" etc
		local levelStr = tostring(level:intValue())
		if level:intValue() > 0 then levelStr = "+" .. levelStr end
		table.insert(infoList, { perkName = perkName, levelStr = levelStr })
	end
	table.sort(infoList, function(a,b) return not string.sort(a.perkName, b.perkName) end)
	for _,info in ipairs(infoList) do
		if desc ~= "" then desc = desc .. "\n" end
		desc = desc .. info.levelStr .. " " .. info.perkName
	end
	trait:setDescription(desc)
end

BaseGameCharacterDetails.DoObservations = function()

    ObservationFactory.addObservation("Tough", "Tough", "He looks like he can handle $himselfherself$ in a fight.");
    ObservationFactory.addObservation("Weak", "Weak", "He doesn't look like he is ready to fight.");
    ObservationFactory.setMutualExclusive("Tough", "Weak");

    ObservationFactory.addObservation("Nervous", "Nervous", "He looks a bit edgy, nervous even.");
    ObservationFactory.addObservation("Confident", "Confident", "He oozes confidence.");
    ObservationFactory.setMutualExclusive("Nervous", "Confident");

    ObservationFactory.addObservation("Aggressive", "Aggressive", "His demeanour makes you feel nervous.");
    ObservationFactory.addObservation("Friendly", "Friendly", "He seems relaxed and friendly.");
    ObservationFactory.setMutualExclusive("Aggressive", "Friendly");

    ObservationFactory.addObservation("Quiet", "Quiet", "He is a man of few words.");
    ObservationFactory.addObservation("Loud", "Loud", "He never seems to shut up.");
    ObservationFactory.setMutualExclusive("Quiet", "Loud");

--ObservationFactory.setMutualExclusive("ShortTemper", "Patient");



end

BaseGameCharacterDetails.DoProfessions = function()

	local unemployed = ProfessionFactory.addProfession("unemployed", getText("UI_prof_unemployed"), "", 8);

    local fireofficer = ProfessionFactory.addProfession("fireofficer", getText("UI_prof_fireoff"), "profession_fireofficer2", 0);
--    fireofficer:addFreeTrait("Axeman");
	fireofficer:addXPBoost(Perks.Sprinting, 1)
    fireofficer:addXPBoost(Perks.Strength, 1)
    fireofficer:addXPBoost(Perks.Fitness, 1)
    --fireofficer:getFreeRecipes():add("Saw Logs");

    local policeofficer = ProfessionFactory.addProfession("policeofficer", getText("UI_prof_policeoff"), "profession_policeofficer2", -4);
    --policeofficer:addFreeTrait("Marksman");
	policeofficer:addXPBoost(Perks.Aiming, 3)
	policeofficer:addXPBoost(Perks.Reloading, 2)
	policeofficer:addXPBoost(Perks.Nimble, 1)

    local parkranger = ProfessionFactory.addProfession("parkranger", getText("UI_prof_parkranger"), "profession_parkranger2", -4);
	parkranger:addXPBoost(Perks.Trapping, 2)
	parkranger:addXPBoost(Perks.PlantScavenging, 2)
	parkranger:addXPBoost(Perks.Woodwork, 1)
    parkranger:getFreeRecipes():add("Make Stick Trap");
    parkranger:getFreeRecipes():add("Make Snare Trap");
    parkranger:getFreeRecipes():add("Make Wooden Cage Trap");
    parkranger:getFreeRecipes():add("Make Trap Box");
    parkranger:getFreeRecipes():add("Make Cage Trap");


    local constructionworker = ProfessionFactory.addProfession("constructionworker", getText("UI_prof_constructionworker"), "profession_constructionworker2", -2);
--    constructionworker:addFreeTrait("ThickSkinned");
	constructionworker:addXPBoost(Perks.Blunt, 3)
	constructionworker:addXPBoost(Perks.Woodwork, 1)
--    constructionworker:addFreeTrait("Handy");

    local securityguard = ProfessionFactory.addProfession("securityguard", getText("UI_prof_securityguard"), "profession_securityguard2", -2);
	securityguard:addXPBoost(Perks.Sprinting, 2)
	securityguard:addXPBoost(Perks.Lightfoot, 1)
	securityguard:addFreeTrait("NightOwl");

	local carpenter = ProfessionFactory.addProfession("carpenter", getText("UI_prof_Carpenter"), "profession_hammer2", 2);
	carpenter:addXPBoost(Perks.Woodwork, 3)

	local burglar = ProfessionFactory.addProfession("burglar", getText("UI_prof_Burglar"), "profession_burglar2", -6);
	burglar:addXPBoost(Perks.Nimble, 2)
	burglar:addXPBoost(Perks.Sneak, 2)
	burglar:addXPBoost(Perks.Lightfoot, 2)

	local chef = ProfessionFactory.addProfession("chef", getText("UI_prof_Chef"), "profession_chef2", -4);
	chef:addXPBoost(Perks.Cooking, 3)
	chef:addXPBoost(Perks.BladeMaintenance, 1)
	chef:addXPBoost(Perks.Axe, 1)
    chef:getFreeRecipes():add("Make Cake Batter");
    chef:getFreeRecipes():add("Make Pie Dough");
    chef:getFreeRecipes():add("Make Bread Dough");
    chef:addFreeTrait("Cook2");

	local repairman = ProfessionFactory.addProfession("repairman", getText("UI_prof_Repairman"), "profession_repairman2", -4);
	repairman:addXPBoost(Perks.Woodwork, 1)
	repairman:addXPBoost(Perks.BladeMaintenance, 2)
	repairman:addXPBoost(Perks.BluntMaintenance, 2)

	local farmer = ProfessionFactory.addProfession("farmer", getText("UI_prof_Farmer"), "profession_farmer2", 2);
	farmer:addXPBoost(Perks.Farming, 3)
    farmer:getFreeRecipes():add("Make Mildew Cure");
    farmer:getFreeRecipes():add("Make Flies Cure");

	local fisherman = ProfessionFactory.addProfession("fisherman", getText("UI_prof_Fisherman"), "profession_fisher2", -2);
	fisherman:addXPBoost(Perks.Fishing, 3)
	fisherman:addXPBoost(Perks.PlantScavenging, 1)
    fisherman:getFreeRecipes():add("Make Fishing Rod");
    fisherman:getFreeRecipes():add("Fix Fishing Rod");
    fisherman:getFreeRecipes():add("Get Wire Back");
    fisherman:getFreeRecipes():add("Make Fishing Net");

	local doctor = ProfessionFactory.addProfession("doctor", getText("UI_prof_Doctor"), "profession_doctor2", 2);
	doctor:addXPBoost(Perks.Doctor, 3)

	local veteran = ProfessionFactory.addProfession("veteran", getText("UI_prof_Veteran"), "profession_veteran2", -8);
	veteran:addFreeTrait("Desensitized");
	veteran:addXPBoost(Perks.Aiming, 2)
	veteran:addXPBoost(Perks.Reloading, 2)

    local nurse = ProfessionFactory.addProfession("nurse", getText("UI_prof_Nurse"), "profession_nurse", 2);
    nurse:addXPBoost(Perks.Doctor, 2)
    nurse:addXPBoost(Perks.Lightfoot, 1)

    local lumberjack = ProfessionFactory.addProfession("lumberjack", getText("UI_prof_Lumberjack"), "profession_lumberjack", 0);
    lumberjack:addXPBoost(Perks.Axe, 2)
    lumberjack:addXPBoost(Perks.Strength, 1)
    lumberjack:addFreeTrait("Axeman");

    local fitnessInstructor = ProfessionFactory.addProfession("fitnessInstructor", getText("UI_prof_FitnessInstructor"), "profession_fitnessinstructor", -6);
    fitnessInstructor:addXPBoost(Perks.Fitness, 3)
    fitnessInstructor:addXPBoost(Perks.Sprinting, 2)
    fitnessInstructor:addFreeTrait("Nutritionist2");

    local burger = ProfessionFactory.addProfession("burgerflipper", getText("UI_prof_BurgerFlipper"), "profession_burgerflipper", 2);
    burger:addXPBoost(Perks.Cooking, 2)
    burger:addXPBoost(Perks.BladeMaintenance, 1)
    burger:addFreeTrait("Cook2");

    local electrician = ProfessionFactory.addProfession("electrician", getText("UI_prof_Electrician"), "profession_electrician", -4);
    electrician:addXPBoost(Perks.Electricity, 3)
    electrician:getFreeRecipes():add("Generator");
    electrician:getFreeRecipes():add("Make Remote Controller V1");
    electrician:getFreeRecipes():add("Make Remote Controller V2");
    electrician:getFreeRecipes():add("Make Remote Controller V3");
    electrician:getFreeRecipes():add("Make Remote Trigger");
    electrician:getFreeRecipes():add("Make Timer");
    electrician:getFreeRecipes():add("Craft Makeshift Radio");
    electrician:getFreeRecipes():add("Craft Makeshift HAM Radio");
    electrician:getFreeRecipes():add("Craft Makeshift Walkie Talkie");

    local engineer = ProfessionFactory.addProfession("engineer", getText("UI_prof_Engineer"), "profession_engineer", -4);
    engineer:addXPBoost(Perks.Electricity, 1);
    engineer:addXPBoost(Perks.Woodwork, 1);
    engineer:getFreeRecipes():add("Make Aerosol bomb");
    engineer:getFreeRecipes():add("Make Flame bomb");
    engineer:getFreeRecipes():add("Make Pipe bomb");
    engineer:getFreeRecipes():add("Make Noise generator");
    engineer:getFreeRecipes():add("Make Smoke Bomb");

    local metalworker = ProfessionFactory.addProfession("metalworker", getText("UI_prof_MetalWorker"), "profession_metalworker", -6);
    metalworker:addXPBoost(Perks.MetalWelding, 3);
--    metalworker:addXPBoost(Perks.Blacksmith, 2)
--    metalworker:addXPBoost(Perks.Melting, 1);
    metalworker:getFreeRecipes():add("Make Metal Walls");
    metalworker:getFreeRecipes():add("Make Metal Fences");
    metalworker:getFreeRecipes():add("Make Metal Containers");
    metalworker:getFreeRecipes():add("Make Metal Sheet");
    metalworker:getFreeRecipes():add("Make Small Metal Sheet");
    metalworker:getFreeRecipes():add("Make Metal Roof");
    --    metalworker:getFreeRecipes():add("Make Metal Pipe");
--    metalworker:addFreeTrait("Metalworker2");
--    metalworker:addFreeTrait("Blacksmith2");
--    doMetalWorkerRecipes(metalworker);

--    local smither = ProfessionFactory.addProfession("smither", getText("UI_prof_Smither"), "profession_smither", -6);
--    smither:addXPBoost(Perks.Blacksmith, 3);
--    smither:addXPBoost(Perks.Melting, 2);
--    smither:addFreeTrait("Blacksmith2");
--    doMetalWorkerRecipes(smither);
	
	local mechanics = ProfessionFactory.addProfession("mechanics", getText("UI_prof_Mechanics"), "profession_mechanic", -4);
	mechanics:addXPBoost(Perks.Mechanics, 3);
	mechanics:getFreeRecipes():add("Basic Mechanics");
	mechanics:getFreeRecipes():add("Intermediate Mechanics");
	mechanics:getFreeRecipes():add("Advanced Mechanics");
	mechanics:addFreeTrait("Mechanics2");

	local profList = ProfessionFactory.getProfessions()
	for i=1,profList:size() do
		local prof = profList:get(i-1)
		BaseGameCharacterDetails.SetProfessionDescription(prof)
	end
end

function BaseGameCharacterDetails.SetProfessionDescription(prof)
	local desc = getTextOrNull("UI_profdesc_" .. prof:getType()) or ""
	local boost = transformIntoKahluaTable(prof:getXPBoostMap())
	local infoList = {}
	for perk,level in pairs(boost) do
		local perkName = PerkFactory.getPerkName(perk)
		if perk == Perks.Axe then
			perkName = getText("IGUI_perks_Blade") .. " " .. perkName
		elseif perk == Perks.Blunt then
			perkName = getText("IGUI_perks_Blunt") .. " " .. perkName
		elseif perk == Perks.BluntMaintenance or perk == Perks.BluntGuard then
			perkName = getText("IGUI_perks_Blunt") .. " " .. perkName
		elseif perk == Perks.BladeMaintenance or perk == Perks.BladeGuard then
			perkName = getText("IGUI_perks_Blade") .. " " .. perkName
		end
		-- "+1 Cooking" etc
		local levelStr = tostring(level:intValue())
		if level:intValue() > 0 then levelStr = "+" .. levelStr end
		table.insert(infoList, { perkName = perkName, levelStr = levelStr })
	end
	table.sort(infoList, function(a,b) return not string.sort(a.perkName, b.perkName) end)
	for _,info in ipairs(infoList) do
		if desc ~= "" then desc = desc .. "\n" end
		desc = desc .. info.levelStr .. " " .. info.perkName
	end
	local traits = prof:getFreeTraits()
	for j=1,traits:size() do
		if desc ~= "" then desc = desc .. "\n" end
		local traitName = traits:get(j-1)
		local trait = TraitFactory.getTrait(traitName)
		desc = desc .. trait:getLabel()
	end
	prof:setDescription(desc)
end

BaseGameCharacterDetails.DoSurname = function()
	SurvivorFactory.addSurname("Simpson");SurvivorFactory.addSurname("Hodgetts");SurvivorFactory.addSurname("Porter");SurvivorFactory.addSurname("Smith");SurvivorFactory.addSurname("Johnson");SurvivorFactory.addSurname("Williams");
	SurvivorFactory.addSurname("Jones");SurvivorFactory.addSurname("Brown");SurvivorFactory.addSurname("Davis");SurvivorFactory.addSurname("Miller");SurvivorFactory.addSurname("Wilson");SurvivorFactory.addSurname("Moore");
	SurvivorFactory.addSurname("Taylor");SurvivorFactory.addSurname("Anderson");SurvivorFactory.addSurname("Thomas");SurvivorFactory.addSurname("Jackson");SurvivorFactory.addSurname("White");SurvivorFactory.addSurname("Harris");
	SurvivorFactory.addSurname("Martin");SurvivorFactory.addSurname("Thompson");SurvivorFactory.addSurname("Garcia");SurvivorFactory.addSurname("Martinez");SurvivorFactory.addSurname("Robinson");SurvivorFactory.addSurname("Clark");
	SurvivorFactory.addSurname("Rodriguez");SurvivorFactory.addSurname("Lewis");SurvivorFactory.addSurname("Lee");SurvivorFactory.addSurname("Walker");SurvivorFactory.addSurname("Hall");SurvivorFactory.addSurname("Allen");
	SurvivorFactory.addSurname("Young");SurvivorFactory.addSurname("Hernandez");SurvivorFactory.addSurname("King");SurvivorFactory.addSurname("Wright");SurvivorFactory.addSurname("Lopez");SurvivorFactory.addSurname("Hill");
	SurvivorFactory.addSurname("Scott");SurvivorFactory.addSurname("Green");SurvivorFactory.addSurname("Adams");SurvivorFactory.addSurname("Baker");SurvivorFactory.addSurname("Gonzalez");SurvivorFactory.addSurname("Nelson");
	SurvivorFactory.addSurname("Carter");SurvivorFactory.addSurname("Mitchell");SurvivorFactory.addSurname("Perez");SurvivorFactory.addSurname("Roberts");SurvivorFactory.addSurname("Turner");SurvivorFactory.addSurname("Phillips");
	SurvivorFactory.addSurname("Campbell");SurvivorFactory.addSurname("Parker");SurvivorFactory.addSurname("Evans");SurvivorFactory.addSurname("Edwards");SurvivorFactory.addSurname("Collins");SurvivorFactory.addSurname("Stewart");
	SurvivorFactory.addSurname("Sanchez");SurvivorFactory.addSurname("Morris");SurvivorFactory.addSurname("Rogers");SurvivorFactory.addSurname("Reed");SurvivorFactory.addSurname("Cook");SurvivorFactory.addSurname("Morgan");
	SurvivorFactory.addSurname("Bell");SurvivorFactory.addSurname("Murphy");SurvivorFactory.addSurname("Bailey");SurvivorFactory.addSurname("Cooper");SurvivorFactory.addSurname("Richardson");SurvivorFactory.addSurname("Cox");
	SurvivorFactory.addSurname("Howard");SurvivorFactory.addSurname("Ward");SurvivorFactory.addSurname("Torres");SurvivorFactory.addSurname("Peterson");SurvivorFactory.addSurname("Gray");SurvivorFactory.addSurname("Ramirez");
	SurvivorFactory.addSurname("James");SurvivorFactory.addSurname("Watson");SurvivorFactory.addSurname("Brooks");SurvivorFactory.addSurname("Kelly");SurvivorFactory.addSurname("Sanders");SurvivorFactory.addSurname("Price");
	SurvivorFactory.addSurname("Bennet");SurvivorFactory.addSurname("Wood");
end

BaseGameCharacterDetails.DoFemaleForename = function()
	SurvivorFactory.addFemaleForename("Marina");SurvivorFactory.addFemaleForename("Patricia");SurvivorFactory.addFemaleForename("Mary");
	SurvivorFactory.addFemaleForename("Linda"); SurvivorFactory.addFemaleForename("Barbara"); SurvivorFactory.addFemaleForename("Elizabeth"); SurvivorFactory.addFemaleForename("Jennifer"); SurvivorFactory.addFemaleForename("Maria"); SurvivorFactory.addFemaleForename("Susan");
	SurvivorFactory.addFemaleForename("Margaret"); SurvivorFactory.addFemaleForename("Dorothy"); SurvivorFactory.addFemaleForename("Lisa"); SurvivorFactory.addFemaleForename("Nancy"); SurvivorFactory.addFemaleForename("Karen"); SurvivorFactory.addFemaleForename("Betty"); SurvivorFactory.addFemaleForename("Helen");
	SurvivorFactory.addFemaleForename("Sandra"); SurvivorFactory.addFemaleForename("Donna"); SurvivorFactory.addFemaleForename("Carol"); SurvivorFactory.addFemaleForename("Ruth"); SurvivorFactory.addFemaleForename("Sharon"); SurvivorFactory.addFemaleForename("Michelle"); SurvivorFactory.addFemaleForename("Laura");
	SurvivorFactory.addFemaleForename("Sarah"); SurvivorFactory.addFemaleForename("Kimberly"); SurvivorFactory.addFemaleForename("Deborah"); SurvivorFactory.addFemaleForename("Jessica"); SurvivorFactory.addFemaleForename("Shirley"); SurvivorFactory.addFemaleForename("Cynthia");
	SurvivorFactory.addFemaleForename("Angela"); SurvivorFactory.addFemaleForename("Melissa"); SurvivorFactory.addFemaleForename("Brenda"); SurvivorFactory.addFemaleForename("Amy"); SurvivorFactory.addFemaleForename("Anna"); SurvivorFactory.addFemaleForename("Rebecca");
end

BaseGameCharacterDetails.DoMaleForename = function()
	SurvivorFactory.addMaleForename("James"); SurvivorFactory.addMaleForename("John");
	SurvivorFactory.addMaleForename("Robert"); SurvivorFactory.addMaleForename("Bobby"); SurvivorFactory.addMaleForename("Michael"); SurvivorFactory.addMaleForename("William"); SurvivorFactory.addMaleForename("David"); SurvivorFactory.addMaleForename("Richard");
	SurvivorFactory.addMaleForename("Charles"); SurvivorFactory.addMaleForename("Joseph"); SurvivorFactory.addMaleForename("Thomas"); SurvivorFactory.addMaleForename("Chris"); SurvivorFactory.addMaleForename("Christopher"); SurvivorFactory.addMaleForename("Daniel");
	SurvivorFactory.addMaleForename("Paul"); SurvivorFactory.addMaleForename("Mark"); SurvivorFactory.addMaleForename("Donald"); SurvivorFactory.addMaleForename("George"); SurvivorFactory.addMaleForename("Kenneth"); SurvivorFactory.addMaleForename("Steven"); SurvivorFactory.addMaleForename("Stephen");
	SurvivorFactory.addMaleForename("Edward"); SurvivorFactory.addMaleForename("Brian"); SurvivorFactory.addMaleForename("Ronald"); SurvivorFactory.addMaleForename("Anthony"); SurvivorFactory.addMaleForename("Kevin"); SurvivorFactory.addMaleForename("Jason");
	SurvivorFactory.addMaleForename("Matthew"); SurvivorFactory.addMaleForename("Gary"); SurvivorFactory.addMaleForename("Timothy"); SurvivorFactory.addMaleForename("Jose"); SurvivorFactory.addMaleForename("Larry"); SurvivorFactory.addMaleForename("Jeffrey"); SurvivorFactory.addMaleForename("Frank");
	SurvivorFactory.addMaleForename("Scott"); SurvivorFactory.addMaleForename("Eric"); SurvivorFactory.addMaleForename("Andrew"); SurvivorFactory.addMaleForename("Andy"); SurvivorFactory.addMaleForename("Raymond"); SurvivorFactory.addMaleForename("Gregory"); SurvivorFactory.addMaleForename("Greg");
	SurvivorFactory.addMaleForename("Joshua"); SurvivorFactory.addMaleForename("Josh"); SurvivorFactory.addMaleForename("Jerry"); SurvivorFactory.addMaleForename("Dennis"); SurvivorFactory.addMaleForename("Walter"); SurvivorFactory.addMaleForename("Patrick"); SurvivorFactory.addMaleForename("Peter");
	SurvivorFactory.addMaleForename("Harold"); SurvivorFactory.addMaleForename("Douglas"); SurvivorFactory.addMaleForename("Henry"); SurvivorFactory.addMaleForename("Carl"); SurvivorFactory.addMaleForename("Arthur"); SurvivorFactory.addMaleForename("Ryan"); SurvivorFactory.addMaleForename("Roger");
	SurvivorFactory.addMaleForename("Joe"); SurvivorFactory.addMaleForename("Juan"); SurvivorFactory.addMaleForename("Albert"); SurvivorFactory.addMaleForename("Jonathan"); SurvivorFactory.addMaleForename("Justin"); SurvivorFactory.addMaleForename("Terry"); SurvivorFactory.addMaleForename("Gerald");
	SurvivorFactory.addMaleForename("Keith"); SurvivorFactory.addMaleForename("Samuel"); SurvivorFactory.addMaleForename("Sam"); SurvivorFactory.addMaleForename("Willie"); SurvivorFactory.addMaleForename("Ralph"); SurvivorFactory.addMaleForename("Lawrence");
	SurvivorFactory.addMaleForename("Nicholas"); SurvivorFactory.addMaleForename("Nick"); SurvivorFactory.addMaleForename("Roy"); SurvivorFactory.addMaleForename("Benjamin"); SurvivorFactory.addMaleForename("Ben"); SurvivorFactory.addMaleForename("Bruce"); SurvivorFactory.addMaleForename("Brandon");
	SurvivorFactory.addMaleForename("Adam"); SurvivorFactory.addMaleForename("Harry"); SurvivorFactory.addMaleForename("Fred"); SurvivorFactory.addMaleForename("Wayne"); SurvivorFactory.addMaleForename("Billy"); SurvivorFactory.addMaleForename("Steve"); SurvivorFactory.addMaleForename("Louis");
	SurvivorFactory.addMaleForename("Jeremy"); SurvivorFactory.addMaleForename("Aaron"); SurvivorFactory.addMaleForename("Randy"); SurvivorFactory.addMaleForename("Howard"); SurvivorFactory.addMaleForename("Eugene"); SurvivorFactory.addMaleForename("Carlos");
	SurvivorFactory.addMaleForename("Russell"); SurvivorFactory.addMaleForename("Bobby"); SurvivorFactory.addMaleForename("Victor"); SurvivorFactory.addMaleForename("Martin"); SurvivorFactory.addMaleForename("Ernest"); SurvivorFactory.addMaleForename("Phillip");
	SurvivorFactory.addMaleForename("Todd"); SurvivorFactory.addMaleForename("Jesse"); SurvivorFactory.addMaleForename("Craig"); SurvivorFactory.addMaleForename("Alan"); SurvivorFactory.addMaleForename("Shawn"); SurvivorFactory.addMaleForename("Clarence"); SurvivorFactory.addMaleForename("Sean");
	SurvivorFactory.addMaleForename("Philip"); SurvivorFactory.addMaleForename("Johnny"); SurvivorFactory.addMaleForename("Earl"); SurvivorFactory.addMaleForename("Jimmy"); SurvivorFactory.addMaleForename("Antonio"); SurvivorFactory.addMaleForename("Danny"); SurvivorFactory.addMaleForename("Bryan");
	SurvivorFactory.addMaleForename("Tony"); SurvivorFactory.addMaleForename("Luis"); SurvivorFactory.addMaleForename("Mike"); SurvivorFactory.addMaleForename("Stanley"); SurvivorFactory.addMaleForename("Leonard"); SurvivorFactory.addMaleForename("Nathan"); SurvivorFactory.addMaleForename("Dale");
end

BaseGameCharacterDetails.DoSpawnPoint = function()
end

-- do all the specific traits/passiv skills when a new player is created
--BaseGameCharacterDetails.DoSpawnTrait = function(player, square)
--
--	if getCore():getGameMode() ~= "LastStand" then
--		-- a new player start with fitness and strength at lvl 2 (it means average), then it depend on your trait
--		if player:HasTrait("Weak") then
--			-- nothing :)
--		elseif player:HasTrait("Feeble") then
--			player:getXp():AddXP(Perks.Strength, PerkFactory.getPerk(Perks.Strength):getXp2());
--		elseif player:HasTrait("Stout") then
--			player:getXp():AddXP(Perks.Strength, PerkFactory.getPerk(Perks.Strength):getXp6());
--		elseif player:HasTrait("Strong") then
--			player:getXp():AddXP(Perks.Strength, PerkFactory.getPerk(Perks.Strength):getXp9());
--		else
--			player:getXp():AddXP(Perks.Strength, PerkFactory.getPerk(Perks.Strength):getXp5());
--		end
--
--		if player:HasTrait("Obese") then
--			-- nothing :)
--		elseif player:HasTrait("Overweight") then
--			player:getXp():AddXP(Perks.Fitness, PerkFactory.getPerk(Perks.Fitness):getXp2());
--		elseif player:HasTrait("Fit") then
--			player:getXp():AddXP(Perks.Fitness, PerkFactory.getPerk(Perks.Fitness):getXp6());
--		elseif player:HasTrait("Athletic") then
--			player:getXp():AddXP(Perks.Fitness, PerkFactory.getPerk(Perks.Fitness):getXp9());
--		else
--			player:getXp():AddXP(Perks.Fitness, PerkFactory.getPerk(Perks.Fitness):getXp5());
--		end
--	end
--end

-- Hair/Clothing color : Be carefull to not add too much color, it'll create too much garbage colector :)

BaseGameCharacterDetails.DoTrouserColor = function()
	SurvivorDesc.addTrouserColor(ColorInfo.new(0.5411764979362488,0.6470588445663452,0.7176470756530762, 1)) -- light blue
	SurvivorDesc.addTrouserColor(ColorInfo.new(0.364705890417099,0.47058823704719543,0.5372549295425415, 1)) -- medium blue
	SurvivorDesc.addTrouserColor(ColorInfo.new(0.20000000298023224,0.30588236451148987,0.37254902720451355, 1)) -- grey blue
	SurvivorDesc.addTrouserColor(ColorInfo.new(0.0941176488995552,0.18039216101169586,0.239215686917305, 1)) -- navy blue
	SurvivorDesc.addTrouserColor(ColorInfo.new(0.1725490242242813,0.27450981736183167,0.5411764979362488, 1)) -- blue
	SurvivorDesc.addTrouserColor(ColorInfo.new(0.7333333492279053,0.7333333492279053,0.7333333492279053, 1)) -- white
	SurvivorDesc.addTrouserColor(ColorInfo.new(0.47058823704719543,0.47058823704719543,0.47058823704719543, 1)) -- grey
	SurvivorDesc.addTrouserColor(ColorInfo.new(0.19607843458652496,0.19607843458652496,0.19607843458652496, 1)) -- dark grey
	SurvivorDesc.addTrouserColor(ColorInfo.new(0.5058823823928833,0.3333333432674408,0.16862745583057404, 1)) -- sand
	SurvivorDesc.addTrouserColor(ColorInfo.new(0.3019607961177826,0.1725490242242813,0.0470588244497776, 1)) -- brown
	SurvivorDesc.addTrouserColor(ColorInfo.new(0.239215686917305,0.35686275362968445,0.14509804546833038, 1)) -- green
end

BaseGameCharacterDetails.DoHairColor = function()
	SurvivorDesc.addHairColor(ColorInfo.new(0.8313725590705872,0.6705882549285889,0.2705882489681244, 1)) -- blonde
	SurvivorDesc.addHairColor(ColorInfo.new(0.6235294342041016,0.42352941632270813,0.16862745583057404, 1)) -- light brown
	SurvivorDesc.addHairColor(ColorInfo.new(0.572549045085907,0.4274509847164154,   0.27450981736183167, 1)) -- sand
	SurvivorDesc.addHairColor(ColorInfo.new(0.4156862795352936,0.18039216101169586,0.07058823853731155, 1)) -- brown
	SurvivorDesc.addHairColor(ColorInfo.new(0.6980392336845398,0.21176470816135406,0.13333334028720856, 1)) -- red
	SurvivorDesc.addHairColor(ColorInfo.new(0.572549045085907,0.6235294342041016,0.6549019813537598, 1)) -- grey
	SurvivorDesc.addHairColor(ColorInfo.new(0.12156862765550613,0.14509804546833038,0.16078431904315948, 1)) -- black
end

doMetalWorkerRecipes = function (metalworker)
    metalworker:getFreeRecipes():add("Make Fork");
    metalworker:getFreeRecipes():add("Make Spoon");
    metalworker:getFreeRecipes():add("Make Cooking Pot");
    metalworker:getFreeRecipes():add("Make Roasting Pan");
    metalworker:getFreeRecipes():add("Make Saucepan");
    metalworker:getFreeRecipes():add("Make Baking Tray");
    metalworker:getFreeRecipes():add("Make Baking Pan");
    metalworker:getFreeRecipes():add("Make Pan");
    metalworker:getFreeRecipes():add("Make Letter Opener");
    metalworker:getFreeRecipes():add("Make Nails");
    metalworker:getFreeRecipes():add("Make Paperclips");
    metalworker:getFreeRecipes():add("Make Scissors");
    metalworker:getFreeRecipes():add("Make Door Knob");
    metalworker:getFreeRecipes():add("Make Hinge");
    metalworker:getFreeRecipes():add("Make Butter Knife");
    metalworker:getFreeRecipes():add("Make Ball Peen Hammer");
    metalworker:getFreeRecipes():add("Make Tongs");
    metalworker:getFreeRecipes():add("Make Hammer");
    metalworker:getFreeRecipes():add("Make Sheet Metal");
    metalworker:getFreeRecipes():add("Make Suture Needle Holder");
    metalworker:getFreeRecipes():add("Make Tweezers");
    metalworker:getFreeRecipes():add("Make Suture Needle");
    metalworker:getFreeRecipes():add("Make Metal Drum");
    metalworker:getFreeRecipes():add("Make Kitchen Knife");
    metalworker:getFreeRecipes():add("Make Saw");
    metalworker:getFreeRecipes():add("Make Hunting Knife");
    metalworker:getFreeRecipes():add("Make 9mm Bullets Mold");
    metalworker:getFreeRecipes():add("Make 308 Bullets Mold");
    metalworker:getFreeRecipes():add("Make 223 Bullets Mold");
    metalworker:getFreeRecipes():add("Make Shotgun Shells Mold");
    metalworker:getFreeRecipes():add("Make 9mm Bullets");
    metalworker:getFreeRecipes():add("Make Shotgun Shells");
    metalworker:getFreeRecipes():add("Make 308 Bullets");
    metalworker:getFreeRecipes():add("Make 223 Bullets");
    metalworker:getFreeRecipes():add("Make Crowbar");
    metalworker:getFreeRecipes():add("Make Golfclub");
    metalworker:getFreeRecipes():add("Make Axe");
    metalworker:getFreeRecipes():add("Make Sledgehammer");
    metalworker:getFreeRecipes():add("Make Shovel");
    metalworker:getFreeRecipes():add("Make Hand Shovel");
end

--Events.OnCharacterCreateStats.Add(BaseGameCharacterDetails.CreateCharacterStats);
Events.OnCreateSurvivor.Add(BaseGameCharacterDetails.CreateCharacterInstance);

Events.OnGameBoot.Add(BaseGameCharacterDetails.DoTraits);
Events.OnGameBoot.Add(BaseGameCharacterDetails.DoProfessions);
Events.OnGameBoot.Add(BaseGameCharacterDetails.DoSpawnPoint);
Events.OnGameBoot.Add(BaseGameCharacterDetails.DoObservations);
Events.OnGameBoot.Add(BaseGameCharacterDetails.DoSurname);
Events.OnGameBoot.Add(BaseGameCharacterDetails.DoFemaleForename);
Events.OnGameBoot.Add(BaseGameCharacterDetails.DoMaleForename);
Events.OnGameBoot.Add(BaseGameCharacterDetails.DoTrouserColor);
Events.OnGameBoot.Add(BaseGameCharacterDetails.DoHairColor);

--Events.OnNewGame.Add(BaseGameCharacterDetails.DoSpawnTrait);
