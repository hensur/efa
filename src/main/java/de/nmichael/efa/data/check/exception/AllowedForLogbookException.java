package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.Daten;
import de.nmichael.efa.data.Logbook;
import de.nmichael.efa.data.LogbookRecord;
import de.nmichael.efa.data.MessageRecord;
import de.nmichael.efa.ex.CheckException;
import de.nmichael.efa.gui.EfaBaseFrame;
import de.nmichael.efa.gui.dataedit.MessageEditDialog;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.LogString;

import static de.nmichael.efa.gui.EfaBaseFrame.MODE_BOATHOUSE_LATEENTRY;

public class AllowedForLogbookException extends CheckException {

    LogbookRecord currentRecord;
    Logbook logbook;

    public AllowedForLogbookException(String title, String msg, LogbookRecord currentRecord, Logbook logbook) {
        super(title, msg);
        this.currentRecord = currentRecord;
        this.logbook = logbook;
    }

    @Override
    public boolean handleGUI(EfaBaseFrame efa) {
        if (efa.getMode() != MODE_BOATHOUSE_LATEENTRY) {
            Dialog.error(getMessage());
            efa.getDate().requestFocus();
        } else {
            String msg = getMessage() +
                            "Du kannst diesen Eintrag aber zum Nachtrag an den Administrator senden." + "\n" +
                    International.getString("Was möchtest Du tun?");
            switch(Dialog.auswahlDialog(EfaUtil.getLogbookRecordStringWithEntryNo(currentRecord), msg,
                    International.getString("Datum korrigieren"),
                    International.getString("Nachtrag an Admin senden"), true)) {
                case 0:
                    efa.getDate().requestFocus();
                    break;
                case 1:
                    String entryNo = EfaUtil.getLogbookRecordStringWithEntryNo(currentRecord);
                    MessageRecord adminMessage = logbook.saveForLateEntry(currentRecord);
                    if (adminMessage != null && Daten.project.getMessages(false).saveMessage(adminMessage)) {
                        Dialog.infoDialog(International.getMessage("{entry} wurde zum Nachtrag an den Admin geschickt und wird erst nach der Bestätigung durch den Admin sichtbar sein.",
                                entryNo));
                        efa.cancel();
                    } else {
                        Dialog.error(LogString.operationFailed(International.getString("Nachtrag an Admin senden")));
                    }
                    break;
                default:
                    break;
            }

        }
        return false;
    }
}
