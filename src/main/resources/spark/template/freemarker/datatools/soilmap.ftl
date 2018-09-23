<!DOCTYPE html>
<html>
    <head>
        <#include "../header.ftl">
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
        <meta name="description" content="">
        <meta name="author" content="Meng Zhang@UF">
        <link rel="shortcut icon" href="/images/LOGO.png">
        <title>AFSIRS Soil Map</title>

        <!-- Bootstrap core CSS -->
        <!--<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">-->
        <link rel="stylesheet" href="https://js.arcgis.com/3.23/dijit/themes/claro/claro.css">
        <link rel="stylesheet" href="https://js.arcgis.com/3.23/esri/css/esri.css">

        <!-- Custom styles for this template -->
        <link rel="stylesheet" href="/stylesheets/soilmap/afsirs.css" media="screen">
        <link rel="stylesheet" href="/stylesheets/soilmap/bootstrapmap.css">

        <!-- File-saver-js -->
        <script src='https://s3-us-west-2.amazonaws.com/s.cdpn.io/14082/FileSaver.js'></script>

        <style type="text/css">
            #mapDiv {
                min-height: 50px;
            }
        </style>
    </head>

    <body class="claro">

        <#include "../nav.ftl">
        <div class="container-fluid main-container">

            <form id="createSoilForm" action="/datatools/soilmap/create" class="form-horizontal" method="post">
                <#if currentUserRank?? && currentUserRank == "admin" >
                <input type="hidden" id="user_id" name="user_id" value="${soilData['user_id']!}">
                <#else>
                <input type="hidden" id="user_id" name="user_id" value="${currentUser!}">
                </#if>
                <#if soilData["polygon_info"]?? >
                <input type="hidden" id="update_flg" name="update_flg" value="true">
                <#else>
                <input type="hidden" id="update_flg" name="update_flg" value="false">
                </#if>
                <input type="hidden" id="soil_unit_name" name="soil_unit_name" value="${soilData['soil_unit_name']!}">
                <input type="hidden" id="latitude" name="latitude" value="${soilData['latitude']!'28.3'}">
                <input type="hidden" id="longitude" name="longitude" value="${soilData['longitude']!'-81.5'}">
                <input type="hidden" id="polygon_info" name="polygon_info" value='${soilData["polygon_info"]!}'>
                <input type="hidden" id="planted_area" name="planted_area" value="${soilData['plantedArea']!}">
                <input type="hidden" id="total_area" name="total_area" value="${soilData['totalArea']!}">
                <input type="hidden" id="zoom" name="zoom" value="${zoom!}">
                <input type="hidden" id="checked_mukeys" name="checked_mukeys" value="${checked_mukeys!}">
                <fieldset>
                    <legend class="text-center">AFSIRS - Farm Area Selection Tool</legend>
                    <div class="row row-map">
                        <div class="col-sm-3 sidenav">
                            <div id="nav">
                                <div id="contentPane4" class="contentPane">
                                    <div id="buttonPane" class="text-left buttonPane contentPane" style="border:none">
                                        <button id="step2Button" type="button" class="btn btn-primary"><span class="glyphicon glyphicon-pencil"></span> Draw</button>
                                        <button id="calcButton" type="button" class="btn btn-primary" disabled><span class="glyphicon glyphicon-list-alt"></span> Query</button>
                                        <button id="clearButton" type="button" class="btn btn-primary"><span class="glyphicon glyphicon-remove"></span> Clear</button>
                                        <br/>
                                        <br/>
                                        <button id="saveButton" type="button" class="btn btn-primary" disabled><span class="glyphicon glyphicon-save"></span> Save</button>
                                        <button id="exportButton" type="button" class="btn btn-primary" disabled><span class="glyphicon glyphicon-save-file"></span> Export</button>
                                    </div>
                                </div>
                                <div id="messages">
                                    <h4>Alert</h4>
                                    Please select a polygon first.
                                </div>
                                <br>
                                <div id="progressDiv" class="progress" style="display:none">
                                    <div id="progressBar" class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width:0%">0%</div>
                                </div>
                                <hr>
                                <div id="queryRetInfo">
<!--                                    <div class="row">
                                        <label class="col-sm-1"></label>
                                        <label class="col-sm-4 text-right label-info">Permit ID :</label>
                                        <label class="col-sm-push-1 col-sm-5 text-left" id="permit_id_lb" style="border-style: inset" contentEditable="true" onclick="startInput(this, 'Enter permit ID')" onfocusout="endInput(this, 'Enter permit ID')">Enter permit ID</label>
                                        <span id="permit_id_edit_mark" class="col-sm-push-1 col-sm-1 text-left glyphicon glyphicon-edit"></span>
                                    </div>-->
                                    <div class="row">
                                        <label class="col-sm-1"></label>
                                        <label class="col-sm-4 text-right label-info">Map Name :</label>
                                        <label class="col-sm-push-1 col-sm-5 text-left" style="border-style: inset" id="map_name_lb" contentEditable="true" onclick="startInput(this, 'Enter map name')" onfocusout="endInput(this, 'Enter map name')">${soilData['soil_unit_name']!'Enter map name'}</label>
                                        <span id="map_name_edit_mark" class="col-sm-push-1 col-sm-1 text-left glyphicon glyphicon-edit"></span>
                                        <script>
                                            function startInput(label, defStr) {
                                                if (label.innerHTML === defStr) {
                                                    label.innerHTML = "";
                                                }
                                            }
                                            function endInput(label, defStr) {
                                                if (label.innerHTML === "") {
                                                    label.innerHTML = defStr;
                                                }
                                            }
                                        </script>
                                    </div>
                                    <div class="row">
                                        <label class="col-sm-1"></label>
                                        <label class="col-sm-4 text-right label-info">Site Area :</label>
                                        <label class="col-sm-push-1 col-sm-4 text-left" id="site_area_lb">${soilData['totalArea']!}</label>
                                        <label class="col-sm-push-1 col-sm-2 text-left">(acres)</label>
                                    </div>
                                </div>
                                <br>
                                <div id="queryRetTable" style="max-height:50vh;overflow-y:auto;display:none">
                                    <table id="tbl" class="table table-hover table-condensed text-center" >
                                        <thead>
                                            <tr class="info">
                                                <th>Select</th>
                                                <th><span data-toggle="tooltip" title="Soil Unit Symbol #">Symbol</span></th>
                                                <th><span data-toggle="tooltip" title="Soil Series Name">Soil Name</span></th>
                                                <th class=""><span data-toggle="tooltip" title="Map Unit Area (acres)" >Area</span></th>
                                                <th style="display:none;"><span data-toggle="tooltip" title="Map Unit Key">Mukey</span></th>
                                            </tr>
                                            <tr class="info">
                                                <td colspan="4" class="text-left"><button type="button" onclick="checkAll(this)" >Select All</button></td>
                                                <th style="display:none;"></th>
                                            </tr>
                                        </thead>
                                        <tbody id="tblBody">
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-7 text-left">
                            <div id="mapDiv" >

                                <div class="mapButtons" style="region: 'center'">
                                    <div id="mapprops" class="mapButtons">
                                        <div id="scale">
                                        </div>
                                        <div id="mousemove">
                                        </div>
                                    </div>
                                </div>
                                <!-- loading image on map--><img id="loadingImg" src="/images/loading.gif" alt="loading" style="position:absolute; right:50%; top:50%; z-index:100;"/>
                                <div style="position:absolute; right:20px; top:10px; z-Index:999;">
                                    <div id="basemapGalleryTP">
                                        <div id="basemapGalleryCP" style="width:380px; height:280px; overflow:auto;">
                                            <div id="basemapGallery"></div>
                                        </div>
                                    </div>
                                </div>
                                <div style="position:absolute; left:20px; bottom:10px; z-Index:999;">
                                    <div style="width:40px; height:40px; overflow:hidden;">
                                        <div id="HomeButton"></div>
                                    </div>
                                </div>
                                <div style="position:absolute; left:70px; bottom:10px; z-Index:999;">
                                    <div style="width:100px; height:35px; overflow:hidden;">
                                        <button id="progButtonNode" type="button" class="btn btn-primary"></button>
                                    </div>
                                </div>

                            </div>
                        </div>
                        <div class="col-sm-2 sidenav">
                            <div id="contentPane2" class="contentPane">
                                <legend>Steps to follow</legend>
                                <p class="text-left">
                                    &bull; <b>Find the farm area</b> on the map and zoom to appropriate level.
                                    <br>
                                    &bull; <b>Click on Draw button</b> to activate/deactivate drawing mode.
                                    <br>
                                    &bull; <b>Draw a polygon</b> around the farm area you want to evaluate.

                                    <br>
                                    &bull; <b>Wait until the polygon drawing process is done</b>. After that, the soil data table will show up in the left button of page .
                                    <br>
                                    &bull; <b>Click the Query button to refresh the data if necessary</b>.
                                    <br>
                                    &bull; <b>Select the soil types</b> you want to evaluate by checking the checkboxes. <i>(Any soil type which is less than 5% of total area will be <i>unchecked by default)</i>.
                                    <br>
                                    &bull; <b>Click the Save button</b> to save data into database, or
                                    <b>Click the Export button</b> to save file to download folder.
                                </p>
                                <button id="backButton" type="button" class="btn btn-primary" onclick="window.location.href = '/soildata/list'"><span class="glyphicon glyphicon-list-alt"></span> Back to List</button>
                            </div>
                        </div>
                    </div>
                </fieldset>
            </form>
            <hr/>
            <div id="footer" class="footer text-center">
                <p>
                    Disclaimer
                    | Privacy Policy
                    |
                    Copyright University of Florida
                    &bull; All Rights Reserved
                </p>
            </div>
        </div>

        <!-- Bootstrap core JavaScript
        ================================================== -->
        <!-- Placed at the end of the document so the pages load faster -->
        <!-- Bootstrap-map-js -->
        <script type="text/javascript">
//            var package_path = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/'));
            var dojoConfig = {
                // The location.pathname.replace() logic below may look confusing but all its doing is
                // enabling us to load the api from a CDN and load local modules from the correct location.
                async: true,
                //isDebug: true, parseOnLoad: true,
                packages: [{
                        name: "application",
                        location: '/js/soilmap'
                    }]
            };
        </script>
        <script src="https://js.arcgis.com/3.23"></script>

        <!--<script src='https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js'></script>-->
        <script type="text/javascript" src="/js/soilmap/afsirs.js"></script>
        <script type="text/javascript" src="/js/soilmap/knn.js"></script>
        <!--<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>-->
        <script>
            $(document).ready(function () {
                $('[data-toggle="tooltip"]').tooltip();
            });
        </script>
    </body>
</html>