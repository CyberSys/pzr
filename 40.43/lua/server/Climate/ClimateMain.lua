--[[
-- Generated file (ClimateMain_20181206_080403)
-- Climate color configuration
-- File should be placed in: media/lua/server/Climate/ClimateMain.lua (remove date stamp)
--]]

ClimateMain = {};
ClimateMain.versionStamp = "20181206_080403";

local WARM,NORMAL,CLOUDY = 0,1,2;

local SUMMER,FALL,WINTER,SPRING = 0,1,2,3;

function ClimateMain.onClimateManagerInit(_clim)
    local c;
    c = _clim:getColNightNoMoon();
    c:setExterior(0.25,0.25,0.7,0.8);
    c:setInterior(0.06,0.06,0.35,0.4);

    c = _clim:getColNightMoon();
    c:setExterior(0.33,0.33,1.0,0.8);
    c:setInterior(0.12,0.13,0.4,0.4);

    c = _clim:getColFog();
    c:setExterior(0.2,0.2,0.4,0.8);
    c:setInterior(0.1,0.1,0.5,0.5);

    c = _clim:getColFogLegacy();
    c:setExterior(0.3,0.3,0.3,0.8);
    c:setInterior(0.3,0.3,0.3,0.8);

    local w = _clim:getWeatherPeriod();

    c = w:getCloudColorReddish();
    c:setExterior(0.4,0.05,0.05,0.6);
    c:setInterior(0.21,0.05,0.05,0.5);

    c = w:getCloudColorGreenish();
    c:setExterior(0.14,0.27,0.05,0.6);
    c:setInterior(0.07,0.15,0.02,0.5);

    c = w:getCloudColorBlueish();
    c:setExterior(0.13,0.28,0.3,0.6);
    c:setInterior(0.05,0.15,0.16,0.43);

    c = w:getCloudColorPurplish();
    c:setExterior(0.38,0.12,0.49,0.7);
    c:setInterior(0.26,0.03,0.34,0.37);

    -- ###################### Dawn ######################
    _clim:setSeasonColorDawn(WARM,SUMMER,0.44,0.69,0.75,0.75,true);		--exterior
    _clim:setSeasonColorDawn(WARM,SUMMER,0.03,0.32,0.37,0.26,false);		--interior

    _clim:setSeasonColorDawn(CLOUDY,SUMMER,0.29,0.49,0.61,0.66,true);		--exterior
    _clim:setSeasonColorDawn(CLOUDY,SUMMER,0.05,0.14,0.22,0.38,false);		--interior

    _clim:setSeasonColorDawn(WARM,FALL,0.61,0.5,0.72,0.75,true);		--exterior
    _clim:setSeasonColorDawn(WARM,FALL,0.25,0.18,0.35,0.26,false);		--interior

    _clim:setSeasonColorDawn(CLOUDY,FALL,0.41,0.29,0.46,0.68,true);		--exterior
    _clim:setSeasonColorDawn(CLOUDY,FALL,0.23,0.14,0.3,0.37,false);		--interior

    _clim:setSeasonColorDawn(WARM,WINTER,0.48,0.57,0.72,0.75,true);		--exterior
    _clim:setSeasonColorDawn(WARM,WINTER,0.14,0.21,0.32,0.41,false);		--interior

    _clim:setSeasonColorDawn(CLOUDY,WINTER,0.19,0.3,0.43,0.69,true);		--exterior
    _clim:setSeasonColorDawn(CLOUDY,WINTER,0.12,0.19,0.28,0.46,false);		--interior

    _clim:setSeasonColorDawn(WARM,SPRING,0.35,0.72,0.64,0.75,true);		--exterior
    _clim:setSeasonColorDawn(WARM,SPRING,0.04,0.33,0.26,0.28,false);		--interior

    _clim:setSeasonColorDawn(CLOUDY,SPRING,0.23,0.49,0.43,0.65,true);		--exterior
    _clim:setSeasonColorDawn(CLOUDY,SPRING,0.08,0.23,0.18,0.39,false);		--interior

    -- ###################### Day ######################
    _clim:setSeasonColorDay(WARM,SUMMER,0.9,0.85,0.65,0.8,true);		--exterior
    _clim:setSeasonColorDay(WARM,SUMMER,0.41,0.26,0.0,0.14,false);		--interior

    _clim:setSeasonColorDay(CLOUDY,SUMMER,0.6,0.51,0.6,0.8,true);		--exterior
    _clim:setSeasonColorDay(CLOUDY,SUMMER,0.25,0.19,0.26,0.28,false);		--interior

    _clim:setSeasonColorDay(WARM,FALL,0.95,0.68,0.48,0.8,true);		--exterior
    _clim:setSeasonColorDay(WARM,FALL,0.65,0.28,0.05,0.15,false);		--interior

    _clim:setSeasonColorDay(CLOUDY,FALL,0.6,0.4,0.5,0.8,true);		--exterior
    _clim:setSeasonColorDay(CLOUDY,FALL,0.4,0.23,0.31,0.3,false);		--interior

    _clim:setSeasonColorDay(WARM,WINTER,0.75,0.55,0.37,0.75,true);		--exterior
    _clim:setSeasonColorDay(WARM,WINTER,0.34,0.18,0.05,0.31,false);		--interior

    _clim:setSeasonColorDay(CLOUDY,WINTER,0.35,0.35,0.46,0.75,true);		--exterior
    _clim:setSeasonColorDay(CLOUDY,WINTER,0.15,0.16,0.26,0.32,false);		--interior

    _clim:setSeasonColorDay(WARM,SPRING,0.7,0.75,0.5,0.7,true);		--exterior
    _clim:setSeasonColorDay(WARM,SPRING,0.35,0.43,0.19,0.17,false);		--interior

    _clim:setSeasonColorDay(CLOUDY,SPRING,0.45,0.55,0.45,0.7,true);		--exterior
    _clim:setSeasonColorDay(CLOUDY,SPRING,0.11,0.17,0.11,0.2,false);		--interior

    -- ###################### Dusk ######################
    _clim:setSeasonColorDusk(WARM,SUMMER,0.93,0.3,0.1,0.85,true);		--exterior
    _clim:setSeasonColorDusk(WARM,SUMMER,0.39,0.14,0.02,0.52,false);		--interior

    _clim:setSeasonColorDusk(NORMAL,SUMMER,0.35,0.53,0.74,0.7,true);		--exterior
    _clim:setSeasonColorDusk(NORMAL,SUMMER,0.1,0.26,0.39,0.33,false);		--interior

    _clim:setSeasonColorDusk(CLOUDY,SUMMER,1.0,0.3,0.5,0.85,true);		--exterior
    _clim:setSeasonColorDusk(CLOUDY,SUMMER,0.48,0.1,0.21,0.39,false);		--interior

    _clim:setSeasonColorDusk(WARM,FALL,0.8,0.3,0.2,0.85,true);		--exterior
    _clim:setSeasonColorDusk(WARM,FALL,0.45,0.11,0.05,0.4,false);		--interior

    _clim:setSeasonColorDusk(NORMAL,FALL,0.45,0.35,0.55,0.7,true);		--exterior
    _clim:setSeasonColorDusk(NORMAL,FALL,0.2,0.14,0.25,0.41,false);		--interior

    _clim:setSeasonColorDusk(CLOUDY,FALL,0.8,0.3,0.5,0.75,true);		--exterior
    _clim:setSeasonColorDusk(CLOUDY,FALL,0.36,0.09,0.18,0.42,false);		--interior

    _clim:setSeasonColorDusk(WARM,WINTER,0.6,0.4,0.3,0.93,true);		--exterior
    _clim:setSeasonColorDusk(WARM,WINTER,0.21,0.1,0.07,0.44,false);		--interior

    _clim:setSeasonColorDusk(NORMAL,WINTER,0.3,0.4,0.54,0.85,true);		--exterior
    _clim:setSeasonColorDusk(NORMAL,WINTER,0.1,0.15,0.22,0.47,false);		--interior

    _clim:setSeasonColorDusk(CLOUDY,WINTER,0.6,0.3,0.4,0.9,true);		--exterior
    _clim:setSeasonColorDusk(CLOUDY,WINTER,0.32,0.12,0.19,0.53,false);		--interior

    _clim:setSeasonColorDusk(WARM,SPRING,0.7,0.4,0.3,0.95,true);		--exterior
    _clim:setSeasonColorDusk(WARM,SPRING,0.3,0.16,0.09,0.53,false);		--interior

    _clim:setSeasonColorDusk(NORMAL,SPRING,0.4,0.58,0.51,0.75,true);		--exterior
    _clim:setSeasonColorDusk(NORMAL,SPRING,0.1,0.22,0.18,0.44,false);		--interior

    _clim:setSeasonColorDusk(CLOUDY,SPRING,0.56,0.38,0.74,0.85,true);		--exterior
    _clim:setSeasonColorDusk(CLOUDY,SPRING,0.19,0.1,0.27,0.45,false);		--interior

end

Events.OnClimateManagerInit.Add(ClimateMain.onClimateManagerInit);
