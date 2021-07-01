package model;

import java.util.UUID;

public class ModelFactory {

    private ModelFactory() { }

    public static User initializeUser() {
        return new User(UUID.randomUUID().toString());
    }
}
