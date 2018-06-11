Brewer = Brewer || {};

Brewer.GraficoVendaPorMes = (function() {
	
	function GraficoVendaPorMes() {
		this.ctx = $('#chartVendasMes');
	}
	
	GraficoVendaPorMes.prototype.iniciar = function() {
		$.ajax({
			url: 'vendas/dadosVendaPorMes',
			method: 'get',
			success: onRetornoDadosServidor.bind(this),
			error: onErroRetorno.bind(this)
		});
	}
	
	function onRetornoDadosServidor(dados) {
		var meses = [];
		var emitidas = [];
		var canceladas = [];
		dados.forEach(function(obj){
			meses.unshift(obj.mes);
			emitidas.unshift(obj.totalEmitida);
			canceladas.unshift(obj.totalCancelada);
		});
		
		var myChart = new Chart(this.ctx, {
			type: 'line',
			data: {
				labels: meses,
				datasets: [
					{
						label: 'Vendas',
						backgroundColor: "rgba(26,179,148,0.5)",
						pointBorderColor: "rgba(26,179,148,1)",
						pointBackgroundColor: "#fff",
						fill: 1,
						data: emitidas
					},
					{
						label: 'Canceladas',
						backgroundColor: "rgba(179,26,148,0.5)",
						pointBorderColor: "rgba(179,26,148,1)",
						pointBackgroundColor: "#fff",
						data: canceladas
					}
				]
			}
		});
		$('.js-vendasmes-vazio').addClass('hidden');
	}
	
	function onErroRetorno() {
		$('.js-vendasmes-vazio').removeClass('hidden');
	}
	
	return GraficoVendaPorMes;
	
}());

Brewer.GraficoVendaPorOrigem = (function() {
	
	function GraficoVendaPorOrigem() {
		this.ctx = $('#chartVendasOrigem');
	}
	
	GraficoVendaPorOrigem.prototype.iniciar = function() {
		$.ajax({
			url: 'vendas/dadosVendaPorOrigem',
			method: 'get',
			success: onRetornoDadosServidor.bind(this),
			error: onErroRetorno.bind(this)
		});
	}
	
	function onRetornoDadosServidor(dados) {
		var meses = [];
		var nacional = [];
		var internacional = [];
		dados.forEach(function(obj){
			meses.unshift(obj.mes);
			nacional.unshift(obj.totalNacional);
			internacional.unshift(obj.totalInternacional);
		});
		
		var myChart = new Chart(this.ctx, {
			type: 'bar',
			data: {
				labels: meses,
				datasets: [
					{
						label: 'Nacional',
						backgroundColor: "rgba(26,179,148,0.5)",
						data: nacional
					},
					{
						label: 'Internacional',
						backgroundColor: "rgba(179,26,148,0.5)",
						data: internacional
					}
				]
			}
		});
		$('.js-vendasorigem-vazio').addClass('hidden');
	}
	
	function onErroRetorno() {
		$('.js-vendasorigem-vazio').removeClass('hidden');
	}
	
	return GraficoVendaPorOrigem;
	
}());

$(function() {
	var graficoVendaPorMes = new Brewer.GraficoVendaPorMes();
	graficoVendaPorMes.iniciar();

	var graficoVendaPorOrigem = new Brewer.GraficoVendaPorOrigem();
	graficoVendaPorOrigem.iniciar();
});
