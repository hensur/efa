package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.ex.CheckException;

public class EntryNumberExistsException extends CheckException {

    public EntryNumberExistsException(String title, String msg){
        super(title, msg);
    }
}
