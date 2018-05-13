/*
 * Copyright (c) 2018  Rubicon Bot Development Team
 * Licensed under the GPL-3.0 license.
 * The full license text is available in the LICENSE file provided with this project.
 */

package fun.rubicon.core.translation;

import fun.rubicon.RubiconBot;
import fun.rubicon.core.entities.RubiconUser;
import fun.rubicon.util.Logger;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Manages translation bundles.
 *
 * @author tr808axm
 */
public class TranslationManager {
    private final List<TranslationLocale> translationLocaleList;
    private final TranslationLocale defaultTranslationLocale;

    public TranslationManager() {
        defaultTranslationLocale = new TranslationLocale(this, new Locale("en", "US"), "English (United States)") {
            @Override
            public String getTranslationOrDefault(String key) {
                if (getResourceBundle().containsKey(key))
                    return getResourceBundle().getString(key);
                else {
                    Logger.error("TranslationLocale for '" + key + "' missing in default locale " + getLocaleCode());
                    return "Missing translation.";
                }
            }
        };
        List<TranslationLocale> translationLocales = new ArrayList<>();
        translationLocales.add(defaultTranslationLocale);
        translationLocales.add(new TranslationLocale(this, new Locale("de", "DE"), "German (Germany)"));
        translationLocales.add(new TranslationLocale(this, new Locale("de", "AT"), "German (Austria)"));
        translationLocales.add(new TranslationLocale(this, new Locale("en", "US"), "English (America)"));
        translationLocales.add(new TranslationLocale(this, new Locale("es", "ES"), "Spanish (Spain)"));
        translationLocales.add(new TranslationLocale(this, new Locale("fi", "FI"), "Finnish (Finland)"));
        translationLocales.add(new TranslationLocale(this, new Locale("fr", "FR"), "French (France)"));
        translationLocales.add(new TranslationLocale(this, new Locale("hu", "HU"), "Hungarian (Hungary)"));
        translationLocales.add(new TranslationLocale(this, new Locale("it", "IT"), "Italian (Italy)"));
        translationLocales.add(new TranslationLocale(this, new Locale("no", "NO"), "Norwegian (Norway)"));
        translationLocales.add(new TranslationLocale(this, new Locale("pt", "BR"), "Portuguese (Brasilia)"));
        translationLocales.add(new TranslationLocale(this, new Locale("pt", "PT"), "Portuguese (Portugal)"));
        translationLocaleList = Collections.unmodifiableList(translationLocales);

        RubiconBot.getCommandManager().registerCommandHandler(new LanguageCommandHandler(this));
    }

    public TranslationLocale getDefaultTranslationLocale() {
        return defaultTranslationLocale;
    }

    public TranslationLocale getTranslationLocaleByLocale(Locale locale) {
        for (TranslationLocale translationLocale : translationLocaleList)
            if (translationLocale.getLocale().equals(locale))
                return translationLocale;
        return null;
    }

    public TranslationLocale getTranslationLocaleByLocaleOrDefault(Locale locale) {
        TranslationLocale found = getTranslationLocaleByLocale(locale);
        return found == null ? defaultTranslationLocale : found;
    }

    public TranslationLocale getUserLocale(User user) {
        String languageTag = RubiconUser.fromUser(user).getLanguage();
        try {
            Locale locale = Locale.forLanguageTag(languageTag);
            TranslationLocale translationLocale = getTranslationLocaleByLocale(locale);
            translationLocale.getResourceBundle();
            return getTranslationLocaleByLocale(locale);
        } catch (Exception ex) {
            return defaultTranslationLocale;
        }
    }

    public List<TranslationLocale> getLocales() {
        return translationLocaleList;
    }
}
