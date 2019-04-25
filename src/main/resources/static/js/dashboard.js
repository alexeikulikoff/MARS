/**
 * 
 */

var dashboard = dashboard || {};

var table = null;
var journalTable = null;

dashboard.action = {
		"CABINET_BUILDED" : function(){
		   $("#dashboard_dialog").addClass("hidden");
			core.showStatus($success.explUpdate,"success");
			dashboard.showJournalTable();
		 },
		"SUCCESS_EXPLORATION_SAVE" : function(){
		   $("#dashboard_dialog").addClass("hidden");
			core.showStatus($success.explUpdate,"success");
			dashboard.showJournalTable();
		 },
		"ERROR_CABINET_BUILDING" : function(){
		   $("#dashboard_dialog").addClass("hidden");
			core.showStatus($error.explUpdate,"error");
		 },
		"ERROR_EXPLORATION_SAVE"  : function(){
			$("#dashboard_dialog").addClass("hidden");
			core.showStatus($error.explUpdate,"error");
        },
        "ERROR_CABINET_REBUILD"  : function(){
			$("#dashboard_dialog").addClass("hidden");
			core.showStatus($error.explUpdate,"error");
        },
        "CABINET_REBUILDED"  : function(){
			$("#dashboard_dialog").addClass("hidden");
            core.showStatus($success.explUpdate,"success");
            dashboard.showJournalTable();
	    }
}




dashboard.init = function(){
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
	table =  $("#journalTable")
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
				"url" : "getJournalTable", 
	       		"type": "GET",
	            "dataSrc": "",
	            "error": function(e){
	            
	            	console.log("data loading...");
	            	}
				},
		columns : [
			 {  title : "id", data : "id" },
			 {  title : "Date", data : "date" },
			 {  title : "UniqueID", data : "uniqueid" },
			 {  title : "Name", data : "explname" },
			 {  title : "DicomSize", data : "dicomSize" },
			 {  title : "Dicomname", data : "dicomname" },
			 {  title : "RemotePath", data : "remotepath" },
			 {  title : "login", data : "users.email" },
			 {  title : "Surname", data : "users.surname" },
			 {  title : "FirstName", data : "users.firstname" },
			 {  title : "lastname", data : "users.lastname" }
	
			],
		 "iDisplayLength" : 100,
		 "searching" : false,
		 "info" :     false,
		 "paging" :  false,
		 "scrollY": 600
	 });

}
dashboard.picker_from = {};
dashboard.picker_to = {};

dashboard.showJournalTable = function(){
	
	$("#journalTableContainer").empty();
	$("#journalTableContainer").append('<table id="journalTable" class="table"></table>');
	
	
	if (journalTable !=  null){
		journalTable.destroy();
	}
	//core.showWaitDialog();
	
	var d1 = $("#picker_from").val();
	var d2 = $("#picker_to").val();
	
	$.ajax({
		type: "GET",
		url: "showJournalTable?d1=" + d1 + "&d2=" + d2,
		dataType: "json",
		success: function(value){
				 journalTable =  $("#journalTable")
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
				data : value,
				columns : [
					 {  title : $profile.id	, data : "id" },
					 {  title : $label.date	, data : "date" },
					 {  title : "ID", data : "uniqueid" },
					 {  title : $label.name, data : "explname" },
					 {  title : "DicomSize", data : "dicomSize" },
					 {  title : $label.name, data : "dicomname" },
					 {  title : $profile.email, data : "users.email" },
					 {  title : $profile.surname	, data : "users.surname" , render: function(data, type, row ){
						 return row.users.surname + " " + row.users.firstname + " " + row.users.lastname;
					 } },
					 {  title : $label.path, data : "remotepath" },
					 {  title : $label.action, data : "id", render:function(data, type, row ){
						 var btn;
						 if (row.dicomSize == "0"){
							 btn =  $button.build;
							 return '<p><a class="btn  btn-danger btn-bitbucket" onclick="dashboard.loadExploration(\'' + row.id + '\')"><i class="fa fa-user-md"></i></a>' + 
							 '<a class="btn btn-warning btn-bitbucket" onclick="dashboard.editNetPath(\'' + row.id + '\')"><i class="fa fa-exchange"></i></a></p>';
							 
							// return '<button type="button" class="btn btn-danger btn-sm" onclick="dashboard.loadExploration(\'' + row.id + '\')">' +  btn + '</button>';
						 }else{
							 btn =  $button.rebuild;
							// return '<button type="button" class="btn btn-primary btn-sm" onclick="dashboard.rebuild(\'' + row.id + '\')">' +  btn + '</button>';
							 return '<p><a class="btn btn-primary btn-bitbucket" onclick="dashboard.rebuild(\'' + row.id + '\')"><i class="fa fa-wrench"></i></a>' + 
							 '<a class="btn btn-warning btn-bitbucket" onclick="dashboard.editNetPath(\'' + row.id + '\')"><i class="fa fa-exchange"></i></a></p>';
							 
						 }
					   }
					 }
					],
				 "iDisplayLength" : 100,
				 "searching" : false,
				 "info" :     false,
				 "paging" :  false,
				 "scrollY": 600
			 });
		},
		error: function(e){
			console.log(e);
			 core.hideWaitDialog();
		}
	});	
			
}
dashboard.editNetPath = function(id){
	
	$.ajax({
		  type: "GET",
		  url: "editNetPath?id=" + id,
		  contentType : 'application/json',
		  dataType: "html",
		  success: function(e){
			  switch(e){
			  case "PATH_NOT_FOUND":
				  core.showStatus($error.explUpdate,"error");
				  break;
			default:
				  $("#netPath").val(e);
				  $("#netPathId").val(id);	
			  	
			  }	  
		  },
		  error: function(e){
			 
			  core.showStatus($error.explUpdate,"error");
		  }
	});
}
dashboard.updateNetPath = function(){
	
	var $data={
			id :  $("#netPathId").val(),	
			path : $("#netPath").val()
	}
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
	$.ajax({
		  type: "POST",
		  url:  "updateNetPath",
		  data: JSON.stringify($data),
		  contentType : 'application/json',
		  dataType: "html",
		  headers : headers ,    	
		  success: function(e){
		  switch( e ){
			case "SUCCESS_PATH_UPDATE" : 
				dashboard.showJournalTable();
				core.showStatus($success.pathUpdate,"success");
			   break;
		  	case "ERROR_PATH_UPDATE" : 
			
			  core.showStatus($error.pathUpdate,"error");
			  break;
		  	}
			
		  },error : function( e) {
			  console.log(e);
			 // core.showStatus($error.network,"error");
			}
		});	
	
}
dashboard.rebuild = function( id ){

	$("#dashboard_dialog").removeClass("hidden");
	$.ajax({
		  type: "GET",
		  url: "rebuild?id=" + id,
		  contentType : 'application/json',
		  dataType: "json",
		  success: function(e){
		        dashboard.action[e.message]();
		  },
		  
		  error: function(e){
			
			  $("#dashboard_dialog").addClass("hidden");
			  core.showStatus($error.explSaved,"error");
		  }
	});
}
dashboard.loadExploration = function( id ){

	$("#dashboard_dialog").removeClass("hidden");
	$.ajax({
		  type: "GET",
		  url: "loadExploration?id=" + id,
		  contentType : 'application/json',
		  dataType: "json",
		  success: function(e){
            dashboard.action[e.message]();
		  },
		  error: function(e){
			  $("#dashboard_dialog").addClass("hidden");
			  core.showStatus($error.explSaved,"error");
		  }
	});
}
$(document).ready(function() {
	
	core.init();
		
	//dashboard.init();
	
	dashboard.picker_from = jQuery('#picker_from').datetimepicker({
		  	 lang: 'ru',
			  timepicker:false,
			  format:'d.m.Y',
			  onChangeDateTime:function(dp,$input){
				  dashboard.date_from = $input.val();
		      }					 
	});

	dashboard.picker_to = jQuery('#picker_to').datetimepicker({
		  lang: 'ru',
		  timepicker:false,
		  format:'d.m.Y',
		  onChangeDateTime:function(dp,$input){
			  dashboard.date_to = $input.val();
	    }
	});
	$( "#picker_from" ).val( core.getFirstDayOfMonth() );
	$( "#picker_to" ).val( core.getCurrentDate() );
	
	dashboard.showJournalTable();
	
});