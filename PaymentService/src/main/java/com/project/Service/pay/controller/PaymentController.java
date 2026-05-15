package com.project.Service.pay.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.Service.pay.service.RazorpayService;
import com.project.Service.pay.client.PaymentVerificationRequest;
import com.razorpay.RazorpayException;


@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private RazorpayService razorpayService;

    @PostMapping("/create-order")
    public ResponseEntity<String> createOrder(@RequestParam int amount, @RequestParam Long bookingId) throws RazorpayException {
        return ResponseEntity.ok(razorpayService.createOrder(amount, bookingId));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyAndConfirmPayment(@RequestBody PaymentVerificationRequest request) throws RazorpayException {
   
        return ResponseEntity.ok(razorpayService.verifyAndConfirmPayment(request));
    }
}
