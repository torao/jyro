
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
