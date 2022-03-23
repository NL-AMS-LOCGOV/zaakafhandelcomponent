package net.atos.zac.app.klanten.model;

public abstract class RESTKlant {
    public abstract String getIdentificatie();

    public abstract KlantType getKlantType();

    public abstract String getNaam();
}
