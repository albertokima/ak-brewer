var Brewer = Brewer || {};

Brewer.PesquisaRapidaCliente = (function() {
	
	function PesquisaRapidaCliente() {
		this.nomeCliente = $('#nomeClienteModal');
		this.modalPesquisa = $('#pesquisaRapidaClientes');
		this.divModalTabelaPesquisa = $('.js-table-clientes-content');
		this.btnPesquisa = $('.js-btn-pesquisa-clientes-modal');
		this.form = this.modalPesquisa.find('form');
		this.urlPesquisa = this.form.attr('action');
		this.mensagemErro = $('.js-alert-modal');
		
		this.htmlPesquisaTemplate = $('#tabela-pesquisa-rapida-clientes').html();
		this.template = Handlebars.compile(this.htmlPesquisaTemplate);
	}
	
	PesquisaRapidaCliente.prototype.iniciar = function() {
		this.form.on('submit', onSubmitPesquisarCliente.bind(this));
		this.modalPesquisa.on('shown.bs.modal', onModalShow.bind(this));
		this.modalPesquisa.on('hide.bs.modal', onModalClose.bind(this))
	}

	function onModalShow() {
		this.nomeCliente.focus();
	}
	
	function onModalClose() {
		this.nomeCliente.val('');
		this.mensagemErro.addClass('hidden');
		this.divModalTabelaPesquisa.html('');
	}	
	
	function onSubmitPesquisarCliente(event) {
		event.preventDefault();
		var nome = this.nomeCliente.val();
		$.ajax({
			url: this.urlPesquisa,
			method: 'get',
			contentType: 'application/json',
			data: {
				nome: nome
			},
			success: onPesquisaComplete.bind(this),
			error: onPesquisaError.bind(this)
		});
	}
	
	function onPesquisaComplete(resultado) {
		var html = this.template(resultado);
		this.mensagemErro.addClass('hidden');
		this.divModalTabelaPesquisa.html(html);
		
		var tabelaPesquisaRapidaCliente = new Brewer.TabelaPesquisaRapidaCliente(this.modalPesquisa);
		tabelaPesquisaRapidaCliente.iniciar();
	}
	
	function onPesquisaError() {
		this.mensagemErro.removeClass('hidden');
	}
	
	return PesquisaRapidaCliente;
	
}());

Brewer.TabelaPesquisaRapidaCliente = (function() {
	
	function TabelaPesquisaRapidaCliente(modal) {
		this.modalCliente = modal;
		this.cliente = $('.js-cliente-tabela-rapida');
	}
	
	TabelaPesquisaRapidaCliente.prototype.iniciar = function() {
		this.cliente.on('click', onClickClienteSelecionado.bind(this));
	}
	
	function onClickClienteSelecionado(evento) {
		this.modalCliente.modal('hide');
		
		var clienteSelecionado = $(evento.currentTarget);
		$('#codigoCliente').val(clienteSelecionado.data('codigo'));
		$('#nomeCliente').val(clienteSelecionado.data('nome'));
	}
	
	return TabelaPesquisaRapidaCliente;
	
}());

$(function() {
	var pesquisaRapidaCliente = new Brewer.PesquisaRapidaCliente();
	pesquisaRapidaCliente.iniciar();
});
