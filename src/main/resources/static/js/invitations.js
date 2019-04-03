var invitations = invitations || {};

$(document).ready(function() {
	var id = $("#invUserID").val();
	
	core.showInvitations(id, "patient" );
	
	
});