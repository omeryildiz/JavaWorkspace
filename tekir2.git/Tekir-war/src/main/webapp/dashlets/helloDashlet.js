/*
 * helloDashlet.js
 */

function save() {
    Seam.Component.getInstance("helloDashlet").changeMessage( jQuery("#message").val());
    iNettuts.closeEdit("helloDashlet");
    iNettuts.refresh("helloDashlet");
}

function testCallback(result) {
    alert(result);
}