<script>
    function switchSoilSource(soilSource) {
        var i, soilTypeSB;
        soilTypeSB = document.getElementsByClassName("soilTypeSB");
        for (i = 0; i < soilTypeSB.length; i++) {
            soilTypeSB[i].style.display = "none";
        }
        document.getElementById("soilTypeSB_" + soilSource).style.display = "block";
        updateWthSB(soilSource);
    }

    function hideComp(switchClass) {
        var switchcontent = document.getElementsByClassName(switchClass);
        for (i = 0; i < switchcontent.length; i++) {
            switchcontent[i].style.display = "none";
        }
    }
    
    function readFile() {
        var file = document.getElementById("soil_file").value;
    }
</script>
<div class="subcontainer">
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-3" for="soil_source">Data Source :</label>
            <div class="col-md-6">
                <div class="col-md-4">
                    <label><input type="radio" name="soil_source" id="soil_source_db" class="form-control" value="DB" onclick="switchSoilSource('DB')" <#if permit['soil_source']?? && permit['soil_source'] == "DB">checked</#if>>Soil Database</label>
                </div>
                <div class="col-md-4">
                    <label><input type="radio" name="soil_source" id="soil_source_map" class="form-control" value="MAP" onclick="switchSoilSource('MAP')" <#if permit['soil_source']?? && permit['soil_source'] == "MAP">checked</#if>>Soil Map</label>
                </div>
                <div class="col-md-4">
                    <label><input type="radio" name="soil_source" id="soil_source_user" class="form-control" value="USER" onclick="switchSoilSource('USER')" <#if permit['soil_source']?? && permit['soil_source'] == "USER">checked</#if> disabled>User Defined</label>
                </div>
            </div>
        </div>
        <div id="soilTypeSB_DB" class="form-group soilTypeSB">
            <label class="control-label col-sm-3" for="soil_type_db">Soil Types :</label>
            <div class="col-md-5">
                <select id="crop_name_annual" name="soil_type_db" class="form-control" onchange="" title="Select soil types for simulation." multiple>
                    <#list soilDBNameList as soilName>
                    <option value="${soilName!}" <#if permit['soil_name']?? && permit['soil_name'].contains(soilName)>selected</#if>>${soilName!}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div id="soilTypeSB_MAP" class="form-group soilTypeSB">
            <label class="control-label col-sm-3" for="soil_file">Upload Soil File :</label>
            <div class="col-sm-5">
                <input type="file" id="soil_file" name="soil_file" class="form-control" value="" accept=".json" onchange="readFile()" placeholder="Browse Soil File (.json)" data-toggle="tooltip" title="Browse Soil File (.json)">
            </div>
            <div class="col-sm-4">
                <button type="button" class="btn btn-primary text-right" onclick="window.open('http://abe.ufl.edu/bmpmodel/arcGIS/Test/index_5CountyMerge.html')">View Soil Map</button>
                <button type="button" class="btn btn-primary text-right" onclick="" disabled>Show Soil Data</button>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-md-3" for="water_hold_capacity">Water Hold Capacity :</label>
            <div class="col-md-5">
                <select id="water_hold_capacity" name="water_hold_capacity" class="form-control" onchange="" title="Select Water Hold Capacity level">
                    <#list ['Minimum','Average','Maximum'] as x>
                    <option value="${x!}" <#if permit['water_hold_capacity']?? && permit['water_hold_capacity'] == x>selected</#if>>${x!}</option>
                    </#list>
                </select>
            </div>
        </div>
    </div>
    <div class="text-center">
        <div>
            <button type="button" class="btn btn-primary text-left" onclick="openTab('Irrigation')">Back</button>&nbsp;&nbsp;&nbsp;
            <button type="button" class="btn btn-primary text-right" onclick="openTab('Climate')">Next</button>
        </div>
    </div>
</div>