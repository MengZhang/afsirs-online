<div class="subcontainer">
    <div class="row">
        <div class="form-group">
            <label class="control-label col-sm-2" for="irr_type">Irrigation Type :</label>
            <div class="col-md-6">
                <select name="irr_type" class="form-control">
                    <#list irSysList as x>
                    <option value="${x?counter}" <#if permit['irr_type']?? && permit['irr_type']?number == x?counter>selected</#if>>${x!}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-2" for="irr_option">Calculation Type :</label>
            <div class="col-md-6">
                <div class="col-md-6">
                    <label><input type="radio" name="irr_option" id="irr_option_net" class="form-control" value="NET" onclick="" <#if permit['irr_option']?? && permit['irr_option'] == "NET">checked</#if>>&nbsp;Net&nbsp;</label>
                </div>
                <div class="col-md-6">
                    <label><input type="radio" name="irr_option" id="irr_option_gross" class="form-control" value="GROSS" onclick="" <#if permit['irr_option']?? && permit['irr_option'] == "GROSS">checked</#if>>Gross</label>
                </div>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-md-2" for="irr_depth_type">Irrigation water depths definition :</label>
            <div class="col-md-6">
                <select name="irr_depth_type" class="form-control">
                    <#list ['Irrigate to field capacity','Apply a fixed depth per application(>0.1)','Deficit Irrigation application','None'] as x>
                    <option value="${x?counter}" <#if permit['irr_depth_type']?? && permit['irr_depth_type']?number == x?counter>selected</#if>>${x!}</option>
                    </#list>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-md-1" for="ir_dat"></label>
            <div class="col-md-6">
                <label class="form-check-label"><input name="ir_dat" type="checkbox" value="true" id="isBlackListed" class="form-check-input">&nbsp; &nbsp; Check to use default values from IR.DAT</label>
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-2" for="irr_efficiency">Efficiency :</label>
            <div class="col-sm-6">
                <input type="text" name="irr_efficiency" class="form-control" value="${permit['irr_efficiency']!}" placeholder="Irrigation Application Efficiency" data-toggle="tooltip" title="">
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-2" for="soil_surface_irr">Surface fraction :</label>
            <div class="col-sm-6">
                <input type="text" name="soil_surface_irr" class="form-control" value="${permit['soil_surface_irr']!}" placeholder="Fraction of soil surface irrigated" data-toggle="tooltip" title="">
            </div>
        </div>
        <div class="form-group">
            <label class="control-label col-sm-2" for="et_extracted">ET fraction :</label>
            <div class="col-sm-6">
                <input type="text" name="et_extracted" class="form-control" value="${permit['et_extracted']!}" placeholder="Fraction of ET extracted from the irrigated zone" data-toggle="tooltip" title="">
            </div>
        </div>
    </div>
</div>