package io.rapa.backendcrossing.common.util;

import io.rapa.backendcrossing.common.constants.ErrorCode;
import io.rapa.backendcrossing.common.exception.CustomException;

public final class PreConditions {
    public static void validate(boolean expression, ErrorCode errorCode){
        if (!expression) throw new CustomException(errorCode);
    }
}