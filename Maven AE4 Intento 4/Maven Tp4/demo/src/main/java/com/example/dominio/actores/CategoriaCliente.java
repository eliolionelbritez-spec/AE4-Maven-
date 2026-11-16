package com.example.dominio.actores;

import com.example.dominio.descuentos.DescuentoCorporativo;
import com.example.dominio.descuentos.DescuentoPremium;
import com.example.dominio.descuentos.DescuentoRegular;
import com.example.dominio.descuentos.EstrategiaDescuento;
import com.example.dominio.excepciones.DatoInvalidoException;

/**
 * Categorías válidas de cliente. Cada una conoce su política de descuento (Strategy),
 * así que agregar una categoría no obliga a tocar Factura ni Cliente.
 */
public enum CategoriaCliente {
    REGULAR(new DescuentoRegular()),
    PREMIUM(new DescuentoPremium()),
    CORPORATIVO(new DescuentoCorporativo());

    private final EstrategiaDescuento descuento;

    CategoriaCliente(EstrategiaDescuento descuento) { this.descuento = descuento; }

    public EstrategiaDescuento getEstrategiaDescuento() { return descuento; }

    /** Convierte el texto ingresado (sin distinguir mayúsculas) o falla si no es una categoría válida. */
    public static CategoriaCliente desde(String texto) {
        for (CategoriaCliente c : values())
            if (c.name().equalsIgnoreCase(texto)) return c;
        throw new DatoInvalidoException("Categoría inválida (regular, premium o corporativo): " + texto);
    }

    @Override
    public String toString() { return name().toLowerCase(); }
}
