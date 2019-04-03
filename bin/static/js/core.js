/**
 * 
 */

var core = core || {};

core.config = {
		urlPrefix : ""
};
var $data = {
		id : 		"",
		lastname : 	"",
		firstname : "",
		surname : 	"",
		email : 	"",
		login : 	"",
		passwd : 	"",
		photo : "",
		role : ""
			
	}

var explorTable = null;
var invitationTable = null;
var contactTable=null;
var tablePayments;

var tableID= {
		"admins" 	: "tbAdmins",
		"doctors" 	: "tbDoctors",
		"patients" 	: "tbPatints"
}
var Roles = {
		"admins" 	: "ADMIN",
		"doctors" 	: "DOCTOR",
		"patients" 	: "PATIENT"	
}
var cc = {
		"admins" : { id : "tbAdmins",
					 role : "ADMIN"
					},
		"doctors" : { id : "tbDoctors",
					  role : "DOCTOR"
					},
		"patients" : { id : "tbPatients",
					  role : "PATIENT"
				 }					
}

function testInput( input ){
	var result = false;
	 if (( input.attr("type") == "text" )  && (input.attr("id").startsWith("_"))) result = true;
	return result; 
}
function setRequired( state ){
	  
	 $( "input" ).each(function( index ) {
		 if ( testInput($(this))){
			 $(this).attr("required", state );
		 }
	 });
}

/*
core.createArray = function(length) {
    var arr = new Array(length || 0),
        i = length;

    if (arguments.length > 1) {
        var args = Array.prototype.slice.call(arguments, 1);
        while(i--) arr[length-1 - i] = createArray.apply(this, args);
    }

    return arr;
}
*/

function convertUnixtimestamp( unixtimestamp ){

	 var months_arr = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
	 var date = new Date(unixtimestamp*1000);
	 var year = date.getFullYear();
	 var month = months_arr[date.getMonth()];
	 var day = date.getDate();
	 var hours = date.getHours();
	 var minutes = "0" + date.getMinutes();
	 var seconds = "0" + date.getSeconds();
	 var convdataTime = month+'-'+day+'-'+year+' '+hours + ':' + minutes.substr(-2) + ':' + seconds.substr(-2);
	 
	 return convdataTime;
	 
	}


core.showExploration = function(id, pageName ){

	$("#" + pageName + "-ExplTableContainer").empty();
	$("#" + pageName + "-ExplTableContainer").append('<table id="' + pageName + '-explorationTable" class="table"></table>');
	
	if (explorTable !=  null){
		explorTable.destroy();
	}
	core.showWaitDialog();
	$.ajax({
		type: "GET",
		url: "getExploration?id=" + id,
		dataType: "json",
		success: function(e){
			
			explorTable = $('#' + pageName + '-explorationTable')
			.on('draw.dt', function(){
				core.hideWaitDialog();
				
				$("#" + pageName + "-exDetails").removeClass("hidden");
				$("#" + pageName + "-exDetailsNum").text(e.explorationnumber);
				$("#" + pageName + "-exDetailsPatient").text(e.username);
				
			})
			.on('click', 'td.details-control', function () {
				var tr = $(this).closest('tr');
		        var row = explorTable.row( tr );
		 
		        if ( row.child.isShown() ) {
		            // This row is already open - close it
		            row.child.hide();
		            tr.removeClass('shown');
		        }
		        else {
		            // Open this row
		            row.child( format(row.data()) ).show();
		            tr.addClass('shown');
		        }
			})
			.DataTable({
				data : e.explorations,
				columns : 
					[
					  {
						  className: "details-control col-md-1",
				        	orderable: false,
				        	data: null,
				         	defaultContent: ""
			            },
						{ title	: $label.date, data : "date" , className : "col-md-3", render : function( data, type, row){
							return '<i class="fa fa-clock-o"></i>&nbsp;' + data  ;
						} },
						{ title	: $label.name, data : "explname" , render : function( data, type, row ){
							if (parseInt( row.dicomsize ) > 0){
								return data;
							}else{
								return '<button type="button" class="btn btn-danger m-r-sm">' + data + '</button>';
							}
						}},
						{  title : $profile.id, data : "id", className : "col-md-1", render : function( data, type, row ){

							var stateButton;
							if (parseInt( row.dicomsize ) > 0){
								stateButton = '<li><a href="#" onclick="explorations.downloadDicom(\'' + row.id + '\')"><i class="fa fa-download"></i><span style="padding-left: 5px;">' + $label.dicomArchive2 + '</span></a></li>' +
								'<li class="divider"></li>'+
								'<li><a href="#" onclick="explorations.showDicom(\'' + row.id + '\')"><i class="fa fa-picture-o"></i><span style="padding-left: 5px;">' + $label.showDicom + '</span></a></li>';
							
							}else{
								stateButton = '<li><a href="#" onclick="explorations.loadExploration(\'' + row.id + '\')"><i class="fa fa-download"></i><span style="padding-left: 5px;">' + $label.loadExploration + '</span></a></li>';
							} 
								
							return '<span class="hidden">' + row.id + '</span><div class="btn-group">' + 
							'<button data-toggle="dropdown" class="btn btn-primary btn-xs dropdown-toggle" aria-expanded="false"><i class="fa fa-edit"></i>' +
							'<span class="caret"></span></button>'+ 
							'<ul class="dropdown-menu pull-right">' + 
							'<li><a href="#" onclick="explorations.addConclusion(\'' + row.id + '\',\'' +  row.usersId + '\')"><i class="fa fa-desktop"></i><span style="padding-left: 5px;">' + $label.conclusion2 + '</span></a></li>' +
							stateButton + 
							'</ul>' + 
							'</div>' ; 

						} },
		
						],	
						
						"order": [[1, 'asc']],
						
						"paging"    : false,
						"info" 	 : false,
						"searching" : false, 
						"iDisplayLength" : 100,
						"scrollY" : 300
			});

		},
		error: function(e){
			core.hideWaitDialog( );
			$("#" + pageName + "-exDetails").addClass("hidden");
			$("#" + pageName + "-exDetailsNum").text("");
			$("#" + pageName + "-exDetailsPatient").text("");

		}
	});  
	if (explorTable != null) explorTable.draw();
	
}

function showDoctor ( d ) {

	var str = "";
	for(var i=0; i < d.invitations.length; i++){
		str = str + '<tr><td><div class="stat-percent font-bold text-navy"><i class="fa fa-level-up fa-rotate-90"></i></div></td>' + 
		'<td><img alt="image" class="img-circle" width="38px" height="38px"  src="data:image/jpeg;base64,' +  d.invitations[i].doctorphoto  +'" />' 
		+ '&nbsp;</td><td class="">' +  d.invitations[i].doctorwork + ' </td><td class=""><h4>' +  d.invitations[i].doctorname  + '</h4></td>' + 
		'<td class=""><p><span class="text-warning"><i class="fa fa-warning"></i></span>&nbsp;' +  d.invitations[i].comments  + '</p></td>' + 
		'<td class=""><p>Отправлено:&nbsp;<i class="fa fa-clock-o"></i>&nbsp;' +  d.invitations[i].date  + '</p></td>' + 
	    '</tr>';
	}
    return '<div class="col-lg-10 col-lg-offset-1"><table class="table">'+ str + '</table></div>';
}

core.showInvitations = function(id, pageName ){

	$("#" + pageName + "-InvTableContainer").empty();
	$("#" + pageName + "-InvTableContainer").append('<table id="' + pageName + '-invitationTable" class="table"></table>');
	
	if (invitationTable !=  null){
		invitationTable.destroy();
	}
	core.showWaitDialog();
	$.ajax({
		type: "GET",
		url: "getInvitations?id=" + id,
		dataType: "json",
		success: function(e){
			console.log( e );
			invitationTable = $('#' + pageName + '-invitationTable')
			.on('draw.dt', function(){
				core.hideWaitDialog();
			})
			.on('click', 'td.details-control', function () {
				var tr = $(this).closest('tr');
		        var row = invitationTable.row( tr );
		 
		        if ( row.child.isShown() ) {
		            // This row is already open - close it
		            row.child.hide();
		            tr.removeClass('shown');
		        }
		        else {
		            // Open this row
		            row.child( showDoctor(row.data()) ).show();
		            tr.addClass('shown');
		        }
			})
			.DataTable({
				data : e,
				columns : 
					[
						 {
							  className: "details-control",
					          orderable: false,
					        	data: null,
					         	defaultContent: ""
				         },
						{ title	: $label.name, data : "explname" },
						{ title	: $label.date, data : "date" , className : "col-md-3", render : function( data, type, row){
							return '<i class="fa fa-clock-o"></i>&nbsp;' + data  ;
						} },
						{  title : $profile.id, data : "id", className : "col-md-1", render : function( data, type, row ){
							
							return '<span class="hidden">' + row.id + '</span><div class="btn-group">' + 
							'<button data-toggle="dropdown" class="btn btn-primary btn-xs dropdown-toggle" aria-expanded="false"><i class="fa fa-edit"></i>' +
							'<span class="caret"></span></button>'+ 
							'<ul class="dropdown-menu pull-right">' + 
							'<li><a href="#" onclick="core.invite(\'' + row.id + '\',\'' +  row.usersId + '\')"><i class="fa fa fa-medkit"></i><span style="padding-left: 5px;">' + $label.invitation + '</span></a></li>' +
							'</ul>' + 
							'</div>' ; 
						} },
						],	
						"order": [[1, 'asc']],
						"paging"    : false,
						"info" 	 : false,
						"searching" : false, 
						"iDisplayLength" : 100,
						"scrollY" : 300,
						
			});

		},
		error: function(e){
			core.hideWaitDialog( );
		}
	});  
	if (invitationTable != null) invitationTable.draw();
	
}

core.invite = function(explorationID, userID){
	  $("#invitationForm").attr("style", "display: block; padding-right: 14px;");
	  $("#body").attr("style", "padding-right: 14px;");
	  $("#body").addClass("modal-open");
	  $('#body').append('<div id="fadeIdInvitation" class="modal-backdrop in"></div>');  
	  $("#btnInvite").attr("onclick","core.confirmInvitation('" + explorationID + "','" + userID + "')" );
	  
	  core.initInvitationContacts(explorationID, userID);
}
core.closeInvitationForm = function(){
	$("#fadeIdInvitation").remove();  
	$("#invitationForm").removeClass("in");
	$("#invitationForm").attr("style", "display: none;");
	$("#body").removeClass("modal-open");
	$("#body").removeAttr("style");
}
core.confirmInvitation = function(explorationID, userID){
	
	var d = contactTable.rows( {selected:true} ).data()[0];
	
	var $data={
			explorationid : explorationID,
			contactid : d.id,
			comments : $("#InvComments").val()
	}
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
	$.ajax({
		  type: "POST",
		  url:  "addInvitation",
		  data: JSON.stringify($data),
		  contentType : 'application/json',
		  dataType: "json",
		  headers : headers ,    	
		  success: function(e){
		  switch( e.message ){
			case "SUCCESS_INVITATION_SAVE" : 
			   core.showInvitations( $("#invUserID").val() , "patient" );
			   core.closeInvitationForm();
			   core.showStatus($success.invitationSaved,"success");
			   break;
		  	case "ERROR_INVITATION_SAVE" : 
			  core.closeInvitationForm();
			  core.showStatus($error.invitationSaved,"error");
			  break;
		  	}
			
		  },error : function( e) {
			  core.showStatus($error.network,"error");
			}
		});	
	
}
core.initInvitationContacts=function(explorationID, userID ){
	$("#contactTableContainer").empty();
	$("#contactTableContainer").append('<table id="contactTable" class="table"></table>');
	if (contactTable !=  null){
		contactTable.destroy();
	}
	$.ajax({
		type: "GET",
		url: "getContacts?id=" + userID,
		dataType: "json",
		success: function(e){
			console.log(e);
			contactTable = $('#contactTable')
			.on('draw.dt', function(){
				 core.hideWaitDialog();
			})
			.on('click', 'td.details-control', function () {
/*				var tr = $(this).closest('tr');
		        var row = explorTable.row( tr );
		 
		        if ( row.child.isShown() ) {
		            row.child.hide();
		            tr.removeClass('shown');
		        }
		        else {
		            row.child( format(row.data()) ).show();
		            tr.addClass('shown');
		        }
*/		        
			})
			.DataTable({
				data : e,
				 columnDefs: [ {
			            orderable: false,
			            className: 'col-sm-1 select-checkbox',
			            targets:   0
			        } ],
				columns : 
					[
						{
					        data: null,
					        defaultContent: ""
						},
						{ title	: '', data : "photo", className: "col-sm-1", orderable: false, render: function(data){
							return '<img alt="image" width="38px" height="38px" class="img-circle" src="data:image/jpeg;base64,' + data  +'" />';
						} },
						{ title	: '', data : "surname" , className : "col-md-8", render : function( data, type, row){
							return '<h3 class="m-b-xs">' + row.surname + ' ' + row.firstname + ' ' + row.lastname  +'</h3>';

						} }
					
						],	
						 select: {
					          style:    'os',
					          selector: 'td:first-child'
					     },
						"order": [[2, 'asc']],
						"paging"    : false,
						"info" 	 : false,
						"searching" : false, 
						"iDisplayLength" : 100,
						"scrollY" : 200
			});

		},
		error: function(e){
			core.hideWaitDialog( );
			

		}
	});  
	if (invitationTable != null) invitationTable.draw();

	
}

core.uploadTempImage = function( theThis, formContent, upload, userImage  ){
	var formData = new FormData($("#" + formContent)[0]);
	var file = $("#" + upload)[0].files[0];
	
	var file_size = parseInt( file.size );
	if (file_size > 1100000){
		core.showStatus($error.uploadFileSize,"error");
		return;
	}
	formData.append('file', file);
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
    $.ajax({
            url: "uploadTempImage",
            type: "POST",
            data: formData,
            mimeTypes:"multipart/form-data",
            headers : headers,    	
            contentType: false,
            cache: false,
            processData: false,	     
            success: function( data ){
            	  var src =((data.image != null) && (data.image.length != 0)) ? "data:image/jpeg;base64," + data.image : "img/mibs-empty-profile.jpg";
    		      $("#" + userImage).attr("src",src);
            	
            },error: function(e){
            	core.showStatus($error.network,"error");
            }
    }); 
	
}
core.saveUser = function(){
	if ($("#_id").val() == "-1") {
		
		core.usersCreateNew(); 
		
	}else{
		core.usersUpdateAll();
	}
}
core.usersCreateNew = function(){
	
	var flag = true;
	 $( "input" ).each(function( index ) {
		 if ( testInput($(this))){
			var val =  $(this).val();
			if(val.length == 0){
				flag = false;
				var group =  $(this).parent().parent();
				if (!group.hasClass("has-error")){
					$(this).parent().parent().addClass("has-error");
				}
			}else{
				$(this).parent().parent().removeClass("has-error");
			}
		 }
	 });
	 var editForm = $("#profileEditForm").validate({
			rules:{
				  email: {
						 required: true,
						 email: true
				  },
				  passwd : {
					  required: true,
				      minlength: 6
				  },
				  lastname: {
					  required: true,
				      minlength: 3
				  },
				  firstname: {
					  required: true,
				      minlength: 3
				  },
				  surname: {
					  required: true,
				      minlength: 3
				  }
			}
		});
	 
	 if ( !(flag && editForm.valid()) ) return;
	
	mapFormToData();
	
	$data.photo = $("#userImage").attr("src");
	$data.role = cc[core.getCurrentPageName()].role;
		
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
   $.ajax({
   	  type: "POST",
		  url:  "usersCreateNew",
		  data: JSON.stringify($data),
		  contentType : 'application/json',
		  dataType: "json",
		  headers : headers ,    	
         success: function(e){
       	 
         	  switch(e.message){
			  case "SUCCESS_USER_SAVE": 
				  core.setEditDisable( true );
				  
				  $("#btnSaveUsers").prop("disabled", true);
				  $("#btnAddUserImage").prop("disabled", true);
				  $("#btnCansel").prop("disabled", true);
				  $("#btnAddUserImage").addClass("hidden");
				  $("#upload").prop("disabled", true);
				  
				  tables[core.getCurrentPageName()].ajax.reload();
				  core.showStatus($success.userSaved,"success");
				  break;
			  case "ERROR_DUBLE_EMAIL" :
				  core.showStatus($error.duplicateEmail,"error");
				  break;
			  case "ERROR_DUBLE_LOGIN" :
				  core.showStatus($error.duplicateLogin,"error");
				  break;
				  
			 default:
			 	  core.showStatus($error.userUpdated,"error");
			  }
			  
           },error: function(e){
           	console.log(e);
           	//core.showStatus($error.network,"error");
           }
   }); 

		
}
core.usersUpdateAll=function(){

	var flag = true;
	 $( "input" ).each(function( index ) {
		 if ( testInput($(this))){
			var val =  $(this).val();
			if(val.length == 0){
				flag = false;
				var group =  $(this).parent().parent();
				if (!group.hasClass("has-error")){
					$(this).parent().parent().addClass("has-error");
				}
			}else{
				$(this).parent().parent().removeClass("has-error");
			}
		 }
	 });
	
	var editForm = $("#profileEditForm").validate({
			rules:{
				  email: {
						 required: true,
						 email: true
				  },
				  passwd : {
					  required: true,
				      minlength: 6
				  },
				  lastname: {
					  required: true,
				      minlength: 3
				  },
				  firstname: {
					  required: true,
				      minlength: 3
				  },
				  surname: {
					  required: true,
				      minlength: 3
				  }
			}
		});		
	
	 if ( !(flag && editForm.valid()) ) return;
	
	mapFormToData();
	$data.id = $("#_id").val();
	$data.photo = $("#userImage").attr("src");
	
	
	
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
    $.ajax({
    	  type: "POST",
		  url:  "usersUpdateAll",
		  data: JSON.stringify($data),
		  contentType : 'application/json',
		  dataType: "json",
		  headers : headers ,    	
          success: function(e){
        	 
          	  switch(e.message){
			  case "SUCCESS_USER_UPDATE": 

				  if (core.getCurrentPageName() != "profile") {
					  core.setEditDisable( true );
					  $("#btnSaveUsers").prop("disabled", true);
					  $("#btnAddUserImage").prop("disabled", true);
					  $("#btnCansel").prop("disabled", true);
					  $("#btnAddUserImage").addClass("hidden");
					  $("#upload").prop("disabled", true);
					  tables[core.getCurrentPageName()].ajax.reload();
				  }
				  core.showStatus($success.userUpdated,"success");
				  break;
			  case "ERROR_USER_UPDATE" :
				  core.showStatus($error.userUpdated,"error");
				  break;
			 default:
			 	  core.showStatus($error.userUpdated,"error");
			  }
			  
            },error: function(e){
            	console.log(e);
            	//core.showStatus($error.network,"error");
            }
    }); 

	
}


core.measureText = function(pText, pFontSize, pStyle) {
    
	if (pText == null) return pText;
	
	var lDiv = document.createElement('div');

    document.body.appendChild(lDiv);

    if (pStyle != null) {
        lDiv.style = pStyle;
    }
    lDiv.style.fontSize = "" + pFontSize + "px";
    lDiv.style.position = "absolute";
    lDiv.style.left = -1000;
    lDiv.style.top = -1000;

    lDiv.innerHTML = pText;

    var lResult = {
        width: lDiv.clientWidth,
        height: lDiv.clientHeight
    };

    document.body.removeChild(lDiv);
    lDiv = null;

    return lResult;
}

core.setEditDisable=function( state ){
	  
	 $( "input" ).each(function( index ) {
		 if ( testInput($(this))){
			 $(this).prop("disabled", state );
		 }
	 });
}
core.clearInputFields = function(){
	 $( "input" ).each(function( index ) {
		 if ( testInput($(this))){
			 $(this).val("");
		 }
	 });
}
function mapDataToForm(e){

	$( "input" ).each(function( index ) {
		if ( testInput($(this))){
		 var id = $( this ).attr("id");
		 var prop = id.substring(1,id.length);
		 $("#" + id).val(e[prop]);
		}
	 });
	
	 var src = (e["photo"] != null) ? "data:image/jpeg;base64," + e["photo"] : "img/mibs-empty-profile.jpg";
	 $("#userImage").attr("src", src);
}
function mapFormToData(){
	
	$( "input" ).each(function( index ) {
		if ( testInput($(this))){
		 var id = $( this ).attr("id");
		 var prop = id.substring(1,id.length);
		 $data[prop] =  $("#" + id).val();
		 
		}
	 });
}

core.initDataTable = function( pageName ){

	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
	
	var data ={};
	var tb =  $("#" + cc[pageName].id)
			 .on('preXhr.dt', function ( e, settings, data ) {
				 core.showWaitDialog();
		    } )
		    .on('xhr.dt', function ( e, settings, json, xhr ) {
		    	 core.hideWaitDialog();
		    	  
          })
          .on( 'init.dt', function () {
        	   
        	  var data = tb.rows().data()[0];
		      mapDataToForm( data );
		      var src =((data.photo != null) && (data.photo.length != 0)) ? "data:image/jpeg;base64," + data.photo : "img/mibs-empty-profile.jpg";
		      $("#userImage").attr("src",src);
		      $("#nUserTitleID").text(data.surname +' ' + data.firstname + ' ' + data.lastname);
		      $("#nPaymentTitleID").text(data.surname +' ' + data.firstname + ' ' + data.lastname);
		   
		    
		      core.setTabs();
		      
           } )
		   .DataTable({
		 "language": {
			  "processing": "Подождите...",
			  "search": '<i class="fa fa-search fa-rotate-90"></i>',
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
				"url" : "getUsers?role=" + cc[pageName].role, 
	       		"type": "GET",
	            "dataSrc": "data",
	            "error": function(e){
	            
	            	console.log("data loading...");
	            	}
				},
		columns : [
			 {  title : $profile.id, data : "id", render : function( data, type, row ){
				 return "<span class='label label-info'>" + row.id +"</span>"; 
			 } },
			 {  title : $profile.fio, data : "lastname", render : function( data, type, row ){
				 return row.surname + ' ' + row.firstname + ' ' + row.lastname ;
			 }	},	
			 {  title : $profile.email, data : "email"	},	
			 //{  title : $profile.login, data : "login" },
		
			 {  title : $profile.action,  /*className: "text-center", */ data : "id",  render: function(data,type,row){
				 
				 var actionUpdate='UPDATE';
				 var actionDrop='DROP';
				 var actionShow='SHOW';
				 
				 return '<div class="btn-group">' + 
                 		'<button id="actionBtn-"'+ row.id + '"  data-toggle="dropdown" class="btn btn-primary btn-xs dropdown-toggle " aria-expanded="false"><i class="fa fa-edit"></i>' 
                 			+ '<span class="caret"></span></button>' 
	                 		+ '<ul class="dropdown-menu pull-right">' + 
	                 			'<li><a href="#" onclick="core.doAction(\'' + row.id + '\',\'' + actionShow + '\',\'' + cc[pageName].id +  '\')"><i class="fa fa-folder-open-o"></i><span style="padding-left: 5px;">' + $label.show + '</span></a></li>' +
	                 			'<li class="divider"></li>'+
	                 			'<li><a href="#" onclick="core.doAction(\'' + row.id + '\',\'' + actionUpdate + '\',\'' + cc[pageName].id +  '\')"><i class="fa fa-edit"></i><span style="padding-left: 5px;">' + $button.change + '</span></a></li>' +
	                 			'<li><a href="#" onclick="core.doAction(\'' + row.id + '\',\'' + actionDrop + '\',\'' + cc[pageName].id + '\')"><i class="fa fa-ban"></i><span style="padding-left: 5px;">' + $button.drop + '</span></a></li>' +
	                 		 '</ul>' + 
	                 		'</div>' ;
				
			 }	}	
			],
		 iDisplayLength : 100,
		 searching : true,
		 info :     false,
		 paging :  false,
		 scrollY: 600
		 
	 });

	return tb;
}
core.setFirstRow = function( table ){
	  
	var data = table.rows().data()[0];
    mapDataToForm( data );
    var src =((data.photo != null) && (data.photo.length != 0)) ? "data:image/jpeg;base64," + data.photo : "img/mibs-empty-profile.jpg";
    $("#userImage").attr("src",src);
}
core.setTabs = function(){
	 var page = core.getCurrentPageName();
	 if (page == "patients"){
		if (!$("#li-tab3").hasClass("active")){
			$("#li-tab3").addClass("active");
			$("#li-tab4").removeClass("active");
		}
		if (!$("#tab3").hasClass("active")){
			$("#tab3").addClass("active");
			$("#tab4").removeClass("active");
		}
	 }else{
			$("#tab4").addClass("hidden");
			$("#li-tab4").addClass("hidden");
	 }
}
core.setTabs2 = function( role ){
	
	 if (role == "PATIENT"){
		 $("#tab4").removeClass("active");
		 $("#tab4").addClass("hidden");
	 }
/*	 
	 if (role == "PATIENT"){
		if (!$("#li-tab3").hasClass("active")){
			$("#li-tab3").addClass("active");
			$("#li-tab4").removeClass("active");
		}
		if (!$("#tab3").hasClass("active")){
			$("#tab3").addClass("active");
			$("#tab4").removeClass("active");
		}
	 }else{
			$("#tab4").addClass("hidden");
			$("#li-tab4").addClass("hidden");
	 }
*/	 
}
core.initPaymentsTable = function( dataSrc ){
	$("#tablePaymentsContainer").empty();
	$("#tablePaymentsContainer").append('<table id="tablePayments" class="table"></table>');
	
	tablePayments =  $("#tablePayments")
	 
	 	.on( 'init.dt', function () {
	   
	 			//var data = tb.rows().data()[0];
     
	 	} )
	 	.DataTable({
	 		"language": {
			  "processing": "Подождите...",
			  "search": '<i class="fa fa-search fa-rotate-90"></i>',
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
		data : dataSrc.payments,
		columns : [
				 {  title : "#",  data : "id", render: function( data ){
					 return '<span class="label label-info">' + data + '</span>';
				 } },
				 {  title : $label.paidDate, data : "paiddate",render : function( data, type, row){
						return '<i class="fa fa-clock-o"></i>&nbsp;' + data  ;
					} 	
				 },	
				 {  title : $label.sum, className: "text-center", data : "paidsum" ,render: function( data ){
					 return '<div class="font-bold text-info"><i class="fa fa fa-rub"></i><span style="padding-left: 5px;">' + data + '</style></div>';
				 } },
				 {  title : $label.comments, data : "comments" },
				
				 {  title : $label.paidtill, data : "paidtill", render : function( data, type, row){
						return '<i class="fa fa-clock-o"></i>&nbsp;' + data  ;
					} 
				 }	
				
			],
		iDisplayLength : 100,
		searching : false,
		info :     false,
		paging :  false,
		scrollY: 200
		
		});
	
	tablePayments.draw();
	
	
}
core.doAction = function( id, action, tableId ){
	$.ajax({
		  type: "GET",
		  url: "getUser?id=" + id,
		  contentType : 'application/json',
		  dataType: "json",
		  success: function(e){
			 mapDataToForm( e );
			 $('#_id').val( id );
			 $("#nUserTitleID").text(e.surname +' ' + e.firstname + ' ' + e.lastname);
			 
			 core.setTabs();
			
			 switch(action){
				 case "DROP" :  
					 $("#btnDropUsers").removeAttr("disabled");
					 $("#btnCancel").removeAttr("disabled");
					 break;
				 case "UPDATE" : 
					 core.setEditDisable( false );
					 $("#btnSaveUsers").removeAttr("disabled");
					 $("#btnAddUserImage").removeAttr("disabled");
					 $("#btnAddUserImage").removeClass("hidden");
					 $("#btnCancel").removeAttr("disabled");
					 $("#upload").removeAttr("disabled");
					 break;
				 case "SHOW" :
					 core.setEditDisable( true );
					 core.initPaymentsTable( e );
					 
					 break;
				 }
		  },
		  error : function(e) {
			  core.showStatus($error.network,"error");
			 
			}
		});	
}

core.initProfile = function( id, role ){
	
	$("#_id").val( id );
	
	$.ajax({
		  type: "GET",
		  url: "getUser?id=" + id,
		  contentType : 'application/json',
		  dataType: "json",
		  success: function(e){
		
			 mapDataToForm( e );
			 core.setEditDisable( false );
			 
			 
			if (role == "PATIENT"){
				 //core.initPaymentsTable( e );		
				 
				 var form=$('<img src="img/yandex-payment.png" width="423" height="224" />') ;
				// $("#tab-4-content").append( form );
				 
				 $("#_email").prop("disabled", true);
			 }
 		 
			 $("#btnSaveUsers").removeAttr("disabled");
			 $("#btnAddUserImage").removeAttr("disabled");
			 $("#btnAddUserImage").removeClass("hidden");
			 $("#btnCancel").addClass("hidden");
			 $("#btnDropUsers").addClass("hidden");
			 $("#upload").removeAttr("disabled");
			 
		  },
		  error : function(e) {
			  core.showStatus($error.network,"error");
			 
			}
		});	
}


core.isNumber = function(n) {
  return !isNaN(parseFloat(n)) && isFinite(n);
}


core.triggerUploadForm = function(trigger, id){
	switch(trigger){
		case "hide" : $("#" + id).addClass("hidden"); break;
		case "show" : $("#" + id).removeClass("hidden"); break;
	}
}
core.getCurrentPageName = function(){
	var thisUrl = window.location.href;
	thisUrl = thisUrl.replace('#','').replace('?','');
	return thisUrl.substring(thisUrl.lastIndexOf("/") + 1, thisUrl.length);
}
core.showStatus = function(msg, type){
	   setTimeout(function() {
        toastr.options = {
     		  "closeButton": false,
  			  "debug": false,
  			  "newestOnTop": false,
  			  "progressBar": false,
  			  "positionClass": "toast-top-right",
  			  "preventDuplicates": false,
  			  "onclick": null,
  			  "showDuration": "300",
  			  "hideDuration": "1000",
  			  "timeOut": "5000",
  			  "extendedTimeOut": "1000",
  			  "showEasing": "swing",
  			  "hideEasing": "linear",
  			  "showMethod": "fadeIn",
  			  "hideMethod": "fadeOut"
        };
        toastr[type](msg);
      
        
        if ((type == "error") && (msg == $error.network)){
        	  setTimeout(function(){
             	 location.href = "/mars/login";
             },1300);
        }
      
       
    }, 1300);
}

core.getCurrentDate = function(){
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth()+1; //January is 0!

	var yyyy = today.getFullYear();
	if(dd<10) {
	    dd='0'+dd
	} 
	if(mm<10) {
	    mm='0'+mm
	} 
	today = dd + '.'+ mm + '.' + yyyy ;
	return today;
}
core.getFirstDayOfMonth = function(){
	var today = new Date();
	var dd = "01";
	var mm = today.getMonth()+1; //January is 0!
	var yyyy = today.getFullYear();
	if(mm<10) {
	    mm='0'+mm
	} 
	today = dd + '.'+ mm + '.' + yyyy ;
	return today;
}
core.gcsrf = function(){
	var self = this;
	self.csrfToken = ko.computed(function() {
		return JSON.parse($.ajax({
			type: 'GET',
			url: 'csrf',
			dataType: 'json',
			success: function() { },
			data: {},
			async: false,
			error: function(e){
				location.href = "/mars/login";
			}
		}).responseText);
	}, this);
	return self.csrfToken();
}
core.hideWaitDialog = function(){
	$("#wait_dialog").addClass("hidden");
}
core.showWaitDialog = function(){
	$("#wait_dialog").removeClass("hidden");
}

core.init = function(){
	 $.ajax({
		  type: "GET",
		  url: "getAppConfig",		
		  contentType : "application/json",
		  dataType: "json",
		  processData : false,
		  success : function(e){
			   core.config.urlPrefix = e.urlPrefix;
			   return "INIT_SUCCESS";
			   
		  },
		   error : function(e){
			   return "INIT_ERROR";
		 }
});			

}