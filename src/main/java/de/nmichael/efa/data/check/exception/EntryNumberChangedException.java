package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.data.check.LogbookCheckRunner;
import de.nmichael.efa.ex.CheckCallbackException;
import de.nmichael.efa.gui.EfaBaseFrame;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.International;

import java.util.HashMap;

public class EntryNumberChangedException extends CheckCallbackException {

    public EntryNumberChangedException(String title, String msg, HashMap<String, Integer> override) {
        super(title, msg, override, LogbookCheckRunner.ENTRY_NUMBER_CHANGED_OVERRIDE);
    }

    @Override
    public boolean handleGUI(EfaBaseFrame efa) {
        return handleSaveAnyway();
    }
}
