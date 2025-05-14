package com.restaurant.services;

import com.restaurant.models.Client;
import com.restaurant.models.Order;
import com.restaurant.models.User;
import java.util.List;
import java.util.stream.Collectors;

public class DataConsistencyService {
    private final ClientService clientService;
    private final AuthenticationService authenticationService;
    private final OrderService orderService;

    public DataConsistencyService(ClientService clientService, AuthenticationService authenticationService, OrderService orderService) {
        this.clientService = clientService;
        this.authenticationService = authenticationService;
        this.orderService = orderService;
    }

    /**
     * Deletes a client and all related data (user account, orders).
     * @param username The username of the client to delete.
     * @return true if deletion was successful, false otherwise.
     */
    public boolean deleteClientAndRelatedData(String username) {
        // Remove from ClientService
        List<Client> clients = clientService.getAllClients();
        int idx = -1;
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getUsername().equals(username)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) return false;
        clientService.deleteClient(idx);

        // Remove from AuthenticationService
        authenticationService.removeUser(username);

        // Remove all orders for this client
        List<Order> allOrders = orderService.getAllOrders();
        List<Integer> orderIndexesToRemove =
            allOrders.stream()
                .filter(o -> o.getClientUsername().equals(username))
                .map(allOrders::indexOf)
                .collect(Collectors.toList());
        // Remove from highest index to lowest to avoid shifting
        orderIndexesToRemove.sort((a, b) -> b - a);
        for (int orderIdx : orderIndexesToRemove) {
            orderService.deleteOrder(orderIdx);
        }
        return true;
    }
} 