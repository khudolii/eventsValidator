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
<style type="text/css">
    .hide {
        display: none;
    }

    .hide.show {
        display: block;
    }

</style>
<body>
<div class="container">
    <div class="row" style="top: 20px;">
        <div class="col-sm-6">
            <div class="row-sm-6">
                <div class="card">
                    <div class="card-body">
                        <h4 class="card-title alert alert-success">Check events from DB</h4>
                        <form action="getReport" method="GET">
                            <div class="input-group mb-3">
                                <div class="input-group-prepend">
                                    <span class="input-group-text">Driver ID</span>
                                </div>
                                <input name="driverId" placeholder="Driver ID" pattern="[0-9]{2,7}"
                                       class="form-control"/>
                                <div class="input-group-prepend">
                                    <span class="input-group-text">Truck ID</span>
                                </div>
                                <input name="truckId" placeholder="Truck ID" pattern="[0-9]{2,7}"
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
            <div class="row-sm-6">
                <div class="card">
                    <div class="card-body">
                        <h4 class="card-title alert alert-primary">Check CSV file</h4>
                        <form id="csvForm" enctype="multipart/form-data" method="post" class="form-group">
                            <div class="input-group mb-3">
                                <div class="input-group-prepend">
                                    <span class="input-group-text">ID</span>
                                </div>
                                <input name="driverId" id="driverId" required placeholder="Driver ID"
                                       pattern="[0-9]{2,7}"
                                       class="form-control"/>
                            </div>
                            <div class="input-group" title="Промежуток за который генерировали CSV отчет">
                                <div class="input-group-prepend">
                                    <span class="input-group-text" id="">Date From and Date To</span>
                                </div>
                                <input name="dateFrom" id="dateFrom" type="date" required
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
        <div class="col-sm-6">
            <div class="card">
                <div class="card-body">
                    <h4 class="card-title alert alert-secondary">QA Tool</h4>
                    <div class="input-group mb-3 card-title alert alert-info">
                        <div class="input-group-prepend">
                            <label class="input-group-text" for="inputGroupSelect01">Action</label>
                        </div>
                        <select name="actionName" class="custom-select" id="actionName">
                            <option selected>Choose action...</option>
                            <option value="createEvent">Create Event</option>
                            <option value="addGroupOfEvent">Add Group Of Event</option>
                            <option value="findCoDrivers">Find Co-Drivers</option>
                        </select>
                    </div>
                    <form id="qaTool" action="QaTool" method="GET" class="hide createEvent">
                        <input value="createEvent" name="actionName" class="hide form-control">
                            <div class="input-group mb-3">
                                <div class="input-group-prepend">
                                    <label class="input-group-text" for="inputGroupSelect01">Event Type</label>
                                </div>
                                <select name="eventType" class="custom-select" id="inputGroupSelect01">
                                    <option selected>Choose event...</option>
                                    <option value="On Duty">ON DUTY</option>
                                    <option value="Off Duty">OFF DUTY</option>
                                    <option value="Sleep">SLEEPER BERTH</option>
                                    <option value="Driving">DRIVING</option>
                                    <option value="Intermediate">INTERMEDIATE EVENT</option>
                                    <option value="Login">LOGIN</option>
                                    <option value="Logout">LOGOUT</option>
                                    <option value="PowerUp">POWER UP</option>
                                    <option value="PowerDown">POWER DOWN</option>
                                    <option value="BDX">BDX</option>
                                    <option value="Cert">CERTIFICATION</option>
                                </select>
                            </div>
                            <div class="input-group mb-3">
                                <div class="input-group-prepend">
                                    <span class="input-group-text">Driver ID</span>
                                </div>
                                <input name="driverId" required placeholder="Driver ID" pattern="[0-9]{2,7}"
                                       class="form-control"/>
                                <div class="input-group-prepend">
                                    <span class="input-group-text">Truck ID</span>
                                </div>
                                <input name="truckId" required placeholder="Truck ID" pattern="[0-9]{2,7}"
                                       class="form-control"/>
                                <div class="input-group-prepend">
                                    <span class="input-group-text">Org ID</span>
                                </div>
                                <input name="orgId" required placeholder="Org ID" pattern="[0-9]{2,7}"
                                       class="form-control"/>
                            </div>
                            <div class="input-group mb-3">
                                <div class="input-group-prepend">
                                    <span class="input-group-text">Date</span>
                                </div>
                                <input name="date"  required id="date" type="date"
                                       pattern="[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"
                                       class="form-control"/>
                                <div class="input-group-prepend">
                                    <span class="input-group-text">Time</span>
                                </div>
                                <input required type="time" id="time" name="time"/>
                            </div>
                        <div class="input-group mb-3">
                            <div class="input-group-prepend">
                                <span class="input-group-text">RO</span>
                            </div>
                            <input required value="1" name="ro"  pattern="[0-9]"
                                   class="form-control"/>
                            <div class="input-group-prepend">
                                <span class="input-group-text">TVM</span>
                            </div>
                            <input required value="0" name="tvm"  pattern="[0-9]{1,7}"
                                   class="form-control"/>
                            <div class="input-group-prepend">
                                <span class="input-group-text">TEH</span>
                            </div>
                            <input name="teh" required value="0" pattern="[0-9]{1,7}"
                                   class="form-control"/>
                        </div>
                        <div class="input-group mb-3">
                            <div class="input-group-prepend">
                                <span class="input-group-text">Location</span>
                            </div>
                            <input required value="New York, NY, US" name="location"
                                   class="form-control"/>
                        </div>
                            <input type="submit" value="Submit" class="btn btn-primary btn-lg btn-block"/>
                    </form>
                    <form action="QaTool" method="GET">
                        <input value="addGroupOfEvent" name="actionName" class="hide form-control">
                        <div class="hide addGroupOfEvent">
                            <div class="input-group mb-3">
                                <div class="input-group-prepend">
                                    <span class="input-group-text">Driver ID</span>
                                </div>
                                <input name="driverId" required placeholder="Driver ID" pattern="[0-9]{2,7}"
                                       class="form-control"/>
                                <div class="input-group-prepend">
                                    <span class="input-group-text">Truck ID</span>
                                </div>
                                <input name="truckId" required placeholder="Truck ID" pattern="[0-9]{2,7}"
                                       class="form-control"/>
                            </div>
                            <div class="input-group mb-3">
                                <div class="input-group-prepend">
                                    <span class="input-group-text">Org ID</span>
                                </div>
                                <input name="orgId" required placeholder="Org ID" pattern="[0-9]{2,7}"
                                       class="form-control"/>
                                <div class="input-group-prepend">
                                    <span class="input-group-text">Num of Days</span>
                                </div>
                                <input name="days" placeholder="Days" pattern="[0-9]"
                                       class="form-control"/>
                            </div>
                            <input type="submit" value="Submit" class="btn btn-primary btn-lg btn-block"/>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
