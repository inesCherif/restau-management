package com.restaurant.models;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a special offer in the restaurant system.
 * Used for both manager and client views.
 */
public class SpecialOffer implements Serializable {
    private static final long serialVersionUID = 1L;
    /** Title of the offer (e.g., "Pizza Combo") */
    private String title;
    /** Description of the offer (e.g., "Buy 1 Get 1 Free") */
    private String description;
    /** Type of the offer (e.g., "Discount", "Combo", "Free Item") */
    private String offerType;
    /** Optional expiry date (null if no expiry) */
    private LocalDate validUntil;

    public SpecialOffer(String title, String description, String offerType, LocalDate validUntil) {
        this.title = title;
        this.description = description;
        this.offerType = offerType;
        this.validUntil = validUntil;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOfferType() { return offerType; }
    public void setOfferType(String offerType) { this.offerType = offerType; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    /**
     * Returns true if the offer is currently valid (not expired).
     */
    public boolean isValid() {
        return validUntil == null || !validUntil.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return title + " (" + offerType + ")" + (validUntil != null ? " - valid until " + validUntil : "");
    }
} 