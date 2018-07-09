package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.data.check.LogbookCheckRunner;
import de.nmichael.efa.ex.CheckCallbackException;
import de.nmichael.efa.gui.EfaBaseFrame;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.International;

import java.util.HashMap;

public class DuplicateEntryException extends CheckCallbackException {

    private LogbookRecord duplicate;

    public DuplicateEntryException(String title, String msg, HashMap<String, Integer> override, LogbookRecord duplicate) {
        super(title, msg, override, LogbookCheckRunner.PERSON_FOR_BOAT_TYPE_OVERRIDE);
        this.duplicate = duplicate;
    }

    @Override
    public boolean handleGUI(EfaBaseFrame efa) {
        switch (Dialog.auswahlDialog(getTitle(),
                getMessage() + "\n"
                        + International.getString("Was möchtest Du tun?"),
                International.getString("Eintrag hinzufügen")
                        + " (" + International.getString("kein Doppeleintrag") + ")",
                International.getString("Eintrag nicht hinzufügen")
                        + " (" + International.getString("Doppeleintrag") + ")",
                International.getString("Zurück zum Eintrag"))) {
            case 0: // no duplicate: add the entry by overriding the check
                override();
                return true;
            case 1: // duplicate: don't save
                efa.cancel();
                return false;
            default: // Abort, user should edit the entry again
                return false;
        }

    }
}
