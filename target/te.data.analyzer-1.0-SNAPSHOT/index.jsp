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
<div class="row">
    <div class="col-6">
        <h4>Check events from DB</h4>
        <form action="getReport" method="GET">
            <label>Driver ID</label>
            <input name="driverId" placeholder="Driver ID" pattern="[0-9]{2,7}"  class="form-control"/>
            <br>
            <label>Date From</label>
            <input name="dateFrom" type="date" pattern="[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}" class="form-control"/>
            <br>
            <label>Date To</label>
            <input name="dateTo" type="date" pattern="[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"  class="form-control"/>
            <br>
            <input type="submit" value="Submit" class="btn btn-primary"/>
        </form>
    </div>
    <div class="col-6">
        <h4>Check CSV file</h4>
        <form id="csvForm" enctype="multipart/form-data" method="post" class="form-group">
            <label>Driver ID</label>
            <input id="driverId" placeholder="Driver ID" name="driverId" pattern="[0-9]{2,7}" class="form-control">
            <br>
            <label>Date From</label>
            <input id="dateFrom" name="dateFrom" type="date"
                   pattern="[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"  class="form-control"/>
            <br>
            <label>Date To</label>
            <input id="dateTo" name="dateTo" type="date"
                   pattern="[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"  class="form-control"/>
            <br>
            <label>CSV </label>
            <input id="data" name="data" type="file" class="btn btn-outline-info" accept="text/txt"><br>
            <br>
            <button class="btn btn-primary">Submit</button>
            <br>
        </form>
        <form action="getCsvReport" method="GET">
            <input class="btn btn-outline-info" type="submit" value="Download Report"/>
        </form>
    </div>
</div>
</body>
</html>
