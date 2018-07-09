package de.nmichael.efa.data.check.exception;

import de.nmichael.efa.ex.CheckException;
import de.nmichael.efa.gui.EfaBaseFrame;

public class DataMissingException extends CheckException {

    String dataType;

    public DataMissingException(String title, String msg, String dataType){
        super(title, msg);
        this.dataType = dataType;
    }

    @Override
    public boolean handleGUI(EfaBaseFrame efa) {
        handleError();
        switch (dataType) {
            case "BOAT":
                efa.getBoat().requestFocus();
                break;
            case "PERSON":
                if (efa.getCrew(0).isEditable()) {
                    efa.getCrew(0).requestFocus();
                } else {
                    efa.getCrew(1).requestFocus();
                }
                break;
            case "DESTINATION":
                efa.getDestination().requestFocus();
                break;
            case "WATERS":
                efa.getWaters().requestFocus();
                break;
            case "DISTANCE":
                efa.getDistance().requestFocus();
                break;
        }
        return false;
    }
}
