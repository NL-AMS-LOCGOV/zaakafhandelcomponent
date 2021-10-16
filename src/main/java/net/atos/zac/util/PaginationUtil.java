/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.util;

import java.util.List;

import net.atos.zac.app.util.datatable.Pagination;

public final class PaginationUtil {

    private PaginationUtil() {
    }

    /**
     * Geeft het juiste pagina nummer terug
     * fix voor ESUITEDEV-24924
     *
     * @param pagination het paginatie object met pagina nummer en aantal per pagina
     * @return het juiste paginanummer voor de zgwApis
     */
    public static int getZGWClientPage(final Pagination pagination) {
        return pagination.getPageNumber() * pagination.getPageSize() / 100 + 1;
    }

    /**
     * Geeft de juiste aantal resultaten terug aan de hand van de opgegeven {@link Pagination}
     * fix voor ESUITEDEV-24924
     *
     * @param results    de resultaten die voldoen aan de opgegeven paginering
     * @param pagination de paginering voor de resultaten
     * @param <T>        het Type object dat terug wordt gegeven.
     * @return een selectie van de lijst
     */
    public static <T> List<T> getZGWClientResults(final List<T> results, final Pagination pagination) {
        final int absolutePageNumber = pagination.getPageNumber() * pagination.getPageSize() / 100;
        final int relativePageNumber = pagination.getPageNumber() - (absolutePageNumber * 100 / pagination.getPageSize());
        final int fromIndex = relativePageNumber * pagination.getPageSize();
        final int toIndex = Math.min(fromIndex + pagination.getPageSize(), results.size());
        return results.subList(fromIndex, toIndex);
    }
}
