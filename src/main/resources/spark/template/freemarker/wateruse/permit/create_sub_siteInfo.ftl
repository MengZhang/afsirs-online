<script>
    function switchCropType(cropType) {
        hideComp("cropNameSB");
        if (cropType === "annual") {
            document.getElementById("cropNameAnnualSB").style.display = "block";
            document.getElementById("startMonthSB").disabled = false;
            document.getElementById("startDaySB").disabled = false;
            document.getElementById("endMonthSB").disabled = false;
            document.getElementById("endDaySB").disabled = false;
            changeIrrSysListByCrop("crop_name_annual");
        } else if (cropType === "perennial") {
            document.getElementById("cropNamePerennialSB").style.display = "block";
            document.getElementById("startMonthSB").disabled = true;
            document.getElementById("startDaySB").disabled = true;
            document.getElementById("endMonthSB").disabled = true;
            document.getElementById("endDaySB").disabled = true;
            changeIrrSysListByCrop("crop_name_perennial");
        }
        document.getElementById("coefficent_type_default").checked = true;
        switchCropData();
    }

    function switchMonthDayList(monthSBID, daySBID) {
        var x = document.getElementById(monthSBID).value;

        switch (x) {
            case "2":
                changeDayList(daySBID, 28);
                break;
            case "4":
            case "6":
            case "9":
            case "11":
                changeDayList(daySBID, 30);
                break;
            default:
                changeDayList(daySBID, 31);
                break;
        }
    }

    function changeDayList(daySBID, totalDays) {
        var select = document.getElementById(daySBID);
        var length = select.options.length;
        for (i = length - 1; i > totalDays; i--) {
            select.remove(i);
        }
        for (i = length; i <= totalDays; i++) {
            var option = document.createElement('option');
            option.innerHTML = i;
            option.value = i;
            select.append(option);
        }
    }

    function changeIrrSysListByCrop(cropTypeSBId) {
        var crop = document.getElementById(cropTypeSBId).value;
        var irrType = document.getElementById("irr_type");
        irrType.options[4].disabled = crop !== "NURSERY,CNTR.";
        irrType.options[7].disabled = crop !== "CITRUS";
        irrType.options[8].disabled = crop !== "RICE";
        
        if (crop !== "NURSERY,CNTR." && irrType.selectedIndex === 4) {
            irrType.selectedIndex = 0;
            setDefIrrParams();
        } else if (crop !== "CITRUS" && irrType.selectedIndex === 7) {
            irrType.selectedIndex = 0;
            setDefIrrParams();
        } else if (crop !== "RICE" && irrType.selectedIndex === 8) {
            irrType.selectedIndex = 0;
            setDefIrrParams();
        }
    }
</script>
<div class="subcontainer">
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-3" for="permit_id">Permit ID :</label>
            <div class="col-sm-6">
                <#if permit['permit_id']??>
                <input type="text" id="permit_id" class="form-control" value="${permit['permit_id']!}" placeholder="Enter Permit ID" data-toggle="tooltip" title="This field accepts alphanumeric characters without spaces" disabled>
                <input type="hidden" name="permit_id" value="${permit['permit_id']!}" >
                <#else>
                <input type="text" id="permit_id" name="permit_id" class="form-control" value="${permit['permit_id']!}" placeholder="Enter Permit ID" data-toggle="tooltip" title="This field accepts alphanumeric characters without spaces">
                </#if>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-3" for="owner_name">Owner Name :</label>
            <div class="col-sm-6">
                <input type="text" name="owner_name" class="form-control" value="${permit['owner_name']!}" placeholder="Enter Owner Name" data-toggle="tooltip" title="This field accepts alphanumeric characters without spaces">
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-3" for="crop_type">Crop :</label>
            <div class="row col-sm-6">
                <div class="col-sm-6">
                    <label><input type="radio" name="crop_type" id="crop_type_annual" class="form-control" value="annual" onclick="switchCropType('annual')" <#if permit['crop_type']?? && permit['crop_type'] == "annual">checked</#if>>Annual</label>
                </div>
                <div class="col-sm-6">
                    <label><input type="radio" name="crop_type" id="crop_type_perennial" class="form-control" value="perennial" onclick="switchCropType('perennial')" <#if permit['crop_type']?? && permit['crop_type'] == "perennial">checked</#if>>Perennial</label>
                </div>
            </div>
        </div>
        <div id="cropNameAnnualSB" class="form-group cropNameSB">
            <label class="control-label col-sm-3" for="crop_name_annual"></label>
            <div class="col-sm-6">
                <select id="crop_name_annual" name="crop_name_annual" class="form-control" onchange="changeIrrSysListByCrop('crop_name_annual')">
                    <#list cropListAnnual as cropName>
                    <option value="${cropName!}" <#if permit['crop_name']?? && permit['crop_name'] == cropName>selected</#if>>${cropName!}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div id="cropNamePerennialSB" class="form-group cropNameSB">
            <label class="control-label col-sm-3" for="crop_name_perennial"></label>
            <div class="col-sm-6">
                <select id="crop_name_perennial" name="crop_name_perennial" class="form-control" onchange="changeIrrSysListByCrop('crop_name_perennial')">
                    <#list cropListPerennial as cropName>
                    <option value="${cropName!}" <#if permit['crop_name']?? && permit['crop_name'] == cropName>selected</#if>>${cropName!}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div id="startDateSB" class="form-group">
            <label class="control-label col-sm-3" for="beg_date_month">Start Date :</label>
            <div class="row col-sm-6">
                <div class="col-sm-4">
                    <select id="startMonthSB"  name="beg_date_month" id="beg_date_month" class="form-control" onchange="switchMonthDayList('startMonthSB', 'startDaySB')">
                        <option value="0" >Month</option>
                        <#list ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'] as x>
                        <option value="${x?counter}" <#if permit['beg_date_month']?? && permit['beg_date_month']?number == x?counter>selected</#if>>${x!}</option>
                        </#list>
                    </select>
                </div>
                <div class="col-sm-4">
                    <select id="startDaySB" name="beg_date_day" id="beg_date_day" class="form-control">
                        <option value="0" >Day</option>
                        <#list 1..31 as x>
                        <option value="${x?counter}" <#if permit['beg_date_day']?? && permit['beg_date_day']?number == x?counter>selected</#if>>${x!}</option>
                        </#list>
                    </select>
                </div>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-3" for="end_date_month">End Date :</label>
            <div class="row col-sm-6">
                <div class="col-sm-4">
                    <select id="endMonthSB" name="end_date_month" class="form-control" onchange="switchMonthDayList('endMonthSB', 'endDaySB')">
                        <option value ="0">Month</option>
                        <#list ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'] as x>
                        <option value="${x?counter}" <#if permit['end_date_month']?? && permit['end_date_month']?number == x?counter>selected</#if>>${x!}</option>
                        </#list>
                    </select>
                </div>
                <div class="col-sm-4">
                    <select id="endDaySB" name="end_date_day" class="form-control">
                        <option value="0" >Day</option>
                        <#list 1..31 as x>
                        <option value="${x?counter}" <#if permit['end_date_day']?? && permit['end_date_day']?number == x?counter>selected</#if>>${x!}</option>
                        </#list>
                    </select>
                </div>
            </div>
        </div>
    </div>
    <br><br>
    <div class="text-center">
        <div>
            <button type="button" class="btn btn-primary text-right" onclick="openTab('Irrigation')">Next</button>
        </div>
    </div>
</div>