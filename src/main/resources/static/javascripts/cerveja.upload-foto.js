var Brewer = Brewer || {};

Brewer.UploadFoto = (function() {
	
	function UploadFoto() {
		this.uploadSelect = $('#upload-select');
		this.btnSalvarCerveja = $('.js-btn-salvar-cerveja');

		this.inputNomeFoto = $('input[name=foto]');
		this.inputContentType = $('input[name=contentType]');
		this.inputNovaFoto = $('input[name=novaFoto]');
		this.inputUrlFoto = $('input[name=urlFoto]');
		
		this.htmlFotoCervejaTemplate = $('#foto-cerveja').html();
		this.template = Handlebars.compile(this.htmlFotoCervejaTemplate);
		
		this.containerFotoCerveja = $('.js-container-foto-cerveja');
		this.urlFotos = this.containerFotoCerveja.data('url-fotos');
		
		this.uploadDrop = $('#upload-drop');
		this.imgLoading = $('.js-img-loading');
		this.linkSelecaoFoto = $('.js-link-selecao-foto');
	}
	
	UploadFoto.prototype.iniciar = function () {
		var settings = {
			type: 'json',
			filelimit: 1,
			allow: '*.(jpg|jpeg|png)',
			action: this.urlFotos,
			complete: onUploadComplete.bind(this),
			loadstart: onUploadStart.bind(this),
			beforeSend: adicionarCsrfToken.bind(this),
			error: onUploadError.bind(this)
		}
		
		UIkit.uploadSelect(this.uploadSelect, settings);
		UIkit.uploadDrop(this.uploadDrop, settings);
		
		if (this.inputNomeFoto.val()) {
			renderizaFoto.call(this,
				{ foto: this.inputNomeFoto.val(), contentType: this.inputContentType.val(), url: this.inputUrlFoto.val() }
			);
		}
	}

	function onBeforeAll(f) {
		if (!this.imgLoading.hasClass('hidden')) {
			console.log('Aborted!!!!', f);
			return;
		}
	}
	
	function onUploadError(e) {
		this.imgLoading.addClass('hidden');
		this.btnSalvarCerveja.removeAttr('disabled');
		this.linkSelecaoFoto.removeClass('disabled');
		swal('Oops!', 'Não foi possível enviar a foto para o Servidor.', 'error');
	}
	
	function onUploadStart(e) {
		this.imgLoading.removeClass('hidden');
		this.btnSalvarCerveja.attr('disabled','disabled');
		this.linkSelecaoFoto.addClass('disabled');
	}
	
	function onUploadComplete(resposta) {
		this.imgLoading.addClass('hidden');
		this.btnSalvarCerveja.removeAttr('disabled');
		this.linkSelecaoFoto.removeClass('disabled');
		if (resposta) {
			this.inputNovaFoto.val('true');
			this.inputUrlFoto.val(resposta.url);
			renderizaFoto.call(this, resposta);
		} else {
			swal('Oops!', 'Não foi possível enviar a foto para o Servidor.', 'error');
		}
	}
	
	function renderizaFoto(resposta) {
		this.nomeFoto = resposta.foto;
		this.inputNomeFoto.val(resposta.foto);
		this.inputContentType.val(resposta.contentType);
		
		this.uploadDrop.addClass('hidden');
		var htmlFotoCerveja = this.template({url: resposta.url});
		this.containerFotoCerveja.append(htmlFotoCerveja);
		
		$('.js-remove-foto').on('click', onRemoverFoto.bind(this));
	}
	
	function onRemoverFoto() {
		$('.js-foto-cerveja').remove();
		this.uploadSelect.val('');
		this.uploadDrop.removeClass('hidden');
		this.inputNomeFoto.val('');
		this.inputContentType.val('');
		this.inputUrlFoto.val('');

		if (this.inputNovaFoto.val() == 'true'){
			$.ajax({
				url: this.urlFotos + '/temp/' + this.nomeFoto,
				method: 'DELETE'
			});
		}

		this.inputNovaFoto.val('false');
	}

	function adicionarCsrfToken(xhr) {
		if (!this.imgLoading.hasClass('hidden')) {
			console.log('Aborted!!!!', xhr);
	        xhr.onreadystatechange = null;
	        xhr.abort();
	        return false;
	    }
	
		var token = $('input[name=_csrf]').val();
		var header = $('input[name=_csrf_header]').val();
		xhr.setRequestHeader(header, token);
	}
	
	return UploadFoto;
	
})();

$(function() {
	var uploadFoto = new Brewer.UploadFoto();
	uploadFoto.iniciar();
});