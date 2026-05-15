package com.project.Service.pay.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Order;
import com.razorpay.Utils;
import com.project.Service.pay.client.BookingClient;
import com.project.Service.pay.client.ConfirmBookingRequest;
import com.project.Service.pay.client.PaymentVerificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;

@Service
public class RazorpayService {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayService.class);

    private RazorpayClient razorpayClient;

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    private final BookingClient bookingClient;

    public RazorpayService(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostConstruct
    public void init() throws RazorpayException {
        razorpayClient = new RazorpayClient(keyId, keySecret);
        logger.info("RazorpayClient initialized successfully");
    }

    public String createOrder(int amountInRupees, Long bookingId) throws RazorpayException {
        logger.info("Creating Razorpay order for bookingId: {}, amount: {}", bookingId, amountInRupees);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInRupees * 100); // Amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "booking-" + bookingId);
        orderRequest.put("payment_capture", true);
        Order order = razorpayClient.orders.create(orderRequest);
        logger.info("Order created successfully for bookingId: {}", bookingId);
        return order.toString();
    }

    public String getKeySecret() {
        return keySecret;
    }

    // Method to verify payment and confirm booking
    public String verifyAndConfirmPayment(PaymentVerificationRequest request) throws RazorpayException {
        logger.info("Verifying payment for Razorpay order: {}", request.getRazorpayOrderId());
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", request.getRazorpayOrderId());
        options.put("razorpay_payment_id", request.getRazorpayPaymentId());
        options.put("razorpay_signature", request.getRazorpaySignature());

        // Verify the payment signature
        boolean isValid = Utils.verifyPaymentSignature(options, getKeySecret());

        if (isValid) {
            logger.info("Payment verified successfully for Razorpay order: {}", request.getRazorpayOrderId());

            // Confirm booking once payment is verified
            ConfirmBookingRequest confirmRequest = new ConfirmBookingRequest();
            confirmRequest.setBookingId(request.getBookingId());

            String confirmationResponse = bookingClient.confirmBooking(confirmRequest);
            logger.info("Booking confirmed for bookingId: {}", request.getBookingId());

            return "Payment verified and booking confirmed: " + confirmationResponse;
        } else {
            logger.error("Invalid payment signature for Razorpay order: {}", request.getRazorpayOrderId());
            return "Invalid payment signature";
        }
    }
}
