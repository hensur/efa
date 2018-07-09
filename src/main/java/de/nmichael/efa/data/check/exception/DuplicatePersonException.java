package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.ex.CheckException;

public class DuplicatePersonException extends CheckException {

    private int[] positions;

    public DuplicatePersonException(String title, String msg, int[] positions){
        super(title, msg);
        this.positions = positions;
    }

    public int[] getPositions() {
        return positions;
    }
}
