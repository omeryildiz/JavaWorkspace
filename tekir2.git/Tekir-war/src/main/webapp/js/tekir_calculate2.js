
function commaToDot(str) {
	str = str.replace(/\./g,'');
	return str.replace(/,/g,'.');
}

function dotToComma(str) {
	return str.replace(/\./g,',');
}

//foreignExchange sayfasinda yatirilan miktari hesapliyor..
function calculateToAmount (flag){

	if (flag=='Empty'){
		return;
	}
	var from =$('form:decoFromAmount:inputFromAmount');
	var to =$('form:decoToAmount:inputToAmount');
	var curr =$('form:decoCurrencyRate:currencyRate');
	
	if(brow.mozilla && parseFloat(brow.version.substr(0,3)) < 1.9 ) {

	}else {
		from = commaToDot($F(from));
		curr = commaToDot($F(curr));
	}
	
	if (flag=='Hard'){
		var z = from * curr;
    }else{
    	var z = from / curr;
    }   
	to.value =z.toFixed(2);
	
	setMasks();

}

//foreignExchange sayfasinda doviz kurunu hesapliyor..
function calculateCurrencyRate (){
	var from =$('form:decoFromAmount:inputFromAmount');
	var to =$('form:decoToAmount:inputToAmount');
	var curr =$('form:decoCurrencyRate:currencyRate');
	
	if(brow.mozilla && parseFloat(brow.version.substr(0,3)) < 1.9 ) {

	}else {
		from = commaToDot($F(from));
		to = commaToDot($F(to));
	}
	
	if(from >= to){
		var z = from / to;
	} else{
		var z = to / from;
	}
	
         
	curr.value =z.toFixed(4);
	
	setMasks();

}

function getDatatableId() {
	var table  = $('form:its');
	if (table != null) {
		return "its:";
	}
	return "itsMini:";
}

function calcAmount( rowId ){
	var tableId = getDatatableId();
	
	var rid = 'form:' + tableId + rowId + ':';
    var q = $( rid + 'quantity' );
    var a = $( rid + 'amount' );
    var p = $( rid + 'unitPrice' );
    
    if(brow.mozilla && parseFloat(brow.version.substr(0,3)) < 1.9 ) {

	}else {
		q = commaToDot($F(q));
		p = commaToDot($F(p));
	}
    
    var z = q * p;
    
    if (p.alt == 'decimal_sixscale') {
    	a.value =z.toFixed(6);
    } else {
    	a.value =z.toFixed(2);
    }
               
    setMasks();
    if ($( rid + 'taxRate' ) != null) {
    	calcTaxExcluded(rowId);
    }
}

function calcAmountOfTenderItem(quantity, amount, unitPrice){

    var q = $( quantity );
    var a = $( amount );
    var p = $( unitPrice );
    
    if(brow.mozilla && parseFloat(brow.version.substr(0,3)) < 1.9 ) {

	}else {
		q = commaToDot($F(q));
		p = commaToDot($F(p));
	}
    
    var z = q * p;
         
	a.value =z.toFixed(2);
               
    setMasks();

//    if ($( rid + 'taxRate' ) != null) {
//    	calcTaxExcluded(rowId);
//    }
}
           
function calcPrice( rowId ){
	var tableId = getDatatableId();

	var rid = 'form:' + tableId + rowId + ':';
	var q = $( rid + 'quantity' );
	var a = $( rid + 'amount' );
	var p = $( rid + 'unitPrice' );
                   
	q = commaToDot($F(q)).replace(/\./g,'');
	a = commaToDot($F(a)).replace(/\./g,'');

    var z = a / q;
               
    if (p.alt == 'decimal_sixscale') {
    	p.value =z.toFixed(6);
    } else {
    	p.value =z.toFixed(2);
    }

	setMasks();
	calcAmount(rowId);
}


function calcTaxExcluded(rowId) {
	var tableId = getDatatableId();
	
    var rid = 'form:' + tableId + rowId + ':';

    var a = $( rid + 'amount' );
    var r = $( rid + 'taxRate' ).innerHTML;
    var t = $( rid + 'taxIncluded' ).innerHTML;

    var x = $( rid + 'taxExcludedTotal' );
	
	a = commaToDot($F(a));
	
	if (t == 'true') {
		var z = 1 + (r / 100);
		z = a / z;
		x.value = z.toFixed(2);
	} else {
		x.value = a;	
	}
}