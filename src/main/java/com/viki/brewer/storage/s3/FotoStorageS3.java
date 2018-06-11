package com.viki.brewer.storage.s3;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.util.IOUtils;
import com.viki.brewer.storage.FotoStorage;

import net.coobird.thumbnailator.Thumbnails;

@Component
@Profile("prod")
public class FotoStorageS3 implements FotoStorage {

	private static final Logger LOG = LoggerFactory.getLogger(FotoStorageS3.class);
	
	private static final String BUCKET = "ak-brewer-fotos";
	
	@Autowired
	private AmazonS3 amazonS3;	
	
	@Value("${brewer.foto-storage-s3.url-base}")
	private String urlBase;
	
	@Override
	public String salvarTemporariamente(MultipartFile[] files) {
		String novoNome = null;
		if (files != null && files.length > 0) {
			MultipartFile arquivo = files[0];
			novoNome = renomearArquivo(arquivo.getOriginalFilename());
			try {
				AccessControlList acl = new AccessControlList();
				acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

				enviarFoto(novoNome, arquivo, acl);
				enviarThumbnail(novoNome, arquivo, acl);

				if (LOG.isDebugEnabled()) {
					LOG.debug("Foto {} enviada com sucesso para o S3.", arquivo.getOriginalFilename());
				}
			} catch (IOException e) {
				throw new RuntimeException("Problemas ao tentar enviar a foto para o S3.", e);
			}
		}
		return novoNome;
	}

	@Override
	public void salvarFoto(String nome) {
		SetObjectTaggingRequest fotoRequest = new SetObjectTaggingRequest(
				BUCKET,	nome, new ObjectTagging(Collections.emptyList()));
		amazonS3.setObjectTagging(fotoRequest);		

		SetObjectTaggingRequest thumbsRequest = new SetObjectTaggingRequest(
				BUCKET,	THUMBNAIL_PREFIX + nome, new ObjectTagging(Collections.emptyList()));
		amazonS3.setObjectTagging(thumbsRequest);		
	}
	
	@Override
	public byte[] recuperarFotoTemporaria(String nome) {
		return recuperarFoto(nome);
	}

	@Override
	public byte[] recuperarFoto(String nome) {
		InputStream is = amazonS3.getObject(BUCKET, nome).getObjectContent();
		try {
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			LOG.error("Não conseguiu recuperar foto do S3", e);
		}
		return null;
	}

	@Override
	public byte[] recuperarThumbnail(String nome) {
		return recuperarFoto(THUMBNAIL_PREFIX + nome);
	}

	@Override
	public boolean apagarFotoTemporaria(String nome) {
		//apagarFoto(nome);
		return true;
	}

	@Override
	public void apagarFoto(String nome) {
		amazonS3.deleteObjects(new DeleteObjectsRequest(BUCKET).withKeys(nome, THUMBNAIL_PREFIX + nome));
	}

	@Override
	public String getUrl(String foto) {
		return urlBase + BUCKET + "/" + foto;
	}

	@Override
	public String getUrlTemporaria(String foto) {
		return getUrl(foto);
	}

	private ObjectMetadata enviarFoto(String novoNome, MultipartFile arquivo, AccessControlList acl)
			throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(arquivo.getContentType());
		metadata.setContentLength(arquivo.getSize());
		PutObjectRequest putObjectRequest = new PutObjectRequest(
				BUCKET,	novoNome, arquivo.getInputStream(), metadata)
					.withAccessControlList(acl);
		putObjectRequest.setTagging(new ObjectTagging(
				Arrays.asList(new Tag("expirar", "true"))));
		
		amazonS3.putObject(putObjectRequest);
		return metadata;
	}
	
	private void enviarThumbnail(String novoNome, MultipartFile arquivo, AccessControlList acl)	throws IOException {
		int altura = 68;
		int largura =retornaLarguraThumbnail(arquivo.getBytes(), altura);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Thumbnails.of(arquivo.getInputStream()).size(largura, altura).toOutputStream(os);

		byte[] array = os.toByteArray();
		InputStream is = new ByteArrayInputStream(array);
		ObjectMetadata thumbMetadata = new ObjectMetadata();
		thumbMetadata.setContentType(arquivo.getContentType());
		thumbMetadata.setContentLength(array.length);

		PutObjectRequest putObjectRequest = new PutObjectRequest(
				BUCKET,	THUMBNAIL_PREFIX + novoNome, is, thumbMetadata)
					.withAccessControlList(acl);
		putObjectRequest.setTagging(new ObjectTagging(
				Arrays.asList(new Tag("expirar", "true"))));
		amazonS3.putObject(putObjectRequest);
	}
	
	private int retornaLarguraThumbnail(byte[] arquivo, int height){
        ImageIcon imagem = new ImageIcon(arquivo);
        int width = new ImageIcon(
        		imagem.getImage().getScaledInstance(-1, height, Image.SCALE_FAST)
        ).getIconHeight();

        return width;
    }
	
}
