package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.data.check.LogbookCheckRunner;
import de.nmichael.efa.ex.CheckCallbackException;
import de.nmichael.efa.gui.EfaBaseFrame;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.Logger;

import java.util.HashMap;

public class NameInvalidException extends CheckCallbackException {

    private String fieldID;
    private LogbookRecord currentRecord;

    public NameInvalidException(String title, String msg, HashMap<String, Integer> override, String fieldID, LogbookRecord record) {
        super(title, msg, override, getTypeString(fieldID));
        this.fieldID = fieldID;
        this.currentRecord = record;
    }

    public NameInvalidException(String title, String msg, HashMap<String, Integer> override) {
        super(title, msg, override, LogbookCheckRunner.NAME_INVALID_OVERRIDE);
    }

    public static String getTypeString(String fieldID) {
        return LogbookCheckRunner.NAME_INVALID_OVERRIDE + fieldID;
    }

    @Override
    public boolean handleGUI(EfaBaseFrame efa) {
        if (efa.isModeBoathouse()) {
            Logger.log(Logger.WARNING, Logger.MSG_EVT_ERRORRECORDINVALIDATTIME,
                    EfaUtil.getLogbookRecordStringWithEntryNo(currentRecord) + ": " + getMessage() +
                            (currentRecord != null ? " (" + currentRecord.toString() + ")" : ""));
            override();
            return true;
        }
        if (Dialog.auswahlDialog(International.getString("Warnung"),
                getMessage(),
                International.getString("als unbekannten Namen speichern"),
                International.getString("Abbruch"), false) != 0) {
            // abort: don't save
            // todo: BaseFrame should enable access to fields by an identifier
            if (fieldID.contains("PERSON")) {
                int pos = Integer.parseInt(fieldID.replace("PERSON", "")); // parse PERSON[0-24]
                efa.getCrew(pos).requestFocus();
            } else {
                switch (fieldID) {
                    case "BOAT":
                        efa.getBoat().requestFocus();
                        break;
                    case "DESTINATION":
                        efa.getDestination().requestFocus();
                        break;
                }
            }
        } else {
            override();
            return true;
        }
        return false;
    }
}
