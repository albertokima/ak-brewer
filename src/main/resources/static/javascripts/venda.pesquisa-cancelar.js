Brewer = Brewer || {};

Brewer.PesquisaCancelar = (function() {
	
	function PesquisaCancelar() {
		this.btnCancelar = $('.js-pesquisa-cancelar-btn');
	}
	
	PesquisaCancelar.prototype.iniciar = function() {
		this.btnCancelar.on('click', onBotaoCancelarVendaClick.bind(this));
	}
	
	function onBotaoCancelarVendaClick(evento) {
		evento.preventDefault();
		var botaoClicado = $(evento.currentTarget);
		var url = botaoClicado.data('url') + '/' + botaoClicado.data('codigo');
		var acaoInput = $('<input type="hidden" name="cancelar">');

		$.ajax({
			url: url,
			method: 'put',
			data: acaoInput,
			error: onCancelarVendaErro.bind(this),
			success: onCancelarVendaSucesso.bind(this)
		});
	}
	
	function onCancelarVendaSucesso(e) {
		window.location.reload();
	}
	
	function onCancelarVendaErro(e) {
		swal('Oops!', e.responseText, 'error');
	}
	
	return PesquisaCancelar;
	
}());

$(function() {
	var pesquisaCancelar = new Brewer.PesquisaCancelar();
	pesquisaCancelar.iniciar();
});
