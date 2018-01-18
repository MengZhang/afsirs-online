<!DOCTYPE html>
<html>
    <head>
        <#include "header.ftl">
    </head>

    <body>

        <#include "nav.ftl">

        <div id="myCarousel" class="carousel slide" data-ride="carousel">
            <!-- Indicators -->
            <ol class="carousel-indicators">
                <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
                <li data-target="#myCarousel" data-slide-to="1"></li>
                <li data-target="#myCarousel" data-slide-to="2"></li>
            </ol>

            <!-- Wrapper for slides -->
            <div class="carousel-inner" role="listbox">
                <div class="item active">
                    <img src="1-1-S.jpg" alt="field" width="1200" height="521">
                    <div class="carousel-caption ">
                        <h3>AFSIRS Online Prototype</h3>
                        <p>Simulate the water usage</p>
                    </div>
                </div>

                <div class="item">
                    <img src="1-2-S.jpg" alt="field" width="1200" height="521">
                    <div class="carousel-caption">
                        <h3>AFSIRS Online Prototype</h3>
                        <p>Collect site information</p>
                    </div>      
                </div>

                <div class="item">
                    <img src="1-3-S.jpg" alt="field" width="1200" height="521">
                    <div class="carousel-caption">
                        <h3>AFSIRS Online  Prototype</h3>
                        <p>Manage soil and water use data</p>
                    </div>      
                </div>
            </div>

            <!-- Left and right controls -->
            <a class="left carousel-control" href="#myCarousel" role="button" data-slide="prev">
                <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                <span class="sr-only">Previous</span>
            </a>
            <a class="right carousel-control" href="#myCarousel" role="button" data-slide="next">
                <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
                <span class="sr-only">Next</span>
            </a>
        </div>

        <div class="container">

            <div class="container-fluid text-left">
                <h3>To run AFSIRS module</h3>
                <p>Select Water Use from menu bar</p>
                <p>Choose Create Permit and input your site info and configure your simulation</p>
                <p>Once you save your Permit, you can find it in the Permit List</p>
                <p>Click Run AFSIRS to show the graph or download as PDF file</p>
            </div>

        </div>
        <#include "footer.ftl">

    </body>
</html>
