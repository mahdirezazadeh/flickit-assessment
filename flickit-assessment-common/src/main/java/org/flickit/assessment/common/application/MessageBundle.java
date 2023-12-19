package org.flickit.assessment.common.application;

import lombok.experimental.UtilityClass;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

@UtilityClass
public class MessageBundle {

    private static final MessageSource messageSource = messageSource();

    public static String message(String key, Object... args) {
        return message(key, Locale.ENGLISH, args);
    }

	public static String message(String key, Locale locale, Object... args) {
		return messageSource.getMessage(key, args, locale);
	}

    private static MessageSource messageSource() {
        var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/core/messages", "i18n/kit/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.ENGLISH);
        return messageSource;
    }

}
