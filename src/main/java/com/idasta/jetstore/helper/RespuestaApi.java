package com.idasta.jetstore.helper;

public record RespuestaApi(
        boolean ok,
        String mensaje,
        Object payload
        ) {

}
