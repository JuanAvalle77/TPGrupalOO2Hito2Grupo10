package com.unla.epicentrogourmet.services.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envio de mails. Si no estan configuradas las variables de entorno
 * MAIL_USERNAME / MAIL_PASSWORD, el mensaje solo se muestra en el log (util en
 * desarrollo). Lo usa, por ejemplo, el alta de empleados.
 */
@Service("mailService")
public class MailService {

	private static final Logger logger = LoggerFactory.getLogger(MailService.class);

	private final JavaMailSender mailSender;
	private final String remitente;
	private final boolean configurado;

	public MailService(JavaMailSender mailSender, @Value("${app.mail.from}") String remitente,
			@Value("${spring.mail.username:}") String usuarioSmtp) {
		this.mailSender = mailSender;
		this.remitente = remitente;
		this.configurado = usuarioSmtp != null && !usuarioSmtp.isBlank();
	}

	public void enviar(String destinatario, String asunto, String texto) {
		if (!configurado) {
			logger.info("MAIL (no configurado, solo log) para {} | {} | {}", destinatario, asunto, texto);
			return;
		}
		SimpleMailMessage mensaje = new SimpleMailMessage();
		mensaje.setFrom(remitente);
		mensaje.setTo(destinatario);
		mensaje.setSubject(asunto);
		mensaje.setText(texto);
		mailSender.send(mensaje);
	}
}
