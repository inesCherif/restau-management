package com.restaurant.services;

import com.restaurant.models.Client;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClientService {
    private static final String CLIENTS_FILE = "clients.dat";
    private List<Client> clients;

    public ClientService() {
        clients = loadClients();
    }

    public List<Client> getAllClients() {
        return new ArrayList<>(clients);
    }

    public void addClient(Client client) {
        clients.add(client);
        saveClients();
    }

    public void updateClient(int index, Client updatedClient) {
        if (index >= 0 && index < clients.size()) {
            clients.set(index, updatedClient);
            saveClients();
        }
    }

    public void deleteClient(int index) {
        if (index >= 0 && index < clients.size()) {
            clients.remove(index);
            saveClients();
        }
    }

    private List<Client> loadClients() {
        File file = new File(CLIENTS_FILE);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Client>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveClients() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CLIENTS_FILE))) {
            oos.writeObject(clients);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 