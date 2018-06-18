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
            var wsLocal;
            var alive = true;
            var port = "";
            if (location.port !== "") {
                port = ":" + location.port;
            }
            var wsAddrLocal = "wss://" + location.hostname + port + "/simulation/afsirs_wait";
            if (location.protocol === "http:") {
                wsAddrLocal = "ws://" + location.hostname + port + "/simulation/afsirs_wait";
            }

            function keepLocalConn() {
                wsLocal.onmessage = function (msg) {
                    processLocalMsg(msg);
                };
                wsLocal.onclose = function () {
                    if (alive) {
                        wsLocal = new WebSocket(wsAddrLocal);
                        keepLocalConn();
                        wsLocal.onopen = sendRenewRequest;
                    }
                };
            }

            function initConnect() {
                alive = true;
                wsLocal = new WebSocket(wsAddrLocal);
                wsLocal.onopen = sendLoginRequest;
                keepLocalConn();
            }

            //Send a message if it's not empty, then clear the input field
            function sendMessage(webSocket, message) {
                if (message !== "") {
                    webSocket.send(message);
                }
            }

            function sendLoginRequest() {
                var request = {
                    action : "Login",
                    user_id : "${user_id!}",
                    permit_id : "${permit_id!}"
                };
                sendMessage(wsLocal, JSON.stringify(request));
            }

            function sendRenewRequest() {
                var request = {
                    action : "Renew",
                    user_id : "${user_id!}",
                    permit_id : "${permit_id!}"
                };
                sendMessage(wsLocal, JSON.stringify(request));
            }

            function sendLogoutRequest() {
                var request = {
                    action : "Logout"
                };
                sendMessage(wsLocal, JSON.stringify(request));
            }

            function processLocalMsg(msg) {
                var data = JSON.parse(msg.data);
                var status = Number(data.status);
                if (data.action === "Login" &&  status === 200) {
                } else if (data.action === "Logout" &&  status === 200) {
                    wsLocal.close();
                } else if (data.action === "Renew" &&  status === 200) {
                } else if (data.action === "Simulation" &&  status === 200) {
                    showProgress(data.progress_pct);
                    if (data.progress_pct >= 100) {
                        sendLogoutRequest();
                        <#if currentUserRank?? && currentUserRank == "admin" >
                        window.location = "/simulation/afsirs?permit_id=${permit_id!}&user_id=${user_id!}";
                        <#else>
                        window.location = "/simulation/afsirs?permit_id=${permit_id!}";
                        </#if>
                        showInfoMsg("Loading result graphs...");
                    }
                } else if (status === 601) {
                    alive = false;
                    showErrMsg("Server connection lost, please refresh your page.");
                } else if (status === 602) {
                    alive = false;
                    if (document.getElementById("progressBar").style.width !== "100%") {
                       showErrMsg("Server connection lost, please refresh your page.");
                    }
                } else if (status === 900) {
                    alive = false;
                    showErrMsg("There is an error happened in your simulation, caused by [" + data.message + "]");
                }
            }
            
            function showProgress(progressVal){
                var pct = Number(progressVal) + "%";
                var progressBar = document.getElementById("progressBar");
                progressBar.innerHTML = pct;
                progressBar.style.width = pct;
                if (progressVal >= 100 || progressVal < 0) {
                    progressBar.classList.add("progress-bar-success");
                    $("#progressDiv").fadeOut("slow","linear");
                } else if (progressVal === 0) {
                    $("#progressDiv").fadeIn();
                    progressBar.classList.remove("progress-bar-success");
                }
            }
            
            function showErrMsg(msg) {
                var errorMsg = document.getElementById("errorMsg");
                errorMsg.innerHTML = "<span class='glyphicon glyphicon-remove-sign'></span> " + msg;
                errorMsg.className = "text-danger";
                document.getElementById("errorMsgDiv").style.display = "block";
            }
            
            function showInfoMsg(msg) {
                var errorMsg = document.getElementById("errorMsg");
                errorMsg.innerHTML = msg;
                errorMsg.className = "text-primary";
                document.getElementById("errorMsgDiv").style.display = "block";
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
                        <button type="button" class="tablinks active" id="IrrReqTab">Irrigation Requirement</button>
                        <button type="button" class="tablinks" id="2in10Tab">2-in-10</button>
                        <button type="button" class="tablinks" id="1in10Tab">1-in-10</button>
                        <button type="button" class="tablinks" id="WgtAvgTab">Weighted Avg</button>
                        <button type="button" class="tablinks" id="ClimateTab">Rain and ET</button>
                        <button type="button" class="tablinks" id="AllTab">All</button>
                    </div>
                    <br><br>
                    <div id="errorMsgDiv" class="row text-left col-sm-11 col-sm-push-1">
                        <label id="errorMsg"></label>
                    </div>
                    <div id="progressDiv" class="row col-sm-9 col-sm-push-1 progress">
                        <div id="progressBar" class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width:0%">0%</div>
                    </div>
                    <div class="row text-left col-sm-6 col-sm-push-6">
                        <img id="loadingImg" src="/images/loading.gif" alt="loading" style="position:relative; right:30%; top:60%;"/>
                    </div>
                </div>
                
            </fieldset>
        </div>
        <div class="row">
            <#if currentUserRank?? && currentUserRank == "admin" >
            <div class="text-right col-md-6">
            </div>
            <div class="text-right col-md-4">
                <div>
                    <button type="button" class="btn btn-primary text-right" onclick="window.location.href = '/wateruse/permit/list'">Back to list</button>
                    <button type="button" class="btn btn-primary text-right" onclick="window.location.href = '/wateruse/permit/find?permit_id=${permit_id!}&user_id=${user_id!}'">Edit Permit</button>
                </div>
            </div>
            <#else>
            <div class="text-right col-md-6">
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
                initConnect();
            });
        </script>

        <#include "../footer.ftl">
    </body>
</html>