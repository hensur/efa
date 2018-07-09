package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.ex.CheckException;
import de.nmichael.efa.gui.EfaBaseFrame;

public class UnknownNameException extends CheckException {

    String type;

    public UnknownNameException(String title, String msg, String type){
        super(title, msg);
        this.type = type;
    }

    @Override
    public boolean handleGUI(EfaBaseFrame efa) {
        handleError();
        if (efa.getItem(type) != null) {
            efa.getItem(type).requestFocus();
        }
        return false;
    }
}
