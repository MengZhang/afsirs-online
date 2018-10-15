<script>
    function updateWthSB(soilSource) {
        var disabled = soilSource !== "MAP";
        disableNeareastOpt("et", disabled);
        disableNeareastOpt("rain", disabled);
    }

    function disableNeareastOpt(SBId, disabled) {
        var CB = document.getElementById(SBId + "_nearest_flg");
        CB.disabled = disabled;
        disableLocSB(SBId, CB);
    }

    function selectNeareast(SBId, checked) {
        var CB = document.getElementById(SBId + "_nearest_flg");
        CB.disabled = !checked;
        CB.checked = checked;
        var SB = document.getElementById(SBId + "_loc");
        SB.disabled = checked;
    }
    
    function calcDistance(lat, longi) {
        calcRainDistance(lat, longi);
        calcClimateDistance(lat, longi);
    }
    
    function calcRainDistance(lat, longi) {
        var SB = document.getElementById("rain_loc");
        for (i = 0; i < SB.options.length; i++) {
            var loc = SB.options[i].value;
            <#list rainfallCityList as rain_loc>
                <#if rain_loc['lat']?? && rain_loc['longi']??>
            if (loc === "${rain_loc['location']!}") {
                SB.options[i].innerHTML = loc + " (${rain_loc['startYear']?string['0']!} - ${rain_loc['endYear']?string['0']!})" + " -- (" + getDistanceFromLatLon(lat, longi, ${rain_loc['lat']!}, ${rain_loc['longi']!}) + " mi)";
            } else
                <#else>
            if (loc === "${rain_loc['location']!}") {
                SB.options[i].innerHTML = loc + " (${rain_loc['startYear']?string['0']!} - ${rain_loc['endYear']?string['0']!})";
            } else
                </#if>
            </#list>
            {
                SB.options[i].innerHTML = loc; // + " -- (N/A mi)"
            }
        }
    }
    
    function calcClimateDistance(lat, longi) {
        var SB = document.getElementById("et_loc");
        for (i = 0; i < SB.options.length; i++) {
            var loc = SB.options[i].value;
            <#list climateCityList as et_loc>
                <#if et_loc['lat']?? && et_loc['longi']??>
            if (loc === "${et_loc['location']!}") {
                SB.options[i].innerHTML = loc + " (${et_loc['startYear']?string['0']!} - ${et_loc['endYear']?string['0']!})" + " -- (" + getDistanceFromLatLon(lat, longi, ${et_loc['lat']!}, ${et_loc['longi']!}) + " mi)";
            } else
                <#else>
            if (loc === "${et_loc['location']!}") {
                SB.options[i].innerHTML = loc + " (${et_loc['startYear']?string['0']!} - ${et_loc['endYear']?string['0']!})";
            } else
                </#if>
            </#list>
            {
                SB.options[i].innerHTML = loc;
            }
        }
    }
    
    function getDistanceFromLatLon(lat1, lon1, lat2, lon2) {
        var R = 6371; // Radius of the earth in km
        var dLat = deg2rad(lat2-lat1);  // deg2rad below
        var dLon = deg2rad(lon2-lon1); 
        var a = 
            Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
            Math.sin(dLon/2) * Math.sin(dLon/2); 
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
        var d = R * c *0.621371; // Distance in mi
        return d.toFixed(2);
    }

    function deg2rad(deg) {
        return deg * (Math.PI/180)
    }
    
    function selectNearestLoc(SBId, CB) {
        var SB = document.getElementById(SBId + "_loc");
        if (CB.checked && !CB.disabled) {
            var miniDist = Number.MAX_VALUE;
            for (i = 0; i < SB.options.length; i++) {
                var locInfo = SB.options[i].innerHTML.split(" -- (");
                if (locInfo.length > 1) {
                    var distance = Number(locInfo[1].slice(0, -4));
                    if (miniDist > distance) {
                        SB.selectedIndex = i;
                        miniDist = distance;
                    }
                }
            }
        }
    }
    
    function disableLocSB(SBId, CB) {
        var SB = document.getElementById(SBId + "_loc");
        SB.disabled = CB.checked && !CB.disabled;
    }
    
    function validateClimate() {
        var checklist = ["et_loc", "rain_loc"];
        var ret = true;
        for (var i = 0; i < checklist.length; i++) {
            var comp = document.getElementById(checklist[i]);
            var errFlg = !comp.disabled && comp.selectedIndex < 0;
            if (errFlg) {
                showError(checklist[i], "Please provide " + comp.title, true);
                ret = false;
            } else {
                showError(checklist[i], "", false);
            }
        }
        return ret;
    }
    
    function revalidateSB(comp) {
        var errFlg = false;
        errFlg = !comp.disabled && comp.selectedIndex < 0;
        if (!errFlg) {
            showError(comp.id, "", false);
        }
    }
</script>
<div class="subcontainer">
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-3" for="et_loc">Evapotranspiration Location :</label>
            <div class="col-sm-6">
                <select id="et_loc" name="et_loc" class="form-control" onchange="revalidateSB(this)">
                    <#list climateCityList as et_loc>
                    <option value="${et_loc['location']!}" <#if permit['et_loc']?? && permit['et_loc'] == et_loc['location']>selected</#if>>${et_loc['location']!} (${et_loc['startYear']?string['0']!} - ${et_loc['endYear']?string["0"]!})</option>
                    </#list>
                </select>
            </div>
            <div id="et_locWarning" class="row col-sm-12 hidden">
                <div class="col-sm-3 text-left"></div>
                <div class="col-sm-9 text-left"><label id="et_locWarningMsg"></label></div>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-3" for="et_nearest_flg"></label>
            <div class="col-sm-6">
                <label class="form-check-label"><input id="et_nearest_flg" name="et_nearest_flg" type="checkbox" value="true"  class="form-check-input" onchange="disableLocSB('et', this);selectNearestLoc('et', this);" <#if permit['et_nearest_flg']?? && permit['et_nearest_flg'] == "true">checked</#if>>&nbsp; &nbsp; Check to use nearest station</label>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-3" for="rain_loc">Rainfall Location :</label>
            <div class="col-sm-6">
                <select id="rain_loc" name="rain_loc" class="form-control" onchange="revalidateSB(this)">
                    <#list rainfallCityList as rain_loc>
                    <option value="${rain_loc['location']!}" <#if permit['rain_loc']?? && permit['rain_loc'] == rain_loc['location']>selected</#if>>${rain_loc['location']!} (${rain_loc['startYear']?string['0']!} - ${rain_loc['endYear']?string['0']!})</option>
                    </#list>
                </select>
            </div>
            <div id="rain_locWarning" class="row col-sm-12 hidden">
                <div class="col-sm-3 text-left"></div>
                <div class="col-sm-9 text-left"><label id="rain_locWarningMsg"></label></div>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-3" for="rain_nearest_flg"></label>
            <div class="col-sm-6">
                <label class="form-check-label"><input id="rain_nearest_flg" name="rain_nearest_flg" type="checkbox" value="true"  class="form-check-input" onchange="disableLocSB('rain', this);selectNearestLoc('rain', this);" <#if permit['rain_nearest_flg']?? && permit['rain_nearest_flg'] == "true">checked</#if>>&nbsp; &nbsp; Check to use nearest station</label>
            </div>
        </div>
    </div>
    <br><br><br><br>
    <div class="text-center">
        <div>
            <button type="button" class="btn btn-primary text-left" onclick="openTab('SoilWater')">Back</button>&nbsp;&nbsp;&nbsp;
            <button type="button" class="btn btn-primary text-right" onclick="openTab('Decoef')">Next</button>
        </div>
    </div>
</div>