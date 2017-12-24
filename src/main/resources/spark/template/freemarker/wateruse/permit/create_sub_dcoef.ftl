<div class="subcontainer">
    <div class="row">
<!--        <div class="row col-md-12 ">
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
        </div>-->
    </div>
    <div class="text-center">
        <div>
            <button type="button" class="btn btn-primary text-left" onclick="openTab('Climate')">Back</button>&nbsp;&nbsp;&nbsp;
            <button type="submit" class="btn btn-primary text-right" value="Submit">Save</button>
        </div>
    </div>
</div>