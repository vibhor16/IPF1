/**
 * Created by shuklv1 on 15-Jan-18.
 */
var CLIENT_IP;
var ajax_CLIENT_IP;
var temp;
var names;
var UtilityCallInterval;
var SystemReadyCallInterval;
var Utilities_html = "";

$(document).ready(function () {
    attachFocusOutListeners();
});

function attachFocusOutListeners() {
    $("#searchString").focusout(function () {
        if ($("#searchString").val().length > 0) {
            $("#searchString").attr('style', '1px solid #c8d1d3');
        } else {
            $('#searchString').attr('style', 'border:2px solid red');
        }
    });
    $("#repoList").focusout(function () {
        selRepo = $("#repoList option:selected").text();
        if (selRepo != "Please Select..") {
            $("#repoList").attr('style', '1px solid #c8d1d3');
        } else {
            $('#repoList').attr('style', 'border:2px solid red');
        }
    });
}


function init(ajax_IP, temp1) {
    CLIENT_IP = ajax_IP;
    ajax_CLIENT_IP = ajax_IP;
    temp = temp1;
    document.getElementById("stop_search_button").disabled = true;
    document.getElementById("main_search_button").disabled = true;
    document.getElementById("main_new_search").disabled = true;

    showLoader("Initialising, please wait...");
    $('#search_duration').html("00:00:00");
    populateReleaseList();
    SystemReadyCallInterval = setInterval(function () {
        getSystemReadyStatus(SystemReadyCallInterval)
    }, 500);

}
var new_search_btn_msg = 'New Search <i class="fa fa-refresh"></i>';
var cont_search_btn_msg = 'Continue Search <i class="fa fa-refresh"></i>';

function onNewSearchClick() {
    var el = document.getElementById("main_new_search");
    if (el.innerHTML.indexOf("New") >= 0) {
        document.getElementById("searchString").disabled = false;
        document.getElementById("repoList").disabled = false;
        document.getElementById("main_search_button").disabled = false;
        document.getElementById("main_new_search").innerHTML = cont_search_btn_msg;
        $("#result_main_panel").css("visibility", "hidden");
        document.getElementById("searchString").value = "";

    } else if (el.innerHTML.indexOf("Continue") >= 0) {
        document.getElementById("searchString").disabled = true;
        document.getElementById("repoList").disabled = true;
        document.getElementById("main_search_button").disabled = true;
        document.getElementById("main_new_search").innerHTML = new_search_btn_msg;
        $("#result_main_panel").css("visibility", "");
        document.getElementById("searchString").value = searchString;
    }


}

function handleCollapse(id) {
    var outerId = id;
    var innerId = id.split("#")[1];
    var innerClass = document.getElementById(innerId).className;
    if (innerClass.indexOf(' collapse') >= 0 && innerClass.indexOf(' collapsed') < 0) {
        innerClass = innerClass.replace(' collapse', ' collapsed');
    } else if (innerClass.indexOf(' collapsed') >= 0) {
        innerClass = innerClass.replace(' collapsed', ' collapse');
    }
    document.getElementById(innerId).className = innerClass;

}

function disableLeftPanel() {
    document.getElementById("searchString").disabled = true;
    document.getElementById("repoList").disabled = true;
    document.getElementById("main_search_button").disabled = true;
}


function pathterminator(ajax_CLIENT_IP, level_count, key) {
    // console.log("PathTerm VIbhor:  "+ajax_CLIENT_IP+"  "+level_count+"  "+key);


    var myURL = "/ajax/" + ajax_CLIENT_IP + "/pathterminators/" + level_count + "/" + key;

    $.ajax({

        url: myURL,
        async: true,
        success: function (data) {
            console.log("PathTerm: " + data);
            data=JSON.parse(data);
            // var parts = data.split("<!-- vibhor -->");
            // $('#main_card_body').html(parts[1]);
            updateBody(data);
            addToLeftConsole("Victory", "PATH FOUND!!");
            // getPastSearches();
            // populateReleaseList();
            $('#search_duration').html(elapsedTimeValue);


        }
    });
}

window.onunload = function (event) {
    var myURL = '/ajax/' + CLIENT_IP + '/terminateClient';
    $.ajax({

        url: myURL,
        async: false,
        success: function (data) {

        }
    });
};

function showLoader(content) {
    var loader_icon = '<div class="loader-container text-center">' +
        '<div class="icon">' +
        '<div class="sk-wave">' +
        '<div class="sk-rect sk-rect1">' + '</div>' +
        '<div class="sk-rect sk-rect2">' + '</div>' +
        '<div class="sk-rect sk-rect3">' + '</div>' +
        '<div class="sk-rect sk-rect4">' + '</div>' +
        '<div class="sk-rect sk-rect5">' + '</div>' +
        '</div>' +
        '</div>' +
        '<div class="title" id="loader_title"></div>' +
        '</div>';
    $('#loader').attr("class", "card-body __loading");
    $('#loader_icon').html(loader_icon);
    $('#loader_icon1').html(loader_icon);
    $('#loader_title').html(content);
}
function removeLoader() {
    $('#loader').attr("class", "card-body");
    $('#loader_icon').html("");
    $('#loader_icon1').html("");
    $('#loader_title').html("");
}
function getSystemReadyStatus(SystemReadyCallInterval) {
    var myURL = "/ajax/" + CLIENT_IP + "/SystemReady";

    $.ajax({

        url: myURL,
        async: true,
        success: function (data) {
            if (data == 0) {
                document.getElementById("main_search_button").disabled = false;
                removeLoader();
                clearInterval(SystemReadyCallInterval);
            }

        }
    });

}


var utility_file_func_map = {};
function getUtilityAllFuncAndProcsInFile(index, type) {
    var PkgName = getPkgNameAtIndex(index);
    var divId;
    if (type == 1) {
        divId = "#" + PkgName + "_proc";
        type = "PROCEDURE";
    }
    else if (type == 2) {
        divId = "#" + PkgName + "_func";
        type = "FUNCTION";
    }

    if (PkgName in utility_file_func_map) {

        $(divId).html(utility_file_func_map[PkgName]);
    }
    else {

        var myURL = "/ajax/" + CLIENT_IP + "/UtilityAllFuncAndProcsInFile/" + PkgName + "/" + type;

        $.ajax({

            url: myURL,
            async: true,
            success: function (data) {
                // console.log(divId+" ---"+data);
                utility_file_func_map[PkgName] = data;
                $(divId).html(data);     //put respective func on proc in the collapsible item
            }
        });
    }
}

function getPkgNameAtIndex(index) {
    for (var i = 0; i < allFilesInUtility.length; i++) {
        if (index == i) {
            return allFilesInUtility[i];
        }
    }
}

var allFilesInUtility = [];
var callCount = 1;
function getUtilityFilePkgName(UtilityCallInterval) {

    // console.log("Calling: "+callCount);
//se-rms-13_2_10 type
    var repoName = "se-rms-16_0_x1";
    var myURL = "/ajax/" + CLIENT_IP + "/UtilityFilePkgName/" + repoName;
    $.ajax({

        url: myURL,
        async: true,
        success: function (data) {
            // alert(data);


            var i = 0;

            for (var p in data) {

                if (data.hasOwnProperty(p)) {

                    var fileName = p.substring(p.lastIndexOf("\\") + 1, p.length);
                    var pkgName = data[p].toString();
                    if (!allFilesInUtility.includes(pkgName))   //add pkgName
                        allFilesInUtility.push(pkgName);


                    Utilities_html += '<tr>' +
                        '<td>' + fileName + '</td>' +
                        '<td>' + pkgName + '</td>' +
                        '<td>' +

                        '<div class="panel panel-default">' +
                        '<div class="panel-heading collapsed" data-toggle="collapse" href="#' + pkgName + '_proc" onclick="getUtilityAllFuncAndProcsInFile(' + i + ',1)">' +
                        '<h4 class="panel-title" style="font-size: 14px;">' +
                        'Get Procedures' +
                        '</h4>' +
                        '</div>	' +
                        '<div id="' + pkgName + '_proc" class="panel-collapse collapse" aria-expanded="false" style="height: 0px;padding: 8px;text-align: left;cursor:default">' +


                        '</div>' +
                        '</div>' +


                        '</td>' +
                        '<td>' +

                        '<div class="panel panel-default">' +


                        '<div class="panel-heading collapsed" data-toggle="collapse" href="#' + pkgName + '_func" onclick="getUtilityAllFuncAndProcsInFile(' + i + ',2)">' +
                        '<h4 class="panel-title" style="font-size: 14px;">' +
                        'Get Functions' +
                        '</h4>' +
                        '</div>	' +
                        '<div id="' + pkgName + '_func" class="panel-collapse collapse" aria-expanded="false" style="height: 0px;padding: 8px;text-align: left;cursor:default">' +


                        '</div>' +
                        '</div>' +


                        '</td>' +

                        '</tr>';
                    i++;


                } //end if hasOwnProp
            }


        }
    });

    callCount++;
    if (callCount == 10) {
        $("#Utilities_datatable").html(Utilities_html);
        // console.log("Cleared: "+callCount);
        callCount = 1;
        clearInterval(UtilityCallInterval);
    }
    else {
        var loading = '<tr><td>Loading in ' + (9 - callCount) + ' seconds..</td><td></td><td></td><td></td></tr>';
        $("#Utilities_datatable").html(loading);
    }


}
function setUtilitiesTab() {
    console.log("Called from new:  " + Utilities_html);
    $("#Utilities_datatable").html(Utilities_html);
}


function populateReleaseList() {
    // populate the release list


    temp = temp.substr(1, temp.length - 2);
    var tempArray = temp.split(",");

    var x = document.getElementById("repoList");

    // emptying the list then adding
    $('#repoList').find('option').remove();
    // adding a default field
    var option = document.createElement("option");
    var txt = "Please Select..";
    option.text = txt;
    option.id = txt;
    x.add(option);

    // adding actual list elements
    for (var i = 0; i < tempArray.length; i++) {
        // console.log("cc: "+tempArray[i]);
        var option = document.createElement("option");
        var txt = tempArray[i].substr(tempArray[i].indexOf("-") + 1, tempArray[i].length);
        option.text = txt;
        option.id = txt;
        x.add(option);

    }
}

function selectRelease(repoName) {
    try {
        document.getElementById(repoName).selected = true;
    }
    catch(err){
        console.log(err+"  repoName: "+repoName);
    }
}
// function getPastSearches()
// {


// if(selRepo!=null)
// {
// 	selectRelease(selRepo);
// }
//   var myURL="/ajax/"+CLIENT_IP+"/getPastSearchesList";
//   var html="";
// 	$.ajax({

// 	url: myURL,
// 	async: true,
// 	success:function(data){
// 	  	  names=data.split(';');
// 	  	 // console.log(names.length+" "+names[0]+" "+names[1]+" "+names[2]+" "+names[3]+" ");
// 	  	 for(var i=names.length-2;i>=0;i--)
// 	  	 {

// 	  	  var parts=names[i].split("__");
// 	  	  var timestamp=parts[0];
// 	  	  var searchString=parts[1];
// 	  	  var repo=parts[2].toString().toUpperCase();
// 	  	  repo=repo.substring(0,repo.indexOf(".SER"));
// 	  	  html+='<tr><td style="cursor: hand;" onclick="loadThisSearch('+i+')"><p style="color: green">'+(i+1)+'# '+searchString+'</p>'+repo+'<br><a href="">'+timestamp+'</a></td></tr>';
//         		 }
//         	document.getElementById("pastSearches").innerHTML=html;


// 		}
// });
// }

// function loadThisSearch(searchNameID)
// {
// // alert("sn: "+names[searchNameID]);

// 	$.ajax({

// 						url: '/ajax/"+CLIENT_IP+"/loadThisSearch/'+searchNameID,
// 						async: true,
// 						success:function(data){
// 						// alert(data);
// 						var parts=data.split("<!-- vibhor -->");

// 						//alert(parts2[1]);
// 						 //alert(parts[1]);

// 						$('#main_card_body').html(parts[1]);
// 						$('#loader').attr("class", "card-body __loading");
// 						// getPastSearches();
// 						//$('#test').html(data);


// 				}
// 		});
// }
function remove_modal() {

    $("#warning").hide();
}

function isPresent(array,string){
    var flag=0;
    for(var i=0;i<array.length;i++){
        if(array[i]===string) {
            flag = 1;
            break;
        }
    }
    if(flag==1)
        return true;
    return false;

}


function GetTextboxCordinates(id) {

    var txt = document.getElementById(id);


    getPos(txt);
    //drawEdge(361,437,500,600);
    // alert("X:" + p.x + " Y:" + p.y);
}

function getPos(el) {
    // yay readability
    var midPoint = 15;
    var len = el.innerHTML.offsetLeft;
    var height = el.offsetHeight;
    for (var lx = 0, ly = 0;
         el != null;
         lx += el.offsetLeft, ly += el.offsetTop, el = el.offsetParent);
    // alert("gp  X:" + lx + " Y:" + ly);
    drawEdge(lx + len + 10, ly + height / 2, lx + len + 10, ly + height / 2 + 15);
    //return {x: lx,y: ly};
}
function drawEdge(ax, ay, bx, by) {

    if (ay > by) {
        bx = ax + bx;
        ax = bx - ax;
        bx = bx - ax;
        by = ay + by;
        ay = by - ay;
        by = by - ay;
    }
    var calc = Math.atan((ay - by) / (bx - ax));
    calc = calc * 180 / Math.PI;
    var length = Math.sqrt((ax - bx) * (ax - bx) + (ay - by) * (ay - by));
    document.body.innerHTML += "<div id='line' style='height:" + length + "px;width:1px;background-color:black;position:absolute;top:" + (ay) + "px;left:" + (ax) + "px;transform:rotate(" + calc + "deg);-ms-transform:rotate(" + calc + "deg);transform-origin:0% 0%;-moz-transform:rotate(" + calc + "deg);-moz-transform-origin:0% 0%;-webkit-transform:rotate(" + calc + "deg);-webkit-transform-origin:0% 0%;-o-transform:rotate(" + calc + "deg);-o-transform-origin:0% 0%;'></div>"
}
function printMousePos(event) {
    alert(event.clientX + "  " + event.clientY);

}

/******* For Filter Options *********/

var LList = [];
var RList = [];


function filterInit() {
    //    LList.push("/Batch/Proc/Source");
    // LList.push("/Cross_Pillar/procedures/source");
    // LList.push("/Financials/packages/source");

    var myURL = "/ajax/" + CLIENT_IP + "/filterOptions/DEFAULT/none";
    $.ajax({

        url: myURL,
        async: true,
        success: function (filtersData) {
            // console.log("filterInit: DEFAULT: "+filtersData["type"]);
            // console.log("filterInit: "+filtersData["PLS"]);
            // console.log("filterInit: "+filtersData["FMB"]);
            // console.log("filterInit: "+filtersData["OTHER"]);

            var PLS = filtersData["PLS"];
            for (var p in PLS) {
                if (!listContains("multiRight", PLS[p]))
                    LList.push(PLS[p]);
            }

            var FMB = filtersData["FMB"];
            for (var p in FMB) {
                if (!listContains("multiRight", FMB[p]))
                    LList.push(FMB[p]);
            }


            var OTHER = filtersData["OTHER"];
            for (var p in OTHER) {
                if (!listContains("multiRight", OTHER[p]))
                    LList.push(OTHER[p]);
            }

            populateList("multiLeft", LList);
            populateList("multiRight", RList);
        }
    });


}
function populateList(listId, list) {

    var List = document.getElementById(listId);
    for (var i = 0; i < list.length; i++) {
        var option = document.createElement("option");
        option.text = list[i];
        if (!listContains(listId, list[i])) {
            List.add(option);
        }
    }

}
function clearList(listId, list) {
    var List = document.getElementById(listId);
    $("#" + listId + " > option").each(function () {
        var $this = $(this);
        if ($this.length) {
            var selText = $this.text();
            var option = document.createElement("option");
            option.text = selText;
            List.remove(option);
        }
    });

}
var console_content = "";
function clearLeftConsole() {
    $("#left_console").html("");
}
function addToLeftConsole(heading, content) {

    //Does not contain submit button
    if (heading == "Notice")
        document.getElementById("left_console").innerHTML += "<p class='console_rows notice'>" + content + "</p>";
    else if (heading == "Error")
        document.getElementById("left_console").innerHTML += "<p class='console_rows error'>" + content + "</p>";
    else if (heading == "Victory")
        document.getElementById("left_console").innerHTML += "<p class='console_rows victory'>" + content + "</p>";
    else {
        //Contains submit button
//                document.getElementById("showPopUp").innerHTML="	<div class='col-lg-12' style='margin-bottom: 0px'><div class='modal fade in' id='warning' tabindex='-1' role='dialog' aria-labelledby='myModalLabel' style='display: block; padding-right: 17px;'><div id='makeitmove'  class='modal-dialog' style='margin-top: 10%;margin-left: 10%;margin-right: 10%;'><div class='modal-content'><div class='modal-header'><h4 class='modal-title'>"+heading+"</h4></div><div class='modal-body' style='overflow:auto;height:300px;word-wrap:break-word;'>"+content+"</div><div class='modal-footer'><button type='button' class='btn btn-sm btn-default' onclick='moreOptionsSubmit()' style='color:white'>Submit</button><button type='button' class='btn btn-sm btn-default' onclick='remove_modal()' style='color:white'>Close</button></div></div></div></div></div>";
    }
    //Keep selected filter options intact

    if (heading == "Filters") {

        if (filters != "") {
            console.log("filters: " + filters);
            var parts = filters.split("-");

            if (parts[0] == 1)
                $('#checkbox1').prop('checked', true);
            else
                $('#checkbox1').prop('checked', false);
            if (parts[1] == 1)
                $('#checkbox2').prop('checked', true);
            else
                $('#checkbox2').prop('checked', false);
            if (parts[2] == 1)
                $('#checkbox3').prop('checked', true);
            else
                $('#checkbox3').prop('checked', false);
            if (parts[3] == 1)
                $('#checkbox4').prop('checked', true);
            else
                $('#checkbox4').prop('checked', false);
            if (parts[4] == 1)
                $('#checkbox5').prop('checked', true);
            else
                $('#checkbox5').prop('checked', false);


        }

    }

    console_content = $("#left_console").html();
}

function restoreLeftConsoleData() {
    $("#left_console").html(console_content);
}
function scrollToBottom() {
    //To keep scroll bar bottom for left_console
    if ($('.console_rows').length > 0)
        $('#left_console').scrollTop($('.console_rows')[$('.console_rows').length - 1].scrollHeight * ($('.console_rows').length))
    //To keep scroll bar bottom for current_path
    if ($('.step').length > 0)
        $('#current_path').scrollTop($('.step')[$('.step').length - 1].scrollHeight * ($('.step').length));
}
function scrollToRight(id) {
    $(id).scrollLeft($(id).width());
}
function moreOptions() {

    // var heading="<div class='row'><div class='col-md-12' style='color: #7a8690;'><b>By Directory </b></div></div>";
    var heading = "<div class='row'><div class='col-md-12' style='color: #7a8690;'><b></b></div></div>";

    var byType = "<div class='row'><div class='col-md-12' style='color: #7a8690;'><b>By Type </b></div><br><div class='col-md-4'></div><div class='col-md-4' style='border: 2px solid #f2f5f7;'>" +
        "<div class='checkbox' style='text-align:'>" +
        "<input type='checkbox' id='checkbox1'>" +
        "<label for='checkbox1'>Packages <b>[pls]</b>" +
        "</label>" +
        "</div>" +
        "<div class='checkbox'>" +
        "<input type='checkbox' id='checkbox2'>" +
        "<label for='checkbox2'>Forms <b>[fmb,mmb]</b>" +
        "</label>" +
        "</div>" +
        "<div class='checkbox'>" +
        "<input type='checkbox' id='checkbox3'>" +
        "<label for='checkbox3'>Batches <b>[pc]</b>" +
        "</label>" +
        "</div>" +
        "<div class='checkbox'>" +
        "<input type='checkbox' id='checkbox4'>" +
        "<label for='checkbox4'>Triggers <b>[trg]</b>" +
        "</label>" +
        "</div>" +
        "<div class='checkbox'>" +
        "<input type='checkbox' id='checkbox5'>" +
        "<label for='checkbox5'>Other <b>[h,mk,c,ksh,d,lib,sql,ctl,tmp,SQL,dat,cfg,seq,pll]</b>" +
        "</label>" +
        "</div>" +
        "</div><div class='col-md-4'></div><br></div>";


    var byDirLeft = "<div class='col-md-6' style='color: #7a8690;'><b>All </b></div><div class='col-md-3' style='color: #7a8690;'><b>Selected </b></div>" +
        "<div class='col-md-6' style='border: 2px solid #f2f5f7;padding: 2%;'>" +
        "<select id='multiLeft' style='height: 80%;min-width: 100%;' multiple>" +

        "</select>" +
        "<div class='row'><button type='button' class='btn btn-sm btn-default' onclick='L2RSingle()' style='color:white;margin: 5px;background-color: rgba(165, 155, 163, 0.86);'><b>></b></button>" +
        "<button type='button' class='btn btn-sm btn-default' onclick='L2RMulti()' style='color:white;margin: 5px;background-color: rgba(165, 155, 163, 0.86);'><b>>></b></button></div>" +

        "</div>";

    var byDirRight = "<div class='col-md-6' style='border: 2px solid #f2f5f7;padding: 2%;'>" +
        "<select id='multiRight' style='height: 80%;min-width: 100%;' multiple>" +

        "</select>" +
        "<div class='row'><button type='button' class='btn btn-sm btn-default' onclick='R2LMulti()' style='color:white;margin: 5px;background-color: rgba(165, 155, 163, 0.86);'><b><<</b></button>" +
        "<button type='button' class='btn btn-sm btn-default' onclick='R2LSingle()' style='color:white;margin: 5px;background-color: rgba(165, 155, 163, 0.86);'><b><</b></button></div>" +
        "</div>";

    // var content=heading+byDirLeft+byDirRight+byType;
    var content = heading + byType;
    addToLeftConsole("Filters", content);
    filterInit();
    //window.location="/ajax/"+CLIENT_IP+"/index";
}

var filters = "1-1-1-1-1-";   //initially
function moreOptionsSubmit() {
    //first four for type then follow the directories
    filters = "";
    var packages_sel = document.getElementById("checkbox1").checked ? 1 : 0;
    var forms_sel = document.getElementById("checkbox2").checked ? 1 : 0;
    var batches_sel = document.getElementById("checkbox3").checked ? 1 : 0;
    var triggers_sel = document.getElementById("checkbox4").checked ? 1 : 0;
    var others_sel = document.getElementById("checkbox5").checked ? 1 : 0;

    filters += (packages_sel) + "-";
    filters += (forms_sel) + "-";
    filters += (batches_sel) + "-";
    filters += (triggers_sel) + "-";
    filters += (others_sel) + "-";

    // var directories_sel=document.getElementById("multiRight").options;
    // for(var i=0;i<directories_sel.length;i++)
    //   {
    //   	var path=directories_sel[i].text;
    //   	while(path.indexOf("/")>=0)
    //   	{
    //   		path=path.replace("/","+")
    //   	}

    //   	filters+=path+"-";
    //   }

    // console.log("Filters: "+filters)
    var myURL = "/ajax/" + CLIENT_IP + "/filterOptions/USER-SET/" + filters;
    $.ajax({

        url: myURL,
        async: true,
        success: function (filtersData) {
            // console.log("filterInit: USER-SET: "+filtersData["type"]);
            // console.log("filterInit: "+filtersData["PLS"]);
            // console.log("filterInit: "+filtersData["FMB"]);
            // console.log("filterInit: "+filtersData["OTHER"]);

        }
    });
    remove_modal();

}


function L2RSingle() {

    var rightList = document.getElementById("multiRight");
    var leftList = document.getElementById("multiLeft");
    $("#multiLeft option:selected").each(function () {
        var $this = $(this);
        if ($this.length) {
            var selText = $this.text();
            var option = document.createElement("option");
            option.text = selText;

            if (!listContains("multiRight", selText)) {


                LList.splice(LList.indexOf(selText), 1);
                RList.push(selText);


            }

        }
    });
    clearList("multiLeft", LList);
    clearList("multiRight", RList);

    populateList("multiLeft", LList);
    populateList("multiRight", RList);

}

function R2LSingle() {

    var rightList = document.getElementById("multiRight");
    var leftList = document.getElementById("multiLeft");
    $("#multiRight option:selected").each(function () {
        var $this = $(this);
        if ($this.length) {
            var selText = $this.text();
            var option = document.createElement("option");
            option.text = selText;

            if (!listContains("multiLeft", selText)) {


                RList.splice(RList.indexOf(selText), 1);
                LList.push(selText);

                clearList("multiLeft", LList);
                clearList("multiRight", RList);

                populateList("multiLeft", LList);
                populateList("multiRight", RList);

            }

        }
    });

}
function L2RMulti() {

    var rightList = document.getElementById("multiRight");
    var leftList = document.getElementById("multiLeft");

    for (var i = 0; i < LList.length; i++) {

        var option = document.createElement("option");
        option.text = LList[i];

        if (!listContains("multiRight", LList[i]))
            RList.push(LList[i]);

    }
    clearList("multiRight", RList);
    populateList("multiRight", RList);
    clearList("multiLeft", LList);
    LList.splice(0, LList.length);

}
function R2LMulti() {

    var rightList = document.getElementById("multiRight");
    var leftList = document.getElementById("multiLeft");

    for (var i = 0; i < RList.length; i++) {

        var option = document.createElement("option");
        option.text = RList[i];

        if (!listContains("multiLeft", RList[i]))
            LList.push(RList[i]);

    }
    clearList("multiLeft", LList);
    populateList("multiLeft", LList);
    clearList("multiRight", RList);
    RList.splice(0, RList.length);

}
function listContains(listId, option) {

    var flag = false;
    $("#" + listId + " > option").each(function () {
        var $this = $(this);
        if ($this.length) {
            var selText = $this.text();
            if (selText == option)
                flag = true;


        }
    });
    return flag;
}


/***** Refresh page *****************************/

function refreshPage() {
    window.location.replace('/');
}


/***** mainSearch on the left and right both ****/

var percent, selRepo, callElapsed, searchString, level_count1, key1, func_title1, type1;
var allSearchStrings = [];                              //keeps track of strings searched till now
function mainSearch(type, level_count, key, func_title) {
    type1 = type;
    level_count1 = level_count;
    key1 = key;
    func_title1 = func_title;
    alreadySearchedFlag = 0;
    $('#repoList').attr('style', 'border: 1px solid #c8d1d3');
    $('#searchString').attr('style', 'border: 1px solid #c8d1d3');


    //Default Filters


    var content;
    $('#search_duration').html("00:00:00");
    var myURL, flag = 0;
    percent = 0;
    searchString = document.getElementById("searchString").value.trim();

    selRepo = $("#repoList option:selected").text();


    if (type == "left") {
        console_content_array = [];  //Re-initialize this array for left_console
        allSearchStrings = []; // For a new search it should be empty
        if (selRepo == "Please Select.." && searchString.length == 0) {
            content = "Missing: Search String & Release";
            $('#repoList').attr('style', 'border:2px solid red');
            $('#searchString').attr('style', 'border:2px solid red');
            // addToLeftConsole("Error",content);
        }
        else if (selRepo == "Please Select..") {
            content = "Missing: Release";
            $('#repoList').attr('style', 'border:2px solid red');
            // addToLeftConsole("Error",content);
        }
        else if (searchString.length == 0) {
            content = "Missing: Search String";
            $('#searchString').attr('style', 'border:2px solid red');
            //addToLeftConsole("Error",content);
        }
        else {
            $("#searchString").attr('style', '1px solid #c8d1d3');
            $("#repolist").attr('style', '1px solid #c8d1d3');
            $("#current_path_area").html("");
            document.getElementById("main_search_button").disabled = true;
            document.getElementById("searchString").disabled = true;
            document.getElementById("repoList").disabled = true;
            myURL = "/ajax/" + CLIENT_IP + "/mainSearch/2/" + searchString + "/" + selRepo;  //if left
            flag = 1;
        }
    }// end if type left
    else {

        myURL = "/ajax/" + CLIENT_IP + "/newsearch/" + level_count + "/" + key + "-" + func_title; // if not left
        flag = 1;

    }

    if (flag == 1) {
        flag = 0;
//                alreadySearchedFlag=false;
        initiateSearching(myURL, type, level_count, key, func_title);

    }//flag
}


// Class name "title" is reserved
function getSearchStringAt(position) {
    var allSearchedStrings = document.getElementsByClassName("title");
    if (position == "last") {
        return allSearchedStrings[allSearchedStrings.length - 1].textContent;
    } else if (position == "first") {
        return allSearchedStrings[0].textContent;
    } else {
        return allSearchedStrings[position].textContent;
    }
    return null;
}

var alreadySearchedString = false;
function initiateSearching(myURL, type, level_count, key, func_title) {


    //Initiates the search whether new or main
    $.ajax({

        url: myURL,
        async: true,
        success: function (data) {

            if (data.indexOf("Already searched") >= 0) {
                addToLeftConsole("Error", data);
                forceStop = 1;
                alreadySearchedString = true;
                return;
            } else {
                alreadySearchedString = false;
            }
        }
    });

    myURL = "/ajax/" + CLIENT_IP + "/progress";
    $.ajax({

        url: myURL,
        async: true,
        success: function (data) {

            var loader_icon = '<div class="loader-container text-center">' +
                '<div class="icon">' +
                '<div class="sk-wave">' +
                '<div class="sk-rect sk-rect1">' + '</div>' +
                '<div class="sk-rect sk-rect2">' + '</div>' +
                '<div class="sk-rect sk-rect3">' + '</div>' +
                '<div class="sk-rect sk-rect4">' + '</div>' +
                '<div class="sk-rect sk-rect5">' + '</div>' +
                '</div>' +
                '</div>' +
                '<div class="title" id="loader_title"></div>' +
                '</div>';
            $('#loader').attr("class", "card-body __loading");
            $('#loader_icon').html(loader_icon);
            $('#loader_icon1').html(loader_icon);
            // $('#loader_title').html(progressString);
            var call = setInterval(function () {
                getProgress(call, type, level_count, key, func_title)
            }, 500);
            //console.log("main : percent: "+percent);
            $("#result_main_panel").css("visibility", "");


        }
    });


    //for elapsed time
    callElapsed = setInterval(function () {
        getElapsedSearchTime()
    }, 500);

    $('#progress_bar').css('width', '0'); //Should come back to 0 after every search


}

/******** Progress Bar *********/


var zeroProgressCounter = 0, alreadySearchedFlag = 0;                 //If consecutive 10 times 0 progress, then force stop.
function getProgress(call, type, level_count, key, func_title) {
    var myURL = "/ajax/" + CLIENT_IP + "/progress";

    $.ajax({

        url: myURL,
        async: true,
        success: function (data) {
            //alert(data);
            var currSearchString = "";
            var typeOfSearch = ""; // level_1 or level>1
            var progressString = "";
            console.log("Progress:  " + data + "  zeroProgressCounter: " + zeroProgressCounter);
            if (data === "-1" || data == "alreadySearched") {

                forceStop = 1;
                alreadySearchedFlag = 1;
                console.log("force stop: " + forceStop + "  zeroProgressCounter: " + zeroProgressCounter);
                zeroProgressCounter = 0;
                return;
            }

            percent = Number(data.split(":")[0]);
            currSearchString = data.split(":")[1];
            typeOfSearch = data.split(":")[2];
            $('#progress_bar').css('width', percent + '%');


            if (searchString != "")
                progressString = "Searching <b>" + searchString + "</b> in <b>'" + selRepo + "'</b> (" + percent.toFixed(2) + "%)";
            else
                progressString = "Searching <b>" + currSearchString + "</b> in <b>'" + selRepo + "'</b> (" + percent.toFixed(2) + "%)";

            $('#loader_title').html(progressString);
            //console.log("percent2: "+percent);


        }
    });


    if (percent >= 100 || forceStop == 1) {
        console.log("inside");
        forceStop = 0;
//                document.getElementById("main_search_button").disabled = false;
        document.getElementById("stop_search_button").disabled = true;
        clearInterval(call);
        $('#loader').attr("class", "");
        $('#loader_icon').html("");


        //window.location="/ajax/"+CLIENT_IP+"/mainSearch/2/"+searchString+"/"+selRepo;
        $.ajax({

            url: "/ajax/" + CLIENT_IP + "/test",
            async: true,
            success: function (data) {

                clearInterval(callElapsed);  //stop polling for elapsed time
                   data=JSON.parse(data);
                // var parts = data.split("<!-- vibhor -->");

                // if (parts[1].includes("No results found for this string")) {

                //When lastSearchStringAnyResult = false, no result for last search string
                if(!data["lastSearchStringAnyResult"]){
                    // addToLeftConsole("Notice","<h3>No further mention for this string</h3>");
                    var notFoundMessage = '<div class="alert alert-danger  alert-dismissible" role="alert" style="margin-top: 5%;background-color: rgba(249, 28, 8, 0.81);color: white;">No Results Found For This String!</div>';
//                      $('#area_main_card').html(notFoundMessage);

                    if (type1 == 'left') //First main search moment
                    {
                        $('#area_result_card').html('');
                        document.getElementById("main_new_search").disabled = true;
                        document.getElementById("searchString").disabled = false;
                        document.getElementById("repoList").disabled = false;
                        document.getElementById("main_search_button").disabled = false;
                        document.getElementById("main_new_search").innerHTML = new_search_btn_msg;

                    }
                    // var level="@level_count";
                    console.log("type: " + type1 + " level_count: " + level_count1 + " key: " + key1 + "  func_title: " + func_title1);
                    if (!alreadySearchedString) {
                        if (level_count1 > 1) {

                            var myURL = "/ajax/" + CLIENT_IP + "/pathterminators/" + level_count1 + "/" + key1 + "-" + func_title1;
                            $.ajax({

                                url: myURL,
                                async: true,
                                success: function (data) {
                                    data=JSON.parse(data);
                                    // var parts = data.split("<!-- vibhor -->");
                                    // $('#main_card_body').html(parts[1]);
                                    updateBody(data);
                                    attachFocusOutListeners();
                                    // populateReleaseList();
                                    disableLeftPanel(); // Should disable the left panel
                                    restoreLeftConsoleData();
                                    addToLeftConsole("Victory", "PATH FOUND!!");
                                    $('#search_duration').html(elapsedTimeValue);
                                    scrollToBottom(); //Scroll Top and Left areas to bottom
                                    scrollToRight("#main_area"); //Scroll Rightmost in main area
                                }
                            });

                        } else {
                            addToLeftConsole("Error", "Not found - '" + searchString + "'");
                        }
                    }

                }
                else {
                    // This is the part that updates the main area result
                    updateBody(data);
                    // $('#main_card_body').html(parts[1]);
                    document.getElementById("main_new_search").disabled = false;
                    document.getElementById("main_search_button").disabled = true;
                    restoreLeftConsoleData();
                    if (!alreadySearchedString)
                        addToLeftConsole("Notice", "Success - '" + getSearchStringAt("last") + "'");

                }

                // getPastSearches();
                // populateReleaseList();
                scrollToBottom(); //Scroll Top and Left areas to bottom
                scrollToRight("#main_area"); //Scroll to Right main area

                // if (selRepo) {
                //     selectRelease(selRepo);
                // }
                $('#search_duration').html(elapsedTimeValue);


            }
        });

    }
    else {
//                document.getElementById("main_search_button").disabled = true;
        document.getElementById("stop_search_button").disabled = false;
    }

}

/*********** Stop this search ************/
var elapsedTimeValue;
var forceStop = 0;
function stopSearching() {
    document.getElementById("main_search_button").disabled = false;
    document.getElementById("stop_search_button").disabled = true;
    console.log("Stopping this search");

    //To stop ajax call
    forceStop = 1;
    removeLoader();

    var myURL = "/ajax/" + CLIENT_IP + "/stopSearching";
    $.ajax({

        url: myURL,
        async: true,
        success: function (data) {


            // var parts=data.split("<!-- vibhor -->");


            // 			clearInterval(callElapsed);//stop polling for elapsed time

            // 			$('#main_card_body').html(parts[1]);
            // 			$('#search_duration').html(elapsedTimeValue);


            // 			// getPastSearches();


        }
    });
}

/***** get elapsed time in this search ****/
function getElapsedSearchTime() {
    var myURL = "/ajax/" + CLIENT_IP + "/getElapsedTime";
    $.ajax({

        url: myURL,
        async: true,
        success: function (data) {

            elapsedTimeValue = data;
            console.log("elapsed: " + elapsedTimeValue);

            $('#search_duration').html(elapsedTimeValue);

        }
    });
}

function updateBody(data){
    var FILE_DIRECTORY_PATH=data["FILE_DIRECTORY_PATH"];
    var selectedRepository=data["selectedRepository"];
    var selectRepoList=data["selectRepoList"];
    var FINAL_ALL_PATHS=data["FINAL_ALL_PATHS"];
    var colorSelectedFunctions=data["colorSelectedFunctions"];
    var displayTillLevel=data["displayTillLevel"];
    var CURRENT_PATH=data["CURRENT_PATH"];
    var ALL_SEARCH_STRINGS=data["ALL_SEARCH_STRINGS"];
    var ALL_REFINED_HM=data["ALL_REFINED_HM"];
    var ALL_TYPE_COUNTS=data["ALL_TYPE_COUNTS"];



    updateLeftPanel(CURRENT_PATH,selectRepoList,selectedRepository);
    updateCurrentPathArea(CURRENT_PATH,displayTillLevel);
    updateMainCardBody(FINAL_ALL_PATHS,ALL_SEARCH_STRINGS,CURRENT_PATH,ALL_TYPE_COUNTS,ALL_REFINED_HM,displayTillLevel,colorSelectedFunctions);

}

function updateLeftPanel(CURRENT_PATH,selectRepoList,selectedRepository) {
    // Left Panel
    if(CURRENT_PATH.length > 0){
        $('#searchstring_area').attr("placeholder",CURRENT_PATH[0]);
    }else{
        $('#searchstring_area').attr("placeholder","");
    }

    $('#repoList').html('');
    for(var index in selectRepoList){
        var dir = selectRepoList[index];
        var repoName=dir.substring(dir.indexOf("-")+1,dir.length);
        var option;
        if(selectedRepository != null){
            if(selectedRepository == repoName){
                option = '<option value="'+dir+'" selected>'+repoName+'</option>';
            }else{
                option = '<option value="'+dir+'">'+repoName+'</option>';
            }
        }else{
            option = '<option value="'+dir+'">'+repoName+'</option>';
        }
        $('#repoList').append(option);
    }

}

function updateCurrentPathArea(CURRENT_PATH,displayTillLevel) {
    var counter = 1, level_count = 1, cur_count = 1;
    var htmlString = "";
    htmlString += '<ul class="nav nav-tabs" role="tablist" style="margin-bottom: 0px;">';
    for (var index in CURRENT_PATH) {
        var curr_string = CURRENT_PATH[index];
        if (level_count <= displayTillLevel) {
            if (counter <= 4) {
                htmlString += '<li role="step">' +
                    '<a href="#step1" id="step1-tab" role="tab"  aria-controls="home" aria-expanded="true" style="padding-bottom: 0px;padding-top: 0px;">' +
                    '<div class="icon">' + cur_count + '</div>' +
                    '<div class="heading">' +
                    '<div class="title">' + curr_string + '</div>';
                if (level_count == 1) {
                    htmlString += '<div class="description">Search String</div>';
                } else {
                    if (curr_string.substring(curr_string.lastIndexOf(".") + 1, curr_string.length) == "fmb") {
                        htmlString += '<div class="description">Form</div>';
                    }
                    if (curr_string.substring(curr_string.lastIndexOf(".") + 1, curr_string.length).length > 3) {

                        htmlString += '<div class="description">Package</div>';

                    }
                    if (curr_string.substring(curr_string.lastIndexOf(".") + 1, curr_string.length) == "trg") {
                        htmlString += '<div class="description">Trigger</div>';
                    }
                    if (curr_string.substring(curr_string.lastIndexOf(".") + 1, curr_string.length) == "pc") {
                        htmlString += '<div class="description">Batches</div>';
                    }
                }
                htmlString += '</div>'+
                    '</a>'+
                    '</li>';
                counter++;
            }else {
                counter = 2;
                htmlString += '</ul>';
                htmlString += '  <ul class="nav nav-tabs " role="tablist" style="margin-bottom: 0px;margin-top: 10px;">' +
                    '<li role="step">' +
                    '<a href="#step1" id="step1-tab" role="tab"  aria-controls="home" aria-expanded="true" style="padding-bottom: 0px;padding-top: 0px;">' +
                    '<div class="icon">' + cur_count + '</div>' +
                    '<div class="heading">' +
                    '<div class="title">' + curr_string + '</div>';
                if (level_count == 1) {
                    htmlString += '<div class="description">Search String</div>';

                } else {
                    if (curr_string.substring(curr_string.lastIndexOf(".") + 1, curr_string.length) == "fmb") {
                        htmlString += '<div class="description">Form</div>';
                    }
                    if (curr_string.substring(curr_string.lastIndexOf(".") + 1, curr_string.length).length > 3) {

                        htmlString += '<div class="description">Package</div>';

                    }
                    if (curr_string.substring(curr_string.lastIndexOf(".") + 1, curr_string.length) == "trg") {
                        htmlString += '<div class="description">Trigger</div>';
                    }
                    if (curr_string.substring(curr_string.lastIndexOf(".") + 1, curr_string.length) == "pc") {
                        htmlString += '<div class="description">Batches</div>';
                    }
                }

                htmlString += '</div>'+
                '</a>'+
                '</li>';
            }
            cur_count++;
            level_count++;
        }
    }
    htmlString += '</ul>';
    $('#current_path_area').html(htmlString);

}

function updateMainCardBody(FINAL_ALL_PATHS,ALL_SEARCH_STRINGS,CURRENT_PATH,ALL_TYPE_COUNTS,ALL_REFINED_HM,displayTillLevel,colorSelectedFunctions) {

    // View Path Flow button
    var viewPathFlowsBtn = $('#view_path_flows_btn');
    if (FINAL_ALL_PATHS.hasOwnProperty(1)) {
        viewPathFlowsBtn.prop('disabled', false);
        viewPathFlowsBtn.click(function () {
            var url = '/ajax/' + CLIENT_IP + '/graph';
            window.open(url);
        });
        viewPathFlowsBtn.addClass("btn-vibrate");
    } else {
        viewPathFlowsBtn.prop('disabled', true);
        viewPathFlowsBtn.click(function () {
        });
        viewPathFlowsBtn.removeClass("btn-vibrate");
    }


    // Area main card
    var htmlString = "";
    var main_index = -1;
    if (ALL_SEARCH_STRINGS.length == 0) {
        htmlString += '<div class="col-md-12 col-sm-12">' +
            '<div class="alert alert-danger  alert-dismissible" role="alert" style="    background-color: rgba(212, 59, 138, 0.23)">' +
            '<strong>Welcome!</strong> Find your location in the digital world.' +
            '</div>' +
            '</div>' +
            '<div class="col-md-12 col-sm-12">' +
            '<div class="alert alert-danger  alert-dismissible" role="alert" style="margin-top:0px;background-color:rgba(154, 30, 220, 0.16)">' +
            'Enter the exact search string and select the repository you want to search in and get on with finding the levels of your pathflow.' +
            '</div>' +
            '</div>' +
            '<div class="col-md-12 col-sm-12">' +
            '<div class="alert alert-danger  alert-dismissible" role="alert" style="margin-top:0px;    background-color: rgba(108, 127, 249, 0.23)">' +
            'A Google Map for the digital world!' +
            '</div>' +
            '</div>';
    }

    $('#area_main_card').html(htmlString);





    //Area result card
    var level_count = 1;
    htmlString = "";
    if (ALL_SEARCH_STRINGS.length != 0) {
        for (var index in ALL_SEARCH_STRINGS) {
            var search_string = ALL_SEARCH_STRINGS[index];
            main_index++;
            if (level_count <= CURRENT_PATH.length) {
                if (CURRENT_PATH[level_count - 1] == search_string && level_count <= displayTillLevel) {
                    if (ALL_TYPE_COUNTS[main_index]["pls"] == "0" && ALL_TYPE_COUNTS[main_index]["fmb"] == "0" && ALL_TYPE_COUNTS[main_index]["pc"] == "0" && ALL_TYPE_COUNTS[main_index]["trg"] == "0" && ALL_TYPE_COUNTS[main_index]["h"] == "0") {

                        htmlString += '<div class="col-md-12 col-sm-12" hidden>' +

                            '<div class="alert alert-danger  alert-dismissible" role="alert" style="background-color: rgba(212, 59, 138, 0.23)"><i class="fa fa-warning"></i>' +
                            '<p>No results found for this string</p>' +
                            '</div>' +
                            '</div>';


                    } else {
                        htmlString += '<div class="area_result_elements">' +
                            '<div class="row no-gap">' +
                            '<br>' +
                            '<h4 align="middle">Level ' + level_count + '</h4>';
                        level_count++;

                        htmlString += '</div>' +
                            '<!-- PACKAGES -->' +
                            '<div class="panel panel-default">' +
                            '<div class="panel-heading" onclick="handleCollapse(id)" id="#collapse_pkg_' + level_count + '">' +
                            '<h4 class="panel-title">' +
                            '<pre class="tab"><sup>' + ALL_TYPE_COUNTS[main_index]["pls"] + '</sup><span> Packages</span></pre>' +
                            '</h4>' +
                            '</div>' +
                            ' <div id="collapse_pkg_' + level_count + '" class="panel-collapse collapsed" >';
                        var func_count = 1;
                        for (var key in ALL_REFINED_HM[main_index]) {


                            if (ALL_REFINED_HM[main_index][key] != null) {

                                htmlString += '<div class="panel-body">' +
                                    '<div class="col-md-12 col-sm-2 col-inside">' +
                                    '<div class="panel panel-default">' +
                                    '<div class="panel-heading" onclick="handleCollapse(id)" id="#collapse_' + level_count + '+' + func_count + '" >' +
                                    '<h4 class="panel-title-nest">' +
                                '<pre class="tab"><sup>(' + ALL_REFINED_HM[main_index][key].length + ')</sup><span> ' + key + '</span></pre>' +
                                '</h4>' +
                                '</div>';

                                if (ALL_REFINED_HM[main_index][key].length > 0) {
                                    htmlString += '<div id="collapse_' + level_count + '+' + func_count + '" class="panel-collapse collapsed">';
                                } else {
                                    htmlString += '<div id="collapse_' + level_count + '+' + func_count + '" class="panel-collapse collapse">';
                                }

                                for (var index1 in ALL_REFINED_HM[main_index][key]) {
                                    var func_title = ALL_REFINED_HM[main_index][key][index1];
                                    if (colorSelectedFunctions.length != 0) {
                                        if (isPresent(colorSelectedFunctions,((level_count - 1) + "-" + key + "-" + func_title))) { //problem


                                            htmlString += '<div class="panel-body selected" id="' + key + '-' + func_title + '">' +
                                                '<a onclick="mainSearch(\'new\',\'' + level_count + '\',\'' + key + '\',\'' + func_title + '\')">' + func_title + '</a>' +
                                                '</div>';
                                        } else {

                                            htmlString += '<div class="panel-body" id="' + key + '-' + func_title + '">' +
                                                '<a onclick="mainSearch(\'new\',\'' + level_count + '\',\'' + key + '\',\'' + func_title + '\')">' + func_title + '</a>' +
                                                '</div>';
                                        }
                                    } else {
                                        htmlString += '<div class="panel-body" id="' + key + '-' + func_title + '">' +
                                            '<a onclick="mainSearch(\'new\',\'' + level_count + '\',\'' + key + '\',\'' + func_title + '\')">' + func_title + '</a>' +
                                            '</div>';
                                    }

                                    func_count++;
                                }
                                htmlString += '<div class="panel-footer" id="'+key+'" onclick="viewFile(id)">' +
                                    '<a href="/ajax/'+ajax_CLIENT_IP+'/showFileContents/'+key+'" target="_blank">View File</a>' +
                                    '</div>' +
                                    '</div>' +
                                    '</div>' +
                                    '</div>' +
                                    '</div>';

                            } //end if

                        }// end for

                        htmlString += '</div>' +
                            '</div>';


                        <!-- FORMS -->


                        htmlString += '<div class="panel panel-default">' +
                            '<div class="panel-heading" onclick="handleCollapse(id)" id="#collapse_forms_' + level_count + '">' +
                            '<h4 class="panel-title">' +
                            '<pre class="tab"><sup>(' + ALL_TYPE_COUNTS[main_index]["fmb"] + ')</sup><span> Forms</span></pre>' +
                            '</h4>' +
                            '</div>';
                        if (ALL_TYPE_COUNTS[main_index]["fmb"].length > 0) {
                            htmlString += '<div id="collapse_forms_' + level_count + '" class="panel-collapse collapsed">';
                        } else {
                            htmlString += '<div id="collapse_forms_' + level_count + '" class="panel-collapse collapse">';
                        }

                        for (var key in ALL_REFINED_HM[main_index]) {


                            if (key.indexOf(".fmb") >= 0) {

                                if (colorSelectedFunctions.length != 0) {

                                    if (isPresent(colorSelectedFunctions,((level_count - 1) + "-" + key))) { //problem

                                        htmlString += '<div class="panel-body selected" id="'+key+'">' +
                                            '<div onclick="pathterminator(\''+ajax_CLIENT_IP+'\',\''+level_count+'\',\''+key+'\')">'+key+'</div>' + '</div>';
                                    } else {
                                        htmlString += '<div class="panel-body" id="' + key + '">' +
                                        '<div onclick="pathterminator(\'' + ajax_CLIENT_IP + '\',\'' + level_count + '\',\'' + key + '\')">' + key + '</div>' +
                                        '</div>';
                                    }
                                } else {
                                    htmlString += '<div class="panel-body" id="' + key + '">' +
                                        '<div onclick="pathterminator(\'' + ajax_CLIENT_IP + '\',\'' + level_count + '\',\'' + key + '\')">' + key + '</div>' +
                                        '</div>';
                                }
                            }
                        }
                        <!-- if  of fmb ends here -->
                        <!-- for ends here -->
                        htmlString += '</div>' +
                            '</div>';


                        <!-- BATCHES -->
                        htmlString += '<div class="panel panel-default">' +
                            '<div class="panel-heading" onclick="handleCollapse(id)" id="#collapse_batches_' + level_count + '">' +
                            '<h4 class="panel-title">' +
                            '<pre class="tab"><sup>(' + ALL_TYPE_COUNTS[main_index]["pc"] + ')</sup><span> Batches</span> </pre>' +
                            '</h4>' +
                            '</div>';
                        if (ALL_TYPE_COUNTS[main_index]["pc"].length > 0) {
                            htmlString += '<div id="collapse_batches_' + level_count + '" class="panel-collapse collapsed">';
                        } else {
                            htmlString += '<div id="collapse_batches_' + level_count + '" class="panel-collapse collapse">';
                        }
                        for (var key in ALL_REFINED_HM[main_index]) {

                            if (key.indexOf(".pc") >= 0) {
                                if (colorSelectedFunctions.length != 0) {
                                    if (isPresent(colorSelectedFunctions,((level_count - 1) + "-" + key))) { //problem

                                        htmlString += '<div class="panel-body selected" id="' + key + '">' +
                                            '<div onclick="pathterminator(\'' + ajax_CLIENT_IP + '\',\'' + level_count + '\',\'' + key + '\')">' + key + '</div>' +
                                            '<a href="/ajax/' + ajax_CLIENT_IP + '/showFileContents/' + key + '" target="_blank"><i class="fa fa-file-code-o" style="margin-left:10px"></i></a>' +
                                            '</div>';
                                    } else {
                                        htmlString += '<div class="panel-body" id="' + key + '">' +
                                            '<div onclick="pathterminator(\'' + ajax_CLIENT_IP + '\',\'' + level_count + '\',\'' + key + '\')">' + key + '</div>' +
                                            '<a href="/ajax/' + ajax_CLIENT_IP + '/showFileContents/' + key + '" target="_blank"><i class="fa fa-file-code-o" style="margin-left:10px"></i></a>'+
                                        '</div>';
                                    }
                                } else {
                                    htmlString += '<div class="panel-body" id="' + key + '">' +
                                        '<div onclick="pathterminator(\'' + ajax_CLIENT_IP + '\',\'' + level_count + '\',\'' + key + '\')">' + key + '</div>' +
                                        '<a href="/ajax/' + ajax_CLIENT_IP + '/showFileContents/' + key + '" target="_blank"><i class="fa fa-file-code-o" style="margin-left:10px"></i></a>' +
                                        '</div>';
                                }
                            }
                            <!-- if ends here -->
                        }
                        <!-- for ends here -->

                        htmlString += '</div>' +
                            '</div>';


                        <!-- TRIGGERS -->
                        htmlString += '<div class="panel panel-default">' +
                            '<div class="panel-heading" onclick="handleCollapse(id)" id="#collapse_trg_' + level_count + '">' +
                            '<h4 class="panel-title">' +
                            '<pre class="tab"><sup>(' + ALL_TYPE_COUNTS[main_index]["trg"] + ')</sup><span> Triggers</span></pre>' +
                            '</h4>' +
                            '</div>';
                        if (ALL_TYPE_COUNTS[main_index]["trg"].length > 0) {
                            htmlString += '<div id="collapse_trg_' + level_count + '" class="panel-collapse collapsed">';
                        } else {
                            htmlString += '<div id="collapse_trg_' + level_count + '" class="panel-collapse collapse">';
                        }
                        for (var key in ALL_REFINED_HM[main_index]) {

                            if (key.indexOf(".trg") >= 0) {
                                if (colorSelectedFunctions.length != 0) {
                                    if (isPresent(colorSelectedFunctions,((level_count - 1) + "-" + key))) { //problem

                                        htmlString += '<div class="panel-body selected" id="' + key + '">' +
                                            '<div onclick="pathterminator(\'' + ajax_CLIENT_IP + '\',\'' + level_count + '\',\'' + key + '\')">' + key + '</div>' +
                                            '<a href="/ajax/' + ajax_CLIENT_IP + '/showFileContents/' + key + '"target="_blank"><i class="fa fa-file-code-o" style="margin-left:10px"></i></a>'+
                                        '</div>';
                                    } else {
                                        htmlString += '<div class="panel-body" id="' + key + '">' +
                                            '<div onclick="pathterminator(\'' + ajax_CLIENT_IP + '\',\'' + level_count + '\',\'' + key + '\')">' + key + '</div>' +
                                            '<a href="/ajax/' + ajax_CLIENT_IP + '/showFileContents/' + key + '" target="_blank"><i class="fa fa-file-code-o" style="margin-left:10px"></i></a>' +

                                            '</div>';
                                    }
                                } else {
                                    htmlString += '<div class="panel-body" id="' + key + '">' +
                                        '<div onclick="pathterminator(\'' + ajax_CLIENT_IP + '\',\'' + level_count + '\',\'' + key + '\')">' + key + '</div>' +
                                        '<a href="/ajax/' + ajax_CLIENT_IP + '/showFileContents/' + key + '" target="_blank"><i class="fa fa-file-code-o" style="margin-left:10px"></i></a>' +
                                        '</div>';
                                }
                            }
                            <!-- if ends here -->
                        }
                        <!-- for ends here -->
                        htmlString += '</div>' +
                            '</div>' +
                            '</div>';
                    } // end of else
                }


            }
            <!--end of search_string for -->
        }
        <!-- end of if -->
        htmlString += '</div>' +
            '</div>' +
            '</div>' +

            '</div>' +
            '</div><!-- area_result_card ends -->';
    }

    $('#area_result_card').html(htmlString);


}