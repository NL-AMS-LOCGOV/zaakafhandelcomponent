package net.atos.client.zgw.zrc.model;

import javax.json.bind.annotation.JsonbNillable;

import java.net.URI;

@JsonbNillable
public class ZaakHoofdzaakPatch extends Zaak {

    private URI hoofdzaak;

    public ZaakHoofdzaakPatch(final URI hoofdzaak) {
        this.hoofdzaak = hoofdzaak;
    }

    public URI getHoofdzaak() {
        return hoofdzaak;
    }

    public void setHoofdzaak(final URI hoofdzaak) {
        this.hoofdzaak = hoofdzaak;
    }
}
