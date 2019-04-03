var profile = profile || {};

profile.buildCabinet = function(){
	console.log("build cabinet");
	$("#create_dialog").removeClass("hidden");
	$.ajax({
		  type: "GET",
		  url: "buildCabinet",
		  contentType : 'application/json',
		  dataType: "html",
		  success: function(e){
			  $("#create_dialog").addClass("hidden");
			  switch(e){
			  case "CABINET_BUILDED" : 
				  core.showStatus($success.explSaved,"success");
				  break;
			  case "ERROR_CABINET_BUILDING" :
				  core.showStatus($error.explSaved,"error");
			  	  break;
			  }
		  },
		  error: function(e){
			 $("#create_dialog").addClass("hidden");
			 core.showStatus($error.explSaved,"error");
		  }
	});
}

profile.genPassword = function(){
	$("#_passwd").val(Math.random().toString(36).substr(2, 9));
}
$(document).ready(function() {
	
	var id = $("#ProfileuserID").val();
	var role = $("#userRole").val();
//	$("#tab-4-content").empty();
	
	core.initProfile( id,role ); 
	if (role=="PATIENT"){
		profile.buildCabinet();
	}


	
	
});