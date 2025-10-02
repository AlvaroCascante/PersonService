package com.quetoquenana.personservice.exception;

import lombok.Getter;

@Getter
public class RecordNotFoundException extends RuntimeException {
    private final String messageKey;
    private final Object[] messageArgs;

    public RecordNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey);
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

}
