package de.nmichael.efa.data.check;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.check.exception.*;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.data.types.DataTypeIntString;
import de.nmichael.efa.data.types.DataTypeList;
import de.nmichael.efa.ex.CheckException;
import de.nmichael.efa.util.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

import static de.nmichael.efa.core.config.AdminRecord.EDITLOGBOOK;

public class LogbookCheckRunner extends CheckRunner {

    public static final String PERSON_FOR_BOAT_TYPE_OVERRIDE = "personForBoatType";
    public static final String DUPLICATE_ENTRY_OVERRIDE = "duplicateEntry";
    public static final String ENTRY_NUMBER_TOO_LOW_OVERRIDE = "entryNumberIsLower";
    public static final String ENTRY_NUMBER_CHANGED_OVERRIDE = "entryNumberChanged";
    public static final String NAME_INVALID_OVERRIDE = "nameInvalid";
    public static final String BOAT_USAGE_RESTRICTED = "boatUsage";
    public static final String BOAT_GROUP_RESTRICTED = "boatGroup";
    public static final String BOAT_ONE_OF_GROUP_RESTRICTED = "boatOneOfGroup";


    private boolean isNewRecord;
    private String boatHouseName;
    private LogbookRecord currentRecord;

    public LogbookCheckRunner(LogbookRecord lr, Logbook lb, HashMap<String, Integer> override, LogbookRecord currentRecord, boolean isNewRecord, String boatHouseName) {
        super(lr, lb, override);
        this.isNewRecord = isNewRecord;
        this.boatHouseName = boatHouseName;
        this.currentRecord = currentRecord;
    }

    @Override
    public boolean execute() throws CheckException {
        ensureUniqueness();
        return checkDuplicatePersons() &&
                checkPersonsForBoatType() &&
                checkDuplicateEntry() &&
                checkEntryNo() &&
                checkBoatCaptain() &&
                checkMultiDayTours() &&
                checkDate() &&
                checkTime() &&
                checkAllowedDateForLogbook() &&
                checkAllDataEntered() &&
                checkNamesValid() &&
                checkUnknownNames() &&
                checkProperUnknownNames() &&
                checkAllowedPersons();
    }

    @Override
    public LogbookRecord record() {
        return (LogbookRecord) record;
    }

    @Override
    Logbook storage() {
        return (Logbook) storage;
    }

    /**
     * Check if the same rower is entered at multiple crew positions
     *
     * @return check result
     * @throws DuplicatePersonException one person can only sit once in a boat
     *                                  Show an error to the user if entered twice
     */
    private boolean checkDuplicatePersons() throws DuplicatePersonException {
        Hashtable<UUID, Integer> h = new Hashtable<>();
        PersonRecord duplicate = null; // Person who appeared twice; if null, no one was found
        int[] positions = null;
        PersonRecord r;
        for (int i = 0; i <= LogbookRecord.CREW_MAX; i++) {
            // Search for any person with that name in the logbook range
            // (maybe the name changed)
            r = Daten.project.getPersons(false).findPerson(record().getCrewAsName(i), record().getValidAtTimestamp(), storage().getValidFrom(), storage().getInvalidFrom());
            if (r != null) {
                UUID id = r.getId();
                if (h.get(id) == null) {
                    h.put(id, i);
                } else {
                    duplicate = r;
                    positions = new int[]{h.get(id), i};
                    // break the loop at this point, one duplicate at a time
                    break;
                }
            }
        }
        if (duplicate != null) {
            throw new DuplicatePersonException(
                    null,
                    International.getMessage("Die Person '{name}' wurde mehrfach eingegeben!", duplicate.getQualifiedName()),
                    positions);
        }
        return true;
    }

    /**
     * Check if this is a coxless boat
     * If a cox was entered, ask the user if that is right
     *
     * @return check result
     * @throws PersonsForBoatTypeException captain has to match boat type
     *                                     un-coxed boat can't have a cox
     */
    private boolean checkPersonsForBoatType() throws PersonsForBoatTypeException {
        if (record().getCoxAsName().length() > 0) {
            BoatRecord boat = record().getBoatRecord(record().getValidAtTimestamp());
            if (boat != null && boat.getTypeCoxing(record().getBoatVariant()) != null) {
                String boatType = boat.getTypeCoxing(record().getBoatVariant());
                if (boatType.equals(EfaTypes.TYPE_COXING_COXLESS)) {
                    if (!isOverridden(PERSON_FOR_BOAT_TYPE_OVERRIDE)) {
                        throw new PersonsForBoatTypeException(
                                International.getString("Steuermann"),
                                International.getString("Du hast für ein steuermannsloses Boot einen Steuermann eingetragen."),
                                override);
                    }
                }
            }
        }
        return true; // user said yes or check didn't apply
    }

    /**
     * Check if this entry is similar to one of the last 25 entries.
     *
     * @return check result
     * @throws DuplicateEntryException if this entry is a duplicate
     *                                 ask the user to confirm this by displaying matching entries side by side.
     *                                 This can be handled by editing the entry again or by overriding it and therefore saving the entry again
     */
    private boolean checkDuplicateEntry() throws DuplicateEntryException {
        LogbookRecord duplicate = storage().findDuplicateEntry(record(), 25);
        if (duplicate != null) {
            Vector<String> v = duplicate.getAllCoxAndCrewAsNames();
            StringBuilder mb = new StringBuilder();
            for (String name : v) {
                mb.append(mb.length() > 0 ? "; " : "");
                mb.append(name);
            }

            if (!isOverridden(DUPLICATE_ENTRY_OVERRIDE)) {
                throw new DuplicateEntryException(
                        International.getString("Doppeleintrag") + "?",
                        // @todo (P5) make duplicate entry dialog a bit more readable
                        International.getString("efa hat einen ähnlichen Eintrag im Fahrtenbuch gefunden.") + "\n"
                                + International.getString("Eventuell hast Du oder jemand anderes die Fahrt bereits eingetragen.") + "\n\n"
                                + International.getString("Vorhandener Eintrag") + ":\n"
                                + International.getMessage("#{entry} vom {date} mit {boat}",
                                duplicate.getEntryId().toString(), duplicate.getDate().toString(), duplicate.getBoatAsName()) + ":\n"
                                + International.getString("Mannschaft") + ": " + mb.toString() + "\n"
                                + International.getString("Abfahrt") + ": " + (duplicate.getStartTime() != null ? duplicate.getStartTime().toString() : "") + "; "
                                + International.getString("Ankunft") + ": " + (duplicate.getEndTime() != null ? duplicate.getEndTime().toString() : "") + "; "
                                + International.getString("Ziel") + " / " +
                                International.getString("Strecke") + ": " + duplicate.getDestinationAndVariantName() + " (" + (duplicate.getDistance() != null ? duplicate.getDistance().getAsFormattedString() : "") + " Km)" + "\n\n"
                                + International.getString("Bitte füge den aktuellen Eintrag nur hinzu, falls es sich NICHT um einen Doppeleintrag handelt."),
                        override,
                        duplicate);
            }
        }
        return true;
    }

    /**
     * Check if the entry number is valid.
     * Several problems can occur, these are handled by different exceptions.
     *
     * @return check result
     * @throws EntryNumberTooLowException  this number is lower than the previous entry's one
     * @throws EntryNumberChangedException number of updated entry has changed, user has to confirm this
     * @throws EntryNumberExistsException  this number has been assigned to another record already,
     *                                     ask the user to change it or to add a alphabetical suffix.
     */
    private boolean checkEntryNo() throws EntryNumberExistsException, EntryNumberChangedException, EntryNumberTooLowException {
        DataTypeIntString newEntryNo = record().getEntryId();
        // todo: check in the gui class if we are in ModeBoathouse, if that is the case
        // increase the version number like it was done in the former BaseFrame method
        if ((storage().getLogbookRecord(newEntryNo) != null && isNewRecord) || newEntryNo.length() == 0) {
            throw new EntryNumberExistsException(
                    null,
                    International.getString("Diese Laufende Nummer ist bereits vergeben!") + " "
                            + International.getString("Bitte korrigiere die laufende Nummer des Eintrags!") + "\n\n"
                            + International.getString("Hinweis") + ": "
                            + International.getString("Um mehrere Einträge unter 'derselben' Nummer hinzuzufügen, "
                            + "füge einen Buchstaben von A bis Z direkt an die Nummer an!"));
        }

        // After ruling out existing version numbers, check the new numbers validity
        if (isNewRecord || currentRecord == null) {
            DataTypeIntString highestEntryNo = new DataTypeIntString(" ");
            try {
                LogbookRecord r = (LogbookRecord) (storage().data().getLast());
                if (r != null) {
                    highestEntryNo = r.getEntryId();
                }
            } catch (Exception e) {
                Logger.logdebug(e);
            }
            if (newEntryNo.compareTo(highestEntryNo) <= 0 && !isOverridden(ENTRY_NUMBER_TOO_LOW_OVERRIDE)) { // newEntryNo is smaller than current highest number
                throw new EntryNumberTooLowException(
                        International.getString("Warnung"),
                        International.getString("Die Laufende Nummer dieses Eintrags ist kleiner als die des "
                                + "letzten Eintrags."),
                        override);
            }
        } else { // updated record
            if (!isOverridden(ENTRY_NUMBER_CHANGED_OVERRIDE) && !StringUtils.equals(currentRecord.getEntryId().toString(), record().getEntryId().toString())) {
                throw new EntryNumberChangedException(
                        International.getString("Warnung"),
                        International.getString("Du hast die Laufende Nummer dieses Eintrags verändert!"),
                        override);
            }
        }
        return true;
    }

    /**
     * Check if a valid BoatCaptain was entered.
     * A valid member has to sit at the captain's position
     * It is also invalid to not enter a captain if one is forced in the config.
     *
     * @return check result
     * @throws BoatCaptainException ch
     */
    private boolean checkBoatCaptain() throws BoatCaptainException {
        // todo: captain auto select should be a "client-side" feature
        // implement it in java, maybe using some kind of share efa lib that works for all base classes
        int boatCaptain = record().getBoatCaptainPosition();
        if (StringUtils.isEmpty(record().getCrewName(boatCaptain))) {
            throw new BoatCaptainException(
                    null,
                    International.getString("Bitte wähle als Obmann eine Person aus, die tatsächlich im Boot sitzt!"));
        }

        if (Daten.efaConfig.getValueEfaDirekt_eintragErzwingeObmann() && boatCaptain < 0) {
            throw new BoatCaptainException(
                    null,
                    International.getString("Bitte wähle einen Obmann aus!"));
        }

        return true;
    }

    // todo: implement checkBoatStatus (current logic is in EfaBoathouseFrame.java:checkStartSessionForBoat)

    /**
     * Check is the entry is in the range of a selected MultiDayTour.
     *
     * @return check result
     * @throws TimeOutOfRangeException entered date has to match with MultiDayTour
     */
    private boolean checkMultiDayTours() throws TimeOutOfRangeException {
        UUID sgId = record().getSessionGroupId();
        SessionGroupRecord g = (sgId != null ? Daten.project.getSessionGroups(false).findSessionGroupRecord(sgId) : null);
        if (!record().getDate().isSet()) {
            return true; // shouldn't happen
        }
        if (g != null) {
            DataTypeDate entryStartDate = record().getDate();
            DataTypeDate entryEndDate = record().getEndDate();
            DataTypeDate groupStartDate = g.getStartDate();
            DataTypeDate groupEndDate = g.getEndDate();
            if (entryStartDate.isBefore(groupStartDate) || entryStartDate.isAfter(groupEndDate) ||
                    (entryEndDate.isSet() && (entryEndDate.isBefore(groupStartDate) || entryEndDate.isAfter(groupEndDate)))) {
                throw new TimeOutOfRangeException(
                        null,
                        International.getMessage("Das Datum des Fahrtenbucheintrags {entry} liegt außerhalb des Zeitraums, "
                                        + "der für die ausgewählte Fahrtgruppe '{name}' angegeben wurde.",
                                record().getEntryId().toString(), g.getName()),
                        TimeOutOfRangeException.MODE_ERROR);
            }
        }
        return true;
    }

    // todo: checkSessionType is some client side assistance

    /**
     * EndDate cant be before StartDate
     *
     * @return check result
     * @throws TimeOutOfRangeException entered date has to make sense
     */
    private boolean checkDate() throws TimeOutOfRangeException {
        if (record().getDate() == null || record().getEndDate() == null) {
            return true;
        }
        if (record().getDate().isSet() && record().getEndDate().isSet() && !record().getDate().isBefore(record().getEndDate())) {
            throw new TimeOutOfRangeException(null,
                    International.getString("Das Enddatum muß nach dem Startdatum liegen."),
                    TimeOutOfRangeException.MODE_FOCUS_ENDDATE);
        }
        return true;
    }

    /**
     * Check if StartTime is before EndTime
     * or if a boat has traveled faster than 10m/s
     *
     * @return check result
     * @throws TimeOutOfRangeException entered time has to make sense
     */
    private boolean checkTime() throws TimeOutOfRangeException {
        if (record().getStartTime() == null || record().getEndTime() == null || record().getDistance() == null) {
            return true;
        }
        if (record().getStartTime().isSet() && record().getEndTime().isSet() &&
                record().getStartTime().isAfter(record().getEndTime()) &&
                (record().getEndDate().isSet() && record().getDate().isSet() && !record().getEndDate().isAfter(record().getDate()))) {
            throw new TimeOutOfRangeException(
                    International.getMessage("Ungültige Eingabe im Feld '{field}'",
                            International.getString("Zeit")),
                    International.getString("Bitte überprüfe die eingetragenen Uhrzeiten.") + "\n" +
                            International.getString("Ist dieser Eintrag eine Mehrtagsfahrt?"),
                    TimeOutOfRangeException.MODE_END_BEFORE_START_TIME);
        }

        // check whether the elapsed time is long enough
        String sType = record().getSessionType();
        if (!sType.equals(EfaTypes.TYPE_SESSION_LATEENTRY) &&
                !sType.equals(EfaTypes.TYPE_SESSION_TRAININGCAMP) &&
                record().getStartTime().isSet() && record().getEndTime().isSet() && record().getDistance().isSet()) {
            long timediff = Math.abs(record().getEndTime().getTimeAsSeconds() - record().getStartTime().getTimeAsSeconds());
            long dist = record().getDistance().getValueInMeters();
            if (timediff < 15 * 60 &&
                    timediff < dist / 10 &&
                    dist < 100 * 1000) {
                // if a short elapsed time (anything less than 15 minutes) has been entered,
                // then check whether it is plausible; plausible times need to be at least
                // above 1 s per 10 meters; everything else is implausible
                // we skip the check if the distance is >= 100 Km (that's probably a late entry
                // then).
                throw new TimeOutOfRangeException(
                        null,
                        International.getString("Bitte überprüfe die eingetragenen Uhrzeiten."),
                        TimeOutOfRangeException.MODE_FOCUS_ENDTIME);
            }
        }
        return true;
    }

    /**
     * Check if Entry is in the validity range of the logbook
     *
     * @return check result
     * @throws TimeOutOfRangeException entry can only be saved if in the logbook's validity range
     */
    private boolean checkAllowedDateForLogbook() throws AllowedForLogbookException {
        long tRec = record().getValidAtTimestamp();
        if (tRec < storage().getValidFrom() || tRec >= storage().getInvalidFrom()) {
            String msg = International.getMessage("Der Eintrag kann nicht gespeichert werden, da er außerhalb des gültigen Zeitraums ({startdate} - {enddate}) "
                    + "für dieses Fahrtenbuch liegt.", storage().getStartDate().toString(), storage().getEndDate().toString());
            Logger.log(Logger.WARNING, Logger.MSG_EVT_ERRORADDRECORDOUTOFRANGE, msg + " (" + record().toString() + ")");
            throw new AllowedForLogbookException(
                    null,
                    msg,
                    record(),
                    storage());
        }
        return true;
    }

    /**
     * Check if all data was entered, these restrictions don't apply to admins.
     * This is checked by the EDITLOGBOOK permission.
     *
     * @return check result
     * @throws DataMissingException Some important data is missing
     */
    private boolean checkAllDataEntered() throws DataMissingException {
        if (!isAllowed(EDITLOGBOOK)) { // Was this check initiated by an admin record with sufficient permissions
            if (StringUtils.isEmpty(record().getBoatName())) {
                throw new DataMissingException(
                        null,
                        International.getString("Bitte gib einen Bootsnamen ein!"),
                        "BOAT");
            }

            if (record().getNumberOfCrewMembers() == 0) {
                throw new DataMissingException(
                        null,
                        International.getString("Bitte trage mindestens eine Person ein!"),
                        "PERSON");
            }

            // Enter the destination before start
            if (record().getSessionIsOpen()
                    && Daten.efaConfig.getValueEfaDirekt_zielBeiFahrtbeginnPflicht() && StringUtils.isEmpty(record().getDestinationName())) {
                throw new DataMissingException(
                        null,
                        International.getString("Bitte trage ein voraussichtliches Fahrtziel/Strecke ein!"),
                        "DESTINATION");
            }

            if (!record().getSessionIsOpen() &&
                    StringUtils.isEmpty(record().getDestinationName())) {
                throw new DataMissingException(
                        null,
                        International.getString("Bitte trage ein Fahrtziel/Strecke ein!"),
                        "DESTINATION");
            }

            if (!record().getSessionIsOpen() &&
                    Daten.efaConfig.getValueEfaDirekt_gewaesserBeiUnbekanntenZielenPflicht() &&
                    record().getWatersNamesStringList().length() == 0) {
                throw new DataMissingException(
                        null,
                        International.getString("Bitte trage ein Gewässer ein!"),
                        "WATERS");
            }

            if ((record().getDistance() == null || !record().getDistance().isSet() || record().getDistance().getValueInDefaultUnit() == 0)) {
                if (!record().getSessionIsOpen()) {
                    if (!Daten.efaConfig.getValueAllowSessionsWithoutDistance()) {
                        throw new DataMissingException(
                                null,
                                International.getString("Bitte trage die gefahrenen Entfernung ein!"),
                                "DISTANCE");
                    }
                }
            }
        }
        return true;
    }

    private void ignoreNameInvalid(String name, long validAt, String type, String fieldID) throws NameInvalidException {
        String msg = International.getMessage("{type} '{name}' ist zum Zeitpunkt {dateandtime} ungültig.",
                type, name, EfaUtil.getTimeStamp(validAt));
        if (!isOverridden(NameInvalidException.getTypeString(fieldID))) {
            throw new NameInvalidException(null, msg, override, fieldID, record());
        }
    }

    /**
     * Check if a record is in the validity range for the logbook entry
     * Tries to deep search for a fitting record, if no one could be found in the
     * validity range of the logbook entry.
     * This is an error, since all entered entities have to be valid at the time of creation.
     *
     * @return check result
     * @throws NameInvalidException (can be overridden); error if a record is invalid
     */
    private boolean checkNamesValid() throws NameInvalidException {
        long preferredValidAt = record().getValidAtTimestamp();
        long validFrom = storage().getValidFrom();
        long validUntil = storage().getInvalidFrom() - 1;

        String name = record().getBoatName();
        if (name != null && name.length() > 0) {
            BoatRecord r = Daten.project.getBoats(false).findBoat(name, preferredValidAt, validFrom, validUntil);
            if (r == null) { // If the deep Search couldn't find a boat search for any boat that has ever existed
                r = Daten.project.getBoats(false).findBoat(name, -1, -1, -1);
            }
            if (preferredValidAt > 0 && r != null && !r.isValidAt(preferredValidAt)) {
                ignoreNameInvalid(r.getQualifiedName(), preferredValidAt, International.getString("Boot"), "BOAT");
            }
        }

        for (int i = 0; i <= LogbookRecord.CREW_MAX; i++) {
            name = record().getCrewName(i);
            if (name != null && name.length() > 0) {
                PersonRecord r = Daten.project.getPersons(false).findPerson(name, preferredValidAt, validFrom, validUntil);
                if (r == null) { // If the previous deep search couldn't find a record, lift all restrictions
                    r = Daten.project.getPersons(false).findPerson(name, -1, -1, -1);
                }
                if (preferredValidAt > 0 && r != null && !r.isValidAt(preferredValidAt)) {
                    ignoreNameInvalid(r.getQualifiedName(), preferredValidAt, LogbookRecord.getCrewI18nName(i), "PERSON" + i);
                }
            }
        }

        name = record().getDestinationName();
        if (name != null && name.length() > 0) {
            // boathouse restriction only applies if a boathouse name was given in the override map
            DestinationRecord r = Daten.project.getDestinations(false).findDestination(
                    name, boatHouseName, preferredValidAt, validFrom, validUntil);
            if (r == null) {
                r = Daten.project.getDestinations(false).findDestination(
                        name, boatHouseName, -1, -1, -1);
            }
            if (preferredValidAt > 0 && r != null && !r.isValidAt(preferredValidAt)) {
                ignoreNameInvalid(r.getQualifiedName(), preferredValidAt,
                        International.getString("Ziel"), "DESTINATION");
            }
        }

        return true;
    }

    /**
     * We want to ensure that data is saved only once, either in form of a UUID
     * or in form of a Name/String.
     * We always prefer the UUID, so we try to replace a name with a fitting
     * UUID (by running find...), if that fails, we fall back to the name and leave UUID empty.
     * In case of a Waters List, both can happen, but each Water should only exist once in the lists,
     * preferable as a UUID.
     * <p>
     * This also creates a safe way of knowing if an entry is known or unknown
     */
    private void ensureUniqueness() {
        long preferredValidAt = record().getValidAtTimestamp();
        long validFrom = storage().getValidFrom();
        long validUntil = storage().getInvalidFrom() - 1;

        if (!StringUtils.isEmpty(record().getDestinationName())) {
            if (record().getDestinationRecord(record().getValidAtTimestamp()) != null) {
                record().setDestinationName(null);
            } else {
                // Record is null, try to find one by name
                DestinationRecord match = Daten.project.getDestinations(false).findDestination(record().getDestinationName(), boatHouseName, preferredValidAt, validFrom, validUntil);
                if (match != null && match.getId() != null) {
                    record().setDestinationId(match.getId());
                    record().setDestinationName(null);
                }
            }
        }
        if (!StringUtils.isEmpty(record().getBoatName())) {
            if (record().getBoatRecord(record().getValidAtTimestamp()) != null) {
                record().setBoatName(null);
            } else {
                BoatRecord match = Daten.project.getBoats(false).findBoat(record().getBoatName(), preferredValidAt, validFrom, validUntil);
                if (match != null && match.getId() != null) {
                    record().setBoatId(match.getId());
                    record().setBoatName(null);
                }
            }
        }
        for (int i = 0; i <= LogbookRecord.CREW_MAX; i++) {
            if (!StringUtils.isEmpty(record().getCrewName(i))) {
                if (record().getCrewRecord(i, record().getValidAtTimestamp()) != null) {
                    record().setCrewName(i, null);
                } else {
                    PersonRecord match = Daten.project.getPersons(false).findPerson(record().getCrewName(i), preferredValidAt, validFrom, validUntil);
                    if (match != null && match.getId() != null) {
                        record().setCrewId(i, match.getId());
                        record().setCrewName(i, null);
                    }
                }
            }
        }
        if ((record().getWatersIdList() != null && record().getWatersIdList().length() > 0) && (record().getWatersNameList() != null && record().getWatersNameList().length() > 0)) {
            DataTypeList<UUID> watersId = record().getWatersIdList();
            DataTypeList<String> watersName = record().getWatersNameList();
            for (int i = 0; i < watersId.length(); i++) {
                String name = Daten.project.getWaters(false).getWaters(watersId.get(i)).getName();
                if (watersName.contains(name)) { // If this item exists as UUID, wo don't want it as String as well
                    watersName.remove(name);
                }
            }
        }
    }

    /**
     * This Check ensures that all entered names have known records. This is only done when
     * the check is performed by a user/admin who doesn't have permission to edit the logbook, which
     * is given in the boathouse mode. (Before the CheckRunner, this was just a "isModeBoathouse()" check.)
     * Also, the efaConfig is checked before a entity is checked, since "known[Boats|Persons|...]"
     * can be disabled in the config.
     *
     * @return check results
     * @throws UnknownNameException thrown if entered name has no associated record
     */
    private boolean checkUnknownNames() throws UnknownNameException {
        if (!isAllowed(EDITLOGBOOK)) {
            if (Daten.efaConfig.getValueEfaDirekt_eintragNurBekannteBoote()) {
                String name = record().getBoatName();
                // thanks to ensureUniqueness we can assume unknown if the Name is set
                if (name != null && name.length() > 0) {
                    throw new UnknownNameException(
                            null,
                            LogString.itemIsUnknown(name, International.getString("Boot")),
                            "BOAT");
                }
            }
            if (Daten.efaConfig.getValueEfaDirekt_eintragNurBekannteRuderer()) {
                for (int i = 0; i <= LogbookRecord.CREW_MAX; i++) {
                    String name = record().getCrewName(i);
                    if (name != null && name.length() > 0) {
                        throw new UnknownNameException(
                                null,
                                LogString.itemIsUnknown(name, LogbookRecord.getCrewI18nName(i)),
                                "PERSON" + i);
                    }
                }
            }
            if (Daten.efaConfig.getValueEfaDirekt_eintragNurBekannteZiele()) {
                String name = record().getDestinationName();
                if (name != null && name.length() > 0) {
                    throw new UnknownNameException(
                            null,
                            LogString.itemIsUnknown(name, International.getString("Ziel/Strecke")),
                            "DESTINATION");
                }
            }

            if (Daten.efaConfig.getValueEfaDirekt_eintragNurBekannteGewaesser()) {
                DataTypeList<String> wlists = record().getWatersNameList();
                if (wlists != null && wlists.length() != 0) {
                    throw new UnknownNameException(
                            null,
                            LogString.itemIsUnknown(wlists.toString(), International.getString("Gewässer")),
                            "WATERS");
                }
            }
        }
        return true;
    }

    /**
     * This check runs on unknown person names and checks if those are properly formatted.
     * It compiles some regular expressions and checks every person name against it.
     * Person names can also contain name additions or the club name.
     * Example: (nameAdditions) [clubName]
     *
     * @return check result
     * @throws NameFormatInvalidException thrown if entered entity name contains invalid characters
     *                                    or is formatted in a wrong way.
     */
    private boolean checkProperUnknownNames() throws NameFormatInvalidException {
        if (!isAllowed(EDITLOGBOOK)) {
            String slist = Daten.efaConfig.getValueBoathouseNonAllowedUnknownPersonNames();
            String[] list = null;
            if (slist != null && slist.length() > 0) {
                list = slist.split(";");
                for (int i = 0; i < list.length; i++) {
                    list[i] = list[i] != null ? list[i].trim().toLowerCase() : null;
                }
            }
            Pattern pname = Daten.efaConfig.getValueNameFormatIsFirstNameFirst() ?
                    Pattern.compile("[X ]+ [X ]+") : Pattern.compile("[X ]+, [X ]+");
            Pattern pnameadd = Daten.efaConfig.getValueNameFormatIsFirstNameFirst() ?
                    Pattern.compile("[X ]+ [X ]+ \\([X 0-9]+\\)") : Pattern.compile("[X ]+, [X ]+ \\([X 0-9]+\\)");
            Pattern pnameclub = Daten.efaConfig.getValueNameFormatIsFirstNameFirst() ?
                    Pattern.compile("[X ]+ [X ]+ \\[[X 0-9]+\\]") : Pattern.compile("[X ]+, [X ]+ \\[[X 0-9]+\\]");
            Pattern pnameaddclub = Daten.efaConfig.getValueNameFormatIsFirstNameFirst() ?
                    Pattern.compile("[X ]+ [X ]+ \\([X 0-9]+\\) \\[[X 0-9]+\\]") : Pattern.compile("[X ]+, [X ]+ \\([X 0-9]+\\) \\[[X 0-9]+\\]");
            for (int i = 0; i <= LogbookRecord.CREW_MAX; i++) {
                String name = record().getCrewName(i);
                if (name != null && name.length() > 0) {
                    if (Daten.efaConfig.getValueBoathouseStrictUnknownPersons()) {
                        String _name = name;
                        name = EfaUtil.replace(name, ",", ", ", true);
                        name = EfaUtil.replace(name, " ,", ",", true);
                        name = EfaUtil.replace(name, "(", " (", true);
                        name = EfaUtil.replace(name, "( ", "(", true);
                        name = EfaUtil.replace(name, ")", ") ", true);
                        name = EfaUtil.replace(name, " )", ")", true);
                        name = EfaUtil.replace(name, "  ", " ", true);
                        name = name.trim();
                        if (!name.equals(_name)) {
                            record().setCrewName(i, name);
                        }
                        String xname = EfaUtil.transformNameParts(name);
                        if (!pname.matcher(xname).matches() &&
                                !pnameadd.matcher(xname).matches() &&
                                !pnameclub.matcher(xname).matches() &&
                                !pnameaddclub.matcher(xname).matches()
                                ) {
                            String nameformat = Daten.efaConfig.getValueNameFormatIsFirstNameFirst()
                                    ? International.getString("Vorname") + " "
                                    + International.getString("Nachname")
                                    : International.getString("Nachname") + ", "
                                    + International.getString("Vorname");
                            throw new NameFormatInvalidException(
                                    null,
                                    International.getString("Ungültiger Name") + ": " + name + "\n"
                                            + International.getString("Personennamen müssen eines der folgenden Formate haben:") + "\n\n"
                                            + nameformat + "\n"
                                            + nameformat + " (" + International.getString("Namenszusatz") + ")\n"
                                            + nameformat + " [" + International.getString("Verein") + "]\n"
                                            + nameformat + " (" + International.getString("Namenszusatz") + ")" +
                                            " [" + International.getString("Verein") + "]");
                        }
                    }
                    if (list != null) {
                        for (String testName : list) {
                            if (!StringUtils.isEmpty(testName) && StringUtils.contains(name.toLowerCase(), testName)) {
                                throw new NameFormatInvalidException(
                                        null,
                                        International.getString("Ungültiger Name") + ": " + name + "\n"
                                                + International.getMessage("'{string}' ist nicht erlaubt in Personennamen.",
                                                testName.toUpperCase()));
                            }
                        }
                    }
                }
            }
        }
        return true;
    }


    /**
     * Check if all crew members are allowed to use the boat.
     * A boat can be blocked for everyone (e.g. because it has to be repaired)
     * Or boat usage can be restricted to members of a group
     *
     * @return check result
     * @throws BoatUsageGroupRestrictedException thrown if some members are not in the expected groups
     * @throws BoatUsagePersonRestrictedException thrown if a person is not permitted to use a boat
     */
    private boolean checkAllowedPersons() throws BoatUsageGroupRestrictedException, BoatUsagePersonRestrictedException {
        Groups groups = Daten.project.getGroups(false);
        long tstmp = record().getValidAtTimestamp();
        BoatRecord currentBoat = record().getBoatRecord(tstmp);
        if (currentBoat == null) {
            return true;
        }

        DataTypeList<UUID> groupIdList = record().getBoatRecord(tstmp).getAllowedGroupIdList();
        if (groupIdList != null && groupIdList.length() > 0) {
            String notAllowed = null;
            int notAllowedCount = 0;
            for (int i = 0; i <= LogbookRecord.CREW_MAX; i++) {
                PersonRecord p = record().getCrewRecord(i, tstmp);
                String pText = record().getCrewName(i);
                if (p == null && pText == null) {
                    continue;
                }

                if (p != null && p.getBoatUsageBan() && !isOverridden(BoatUsagePersonRestrictedException.getPosOverride(i))) {
                    throw new BoatUsagePersonRestrictedException(
                            International.getString("Bootsbenutzungs-Sperre"),
                            International.getMessage("Für {name} liegt zur Zeit eine Bootsbenutzungs-Sperre vor.", p.getQualifiedName()),
                            override,
                            i);
                }

                boolean inAnyGroup = false;
                if (p != null) {
                    for (int j = 0; j < groupIdList.length(); j++) {
                        GroupRecord g = groups.findGroupRecord(groupIdList.get(j), tstmp);
                        if (g != null && g.getMemberIdList() != null && g.getMemberIdList().contains(p.getId())) {
                            inAnyGroup = true;
                            break;
                        }
                    }
                }
                if (!inAnyGroup) {
                    String name = (p != null ? p.getQualifiedName() : pText);
                    notAllowed = (notAllowed == null ? name : notAllowed + "\n" + name);
                    notAllowedCount++;
                }
            }
            if (Daten.efaConfig.getValueCheckAllowedPersonsInBoat() &&
                    notAllowedCount > 0 &&
                    notAllowedCount > record().getBoatRecord(tstmp).getMaxNotInGroup()) {
                String allowedGroups = null;
                for (int j = 0; j < groupIdList.length(); j++) {
                    GroupRecord g = groups.findGroupRecord(groupIdList.get(j), tstmp);
                    String name = (g != null ? g.getName() : null);
                    if (name == null) {
                        continue;
                    }
                    allowedGroups = (allowedGroups == null ? name : allowedGroups + (j + 1 < groupIdList.length() ? ", " + name : " "
                            + International.getString("und") + " " + name));
                }
                if (!isOverridden(BOAT_GROUP_RESTRICTED)) {
                    throw new BoatUsageGroupRestrictedException(
                            International.getString("Boot nur für bestimmte Gruppen freigegeben"),
                            International.getMessage("Dieses Boot dürfen nur {list_of_valid_groups} nutzen.", allowedGroups) + "\n"
                                    + International.getString("Folgende Personen gehören keiner der Gruppen an und dürfen das Boot nicht benutzen:") + " \n"
                                    + notAllowed,
                            override,
                            BOAT_GROUP_RESTRICTED,
                            record());
                }
            }
        }
        // Check if at least one of the "one of this group"-rowers is in the boat
        if (Daten.efaConfig.getValueCheckMinOnePersonsFromGroupInBoat() &&
                currentBoat.getRequiredGroupId() != null) {
            GroupRecord g = groups.findGroupRecord(currentBoat.getRequiredGroupId(), tstmp);
            boolean found = false;
            if (g != null && g.getMemberIdList() != null) {
                for (int i = 0; i <= LogbookRecord.CREW_MAX; i++) {
                    PersonRecord p = record().getCrewRecord(i, tstmp);
                    if (p != null && g.getMemberIdList().contains(p.getId())) {
                        found = true;
                        break;
                    }
                }
            }
            if (g != null && !found && !isOverridden(BOAT_ONE_OF_GROUP_RESTRICTED)) {
                throw new BoatUsageGroupRestrictedException(
                        International.getString("Boot erfordert bestimmte Berechtigung"),
                        International.getMessage("In diesem Boot muß mindestens ein Mitglied der Gruppe {groupname} sitzen.", g.getName()),
                        override,
                        BOAT_ONE_OF_GROUP_RESTRICTED,
                        record());
            }
        }
        return true;
    }
}
