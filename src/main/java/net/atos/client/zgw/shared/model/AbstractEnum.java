/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.model;

import java.util.Arrays;

import javax.json.bind.adapter.JsonbAdapter;

public interface AbstractEnum<T extends AbstractEnum> {

    String toValue();

    static <T extends AbstractEnum> T fromValue(final T[] enums, final String value) {
        return Arrays.stream(enums)
                .filter(anEnum -> anEnum.toValue().equals(value))
                .findAny()
                .orElseThrow();
    }

    abstract class Adapter<T extends AbstractEnum> implements JsonbAdapter<T, String> {

        @Override
        public String adaptToJson(final T anEnum) {
            return anEnum.toValue();
        }

        @Override
        public T adaptFromJson(final String json) {
            return Arrays.stream(getEnums())
                    .filter(anEnum -> anEnum.toValue().equals(json))
                    .findAny()
                    .orElse(null);
        }

        protected abstract T[] getEnums();
    }
}
