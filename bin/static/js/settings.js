/**
 * 
 */

var settings = settings || {};

var table = null;
var settingsTable = null;

settings.summary = function(){
	
	$.ajax({
		  type: "GET",
		  url: "getSettings",
		  dataType: "json",
		  success: function(e){
			
			  $("#numTotal").text(e.numTotal);
			  $("#numConnected").text(e.numConnected);
			  $("#numDisconnected").text(e.numDisconnected);
		
		  },
		  error: function(e){
			  core.showStatus($error.network,"error");
		  }
	});
	
	
}
settings.init = function(){

	var utc = new Date().toJSON().slice(0,10).replace(/-/g,'/');
	$("#curDate").text( utc );
	
	
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
	table =  $("#settingsTable")
			 .on('preXhr.dt', function ( e, settings, data ) {
				 core.showWaitDialog();
		    } )
		    .on('xhr.dt', function ( e, settings, json, xhr ) {
		    	 core.hideWaitDialog();
		    	 
		    	
		    	
          } )
		   .DataTable({
		 "language": {
			  "processing": "Подождите...",
			  "search": "F:",
			  "lengthMenu": "Показать _MENU_ записей",
			  "info": "Записи с _START_ до _END_ из _TOTAL_ записей",
			  "infoEmpty": "Записи с 0 до 0 из 0 записей",
			  "infoFiltered": "(отфильтровано из _MAX_ записей)",
			  "infoPostFix": "",
			  "loadingRecords": "Загрузка записей...",
			  "zeroRecords": "Записи отсутствуют.",
			  "emptyTable": "В таблице отсутствуют данные",
			  "paginate": {
			    "first": "Первая",
			    "previous": "Предыдущая",
			    "next": "Следующая",
			    "last": "Последняя"
			  },
			  "aria": {
			    "sortAscending": ": активировать для сортировки столбца по возрастанию",
			    "sortDescending": ": активировать для сортировки столбца по убыванию"
			  },
		 },
		 "ajax": {
				"url" : "getSettings", 
	       		"type": "GET",
	            "dataSrc": "rp",
	            "error": function(e){
	            
	            	console.log("data loading...");
	            	}
				},
		columns : [
			{  title : $label.action, data : "id", render : function( data, type, row ){
				 
				 return '<div class="btn-group">' + 
          		'<button data-toggle="dropdown" class="btn btn-primary btn-xs dropdown-toggle" aria-expanded="false">'  + '<i class="fa fa-edit"></i>' +    
          			'<span class="caret"></span></button>' 
              		+ '<ul class="dropdown-menu">' + 
              			'<li><a href="#" onclick="settings.change(\'' + row.id + '\')"><i class="fa fa-edit"></i><span style="padding-left: 5px;">' + $button.change + '</span></a></li>' +
              			'<li><a href="#" onclick="settings.del(\'' + row.id + '\')"><i class="fa fa-dropbox"></i><span style="padding-left: 5px;">' + $button.drop + '</span></a></li>' +
              			'<li class="divider"></li>'+
              			'<li><a href="#" onclick="settings.testPathConnection(\'' + row.id + '\')"><i class="fa fa-link"></i><span style="padding-left: 5px;">' + $button.testLink + '</span></a></li>' +
              		 '</ul>' + 
              		'</div>' ; 
				 
				 
			 } },
			 {  title : $profile.id, data : "id" },
			 {  title : $label.department, data : "department" },
			 {  title : $label.code	, data : "uniqueid" },
			 {  title : $label.ipaddress, data : "ipaddress" },
			
			 {  title : $label.dirname, data : "dirname" },
			 {  title : $profile.login, data : "login" },
			 {  title : $profile.password, data : "passwd" },
			 {  title : $label.status, data : "checked",  render : function(data){
				var html; 
				var d = parseInt( data );
				switch( d ){
				case 1 : html =  '<div class="text-navy"><i class="fa fa-check-square"></i></div>';
						   break;
				case 0 : html =  '<div class="text-danger"><i class="fa fa-ban"></i></div>';
						  break;
				}
				return html;
				 
			 } }
			
	
			],
		
		 "iDisplayLength" : 100,
		 "info" :     false,
		 "paging" :  false,
		 "scrollY": 600,
		 "searching": false
		 //"order": [[ 0, "asc" ]]
		 
	 });
}

settings.testPathConnection = function(id){
	core.showWaitDialog();
	
	$.ajax({
		  type: "GET",
		  url: "testPathConnection?id=" + id,
		  dataType: "json",
		  success: function(e){
			  table.ajax.reload();
		
		  },
		  error: function(e){
			  core.showStatus($error.network,"error");
		  }
	});
	core.hideWaitDialog();	
}
settings.del = function(id){
	$("#wornBeforeDropPath").addClass("in");
	$("#wornBeforeDropPath").attr("style", "display: block; padding-right: 14px;");
	$("#body").addClass("modal-open");
	$("#body").attr("style", "padding-right: 14px;");
	$('#body').append('<div id="fadeId" class="modal-backdrop in"></div>');    
	$.ajax({
		  type: "GET",
		  url: "getSettingsByID?id=" + id,
		  dataType: "json",
		  success: function(e){
			
			  $("#wornId").text(e.id);
			  $("#wornDepartnemt").text(e.department);
			  $("#wornIpaddress").text(e.ipaddress);
			  $("#wornDirname").text(e.dirname);
			  $("#wornId2").val(e.id);
		
		  },
		  error: function(e){
			  core.showStatus($error.network,"error");
		  }
	});
}
settings.closeWorning = function(){
	$('#fadeId').remove();  
	$("#wornBeforeDropPath").removeClass("in");
	$("#wornBeforeDropPath").attr("style", "display: none;");
	$("#body").removeClass("modal-open");
	$("#body").removeAttr("style");
}
settings.change = function(id){
	core.showWaitDialog();
	
	$.ajax({
		  type: "GET",
		  url: "getSettingsByID?id=" + id,
		  dataType: "json",
		  success: function(e){
			  $("#nSetId").removeClass("hidden");
			  $("#settingsId").val(e.id);
			  $("#departmentTxt").val(e.department);
			  $("#ipaddressTxt").val(e.ipaddress);
			  $("#dirnameTxt").val(e.dirname);
			  $("#loginTxt").val(e.login);
			  $("#passwdTxt").val(e.passwd);
			  $("#uniquePathId").val(e.uniqueid);
		
		  },
		  error: function(e){
			  core.showStatus($error.network,"error");
		  }
	});
	core.hideWaitDialog();
}
settings.updatePath = function(){
	core.showWaitDialog();
	var data = {
			id :   		 $("#settingsId").val(),
			department:  $("#departmentTxt").val(),
			ipaddress:   $("#ipaddressTxt").val(),
			dirname: 	 $("#dirnameTxt").val(),
			login :   	 $("#loginTxt").val(),
			passwd: 	 $("#passwdTxt").val(),
			uniqueid: 	 $("#uniquePathId").val()
	};
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
	$.ajax({
		  type: "POST",
		  url:  "updateRemotePath",
		  data: JSON.stringify(data),
		  contentType : 'application/json',
		  dataType: "json",
		  headers : headers ,    	
		  success: function(e){
			  core.hideWaitDialog();
			  switch (e.message){
			  	case "SUCCESS_REMOUTE_PATH_UPDATE" : 
			  	  table.ajax.reload();
			  	  core.showStatus($success.pathUpdated,"success");
			  	  settings.hideNSetId();
			  	  break;
			  	case "ERROR_REMOUTE_PATH_UPDATE" : 
				  	  core.showStatus($error.pathUpdated,"success");
				  	  break;
			  }
		  },
		  error: function(e){
			  core.hideWaitDialog();
			  core.showStatus($error.network,"error");
		  }
	});
}
function testEmptyFields(){
	var newDepartment = $("#newDepartment").val();
	if (newDepartment.length == 0) {
		$("#newDepartment").addClass("has-error");
		return false;
	}else{
		$("#newDepartment").removeClass("has-error");
		
	}
	return true;
}
settings.saveRemotePath = function(){
	
	testEmptyFields();
	
	core.showWaitDialog();
	var data = {
			id :   		 "",
			department:  $("#newDepartment").val(),
			ipaddress:   $("#newIpaddress").val(),
			dirname: 	 $("#newDirname").val(),
			login :   	 $("#newLogin").val(),
			passwd: 	 $("#newPassword").val(),
			uniqueid:	 $("#newUniqueId").val()
	};
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
/*	$.ajax({
		  type: "POST",
		  url:  "saveRemotePath",
		  data: JSON.stringify(data),
		  contentType : 'application/json',
		  dataType: "json",
		  headers : headers ,    	
		  success: function(e){
			  core.hideWaitDialog();
			  switch (e.message){
			  	case "SUCCESS_REMOUTE_PATH_SAVE" : 
			  	  table.ajax.reload();
			  	  settings.closeWorning();
			  	  core.showStatus($success.pathUpdated,"success");
			  	  break;
			  	case "ERROR_REMOUTE_PATH_SAVE" : 
				  	  core.showStatus($error.pathUpdated,"success");
				  	  break;
			  }
		  },
		  error: function(e){
			  core.hideWaitDialog();
			  core.showStatus($error.network,"error");
		  }
	});
*/	
}

settings.dropRemotePath = function(){
	core.showWaitDialog();
	var data = {
			id :   		 $("#wornId2").val(),
			department:  "",
			ipaddress:   "",
			dirname: 	 "",
			login :   	 "",
			passwd: 	 ""	
	};
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
	$.ajax({
		  type: "POST",
		  url:  "dropRemotePath",
		  data: JSON.stringify(data),
		  contentType : 'application/json',
		  dataType: "json",
		  headers : headers ,    	
		  success: function(e){
			  core.hideWaitDialog();
			  switch (e.message){
			  	case "SUCCESS_REMOUTE_PATH_DELETE" : 
			  	  settings.closeWorning();
			  	  table.ajax.reload();
			  	  core.showStatus($success.pathDeleted,"success");
			  	 
			  	  break;
			  	case "ERROR_REMOUTE_PATH_DELETE" : 
				  	  core.showStatus($error.pathUpdated,"success");
				  	  break;
			  }
		  },
		  error: function(e){
			  core.hideWaitDialog();
			  core.showStatus($error.network,"error");
		  }
	});	
}
settings.hideNSetId = function(){
	  $("#nSetId").addClass("hidden");
	  $("#settingsId").val(""),
	  $("#departmentTxt").val("");
	  $("#ipaddressTxt").val("");
	  $("#dirnameTxt").val("");
	  $("#loginTxt").val("");
	  $("#passwdTxt").val("");
	  $("#uniquePathId").val("");

}
$(document).ready(function() {
	
	$("#settingWarningDelClose").click( function(){
		settings.closeWorning();
	});
	$("#settingWarningDelete").click( function(){
		settings.dropRemotePath();
	});
	$("#btnSettingSavePath").click( function(){
		settings.saveRemotePath();
	});
	
	$("#btnaddNewPath").click( function(){
		$("#addNewPath").removeAttr("style");
	});
    settings.init();
    
    settings.summary();
	
});