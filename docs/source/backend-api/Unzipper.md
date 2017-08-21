---
layout: default
id: Unzipper
title: Backend API
parent: backend-api
---
# Documentation

## `public class Unzipper`

This utility extracts files and directories of a standard zip file to a destination directory.

 * **Author:** www.codejava.net
 * **Link:** http://www.codejava.net/java-se/file-io/programmatically-extract-a-zip-file-using-java

     <p>

## `private static final int BUFFER_SIZE = 4096`

Size of the buffer to read/write data

## `public static void unzip(InputStream zipFileIS, String destDirectory) throws IOException`

Extracts a zip file specified by the zipFilePath to a directory specified by destDirectory (will be created if does not exists)

 * **Parameters:**
   * `zipFileIS` — 
   * `destDirectory` — 
 * **Exceptions:** `IOException` — 

## `private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException`

Extracts a zip entry (file entry)

 * **Parameters:**
   * `zipIn` — 
   * `filePath` — 
 * **Exceptions:** `IOException` — 
