var Brewer = Brewer || {};

Brewer.MascaraCep = (function() {
	
	function MascaraCep() {
		this.inputCep = $('.js-cep');
	}
	
	MascaraCep.prototype.iniciar = function() {
		var options =  {
				clearIfNotMatch: true,
				onComplete: function(cep) {
					limpaFormulario();
					$('.js-cep').attr('disabled','disabled');
					
					consultaCepPostmon.call(this, cep);
				}
			}
		var mascara = this.inputCep.data('mascara');
		this.inputCep.mask(mascara, options);
		var inputCepInvalido = $('#inputCepInvalido').val();
		if (inputCepInvalido) {
	    	$('.js-div-cep').addClass("has-error");
		}
	}

	function limpaFormulario() {
        $("#logradouro").val('');
        $("#bairro").val('');
        $("#complemento").val('');
        $("#cidade").val('');
        $("#estado").val('');
    	$('#inputCepInvalido').val('');
    	$('#enderecoCidade').val('');
		$('#cidadeCep').val('');
    	$('.js-div-cep').removeClass("has-error");
	}
	
	function consultaCep(cep) {
        //Consulta o webservice viacep.com.br/
        $.getJSON("https://viacep.com.br/ws/"+ cep +"/json/?callback=?", function(dados) {
            $('.js-cep').removeAttr('disabled');
            if (!("erro" in dados)) {
                console.log('endereco', dados);
                $("#logradouro").val(dados.logradouro);
                $("#bairro").val(dados.bairro);
                $("#complemento").val(dados.complemento);
                $("#cidade").val(dados.localidade);
                $("#estado").val(dados.uf);
            } else {
            	$('.js-div-cep').addClass("has-error");
                console.log('CEP não encontrado.');
            }
        });		
	}
	
	function consultaCepPostmon(cep) {
		$.getJSON("http://api.postmon.com.br/v1/cep/" + cep)
			.success(onAjaxSuccess)
			.error(onAjaxError);
	}

	function onAjaxSuccess(data) {
		if(data) {
            console.log('endereco', data);
            $("#logradouro").val(data.logradouro);
            $("#bairro").val(data.bairro);
            $("#complemento").val(data.complemento);
            $('#cidadeCep').val(data.cidade);
            $("#estado option:contains(" + data.estado +")").prop("selected", true).change();
            
            $('.js-cep').removeAttr('disabled');
            $("#numero").focus();
		} else {
			onAjaxError.call(this, data);
		} 
	}
	
	function onAjaxError(data) {
        $('.js-cep').removeAttr('disabled');
		$('#inputCepInvalido').val(data.statusText);
    	$('.js-div-cep').addClass("has-error");
        $("#estado").change();
		console.log('Erro', data.status, data.statusText);
	}	
	
	return MascaraCep;
	
}());

$(function() {
	var mascaraCep = new Brewer.MascaraCep();
	mascaraCep.iniciar();
});