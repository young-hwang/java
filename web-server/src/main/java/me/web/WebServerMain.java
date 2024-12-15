package me.web;

import java.io.IOException;
import me.web.v1.HttpServerV1;
import me.web.v2.HttpServerV2;
import me.web.v3.HttpServerV3;
import me.web.v4.HttpServerV4;

public class WebServerMain {

    public static void main(String[] args) throws IOException {
//        new HttpServerV1(8080).start();
//        new HttpServerV2(8080).start();
//        new HttpServerV3(8080).start();
        new HttpServerV4(8080).start();
    }
}
