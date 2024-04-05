package moyeora.myapp;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.ibatis.javassist.Loader.Simple;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@EnableTransactionManagement

@Controller
@PropertySource({"classpath:ncp-storage.properties","classpath:ncp-secret.properties"})
public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("과제관리 시스템 서버 1실행!");

        SpringApplication.run(App.class, args);
    }

    @GetMapping("/home")
    public void home() {
    }
}
