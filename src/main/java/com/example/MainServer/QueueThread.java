package com.example.MainServer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

public class QueueThread {
    private static final Logger logger = Logger.getLogger(QueueThread.class.getName());
    public void menu(int num, String maskAsk, DataOutputStream outToClient){
        String rootDir = "src/main/resources/rootPath";

         BlockingQueue<String> queue = new LinkedBlockingQueue<>();

        Thread searcher = new Thread(() -> {
            try {
                Files.find(Paths.get(rootDir),
                                num,
                                (path, file) -> file.isRegularFile())
                        .forEach(path -> {
                            File currentFile = path.toFile();
                            if (currentFile.getName().contains(maskAsk)) {
                                try {
                                    queue.put(currentFile.getName());
                                } catch (InterruptedException e) {
                                    logger.info(Thread.currentThread().getName() + "is interrupted");

                                }
                            }
                        });
                queue.put("DONE");
            } catch (IOException | InterruptedException e) {
                logger.info(Thread.currentThread().getName() + "is interrupted");
            }
        });
        searcher.start();

        Thread printer = new Thread(() -> {
            int counter = 0;
            try {
                String fileName;
                while (!(fileName = queue.take()).equals("DONE")) {
                    outToClient.writeBytes(fileName + "\n");
                    logger.info(fileName);
                    counter++;
                }
                if (counter == 0) {
                    outToClient.writeBytes("Nothing file with this mask" + "\n");
                    logger.info("Nothing file with this mask");
                }
            } catch (InterruptedException e) {
                logger.info(Thread.currentThread().getName() + "is interrupted");
                } catch (IOException e) {
                e.printStackTrace();
            }
        });
        printer.start();

        try {
            searcher.join();
            queue.put("DONE");
        } catch (InterruptedException e) {
            logger.info(Thread.currentThread().getName() + "is interrupted");
        }

        try {
            printer.join();
        } catch (InterruptedException e) {
            logger.info(Thread.currentThread().getName() + "is interrupted");
        }
    }
}
