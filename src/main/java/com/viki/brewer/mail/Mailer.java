package com.viki.brewer.mail;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.viki.brewer.model.Cerveja;
import com.viki.brewer.model.ItemVenda;
import com.viki.brewer.model.Venda;
import com.viki.brewer.repository.Clientes;
import com.viki.brewer.storage.FotoStorage;

@Component
public class Mailer {
	
	private static final Logger LOG = LoggerFactory.getLogger(Mailer.class);
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine thymeleaf;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	@Autowired
	private Clientes clientes;
	
	@Async
	public void enviar(Venda venda, boolean template) {
		if (!template) {
			enviar(venda);
		} else {
			Context context = new Context(new Locale("pt","BR"));
			context.setVariable("venda", venda);
			context.setVariable("logo", "logo");

			Map<String, String> fotos = new HashMap<>();
			boolean adicionarMockCerveja = false;
			for (ItemVenda item : venda.getItens()) {
				Cerveja cerveja = item.getCerveja();
				if (cerveja.temFoto()) {
					String cid = "foto-" + cerveja.getCodigo();
					context.setVariable(cid, cid);
					
					fotos.put(cid, cerveja.getFoto() + "|" + cerveja.getContentType());
				} else {
					adicionarMockCerveja = true;
					context.setVariable("mockCerveja", "mockCerveja");
				}
			}
			
			String emailCliente = retornaEmailCliente(venda);
			try {
				String email = thymeleaf.process("mail/ResumoVenda", context);
				
				MimeMessage mimeMessage = mailSender.createMimeMessage();
				MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
				message.setFrom("hiperjedi@gmail.com");
				message.setTo(emailCliente);
				message.setSubject(String.format("Brewer - Venda n° %d", venda.getCodigo()));
				message.setText(email, true);
				
				message.addInline("logo", new ClassPathResource("static/images/logo-gray.png"));

				if (adicionarMockCerveja) {
					message.addInline("mockCerveja", new ClassPathResource("static/images/mock-cerveja.png"));
				}
				
				for (String cid : fotos.keySet()) {
					String[] fotoContentType = fotos.get(cid).split("\\|");
					String foto = fotoContentType[0];
					String contentType = fotoContentType[1];
					byte[] arrayFoto = fotoStorage.recuperarThumbnail(foto);
					message.addInline(cid, new ByteArrayResource(arrayFoto), contentType);
				}
				
				mailSender.send(mimeMessage);
			} catch(MessagingException e) {
				LOG.warn(String.format("Erro enviando e-mail para: ",emailCliente), e);
			}
		}
	}

	private String retornaEmailCliente(Venda venda) {
		String emailCliente = venda.getCliente().getEmail();
		if (emailCliente == null) {
			emailCliente = clientes.getOne(venda.getCliente().getCodigo()).getEmail();
		}
		return emailCliente;
	}

	@Async
	private void enviar(Venda venda) {
		String emailCliente = retornaEmailCliente(venda);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("hiperjedi@gmail.com");
		message.setTo(emailCliente);
		message.setSubject(String.format("Brewer - Venda n° %d", venda.getCodigo()));
		message.setText("Obrigado, sua venda foi processada!");
		
		mailSender.send(message);
	}
	
}
