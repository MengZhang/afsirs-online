<!DOCTYPE html>
<html>
    <head>
        <#include "../header.ftl">
        <script src="https://www.w3schools.com/lib/w3.js"></script>

        <style>
            body {font-family: Arial;}

            /* Style the tab */
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
        </style>
        <!-- Required meta tags -->
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <!--<script src="/tether-1.3.3/src/js/tether.min.js" ></script>-->

        <script type="text/javascript">
            function clearMessage() {
                $('#shortUrlSpan').text("");
                $('#originalUrlSpan').text("");
                $('#blacklistUrlSpan').text("");
            }
            function shortUrl() {
                clearMessage();
                var posturl = 'http://localhost:8080/tinyurl/logicmonitor/url/encode';
                var request = $.ajax({
                    url: posturl,
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({"urlname": $('#originalUrl').val()})
                });
                request.done(function (data) {
                    $('#shortUrlSpan').text("Tiny Url : " + data);
                });
                request.fail(function (data) {
                    $('#shortUrlSpan').text(data.responseText);
                });
            }

            function originalUrl() {
                clearMessage();
                var geturl = 'http://localhost:8080/tinyurl/logicmonitor/url/decode?shorturl=' + $('#shortUrl').val();
                var request = $.ajax({
                    url: geturl,
                    type: 'GET'
                });
                request.done(function (data) {
                    $("#originalUrlSpan").text("Original Url : " + data);
                });
                request.fail(function (data) {
                    console.log(data);
                    $('#originalUrlSpan').text(data.responseText);
                });
            }

            function blackListUrl() {
                clearMessage();
                var isBlackListed = false;
                if ($("#isBlackListed").is(':checked'))
                    isBlackListed = true;
                var blacklistURL = 'http://localhost:8080/tinyurl/logicmonitor/url/blacklist';
                var request = $.ajax({
                    url: blacklistURL,
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({"url": $('#blacklistUrl').val(), "status": isBlackListed})
                });
                request.done(function (data) {
                    if (isBlackListed) {
                        $('#blacklistUrlSpan').text(data);
                    } else {
                        $('#blacklistUrlSpan').text(data);
                    }
                });
                request.fail(function (data) {
                    $('#blacklistUrlSpan').text(data.responseText);
                });
            }
        </script>
    </head>

    <body>

        <#include "../nav.ftl">
        <div class="container">
            <div class="jumbotron text-center">
                <h1>AFSIRS Online Version</h1>
                <h2>Revision Date : 07 SEP 2017</h2>
                <h3>AGRICULTURAL FIELD SCALE IRRIGATION REQUIREMENTS SIMULATION</h3><br>
            </div>
            <div class="tab">
                <button class="tablinks" onclick="openCity(event, 'Step1')" id= "step1">Step - 1</button>
                <button class="tablinks" onclick="openCity(event, 'Step2')" id = "step2">Step - 2</button>
                <button class="tablinks" onclick="openCity(event, 'Step3')" id = "step3">Step - 3</button>
            </div>

            <div id="Step1" class="tabcontent">
                <center>
                    <div class="subcontainer">
                        <div class="row">
                            <div class="row col-md-6 ">
                                <div class="label label-success font-weight-bold col-md-6 text-left">Permit ID </div>
                                <div class="col-md-6"><input type="text" id="permitID" placeholder="Enter Permit ID" data-toggle="tooltip" title="This field accepts alphanumeric characters without spaces"></div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Output File Name </div>
                                <div class="col-md-6"><input type="text" id="originalUrl" placeholder="Enter Output File Name" data-toggle="tooltip" title="This field accepts alphanumeric characters without spaces"></div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Evapotranspiration Location</div>
                                <div class="col-md-6"><select id="etlocation" style="width: 200px; height: 30px;">
                                        <option value="Orlando" selected>Orlando</option>
                                        <option value="Tampa">Tampa</option>
                                        <option value="Miami">Miami</option>
                                        <option value="Jacksonville">Jacksonville</option>
                                    </select>
                                </div><br><br>
                                <span id="stationSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Rainfall Location</div>
                                <div class="col-md-6"><select id="etlocation" style="width: 200px; height: 30px;">
                                        <option value="Orlando" selected>Orlando</option>
                                        <option value="Tampa">Tampa</option>
                                        <option value="Miami">Miami</option>
                                        <option value="Jacksonville">Jacksonville</option>
                                    </select>
                                </div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Planted Area(Acres)</div>
                                <div class="col-md-6"><input type="text" id="originalUrl" placeholder="Enter Planted Area" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Crop Type</div>
                                <div class="row col-md-6 radio">
                                    <div class="col-md-6">
                                        <label><input type="radio" name="croptype" >Annual</label>
                                    </div>
                                    <div class="col-md-6">
                                        <label><input type="radio" name="croptype" checked>Perennial</label>
                                    </div>
                                </div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Crop</div>
                                <div class="col-md-6"><select id="crop" style="width: 200px; height: 30px;">
                                        <option value="Orlando" selected>BEANS,GRN</option>
                                        <option value="Tampa">CORN,SWEET</option>
                                        <option value="Miami">BEANS,DRY</option>
                                        <option value="Jacksonville">AVOCADO</option>
                                    </select>
                                </div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Start Date</div>
                                <div class="col-md-6"><input type="date" id="startdate" placeholder="Crop start date" style="width: 200px; height: 30px;" data-toggle="tooltip" title="Select the crop start date"></div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">End Date</div>
                                <div class="col-md-6"><input type="date" id="startdate" placeholder="Crop start date" style="width: 200px; height: 30px;" data-toggle="tooltip" title="Select the crop end date"></div>
                            </div>
                            <div class="row col-md-6">
                                <div class="label label-success font-weight-bold col-md-6 text-left">Irrigation Type</div>
                                <div class="col-md-6"><select id="crop" style="width: 200px; height: 30px;">
                                        <option value="Orlando" selected>TRICKLE,DRIP</option>
                                        <option value="Tampa">SPRINKLER</option>
                                        <option value="Miami">SPRINKLER,WET</option>
                                        <option value="Jacksonville">TRICKLE,SPRAY</option>
                                    </select>
                                </div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Calculation Type</div>
                                <div class="row col-md-6 radio">
                                    <div class="col-md-6">
                                        <label><input type="radio" name="caltype" >Net</label>
                                    </div>
                                    <div class="col-md-6">
                                        <label><input type="radio" name="caltype" checked>Gross</label>
                                    </div>
                                </div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Define Irrigation water depths per application</div>
                                <div class="row col-md-6 radio text-left">
                                    <label><input type="radio" name="waterdepth" checked>Irrigate to field capacity</label>
                                    <label><input type="radio" name="waterdepth">Apply a fixed depth per application(>0.1)</label>
                                    <label><input type="radio" name="waterdepth">Deficit Irrigation application</label>
                                    <label><input type="radio" name="waterdepth">None</label>
                                </div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <label class="form-check-label" style="margin-right:5%"><input type="checkbox" id="isBlackListed" class="form-check-input">&nbsp; &nbsp; Check to use default values from IR.DAT</label>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Irrigation Application Efficiency</div>
                                <div class="col-md-6"><input type="text" id="originalUrl" placeholder="Irrigation Application Efficiency"></div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Fraction of soil surface irrigated </div>
                                <div class="col-md-6"><input type="text" id="originalUrl" placeholder="Fraction of soil surface irrigated"></div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Fraction of ET extracted from the irrigated zone </div>
                                <div class="col-md-6"><input type="text" id="originalUrl" placeholder="Fraction of ET extracted from the irrigated zone"></div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                            </div>
                        </div>
                        <br><br>
                    </div>
                </center>   
            </div>

            <div id="Step2" class="tabcontent">
                <center>
                    <div class="subcontainer">
                        <div class="row">
                            <div class="row col-md-12 ">
                                <div class="label label-success font-weight-bold col-md-6 text-left" >Depth of Water Table</div>
                                <div class="col-md-6 text-left"><input type="text" id="originalUrl" placeholder="Enter Depth of Water Table" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Soil Layer Characteristics </div><div class="col-md-6"></div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Input Data From</div>
                                <div class="row col-md-6 radio text-left">
                                    <div class="col-md-4 text-left">
                                        <label><input type="radio" name="croptype" >Soil Database</label>
                                    </div>
                                    <div class="col-md-4">
                                        <label><input type="radio" name="croptype">Soil Map</label>
                                    </div>
                                    <div class="col-md-4">
                                        <label><input type="radio" name="croptype">User Defined</label>
                                    </div>
                                </div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Selected Map</div>
                                <div class="col-md-6 text-left"><select id="etlocation" style="width: 200px; height: 30px;">
                                        <option value="Orlando" selected>Map1</option>
                                        <option value="Tampa">Map2</option>
                                        <option value="Miami">Map3</option>
                                        <option value="Jacksonville">Map4</option>
                                    </select>
                                </div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-4 text-left"><button type="submit" class="btn btn-primary" onclick="blackListUrl()">View/Edit Map</button></div>
                                <div class="label label-success font-weight-bold col-md-4 text-left"><button type="submit" class="btn btn-primary" onclick="blackListUrl()">Show Soil Data</button></div>
                                <div class="label label-success font-weight-bold col-md-4 text-left"><button type="submit" class="btn btn-primary" onclick="blackListUrl()">Refresh</button></div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="col-md-12"> 
                                    <object type="text/html" data="http://abe.ufl.edu/bmpmodel/arcGIS/Hiranava/index.html?site=test123&unit=test8&json=%7B%22rings%22%3A%5B%5B%5B-9074755.428262126%2C3243513.702381273%5D%2C%5B-9074774.537519196%2C3243499.370438469%5D%2C%5B-9074105.713521684%2C3243556.6982096843%5D%2C%5B-9073580.208952209%2C3243322.609810555%5D%2C%5B-9073699.641808908%2C3242840.101069492%5D%2C%5B-9073976.72603645%2C3242887.8742121714%5D%2C%5B-9074043.6084362%2C3242825.769126688%5D%2C%5B-9074225.146378383%2C3242887.8742121714%5D%2C%5B-9074497.453291656%2C3242744.5547841326%5D%2C%5B-9074870.083804555%2C3242758.886726937%5D%2C%5B-9074870.083804555%2C3243093.298725693%5D%2C%5B-9074760.205576394%2C3243313.055182019%5D%2C%5B-9074755.428262126%2C3243513.702381273%5D%5D%5D%2C%22spatialReference%22%3A%7B%22wkid%22%3A102100%2C%22latestWkid%22%3A3857%7D%7D#" width="800px" height="600px" style="overflow:auto;border:5px ridge blue">
                                    </object>
                                </div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Soil Type</div>
                                <div class="col-md-6 text-left"><select id="crop" style="width: 200px; height: 30px;">
                                        <option value="Orlando" selected>ADAMSVILLE FAR,FS</option>
                                        <option value="Tampa">ADAMSVILLE FAR,FS</option>
                                        <option value="Miami">ADAMSVILLE FAR,FS</option>
                                        <option value="Jacksonville">ADAMSVILLE FAR,FS</option>
                                    </select>
                                </div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Water Hold Capacity</div>
                                <div class="col-md-6 text-left"><select id="crop" style="width: 200px; height: 30px;">
                                        <option value="Orlando" selected>Average</option>
                                        <option value="Tampa">High</option>
                                        <option value="Miami">Low</option>
                                    </select>
                                </div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Soil Series Name</div>
                                <div class="col-md-6 text-left"><input type="text" id="soilSeriesName" placeholder="Enter Soil Series"></div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Soil Texture</div>
                                <div class="col-md-3 text-left"><select id="soilTexture" style="width: 200px; height: 30px;">
                                        <option value="Astatula" selected>Astatula</option>
                                        <option value="Candula">Candula</option>
                                        <option value="Astor">Astor</option>
                                    </select>
                                </div>
                                <div class="col-md-3 text-left"><input type="text" id="soilSeriesName" placeholder="Enter Soil Texture"></div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <div class="label label-success font-weight-bold col-md-6 text-left">Number of Soil Layers</div>
                                <div class="col-md-6 text-left"><select id="noSoilLayers" style="width: 200px; height: 30px;">
                                        <option value="1" selected>1</option>
                                        <option value="2">2</option>
                                        <option value="3">3</option>
                                    </select>
                                </div>
                                <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                <table class="table table-hover" contenteditable='true'>
                                    <thead>
                                        <tr>
                                            <th>Layer</th>
                                            <th>Depth from Soil Surface to bottom Layer(inches)</th>
                                            <th>Volumetric Water Content (0.01-0.90)</th>
                                            <th>WCU-WCL</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td>1</td>
                                            <td>3.149</td>
                                            <td>0.023</td>
                                            <td>0.024</td>
                                        </tr>
                                        <tr>
                                            <td>2</td>
                                            <td>79.921</td>
                                            <td>0.013</td>
                                            <td>0.015</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <br><br>
                        </center>
                    </div>

                    <div id="Step3" class="tabcontent">
                        <div class="subcontainer">
                            <div class="row">
                                <div class="row col-md-12 ">
                                    <div class="label label-success font-weight-bold col-md-12 text-left" >Irrigation System = MULTIPLE SPRINKLER</div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-12 text-left" >Crop Selected = BEANS, GRN</div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-12 text-left" >Entered depth of Water table = 60.0 inches</div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-6 text-left">Crop Data</div>
                                    <div class="row col-md-6 radio text-left">
                                        <div class="col-md-6">
                                            <label><input type="radio" name="croptype" checked>Default</label>
                                        </div>
                                        <div class="col-md-6">
                                            <label><input type="radio" name="croptype">Manual Input</label>
                                        </div>
                                    </div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-6 text-left" >Initial irrigated root zone depth</div>
                                    <div class="col-md-6 text-left"><input type="text" id="originalUrl" placeholder="Enter Depth of Water Table" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-6 text-left" >Maximum irrigated root zone depth</div>
                                    <div class="col-md-6 text-left"><input type="text" id="originalUrl" placeholder="Enter Depth of Water Table" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-12 text-left" >Monthly crop water use coefficients (0.0 - 2.0)</div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <table class="table table-hover table-bordered" contenteditable='true'>
                                        <thead>
                                            <tr>
                                                <th>Jan</th>
                                                <th>Feb</th>
                                                <th>Mar</th>
                                                <th>Apr</th>
                                                <th>Jun</th>
                                                <th>Jul</th>
                                                <th>Aug</th>
                                                <th>Sep</th>
                                                <th>Oct</th>
                                                <th>Nov</th>
                                                <th>Dec</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>1</td>
                                                <td>1.149</td>
                                                <td>0.023</td>
                                                <td>0.024</td>
                                                <td>1</td>
                                                <td>1.149</td>
                                                <td>0.023</td>
                                                <td>0.024</td>
                                                <td>1</td>
                                                <td>1.149</td>
                                                <td>0.023</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-12 text-left" >Allowable soil water depletions (0.0 - 1.0)</div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <table class="table table-hover table-bordered" contenteditable='true'>
                                        <thead>
                                            <tr>
                                                <th>Jan</th>
                                                <th>Feb</th>
                                                <th>Mar</th>
                                                <th>Apr</th>
                                                <th>Jun</th>
                                                <th>Jul</th>
                                                <th>Aug</th>
                                                <th>Sep</th>
                                                <th>Oct</th>
                                                <th>Nov</th>
                                                <th>Dec</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td>1</td>
                                                <td>0.149</td>
                                                <td>0.023</td>
                                                <td>0.024</td>
                                                <td>1</td>
                                                <td>0.149</td>
                                                <td>0.023</td>
                                                <td>0.024</td>
                                                <td>1</td>
                                                <td>0.149</td>
                                                <td>0.023</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-6 text-left" >Height of crown flood system bed (1.0 - 5.0)</div>
                                    <div class="col-md-6 text-left"><input type="text" id="originalUrl" placeholder="Enter height of crown flood system" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-12 text-left" >Crop water use coefficients for growth stages 3 and 4 </div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-3 text-left" >Stage 3</div>
                                    <div class="col-md-3 text-left"><input type="text" id="originalUrl" placeholder="Stage 3" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                    <div class="label label-success font-weight-bold col-md-3 text-left" >Stage 4</div>
                                    <div class="col-md-3 text-left"><input type="text" id="originalUrl" placeholder="Stage 4" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-12 text-left" >Fraction of Growing season for each stage</div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-3 text-left" >Stage 1</div>
                                    <div class="col-md-3 text-left"><input type="text" id="originalUrl" placeholder="Stage 1" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                    <div class="label label-success font-weight-bold col-md-3 text-left" >Stage 2</div>
                                    <div class="col-md-3 text-left"><input type="text" id="originalUrl" placeholder="Stage 2" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-3 text-left" >Stage 3</div>
                                    <div class="col-md-3 text-left"><input type="text" id="originalUrl" placeholder="Stage 3" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                    <div class="label label-success font-weight-bold col-md-3 text-left" >Stage 4</div>
                                    <div class="col-md-3 text-left"><input type="text" id="originalUrl" placeholder="Stage 4" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-12 text-left" >Allowable soil water depletions</div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-3 text-left" >Stage 1</div>
                                    <div class="col-md-3 text-left"><input type="text" id="originalUrl" placeholder="Stage 1" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                    <div class="label label-success font-weight-bold col-md-3 text-left" >Stage 2</div>
                                    <div class="col-md-3 text-left"><input type="text" id="originalUrl" placeholder="Stage 2" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                    <div class="label label-success font-weight-bold col-md-3 text-left" >Stage 3</div>
                                    <div class="col-md-3 text-left"><input type="text" id="originalUrl" placeholder="Stage 3" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                    <div class="label label-success font-weight-bold col-md-3 text-left" >Stage 4</div>
                                    <div class="col-md-3 text-left"><input type="text" id="originalUrl" placeholder="Stage 4" data-toggle="tooltip" title="This field accepts numeric values only"></div>
                                    <span id="shortUrlSpan" class="label label-success font-weight-bold"></span><br><br>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="jumbotron text-center">
                        <div class="btn btn-success text-right">
                             <button type="submit"  onclick="window.location.href='http://localhost:8080/RESTfulExample/page4.html'">Submit</button><br>
                        </div>
                    </div>

                    <script>
                        w3.includeHTML();
                    </script>

                    <script>
                        $(document).ready(function () {
                            $("#step1").trigger('click');
                        });
                    </script>

                    <script>
                        function openCity(evt, cityName) {
                            var i, tabcontent, tablinks;
                            tabcontent = document.getElementsByClassName("tabcontent");
                            for (i = 0; i < tabcontent.length; i++) {
                                tabcontent[i].style.display = "none";
                            }
                            tablinks = document.getElementsByClassName("tablinks");
                            for (i = 0; i < tablinks.length; i++) {
                                tablinks[i].className = tablinks[i].className.replace(" active", "");
                            }
                            document.getElementById(cityName).style.display = "block";
                            evt.currentTarget.className += " active";
                        }
                    </script>
            </div>

            <#include "../footer.ftl">
    </body>
</html>