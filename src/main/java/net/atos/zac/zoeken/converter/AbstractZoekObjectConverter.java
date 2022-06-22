package net.atos.zac.zoeken.converter;

import java.util.UUID;

import net.atos.zac.zoeken.model.ZoekObject;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public abstract class AbstractZoekObjectConverter<ZOEKOBJECT extends ZoekObject> {

    public abstract boolean supports(final ZoekObjectType objectType);

    public abstract ZOEKOBJECT convert(final UUID id);

}
