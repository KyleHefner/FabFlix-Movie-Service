$(document).ready(function () {

    $('#Order').click(function(){
        var quantity = $('#orderAmount').val();
        console.log("Ordering: " + quantity);
        if (quantity < 1 ) {
            //not a valid rating...
            $('#OrderResponse').text("Must order 1 or more");
        } else {
            var movieId = Cookies.get("MovieID");
            $('#Order').css('visibility', 'hidden');
            $('#orderAmount').css('visibility', 'hidden');
            OrderMovie(movieId, quantity);
        }
    });




    function OrderMovie(movieId, quantity){
        console.log("ordering a move");
        $.ajax({
            type: 'POST',
            url: 'http://127.0.0.1:3999/api/g/billing/cart/insert',
            contentType: 'application/json',
            dataType: "json",
            headers: {
                'email': Cookies.get('email'),
                'sessionID': Cookies.get('sessionID')
            },
            data: JSON.stringify({email: Cookies.get('email'), movieId: movieId, quantity: quantity}),
            success: function (data, textStatus, jqXHR) {
                console.log("Success in Ordering a movie...")
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
                    if (resultCode == 33) {
                        console.log('Quantity has invalid value');
                        $('#OrderResponse').text("Invalid order quantity");


                    } else if (resultCode == 311) {
                        console.log("Movie is already in the shopping cart");
                        $('#OrderResponse').text("This movie has already been added to your shopping cart");
                    }
                    else if (resultCode == 3100) {
                        console.log("Movie has been added to the cart");
                        $('#OrderResponse').text("Successfully added to your shopping cart");

                    }
                    else{
                        console.log("must log in before rating a movie...")
                        $('#ratingResponse').text("Thank You for Rating this movie");
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
