importPackage(org.koiroha.jyro);

function receive(job){
	var job = Job.parse("greeting{from:\"javascript\"}");
	jyro.send("URL取得Java", job);
	println(Jyro.VERSION);
	println(new org.koiroga.jyro.lib.UserAgent());
	return;
}