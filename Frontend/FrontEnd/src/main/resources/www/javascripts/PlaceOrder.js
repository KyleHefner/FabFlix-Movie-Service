$(document).ready(function () {

    $('#PlaceOrderbtn').click(function(){
        console.log("placing an order")
        //$.when(AddCreditCard()).then(PlaceOrder());
        PlaceOrder();

    });








    function PlaceOrder() {


        console.log("placing order...");
        $.ajax({
            type: 'POST',
            url: 'http://127.0.0.1:3999/api/g/billing/order/place',
            contentType: 'application/json',
            dataType: "json",
            headers: {
                'email': Cookies.get('email'),
                'sessionID': Cookies.get('sessionID')
            },
            data: JSON.stringify({email: Cookies.get('email')}),
            success: function (data, textStatus, jqXHR) {
                console.log("Success in placing order...")
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
                    console.log("redirectURL: " + dataResponse.redirectURL);
                    if (resultCode == 332) {
                        console.log('Customer does not exist');


                    } else if (resultCode == 341) {
                        console.log("Shopping cart for customer does not exist");

                    }
                    else if (resultCode == 342) {
                        console.log("Create payment failed");


                    }else if (resultCode == 3400) {
                        console.log("order placed successfully");
                        window.open(dataResponse.redirectURL, "payment", 'width = 800, height = 600');

                    }


                    else{
                        console.log("must log in before placing an order...")
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