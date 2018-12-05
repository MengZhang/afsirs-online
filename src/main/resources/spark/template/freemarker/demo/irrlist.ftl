
<!DOCTYPE html>
<html>
    <head>
        <#include "../header.ftl">
        <style>
            table#t01, table#t01 th, table#t01 td {
                border: 1px solid black;
                border-collapse: collapse;
            }
            table#t01 th, table#t01 td {
                padding: 5px;
                text-align: left;
            }
            th#descCol {
                width: 400px;
            }
            table#t01 {
                width: 100%;
            }
            table#t01 tr:nth-child(even) {
                background-color: #eee;
            }
            table#t01 tr:nth-child(odd) {
                background-color:#fff;
            }
            table#t01 th {
                background-color: lightskyblue;
                color: black;
            }
            /* The actual popup */
            .popuptext {
                display:none;
                background-color: #555;
                color: #fff;
                text-align: center;
                border-radius: 6px;
                padding: 0px 8px 0px 8px;
                padding-left: 5px;
                margin-left: 10px;
                position: absolute;
                z-index: 1;
            }

            /* Popup arrow */
            .popuptext::after {
                width: 0; 
                height: 0; 
                border-top: 5px solid transparent;
                border-bottom: 5px solid transparent; 
                border-right:5px solid #555;
                content: "";
                position: absolute;
                top: 35%;
                left: 0%;
                margin-left: -5px;
            }
        </style>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
        <script>
            function switchIrrSys(select) {
                var cur = select.value;
                if (cur.match(/\d+\w+/i) !== null) {
                    cur = cur.substring(0, cur.length - 1);
                    select.value = cur;
                    $("#irrSysInfo").fadeIn();
                    setTimeout(function () {
                        $("#irrSysInfo").fadeOut();
                    }, 2000);
                }

            }
            
            function hideSub(check) {
                var select = document.getElementById("irr_depth_type");
                for (var i = 0; i < select.options.length; i++) {
                    if (select.options[i].value.match(/\d+\w+/i) !== null) {
                        select.options[i].hidden = check.checked;
                    }
                }
            }
            

            function switchIrrSub(select) {
                for (var i = 0; i < select.options.length; i++) {
                    document.getElementById("irr_depth_type_sub_" + i).style.display = "none";
                }
                document.getElementById("irr_depth_type_sub_" + select.selectedIndex).style.display = "block";
            }
        </script>
    </head>

    <body>

        <#include "../nav.ftl">

        <div class="container">
            Style 1: <br/>
            <select id="irr_depth_type" name="irr_depth_type" onchange="switchIrrSys(this)" style="font-weight:bold" title="Define Irrigation Water depths per application">
                <option value="0" selected style="font-weight:bold">None</option>
                <option value="1" style="font-weight:bold">Micro, Drip</option>
                <option value="1a">|-- Drip</option>
                <option value="1b">|-- Micro-irrigation Drip</option>
                <option value="1c">|-- Overhead Drip</option>
                <option value="1d">|-- Micro-sprinkler</option>
                <option value="1e">|-- Low Volume</option>
                <option value="1f">|-- Drip -With Plastic</option>
                <option value="1g">|-- Drip-Without Plastic</option>
                <option value="1h">|-- Drip Irrigation (Surface and Subsurface) Trickle</option>
                <option value="2" style="font-weight:bold">Micro, Spray</option>
                <option value="2a">|-- Micro-Jet</option>
                <option value="2b">|-- Spray Jet Spinners</option>
                <option value="2c">|-- Low Volume Spray</option>
                <option value="3" style="font-weight:bold">Center Pivot</option>
                <option value="3a">|-- Center Pivot/Linear Move</option>
                <option value="4" style="font-weight:bold">Sprinkler</option>
                <option value="4a">|-- Overhead Sprinkler</option>
                <option value="4b">|-- Sprinkler (overhead, under tree)</option>
                <option value="4c">|-- Overhead (multiple sprinkler)</option>
                <option value="4d">|-- Sprinkler (Over Plant)</option>
                <option value="4e">|-- Sprinkler (Under Tree)</option>
                <option value="5" style="font-weight:bold">Gun </option>
                <option value="5a">|-- Traveling Gun</option>
                <option value="5b">|-- Walking Gun</option>
                <option value="5c">|-- Large Gun Sprinkler</option>
                <option value="5d">|-- Volume Gun</option>
                <option value="5e">|-- Portable Gun</option>
                <option value="6" style="font-weight:bold">Seepage Fully Enclosed</option>
                <option value="7" style="font-weight:bold">Seepage</option>
                <option value="7a">|-- Semi-Closed Ditch</option>
                <option value="7b">|-- Seepage/Furrow</option>
                <option value="7c">|-- Sub-irrigation</option>
                <option value="7d">|-- Semi-closed Flow-Through</option>
                <option value="7e">|-- Flood/Seepage</option>
                <option value="7f">|-- Seepage - Citrus, Hay, Pasture</option>
                <option value="7g">|-- Seepage - With Plastic</option>
                <option value="7h">|-- Seepage - Without Plastic</option>
                <option value="8" style="font-weight:bold">Flood/Canal Seepage</option>
            </select>
            <span id="irrSysInfo" class="popuptext">Selection has been change to associated main category</span>
            <br/>
            <label><input type="checkbox" onchange="hideSub(this)" value="test"> hide sub category from the list</label>
            <br/>
            <br/>
            Style 2: <br/>
            Primary category: <br/>
            <select id="irr_depth_type_main" name="irr_depth_type" onchange="switchIrrSub(this);" title="Define Irrigation Water depths per application">
                <option value="-1" selected>None</option>
                <option value="1">Micro, Drip</option>
                <option value="2">Micro, Spray</option>
                <option value="3">Center Pivot</option>
                <option value="4">Sprinkler</option>
                <option value="5">Gun </option>
                <option value="6">Seepage Fully Enclosed</option>
                <option value="7">Seepage</option>
                <option value="8">Flood/Canal Seepage</option>
            </select>
            <br/>
            Sub category: <br/>
            <ul id="irr_depth_type_sub_0" name="irr_depth_type" title="Define Irrigation Water depths per application">
                <li>None</li>
            </ul>
            <ul id="irr_depth_type_sub_1" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <li value="1">Drip</li>
                <li value="1">Micro-irrigation Drip</li>
                <li value="1">Overhead Drip</li>
                <li value="1">Micro-sprinkler</li>
                <li value="1">Low Volume</li>
                <li value="1">Drip -With Plastic</li>
                <li value="1">Drip-Without Plastic</li>
                <li value="1">Drip Irrigation (Surface and Subsurface) Trickle</li>
            </ul>
            <ul id="irr_depth_type_sub_2" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <li value="2">Micro-Jet</li>
                <li value="2">Spray Jet Spinners</li>
                <li value="2">Low Volume Spray</li>
            </ul>
            <ul id="irr_depth_type_sub_3" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <option value="3">Center Pivot/Linear Move</option>
            </ul>
            <ul id="irr_depth_type_sub_4" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <li value="4">Overhead Sprinkler</li>
                <li value="4">Sprinkler (overhead, under tree)</li>
                <li value="4">Overhead (multiple sprinkler)</li>
                <li value="4">Sprinkler (Over Plant)</li>
                <li value="4">Sprinkler (Under Tree)</li>
            </ul>
            <ul id="irr_depth_type_sub_5" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <li value="5">Traveling Gun</li>
                <li value="5">Walking Gun</li>
                <li value="5">Large Gun Sprinkler</li>
                <li value="5">Volume Gun</li>
                <li value="5">Portable Gun</li>
            </ul>
            <ul id="irr_depth_type_sub_6" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <li value="-1" selected>None</li>
            </ul>
            <ul id="irr_depth_type_sub_7" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <li value="7">Semi-Closed Ditch</li>
                <li value="7">Seepage/Furrow</li>
                <li value="7">Sub-irrigation</li>
                <li value="7">Semi-closed Flow-Through</li>
                <li value="7">Flood/Seepage</li>
                <li value="7">Seepage - Citrus, Hay, Pasture</li>
                <li value="7">Seepage - With Plastic</li>
                <li value="7">Seepage - Without Plastic</li>
            </ul>
            <ul id="irr_depth_type_sub_8" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <li value="-1" selected>None</li>
            </ul>
        </div>

        <#include "../footer.ftl">
    </body>
</html>
