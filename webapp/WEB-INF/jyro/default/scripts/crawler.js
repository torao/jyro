importPackage(org.koiroha.jyro);

function receive(job){
	var thread = java.lang.Thread.currentThread();
	var loader = thread.getContextClassLoader();
	var job = Job.parse("greeting{from:\"javascript\"}");

	println(jyro.send("URL取得Java", job));
	println(Jyro.VERSION);
	println(util.IO.getExtension("foo.txt"));
	println(new org.koiroga.jyro.lib.UserAgent());
	return;
}