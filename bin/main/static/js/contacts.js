
var contacts = contacts || {};

var $data = {
		email : 	"",
		lastname : 	"",
		firstname : "",
		surname : 	"",
		childwork : 	"",
		childpost : 	"",
		childphone : "",
		photo : ""
	}

contacts.init = function(){
	$("#contactContainer").empty();
	 $.ajax({
		  type: "GET",
		  url: "getContacts",		
		  contentType : "application/json",
		  dataType: "json",
		  processData : false,
		  success : function( e ){
			  e.forEach(function(element) {
				  
				  var str = (parseInt(element.accessed) == 0) ? '<span class="label label-danger">Доступ закрыт</span>' : '<span class="label label-primary">Доступ открыт</span>';
				 $('<div class="col-lg-3"><div class="contact-box center-version">'+
						 '<div class="col-lg-1" style="padding-top: 15px;" >' + str + '</div>' + 
						 '<a href="profile.html">' + 
						 '<img alt="image" class="img-circle" src="data:image/jpeg;base64,' + element.photo  +'" />' +
						 ' <h3 class="m-b-xs"><strong>' +  element.surname  + '  ' + element.firstname + '  ' + element.lastname +  '</strong></h3>' +
						 '<div class="font-bold">' + element.childpost + '</div>' + 
						 '<address class="m-t-md">' + 
						 '<strong>' + element.childwork + '</strong><br /><abbr title="Phone">T:</abbr>' + element.childphone +  
						 '<br />' + element.email + 
						 '</address></a>' + 
						 '<div class="contact-box-footer">' +
						 '<div class="m-t-xs btn-group">' +
		//				 '<a class="btn btn-xs btn-white"  onclick="contacts.invitation(\'' + element.id + '\')"><i class="fa fa-bell-o"></i>' +
		//				 '<span style="padding-left: 5px;">' + $button.invitation + '</span></a>'+
						 '<a class="btn btn-xs btn-white"  onclick="contacts.access(\'' + element.id + '\')"><i class="fa fa-lock"></i>'+
						 '<span style="padding-left: 5px;">' + $button.access + '</span></a>' + 
						 '<a class="btn btn-xs btn-white" onclick="contacts.hide(\'' + element.id + '\')"><i class="fa fa-window-close"></i>'+
						 '<span style="padding-left: 5px;">' + $button.hide + '</span></a>' + 
						 '</div></div>' +
						 '</div></div>').appendTo("#contactContainer");
		  	});
		  },
		  error : function(e){
			  console.log(e);
		  }
	 });	
}
contacts.access= function(id){
	console.log(id);
	 $.ajax({
		  type: "GET",
		  url: "changeaccess?id=" + id,		
		  contentType : "application/json",
		  dataType: "json",
		  processData : false,
		  success : function( e ){
			  contacts.init();
		  },
		  error : function(e){
			  core.showStatus($error.accessChanged,"error");
		  }
	 });	  
	
}
contacts.invitation= function(id){
	console.log(id);
}
contacts.hide = function(id){
	 $.ajax({
		  type: "GET",
		  url: "hideContact?id=" + id,		
		  contentType : "application/json",
		  dataType: "json",
		  processData : false,
		  success : function( e ){
			  contacts.init();
		  },
		  error : function(e){
			  core.showStatus($error.accessChanged,"error");
		  }
	 });	  
}
function clearAll(){
	$("#email").val("");
	$("#lastname").val("");
	$("#firstname").val("");
	$("#surname").val("");
	$("#childwork").val("");
	$("#childpost").val("");
	$("#childphone").val("");
	$("#userImage_Cont").attr("src","img/mibs-empty-profile.jpg");
}
$(document).ready(function() {
	
	contacts.init();
	$("#form").steps(
			{
				labels: {
			        cancel: $button.cancel,
			        current: $label.currentstep,	
			        pagination: $label.Pagination,
			        finish: $label.Finish,
			        next: $label.Next,
			        previous: $label.Previous,
			        loading: $label.Loading	
				},
				bodyTag : "fieldset",
				enableCancelButton: true,
				onCanceled : function(event){
					$("#newContactId").addClass("hidden");
				},
				onStepChanging : function(event,currentIndex, newIndex) {
					// Always allow going backward even
					// if the current step contains
					// invalid fields!
				
					if (currentIndex > newIndex) {
						return true;
					}

					
					// Forbid suppressing "Warning" step
					// if the user is to young
					if (newIndex === 3 && Number($("#age").val()) < 18) {
						return false;
					}

					var form = $(this);

					// Clean up if user went backward
					// before
					if (currentIndex < newIndex) {
						// To remove error styles
						$(".body:eq("+ newIndex+ ") label.error",form).remove();
						$(".body:eq(" + newIndex + ") .error", form).removeClass("error");
					}

					// Disable validation on fields that
					// are disabled or hidden.
					form.validate().settings.ignore = ":disabled,:hidden";

					// Start validation; Prevent going
					// forward if false
					return form.valid();
				},
				onStepChanged : function(event, currentIndex, priorIndex) {
					
					
					// Suppress (skip) "Warning" step if
					// the user is old enough.
					if (currentIndex === 1 ){
						
						 $.ajax({
							  type: "GET",
							  url: "getContactByEmail?email=" + $("#email").val(),		
							  contentType : "application/json",
							  dataType: "json",
							  processData : false,
							  success : function( e ){
								  console.log(e);
								  switch(e.state){
								  case "EXIST" :
									  $("#firstname").val( e.firstname );
									  $("#firstname").prop("disabled", true);
									  $("#lastname").val( e.lastname );
									  $("#lastname").prop("disabled", true);
									  $("#surname").val( e.surname );
									  $("#surname").prop("disabled", true);
									  $("#childwork").val(e.childwork);
									  $("#childwork").prop("disabled", true);
									  $("#childpost").val(e.childpost);
									  $("#childpost").prop("disabled", true);
									  $("#childphone").val(e.childphone);
									  $("#childphone").prop("disabled", true);
									  $("#userImage_Cont").attr("src","data:image/jpeg;base64," + e.photo ); 
									//  $("#userImage_Cont").attr("src","img/mibs-empty-profile.jpg");
									  $("#btnAddUserImage_Cont").addClass("hidden");
									  break;
								  case "NOT_EXIST" :
									  $("#firstname").removeAttr('disabled');
									  $("#lastname").removeAttr('disabled');
									  $("#surname").removeAttr('disabled');
									  $("#childwork").removeAttr('disabled');
									  $("#childpost").removeAttr('disabled');
									  $("#childphone").removeAttr('disabled');
									  $("#userImage_Cont").attr("src","img/mibs-empty-profile.jpg" );
									  $("#btnAddUserImage_Cont").removeClass("hidden");
									  break;
								  }
								 
							  },
							  error : function(e){
								  $("#newContactId").addClass("hidden");
				        	  	  core.showStatus($error.contactCreated,"error");
							  }
						 });
					}

					// Suppress (skip) "Warning" step if
					// the user is old enough and wants
					// to the previous step.
					if (currentIndex === 2 && priorIndex === 3) {
						$(this).steps("previous");
					}
				},
				onFinishing : function(event,currentIndex) {
					var form = $(this);

					// Disable validation on fields that
					// are disabled.
					// At this point it's recommended to
					// do an overall check (mean
					// ignoring only disabled fields)
					form.validate().settings.ignore = ":disabled";

					// Start validation; Prevent form
					// submission if false
					return form.valid();
				},
				onFinished : function(event, currentIndex) {
					var form = $(this);

					$data.email 	 = $("#email").val();
					$data.lastname 	 = $("#lastname").val();
					$data.firstname  = $("#firstname").val();
					$data.surname 	 = $("#surname").val();
					$data.childwork  = $("#childwork").val();
					$data.childpost  = $("#childpost").val();
					$data.childphone = $("#childphone").val();
					$data.photo 	= $("#userImage_Cont").attr("src");
		
				   var headers = {};
				   headers[core.gcsrf().headerName] = core.gcsrf().token;
				   $.ajax({
				   	  type: "POST",
						  url:  "createNewContact",
						  data: JSON.stringify($data),
						  contentType : 'application/json',
						  dataType: "json",
						  headers : headers ,    	
				          success: function(e){
				        	  switch(e.message){
				        	  	case "SUCCESS_CONTACT_CREATE" : 
				        	  			$("#newContactId").addClass("hidden");
				        	  			contacts.init();
				        	  			clearAll();
				        	  		    core.showStatus($success.contactCreated,"success");
				        	  		    break;
				        		case "ERROR_CONTACT_CREATE" : 
			        	  			$("#newContactId").addClass("hidden");
			        	  		    core.showStatus($error.contactCreated,"error");
			        	  		    break;
				        	  }
				        	
				         },
				         error : function(e){
				        	 $("#newContactId").addClass("hidden");
		        	  		 core.showStatus($error.contactCreated,"error");
				         }
					});  	 
				}
			}).validate({
				errorPlacement : function(error, element) {
					element.before(error);
				},
				rules : {
					confirm : {
						equalTo : "#password"
					}
				}
			
			});

	$("#newContactForm").click(function() {

		$("#newContactId").removeClass("hidden");
		clearAll();

	});
	 $("#removeImageBtn_Cont").click( function(){
		  $("#userImage_Cont").attr("src","img/mibs-empty-profile.jpg" );
		  $("#removeImageBtn_Cont").attr("style","display: none;");
		 // $("#addUserBtn").attr("style","display: block;");
		  
		//  $("#userUpload_Cont")[0].files[0] = null;
	});
});
