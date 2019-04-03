/**
 * 
 */

var explorations = explorations || {};

var table = null;
var explorTable = null;
var dicomSeriesTable = null;

var Width;
var Height;

var scrollImages = new Array();

var Images =  {};
var Image = {};

var cs;
var ci;

var imageGroup;
var BottomRulerlength;
var PixelSpacing;
var zoomer = 0;
var bottomRulerMaxValue = new Array();

var documentHeight;
var svg;
explorations.init = function(){

	//explorations.showExploration
	
	$("#exDetails").addClass("hidden");
	$("#exDetailsNum").text("");
	$("#exDetailsPatient").text("");
	
	var initId;
	
	var userRole= $("#userRole").val();
	
	var URL = $("#userRole").val() == "ADMIN" ? "getUsers?role=PATIENT" : "getUsersByContacts?id=" + $("#userID").val();
	
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
	table =  $("#tbPatients")
			 .on('preXhr.dt', function ( e, settings, data ) {
				 core.showWaitDialog();
		    } )
		    .on('xhr.dt', function ( e, settings, json, xhr ) {
		    	
		    	 core.hideWaitDialog();
          })
          .on( 'init.dt', function () {
        	  var row = table.rows().data()[0];
        	  console.log(row);
        	  core.showExploration(row.id, "admin");
          })
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
				"url" : URL, 
	       		"type": "GET",
	            "dataSrc": "data",
	            "error": function(e){
	            
	            	console.log("data loading...");
	            	}
				},
		columns : [
			 {  title : "#", data : "id", render: function( data ){
				 return '<span class="label label-info">' + data + '</span>';
			 }  },
			 {  title : $profile.fio, data : "lastname", render : function( data, type, row ){
				 return  row.surname + ' ' + row.firstname + ' ' + row.lastname ;
			 }	},
			 {  title : $label.action, data : "id", render : function( data, type, row ){
				 initId = row.id;
				 var admin='admin';
				 
				 var str = userRole == "ADMIN" ? '<li><a href="#" onclick="explorations.newExploration(\'' + row.id + '\',\'' + row.surname + '\',\'' + row.firstname + '\',\'' + row.lastname + '\')"><i class="fa fa-plus-square"></i><span style="padding-left: 5px;">' + $button.addExploration + '</span></a></li>' : ''; 
				 return '<div class="btn-group">' + 
          		'<button data-toggle="dropdown" class="btn btn-primary btn-xs dropdown-toggle" aria-expanded="false"><i class="fa fa-edit"></i>' +  
          			'<span class="caret"></span></button>' 
              		+ '<ul class="dropdown-menu pull-right">' + 
              			'<li><a href="#" onclick="core.showExploration(\'' + row.id +'\',\''  + admin  +  '\')"><i class="fa fa-stethoscope"></i><span style="padding-left: 5px;">' + $button.showExploration + '</span></a></li>' +
              			
              			 str + 
              			
              		 '</ul>' + 
              		'</div>' ; 
			 } }
			],
		 "iDisplayLength" : 100,
		 "searching" : true,
		 "info" :     false,
		 "paging" :  false,
		 "scrollY": 600
		 //"order": [[ 0, "asc" ]]
		 
	 });
}

explorations.loadExploration = function( id ){
	console.log("load Exploration");
	$("#loadExploration_dialog").removeClass("hidden");
	$.ajax({
		  type: "GET",
		  url: "loadExploration?id=" + id,
		  contentType : 'application/json',
		  dataType: "html",
		  success: function(e){
			  switch(e.message){
			  case "CABINET_BUILDED": 
				  $("#loadExploration_dialog").addClass("hidden");
				  core.showStatus($success.explSaved,"success");
				  break;
			  case "ERROR_CABINET_BUILDING":
				  $("#loadExploration_dialog").addClass("hidden");
				  core.showStatus($error.explSaved,"error");
				  break;
			default:
				  $("#loadExploration_dialog").addClass("hidden");
				  core.showStatus($error.explSaved,"error");
			  }	  
		  },
		  
		  error: function(e){
			  $("#loadExploration_dialog").addClass("hidden");
			  core.showStatus($error.explSaved,"error");
		  }
	});
}
explorations.saveExplorationViaNetwork = function(){
	var data = {
		name : $("#exNameID_tab2").val(),
		path : $("#exNetPath").val(),
		username : $("#exUserID").val(),
		password : $("#exPasswdID").val(),
		userid : $("#userID").val()
	};
	core.showWaitDialog();
	
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
	$.ajax({
		  type: "POST",
		  url:  "saveExplorationViaNetwork",
		  data: JSON.stringify(data),
		  contentType : 'application/json',
		  dataType: "json",
		  headers : headers ,    	
		  success: function(e){
		
			  switch(e.message){
			  case "SUCCESS_EXPLORATION_SAVE": 
				  $("input").each(function( index ) {
					  $( this ).val("");
				  });
				  core.showStatus($success.explSaved,"success");
				  break;
			  case "ERROR_EXPLORATION_SAVE":
				  core.showStatus($error.explSaved,"error");
				  break;
			 default:
				  users.closeEditPatients();
			 	  core.showStatus($error.explSaved,"error");
			  }
			  core.hideWaitDialog();
		  },
		  error : function(e) {
			  core.hideWaitDialog();
			  core.showStatus($error.network,"error");
		}
	});	
	
	
}
explorations.hideExplorationForm = function(){
	$("#exDetails").addClass("hidden");
}
explorations.hideNExp = function(){
	
	if ($("#nExpId").hasClass("hidden")) return;
	
	$("#nExpId").addClass("hidden");
}

explorations.newExploration = function(id, fname, lname, sname){
	
	$("#nExpId").removeClass("hidden");
	$("#nExTitleID").text( fname + " " + lname + " " + sname);
	
	$("#uploadDicomNameID").removeAttr("disabled");
	$("#userID").val(id);
	
}
explorations.uploadDicom = function( value ){
	
	$("#uploadDicomNameID").val(value.substring(value.lastIndexOf("\\") + 1, value.length));
	$("#uploadDicomNameID").prop("disabled", true);
}
explorations.uploadConclusion = function(value){
	
	$("#uploadConclusionText").val(value.substring(value.lastIndexOf("\\") + 1, value.length));
	$("#uploadConclusionText").prop("disabled", true);
}
function format ( d ) {

	var str = "";
	for(var i=0; i < d.conclusions.length; i++){
		str = str + '<tr><td><div class="stat-percent font-bold text-navy"><i class="fa fa-level-up fa-rotate-90"></i></div></td>' + 
		'<td>' + $label.app + '&nbsp;</td><td class="col-lg-8">' + d.conclusions[i].filename  + '</td>' + 
		'<td><button class="btn  btn-primary btn-xs" onclick="explorations.downloadConclusion(\'' + d.conclusions[i].id + '\')">' + 
		'<i class="fa fa-save"></i><span style="padding-left: 5px;">' + $button.download + '</span>' +
		'</button></td></tr>';
	}
    return '<div class="col-lg-10 col-lg-offset-2"><table class="table">'+ str + '</table></div>';
}

function addRowConclusion( id, usersId ){
	return '<form role="form" class="form-horizontal" id="formAddConclusionForm">' + 
		'<div class="form-group">' + 
    		'<div class="row" style="margin-left: -5px">' + 
    		 '<div class="col-sm-6">' + 
    		  '<div class="input-group">'+
    		
    		   '<input type="text" id="uploadConclusionText" class="form-control" style="width:100%;" placeholder="'+$label.loadFile +'"/>' +
    		   '<span class="input-group-btn">' + 
    		   '<label for="inpAddConclusion" type="button" id="btnAddConclusion" class="btn btn-primary">' + 
			   '<i class="fa fa-folder-open"></i></label>' +
			   '</span>' + 
			    '<input type="file" id="inpAddConclusion" class="form-control" style="display: none" oninput="explorations.beforeAddConclusion(this.value)" />' +
			 '</div></div>' +  
			 '<div class="col-sm-4"><div class="btn-group">' + 
			'<button id="btnSaveExpl" class="btn btn-primary" stype="padding-left:15px;" onclick="explorations.saveConclusion(\'' + id + '\',\'' + usersId +  '\')">' + 
				'<i class="fa fa-save"></i><span style="padding-left: 5px;">' +  $button.save + '</span></button></div></div></div></div></form>';
}

explorations.beforeAddConclusion = function(value){	
	$("#uploadConclusionText").val(value.substring(value.lastIndexOf("\\") + 1, value.length));
	$("#uploadConclusionText").prop("disabled", true);
	
}
explorations.saveConclusion = function(id, usersId){
	
	var formData = new FormData($("#formAddConclusionForm")[0]);
	var conclusion = $("#inpAddConclusion")[0].files[0];
	formData.append('conclusion', conclusion);
	formData.append('id', id);
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
    $.ajax({
            url: "saveConclusion",
            type: "POST",
            data: formData,
            mimeTypes:"multipart/form-data",
            headers : headers,    	
            contentType: false,
            cache: false,
            processData: false,	     
            success: function( e ){
           	  switch(e.message){
   			  case "SUCCESS_CONCLUSION_SAVE": 
   				     core.showExploration( usersId, "admin" );
   				  	  $("#uploadConclusionText").val("");
   				  	  core.hideWaitDialog();    				 
    				  core.showStatus($success.conclusionSaved,"success");
    				
    				  break;
   			  case "ERROR_CONCLUSION_SAVE":
    				  core.showStatus($error.conclusionSaved,"error");
    				  core.hideWaitDialog();
    				  break;
    			 default:
    				 core.showStatus($error.conclusionSaved,"error");
   			  }
            	
            },error: function(e){
            	 core.hideWaitDialog();
    			core.showStatus($error.network,"error");
            }
    }); 
	
}
explorations.addConclusion = function( id, usersId ){

	explorTable.rows().eq(0).each(function(index) {
           if (explorTable.row(index).data().id == id) {
        	     var row = explorTable.row( index );
        	     console.log( row );
        	     row.child( addRowConclusion( id, usersId ) ).show();
           }
	});       
   	
}
explorations.showDicom = function( id ){
	if ( dicomSeriesTable != null){
		dicomSeriesTable.destroy();
	}
	var dataSet = [];
	 $.ajax({
		  type: "GET",
		  url: "showDicom?id=" + id,		
		  contentType : "application/json",
		  dataType: "json",
		  processData : false,
		  success : function( e ){
			  $("#hdinfo").text( e.explorationName );
			  Object.assign(Images,e);
			  cs = Images.series[0].seriaNum;
			  ci  = parseInt(Images.series[0].instance[0].instNum);
			  Image = getImage( cs );
			  var height = explorations.initViewer( id );
			  for(var i=0; i < e.series.length; i++){
				  dataSet.push({seriaNum: e.series[i].seriaNum, image :e.series[i].instance[0].serializedDicom.image, seriaSize : e.series[i].instance.length  });
			  }
 			  dicomSeriesTable = $('#dicomSeriesTable')
					.on('draw.dt', function(){
						//core.showWaitDialog(false);
					})
					.DataTable({
					 	data : dataSet,
					 	columns : [
						             { data: "image", className: "text-center", render : function( data, type, row, meta ){
						            	 return '<button class="btn btn-info  dim btn-large-dim btn-outline" type="button" onclick="explorations.showSeria(' + row.seriaNum + ')"><img src="data:image/jpeg;base64,' + data + '" width="65" height="65" class="img-rounded"></img></button>' +
						            	 '<p><span class="label label-primary"> ' + row.seriaNum + '</span>&nbsp;<span class="label label-warning-light">' + row.seriaSize + '</span></p>';
						             } }
						         ],	
						  columnDefs: [
						        	    { title: '<span class="label label-primary">SERIES</span><span class="label label-warning-light">&nbsp;IMAGES</span>',
						        	      targets: [ 0 ] }
						        	  ],         
					
						paging: false,
						info:     false,
						searching : false ,
						scrollY : height,
						  "createdRow": function(row, data, index) {
		                      //$(row).addClass(dispositionClass(data));
		                  },
		                  "rowCallback": function(row, data, index) {
		                      //$(row).addClass(dispositionClass(data));
		                  }
						});
		  },
		  error : function(e){
			  core.showStatus($error.network,"error");
		}
	});	
}


explorations.downloadDicom  = function(id){
	 core.showWaitDialog();
	 $.ajax({
		  type: "GET",
		  url: "getDicomFileName?id=" + id,		
		  contentType : "application/x-www-form-urlencoded; charset=UTF-8",
		  dataType: "text",
		  processData : false,
		  success : function(file){
			  if( file == "NULL" ){
				  core.hideWaitDialog();
				  core.showStatus($error.network,"error");
				  return
			  }  
			  var url = core.config.urlPrefix + '/dicom/' + id;
			  var pom = document.createElement('a');
			  pom.setAttribute('href', url);
			  pom.setAttribute('download', file);
			  if (document.createEvent) {
				  var event = document.createEvent('MouseEvents');
				  event.initEvent('click', true, true);
				  pom.dispatchEvent(event);
			  }
			  else {
				  pom.click();
			  }  
			  core.hideWaitDialog();
		  },
		  error : function(e){
			  core.hideWaitDialog();
			  core.showStatus($error.network,"error");
			}
		});	
		
}
explorations.downloadConclusion = function(id){

	 core.showWaitDialog();
	 $.ajax({
		  type: "GET",
		  url: "getCinclusionFileName?id=" + id,		
		  contentType : "application/x-www-form-urlencoded; charset=UTF-8",
		  dataType: "text",
		  processData : false,
		  success : function(file){
			  if (file == "NULL") {
				  core.hideWaitDialog();
				  core.showStatus($error.network,"error");
				  return
			  }
			    var url = core.config.urlPrefix + '/conclusion/' + id;
			    var pom = document.createElement('a');
			    pom.setAttribute('href', url);
			    pom.setAttribute('download', file);
			    if (document.createEvent) {
			        var event = document.createEvent('MouseEvents');
			        event.initEvent('click', true, true);
			        pom.dispatchEvent(event);
			    }
			    else {
			        pom.click();
			    }  
			  core.hideWaitDialog();
		  },
		  error : function(e){
			  core.hideWaitDialog();
			  core.showStatus($error.network,"error");
			}
		});	
	
}
function clearDicomEdit(){
	$("#uploadDicomText").val("");
	$("#uploadDicomText").removeAttr("disabled");
}
function clearConclusionEdit(){
	$("#uploadConclusionText").val("");
	$("#uploadConclusionText").removeAttr("disabled");
}
explorations.uploadXML = function( value ){
	$("#uploadXMLFile").val(value.substring(value.lastIndexOf("\\") + 1, value.length));
	$("#uploadXMLFile").prop("disabled", true);
}
explorations.uploadXMLFile = function(){
	var formData = new FormData($("#formContent")[0]);
	var fls = $("#uploadXML")[0].files[0];
	formData.append('fls', fls);
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
    $.ajax({
            url: "uploadXML2",
            type: "POST",
            data: formData,
            mimeTypes:"multipart/form-data",
            headers : headers,    	
            contentType: false,
            cache: false,
            processData: false,	     
            success: function( e ){
            
            	console.log(e);
            	
            },error: function(e){
            	console.log(e);
            }
    }); 
	
}
explorations.saveExploration = function(){
	
	var formData = new FormData($("#formSaveExploration")[0]);
	var uploadDicom = $("#uploadDicom")[0].files[0];

	if ((uploadDicom.name.endsWith("zip") != true) ) {
		clearDicomEdit();
		core.showStatus($error.filetypeDIC,"error");
		return;
	}
	var explorationName = $("#explorationNameID").val();

	formData.append('uploadDicom', uploadDicom);
	formData.append('explorationName', explorationName);
	formData.append('userid', $("#userID").val());
	
	core.showWaitDialog();
	
	var headers = {};
	headers[core.gcsrf().headerName] = core.gcsrf().token;
    $.ajax({
            url: "uploadExploration",
            type: "POST",
            data: formData,
            mimeTypes:"multipart/form-data",
            headers : headers,    	
            contentType: false,
            cache: false,
            processData: false,	     
            success: function( data ){
            	switch(data.message){
            		case "SUCCESS_DICOM_SAVE" : 
            			core.showStatus($success.explSaved,"success");
            			clearConclusionEdit();
            			clearDicomEdit();
            			
            		default:
            			core.hideWaitDialog();
            	}
            },error: function(e){
            	core.showStatus($error.explSaved,"error");
            }
    }); 
}

function dicomInfo( e, s, i){
	 var result = {
			 Image						: e.series[s].instance[i].serializedDicom.image,
			 Seria 						: e.series[s].instance[i].serializedDicom.seria,
			 Instance 					: e.series[s].instance[i].serializedDicom.instance,
			 PatientName 				: e.series[s].instance[i].serializedDicom.patientName,
			 PatientBirthDate 			: e.series[s].instance[i].serializedDicom.patientBirthDate,
			 PatientID 					: e.series[s].instance[i].serializedDicom.patientID,
			 PatientAge 				: e.series[s].instance[i].serializedDicom.patientAge,
			 PatientWeight 				: e.series[s].instance[i].serializedDicom.patientWeight,
			 PatientSex					: e.series[s].instance[i].serializedDicom.patientSex,
			 StudyDate 					: e.series[s].instance[i].serializedDicom.studyDate,
			 StudyDescription 			: e.series[s].instance[i].serializedDicom.studyDescription,
			 StudyComment 				: e.series[s].instance[i].serializedDicom.studyComment,
			 PerformingPhysicianName 	: e.series[s].instance[i].serializedDicom.performingPhysicianName,
			 SliceLocation 				: e.series[s].instance[i].serializedDicom.sliceLocation,
			 SliceThickness 			: e.series[s].instance[i].serializedDicom.sliceThickness,
			 Rows 						: e.series[s].instance[i].serializedDicom.rows,
			 Columns 					: e.series[s].instance[i].serializedDicom.columns,
			 PixelSpacing 				: e.series[s].instance[i].serializedDicom.pixelSpacing,
			 ManufactoreModelName 		: e.series[s].instance[i].serializedDicom.manufactoreModelName
	  }
	
	 return result;
}
explorations.initViewer = function( id ){
	  $("#modalViewer").attr("style", "display: block; padding-right: 14px;");
	  $("#body").attr("style", "padding-right: 14px;");
	  $("#body").addClass("modal-open");
	  $('#body').append('<div id="fadeIdDicom" class="modal-backdrop in"></div>');  
	  $("#btnDownloadDicom").attr("onclick", "explorations.downloadDicom('" + id +"')");
	 return explorations.updateSeriesView();
}	

var index = 0;
var circles = [];
var lineIndex = 0;
explorations.updateSeriesView = function(  ){	
	
	  Image = getImage( cs );

	  var row = parseInt( Image.instance[0].serializedDicom.rows );
	  var col = parseInt( Image.instance[0].serializedDicom.columns );
	 // var k = col/row;
	  var k = row/col;
	  //var iw = parseInt( $("#imageFrame").width() );
	  //var ih =  k * iw;
	  
	  ih = documentHeight;
	  iw = ih / k ;
	  var sLen =  330 + Math.round( iw )  + "px";
	  $(".modal-dialog-wide").css("width", sLen );
	  svg = d3.select("#canvasDiv").append("svg")
	  		.attr("id","dicomSVG")
			.attr("width", iw)
			.attr("height", ih)
			.on("click", function(){
					if (svg.attr("style").includes("cursor:default;")) return;
					lineIndex++;
					index++;
					var coords = d3.mouse(this);
		       		 var circle = svg.append("circle")
							    .attr("cx",coords[0] )
							    .attr("cy",coords[1])
							    .attr("r", 2)
							    .attr("fill","white")
		       		 			.attr("id","circle-" + lineIndex);
		       		circles.push({ x : coords[0], y : coords[1] } );
		       		if (index % 2 == 0){
		       			index = 0;
		       			var line = svg.append("line")
		       						.attr("x1", circles[0].x)
		       					    .attr("y1", circles[0].y)
		       					    .attr("x2", circles[1].x)
		       					    .attr("y2", circles[1].y)
		       					    .attr("id", "rulerLine-" + lineIndex)
		       					 	.attr("stroke-width", 2)
		       					    .attr("stroke", "white");
		       			var x = d3.event.clientX ;
		       			var y = d3.event.clientY ;
		       			$("#showLineInfo").attr("style","display: block; position: absolute; left: " +  x + "px; top: " + y + "px;");

		       			var dx = (circles[1].x - circles[0].x);
		       			var dy = (circles[1].y - circles[0].y);

		       			console.log("x0=" +  circles[0].x );
		       			console.log("x1=" +  circles[1].x );
		       			console.log("PixelSpacing = " + PixelSpacing);
		       			
		       			var rulerLen = Math.round(PixelSpacing * Math.sqrt( dx * dx + dy * dy)/(1 +  0.2 * zoomer));
		       			//var rulerLen = Math.round( Math.sqrt( dx * dx + dy + dy));
		       			$("#rulerInfo").text("L=" +  rulerLen +" mm" );
		       			
		       			for(var ln = 0; ln < lineIndex; ln++ ){
		       				$("#rulerLine-" + ln ).remove();
		       			}
		       			
		       			circles.length = 0;
		       		}else{
		       			for(var ln = 0; ln < lineIndex; ln++ ){
		       				svg.select("#circle-" + ln).remove();
		       			}
		       			
		       		}
					
				});
	  
	  imageGroup = svg.append("g").attr("id", "imageGroup");
	  
	  imageGroup.append("rect")
	  		.attr("width", "100%")
	  		.attr("height", "100%")
	  		.attr("fill", "#000000");
	  
	  var src = "data:image/jpeg;base64," + Image.instance[0].serializedDicom.image;
	  var image = imageGroup.append("image")
			.attr("xlink:href",src)
			.attr("width", iw)
			.attr("height", ih)
			.attr("id", "dicomImage");
	
	  var g4 = svg.append("g").attr("id", "g4");
	  
	  var SeriaTxt = "Series Nb: " + Image.instance[0].serializedDicom.seria;
	  var SeriaTxtLength = core.measureText(SeriaTxt,"sans-serif","");
	  var Seria  = g4.append("text")
	  				.attr("x", iw - SeriaTxtLength.width - 15)
	  				.attr("y", ih - 50)
	  				.text(SeriaTxt)
	  				.attr("font-family", "sans-serif")
	  				.attr("font-size", "12px")
	  				.attr("fill", "#FFFFFF");
	  var SliceThicknessText = (Image.instance[0].serializedDicom.sliceThickness != null ) ? "Thickness: " + 
			  Image.instance[0].serializedDicom.sliceThickness +" mm" : null ;
			  	
	  var SliceThicknessTextLength = core.measureText(SliceThicknessText,"sans-serif","");
	  var SliceThickness = (SliceThicknessText !=null ) ?  g4.append("text")
					.attr("x", iw - SliceThicknessTextLength.width - 15)
					.attr("y", ih - 30)
					.text(SliceThicknessText)
					.attr("font-family", "sans-serif")
					.attr("font-size", "12px")
					.attr("fill", "#FFFFFF") : null;
	  var g3 = svg.append("g").attr("id", "g3");
	  var InstanceTxt = "Frame:[" + Image.instance[0].serializedDicom.instance + "]";
	  var Instance  = g3.append("text")
					.attr("x", 15)
					.attr("y", ih - 50)
					.text(InstanceTxt)
					.attr("font-family", "sans-serif")
					.attr("font-size", "12px")
					.attr("fill", "#FFFFFF")
	  				.attr("id", "InstanceTextId");
	  var MRTxt = "MR (" + Image.instance[0].serializedDicom.columns + "x" +Image.instance[0].serializedDicom.rows + ")";
	  var MR  = g3.append("text")
					.attr("x", 15)
					.attr("y", ih - 30)
					.text(MRTxt)
					.attr("font-family", "sans-serif")
					.attr("font-size", "12px")
					.attr("fill", "#FFFFFF")
	  				.attr("id", "MRTextId");
	  
	  var g1 = svg.append("g").attr("id", "g1");
	  var PatientNameTxt = Image.instance[0].serializedDicom.patientName;
	  var PatientName  = g1.append("text")
					.attr("x", 15)
					.attr("y", 30)
					.text(PatientNameTxt)
					.attr("font-family", "sans-serif")
					.attr("font-size", "12px")
					.attr("fill", "#FFFFFF")
	  				.attr("id", "PatientNameId");
	  var str = Image.instance[0].serializedDicom.patientBirthDate;
	  var PatientBirthDateTxt = str.substring(6)  + "." + str.substring(4,6) + "." + str.substring(0,4);
	  var PatientBirthDate  = g1.append("text")
					.attr("x", 15)
					.attr("y", 50)
					.text(PatientBirthDateTxt)
					.attr("font-family", "sans-serif")
					.attr("font-size", "12px")
					.attr("fill", "#FFFFFF")
	  				.attr("id", "PatientBirthDateId");

	  var PatientIDTxt = "ID: " + Image.instance[0].serializedDicom.patientID;
	  var PatientID  = g1.append("text")
					.attr("x", 15)
					.attr("y", 70)
					.text(PatientIDTxt)
					.attr("font-family", "sans-serif")
					.attr("font-size", "12px")
					.attr("fill", "#FFFFFF")
	  				.attr("id", "PatientIDId");
	  var PatientSexTxt = (Image.instance[0].serializedDicom.patientSex == 'F')? "Female" : "Male";
	  var PatientSex  = g1.append("text")
					.attr("x", 15)
					.attr("y", 90)
					.text( PatientSexTxt )
					.attr("font-family", "sans-serif")
					.attr("font-size", "12px")
					.attr("fill", "#FFFFFF");
	  			
	 
	  var PatientAgeTxt = Image.instance[0].serializedDicom.patientAge.substring(1,3) + " Years";
	  var PatientAge  = g1.append("text")
					.attr("x", 15)
					.attr("y", 110)
					.text( PatientAgeTxt )
					.attr("font-family", "sans-serif")
					.attr("font-size", "12px")
					.attr("fill", "#FFFFFF");
	  			
	  
	  
	  var g2 = svg.append("g").attr("id", "g2");
	  
	  var InstTxt = Image.instance[0].serializedDicom.institutionName;
	  var InstitutionName  = g2.append("text")
					.attr("x", iw - core.measureText(InstTxt,"sans-serif","").width - 15)
					.attr("y", 30)
					.text(InstTxt )
					.attr("font-family", "sans-serif")
					.attr("font-size", "12px")
					.attr("fill", "#FFFFFF");
	  			
	  
	  var stdTxt = Image.instance[0].serializedDicom.studyDescription;
	  var StudyDescription  = g2.append("text")
					.attr("x", iw - core.measureText(stdTxt,"sans-serif","").width - 15)
					.attr("y", 50)
					.text( stdTxt )
					.attr("font-family", "sans-serif")
					.attr("font-size", "12px")
					.attr("fill", "#FFFFFF");

	  var ss0 = Image.instance[0].serializedDicom.studyDate;
	  var StudyDateTxt = ss0.substring(6)  + "." + ss0.substring(4,6) + "." + ss0.substring(0,4);
	  var StudyDate  = g2.append("text")
					.attr("x", iw - core.measureText(StudyDateTxt,"sans-serif","").width - 15)
					.attr("y", 70)
					.text( StudyDateTxt )
					.attr("font-family", "sans-serif")
					.attr("font-size", "12px")
					.attr("fill", "#FFFFFF");
	  
	  var bottomRulerGroup = svg.append("g").attr("id", "bottomRulerGroupID");
	  var bottomRuler = bottomRulerGroup.append("line")
	  					.attr("x1", iw/4 )
	  					.attr("y1", ih - 10 )
	  					.attr("x2", 3 * iw/4 )
	  					.attr("y2", ih - 10)
	  					.attr("stroke-width", 1 )
	  					.attr("stroke", "white");
	  	// left tick line
	  bottomRulerGroup.append("line")
					.attr("x1", iw/4 )
					.attr("y1", ih - 35 )
					.attr("x2", iw/4 )
					.attr("y2", ih - 10)
					.attr("stroke-width", 1 )
					.attr("stroke", "white");
	// right tick line
	  	bottomRulerGroup.append("line")
					.attr("x1", 3 * iw/4 )
					.attr("y1", ih - 35 )
					.attr("x2", 3 * iw/4 )
					.attr("y2", ih - 10)
					.attr("stroke-width", 1 )
					.attr("stroke", "white");
	 // center tick line
		bottomRulerGroup.append("line")
					.attr("x1", iw/2 )
					.attr("y1", ih - 27 )
					.attr("x2", iw/2 )
					.attr("y2", ih - 10)
					.attr("stroke-width", 1 )
					.attr("stroke", "white");
		
		for (var j=1 ; j < 10 ; j++){
			bottomRulerGroup.append("line")
						.attr("x1",  iw / 4 + j *  iw/(2 * 10)  )
						.attr("y1", ih - 20 )
						.attr("x2",   iw / 4 + j *  iw/(2 * 10) )
						.attr("y2", ih - 10)
						.attr("stroke-width", 1 )
						.attr("stroke", "white");
		}
	PixelSpacing = parseFloat(Image.instance[0].serializedDicom.pixelSpacing);
	
	
	
	BottomRulerlength =Math.round(  PixelSpacing * iw /( 2 * 10 ) );

	bottomRulerGroup.append("text")
					.attr()
					.attr("x", 3 * iw/4 + 10)
					.attr("y", ih - 10)
					.text( BottomRulerlength + "cm" )
					.attr("font-family", "sans-serif")
					.attr("font-size", "12px")
					.attr("fill", "#FFFFFF")
					.attr("id", "bottomRuleMax");

	svg.append("line").attr("x1", 0 ).attr("y1", 1).attr("x2", iw).attr("y2", 1).attr("stroke-width", 2 ).attr("stroke", "#f8ac59");
	svg.append("line").attr("x1", 1 ).attr("y1", 0).attr("x2", 1).attr("y2", ih).attr("stroke-width", 2 ).attr("stroke", "#f8ac59");
	svg.append("line").attr("x1", 0 ).attr("y1", ih-1).attr("x2", iw).attr("y2", ih-1).attr("stroke-width", 2 ).attr("stroke", "#f8ac59");
	svg.append("line").attr("x1", iw-1 ).attr("y1", 0).attr("x2", iw-1).attr("y2", ih).attr("stroke-width", 2 ).attr("stroke", "#f8ac59");
	
	var isMobile = window.orientation > -1;
	
	Width =  iw;
	
	Height = ih;
	  
	  return ih;
}
function getImage(sN){
	obj = {};
	for(var i=0 ; i < Images.series.length; i++ ){
		if (Images.series[i].seriaNum == sN){
			 Object.assign(obj,Images.series[i]);
			  break;
		}
	}
	return obj;
}
explorations.showSeria = function(seria){
	$("#dicomSVG").remove();
	cs = seria;
	ci = parseInt(Image.instance[0].instNum);
	Image = getImage( cs );
	$("#dicomImage").attr("href","data:image/jpeg;base64," + Image.instance[ ci ].serializedDicom.image );
	explorations.updateSeriesView();
}

$(document).ready(function() {
	
	documentHeight = screen.height * 0.7;
	core.init();
	
	$("#closeRuler").click( function(e){
		$("#showLineInfo").attr("style", "display: none;");
		svg.select("#circle-" + lineIndex).remove();
		for(var cc=0; cc <= lineIndex; cc++){
			svg.select("#circle-" + cc).remove();
			$("#rulerLine-" + cc ).remove();
		}
		
	});

	$("#btnSaveExpl").click(function(){
    	console.log($("#uploadDicomText").val());
    });
	$("#closeDocom").click( function(){
		$("#fadeIdDicom").remove();  
		$("#modalViewer").removeClass("in");
		$("#modalViewer").attr("style", "display: none;");
		$("#body").removeClass("modal-open");
		$("#body").removeAttr("style");
		$("#dicomSVG").remove();
		
		$("#showLineInfo").attr("style", "display: none;");
		svg.select("#circle-" + lineIndex).remove();
		for(var cc=0; cc <= lineIndex; cc++){
			svg.select("#circle-" + cc).remove();
			$("#rulerLine-" + cc ).remove();
		}
		
		
	});
	$("#canvasDiv").on('mousewheel', function(event) {
		var Len = Image.instance.length;
		ci = parseInt(ci) + event.deltaY;
		ci = (ci < 0) ? ci = 0 : ci > (Len-1) ? parseInt(Image.instance[ Len-1 ].serializedDicom.instance) - 1 : ci;
		$("#dicomImage").attr("href","data:image/jpeg;base64," + Image.instance[ ci ].serializedDicom.image );
		
		$("#InstanceTextId").text("Frame:[" + Image.instance[ ci ].serializedDicom.instance+"]");
		
	});
	
	$("#rulerBtn").click( function(){
	
		svg.attr("style","cursor:crosshair;");
		
	});
	$("#canselRuler").click( function(){
		
		svg.attr("style","cursor:default;");
		
	});
	$("#ZoomPlus").click( function(){
		zoomer =  zoomer + 1;
		var dZ = 1 +  0.2 * zoomer;
		if (dZ > 2) {
			dZ = 2;
			zoomer = 5;  
		}
		
		var len = Math.round( BottomRulerlength / dZ );
		
		var scaler = "scale(" + dZ + ")";
		var dX = (-1) * Width/2 * ( dZ -1 );
		var dY = (-1) * Height/2 * ( dZ -1 );
		var translate ="translate(" + dX +"," + dY + ") ";
		imageGroup.attr("transform", translate  +  scaler );
		$("#bottomRuleMax").text(len + " cm");
	});
	$("#ZoomMinus").click( function(){
		zoomer = zoomer - 1;
		var dZ = 1 +  0.2 * zoomer;
		if (dZ < 1) {
			dZ = 1;
			zoomer = 0;
		}
		var len = Math.round( BottomRulerlength /  dZ );
		
		var scaler = "scale(" + dZ + ")";
		var dX = (-1) * Width/2 * ( dZ -1 );
		var dY = (-1) * Height/2 * ( dZ -1 );
		var translate ="translate(" + dX +"," + dY + ") ";
		imageGroup.attr("transform", translate  +  scaler );
	
		$("#bottomRuleMax").text(len +  " cm");
		
	});
	$("#ZoomDefault").click( function(){
		imageGroup.attr("transform", "translate(0,0) scale(1,1)" );
		$("#bottomRuleMax").text(BottomRulerlength + " cm");
	});	
	
	
	explorations.init();
	
});