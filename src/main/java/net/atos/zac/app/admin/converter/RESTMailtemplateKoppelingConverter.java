/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import net.atos.zac.app.admin.model.RESTMailtemplateKoppeling;
import net.atos.zac.zaaksturing.model.MailtemplateKoppeling;

public class RESTMailtemplateKoppelingConverter {

    @Inject
    private RESTMailtemplateConverter restMailtemplateConverter;

    public RESTMailtemplateKoppeling convert(final MailtemplateKoppeling mailtemplateKoppeling) {
        final RESTMailtemplateKoppeling restMailtemplateKoppeling = new RESTMailtemplateKoppeling();
        restMailtemplateKoppeling.id = mailtemplateKoppeling.getId();
        restMailtemplateKoppeling.mailtemplate =
                restMailtemplateConverter.convert(mailtemplateKoppeling.getMailTemplate());

        return restMailtemplateKoppeling;
    }

    public MailtemplateKoppeling convert(final RESTMailtemplateKoppeling restMailtemplateKoppeling) {
        final MailtemplateKoppeling mailtemplateKoppeling = new MailtemplateKoppeling();
        mailtemplateKoppeling.setMailTemplate(
                restMailtemplateConverter.convert(restMailtemplateKoppeling.mailtemplate));
        return mailtemplateKoppeling;
    }

    public List<RESTMailtemplateKoppeling> convert(final Set<MailtemplateKoppeling> mailtemplateKoppelingList) {
        return mailtemplateKoppelingList.stream().map(this::convert).toList();
    }

    public List<MailtemplateKoppeling> convertRESTmailtemplateKoppelingen(final List<RESTMailtemplateKoppeling> restMailtemplateKoppelingList) {
        return restMailtemplateKoppelingList.stream().map(this::convert).toList();
    }
}
