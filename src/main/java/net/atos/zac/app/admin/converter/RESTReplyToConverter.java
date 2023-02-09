/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.List;
import java.util.stream.Collectors;

import net.atos.zac.app.admin.model.RESTReplyTo;
import net.atos.zac.zaaksturing.model.ReferentieTabelWaarde;
import net.atos.zac.zaaksturing.model.ZaakAfzender;

public class RESTReplyToConverter {

    public List<RESTReplyTo> convertReplyTos(final List<ReferentieTabelWaarde> waarden) {
        final List<RESTReplyTo> restReplyTos = waarden.stream()
                .map(this::convertReplyTo)
                .collect(Collectors.toList());
        for (final ZaakAfzender.Speciaal speciaal : ZaakAfzender.Speciaal.values()) {
            restReplyTos.add(new RESTReplyTo(speciaal));
        }
        restReplyTos.sort((a, b) -> a.speciaal != b.speciaal ? a.speciaal ? -1 : 1 : a.mail.compareTo(b.mail));
        return restReplyTos;
    }

    public RESTReplyTo convertReplyTo(final ReferentieTabelWaarde waarde) {
        final RESTReplyTo restReplyTo = new RESTReplyTo();
        restReplyTo.mail = waarde.getNaam();
        restReplyTo.speciaal = false;
        return restReplyTo;
    }
}
