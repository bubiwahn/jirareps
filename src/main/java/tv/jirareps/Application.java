package tv.jirareps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	
	private static void checkEnvironment () {
		Authorization.getBasicAuth();  // ensure that credentials are provided
	}
	
	public static void main(String[] args) {
		checkEnvironment();
        SpringApplication.run(Application.class, args);
    }
}
