<div class="subcontainer">
    <div class="row">
<!--        <div class="row col-md-12 ">
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
        </div>-->
    </div>
    <div class="text-center">
        <div>
            <button type="button" class="btn btn-primary text-left" onclick="openTab('Irrigation')">Back</button>&nbsp;&nbsp;&nbsp;
            <button type="button" class="btn btn-primary text-right" onclick="openTab('Decoef')">Next</button>
        </div>
    </div>
</div>