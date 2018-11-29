
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
        </style>
        <script>
            function switchIrrSub(select) {
                for (var i = 0; i < select.options.length;i++) {
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
            <select id="irr_depth_type" name="irr_depth_type" title="Define Irrigation Water depths per application">
                <option value="0" selected>None</option>
                <option value="1" style="font-weight:bold">Micro, Drip</option>
                <option value="1">|-- Drip</option>
                <option value="1">|-- Micro-irrigation Drip</option>
                <option value="1">|-- Overhead Drip</option>
                <option value="1">|-- Micro-sprinkler</option>
                <option value="1">|-- Low Volume</option>
                <option value="1">|-- Drip -With Plastic</option>
                <option value="1">|-- Drip-Without Plastic</option>
                <option value="1">|-- Drip Irrigation (Surface and Subsurface) Trickle</option>
                <option value="2" style="font-weight:bold">Micro, Spray</option>
                <option value="2">|-- Micro-Jet</option>
                <option value="2">|-- Spray Jet Spinners</option>
                <option value="2">|-- Low Volume Spray</option>
                <option value="3" style="font-weight:bold">Center Pivot</option>
                <option value="3">|-- Center Pivot/Linear Move</option>
                <option value="4" style="font-weight:bold">Sprinkler</option>
                <option value="4">|-- Overhead Sprinkler</option>
                <option value="4">|-- Sprinkler (overhead, under tree)</option>
                <option value="4">|-- Overhead (multiple sprinkler)</option>
                <option value="4">|-- Sprinkler (Over Plant)</option>
                <option value="4">|-- Sprinkler (Under Tree)</option>
                <option value="5" style="font-weight:bold">Gun </option>
                <option value="5">|-- Traveling Gun</option>
                <option value="5">|-- Walking Gun</option>
                <option value="5">|-- Large Gun Sprinkler</option>
                <option value="5">|-- Volume Gun</option>
                <option value="5">|-- Portable Gun</option>
                <option value="6" style="font-weight:bold">Seepage Fully Enclosed</option>
                <option value="7" style="font-weight:bold">Seepage</option>
                <option value="7">|-- Semi-Closed Ditch</option>
                <option value="7">|-- Seepage/Furrow</option>
                <option value="7">|-- Sub-irrigation</option>
                <option value="7">|-- Semi-closed Flow-Through</option>
                <option value="7">|-- Flood/Seepage</option>
                <option value="7">|-- Seepage - Citrus, Hay, Pasture</option>
                <option value="7">|-- Seepage - With Plastic</option>
                <option value="7">|-- Seepage - Without Plastic</option>
                <option value="8" style="font-weight:bold">Flood/Canal Seepage</option>
            </select>
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
            <select id="irr_depth_type_sub_0" name="irr_depth_type" title="Define Irrigation Water depths per application">
                <option value="-1" selected>None</option>
            </select>
            <select id="irr_depth_type_sub_1" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <option value="-1" selected>None</option>
                <option value="1">Drip</option>
                <option value="1">Micro-irrigation Drip</option>
                <option value="1">Overhead Drip</option>
                <option value="1">Micro-sprinkler</option>
                <option value="1">Low Volume</option>
                <option value="1">Drip -With Plastic</option>
                <option value="1">Drip-Without Plastic</option>
                <option value="1">Drip Irrigation (Surface and Subsurface) Trickle</option>
            </select>
            <select id="irr_depth_type_sub_2" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <option value="-1" selected>None</option>
                <option value="2">Micro-Jet</option>
                <option value="2">Spray Jet Spinners</option>
                <option value="2">Low Volume Spray</option>
            </select>
            <select id="irr_depth_type_sub_3" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <option value="-1" selected>None</option>
                <option value="3">Center Pivot/Linear Move</option>
            </select>
            <select id="irr_depth_type_sub_4" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <option value="-1" selected>None</option>
                <option value="4">Overhead Sprinkler</option>
                <option value="4">Sprinkler (overhead, under tree)</option>
                <option value="4">Overhead (multiple sprinkler)</option>
                <option value="4">Sprinkler (Over Plant)</option>
                <option value="4">Sprinkler (Under Tree)</option>
            </select>
            <select id="irr_depth_type_sub_5" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <option value="-1" selected>None</option>
                <option value="5">Traveling Gun</option>
                <option value="5">Walking Gun</option>
                <option value="5">Large Gun Sprinkler</option>
                <option value="5">Volume Gun</option>
                <option value="5">Portable Gun</option>
            </select>
            <select id="irr_depth_type_sub_6" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <option value="-1" selected>None</option>
            </select>
            <select id="irr_depth_type_sub_7" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <option value="-1" selected>None</option>
                <option value="7">Semi-Closed Ditch</option>
                <option value="7">Seepage/Furrow</option>
                <option value="7">Sub-irrigation</option>
                <option value="7">Semi-closed Flow-Through</option>
                <option value="7">Flood/Seepage</option>
                <option value="7">Seepage - Citrus, Hay, Pasture</option>
                <option value="7">Seepage - With Plastic</option>
                <option value="7">Seepage - Without Plastic</option>
            </select>
            <select id="irr_depth_type_sub_8" name="irr_depth_type" title="Define Irrigation Water depths per application" style="display:none;">
                <option value="-1" selected>None</option>
            </select>
        </div>

        <#include "../footer.ftl">
    </body>
</html>
