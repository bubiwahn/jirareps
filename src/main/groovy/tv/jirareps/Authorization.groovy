package tv.jirareps

class Authorization {
	static String getBasicAuth() {
		String userpass = "xxx" + ":" + "xxx";
		String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
		return basicAuth;
	}
}
