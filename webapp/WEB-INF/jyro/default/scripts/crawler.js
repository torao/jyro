importPackage(org.koiroha.jyro.lib);
function main(job){
	println("hello, world: " + job);
	var ua = new org.koiroha.jyro.lib.UserAgent.getInstance();
	println(ua);
	return;
}