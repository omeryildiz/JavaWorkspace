var oldloader;

function init(){
    focusFirstField();
//oldloader();
}

function toggleActionIcons(elementId) {
    var elem = $(elementId);
    if (elem.style.display=='') {
        elem.style.display='none';
    } else {
        elem.style.display='';
    }
}

function openPreviewPopup(themeName) {
    var popupImage = $('popImage');
	
    popupImage.src='../img/' + themeName + '_screenshot.png';
	
    Richfaces.showModalPanel('previewPop');
}

function setThemeLineAttributes(row) {
    //checkboxlar
    jQuery(":checkbox").each(function() {
        this.checked = false;
    });
    $('themePaneForm:themeTable:' + row.rowIndex + ':checkbox').checked=true;

    //arkaplan rengi
    jQuery(".selectedRowStyle").removeClass('selectedRowStyle');
    row.className='rich-table-row selectedRowStyle';
}

function isEnterCode(event) {
    if (event.keyCode==13) {
        //		alert('u pressed enter');
        msjs();
        return false;
    }
}

function disableEnterCode(event) {
    if (event.keyCode==13) {
        return false;
    }
}

function submitOnEnterPressed(event, buttonId) {
    if (event.keyCode==13) {
        document.getElementById(buttonId).click();
    	return false;
    }
}

function clearBarcodeInput() {
    var barcodeBox = $('barcodeInclude:barcodeForm:barcodeBox');
    barcodeBox.value = '';
    barcodeBox.focus();
}

function focusToBarcodeInput() {
    var barcodeBox = $('barcodeInclude:barcodeForm:barcodeBox');
    barcodeBox.focus();
}

function focusFirstField(){
    
    var ff = $$( '.utff' );
	
    if( ff.length > 0 ){
        var f = ff[ ( ff.length - 1 ) ];
        Form.Element.focus(f);
        return;
    }
    
    var form = $('form');
    if( form != null ){
        Form.focusFirstElement( form );
    }
}
  
function focusLastRow(){

    var ee = $$( '.utdff' );
    
    if( ee.length > 0 ){
        var e = ee[ ( ee.length - 1 ) ];
        Form.Element.focus(e);
    }
    
}

function focusToField(elementId){

    var elem = $( elementId );
    if ( elem != null ){
    	elem.focus();
    }
    
}

// personel ekleme sayfasinda pasaport ve tckimlik numarası kontrolu
function tcnoPersonelNoBosmu(){
    var tcno=$('form:tcno:tckimlikno').value;
    var passportNo=$('form:passport:passportNo').value;
	
    if(tcno == '' && passportNo == ''){
        alert('Pasaport No veya TC Kimlik No degerlerinden en az bir tanesinin girilmesi zorunludur');
        return false;
    }
    //eger ikisinden biri girilmisse boş alan kontrolu yap.
    return checkReq();
}

/**
 * Bun fonksiyon verilen değeri verilen elemente koyar ve focusu o elemente taşır...
 */
function selectValue( retpoint, val ){
    var elem = $( retpoint );
    if (elem == null) {
        alert('Lütfen tekrar deneyin.');
        return false;
    } else {
        if( val != '' ){
            elem.value = val;
        }
        elem.focus();
    }
}

function selectValueWithoutAlert( retpoint, val ){
    var elem = $( retpoint );
    if (elem != null) {
        if( val != '' ){
            elem.value = val;
        }
        elem.focus();
    }
}
 
/**
 * Bu fonksiyon ile Modal Panel açılırken alnınan geri dönüş id'si form üzerinde gizli bir alana yazılır.
 */
function saveReturnPoint( retpoint ){
    var elem = $( 'retpoint' );
    elem.value = retpoint;
}

function saveContactReturnPoint( retpoint ){
    var elem = $('retcontact');
    elem.value = retpoint;
}
 
/**
 * Dialogda seçilmiş olan değeri hidden alanda yazılı yere göndermek iüzere richModeli kapatır..
 */
 
function returnValue( mp, val ){
    if ($F( 'retpoint' ) == null) {
        return false;
    } else {
        param = new Object();
        param.retpoint = $F( 'retpoint' );
        param.result = val;
        Richfaces.hideModalPanel( mp, param );
    }
}

function returnContactValue( mp, val ){
    if ($F('retcontact') == null) {
        return false;
    } else {
        param = new Object();
        param.retpoint = $F('retcontact');
        param.result = val;
        Richfaces.hideModalPanel( mp, param );
    }
}
 
function contactSelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'conSelectPop', param );
}
 
function closeContactSelect( val ){
    returnValue( 'conSelectPop', val );
}

function departmentSelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'deptSelectPop', param );
}
 
function closeDepartmentSelect( val ){
    returnValue( 'deptSelectPop', val );
}

function authorityDocumentSelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'authDocSelectPop', param );
}
 
function closeAuthorityDocumentSelect( val ){
    returnValue( 'authDocSelectPop', val );
}




function loadTypeSelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'loadTypeSelectPop', param );
}
 
function closeLoadTypeSelect( val ){
    returnValue( 'loadTypeSelectPop', val );
}
 
function securitySelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'secSelectPop', param );
}
 
function closeSecuritySelect( val ){
    returnValue( 'secSelectPop', val );
}
 
function transportShipmentSelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'transportShipmentSelectPop', param );
}
 
function closeTransportShipmentSelect( val ){
    returnValue( 'transportShipmentSelectPop', val );
}
function chequeSelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'cheSelectPop', param );
}

 
function closeChequeSelect( val ){
    returnValue( 'cheSelectPop', val );
}

function promissorySelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'promissorySelectPop', param );
}

 
function closePromissorySelect( val ){
    returnValue( 'promissorySelectPop', val );
}

function chequeAddSelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'cheAddSelectPop', param );
}

function closeChequeAddSelect( val ){
    returnValue( 'cheAddSelectPop', val );
}

function customerChequeAddSelectPopup( ){
     
    Richfaces.showModalPanel( 'customerCheAddSelectPop' );
}

function closeCustomerChequeAddSelect( ){

    var er = 0;
    jQuery(".warning").each(function (){
        er ++;
    });
	
    if(er == 0){
        Richfaces.hideModalPanel( 'customerCheAddSelectPop' );
    }
  
}

function firmChequeAddSelectPopup( ){
     
    Richfaces.showModalPanel( 'firmCheAddSelectPop' );
}

function closeFirmChequeAddSelect( ){

    var er = 0;
    jQuery(".warning").each(function (){
        er ++;
    });
	
    if(er == 0){
        Richfaces.hideModalPanel( 'firmCheAddSelectPop' );
    }

}

function quickContactAddPopup(retpoint){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'quickContactAddPop' ,param);
}

function quickCopyProductPopup(retpoint){
    param = new Object()
    param.retpoint = retpoint;

    Richfaces.showModalPanel( 'copyProductPopup' ,param);
}




function copyProductPopup(retpoint){
    param = new Object()
    param.retpoint = retpoint;

    Richfaces.showModalPanel( 'copyProductPopup' ,param);
}

function closeQuickContactAdd( val ){

    returnContactValue( 'quickContactAddPop', val );
    
}

function quickContactAddInvShipPopup( ){
     
    Richfaces.showModalPanel( 'quickContactAddInvShipPop' );
}

function closeQuickContactAddInvShip( ){

    var er = 0;
    jQuery(".warning").each(function (){
        er ++;
    });
	
    if(er == 0){
        Richfaces.hideModalPanel( 'quickContactAddInvShipPop' );
    }

}

function vehicleAddSelectPopup( ){
     
    Richfaces.showModalPanel( 'vehicleAddSelectPop' );
}

function closeVehicleAddSelect( ){

    var er = 0;
    jQuery(".warning").each(function (){
        er ++;
    });
	
    if(er == 0){
        Richfaces.hideModalPanel( 'vehicleAddSelectPop' );
    }
}

function documentInfoPopup( ){
    Richfaces.showModalPanel( 'documentInfoPop' );
}

function closeDocumentInfo( ){

    var er = 0;
    jQuery(".warning").each(function (){
        er ++;
    });

    if(er == 0){
        Richfaces.hideModalPanel( 'documentInfoPop' );
    }
}

function customerPromissoryAddSelectPopup( ){
     
    Richfaces.showModalPanel( 'customerPromissoryAddSelectPop' );
}

function closeCustomerPromissoryAddSelect( ){

    var er = 0;
    jQuery(".warning").each(function (){
        er ++;
    });
	
    if(er == 0){
        Richfaces.hideModalPanel( 'customerPromissoryAddSelectPop' );
    }
  
}

function firmPromissoryAddSelectPopup( ){
     
    Richfaces.showModalPanel( 'firmPromissoryAddSelectPop' );
}

function closeFirmPromissoryAddSelect( ){

    var er = 0;
    jQuery(".warning").each(function (){
        er ++;
    });
	
    if(er == 0){
        Richfaces.hideModalPanel( 'firmPromissoryAddSelectPop' );
    }
}

function vehicleSelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'vehicleSelectPop', param );
}
 
function closeVehicleSelect( val ){
    returnValue( 'vehicleSelectPop', val );
}

function productSelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'prodSelectPop', param );
}
 
function closeProductSelect( val ){
    returnValue( 'prodSelectPop', val );
}

function bankCardSelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'bankCardSelectPop', param );
}
 
function closeBankCardSelect( val ){
    returnValue( 'bankCardSelectPop', val );
}

function contractCommisionSelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
	
    Richfaces.showModalPanel( 'contractCommisionSelectPop', param );
}

function closeContractCommisionSelect( val ){
    returnValue( 'contractCommisionSelectPop', val );
}

function expenseAndDiscountSelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'expenseAndDiscountSelectPop', param );
}
 
function closeExpenseAndDiscountSelect( val ){
    returnValue( 'expenseAndDiscountSelectPop', val );
}

function contactDocumentAddPopup(){
    
    Richfaces.showModalPanel( 'docAddPop');
}
 
function closeContactDocumentAdd(){
    var er = 0;
    jQuery(".warning").each(function (){
        er ++;
    });
	
    if(er == 0){
        Richfaces.hideModalPanel( 'docAddPop' );
    }
}

function closeDocumentDiaog(){

    var er = 0;
    jQuery(".warning").each(function (){
        er ++;
    });

    if(er == 0){
        Richfaces.hideModalPanel( 'documentpanel' );
    }
}

function closeDocumentInfoDiaog(){

    var er = 0;
    jQuery(".warning").each(function (){
        er ++;
    });

    if(er == 0){
        Richfaces.hideModalPanel( 'documentinfopanel' );
    }
}
  
 
function deleteConfirmation(){
    return !confirm('Kaydı silmek istediğinizden emin misiniz?');
}
 
 
function openHelp(){
    newwin = window.open(null, 'HelpWinId', 'width=450,height=600,scrollbars=yes,resizable=yes')
    if (window.focus) {
        newwin.focus()
        }

}

/**
 * www.tekir.com.tr/files/tekir.annon.js dosyasının önceden yüklenmiş olmasılazım.
 * O dosya içerisinde bulunan _tekir_news_body_ değişkenini bulunduğu form üzerindeki _tekir_news_ id'li bileşene yazar.
 * 
 */
function showAnnouncements(){

    var b = $( "_tekir_news_");
  
    if( _tekir_news_body_ == null ){
        _tekir_news_body_ = "Duyuru gelmesi için internet ba\u011flantısı gerekmektedir."
    }
  
    b.innerHTML = _tekir_news_body_;
     
}
 
//oldloader = window.onload;
//window.onload = init();
window.setTimeout( 'focusFirstField()', 100 );

function toggleCheckAll(){
    jQuery(":checkbox").not(":disabled").each(function() {
        this.checked = !$("form:browseList:checkAll").checked;
    });
    $("form:browseList:checkAll").checked = !$("form:browseList:checkAll").checked;
}

function closeActivePanel() {
    topPanel = ModalPanel.activePanels.last();
    if (topPanel != null) {
        topPanel.hide();
    }
}

document.onkeydown = function(e){
    if (e == null) { // ie
        keycode = event.keyCode;
    } else { // mozilla
        keycode = e.which;
    }
    if(keycode == 27){ // escape, close box
        closeActivePanel();
    }
}


function stopEventBubling(e){
    e.stopPropagation();
}
/**
 * Toogle edilecek olan row verilir. Aynı tbody altındaki rowları gizler ya da açar.
 */
function toggleTableRows(e){

    //e : bir <a> üstünde bir td onun üstünde bir tr onunda üstünde bir tbody
    jQuery("tr.child", jQuery(e).parent().parent().parent()).slideToggle("fast");
//jQuery(e).toggleClass("collapsed");

/*
$(document).ready(function() {

   $("tr.header").click(function () {
      $("tr.child", $(this).parent()).slideToggle("fast");
   });


});
*/
}


var expandTable = {
    init: function(tableid){
        jQuery(".toggleCell").each(function(){
            jQuery('<a href="#" class="toggler">Toogler</a>').mousedown(function (e) {
                /* STOP event bubbling */
                e.stopPropagation();
            }).click(function(){
                if( jQuery(this).hasClass("collapsed") ){
                    jQuery(this).removeClass("collapsed");
                    jQuery("tr.child", jQuery(this).parent().parent().parent()).show();
                } else {
                    jQuery(this).addClass("collapsed");
                    jQuery("tr.child", jQuery(this).parent().parent().parent()).hide();
                }
                return false;
            }).prependTo(jQuery(this));
        });
    }
}


function initRoleTable(){
    jQuery("#permissionTable th").css("text-align","left");
    jQuery("#permissionTable td").css("text-align","left");
    jQuery("#permissionTable .checkAll").click(function(){
        var v = this.checked;
        var s = jQuery(this).attr("id");
        var ss = s.split(":")[1];
        jQuery(".p"+ss+":checkbox").each(function() {
            this.checked = v;
        });
    });
    jQuery("#permissionTable .checkGroup").click(function(){
        var v = this.checked;
        var s = jQuery(this).attr("id");
        var ss = s.split(":")[1];
        var pgs = s.split(":")[2];
        jQuery(".g" + ss + pgs + ":checkbox").each(function() {
            this.checked = v;
        });
    });
    jQuery("#permissionTable .checkOther").click(function(){
        var v = this.checked;
        var s = jQuery(this).attr("id");
        var g = s.split(":")[1];
        var r = s.split(":")[2];
        jQuery(".go" + g + r + ":checkbox").each(function() {
            this.checked = v;
        });
    });
    //Diger Haklar kolonunu kapatıp acar
    jQuery(".oatoggler").each(function(){
            jQuery('<a href="#" class="toggler">Toogler</a>').mousedown(function (e) {
                /* STOP event bubbling */
                e.stopPropagation();
            }).click(function(){
                if( jQuery(this).hasClass("collapsed") ){
                    jQuery(this).removeClass("collapsed");
                    jQuery(".oatable", jQuery(this).parent().parent()).show();
                } else {
                    jQuery(this).addClass("collapsed");
                    jQuery(".oatable", jQuery(this).parent().parent()).hide();
                }
                return false;
            }).prependTo(jQuery(this));
        });
}

function workBunchSelectPopup( retpoint ){
    param = new Object()
    param.retpoint = retpoint;
     
    Richfaces.showModalPanel( 'workBunchSelectPop', param );
}
function closeWorkBunchSelect( val ){
    returnValue( 'workBunchSelectPop', val );
}
