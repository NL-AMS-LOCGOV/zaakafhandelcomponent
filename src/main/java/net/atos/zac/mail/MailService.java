package net.atos.zac.mail;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.ConfigProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;

@ApplicationScoped
public class MailService {

    private static final String MAILJET_API_KEY = ConfigProvider.getConfig().getValue("mailjet.api.key", String.class);

    private static final String MAILJET_API_SECRET_KEY = ConfigProvider.getConfig().getValue("mailjet.api.secret.key", String.class);

    private static final String MAIL_DOMEIN = ConfigProvider.getConfig().getValue("mail.domein", String.class);

    private final ClientOptions clientOptions = ClientOptions.builder().apiKey(MAILJET_API_KEY).apiSecretKey(MAILJET_API_SECRET_KEY).build();

    private final MailjetClient mailjetClient = new MailjetClient(clientOptions);

    public MailjetResponse sendMail(final String ontvanger, final String onderwerp, final String body) throws MailjetException {
        final MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES,
                          new JSONArray().put(new JSONObject().put(Emailv31.Message.FROM, new JSONObject()
                                          .put("Email", "zaakafhandelcomponent@" + MAIL_DOMEIN)
                                          .put("Name", "Zaakafhandelcomponent"))
                                                      .put(Emailv31.Message.TO,
                                                           new JSONArray().put(new JSONObject().put("Email", ontvanger)))
                                                      .put(Emailv31.Message.SUBJECT, onderwerp)
                                                      .put(Emailv31.Message.HTMLPART, "<pre>" + body + "</pre>")));

        return mailjetClient.post(request);
    }
}
