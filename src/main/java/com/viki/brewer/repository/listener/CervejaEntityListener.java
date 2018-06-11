package com.viki.brewer.repository.listener;

import javax.persistence.PostLoad;

import com.viki.brewer.BrewerApplication;
import com.viki.brewer.model.Cerveja;
import com.viki.brewer.storage.FotoStorage;

public class CervejaEntityListener {

	@PostLoad
	public void postLoad(final Cerveja cerveja) {
		FotoStorage fotoStorage = BrewerApplication.getBean(FotoStorage.class);
		
		cerveja.setUrlFoto(fotoStorage.getUrl(cerveja.getFotoOuMock()));
		cerveja.setUrlThumbnailFoto(fotoStorage.getUrl(FotoStorage.THUMBNAIL_PREFIX + cerveja.getFotoOuMock()));
	}
	
}