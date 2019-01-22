<!DOCTYPE html>
<html>
    <head>
        <#include "header.ftl">
        <style>
            th, td {
                padding: 5px;
                text-align: left;
            }
        </style>
    </head>
    <body>

        <#include "nav.ftl">

        <div class="container">

            <legend>LOGIN PAGE</legend>
            <#if operation_result == "Failed" >
            <div class="alert alert-warning">LOGIN_AUTH_FAILED</div>
            </#if>
            <form id="loginForm" method="post">
                <div class="row">
                    <div class="form-group">
                        <div class="col-sm-3"><label>User Name :</label></div>
                        <div class="col-sm-9"><input type="text" name="username" placeholder="User Name" value="" required></div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-3"><label>Password :</label></div>
                        <div class="col-sm-9"><input type="password" name="password" placeholder="Passowrd" value="" required></div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-3"><input type="submit" value="Login"></div>
                    </div>
                </div>
            </form>
        </div>

        <#include "footer.ftl">
    </body>
</html>
