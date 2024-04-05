/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package moyeora.myapp;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.ibatis.javassist.Loader.Simple;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@PropertySource({"classpath:ncp-storage.properties","classpath:ncp-secret.properties"})
public class App {
    public static void main(String[] args) throws Exception {

        SpringApplication.run(App.class, args);
    };
}
