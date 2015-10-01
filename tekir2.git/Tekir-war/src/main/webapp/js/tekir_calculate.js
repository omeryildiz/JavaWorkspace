
function commaToDot(str) {
	str = str.replace(/\./g,'');
	return str.replace(/,/g,'.');
}

function dotToComma(str) {
	return str.replace(/\./g,',');
}
               
function calcAmount( rowId ){
    var rid = 'form:its:' + rowId + ':';
    var q = $( rid + 'quantity' );
    var a = $( rid + 'amount' );
    var p = $( rid + 'unitPrice' );
    var v = $( rid + 'taxRate' );
    
    if(brow.mozilla && parseFloat(brow.version.substr(0,3)) < 1.9 ) {
    
	}else {
		q = commaToDot($F(q));
		p = commaToDot($F(p));
	}
	
    var kdv = v.innerHTML;
    var vatMultiplier = (100 + parseInt(kdv))/100;
    var z = q * p * vatMultiplier;
    
   	a.value =z.toFixed(2);
    
    setMasks();
    
}

function calcPrice( rowId ){
    var rid = 'form:its:' + rowId + ':';
    var q = $( rid + 'quantity' );
    var a = $( rid + 'amount' );
    var p = $( rid + 'unitPrice' );
    var v = $( rid + 'taxRate' );
    
    q = commaToDot($F(q)).replace(/\./g,'');
    a = commaToDot($F(a)).replace(/\./g,'');
    
    var kdv = v.innerHTML;
    var vatMultiplier = (100 + parseInt(kdv))/100;
    var z = (  a * 100/(100+parseInt(kdv)))/ q;
    
    p.value =z.toFixed(2);
    
    setMasks();
    calcAmount(rowId);
    
}
               