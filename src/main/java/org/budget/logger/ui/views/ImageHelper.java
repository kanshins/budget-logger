/**
 * 
 */
package org.budget.logger.ui.views;

import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

/**
 * @author <sergey.kanshin@gmail.com>
 * @date 16 сент. 2015 г.
 */
public final class ImageHelper {
    private static Logger logger = Logger.getLogger(ImageHelper.class);

    public static final ImageIcon EDIT = createIcon("img/edit_ico.gif");
    public static final ImageIcon DELETE = createIcon("img/delete_ico.png");

    private ImageHelper() {
    }

    private static ImageIcon createIcon(String path) {
        try {
            Image img = ImageIO.read(ClassLoader.getSystemResource(path));
            return new ImageIcon(img);
        } catch (Exception e) {
            logger.error("Cannot load image " + path, e);
        }
        return null;
    }
}
