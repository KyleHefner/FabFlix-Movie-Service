

$(document).ready(function () {
    $('#loader').css('visibility', 'hidden');


    $('#next').click(function(){
        var requestData = JSON.parse($('#searchResults').text());
        var title = requestData.title;
        var genre = requestData.genre;
        var year = requestData.year;
        var director = requestData.director;
        var limit = requestData.limit;
        var offset = requestData.offset;
        var orderBy = requestData.orderby;
        var direction = requestData.direction;
        offset = offset + limit;
        SearchMovie(title, director, genre, year,limit, offset, orderBy, direction);


    });

    $('#previous').click(function(){
        var requestData = JSON.parse($('#searchResults').text());
        var title = requestData.title;
        var genre = requestData.genre;
        var year = requestData.year;
        var director = requestData.director;
        var limit = requestData.limit;
        var offset = requestData.offset;
        var orderBy = requestData.orderby;
        var direction = requestData.direction;
        offset = offset - limit;
        SearchMovie(title, director, genre, year,limit, offset, orderBy, direction);


    });

    $('#SearchBtn').click(function() {
        $('#loader').css('visibility', 'visible');
        console.log("Here again");
        var title = $('#title').val();
        var director = $('#director').val();
        var genre = $('#genre').val();
        var year = $('#year').val();
        var sessionID = Cookies.get("sessionID");
        var limit = 10;
        var offset = 0;
        var orderBy = "rating";
        var direction = "desc";
        console.log("title: " + title);
        console.log("director: " + director);
        console.log("genre: " + genre);
        console.log("year: " + year);
        console.log("sessionID: " + sessionID);
        console.log("title: " + title);

        var obj = { title: title, genre: genre, year: year, director: director, limit: limit, offset: offset, orderby: orderBy, direction:direction };
        $('#searchResults').html(JSON.stringify(obj));


        SearchMovie(title, director, genre, year,limit, offset, orderBy, direction);


    });


























    function SearchMovie(title, director, genre, year, limit, offset, orderBy, direction) {


        $.ajax({
            type: 'GET',
            url: 'http://127.0.0.1:3999/api/g/movies/search',
            contentType: 'application/json',
            dataType: "json",
            data: {
                "title": title,
                "director": director,
                "genre": genre,
                "year": year,
                "limit": limit,
                "offset": offset,
                "orderby": orderBy,
                "direction": direction
            },
            headers: {
                'email': Cookies.get('email'),
                'sessionID': Cookies.get('sessionID')
            },

            success: function (data, textStatus, jqXHR) {
                console.log(textStatus);
                var transactionID = jqXHR.getResponseHeader("transactionID");
                var RequestDelay = jqXHR.getResponseHeader("RequestDelay");
                if (!textStatus == "nocontent") {
                    var dataResponse = JSON.parse(jqqXHR.responseText);
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
                    console.log("HRERRERERER")

                    getReport();

                    function getReport() {

                        //setTimeout(function () {
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
                                        setTimeout(function () {
                                            getReport();
                                        }, RequestDelay);

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
                                            console.log("movies: " + JSON.stringify(dataResponse.movies));
                                            $('#Response').load("movielist.html", function(){
                                                var obj = { title: title, genre: genre, year: year, director: director, limit: limit, offset: offset, orderby: orderBy, direction:direction };
                                                $('#searchResults').html(JSON.stringify(obj));
                                                if(offset == 0){
                                                    $('#previous').css('visibility', 'hidden');}

                                                 if(dataResponse.movies.length < limit){
                                                     $('#next').css('visibility', 'hidden');
                                                 }
                                                 var i;
                                                for(i = 0; i < limit; i++) {
                                                    console.log(dataResponse.movies[i].title);
                                                    $('#movielist').append("<tr>" + "<td>" + "<button type = 'button' class = 'singleMovie' id = " + dataResponse.movies[i].movieId +" border = 'none'>" + dataResponse.movies[i].title + "</button>" +"</td>" + "<td>" + dataResponse.movies[i].movieId + "</td>" + "<td>" + dataResponse.movies[i].director + "</td>" + "<td>" + dataResponse.movies[i].year + "</td>" +  "<td>" + dataResponse.movies[i].rating + "</td>" + "<td>" + dataResponse.movies[i].numVotes + "</td>" +"</tr>");

                                                }

                                            });




                                        } else if (resultCode == 211) {
                                            console.log("No movies found with search parameters");
                                            $('#SearchResponse').text("No movies found with search parameters...")
                                        }

                                    }


                                },
                                error: function (jqXHR, exception) {
                                    console.log("Internal Server error");
                                }

                            });
                        //}, RequestDelay);
                    }


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




    //send ajax request to /api/g/idm/login whatever...
});