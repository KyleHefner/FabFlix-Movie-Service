$(document).ready(function (event) {


    $(document).on("click", ".update", function (event) {
        event.stopImmediatePropagation();
        console.log('will update this order...');
        console.log(event.target.id);
        var movieId = event.target.id;
        var newQuantity = $(this).closest("tr").find(".cartinput").val();
        console.log("new quantity: " + newQuantity);
        if(newQuantity < 0){
            $('#UpdateResponse').text("quantity must be at least 0");
        }
        else{
            UpdateCart(movieId, newQuantity)
            $(this).closest("tr").find(".orderQuantity").text(newQuantity);
        }
    });






    function UpdateCart(movieId, quantity){
        console.log("Updating cart");
        $.ajax({
            type: 'POST',
            url: 'http://127.0.0.1:3999/api/g/billing/cart/update',
            contentType: 'application/json',
            dataType: "json",
            headers: {
                'email': Cookies.get('email'),
                'sessionID': Cookies.get('sessionID')
            },
            data: JSON.stringify({email: Cookies.get('email'), movieId: movieId, quantity: quantity}),
            success: function (data, textStatus, jqXHR) {
                console.log("Success in updating cart")
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


                console.log("HERE1")

                if (xhr.status == 204) {
                    console.log("status: " + xhr.status)
                    setTimeout(function () {
                        getReport(transactionID, RequestDelay);
                    }, RequestDelay);

                } else if (xhr.status == 200) {
                    console.log("HREE2")
                    var dataResponse = JSON.parse(xhr.responseText);
                    var resultCode = dataResponse.resultCode;
                    var messsage = dataResponse.message;
                    console.log("good request...");
                    console.log("resultCode: " + resultCode);
                    console.log("message: " + messsage);
                    if (resultCode == 33) {
                        console.log('Quantity has invalid value');
                        $('#UpdateResponse').text("Invalid order quantity");


                    } else if (resultCode == 312) {
                        console.log("shopping cart item does not exist...");

                    }
                    else if (resultCode == 3110) {
                        console.log("Shopping cart item updated successfully");
                        $('#UpdateResponse').text("Successfully updated");

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