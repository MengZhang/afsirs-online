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
                    <div class="row col-md-12 tabcontent" id="IrrReqContainer" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
                    <script>
                        function drawIrrReqChart() {
                            Highcharts.chart('IrrReqContainer', {
                                chart: {
                                    type: 'column'
                                },
                                title: {
                                    text: 'Irrigation Requirement(inches)'
                                },
                                subtitle: {
                                    text: 'Source: AFSIRS'
                                },
                                xAxis: {
                                    categories: [
                                        'Jan',
                                        'Feb',
                                        'Mar',
                                        'Apr',
                                        'May',
                                        'Jun',
                                        'Jul',
                                        'Aug',
                                        'Sep',
                                        'Oct',
                                        'Nov',
                                        'Dec'
                                    ],
                                    crosshair: true
                                },
                                yAxis: {
                                    min: 0,
                                    title: {
                                        text: 'Irrigation (inches)'
                                    }
                                },
                                tooltip: {
                                    headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                    pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                            '<td style="padding:0"><b>{point.y:.1f} inches</b></td></tr>',
                                    footerFormat: '</table>',
                                    shared: true,
                                    useHTML: true
                                },
                                plotOptions: {
                                    column: {
                                        pointPadding: 0.2,
                                        borderWidth: 0
                                    }
                                },
                                series: [
                                    <#list irrReqData?keys as name>
                                    {
                                        name: '${name}',
                                        data: [<#list irrReqData[name] as value>${value}<#sep>, </#sep></#list>]

                                    }<#sep>, </#sep>
                                    </#list>
                                ]
                            });
                        }
                    </script>
                    <div class="row col-md-12 tabcontent" id="2in10Container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
                    <script>
                        function draw2in10Chart() {
                            Highcharts.chart('2in10Container', {
                                chart: {
                                    type: 'column'
                                },
                                title: {
                                    text: '2-in-10(inches)'
                                },
                                subtitle: {
                                    text: 'Source: AFSIRS'
                                },
                                xAxis: {
                                    categories: [
                                        'Jan',
                                        'Feb',
                                        'Mar',
                                        'Apr',
                                        'May',
                                        'Jun',
                                        'Jul',
                                        'Aug',
                                        'Sep',
                                        'Oct',
                                        'Nov',
                                        'Dec'
                                    ],
                                    crosshair: true
                                },
                                yAxis: {
                                    min: 0,
                                    title: {
                                        text: 'Irrigation (inches)'
                                    }
                                },
                                tooltip: {
                                    headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                    pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                            '<td style="padding:0"><b>{point.y:.1f} inches</b></td></tr>',
                                    footerFormat: '</table>',
                                    shared: true,
                                    useHTML: true
                                },
                                plotOptions: {
                                    column: {
                                        pointPadding: 0.2,
                                        borderWidth: 0
                                    }
                                },
                                series: [
                                    <#list twoIn10Data?keys as name>
                                    {
                                        name: '${name}',
                                        data: [<#list twoIn10Data[name] as value>${value}<#sep>, </#sep></#list>]

                                    }<#sep>, </#sep>
                                    </#list>
                                ]
                            });
                        }
                    </script>
                    <div class="row col-md-12 tabcontent" id="1in10Container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
                    <script>
                        function draw1in10Chart() {
                            Highcharts.chart('1in10Container', {
                                chart: {
                                    type: 'column'
                                },
                                title: {
                                    text: '1-in-10(inches)'
                                },
                                subtitle: {
                                    text: 'Source: AFSIRS'
                                },
                                xAxis: {
                                    categories: [
                                        'Jan',
                                        'Feb',
                                        'Mar',
                                        'Apr',
                                        'May',
                                        'Jun',
                                        'Jul',
                                        'Aug',
                                        'Sep',
                                        'Oct',
                                        'Nov',
                                        'Dec'
                                    ],
                                    crosshair: true
                                },
                                yAxis: {
                                    min: 0,
                                    title: {
                                        text: 'Irrigation (inches)'
                                    }
                                },
                                tooltip: {
                                    headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                    pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                            '<td style="padding:0"><b>{point.y:.1f} inches</b></td></tr>',
                                    footerFormat: '</table>',
                                    shared: true,
                                    useHTML: true
                                },
                                plotOptions: {
                                    column: {
                                        pointPadding: 0.2,
                                        borderWidth: 0
                                    }
                                },
                                series: [
                                    <#list oneIn10Data?keys as name>
                                    {
                                        name: '${name}',
                                        data: [<#list oneIn10Data[name] as value>${value}<#sep>, </#sep></#list>]

                                    }<#sep>, </#sep>
                                    </#list>
                                ]
                            });
                        }
                    </script>
                    <div class="row col-md-12 tabcontent" id="WgtAvgContainer" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
                    <script>
                        function drawWgtAvgChart() {
                            Highcharts.chart('WgtAvgContainer', {
                                chart: {
                                    type: 'column'
                                },
                                title: {
                                    text: 'Weighted Average(inches)'
                                },
                                subtitle: {
                                    text: 'Source: AFSIRS'
                                },
                                xAxis: {
                                    categories: [
                                        'Jan',
                                        'Feb',
                                        'Mar',
                                        'Apr',
                                        'May',
                                        'Jun',
                                        'Jul',
                                        'Aug',
                                        'Sep',
                                        'Oct',
                                        'Nov',
                                        'Dec'
                                    ],
                                    crosshair: true
                                },
                                yAxis: {
                                    min: 0,
                                    title: {
                                        text: 'Irrigation'
                                    }
                                },
                                tooltip: {
                                    headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                    pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                            '<td style="padding:0"><b>{point.y:.1f} inches</b></td></tr>',
                                    footerFormat: '</table>',
                                    shared: true,
                                    useHTML: true
                                },
                                plotOptions: {
                                    column: {
                                        pointPadding: 0.2,
                                        borderWidth: 0
                                    }
                                },
                                series: [
                                    <#list wgtAvgData?keys as name>
                                    {
                                        name: '${name}',
                                        data: [<#list wgtAvgData[name] as value>${value}<#sep>, </#sep></#list>]

                                    }<#sep>, </#sep>
                                    </#list>
                                ]
                            });
                        }
                    </script>
                    <div class="row col-md-12 tabcontent" id="ClimateContainer" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
                    <script>
                        function drawClimateChart() {
                            Highcharts.chart('ClimateContainer', {
                                chart: {
                                    type: 'column'
                                },
                                title: {
                                    text: 'Rainfall (inches)'
                                },
                                subtitle: {
                                    text: 'Source: AFSIRS'
                                },
                                xAxis: {
                                    categories: [
                                        'Jan',
                                        'Feb',
                                        'Mar',
                                        'Apr',
                                        'May',
                                        'Jun',
                                        'Jul',
                                        'Aug',
                                        'Sep',
                                        'Oct',
                                        'Nov',
                                        'Dec'
                                    ],
                                    crosshair: true
                                },
                                yAxis: {
                                    min: 0,
                                    title: {
                                        text: 'Amount(inches)'
                                    }
                                },
                                tooltip: {
                                    headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
                                    pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                                            '<td style="padding:0"><b>{point.y:.1f} inches</b></td></tr>',
                                    footerFormat: '</table>',
                                    shared: true,
                                    useHTML: true
                                },
                                plotOptions: {
                                    column: {
                                        pointPadding: 0.2,
                                        borderWidth: 0
                                    }
                                },
                                series: [
                                    <#list climateData?keys as name>
                                    {
                                        name: '${name}',
                                        data: [<#list climateData[name] as value>${value}<#sep>, </#sep></#list>]

                                    }<#sep>, </#sep>
                                    </#list>
                                ]
                            });
                        }
                    </script>
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