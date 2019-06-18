$(document).ready(function () {

    $('#loader').css('visibility', 'hidden');


    $('#RegisterBtn').click(function(){
        $('#loader').css('visibility', 'visible');
        console.log("Here again");
        var email = $('#email').val();
        var password = $('#password').val();
        console.log(email);
        console.log(password);
        var obj = { email: email, password: password};


        $.ajax({
            type:'POST',
            url: 'http://127.0.0.1:3999/api/g/idm/register',
            contentType: 'application/json',
            dataType: "json",
            data: JSON.stringify({email: email, password: password}),
            error: function(xhr, error){
                console.log("error: " + error);
                //should never be an error
            },
            success: function(data, textStatus, xhr) {
                console.log('success', data);
                var transactionID = xhr.getResponseHeader('transactionID');
                console.log(transactionID);
                var RequestDelay = xhr.getResponseHeader("RequestDelay");
                console.log(RequestDelay);
                console.log("text status: " + textStatus)
                if (textStatus == "nocontent") {
                    console.log("In nocontent");
                    //now make request to report endpoint...
                    setTimeout(function () {
                        $.ajax({
                            type: 'GET',
                            url: 'http://127.0.0.1:3999/api/g/report',
                            contentType: 'application/json',
                            dataType: "json",
                            headers: {'transactionID':transactionID},
                            success: function (data, textStatus, xhr) {
                                $('#loader').css('visibility', 'hidden');

                                if(xhr.status == 200){
                                    var dataResponse = JSON.parse(xhr.responseText);
                                    var resultCode = dataResponse.resultCode;
                                    var messsage = dataResponse.message;
                                    console.log("good request...");
                                    console.log("resultCode: " + resultCode);
                                    console.log("message: " + messsage);
                                    if(resultCode == 12){
                                        $('#RegisterResponse').empty().append("Password must be between 7 and 16 characters");
                                    }
                                    else if(resultCode == 13){
                                        $('#RegisterResponse').empty().append("Password must contain one upper case, lower case, numeric, and special character");

                                    }
                                    else if(resultCode == 16){
                                        $('#RegisterResponse').empty().append("Email already in use");
                                    }
                                    else if(resultCode == 110){
                                        $('#RegisterResponse').empty().append("User Registered successfully...Redirecting to Login Page");
                                        setTimeout(function () {
                                            $('#Response').load("login.html");
                                        }, 2500);
                                    }
                                }


                            },
                            error: function (jqXHR, textStatus, errorThrown) {
                                $('#loader').css('visibility', 'hidden');
                                if(jqXHR.status == 0){
                                    console.log("couldn't connect")
                                }
                                if(jqXHR.status == 400){

                                    var data = JSON.parse(jqXHR.responseText);
                                    var resultCode = data.resultCode;
                                    var messsage = data.message;
                                    console.log("bad request...");
                                    console.log("resultCode: " + resultCode);
                                    console.log("message: " + messsage);
                                    if(resultCode == -12){
                                        $('#RegisterResponse').empty().append("Password must be between 7 and 16 characters");
                                    }else if(resultCode == -11){
                                        $('#RegisterResponse').empty().append("email must be of form xxx@xxx.xxx");
                                    }else if(resultCode == -10){
                                        $('#RegisterResponse').empty().append("email must be of form xxx@xxx.xxx");

                                    }
                                }
                            }


                        });
                    }, RequestDelay);

                }

            }


        });

    });

});