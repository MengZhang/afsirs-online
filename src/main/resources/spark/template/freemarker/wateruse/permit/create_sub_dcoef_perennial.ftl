<script>
    function setCropDefValPerennial(isDefault) {
        var crop = getSelectedText("crop_name_perennial");
        var akcRow = document.getElementById("akc_row");
        var aldpRow = document.getElementById("aldp_row");
        if (isDefault) {
//         <#list cropDataPerennial?keys as cropName>
            if (crop === "${cropName}") {
                document.getElementById('drzirr').value = "${cropDataPerennial[cropName]['DRZIRR']}";
                document.getElementById('drztot').value = "${cropDataPerennial[cropName]['DRZTOT']}";
                var akcArr = [];
                var aldpArr = [];
//              <#list 0..11 as i>
                akcRow.cells[${i}].textContent = "${cropDataPerennial[cropName]['AKC'][i]}";
                aldpRow.cells[${i}].textContent = "${cropDataPerennial[cropName]['ALDP'][i]}";
                akcArr.push("${cropDataPerennial[cropName]['AKC'][i]}");
                aldpArr.push("${cropDataPerennial[cropName]['ALDP'][i]}");
//              </#list>
                document.getElementById("akc_arr").value = JSON.stringify(akcArr);
                document.getElementById("aldp_arr").value = JSON.stringify(aldpArr);
            } else
//          </#list>
            {}
        }
        
        document.getElementById('drzirr').disabled = isDefault;
        document.getElementById('drztot').disabled = isDefault;
        akcRow.contentEditable = !isDefault;
        aldpRow.contentEditable = !isDefault;
        
        activeHGT();
    }
    
    function activeHGT() {
        if (getSelectedText("irr_type") === "CROWN FLOOD (CITRUS)") {
            document.getElementById('hgt').disabled = false;
            document.getElementById('hgt_input').disabled = false;
        } else {
            document.getElementById('hgt').disabled = true;
            document.getElementById('hgt_input').disabled = true;
        }
    }
    
    function savePerennialCropInfo() {
        saveTableInput("akc");
        saveTableInput("aldp");
    }
    
    function saveTableInput(compId) {
        var row = document.getElementById(compId + "_row");
        var arr = [];
        for (i = 0; i < row.cells.length; i++) { 
            arr.push(row.cells[i].textContent);
        }
        document.getElementById(compId + "_arr").value = JSON.stringify(arr);
    }
    
    function adjustCoeffForCrownFloodIrr() {
        var hgt = Number(document.getElementById("hgt").value);
        var hgtInch = Math.round(hgt * 1200)/100;
        document.getElementById("water_table_depth").value = hgtInch;
        document.getElementById("water_table_depth_input").value = hgtInch;
        document.getElementById("drzirr").value = Math.round((hgtInch - 6.0)*100)/100;
        document.getElementById("drztot").value = Math.round (hgtInch * 100 + (1200 / hgt))/100;
        var row = document.getElementById("aldp_row");
        var arr = [];
        for (i = 0; i < row.cells.length; i++) {
            var aldp = Number(row.cells[i].textContent);
            if (aldp < 0.5) {
                row.cells[i].textContent = "0.5";
            }
            arr.push(row.cells[i].textContent);
        }
        document.getElementById("aldp_arr").value = JSON.stringify(arr);
        setDecoefLabels();
    }
</script>
<div id="decoef_perennial">
    <div class="form-group">
        <label class="control-label col-sm-3"><u>Root Zone Depth</u></label>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-3" for="drzirr">Irrigated :</label>
        <div class="col-sm-4">
            <input type="text" id="drzirr" name="drzirr" class="form-control" value="${permit['drzirr']!}" placeholder="Enter Irrigated root zone depth" data-toggle="tooltip" title="Irrigated root zone depth">
        </div>
        <label class="control-label col-sm-1">(Inches)</label>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-3" for="drztot">Crop Total :</label>
        <div class="col-sm-4">
            <input type="text" id="drztot" name="drztot" class="form-control" value="${permit['drztot']!}" placeholder="Enter total crop root zone depth" data-toggle="tooltip" title="total crop root zone depth">
        </div>
        <label class="control-label col-sm-1">(Inches)</label>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-5"><u>Monthly crop water use coefficients (0.0 - 2.0)</u></label>
    </div>
    <div class="form-group">
        <table class="table table-hover table-bordered" >
            <thead>
                <tr class="success">
                    <th>Jan</th>
                    <th>Feb</th>
                    <th>Mar</th>
                    <th>Apr</th>
                    <th>May</th>
                    <th>Jun</th>
                    <th>Jul</th>
                    <th>Aug</th>
                    <th>Sep</th>
                    <th>Oct</th>
                    <th>Nov</th>
                    <th>Dec</th>
                </tr>
            </thead>
            <tbody>
                <tr id="akc_row" contentEditable='true'>
                    <#if permit["akcArr"]?? && permit["akcArr"]?size == 12>
                    <#list permit["akcArr"] as akc>
                    <td>${akc}</td>
                    </#list>
                    <#else>
                    <#list 1..12 as i>
                    <td>?</td>
                    </#list>
                    </#if>
                </tr>
            </tbody>
        </table>
        <input type="hidden" id="akc_arr" name="akc_arr" value="">
    </div>
    <div class="form-group">
        <label class="control-label col-sm-5"><u>Allowable soil water depletions (0.0 - 1.0)</u></label>
    </div>
    <div class="form-group">
        <table class="table table-hover table-bordered">
            <thead>
                <tr class="success">
                    <th>Jan</th>
                    <th>Feb</th>
                    <th>Mar</th>
                    <th>Apr</th>
                    <th>May</th>
                    <th>Jun</th>
                    <th>Jul</th>
                    <th>Aug</th>
                    <th>Sep</th>
                    <th>Oct</th>
                    <th>Nov</th>
                    <th>Dec</th>
                </tr>
            </thead>
            <tbody>
                <tr id="aldp_row" contentEditable='true'>
                    <#if permit["aldpArr"]?? && permit["aldpArr"]?size == 12>
                    <#list permit["aldpArr"] as aldp>
                    <td>${aldp}</td>
                    </#list>
                    <#else>
                    <#list 1..12 as i>
                    <td>?</td>
                    </#list>
                    </#if>
                </tr>
            </tbody>
        </table>
        <input type="hidden" id="aldp_arr" name="aldp_arr" value="">
    </div>
    <div class="form-group">
        <label class="control-label col-sm-3"><u>Crown Flood System</u></label>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-3" for="hgt">Water Table Depth :</label>
        <div class="col-sm-4">
            <input type="range" id="hgt" name="hgt" step="0.01" max="5.0" min="1.0" class="form-control" value="${permit['hgt']!}" placeholder="Enter bed height of crown flood system" data-toggle="tooltip" title="Bed Height of crown flood system" onchange="showValue('hgt');adjustCoeffForCrownFloodIrr();">
        </div>
        <div class="col-sm-2">
            <input type="number" id="hgt_input" step="0.1" max="5.0" min="1.0" class="form-control" value="${permit['hgt']!}" placeholder="Enter bed height of crown flood system" data-toggle="tooltip" title="Bed Height of crown flood system" onchange="showRange('hgt');adjustCoeffForCrownFloodIrr();">
        </div>
        <label class="control-label col-sm-1">(Feet)</label>
    </div>
</div>