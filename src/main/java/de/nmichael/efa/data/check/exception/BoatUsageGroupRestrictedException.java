package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.ex.CheckCallbackException;
import de.nmichael.efa.gui.EfaBaseFrame;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.Logger;

import java.util.HashMap;

public class BoatUsageGroupRestrictedException extends CheckCallbackException {

    private LogbookRecord record;

    public BoatUsageGroupRestrictedException(String title, String msg, HashMap<String, Integer> override, String mapField, LogbookRecord record) {
        super(title, msg, override, mapField);
        this.record = record;
    }

    @Override
    public boolean handleGUI(EfaBaseFrame efa) {
        switch (Dialog.auswahlDialog(
                getTitle(),
                getMessage() + "\n" +
                        International.getString("Was möchtest Du tun?"),
                International.getString("Anderes Boot wählen"),
                International.getString("Mannschaft ändern"),
                International.getString("Trotzdem benutzen"),
                International.getString("Eintrag abbrechen"))) {
            case 0:
                efa.setFieldEnabled(true, true, efa.getItem("BOAT"));
                efa.getItem("BOAT").parseAndShowValue("");
                efa.getItem("BOAT").requestFocus();
                break;
            case 1:
                efa.getItem("PERSON0").requestFocus();
                break;
            case 2:
                EfaBaseFrame.logBoathouseEvent(Logger.INFO, Logger.MSG_EVT_UNALLOWEDBOATUSAGE,
                        International.getString("Unerlaubte Benutzung eines Bootes"),
                        record);
                override();
                return true;
            case 3:
                efa.cancel();
        }
        return false;
    }
}
