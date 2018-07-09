package de.nmichael.efa.ex;

import de.nmichael.efa.data.check.exception.GUIExceptionHandler;
import de.nmichael.efa.gui.EfaBaseFrame;
import de.nmichael.efa.util.Dialog;

public class CheckException extends EfaException implements GUIExceptionHandler {

    /**
     *
     @param title short description of the exception
     *            can also internationalization string for the question dialog
     *            todo: use a unified i18n string that is not the german equivalent of what we are looking for
     * @param msg extended error description for display to the user
     */
    public CheckException(String title, String msg) {
        super(title, msg, null);
    }

    @Override
    public boolean handleGUI(EfaBaseFrame efa) {
        return handleError();
    }

    protected boolean handleError() {
        Dialog.error(getMessage());
        return false;
    }
}
