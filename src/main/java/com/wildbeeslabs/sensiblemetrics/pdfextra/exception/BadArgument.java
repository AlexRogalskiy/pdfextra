package com.wildbeeslabs.sensiblemetrics.pdfextra.exception;

import com.wildbeeslabs.sensiblemetrics.pdfextra.model.DocumentInfo;

public class BadArgument extends GeneralException {
    private Object argument;

    public BadArgument() {
        super();
    }

    public BadArgument(Object argument) {
        super();
        this.argument = argument;
    }

    public BadArgument(Class<?> clazz, String objectId) {
        super();
        try {
            this.argument = clazz.newInstance();

            if (DocumentInfo.class.isAssignableFrom(clazz)) {
                ((DocumentInfo) this.argument).setId(objectId);
            }
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public String toString() {
        if (argument != null) {
            return "BadArgument{" +
                "argument=" + argument +
                '}';
        } else {
            return "BadArgument{argument not specified}";
        }
    }

    public Object getArgument() {
        return argument;
    }
}
