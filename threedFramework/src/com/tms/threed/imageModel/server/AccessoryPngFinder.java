package com.tms.threed.imageModel.server;

import com.tms.threed.threedCore.shared.ThreedConfig;
import com.tms.threed.util.fileWalker.FileProcessor;
import com.tms.threed.util.fileWalker.FileWalker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

public class AccessoryPngFinder {

    private ThreedConfig threedConfig;

    public AccessoryPngFinder(ThreedConfig threedConfig) {
        this.threedConfig = threedConfig;
    }

    public static int deleteEmptyPngFiles(File pngRoot) {
        int pngCount = getPngCount(pngRoot);
        System.out.println("pngCount = [" + pngCount + "]");
        int emptyPngCount = getEmptyPngCount(pngRoot);
        System.out.println("emptyPngCount = [" + emptyPngCount + "]");

        FileWalker walker = new FileWalker(pngRoot);
        walker.setFileFilter(FileFilters.EMPTY_PNG_FILTER);
        DeletePngProcessor deletePngProcessor = new DeletePngProcessor();
        walker.setFileProcessor(deletePngProcessor);
        walker.start();
        int numberOfFilesDeleted = deletePngProcessor.getNumberOfFilesDeleted();

        System.out.println("numberOfFilesDeleted = [" + numberOfFilesDeleted + "]");
        return numberOfFilesDeleted;
    }

    public static void showEmptyPngFiles(File pngRoot) {
        int pngCount = getPngCount(pngRoot);
        System.out.println("pngCount = [" + pngCount + "]");
        int emptyPngCount = getEmptyPngCount(pngRoot);
        System.out.println("emptyPngCount = [" + emptyPngCount + "]");

        FileWalker walker = new FileWalker(pngRoot);
        walker.setFileFilter(FileFilters.EMPTY_PNG_FILTER);
        walker.setFileProcessor(new FileProcessor() {
            @Override public void processFile(File f) {
                System.out.println(f);
            }
        });
        walker.start();
    }

    public static int getPngCount(File pngRoot) {
        FileWalker w1 = new FileWalker(pngRoot);
        w1.setFileFilter(FileFilters.PNG_FILE_FILTER);
        w1.start();
        return w1.getFilesProcessedCount();
    }


   public static int getEmptyPngCount(File pngRoot) {
        FileWalker w1 = new FileWalker(pngRoot);
        w1.setFileFilter(FileFilters.EMPTY_PNG_FILTER);
        w1.start();
        return w1.getFilesProcessedCount();
    }

    public static class DeletePngProcessor implements FileProcessor {

        private int numberOfFilesDeleted;

        public void processFile(File pngFile) {
            if (!pngFile.delete()) {
                log.warn("Failed to delete PNG: [" + pngFile + "]");
            } else {
                numberOfFilesDeleted++;
            }
        }

        public int getNumberOfFilesDeleted() {
            return numberOfFilesDeleted;
        }
    }

    private static class FileCounter implements FileProcessor {

        private int count = 0;

        public void processFile(File pngFile) {
            count++;
        }

        public int getCount() {
            return count;
        }
    }


    private static final Log log = LogFactory.getLog(AccessoryPngFinder.class);


}