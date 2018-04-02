<!DOCTYPE html>
<html>
    <head>
        <#include "../header.ftl">
        <script src="https://code.highcharts.com/highcharts.js"></script>
        <script src="https://code.highcharts.com/modules/exporting.js"></script>
        <style>
            .bg-1 { 
                background-color: #1abc9c; /* Green */
                color: #ffffff;
            }
            .bg-2 { 
                background-color: #474e5d; /* Dark Blue */
                color: #ffffff;
            }
            .bg-3 { 
                background-color: #fff; /* White */
                color: #555555;
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
        </style>
        <script>

            function init() {
                
                var xhttp = new XMLHttpRequest();
                xhttp.onreadystatechange = function() {
                  if (this.readyState == 4 && this.status == 200) {
                    document.getElementById("demo").innerHTML =
                    this.responseText;
                  }
                };
                xhttp.open("GET", "ajax_info.txt", true);
                xhttp.send();
                
                openTab('IrrReq');
                drawIrrReqChart();
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
                document.getElementById(tabName + "Container").style.display = "block";
                document.getElementById(tabName + "Tab").className += " active";
            }

            function openAllTab() {
                tabcontent = document.getElementsByClassName("tabcontent");
                for (i = 0; i < tabcontent.length; i++) {
                    tabcontent[i].style.display = "block";
                }
                tablinks = document.getElementsByClassName("tablinks");
                for (i = 0; i < tablinks.length; i++) {
                    tablinks[i].className = tablinks[i].className.replace(" active", "");
                }
                document.getElementById("AllTab").className += " active";

                drawIrrReqChart();
                draw2in10Chart();
                draw1in10Chart();
                drawWgtAvgChart();
                drawClimateChart();
            }
        </script>
    </head>

    <body>
        <#include "../nav.ftl">
        <div class="container">
            <fieldset>
                <legend>AFSIRS Simulation Result</legend>
                <div class="row">
                    <div class="tab">
                        <button type="button" class="tablinks active" onclick="openTab('IrrReq');drawIrrReqChart();" id="IrrReqTab">Irrigation Requirement</button>
                        <button type="button" class="tablinks" onclick="openTab('2in10');draw2in10Chart();" id="2in10Tab">2-in-10</button>
                        <button type="button" class="tablinks" onclick="openTab('1in10');draw1in10Chart();" id="1in10Tab">1-in-10</button>
                        <button type="button" class="tablinks" onclick="openTab('WgtAvg');drawWgtAvgChart();" id="WgtAvgTab">Weighted Avg</button>
                        <button type="button" class="tablinks" onclick="openTab('Climate');drawClimateChart();" id="ClimateTab">Rain and ET</button>
                        <button type="button" class="tablinks" onclick="openAllTab()" id="AllTab">All</button>
                    </div>
                    <br><br>
                    <div id="progressDiv" class="row col-md-12 progress" style="display:none">
                        <div id="progressBar" class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width:0%">0%</div>
                    </div>
                    <div class="row col-md-12 tabcontent" id="IrrReqContainer" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
                    <div class="row col-md-12 tabcontent" id="2in10Container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
                    <div class="row col-md-12 tabcontent" id="1in10Container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
                    <div class="row col-md-12 tabcontent" id="WgtAvgContainer" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
                    <div class="row col-md-12 tabcontent" id="ClimateContainer" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
                </div>
            </fieldset>
        </div>
        <div class="row">
            <#if currentUserRank?? && currentUserRank == "admin" >
            <div class="text-right col-md-6">
                <div>
                    <button type="button" class="btn btn-success text-right" onclick="window.open('/simulation/afsirs_result?permit_id=${permit_id!}&user_id=${user_id!}&file_type=pdf')">Summary PDF</button>
                    <button type="button" class="btn btn-success text-right" onclick="window.location.href = '/simulation/afsirs_result?permit_id=${permit_id!}&user_id=${user_id!}&file_type=excel'">Summary EXCEL</button>
                    <button type="button" class="btn btn-success text-right" onclick="window.location.href = '/simulation/afsirs_result?permit_id=${permit_id!}&user_id=${user_id!}&file_type=calcExcel'">Calculation EXCEL</button>
                    <button type="button" class="btn btn-success text-right" onclick="window.location.href = '/simulation/afsirs_result?permit_id=${permit_id!}&user_id=${user_id!}&file_type=text'">Raw Text</button>
                </div>
            </div>
            <div class="text-right col-md-4">
                <div>
                    <button type="button" class="btn btn-primary text-right" onclick="window.location.href = '/wateruse/permit/list'">Back to list</button>
                    <button type="button" class="btn btn-primary text-right" onclick="window.location.href = '/wateruse/permit/find?permit_id=${permit_id!}&user_id=${user_id!}'">Edit Permit</button>
                </div>
            </div>
            <#else>
            <div class="text-right col-md-6">
                <div>
                    <button type="button" class="btn btn-success text-right" onclick="window.open('/simulation/afsirs_result?permit_id=${permit_id!}&file_type=pdf')">Summary PDF</button>
                    <button type="button" class="btn btn-success text-right" onclick="window.location.href = '/simulation/afsirs_result?permit_id=${permit_id!}&file_type=excel'">Summary EXCEL</button>
                    <button type="button" class="btn btn-success text-right" onclick="window.location.href = '/simulation/afsirs_result?permit_id=${permit_id!}&file_type=calcExcel'">Calculation EXCEL</button>
                    <button type="button" class="btn btn-success text-right" onclick="window.location.href = '/simulation/afsirs_result?permit_id=${permit_id!}&file_type=text'">Raw Text</button>
                </div>
            </div>
            <div class="text-right col-md-4">
                <div>
                    <button type="button" class="btn btn-primary text-right" onclick="window.location.href = '/wateruse/permit/list'">Back to list</button>
                    <button type="button" class="btn btn-primary text-right" onclick="window.location.href = '/wateruse/permit/find?permit_id=${permit_id!}'">Edit Permit</button>
                </div>
            </div>
            </#if>
        </div>
        <br><br>

        <script>
            $(document).ready(function () {
                init();
            });
        </script>

        <#include "../footer.ftl">
    </body>
</html>