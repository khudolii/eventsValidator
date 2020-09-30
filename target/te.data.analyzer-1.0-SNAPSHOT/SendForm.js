window.addEventListener('load', function () {
    const driverId = document.getElementById("driverId");
    const dateFrom = document.getElementById("dateFrom");
    const dateTo = document.getElementById("dateTo");
    const file = {
        dom: document.getElementById("data"),
        binary: null
    };
    const reader = new FileReader();
    reader.addEventListener("load", function () {
        file.binary = reader.result;
    });
    if (file.dom.files[0]) {
        reader.readAsBinaryString(file.dom.files[0]);
    }

    // If not, read the file once the user selects it.
    file.dom.addEventListener("change", function () {
        if (reader.readyState === FileReader.LOADING) {
            reader.abort();
        }

        reader.readAsBinaryString(file.dom.files[0]);
    });

    // sendData is our main function
    function sendData() {
        // If there is a selected file, wait it is read
        // If there is not, delay the execution of the function
        if (!file.binary && file.dom.files.length > 0) {
            setTimeout(sendData, 10);
            return;
        }

        // To construct our multipart form data request,
        // We need an XMLHttpRequest instance
        const XHR = new XMLHttpRequest();

        // We need a separator to define each part of the request
        const boundary = "blob";

        // Store our body request in a string.
        let data = "";
        // Text data is simpler
        // Start a new part in our body's request
        data += "--" + boundary + "\r\n";
        data += 'content-disposition: form-data; name="' + driverId.name + '"\r\n';
        data += '\r\n';
        data += driverId.value + "\r\n";
        data += "--" + boundary + "\r\n";
        data += 'content-disposition: form-data; name="' + dateTo.name + '"\r\n';
        data += '\r\n';
        data += dateTo.value + "\r\n";
        data += "--" + boundary + "\r\n";
        data += 'content-disposition: form-data; name="' + dateFrom.name + '"\r\n';
        data += '\r\n';
        data += dateFrom.value + "\r\n";
        // So, if the user has selected a file
        if (file.dom.files[0]) {
            // Start a new part in our body's request
            data += "--" + boundary + "\r\n";

            // Describe it as form data
            data += 'content-disposition: form-data; '
                // Define the name of the form data
                + 'name="' + file.dom.name + '"; '
                // Provide the real name of the file
                + 'filename="' + file.dom.files[0].name + '"\r\n';
            // And the MIME type of the file
            data += 'Content-Type: ' + file.dom.files[0].type + '\r\n';

            // There's a blank line between the metadata and the data
            data += '\r\n';

            // Append the binary data to our body's request
            data += file.binary + '\r\n';
        }


        // Once we are done, "close" the body's request
        data += "--" + boundary + "--";

        // Define what happens on successful data submission
        XHR.addEventListener('load', function (event) {
            alert('The file has been sent to the server and verified! You can download the PDF report.');
            document.getElementById('downloadReport').removeAttribute('disabled');
            document.getElementById('downloadReport').removeAttribute('class');
            document.getElementById('downloadReport').setAttribute('class', 'btn btn-info');

        });

        // Define what happens in case of error
        XHR.addEventListener('error', function (event) {
            alert('Oops! Something went wrong.');
        });

        // Set up our request
        XHR.open('POST', 'http://localhost:8081/getCsvReport');

        // Add the required HTTP header to handle a multipart form data POST request
        XHR.setRequestHeader('Content-Type', 'multipart/form-data; boundary=' + boundary);

        // And finally, send our data.
        XHR.send(data);
    }

    // Access our form...
    const form = document.getElementById("csvForm");

    // ...to take over the submit event
    form.addEventListener('submit', function (event) {
        event.preventDefault();
        sendData();
    });
});

function hideBtn() {
    document.hhhgs.submit();
    document.getElementById('downloadReport').setAttribute('disabled', true);
    document.getElementById('downloadReport').removeAttribute('class');
    document.getElementById('downloadReport').setAttribute('class', 'btn btn-outline-dark');
}

window.addEventListener('DOMContentLoaded', function () {
    var select = document.querySelector('#actionName'),
        hide = document.querySelectorAll('.hide');

    function change() {
        [].forEach.call(hide, function (el) {
            var add = el.classList.contains(select.value) ? "add" : "remove"
            el.classList[add]('show');
        });
    }

    select.addEventListener('change', change);
    change()
});

window.addEventListener('load', function () {
    const form = document.getElementById("qaTool");

    // ...to take over the submit event
    form.addEventListener('submit', function (event) {
        event.preventDefault();
        alert("Done")
    });
});