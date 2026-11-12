package com.int_solutions.sys.service.userService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImp implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    @Async
    public void enviarActivacion(
            String destinatario, String nombreCompleto,
            String userName, String password, String tokenActivacion, String tipoUsuario) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(remitente, "INT-SOLUTIONS Soporte Técnico");
            helper.setTo(destinatario);
            helper.setSubject("Bienvenido a INT-SOLUTIONS - Activación de Cuenta y Credenciales");

            String urlActivacion = UriComponentsBuilder.fromUriString(frontendUrl)
                    .path("/activar-cuenta")
                    .queryParam("token", tokenActivacion)
                    .build()
                    .encode()
                    .toUriString();

            String contenidoHtml = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
                <div style="background-color: #4992BF; color: #ffffff; padding: 20px; text-align: center;">
                    <h2 style="margin: 0;">INT-SOLUTIONS</h2>
                    <p style="margin: 5px 0 0 0; font-size: 14px;">Soluciones Inteligentes en Tecnología</p>
                </div>
                <div style="padding: 25px; color: #333333; line-height: 1.6;">
                    <p>Estimado(a) <strong>%s</strong>,</p>
                    <p>Se ha creado exitosamente su cuenta de <strong>%s</strong> en la plataforma de INT-SOLUTIONS.</p>
                    <div style="background-color: #f8f9fa; border-left: 4px solid #4992BF; padding: 15px; margin: 20px 0;">
                        <p style="margin: 0 0 8px 0;"><strong>Usuario / Identificación:</strong> %s</p>
                        <p style="margin: 0;"><strong>Contraseña temporal:</strong> <code style="background: #e9ecef; padding: 3px 6px; border-radius: 4px; font-size: 15px;">%s</code></p>
                    </div>
                    <p>Para activar su cuenta y habilitar el acceso al sistema, por favor haga clic en el siguiente botón:</p>
                    <div style="text-align: center; margin: 25px 0;">
                        <a href="%s" style="background-color: #4992BF; color: #ffffff; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">Activar Cuenta</a>
                    </div>
                    <p style="font-size: 13px; color: #666666; background-color: #fff3cd; border: 1px solid #ffeeba; border-radius: 4px; padding: 10px;">
                        <strong>Importante:</strong> Al activar su cuenta, ingrese a la plataforma con su usuario y contraseña temporal. Por políticas de seguridad, en su primer inicio de sesión el sistema le solicitará actualizar obligatoriamente su contraseña.
                    </p>
                </div>
            </div>
        """.formatted(nombreCompleto, tipoUsuario, userName, password, urlActivacion);

            helper.setText(contenidoHtml, true);
            mailSender.send(mensaje);
            log.info("Correo de activación y credenciales temporales enviado exitosamente a: {}", destinatario);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Error al enviar el correo de activación a {}: {}", destinatario, e.getMessage());
        }
    }
}
