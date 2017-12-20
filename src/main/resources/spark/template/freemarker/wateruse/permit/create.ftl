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
            switchBtns {
                display: none;
            }
            cropNameSB {
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
                    switchComp('cropNameAnnualSB', 'cropNameSB');
                } else if (document.getElementById("crop_type_perennial").checked) {
                    switchComp('cropNamePerennialSB', 'cropNameSB');
                }
            }

            function openTab(tabName) {
                var i, tabcontent, tablinks, switchBtns;
                tabcontent = document.getElementsByClassName("tabcontent");
                for (i = 0; i < tabcontent.length; i++) {
                    tabcontent[i].style.display = "none";
                }
                tablinks = document.getElementsByClassName("tablinks");
                for (i = 0; i < tablinks.length; i++) {
                    tablinks[i].className = tablinks[i].className.replace(" active", "");
                }
                switchBtns = document.getElementsByClassName("switchBtns");
                for (i = 0; i < tabcontent.length; i++) {
                    switchBtns[i].style.display = "none";
                }
                document.getElementById(tabName).style.display = "block";
                document.getElementById(tabName + "Btn").style.display = "block";
                document.getElementById(tabName + "Tab").className += " active";
            }

            function hideComp(switchClass) {
                var switchcontent = document.getElementsByClassName(switchClass);
                for (i = 0; i < switchcontent.length; i++) {
                    switchcontent[i].style.display = "none";
                }
            }

            function switchComp(compName, switchClass) {
                hideComp(switchClass);
                document.getElementById(compName).style.display = "block";
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
        </script>

    </head>
    <body>

        <#include "../../nav.ftl">

        <div class="container">
            <#if operation_result == "Failed" >
            <p>${error_message!"Permit Already Exist"}</p>
            </#if>

            <form id="createPermitForm" class="form-horizontal" method="post">
                <fieldset>
                    <legend>Create New Permit</legend>

                    <div class="tab">
                        <button type="button" class="tablinks active" onclick="openTab('SiteInfo')" id= "SiteInfoTab">General</button>
                        <button type="button" class="tablinks" onclick="openTab('Irrigation')" id = "IrrigationTab">Irrigation</button>
                        <button type="button" class="tablinks" onclick="openTab('SoilWater')" id = "SoilWaterTab">Soil</button>
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
                            <!--<#include "create_sub_soilWater.ftl">-->
                        </center>   
                    </div>
                    <div id="Decoef" class="tabcontent">
                        <center>
                            <!--<#include "create_sub_dcoef.ftl">-->
                        </center>   
                    </div>
                    <br><br>
                    <div class="text-center">
                        <div id="SiteInfoBtn" class="switchBtns">
                            <button type="button" class="btn btn-primary text-right" onclick="openTab('SoilWater')">Next</button>
                        </div>
                        <div id="IrrigationBtn" class="switchBtns">
                            <button type="button" class="btn btn-primary text-left" onclick="openTab('SiteInfo')">Back</button>&nbsp;&nbsp;&nbsp;
                            <button type="button" class="btn btn-primary text-right" onclick="openTab('SoilWater')">Next</button>
                        </div>
                        <div id="SoilWaterBtn" class="switchBtns">
                            <button type="button" class="btn btn-primary text-left" onclick="openTab('Irrigation')">Back</button>&nbsp;&nbsp;&nbsp;
                            <button type="button" class="btn btn-primary text-right" onclick="openTab('Decoef')">Next</button>
                        </div>
                        <div id="DecoefBtn" class="switchBtns">
                            <button type="button" class="btn btn-primary text-left" onclick="openTab('SoilWater')">Back</button>&nbsp;&nbsp;&nbsp;
                            <button type="submit" class="btn btn-primary text-right" value="Submit">Save</button>
                        </div>
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
