package com.cchangy.netty.nio.file;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 遍历文件或目录
 *
 * @author cchangy
 * @date 2022/01/02
 */
@Slf4j
public class FilesWalkFileTree {

    public static void main(String[] args) throws Exception {
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("/Users/chency/documents/ebook"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                log.info(dir.toString());
                dirCount.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                log.info(file.toString());
                fileCount.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });

        log.info("dir count: {}", dirCount);
        log.info("file count: {}", fileCount);
    }
}
