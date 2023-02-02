package net.atos.zac.app.mail.converter;

import javax.inject.Inject;

import net.atos.zac.app.mail.model.RESTMailGegevens;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.mail.model.MailAdres;
import net.atos.zac.mailtemplates.model.MailGegevens;

public class RESTMailGegevensConverter {

    @Inject
    private ConfiguratieService configuratieService;

    public MailGegevens convert(final RESTMailGegevens restMailGegevens) {
        // Note that most of the actual conversion happens in the constructor.
        // Please do not move it here, because MailGegevens do not always get constructed here.
        final String afzender = configuratieService.readGemeenteNaam();
        return new MailGegevens(
                new MailAdres(restMailGegevens.verzender, afzender),
                new MailAdres(restMailGegevens.ontvanger),
                restMailGegevens.replyTo == null ? null : new MailAdres(restMailGegevens.replyTo, afzender),
                restMailGegevens.onderwerp,
                restMailGegevens.body,
                restMailGegevens.bijlagen,
                restMailGegevens.createDocumentFromMail);
    }
}
