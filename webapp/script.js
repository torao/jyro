

function update(){
    $.ajax({
        type: "GET",
        url: "console/status",
        cache: false,
        success: function(result){
            var servers = eval(result);
            for(server in servers){
                for(instance in server.instances){
                    for(queue in instance.queues){
                    }
                    for(node in instance.nodes){
                    }
                }
            }
        }
    });
    return;
}

function reload(){
	$.get("api/status.html", function(html){
		$("#main").empty();
		$("#main").append(html);
	});
}

function post_job(job){
	$.post("api/post", job, function(){
		reload();
	});
}

$(window).load(reload);
