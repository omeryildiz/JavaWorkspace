 /************************************************************************
 ************************************************************************
 * csv4seam.js
 * Client-Side Validation For Seam 
 * Created by Suayip Ozmen on 27/11/2008
 * @version 0.3
 * Last Change 12/06/2009
 ************************************************************************
 *****Changelog*****
 * version 0.3.1 (03/01/2009)
 * Firefox 2 bug. disable mask controls for firefox2
 * jqbrowser Browser info plugin
 * decimal support
 ------------------
 * version 0.3 (12/12/2008)
 * meiomask.js updated to 1.0.4
 * Added infinite mask support
 * Added zipcode support for Turkey (5 digits)
 * onlynumeric() deleted. Replaced by infinite number mask.{mask:'9',type:'infinite'}
 ------------------
 * version 0.2.1 (06/12/2008)
 * Added - ssn validation (Tckimlikno)
 ------------------
 * version 0.2 (02/12/2008)
 * Added - Email validation
 ************************************************************************
 * If you want to use this library for your project,
 * please be sure jQuery.js and meiomask.js scripts are included.
 ***************************************
 * Many of these functions based on jQuery.
 * For mask controls meiomask.js plugin (coded by Fabio M. Costa) is used.
 * This javascript library is used for handling some basic exceptions
 * before user submit the form to the application server.
 * (supports; required fields, numeric values,email validator, using predefined masks or some range controls etc..)
 * 
 * Problems may occur if you dont obey predefined syntax(class,alt,field,form names). 
 * These functions have checked on Mozilla Firefox 3.0.x without any problems.
 * 
 * Some functions are named in Turkish. Read comments above the functions carefully.
 * 
 ************************************************
 ****************Validation Rules****************
 ************************************************
 * 1. Checks if required fields are empty.
 * Give a class name "checkRequired" to your submit button of your form.   
 * There is no need to change any class name of your textareas or input fields
 *
 * 2. Check if user entered length of a string defined in
 * alt tag between minimum and maximum.
 * Warns if entered text has a length is not between in defined values
 * function : checkLength() 
 * Usage : styleClass="length" alt="minlen=3;maxlen=8;"
 *
 * 3. Range control for integer values
 * Force user to enter only integer numbers. You dont need to call onlyNumeric()
 * Usage : styleClass="rangeMask" alt="min=10;max=300"
 * If entered number(5 or 567) is not between 10 and 300 shows a warning alert.
 * function : checkRange()
 * 
 * 4. Force user to enter only numeric values to input fields
 * Usage : styleClass="numeric"
 *
 * 5. Using predefined masks or writing your own rules
 * This property is based on meiomask.js plugin.
 *  
 * a) Using defined masks: 
 * 'time' : '29:69'
 * 'date' : '39/19/9999'
 * 'creditCard': '9999 9999 9999 9999'
 * 'decimal' : '99999999.99'
 * 
 * Usage : styleClass="phone"
 * 
 * b) Writing your own masks
 *********************************************************
 * You should write styleClass="mask" for this input field
 *********************************************************
 * Rules 
 	'z': /[a-z]/,
	'Z': /[A-Z]/,
	'a': /[a-zA-Z]/,
	'*': /[0-9a-zA-Z]/,
	'@': /[0-9a-zA-ZçÇáàãéèíìóòõúùü]/
 * Sample 1 : If you want to an input field that will control 3 integers less than 200,
 * and a minus(-) sign, after enter 2 characters (allows 176-bd, but not 455-bc or 176bc or 176-45)
 * You should write your own mask in alt="mask=199-aa;"
 * Sample 2 : Control 4 characters, 1 space, 3 capital letter, 1 plus
 * Alt tag will alt="mask=**** ZZZ+"
 *
 * 6. Email Validation
 * This function controls most of email types like asadas@192.146.45.34
 * or subdomains and email lists or combined mails for users...etc.
 * Usage : styleClass="email"
 * Control : Prints alert dialog onblur when email is not allowed form
 *
 * function : checkEmail()
 * 
 * 7. Ssn validator for Turkey (TCKimlikNo)
 * Allows enter 11 integer digits.
 * You dont need to write class numeric. 
 * Warns user if entered ssn is not valid
 * This algorithm is obfuscated. 
 * Usage : styleClass="tckimlik"
 *
 * function : checkTcno()
 *
 * 8. Zipcode control for Turkey
 * Allows enter 5 integer digits.
 * You dont need to write class numeric. 
 * Usage : styleClass="zipcode"
 *
 *
 ************************************************************************
 ************************************************************************
 */
 
 /************************************/
 var brow;
 /************************************/
 
 /*
 * Overwrite onclick functions of save or update buttons
 * which have a classnames 'checkRequired'
 */
 jQuery(function($){
 	brow=$.browser;
	var submit = $(".checkRequired");
	var i=0;
	for(i=0;i<submit.length;i++){
		if(submit[i] != null){
			submit[i].onclick=function () { return checkReq();}
		}
	}
});
 
 /**
 * Checks empty fields if they are marked as required.
 * And changes background color of that field to red.
 * If there is no empty fields,it submits the form
 */
 function checkReq(){
 	var req = $$('.required');
 	
 	var popreq = $$('.innerpop');

 	var isEmpty=false;
 	var emptyFields = new Array();
 	var emptyCount=0;
 	
 	for(i=0;i<req.length;i++){
 		var prob= $$('.required')[i].parentNode.parentNode;
 		if(prob.className == 'prop'){
 			prob=prob.childNodes;
 			for(j=0;j<prob.length;j++){
 			//you should set default class name to "value" form layout/edit.xhtml
	 			var valmatch = /value(.*?)/i.exec(prob[j].className);
				if(valmatch != null){
	 				var z=prob[j].childNodes;
	 				for(h=0;h<z.length;h++){
	 					//check for fields like contact popup right of input text
	 					var mh = /(.*?)_selection/i.exec(z[h].id);
					 	if(mh != null){
					 		continue;
					 	}
					 	//for all popups dont check for required fields
	 					var flag=false;
	 					for(a=0;a<popreq.length;a++){
	 						var re = new RegExp('(.*?)'+popreq[a].id+'(.*?)', 'ig');
	 						var match = re.exec(z[h].name);
	 						if(match !=null){
	 							flag=true;
	 							break;
	 						}
	 					}
	 					//if it is inside a popup try next input
	 					if(flag){
	 						break;
	 					}
	 					
	 					//check for input and textarea
	 					if(z[h].nodeName=='INPUT' || z[h].nodeName=='input' || 
	 							z[h].nodeName=='TEXTAREA' || z[h].nodeName=='textarea'){
	 						
	 						if(z[h].value == ''){
	 							isEmpty=true;
	 							emptyFields[emptyCount++]=z[h];
	 						}else{
	 							z[h].style.backgroundColor='white';
	 							if(brow.msie){
	 								z[h].style.border='';
	 							}else{
	 								z[h].style.border=null;
	 							}
	 						}
	 					//for selectbox
	 					}else if(z[h].nodeName=='SELECT' || z[h].nodeName=='select'){
	 						if(z[h].value == 'org.jboss.seam.ui.NoSelectionConverter.noSelectionValue'){
	 							isEmpty=true;
	 							emptyFields[emptyCount++]=z[h];
	 						}else{
	 							z[h].style.backgroundColor='white';
	 							if(brow.msie){
	 								z[h].style.border='';
	 							}else{
	 								z[h].style.border=null;
	 							}
	 						}
	 					}
	 				}
	 			}
 			}	
 		}
 	}
 	
 	if(isEmpty){ //is there any empty fields?
 		for(var i=0;i<emptyCount;i++){
 			emptyFields[i].style.backgroundColor='#fcb4b4';
 			emptyFields[i].style.border='red solid 1px';
 		}
 		alert('Lütfen zorunlu alanları doldurunuz!');
 		
 		emptyFields[0].focus;
 		emptyFields[0].focus();
 		
 		return false;
 	}
 	return true;
 }
 /************************************************************************/

 /**
 * Email Validation
 */
  function checkEmail(event){
 	var email=event.target.value;
 	if(email != '' ){
 		var match=/^(("[\w-\s]+")|([\w-]+(?:\.[\w-]+)*)|("[\w-\s]+")([\w-]+(?:\.[\w-]+)*))(@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/i.test(email); 
 	
	 	if(match == false){
	 		alert("Email adresi hatalı girildi.");
	 		return false;
	 	}
	}
 	return true;
 }
 
 /************************************************************************/
  
 /**
 * Checks text length of an input.
 * alt="minlen=3;maxlen=10;"
 */
 function checkLength(event){
 	
 	var c = (event.which) ? event.which : event.keyCode;
 	if(c==8 || c==46) return true; //backspace & delete allowed
 	
 	var rangeText=event.target.alt; //read regex from alt tag "minlen=2;maxlen=10;"
 	var match = /minlen=([0-9]+)/i.exec(rangeText);
 	if(match != null){
 		var min=match[1];
 	}
 	match = /maxlen=([0-9]+)/i.exec(rangeText);
 	if(match != null){
 		var max=match[1];
 	}
 	
 	//max limitini gectiyse yazma
 	if(event.type == 'keydown' && event.target.value.length >=max){
 		alert("En fazla "+max+" karakter girebilirsiniz!");
 		return false;
 	}
 	//min olarak belirtilen karakterden az yazildiysa hooppp...
 	if(event.type == 'blur' && event.target.value.length <min){
 		alert("En az "+min+" karakter girebilirsiniz!");
 		return false;
 	}
 	
 	return true;
 	
 }
 
 /************************************************************************/
 
 
  function checkRate(event){
  
  	var rangeText=event.target.title; //read regex from alt tag "min=30;max=100;"
	var match = /min=([0-9]+)/i.exec(rangeText);
	if(match != null){
		var min=match[1];
	}
	match = /max=([0-9]+)/i.exec(rangeText);
	if(match != null){
		var max=match[1];
	}
  	
 	match = /([0-9]+)[,.]([0-9]+)/i.exec(event.target.value);
 	if(match != null){
 		var fraction=match[1];
 		var decVal=match[2];
 		
 		if(parseInt(fraction) < min){
 			alert("Daha büyük bir sayı giriniz! "+min+"<= sayı <="+max);
 			return false;
 		}
 		if(parseInt(fraction) > max){
 			alert("Daha küçük bir sayı giriniz! "+min+"<= sayı <="+max);
 			return false;
 		}
 		
 	}
 	
 	return true;
 	
 }
 
 /************************************************************************/
 
  /**
 * Checks number is in range.
 * alt="min=30;max=100;"
 * 30 <= number <= 100
 */
 function checkRange(event){
 	
 	var rangeText=event.target.alt; //read regex from alt tag "min=30;max=100;"
 	var match = /min=([0-9]+)/i.exec(rangeText);
 	if(match != null){
 		var min=match[1];
 	}
 	match = /max=([0-9]+)/i.exec(rangeText);
 	if(match != null){
 		var max=match[1];
 	}
 	
 	if(event.type == 'blur'){
 		if(parseInt(event.target.value) < min){
 			alert("Daha büyük bir sayı giriniz! "+min+"<= sayı <="+max);
 			event.target.value="";
 			return false;
 		}
 		if(parseInt(event.target.value) > max){
 			alert("Daha küçük bir sayı giriniz! "+min+"<= sayı <="+max);
 			event.target.value="";
 			return false;
 		}
 		return true;
 	}
 	
 	return true;
 	
 }
 
 /************************************************************************/
 /**
 * Checks for valid ssn (TCKimlikNo)
 */
 
 var _0x81d5=["\x76\x61\x6C\x75\x65","\x74\x61\x72\x67\x65\x74","","\x73\x75\x62\x73\x74\x72\x69\x6E\x67","\x63\x68\x61\x72\x41\x74","\x59\x61\x6E\x6C\u0131\u015F\x20\x62\x69\x72\x20\x54\x43\x4B\x69\x6D\x6C\x69\x6B\x20\x4E\x75\x6D\x61\x72\x61\x73\u0131\x20\x67\x69\x72\x64\x69\x6E\x69\x7A\x21"];
 function checkTcno(_0xb613x2){var _0xb613x3=_0xb613x2[_0x81d5[0x1]][_0x81d5[0x0]];if(_0xb613x3==_0x81d5[0x2]){return true;} ;var _0xb613x4=_0xb613x3[_0x81d5[0x3]](0x0,0x9);var _0xb613x5=parseInt(_0xb613x4[_0x81d5[0x4]](0x0))+parseInt(_0xb613x4[_0x81d5[0x4]](0x2))+parseInt(_0xb613x4[_0x81d5[0x4]](0x4))+parseInt(_0xb613x4[_0x81d5[0x4]](0x6))+parseInt(_0xb613x4[_0x81d5[0x4]](0x8));var _0xb613x6=parseInt(_0xb613x4[_0x81d5[0x4]](0x1))+parseInt(_0xb613x4[_0x81d5[0x4]](0x3))+parseInt(_0xb613x4[_0x81d5[0x4]](0x5))+parseInt(_0xb613x4[_0x81d5[0x4]](0x7));var _0xb613x7=((0x7*_0xb613x5)+(0x9*_0xb613x6))%0xa;var _0xb613x8=(0x8*_0xb613x5)%0xa;var _0xb613x9=_0xb613x4+_0xb613x7+_0xb613x8;if(_0xb613x9==_0xb613x3){return true;} else {alert(_0x81d5[0x5]);return false;} ;} ;
 

jQuery(function($){	

	setMasks();
	
});

function setMasks() {
	
	if(brow.mozilla && parseFloat(brow.version.substr(0,3)) < 1.9 ) {
		return; //disable mask controls 
	}
	
	(function($){
		$(
			function(){
			
				$('input:text').setMask();
				
				var maskList = $('.mask');
				if(maskList.length > 0){
					var i,rangeText,match;
					for(i=0;i<maskList.length;i++){
						rangeText=maskList[i].alt;
						match = /mask=([0-9a-zA-Z#*@._\-]+)/i.exec(rangeText);
					 	if(match != null){
					 		var masktext=match[1];
					 		//this array is required because setMask function
					 		//only can be applied for arrays
					 		var tempArr=maskList;
					 		tempArr[0]=maskList[i];
					 		tempArr.setMask(masktext);
					 	}
					}		
				}
				
				//generic alanlar icin genel masklar belirtilebilir.
				$('.phone').setMask(
					{mask:'(999) 999 99 99 #99999',defaultValue:''}
				);
				
				$('.phone').removeAttr('maxLength');

				//firefox un 3.0.13 versiyonunda L harfi kucuk olmazsa calismiyor. 
				$('.phone').removeAttr('maxlength');

				
				$('.phone_foreign').setMask(
						{mask:'+999 (999) 999 99 99 #99999',defaultValue:''}
				);
				$('.phone_foreign').removeAttr('maxLength');
				$('.phone_foreign').removeAttr('maxlength');
				
				
				$('.tckimlik').setMask(
					{mask:'99999999999',defaultValue:''}
				);

				$('.tckimlik').removeAttr('maxLength');
				
				$('.numeric').setMask(
					{mask:'9',type:'repeat',defaultValue:''}
				);

				$('.numeric').removeAttr('maxLength');

				//firefox un 3.0.13 versiyonunda L harfi kucuk olmazsa calismiyor. 
				$('.numeric').removeAttr('maxlength');

				$('.rangeMask').setMask(
					{mask:'9',type:'repeat',defaultValue:''}
				);
				
				$('.zipcode').setMask(
					{mask:'99999',defaultValue:''}
				);
				$('.zipcode').removeAttr('maxLength');
				
				$('.creditCard').setMask(
					{mask:'9999 9999 9999 9999'}
				);
				
				$('.plate').setMask(
					{mask:'99a**999'}
				);
				/*
				$('.decimal').setMask(
					{mask:'99.9999999999999999999',type:'reverse',defaultValue:'000'}
				);
				*/
			}
		);
	})(jQuery);
}
 
 /************************************************************************/
 
 function ustSatiriKopyala(e){
 	
 	var focused=e.target; //ie 6 ile uyumsuz
 	
 	if(focused != null){
	 	var match = /form:fdt:([0-9]+):([a-z]+)/i.exec(focused.name);
	 	var rowkey=match[1];
	 	var inputAlan=match[2];
	 	if(inputAlan == 'aciklama' && rowkey>0){
	 		var ustrow=$('form:fdt:'+(rowkey-1)+':aciklama');
	 		var currow=$('form:fdt:'+rowkey+':aciklama');
	 		
	 		currow.value=ustrow.value;
	 		
	 	}
	 }
 }
 
 function focusFirstField(){
    if($('form') !=null){
    	Form.focusFirstElement( $('form') );
    }
 }
 
 function focusLastRow(){

    var ee = $$( '.utdff' );
    
    if( ee.length > 0 ){
        var e = ee[ ( ee.length - 1 ) ];
        Form.Element.focus(e);
    } 
 }

 /************************************************************************/
 
//window.setTimeout( '', 100 );
 
 