/**
 * 
 */

var users = users || {};

var $div_modal_backdrop = null;
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

var editForm = null;
var validator = null;

var tables = {};
function showFileInputForm(){
	$("#myModal_100").addClass("in");
	$("#myModal_100").attr("style","display: block;");
}
function hideFileInputForm(){
	$("#myModal_100").removeClass("in");
	$("#myModal_100").attr("style","display: none;");
}
users.newUser = function(){
	core.setEditDisable( false );
	core.clearInputFields();
	$("#userImage").attr("src","img/mibs-empty-profile.jpg");
	$("#btnAddUserImage").removeClass("hidden");
	$("#btnAddUserImage").removeAttr("disabled");
	$("#upload").removeAttr("disabled");
	$("#btnSaveUsers").removeAttr("disabled");
	$("#btnCancel").removeAttr("disabled");
	
	$("#_id").val("-1");
	
	$("#btnCansel").removeAttr("disabled");
}
users.dropUser  = function( ){

	mapFormToData();
	$data.id = $("#_id").val();
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
	$.ajax({
		  type: "POST",
		  url:  "dropUser",
		  data: JSON.stringify($data),
		  contentType : 'application/json',
		  dataType: "json",
		  headers : headers ,    	
		  success: function(e){
			  switch(e.message){
			  case "QUERY_STATUS_FINE": 
				  $("#btnDropUsers").prop("disabled", true);
				  $("#btnCansel").prop("disabled", true);
				  tables[core.getCurrentPageName()].ajax.reload();
				  core.showStatus($success.userDroped,"success");
				  core.setFirstRow(  tables[core.getCurrentPageName()] );
				
				  break;
			  case "ERROR_USER_DROP":
				  core.showStatus($error.userdrop,"error");
				  break;
			 default:
				  users.closeEditPatients();
			 	  core.showStatus($error.userdrop,"error");
			  }
		  },
		  error : function(e) {
			  core.showStatus($error.network,"error");
		}
	});	
	
}
users.uploadTempImage = function( v ){
	var formData = new FormData($("#formContent")[0]);
	var file = $("#upload")[0].files[0];
	
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
    		      $("#userImage").attr("src",src);
            	
            },error: function(e){
            	core.showStatus($error.network,"error");
            }
    }); 
	
}
users.closeEditPatients = function(){
	
	$("#fadeDiv").remove();
	$("#body").removeClass("modal-open");
	$("#body").attr("style","");
	$("#editPatients").removeClass("in");
	$("#editPatients").attr("style","none;");
	$div_modal_backdrop.remove();
}

users.setEditMode = function(){
	 if ($("#btnSaveProfile").hasClass("hidden") &&  !$("#btnEditProfile").hasClass("hidden")){
		  $("#btnEditProfile").addClass("hidden");
		  $("#btnSaveProfile").removeClass("hidden");
	 }
	 core.setEditDisable( false );
}

users.saveDropCancel = function(){
	 core.setEditDisable( true );
	 $("#btnDropUsers").prop("disabled", true);
	 $("#btnSaveUsers").prop("disabled", true);
	 $("#btnAddUserImage").prop("disabled", true);
	 $("#btnCancel").prop("disabled", true);
	 $("#btnAddUserImage").addClass("hidden");
	 $("#upload").prop("disabled", true);
	 
}

users.confirmPayment = function(){
	$("#payment").removeClass("in");
	$("#payment").attr("style","display: none;");
	$("#body").removeClass("modal-open");
	$("#body").attr("style","");
	
	$(".modal-backdrop").remove();
	
	
	if (($("#paymentSumId").val().length == 0) || !core.isNumber( $("#paymentSumId").val() ) ){
		  core.showStatus($error.sumisnull,"error");
		  return;
	}
	if ($("#paymentCommentsId").val().length == 0){
		  core.showStatus($error.scommentsnull,"error");
		  return;
	}
	
	var $data = {
			email 		: $("#_email").val(),
			sum   		: $("#paymentSumId").val(),
			comments 	: $("#paymentCommentsId").val(),
			period 		: $('select[name="paymentPeriod"]').val()
		}
	
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
    $.ajax({
    	  type: "POST",
		  url:  "addPayment",
		  data: JSON.stringify($data),
		  contentType : 'application/json',
		  dataType: "json",
		  headers : headers ,    	
          success: function(e){
        
          	  switch(e.id){
			  case null: 
				  core.showStatus($error.userUpdated,"error");
				  break;
			 default:
				  console.log(e);
				  core.initPaymentsTable( e );
			  	  core.showStatus($success.paymentSaved,"success");
			  	  $("#paymentSumId").val("");
			  	  $("#paymentCommentsId").val("");
			  }
			  
            },error: function(e){
            	  core.showStatus($error.network,"error");
            	
            }
     }); 
	
}
$(document).ready(function() {

	 var thisPage = core.getCurrentPageName();
	 var table = core.initDataTable( thisPage );
	 tables[thisPage] =  table;
	
	 core.setEditDisable( true );

	 $('[data-toggle="tooltip"]').tooltip();

	 jQuery.validator.setDefaults({
		  debug: true,
		  success: "valid"
		});

/*
	 editForm = $("#profileEditForm").validate({
			rules:{
				  email: {
						 required: true,
						 email: true
				  },
				  login: {
				      required: true,
				      minlength: 6

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
	
*/

	
	$("#uploadUserImageBtn").click(function(){
	
		$(".modal-backdrop").each(function(index, obj) {
		
			var i = parseInt( index );
			if (i == 1) $(obj).remove();
			
		});
		var formData = new FormData($('#formUserImage')[0]);
		var file = $("#userUpload")[0].files[0];
		
		//core.triggerUploadForm("hide", "myModal_100");
		hideFileInputForm();
		$("#pictureName").text($label.fileToUpload + " " + file.name);
	});

  $("#removeImageBtn").click( function(){
	  $("#userImage").attr("src","img/mibs-empty-profile.jpg" );
	  $("#removeImageBtn").attr("style","display: none;");
	  $("#addUserBtn").attr("style","display: block;");
	  
	  $("#userUpload")[0].files[0] = null;
  });

  
 
	
});