package net.atos.zac.app.klanten.model;

public enum KlantType {
    BURGER,
    BEDRIJF;

    KlantType() {
    }

    public static KlantType getType(final String identificatieNummer) {
        return switch (identificatieNummer.length()) {
            case 9 -> BURGER;
            case 12 -> BEDRIJF;
            default -> throw new IllegalStateException("Unexpected value: " + identificatieNummer.length());
        };
    }
}
