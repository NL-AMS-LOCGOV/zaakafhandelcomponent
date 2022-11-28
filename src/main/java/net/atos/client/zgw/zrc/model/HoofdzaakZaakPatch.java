package net.atos.client.zgw.zrc.model;

import java.net.URI;

import javax.json.bind.annotation.JsonbProperty;

public class HoofdzaakZaakPatch extends Zaak {

    @JsonbProperty(nillable = true)
    private final URI hoofdzaak;

    public HoofdzaakZaakPatch(final URI hoofdzaak) {
        this.hoofdzaak = hoofdzaak;
    }

    public URI getHoofdzaak() {
        return hoofdzaak;
    }
}
