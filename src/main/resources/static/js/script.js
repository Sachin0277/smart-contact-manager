console.log("This is a javascript file.")

const toggleSidebar = () => {
    if ($('.sidebar').is(":visible")) {
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    } else {
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};


const search = () => {
    let query = encodeURIComponent($("#search-input").val());

    if (query === "") {
        $(".search-result").hide();
    } else {
        let url = `http://localhost:8181/search/${query}`;

        fetch(url)
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then((data) => {
                console.log(data);
                if (data && Array.isArray(data)) {
                    let text = `<div class='list-group'>`;
                    data.forEach((contact) => {
                        text += `<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'> ${contact.name}</a>`;
                    });
                    text += `</div>`;
                    $(".search-result").html(text);
                    $(".search-result").show();
                } else {
                    console.error('Invalid data format received from the server.');
                    // Handle the error appropriately (e.g., display an error message to the user)
                }
            })
            .catch((error) => {
                console.error('Fetch error:', error);
                // Handle the error appropriately (e.g., display an error message to the user)
            });
    }
};


/* first request to the server */


const paymentStart = () => {
    console.log("payment started...");
    var amount = $("#payment-field").val();
    console.log(amount);
    if (amount === "" || amount == null) {
        swal("Failed !", "Amount is Required", "error");

        return
    }
    /*Code to run ajax to create order*/
    const requestData = {
        amount: amount,
        info: 'order_request',
    };

    // Make the AJAX request using jQuery
    $.ajax({
        url: '/user/create-order',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(requestData),
        dataType: 'json',
        success: function (response) {
            console.log(response);
            // Process the response as needed
            if (response.status === 'created') {
                /* Open payment Form */
                let options = {
                    key: "rzp_test_GkzuBp2p3yGfe8",
                    amount: response.amount,
                    currency: "INR",
                    name: "Smart Contact Manager",
                    description: "Donation",
                    image: "https://www.pngmart.com/files/7/Payment-Transparent-Images-PNG.png",
                    order_id: response.id,
                    handler: function (response) {
                        console.log(response.razorpay_payment_id);
                        console.log(response.razorpay_order_id);
                        console.log(response.razorpay_signature);
                        console.log("payment successful !");
                        /*
                                                alert("congrats ! payment successful");
                        */

                        updatePaymentOnServer(response.razorpay_payment_id, response.razorpay_order_id, "paid");
                    },
                    prefill: {
                        name: "",
                        email: "",
                        contact: "",
                    },
                    notes: {
                        address: "Sachin Yadav - Intern",
                    },
                    theme: {
                        color: "#3399cc",
                    },
                };
                let rzp = new Razorpay(options);
                rzp.on('payment.failed', function (response) {
                    console.log(response.error.code);
                    console.log(response.error.description);
                    console.log(response.error.source);
                    console.log(response.error.step);
                    console.log(response.error.reason);
                    console.log(response.error.metadata.order_id);
                    console.log(response.error.metadata.payment_id);
                    /* alert("Oops payment failed");*/
                    swal("Failed !", "Oops payment failed!", "error");
                });
                rzp.open();
            }
        },
        error: function (error) {
            console.error('AJAX error:', error);
            alert("Something went wrong!");
        },
    });
};


function updatePaymentOnServer(payment_id, order_id, status) {
    $.ajax({
        url: '/user/update_order',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({payment_id: payment_id, order_id: order_id, status: status}),
        dataType: 'json',
        success: function (response) {
            swal("Good job!", "Congrats! Payment Successful.", "success");
        },
        error: function (error) {
            swal("Failed !", "Your payment is successful , but cannot be captured on the server!", "error");
        },
    })
}