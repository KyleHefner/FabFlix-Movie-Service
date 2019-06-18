$(document).ready(function () {

    //load html correct html file into div with id 'Response'


    $('#Login').click(function () {

        $('#Response').load("login.html");
    })


    $('#Register').click(function () {

        $('#Response').load("register.html");
    })

    $('#Home').click(function () {

        $('#Response').load("home.html");
    })

    $('#Search').click(function () {

        var testemail = Cookies.get('email');
        var sessionID = Cookies.get('sessionID');
        if(testemail == null || sessionID == null){
            alert("must login before searching movie!")
        }
        else {
            $('#Response').load("search.html");
        }
    })

    $('#ShoppingCart').on('click',function (event) {
        var testemail = Cookies.get('email');
        var sessionID = Cookies.get('sessionID');
        if(testemail == null || sessionID == null){
            alert("must login before viewing cart!")
        }
        else {

            event.stopImmediatePropagation();

            $('#Response').load("shoppingcart.html", function () {
                var email = Cookies.get('email');
                $('#cartlist').css('visibility', 'hidden');
                $('#emptyCartbtn').css('visibility', 'hidden');
                $('#Checkoutbtn').css('visibility', 'hidden');
                GetShoppingCart(email);
            });
        }

    });

    $('#OrderHistory').on('click', function (event) {
        var testemail = Cookies.get('email');
        var sessionID = Cookies.get('sessionID');
        if(testemail == null || sessionID == null){
            alert("must login before viewing Order History!")
        }
        else {
            event.stopImmediatePropagation();
            $('#Response').load("OrderHistory.html", function () {
                var email = Cookies.get('email');
                $('#historylist').css('visibility', 'hidden');
                GetOrderHistory(email);

            });
        }
    });





    function GetOrderHistory(email){

        console.log("retrieving order history");
        $.ajax({
            type: 'POST',
            url: 'http://127.0.0.1:3999/api/g/billing/order/retrieve',
            contentType: 'application/json',
            dataType: "json",
            headers: {
                'email': Cookies.get('email'),
                'sessionID': Cookies.get('sessionID')
            },
            data: JSON.stringify({email: Cookies.get('email')}),
            success: function (data, textStatus, jqXHR) {
                console.log("Success in retrieving order...")
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
                                    if (resultCode == 332) {
                                        console.log('Customer does not exist');
                                        $('#historylist').css('visibility', 'hidden');
                                        $('#OrderHistoryResponse').text("No Order History")


                                    }
                                    else if (resultCode == 3410) {
                                        console.log("Orders retrieved successfully");
                                        if (dataResponse.transactions.length == 0) {
                                            $('#OrderHistoryResponse').text("No Order History")
                                        } else {

                                            var j;
                                            for (j = 0; j < dataResponse.transactions.length; j++) {
                                                console.log("transactionid: " + dataResponse.transactions[j].transactionId);
                                                console.log("state: " + dataResponse.transactions[j].state);
                                                var i;
                                                for (i = 0; i < dataResponse.transactions[j].items.length; i++) {
                                                    console.log('email' + dataResponse.transactions[j].items[i].email)
                                                    console.log('movieId' + dataResponse.transactions[j].items[i].movieId)
                                                    console.log('quantity' + dataResponse.transactions[j].items[i].quantity)
                                                    $('#historylist').append("<tr>" + "<td>" + dataResponse.transactions[j].items[i].movieId + "</td>" + "<td>" + dataResponse.transactions[j].items[i].quantity + "</td>" + "<td>" + dataResponse.transactions[j].items[i].unit_price + "</td>" + "<td>" + dataResponse.transactions[j].items[i].discount + "</td>" + "<td>" + dataResponse.transactions[j].items[i].saleDate + "</td>" + "</tr>")
                                                }

                                            }
                                            $('#historylist').append("<tr>" + "<td> Total: " + dataResponse.transactions[0].amount.total + "</td>" + "</tr>")
                                            $('#historylist').css('visibility', 'visible');


                                        }
                                    }

                                    else{
                                        console.log("must log in before asking for cart items")
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




























    function GetShoppingCart(email){

        console.log("retrieving shopping cart");
        $.ajax({
            type: 'POST',
            url: 'http://127.0.0.1:3999/api/g/billing/cart/retrieve',
            contentType: 'application/json',
            dataType: "json",
            headers: {
                'email': Cookies.get('email'),
                'sessionID': Cookies.get('sessionID')
            },
            data: JSON.stringify({email: Cookies.get('email')}),
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
                                    if (resultCode == 312) {
                                        console.log('Shopping item does not exist');
                                        $('#cartlist').css('visibility', 'hidden');
                                        $('#emptyCartbtn').css('visibility', 'hidden');
                                        $('#Checkoutbtn').css('visibility', 'hidden');
                                        $('#emptyCartResponse').text("Cart is empty");

                                    }
                                    else if (resultCode == 3130) {
                                        console.log("Shopping cart retrieved successfully.");

                                            console.log("cart is not empty" + dataResponse.items.length);
                                            var j;
                                            for (j = 0; j < dataResponse.items.length; j++) {
                                                console.log("movieID: " + dataResponse.items[j].movieId);
                                                console.log("quantity: " + dataResponse.items[j].quantity)
                                                $('#cartlist').append("<tr id = 'table" + dataResponse.items[j].movieId + "'" + ">" + "<td>" + dataResponse.items[j].movieId + "</td>" + "<td class = 'orderQuantity'>" + dataResponse.items[j].quantity + "</td>" + "<td>" + "<input type = 'number'  class = 'cartinput' id ='" + dataResponse.items[j].movieId + "'" + " >" + "<button type = 'button' class  = 'update' id = '" + dataResponse.items[j].movieId + "'>" + "UPDATE" + "</button>" + "</td>" + "<td>" + "<button type = 'button' class = 'removebtn' id = '" + dataResponse.items[j].movieId + "'> REMOVE </button>" + "</td>" +  "</tr>");

                                            }
                                        $('#cartlist').css('visibility', 'visible');
                                        $('#emptyCartbtn').css('visibility', 'visible');
                                        $('#Checkoutbtn').css('visibility', 'visible');


                                    }
                                    else{
                                        console.log("must log in before asking for cart items")
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