<div class="subcontainer">
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-2" for="permit_id">Permit ID :</label>
            <div class="col-sm-6">
                <#if permit['permit_id']??>
                <label class="control-label">${permit['permit_id']!}</label>
                <input type="hidden" name="permit_id" value="${permit['permit_id']!}" >
                <#else>
                <input type="text" name="permit_id" class="form-control" value="${permit['permit_id']!}" placeholder="Enter Permit ID" data-toggle="tooltip" title="This field accepts alphanumeric characters without spaces">
                </#if>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-2" for="owner_name">Owner Name :</label>
            <div class="col-sm-6">
                <input type="text" name="owner_name" class="form-control" value="${permit['owner_name']!}" placeholder="Enter Owner Name" data-toggle="tooltip" title="This field accepts alphanumeric characters without spaces">
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-2" for="crop_type">Crop :</label>
            <div class="row col-md-6">
                <div class="col-md-6">
                    <label><input type="radio" name="crop_type" id="crop_type_annual" class="form-control" value="annual" onclick="switchComp('cropNameAnnualSB', 'cropNameSB')" <#if permit['crop_type']?? && permit['crop_type'] == "annual">checked</#if>>Annual</label>
                </div>
                <div class="col-md-6">
                    <label><input type="radio" name="crop_type" id="crop_type_perennial" class="form-control" value="perennial" onclick="switchComp('cropNamePerennialSB', 'cropNameSB')" <#if permit['crop_type']?? && permit['crop_type'] == "perennial">checked</#if>>Perennial</label>
                </div>
            </div>
        </div>
        <div id="cropNameAnnualSB" class="form-group cropNameSB">
            <label class="control-label col-sm-2" for="crop_name_annual"></label>
            <div class="col-md-6">
                <select name="crop_name_annual" class="form-control">
                    <#list cropListAnnual as cropName>
                        <option value="${cropName!}" <#if permit['crop_name']?? && permit['crop_name'] == cropName>selected</#if>>${cropName!}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div id="cropNamePerennialSB" class="form-group cropNameSB">
            <label class="control-label col-sm-2" for="crop_name_perennial"></label>
            <div class="col-md-6">
                <select name="crop_name_perennial" class="form-control">
                    <#list cropListPerennial as cropName>
                        <option value="${cropName!}" <#if permit['crop_name']?? && permit['crop_name'] == cropName>selected</#if>>${cropName!}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-2" for="beg_date_month">Start Date :</label>
            <div class="row col-md-6">
                <div class="col-md-4">
                    <select name="beg_date_month" id="beg_date_month" class="form-control" onchange="switchMonthDayList('beg_date_month', 'beg_date_day')">
                        <option value="0" >Month</option>
                        <#list ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'] as x>
                        <option value="${x?counter}" <#if permit['beg_date_month']?? && permit['beg_date_month'] == x?counter>selected</#if>>${x!}</option>
                        </#list>
                    </select>
                </div>
                <div class="col-md-4">
                    <select name="beg_date_day" id="beg_date_day" class="form-control">
                        <option value="0" >Day</option>
                        <#list 1..31 as x>
                        <option value="${x?counter}" <#if permit['beg_date_day']?? && permit['beg_date_day'] == x?counter>selected</#if>>${x!}</option>
                        </#list>
                    </select>
                </div>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-2" for="end_date_month">End Date :</label>
            <div class="row col-md-6">
                <div class="col-md-4">
                    <select name="end_date_month" class="form-control">
                        <option value="0" >Month</option>
                        <#list ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'] as x>
                        <option value="${x?counter}" <#if permit['end_date_month']?? && permit['end_date_month'] == x?counter>selected</#if>>${x!}</option>
                        </#list>
                    </select>
                </div>
                <div class="col-md-4">
                    <select name="end_date_day" class="form-control">
                        <option value="0" >Day</option>
                        <#list 1..31 as x>
                        <option value="${x?counter}" <#if permit['end_date_day']?? && permit['end_date_day'] == x?counter>selected</#if>>${x!}</option>
                        </#list>
                    </select>
                </div>
            </div>
        </div>
    </div>
    <br><br>
</div>