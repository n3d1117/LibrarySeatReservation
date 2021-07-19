package model;

import java.util.UUID;

public class ModelFactory {

    private ModelFactory() { }

    public static User initializeUser() {
        return new User(UUID.randomUUID().toString());
    }

    public static Library initializeLibrary() {
        return new Library(UUID.randomUUID().toString());
    }

    public static Reservation initializeReservation() {
        return new Reservation(UUID.randomUUID().toString());
    }
}
