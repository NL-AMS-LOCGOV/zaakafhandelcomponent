package net.atos.zac.app.mail.converter;

import javax.inject.Inject;

import net.atos.zac.app.mail.model.RESTMailGegevens;
import net.atos.zac.configuratie.ConfiguratieService;
import net.atos.zac.mail.model.Ontvanger;
import net.atos.zac.mail.model.Verzender;
import net.atos.zac.mailtemplates.model.MailGegevens;

public class RESTMailGegevensConverter {

    @Inject
    private ConfiguratieService configuratieService;

    public MailGegevens convert(final RESTMailGegevens restMailGegevens) {
        // Note that most of the actual conversion happens in the constructor.
        // Please do not move it here, because MailGegevens do not always get constructed here.
        return new MailGegevens(
                new Verzender(restMailGegevens.verzender, configuratieService.readGemeenteNaam()),
                new Ontvanger(restMailGegevens.ontvanger),
                restMailGegevens.onderwerp,
                restMailGegevens.body,
                restMailGegevens.bijlagen,
                restMailGegevens.createDocumentFromMail);
    }
}
