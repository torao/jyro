importPackage(org.koiroha.jyro.lib);
function main(job){
	println("hello, world: " + job);
	println(new java.lang.String("hello, world") + " this is a pen.");
	var ua = new org.koiroha.jyro.lib.UserAgent.getInstance();
	println(ua);
	return;
}