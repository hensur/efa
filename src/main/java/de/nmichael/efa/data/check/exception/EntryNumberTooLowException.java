package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.data.check.LogbookCheckRunner;
import de.nmichael.efa.ex.CheckCallbackException;
import de.nmichael.efa.gui.EfaBaseFrame;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.International;

import java.util.HashMap;

public class EntryNumberTooLowException extends CheckCallbackException {

    public EntryNumberTooLowException(String title, String msg, HashMap<String, Integer> override) {
        super(title, msg, override, LogbookCheckRunner.ENTRY_NUMBER_TOO_LOW_OVERRIDE);
    }

    @Override
    public boolean handleGUI(EfaBaseFrame efa) {
        return handleSaveAnyway();
    }
}
