
/*
File Name: trimEmptySpace.java
Developed by: Andrews F.
Date: 3/30/2020
Version: 1.0
Purpose: To batch process PNG files in order to trim all 4 sides to the depth of the first solid pixel.
*/
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.*;
import java.awt.image.WritableRaster;

import java.util.stream.*;
import java.nio.file.*;

// Imports the Scanner class to record user input.
import java.util.Scanner;

public class trimEmptySpace {

    public static void main(final String args[]) throws IOException {

        // Asks the user for the path to the folder with the images to be processed.

        final List<String> imagePaths = imagePathStorage(TextProcessing(folderLocation()));

        for (final String output : imagePaths) {

            System.out.println("Currently Processing Image:  " + output);
            imageLoader(output);
        }

        System.out.println("Program terminated successfully.");

    }

    public static String folderLocation() {

        Scanner readInput = new Scanner(System.in); // Create a Scanner object
        System.out.println("Enter path: ");

        String folderPath = readInput.nextLine(); // Read user input
        System.out.println("Folder path is: " + folderPath); // Output user input

        readInput.close();

        return folderPath;

    }

    public static String TextProcessing(String folderPathInsertion) {

        // String builder to is being used to create a modified String.
        System.out.println("PASSED STRING IS: " + folderPathInsertion);
        StringBuffer modifiedString = new StringBuffer("");

        // For loop that iterates through each Character in the String.
        for (int i = 0; i < folderPathInsertion.length(); i++) {

            modifiedString.append(folderPathInsertion.charAt(i));

            // Conditional checks if the current Character is "\" if so then it adds an
            // additional "\".
            if (folderPathInsertion.charAt(i) == '\\') {

                // insert boolean value at offset 8
                modifiedString.append('\\');

            }
        }

        System.out.println("MODIFIED Folder path is: " + modifiedString.toString()); // Output user input
        return modifiedString.toString();
    }

    // This method navigates to the directory of the folder where the images are to
    // be processed and saves the directory location for each one in a List.
    public static List<String> imagePathStorage(String folderPathInsertionModified) {

        try (Stream<Path> walk = Files.walk(Paths.get(folderPathInsertionModified))) {

            final List<String> result = walk.filter(Files::isRegularFile).map(x -> x.toString())
                    .collect(Collectors.toList());

            return result;

        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void imageLoader(final String fileLocation) {

        // Conditional ensures only PNG's are processed.
        if (fileLocation.substring(fileLocation.length() - 3).equalsIgnoreCase("png")) {

            BufferedImage img = null;
            BufferedImage img2 = null;
            File finalOutput = null;

            final String imageName = fileLocation;

            // This imports the image file.
            try {

                img = ImageIO.read(new File(imageName));

            } catch (final IOException e) {

                System.out.println(e + " Could not read image.");

            }

            img2 = trimmingEmptySpace(img);

            // Writes the new image to the location of the original image path.
            try {

                finalOutput = new File(imageName);
                ImageIO.write(img2, "png", finalOutput);

            } catch (final IOException e) {

                System.out.println(e + " Could not write image.");

            }

            // User message indicating successful process.
            System.out.println("Image Processed!");

        } else {

            System.out.println("Not a PNG. SKIPPED!");

        }
    }

    // Method loads BufferedImage then renders a new image trimmed by removing empty
    // pixels.
    private static BufferedImage trimmingEmptySpace(final BufferedImage image) {

        final WritableRaster raster = image.getAlphaRaster();
        final int width = raster.getWidth();
        final int height = raster.getHeight();

        // These variables store the depth of the new image minimum values.
        int topSide = 0;
        int bottomSide = height - 1;
        int leftSide = 0;
        int rightSide = width - 1;

        int minRight = width - 1;
        int minBottom = height - 1;

        // Each for loop processes each side of the image.
        topSide: for (; topSide < bottomSide; topSide++) {

            for (int x = 0; x < width; x++) {

                if (raster.getSample(x, topSide, 0) != 0) {

                    minRight = x;
                    minBottom = topSide;
                    break topSide;

                }
            }
        }

        bottomSide: for (; bottomSide > minBottom; bottomSide--) {

            for (int x = width - 1; x >= leftSide; x--) {

                if (raster.getSample(x, bottomSide, 0) != 0) {

                    minRight = x;
                    break bottomSide;

                }
            }
        }

        leftSide: for (; leftSide < minRight; leftSide++) {

            for (int y = height - 1; y > topSide; y--) {

                if (raster.getSample(leftSide, y, 0) != 0) {

                    minBottom = y;
                    break leftSide;

                }
            }
        }

        rightSide: for (; rightSide > minRight; rightSide--) {

            for (int y = bottomSide; y >= topSide; y--) {

                if (raster.getSample(rightSide, y, 0) != 0) {

                    break rightSide;

                }
            }
        }

        return image.getSubimage(leftSide, topSide, rightSide - leftSide + 1, bottomSide - topSide + 1);
    }
}
