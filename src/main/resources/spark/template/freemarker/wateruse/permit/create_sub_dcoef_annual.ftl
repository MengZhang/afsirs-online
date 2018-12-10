<script>
    function setCropDefValAnnual(isDefault) {
        var crop = getSelectedText("crop_name_annual");
        if (isDefault) {
//         <#list cropDataAnnual?keys as cropName>
            if (crop === "${cropName}") {
                document.getElementById('dzn').value = "${cropDataAnnual[cropName]['DZN']}";
                document.getElementById('dzx').value = "${cropDataAnnual[cropName]['DZX']}";
                document.getElementById('akc3').value = "${cropDataAnnual[cropName]['AKC3']}";
                document.getElementById('akc4').value = "${cropDataAnnual[cropName]['AKC4']}";
                document.getElementById('f1').value = "${cropDataAnnual[cropName]['FR'][0]}";
                document.getElementById('f2').value = "${cropDataAnnual[cropName]['FR'][1]}";
                document.getElementById('f3').value = "${cropDataAnnual[cropName]['FR'][2]}";
                document.getElementById('f4').value = "${cropDataAnnual[cropName]['FR'][3]}";
                document.getElementById('ald1').value = "${cropDataAnnual[cropName]['ALD'][0]}";
                document.getElementById('ald2').value = "${cropDataAnnual[cropName]['ALD'][1]}";
                document.getElementById('ald3').value = "${cropDataAnnual[cropName]['ALD'][2]}";
                document.getElementById('ald4').value = "${cropDataAnnual[cropName]['ALD'][3]}";
            } else
//          </#list>
            {}
        }
        // Seepage irrigation
        if (Number(getSelectedValue("irr_type")) === 6) {
            var wtd = document.getElementById("water_table_depth").value;
            document.getElementById("dzn").value = wtd;
            document.getElementById("dzx").value = wtd;
            if (Number(document.getElementById('akc3').value) < 1.0 && isDefault) {
                document.getElementById('akc3').value = "1.0";
            }
            if (Number(document.getElementById('akc4').value) < 1.0 && isDefault) {
                document.getElementById('akc4').value = "1.0";
            }
        }
        document.getElementById('dzn').disabled = isDefault || Number(getSelectedValue("irr_type")) === 6;
        document.getElementById('dzx').disabled = isDefault || Number(getSelectedValue("irr_type")) === 6;
        document.getElementById('akc3').disabled = isDefault;
        document.getElementById('akc4').disabled = isDefault;
        document.getElementById('f1').disabled = isDefault;
        document.getElementById('f2').disabled = isDefault;
        document.getElementById('f3').disabled = isDefault;
        document.getElementById('f4').disabled = isDefault;
        document.getElementById('ald1').disabled = isDefault;
        document.getElementById('ald2').disabled = isDefault;
        document.getElementById('ald3').disabled = isDefault;
        document.getElementById('ald4').disabled = isDefault;
    }
    
    function checkAnnualCropInfo() {
        
        var f1 = Number(document.getElementById('f1').value);
        var f2 = Number(document.getElementById('f2').value);
        var f3 = Number(document.getElementById('f3').value);
        var f4 = Number(document.getElementById('f4').value);
        var sum = f1 + f2 + f3 +  f4;
        if (sum !== 1) {
            alert("The sum of Fraction of Growing Season for each stage must be 1!");
            return false;
        }

        if (Number(getSelectedValue("irr_type")) === 6) {
            var akc3 = Number(document.getElementById('akc3').value);
            var akc4 = Number(document.getElementById('akc4').value);
            if (akc3 < 1 || akc4 <1) {
                alert("Crop water use coefficients could not be less than 1 for Seepage irrigation!");
                return false;
            }
        }

        return true;
    }
</script>
<div id="decoef_annual">
    <hr>
    <div class="form-group">
        <label class="text-left col-sm-8 col-sm-push-1"><u>Irrigated Root Zone Depth</u></label>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-3" for="dzn">Initial :</label>
        <div class="col-sm-4">
            <input type="text" id="dzn" name="dzn" class="form-control" value="${permit['dzn']!}" placeholder="Enter Initial irrigated root zone depth" data-toggle="tooltip" title="Initial irrigated root zone depth">
        </div>
        <label class="control-label col-sm-1">(Inches)</label>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-3" for="dzx">Maximum :</label>
        <div class="col-sm-4">
            <input type="text" id="dzx" name="dzx" class="form-control" value="${permit['dzx']!}" placeholder="Enter Maximum irrigated root zone depth" data-toggle="tooltip" title="Maximum irrigated root zone depth">
        </div>
        <label class="control-label col-sm-1">(Inches)</label>
    </div>
    <hr>
    <div class="form-group">
        <label class="text-left col-sm-8 col-sm-push-1"><u>Crop water use coefficients</u></label>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-3" for="akc3">Growth Stage 3 :</label>
        <div class="col-sm-2">
            <input type="text" id="akc3" name="akc3" class="form-control" value="${permit['akc3']!}" placeholder="Coefficients for growth stage 3" data-toggle="tooltip" title="Crop water use coefficients for growth stage 3">
        </div>
        <label class="control-label col-sm-2" for="akc4">Growth Stage 4 :</label>
        <div class="col-sm-2">
            <input type="text" id="akc4" name="akc4" class="form-control" value="${permit['akc4']!}" placeholder="Coefficients for growth stage 4" data-toggle="tooltip" title="Crop water use coefficients for growth stage 4">
        </div>
    </div>
    <hr>
    <div class="form-group">
        <label class="text-left col-sm-8 col-sm-push-1"><u>Fraction of Growing Season</u></label>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-3" for="f1">Growth Stage 1 :</label>
        <div class="col-sm-2">
            <input type="text" id="f1" name="f1" class="form-control" value="${permit['f1']!}" placeholder="Fraction for growth stage 1" data-toggle="tooltip" title="Fraction of Growing season for growth stage 1">
        </div>
        <label class="control-label col-sm-2" for="f2">Growth Stage 2 :</label>
        <div class="col-sm-2">
            <input type="text" id="f2" name="f2" class="form-control" value="${permit['f2']!}" placeholder="Fraction for growth stage 2" data-toggle="tooltip" title="Fraction of Growing season for growth stage 2">
        </div>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-3" for="f3">Growth Stage 3 :</label>
        <div class="col-sm-2">
            <input type="text" id="f3" name="f3" class="form-control" value="${permit['f3']!}" placeholder="Fraction for growth stage 3" data-toggle="tooltip" title="Fraction of Growing season for growth stage 3">
        </div>
        <label class="control-label col-sm-2" for="f4">Growth Stage 4 :</label>
        <div class="col-sm-2">
            <input type="text" id="f4" name="f4" class="form-control" value="${permit['f4']!}" placeholder="Fraction for growth stage 4" data-toggle="tooltip" title="Fraction of Growing season for growth stage 4">
        </div>
    </div>
    <hr>
    <div class="form-group">
        <label class="text-left col-sm-8 col-sm-push-1"><u>Allowable Soil Water Depletions</u></label>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-3" for="ald1">Growth Stage 1 :</label>
        <div class="col-sm-2">
            <input type="text" id="ald1" name="ald1" class="form-control" value="${permit['ald1']!}" placeholder="Depletions for growth stage 1" data-toggle="tooltip" title="Allowable Soil Water Depletions for growth stage 1">
        </div>
        <label class="control-label col-sm-2" for="ald2">Growth Stage 2 :</label>
        <div class="col-sm-2">
            <input type="text" id="ald2" name="ald2" class="form-control" value="${permit['ald2']!}" placeholder="Depletions for growth stage 2" data-toggle="tooltip" title="Allowable Soil Water Depletions for growth stage 2">
        </div>
    </div>
    <div class="form-group">
        <label class="control-label col-sm-3" for="ald3">Growth Stage 3 :</label>
        <div class="col-sm-2">
            <input type="text" id="ald3" name="ald3" class="form-control" value="${permit['ald3']!}" placeholder="Depletions for growth stage 3" data-toggle="tooltip" title="Allowable Soil Water Depletions for growth stage 3">
        </div>
        <label class="control-label col-sm-2" for="ald4">Growth Stage 4 :</label>
        <div class="col-sm-2">
            <input type="text" id="ald4" name="ald4" class="form-control" value="${permit['ald4']!}" placeholder="Depletions for growth stage 4" data-toggle="tooltip" title="Allowable Soil Water Depletions for growth stage 4">
        </div>
    </div>
</div>