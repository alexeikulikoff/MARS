var patients = patients || {};


$(document).ready(function() {
	
	var UserID = $("#pexUserID").val();
	console.log(UserID);
	core.showExploration( UserID, "patient");
	
});