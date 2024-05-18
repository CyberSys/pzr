require "Util/LuaList"

SurvivalGuideEntries = {}

SurvivalGuideEntries.list = LuaList:new();

SurvivalGuideEntries.addSurvivalGuideEntry = function (title, text, moreInfo, openConditionMethod, completeConditionMethod, moreInfoText)
    local entry = {title = title, text = text, moreInfo = moreInfo};

    SurvivalGuideEntries.list:add(entry);
end

SurvivalGuideEntries.getEntry = function(num)
    return SurvivalGuideEntries.list:get(num);
end

SurvivalGuideEntries.getEntryCount = function()
    return SurvivalGuideEntries.list:size();
end

SurvivalGuideEntries.addSurvivalGuideEntry(getText("SurvivalGuide_entrie1title"), getText("SurvivalGuide_entrie1txt"), getText("SurvivalGuide_entrie1moreinfo"));

SurvivalGuideEntries.addSurvivalGuideEntry(getText("SurvivalGuide_entrie2title"), getText("SurvivalGuide_entrie2txt"), getText("SurvivalGuide_entrie2moreinfo"));

SurvivalGuideEntries.addSurvivalGuideEntry(getText("SurvivalGuide_entrie3title"), getText("SurvivalGuide_entrie3txt"), getText("SurvivalGuide_entrie3moreinfo"));

SurvivalGuideEntries.addSurvivalGuideEntry(getText("SurvivalGuide_entrie4title"), getText("SurvivalGuide_entrie4txt"), getText("SurvivalGuide_entrie4moreinfo"));

SurvivalGuideEntries.addSurvivalGuideEntry(getText("SurvivalGuide_entrie5title"), getText("SurvivalGuide_entrie5txt"), getText("SurvivalGuide_entrie5moreinfo"));

SurvivalGuideEntries.addSurvivalGuideEntry(getText("SurvivalGuide_entrie6title"), getText("SurvivalGuide_entrie6txt"), getText("SurvivalGuide_entrie6moreinfo"));

SurvivalGuideEntries.addSurvivalGuideEntry(getText("SurvivalGuide_entrie7title"), getText("SurvivalGuide_entrie7txt"), getText("SurvivalGuide_entrie7moreinfo"));

SurvivalGuideEntries.addSurvivalGuideEntry(getText("SurvivalGuide_entrie8title"), getText("SurvivalGuide_entrie8txt"), getText("SurvivalGuide_entrie8moreinfo"));

SurvivalGuideEntries.addSurvivalGuideEntry(getText("SurvivalGuide_entrie9title"), getText("SurvivalGuide_entrie9txt"), getText("SurvivalGuide_entrie9moreinfo"));

SurvivalGuideEntries.addSurvivalGuideEntry(getText("SurvivalGuide_entrie10title"), getText("SurvivalGuide_entrie10txt"), getText("SurvivalGuide_entrie10moreinfo"));
