package i18n;

import utils.Config;
import utils.ErrorMessages;

import java.util.HashMap;
import java.util.Map;

public class Txt {
    public static final Map<String, Map<String, String>> TRANSLATIONS;

    static {
        Map<String, String> en = new HashMap<>();
        Map<String, String> de = new HashMap<>();

        en.put("about", "About us");
        de.put("about", "Über uns");

        en.put("addressStreetAndNr", "Street and Nr.");
        de.put("addressStreetAndNr", "Strasse, Nr.");

        en.put("addressRemarks", "Address Remarks");
        de.put("addressRemarks", "Adresszusatz");

        en.put("addressShipping", "Shipping address");
        de.put("addressShipping", "Lieferadresse");

        en.put("allSeries", "All series");
        de.put("allSeries", "Alle Fahrzeugfamilien");

        en.put("and", "and");
        de.put("and", "und");

        en.put("aperture", "Aperture");
        de.put("aperture", "Blende");

        en.put("asInvoiced", "Price as invoiced");
        de.put("asInvoiced", "Preis gemäss Rechnung");

        en.put("atLoc", "at");
        de.put("atLoc", "in");

        en.put("author", "Author");
        de.put("author", "Autor");

        en.put("autodetect", "Autodetect");
        de.put("autodetect", "Automatische Erkennung");

        en.put("back", "Back");
        de.put("back", "Zurück");

        en.put("between", "between");
        de.put("between", "zwischen");

        en.put("browse", "Browse");
        de.put("browse", "Durchsuchen");

        en.put("calendar", "Calendar");
        de.put("calendar", "Kalender");

        en.put("calendarOrder", "Order calendars");
        de.put("calendarOrder", "Kalender bestellen");

        en.put("calendarMoreInformation", "More information about this and future calendars is available at https://rail.pictures/calendar");
        de.put("calendarMoreInformation", "Weitere Informationen zu diesem und zukünftigen Kalendern findest du unter https://bahnbilder.ch/calendar");

        en.put("calendarOrdered", "ordered from rail.pictures");
        de.put("calendarOrdered", "bestellt bei bahnbilder.ch");

        en.put("calendarOrderedYouHave", "You have ordered the following calendar(s)");
        de.put("calendarOrderedYouHave", "Du hast die folgenden Kalender bestellt");

        en.put("calendarThanksForOrder", "Thank you for your order! We hope you enjoy our calendar and have a merry christmas!");
        de.put("calendarThanksForOrder", "Vielen Dank für die Bestellung! Wir wünschen viel Freude und frohe Weihnachten!");

        en.put("team", "The rail.pictures team");
        de.put("team", "Das Team von bahnbilder.ch");

        en.put("camera", "Camera");
        de.put("camera", "Kamera");

        en.put("cancel", "Cancel");
        de.put("cancel", "Abbrechen");

        en.put("chooseCountry", "Choose country");
        de.put("chooseCountry", "Land wählen");

        en.put("city", "City");
        de.put("city", "Ort");

        en.put("collection", "Collection");
        de.put("collection", "Sammlung");

        en.put("copyToClipboard", "Copy to Clipboard");
        de.put("copyToClipboard", "In Zwischenablage kopieren");

        en.put("countriesByViews", "Top countries by views");
        de.put("countriesByViews", "Länder nach Aufrufen");

        en.put("coordinates", "Coordinates");
        de.put("coordinates", "Koordinaten");

        en.put("countries", "Countries");
        de.put("countries", "Länder");

        en.put("country", "Country");
        de.put("country", "Land");

        en.put("countryCH", "Switzerland");
        de.put("countryCH", "Schweiz");

        en.put("countryDE", "Germany");
        de.put("countryDE", "Deutschland");

        en.put("countryAT", "Austria");
        de.put("countryAT", "Österreich");

        en.put("createCountry", "Create country");
        de.put("createCountry", "Land erstellen");

        en.put("createKeyword", "Create keyword");
        de.put("createKeyword", "Stichwort erstellen");

        en.put("createOperator", "Create operator");
        de.put("createOperator", "Betreiber erstellen");

        en.put("createVehicleSeries", "Create series");
        de.put("createVehicleSeries", "Fahrzeugfamilie erstellen");

        en.put("createVehicleClass", "Add class");
        de.put("createVehicleClass", "Baureihe erstellen");

        en.put("date", "Date");
        de.put("date", "Datum");

        en.put("delete", "Delete");
        de.put("delete", "Löschen");

        en.put("description", "Description");
        de.put("description", "Beschreibung");

        en.put("download", "Download");
        de.put("download", "Herunterladen");

        en.put("drone", "Drone");
        de.put("drone", "Drohne");

        en.put("email", "Email Address");
        de.put("email", "E-Mail-Adresse");

        en.put("emailConfirm", "Confirm email");
        de.put("emailConfirm", "E-Mail wiederholen");

        en.put("error", "Error");
        de.put("error", "Fehler");

        en.put(ErrorMessages.ACCEPT_TC, "Our terms and conditions must be accepted");
        de.put(ErrorMessages.ACCEPT_TC, "Unsere Geschäftsbedingungen müssen akzeptiert werden");

        en.put(ErrorMessages.ALREADY_EXISTS, "Already exists");
        de.put(ErrorMessages.ALREADY_EXISTS, "Existiert bereits");

        en.put(ErrorMessages.MUST_SET_DATE_AND_TIME, "Date and time must both be set");
        de.put(ErrorMessages.MUST_SET_DATE_AND_TIME, "Datum und Zeit müssen gesetzt sein");

        en.put(ErrorMessages.MISSING_VALUE, "Please fill in this field");
        de.put(ErrorMessages.MISSING_VALUE, "Bitte ausfüllen");

        en.put(ErrorMessages.PHOTO_INVALID_EXIF, "Photo has invalid EXIF data");
        de.put(ErrorMessages.PHOTO_INVALID_EXIF, "Foto hat ungültige EXIF-Daten");

        en.put(ErrorMessages.PHOTO_TOO_LARGE, "Photo too large (>" + Config.MAX_PHOTO_SIZE / 1024 / 1024 + " MiB)");
        de.put(ErrorMessages.PHOTO_TOO_LARGE, "Foto zu gross (>" + Config.MAX_PHOTO_SIZE / 1024 / 1024 + " MiB)");

        en.put(ErrorMessages.PHOTO_WRONG_FORMAT, "Photo in wrong format (must be JPEG)");
        de.put(ErrorMessages.PHOTO_WRONG_FORMAT, "Foto in falschem Format (nur JPEG akzeptiert)");

        en.put(ErrorMessages.INVALID_EMAIL_DOES_NOT_MATCH, "Email addresses do not match");
        de.put(ErrorMessages.INVALID_EMAIL_DOES_NOT_MATCH, "E-Mail-Adressen stimmen nicht überein");

        en.put(ErrorMessages.INVALID_EMAIL, "Invalid email address");
        de.put(ErrorMessages.INVALID_EMAIL, "E-Mail-Adresse ungültig");

        en.put(ErrorMessages.INVALID_EMAIL_OR_PASSWORD, "Invalid email address or password");
        de.put(ErrorMessages.INVALID_EMAIL_OR_PASSWORD, "E-Mail-Adresse oder Passwort ungültig");

        en.put(ErrorMessages.INVALID_PHOTO_ID, "Invalid photo ID");
        de.put(ErrorMessages.INVALID_PHOTO_ID, "Ungültige Foto-ID");

        en.put(ErrorMessages.INVALID_VALUE, "Invalid value");
        de.put(ErrorMessages.INVALID_VALUE, "Ungültige Eingabe");

        en.put("errorNoCountry", "A country must be set");
        de.put("errorNoCountry", "Ein Land muss gesetzt sein");

        en.put("exposureTime", "Exposure time");
        de.put("exposureTime", "Belichtungszeit");

        en.put("findAllOfSeries", "Find other classes of the same series");
        de.put("findAllOfSeries", "Alle Fahrzeuge dieser Familie finden");

        en.put("firstName", "First Name");
        de.put("firstName", "Vorname");

        en.put("focalLength", "Focal length");
        de.put("focalLength", "Brennweite");

        en.put("followUsOnFacebook", "Follow us on Facebook");
        de.put("followUsOnFacebook", "Folge uns auf Facebook");

        en.put("fromDate", "from");
        de.put("fromDate", "vom");

        en.put("fromDateAlt", "from");
        de.put("fromDateAlt", "ab");

        en.put("fullscreen", "Full-screen");
        de.put("fullscreen", "Vollbild");

        en.put("fullscreenEnd", "Exit full-screen");
        de.put("fullscreenEnd", "Vollbild beenden");

        en.put("hi", "Hi {0},");
        de.put("hi", "Hallo {0},");

        en.put("home", "Home");
        de.put("home", "Home");

        en.put("incomplete", "Incomplete");
        de.put("incomplete", "Unvollständig");

        en.put("incompleteDeleteSelected", "Delete Selected");
        de.put("incompleteDeleteSelected", "Ausgewählte löschen");

        en.put("incompleteModifySelected", "Modify Selected");
        de.put("incompleteModifySelected", "Ausgewählte ändern");

        en.put("incompleteSelectAll", "Select all");
        de.put("incompleteSelectAll", "Alle auswählen");

        en.put("incompleteSelectNone", "Select none");
        de.put("incompleteSelectNone", "Keine auswählen");

        en.put("invoiceFollowsViaEmail", "Invoice follows via email");
        de.put("invoiceFollowsViaEmail", "Rechnung folgt per E-Mail");

        en.put("keywords", "Keywords");
        de.put("keywords", "Stichworte");

        en.put("labels", "Labels");
        de.put("labels", "Labels");

        en.put("lastName", "Last Name");
        de.put("lastName", "Nachname");

        en.put("latest", "Latest");
        de.put("latest", "Aktuell");

        en.put("license", "License");
        de.put("license", "Lizenz");

        en.put("location", "Location");
        de.put("location", "Ort");

        en.put("locationCreateNewHint", "or create new location");
        de.put("locationCreateNewHint", "oder neuen Ort anlegen");

        en.put("locationHint", "<b>[Place]</b> or <b>[Place] - [Place]</b> or <b>[City], [Stop]</b>");
        de.put("locationHint", "<b>[Ort]</b> oder <b>[Ort] - [Ort]</b> oder <b>[Stadt], [Haltestelle]</b>");

        en.put("login", "Log in");
        de.put("login", "Anmelden");

        en.put("logo", "<span style=\"color: #a0a0a0; font-size: 30px;\">rail.pictures</span>");
        de.put("logo", "<span style=\"color: #a0a0a0; font-size: 30px;\">bahnbilder</span><span style=\"color: #707070; font-size: 22px;\">.ch</span>");

        en.put("logout", "Log out");
        de.put("logout", "Abmelden");

        en.put("logoutUser", "Log out {0}");
        de.put("logoutUser", "{0} abmelden");

        en.put("lostPassword", "Lost password");
        de.put("lostPassword", "Passwort verloren");

        en.put("lostPasswordLinkExpired", "This link has expired. The links in the password recovery email are only valid for 10 minutes.");
        de.put("lostPasswordLinkExpired", "Dieser Link ist nicht mehr gültig. Die Links im Passwort-Wiederherstellungsmail sind nur 10 Minuten gültig.");

        en.put("lostPasswordMail", "Send password recovery mail");
        de.put("lostPasswordMail", "Passwort-Wiederherstellungsmail senden");

        en.put("lostPasswordMailSent", "Password recovery mail sent. Please check your mailbox.");
        de.put("lostPasswordMailSent", "Passwort-Wiederherstellungsmail gesendet. Bitte schaue in deinem E-Mail-Postfach nach.");

        en.put("lostPasswordSubject", "Password recovery for {0}");
        de.put("lostPasswordSubject", "Passwort-Wiederherstellung für {0}");

        en.put("lostPasswordMail1", "Somebody has requested a password recovery mail for your address. If this wasn't you you should delete this email.");
        de.put("lostPasswordMail1", "Jemand hat für deine E-Mail-Adresse ein Passwort-Wiederherstellungsmail angefordert. Falls das nicht du warst solltest du dieses Mail löschen.");

        en.put("lostPasswordMail2", "If you want to request a new password please click the following link:");
        de.put("lostPasswordMail2", "Falls du ein neues Passwort anfordern möchtest, klicke den folgenden Link:");

        en.put("lostPasswordMail3", "If you just want to log in without changing your password click the following link:");
        de.put("lostPasswordMail3", "Falls du dich einfach anmelden möchtest ohne dein Passwort zu ändern, klicke den folgenden Link:");

        en.put("lostPasswordNewPw", "You have successfully created a new password! Please store it in your password manager. You can only view it once.");
        de.put("lostPasswordNewPw", "Du hast erfolgreich ein neues Passwort erstellt! Bitte speichere es in deinem Passwort-Manager. Du kannst es nur einmal ansehen.");

        en.put("manage", "Manage");
        de.put("manage", "Verwaltung");

        en.put("map", "Photo Map");
        de.put("map", "Fotokarte");

        en.put("modifySearch", "Modify Search");
        de.put("modifySearch", "Suche ändern");

        en.put("more", "More");
        de.put("more", "Mehr");

        en.put("name", "Name");
        de.put("name", "Name");

        en.put("nameEn", "Name (English)");
        de.put("nameEn", "Name (Englisch)");

        en.put("nameDe", "Name (German)");
        de.put("nameDe", "Name (Deutsch)");

        en.put("nameShort", "Abbreviation");
        de.put("nameShort", "Kürzel");

        en.put("nameNumberFormat", "Format with number");
        de.put("nameNumberFormat", "Darstellung mit Nummer");

        en.put("nameNumberHint1", "e.g. CC #, Re 460 ###, Bhe 1/2 #");
        de.put("nameNumberHint1", "z.B. CC #, Re 460 ###, Bhe 1/2 #");

        en.put("nameNumberHint2", "Number of # = min. length (leading zeros if necessary)");
        de.put("nameNumberHint2", "Anzahl # = Mindestlänge (ggf. führende Nullen)");

        en.put("newest", "Newest Photos");
        de.put("newest", "Neueste Fotos");

        en.put("newLocation", "");
        de.put("newLocation", "");

        en.put("noRestriction", "No restriction");
        de.put("noRestriction", "Keine Einschränkung");

        en.put("not", "not");
        de.put("not", "nicht");

        en.put("nr", "Number");
        de.put("nr", "Nummer");

        en.put("nrOfRailCalendars", "Number of rail calendars");
        de.put("nrOfRailCalendars", "Anzahl Eisenbahnkalender");

        en.put("nrOfAnimalCalendars", "Number of animal calendars");
        de.put("nrOfAnimalCalendars", "Anzahl Tierkalender");

        en.put("nrOfAnimalCalendarsExplanation", "The animal calendar is a private project of Daniel Wipf and can be ordered here as well");
        de.put("nrOfAnimalCalendarsExplanation", "Der Tierkalender ist ein Privatprojekt von Daniel Wipf und kann ebenfalls hier bestellt werden");

        en.put("operator", "Operator");
        de.put("operator", "Betreiber");

        en.put("operators", "Operators");
        de.put("operators", "Betreiber");

        en.put("operatorsNoWikidata", "Operators without Wikidata");
        de.put("operatorsNoWikidata", "Betreiber ohne Wikidata");

        en.put("orderConfirm", "Confirm Order");
        de.put("orderConfirm", "Bestellung bestätigen");

        en.put("orderPreview", "Order preview");
        de.put("orderPreview", "Bestellungs-Vorschau");

        en.put("orderSend", "Send Order");
        de.put("orderSend", "Bestellung absenden");

        en.put("other", "Other");
        de.put("other", "Anderes");

        en.put("others", "Others");
        de.put("others", "Andere");

        en.put("partOfSeries", "Part of the {0} series.");
        de.put("partOfSeries", "Gehört zur Familie der {0}.");

        en.put("password", "Password");
        de.put("password", "Passwort");

        en.put("payment", "Payment");
        de.put("payment", "Bezahlung");

        en.put("photographer", "Photographer");
        de.put("photographer", "Fotograf");

        en.put("photographerHint", "Leave empty unless this photo was not taken by you");
        de.put("photographerHint", "Nur ausfüllen wenn das Bild nicht von dir ist");

        en.put("photo", "Photo");
        de.put("photo", "Foto");

        en.put("photos", "Photos");
        de.put("photos", "Fotos");

        en.put("photosDelete", "Delete photos");
        de.put("photosDelete", "Fotos löschen");

        en.put("photosEdit", "Edit photos");
        de.put("photosEdit", "Fotos bearbeiten");

        en.put("photoDate", "Photo date");
        de.put("photoDate", "Aufnahmedatum");

        en.put("photoDetails", "Photo details");
        de.put("photoDetails", "Aufnahmedetails");

        en.put("photoType", "Photo type");
        de.put("photoType", "Aufnahmetyp");

        en.put("privacyPolicy", "Privacy Policy");
        de.put("privacyPolicy", "Datenschutzerklärung");

        en.put("propulsion", "Propulsion");
        de.put("propulsion", "Antrieb");

        en.put("rating", "Rating");
        de.put("rating", "Bewertung");

        en.put("reallyDelete", "Yes, delete it!");
        de.put("reallyDelete", "Ja, löschen!");

        en.put("save", "Save");
        de.put("save", "Speichern");

        en.put("search", "Search");
        de.put("search", "Suchen");

        en.put("sensitivity", "Sensitivity");
        de.put("sensitivity", "Empfindlichkeit");

        en.put("series", "Series");
        de.put("series", "Fahrzeugfamilie");

        en.put("share", "Share");
        de.put("share", "Weiterleiten");

        en.put("shippingCost", "Shipping cost");
        de.put("shippingCost", "Versandkosten");

        en.put("shippingDate", "Shipping date");
        de.put("shippingDate", "Versandzeitpunkt");

        en.put("shippingDateDesc", "Shipped ca. 1 week before 24th of December for delivery before christmas");
        de.put("shippingDateDesc", "Postaufgabe ca. 1 Woche vor dem 24. Dezember für Lieferung vor Weihnachten");

        en.put("showDetails", "Show details");
        de.put("showDetails", "Details anzeigen");

        en.put("showOnMap", "Show on map");
        de.put("showOnMap", "Karte zeigen");

        en.put("showList", "Show as list");
        de.put("showList", "Liste zeigen");

        en.put("siteName", "rail.pictures");
        de.put("siteName", "bahnbilder.ch");

        en.put("solutions", "Solutions");
        de.put("solutions", "Lösungen");

        en.put("sortBy", "Sort by");
        de.put("sortBy", "Sortierung");

        en.put("startOver", "Start over");
        de.put("startOver", "Zur Hauptseite");

        en.put("stats", "Stats");
        de.put("stats", "Statistiken");

        en.put("suggestions", "Suggestions");
        de.put("suggestions", "Vorschläge");

        en.put("summary", "Summary");
        de.put("summary", "Zusammenfassung");

        en.put("switchLanguage", "Deutsch");
        de.put("switchLanguage", "English");

        en.put("systemState", "System State");
        de.put("systemState", "Systemzustand");

        en.put("systemStateDbIsWritable", "Database is writable");
        de.put("systemStateDbIsWritable", "Datenbank ist beschreibbar");

        en.put("systemStateReplSetStatus", "Replica Set status");
        de.put("systemStateReplSetStatus", "Replica Set-Status");

        en.put("systemStateNotAvailable", "not available");
        de.put("systemStateNotAvailable", "nicht verfügbar");

        en.put("tagPic", "Insert photo with your own label");
        de.put("tagPic", "Foto mit eigener Bildunterschrift einfügen");

        en.put("tagPicauto", "Insert photo with generated label");
        de.put("tagPicauto", "Foto mit generierter Bildunterschrift einfügen");

        en.put("tagTitle", "Insert title");
        de.put("tagTitle", "Titel einfügen");

        en.put("tagBold", "Highlight text in bold");
        de.put("tagBold", "Text fett hervorheben");

        en.put("tagItalic", "Highlight text in italic");
        de.put("tagItalic", "Text kursiv hervorheben");

        en.put("tagLink", "Link without description (always include https://)");
        de.put("tagLink", "Link ohne Beschreibung (immer mit https://)");

        en.put("tagSize", "Font size in pixels");
        de.put("tagSize", "Textgrösse in Pixeln");

        en.put("tagColor", "Change text color (use color names or RGB values like #rrggbb)");
        de.put("tagColor", "Textfarbe ändern (Namen oder RGB-Werte wie #rrggbb verwenden)");

        en.put("tagUrl", "Link with description (always include https://)");
        de.put("tagUrl", "Link mit Beschreibung (immer mit https://)");

        en.put("text", "Text");
        de.put("text", "Text");

        en.put("texts", "Texts");
        de.put("texts", "Texte");

        en.put("time", "Time");
        de.put("time", "Zeit");

        en.put("title", "Title");
        de.put("title", "Titel");

        en.put("titlePhoto", "Title photo");
        de.put("titlePhoto", "Titelbild");

        en.put("to", "to");
        de.put("to", "bis");

        en.put("total", "Total");
        de.put("total", "Total");

        en.put("acceptTC", "I accept the terms and conditions");
        de.put("acceptTC", "Ich akzeptiere die Geschäftsbedingungen");

        en.put("travelogues", "Travelogues");
        de.put("travelogues", "Reiseberichte");

        en.put("travelogueCreate", "Create travelogues");
        de.put("travelogueCreate", "Reisebericht erstellen");

        en.put("unused", "Unused");
        de.put("unused", "Unbenutzt");

        en.put("updateCountry", "Edit Country");
        de.put("updateCountry", "Land bearbeiten");

        en.put("updateKeyword", "Edit Keyword");
        de.put("updateKeyword", "Stichwort bearbeiten");

        en.put("updateOperator", "Edit Operator");
        de.put("updateOperator", "Betreiber bearbeiten");

        en.put("updatePhotos", "Edit Photos");
        de.put("updatePhotos", "Fotos bearbeiten");

        en.put("updateVehicleClass", "Edit Class");
        de.put("updateVehicleClass", "Baureihe bearbeiten");

        en.put("updateVehicleSeries", "Edit Series");
        de.put("updateVehicleSeries", "Fahrzeugfamilie bearbeiten");

        en.put("upload", "Upload");
        de.put("upload", "Upload");

        en.put("uploadDate", "Upload date");
        de.put("uploadDate", "Datum des Hochladens");

        en.put("uploadLimits", "You can upload up to 10 photos at a time of up to " + Config.MAX_PHOTO_SIZE / 1024 / 1024 + " MiB each.");
        de.put("uploadLimits", "Es können gleichzeitig bis zu 10 Fotos zu je " + Config.MAX_PHOTO_SIZE / 1024 / 1024 + " MiB hochgeladen werden.");

        en.put("uploadNewVersion", "Upload new version");
        de.put("uploadNewVersion", "Neue Version hochladen");

        en.put("uploadPhotos", "Upload photos");
        de.put("uploadPhotos", "Fotos hochladen");

        en.put("username", "Username");
        de.put("username", "Benutzername");

        en.put("vclass", "Class");
        de.put("vclass", "Baureihe");

        en.put("vclasses", "Classes");
        de.put("vclasses", "Baureihen");

        en.put("vclassesWithoutSeries", "Classes without series");
        de.put("vclassesWithoutSeries", "Baureihen ohne Familie");

        en.put("vclassesWithoutTypeProp", "Classes without type or propulsion");
        de.put("vclassesWithoutTypeProp", "Baureihen ohne Typ oder Antriebssystem");

        en.put("webcamZHB", "Webcam Zürich HB");
        de.put("webcamZHB", "Webcam Zürich HB");

        en.put("vehicleType", "Vehicle type");
        de.put("vehicleType", "Fahrzeugtyp");

        en.put("views", "Views");
        de.put("views", "Aufrufe");

        en.put("wikiData", "WikiData");
        de.put("wikiData", "WikiData");

        en.put("world", "World");
        de.put("world", "Welt");

        en.put("year", "Year");
        de.put("year", "Jahr");

        en.put("zip", "ZIP Code");
        de.put("zip", "Postleitzahl");

        TRANSLATIONS = Map.of("en", en, "de", de);
    }

    public static String get(String lang, String key) {
        String translation = TRANSLATIONS.get(lang).get(key);
        return translation == null ? "MISSINGTRANSLATION[" + key + "]" : translation;
	}

    public static String get(String lang, String key, String... params) {
        String txt = TRANSLATIONS.get(lang).get(key);
        int i = 0;
        for (String param : params) {
            txt = txt.replace("{" + i++ + "}", param);
        }
        return txt;
    }
}
