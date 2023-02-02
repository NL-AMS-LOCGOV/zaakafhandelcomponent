package net.atos.zac.app.mail.model;

/**
 * REST gegevens voor het verzenden van mail
 */
public class RESTMailGegevens {

    public String verzender;

    public String ontvanger;

    public String replyTo;

    public String onderwerp;

    public String body;

    public String bijlagen;

    public boolean createDocumentFromMail;
}
