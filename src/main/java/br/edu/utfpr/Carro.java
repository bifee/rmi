package br.edu.utfpr;

import java.io.Serializable;

public record Carro(
        String marca,
        String modelo,
        int ano,
        String cambio,
        String tipo
) implements Serializable {}