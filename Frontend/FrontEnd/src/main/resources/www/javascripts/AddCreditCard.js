$(document).ready(function () {

    function AddCreditCard(){

        console.log("adding a credit card.");
        var id = "9999888877776666123";
        var firstname = "Kyle"
        var lastname = "Hefner"
        var expr = "2022-02-02"
        $.ajax({
            type: 'POST',
            url: 'http://127.0.0.1:3999/api/g/billing/creditcard/insert',
            contentType: 'application/json',
            dataType: "json",
            headers: {
                'email': Cookies.get('email'),
                'sessionID': Cookies.get('sessionID')
            },
            data: JSON.stringify({id: id, firstName: firstname, lastName: lastname, expiration: expr}),
            success: function (data, textStatus, jqXHR) {
                console.log("Success in inserting credit card...")
                var transactionID = jqXHR.getResponseHeader("transactionID");
                var RequestDelay = jqXHR.getResponseHeader("RequestDelay");
                console.log("transactionID: " + transactionID);
                console.log("RequestDelay" + RequestDelay);

                if (!textStatus == "nocontent") {
                    var dataResponse = JSON.parse(jqXHR.responseText);
                    var resultCode = dataResponse.resultCode;

                    if (resultCode == 131 || resultCode == 132 || resultCode == 133 || resultCode == 134) {

                        console.log("session has expired");
                        $('#SearchResponse').text("Session has expired...Redirecting to Login Page");
                        setTimeout(function () {
                            $('#Response').load("login.html");
                        }, 2500);
                    }
                }


                if (textStatus == "nocontent") {
                    console.log("In nocontent");
                    //now make request to report endpoint...
                    //RequestDelay = RequestDelay + 1000;

                    console.log("transactionID: " + transactionID)
                    console.log("Request Delay: " + RequestDelay)
                    getReport(transactionID, RequestDelay);


                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                var statusCode = jqXHR.status;
                console.log("status coder: " + statusCode);
                if (statusCode == 400) {
                    var data = JSON.parse(jqXHR.responseText);
                    console.log("jqXHR: " + jqXHR.responseText);
                    var resultCode = data.resultCode;
                    var message = data.message;
                    if (resultCode == -16) {
                        console.log("Email was not provided in request header");
                        $('#SearchResponse').text("Need to Login before searching movies...Redirecting to Login Page");
                        setTimeout(function () {
                            $('#Response').load("login.html");
                        }, 2500);
                    } else if (resultCode == -17) {
                        console.log("SessionID was not provided in request header");
                        $('#SearchResponse').text("Need to Login before searching movies...Redirecting to Login Page");
                        setTimeout(function () {
                            $('#Response').load("login.html");
                        }, 2500);
                    } else {
                        console.log("one of the other resultcodes from Verify session endpoint");
                        $('#SearchResponse').text("Need to Login before searching movies...Redirecting to Login Page");
                        setTimeout(function () {
                            $('#Response').load("login.html");
                        }, 2500);

                    }
                }
            }

        });


    }




    function getReport(transactionID, RequestDelay) {

        //setTimeout(function () {
        $.ajax({
            type: 'GET',
            url: 'http://127.0.0.1:3999/api/g/report',
            async: true,
            contentType: 'application/json',
            dataType: "json",
            headers: {'transactionID': transactionID},
            success: function (data, textStatus, xhr) {


                if (xhr.status == 204) {
                    console.log("status: " + xhr.status)
                    setTimeout(function () {
                        getReport(transactionID, RequestDelay);
                    }, RequestDelay);

                } else if (xhr.status == 200) {
                    var dataResponse = JSON.parse(xhr.responseText);
                    var resultCode = dataResponse.resultCode;
                    var messsage = dataResponse.message;
                    console.log("good request...");
                    console.log("resultCode: " + resultCode);
                    console.log("message: " + messsage);
                    if (resultCode == 321) {
                        console.log('Credit card ID has invalid length');


                    } else if (resultCode == 322) {
                        console.log("Credit card ID has invalid value");

                    }
                    else if (resultCode == 323) {
                        console.log("expiration has invalid value");


                    }else if (resultCode == 325) {
                        console.log("Duplicate insertion");



                    }else if (resultCode == 3200) {
                        console.log("Credit card inserted successfully.");


                    }



                    else{
                        console.log("must log in before inserting a credit card...")
                        setTimeout(function () {
                            $('#Response').load('login.html')
                        }, 2500);
                    }




                }



            },
            error: function (jqXHR, exception) {
                console.log("Internal Server error");
            }

        });
        //}, RequestDelay);
    }










});