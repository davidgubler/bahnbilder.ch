package services;

import com.google.inject.Inject;
import controllers.routes;
import entities.CalendarOrder;
import entities.User;
import i18n.Txt;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import utils.Config;

import java.util.List;


public class Mail {
    @Inject
    private MailerClient mailerClient;

    public void caledarConfirmation(CalendarOrder order, String lang) {
        String subject;
        String body = "";
        subject = Txt.get(lang, "calendar") + " " + Config.Option.CALENDAR_YEAR.getInt() + " " + Txt.get(lang, "calendarOrdered");
        body += Txt.get(lang, "hi", order.getFirstName() + " " + order.getLastName()) + "\n";
        body += "\n";
        body += Txt.get(lang, "calendarOrderedYouHave") + ":\n";
        body += "\n";
        body += Txt.get(lang, "nrOfRailCalendars") + ": " + order.getNrOfRailCalendars() + " (" + order.getRailCalendarCost(lang) + ")\n";
        body += Txt.get(lang, "nrOfAnimalCalendars") + ": " + order.getNrOfAnimalCalendars() + " (" + order.getAnimalCalendarCost(lang) + ")\n";
        body += Txt.get(lang, "addressShipping") + ":\n";
        body += order.getAddressStreetAndNr() + "\n";
        if (order.getAddressRemarks() != null) {
            body += order.getAddressRemarks() + "\n";
        }
        body += order.getZip() + " " + order.getCity() + "\n";
        body += order.getCountry() + "\n";
        body += Txt.get(lang, "shippingCost") + ": " + order.getShippingCost(lang) + "\n";
        body += Txt.get(lang, "payment") + ": " + Txt.get(lang, "invoiceFollowsViaEmail") + "\n";
        body += Txt.get(lang, "shippingDate") + ": " + Txt.get(lang, "shippingDateDesc") + "\n";
        body += "\n";
        body += Txt.get(lang, "calendarMoreInformation") + "\n";
        body += "\n";
        body += "          - " + Txt.get(lang, "team") + "\n";
        send(Config.Option.CALENDAR_EMAIL.get(), order.getEmail(), subject, body);
    }

    private void send(String replyTo, String to, String subject, String body) {
        Email m = new Email();
        m.setSubject(subject);
        m.addTo(to);
        m.setBodyText(body);
        m.setFrom(Config.Option.MAIL_FROM.get());
        m.setReplyTo(List.of(replyTo));
        mailerClient.send(m);
    }

    public void lostPassword(User user, String host, String baseUrl, String lang, Long ts, String sig) {
        String subject = Txt.get(lang, "lostPasswordSubject", host);
        String body = "";
        body += Txt.get(lang, "hi", user.getName()) + "\n";
        body += "\n";
        body += Txt.get(lang, "lostPasswordMail1") + "\n";
        body += "\n";
        body += Txt.get(lang, "lostPasswordMail2") + "\n";
        body += baseUrl + routes.LoginController.newPw(ts, user.getEmail(), sig) + "\n";
        body += "\n";
        body += Txt.get(lang, "lostPasswordMail3") + "\n";
        body += baseUrl + routes.LoginController.linkLogin(ts, user.getEmail(), sig) + "\n";
        body += "\n";
        body += "          - " + Txt.get(lang, "team") + "\n";
        send(Config.Option.MAIL_FROM.get(), user.getEmail(), subject, body);
    }
}
