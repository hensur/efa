package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.gui.EfaBaseFrame;

public interface GUIExceptionHandler {
    /**
     *
     * @param efa current EfaBaseFrame that contains all GUI elements (can be null)
     * @return true if the exception is solved and the Record should be checked again,
     * false if the user is expected to make changes to the input.
     */
    boolean handleGUI(EfaBaseFrame efa);
}
