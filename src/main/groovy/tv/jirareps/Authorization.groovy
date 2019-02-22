package tv.jirareps

class Authorization {
	static String getBasicAuth() {
		String userpass = System.properties.get("userpass")
		if(userpass == null)
			throw new RuntimeException("No system property 'userpass=<user>:<passwd>' defined. Could not connect.")
		String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
		return basicAuth;
	}
}
