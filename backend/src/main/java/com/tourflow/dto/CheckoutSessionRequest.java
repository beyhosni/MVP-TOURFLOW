package com.tourflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CheckoutSessionRequest {

    @NotNull(message = "L'ID de réservation est obligatoire")
    private UUID bookingId;

    @NotBlank(message = "L'URL de succès est obligatoire")
    private String successUrl;

    @NotBlank(message = "L'URL d'annulation est obligatoire")
    private String cancelUrl;

    // Constructeurs
    public CheckoutSessionRequest() {
    }

    public CheckoutSessionRequest(UUID bookingId, String successUrl, String cancelUrl) {
        this.bookingId = bookingId;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
    }

    // Getters et Setters
    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }
}
