Brewer = Brewer || {};

Brewer.Autocomplete = (function() {
	
	function Autocomplete() {
		this.skuNomeCervejaInput = $('.js-sku-nome-cerveja');
		this.urlPesquisa = this.skuNomeCervejaInput.data('url');
		
		var htmlTemplateAutocomplete = $('#template-autocomplete-cerveja').html();
		this.template = Handlebars.compile(htmlTemplateAutocomplete);
		
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	
	Autocomplete.prototype.iniciar = function() {
		var url = this.urlPesquisa + '?skuOuNome=';
		var options = {
			url: function(skuOuNome) {
				return url + skuOuNome;
			},
			getValue: 'nome',
			requestDelay: 300,
			minCharNumber: 3,
			ajaxSettings: {
				contentType: 'application/json'
			},			
			template: {
				type: 'custom',
				method: onTemplate.bind(this)
			},
			list: {
				onChooseEvent: onItemSelecionado.bind(this),
				showAnimation: {
					type: "slide", //normal|slide|fade
					time: 200,
					callback: function() {}
				}
			}
		}
		this.skuNomeCervejaInput.easyAutocomplete(options);

		//resolvendo problema na hora de renderizar: aula 22.7
		$('div.easy-autocomplete').attr('style', 'width: auto;');
	}

	function novaPesquisa() {
		this.skuNomeCervejaInput.val('');
		this.skuNomeCervejaInput.focus();
	}
	
	function onItemSelecionado(){
		this.emitter.trigger('item-selecionado', this.skuNomeCervejaInput.getSelectedItemData());
	}
	
	function onTemplate(nome, cerveja){
		cerveja.valorFormatado = Brewer.formatarMoeda(cerveja.valor);
		return this.template(cerveja);
	}
	
	return Autocomplete;
	
}());