
var main = main || {};

$(document).ready(function() {
	console.log("ini main");
	$("#changePasswordForm").hide();
	
	$("#changePasswordButton").click( function(){
		$("#changePasswordForm").show();
		$("#changePasswordButton").hide();
	});
	$("#savePassword").click( function(){
		
		var passwd1 = $("#new_password1").val();
		var passwd2 = $("#new_password2").val();
		if (passwd1 != passwd2){
			
		}
		
		$("#changePasswordForm").hide();
		$("#changePasswordButton").show();
	});
	
	if ($("#profilImage").attr("src").includes("mibs-empty-profile.jpg") ){
		$("#doRemove").hide();
		$("#addImage").show();
	}else{
		$("#doRemove").show();
		$("#addImage").hide();
	}
	
	main.triggerUpload("hide");
	var email =  $("#email").val();
	main.checkEmailStatus( email );


	
	$("#upload").change(function() {
		main.triggerUpload("show");
		$(".fileinput-filename").trigger("select");
	});
	
	$("#verifyMailYes").click( function(){
		console.log("click verifyMailYes");
		var value = $( "#email" ).val();
		main.saveMail( value );
	});
	
	$("#editEmail").click( function(){
		$("#email" ).prop( "disabled", false );
		$("#editSpan").addClass("hidden");
		$("#saveSpan").removeClass("hidden");
	});	
	$("#saveEmail").click( function(){
		$("#email" ).prop( "disabled", true );
		$("#editSpan").removeClass("hidden");
		$("#saveSpan").addClass("hidden");
		core.showWaitDialog();
		var id = $("#profileid").val();
		var email =  $("#email").val();
		main.updateMail( id , email);
		core.hideWaitDialog();
	});	
	$("#saveFace").click( function() {
		var v = $("#fileId").val();
		console.log(v);
	});
	
	$("#doRemove").click( function(){
		main.removeImage($("#email").val());
	});
	
	$("#doUpload").click( function(){
	
		var email = $("#email").val();
		var formData = new FormData($('#formContent')[0]);
		var file = $("#upload")[0].files[0];
		
		formData.append('file', file);
		formData.append('email', email);
		
		var headers = {};
		headers[core.gcsrf().headerName] = core.gcsrf().token;
	    $.ajax({
	            url: "saveProfileImage",
	            type: "POST",
	            data: formData,
	            mimeTypes:"multipart/form-data",
	            headers : headers,    	
	            contentType: false,
	            cache: false,
	            processData: false,	     
	            success: function(e){
	            	switch(e.message){
	            	case "ERROR_IMAGE_SAVE" :  main.showStatus($error.imageSave,"error"); break; 
	            	case "SUCCESS_IMAGE_SAVE" : 
	            		main.updateImage( email );
	            		main.showStatus($success.imageSave,"success"); 
	            		main.triggerUpload("hide");
	            		main.checkFileLength();
	            		break; 
	            	}
	              
	            },error: function(e){
	            	main.showStatus($error.network,"error");
	            }
	    }); 
	});
});
main.checkFileLength = function(){
	console.log($(".fileinput-filename").html());
	console.log($(".fileinput-filename").text());
}
main.triggerUpload = function(trigger){
	switch(trigger){
		case "hide" : $("#doUpload").addClass("hidden"); break;
		case "show" : $("#doUpload").removeClass("hidden"); break;
	}
}
main.showStatus = function(msg, type){
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
           toastr[type](msg)
       }, 1300);
}
main.removeImage = function( email ){
	 $.ajax({
		  type: "GET",
		  url: "removeImage?email=" + email,		
		  contentType : "application/json",
		  dataType: "json",
		  processData : false,
		  success : function(e){
			  switch(e.message){
			  case "ERROR_IMAGE_REMOVE"   :  main.showStatus($error.imageRemove,"error"); break;
			  case "SUCCESS_IMAGE_REMOVE" : 
				  main.updateImage(email);
				  main.showStatus($success.imageRemove,"success");
				  $("#doRemove").hide();
				  $("#addImage").show();
				  break;
			  }
		  },
		  error : function(e){
			  main.showStatus($error.network,"error");
			}
		});				
	
}
main.updateImage = function( email ){
	
	 core.showWaitDialog();
	
	 $.ajax({
		  type: "GET",
		  url: "updateImage?email=" + email,		
		  contentType : "application/json",
		  dataType: "json",
		  processData : false,
		  success : function(e){
			 var ss ;
			if (e.image == null){
				ss = "img/mibs-empty-profile.jpg";
				 $("#doRemove").hide();
				 $("#addImage").show();
			}else{
				 ss = "data:image/jpeg;base64," + e.image;
				 $("#doRemove").show();
				 $("#addImage").hide();
			}
			
			$("#profilImage").attr("src", ss);
			$("#topProfImg").attr("src", ss);

			$("#updateImage").show();
			 core.hideWaitDialog();
		  },
		  error : function(e){
				console.log( "error");
				console.log( e );
			}
		});			
}  
main.checkEmailStatus = function( email ){

	$.ajax({
		  type: "GET",
		  url: "getEmailStatus?email=" + email,		
		  contentType : 'application/json',
		  dataType: "json",
		  success: function(e){
			  switch(e.message){
			  case "EMAIL_IS_CONFIRMED": 
				  $("#emailConfirmationBox").addClass("hidden");
				  console.log("EMAIL_IS_CONFIRMED");
				  break;
			  case "EMAIL_IS_NOT_CONFIRMED":
				  $("#emailConfirmationBox").removeClass("hidden");
				  console.log("EMAIL_IS_NOT_CONFIRMED");
				  break;
			 default:
// ERROR_EMAIL_NOT_EXIST				 
				 console.log("ERROR_EMAIL_NOT_EXIST") ;
			  }
			
		  },
		  error : function(e) {
				console.log( e);
			}
		});		
}

main.updateMail = function( id, email  ) {

	var data={ 
			id : id,
			email : email
	   }
	
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
	
	$.ajax({
		  type: "POST",
		  url:  "updateEmail",
		  data: JSON.stringify(data),
		  contentType : 'application/json',
		  dataType: "json",
		  headers : headers ,    	
		  success: function(e){
			  switch(e.message){
			  case "QUERY_STATUS_FINE": 
				  console.log("QUERY_STATUS_FINE");
				  window.location.href = 'login.html';
				  break;
			  case "ERROR_UPDATE_EMAIL":
				  console.log("ERROR_UPDATE_EMAIL");
				  break;
			 default:
				 console.log("ERROR_PROFILE_NOT_FOUND") ;
			  }
		  },
		  error : function(e) {
			  console.log(e);
		}
	});	
}
main.saveMail = function( value ) {

	var data={ 
			email : value
	   }
	
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
	
	$.ajax({
		  type: "POST",
		  url:  "checkMail",
		  data: JSON.stringify(data),
		  contentType : 'application/json',
		  dataType: "json",
		  headers : headers ,    	
		  success: function(e){
			 console.log(e);
		  },
		  error : function(e) {
			  console.log(e);
		}
	});	
}
