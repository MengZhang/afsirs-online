var map;
require([
    "esri/map",
    "application/bootstrapmap",
    "esri/toolbars/draw",
    "esri/toolbars/edit",
    "esri/tasks/geometry",
    "esri/tasks/query",
    "esri/geometry",
    "esri/layers/GraphicsLayer",
    "esri/layers/ArcGISDynamicMapServiceLayer",
    "esri/tasks/QueryTask",
    "esri/tasks/GeometryService",
    "esri/undoManager",
    "dojo/parser",
    "dijit/layout/BorderContainer",
    "dijit/layout/ContentPane",
    "dijit/form/Button",
    "dijit/form/TextBox",
    "dijit/Menu",
    "dijit/Dialog",
    "dojo/_base/connect", 
    "dijit/registry",
    "dijit/TitlePane",
    "dojo/data/ItemFileReadStore",
    "esri/dijit/InfoWindow", 
    "dojo/_base/html",
    "dijit/layout/TabContainer",
    "dojo/dom",
    "dojo/on",
    "esri/dijit/BasemapGallery",
    "esri/basemaps",
    "esri/dijit/HomeButton",
    "esri/geometry/Point",
    "dojo/domReady!"
], function (map,
    BootstrapMap,
    draw,
    edit,
    geometry,
    query,
    geometry1,
    GraphicsLayer,
    ArcGISDynamicMapServiceLayer,
    queryTask,
    geometryService,
    undoManager,
    parser,
    BorderContainer,
    ContentPane,
    Button,
    TextBox,
    Menu,
    Dialog, 
    connect, 
    registry,
    TitlePane,
    ItemFileReadStore,
    InfoWindow,
    html,
    TabContainer,
    dom,
    on,
    BasemapGallery,
    basemaps,
    HomeButton) {

    var isDeveloper = true;
    var ServicePATH = "http://ifs-arcgis-1.ad.ufl.edu:6080/arcgis/rest/services";
    var MapServer = ServicePATH + "/Soil_5Counties_Merge/MapServer/";
    var MapServer_Polk =  ServicePATH + "/Soil_Polk/MapServer/";
    var MapServer_Lake607 = ServicePATH + "/Soil_Lake607/MapServer/";
    var MapServer_Lake609 = ServicePATH + "/Soil_Lake609/MapServer/";
    var MapServer_Orange = ServicePATH + "/Soil_Orange/MapServer/";
    var MapServer_Highlands = ServicePATH + "/Soil_Highlands/MapServer/";
    var MapServer_Osceola = ServicePATH + "/Soil_Osceola/MapServer/";
    
    //var BaseMap = "http://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer";
    /*if(true)//!isDeveloper)
        BaseMap = "http://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer";
    else
        BaseMap = "http://services.arcgisonline.com/ArcGIS/rest/services/Canvas/World_Light_Gray_Base/MapServer";*/
    var undoManager;
    var toolbar, editToolbar;
    var soilGLayerSelected;
    var map;

    //layers
    var soilFeatures;
    var soilSelLayer;
    var soilGLayer;

    //other misc stuff
    var loadingImg;

    //queries
    var soilOverlapQuery;
    var soilContainsQuery;

    var chfrQuery;
    var chorQuery;
    var compQuery;
    var mapuQuery;

    //queryTasks
    var gQueryCTask, gQueryOTask;
    var dQueryCTask, dQueryOTask;

//    var chfrQueryTask;
    var chorQueryTask;
    var compQueryTask;
    var mapuQueryTask;
    
    //County Name
    var County = [];
    var getMukeyCheck = false;
    //var countyCounter = 0;
    var oldMukey = 0;
    var oldsldul =0.0;
    var oldsllb=0.0;
    var oldslll=0.0;
    //state variables
    var currentState;
    var StateEnum = {
        ZONESELECT : 1,
        WSSELECT : 2,
        RESET : 4
    };
    var polygonMode;
    var GeometryServicePATH = ServicePATH + "/Utilities/Geometry/GeometryServer";
    var geometryService;
    var geometryProcessor;

    //area
    var polyArea;
    var soilArea;

    //infowindow
    var currentClick;


    //parameters
    var url = window.location.href;
    var fileName;
    var dateName;
    var unit;

    var buttonToggle = false;
    //center of polygon
    var centroid;
    //JSON file of Polygon
    var polyJSON;
  /*undoManager = new undoManager();
    dojo.connect(undoManager, "onChange", function(){
        //enable or disable buttons depending on current state of application
        if (undoManager.canUndo) {
            dijit.byId("undo").set("disabled", false);
            dijit.byId("undo").set("iconClass", "undoIcon");
        }
        else {
            dijit.byId("undo").set("disabled", true);
            dijit.byId("undo").set("iconClass", "undoGrayIcon");
        }

        if (undoManager.canRedo) {
            dijit.byId("redo").set("disabled", false);
            dijit.byId("redo").set("iconClass", "redoIcon");
        }
        else {
            dijit.byId("redo").set("disabled", true);
            dijit.byId("redo").set("iconClass", "redoGrayIcon");
        }
    });*/

    //proxy url
    esri.config.defaults.io.proxyUrl = "http://abe.ufl.edu/bmpmodel/Shivam/v3_shivam/proxy.php";
    esri.config.defaults.io.alwaysUseProxy = false;

    //getParameters
    getParams(url);
    
    var mapDiv = new ContentPane({
        region: "center",
        }, "mapDiv");
    mapDiv.startup();
    
    //adding map to the map div
    var map = BootstrapMap.create("mapDiv", {
                    basemap: "hybrid",
                    center: [-81.7, 28.4],
                    sliderPosition: "top-left",
                    sliderStyle: "large",
                    visible: true,
                    autoResize: true,
                    zoom: 7,
                    scrollWheelZoom: true
                });
                //var scalebar = new Scalebar({
                //    map: map,
                //    scalebarUnit: "dual"
                //}, "scaleBar");

    //adding home button 
    var home = new HomeButton({
        map: map
    }, "HomeButton");
    
    //Change basemap 
    var basemapGalleryTP = new TitlePane({
        title:"Switch Basemap",
        closable: false,
        open: false
    }, "basemapGalleryTP");
    basemapGalleryTP.startup();
    var basemapGalleryCP = new ContentPane({
    }, "basemapGalleryCP");
    basemapGalleryCP.startup();
    var basemapGallery = new BasemapGallery({
        showArcGISBasemaps: true,
        map: map
      }, "basemapGallery");
    basemapGallery.startup();
    //end

    //adding layers
    if(isDeveloper){
        soilFeatures = new ArcGISDynamicMapServiceLayer(MapServer);
        soilSelLayer = new GraphicsLayer();
        soilFeatures.setOpacity(0.05);

        map.addLayer(soilFeatures);
        map.addLayer(soilSelLayer);
    }

    soilGLayer = new GraphicsLayer();
    map.addLayer(soilGLayer);

    //button to show soil Map Unit
    var myButton = new Button({
        label: "Soil Map",
        onClick: function(){
            // Do something:   
            if(buttonToggle==false){
                soilFeatures.setOpacity(0.6);
                buttonToggle = true;
                //console.log('clicked');
            }else{
                soilFeatures.setOpacity(0.05);
                buttonToggle = false;
                //console.log('unclicked');
            }
            //dom.byId("result1").innerHTML += "Thank you! ";
        }
    }, "progButtonNode").startup();
        
    //to get the polygon Json from URL. 
    var uri_dec;
    function getPolygonJson(url){
        var fileNameTemp = "";
        var longi = -81.5;
        var lat = 28.3;
        var zoom = 9;
        
        if(url.indexOf("json") != -1){
            var queryStart = url.indexOf("json") + 5,
            queryEnd   = url.indexOf("#") + 1 || url.length + 1,
            query = url.slice(queryStart, queryEnd - 1);
            //console.log("Encoded :" + query);
            uri_dec = decodeURIComponent(query);
            ////console.log("Decoded :" + uri_dec);
            var jsonLoc = loadPolygon(uri_dec);
            longi = jsonLoc.long;
            lat = jsonLoc.lat;
            zoom = jsonLoc.zoom;
            
        }
        //console.log("Index of "+url.indexOf("long") +"::" +url.indexOf("lat"));
        if(url.indexOf("long") != -1 || url.indexOf("lat") != -1 || url.indexOf("zoom") != -1){
            //console.log("Inside long");
            var queryStart = url.indexOf("?") + 1;
            var queryEnd  = url.indexOf("#") + 1 || url.length + 1;
            var query = url.slice(queryStart, queryEnd - 1);
            var params = query.split("&");
            //console.log(split1);
            for (i = 0; i < params.length; i++) {
                var tmp = params[i].split("=");
                if (tmp[0] === "long") {
                    longi = tmp[1];
                } else if (tmp[0] === "lat") {
                    lat = tmp[1];
                } else if (tmp[0] === "zoom") {
                    zoom = parseInt(tmp[1]);
                }
            }
            //console.log("Longi :" +longi);
            //console.log("Lat :" +lat);
            var latLongPoint = new esri.geometry.Point(longi, lat, new esri.SpatialReference({ wkid: 4326 }));
            //mapOnClickSoilGLayer(graphic);
            map.setZoom(zoom);
            map.centerAt(latLongPoint);
        }
        else{
            fileName = "not specified";
            unit = "not specified";
        } 
    }
    //Load earlier polygon
    //loadPolygon();
    function loadPolygon(uri_dec){
        
        var text = JSON.parse(uri_dec);
        var polygon = new esri.geometry.Polygon(text);
        ////console.log(polygon);
        //Polygon edit tool
        editToolbar = new edit(map);
        selectState(StateEnum.ZONESELECT);
        currentClick = polygon.getPoint(0,1);
            //create a random color for the symbols
            var r = Math.floor(Math.random() * 250);
            var g = Math.floor(Math.random() * 100);
            var b = Math.floor(Math.random() * 100);

            var symbol = new esri.symbol.SimpleFillSymbol(esri.symbol.SimpleFillSymbol.STYLE_SOLID, new esri.symbol.SimpleLineSymbol(esri.symbol.SimpleLineSymbol.STYLE_SOLID, new dojo.Color([r, g, b, 0.9]), 4), new dojo.Color([r, g, b, 0]));
            var infoTemplate = new esri.InfoTemplate("queryRegion", "content");
            var graphic = new esri.Graphic(polygon, symbol);
            //soilGLayer.add(graphic);
            //mapOnClickSoilGLayer(graphic);
            var center = polygon.getCentroid();
            centroid = center;
            var longi = center.getLongitude();
            var lat = center.getLatitude();
            var latLongPoint = new esri.geometry.Point(longi, lat, new esri.SpatialReference({ wkid: 4326 })); // TODO check if this should be hard-coded
            //mapOnClickSoilGLayer(graphic);
            map.setZoom(14);
            map.centerAt(latLongPoint);
            //console.log("Adding call to save polygon");
            //console.log("polygon " + polygon);
            addSoilGLayerPolygon(polygon);
            polygonToggle();
            //console.log("Poly toggle");
            return {"long":longi, "lat":lat, "zoom":14};
    }
        
    //event handlers
    map.on("click", mapOnClick);
    soilGLayer.on("click", mapOnClickSoilGLayer);
    map.on("load", function(){
        //Polygon tool
        //parser.parse();
        toolbar = new draw(map);
        dojo.connect(toolbar, "onDrawEnd", addSoilGLayerPolygon);

        //Polygon edit tool
        editToolbar = new edit(map);
        selectState(StateEnum.ZONESELECT);

        if(isDeveloper){
            dojo.connect(editToolbar, "graphic-move-stop", graphicQuery);
            dojo.connect(editToolbar, "vertex-move-stop", graphicQuery);
            dojo.connect(editToolbar, "vertex-add", graphicQuery);
            dojo.connect(editToolbar, "vertex-delete", graphicQuery);
        }

       //query preparation
        soilOverlapQuery = new query();
        soilOverlapQuery.returnGeometry = true;
        soilOverlapQuery.outFields = ["*"];
        soilOverlapQuery.spatialRelationship = query.SPATIAL_REL_OVERLAPS;

        soilContainsQuery = new query();
        soilContainsQuery.returnGeometry = true;
        soilContainsQuery.outFields = ["*"];
        soilContainsQuery.spatialRelationship = query.SPATIAL_REL_CONTAINS;

        soilIntersectQuery = new query();
        soilIntersectQuery.returnGeometry = true;
        soilIntersectQuery.outFields = ["*"];
        soilIntersectQuery.spatialRelationship = query.SPATIAL_REL_INTERSECTS;


        chfrQuery = new query();
        chfrQuery.returnGeometry = false;
        chfrQuery.outFields = ["*"];

        chorQuery = new query();
        chorQuery.returnGeometry = false;
        chorQuery.outFields = ["*"];

        compQuery = new query();
        compQuery.returnGeometry = false;
        compQuery.outFields = ["*"];

        mapuQuery = new query();
        mapuQuery.returnGeometry = false;
//        mapuQuery.outFields = ["*"];
        mapuQuery.outFields = ["muname","musym"];

        gQueryCTask = new queryTask(MapServer + "0");
        gQueryOTask = new queryTask(MapServer + "0");
        dQueryCTask = new queryTask(MapServer + "0");
        dQueryOTask = new queryTask(MapServer + "0");

        //chfrQueryTask = new queryTask(MapServer + "1");
        //chorQueryTask = new queryTask(MapServer + "2");
        //compQueryTask = new queryTask(MapServer + "3");
        //mapuQueryTask = new queryTask(MapServer + "4");
        
        //=============For Highlands =====================
        gQueryCTask_Highlands = new queryTask(MapServer_Highlands + "0");
        gQueryOTask_Highlands = new queryTask(MapServer_Highlands + "0");
        dQueryCTask_Highlands = new queryTask(MapServer_Highlands + "0");
        dQueryOTask_Highlands = new queryTask(MapServer_Highlands + "0");
        
        chorQueryTask_Highlands = new queryTask(MapServer_Highlands + "1");
        compQueryTask_Highlands = new queryTask(MapServer_Highlands + "2");
        mapuQueryTask_Highlands = new queryTask(MapServer_Highlands + "3");
        //================================================
        
        //=============For Lake607 =======================
        gQueryCTask_Lake607  = new queryTask(MapServer_Lake607 + "0");
        gQueryOTask_Lake607  = new queryTask(MapServer_Lake607 + "0");
        dQueryCTask_Lake607  = new queryTask(MapServer_Lake607 + "0");
        dQueryOTask_Lake607  = new queryTask(MapServer_Lake607 + "0");
        
        chorQueryTask_Lake607 = new queryTask(MapServer_Lake607 + "1");
        compQueryTask_Lake607 = new queryTask(MapServer_Lake607 + "2");
        mapuQueryTask_Lake607 = new queryTask(MapServer_Lake607 + "3");
        //================================================
        
        //=============For Lake 609 ======================
        gQueryCTask_Lake609 = new queryTask(MapServer_Lake609 + "0");
        gQueryOTask_Lake609 = new queryTask(MapServer_Lake609 + "0");
        dQueryCTask_Lake609 = new queryTask(MapServer_Lake609 + "0");
        dQueryOTask_Lake609 = new queryTask(MapServer_Lake609 + "0");
        
        chorQueryTask_Lake609 = new queryTask(MapServer_Lake609 + "1");
        compQueryTask_Lake609 = new queryTask(MapServer_Lake609 + "2");
        mapuQueryTask_Lake609 = new queryTask(MapServer_Lake609 + "3");
        //================================================
        
        //=============For Orange ========================
        gQueryCTask_Orange  = new queryTask(MapServer_Orange + "0");
        gQueryOTask_Orange  = new queryTask(MapServer_Orange + "0");
        dQueryCTask_Orange  = new queryTask(MapServer_Orange + "0");
        dQueryOTask_Orange  = new queryTask(MapServer_Orange + "0");
        
        chorQueryTask_Orange  = new queryTask(MapServer_Orange + "1");
        compQueryTask_Orange  = new queryTask(MapServer_Orange + "2");
        mapuQueryTask_Orange  = new queryTask(MapServer_Orange + "3");
        //================================================ 
        
        //=============For Osceola ========================
        gQueryCTask_Osceola = new queryTask(MapServer_Osceola + "0");
        gQueryOTask_Osceola = new queryTask(MapServer_Osceola + "0");
        dQueryCTask_Osceola = new queryTask(MapServer_Osceola + "0");
        dQueryOTask_Osceola = new queryTask(MapServer_Osceola + "0");
        
        chorQueryTask_Osceola = new queryTask(MapServer_Osceola + "1");
        compQueryTask_Osceola = new queryTask(MapServer_Osceola + "2");
        mapuQueryTask_Osceola = new queryTask(MapServer_Osceola + "3");
        //================================================

        //=============For Polk ========================
        gQueryCTask_Polk = new queryTask(MapServer_Polk + "0");
        gQueryOTask_Polk = new queryTask(MapServer_Polk + "0");
        dQueryCTask_Polk = new queryTask(MapServer_Polk + "0");
        dQueryOTask_Polk = new queryTask(MapServer_Polk + "0");
        
        chorQueryTask_Polk = new queryTask(MapServer_Polk + "1");
        compQueryTask_Polk = new queryTask(MapServer_Polk + "2");
        mapuQueryTask_Polk = new queryTask(MapServer_Polk + "3");
        //================================================

        geometryService = new geometryService(GeometryServicePATH);
        getPolygonJson(url);
        //esri.config.defaults.geometryService = new geometryService(GeometryServicePATH);
        //dojo.connect(geometryService, "onAreasAndLengthsComplete", onFinishGeometryCalculation)
        //geometryService.on("onAreasAndLengthsComplete", onFinishGeometryCalculation);
    });

    //register loading gif visibility handlers
    loadingImg = dojo.byId("loadingImg");
    dojo.connect(map, "onUpdateStart", showLoadingImg);
    dojo.connect(map, "onUpdateEnd", hideLoadingImg);

    //event handler for buttons
    on(dom.byId("step2Button"),"click", polygonToggle);
    on(dom.byId("calcButton"), "click", function(evt){dataQuery();});
    on(dom.byId("clearButton"), "click", function(evt){selectState(StateEnum.RESET);});

    //Date on File Name
    function getDateFileName(){
        var dtObj = new Date();
        dateName = (dtObj.getMonth()+1)+"/"+dtObj.getDate()+"/"+dtObj.getFullYear()+"_"+dtObj.getHours()+":"+dtObj.getMinutes();
    }

    //get arguments passed by URL like site name and unit name 
    function getParams(url){
        //console.log("URL :" +url);
        var fileNameTemp = ""; 
        if(url.indexOf("?") != -1) {
            fileName = "not specified";
            unit = "not specified";
            var queryStart = url.indexOf("?") + 1,
            queryEnd   = url.indexOf("#") + 1 || url.length + 1,
            query = url.slice(queryStart, queryEnd - 1),
            split1 = query.split("&");
            //console.log(split1);
            for (i = 0; i < split1.length; i++) {
                var split2 = split1[i].split("=");
                if (split2[0] === "site") {
                    fileName = split2[1].split("%20").join('_');
                } else if (split2[0] === "unit") {
                    unit = split2[1].split("%20").join('_');
                }
            }
            /*if(unit.length >7){
                unit = unit.substring(0,7);
            }*/
            //console.log(fileName,unit);
            if (fileName !== "not specified") {
	            var permitIdLB = document.getElementById("permit_id_lb");
		        permitIdLB.innerHTML = fileName;
		        permitIdLB.contentEditable = false;
		        permitIdLB.style.border = "solid";
		        permitIdLB.style.borderColor = "white";
		        document.getElementById("map_name_lb").innerHTML = fileName;
		        document.getElementById("permit_id_edit_mark").style.display = "none";
	    	}
	    	if (unit !== "not specified") {
	        	document.getElementById("map_name_lb").innerHTML = unit;
	        } else {
	        	unit = fileName;
	        }
        } else {
            fileName = "not specified";
            unit = "not specified";
            //console.log(fileName,unit);
        } 
    }
    
    function getInputParams() {
    	var permitId = document.getElementById("permit_id_lb").innerHTML;
    	var mapName = document.getElementById("map_name_lb").innerHTML;
        fileName = "not specified";
        unit = "not specified";
    	if (permitId !== "Enter permit ID") {
    		fileName = permitId;
    	}
    	if (mapName !== "Enter map name") {
    		unit = mapName;
    	}
    }

    //loading gif visibility handlers
    function showLoadingImg(){
        esri.show(loadingImg);
        map.disableMapNavigation();
        map.hideZoomSlider();
    }
    function hideLoadingImg(error){
        esri.hide(loadingImg);
        map.enableMapNavigation();
        map.showZoomSlider();
    }
    function showProgress(progressVal){
    	var pct = (Number(progressVal) * 100).toFixed(0) + "%";
    	var progressBar = document.getElementById("progressBar");
        	progressBar.innerHTML = pct;
        	progressBar.style.width = pct;
        if (progressVal >= 1 || progressVal < 0) {
        	progressBar.classList.add("progress-bar-success");
        	$("#progressDiv").fadeOut("slow","linear");
        } else if (progressVal === 0) {
        	$("#progressDiv").fadeIn();
        	progressBar.classList.remove("progress-bar-success");
        }
    }

    //calculate area of the polygons
    function calculateGeometry(geometries, callback){
        showLoadingImg();

        geometryProcessor = callback;
        geometryService.simplify(geometries, function(simplifiedGeometries) {
            var areasAndLengthParams = new esri.tasks.AreasAndLengthsParameters();
            areasAndLengthParams.areaUnit = esri.tasks.GeometryService.UNIT_ACRES;
            areasAndLengthParams.calculationType = "geodesic";
            areasAndLengthParams.polygons = simplifiedGeometries;
            geometryService.areasAndLengths(areasAndLengthParams, callback);
        });
    }

    /*function onFinishGeometryCalculation(result){
        hideLoadingImg();
    }*/

    //mouse click handler
    function mapOnClick(evt){
        //deactivate edit toolbar when user click on map
        deselectSoilGLayer();
        //depending on state, handle mouse click
        switch(currentState){
        case StateEnum.ZONESELECT:
            break;
        case StateEnum.WSSELECT:
            break;
        }
    }

    //state functions
    function selectState(state){
        switch(state){
        case StateEnum.ZONESELECT:
            setState(state);
            break;
        case StateEnum.WSSELECT:
            //TODO: check progress?
            setState(state);
            break;
        case StateEnum.RESET:
            //TODO: reset progress
            soilGLayer.clear();
            document.getElementById("calcButton").disabled = true;
            document.getElementById("saveButton").disabled = true;
            document.getElementById("site_area_lb").innerHTML = "";
            document.getElementById("queryRetTable").style.display = "none";
            setState(StateEnum.ZONESELECT);
            County = [];
            break;
        }
    }

    function setState(state){
        setPolygonMode(false);
        deselectSoilGLayer();
        switch(state){
        case StateEnum.ZONESELECT:
            currentState = state;
            document.getElementById("contentPane2").style.display = "block";
            document.getElementById("messages").style.display = "none";
            //document.getElementById("contentPane3").style.display = "none";
            //document.getElementById("submenu").style.display = "block";
            document.getElementById("contentPane4").classList.add("active");
            //document.getElementById("step2Button").style.visibility = 'visible';
            //document.getElementById("calcButton").style.visibility = 'visible';
            //document.getElementById("clearButton").style.visibility = 'visible';
            document.getElementById("footer").style.visibility = 'visible';
            //document.getElementById("footer").style.visibility = 'visible';
            /*document.getElementById("step3Button").classList.remove("active");
            document.getElementById("calcButton").classList.add("active");*/
           
            break;
        case StateEnum.WSSELECT:
            currentState = state;
            document.getElementById("contentPane2").style.display = "none";
            //document.getElementById("contentPane3").style.display = "block";
            //document.getElementById("submenu").style.display = "none";
            document.getElementById("step2Button").classList.remove("active");
           // document.getElementById("step3Button").classList.add("active");
            document.getElementById("calcButton").classList.add("active");
            break;
        }
    }

    function polygonToggle(){
        // TODO commeted out for now, might use for future if it is necessasry to clean the last polygon before drawing a new one
//        if (!polygonMode) {
//            soilGLayer.clear();
//            map.infoWindow.hide();
//            County = [];
//        }
        setPolygonMode(!polygonMode);
        document.getElementById("calcButton").disabled = !polygonMode;
    }

    function setPolygonMode(state){
        polygonMode = state;
        if(polygonMode){
            toolbar.activate(esri.toolbars.Draw.POLYGON);
            //document.getElementById("polygonButton").classList.add("polygonActive");
            document.getElementById("step2Button").classList.add("active");
        }
        else {
            toolbar.deactivate();
            //document.getElementById("polygonButton").classList.remove("polygonActive");
            document.getElementById("step2Button").classList.remove("active");
        }
    }

    function deleteSoilGLayerPolygon(){
        if (soilGLayer.graphics.length > 0 && soilGLayerSelected) {
            soilGLayer.remove(editToolbar.getCurrentState().graphic);

            var operation = new customoperation.Add({
                graphicsLayer: soilGLayer,
                addedGraphic: editToolbar.getCurrentState().graphic
            });
            undoManager.add(operation);
        }

        deselectSoilGLayer();
        //close the corresponding infowindow.
    //    map.infoWindow.hide();
    }
    var long;
    var lat;
    //called when user completes drawing a polygon
    function addSoilGLayerPolygon(geometry){
        //console.log("Inside addSoilGLayerPolygon");
        if (soilGLayer.graphics.length >= 0 && soilGLayer.graphics.length <= 4) {
            currentClick = geometry.getPoint(0,1);
            //create a random color for the symbols
            var r = Math.floor(Math.random() * 0);
            var g = Math.floor(Math.random() * 10);
            var b = Math.floor(Math.random() * 100);

            var symbol = new esri.symbol.SimpleFillSymbol(esri.symbol.SimpleFillSymbol.STYLE_SOLID, new esri.symbol.SimpleLineSymbol(esri.symbol.SimpleLineSymbol.STYLE_SOLID, new dojo.Color([r, g, b, 0.9]), 4), new dojo.Color([r, g, b, 0]));
            var infoTemplate = new esri.InfoTemplate("queryRegion", "content");
            var graphic = new esri.Graphic(geometry, symbol);
            soilGLayer.add(graphic);
            //console.log("before");
            mapOnClickSoilGLayer(graphic);
            //console.log("after");
            //Hiranava - Adding new code to get center point
            //var centroid = new esri.Point(0,0);
            //console.log("[Hiranava] Before calculating center: ");
            centroid = geometry.getCentroid();
            //console.log("[Hiranava] Printing Center: " + centroid.getLongitude() + ", " + centroid.getLatitude()); 
            //console.log("The Polygon " + geometry);
            var center = geometry.getCentroid();
            //console.log("The Polygon center" + center);
            polyJSON = geometry.toJson();
            //console.log("The Polygon JSON " + JSON.stringify(polyJSON));
        }
        else
            alert("You can draw atmost five query polygons");
        polygonToggle();
    }

    //Activate the toolbar when you click on a graphic
    function mapOnClickSoilGLayer(graphic){
        ////console.log("Inside mapOnClickSoilGLayer");
        if (currentState === StateEnum.ZONESELECT && soilGLayer.graphics.length > 0) {
            //dojo.stopEvent(evt);
            ////console.log("Inside  if");
            editToolbar.activate((esri.toolbars.Edit.MOVE | esri.toolbars.Edit.EDIT_VERTICES), graphic);
            soilGLayerSelected = true;
        }
        ////console.log("Inside mapOnClickSoilGLayer after if");
        // if(isDeveloper){
        calculateGeometry([editToolbar.getCurrentState().graphic.geometry], function(result){
            polyArea = result.areas[0].toFixed(3);
            ////console.log("Inside mapOnClickSoilGLayer before showLoadingImg");
            showLoadingImg();
            ////console.log("Inside mapOnClickSoilGLayer before graphicQuery");
            graphicQuery();
        });
        // }
    }

    function deselectSoilGLayer(){
        if(isDeveloper)
            soilSelLayer.clear();
        editToolbar.deactivate();
        soilGLayerSelected = false;
    }

    //custom opration for undo/redo manager
    dojo.declare("customoperation.Add", esri.OperationBase, {
        label: "Add Graphic",
        constructor: function(params){   /*graphicsLayer, addedGraphic*/
            params = params || {};
            if (!params.graphicsLayer) {
                //console.error("graphicsLayer is not provided");
                return;
            }
            this.graphicsLayer = params.graphicsLayer;

            if (!params.addedGraphic) {
                //console.error("no graphics provided");
                return;
            }
            this._addedGraphic = params.addedGraphic;
        },

        performUndo: function(){
            this.graphicsLayer.add(this._addedGraphic);
        },

        performRedo: function(){
            this.graphicsLayer.remove(this._addedGraphic);
        }
    });

    function checkResultCriteria(){
        if(!soilGLayerSelected){
            document.getElementById("messages").style.display = "block";
            //alert("Please select a polygon first.");
            return false;
        }else{
            document.getElementById("messages").style.display = "none";  
            return true;
        }
    }

    function graphicQuery(){
          if(checkResultCriteria()){
            var resultSetCount = 0;
            var rawGeometries = [];
            County = []; // TODO this might be removed for support multiple plygon query

            function showSoilFeature(){}

            function processResults(featureSet){
                featureSet.features.forEach(function(graphic){
//                    rawGeometries.push({mukey: graphic.attributes.MUKEY, geometry: graphic.geometry});
                    rawGeometries.push({mukey: graphic.attributes.MUKEY, geometry: graphic.geometry, county: graphic.attributes.AREASYMBOL, musym:graphic.attributes.MUSYM});
                });
                resultSetCount++;
                ////console.log("GQ resultSetCount" +resultSetCount);
                if(resultSetCount === 1){
                    var graphicResultCount = 0;
                    rawGeometries.forEach(function(rawGeometry){
                        rawGeometriessmall = [];
                        soilArea = [];
                        var syncMukey = rawGeometry.mukey;
                        rawGeometriessmall[0] =  rawGeometry.geometry; 
                        geometryService.intersect(rawGeometriessmall, editToolbar.getCurrentState().graphic.geometry, function(result){
                            graphicResultCount++;
                            result.forEach(function(geometry){
                                if(geometry != null){
                                    geometryArray = [];
                                    geometryArray[0] = geometry;
                                    var currentMukey = rawGeometry.mukey;
                                    var county = rawGeometry.county;
                                    if (County.indexOf(county) < 0) {
                                        County.push(county);
                                    }
                                    ////console.log("GQ County  " +County); 
                                    var polyArea1 = 0;
                                    (function wrapper(currentMukey){
                                        calculateGeometry(geometryArray, function areaCallback(result){
                                            var obj = {
                                            mukey: currentMukey, 
                                            polyarea: result.areas[0].toFixed(3),
                                            county: county
                                            }
                                            ////console.log("value : "+ JSON.stringify(obj));
                                            soilArea.push(obj);
                                        });
                                    }(currentMukey));
                                    var symbol = new esri.symbol.SimpleFillSymbol(esri.symbol.SimpleFillSymbol.STYLE_SOLID, new esri.symbol.SimpleLineSymbol(esri.symbol.SimpleLineSymbol.STYLE_SOLID, new dojo.Color([255, 255, 0]), 1), new dojo.Color([43, 149, 255, 0.5]));
                                    var symbol2 = new esri.symbol.TextSymbol("").setColor(new dojo.Color([255, 255, 255, 1]));
                                    var graphic = new esri.Graphic(geometry, symbol);
                                    graphic.setSymbol(symbol);
                                    symbol2.setText(rawGeometry.musym);
                                    var graphic2 = new esri.Graphic(geometry, symbol2);
                                    graphic2.setSymbol(symbol2);
                                    soilSelLayer.add(graphic);
                                    soilSelLayer.add(graphic2);
                                    bool = true;
                                }
                            });
                            showProgress(graphicResultCount/rawGeometries.length);
                            if (graphicResultCount === rawGeometries.length) {
                                hideLoadingImg();
                                document.getElementById("calcButton").disabled = false;
                            }
                        });
                    });
                    //hideLoadingImg();
                }
            }

            soilSelLayer.clear();

            soilIntersectQuery.geometry = editToolbar.getCurrentState().graphic.geometry;
            
            //console.log("before calling gQueryCTask");
            gQueryOTask.execute(soilIntersectQuery, processResults);
            showLoadingImg();
            document.getElementById("calcButton").disabled = true;
            showProgress(0);
        }
    }

    function dataQuery(){
         if(checkResultCriteria()){
            var resultSetCount = 0;
            var rawGeometries = [];
            var Attributes = [];
            var filteredresults = [];

            function processResults(featureSet){
                featureSet.features.forEach(function(graphic){
                    rawGeometries.push(graphic.geometry);
//                    Attributes.push({mukey: graphic.attributes.MUKEY});
                    Attributes.push({mukey: graphic.attributes.MUKEY,county: graphic.attributes.AREASYMBOL, musym:graphic.attributes.MUSYM});
                });


                resultSetCount++;
//                console.log("DQ resultSetCount" +resultSetCount);
                if(resultSetCount === County.length){
                    var len = Attributes.length;

                    var index = 0;
                    Attributes.sort(function(a, b){
                        if(a.mukey > b.mukey)
                            return 1;
                        if(a.mukey < b.mukey)
                            return -1;
                        return 0;
                    });
                    soilArea.sort(function(a, b){
                        if(a.mukey > b.mukey)
                            return 1;
                        if(a.mukey < b.mukey)
                            return -1;
                        return 0;
                    });
                    //console.log("SOil Area" +JSON.stringify(soilArea));
                    //console.log("Attributes" +JSON.stringify(Attributes));
                    for(var i = 0; i < len; i++){
                        if(soilArea[i]!=null){
//                            console.log("soilArea[i].polyarea" +soilArea[i].polyarea);
                            Attributes[i].soilArea = soilArea[i].polyarea;
                            Attributes[i].unitPct = (soilArea[i].polyarea/polyArea) * 100;
                        }
                    }    

                    filteredresults.push(Attributes[0]);
                    for(var i = 1; i < len; i++){
                        //console.log("filteredresults[index].mukey "+filteredresults[index].mukey);
                        //console.log("Attributes[i].mukey " +Attributes[i].mukey);
                        if(filteredresults[index].mukey !== Attributes[i].mukey){
                            filteredresults.push(Attributes[i]);
                            index++;
                        } else {
                            var tempArea = parseFloat(filteredresults[index].soilArea) + parseFloat(Attributes[i].soilArea);
                            filteredresults[index].soilArea = tempArea.toFixed(3);
                            filteredresults[index].unitPct = filteredresults[index].unitPct + Attributes[i].unitPct;
                            ////console.log(filteredresults[index].soilArea, Attributes[i].soilArea);
                        }
                    }
                    var filterLen = filteredresults.length;
                    var id = 1;
                    for(var i = 1; i < filterLen; i++){
                        if(filteredresults[i] != filteredresults[i-1]) {
                            //console.log("filteredresults[id].mukey "+filteredresults[id].mukey);
                            filteredresults[id++] = filteredresults[i];
                        }    
                    }
                    id = 1;
                    for(var i = 1; i < len; i++){
                        if(Attributes[i] != Attributes[i-1]) {
                            //console.log("Attributes[id].mukey "+Attributes[id].mukey);
                            Attributes[id++] = Attributes[i];
                        }    
                    }
                    //console.log("filteredresults "+ JSON.stringify(filteredresults))
                    for(var i = 1; i < filterLen; i++){
                        if(isNaN(filteredresults[i].soilArea) == true ){
                            getMukeyCheck == true;
                        }
                    }
                    if(resultSetCount == County.length){
                        getMUKEYNames(filteredresults);
                    }
                    hideLoadingImg("");
                    document.getElementById("calcButton").disabled = false;
                }
            }
            soilIntersectQuery.geometry = editToolbar.getCurrentState().graphic.geometry;
            County.sort(function(a, b){
                if(a > b)
                    return 1;
                if(a < b)
                    return -1;
                return 0;
            });
            for(var i = 0; i < County.length; i++){
                //console.log("DQ County[i] "+County[i]);
                switch(County[i]){
                    //Highlands County
                    case 'FL055':
                        gQueryCTask = gQueryCTask_Highlands;
                        chorQueryTask = chorQueryTask_Highlands;
                        compQueryTask = compQueryTask_Highlands;
                        mapuQueryTask = mapuQueryTask_Highlands;
                    break;
                    //Orange County
                    case 'FL095':
                        gQueryCTask = gQueryCTask_Orange;
                        chorQueryTask = chorQueryTask_Orange;
                        compQueryTask = compQueryTask_Orange;
                        mapuQueryTask = mapuQueryTask_Orange;
                    break;
                    //Osecala County
                    case 'FL097':
                        gQueryCTask = gQueryCTask_Osceola;
                        chorQueryTask = chorQueryTask_Osceola;
                        compQueryTask = compQueryTask_Osceola;
                        mapuQueryTask = mapuQueryTask_Osceola;
                    break;
                    //Polk County
                    case 'FL105':
                        gQueryCTask = gQueryCTask_Polk;
                        chorQueryTask = chorQueryTask_Polk;
                        compQueryTask = compQueryTask_Polk;
                        mapuQueryTask = mapuQueryTask_Polk;
                    break;
                    //Lake County 
                    case 'FL607':
                        gQueryCTask = gQueryCTask_Lake607;
                        chorQueryTask = chorQueryTask_Lake607;
                        compQueryTask = compQueryTask_Lake607;
                        mapuQueryTask = mapuQueryTask_Lake607;
                    break;
                    //Lake County
                    case 'FL609':
                        gQueryCTask = gQueryCTask_Lake609;
                        chorQueryTask = chorQueryTask_Lake609;
                        compQueryTask = compQueryTask_Lake609;
                        mapuQueryTask = mapuQueryTask_Lake609;
                    break;
                    default:
                        //do nothing
                        console.log("Detect polygon with undefined county information at index " + i);
                }
                gQueryCTask.execute(soilIntersectQuery, processResults);
                showLoadingImg();
                document.getElementById("calcButton").disabled = true;
            }
        }
    }
    function getFinalJson(Attributes, cokeyList){
        var soils = [];
        var afsirs = [];
        var polygon = [];
        var result = {"soils": soils,"afsirs": afsirs,"polygon": polygon, "version" : "1.0.1"};
        
        Attributes.forEach(function(record){
            cokeyList.forEach(function(obj){
                if(obj.mukey==record.mukey){
                    record.componentList.forEach(function(cokeyObj){
                        if(obj.cokeyArray.indexOf(cokeyObj.cokey) >= 0){
                            result.soils.push(cokeyObj);
                        }
                    });
                }
            });
        });
        var array = [];
        array.push({
            "long":centroid.getLongitude(),
            "lat": centroid.getLatitude(),
            "TotalArea": polyArea
        });
        //console.log("Array of lon lat "  + array.lat);
        array.forEach(function(position){
                result.afsirs.push(position);
                
        });
        var arrayPolygon = [];
        arrayPolygon.push(polyJSON);
        arrayPolygon.forEach(function(ring){
                result.polygon.push(ring);
                
        });
        return JSON.stringify(result);
    }
    function getMUKEYNames(Attributes){
        showLoadingImg("");
        var resultSetCount = 0;

        Attributes.forEach(function(attribute){
            mapuQuery.where = "mukey = '" + attribute.mukey + "'";
            //console.log("MQ County" +attribute.county);
            ////console.log("MQ County[i] "+County[i]);
            //for(var i=0;i<County.length;i++){
                switch(attribute.county){
                    //Highlands County
                    case 'FL055':
                        mapuQueryTask = mapuQueryTask_Highlands;
                    break;
                    //Orange County
                    case 'FL095':
                        mapuQueryTask = mapuQueryTask_Orange;
                    break;
                    //Osecala County
                    case 'FL097':
                        mapuQueryTask = mapuQueryTask_Osceola;
                    break;
                    //Polk County
                    case 'FL105':
                        mapuQueryTask = mapuQueryTask_Polk;
                    break;
                    //Lake County 
                    case 'FL607':
                        
                        mapuQueryTask = mapuQueryTask_Lake607;
                    break;
                    //Lake County
                    case 'FL609':
                        mapuQueryTask = mapuQueryTask_Lake609;
                    break;
                    default:
                            //do nothing
                            //console.log("In nothing");
                }
                mapuQueryTask.execute(mapuQuery, function(featureSet){
                    featureSet.features.forEach(function(graphic){
                        attribute.soilName = graphic.attributes.muname;
                    });
                    resultSetCount++;
                    if(resultSetCount === Attributes.length)
                        getCokeys(Attributes);
                });
        });
    }

    function getCokeys(Attributes){
        var resultSetCount = 0;
        
        Attributes.forEach(function(attribute){
            attribute.componentList = [];
            compQuery.where = "mukey = '" + attribute.mukey + "'";
            //console.log("CQ County" +attribute.county);
            switch(attribute.county){
                    //Highlands County
                    case 'FL055':
                        
                        compQueryTask = compQueryTask_Highlands;
                        
                    break;
                    //Orange County
                    case 'FL095':
                        
                        compQueryTask = compQueryTask_Orange;
                        
                    break;
                    //Osecala County
                    case 'FL097':
                        
                        compQueryTask = compQueryTask_Osceola;
                        
                    break;
                    //Polk County
                    case 'FL105':
                        
                        compQueryTask = compQueryTask_Polk;
                        
                    break;
                    //Lake County 
                    case 'FL607':
                        
                        compQueryTask = compQueryTask_Lake607;
                        
                    break;
                    //Lake County
                    case 'FL609':
                        
                        compQueryTask = compQueryTask_Lake609;
                        
                    break;
                    default:
                            //do nothing
                            //console.log("In nothing");
                }
            compQueryTask.execute(compQuery, function(featureSet){
                var length = featureSet.features.length;
                var maxindex = 0;
                var maxval = featureSet.features[0].attributes.comppct_r;
                for(var i = 0; i < length; i++){
                    if(featureSet.features[i].attributes.comppct_r > maxval) {
                        maxindex = i;
                        maxval = featureSet.features[i].attributes.comppct_r;
                    }
                    var componentArea = ((attribute.soilArea * featureSet.features[i].attributes.comppct_r)/100).toFixed(3);
                    //console.log("Componenet Area " + componentArea);
                    //console.log("Total Area " + polyArea);
                    var soilPercentage = (componentArea/polyArea) * 100;
                    //console.log("soilPercentage " + soilPercentage);
                    //if(featureSet.features[i].attributes.comppct_r>5){
                    if(soilPercentage>=0){                      
                        attribute.componentList.push({
                             "mukey":attribute.mukey,
                             "musym":attribute.musym,
                             "mukeyName":attribute.soilName,
                             "county":attribute.county,
                             "sl_source" : "Fl soil map",
                             "cokey":featureSet.features[i].attributes.cokey,
                             "soilName":featureSet.features[i].attributes.compname,
                             "comppct_r":featureSet.features[i].attributes.comppct_r,
                             "slro":featureSet.features[i].attributes.slro,
                             "salb":featureSet.features[i].attributes.salb,
                             "sadr":featureSet.features[i].attributes.sadr,
                             "compArea": componentArea
                        });
                    }
                }
                attribute.cokey = featureSet.features[maxindex].attributes.cokey;
                attribute.compName = featureSet.features[maxindex].attributes.compname;
                attribute.comppct_r = featureSet.features[maxindex].attributes.comppct_r;
                attribute.slro = featureSet.features[maxindex].attributes.runoff;
                attribute.salb = featureSet.features[maxindex].attributes.albedodry_r;
                attribute.sadr = featureSet.features[maxindex].attributes.drainagecl;

                attribute.soilLayer = [];
                var aryLength = attribute.componentList.length-1;
                attribute.componentList.forEach(function(component){
                    component.soilLayer = [];
                    chorQuery.where = "cokey = '" + component.cokey + "'";
                    //console.log("CHQ County" +attribute.county);
                    switch(component.county){
                    //Highlands County
                    case 'FL055':
                        
                        chorQueryTask = chorQueryTask_Highlands;
                        
                    break;
                    //Orange County
                    case 'FL095':
                        
                        chorQueryTask = chorQueryTask_Orange;
                        
                    break;
                    //Osecala County
                    case 'FL097':
                        
                        chorQueryTask = chorQueryTask_Osceola;
                        
                    break;
                    //Polk County
                    case 'FL105':
                        
                        chorQueryTask = chorQueryTask_Polk;
                        
                    break;
                    //Lake County 
                    case 'FL607':
                        
                        chorQueryTask = chorQueryTask_Lake607;
                        
                    break;
                    //Lake County
                    case 'FL609':
                        chorQueryTask = chorQueryTask_Lake609;
                        break;
                    default:
                            //do nothing
                            //console.log("In nothing");
                }
                    chorQueryTask.execute(chorQuery, function(featureSet){
                        featureSet.features.forEach(function(graphic){
                            if(graphic.attributes.wthirdbar_r == null || graphic.attributes.wfifteenbar_r == null){ 
                                var missing_data = missingVals(graphic.attributes.claytotal_r,graphic.attributes.silttotal_r,graphic.attributes.sandtotal_r);
                                if(graphic.attributes.wthirdbar_r == null){
                                    //console.log("dul = "+ missing_data.dul+ "ll = "+missing_data.ll);
                                    graphic.attributes.wthirdbar_r = parseFloat(missing_data.dul);
                                }
                                if(graphic.attributes.wfifteenbar_r == null){
                                    //console.log("ll = null");
                                    graphic.attributes.wfifteenbar_r = parseFloat(missing_data.ll);
                                    //console.log("ll new = " + graphic.attributes.wfifteenbar_r);
                                }    
                            }
                            //console.log(component.cokey+","+graphic.attributes.wthirdbar_r);
                            //checking the first 3 values to remove duplicates. Hypotheseis : Duplicate values come in pairs and 3 values are enough to find that.
                            if(graphic.attributes.wthirdbar_r==oldsldul && oldsllb == graphic.attributes.hzdepb_r && oldslll == graphic.attributes.wfifteenbar_r){
                                //do nothing
                            } else {
                                //console.log("I am here "+oldsldul+" "+component.cokey+","+graphic.attributes.wthirdbar_r);
                                oldsldul =graphic.attributes.wthirdbar_r
                                oldsllb=graphic.attributes.hzdepb_r
                                oldslll=graphic.attributes.wfifteenbar_r
                                component.soilLayer.push({
                                    "sldul" : Number((Number(graphic.attributes.wthirdbar_r) / 100).toFixed(3)),
                                    "sllb"  : graphic.attributes.hzdepb_r, //hzdepb
                                    "slll"  : Number((Number(graphic.attributes.wfifteenbar_r) / 100).toFixed(3)),
                                    "clay"  : graphic.attributes.claytotal_r,
                                    "sand"  : graphic.attributes.sandtotal_r,
                                    "silt"  : graphic.attributes.silttotal_r,
                                    "slcf"  : graphic.attributes.fragvol_r,
                                    "slbdm" : graphic.attributes.partdensity,
                                    "sloc"  : graphic.attributes.claysizedcarb_r,
                                    "slphw" : graphic.attributes.ph1to1h2o_r,
                                    "slcec" : graphic.attributes.cec7_r,
                                    "slna" : graphic.attributes.sar_r,
                                    "slfe" : graphic.attributes.freeiron_r
                                });
                            }
                        });
                        resultSetCount++;
                        //if(resultSetCount === Attributes.length){
                            // alert(JSON.stringify(Attributes));
                            showPopup(Attributes);
                        //}
                    });
                });
            });
        });
    }

    function showPopup(Attributes){
        var htmlString = getWindowContent(Attributes);
        //console.log("Attributes in show popup" +JSON.stringify(Attributes));
        getInputParams();

        document.getElementById("site_area_lb").innerHTML = polyArea;
        document.getElementById("queryRetTable").style.display = "block";
        var retTableDir = document.getElementById("tblBody");
        retTableDir.innerHTML = htmlString;
        
        Attributes.forEach(function(attribute){
            $('[data-toggle="' + attribute.mukey + '"]').popover();  
        });

        var saveButton = document.getElementById("saveButton");
        saveButton.disabled = false;
        saveButton.onclick = function() {
            var array = [];
            //console.log("Inside save " );
            $("#tbl input[name='link']:checked").each(function() {
                //for each checked checkbox, iterate through its parent's siblings
	            //console.log("Sub array " + subArray);
	            var key = this.value;
	            $("#tbl input[name='link_" + key + "']:checked").each(function() {
	            	subArray = $(this).parent().siblings().map(function() {
		                return $(this).text().trim();
		            }).get();
		            var cokey = subArray[3];
		            var mukey = subArray[4];
		            //console.log("cokey " + cokey);
		            //console.log("mukey " + mukey);
		            //console.log("Array " + array);
		            if(array == null){
		                var subArray = [];
		                subArray.push(cokey);
		                array.push({
		                    "mukey":mukey,
		                    "cokeyArray": subArray
		                });
	                } else {
	                    var bool = false;
	                    array.forEach(function(obj){
	                        if(obj.mukey==mukey){
	                            obj.cokeyArray.push(cokey);
	                            bool = true;
	                        }
	                    });
	                    if(bool == false){
	                        var subArray = [];
	                        subArray.push(cokey);
	                        array.push({
	                            "mukey":mukey,
	                            "cokeyArray": subArray
	                        });
	                    }
	                }
	                array.push(mukey);
	            });
            })
            //to print the value of array
            var text = getFinalJson(Attributes, array);
            var blob = new Blob([text], {type: "text/plain;charset=utf-8"});
            //get date for fileName
            getDateFileName();
            getInputParams();
            //console.log("Before save ",fileName,unit);
            saveAs(blob,unit+".json");
            //setTimeout(window.location.href = "http://abe.ufl.edu/bmpmodel/Shivam/v3_shivam/success.html#",2000);
        };
    }
    
    function getWindowContent(Attributes){
        
        var htmlString;
        var soilTypeHtml1 = "<table class=\"table table-striped table-condensed\" style=\"width:220px\"><thead><tr><th>Soil Type</th><th>Pct.(%)</th><th style=\"display:none;\">Mukey</th><tr></thead><tbody>";
        var soilTypeHtml3 = "</tbody></table>";
        var soilTypeHtml2;

        Attributes.sort(function(a, b){
             var areaA=parseFloat(a.soilArea), areaB=parseFloat(b.soilArea);
             if (areaB < areaA) //sort string ascending
              return -1;
             if (areaB > areaA)
              return 1;
             return 0; //default return value (no sorting)
        });

       Attributes.forEach(function(attribute){
            attribute.componentList.forEach(function(component){
                component.soilLayer.sort(function(a,b){
                    var comA=a.sllb, comB=b.sllb;
                     if (comB > comA) //sort string ascending
                      return -1;
                     if (comB < comA)
                      return 1;
                     return 0; //default return value
                });
            });
        });

       Attributes.forEach(function(attribute){
            attribute.componentList.sort(function(a,b){
                var areaA=parseFloat(a.compArea), areaB=parseFloat(b.compArea);
                 if (areaB < areaA) //sort string ascending
                  return -1;
                 if (areaB > areaA)
                  return 1;
                 return 0; //default return value (no sorting)
            });
        });



        Attributes.forEach(function(attribute){
            if(htmlString==null){
                  htmlString = "";
            }
            soilTypeHtml2 = "";
            var compHtmlStr = "";
            var ifOnlyWater = true;
            attribute.componentList.forEach(function(component){
            	var areaStr = component.compArea;
            	if (Number(areaStr) === 0) {
            		areaStr = "<0.001"
            	}
                if(component.soilName=="Water"){
                    compHtmlStr = compHtmlStr + "<tr style=\"display:none;\"><td><input type=\"checkbox\" disabled/></td><td>" + component.musym + "</td><td>" + component.soilName + "</td><td>" + areaStr + "</td><td style=\"display:none;\">" + component.cokey + "</td><td style=\"display:none;\">" + attribute.mukey + "</td></tr>";
                } else {
                    ifOnlyWater = false;
                    if (attribute.unitPct >= 5) {
                    	compHtmlStr = compHtmlStr + "<tr style=\"display:none;\"><td><input type=\"checkbox\" name=\"link_" + component.mukey + "\" checked/></td><td>" + component.musym + "</td><td>" + component.soilName + "</td><td>" + areaStr + "</td><td style=\"display:none;\">" + component.cokey + "</td><td style=\"display:none;\">" + attribute.mukey + "</td></tr>";
                    } else {
                    	compHtmlStr = compHtmlStr + "<tr style=\"display:none;\"><td><input type=\"checkbox\" name=\"link_" + component.mukey + "\"/></td><td>" + component.musym + "</td><td>" + component.soilName + "</td><td>" + areaStr + "</td><td style=\"display:none;\">" + component.cokey + "</td><td style=\"display:none;\">" + attribute.mukey + "</td></tr>";
                    }
                    soilTypeHtml2 = soilTypeHtml2 + "<tr><td class=\"text-left\">" + component.soilName + "</td><td>" + component.comppct_r + "</td><td style=\"display:none;\">" + component.cokey + "</td></tr>";
                }
            });
            var areaStr = attribute.soilArea;
        	if (Number(areaStr) === 0) {
        		areaStr = "<0.001"
        	}
            if (ifOnlyWater) {
                htmlString = htmlString + "<tr class=\"active\"><td><input type=\"checkbox\" disabled/></td><td>" + attribute.musym + "</td><td class=\"text-left\">" + attribute.soilName + "</td><td>" + areaStr + "</td><td style=\"display:none;\">" + attribute.mukey + "</td></tr>";
            } else {
            	if (attribute.unitPct >= 5) {
                	htmlString = htmlString + "<tr class=\"active\"><td><input type=\"checkbox\" name=\"link\" checked onchange=\"checkAllSub(this)\" value=\"" + attribute.mukey + "\"/></td><td>" + attribute.musym + "</td><td class=\"text-left\"><a href=\"#\" data-toggle=\""+ attribute.mukey + "\" title=\"Map Unit Composition\" data-html=\"true\" data-trigger=\"focus\" data-content='" + soilTypeHtml1 + soilTypeHtml2 + soilTypeHtml3 + "'>" + attribute.soilName + "</a></td><td>" + areaStr + "</td><td style=\"display:none;\">" + attribute.mukey + "</td></tr>";
                } else {
                	htmlString = htmlString + "<tr class=\"active\"><td><input type=\"checkbox\" name=\"link\" onchange=\"checkAllSub(this)\" value=\"" + attribute.mukey + "\"/></td><td>" + attribute.musym + "</td><td class=\"text-left\"><a href=\"#\" data-toggle=\""+ attribute.mukey + "\" title=\"Map Unit Composition\" data-html=\"true\" data-trigger=\"focus\" data-content='" + soilTypeHtml1 + soilTypeHtml2 + soilTypeHtml3 + "'>" + attribute.soilName + "</a></td><td>" + areaStr + "</td><td style=\"display:none;\">" + attribute.mukey + "</td></tr>";
                }
            }
            htmlString = htmlString + compHtmlStr;
        });
        
        return htmlString;
    }
});



function checkAll(source) {
    //console.log("Inside checkall");
    var checkboxes = document.getElementsByName('link');
    var checked = source.innerHTML === "Select All";
    //console.log(checkboxes);
    for (var i = 0; i < checkboxes.length; i++){
        checkboxes[i].checked = checked;
        checkAllSub(checkboxes[i]);
    }
    if (checked) {
        source.innerHTML = "Unselect All";
    } else {
        source.innerHTML = "Select All";
    }
}

function checkAllSub(source) {
    var checkboxes = document.getElementsByName('link_' + source.value);
    for (var i = 0; i < checkboxes.length; i++){
        checkboxes[i].checked = source.checked;
    }
	
}