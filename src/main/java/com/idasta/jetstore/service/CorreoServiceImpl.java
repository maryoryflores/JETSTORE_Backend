package com.idasta.jetstore.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class CorreoServiceImpl implements CorreoService {

    private static final Logger log = LoggerFactory.getLogger(CorreoServiceImpl.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final boolean habilitado;
    private final String remitente;

    public CorreoServiceImpl(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${jetstore.correo.habilitado:true}") boolean habilitado,
            @Value("${jetstore.correo.remitente:no-responder@jetstore.pe}") String remitente) {
        this.mailSenderProvider = mailSenderProvider;
        this.habilitado = habilitado;
        this.remitente = remitente;
    }

    @Override
    public void enviarCorreoRecuperacion(String destinatario, String nombreUsuario, String enlace) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (habilitado && mailSender != null) {
            try {
                MimeMessage mensaje = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
                helper.setFrom(remitente);
                helper.setTo(destinatario);
                helper.setSubject("JetStore Digital - Recuperación de contraseña");
                helper.setText(contenido(destinatario, nombreUsuario, enlace), true);
                mailSender.send(mensaje);
                log.info("Correo de recuperación enviado a {}", destinatario);
                return;
            } catch (Exception e) {
                log.error("No se pudo enviar el correo de recuperación a {}", destinatario, e);
            }
        }
        log.info("[JETSTORE] Enlace de recuperación para {} ({}): {}", nombreUsuario, destinatario, enlace);
    }

    private String contenido(String destinatario, String nombreUsuario, String enlace) {
        return "<!DOCTYPE html>"
                + "<html lang=\"es\"><body style=\"margin:0;padding:0;background-color:#f3f4f6;font-family:Arial,Helvetica,sans-serif;\">"
                + "<div style=\"max-width:560px;margin:0 auto;padding:24px;\">"
                + "<div style=\"background:#111111;border-radius:16px;padding:32px;color:#ffffff;\">"
                + "<div style=\"width:56px;height:56px;background:#DC2626;border-radius:12px;display:inline-flex;align-items:center;justify-content:center;font-size:24px;font-weight:700;margin-bottom:16px;\">J</div>"
                + "<h2 style=\"margin:0 0 12px;font-size:20px;\">Recuperación de contraseña</h2>"
                + "<p style=\"margin:0 0 8px;color:#c0c0c0;font-size:14px;\">Hola, " + nombreUsuario + ":</p>"
                + "<p style=\"margin:0 0 24px;color:#c0c0c0;font-size:14px;line-height:1.6;\">Recibimos una solicitud para cambiar la contraseña de tu cuenta asociada a <strong>" + destinatario + "</strong>. Haz clic en el siguiente botón para crear una nueva contraseña:</p>"
                + "<a href=\"" + enlace + "\" style=\"display:inline-block;background:#DC2626;color:#ffffff;padding:14px 28px;border-radius:10px;text-decoration:none;font-weight:700;font-size:14px;\">Restablecer contraseña</a>"
                + "<p style=\"margin-top:24px;color:#9ca3af;font-size:12px;line-height:1.6;\">Si no solicitaste este cambio, ignora este correo. El enlace es de un solo uso y expira pronto. Por seguridad, nunca compartas este enlace con nadie.</p>"
                + "</div>"
                + "<p style=\"text-align:center;color:#9ca3af;font-size:12px;margin-top:24px;\">&copy; " + Year.now().getValue() + " JetStore Digital. Todos los derechos reservados.</p>"
                + "</div></body></html>";
    }
}