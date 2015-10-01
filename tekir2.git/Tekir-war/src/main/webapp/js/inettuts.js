/*
 * Script from NETTUTS.com [by James Padolsey] V.2 (ENHANCED, WITH COOKIES!!!)
 * @requires jQuery(jQuery), jQuery UI & sortable/draggable UI modules & jQuery COOKIE plugin
 *
 * heavily modifed for working in tekir framework by Hakan Uygun
 */

var iNettuts = {
    
    settings : {
        columns : '.column',
        widgetSelector: '.widget',
        //handleSelector: '.widget-head',
        //contentSelector: '.widget-content',
        handleSelector: '.rich-panel-header',
        contentSelector: '.rich-panel-body',
        contextRoot: '/mestan',
        /* If you don't want preferences to be saved change this value to
            false, otherwise change it to the name of the cookie: */
        //saveToCookie: 'inettuts-widget-preferences',
        saveToCookie:false,
        
        widgetDefault : {
            movable: true,
            removable: true,
            refreshable: true,
            collapsible: true,
            editable: true,
            colorClasses : ['color-yellow', 'color-red', 'color-blue', 'color-white', 'color-orange', 'color-green']
        },
        widgetIndividual : {}
    },

    init : function (contextRoot, dashletProps ) {
        this.settings.contextRoot = contextRoot;
        this.settings.widgetIndividual = dashletProps;
        this.attachStylesheet('inettuts.js.css');
        this.sortWidgets();
        this.addWidgetControls();
        this.makeSortable();
    },
    
    getWidgetSettings : function (id) {
        var settings = this.settings;
        return (id&&settings.widgetIndividual[id]) ? jQuery.extend({},settings.widgetDefault,settings.widgetIndividual[id]) : settings.widgetDefault;
    },
    
    addWidgetControls : function () {
        var iNettuts = this,
            settings = this.settings;
            
        jQuery(settings.widgetSelector, jQuery(settings.columns)).each(function () {
            var thisWidgetSettings = iNettuts.getWidgetSettings(this.id);
            if (thisWidgetSettings.removable) {
                jQuery('<a href="#" class="dash-tool-close">CLOSE</a>').mousedown(function (e) {
                    /* STOP event bubbling */
                    e.stopPropagation();    
                }).click(function () {
                    if(confirm('This widget will be removed, ok?')) {
                        jQuery(this).parents(settings.widgetSelector).animate({
                            opacity: 0    
                        },function () {
                            jQuery(this).wrap('<div/>').parent().slideUp(function () {
                                jQuery(this).remove();
                            });
                        });
                    }
                    return false;
                }).appendTo(jQuery(settings.handleSelector, this));
            }

            if (thisWidgetSettings.collapsible) {
                jQuery('<a href="#" class="dash-tool-toggle">COLLAPSE</a>').mousedown(function (e) {
                    /* STOP event bubbling */
                    e.stopPropagation();
                }).click(function(){
                    jQuery(this).parents(settings.widgetSelector).toggleClass('collapsed');
                    /* Save prefs to cookie: */
                    //State değişimini saklamayalım...
                    //iNettuts.savePreferences();
                    return false;
                }).appendTo(jQuery(settings.handleSelector,this));
            }

            
            
            if (thisWidgetSettings.editable) {
                jQuery('<a href="#" class="dash-tool-edit">EDIT</a>').mousedown(function (e) {
                    /* STOP event bubbling */
                    e.stopPropagation();    
                }).toggle(function () {
                    var elem = jQuery(this).parents(settings.widgetSelector);
                    var id = elem.attr("id");
                    jQuery(elem.find('.edit-box')).load( settings.contextRoot + "/dashlets/" + id + ".edit.seam #inner_content");
                    elem.find('.edit-box').show().find('input').focus();
                    return false;
                },function () {
                    jQuery(this).parents(settings.widgetSelector)
                            .find('.edit-box').hide();
                    return false;
                }).appendTo(jQuery(settings.handleSelector,this));
                
                jQuery('<div class="edit-box" style="display:none;"/>')
                    .insertAfter(jQuery(settings.handleSelector,this));
                
            }
            
            if (thisWidgetSettings.refreshable) {
                jQuery('<a href="#" class="dash-tool-refresh">Refresh</a>').mousedown(function (e) {
                    /* STOP event bubbling */
                    e.stopPropagation();
                }).click(function () {
                    var elem = jQuery(this).parents(settings.widgetSelector);
                    var id = elem.attr("id");
                    jQuery("#" + id +"_view").load( settings.contextRoot + "/dashlets/" + id + ".view.seam #inner_content");

                    return false;
                }).appendTo(jQuery(settings.handleSelector, this));
            }
        });

        /*
        jQuery('.edit-box').each(function () {
            jQuery('input',this).keyup(function () {
                jQuery(this).parents(settings.widgetSelector).find('h3').text( jQuery(this).val().length>20 ? jQuery(this).val().substr(0,20)+'...' : jQuery(this).val() );
                iNettuts.savePreferences();
            });
            jQuery('ul.colors li',this).click(function () {
                
                var colorStylePattern = /\bcolor-[\w]{1,}\b/,
                    thisWidgetColorClass = jQuery(this).parents(settings.widgetSelector).attr('class').match(colorStylePattern)
                if (thisWidgetColorClass) {
                    jQuery(this).parents(settings.widgetSelector)
                        .removeClass(thisWidgetColorClass[0])
                        .addClass(jQuery(this).attr('class').match(colorStylePattern)[0]);
                    / Save prefs to cookie: /
                    iNettuts.savePreferences();
                }
                return false;
                
            });
        });
        */
    },

    refresh : function( id ){
        //Refresh content...
        jQuery("#" + id +"_view").load( this.settings.contextRoot + "/dashlets/" + id + ".view.seam #inner_content");
    },

    closeEdit : function( id ){
        jQuery( "#" + id + " .dash-tool-edit").click();
    },

    attachStylesheet : function (href) {
        //var jQuery = this.jQuery;
        //return jQuery('<link href="/dashlet/' + href + '" rel="stylesheet" type="text/css" />').appendTo('head');
    },
    
    makeSortable : function () {
        var iNettuts = this,
            settings = this.settings,
            jQuerysortableItems = (function () {
                var notSortable = '';
                jQuery(settings.widgetSelector,jQuery(settings.columns)).each(function (i) {
                    if (!iNettuts.getWidgetSettings(this.id).movable) {
                        if(!this.id) {
                            this.id = 'widget-no-id-' + i;
                        }
                        notSortable += '#' + this.id + ',';
                    }
                });
                //return jQuery('> li:not(' + notSortable + ')', settings.columns);
                return jQuery(settings.columns + ' li');
            })();
        
        jQuerysortableItems.find(settings.handleSelector).css({
            cursor: 'move'
        }).mousedown(function (e) {
            jQuerysortableItems.css({width:''});
            jQuery(this).parent().css({
                width: jQuery(this).parent().width() + 'px'
            });
        }).mouseup(function () {
            if(!jQuery(this).parent().hasClass('dragging')) {
                jQuery(this).parent().css({width:''});
            } else {
                jQuery(settings.columns).sortable('disable');
            }
        });

        jQuery(settings.columns).sortable({
            items: jQuerysortableItems,
            connectWith: jQuery(settings.columns),
            handle: settings.handleSelector,
            placeholder: 'widget-placeholder',
            forcePlaceholderSize: true,
            revert: 300,
            delay: 100,
            opacity: 0.8,
            containment: 'document',
            start: function (e,ui) {
                jQuery(ui.helper).addClass('dragging');
            },
            stop: function (e,ui) {
                jQuery(ui.item).css({width:''}).removeClass('dragging');
                jQuery(settings.columns).sortable('enable');
                /* Save prefs to cookie: */
                iNettuts.savePreferences();
            }
        });
    },
    
    savePreferences : function () {
        var iNettuts = this,
            settings = this.settings,
            cookieString = '';


           /* Assemble the cookie string */
        jQuery(settings.columns).each(function(i){
            cookieString += (i===0) ? '' : ';';
            cookieString += jQuery(this).attr('id') + "=";
            
            jQuery(settings.widgetSelector,this).each(function(i){
                cookieString += (i===0) ? '' : ',';
                /* ID of widget: */
                cookieString += jQuery(this).attr('id');
                //* Color of widget (color classes) */
                //cookieString += jQuery(this).attr('class').match(/\bcolor-[\w]{1,}\b/) + ',';
                /* Title of widget (replaced used characters) */
                //cookieString += jQuery('h3:eq(0)',this).text().replace(/\|/g,'[-PIPE-]').replace(/,/g,'[-COMMA-]') + ',';
                /* Collapsed/not collapsed widget? : */
                //cookieString += jQuery(settings.contentSelector,this).css('display') === 'none' ? 'collapsed' : 'not-collapsed';
            });
        });

        Seam.Component.getInstance("dashboardManager").saveDashletPositions( cookieString );

        if(!settings.saveToCookie) {return;}
        
        /* Assemble the cookie string */
        jQuery(settings.columns).each(function(i){
            cookieString += (i===0) ? '' : '|';
            jQuery(settings.widgetSelector,this).each(function(i){
                cookieString += (i===0) ? '' : ';';
                /* ID of widget: */
                cookieString += jQuery(this).attr('id') + ',';
                /* Color of widget (color classes) */
                cookieString += jQuery(this).attr('class').match(/\bcolor-[\w]{1,}\b/) + ',';
                /* Title of widget (replaced used characters) */
                cookieString += jQuery('h3:eq(0)',this).text().replace(/\|/g,'[-PIPE-]').replace(/,/g,'[-COMMA-]') + ',';
                /* Collapsed/not collapsed widget? : */
                cookieString += jQuery(settings.contentSelector,this).css('display') === 'none' ? 'collapsed' : 'not-collapsed';
            });
        });
        jQuery.cookie(settings.saveToCookie,cookieString,{
            expires: 10
            //path: '/'
        });
    },
    
    sortWidgets : function () {
        var iNettuts = this,
            settings = this.settings;
        
        /* Read cookie: */
        //var cookie = jQuery.cookie(settings.saveToCookie);
        //if(!settings.saveToCookie||!cookie) {
            /* Get rid of loading gif and show columns: */
            //jQuery('body').css({background:'#000'});
            jQuery(settings.columns).css({visibility:'visible'});
            return;
        //}
        
        /* For each column */
        jQuery(settings.columns).each(function(i){
            
            var thisColumn = jQuery(this),
                widgetData = cookie.split('|')[i].split(';');
                
            jQuery(widgetData).each(function(){
                if(!this.length) {return;}
                var thisWidgetData = this.split(','),
                    clonedWidget = jQuery('#' + thisWidgetData[0]),
                    colorStylePattern = /\bcolor-[\w]{1,}\b/,
                    thisWidgetColorClass = jQuery(clonedWidget).attr('class').match(colorStylePattern);
                
                /* Add/Replace new colour class: */
                if (thisWidgetColorClass) {
                    jQuery(clonedWidget).removeClass(thisWidgetColorClass[0]).addClass(thisWidgetData[1]);
                }
                
                /* Add/replace new title (Bring back reserved characters): */
                jQuery(clonedWidget).find('h3:eq(0)').html(thisWidgetData[2].replace(/\[-PIPE-\]/g,'|').replace(/\[-COMMA-\]/g,','));
                
                /* Modify collapsed state if needed: */
                if(thisWidgetData[3]==='collapsed') {
                    /* Set CSS styles so widget is in COLLAPSED state */
                    jQuery(clonedWidget).addClass('collapsed');
                }

                jQuery('#' + thisWidgetData[0]).remove();
                jQuery(thisColumn).append(clonedWidget);
            });
        });
        
        /* All done, remove loading gif and show columns: */
        jQuery('body').css({background:'#000'});
        jQuery(settings.columns).css({visibility:'visible'});
    }
  
};

