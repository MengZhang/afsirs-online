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
    </head>

    <body>

        <#include "../nav.ftl">

        <div class="container">

            <fieldset>
                <legend>Soil Data List</legend>
                <#if operation_result == "Failed" >
                <p>No Saved Soil Data Exists for current user</p>
                <#else>
                <table id="t01">
                    <tr>
                        <th>Name</th>
                        <th>Location (long/lat)</th>
                        <th>Area (Planted/Total) (acres)</th>
                        <th>Status</th>
                        <th>Option</th>
                    </tr>
                    <#list soils as soil>
                    <tr>
                        <!-- Name -->
                        <#if currentUserRank?? && currentUserRank == "admin" && soil["soil_source"] == "MAP">
                        <td><a href='/datatools/soilmap/?soil_id=${soil["soil_id"]!}&zoom=${soil["zoom"]!}&user_id=${soil["user_id"]!}' data-toggle="tooltip" title="View/Edit this soil data record">${soil["soil_unit_name"]!}</a> [${soil["user_id"]!}]</td>
                        <#elseif soil["soil_source"] == "MAP" >
                        <td><a href='/datatools/soilmap/?soil_id=${soil["soil_id"]!}&zoom=${soil["zoom"]!}' data-toggle="tooltip" title="View/Edit this permit record">${soil["soil_unit_name"]!}</a></td>  
                        <#else>
                        <td>${soil["soil_unit_name"]!}</td>
                        </#if>
                        <!-- Location -->
                        <#if soil["longitude"]?? || soil["latitude"]??>
                        <td>${soil["longitude"]?number!"?"}, ${soil["latitude"]?number!"?"}</td>
                        <#else>
                        <td>N/A</td>
                        </#if>
                        <!-- Area -->
                        <td>${soil["plantedArea"]!"?"} / ${soil["totalArea"]!"?"}</td>
                        <!-- Status -->
                        <td>Owned</td>
                        <!-- Option -->
                        <#if currentUserRank?? && currentUserRank == "admin" >
                        <td>
                            <a href='/simulation/afsirs_load?permit_id=${soil["permit_id"]!}&user_id=${soil["user_id"]!}' class="btn btn-default disabled" data-toggle="tooltip" title="Delete the permit record from system">Publish</a>
                            <a href='/simulation/afsirs_load?permit_id=${soil["permit_id"]!}&user_id=${soil["user_id"]!}' class="btn btn-default disabled" data-toggle="tooltip" title="Delete the permit record from system">Share</a>
                            <a href='/simulation/afsirs_load?permit_id=${soil["permit_id"]!}&user_id=${soil["user_id"]!}' class="btn btn-default disabled" data-toggle="tooltip" title="Delete the permit record from system">Delete</a>
                        </td>
                        <#else>
                        <td>
                            
                            <a href='/simulation/afsirs_load?permit_id=${soil["permit_id"]!}' class="btn btn-default disabled" data-toggle="tooltip" title="Delete the permit record from system">Publish</a>
                            <a href='/simulation/afsirs_load?permit_id=${soil["permit_id"]!}' class="btn btn-default disabled" data-toggle="tooltip" title="Delete the permit record from system">Share</a>
                            <a href='/simulation/afsirs_load?permit_id=${soil["permit_id"]!}' class="btn btn-default disabled" data-toggle="tooltip" title="Delete the permit record from system">Delete</a>
                        </td>
                        </#if>
                    </tr>
                    <#else>
                    <tr><td colspan="4">No soil data has been created yet.</td></tr>
                    </#list>
                </table>
                </#if>
            </fieldset>
        </div>

        <#include "../footer.ftl">
    </body>
</html>
