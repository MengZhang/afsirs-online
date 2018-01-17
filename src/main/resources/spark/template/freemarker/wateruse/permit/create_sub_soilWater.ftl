<script>
    function switchSoilSource(soilSource) {
        var i, soilTypeSB;
        soilTypeSB = document.getElementsByClassName("soilTypeSB");
        for (i = 0; i < soilTypeSB.length; i++) {
            soilTypeSB[i].style.display = "none";
        }
        document.getElementById("soilTypeSB_" + soilSource).style.display = "block";
        if (soilSource !== "MAP") {
            document.getElementById("total_area").disabled = true;
            document.getElementById('planted_area').max = "1000";
            document.getElementById('planted_area_input').max = "1000";
            document.getElementById('soil_file_content').style.display = "none";
        } else {
            document.getElementById("total_area").disabled = false;
            var max = Number(document.getElementById('total_area').value);
            if (document.getElementById('planted_area').value > max) {
                document.getElementById('planted_area').value = max;
                document.getElementById('planted_area_input').value = max;
            }
            document.getElementById('planted_area').max = max;
            document.getElementById('planted_area_input').max = max;
            document.getElementById('soil_file_content').style.display = "block";
        }
        
        updateWthSB(soilSource);
    }

    function readFile() {
        var files = document.getElementById('soil_file').files;
        if (files.length !== 1) {
            alert('Please select one file!');
            return;
        }

        var file = files[0];
        var unitName = file.name.slice(0, -5);
        document.getElementById('soil_unit_name').value = unitName;
        var start = 0;
        var stop = file.size - 1;
        var reader = new FileReader();
        reader.onloadend = function (evt) {
            if (evt.target.readyState === FileReader.DONE) { // DONE == 2
                var jsonStr = evt.target.result;
                var obj = JSON.parse(jsonStr);
                var soilsStr = JSON.stringify(obj["soils"]);
                var plygonStr = JSON.stringify(obj["polygon"][0]);
                var afsirsInfo = obj["afsirs"];
                if (afsirsInfo === undefined) {
                    afsirsInfo = obj["asfirs"];
                }
                var totArea = Number(afsirsInfo[0].TotalArea);
                document.getElementById('soil_file_content').style.display = "block";
                document.getElementById('soil_file_content').textContent = soilsStr;
                document.getElementById('soil_file_json').value = jsonStr;
                document.getElementById('polygon_info').value = plygonStr;
//                var value = document.getElementById('planted_area_input').value;
                document.getElementById('total_area').value = totArea;
                document.getElementById('planted_area').max = totArea;
                document.getElementById('planted_area_input').max = totArea;
                //if (value === "" || value > totArea) {
                    document.getElementById('planted_area').value = totArea;
                    document.getElementById('planted_area_input').value = totArea;
                //}
                showError("soil_file", "", false);
            }
        };

        var blob = file.slice(start, stop + 1);
        reader.readAsBinaryString(blob);
    }
    
    function openSoilMap() {
        var base = document.getElementById('soil_map_url').value;
        var site = document.getElementById('permitId').value;
        var unit = document.getElementById('soil_unit_name').value;
        var json = document.getElementById('polygon_info').value;
        var totalArea = document.getElementById('total_area').value;
        var zoom = 9;
        if (totalArea !== "") {
            zoom = 17 - (Math.log(Number(totalArea))/Math.log(8)).toFixed(0);
            if (zoom > 16) {
                zoom = 16;
            } else if (zoom < 1) {
                zoom = 1;
            }
        }
        var url;
        if (json !== "") {
            if (base.indexOf("?") > 0) {
                base = base.substring(0, base.indexOf("?"));
            }
            url = base + "?site=" + site + "&unit=" + unit + "&zoom=" + zoom + "&json=" + encodeURIComponent(json);
        } else {
            if (base.indexOf("?") > 0) {
                url = base + "&site=" + site + "&unit=" + unit + "&zoom=" + zoom;
            } else {
                url = base + "?site=" + site + "&unit=" + unit + "&zoom=" + zoom;
            }
            
        }
        window.open(url);
    }
    
    function validateSoilWater() {
        var jsonStr = document.getElementById('soil_file_json').value;
        if (jsonStr === "" || jsonStr === '{"soils":}') {
            showError("soil_file", "Please upload your soil file", true);
            return false;
        } else {
            showError("soil_file", "", false);
            return true;
        }
    }
</script>
<div class="subcontainer">
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-3" for="soil_source">Data Source :</label>
            <div class="col-sm-6">
                <div class="col-sm-4">
                    <label><input type="radio" name="soil_source" id="soil_source_db" class="form-control" value="DB" onclick="switchSoilSource('DB')" <#if permit['soil_source']?? && permit['soil_source'] == "DB">checked</#if>>Soil Database</label>
                </div>
                <div class="col-sm-4">
                    <label><input type="radio" name="soil_source" id="soil_source_map" class="form-control" value="MAP" onclick="switchSoilSource('MAP')" <#if permit['soil_source']?? && permit['soil_source'] == "MAP">checked</#if>>Soil Map</label>
                </div>
                <div class="col-sm-4">
                    <label><input type="radio" name="soil_source" id="soil_source_user" class="form-control" value="USER" onclick="switchSoilSource('USER')" <#if permit['soil_source']?? && permit['soil_source'] == "USER">checked</#if> disabled>User Defined</label>
                </div>
            </div>
        </div>
        <div id="soilTypeSB_DB" class="form-group soilTypeSB">
            <label class="control-label col-sm-3" for="soil_type_db">Soil Types :</label>
            <div class="col-sm-5">
                <select id="crop_name_annual" name="soil_type_db" class="form-control" onchange="" title="Select soil types for simulation." multiple>
                    <#list soilDBNameList as soilName>
                    <option value="${soilName!}" <#if permit['dbSoilNames']?? && permit['dbSoilNames'].contains(soilName)>selected</#if>>${soilName!}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div id="soilTypeSB_MAP" class="form-group soilTypeSB">
            <label class="control-label col-sm-3" for="soil_file">Upload Soil File :</label>
            <div class="col-sm-5">
                <input type="file" id="soil_file" name="soil_file" class="form-control" value="" accept=".json" onchange="readFile()" placeholder="Browse Soil File (.json)" data-toggle="tooltip" title="Browse Soil File (.json)">
                <input type="hidden" id="soil_file_json" name="soil_file_json" value='{"soils":${permit["soil_json"]!}}'>
                
            </div>
            <div class="col-sm-4">
                <input type="hidden" id="polygon_info" value='${permit["polygon_info"]!}'>
                <input type="hidden" id="soil_map_url" value="${soil_map_url!'http://abe.ufl.edu/bmpmodel/arcGIS/Test'}">
                <button type="button" class="btn btn-primary text-right" onclick="openSoilMap()">View Soil Map</button>
                <button type="button" class="btn btn-primary text-right" onclick="" disabled>Show Soil Data</button>
            </div>
            <div id="soil_fileWarning" class="row col-sm-12 hidden">
                <div class="col-sm-3 text-left"></div>
                <div class="col-sm-9 text-left"><label id="soil_fileWarningMsg"></label></div>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-3" for="soil_unit_name">Soil Name :</label>
            <div class="col-sm-5">
                <input type="text" id="soil_unit_name" name="soil_unit_name" value="${permit['soil_unit_name']!'Unspecified'}" class="form-control" placeholder="Enter Soil Name" data-toggle="tooltip" title="This field accepts characters without spaces">
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-3" for="planted_area">Planted Area :</label>
            <div class="col-sm-4">
                <input type="range" id="planted_area" name="planted_area" step="0.001" max="${permit['plantedArea']!'1000'}" min="0.001" class="form-control" value="${permit['plantedArea']!}" placeholder="Enter Planted Area" data-toggle="tooltip" title="Planted Area" onchange="showValue('planted_area')">
                <input type="hidden" id="total_area" name="total_area" value="${permit['totalArea']!}">
            </div>
            <div class="col-sm-3">
                <input type="number" id="planted_area_input" step="10" max="${permit['plantedArea']!'1000'}" min="0.1" class="form-control" value="${permit['plantedArea']!}" placeholder="Enter Planted Area" data-toggle="tooltip" title="Planted Area" onchange="showRange('planted_area')">
            </div>
            <label class="control-label col-sm-1" for="planted_area">(Acres)</label>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-3" for="water_hold_capacity">Water Hold Capacity :</label>
            <div class="col-sm-5">
                <select id="water_hold_capacity" name="water_hold_capacity" class="form-control" onchange="" title="Select Water Hold Capacity level">
                    <#list ['Minimum','Average','Maximum'] as x>
                    <option value="${x!}" <#if permit['water_hold_capacity']?? && permit['water_hold_capacity'] == x>selected</#if>>${x!}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div id="soil_file_content" class="form-group">${permit['soil_json']!}</div>
    </div>
    <div class="text-center">
        <div>
            <button type="button" class="btn btn-primary text-left" onclick="openTab('Irrigation')">Back</button>&nbsp;&nbsp;&nbsp;
            <button type="button" class="btn btn-primary text-right" onclick="openTab('Climate')">Next</button>
        </div>
    </div>
</div>