package com.tourflow.dto;

import jakarta.validation.constraints.NotBlank;

public class ExternalCalendarRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotBlank(message = "L'URL iCal est obligatoire")
    private String icsUrl;

    // Constructeurs
    public ExternalCalendarRequest() {
    }

    public ExternalCalendarRequest(String name, String icsUrl) {
        this.name = name;
        this.icsUrl = icsUrl;
    }

    // Getters et Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcsUrl() {
        return icsUrl;
    }

    public void setIcsUrl(String icsUrl) {
        this.icsUrl = icsUrl;
    }
}
