<nav class="navbar navbar-default navbar-fixed-top navbar-inverse">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand navbar-static-top"><img src="/LOGO.png" height="100%" alt="Agricultural Field Scale Irrigation Requirements Simulation"></a>
        </div>
        <ul class="nav navbar-nav">
            <li class="active">
                <a href="/"><span class="glyphicon glyphicon-home"></span> Home</a>
            </li>
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><span class="glyphicon glyphicon-tint"></span> Water Use <span class="caret"></span></a>
                <ul class="dropdown-menu" role="menu">
                    <li><a href="/simulation/afsirs"><span class="glyphicon glyphicon-file"></span> Create Permit </a></li>
                    <li><a href="/simulation/afsirs"><span class="glyphicon glyphicon-list-alt"></span> Permit List </a></li>
                    <li><a href="/simulation/afsirs"><span class="glyphicon glyphicon-search"></span> Find Permit </a></li>
                </ul>
            </li>
            <li class="dropdown">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><span class="glyphicon glyphicon-briefcase"></span> Data Tools <span class="caret"></span></a>
                <ul class="dropdown-menu" role="menu">
                    <li><a href="/data/soilmap"><span class="glyphicon glyphicon-globe"></span> Soil Map</a></li>
                </ul>
            </li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
            <#if currentUser?? >
            <li class="active navbar-left">
                <a>Hello, ${currentUser}</a>
            </li>
            <li class="navbar-defalut">
                <a href="/logout"><span class="glyphicon glyphicon-log-out"></span> Logout&nbsp;&nbsp;&nbsp;&nbsp;</a>
            </li>
            <#else>
            <li class="navbar-left">
                <a href="/login"><span class="glyphicon glyphicon-log-in"></span> Login</a>
            </li>
            <li class="navbar-defalut">
                <a href="/register"><span class="glyphicon glyphicon-user"></span> Register&nbsp;&nbsp;&nbsp;&nbsp;</a>
            </li>
            </#if>
        </ul>
    </div><!-- /container -->
</nav><!-- /navbar wrapper -->