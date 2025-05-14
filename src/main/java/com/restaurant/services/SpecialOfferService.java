package com.restaurant.services;

import com.restaurant.models.SpecialOffer;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing SpecialOffer objects (CRUD, file I/O).
 * Stores offers in special_offers.dat as a binary file.
 */
public class SpecialOfferService {
    private static final String FILE_NAME = "special_offers.dat";
    private List<SpecialOffer> offers;

    public SpecialOfferService() {
        offers = loadOffers();
    }

    /**
     * Returns a list of all offers (including expired).
     */
    public List<SpecialOffer> getAllOffers() {
        return new ArrayList<>(offers);
    }

    /**
     * Returns a list of only valid (non-expired) offers.
     */
    public List<SpecialOffer> getValidOffers() {
        List<SpecialOffer> valid = new ArrayList<>();
        for (SpecialOffer offer : offers) {
            if (offer.isValid()) valid.add(offer);
        }
        return valid;
    }

    /**
     * Adds a new offer and saves to file.
     */
    public void addOffer(SpecialOffer offer) {
        offers.add(offer);
        saveOffers();
    }

    /**
     * Deletes an offer by index and saves to file.
     */
    public void deleteOffer(int index) {
        if (index >= 0 && index < offers.size()) {
            offers.remove(index);
            saveOffers();
        }
    }

    /**
     * Loads offers from the binary file.
     */
    private List<SpecialOffer> loadOffers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                return (List<SpecialOffer>) obj;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Saves offers to the binary file.
     */
    private void saveOffers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(offers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 