var Brewer = Brewer || {};

Brewer.ComboEstado = (function() {
	
	function ComboEstado() {
		this.combo = $('#estado');
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	
	ComboEstado.prototype.iniciar = function() {
		this.combo.on('change', onEstadoSelecionado.bind(this));
	}
	
	function onEstadoSelecionado() {
		this.emitter.trigger('alterado', this.combo.val());
	}
	
	return ComboEstado;
	
}());

Brewer.ComboCidade = (function(comboEstado) {

	function ComboCidade(comboEstado) {
		this.comboEstado = comboEstado;
		this.combo = $('#cidade');
		this.enderecoCidade = $('#enderecoCidade');
		this.cidadeCep = $('#cidadeCep');
		this.imgLoading = $('.js-img-loading');
	}
	
	ComboCidade.prototype.iniciar = function() {
		reset.call(this);
		this.comboEstado.on('alterado', onEstadoAlterado.bind(this));
		var estadoSelecionado = this.comboEstado.combo.val();
		onEstadoAlterado.call(this, undefined, estadoSelecionado);
	}
	
	function onEstadoAlterado(evento, estado) {
		if (estado) {
			var resposta = $.ajax({
				url: this.combo.data('url'),
				method: 'GET',
				contentType: 'application/json',
				data: {'estado': estado},
				beforeSend: onIniciarPesquisar.bind(this),
				complete: onPesquisaCompleta.bind(this)
			});
			resposta.done(onPesquisaFinalizado.bind(this));
		} else {
			reset.call(this);
		}
		if (evento) {
			this.enderecoCidade.val('');
		}
	}
	
	function reset() {
		this.combo.html('<option value="">Selecione...</option>');
		this.combo.val('');
		this.combo.attr('disabled', 'disabled');
	}
	
	function onIniciarPesquisar() {
		reset.call(this);
		this.imgLoading.show();
	}
	
	function onPesquisaCompleta() {
		this.imgLoading.hide();
	}
	
	function onPesquisaFinalizado(cidades) {
		var options = [];
		cidades.forEach(function(cidade) {
			options.push('<option value="' + cidade.codigo + '">' + cidade.nome + '</option>');
		});
		this.combo.html(options.join(''));
		this.combo.removeAttr('disabled');
		
		var enderecoCidade = this.enderecoCidade.val();
		if (enderecoCidade) {
			this.combo.val(enderecoCidade);
		} else {
			var cidadeCep = this.cidadeCep.val();
			if (cidadeCep) {
				$("#cidade option:contains(" + cidadeCep +")").prop("selected", true);
			}
		}
	}
	
	return ComboCidade;
	
}());


$(function(){
	var comboEstado = new Brewer.ComboEstado();
	comboEstado.iniciar();
	
	var comboCidade = new Brewer.ComboCidade(comboEstado);
	comboCidade.iniciar();
});