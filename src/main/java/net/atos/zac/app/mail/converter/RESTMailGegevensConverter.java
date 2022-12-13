package net.atos.zac.app.mail.converter;

import net.atos.zac.app.mail.model.RESTMailGegevens;
import net.atos.zac.mail.model.Ontvanger;
import net.atos.zac.mailtemplates.model.MailGegevens;

public class RESTMailGegevensConverter {
    public MailGegevens convert(final RESTMailGegevens restMailGegevens) {
        // Note that most of the actual conversion happens in the constructor.
        // Please do not move it here, because MailGegevens do not always get constructed here.
        return new MailGegevens(
                new Ontvanger(restMailGegevens.ontvanger),
                restMailGegevens.onderwerp,
                restMailGegevens.body,
                restMailGegevens.bijlagen,
                restMailGegevens.createDocumentFromMail);
    }
}
