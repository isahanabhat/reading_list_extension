package com.mycompany.reading_list_extension;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import com.opencsv.CSVWriter;
import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 *
 * @author bhats
 */
public class UrlServer {

    public String url;
    public int sender_port;
    public int receiver_port;
    public Path csvPath;

    public UrlServer(int receiver_port) throws IOException {
        // this.sender_port = sender_port;
        this.receiver_port = receiver_port;
        
        String pathValue = System.getenv("DATA_DIR"); // get user's data path
        Path folderPath = Paths.get(pathValue, "reading_folder"); // reading list folder
        this.csvPath = Paths.get(pathValue, "reading_folder", "reading_list.csv"); // reading list csv
        
        if (Files.isDirectory(folderPath)) {
            System.out.println("Folder exists.");
            if (Files.isRegularFile(csvPath)) {
                System.out.println("CSV file exists.");
            } else {
                System.out.println("CSV file does not exists. Creating file...");
                createCSV(csvPath);
            }
        } else {
            System.out.println("Folder does not exist. Creating folder & CSV file...");
            Files.createDirectories(folderPath);
            System.out.println("Folder successfully created. Path: "+ folderPath.toString());
            createCSV(csvPath);
        }

    }
    
    // function to create CSV file
    public void createCSV(Path csvPath) throws IOException {
        Files.createFile(csvPath);
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvPath.toString()))) {
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"URL", "AddedDate"});
            writer.writeAll(data);
            System.out.println("OpenCSV file created successfully! Path: "+ csvPath.toString());
        }
    }

    // function to write to CSV
    public void writeCSV(String url) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvPath.toString(), true))) {
            String[] row1 = {url, LocalDateTime.now().toString()};
            writer.writeNext(row1);
            System.out.println("CSV file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void ServerListen() {
        try {
            ServerSocket ss = new ServerSocket(receiver_port);
            System.out.println("LISTENING...");

            while (true) {
                Socket s = ss.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

                String line;
                int contentLength = 0;
                while (!(line = in.readLine()).isEmpty()) {
                    if (line.toLowerCase().startsWith("content-length")) {
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                    }
                }

                char[] body = new char[contentLength];
                in.read(body);
                url = new String(body);
                writeCSV(url);
                System.out.println("Received: " + url);

                PrintWriter out = new PrintWriter(s.getOutputStream());
                out.print("HTTP/1.1 200 OK\r\n");
                out.print("Access-Control-Allow-Origin: *\r\n");
                out.print("Content-Type: text/plain\r\n");
                out.print("Content-Length: 16\r\n");
                out.print("\r\n");
                out.print("Message received!");
                out.flush();

                s.close();
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
