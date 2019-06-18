$(document).ready(function () {

    $('#Rate').click(function(){
        console.log("give rating: " + $('#giveRating').val());
        //$('#loader').css('visibility', 'visible');
        if ($('#giveRating').val() < 0 || $('#giveRating').val() > 10) {
            //not a valid rating...
            $('#ratingResponse').text("Rating must be between 0.0 and 10.0");
        } else {
            var movieId = Cookies.get("MovieID");
            $('#giveRating').css('visibility', 'hidden');
            $('#Rate').css('visibility', 'hidden');
            $('#RateThisMovie').css('visibility', 'hidden');
            rateMovie(movieId, $('#giveRating').val());
        }
    });













    function rateMovie(movieID, rating) {
        $.ajax({
            type: 'POST',
            url: 'http://127.0.0.1:3999/api/g/movies/rating',
            contentType: 'application/json',
            dataType: 'json',
            headers: {
                'email': Cookies.get('email'),
                'sessionID': Cookies.get('sessionID')
            },
            data: JSON.stringify({id: movieID, rating: rating}),
            success: function (data, textStatus, jqXHR) {
                console.log('Rating ajax success');
                var transactionID = jqXHR.getResponseHeader("transactionID");
                var RequestDelay = jqXHR.getResponseHeader("RequestDelay");

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
                        if (resultCode == 250) {
                            $('#loader').css('visibility', 'hidden');
                            console.log("Rating successfully updated");
                            $('#giveRating').css('visibility', 'hidden');
                            $('#Rate').css('visibility', 'hidden');
                            $('#RateThisMovie').css('visibility', 'hidden');
                            $('#ratingResponse').text("Thank You for Rating this movie");

                        } else if (resultCode == 211) {
                            console.log("No movies found with search parameters");
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