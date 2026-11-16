package com.example.dominio.pagos;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.example.dominio.excepciones.DatoInvalidoException;

/**
 * Catálogo de formas de pago disponibles: asocia el texto que escribe el usuario con la fábrica
 * correspondiente. Para sumar una forma nueva alcanza con registrar una fábrica nueva.
 */
public class FormasDePago {
    private final Map<String, FabricaPago> fabricas = new LinkedHashMap<>();

    public FormasDePago() {
        registrar("efectivo", new FabricaPagoEfectivo());
        registrar("transferencia", new FabricaPagoTransferencia());
        registrar("tarjeta", new FabricaPagoTarjeta());
    }

    public final void registrar(String nombre, FabricaPago fabrica) { fabricas.put(normalizar(nombre), fabrica); }

    public Set<String> disponibles() { return fabricas.keySet(); }

    /** Devuelve la fábrica de la forma de pago pedida (sin distinguir mayúsculas ni tildes). */
    public FabricaPago fabricaPara(String texto) {
        FabricaPago fabrica = fabricas.get(normalizar(texto));
        if (fabrica == null)
            throw new DatoInvalidoException("Forma de pago inválida (" + String.join(", ", fabricas.keySet()) + "): " + texto);
        return fabrica;
    }

    private static String normalizar(String texto) {
        if (texto == null) return "";
        return Normalizer.normalize(texto.trim().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}
