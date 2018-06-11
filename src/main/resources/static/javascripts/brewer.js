var Brewer = Brewer || {};

Brewer.MaskMoney = (function() {
	
	function MaskMoney() {
		this.decimal = $('.js-decimal');
		this.plain = $('.js-plain');
	}
	
	MaskMoney.prototype.enable = function() {
		this.decimal.maskMoney({ decimal: ',', thousands: '.', allowZero: true });
		this.plain.maskMoney({ precision: 0, decimal: '', thousands: '.', allowZero: true });
	}
	
	return MaskMoney;
	
}());

Brewer.MaskNumber = (function() {
	
	function MaskNumber() {
		this.decimal = $('.js-decimal');
		this.plain = $('.js-plain');
	}
	
	MaskNumber.prototype.enable = function() {
		this.decimal.maskNumber({ decimal: ',', thousands: '.' });
		this.plain.maskNumber({ integer: true, thousands: '.' });
	}
	
	return MaskNumber;
	
}());

Brewer.MaskPhoneNumber = (function() {
	
	function MaskPhoneNumber() {
		this.inputPhoneNumber = $('.js-phone-number');
	}

	MaskPhoneNumber.prototype.enable = function() {
		var maskBehavior = function (val) {
		  return val.replace(/\D/g, '').length === 11 ? '(00) 00000-0000' : '(00) 0000-00009';
		},
		options = {
		  clearIfNotMatch: true,
		  onKeyPress: function(val, e, field, options) {
		      field.mask(maskBehavior.apply({}, arguments), options);
		    }
		};

		this.inputPhoneNumber.mask(maskBehavior, options);		
	}
	
	return MaskPhoneNumber;
	
}());

Brewer.MaskDate = (function() {
	
	function MaskDate() {
		this.decimal = $('.js-date');
	}
	
	MaskDate.prototype.enable = function() {
		this.decimal.mask("00/00/0000");
		this.decimal.datepicker({
			format: 'dd/mm/yyyy',
			language: 'pt-BR',
			orientation: 'bottom',
			clearBtn: true,
			autoclose: true
		});
	}
	
	return MaskDate;
	
}());

Brewer.ClockPicker = (function() {
	
	function ClockPicker() {
		this.time = $('.js-time');
	}
	
	ClockPicker.prototype.enable = function() {
		this.time.clockpicker({
			placement: 'bottom',
			align: 'left',
			autoclose: true,
			'default': 'now'
		});
	}
	
	return ClockPicker;
	
}());

Brewer.MaskCpfCnpjGeral = (function() {

	function MaskCpfCnpjGeral() {
		this.inputCpfCnpj = $('.js-cpfCnpj');
	}
	
	MaskCpfCnpjGeral.prototype.enable = function () {
		var maskBehavior = function (val) {
			return val.replace(/\D/g, '').length >= 12 ? '00.000.000/0000-00' : '000.000.000-00999';
		};
		var options = {
		  onKeyPress: function(val, e, field, options) {
		      field.mask(maskBehavior.apply({}, arguments), options);
		    }
		};
		this.inputCpfCnpj.mask(maskBehavior, options);
	}
	
	return MaskCpfCnpjGeral;
}());

Brewer.Security = (function() {
	
	function Security() {
		this.token = $('input[name=_csrf]').val();
		this.header = $('input[name=_csrf_header]').val();
	}
	
	Security.prototype.enable = function() {
		$(document).ajaxSend(function(event, jqxhr, settings) {
			if (settings.type != 'GET') {
				jqxhr.setRequestHeader(this.header, this.token);
			}
		}.bind(this));
	}
	
	return Security;
	
}());

numeral.locale('pt-br');

Brewer.formatarMoeda = function(valor) {
	return numeral(valor).format('0,0.00');
}

Brewer.retornarValor = function(valorFormatado) {
	return numeral(valorFormatado).value();
}

$(function() {
	//$('.js-sidebar, .js-content').toggleClass('is-toggled');
	
	var maskMoney = new Brewer.MaskMoney();
	maskMoney.enable();

//	var maskNumber = new Brewer.MaskNumber();
//	maskNumber.enable();
	
	var maskPhoneNumber = new Brewer.MaskPhoneNumber();
	maskPhoneNumber.enable();

	var maskDate = new Brewer.MaskDate();
	maskDate.enable();

	var clockPicker = new Brewer.ClockPicker();
	clockPicker.enable();

	var maskCpfCnpjGeral = new Brewer.MaskCpfCnpjGeral();
	maskCpfCnpjGeral.enable();

	var security = new Brewer.Security();
	security.enable();

});
