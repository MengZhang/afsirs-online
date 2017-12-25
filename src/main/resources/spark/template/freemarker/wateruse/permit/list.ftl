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
                width: 600px;
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
                        <th>Name</th>
                        <th>Crop</th>
                        <th id="descCol">Description</th>
                        <th>Option</th>
                    </tr>
                    <#list permits as permit>
                    <tr>
                        <td><a href="/wateruse/permit/find?permit_id=${permit["permit_id"]!}">${permit["permit_id"]!}</a></td>
                        <td>${permit["crop_name"]!}</td>
                        <td>${permit["description"]!}</td>
                        <td><a href="/simulation/afsirs?permit_id=${permit["permit_id"]!}" class="btn btn-default">Run AFSIRS</a></td>
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
