/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import static net.atos.client.zgw.shared.ZGWApiService.FIRST_PAGE_NUMBER_ZGW_APIS;

import java.util.List;

import net.atos.zac.datatable.Pagination;

public final class OpenZaakPaginationUtil {

    // Page size implemented by Open Zaak.
    public static final int PAGE_SIZE_OPEN_ZAAK = 100;

    private OpenZaakPaginationUtil() {
    }

    /**
     * Geeft het juiste pagina nummer terug
     *
     * @param pagination het paginatie object met pagina nummer en aantal per pagina
     * @return het juiste paginanummer voor Open Zaak
     */
    public static int calculateOpenZaakPageNumber(final Pagination pagination) {
        return pagination.getPageNumber() * pagination.getPageSize() / PAGE_SIZE_OPEN_ZAAK + FIRST_PAGE_NUMBER_ZGW_APIS;
    }

    /**
     * Geeft de juiste aantal resultaten terug aan de hand van de opgegeven {@link Pagination}
     *
     * @param results    de resultaten die voldoen aan de opgegeven paginering
     * @param pagination de paginering voor de resultaten
     * @param <T>        het Type object dat terug wordt gegeven.
     * @return een selectie van de lijst
     */
    public static <T> List<T> filterPageFromOpenZaakResult(final List<T> results, final Pagination pagination) {
        final int absolutePageNumber = pagination.getPageNumber() * pagination.getPageSize() / PAGE_SIZE_OPEN_ZAAK;
        final int relativePageNumber = pagination.getPageNumber() - (absolutePageNumber * PAGE_SIZE_OPEN_ZAAK / pagination.getPageSize());
        final int fromIndex = relativePageNumber * pagination.getPageSize();
        final int toIndex = Math.min(fromIndex + pagination.getPageSize(), results.size());
        return results.subList(fromIndex, toIndex);
    }
}
