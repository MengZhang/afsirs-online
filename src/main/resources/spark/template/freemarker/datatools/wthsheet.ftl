<!DOCTYPE html>
<html>
    <head>
        <#include "../header.ftl">

        <link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/handsontable-pro@latest/dist/handsontable.full.min.css">
        <script src="https://cdn.jsdelivr.net/npm/handsontable@latest/dist/handsontable.full.min.js"></script>
    </head>

    <body>

        <#include "../nav.ftl">
        
        <div class="container">
            <div class="row">
                <div class="form-group">
                    <label class="text-right col-sm-3">Evapotranspiration Location :</label>
                    <label class="text-primary text-left col-sm-3">${et_loc!}</label>
                    <label class="text-right col-sm-2">Rainfall Location :</label>
                    <label class="text-primary text-left col-sm-3">${rain_loc!}</label>
                </div>
            </div>
            <br/>
            <div>
                <div id="plot" ></div>
            </div>
            <div style="overflow-y: auto;min-height: 600px">
                <div id="table" ></div>
            </div>
        </div>
        
        <#include "../footer.ftl">
    </body>
    <script>
        var dataObject = [
            <#list wthData as daily>
            {
                date: "${daily['date']!}",
                rain: ${daily['rain']!},
                et: ${daily['et']!}
            }<#sep>, </#sep>
            </#list>
        ];
        var hotElement = document.querySelector('#table');
//        var hotElementContainer = hotElement.parentNode;
        var hotSettings = {
            data: dataObject,
            columns: [
                {
                    data: 'date',
                    type: 'date',
                    dateFormat: 'YYYY-MM-DD'
                },
                {
                    data: 'et',
                    type: 'numeric',
                    numericFormat: {
                        pattern: '0.000'
                    }
                },
                {
                    data: 'rain',
                    type: 'numeric',
                    numericFormat: {
                        pattern: '0.000'
                    }
                }
            ],
            stretchH: 'all',
//                    width: 500,
            autoWrapRow: true,
//                    height: 450,
            maxRows: 365 * 30,
            manualRowResize: true,
            manualColumnResize: true,
            rowHeaders: true,
            colHeaders: [
                'Date',
                'ET (inches)',
                'RAIN (inches)'
            ],
            manualRowMove: true,
            manualColumnMove: true,
            contextMenu: true,
            filters: true,
            dropdownMenu: true
        };
        var hot = new Handsontable(hotElement, hotSettings);
    </script>
    
    <script>
        function drawWthPlot() {
            Highcharts.chart('plot', {
                chart: {
                    scrollablePlotArea: {
                        minWidth: 700
                    }
                },
                title: {
                    text: 'Daily Weather Data'
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
                    
                ]
            });
        }
    </script>
</html>