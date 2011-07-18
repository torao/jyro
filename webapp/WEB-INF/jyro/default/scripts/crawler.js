importPackage(org.koiroha.jyro);
function main(job){
	var thread = java.lang.Thread.currentThread();
	var loader = thread.getContextClassLoader();
	println(Jyro.VERSION);
	println(org.koiroha.jyro.util.IO.getExtension("foo.txt"));
	println(new org.koiroga.jyro.lib.UserAgent());
	return;
}