package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.ex.CheckException;
import de.nmichael.efa.gui.EfaBaseFrame;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.International;

public class TimeOutOfRangeException extends CheckException {

    public static final int MODE_ERROR = 0;
    public static final int MODE_END_BEFORE_START_TIME = 1;
    public static final int MODE_FOCUS_ENDDATE = 2;
    public static final int MODE_FOCUS_ENDTIME = 3;
    public static final int MODE_ALLOWED_FOR_LOGBOOK = 4;

    private int mode;

    public TimeOutOfRangeException(String title, String msg, int mode) {
        super(title, msg);
        this.mode = mode;
    }

    @Override
    public boolean handleGUI(EfaBaseFrame efa) {
        switch (mode) {
            case MODE_END_BEFORE_START_TIME:
                if (Dialog.yesNoDialog(
                        getTitle(),
                        getMessage()) == Dialog.YES) {
                    efa.getEndDate().expandToField();
                    efa.getEndDate().requestFocus();
                    break;
                }
                efa.getEndTime().requestFocus();
                break;
            case MODE_FOCUS_ENDTIME:
                handleError();
                efa.getEndTime().requestFocus();
                break;
            case MODE_FOCUS_ENDDATE:
                handleError();
                efa.getEndDate().requestFocus();
                break;
        }
        return false;
    }
}
