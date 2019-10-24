package bingo.internal.ui;

import javax.swing.filechooser.FileFilter;
import java.io.FilenameFilter;
import java.io.File;

/**
 * FileFilterOS is an abstract class to be used with <code>FileChooserOS</code>
 * for filtering the set of files shown to the user. A FileFilter can be set on
 * a <code>FileChooserOS</code> to keep unwanted files from appearing in the
 * directory listing.
 * <p/>
 * It combines <code>javax.swing.filechooser.FileFilter</code> and
 * <code>java.io.FilenameFilter</code>.
 *
 * @see bingo.internal.ui.FileChooserOS#setFileFilter
 * @see javax.swing.filechooser.FileFilter
 * @see java.io.FilenameFilter
 *
 * @author Rolf Lohaus
 */
public abstract class FileFilterOS extends FileFilter implements FilenameFilter {

    public boolean accept(File dir, String name) {
        return accept(new File(dir, name));
    }
}
