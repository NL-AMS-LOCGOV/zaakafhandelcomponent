package net.atos.zac.app.bag.converter;

import org.jetbrains.annotations.NotNull;

public final class RESTBAGConverterUtil {

    @NotNull
    public static String getHuisnummerWeergave(final Integer huisnummer, final String huisletter, final String huisnummertoevoeging) {
        final StringBuilder volledigHuisnummer = new StringBuilder();
        if (huisnummer != null) {
            volledigHuisnummer.append(huisnummer);
        }
        if (huisletter != null) {
            volledigHuisnummer.append(huisletter);
        }
        if (huisnummertoevoeging != null) {
            volledigHuisnummer.append("-").append(huisnummertoevoeging);
        }
        return volledigHuisnummer.toString().trim();
    }

}
