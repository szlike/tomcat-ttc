//CSCC09f12 ASSIGNMENT 3
//STUDENT1:Ke li
//STUDNET#:996999805

//STUDENT2:Kaixiang hou
//STUDNET#:997496281


var serviceURL = "https://mathlab.utsc.utoronto.ca/courses/cscc09f12/houkaixi/a3/";
var alanURL = "http://www.utsc.utoronto.ca/~rosselet/cscc09f12/asn/services/";
var userId = 0;         // global variable for user session check
var fromPage;      // global variable for storing the previous page before login or register action
var busid = 0;        // global variable for reflesh use
var stopRoutes = [];  // global variable for user selected stops
var vehicleRoutes = []; // global variable for user selected vehicles
var che = 0;

function displayRoute() {
	/*
		routePage hangler
		Display all routes list, using ajax to retrieve JSON file and use jQuery to fill them into HTML
	*/
    	$.getJSON(serviceURL + "routes.php", function(data) {
	var items = data.items;
	$.each(items, function(index, r) {
	    var route = '<li><span><label>'+r.display_name+'</label><a onclick="session_check()" href="#Info" id="'+ r.id+'" title="'+ r.display_name+'" data-role="button" data-inline="true" data-mini="true" data=icon="arrow-r" data_theme="b" class="btn dir" data-transition="flip"> Show Direction</a><br><a class="fliperS" id='+r.id+'>My Stops <select id='+r.id+' data-role="slider" data-theme="e" data-track-theme="b"><option value="off">Off</option><option value="on">On</option></select></a><a class="fliperV" id='+r.id+'> My Vehicles <select id='+r.id+' data-role="slider" data-theme="e" data-track-theme="b"><option value="off">Off</option><option value="on">On</option></select></a></span></li>';
           $('#RouteList').append(route);
	});
	$('#RouteList').listview('refresh');
	$('select').slider();
	$('.btn').button();
    });
}


$('#routesPage').live("pageshow", function(event) {
/* 
check user session for login/logout button and other use
if session exist, button toggle logout
*/
    console.log("am i wrong");  
    session_check();
  
});


$('#routesPage').live("pageinit", function(event) {
/*
function call for displayRoute, display all routes list
*/
    console.log("OR ME"); 
    $('#RouteList').listview();
    geoLocation();
    displayRoute();
});


function displayDirection(id, route_title) {

/*
direction(info) page handler
second level of the application, display the direction(run) information of the selected route
using ajax to retrieve JSON file and use jQuery to fill them into HTML
*/

    var URL = serviceURL+"runs.php?id=" + id;
    $('#direction').empty(); 
    $.getJSON(URL, function(data) {
	var items = data.items;
	$.each(items, function(index, d) {
	    var direction = '<li><a onclick="session_check()" href="#stopInfo" id="' + d.route_id + '" title="'
		+ d.id + '" route_title="' + route_title + '" run_title="' + d.display_name
                + '" data-role="button"' + ' data-inline="true" data-mini="true" data-icon="arrow-r"'
		+ ' data-theme="b" class="btn getstop" data-transition="flip">'
		+ d.display_name + '</a></li>';
	    $('#direction').append(direction);
	});
	$('#direction').listview('refresh');
    });
}


$('#directions').live("pageshow", function(event) {
/*
check user session for login/logout button and other use
if session exist, button toggle logout
*/
    session_check(); 
});


$('.dir').live("click", function(event) {
/*
function call for displayDir pass route_title to next level
*/
    var id = $(this).attr('id');
    var display_name = $(this).attr('title');
    $('.busNumber h1').html('Route ' + id);
    displayDirection(id, display_name);
});



function displayStop(routeid, route_title, runid, run_title) {
/*
third level of the application
display the stops' information for the chosen direction(run)
using ajax to retrieve JSON file and use jQuery to fill them into HTML
*/
     var URL = serviceURL+"stops.php?routeid=" + routeid + "&runid=" + runid;
     $('#stop').empty(); 
     $.getJSON(URL, function(data) {
	 var items = data.items;
	 $.each(items, function(index, s) {
	     var stop = '<li><div data-role="content"><h3>' + s.display_name + '</h3><a stopid="' + s.id 
                     + '" onclick="session_check()"' + '" display_name="' + s.display_name + '" routeid="' + routeid 
		     + '" runid="' + runid
                     + '" route_title="' + route_title + '" run_title="' + run_title 
		     + '" data-role="button" data-inline="true" data-mini="true" data-icon="star" data-iconpos="right"'
                     + ' data-theme="b" class="btn savestop" data-transition="flip">Save Stop</a><a onclick="session_check()" href="#nextBus" stopid="' + s.id 
                     + '" data-role="button" data-inline="true" data-mini="true" data-icon="arrow-r" data-iconpos="right"'
                     + ' data-theme="b" class="btn predict" data-transition="flip">Next Arrivals'
                     + ' </a></div></li>';
	     $('#stop').append(stop);
	});
	$('#stop').listview('refresh');
       $('.savestop').button();
       $('.predict').button();
    });
}


$('#stop').live("pageshow", function(event) {
/*
check user session for login/logout button and other use
if session exist, button toggle logout
*/
    session_check(); 
});


$('.getstop').live("click", function(event) {
/*
function call for displayStop pass the run_title and route_title to next level
*/
    var routeid = $(this).attr('id');
    var runid = $(this).attr('title');
    var route_title = $(this).attr('route_title');
    var run_title = $(this).attr('run_title');
    displayStop(routeid, route_title, runid, run_title);
});

function nextArrive(id){
/*
handle nextArrive information return a list of the arrival time of chosen route
using ajax to retrieve JSON file and use jQuery to fill them into HT
*/
	$('#predictions').empty();
	$.getJSON( alanURL+"getpredictions.php?id=" + id, function(data) {
		var arriveList = data.items;
		
	
		$.each(arriveList, function(index, b){
			var next = '<li><div data-role="content">The route#'+ b.route_id +' bus will come in '+ b.minutes + ' minute(s)</div></li>';
			$('#predictions').append(next);
		});
		if ($('#predictions').children().size() == 0){
			$('#predictions').append('<strong><p> Data not available at this moment. Please try again later</p></strong>'); };	
		$('#predictions').listview('refresh');
	});	
}


$('#nextBus').live("pageshow", function(event) {
/*
check user session for login/logout button and other use
if session exist, button toggle logout
*/
    session_check(); 
});


$('.predict').live("click", function(event) {
/*
function call for nextArrive
*/
	  busid = $(this).attr('stopid');
          nextArrive($(this).attr('stopid'));
	  
});


function myStop(saved, htmlList) {
     /* 
	myStop page handler
	display all the saved route
	provide delete and check next arrival function

     */
     $(htmlList).empty();
     $.each(saved, function(stopid, stop) {
        var save = '<li><h3>' + stop[0] + '</h3><span><a stopid="' + stopid 
                 + '" href="#nextBus" onclick="session_check()" data-role="button" data-inline="true" data-mini="true" data-icon="arrow-r"'
                 + ' data-theme="b" class="btn predict" data-transition="flip">prediction</a>'
                 + ' <a href="#" stopid="' + stopid + '" onclick="session_check()" data-role="button" data-inline="true"'
                 + ' data-mini="true" data-icon="delete" data-theme="b" class="btn del" data-transition="flip"'
                 + ' href="#MyStop">delete</a></span></li>';
        $(htmlList).append(save);
    });
    $(htmlList).listview('refresh');
    $('.del').button();
    $('.predict').button();
}


$('#MyStop').live("pageinit", function(event) {
/*
MyStop initialize page, send action=stopslist to controller and receive mystop information from mod
*/
    fromPage = $('.ui-page-active').attr('id');
    $('#mystopList').listview();
    if ( userId != 0 ) {
       $.getJSON('/a3/servlet/Controller?action=stopslist', function(data) {
            myStop(data, '#mystopList'); }); }
    else {
       $.mobile.changePage('#loginPage'); };
});


$('#MyStop').live("pageshow", function(event) {
/*
Everytime use click mystop button, send action=stopslist to controller and receive mystop information from model
do session_check in beginning, if user doesnot sign in, redirect to login page
*/
    session_check();
    fromPage = $('.ui-page-active').attr('id');
    if ( userId != 0 ) {
        $.getJSON('/a3/servlet/Controller?action=stopslist', function(data) {
             myStop(data, '#mystopList'); }); }
     else {
        $('#mystopList').empty();
        $('#mystopList').listview('refresh');
        $.mobile.changePage('#loginPage'); };
});

function lookup( array, ele) {
    for(var i = 0, len = array.length; i < len; i++) {
        if( array[ i ] == ele )
            return true;
    }
    return false;
}

function session_check() {
/*
Check if there is a session for user, yes then show logout buttion
otherwise show login buttion
*/
    if (che == 0){
	che = 0;
    }
    else{
    var user_id = new Array();
    user_id.push({ name: "userId", value: userId });
    $.ajax({
	url: '/a3/servlet/Controller?action=session_check',
	type: 'POST',
	dataType: 'json',
	data: user_id,
	success: function(data, status) {
                 userId = data.user_id; }
    });
    if ( userId == 0 ) {
        $('.login .ui-btn-text').html("Login");
        if ( $('.login').hasClass('logout') ) { $('.login').removeClass('logout'); }
    }
    else {
        $('.login .ui-btn-text').html("Logout");
        if ( !$('.login').hasClass('logout') ) { $('.login').addClass('logout'); }
    };
    }
}


$('#login').live('submit', function(event) {
/*
login action handle, receive login information for form and send it to controller throught ajax call
*/
        che = 1;
        event.preventDefault();
        var loginData = $("#login").serializeArray();
        loginData.push({ name: "page", value: "#"+fromPage });
        loginData.push({ name: "action", value: "login" });
        var $form = $('#login');
        $.ajax({
	    url: $form.attr('action'),
	    type: $form.attr('method'),
	    dataType: 'json',
	    data: loginData,
	    success: function(data, status) {
	        loggedIn = data.authenticated;
	        if (loggedIn == 'true') {
		    // Logout currently not implemented, also Logout does not change state
		    // when autologout occurs on server - need to test loggedIn state after
		    // each server request.
		    $('.login .ui-btn-text').html("Logout");
		    $('.login').toggleClass('logout');
		    userId = data.userId;
                  if ( fromPage == "registerPage" ) {
                       $.mobile.changePage("#routesPage"); }
                  else {
                       $.mobile.changePage("#" + fromPage); }
		 }
	        else {
		    $('#loginStatus').html('<h3 style="color:red">Password and Username do not match. Please try again</h3>'); };
               $('.login').button();	        
    	        return false;  // prevent default page-oriented submit action
            }, 
	    error: function(x,y,z) { console.log(y); }
        });
        for (i = 0; (i < document.forms.length); i++) {//reset login form, avoid user account information loss
             document.forms[i].reset(); }
});

// Asst 3 map-page functionality 
var lat, lon = null;

/* Use HTML5 geolocation capability to provide location-based service */
function geoLocation() {
    if (navigator.geolocation) {  // attempt to get user's geoLocation
        navigator.geolocation.getCurrentPosition (function(position) {
          lat = position.coords.latitude;
          lon = position.coords.longitude;
        });
    }
    else {  // centre on UTSC if user has no geolocation or declines to reveal it
          lat = 43.78646;
          lon = -79.1884399;
    }
    if (lat==null || lon==null) {  // wait until geoLocation determined
	setTimeout(geoLocation, 500);
    }
    else {   // got geoLocation, now draw the map
	drawMap();
    }
}

var map = null;  // google map object
var stopsNearMe = [];  // stops within 1km of user's geolocation
var route_icons = ["img/blue.gif", "img/red.gif", "img/green.gif",
		"img/yellow.gif", "img/purple.gif", "img/cyan.gif",
		"img/black.gif", "img/orange.gif", "img/white.gif"];
// use dictionary for bus icons, so can look up by run direction 
var bus_icons = {"North": "img/bus_north.jpg", "East": "img/bus_east.jpg",
		"South": "img/bus_south.jpg", "West": "img/bus_west.jpg"}; 
    
var PHP_URL = "https://www.utsc.utoronto.ca/~rosselet/cscc09f12/asn/services/";

/* draw the map, and add overlay markers for user-selected information */
function drawMap() {
    map = new google.maps.Map(document.getElementById('map'), {
      zoom: 15,
      center: new google.maps.LatLng(lat, lon),
      mapTypeId: google.maps.MapTypeId.ROADMAP
    });

    mapStops(stopsNearMe);  // add markers for stops near user
    mapRouteStops(stopRoutes);  // add markers for user-selected route stops
    mapVehicles(vehicleRoutes);  // add markers for user-selected route vehicles
    // refresh vehicle markers every 30 seconds to show updated vehicle positions
    setInterval(function() { mapVehicles(vehicleRoutes); }, 30000); 
};

/* add map markers for parameter list of stops with geolocation near the user */
function mapStops(stopList) {
    var infowindow = new google.maps.InfoWindow();
	$.each(stopList, function(index, stop) {
            var i;
	    /* place map markers for parameter stops - make them clickable to
	       obtain predicted arrival times for clicked stop */
	    text = stop.display_name + ', route: ' 
			+ stop.routeid + ', direction: ' + stop.run_display_name
			+ ', distance: ' + (stop.distance*1000).toFixed(0) + 'm';
            var marker = new google.maps.Marker({
                position: new google.maps.LatLng(stop.latitude, stop.longitude),
            	map: map,
                title: text,
		icon: "img/me.gif"  // use a distinct icon for nearby stops
            });
    
            google.maps.event.addListener(marker, 'click', (function(marker) {
                return function() {
		  // Obtain real-time prediction data for user-clicked stop
		  $.getJSON(PHP_URL + 'getpredruns.php?id=' + stop.id, function(data) {
		      // load content of map info-window that opens on click
                      var predList = '<span>' + stop.display_name
                            + '<br/>Vehicles arriving in: ';
                      $.each(data.items, function(index, prediction) {
	    	          var dirName;  // name to display for vehicle direction
	    	          // some vehicles have no run display_name, or their run # has no defined name
	    	          if (prediction.run_name == null || prediction.run_name == undefined) {
	    		      dirName = "#"+prediction.run_id;
	    	          }
	    	          else {  // have a run name, so display it
	    		      dirName = prediction.run_name; 
	    	          };
                          predList += prediction.minutes + ' (' + dirName + '), ';
                      });
                      predList += ' minutes</span>';
                      infowindow.setContent(predList);
                      infowindow.open(map, marker);
                  });       // getJSON prediction data
               }
           })(marker));
       });
};

/* add markers for stops on the routes in parameter routeList */
function mapRouteStops(routeList) {
    var infowindow = new google.maps.InfoWindow();
    // iterate through each route in routeList
    $.each(routeList, function(index, route) {
      // for each route, retrieve its run-list
      $.getJSON(PHP_URL + "getruns.php?id=" + route, function(data) {
        $.each(data.items, function(runidx, run) {
	  // for each run, retrieve its stop-list
	  $.getJSON(PHP_URL + "getstops.php?routeid=" + route + "&runid=" 
						+ run.id, function(data) {
	    // for each stop, create a marker with a run-coded icon
            $.each(data.items, function(index, stop) {
              var i;
	      /* place markers on map for nearby stops - make them clickable to
	         obtain predicted arrival times for clicked stop */
	      var icon = route_icons[runidx];
	      text = "Stop: " + stop.display_name + ', Route: ' 
			+ route + ', Direction: ' + run.display_name;
              var marker = new google.maps.Marker({
                position: new google.maps.LatLng(stop.latitude, stop.longitude),
            	map: map,
                title: text,
		icon: icon
              });
    
              google.maps.event.addListener(marker, 'click', (function(marker) {
                return function() {
		  // Obtain real-time prediction data for user-clicked stop
		  $.getJSON(PHP_URL + 'getpredruns.php?id=' + stop.id, function(data) {
		    if (data.items != null) {
		      // load content of map info-window that opens on click
                      var predList = '<span>' + stop.display_name
                            + '<br/>Vehicles arriving in: ';
                      $.each(data.items, function(index, prediction) {
                          predList += 
                            prediction.minutes + ' (' + prediction.run_name + '), ';
                      });
                      predList += ' minutes</span>';
                      infowindow.setContent(predList);
		    }
		    else {
			infowindow.setContent("Sorry, no prediction data available at this time; please try again later");
		    }
                    infowindow.open(map, marker);
                  });       // getJSON prediction data
                }
             })(marker));
         });  // each stop
       });  // getJSON stops
     });  // each run
   });  // getJSON runs
 });  // each route
};

var markerArray = [];  // remember vehicle markers, so we can remove/refresh them

/* add markers for vehicles on the routes in parameter routeList */
function mapVehicles(routeList) {
    /* clear current vehicle markers before updating with new ones, else end
	up with vehicle markers in old and new locations. */
    while (markerArray.length > 0) {
          var m =  markerArray.pop();
          m.setMap(null);
    }
    /* add vehicle markers for user-selected routeList */
    $.each(routeList, function(index, route) {
      /* retrieve location data for vehicles on this route */
      $.getJSON(PHP_URL + "getvehruns.php?id=" + route, function(data) {
        var vehList = data.items;  // extract the list of vehicles
	/* create a marker for each vehicle */
	$.each(vehList, function(index, vehicle) {
            var marker;
	    var dirName;  // name to display for vehicle direction
	    // some vehicles have no run names, or their run # has no defined name
	    if (vehicle.run_name == null || vehicle.run_name == undefined) {
		dirName = "#"+vehicle.run_id;
	    }
	    else {  // have a run name, so display it
		dirName = vehicle.run_name; 
	    };
	    text = 'direction: ' + dirName + ', vehicle#: ' + vehicle.id;
    	    marker = new google.maps.Marker({
                position: new google.maps.LatLng(vehicle.latitude, vehicle.longitude),
                map: map,
                title: text,
		icon: bus_icons[vehicle.dir_name] 
            });
	    marker.setMap(map);
	    markerArray.push(marker);
	});
      });
    });
};

$('a.fliperS').live("change", function(event) {
	var id = this.id;
	op = $(this).find('option:selected').attr('value');
	if(op=="on"){
		stopRoutes.push(id);
	}
	else{
		delete stopRoutes[stopRoutes.indexOf(id)];
	}
});

$('a.fliperV').live("change", function(event) {
	var id = this.id;
	ve = $(this).find('option:selected').attr('value');
	if(ve=="on"){
		vehicleRoutes.push(id);
	}
	else{
		delete vehicleRoutes[vehicleRoutes.indexOf(id)];
	}
});

/*$('a.map_me').live("click", function(event) {
     stopsnearme.php extracts stops near user geolocation from Stops table
	and Runs table (run_display_name) 
    insertArray = [];
    $.getJSON(serviceURL + "nearme.php?lat=" + lat + "&lon=" + lon, function(data) {
        stopsNearMe = data.items;  // extract the list of nearby stops
	for(var i=0; i < stopsNearMe.length; i++){
		if (lookup(stopRoutes, stopsNearMe[i].routeid)){
			insertArray.push(stopsNearMe[i]);
		}
	}
	stopsNearMe = insertArray;	
	mapStops(stopsNearMe);
    });
    $.mobile.changePage('#mapPage');
});*/

$('a.map_me').live("click", function(event) {
    /* stopsnearme.php extracts stops near user geolocation from Stops table
	and Runs table (run_display_name) */
    $.getJSON(serviceURL + "nearme.php?lat=" + lat + "&lon=" + lon, function(data) {
        stopsNearMe = data.items;  // extract the list of nearby stops
	mapStops(stopsNearMe);
    });
    $.mobile.changePage('#mapPage');
});


$('a.login').live("click", function(event) {
/*record the previous page url before login*/
    fromPage = $('.ui-page-active').attr('id'); 
});


$('#loginPage').live("pageshow", function(event) {
    /* Check the frompage and then update the loginStatus hint according to the frompage. Also reset userid to 0. */
    if ( fromPage == "registerPage" ) {
        $('#loginStatus').html('<h3 style="color:red">You have registerd successfully!</h3>'); }
    else {
        $('#loginStatus').html(''); };
    userId = 0;
});

$('#mapPage').live("pageshow", function(event) {
    drawMap();  // redraw the map when page is shown
});

$('#infoPage').live("pageshow", function(event) {
    $('#svc_alerts').empty();  // clear the HTML list before rebuilding it
    $.get(PHP_URL + "getalerts.php", function(data) {
	var ttcPage = data;
	var svcAlerts = $(ttcPage).find(".ttc-service-alert p").not('.alert-updated');
	$.each(svcAlerts, function(index, data) {
	    $('#svc_alerts').append(data);
	});
    });
});

$('.del').live("click", function(event) {
    /* send delete action to controller and controller contact 
       model to delete the specific row from database
       the row is determined by its stopid
    */
    var stopid = $(this).attr('stopid');
    $.ajax({
	url: '/a3/servlet/Controller?action=delete',
	type: 'POST',
	dataType: 'json',
	data: { id: stopid }
    });
    $(this).closest('li.ui-li').remove();
    $('#mystopList').listview('refresh');
});


$('a.refresh').live("click", function(event) {
    /* using a global variable to store busid
       when user press refresh button, call nextArrive function using parameter busid
    */
    if ( busid.length == 0 ) {
          $('#predictions').empty();
          $('#predictions').append('<li><p><center><font size="4">No predictions available for this stop'
          + ' (Stop ID is ""). Most likely it is the LAST STOP on the line. Come back and check later.'
          + '</font></center></p></li>');
          $('#predictions').listview('refresh'); }
     else {
          nextArrive(busid); };
});

$('.savestop').live("click", function(event) {
    /* 
	record stopid, routeod, runid, run_title, route_title, stop_title and send them to controller
	the controller will add these information into database
    */
    if ( userId == 0 ) {
         fromPage = $('.ui-page-active').attr('id'); 
         $.mobile.changePage('#loginPage'); }
    else {
         event.preventDefault();         
         var stopid = $(this).attr('stopid');
         var routeid = $(this).attr('routeid');
         var runid = $(this).attr('runid');
         var run_title = $(this).attr('run_title');
         var route_title = $(this).attr('route_title');
         var stop_title = $(this).attr('display_name');
         var mystop = new Array();
         var loginData = new Array();
         loginData.push({ name: "page", value: "#"+fromPage });
         loginData.push({ name: "action", value: "login" });
	   /* if a stop is selected (stopid != null), add the new stop item to HTML5
       localStorage so it persists across app reloads.  Begin by reading
       the current value of localStorage for key 'stoplist'.  Represent
       the value field as a dictionary to allow for more data fields to
       be added in future versions of the app. */
         if (stopid != null) {
             mystop.push({ name: "routeid", value: routeid });
             mystop.push({ name: "route_title", value: route_title });
             mystop.push({ name: "runid", value: runid });
             mystop.push({ name: "run_title", value: run_title });
             mystop.push({ name: "stopid", value: stopid });
             mystop.push({ name: "stop_title", value: stop_title });
             mystop.push({ name: "user_id", value: userId });
             mystop.push({ name: "action", value: "save" });
             $.ajax({
    	     	     url: '/a3/servlet/Controller',
    		     type: 'POST',
    		     dataType: 'json',
    		     data: mystop,
    		     success: function(data, status) {
        	         displayPage = data.page; }
	     });
         }
         $.mobile.changePage('#MyStop');
    };
});


$('#register').live('submit', function(event) {
    /* 
	register page collects information by using form and send these 
	information to controller then controller contact model to store new user's information
    */
        event.preventDefault();
        var registerData = $("#register").serializeArray();
        registerData.push({ name: "page", value: "#"+fromPage });
        registerData.push({ name: "action", value: "register" });
        var $form = $('#register');
        $.ajax({
	    url: $form.attr('action'),
	    type: $form.attr('method'),
	    dataType: 'json',
	    data: registerData,
	    success: function(data, status) {
	        regstatus = data.status;
	        if (regstatus == 'true') {
                     $('#loginStatus').html('<h3 style="color:red">Registration Complete, Please Login !</h3>');
                     fromPage = $('.ui-page-active').attr('id');
                     $.mobile.changePage('#loginPage');                     
	        }
	        else {
		     $('#registerStatus').html('<h3 style="color:red">' + regstatus + '</h3>');
	        };
                $('.login').button();
    	        return false;  // prevent default page-oriented submit action
            }, 
	    error: function(x,y,z) { console.log(y); }
        });
        for (i = 0; (i < document.forms.length); i++) {
             document.forms[i].reset(); }
});

$('.logout').live("click", function(event) {
    /* Event handler for click event on <a> elements with class "logout". Signing the user out 
       and then redirect to login page. */
    che = 0;
    $('.login').toggleClass('logout');
    $('.login .ui-btn-text').html("Login");
    $.ajax({
 	url: '/a3/servlet/Controller',
  	type: 'POST',
    	dataType: 'json',
    	data: {"action": "logout", "page": "#"+fromPage},
	success: function(data, status) {
                  userId = 0;
                  $('.login').button(); }
    });
});
