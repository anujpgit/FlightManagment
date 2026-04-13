package com.anuj.flight.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String home() {
        return """
            <html>
                <head>
                    <title>Flight Service</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f6f8;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            height: 100vh;
                        }
                        .box {
                            background: white;
                            padding: 25px 40px;
                            border-radius: 8px;
                            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
                            text-align: center;
                        }
                        h1 {
                            color: #1565c0;
                            margin-bottom: 10px;
                        }
                        p {
                            color: #555;
                            font-size: 14px;
                        }
                    </style>
                </head>
                <body>
                    <div class="box">
                        <h1>✈ Welcome to Flight Service</h1>
                        <p>Service deployed successfully using Docker</p>
                        <p>Running on AWS EC2</p>
                    </div>
                </body>
            </html>
        """;
    }

    @GetMapping("/health")
    public String health() {
        return "Flight Service is running";
    }
}
