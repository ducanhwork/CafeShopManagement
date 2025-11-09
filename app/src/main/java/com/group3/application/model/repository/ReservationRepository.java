
package com.group3.application.model.repository;

import com.group3.application.model.entity.Reservation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// This is a Singleton to simulate a persistent data source for the app's lifecycle.
public class ReservationRepository {

    /*private static ReservationRepository instance;
    private final Map<String, List<Reservation>> reservationsByTable = new HashMap<>();

    private ReservationRepository() {}

    public static synchronized ReservationRepository getInstance() {
        if (instance == null) {
            instance = new ReservationRepository();
        }
        return instance;
    }

    public List<Reservation> getReservationsForTable(String tableNumber) {
        if (!reservationsByTable.containsKey(tableNumber)) {
            // If no data exists for this table, create and store it ONCE.
            List<Reservation> newList = new ArrayList<>();
            newList.add(new Reservation("John Doe", "10:00", "Confirmed", "26/10/2025", 4, tableNumber));
            newList.add(new Reservation("Jane Smith", "11:30", "Pending", "26/10/2025", 2, tableNumber));
            newList.add(new Reservation("Peter Jones", "12:00", "Confirmed", "26/10/2025", 5, tableNumber));
            reservationsByTable.put(tableNumber, newList);
        }
        // Return a copy to prevent direct modification of the source list.
        return new ArrayList<>(reservationsByTable.get(tableNumber));
    }

    public void updateReservation(Reservation updatedReservation) {
        if (updatedReservation == null) return;

        String tableNumber = updatedReservation.getTableNumber();
        List<Reservation> tableReservations = reservationsByTable.get(tableNumber);

        if (tableReservations != null) {
            for (int i = 0; i < tableReservations.size(); i++) {
                // Using customer name as a unique ID for this simulation
                if (Objects.equals(tableReservations.get(i).getCustomerName(), updatedReservation.getCustomerName())) {
                    tableReservations.set(i, updatedReservation);
                    break;
                }
            }
        }
    }

    public void addReservation(Reservation newReservation) {
        if (newReservation == null) return;
        String tableNumber = newReservation.getTableNumber();
        // This ensures the list exists before trying to add to it.
        List<Reservation> tableReservations = getReservationsForTable(tableNumber);
        tableReservations.add(newReservation);
    }*/
}
