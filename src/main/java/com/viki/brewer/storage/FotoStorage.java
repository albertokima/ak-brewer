package com.viki.brewer.storage;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface FotoStorage {
	
	public static final String THUMBNAIL_PREFIX = "thumbnail.";
	
	public String salvarTemporariamente(MultipartFile[] files);

	public byte[] recuperarFotoTemporaria(String nome);
	
	public void salvarFoto(String nome);

	public byte[] recuperarFoto(String nome);

	public byte[] recuperarThumbnail(String nome);

	public boolean apagarFotoTemporaria(String nome);

	public void apagarFoto(String nome);

	public String getUrl(String foto);
	
	public String getUrlTemporaria(String foto);

	default String renomearArquivo(String nomeOriginal) {
		return UUID.randomUUID().toString() + "_" + nomeOriginal;
	}
	
}
