var Brewer = Brewer || {};

Brewer.MultiSelecao = (function() {
	
	function MultiSelecao() {
		this.btnStatus = $('.js-status-btn');
		this.selecao = $('.js-selecao');
		this.selecaoTodos = $('.js-selecao-todos');
	}
	
	MultiSelecao.prototype.iniciar = function() {
		this.btnStatus.on('click', onClickBtnStatus.bind(this));
		this.selecao.on('click', onClickSelecao.bind(this));
		this.selecaoTodos.on('click', onClickSelecaoTodos.bind(this));
	}
	
	function onClickSelecao() {
		var selecionados = this.selecao.filter(':checked');
		this.selecaoTodos.prop('checked', this.selecao.length == selecionados.length);
		inativarBotoes.call(this, selecionados.length == 0);
	}

	function onClickSelecaoTodos() {
		var checked = this.selecaoTodos.is(':checked');
		this.selecao.prop('checked', checked);
		inativarBotoes.call(this, !checked);
	}
	
	function inativarBotoes(opcao){
		this.btnStatus.prop('disabled', opcao);
	}
	
	function onClickBtnStatus(event) {
		var botao = $(event.currentTarget);
		var status = botao.data('status');
		var url = botao.data('url');
		
		var selecionados = this.selecao.filter(':checked');
		var codigos = $.map(selecionados, function(c) {
			return $(c).data('codigo');
		});
		
		if (codigos.length > 0) {
			$.ajax({
				url: url,
				method: 'put',
				data: {
					codigos: codigos, 
					status: status
				},
				success: function() {
					window.location.reload();
				}
			});
		}
	}
	
	return MultiSelecao;
	
}());

$(function(){
	var multiselecao = new Brewer.MultiSelecao();
	multiselecao.iniciar();
});