$(document).ready(function () {


    $('#Checkoutbtn').click(function(){
        console.log("going to checkout...");
        $('#Response').load("Checkout.html", function () {
            $('#PlaceOrderbtn').css('visibility', 'hidden');
        });



    });










});