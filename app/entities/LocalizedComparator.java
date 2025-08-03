package entities;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class LocalizedComparator implements Comparator<LocalizedEntity> {
    private String lang;
    private Collator collator;

    private static LocalizedComparator DE = new LocalizedComparator("de", Locale.GERMANY);

    private static LocalizedComparator EN = new LocalizedComparator("en", Locale.ENGLISH);

    public static LocalizedComparator get(String lang) {
        if ("de".equals(lang)) {
            return DE;
        } else if ("en".equals(lang)) {
            return EN;
        } else {
            throw new IllegalArgumentException("Language " + lang + " not supported");
        }
    }

    private LocalizedComparator(String lang, Locale locale) {
        this.lang = lang;
        this.collator = Collator.getInstance(locale);
    }

    @Override
    public int compare(LocalizedEntity e1, LocalizedEntity e2) {
        return collator.compare(e1.getName(lang), e2.getName(lang));
    }
}
