/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.gebruikersvoorkeuren.converter;

import java.util.List;

import net.atos.zac.app.gebruikersvoorkeuren.model.RESTDashboardCardInstelling;
import net.atos.zac.gebruikersvoorkeuren.model.DashboardCardInstelling;

public class RESTDashboardCardInstellingConverter {

    public RESTDashboardCardInstelling convert(final DashboardCardInstelling card) {
        final RESTDashboardCardInstelling restCard = new RESTDashboardCardInstelling();
        restCard.id = card.getId();
        restCard.cardId = card.getCardId();
        restCard.column = card.getKolom();
        restCard.row = card.getVolgorde();
        return restCard;
    }

    public DashboardCardInstelling convert(final RESTDashboardCardInstelling restCard) {
        final DashboardCardInstelling card = new DashboardCardInstelling();
        card.setId(restCard.id);
        card.setCardId(restCard.cardId);
        card.setSignaleringType(restCard.signaleringType);
        card.setKolom(restCard.column);
        card.setVolgorde(restCard.row);
        return card;
    }

    public List<RESTDashboardCardInstelling> convert(final List<DashboardCardInstelling> cards) {
        return cards.stream()
                .map(this::convert)
                .toList();
    }
}
