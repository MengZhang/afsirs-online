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
            document.getElementById('soil_file_content').classList.add("hidden");
        } else {
            document.getElementById("total_area").disabled = false;
            var max = Number(document.getElementById('total_area').value);
            if (document.getElementById('planted_area').value > max) {
                document.getElementById('planted_area').value = max;
                document.getElementById('planted_area_input').value = max;
            }
            document.getElementById('planted_area').max = max;
            document.getElementById('planted_area_input').max = max;
            document.getElementById('soil_file_content').classList.remove("hidden");
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
                var plygonStr = JSON.stringify(obj["polygon"][0]);
                var afsirsInfo = obj["afsirs"];
                if (afsirsInfo === undefined) {
                    afsirsInfo = obj["asfirs"];
                }
                var totArea = Number(afsirsInfo[0].TotalArea);
                obj = cmToInch(obj);
                document.getElementById('soil_file_content').classList.remove("hidden");
                document.getElementById('tblBody').innerHTML = toTableHtml(obj["soils"]);
                document.getElementById('soil_file_json').value = JSON.stringify(obj);
                document.getElementById('polygon_info').value = plygonStr;
                document.getElementById('polygon_loc_info').value = JSON.stringify(afsirsInfo[0]);
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
    
    function cmToInch(data) {
        var soils = data["soils"];
        var version = data["version"];
        if (version === undefined) {
            version = 0;
        } else {
            version = Number(version);
        }
        for (var i = 0; i < soils.length; i++) {
            var layers = soils[i]["soilLayer"];
            for (var j = 0; j < layers.length; j++) {
                layers[j]["sllb"] = (Number(layers[j]["sllb"]) * 0.39370).toFixed(3);
                if (version < 1) {
                    layers[j]["slll"] = Number(layers[j]["slll"]) / 100;
                    layers[j]["sldul"] = Number(layers[j]["sldul"]) / 100;
                }
            }
        }
        return data;
    }
    
    function toTableHtml(soils) {
        if (soils.length === 0) {
            return "<tr><td colspan='8'>No Data</td></tr>";
        }
        
        var ret = "";
        var unitRowSpan = 0;
        var typeRowSpan = 0;
        var unitStr = [];
        var layerStr = [];
        var typeStr = [];
        var unitIdx = 0;
        var unitRow = 0;
        var typeIdx = 0;
        var typeRow = 0;
        var layerIdx = 0;
        var unitArea = 0;
        var mukey = soils[0]["mukey"];
        var cokey = soils[0]["cokey"];
        
        for (var i = 0; i < soils.length; i++) {
            
            if (soils[i]["mukey"] !== mukey) {
                unitStr[unitRow] = "<td rowspan='" + unitRowSpan + "'>" + soils[unitIdx]["musym"] + "</td><td rowspan='" + unitRowSpan + "'>" + soils[unitIdx]["mukeyName"] + "</td><td rowspan='" + unitRowSpan + "'>" + unitArea.toFixed(3) + "</td>";
                mukey = soils[i]["mukey"];
                unitIdx = i;
                unitRow = layerIdx;
                unitRowSpan = 0;
                unitArea = Number(soils[unitIdx]["compArea"]);
            } else {
                unitArea += Number(soils[unitIdx]["compArea"]); 
            }
            if (soils[i]["cokey"] !== cokey) {
                var typePct = soils[typeIdx]["comppct_r"];
                if (typePct === undefined) {
                    typePct = "";
                }
                typeStr[typeRow] = "<td rowspan='" + typeRowSpan + "'>" + soils[typeIdx]["soilName"] + "</td><td rowspan='" + typeRowSpan + "'>"+ typePct + "</td>";
                cokey = soils[i]["cokey"];
                typeIdx = i;
                typeRow = layerIdx;
                typeRowSpan = 0;
            }
            
            
            var layers = soils[i]["soilLayer"];
            for (var j = 0; j < layers.length; j++, layerIdx++) {
                layerStr[layerIdx] = "<td>" + Number(layers[j]["sllb"]).toFixed(1) + "</td><td>" + Number(layers[j]["slll"]).toFixed(3) + "</td><td>" + Number(layers[j]["sldul"]).toFixed(3) + "</td>";
                unitStr[layerIdx] = "";
                typeStr[layerIdx] = "";
                unitRowSpan++;
                typeRowSpan++;
            }
        }
        unitStr[unitRow] = "<td rowspan='" + unitRowSpan + "'>" + soils[unitIdx]["musym"] + "</td><td rowspan='" + unitRowSpan + "'>" + soils[unitIdx]["mukeyName"] + "</td><td rowspan='" + unitRowSpan + "'>" + unitArea.toFixed(3) + "</td>";
        typeStr[typeRow] = "<td rowspan='" + typeRowSpan + "'>" + soils[typeIdx]["soilName"] + "</td><td rowspan='" + typeRowSpan + "'>"+ typePct + "</td>";
        for (var i = 0; i < layerIdx; i++) {
            ret += "<tr>" + unitStr[i] + typeStr[i] + layerStr[i] + "</tr>"; 
        }
        return ret;
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
    
    function showDBSelection(pctArr) {
        var types = document.getElementById('soil_type_db').selectedOptions;
        var typeList = document.getElementById('selected_soil_type_db');
        var typeBar = document.getElementById('selected_soil_type_db_partition');
        typeList.innerHTML = "";
        typeBar.innerHTML = "";
        var avgPct = (100 / types.length).toFixed(1);
        for (var i = 0; i < types.length; i++) {
            var node = document.createElement("div");
            node.setAttribute("class", "form-group");
            var label = document.createElement("label");
            label.setAttribute("class", "control-label col-sm-4");
            label.innerHTML = types[i].label + " : ";
            
            var label2 = document.createElement("label");
            label2.setAttribute("class", "col-sm-1");
            label2.innerHTML = "(%)";
            
            var inputId = "db_soil_type_" + i;
            var range = document.createElement("div");
            range.setAttribute("class", "col-sm-4");
            var rangeInput = document.createElement("input");
            rangeInput.setAttribute("id", inputId);
            rangeInput.setAttribute("name", "db_soil_type_pct_range");
            rangeInput.setAttribute("type", "range");
            rangeInput.setAttribute("max", "100");
            rangeInput.setAttribute("min", "1");
            rangeInput.setAttribute("step", "1");
            rangeInput.setAttribute("class", "form-control");
            rangeInput.setAttribute("onchange", "showValue('" + inputId + "');updateSoilTypeBar('" + inputId + "', this);");
            range.appendChild(rangeInput);
            
            var number = document.createElement("div");
            number.setAttribute("class", "col-sm-2");
            var numberInput = document.createElement("input");
            numberInput.setAttribute("id", inputId + "_input");
            numberInput.setAttribute("name", "db_soil_type_pct");
            numberInput.setAttribute("type", "number");
            numberInput.setAttribute("max", "100");
            numberInput.setAttribute("min", "1");
            numberInput.setAttribute("step", "1");
            numberInput.setAttribute("class", "form-control");
            numberInput.setAttribute("onchange", "showRange('" + inputId + "');updateSoilTypeBar('" + inputId + "', this);");
            number.appendChild(numberInput);
            
            var bar = document.createElement("div");
            bar.setAttribute("id", inputId + "_bar");
            if (i % 2 === 0) {
                bar.setAttribute("class", "progress-bar progress-bar-success");
            } else {
                bar.setAttribute("class", "progress-bar progress-bar-info");
            }
            bar.setAttribute("role", "progressbar");
            bar.innerHTML = types[i].label;
            
            if (pctArr !== undefined && i < pctArr.length) {
                rangeInput.setAttribute("value", pctArr[i]);
                numberInput.setAttribute("value", pctArr[i]);
                bar.setAttribute("style", "width:" + pctArr[i] + "%");
            } else {
                rangeInput.setAttribute("value", avgPct);
                numberInput.setAttribute("value", avgPct);
                bar.setAttribute("style", "width:" + avgPct + "%");
            }
            
            node.appendChild(label);
            node.appendChild(range);
            node.appendChild(number);
            node.appendChild(label2);
            typeList.appendChild(node);
            typeBar.appendChild(bar);
        }
    }
    
    function updateSoilTypeBar(inputId, comp) {
        var bar = document.getElementById(inputId + "_bar");
        bar.setAttribute("style", "width:" + comp.value + "%");
        checkSoilTypeSum();
    }
    
    function checkSoilTypeSum() {
        var errFlg = false;
        var types = document.getElementById('soil_type_db').selectedOptions;
        var total = 0;
        for (var i = 0; i < types.length; i++) {
            var id = "db_soil_type_" + i + "_input";
            var input = document.getElementById(id);
            total += Number(input.value);
        }
        if (total.toFixed(0) > 100) {
            showError("soil_type_db", "Please adjust partition for soil type to make sure the sum is no more than 100", true);
            errFlg = true;
        } else {
            showError("soil_type_db", "", false);
        }
        return errFlg;
    }
    
    function validateSoilWater() {
        var errFlg = false;
        var jsonStr = document.getElementById('soil_file_json').value;
        if (jsonStr === "" || jsonStr === '{"soils":}') {
            showError("soil_file", "Please upload your soil file", true);
            errFlg = true;
        } else {
            showError("soil_file", "", false);
        }
        
        var area = document.getElementById('planted_area').value;
        if (area === "" || Number(area) < 0.001) {
            showError("planted_area", "Please provide the planted area (arces)", true);
            errFlg = true;
        } else {
            showError("planted_area", "", false);
        }
        
        var types = document.getElementById('soil_type_db').selectedOptions;
        if (types.length === 0) {
            showError("soil_type_db", "Please select one or more soil type", true);
            errFlg = true;
        } else {
            showError("soil_type_db", "", false);
        }
        
        if (checkSoilTypeSum()) {
            errFlg = true;
        }
        return !errFlg;
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
                <select id="soil_type_db" name="soil_type_db" class="form-control" onchange="showDBSelection();" title="Select soil types for simulation." multiple>
                    <#list soilDBNameList as soilName>
                    <option value="${soilName!}" <#if permit['dbSoilNames']?? && permit['dbSoilNames']?seq_contains(soilName)>selected</#if>>${soilName!}</option>
                    </#list>
                </select>
            </div>
            <div id="soil_type_dbWarning" class="row col-sm-12 hidden">
                <div class="col-sm-3 text-left"></div>
                <div class="col-sm-9 text-left"><label id="soil_type_dbWarningMsg"></label></div>
            </div>
            <div class="col-sm-9 col-sm-push-3 text-left">
                <label>Currently selected soil types:</label>
                <div id="selected_soil_type_db"></div>
                <div id="selected_soil_type_db_partition" class="progress"></div>
            </div>
        </div>
        <div id="soilTypeSB_MAP" class="form-group soilTypeSB">
            <label class="control-label col-sm-3" for="soil_file">Upload Soil File :</label>
            <div class="col-sm-5">
                <input type="file" id="soil_file" name="soil_file" class="form-control" value="" accept=".json" onchange="readFile()" placeholder="Browse Soil File (.json)" data-toggle="tooltip" title="Browse Soil File (.json)">
                <input type="hidden" id="soil_file_json" name="soil_file_json" value='{"soils":${permit["soil_json"]!"[]"}}'>
            </div>
            <div class="col-sm-4">
                <input type="hidden" id="polygon_info" name="polygon_info" value='${permit["polygon_info"]!}'>
                <input type="hidden" id="polygon_loc_info" name="polygon_loc_info" value='${permit["polygon_loc_info"]!}'>
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
            <label class="control-label col-sm-3" for="soil_unit_name">Soil Data Name :</label>
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
            <div id="planted_areaWarning" class="row col-sm-12 hidden">
                <div class="col-sm-3 text-left"></div>
                <div class="col-sm-9 text-left"><label id="planted_areaWarningMsg"></label></div>
            </div>
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
        <div id="soil_file_content" class="form-group">
            <label class="control-label col-sm-3" for="water_hold_capacity">Raw soil data :</label>
            <div id="soil_file_content_rawdata" class="col-sm-8 text-left" style="overflow-y:auto;max-height:300px;">
                <table id="tbl" class="table table-hover table-bordered table-condensed text-center" >
                    <thead class="text-center">
                        <tr class="info">
                            <th rowspan="2"><span data-toggle="tooltip" title="Soil Unit Symbol #">Symbol</span></th>
                            <th rowspan="2"><span data-toggle="tooltip" title="Soil Series Name">Soil Name</span></th>
                            <th rowspan="2"><span data-toggle="tooltip" title="Map Unit Area (acres)" >Area</span></th>
                            <th rowspan="2"><span data-toggle="tooltip" title="Soil Type Name">Soil Type</span></th>
                            <th rowspan="2"><span data-toggle="tooltip" title="Soil Type Percentage">Pct.</span></th>
                            <th colspan="3"><span data-toggle="tooltip" title="Soil layer info of a soil type">Soil layer info</span></th>
                        </tr>
                        <tr class="info">
                            <th style="width:10%"><span data-toggle="tooltip" title="Soil layer base depth(in)">SLLB</span></th>
                            <th style="width:10%"><span data-toggle="tooltip" title="Soil water, drained lower limit(in3/in3)">SLLL</span></th>
                            <th style="width:10%"><span data-toggle="tooltip" title="Soil water, drained upper limit(in3/in3)">SLDUL</span></th>
                        </tr>
                    </thead>
                    <tbody id="tblBody"></tbody>
                </table>
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