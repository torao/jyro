
function reset_core(){
	var core = $("#core");
	core.empty();
	$.getJSON("api/", function(names){
		for(var i=0; i<names.length; i++){
			core.append("<option value=\"" + names[i] + "\">" + names[i] + "</option>");
		}
		reset_node();
	});
    return;
}

function reset_node(){
	var core = $("#core option:selected").val();
	var node = $("#node");
	node.empty();
	if(core != ""){
		$.getJSON("api/" + core, function(names){
			for(var i=0; i<names.length; i++){
				node.append("<option value=\"" + names[i] + "\">" + names[i] + "</option>");
			}
		});
	}
    return;
}

function post_job(){
	var core = $("#core option:selected").val();
	var node = $("#node option:selected").val();
	var job = $("#job").val();
	$.post("api/" + core + "/" + node, {"job":job}, function(){
		reload();
	});
}

$(window).load(reset_core);

