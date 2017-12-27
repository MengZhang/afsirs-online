<script>
    function setDecoefLabels() {
        document.getElementById("irr_sys_label").innerHTML = getSelectedText("irr_type");
        if (document.getElementById("crop_type_annual").checked) {
            document.getElementById("crop_label").innerHTML = getSelectedText("crop_name_annual");
        } else if (document.getElementById("crop_type_perennial").checked) {
            document.getElementById("crop_label").innerHTML = getSelectedText("crop_name_perennial");
        }
        document.getElementById("water_table_depth_label").innerHTML = document.getElementById("water_table_depth").value;
    }
    
    function switchDecoef() {
        if (document.getElementById("crop_type_annual").checked) {
            document.getElementById("decoef_annual").style.display = "block";
            document.getElementById("decoef_perennial").style.display = "none";
            switchCropData();
        } else if (document.getElementById("crop_type_perennial").checked) {
            document.getElementById("decoef_annual").style.display = "none";
            document.getElementById("decoef_perennial").style.display = "block";
            switchCropData();
        } else {
            document.getElementById("decoef_annual").style.display = "none";
            document.getElementById("decoef_perennial").style.display = "none";
        }
    }
    
    function switchCropData() {
        var isDefault = document.getElementById("coefficent_type_default").checked;
        if (document.getElementById("crop_type_annual").checked) {
            setCropDefValAnnual(isDefault);
        } else if (document.getElementById("crop_type_perennial").checked) {
            setCropDefValPerennial(isDefault);
        } else {

        }
    }

    function getSelectedText(SBId) {
        var SB = document.getElementById(SBId);
        return SB.options[SB.selectedIndex].innerHTML;
    }
</script>
<div class="subcontainer">
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-3">Irrigation System :</label>
            <mark><label class="control-label col-sm-2" id="irr_sys_label"></label></mark>
            <label class="control-label col-sm-2">Water Table Depth :</label>
            <mark><label class="control-label col-sm-1" id="water_table_depth_label"></label></mark>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-3">Crop :</label>
            <mark><label class="control-label col-sm-2" id="crop_label"></label></mark>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-3" for="crop_type">Crop Data:</label>
            <div class="row col-sm-6">
                <div class="col-sm-4">
                    <label><input type="radio" name="coefficent_type" id="coefficent_type_default" class="form-control" value="default" onclick="switchCropData()" <#if !(permit['coefficent_type']??) || permit['coefficent_type'] == "default">checked</#if>>Default</label>
                </div>
                <div class="col-sm-4">
                    <label><input type="radio" name="coefficent_type" id="coefficent_type_mannual" class="form-control" value="mannual" onclick="switchCropData()" <#if permit['coefficent_type']?? && permit['coefficent_type'] == "mannual">checked</#if>>Mannual Input</label>
                </div>
            </div>
        </div>
        <#include "create_sub_dcoef_annual.ftl">
        <#include "create_sub_dcoef_perennial.ftl">
        <br><br>
        <div class="text-center">
            <div>
                <button type="button" class="btn btn-primary text-left" onclick="openTab('Climate')">Back</button>&nbsp;&nbsp;&nbsp;
                <button type="submit" class="btn btn-primary text-right" value="Submit">Save</button>
            </div>
        </div>
    </div>
</div>