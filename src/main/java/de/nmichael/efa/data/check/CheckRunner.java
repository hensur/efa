/**
 * Runs some checks on a given Record
 *
 * Some checks can be overridden with a HashMap that maps check name and the user's answer
 * The CheckRunner throws a CheckException which contains the current override HashMap together
 * with a failure message and a list of options.
 *
 * The Frontend class can then let the user decide, the decision is represented with the position in the list
 * The new HashMap updated with the decision as int is then given back to a new instance of this class and the
 * whole process can start over.
 */

package de.nmichael.efa.data.check;

import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.data.storage.DataRecord;
import de.nmichael.efa.data.storage.StorageObject;
import de.nmichael.efa.ex.CheckException;
import de.nmichael.efa.ex.EfaException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

public class CheckRunner {

    // Set override field to this value
    // Other options can be added if checks provide
    // two different reactions to the same override
    public static final int OVERRIDDEN = 1;

    DataRecord record;
    StorageObject storage;
    HashMap<String, Integer> override;
    // We have to check if the user is actually allowed to override certain behaviour
    AdminRecord admin;

    /**
     * create a new CheckRunner
     *
     * @param record record that should be checked
     */
    public CheckRunner(DataRecord record, StorageObject storage, HashMap<String, Integer> override){
        this.record = record;
        this.storage = storage;
        if (override == null) {
            this.override = new HashMap<>();
        } else {
            this.override = override;
        }
        this.admin = null;
    }

    /**
     * Checks if a provided admin has permission to perform given action,
     * if there is no admin given, it will always return false
     * @param action
     * @return
     */
    boolean isAllowed(String action){
        if (admin != null){
            return admin.isAllowed(action);
        }
        return false;
    }

    boolean isOverridden(String option){
         if (override.get(option) != null && override.get(option) == OVERRIDDEN) {
             return true;
        }
        return false;
    }

    public DataRecord record() {
        return record;
    }

    StorageObject storage() {
        return storage;
    }

    /**
     * execute all checks; has to be overridden by subclass
     * @return exit code of the last check, the frontend can handle the exit code and act accordingly
     * 0 is good, 1 is generic error, just like unix exit codes
     */
    public boolean execute() throws CheckException {
        return true;
    }
}
