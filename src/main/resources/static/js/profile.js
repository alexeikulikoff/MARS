var profile = profile || {};

profile.action = {
    'CABINET_BUILDED' : function(){
        core.showStatus($success.explSaved,"success");  
    },
    'ERROR_CABINET_BUILDING' : function(){
          core.showStatus($error.explSaved,"error");
    },
    'SUCCESS_EXPLORATION_SAVE' : function(){
        core.showStatus($success.explSaved,"success");  
    },
    'ERROR_EXPLORATION_SAVE' : function(){
        core.showStatus($error.explSaved,"error");
    }
}
profile.buildCabinet = function(){
	$("#create_dialog").removeClass("hidden");
	$.ajax({
		  type: "GET",
		  url: "buildCabinet",
		  contentType : 'application/json',
		  dataType: "json",
		  success: function(e){
			  $("#create_dialog").addClass("hidden");
			  profile.action[e.message](); 
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