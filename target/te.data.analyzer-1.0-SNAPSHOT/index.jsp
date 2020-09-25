<%--
  Created by IntelliJ IDEA.
  User: evgeniy
  Date: 18.09.20
  Time: 02:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Check event data</title>
</head>
<link rel="stylesheet" href="bootstrap.min.css">
<script src="SendForm.js"></script>
<body>
<div class="row col-sm" style="top: 20px;">
    <div class="col-sm-6">
        <div class="card">
            <div class="card-body">
                <h4 class="card-title alert alert-success">Check events from DB</h4>
                <form action="getReport" method="GET">
                    <div class="input-group mb-3">
                        <div class="input-group-prepend">
                            <span class="input-group-text">Driver ID</span>
                        </div>
                        <input name="driverId"  placeholder="Driver ID" pattern="[0-9]{2,7}"
                               class="form-control"/>
                        <div class="input-group-prepend">
                            <span class="input-group-text">Truck ID</span>
                        </div>
                        <input name="truckId"  placeholder="Truck ID" pattern="[0-9]{2,7}"
                               class="form-control"/>
                    </div>
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <span class="input-group-text">Date From and Date To</span>
                        </div>
                        <input name="dateFrom" type="date" required
                               pattern="[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"
                               class="form-control"/>
                        <input name="dateTo" type="date" required
                               pattern="[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"
                               class="form-control"/>
                    </div>
                    <br>
                        <input type="submit" value="Generate report" class="btn btn-primary btn-lg btn-block"/>
                </form>
            </div>
        </div>
    </div>
    <div class="col-sm-6">
        <div class="card">
            <div class="card-body">
                <h4 class="card-title alert alert-primary">Check CSV file</h4>
                <form id="csvForm" enctype="multipart/form-data" method="post" class="form-group">
                    <div class="input-group mb-3">
                        <div class="input-group-prepend">
                            <span class="input-group-text">ID</span>
                        </div>
                        <input name="driverId" id="driverId" required placeholder="Driver ID" pattern="[0-9]{2,7}"
                               class="form-control"/>
                    </div>
                    <div class="input-group" title="Промежуток за который генерировали CSV отчет">
                        <div class="input-group-prepend">
                            <span class="input-group-text" id="">Date From and Date To</span>
                        </div>
                        <input name="dateFrom" id="dateFrom"  type="date" required
                               pattern="[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"
                               class="form-control"/>
                        <input name="dateTo" id="dateTo" type="date" required
                               pattern="[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"
                               class="form-control"/>
                    </div>
                    <br>
                    <div class="input-group mb-3">
                        <div class="input-group-prepend">
                            <span class="input-group-text" id="basic-addon1">CSV File</span>
                        </div>
                        <input id="data" name="data" type="file" class="btn btn-outline-info" required
                               accept="text/txt"><br>
                    </div>
                    <button class="btn btn-primary btn-lg btn-block">Generate report</button>
                </form>
                <form id="getCsvReportBtn" name="hhhgs" action="getCsvReport" method="GET">
                    <button id="downloadReport" disabled="true" class="btn btn-outline-dark btn-lg btn-block"
                            onclick="hideBtn()"
                            type="submit" value="Download Report">Download Report
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
