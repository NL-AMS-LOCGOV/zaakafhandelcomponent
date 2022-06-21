package net.atos.zac.zoeken.converter;

import java.util.UUID;

import net.atos.zac.identity.model.User;
import net.atos.zac.zoeken.model.ZoekObject;
import net.atos.zac.zoeken.model.index.ZoekObjectType;

public abstract class AbstractZoekObjectConverter<ZOEKOBJECT extends ZoekObject> {

    public abstract boolean supports(final ZoekObjectType objectType);

    public abstract ZOEKOBJECT convert(final UUID uuid);

    protected String getUserFullName(final User user) {
        if (user == null) {
            return null;
        }
        if (user.getFullName() != null) {
            return user.getFullName();
        } else if (user.getFirstName() != null && user.getLastName() != null) {
            return String.format("%s %s", user.getFirstName(), user.getLastName());
        } else {
            return user.getId();
        }
    }

}
