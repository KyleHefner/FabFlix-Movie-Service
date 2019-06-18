$(document).ready(function () {


    $(document).on("click", '.singleMovie', function (event) {
        event.stopImmediatePropagation();
        console.log("in single movie click");
        $('#loader').css('visibility', 'visible');
        var movieId = event.target.id;
        Cookies.set('movieID', movieId);
        getMovie(movieId);

        //$('#Response').load("singleMovie.html");
        console.log(event.target.id);
        //need to get information for this specific movie...




        function getMovie(movieID) {
            $.ajax({
                type: 'GET',
                url: 'http://127.0.0.1:3999/api/g/movies/get/' + movieID,
                contentType: 'application/json',
                dataType: "json",
                headers: {
                    'email': Cookies.get('email'),
                    'sessionID': Cookies.get('sessionID')
                },

                success: function (data, textStatus, jqXHR) {
                    console.log(textStatus);
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


    });






    function getReport(transactionID, RequestDelay) {

        setTimeout(function () {
            $.ajax({
                type: 'GET',
                url: 'http://127.0.0.1:3999/api/g/report',
                async: true,
                contentType: 'application/json',
                dataType: "json",
                headers: {'transactionID': transactionID},
                success: function (data, textStatus, xhr) {
                    $('#loader').css('visibility', 'hidden');

                    console.log("HERE1")

                    if (xhr.status == 204) {
                        console.log("status: " + xhr.status)
                        getReport();
                    } else if (xhr.status == 200) {
                        console.log("HREE2")
                        var dataResponse = JSON.parse(xhr.responseText);
                        var resultCode = dataResponse.resultCode;
                        var messsage = dataResponse.message;
                        console.log("good request...");
                        console.log("resultCode: " + resultCode);
                        console.log("message: " + messsage);
                        if (resultCode == 210) {
                            console.log("Found movies with search parameters");
                            console.log("movie: " + JSON.stringify(dataResponse.movie));

                            $('#Response').load("singleMovie.html", function () {
                                $('#loader').css('visibility', 'hidden');
                                $('#MovieTitle').text(dataResponse.movie.title);
                                $('#Director').text("Director: " + dataResponse.movie.director);
                                $('#Year').text("Release Date: " + dataResponse.movie.year);
                                $('#Overview').text(dataResponse.movie.overview);
                                $('#Rating').text("Rating: " + dataResponse.movie.rating);
                                $('#Votes').text("Number of votes: " + dataResponse.movie.numVotes);
                                Cookies.set("MovieID", dataResponse.movie.movieId);

                                $('#Stars').text("Stars: ");
                                $('#Stars').append('<span> ' + dataResponse.movie.stars[0].name + '</span>')
                                var j;
                                for(j = 1; j< dataResponse.movie.stars.length; j++){
                                    console.log(dataResponse.movie.stars[i]);
                                    $('#Stars').append('<span> , ' + dataResponse.movie.stars[j].name + '</span>');
                                }

                                $('#Genres').text("Genres: ");
                                $('#Genres').append('<span> ' + dataResponse.movie.genres[0].name + '</span>')
                                var i;
                                for(i = 1; i< dataResponse.movie.genres.length; i++){
                                    console.log(dataResponse.movie.genres[i]);
                                    $('#Genres').append('<span> , ' + dataResponse.movie.genres[i].name + '</span>');
                                }


                            });


                        } else if (resultCode == 211) {
                            console.log("No movies found with search parameters");
                        }

                    }


                },
                error: function (jqXHR, exception) {
                    console.log("Internal Server error");
                }

            });
        }, RequestDelay);
    }
});