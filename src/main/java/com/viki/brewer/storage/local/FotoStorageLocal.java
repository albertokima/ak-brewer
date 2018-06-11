package com.viki.brewer.storage.local;

import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.viki.brewer.storage.FotoStorage;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

@Component
@Profile("!prod")
public class FotoStorageLocal implements FotoStorage{

	private static final Logger LOG = LoggerFactory.getLogger(FotoStorageLocal.class);
	
	@Value("${brewer.foto-storage-local.local}")
	private Path local;

	private Path localTemporario;
	
	@Value("${brewer.foto-storage-local.url-base}")
	private String urlBase;
	
	@Override
	public String salvarTemporariamente(MultipartFile[] files) {
		String novoNome = null;
		if (files != null && files.length > 0) {
			MultipartFile arquivo = files[0];
			
			File nomeArquivo = new File(arquivo.getOriginalFilename());
			novoNome = renomearArquivo(nomeArquivo.getName());

			try {
				arquivo.transferTo(new File(this.localTemporario.toAbsolutePath().toString() + getDefault().getSeparator() + novoNome));
			} catch (IOException e) {
				throw new RuntimeException("Erro salvando a foto temporária", e);
			}
		}
		
		return novoNome;
	}
	
	@Override
	public byte[] recuperarFotoTemporaria(String nome) {
		try {
			return Files.readAllBytes(this.localTemporario.resolve(nome));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo a foto temporária", e);
		}
	}
	
	@Override
	public byte[] recuperarFoto(String nome) {
		try {
			return Files.readAllBytes(this.local.resolve(nome));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo a foto", e);
		}
	}
	
	@Override
	public byte[] recuperarThumbnail(String nome) {
		return recuperarFoto(THUMBNAIL_PREFIX + nome);
	}
	
	@Override
	public boolean apagarFotoTemporaria(String nome) {
		try {
			return Files.deleteIfExists(this.localTemporario.resolve(nome));
		} catch (Exception e) {
			throw new RuntimeException("Erro ao apagar foto temporária", e);
		}
	}
	
	@Override
	public void apagarFoto(String nome) {
		try {
			Files.deleteIfExists(this.local.resolve(nome));
			Files.deleteIfExists(this.local.resolve(THUMBNAIL_PREFIX + nome));
		} catch (Exception e) {
			LOG.warn(String.format("Erro ao apagar foto: '%s'. Mensagem: '%s'", nome, e.getMessage()));
		}
	}
	
	@Override
	public void salvarFoto(String nome) {
		try {
			Files.move(this.localTemporario.resolve(nome), this.local.resolve(nome), REPLACE_EXISTING);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao salvar foto", e);
		}
		
		int altura = 68;
		String arquivo = this.local.resolve(nome).toString();
		int largura = retornaLarguraThumbnail(arquivo, altura);
		
		try {
			Thumbnails.of(this.local.resolve(nome).toString()).size(largura, altura).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
		} catch (IOException e) {
			throw new RuntimeException("Erro ao gerar thumbnail da foto", e);
		}
	}
	
	@Override
	public String getUrl(String foto) {
		return urlBase + foto;
	}
	
	@Override
	public String getUrlTemporaria(String foto) {
		return urlBase + "temp/" + foto;
	}

	private int retornaLarguraThumbnail(String arquivo, int height){
        ImageIcon imagem = new ImageIcon(arquivo);
        int width = new ImageIcon(
        		imagem.getImage().getScaledInstance(-1, height, Image.SCALE_FAST)
        ).getIconHeight();

        return width;
    }
	
	@PostConstruct
	private void criarPastas() {
		try {
			Files.createDirectories(this.local);
			this.localTemporario = getDefault().getPath(this.local.toString(), "temp");
			Files.createDirectories(this.localTemporario);
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Pastas de fotos criadas.");
				LOG.debug("Pasta default: " + this.local.toAbsolutePath());
				LOG.debug("Pasta temporária: " + this.localTemporario.toAbsolutePath());
			}
		} catch (IOException e) {
			throw new RuntimeException("Erro criando pasta para salvar foto", e);
		}
		
	}
	
}
