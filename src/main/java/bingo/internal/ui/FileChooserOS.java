package bingo.internal.ui;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * <code>FileChooserOS</code> provides a system-dependent dialog for the user
 * to select a file or directory. On macOS it uses the AWT
 * <code>java.awt.FileDialog</code> to present a native file chooser dialog, on
 * all other systems it uses the Swing  <code>javax.swing.JFileChooser</code>.
 * <p/>
 * The file chooser is a modal dialog, upon calling <code>showDialog</code> it
 * blocks the rest of the application until the user has closed the dialog.
 *
 * @author Rolf Lohaus
 */
public class FileChooserOS {

    /**
     * Defines the two modes of the file chooser, <code>OPEN</code> for an
     * "Open File" dialog and <code>SAVE</code> for a "Save File" dialog.
     */
    public enum Mode {OPEN, SAVE}

    /**
     * Defines the return states of the file chooser when the dialog gets
     * closed, <code>CANCEL</code> and <code>APPROVE</code>.
     */
    public enum ReturnState {CANCEL, APPROVE}


    private boolean macOS;

    private Frame parent;
    private Mode mode;

    private FileDialog fd;
    private JFileChooser fc;


    /**
     * Creates an "Open File" file chooser pointing to the user's default
     * directory. This default depends on the operating system. It is typically
     * the "My Documents" folder on Windows, and the user's home directory on
     * Unix.
     *
     * @param parent the owner of the dialog
     */
    public FileChooserOS(Frame parent) {
        this(parent, Mode.OPEN);
    }

    /**
     * Creates a file chooser of the given <code>mode</code> pointing to the
     * user's default directory. This default depends on the operating system.
     * It is typically the "My Documents" folder on Windows, and the user's home
     * directory on Unix.
     *
     * @param parent    the owner of the dialog
     * @param mode      the mode of the dialog; either <code>Mode.OPEN</code>
     *                  or <code>Mode.SAVE</code>
     *
     * @see bingo.internal.ui.FileChooserOS.Mode
     */
    public FileChooserOS(Frame parent, Mode mode) {
        this(parent, mode, null);
    }

    /**
     * Creates an "Open File" file chooser pointing to given directory path.
     * Passing in a <code>null</code> or empty string causes the file chooser
     * to point to the user's default directory. This default depends on the
     * operating system. It is typically the "My Documents" folder on Windows,
     * and the user's home directory on Unix.
     *
     * @param parent                the owner of the dialog
     * @param currentDirectoryPath  a String giving the path to a file or
     *                              directory
     */
    public FileChooserOS(Frame parent, String currentDirectoryPath) {
        this(parent,  Mode.OPEN, currentDirectoryPath);
    }

    /**
     * Creates a file chooser of the given <code>mode</code> pointing to given
     * directory path. Passing in a <code>null</code> or empty string causes the
     * file chooser to point to the user's default directory. This default
     * depends on the operating system. It is typically the "My Documents"
     * folder on Windows, and the user's home directory on Unix.
     *
     * @param parent                the owner of the dialog
     * @param mode                  the mode of the dialog; either
     *                              <code>Mode.OPEN</code> or
     *                              <code>Mode.SAVE</code>
     * @param currentDirectoryPath  a String giving the path to a file or
     *                              directory
     *
     * @see bingo.internal.ui.FileChooserOS.Mode
     */
    public FileChooserOS(Frame parent, Mode mode, String currentDirectoryPath) {
        macOS = (System.getProperty("os.name").toLowerCase().contains("mac"));

        this.parent = parent;
        this.mode = mode;

        if (macOS) {
            fd = new FileDialog(this.parent, null, this.mode == Mode.OPEN ? FileDialog.LOAD : FileDialog.SAVE);
            fd.setDirectory(currentDirectoryPath);
        } else {
            File currentDirectory = null;
            if (currentDirectoryPath != null && !"".equals(currentDirectoryPath)) {
                currentDirectory = new File(currentDirectoryPath);
            }
            fc = new JFileChooser(currentDirectory);
            fc.setDialogType(this.mode == Mode.OPEN ? JFileChooser.OPEN_DIALOG : JFileChooser.SAVE_DIALOG);
        }
    }

    /**
     * Sets the title of this file chooser dialog.
     *
     * @param title the title displayed in the dialog; a null value results in
     *              an empty title
     */
    public void setTitle(String title) {
        if (macOS) {
            fd.setTitle(title);
        } else {
            fc.setDialogTitle(title);
        }
    }

    /**
     * Sets the current file filter. The file filter is used by this file
     * chooser to filter out files from the user's view.
     *
     * @param fileFilterOS   the file filter to use
     */
    public void setFileFilter(FileFilterOS fileFilterOS) {
        if (macOS) {
            fd.setFilenameFilter(fileFilterOS);
        } else {
            fc.setFileFilter(fileFilterOS);
        }
    }

    /**
     * Sets the text used in the approve button in the file chooser dialog.
     * Note that this is not supported for the macOS file chooser!
     *
     * @param approveButtonText text used in the approve button
     */
    public void setApproveButtonText(String approveButtonText) {
        if (!macOS) {
            fc.setApproveButtonText(approveButtonText);
        }
    }

    /**
     * Shows this file chooser dialog to let the user select files only, and
     * since it is a modal dialog blocks the rest of the application until the
     * user has closed the dialog.
     *
     * @return  the return state of the file chooser upon closing,
     *          <code>ReturnState.APPROVE</code> or
     *          <code>ReturnState.CANCEL</code>
     *
     * @see bingo.internal.ui.FileChooserOS.ReturnState
     */
    public ReturnState showDialog() {
        return showDialog(false);
    }

    /**
     * Shows this file chooser dialog to let the user select directories only,
     * and since it is a modal dialog blocks the rest of the application until
     * the user has closed the dialog.
     *
     * @return  the return state of the file chooser upon closing,
     *          <code>ReturnState.APPROVE</code> or
     *          <code>ReturnState.CANCEL</code>
     *
     * @see bingo.internal.ui.FileChooserOS.ReturnState
     */
    public ReturnState showDialog(boolean directoryOnly) {
        if (macOS) {
            return showDialogMacOS(directoryOnly);
        } else {
            return showDialogNonMacOS(directoryOnly);
        }
    }

    private ReturnState showDialogMacOS(boolean directoryOnly) {
        final String fileDialogForDirectories = System.getProperty("apple.awt.fileDialogForDirectories");
        System.setProperty("apple.awt.fileDialogForDirectories", String.valueOf(directoryOnly));

        ReturnState state = ReturnState.CANCEL;

        try {
            fd.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
            fd.setLocationRelativeTo(parent);
            fd.setVisible(true);
            if (fd.getDirectory() != null && fd.getFile() != null) {
                state = ReturnState.APPROVE;
            }
        } finally {
            if (fileDialogForDirectories != null && !"".equals(fileDialogForDirectories)) {
                System.setProperty("apple.awt.fileDialogForDirectories", fileDialogForDirectories);
            } else {
                System.setProperty("apple.awt.fileDialogForDirectories", "false");
            }
        }

        return state;
    }

    private ReturnState showDialogNonMacOS(boolean directoryOnly) {
        if (directoryOnly) {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else {
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }

        ReturnState state = ReturnState.CANCEL;

        if (mode == Mode.OPEN) {
            int fcState = fc.showOpenDialog(parent);
            if (fcState == JFileChooser.APPROVE_OPTION) {
                state = ReturnState.APPROVE;
            }
        } else if (mode == Mode.SAVE) {
            int fcState = fc.showSaveDialog(parent);
            if (fcState == JFileChooser.APPROVE_OPTION) {
                state = ReturnState.APPROVE;
            }
        }

        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        return state;
    }

    /**
     * Returns the file/directory the user has selected in this file chooser.
     * If the user selected CANCEL, the returned file is <code>null</code>.
     *
     * @return the selected file or <code>null</code>
     */
    public File getSelectedFile() {
        if (macOS) {
            if (fd.getFile() == null) {
                return null;
            }

            return new File(fd.getDirectory(), fd.getFile());
        } else {
            return fc.getSelectedFile();
        }
    }

    /**
     * Returns the path of file/directory the user has selected in this
     * file chooser. If the user selected CANCEL, the returned file path is
     * <code>null</code>.
     *
     * @return the selected file path or <code>null</code>
     */
    public String getSelectedFilePath() {
        if (macOS) {
            if (fd.getFile() == null) {
                return null;
            }

            return fd.getDirectory() + fd.getFile();
        } else {
            if (fc.getSelectedFile() == null) {
                return null;
            }
            return fc.getSelectedFile().getPath();
        }
    }

    /**
     * Returns <code>true</code> if running on a macOS system.
     *
     * @return <code>true</code> if running on a macOS system
     */
    public boolean isMacOS() {
        return macOS;
    }
}
