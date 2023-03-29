package net.atos.zac.app.bag.converter;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public final class RESTBAGConverterUtil {

    @NotNull
    public static String getHuisnummerWeergave(final Integer huisnummer, final String huisletter, final String huisnummertoevoeging) {
        final StringBuilder volledigHuisnummer = new StringBuilder();
        volledigHuisnummer.append(huisnummer);

        if (StringUtils.isNotBlank(huisletter)) {
            volledigHuisnummer.append(huisletter);
        }
        if (StringUtils.isNotBlank(huisnummertoevoeging)) {
            volledigHuisnummer.append("-").append(huisnummertoevoeging);
        }
        return volledigHuisnummer.toString().trim();
    }

}
