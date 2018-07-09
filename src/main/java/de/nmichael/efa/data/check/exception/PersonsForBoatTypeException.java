package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.data.check.LogbookCheckRunner;
import de.nmichael.efa.ex.CheckCallbackException;
import de.nmichael.efa.gui.EfaBaseFrame;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.International;

import java.util.HashMap;

public class PersonsForBoatTypeException extends CheckCallbackException {

    public PersonsForBoatTypeException(String title, String msg, HashMap<String, Integer> override) {
        super(title, msg, override, LogbookCheckRunner.PERSON_FOR_BOAT_TYPE_OVERRIDE);
    }

    @Override
    public boolean handleGUI(EfaBaseFrame efa) {
        if (Dialog.yesNoDialog(getTitle(),
                getMessage() + "\n" +
                        International.getString("Trotzdem speichern?")) == Dialog.YES) {
            override();
            return true;
        }
        return false;
    }
}
