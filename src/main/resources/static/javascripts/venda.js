Brewer.Venda = (function() {

	function Venda(tabelaitens) {
		this.tabelaitens = tabelaitens;
		this.valorTotalBox = $('.js-valor-total-box');
		this.valorFreteInput = $('#valorFrete');
		this.valorDescontoInput = $('#valorDesconto');
		
		this.valorFrete = this.valorFreteInput.data('valor');
		this.valorDesconto = this.valorDescontoInput.data('valor');
		this.valorTotal = this.tabelaitens.valorTotalItens();
	}
	
	Venda.prototype.iniciar = function() {
		this.tabelaitens.on('tabela-itens-atualizado', onTabelaItensAtualizado.bind(this));
		this.valorFreteInput.on('change', onValorFreteChange.bind(this));
		this.valorDescontoInput.on('change', onValorDescontoChange.bind(this));
		
		atualizarValores.call(this);
	}
	
	function onTabelaItensAtualizado(evento, valorTotal) {
		this.valorTotal = valorTotal == null ? 0 : parseFloat(valorTotal);
		atualizarValores.call(this);
	}

	function onValorFreteChange(evento) {
		var frete = $(evento.target).val();
		this.valorFrete = Brewer.retornarValor(frete);
		atualizarValores.call(this);
	}
	
	function onValorDescontoChange(evento) {
		var desconto = $(evento.target).val();
		this.valorDesconto = Brewer.retornarValor(desconto);
		atualizarValores.call(this);
	}
	
	function atualizarValores() {
		var valorTotal = parseFloat(this.valorTotal) + parseFloat(this.valorFrete) - parseFloat(this.valorDesconto);
		//console.log(valorTotal, this.valorTotal, this.valorFrete, this.valorDesconto);
		this.valorTotalBox.html(Brewer.formatarMoeda(valorTotal));
		
		$('.js-valor-total-box-container').toggleClass('negativo', valorTotal < 0);
	}

	return Venda;
	
}());

$(function() {
	var autocomplete = new Brewer.Autocomplete();
	autocomplete.iniciar();
	
	var tabelaitens = new Brewer.TabelaItens(autocomplete);
	tabelaitens.iniciar();

	var venda = new Brewer.Venda(tabelaitens);
	venda.iniciar();
});
