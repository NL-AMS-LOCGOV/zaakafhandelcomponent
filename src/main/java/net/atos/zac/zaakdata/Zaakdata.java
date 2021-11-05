/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zaakdata;

import java.util.HashMap;
import java.util.Map;

public class Zaakdata {

    private Map<String, String> elementen = new HashMap<>();

    public Map<String, String> getElementen() {
        return elementen;
    }

    public void addElement(final String key, final String value) {
        elementen.put(key, value);
    }

    public void setElementen(final Map<String, String> elementen) {
        this.elementen = elementen;
    }
}
