Brewer.TabelaItens = (function() {
	
	function TabelaItens(autocomplete) {
		this.autocomplete = autocomplete;
		this.tabelaCervejasContainer = $('.js-tabela-cervejas-container');
		this.uuid = $('#uuid').val();
		
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	
	TabelaItens.prototype.iniciar = function () {
		this.autocomplete.on('item-selecionado', onItemSelecionado.bind(this));
		
		configurarQuantidadeItemInput.call(this);
		configurarExclusaoTabelaItem.call(this);
	}
	
	TabelaItens.prototype.valorTotalItens = function() {
		return this.tabelaCervejasContainer.data('valor');
	}
	
	function onItemSelecionado(evento, item) {
		var resposta = $.ajax({
			url: 'item',
			method: 'post',
			data: {
				codigoCerveja: item.codigo,
				uuid: this.uuid
			}
		});
		
		resposta.done(
			onRetornoHtmlTabelaItensVenda.bind(this)
		);		
		
		novaPesquisa.call(this);
	}

	function novaPesquisa() {
		this.autocomplete.skuNomeCervejaInput.val('');
		this.autocomplete.skuNomeCervejaInput.focus();
	}
	
	function onRetornoHtmlTabelaItensVenda(html) {
		this.tabelaCervejasContainer.html(html);
		
		configurarQuantidadeItemInput.call(this);
		var tabelaItem = configurarExclusaoTabelaItem.call(this);
		this.emitter.trigger('tabela-itens-atualizado', tabelaItem.data('valor-total'));
	}
	
	function onAlterarQuantidadeItem(evento) {
		var input = $(evento.target);
		var codigoCerveja = input.data('codigo-cerveja');

		var quantidade = input.val();
		if (quantidade <= 0) {
			input.val(1);
			quantidade = 1;
		}

		var resposta = $.ajax({
			url: 'item/' + codigoCerveja,
			method: 'put',
			data: {
				quantidade: quantidade,
				uuid: this.uuid
			}
		});
		
		resposta.done(
			onRetornoHtmlTabelaItensVenda.bind(this)
		);		
	}
	
	function onDoubleClick(evento) {
		$(this).toggleClass('solicitando-exclusao');
	}

	function onRemoverItemClick(evento) {
		var codigoCerveja = $(evento.target).data('codigo-cerveja');

		var resposta = $.ajax({
			url: 'item/' + this.uuid + '/' + codigoCerveja,
			method: 'delete'
		});
		
		resposta.done(
			onRetornoHtmlTabelaItensVenda.bind(this)
		);		
	}

	function configurarQuantidadeItemInput(){
		var quantidadeItemInput = $('.js-tabela-cerveja-quantidade');
		quantidadeItemInput.on('change', onAlterarQuantidadeItem.bind(this));
		quantidadeItemInput.maskMoney({ precision: 0, thousands: '' });
	}
	
	function configurarExclusaoTabelaItem(){
		var tabelaItem = $('.js-tabela-item');
		tabelaItem.on('doubletap', onDoubleClick);
		$('.js-remover-item-btn').on('click', onRemoverItemClick.bind(this));
		return tabelaItem;
	}
	
	return TabelaItens;
	
}());
