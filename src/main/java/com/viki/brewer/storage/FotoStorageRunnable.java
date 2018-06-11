package com.viki.brewer.storage;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.viki.brewer.dto.FotoDTO;

public class FotoStorageRunnable implements Runnable{

	private MultipartFile[] files;
	private DeferredResult<FotoDTO> resultado;
	private FotoStorage fotoStorage;
	
	public FotoStorageRunnable(MultipartFile[] files, FotoStorage fotoStorage, DeferredResult<FotoDTO> resultado) {
		this.files = files;
		this.fotoStorage = fotoStorage;
		this.resultado = resultado;
	}

	@Override
	public void run() {
		String nomeFoto = this.fotoStorage.salvarTemporariamente(files);
		String contentType = files[0].getContentType();
		resultado.setResult(new FotoDTO(nomeFoto, contentType, fotoStorage.getUrlTemporaria(nomeFoto)));
	}

}
