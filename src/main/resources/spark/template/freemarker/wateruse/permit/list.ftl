<!DOCTYPE html>
<html>
    <head>
        <#include "../../header.ftl">
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
                width: 216px;
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

        <#include "../../nav.ftl">

        <div class="container">

            <fieldset>
                <legend>Permit List</legend>
                <#if operation_result == "Failed" >
                <p>No Permit Exists for current user</p>
                <#else>
                <table id="t01">
                    <tr>
                        <th>Permit ID</th>
                        <th>Crop</th>
                        <th>Irrigation</th>
                        <th>Location(ET/Rain)</th>
                        <th id="descCol">Option</th>
                    </tr>
                    <#list permits as permit>
                    <tr>
                        <#if currentUserRank?? && currentUserRank == "admin" >
                        <td><a href='/wateruse/permit/find?permit_id=${permit["permit_id"]!}&user_id=${permit["user_id"]!}' data-toggle="tooltip" title="View/Edit this permit record">${permit["permit_id"]!}</a></td>
                        <#else>
                        <td><a href='/wateruse/permit/find?permit_id=${permit["permit_id"]!}' data-toggle="tooltip" title="View/Edit this permit record">${permit["permit_id"]!}</a></td>
                        </#if>
                        <td>${permit["crop_name"]!}</td>
                        <td>${irSysNameList[permit["irr_type"]?number]!}</td>
                        <td>${permit["et_loc"]!} / ${permit["rain_loc"]!}</td>
                        <#if currentUserRank?? && currentUserRank == "admin" >
                        <td>
                            <a href='/simulation/afsirs_load?permit_id=${permit["permit_id"]!}&user_id=${permit["user_id"]!}' class="btn btn-default" data-toggle="tooltip" title="Run AFSIRS module with this permit data">&nbsp;Run&nbsp;&nbsp;</a>
                            <a href='/simulation/afsirs_load?permit_id=${permit["permit_id"]!}&user_id=${permit["user_id"]!}' class="btn btn-default disabled" data-toggle="tooltip" title="Delete the permit record from system">Delete</a>
                            <a href='/simulation/afsirs?permit_id=${permit["permit_id"]!}&user_id=${permit["user_id"]!}' class="btn btn-default" data-toggle="tooltip" title="View the AFSIRS result from last run">Result</a>
                        </td>
                        <#else>
                        <td>
                            <a href='/simulation/afsirs_load?permit_id=${permit["permit_id"]!}' class="btn btn-default" data-toggle="tooltip" title="Run AFSIRS module with this permit data">&nbsp;Run&nbsp;&nbsp;</a>
                            <a href='/simulation/afsirs_load?permit_id=${permit["permit_id"]!}' class="btn btn-default disabled" data-toggle="tooltip" title="Delete the permit record from system">Delete</a>
                            <a href='/simulation/afsirs?permit_id=${permit["permit_id"]!}' class="btn btn-default" data-toggle="tooltip" title="View the AFSIRS result from last run">Result</a>
                        </td>
                        </#if>
                    </tr>
                    <#else>
                    <tr><td colspan="4">No permit has been created yet.</td></tr>
                    </#list>
                </table>
                </#if>
            </fieldset>
        </div>

        <#include "../../footer.ftl">
    </body>
</html>
