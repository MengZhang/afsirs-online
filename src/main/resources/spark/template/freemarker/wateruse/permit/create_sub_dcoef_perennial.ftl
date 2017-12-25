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
//              <#list 0..11 as i>
                akcRow.cells[${i}].textContent = "${cropDataPerennial[cropName]['AKC'][i]}";
                aldpRow.cells[${i}].textContent = "${cropDataPerennial[cropName]['ALDP'][i]}";
//              </#list>
            } else
//          </#list>
            {}
        }
        
        document.getElementById('drzirr').disabled = isDefault;
        document.getElementById('drztot').disabled = isDefault;
        akcRow.contentEditable = !isDefault;
        aldpRow.contentEditable = !isDefault;
        
        if (crop === "CITRUS" && getSelectedText("irr_type") === "CROWN FLOOD (CITRUS)") {
            document.getElementById('hgt').disabled = false;
        } else {
            document.getElementById('hgt').disabled = true;
        }
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
                <tr>
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
                    <#list 1..12 as i>
                    <td></td>
                    </#list>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-5"><u>Allowable soil water depletions (0.0 - 1.0)</u></label>
    </div>
    <div class="form-group">
        <table class="table table-hover table-bordered">
            <thead>
                <tr>
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
                    <#list 1..12 as i>
                    <td></td>
                    </#list>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-3"><u>Crown Flood System</u></label>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-3" for="hgt">Bed Height (1.0 - 5.0):</label>
        <div class="col-sm-4">
            <input type="text" id="hgt" name="hgt" class="form-control" value="${permit['hgt']!}" placeholder="Enter bed height of crown flood system" data-toggle="tooltip" title="Bed Height of crown flood system">
        </div>
        <label class="control-label col-sm-1">(Feet)</label>
    </div>
</div>