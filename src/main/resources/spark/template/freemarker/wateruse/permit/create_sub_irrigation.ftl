<script>
    
    function getIrSysList() {
        var irrSysList = [];
//      <#list irSysList as irSys>
        irrSysList.push({eff:"${irSys['eff']!}", arzi:"${irSys['area']!}", exir:"${irSys['ex']!}", dwt:"${irSys['dwt']!}"});
//      </#list>
        return irrSysList;
    }
    
    function setDefIrrParams() {
        var irrSysList = getIrSysList();
        var irrType = document.getElementById("irr_type").selectedIndex;
        if (document.getElementById("irr_option_net").checked) {
            document.getElementById("irr_efficiency").value = "1.0";
        } else if (document.getElementById("irr_option_gross").checked) {
            document.getElementById("irr_efficiency").value = irrSysList[irrType]["eff"];
        }
        document.getElementById("soil_surface_irr").value = irrSysList[irrType]["arzi"];
        document.getElementById("et_extracted").value = irrSysList[irrType]["exir"];
        document.getElementById("water_table_depth").value = irrSysList[irrType]["dwt"];
    }
    
    function changeIrrDepDefinition() {
        var irrDepType = document.getElementById("irr_depth_type");
        var irrDep = document.getElementById("irr_depth");
        if (irrDepType.value === "1") {
            irrDep.disabled = false;
            irrDep.placeholder = "inches, Depth of water to apply per irrigation (>= 0.1)";
            irrDep.title = "inches, Depth of water to apply per irrigation (>= 0.1)";
        } else if (irrDepType.value === "2") {
            irrDep.disabled = false;
            irrDep.placeholder = "%, of field capacity for deficit irrigation (50-100)";
            irrDep.title = "%, of field capacity for deficit irrigation (50-100)";
        } else {
            irrDep.disabled = true;
            irrDep.placeholder = "";
            irrDep.title = "";
        }
    }
</script>

<div class="subcontainer">
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-2" for="irr_type">Irrigation Type :</label>
            <div class="col-md-6">
                <select id="irr_type" name="irr_type" class="form-control" onchange="setDefIrrParams()" title="Select Irrigation System">
                    <#list irSysNameList as x>
                    <option value="${x?index}" <#if permit['irr_type']?? && permit['irr_type']?number == x?index>selected</#if>>${x!}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-2" for="irr_option">Calculation Type :</label>
            <div class="col-md-6">
                <div class="col-md-6">
                    <label><input type="radio" name="irr_option" id="irr_option_net" class="form-control" value="NET" onclick="setDefIrrParams()" <#if permit['irr_option']?? && permit['irr_option'] == "NET">checked</#if>>&nbsp;Net&nbsp;</label>
                </div>
                <div class="col-md-6">
                    <label><input type="radio" name="irr_option" id="irr_option_gross" class="form-control" value="GROSS" onclick="setDefIrrParams()" <#if permit['irr_option']?? && permit['irr_option'] == "GROSS">checked</#if>>Gross</label>
                </div>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-md-2" for="irr_depth_type">Irrigation water depths definition :</label>
            <div class="col-md-6">
                <select id="irr_depth_type" name="irr_depth_type" class="form-control" onchange="changeIrrDepDefinition();" title="Define Irrigation Water depths per application">
                    <#list ['Irrigate to field capacity','Apply a fixed depth per application(>0.1)','Deficit Irrigation application'] as x>
                    <option value="${x?index}" <#if permit['irr_depth_type']?? && permit['irr_depth_type']?number == x?index>selected</#if>>${x!}</option>
                    </#list>
                    <option value="-1" <#if permit['irr_depth_type']?? && permit['irr_depth_type']?number == -1>selected</#if>>None</option>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-md-2" for="irr_depth"></label>
            <div class="col-md-6">
                <#if permit['irr_depth_type']??>
                  <#if permit['irr_depth_type']?number == 1>
                <input type="text" id="irr_depth" name="irr_depth" class="form-control" value="${permit['irr_depth']!}" placeholder="inches, Depth of water to apply per irrigation (>= 0.1)" data-toggle="tooltip" title="inches, Depth of water to apply per irrigation (>= 0.1)">
                  <#elseif permit['irr_depth_type']?number == 2>
                <input type="text" id="irr_depth" name="irr_depth" class="form-control" value="" placeholder="%, of field capacity for deficit irrigation (50-100)" data-toggle="tooltip" title="%, of field capacity for deficit irrigation (50-100)" disabled>
                  <#else>
                <input type="text" id="irr_depth" name="irr_depth" class="form-control" value="" placeholder="" data-toggle="tooltip" title="" disabled>
                  </#if>
                <#else>
                <input type="text" id="irr_depth" name="irr_depth" class="form-control" value="" placeholder="" data-toggle="tooltip" title="" disabled>  
                </#if>
            </div>
        </div>
<!--        <div class="form-group">
            <label class="control-label col-md-1" for="ir_dat"></label>
            <div class="col-md-6">
                <label class="form-check-label"><input name="ir_dat" type="checkbox" value="true" id="isBlackListed" class="form-check-input" <#if permit['ir_dat']?? && permit['ir_dat'] == "true">checked</#if>>&nbsp; &nbsp; Check to use default values from IR.DAT</label>
            </div>
        </div>-->
        <div class="form-group">
            <label class="control-label col-sm-2" for="irr_efficiency">Efficiency :</label>
            <div class="col-sm-6">
                <input type="text" id="irr_efficiency" name="irr_efficiency" class="form-control" value="${permit['irr_efficiency']!'1.0'}" placeholder="Irrigation Application Efficiency" data-toggle="tooltip" title="">
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-2" for="soil_surface_irr">Surface fraction :</label>
            <div class="col-sm-6">
                <input type="text" id="soil_surface_irr" name="soil_surface_irr" class="form-control" value="${permit['soil_surface_irr']!'1.0'}" placeholder="Fraction of soil surface irrigated" data-toggle="tooltip" title="">
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-2" for="et_extracted">ET fraction :</label>
            <div class="col-sm-6">
                <input type="text" id="et_extracted" name="et_extracted" class="form-control" value="${permit['et_extracted']!'1.0'}" placeholder="Fraction of ET extracted from the irrigated zone" data-toggle="tooltip" title="">
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-2" for="water_table_depth">Water Table Depth:</label>
            <div class="col-sm-6">
                <input type="text" id="water_table_depth" name="water_table_depth" class="form-control" value="${permit['water_table_depth']!}" placeholder="Enter Depth of Water Table" data-toggle="tooltip" title="This field accepts numeric values only">
            </div>
        </div>
    </div>
    <div class="text-center">
        <div>
            <button type="button" class="btn btn-primary text-left" onclick="openTab('SiteInfo')">Back</button>&nbsp;&nbsp;&nbsp;
            <button type="button" class="btn btn-primary text-right" onclick="openTab('SoilWater')">Next</button>
        </div>
    </div>