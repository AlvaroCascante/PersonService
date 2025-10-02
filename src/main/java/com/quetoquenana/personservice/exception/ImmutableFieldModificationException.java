package com.quetoquenana.personservice.exception;

import lombok.Getter;

@Getter
public class ImmutableFieldModificationException extends RuntimeException {
    private final String messageKey;
    private final Object[] messageArgs;

    public ImmutableFieldModificationException(String messageKey, Object... messageArgs) {
        super(messageKey);
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

}
