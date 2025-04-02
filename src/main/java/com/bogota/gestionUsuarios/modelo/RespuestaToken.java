package com.bogota.gestionUsuarios.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RespuestaToken(
        @JsonProperty("token_acceso")
        String tokenAcceso,
        @JsonProperty("token_refresco")
        String tokenRefresco
) {
}