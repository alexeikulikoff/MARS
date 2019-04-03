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
			  console.log(e);
			  $("#create_dialog").addClass("hidden");
		  },
		  
		  error: function(e){
			 console.log(e);
		  }
	});
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