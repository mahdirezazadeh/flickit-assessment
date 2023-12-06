package org.flickit.assessment.kit.application.service.assessmentkit.validate.impl;

import org.flickit.assessment.kit.common.MessageBundle;
import org.flickit.assessment.kit.common.Notification;

import java.util.Set;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public record InvalidAdditionError(String fieldName, Set<String> addedItems) implements Notification.Error {

    @Override
    public String message() {
        String itemsStr = String.join(", ", addedItems);
        return MessageBundle.message(UPDATE_KIT_BY_DSL_ADDITION_UNSUPPORTED,
            MessageBundle.message(entityNameSingleFirst(fieldName)),
            MessageBundle.message(entityNamePlural(fieldName)),
            itemsStr);
    }
}