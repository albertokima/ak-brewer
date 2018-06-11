Brewer = Brewer || {};

Brewer.DialogoExclusao = (function() {
	
	function DialogoExclusao() {
		this.excluirBtn = $('.js-exclusao-btn');
	}
	
	DialogoExclusao.prototype.iniciar = function() {
		this.excluirBtn.on('click', onExclusaoClick.bind(this));

		if (window.location.search.indexOf('excluido') > -1) {
			swal('Pronto!', 'Excluído com sucesso!', 'success');
		}
	}
	
	function onExclusaoClick(e) {
		e.preventDefault();
		var botaoClicado = $(e.currentTarget);
		var url = botaoClicado.data('url');
		var objeto = botaoClicado.data('objeto');
		
		swal({
			title: 'Tem certeza?',
			text: 'Excluir "' + objeto + '"? Você não poderá recuperar depois.',
			showCancelButton: true,
			confirmButtonColor: '#DD6B55',
			confirmButtonText: 'Sim, excluir agora!',
			closeOnConfirm: false
		}, onExcluirConfirmado.bind(this, url));
	}
	
	function onExcluirConfirmado(url) {
		$.ajax({
			url: url,
			method: 'delete',
			success: onExclusaoSucesso.bind(this),
			error: onExclusaoErro.bind(this)
		});
	}

	function onExclusaoSucesso() {
		var urlAtual = window.location.href;
		var separador = urlAtual.indexOf('?') > -1 ? '&' : '?';
		var novaUrl = urlAtual.indexOf('excluido') > -1 ? urlAtual : urlAtual + separador + 'excluido';
		
		window.location = novaUrl;
	}
	
	function onExclusaoErro(e) {
		console.log('erro', e.responseText);
		swal('Oops!', e.responseText, 'error');
	}
	
	return DialogoExclusao;
	
}());

$(function() {
	var dialogo = new Brewer.DialogoExclusao();
	dialogo.iniciar();
});