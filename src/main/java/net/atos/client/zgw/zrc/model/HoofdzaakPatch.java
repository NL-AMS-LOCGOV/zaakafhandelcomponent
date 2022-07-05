package net.atos.client.zgw.zrc.model;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbProperty;

import java.net.URI;

@JsonbNillable
public class HoofdzaakPatch extends Zaak {

    @JsonbProperty(nillable = true)
    private final URI hoofdzaak;

    public HoofdzaakPatch(final URI hoofdzaak) {
        this.hoofdzaak = hoofdzaak;
    }

    public URI getHoofdzaak() {
        return hoofdzaak;
    }
}
