package com.mycompany.reading_list_extension;

import java.io.IOException;
import java.util.Map;

/**
 *
 * @author bhats
 */
public class Reading_list_extension {

    public static void main(String[] args) throws IOException {
        System.out.println("Hello World!");
        UrlServer u = new UrlServer(8080);
        u.ServerListen();
//        Map<String, String> env = System.getenv();
//        env.forEach((key, value) -> System.out.println(key + " : " + value));

    }
}
