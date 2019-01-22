<!DOCTYPE html>
<html>
    <head>
        <#include "../../header.ftl">
        <style>
/*            th, td {
                padding: 5px;
                text-align: left;
            }
            th {
                background-color: #4CAF50;
                color: white;
            }*/
            div.tab {
                overflow: hidden;
                border: 1px solid #ccc;
                background-color: #f1f1f1;
            }

            /* Style the buttons inside the tab */
            div.tab button {
                background-color: inherit;
                float: left;
                border: none;
                outline: none;
                cursor: pointer;
                padding: 14px 16px;
                transition: 0.3s;
                font-size: 17px;
            }

            /* Change background color of buttons on hover */
            div.tab button:hover {
                background-color: #ddd;
            }

            /* Create an active/current tablink class */
            div.tab button.active {
                background-color: #ccc;
            }

            /* Style the tab content */
            .tabcontent {
                display: none;
                padding: 6px 12px;
                border: 1px solid #ccc;
                border-top: none;
            }
            cropNameSB {
                display: none;
            }
            soilTypeSB {
                display: none;
            }
            cell {
                padding: 5px;
                text-align: left;
            }
        </style>

        <script>
            function init() {
                openTab('SiteInfo');
                hideComp("cropNameSB");
                if (document.getElementById("crop_type_annual").checked) {
                    switchCropType('annual', true);
                    setDateRange();
                } else if (document.getElementById("crop_type_perennial").checked) {
                    switchCropType('perennial', true);
                } else {
                    document.getElementById("crop_type_perennial").checked = true;
                    switchCropType('perennial', true);
                }
                hideComp("soilTypeSB");
                if (document.getElementById("soil_source_db").checked) {
                    switchSoilSource('DB');
                    var pcts = [<#if permit['plantedArea']??><#list permit['soils'] as soil>${soil['soilTypeArea'] / permit['plantedArea']?number * 100}<#sep>,</#sep></#list></#if>];
                    showDBSelection(pcts);
                } else if (document.getElementById("soil_source_map").checked) {
                    switchSoilSource('MAP');
                    <#if permit["latitude"]?? && permit["longitude"]??>
                    calcDistance(${permit["latitude"]!}, ${permit["longitude"]!});
                    </#if>
                    document.getElementById('tblBody').innerHTML = toTableHtml(JSON.parse(document.getElementById('soil_file_json').value)["soils"]);
                } else {
                    document.getElementById("soil_source_map").checked = true;
                    switchSoilSource('MAP');
                    document.getElementById("water_hold_capacity").selectedIndex = 1;
                    selectNeareast("et", true);
                    selectNeareast("rain", true);
                    document.getElementById('tblBody').innerHTML = toTableHtml(JSON.parse(document.getElementById('soil_file_json').value)["soils"]);
                }
            }

            function openTab(tabName) {
                var i, tabcontent, tablinks;
                tabcontent = document.getElementsByClassName("tabcontent");
                for (i = 0; i < tabcontent.length; i++) {
                    tabcontent[i].style.display = "none";
                }
                tablinks = document.getElementsByClassName("tablinks");
                for (i = 0; i < tablinks.length; i++) {
                    tablinks[i].className = tablinks[i].className.replace(" active", "");
                }
                document.getElementById(tabName).style.display = "block";
                document.getElementById(tabName + "Tab").className += " active";
                controlValidateInput(tabName);
                if (tabName === "Decoef") {
                    switchDecoef();
                    setDecoefLabels();
                }
            }

            function controlValidateInput(tabName) {
                var disableIrr = tabName !== "Irrigation";
                var disableSW = tabName !== "SoilWater";
                var disableCoef = tabName !== "Decoef";
                if (disableIrr) {
                    document.getElementById("irr_depth_input").disabled = disableIrr;
                } else {
                    changeIrrDepDefinition(true);
                }
                document.getElementById("irr_efficiency_input").disabled = disableIrr;
                document.getElementById("soil_surface_irr_input").disabled = disableIrr;
                document.getElementById("et_extracted_input").disabled = disableIrr;
                document.getElementById("water_table_depth_input").disabled = disableIrr;
                document.getElementById("planted_area_input").disabled = disableSW;
                if (document.getElementById("soil_source_db").checked) {
                    var dbSoilTypeRanges = document.getElementsByName("db_soil_type_pct_range");
                    for (var i = 0; i < dbSoilTypeRanges.length; i++) {
                        dbSoilTypeRanges[i].disabled = disableSW;
                    }
                }
                if (disableCoef) {
                    activeHGT();
                }
            }

            function hideComp(switchClass) {
                var switchcontent = document.getElementsByClassName(switchClass);
                for (i = 0; i < switchcontent.length; i++) {
                    switchcontent[i].style.display = "none";
                }
            }

            function showValue(compId) {
                document.getElementById(compId + "_input").value = document.getElementById(compId).value;
            }

            function showRange(compId) {
                document.getElementById(compId).value = document.getElementById(compId + "_input").value;
            }
            
            function collectData() {
                if (document.getElementById("crop_type_annual").checked) {
                    return checkAnnualCropInfo();
                } else if (document.getElementById("crop_type_perennial").checked) {
                    if (checkPerennialCropInfo()) {
                        savePerennialCropInfo();
                    } else {
                        return false;
                    }
                }
                return true;
            }
            
            function showWarning(id, msg, flg) {
                if (flg) {
                    document.getElementById(id + "Warning").classList.remove("hidden");
                    var warnMsg = document.getElementById(id + "WarningMsg");
                    warnMsg.innerHTML = "<span class='glyphicon glyphicon-exclamation-sign'></span> " + msg;
                    warnMsg.className = "text-warning";
                } else {
                    document.getElementById(id + "Warning").classList.add("hidden");
                }
            }
            
            function showError(id, msg, flg) {
                if (flg) {
                    document.getElementById(id + "Warning").classList.remove("hidden");
                    var warnMsg = document.getElementById(id + "WarningMsg");
                    warnMsg.innerHTML = "<span class='glyphicon glyphicon-remove-sign'></span> " + msg;
                    warnMsg.className = "text-danger";
                } else {
                    document.getElementById(id + "Warning").classList.add("hidden");
                }
            }
            
            function validateInput() {
                var errTab = "";
                if (!validateClimate()) {
                    errTab = "Climate";
                }
                if (!validateSoilWater()) {
                    errTab = "SoilWater";
                }
                if (!validateSiteInfo()) {
                    errTab = "SiteInfo";
                }
                if (errTab === "") {
                    if (collectData()) {
                        document.getElementById("createPermitForm").submit();
                    }
                } else {
                    openTab(errTab);
                }
            }
        </script>

    </head>
    <body>

        <#include "../../nav.ftl">

        <div class="container">
            <#if operation_result == "Failed" >
            <div class="alert alert-warning">${error_message!"Permit ID Already Exists"}</div>
            </#if>

            <form id="createPermitForm" action="/wateruse/permit/create" class="form-horizontal" method="post">
                <input type="hidden" name="update_flg" value="${update_flg!'false'}">
                <input type="hidden" name="user_id" value="${permit['user_id']!}">
                <fieldset>
                    <#if permit['permit_id']??>
                    <legend>Edit Permit - ${permit['permit_id']!'[Unknow]'}</legend>
                    <#else>
                    <legend>Create New Permit</legend>
                    </#if>

                    <div class="tab">
                        <button type="button" class="tablinks active" onclick="openTab('SiteInfo')" id= "SiteInfoTab">General</button>
                        <button type="button" class="tablinks" onclick="openTab('Irrigation')" id = "IrrigationTab">Irrigation</button>
                        <button type="button" class="tablinks" onclick="openTab('SoilWater')" id = "SoilWaterTab">Soil</button>
                        <button type="button" class="tablinks" onclick="openTab('Climate')" id = "ClimateTab">Climate</button>
                        <button type="button" class="tablinks" onclick="openTab('Decoef')" id = "DecoefTab">Coefficient</button>
                    </div>

                    <div id="SiteInfo" class="tabcontent">
                        <center>
                            <#include "create_sub_siteInfo.ftl">
                        </center>   
                    </div>
                    <div id="Irrigation" class="tabcontent">
                        <center>
                            <#include "create_sub_irrigation.ftl">
                        </center>   
                    </div>
                    <div id="SoilWater" class="tabcontent">
                        <center>
                            <#include "create_sub_soilWater.ftl">
                        </center>   
                    </div>
                    <div id="Climate" class="tabcontent">
                        <center>
                            <#include "create_sub_climate.ftl">
                        </center>   
                    </div>
                    <div id="Decoef" class="tabcontent">
                        <center>
                            <#include "create_sub_dcoef.ftl">
                        </center>   
                    </div>
                </fieldset>
            </form>
        </div>

        <script>
            $(document).ready(function () {
                init();
            });
        </script>

        <#include "../../footer.ftl">
    </body>
</html>
