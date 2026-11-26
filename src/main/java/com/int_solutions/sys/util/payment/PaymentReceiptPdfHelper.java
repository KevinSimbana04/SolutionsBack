package com.int_solutions.sys.util.payment;

import com.int_solutions.sys.entity.payment.Payment;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Component
public class PaymentReceiptPdfHelper {

    public byte[] generarRecibo(Payment pago, BigDecimal totalPresupuestado, BigDecimal totalAbonado, BigDecimal saldoRestante) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, out);
            document.open();

            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font subtituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            Paragraph header = new Paragraph("INT-SOLUTIONS — COMPROBANTE DE PAGO", tituloFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(new Paragraph(" "));

            var orden = pago.getOrdenTrabajo();
            var cliente = orden.getCliente();
            var equipo = orden.getEquipo();

            document.add(new Paragraph("Recibo Nro.: " + pago.getNumeroRecibo(), subtituloFont));
            document.add(new Paragraph("Fecha y hora: " + pago.getFechaPago().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), normalFont));
            document.add(new Paragraph("Estado del recibo: " + pago.getEstadoPago().name(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Orden de Trabajo: " + orden.getCodigo(), subtituloFont));
            document.add(new Paragraph("Cliente: " + cliente.getNombres() + " " + cliente.getApellidos() + " (" + cliente.getIdentificacion() + ")", normalFont));
            document.add(new Paragraph("Equipo: " + equipo.getMarca() + " " + equipo.getModelo() + " | Serie: " + equipo.getNumeroSerie(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Detalle de la Transacción", subtituloFont));
            document.add(new Paragraph("Método de pago: " + pago.getMetodoPago().name(), normalFont));
            if (pago.getReferenciaBancaria() != null) {
                document.add(new Paragraph("Referencia bancaria: " + pago.getReferenciaBancaria(), normalFont));
            }
            document.add(new Paragraph("Monto abonado: $" + pago.getMonto(), subtituloFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Resumen de Saldo de la Orden", subtituloFont));
            document.add(new Paragraph("Total presupuestado: $" + totalPresupuestado, normalFont));
            document.add(new Paragraph("Total abonado activo: $" + totalAbonado, normalFont));
            document.add(new Paragraph("Saldo restante: $" + saldoRestante, subtituloFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Registrado por: " + pago.getUsuarioRegistro().getNombreUsuario(), normalFont));

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el comprobante PDF: " + e.getMessage());
        }
    }
}