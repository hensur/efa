package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.ex.CheckException;

public class NameFormatInvalidException extends CheckException {

    public NameFormatInvalidException(String title, String msg){
        super(title, msg);
    }
}
