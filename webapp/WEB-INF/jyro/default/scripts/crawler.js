importPackage(org.koiroha.jyro);

function receive(job){
	var thread = java.lang.Thread.currentThread();
	var loader = thread.getContextClassLoader();
	println(jyro);
	println(Jyro.VERSION);
	println(util.IO.getExtension("foo.txt"));
	println(new org.koiroga.jyro.lib.UserAgent());
	return;
}