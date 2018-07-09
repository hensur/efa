package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.data.check.LogbookCheckRunner;
import de.nmichael.efa.ex.CheckCallbackException;
import de.nmichael.efa.gui.EfaBaseFrame;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.Logger;

import java.util.HashMap;

public class BoatUsagePersonRestrictedException extends CheckCallbackException {

    private int pos;

    public BoatUsagePersonRestrictedException(String title, String msg, HashMap<String, Integer> override, int pos) {
        super(title, msg, override, getPosOverride(pos));
        this.pos = pos;
    }

    public static String getPosOverride(int pos) {
        return LogbookCheckRunner.BOAT_USAGE_RESTRICTED + pos;
    }

    @Override
    public boolean handleGUI(EfaBaseFrame efa) {
        switch (Dialog.auswahlDialog(
                getTitle(),
                getMessage() + "\n" +
                        International.getString("Was möchtest Du tun?"),
                International.getString("Mannschaft ändern"),
                International.getString("Trotzdem benutzen"),
                International.getString("Eintrag abbrechen"))) {
            case 0:
                efa.getItem("PERSON"+pos).requestFocus();
                break;
            case 1:
                override();
                return true;
            case 2:
                efa.cancel();
        }
        return false;
    }
}
