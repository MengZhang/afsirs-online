<!DOCTYPE html>
<html>
    <head>
        <#include "../../header.ftl">
        <style>
            th, td {
                padding: 5px;
                text-align: left;
            }
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
                    switchCropType('annual');
                } else if (document.getElementById("crop_type_perennial").checked) {
                    switchCropType('perennial');
                }
                var irrType = document.getElementById("irr_type");
                irrType.options[4].disabled = true;
                irrType.options[7].disabled = true;
                irrType.options[8].disabled = true;
                hideComp("soilTypeSB");
                if (document.getElementById("soil_source_db").checked) {
                    switchSoilSource('DB');
                } else if (document.getElementById("soil_source_map").checked) {
                    switchSoilSource('MAP');
                } else {
                    document.getElementById("soil_source_map").checked = true;
                    switchSoilSource('MAP');
                    document.getElementById("water_hold_capacity").selectedIndex = 1;
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
            }

            function hideComp(switchClass) {
                var switchcontent = document.getElementsByClassName(switchClass);
                for (i = 0; i < switchcontent.length; i++) {
                    switchcontent[i].style.display = "none";
                }
            }
            
            function showValue(compId) {
                document.getElementById(compId+"_input").value = document.getElementById(compId).value;
            }
            
            function showRange(compId) {
                document.getElementById(compId).value = document.getElementById(compId+"_input").value;
            }
        </script>

    </head>
    <body>

        <#include "../../nav.ftl">

        <div class="container">
            <#if operation_result == "Failed" >
            <p>${error_message!"Permit Already Exist"}</p>
            </#if>

            <form id="createPermitForm" action="/wateruse/permit/create" class="form-horizontal" method="post">
                <fieldset>
                    <legend>Create New Permit</legend>

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
