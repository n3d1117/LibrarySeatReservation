package model;

public enum Role {

    BASIC("BASIC"),
    ADMIN("ADMIN");

    private final String text;

    Role(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
