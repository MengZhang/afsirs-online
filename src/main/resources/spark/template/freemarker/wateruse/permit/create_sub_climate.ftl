<script>
    function updateWthSB(soilSource) {
        var disabled = soilSource !== "MAP";
        disableNeareastOpt("et_loc", disabled);
        disableNeareastOpt("rain_loc", disabled);
    }

    function disableNeareastOpt(SBId, disabled) {
        var SB = document.getElementById(SBId);
        var nearestIdx = SB.length - 1;
        if (disabled && SB.selectedIndex === nearestIdx) {
            SB.selectedIndex = -1;
        } else if (!disabled && SB.selectedIndex < 0) {
            SB.selectedIndex = nearestIdx;
        }

        SB.options[nearestIdx].disabled = disabled;
    }

    function selectNeareast(SBId) {
        var SB = document.getElementById(SBId);
        SB.selectedIndex = SB.length - 1;
    }
</script>
<div class="subcontainer">
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-3" for="et_loc">Evapotranspiration Location :</label>
            <div class="col-sm-6">
                <select id="et_loc" name="et_loc" class="form-control">
                    <#list climateCityList as et_loc>
                    <option value="${et_loc!}" <#if permit['et_loc']?? && permit['et_loc'] == et_loc>selected</#if>>${et_loc!}</option>
                    </#list>
                    <option value='Nearest Station' disabled>Nearest Station</option>>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-3" for="rain_loc">Rainfall Location :</label>
            <div class="col-sm-6">
                <select id="rain_loc" name="rain_loc" class="form-control">
                    <#list rainfallCityList as rain_loc>
                    <option value="${rain_loc!}" <#if permit['rain_loc']?? && permit['rain_loc'] == rain_loc>selected</#if>>${rain_loc!}</option>
                    </#list>
                    <option value='Nearest Station' disabled>Nearest Station</option>>
                </select>
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