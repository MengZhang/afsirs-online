<!DOCTYPE html>
<html>
    <head>
        <#include "../header.ftl">
        <script>
            function deleteSoilData(soilId, userId, rowBtn) {
                rowBtn.disabled = true;
                $.post("/soildata/delete",
                    {
                      soil_id: soilId,
                      user_id: userId
                    },
                    function(status){
                        rowBtn.disabled = false;
                        if (status) {
                            var i = rowBtn.parentNode.parentNode.rowIndex;
                            document.getElementById("t01").deleteRow(i);
                        } else {
                            alert("Soil Data is not removed correctly, please try again!");
                        }
                    }
                );
            }
        </script>
    </head>

    <body>

        <#include "../nav.ftl">

        <div class="container">

            <fieldset>
                <legend>Soil Data List</legend>
                <#if operation_result == "Failed" >
                <div class="alert alert-info">No Saved Soil Data Exists for current user</div>
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
                            <a href='#' class="btn btn-default" onclick="" data-toggle="tooltip" title="Delete the permit record from system" style="display: none;">Publish</a>
                            <a href='/simulation/afsirs_load?permit_id=${soil["permit_id"]!}&user_id=${soil["user_id"]!}' class="btn btn-default disabled" data-toggle="tooltip" title="Delete the permit record from system" style="display: none;">Share</a>
                            <a href='#' class="btn btn-default" onclick='deleteSoilData("${soil.soil_id!}", "${soil.user_id!}", this);' data-toggle="tooltip" title="Delete the soil data record from system">Delete</a>
                        </td>
                        <#else>
                        <td>
                            <a href='/simulation/afsirs_load?permit_id=${soil["permit_id"]!}' class="btn btn-default disabled" data-toggle="tooltip" title="Delete the permit record from system" style="display: none;">Publish</a>
                            <a href='/simulation/afsirs_load?permit_id=${soil["permit_id"]!}' class="btn btn-default disabled" data-toggle="tooltip" title="Delete the permit record from system" style="display: none;">Share</a>
                            <a href='#' class="btn btn-default" onclick='deleteSoilData("${soil.soil_id!}", "${currentUser!}", this);' data-toggle="tooltip" title="Delete the soil data record from system">Delete</a>
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
