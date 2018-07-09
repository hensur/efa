package de.nmichael.efa.ex;

import de.nmichael.efa.data.check.CheckRunner;
import de.nmichael.efa.gui.EfaBaseFrame;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.International;

import java.util.HashMap;

public class CheckCallbackException extends CheckException {

    HashMap<String, Integer> override;
    String mapField;

    /**
     * @param title short description of the exception
     *              can also internationalization string for the question dialog
     *              todo: use a unified i18n string that is not the german equivalent of what we are looking for
     * @param msg   extended error description for display to the user
     */
    public CheckCallbackException(String title, String msg, HashMap<String, Integer> override, String mapField) {
        super(title, msg);
        this.override = override;
        this.mapField = mapField;
    }

    /**
     * Override this Exception by setting its Config Override to true
     *
     * @return New check config after override (for a new run in the CheckRunner)
     */
    protected HashMap<String, Integer> override() {
        if (override != null) {
            override.put(mapField, CheckRunner.OVERRIDDEN);
            return override;
        }
        return null;
    }

    @Override
    public boolean handleGUI(EfaBaseFrame efa) {
        return handleYesNo();
    }

    protected boolean handleSaveAnyway() {
        if (Dialog.yesNoDialog(getTitle(),
                getMessage() + "\n" +
                        International.getString("Trotzdem speichern?")) == Dialog.YES) {
            override();
            return true;
        }
        return false;
    }

    protected boolean handleYesNo() {
        if (Dialog.yesNoDialog(getTitle(), getMessage()) == Dialog.YES) {
            override();
            return true;
        }
        return false;
    }
}
