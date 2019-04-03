/**
 * 
 */
var qdr = qdr || {};


var tableQueueAgentReport = null;
var tableQueueAgentDetailReport = null;

qdr.qrpicker_from = {};
qdr.qrpicker_to = {};

var format  = function ( d ) {
	
	 str='<table class="table table-striped table-bordered table-hover" >'+
				'<thead><tr><td>' + $label.queuename + '</td><td>' + $label.calls + '</td><td>' + $label.duration + '</td><td>' + $label.action + '</td></thead>';
		
		for (var i=0; i < d.childSet.length; i++){
				
			str = str + '<tr >' + 
			
			'<td class="col-lg-6">' + d.childSet[i].queue + '</td>'+
			'<td class="text-center col-lg-2">' + d.childSet[i].calls + '</td>' + 
			'<td class="text-center col-lg-2">' + d.childSet[i].hours + '</td>' + 
			'<td class="text-center"><button   type="button"  id="btnShowAll'+ d.childSet[i].id  + 
			'" class=" btn btn-primary btn-xs" onclick="qdr.showDetail(\'' +1 +'\',\'' + d.childSet[i].agent +'\',\'' +  d.childSet[i].queue +  '\')">'+
			
			$label.show + '</button></td>'+
			'</tr>';
			
		}
		str = str + '</table>';

    return str;
}	

qdr.createSoundButton = function( data ){
	   var data1 = data.split("\.")[0] + "-" + data.split("\.")[1]; 
  	 return '<button  id="queuePlay-' + data1 + '" type="button" class="btn btn-success btn-xs" onclick="qdr.playSound(\'' + data1 +  '\')"><i class="fa fa-headphones"></i></button>' + 
      	// '<div class="btn-toolbar btn-toolbar-sound"> '+
      	 '<button id="queueStop-'+ data1 + '" class="btn btn-danger btn-xs hidden" onclick="qdr.stopSound(\'' + data1 + '\')" ><i class="fa fa-stop"></i></button>' +
      //	 '<button id="queueDownload-'+ data1 + '" class="btn btn-success btn-xs hidden" onclick="qdr.downloadSound(\'' + data + '\')" ><i class="fa fa-download"></i></button>' +
      //	 '</div>'+
      	 '<div id="queueSound_error-' + data1 + '" class="alert alert-danger hidden" role="alert"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i>'+
		 '<span class="sr-only">Error:</span>No sound</div>';
  	   
}
qdr.createPlayer = function(data){
	if(player != null){
		$("#queueReportPlayer").jPlayer( "destroy" );
	}
	var sound_file = data.split("-")[0] + "." + data.split("-")[1]+".mp3";
	
	var sound_url = core.soundPath + sound_file;
	var stopId = "#queueStop-" +sound_file;
	var playId = "#queuePlay-" +sound_file;
	
	player = $("#queueReportPlayer").jPlayer({
		 errorAlerts: true,
          ready: function () {
            $(this).jPlayer("setMedia", {
              mp3: sound_url
            	  
            }).jPlayer("play");
            $("#queueDownload-" + data).removeClass("hidden");
          },
          error: function (event) {
              $("#queueSound_error-" + data).removeClass("hidden");
          	  $("#queuePlay-" + data).addClass("hidden");
			  $("#queueStop-" + data).addClass("hidden");
			  
			  
			  $("#queueDownload-" + data).addClass("hidden");
			
			  setTimeout( function() {
				  $("#queueSound_error-" + data).addClass("hidden");
				  $("#queuePlay-" + data).removeClass("hidden");
			   }, 3000);
			  
			  
          },
          swfPath: "/js",
          supplied: "mp3",
	          cssSelectorAncestor: "",
	          cssSelector: {
	            stop: stopId
	          }
      });
}	

qdr.playSound = function(data){
	$("#queuePlay-" + data).addClass("hidden");
	$("#queueStop-" + data).removeClass("hidden");
	
	if(player != null){
		$("#queueReportPlayer").jPlayer( "destroy" );
	}
	tableQueueAgentDetailReport.rows().eq(0).each( function ( index ) {
		var row = tableQueueAgentDetailReport.row( index );
		var tr = row.node();
		
		if ($(row.node().childNodes[6].childNodes[0]).attr('id') != "queuePlay-" + data){
			$(row.node().childNodes[6].childNodes[0]).removeClass("hidden");
			$(row.node().childNodes[6].childNodes[1].childNodes[1]).addClass("hidden");
			$(row.node().childNodes[6].childNodes[1].childNodes[2]).addClass("hidden");
		}
		
	});
	qdr.createPlayer(data);
}
qdr.stopSound = function(data){
	
	$("#queuePlay-" + data).removeClass("hidden");
	$("#queueStop-" + data).addClass("hidden");
	$("#queueDownload-" + data).addClass("hidden");
	
	if(player != null){
		$("#queueReportPlayer").jPlayer( "destroy" );
	}
}
qdr.downloadSound = function(filename) {

	var url = core.soundPath +  filename + '.mp3';
    var pom = document.createElement('a');
    pom.setAttribute('href', url);
    pom.setAttribute('download', 'sound_' + filename+'.mp3');

    if (document.createEvent) {
        var event = document.createEvent('MouseEvents');
        event.initEvent('click', true, true);
        pom.dispatchEvent(event);
    }
    else {
        pom.click();
    }
}

qdr.showDetail = function(page, agent, queue){
	
	core.showWaitDialog(true);
	
	$("#QueueDetailReport").removeClass("hidden");
	var time1 = core.dataToMysql($( "#qrpicker_from" ).val());
	var time2 = core.dataToMysql($( "#qrpicker_to"  ).val())
	var query = {
			time1 : time1,
			time2 : time2,
			agent : agent,
			queue : queue,
			page : page,
			pageSize : $( "#queueReportPagesizeCDR").val()
	}
	if (tableQueueAgentDetailReport !=  null){
		tableQueueAgentDetailReport.destroy();
	}
	$.ajax({
		  type: "POST",
		  url: "report-queue-detail",
		  contentType : 'application/json',
		  data: JSON.stringify(query),
		  dataType: "json",
		  success: function(e){
			
			  
			  tableQueueAgentDetailReport = $('#tableQueueAgentDetailReport')
			  	.on('draw.dt', function(){
					core.showWaitDialog(false);
			  	})
			  .DataTable({
				 	data : e.records,
				 	
				 	columns : 
				 		[
				 		    { title	: $label.date, className: "text-left", 	 data : "end" , render : function(data){
	                        	return core.dateToDay(data);
				 		    }},
				 		    { title	: $label.time, className: "text-left", 	 data : "end", render : function(data){
				 		       
	                        	return core.dateToTime(data);
	                        	
				 		    } },
				            { title	: $label.name, className: "text-left", 	 data : "peer" },
				            { title	: $label.src, className: "text-center",  data : "src" },
				            { title	: $label.dst, className: "text-center",  data : "dst" },
				            { title	: $label.duration, className: "text-center", 	 data : "billsec" },
					        { title	: $label.action , className: "text-center", data : "uniqueid", render : function(data){
					        	
					        	return qdr.createSoundButton(data);
					        	
					        } }
				           
					        ],	
					     "paging"    : false,
					     "info" 	 : false,
					     "searching" : false 
					});
				$("#queueDetailReportPageTab").empty();
				
				for(var k=0; k < e.tabs.length; k++){
						if (e.tabs[k].caption == "Next"){
						//	e.pageTab[k].caption = button_next;
						}
						if (e.tabs[k].caption == "Previous"){
						//	data[0][k].caption = button_previous;
						}
					var a = $('<li/>', {
						    'id':'page-' + e.tabs[k].p,
						    'class': e.tabs[k].cssClass,
						    // src, dst, dsp, time1, time2, page
						    'html':'<a  href="#" aria-controls="tableQueueAgentDetailReport" onclick="qdr.showDetail(\'' + e.tabs[k].p +'\',\'' +  agent + '\',\'' + queue +  '\')" >' + e.tabs[k].caption + '</a>'
						}).appendTo('#queueDetailReportPageTab');
				} 
				$( "#queueDetailReportSuccessInfo").html($label.agent + ": <strong>" + e.agent + "</strong>, " +  	$label.queue +": <strong> " + e.queue + "</strong>.&nbsp;" + time1 + "-" + time2);
			
				$( "#queueDetailPageInfo" ).html($label.showing + " " + e.startEntries + " " + $label.to + " " + e.endEntries + " " + $label.of + " "  + e.totalEntries + " " + $label.entries );
			  },
			  error: function(e){
				  core.showWaitDialog( false );
				  core.showErrorMessage( " Detail Report fail!" );
			  }
		});  
}

qdr.reportQGR = function(id){
	core.showWaitDialog(true);
	$( "#QGRPanel").removeClass( "hidden");
	var query = {
			time1 : core.dataToMysql($( "#qrpicker_from" ).val()),
			time2 : core.dataToMysql($( "#qrpicker_to"  ).val()),
			agent : $( "#qr_agentName" ).val(),
			queue : $( "#qr_queueName" ).val(),
			page		: "1", 
			pageSize	:  $( "#queueReportPagesizeCDR").val()
	}
	if (tableQueueAgentReport !=  null){
		tableQueueAgentReport.destroy();
	}
	$.ajax({
		  type: "POST",
		  url: "report-queue",
		  contentType : 'application/json',
		  data: JSON.stringify(query),
		  dataType: "json",
		  success: function(e){
			
			  var dataSet = [];
			  for(i=0; i < e.agentReport.length; i++){
				  var childSet = [];
				 
				  for(j=0; j < e.agentReport[i].queueReport.length; j++){
					  childSet.push({
						  'queue' 	  		:  e.agentReport[i].queueReport[j].queue, 
						  'calls' 			:  e.agentReport[i].queueReport[j].calls, 
						  'hours'   		:  e.agentReport[i].queueReport[j].hours,
						  'agent'			:  e.agentReport[i].agent  
					  });  
				  }
				  dataSet.push({
					  'agent'		 		: e.agentReport[i].agent,  
					  'calls'   		    : e.agentReport[i].calls,
					  'hours' 				: e.agentReport[i].hours,
					  'childSet' 		    : childSet
				  });
			  }
			  
			  var maxD = 1;
			  
			  for(n=0; n < dataSet.length; n++){
				  if ( maxD < dataSet[n].calls ){
					  
					  maxD = dataSet[n].calls;
				  }
			  }
			  
			  
			  $( "#tableQueueAgentReportDiv").empty();
			
			  $( "#tableQueueAgentReportDiv").append('<table id="tableQueueAgentReport" class="table table-striped table-bordered table-hover" ></table>');
			  
			  tableQueueAgentReport = $('#tableQueueAgentReport')
			   	.on('click', 'td.details-control', function(){
			   	  
			   		tableQueueAgentReport.off('click');
			   		
					var tr = $(this).closest('tr');
					var row = tableQueueAgentReport.row( tr );
					if ( row.child.isShown() ) {
				        row.child.hide();
			            tr.removeClass('shown');
			        }
			        else {
			       
			            row.child( format(row.data()) ).show();
			            tr.addClass('shown');
			        }	
			  	})
				.on('draw.dt', function(){
					core.showWaitDialog(false);
			  })
			.DataTable({
				 	data : dataSet,
				 	columns : [
				 	          {
					                "className":      "details-control",
					                "orderable":      false,
					                "data":           null,
					                "defaultContent": '',
					                "width": "10%"
					            },
				               { title	: $label.name, className: "text-left col-lg-5", 	 data : "agent" },
					           { title	: $label.calls, className: "text-center col-lg-1", data : "calls" },
				               { title	: $label.duration, className: "text-center col-lg-5", data : "hours" },
					           { title	: $label.action, className: "text-center col-lg-4", data : "calls", render : function( data ){
					        	
					        	   var width = data * 100 / maxD;
					        	   return '<svg width="110" height="12"><rect x="0" y="0" height="12" width="' + width + '" style="stroke: #6aabd7; fill: #6aabd7"/>' + 
					        	   		 '</svg>';
					           } }
					           
				             
					         ],	
				
					paging: false,
					info:     false,
					searching : false 
			});
		  },
		  error: function(e){
			  core.showErrorMessage( $error.getAllAgents );
			  core.showWaitDialog( false );
		  }
	});
	
}
qdr.closeQGRPanel = function(){
	$("#QGRPanel").addClass("hidden");

}
qdr.closeQueueDetailReport = function(){
	$("#QueueDetailReport").addClass("hidden");
}
qdr.initialize = function(){
	
	$("#closeQGRPanel").hover(function() {
	    $(this).css('cursor','pointer');
	}, function() {
	    $(this).css('cursor','auto');
	});
	$("#closeQueueDetailReport").hover(function() {
	    $(this).css('cursor','pointer');
	}, function() {
	    $(this).css('cursor','auto');
	});
	
	qdr.qrpicker_from = jQuery('#qrpicker_from').datetimepicker({
		 lang: 'en',
			timepicker:true,
			  format:'d.m.Y H:i',
			  onChangeDateTime:function(dp,$input){
				  qdr.date_from = $input.val();
		      }					 
	});

	qdr.qrpicker_to = jQuery('#qrpicker_to').datetimepicker({
		 lang: 'en',
		  timepicker:true,
		  format:'d.m.Y H:i',
		  onChangeDateTime:function(dp,$input){
			  qdr.date_to = $input.val();
	    }
	});
	
	$( "#qrpicker_from" ).val( core.getCurrentDate() );
	$( "#qrpicker_to" ).val( core.getCurrentDate() );
	
	core.getAllAgents( $( "#qr_agentName" ));
	core.getAllQueues( $( "#qr_queueName" ))
}	

